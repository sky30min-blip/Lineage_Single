"""
서버 리로드 - GM 툴에서 DB 반영 후 서버에 리로드 명령 전송
서버가 gm_server_command 테이블을 주기적으로 확인해 reload 명령을 실행합니다.
"""
import streamlit as st
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback

st.set_page_config(page_title="서버 리로드", page_icon="🔄", layout="wide")
st.title("🔄 서버 리로드")

st.caption("DB를 수정한 뒤 **서버가 새 데이터를 읽도록** 리로드 명령을 보냅니다. 서버가 주기적으로 `gm_server_command`를 확인해 실행합니다.")

db = get_db()
show_pending_feedback()

# 리로드 종류 (command='reload', param=아래 키)
RELOAD_OPTIONS = [
    ("npc", "npc 테이블 리로드", "NPC 정의·스폰 목록. NPC 배치/위치 수정 후 사용"),
    ("item", "item 테이블 리로드", "아이템 정의. 아이템 추가/수정 후 사용"),
    ("monster", "monster 테이블 리로드", "몬스터 정의"),
    ("monster_spawnlist", "전체스폰 리로드", "monster_spawnlist 기준으로 월드 일반 몬스터 스폰을 재적용"),
    ("monster_drop", "monster_drop 테이블 리로드", "몬스터 드롭 테이블"),
    ("monster_skill", "monster_skill 테이블 리로드", "몬스터 스킬"),
    ("npc_shop", "npc_shop 테이블 리로드", "NPC 상점 목록"),
    ("background_spawnlist", "background_spawnlist 테이블 리로드", "배경 스폰"),
]

st.subheader("리로드 실행")
for param_key, label, desc in RELOAD_OPTIONS:
    col1, col2 = st.columns([3, 1])
    with col1:
        st.markdown(f"**{label}**")
        st.caption(desc)
    with col2:
        if st.button("실행", key=f"reload_{param_key}", help=f"서버에 reload:{param_key} 요청을 넣습니다. {desc}"):
            ok, err = db.execute_query_ex(
                "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                ("reload", param_key),
            )
            if ok:
                queue_feedback(
                    "success",
                    f"✅ '{label}' 리로드 요청이 큐에 등록되었습니다. 서버 콘솔에서 '[gm_server_command] reload: ...' 로그를 확인하세요.",
                )
            else:
                queue_feedback("error", f"❌ 명령 삽입 실패: {err}")
            st.rerun()
    st.divider()

st.subheader("안내")
st.info("""
- **서버가 실행 중**이어야 합니다. 서버 프로그램이 주기적으로 `gm_server_command`를 조회해 `command='reload'`, `param='npc'` 등으로 리로드합니다.
- 처리된 행은 `executed=1`로 갱신됩니다.
- `gm_server_command` 테이블이 없으면 서버에서 생성하거나, 서버 창 메뉴 **[명령어|이벤트|리로드] → [리로드]** 에서 수동으로 실행하세요.
""")

st.warning(
    "⚠️ `전체스폰 리로드(param=monster_spawnlist)`는 서버 코드가 해당 param을 지원해야 동작합니다. "
    "서버 로그에 `reload: 알 수 없는 param=monster_spawnlist`가 나오면, "
    "게임 내 `.리로드 전체스폰`을 사용하거나 서버 업데이트/재시작 후 다시 시도하세요."
)

st.caption("**시즌 원클릭 초기화(계정 유지)** 는 **서버 관리** 페이지의 **「시즌 초기화」** 탭으로 옮겼습니다.")
