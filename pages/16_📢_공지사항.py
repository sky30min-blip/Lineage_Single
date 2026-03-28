"""
공지사항 - notice.txt 편집 (월드 채팅 주기 공지)
각 공지마다 출력 간격(분)을 따로 설정할 수 있습니다. 형식: 분|메시지
접속 팝업(server_notice)·게임 내 게시판(boards)은 DB 탭에서 편집합니다.
"""
import os
import streamlit as st
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.db_manager import get_db
from utils.gm_tabs import gm_section_tabs

# 인게임 BoardController / DB 스키마와 동일한 제한
BOARD_SUBJECT_MAX = 50
BOARD_NAME_MAX = 20
BOARD_ACCOUNT_MAX = 20
BOARD_LIST_PAGE_SIZE = 8
NOTICE_SUBJECT_MAX = 255
# server_notice.type — UI에는 한글 설명 병기, DB에는 영문 값만 저장
NOTICE_TYPE_OPTIONS = (
    ("final", "final (한 번만)"),
    ("static", "static (매번 접속 시)"),
)
NOTICE_TYPE_LABELS = [lbl for _, lbl in NOTICE_TYPE_OPTIONS]
NOTICE_LABEL_TO_DB = {lbl: db for db, lbl in NOTICE_TYPE_OPTIONS}


def _notice_type_label_index(db_type: str) -> int:
    t = (db_type or "final").lower()
    for i, (d, _) in enumerate(NOTICE_TYPE_OPTIONS):
        if d == t:
            return i
    return 0


def _notice_type_caption(db_type: str) -> str:
    t = (db_type or "").lower()
    for d, lbl in NOTICE_TYPE_OPTIONS:
        if d == t:
            return lbl
    return db_type or "?"


DEFAULT_BOARD_TYPES = (
    "guide",
    "server",
    "update",
    "trade",
    "at",
    "rank",
    "aden",
    "giran",
    "heine",
    "powerball_reward",
    "cash",
)

# boards.type — UI 표기용 (DB에는 영문만 저장)
BOARD_TYPE_KO = {
    "guide": "가이드 게시판",
    "server": "서버 공지 게시판",
    "update": "업데이트 게시판",
    "trade": "거래 게시판",
    "at": "AT/출석 관련 게시판",
    "rank": "랭킹 게시판",
    "aden": "아덴 마을·경매 등",
    "giran": "기란 마을·경매 등",
    "heine": "하이네 마을·경매 등",
    "gludin": "글루딘 마을·경매 등",
    "powerball_reward": "파워볼 보상 안내",
    "cash": "현금거래 등 전용",
}


def _board_type_display(db_type: str) -> str:
    t = (db_type or "").strip()
    if not t:
        return ""
    ko = BOARD_TYPE_KO.get(t.lower())
    if ko:
        return f"{t} ({ko})"
    return f"{t} (기타·맵 전용)"


def _board_type_parse_label(label: str) -> str:
    """selectbox에서 고른 'server (서버 공지)' → DB용 'server'."""
    s = (label or "").strip()
    if " (" in s:
        return s.split(" (", 1)[0].strip()
    return s


def _board_type_normalize_input(text: str) -> str:
    """직접 입력에 (한글)까지 붙여 넣었을 때 앞쪽 영문만 사용."""
    return _board_type_parse_label((text or "").strip())


def _db_popup_board():
    db = get_db()
    ok, msg = db.test_connection()
    return db, ok, msg


def _boards_delete_repack(db, btype: str, uid: int) -> tuple[bool, str]:
    """인게임 BoardController.toDelete 와 동일: 삭제 후 uid 재정렬."""
    try:
        db._ensure_connection()
        con = db.connection
        with con.cursor() as cur:
            cur.execute(
                "DELETE FROM boards WHERE type=%s AND uid=%s",
                (btype, uid),
            )
            cur.execute(
                "UPDATE boards SET uid=uid-1 WHERE type=%s AND uid>%s",
                (btype, uid),
            )
        con.commit()
        return True, ""
    except Exception as e:
        try:
            db.connection.rollback()
        except Exception:
            pass
        return False, str(e)


def _boards_next_uid(db, btype: str) -> int:
    row = db.fetch_one(
        "SELECT COALESCE(MAX(uid), 0) AS m FROM boards WHERE type=%s",
        (btype,),
    )
    return int(row["m"]) + 1 if row else 1

