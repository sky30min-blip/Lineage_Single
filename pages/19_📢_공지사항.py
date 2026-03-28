"""
공지사항 - notice.txt 편집 (월드 채팅 주기 공지)
각 공지마다 출력 간격(분)을 따로 설정할 수 있습니다. 형식: 분|메시지
"""
import os
import streamlit as st
from utils.gm_feedback import show_pending_feedback, queue_feedback

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

content, err = read_notice()
if err:
    st.warning(f"notice.txt 읽기 실패: {err}  → 아래 '공지사항' 탭은 비활성화됩니다. 경로: `{NOTICE_PATH}`")
    entries = []
    notice_indices = []
else:
    entries = parse_entries(content or "")
    notice_indices = [i for i, e in enumerate(entries) if e["type"] == "notice"]

# 탭: 목록·수정·삭제 / 추가 / 전체 편집 / 이벤트 공지사항 / 기타(코드·설정)
tab1, tab2, tab3, tab4, tab5 = st.tabs(
    ["📋 목록 보기·수정·삭제", "➕ 공지 추가", "✏️ 전체 편집", "⏰ 이벤트 공지사항", "📡 기타 자동 공지 (코드)"]
)

with tab1:
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

with tab2:
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

with tab3:
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

with tab4:
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

        sub_tab_a, sub_tab_b, sub_tab_c = st.tabs(["📋 이벤트 시각 목록·수정·삭제", "➕ 시각 추가", "⚙️ 지속 시간·공지 주기"])
        with sub_tab_a:
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
        with sub_tab_b:
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
        with sub_tab_c:
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

with tab5:
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
| **DB** `server_notice` 등 | 접속 시 팝업 공지 | DB/GMTool 다른 메뉴 |

**출석체크**는 `lineage.conf`에 `attendance_check_enabled = false` 로 두면 NPC·접속시간 알림이 꺼집니다. (기본 반영됨)
"""
    )

st.divider()
st.caption(f"공지: `{NOTICE_PATH}`  |  이벤트: `{LINEAGE_CONF_PATH}`  |  각 공지는 설정한 간격(분)마다 따로 출력됩니다.")
