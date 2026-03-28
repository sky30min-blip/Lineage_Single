"""
리니지 싱글 서버 GM 툴 - 채팅 모니터링
원래 서버 관리 툴(ChattingComposite)과 동일: 실시간 채팅 표시, 채널별 필터, GM 전체 채팅 전송.
채팅은 서버가 gm_chat_log에 INSERT하고, GM 전송은 gm_chat_send 테이블을 서버가 폴링해 브로드캐스트.
자동 새로고침 시 페이지를 주기적으로 리런해 채팅을 갱신합니다 (st.fragment 미지원 버전 대응).
"""

import time
import streamlit as st
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.table_schemas import get_create_sql, get_all_required_tables
from utils.log_reader import read_chatting_log_lines, list_chatting_log_dates, parse_chatting_line

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()
show_pending_feedback()

# 채널 코드 (Lineage.CHATTING_MODE_*)
CHANNELS = {
    "전체": 3,
    "장사": 12,
    "혈맹": 4,
    "귓말": 9,
    "파티": 11,
    "일반": 0,
    "외침": 2,
    "시스템": 20,
}
CHANNEL_ORDER = ["전체", "장사", "혈맹", "귓말", "파티", "일반", "외침", "시스템"]
MAX_CHAT_LINES = 400  # 표시할 최대 줄 수 (메모리 절약)
CHAT_POLL_SECONDS = 2  # 채팅 영역만 이 간격으로 갱신 (전체 리런 없음 → 깜빡임 없음)


def _ensure_tables():
    """gm_chat_log, gm_chat_send 테이블 없으면 생성."""
    required = get_all_required_tables()
    existing = db.get_all_tables()
    for tbl in ("gm_chat_log", "gm_chat_send"):
        if tbl not in existing and tbl in required:
            sql = get_create_sql(tbl)
            if sql:
                _ok_c, _ = db.execute_query_ex(sql)


def _fetch_chat_log(selected_channels: list):
    """gm_chat_log에서 선택된 채널만 최신순으로 조회 후 과거순으로 반환 (스크롤 시 아래가 최신)."""
    if not selected_channels:
        return []
    codes = [CHANNELS[c] for c in selected_channels if c in CHANNELS]
    if not codes:
        return []
    placeholders = ",".join(["%s"] * len(codes))
    sql = f"""
        SELECT id, created_at, channel, char_name, target_name, msg
        FROM gm_chat_log
        WHERE channel IN ({placeholders})
        ORDER BY id DESC
        LIMIT %s
    """
    params = tuple(codes) + (MAX_CHAT_LINES,)
    try:
        rows = db.fetch_all(sql, params)
        return list(reversed(rows)) if rows else []
    except Exception:
        return []


def _fetch_chat_log_from_file(selected_channels: list, date_str=None):
    """서버 log/chatting/ 파일에서 채팅 읽기. 원본 툴과 동일 소스라 DB 없어도 채팅 표시 가능."""
    if not selected_channels:
        return []
    codes = [CHANNELS[c] for c in selected_channels if c in CHANNELS]
    if not codes:
        return []
    lines = read_chatting_log_lines(date_str=date_str, max_lines=MAX_CHAT_LINES)
    rows = []
    for line in lines:
        r = parse_chatting_line(line)
        if r and int(r.get("channel", 0)) in codes:
            rows.append(r)
    return rows


def _channel_label(channel: int) -> str:
    for name, code in CHANNELS.items():
        if code == channel:
            return name
    return str(channel)


def _format_line(r: dict) -> str:
    ts = str(r.get("created_at", ""))[:19] if r.get("created_at") else ""
    ch = _channel_label(int(r.get("channel", 0)))
    name = (r.get("char_name") or "").strip() or "******"
    target = (r.get("target_name") or "").strip()
    msg = (r.get("msg") or "").strip()
    if target:
        return f"[{ts}] [{ch}] {name} -> {target}: {msg}"
    return f"[{ts}] [{ch}] {name}: {msg}"


# 테이블 없으면 생성 시도
_ensure_tables()

def _chat_table_status():
    """gm_chat_log 테이블 존재 여부와 레코드 수 반환. (exists: bool, count: int or None, error: str or None)"""
    try:
        r = db.fetch_one("SELECT COUNT(*) AS c FROM gm_chat_log")
        if r is not None and "c" in r:
            return True, int(r["c"]), None
        return False, None, "조회 실패"
    except Exception as e:
        err = str(e).lower()
        if "doesn't exist" in err or "exist" in err or "1146" in err:
            return False, None, "테이블 없음"
        return False, None, str(e)