st.set_page_config(page_title="공지사항", page_icon="📢", layout="wide")
st.title("📢 공지사항")
st.caption(
    "월드 채팅에 올라가는 **주기 공지**는 `notice.txt`(UTF-8)에서 읽습니다. 형식: `분|메시지`. "
    "여기서 다루지 않는 공지(타임이벤트·월드보스 등)는 **「기타 자동 공지」** 탭을 보세요."
)

# 경로 (프로젝트 루트 기준)
_BASE = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
NOTICE_PATH = os.path.join(_BASE, "2.싱글리니지 팩", "notice.txt")
LINEAGE_CONF_PATH = os.path.join(_BASE, "2.싱글리니지 팩", "lineage.conf")


def read_notice():
    """notice.txt 읽기 (UTF-8 우선, 실패 시 Windows 한글 메모장 기본인 CP949)"""
    if not os.path.isfile(NOTICE_PATH):
        return None, "파일 없음"
    for enc in ("utf-8-sig", "utf-8", "cp949", "euc-kr"):
        try:
            with open(NOTICE_PATH, "r", encoding=enc) as f:
                return f.read(), None
        except UnicodeDecodeError:
            continue
        except Exception as e:
            return None, str(e)
    try:
        with open(NOTICE_PATH, "r", encoding="utf-8", errors="replace") as f:
            return f.read(), None
    except Exception as e:
        return None, str(e)


def write_notice(text: str):
    """notice.txt UTF-8로 쓰기"""
    try:
        os.makedirs(os.path.dirname(NOTICE_PATH), exist_ok=True)
        with open(NOTICE_PATH, "w", encoding="utf-8") as f:
            f.write(text)
        return None
    except Exception as e:
        return str(e)


def parse_entries(content: str):
    """
    파일 내용을 항목 목록으로 파싱.
    반환: [{"type": "comment", "line": "..."}, {"type": "notice", "delay": 1, "message": "..."}, ...]
    """
    entries = []
    for line in (content or "").splitlines():
        raw = line
        s = line.strip()
        if s.startswith("#") or len(s) == 0:
            entries.append({"type": "comment", "line": raw})
            continue
        sep = line.find("|")
        if sep > 0:
            try:
                delay = int(line[:sep].strip())
                if delay < 1:
                    delay = 1
            except ValueError:
                delay = 1
            msg = line[sep + 1:].strip()
            if msg:
                entries.append({"type": "notice", "delay": delay, "message": msg})
        else:
            entries.append({"type": "notice", "delay": 1, "message": s})
    return entries


def build_content(entries: list) -> str:
    """항목 목록을 파일 내용 문자열로 복원"""
    out = []
    for e in entries:
        if e["type"] == "comment":
            out.append(e["line"])
        else:
            out.append(f"{e['delay']}|{e['message']}")
    return "\n".join(out) + ("\n" if out else "")


# ---------- 이벤트 공지사항 (타임 이벤트, lineage.conf) ----------
def read_lineage_conf():
    """lineage.conf UTF-8로 읽기"""
    if not os.path.isfile(LINEAGE_CONF_PATH):
        return None, "파일 없음"
    try:
        with open(LINEAGE_CONF_PATH, "r", encoding="utf-8", errors="replace") as f:
            return f.read(), None
    except Exception as e:
        return None, str(e)


def parse_time_event_time(value: str):
    """time_event_time 값 파싱. '09:01, 15:01, 21:01' -> [('09','01'), ('15','01'), ...]"""
    result = []
    for part in (value or "").split(","):
        part = part.strip()
        if ":" in part:
            a, b = part.split(":", 1)
            h, m = a.strip(), b.strip()
            if h.isdigit() and m.isdigit():
                result.append((h, m))
    return result


def format_time_event_time(times: list) -> str:
    """[('09','01'), ...] -> '09:01, 15:01, 21:01'"""
    return ", ".join(f"{h}:{m}" for h, m in times)


def get_conf_value(content: str, key: str) -> str:
    """lineage.conf 내용에서 key= 값만 추출 (첫 번째 매칭)"""
    key_lower = key.lower()
    for line in (content or "").splitlines():
        s = line.strip()
        if s.startswith("#") or "=" not in s:
            continue
        k, _, v = line.partition("=")
        if k.strip().lower() == key_lower:
            return v.strip()
    return ""


