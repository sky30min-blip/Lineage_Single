"""
서버 리로드 - GM 툴에서 DB 반영 후 서버에 리로드 명령 전송
서버가 gm_server_command 테이블을 주기적으로 확인해 reload 명령을 실행합니다.
"""
import streamlit as st
from utils.db_manager import get_db

st.set_page_config(page_title="서버 리로드", page_icon="🔄", layout="wide")
st.title("🔄 서버 리로드")

st.caption("DB를 수정한 뒤 **서버가 새 데이터를 읽도록** 리로드 명령을 보냅니다. 서버가 주기적으로 `gm_server_command`를 확인해 실행합니다.")

db = get_db()

# 리로드 종류 (command='reload', param=아래 키)
RELOAD_OPTIONS = [
    ("npc", "npc 테이블 리로드", "NPC 정의·스폰 목록. NPC 배치/위치 수정 후 사용"),
    ("item", "item 테이블 리로드", "아이템 정의. 아이템 추가/수정 후 사용"),
    ("monster", "monster 테이블 리로드", "몬스터 정의"),
    ("monster_drop", "monster_drop 테이블 리로드", "몬스터 드롭 테이블"),
    ("monster_skill", "monster_skill 테이블 리로드", "몬스터 스킬"),
    ("npc_shop", "npc_shop 테이블 리로드", "NPC 상점 목록"),
    ("background_spawnlist", "background_spawnlist 테이블 리로드", "배경 스폰"),
]

if "reload_feedback" in st.session_state:
    msg_type, msg_text = st.session_state["reload_feedback"]
    if msg_type == "success":
        st.success(msg_text)
    else:
        st.error(msg_text)
    del st.session_state["reload_feedback"]

st.subheader("리로드 실행")
for param_key, label, desc in RELOAD_OPTIONS:
    col1, col2 = st.columns([3, 1])
    with col1:
        st.markdown(f"**{label}**")
        st.caption(desc)
    with col2:
        if st.button("실행", key=f"reload_{param_key}", help=f"서버에 reload:{param_key} 요청을 넣습니다. {desc}"):
            ok = db.execute_query(
                "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                ("reload", param_key)
            )
            if ok:
                st.session_state["reload_feedback"] = ("success", f"✅ '{label}' 요청됨. 서버가 곧 처리합니다. 서버 콘솔에서 '[gm_server_command] reload: ...' 로그 확인.")
            else:
                st.session_state["reload_feedback"] = ("error", "❌ 명령 삽입 실패 (gm_server_command 테이블 확인)")
            st.rerun()
    st.divider()

st.subheader("안내")
st.info("""
- **서버가 실행 중**이어야 합니다. 서버 프로그램이 주기적으로 `gm_server_command`를 조회해 `command='reload'`, `param='npc'` 등으로 리로드합니다.
- 처리된 행은 `executed=1`로 갱신됩니다.
- `gm_server_command` 테이블이 없으면 서버에서 생성하거나, 서버 창 메뉴 **[명령어|이벤트|리로드] → [리로드]** 에서 수동으로 실행하세요.
""")