# 채팅 소스: DB는 서버가 gm_chat_log에 쓸 때만 보임. 파일은 원본 툴과 동일하게 서버가 log/chatting/ 에 저장한 걸 읽음.
with st.sidebar:
    chat_source = st.radio(
        "채팅 소스",
        ["서버 로그 파일 (log/chatting/)", "DB (gm_chat_log)"],
        index=0,
        key="chat_source",
        help="원본 툴에 채팅이 보이면 '서버 로그 파일'을 선택하세요. 서버가 월드 채팅을 이 파일에 저장합니다.",
    )
    use_file = chat_source.startswith("서버 로그 파일")
    if use_file:
        dates = list_chatting_log_dates()
        if dates:
            st.selectbox("날짜", ["(최신)"] + dates[:14], key="chat_file_date")
        else:
            st.caption("log/chatting/ 폴더가 없거나 비어 있음.")
    auto_refresh = not st.checkbox("자동 새로고침 끄기", value=False, key="chat_no_auto", help="끄면 채팅이 자동으로 안 올라옵니다. 새로고침 버튼으로 수동 갱신.")

def _render_chat_display():
    """채팅 로그 영역 그리기 (구버전 Streamlit 호환: fragment 없이 한 번만 렌더)."""
    selected = [n for n in CHANNEL_ORDER if st.session_state.get(f"ch_{n}", True)]
    use_file = st.session_state.get("chat_source", "").startswith("서버 로그 파일")
    if use_file:
        fd = st.session_state.get("chat_file_date", "(최신)")
        date_str = None if fd == "(최신)" else fd
        rows = _fetch_chat_log_from_file(selected, date_str=date_str)
    else:
        rows = _fetch_chat_log(selected)
    tbl_exists, tbl_count, _ = _chat_table_status()
    if rows:
        text = "\n".join([_format_line(r) for r in rows])
    elif use_file:
        text = "(서버 로그 파일에 채팅이 없습니다. 서버가 log/chatting/ 에 저장하는지, 선택한 날짜·채널을 확인하세요.)"
    elif tbl_exists and tbl_count == 0:
        text = "(아직 채팅이 없습니다. 사이드바에서 채팅 소스를 '서버 로그 파일'로 바꿔 보세요. 원본 툴에 보이면 파일로 보입니다.)"
    elif not tbl_exists:
        text = "(gm_chat_log 테이블이 없습니다. 위 [채팅 테이블 생성] 버튼을 누르거나, 채팅 소스를 '서버 로그 파일'로 선택하세요.)"
    else:
        text = "(채팅이 없거나 선택한 채널에 해당하는 로그가 없습니다. 채널 필터에서 [전체], [일반] 등을 켜 보세요.)"
    st.text_area("채팅", value=text, height=400, disabled=True, key="chat_display", label_visibility="collapsed")

st.subheader("📢 채팅 모니터링")
st.caption("채팅 소스: **서버 로그 파일** = 원본 툴과 동일(월드 채팅이 여기 보임). **DB** = gm_chat_log 테이블. GM 전송은 gm_chat_send 테이블을 서버가 폴링해 전체 채팅으로 브로드캐스트합니다.")

# 상태 표시 + 테이블 생성 / 테스트 버튼
tbl_exists, tbl_count, tbl_err = _chat_table_status()
status_col1, status_col2 = st.columns([2, 2])
with status_col1:
    if tbl_exists:
        st.caption(
            f"**gm_chat_log** 테이블 있음 · 레코드 수: **{tbl_count}**건. "
            + ("레코드가 0건이면 **게임 서버가 이 DB에 채팅을 쓰지 않는 것**입니다. 아래 **테스트 메시지 삽입**으로 화면이 정상인지 먼저 확인하세요." if tbl_count == 0 else "")
        )
    else:
        st.caption(f"**gm_chat_log** 테이블 없음 또는 오류: {tbl_err or '알 수 없음'}")
with status_col2:
    btn_create = st.button("🔨 채팅 테이블 생성", key="create_chat_tables")
    btn_test = st.button("📝 테스트 메시지 삽입", key="insert_test_chat", help="GM툴에서 직접 한 줄 넣어봅니다. 보이면 DB·화면은 정상이고, 게임 채팅만 서버 미기록입니다.")