def write_conf_key(content: str, key: str, new_value: str) -> tuple:
    """lineage.conf 내용에서 key= 있는 줄의 값을 new_value로 바꾼 전체 내용 반환. (새내용, 에러메시지)"""
    key_lower = key.lower()
    lines = content.splitlines()
    out = []
    replaced = False
    for line in lines:
        if "=" in line and not line.strip().startswith("#"):
            k, _, v = line.partition("=")
            if k.strip().lower() == key_lower:
                # 원래 줄 형식 유지 (탭/공백)
                sep = line.find("=")
                out.append(line[: sep + 1] + " " + str(new_value))
                replaced = True
                continue
        out.append(line)
    if not replaced:
        return None, f"'{key}' 항목을 lineage.conf에서 찾을 수 없습니다."
    try:
        with open(LINEAGE_CONF_PATH, "w", encoding="utf-8", newline="") as f:
            f.write("\n".join(out) + ("\n" if out else ""))
        return "\n".join(out) + ("\n" if out else ""), None
    except Exception as e:
        return None, str(e)


def update_conf_key_in_content(content: str, key: str, new_value: str) -> str:
    """content 문자열 내 key= 값을 new_value로 바꾼 새 문자열 반환 (파일 미저장)."""
    key_lower = key.lower()
    lines = content.splitlines()
    out = []
    for line in lines:
        if "=" in line and not line.strip().startswith("#"):
            k, _, v = line.partition("=")
            if k.strip().lower() == key_lower:
                sep = line.find("=")
                out.append(line[: sep + 1] + " " + str(new_value))
                continue
        out.append(line)
    return "\n".join(out) + ("\n" if out else "")


show_pending_feedback()

_db, _db_ok, _db_msg = _db_popup_board()

content, err = read_notice()
if err:
    st.warning(f"notice.txt 읽기 실패: {err}  → 아래 '공지사항' 탭은 비활성화됩니다. 경로: `{NOTICE_PATH}`")
    entries = []
    notice_indices = []
else:
    entries = parse_entries(content or "")
    notice_indices = [i for i, e in enumerate(entries) if e["type"] == "notice"]

# 탭: DB 팝업·게시판 / notice.txt·이벤트 등
_NOTICE_MAIN_LABELS = [
    "📣 공지 팝업 (접속)",
    "📌 게임 게시판 (DB)",
    "📋 목록 보기·수정·삭제",
    "➕ 공지 추가",
    "✏️ 전체 편집",
    "⏰ 이벤트 공지사항",
    "📡 기타 자동 공지 (코드)",
]
_notice_main_i = gm_section_tabs("notice_admin", _NOTICE_MAIN_LABELS)

if _notice_main_i == 0:
    st.subheader("📣 server_notice (접속 시 팝업 공지)")
    st.caption(
        "**static (매번 접속 시)**: 접속할 때마다 표시(계정이 마지막으로 본 uid보다 큰 공지만 순서대로). "
        "**final (한 번만)**: 계정당 한 번만. 여러 건 등록 가능합니다. "
        "저장 후 게임 서버에 반영하려면 **서버 GUI → server_notice 테이블 리로드** 하거나 서버를 재시작하세요."
    )
    if not _db_ok:
        st.error(f"DB 연결 실패: {_db_msg} — `gm_tool/config.py`(또는 GM_DB_*)를 확인하세요.")
    elif not _db.table_exists("server_notice"):
        st.warning("테이블 `server_notice` 가 없습니다. DB 덤프를 적용하거나 수동으로 생성하세요.")
    else:
        notices = _db.fetch_all(
            "SELECT uid, type, subject, content FROM server_notice ORDER BY uid ASC"
        )
        st.metric("등록된 팝업 공지 수", len(notices))

        with st.expander("➕ 새 팝업 공지 추가", expanded=not notices):
            np_label = st.selectbox(
                "표시 방식",
                options=NOTICE_TYPE_LABELS,
                index=0,
                key="sn_add_type",
                help="저장 값은 DB 기준 final / static 입니다.",
            )
            np_type = NOTICE_LABEL_TO_DB[np_label]
            np_subj = st.text_input(
                "제목",
                max_chars=NOTICE_SUBJECT_MAX,
                key="sn_add_subj",
            )
            np_body = st.text_area(
                "본문",
                height=160,
                key="sn_add_body",
            )
            if st.button("추가", key="sn_add_btn"):
                subj = (np_subj or "").strip()
                body = (np_body or "").strip()
                if not subj or not body:
                    queue_feedback("error", "제목과 본문을 모두 입력하세요.")
                    st.rerun()
                ok, err = _db.execute_query_ex(
                    "INSERT INTO server_notice (type, subject, content) VALUES (%s, %s, %s)",
                    (np_type, subj, body),
                )
                if ok:
                    queue_feedback("success", "팝업 공지를 추가했습니다. 서버에서 server_notice 리로드 하세요.")
                else:
                    queue_feedback("error", f"추가 실패: {err}")
                st.rerun()

        for row in notices:
            uid = row["uid"]
            with st.expander(
                f"uid {uid} · {_notice_type_caption(row.get('type'))} · {row.get('subject', '')[:40]}",
                expanded=False,
            ):
                ed_label = st.selectbox(
                    "표시 방식",
                    options=NOTICE_TYPE_LABELS,
                    index=_notice_type_label_index(row.get("type") or "final"),
                    key=f"sn_type_{uid}",
                )
                ed_type = NOTICE_LABEL_TO_DB[ed_label]
                ed_subj = st.text_input(
                    "제목",
                    value=row.get("subject") or "",
                    max_chars=NOTICE_SUBJECT_MAX,
                    key=f"sn_subj_{uid}",
                )
                ed_body = st.text_area(
                    "본문",
                    value=row.get("content") or "",
                    height=140,
                    key=f"sn_body_{uid}",
                )
                c1, c2 = st.columns(2)
                with c1:
                    if st.button("저장", key=f"sn_save_{uid}"):
                        ok, err = _db.execute_query_ex(
                            "UPDATE server_notice SET type=%s, subject=%s, content=%s WHERE uid=%s",
                            (ed_type, ed_subj.strip(), ed_body.strip(), uid),
                        )
                        if ok:
                            queue_feedback("success", f"uid {uid} 저장함. 서버에서 server_notice 리로드 하세요.")
                        else:
                            queue_feedback("error", f"저장 실패: {err}")
                        st.rerun()
                with c2:
                    if st.button("삭제", key=f"sn_del_{uid}"):
                        ok, err = _db.execute_query_ex(
                            "DELETE FROM server_notice WHERE uid=%s",
                            (uid,),
                        )
                        if ok:
                            queue_feedback("success", f"uid {uid} 삭제함. 서버에서 server_notice 리로드 하세요.")
                        else:
                            queue_feedback("error", f"삭제 실패: {err}")
                        st.rerun()

