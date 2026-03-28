"""
리니지 싱글 서버 GM 툴 - 서버 모니터링 페이지
실시간 통계, DB 현황 (배율 수정은 서버 관리 → 서버 배율·최고레벨 탭)
"""

import streamlit as st
import pandas as pd
import plotly.express as px
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.gm_tabs import gm_section_tabs

st.set_page_config(page_title="서버 모니터링", page_icon="🖥️", layout="wide")

# lineage.conf 경로 주석: pages/12_xxx.py → gm_tool → Lineage_Single → 2.싱글리니지 팩

db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

show_pending_feedback()

st.title("🖥️ 서버 모니터링")
st.caption(
    "DB 기준 요약 통계입니다. **경험치·드랍 등 배율**은 **서버 관리 → 「📊 서버 배율·최고레벨」** 탭에서 수정하세요."
)


def _safe_count(db, table: str, default=0) -> int:
    try:
        row = db.fetch_one(f"SELECT COUNT(*) AS c FROM `{table}`")
        return int(row["c"]) if row and "c" in row else default
    except Exception:
        return default


_MON_TAB_LABELS = ["📊 실시간 통계", "💾 데이터베이스 현황"]
_srvmon_ti = gm_section_tabs("server_monitor", _MON_TAB_LABELS)

if _srvmon_ti == 0:
    st.subheader("실시간 통계")
    placeholder = st.empty()
    with placeholder.container():
        st.write("**서버 기본 정보**")
        col1, col2, col3, col4 = st.columns(4)
        with col1:
            cnt_char = _safe_count(db, "characters")
            st.metric("총 캐릭터 수", f"{cnt_char:,}")
        with col2:
            cnt_monster = _safe_count(db, "monster_spawnlist")
            st.metric("총 몬스터 수", f"{cnt_monster:,}")
        with col3:
            cnt_npc = _safe_count(db, "npc_spawnlist")
            st.metric("총 NPC 수", f"{cnt_npc:,}")
        with col4:
            cnt_item = _safe_count(db, "item")
            st.metric("총 아이템 종류", f"{cnt_item:,}")
        if st.button("🔄 통계 새로고침", key="server_mon_refresh"):
            queue_feedback("info", "📊 통계를 새로고침했습니다.")
            st.rerun()
        st.caption("위 버튼으로 최신 수치를 갱신할 수 있습니다.")

else:
    st.subheader("데이터베이스 현황")
    # 싱글 DB는 `item` 한 테이블에 무기·방어구가 있고 `weapon`/`armor` 테이블이 없을 수 있음
    _want = [
        "accounts",
        "characters",
        "characters_inventory",
        "monster_spawnlist",
        "npc_spawnlist",
        "item",
        "weapon",
        "armor",
    ]
    _have = set(db.get_all_tables())
    _parts = [
        f"SELECT '{t}' AS 테이블, COUNT(*) AS 레코드수 FROM `{t}`"
        for t in _want
        if t in _have
    ]
    try:
        rows = db.fetch_all(" UNION ALL ".join(_parts)) if _parts else []
        if rows:
            df = pd.DataFrame(rows)
            st.dataframe(df, hide_index=True, width="stretch")
            fig = px.bar(df, x="테이블", y="레코드수", title="주요 테이블 레코드 수")
            fig.update_layout(xaxis_tickangle=-45)
            st.plotly_chart(fig, width="stretch")
        else:
            st.warning("조회 결과가 없습니다.")
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")