if btn_create:
    fb_parts: list[str] = []
    for tbl in ("gm_chat_log", "gm_chat_send"):
        sql = get_create_sql(tbl)
        if sql:
            ok_ct, err_ct = db.execute_query_ex(sql)
            if ok_ct:
                fb_parts.append(f"✅ {tbl} 생성됨")
            else:
                fb_parts.append(f"❌ {tbl}: {err_ct}")
    if fb_parts:
        if all(p.startswith("✅") for p in fb_parts):
            queue_feedback("success", " ".join(fb_parts))
        else:
            queue_feedback("error", " | ".join(fb_parts))
    st.rerun()
if btn_test and tbl_exists:
    try:
        with db.connection.cursor() as cur:
            cur.execute(
                "INSERT INTO gm_chat_log (channel, char_name, target_name, msg) VALUES (3, %s, %s, %s)",
                ("******", "", "[GM툴 테스트] 이 메시지가 보이면 DB·화면은 정상입니다. 게임 채팅이 안 쌓이면 서버 재시작을 확인하세요."),
            )
        db.connection.commit()
        queue_feedback("success", "테스트 메시지를 넣었습니다. 잠시 후(약 2초) 채팅 목록에 표시됩니다.")
        st.rerun()
    except Exception as e:
        st.error(f"삽입 실패: {e}")

# 채널 필터 (체크박스)
st.write("**채널 필터**")
cols = st.columns(8)
for i, name in enumerate(CHANNEL_ORDER):
    with cols[i % 8]:
        st.checkbox(name, value=True, key=f"ch_{name}")

# 채팅 로그 영역 (자동 새로고침 켜면 아래에서 주기적으로 페이지 리런)
_render_chat_display()

# GM 전체 채팅 전송
st.write("**GM 전체 채팅 전송**")
send_col1, send_col2 = st.columns([4, 1])
with send_col1:
    gm_msg = st.text_input("메시지", placeholder="전체 채팅으로 전송할 내용", key="gm_chat_msg", label_visibility="collapsed")
with send_col2:
    send_btn = st.button("전송", key="gm_chat_send")
if send_btn and gm_msg and gm_msg.strip():
    ok_send, err_send = db.execute_query_ex(
        "INSERT INTO gm_chat_send (msg, sent) VALUES (%s, 0)",
        (gm_msg.strip()[:500],),
    )
    if ok_send:
        queue_feedback("success", "✅ 전송 요청되었습니다. 서버가 곧 전체 채팅으로 브로드캐스트합니다.")
    else:
        st.error(f"❌ 전송 실패: {err_send}")
        _ensure_tables()
        st.info("테이블이 없었다면 위에서 생성을 시도했습니다. **채팅 테이블 생성** 후 다시 전송해 보세요.")

with st.expander("❓ 채팅이 안 보이거나 전송이 안 될 때"):
    st.markdown("""
    1. **gm_chat_log** 테이블이 있어야 합니다.  
       → 위 **채팅 테이블 생성** 버튼 또는 **DB 관리** 페이지에서 누락 테이블 자동 생성을 실행하세요.
    2. **게임 서버를 반드시 재시작**해야 합니다.  
       → 서버 코드(`GmChatLogDatabase.java`, `ChattingController.java`, `GmDeliveryController.java`) 수정 후 재빌드·재시작해야 채팅이 DB에 기록됩니다.
    3. **GM 툴과 게임 서버가 같은 DB(스키마)**를 사용하는지 확인하세요.  
       → 서버 설정과 GM 툴 config의 database 이름이 같아야 채팅 로그가 보입니다.
    4. 게임에서 **전체 채팅** 또는 **일반 채팅**을 한 번 보내 본 뒤, 이 페이지가 새로고침되면(약 2초마다) 채팅이 표시됩니다.
    """)
st.caption("자동 새로고침 켜면 2초마다 페이지가 갱신됩니다." if auto_refresh else "자동 새로고침이 꺼져 있습니다. 아래 버튼으로 수동 갱신.")
if st.button("🔄 지금 새로고침", key="chat_manual_refresh"):
    st.rerun()

# 자동 새로고침: st.fragment 미지원 버전 대응 — 2초 대기 후 리런 (전체 페이지 갱신)
if auto_refresh:
    time.sleep(CHAT_POLL_SECONDS)
    st.rerun()