elif _notice_main_i == 1:
    st.subheader("📌 boards (게임 내 게시판 DB)")
    st.caption(
        f"DB 테이블 `boards` 를 직접 편집합니다. 인게임과 동일: **목록 한 페이지 {BOARD_LIST_PAGE_SIZE}개**, "
        f"제목 최대 **{BOARD_SUBJECT_MAX}자**, 작성자 **{BOARD_NAME_MAX}자**, 계정 **{BOARD_ACCOUNT_MAX}자**. "
        "**rank (랭킹 게시판)** 은 클라이언트에서 글쓰기가 막혀 있으나 DB 수정은 가능합니다. "
        "**powerball_reward (파워볼 보상 안내)** 는 서버가 주기적으로 덮어쓸 수 있습니다."
    )
    if not _db_ok:
        st.error(f"DB 연결 실패: {_db_msg}")
    elif not _db.table_exists("boards"):
        st.warning("테이블 `boards` 가 없습니다.")
    else:
        st.info(
            "**이게 뭔가요?** 게시글은 DB `boards` 에 저장되고, **어느 게시판 글인지**를 컬럼 **type** (영문 코드)으로 나눕니다. "
            "NPC·맵마다 다른 코드를 씁니다. 예: **server (서버 공지 게시판)**, **guide (가이드 게시판)**, **update (업데이트 게시판)**, "
            "**trade (거래 게시판)**, **aden / giran / heine (마을·경매 등)**. "
            "**왼쪽**은 목록에서 고르기, **오른쪽**은 직접 입력(비우면 왼쪽 값 사용). DB에는 괄호 앞 **영문만** 들어갑니다."
        )
        distinct = _db.fetch_all(
            "SELECT DISTINCT type FROM boards ORDER BY type ASC"
        )
        from_db = [r["type"] for r in distinct if r.get("type")]
        type_codes = sorted(set(from_db) | set(DEFAULT_BOARD_TYPES))
        type_labels = [_board_type_display(t) for t in type_codes]
        col_a, col_b = st.columns([2, 1])
        with col_a:
            pick_label = st.selectbox(
                "게시판 type (DB)",
                options=type_labels,
                key="bd_type_pick",
                help="괄호 앞 영문이 DB에 저장되는 값입니다.",
            )
        with col_b:
            custom = st.text_input(
                "직접 입력 (우선)",
                placeholder="예: server 또는 server (서버 공지)",
                key="bd_type_custom",
                help="영문만 써도 되고, 목록과 같이 '영문 (한글)' 형태로 써도 앞부분만 사용합니다.",
            )
        btype = _board_type_normalize_input(custom) or _board_type_parse_label(pick_label)

        posts = _db.fetch_all(
            "SELECT uid, type, account_id, name, subject, memo, days "
            "FROM boards WHERE type=%s ORDER BY uid ASC",
            (btype,),
        )
        cnt = len(posts)
        st.metric(f"{_board_type_display(btype)} — 글 개수", cnt)
        if cnt > 0:
            st.caption(
                f"인게임에서는 최신 글부터 **{BOARD_LIST_PAGE_SIZE}개씩** 페이지로 보입니다. "
                f"전체 {cnt}개 중 uid 1번이 가장 오래된 글에 가깝습니다."
            )

        with st.expander("➕ 새 글 추가 (운영자용)", expanded=False):
            st.caption("인게임 글쓰기와 같이 uid는 자동으로 MAX+1 입니다.")
            na = st.text_input(
                "account_id (계정 ID)",
                value="GM",
                max_chars=BOARD_ACCOUNT_MAX,
                key="bd_new_acc",
            )
            nn = st.text_input(
                "name (캐릭터명)",
                value="운영자",
                max_chars=BOARD_NAME_MAX,
                key="bd_new_name",
            )
            ns = st.text_input(
                "subject (제목)",
                max_chars=BOARD_SUBJECT_MAX,
                key="bd_new_subj",
            )
            nm = st.text_area("memo (본문)", height=120, key="bd_new_memo")
            if st.button("글 등록", key="bd_new_btn"):
                subj = (ns or "").strip()
                memo = (nm or "").strip()
                acc = (na or "").strip()[:BOARD_ACCOUNT_MAX]
                name = (nn or "").strip()[:BOARD_NAME_MAX]
                if not subj:
                    queue_feedback("error", "제목을 입력하세요.")
                    st.rerun()
                nuid = _boards_next_uid(_db, btype)
                ok, err = _db.execute_query_ex(
                    "INSERT INTO boards (uid, type, account_id, name, days, subject, memo) "
                    "VALUES (%s, %s, %s, %s, NOW(), %s, %s)",
                    (nuid, btype, acc, name, subj[:BOARD_SUBJECT_MAX], memo),
                )
                if ok:
                    queue_feedback(
                        "success",
                        f"{_board_type_display(btype)} 에 글 uid={nuid} 등록했습니다.",
                    )
                else:
                    queue_feedback("error", f"등록 실패: {err}")
                st.rerun()

        for p in posts:
            uid = p["uid"]
            with st.expander(
                f"uid {uid} · {(p.get('subject') or '')[:30]} — {p.get('name', '')}",
                expanded=False,
            ):
                e_acc = st.text_input(
                    "account_id (계정 ID)",
                    value=p.get("account_id") or "",
                    max_chars=BOARD_ACCOUNT_MAX,
                    key=f"bd_acc_{btype}_{uid}",
                )
                e_name = st.text_input(
                    "name (캐릭터명)",
                    value=p.get("name") or "",
                    max_chars=BOARD_NAME_MAX,
                    key=f"bd_name_{btype}_{uid}",
                )
                e_subj = st.text_input(
                    "subject (제목)",
                    value=p.get("subject") or "",
                    max_chars=BOARD_SUBJECT_MAX,
                    key=f"bd_subj_{btype}_{uid}",
                )
                e_memo = st.text_area(
                    "memo (본문)",
                    value=p.get("memo") or "",
                    height=120,
                    key=f"bd_memo_{btype}_{uid}",
                )
                u1, u2 = st.columns(2)
                with u1:
                    if st.button("저장", key=f"bd_save_{btype}_{uid}"):
                        ok, err = _db.execute_query_ex(
                            "UPDATE boards SET account_id=%s, name=%s, subject=%s, memo=%s "
                            "WHERE type=%s AND uid=%s",
                            (
                                (e_acc or "").strip()[:BOARD_ACCOUNT_MAX],
                                (e_name or "").strip()[:BOARD_NAME_MAX],
                                (e_subj or "").strip()[:BOARD_SUBJECT_MAX],
                                (e_memo or "").strip(),
                                btype,
                                uid,
                            ),
                        )
                        if ok:
                            queue_feedback("success", f"uid {uid} 저장했습니다.")
                        else:
                            queue_feedback("error", f"저장 실패: {err}")
                        st.rerun()
                with u2:
                    if st.button("삭제(번호 당김)", key=f"bd_del_{btype}_{uid}"):
                        ok, err = _boards_delete_repack(_db, btype, uid)
                        if ok:
                            queue_feedback(
                                "success",
                                f"uid {uid} 삭제 후 뒤 번호를 당겼습니다. (인게임과 동일)",
                            )
                        else:
                            queue_feedback("error", f"삭제 실패: {err}")
                        st.rerun()

