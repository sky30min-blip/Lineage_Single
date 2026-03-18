"""
리니지 싱글 서버 GM 툴 - 서버 모니터링 페이지
실시간 통계, 배율 정보, DB 현황
"""

import os
import streamlit as st
import pandas as pd
import plotly.express as px
from utils.db_manager import get_db

# lineage.conf 경로: pages/08_xxx.py → gm_tool → Lineage_Single → 2.싱글리니지 팩/lineage.conf
def _get_lineage_conf_path():
    base = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    return os.path.join(base, "2.싱글리니지 팩", "lineage.conf")

RATE_KEYS = ("rate_exp", "rate_aden", "rate_drop", "rate_enchant", "rate_party")
RATE_DEFAULTS = {"rate_exp": 10.0, "rate_aden": 50.0, "rate_drop": 2.0, "rate_enchant": 5.0, "rate_party": 1.2}


def _parse_rates_from_conf(path: str) -> dict:
    """lineage.conf에서 배율 키=값 파싱. 없으면 기본값."""
    result = dict(RATE_DEFAULTS)
    if not os.path.isfile(path):
        return result
    try:
        with open(path, "r", encoding="utf-8", errors="replace") as f:
            for line in f:
                line = line.rstrip("\n\r")
                if line.startswith("#") or "=" not in line:
                    continue
                pos = line.find("=")
                key = line[:pos].strip()
                if key in RATE_KEYS:
                    try:
                        result[key] = float(line[pos + 1 :].strip())
                    except ValueError:
                        pass
    except Exception:
        pass
    return result


def _write_rates_to_conf(path: str, rates: dict) -> bool:
    """lineage.conf에서 해당 키의 값만 교체하여 저장."""
    if not os.path.isfile(path):
        return False
    try:
        with open(path, "r", encoding="utf-8", errors="replace") as f:
            lines = f.readlines()
        new_lines = []
        for line in lines:
            if "=" in line and not line.strip().startswith("#"):
                pos = line.find("=")
                key = line[:pos].strip()
                if key in RATE_KEYS and key in rates:
                    new_lines.append(line[: pos + 1] + " " + str(rates[key]) + "\n")
                    continue
            new_lines.append(line)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.writelines(new_lines)
        return True
    except Exception:
        return False

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()


def _safe_count(db, table: str, default=0) -> int:
    """테이블 레코드 수 조회, 실패 시 default 반환."""
    try:
        row = db.fetch_one(f"SELECT COUNT(*) AS c FROM `{table}`")
        return int(row["c"]) if row and "c" in row else default
    except Exception:
        return default


# 탭 구성
tab1, tab2, tab3 = st.tabs([
    "📊 실시간 통계",
    "⚙️ 서버 배율 정보",
    "💾 데이터베이스 현황",
])

# ========== 탭 1: 실시간 통계 ==========
with tab1:
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
            st.rerun()
        st.caption("위 버튼으로 최신 수치를 갱신할 수 있습니다.")

# ========== 탭 2: 서버 배율 정보 ==========
with tab2:
    st.subheader("서버 배율 정보")
    conf_path = _get_lineage_conf_path()
    current_rates = _parse_rates_from_conf(conf_path)

    st.write("**현재 배율** (lineage.conf 기준)")
    st.info(
        f"**경험치:** {current_rates['rate_exp']}배  |  **아데나:** {current_rates['rate_aden']}배  |  "
        f"**드랍:** {current_rates['rate_drop']}배  |  **인챈트:** {current_rates['rate_enchant']}배  |  "
        f"**파티:** {current_rates['rate_party']}배"
    )

    st.write("**배율 수정**")
    c1, c2 = st.columns(2)
    with c1:
        new_exp = st.number_input("경험치 배율", min_value=0.1, max_value=1000.0, value=float(current_rates["rate_exp"]), step=0.1, key="rate_exp")
        new_aden = st.number_input("아데나 배율", min_value=0.1, max_value=1000.0, value=float(current_rates["rate_aden"]), step=0.1, key="rate_aden")
        new_drop = st.number_input("드랍 배율", min_value=0.1, max_value=1000.0, value=float(current_rates["rate_drop"]), step=0.1, key="rate_drop")
    with c2:
        new_enchant = st.number_input("인챈트 배율", min_value=0.1, max_value=1000.0, value=float(current_rates["rate_enchant"]), step=0.1, key="rate_enchant")
        new_party = st.number_input("파티 배율", min_value=0.1, max_value=100.0, value=float(current_rates["rate_party"]), step=0.1, key="rate_party")

    if st.button("배율 저장", key="save_rates"):
        if not os.path.isfile(conf_path):
            st.error(f"❌ 설정 파일을 찾을 수 없습니다: {conf_path}")
        else:
            new_rates = {
                "rate_exp": new_exp,
                "rate_aden": new_aden,
                "rate_drop": new_drop,
                "rate_enchant": new_enchant,
                "rate_party": new_party,
            }
            if _write_rates_to_conf(conf_path, new_rates):
                st.success("저장 완료! **서버 재시작 후** 적용됩니다.")
                st.rerun()
            else:
                st.error("❌ 저장에 실패했습니다. 파일 경로와 쓰기 권한을 확인하세요.")

    st.caption("배율은 서버 설정 파일(lineage.conf)에서 읽고 씁니다.")
    st.caption(f"설정 파일 위치: `{conf_path}`")

# ========== 탭 3: 데이터베이스 현황 ==========
with tab3:
    st.subheader("데이터베이스 현황")
    sql_tables = """
        SELECT 'accounts' AS 테이블, COUNT(*) AS 레코드수 FROM accounts
        UNION ALL SELECT 'characters', COUNT(*) FROM characters
        UNION ALL SELECT 'characters_inventory', COUNT(*) FROM characters_inventory
        UNION ALL SELECT 'monster_spawnlist', COUNT(*) FROM monster_spawnlist
        UNION ALL SELECT 'npc_spawnlist', COUNT(*) FROM npc_spawnlist
        UNION ALL SELECT 'item', COUNT(*) FROM item
        UNION ALL SELECT 'weapon', COUNT(*) FROM weapon
        UNION ALL SELECT 'armor', COUNT(*) FROM armor
    """
    try:
        rows = db.fetch_all(sql_tables.strip())
        if rows:
            df = pd.DataFrame(rows)
            st.dataframe(df, hide_index=True)
            # Plotly 막대 그래프
            fig = px.bar(df, x="테이블", y="레코드수", title="주요 테이블 레코드 수")
            fig.update_layout(xaxis_tickangle=-45)
            st.plotly_chart(fig)
        else:
            st.warning("조회 결과가 없습니다.")
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")