elif _notice_main_i == 2:
    st.subheader("📋 현재 공지 목록 (공지별 출력 간격)")
    st.caption("각 공지는 **설정한 간격(분)**마다 따로 출력됩니다. # 주석 줄은 공지로 나가지 않습니다.")
    if err:
        st.info("notice.txt를 불러온 후 이용하세요.")
    elif not notice_indices:
        st.info("공지가 없습니다. '공지 추가' 또는 '전체 편집'에서 추가하세요.")
    else:
        editing_idx = st.session_state.get("notice_editing_idx", None)
        for pos, i in enumerate(notice_indices):
            e = entries[i]
            delay, msg = e["delay"], e["message"]
            col1, col2, col3 = st.columns([1, 4, 1])
            with col1:
                st.metric("간격(분)", delay)
            with col2:
                st.text(msg)
            with col3:
                if editing_idx == i:
                    if st.button("취소", key=f"cancel_{i}"):
                        st.session_state.pop("notice_editing_idx", None)
                        st.session_state.pop("notice_edit_delay", None)
                        st.session_state.pop("notice_edit_value", None)
                        st.rerun()
                else:
                    if st.button("수정", key=f"edit_{i}"):
                        st.session_state["notice_editing_idx"] = i
                        st.session_state["notice_edit_delay"] = delay
                        st.session_state["notice_edit_value"] = msg
                        st.rerun()
                    if st.button("삭제", key=f"del_{i}"):
                        entries.pop(i)
                        err = write_notice(build_content(entries))
                        if err:
                            queue_feedback("error", f"저장 실패: {err}")
                        else:
                            queue_feedback("success", "해당 공지를 삭제하고 저장했습니다.")
                        st.rerun()
        if editing_idx is not None and 0 <= editing_idx < len(entries) and entries[editing_idx]["type"] == "notice":
            with st.expander("✏️ 선택한 공지 수정", expanded=True):
                ed = entries[editing_idx]
                new_delay = st.number_input("출력 간격 (분)", min_value=1, max_value=1440, value=st.session_state.get("notice_edit_delay", ed["delay"]), key="notice_edit_delay_input")
                new_msg = st.text_input("공지 메시지", value=st.session_state.get("notice_edit_value", ed["message"]), key="notice_edit_msg_input")
                if st.button("반영"):
                    ed["delay"] = new_delay
                    ed["message"] = new_msg
                    err = write_notice(build_content(entries))
                    st.session_state.pop("notice_editing_idx", None)
                    st.session_state.pop("notice_edit_delay", None)
                    st.session_state.pop("notice_edit_value", None)
                    if err:
                        queue_feedback("error", f"저장 실패: {err}")
                    else:
                        queue_feedback("success", "공지와 간격을 수정하고 저장했습니다.")
                    st.rerun()

elif _notice_main_i == 3:
    st.subheader("➕ 공지 추가")
    st.caption("새 공지 메시지와 **몇 분마다** 출력할지 설정하세요.")
    if err:
        st.info("notice.txt를 불러온 후 이용하세요.")
    else:
        new_msg = st.text_area("공지 메시지", height=80, placeholder="예: 오늘 21시 보스 이벤트가 진행됩니다.", key="new_notice_msg")
        new_delay = st.number_input("출력 간격 (분)", min_value=1, max_value=1440, value=1, key="new_notice_delay")
        if st.button("추가 후 저장"):
            msg = (new_msg or "").strip().split("\n")[0].strip() if new_msg else ""
            if not msg:
                st.warning("공지 메시지를 입력하세요.")
            else:
                entries.append({"type": "notice", "delay": new_delay, "message": msg})
                err = write_notice(build_content(entries))
                if err:
                    queue_feedback("error", f"저장 실패: {err}")
                else:
                    queue_feedback("success", f"공지를 추가했습니다. (간격 {new_delay}분)")
                st.rerun()

elif _notice_main_i == 4:
    st.subheader("✏️ 전체 편집")
    st.caption("한 줄 형식: **분|메시지** (예: `5|환영합니다.`)  # 으로 시작하는 줄은 주석입니다.")
    if err:
        st.info("notice.txt를 불러온 후 이용하세요.")
    else:
        full_edit = st.text_area("notice.txt 전체 내용", value=content or "", height=400, key="notice_full_edit")
        if st.button("저장"):
            write_err = write_notice(full_edit)
            if write_err:
                queue_feedback("error", f"저장 실패: {write_err}")
            else:
                queue_feedback("success", "notice.txt 를 저장했습니다. 서버는 다음 주기에 새 내용을 읽습니다.")
            st.rerun()

elif _notice_main_i == 5:
    st.subheader("⏰ 이벤트 공지사항 (타임 이벤트)")
    st.caption("게임 내 **타임 이벤트** 시작 시각과 지속 시간을 설정합니다. `lineage.conf`의 `time_event_time`, `time_event_play_time`을 수정합니다. 서버 재시작 후 적용됩니다.")
    conf_content, conf_err = read_lineage_conf()
    if conf_err:
        st.error(f"lineage.conf 읽기 실패: {conf_err}")
        st.info(f"경로: `{LINEAGE_CONF_PATH}`")
    else:
        time_event_raw = get_conf_value(conf_content, "time_event_time")
        play_time_raw = get_conf_value(conf_content, "time_event_play_time").strip()
        time_ment_raw = get_conf_value(conf_content, "time_ment").strip()
        event_times = parse_time_event_time(time_event_raw)
        try:
            play_time_sec = int(play_time_raw) if play_time_raw else 600
        except ValueError:
            play_time_sec = 600
        try:
            time_ment_sec = int(time_ment_raw) if time_ment_raw else 60
        except ValueError:
            time_ment_sec = 60

        _ev_sub_labels = ["📋 이벤트 시각 목록·수정·삭제", "➕ 시각 추가", "⚙️ 지속 시간·공지 주기"]
        _ev_sub_i = gm_section_tabs("notice_event_sub", _ev_sub_labels)
        if _ev_sub_i == 0:
            st.caption("타임 이벤트가 **시작되는 시각** 목록 (시:분). 서버가 이 시각에 맞춰 이벤트를 시작합니다.")
            if not event_times:
                st.info("등록된 이벤트 시각이 없습니다. '시각 추가' 탭에서 추가하세요.")
            else:
                edit_idx = st.session_state.get("event_editing_idx", None)
                for pos, (h, m) in enumerate(event_times):
                    col1, col2, col3 = st.columns([1, 2, 1])
                    with col1:
                        st.text(f"{h}:{m}")
                    with col2:
                        st.caption("시:분")
                    with col3:
                        if edit_idx == pos:
                            if st.button("취소", key=f"ev_cancel_{pos}"):
                                st.session_state.pop("event_editing_idx", None)
                                st.session_state.pop("event_edit_h", None)
                                st.session_state.pop("event_edit_m", None)
                                st.rerun()
                        else:
                            if st.button("수정", key=f"ev_edit_{pos}"):
                                st.session_state["event_editing_idx"] = pos
                                st.session_state["event_edit_h"] = h
                                st.session_state["event_edit_m"] = m
                                st.rerun()
                            if st.button("삭제", key=f"ev_del_{pos}"):
                                new_times = event_times[:pos] + event_times[pos + 1 :]
                                new_str = format_time_event_time(new_times)
                                new_content = update_conf_key_in_content(conf_content, "time_event_time", new_str)
                                try:
                                    with open(LINEAGE_CONF_PATH, "w", encoding="utf-8", newline="") as f:
                                        f.write(new_content)
                                    queue_feedback("success", "해당 시각을 삭제하고 저장했습니다.")
                                except Exception as e:
                                    queue_feedback("error", f"저장 실패: {e}")
                                st.rerun()
                if edit_idx is not None and 0 <= edit_idx < len(event_times):
                    with st.expander("✏️ 시각 수정", expanded=True):
                        nh = st.text_input("시 (0~23)", value=st.session_state.get("event_edit_h", event_times[edit_idx][0]), key="ev_edit_h_input")
                        nm = st.text_input("분 (0~59)", value=st.session_state.get("event_edit_m", event_times[edit_idx][1]), key="ev_edit_m_input")
                        if st.button("반영"):
                            try:
                                hh, mm = int(nh), int(nm)
                                if 0 <= hh <= 23 and 0 <= mm <= 59:
                                    new_times = event_times[:]
                                    new_times[edit_idx] = (f"{hh:02d}", f"{mm:02d}")
                                    new_str = format_time_event_time(new_times)
                                    new_content = update_conf_key_in_content(conf_content, "time_event_time", new_str)
                                    with open(LINEAGE_CONF_PATH, "w", encoding="utf-8", newline="") as f:
                                        f.write(new_content)
                                    st.session_state.pop("event_editing_idx", None)
                                    queue_feedback("success", "이벤트 시각을 수정하고 저장했습니다.")
                                else:
                                    queue_feedback("error", "시(0~23), 분(0~59) 범위로 입력하세요.")
                            except ValueError:
                                queue_feedback("error", "시·분은 숫자로 입력하세요.")
                            st.rerun()
        elif _ev_sub_i == 1:
            st.caption("새 **이벤트 시작 시각**을 추가합니다 (시:분).")
            add_h = st.number_input("시 (0~23)", min_value=0, max_value=23, value=12, key="ev_add_h")
            add_m = st.number_input("분 (0~59)", min_value=0, max_value=59, value=0, key="ev_add_m")
            if st.button("시각 추가 후 저장"):
                new_times = event_times + [(f"{add_h:02d}", f"{add_m:02d}")]
                new_str = format_time_event_time(new_times)
                new_content, write_err = write_conf_key(conf_content, "time_event_time", new_str)
                if write_err:
                    queue_feedback("error", write_err)
                else:
                    queue_feedback("success", f"시각 {add_h:02d}:{add_m:02d} 를 추가하고 저장했습니다.")
                st.rerun()
        else:
            st.caption("**이벤트 지속 시간**(초)과 **이벤트 안내 공지 주기**(초)를 설정합니다.")
            new_play = st.number_input("이벤트 지속 시간 (초)", min_value=60, max_value=86400, value=play_time_sec, key="ev_play_sec", help="타임 이벤트가 몇 초 동안 유지되는지 (기본 3600=1시간)")
            new_ment = st.number_input("이벤트 안내 공지 주기 (초)", min_value=10, max_value=600, value=time_ment_sec, key="ev_ment_sec", help="게임 내 '타임이벤트 N분' 안내가 몇 초마다 뜨는지")
            if st.button("지속 시간·공지 주기 저장"):
                updated = update_conf_key_in_content(conf_content, "time_event_play_time", str(new_play))
                updated = update_conf_key_in_content(updated, "time_ment", str(new_ment))
                try:
                    with open(LINEAGE_CONF_PATH, "w", encoding="utf-8", newline="") as f:
                        f.write(updated)
                    queue_feedback("success", "지속 시간과 공지 주기를 저장했습니다. 서버 재시작 후 적용됩니다.")
                except Exception as e:
                    queue_feedback("error", f"저장 실패: {e}")
                st.rerun()

else:
    st.subheader("📡 이 페이지(notice.txt)에 없는 월드 채팅 공지들")
    st.markdown(
        """
서버는 아래도 **별도로** 전체 채팅에 메시지를 올립니다. 삭제·수정은 **대부분 `lineage.conf` 또는 Java 소스**에서 해야 합니다.

| 출처 | 내용 예시 | 끄거나 바꾸는 법 |
|------|-----------|-------------------|
| **`lineage.conf`** `time_event_time` 등 | 타임이벤트(메티스 멘트, 주사위) | 이 페이지 **「이벤트 공지사항」** 탭에서 시각 삭제 또는 `time_event_time` 비우기 |
| **`lineage.conf`** `is_world_clean`, `world_clean_time` | 월드맵 청소 카운트다운 | `is_world_clean = false` |
| **Java** `WorldBossController` | 월드보스 레이드 안내 | 소스 수정 또는 GM 명령 비활성 |
| **Java** `NoticeController` + `lineage.conf` | 기란 공성전 시간 안내 | `is_kingdom_war_notice = false` 등 |
| **Java** `CommandController` (오픈 대기 종료 시) | 서버 오픈 환영 멘트 일괄 | 소스의 해당 문자열 수정 |
| **`lineage.conf`** `open_wait` | 오픈 대기중 테스트 서버 멘트 | `open_wait` 관련 설정 끄기 |
| **DB** `server_notice` | 접속 시 팝업 공지 | 이 페이지 **「공지 팝업 (접속)」** 탭 |

**출석체크**는 `lineage.conf`에 `attendance_check_enabled = false` 로 두면 NPC·접속시간 알림이 꺼집니다. (기본 반영됨)
"""
    )

st.divider()
st.caption(f"공지: `{NOTICE_PATH}`  |  이벤트: `{LINEAGE_CONF_PATH}`  |  각 공지는 설정한 간격(분)마다 따로 출력됩니다.")
