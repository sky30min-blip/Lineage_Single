"""
리니지 싱글 서버 GM 툴 - 통계 분석 페이지
레벨/직업 통계, 랭킹, 서버 통계
"""

import streamlit as st
import pandas as pd
import plotly.express as px
from utils.db_manager import get_db
import config

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

tab1, tab2, tab3, tab4 = st.tabs([
    "📊 레벨 통계",
    "👤 직업 통계",
    "🏆 랭킹",
    "🖥️ 서버 통계",
])

# ========== 탭 1: 레벨 통계 ==========
with tab1:
    st.subheader("레벨 통계")

    try:
        level_rows = db.fetch_all(
            "SELECT level, COUNT(*) as count FROM characters GROUP BY level ORDER BY level"
        )
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")
        level_rows = []

    if level_rows:
        df_level = pd.DataFrame(level_rows)
        df_level = df_level.rename(columns={"level": "레벨", "count": "캐릭터 수"})

        fig = px.bar(
            df_level, x="레벨", y="캐릭터 수",
            title="레벨별 캐릭터 분포",
            color_discrete_sequence=["#1f77b4"],
        )
        fig.update_layout(xaxis_title="레벨", yaxis_title="캐릭터 수")
        st.plotly_chart(fig, use_container_width=True)

        st.subheader("레벨 구간별 통계")
        total = df_level["캐릭터 수"].sum()
        ranges = [
            (1, 20, "1~20레벨"),
            (21, 40, "21~40레벨"),
            (41, 60, "41~60레벨"),
            (61, 80, "61~80레벨"),
            (81, 99, "81~99레벨"),
        ]
        range_data = []
        for low, high, label in ranges:
            cnt = int(df_level[(df_level["레벨"] >= low) & (df_level["레벨"] <= high)]["캐릭터 수"].sum())
            range_data.append({"구간": label, "인원(명)": cnt})
        range_df = pd.DataFrame(range_data)
        st.dataframe(range_df, use_container_width=True, hide_index=True)
    else:
        st.info("데이터가 없습니다.")

# ========== 탭 2: 직업 통계 ==========
with tab2:
    st.subheader("직업 통계")

    try:
        class_rows = db.fetch_all(
            "SELECT class, COUNT(*) as count FROM characters GROUP BY class ORDER BY class"
        )
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")
        class_rows = []

    if class_rows:
        df_class = pd.DataFrame(class_rows)
        df_class["직업명"] = df_class["class"].map(
            lambda x: config.CLASS_NAMES.get(int(x) if x is not None else 0, f"직업{x}")
        )

        fig2 = px.pie(
            df_class, values="count", names="직업명",
            title="직업별 캐릭터 분포",
            hole=0.3,
        )
        st.plotly_chart(fig2, use_container_width=True)

        st.subheader("직업별 평균 레벨")
        try:
            avg_rows = db.fetch_all(
                "SELECT class, AVG(level) as avg_level, COUNT(*) as count FROM characters GROUP BY class ORDER BY class"
            )
            if avg_rows:
                df_avg = pd.DataFrame(avg_rows)
                df_avg["직업"] = df_avg["class"].map(
                    lambda x: config.CLASS_NAMES.get(int(x) if x is not None else 0, str(x))
                )
                df_avg["평균 레벨"] = df_avg["avg_level"].round(1)
                df_avg = df_avg[["직업", "평균 레벨", "count"]].rename(columns={"count": "캐릭터 수"})
                st.dataframe(df_avg, use_container_width=True, hide_index=True)
            else:
                st.info("데이터가 없습니다.")
        except Exception as e:
            st.warning(f"직업별 평균 레벨 조회 실패: {e}")
    else:
        st.info("데이터가 없습니다.")

# ========== 탭 3: 랭킹 ==========
with tab3:
    st.subheader("랭킹")

    st.write("**레벨 순위 TOP 10**")
    try:
        level_top = db.fetch_all(
            "SELECT name, level, class FROM characters ORDER BY level DESC LIMIT 10"
        )
        if level_top:
            df_rank = pd.DataFrame(level_top)
            df_rank["순위"] = range(1, len(df_rank) + 1)
            df_rank["직업"] = df_rank["class"].map(
                lambda x: config.CLASS_NAMES.get(int(x) if x is not None else 0, str(x))
            )
            df_rank = df_rank[["순위", "name", "level", "직업"]].rename(
                columns={"name": "캐릭터명", "level": "레벨"}
            )
            st.dataframe(df_rank, use_container_width=True, hide_index=True)
        else:
            st.info("데이터가 없습니다.")
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")

    st.write("**아데나 순위 TOP 10**")
    try:
        adena_top = db.fetch_all("""
            SELECT cha_name, SUM(count) as total_adena
            FROM characters_inventory
            WHERE name LIKE %s OR name LIKE %s
            GROUP BY cha_name
            ORDER BY total_adena DESC
            LIMIT 10
        """, ("%아데나%", "%adena%"))
        if adena_top:
            df_adena = pd.DataFrame(adena_top)
            df_adena["순위"] = range(1, len(df_adena) + 1)
            df_adena = df_adena[["순위", "cha_name", "total_adena"]].rename(
                columns={"cha_name": "캐릭터명", "total_adena": "아데나"}
            )
            st.dataframe(df_adena, use_container_width=True, hide_index=True)
        else:
            st.info("데이터가 없습니다.")
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")

    st.write("**PK 순위 TOP 10**")
    try:
        pk_top = db.fetch_all(
            "SELECT name, pkcount FROM characters WHERE pkcount > 0 ORDER BY pkcount DESC LIMIT 10"
        )
        if pk_top:
            df_pk = pd.DataFrame(pk_top)
            df_pk["순위"] = range(1, len(df_pk) + 1)
            df_pk = df_pk[["순위", "name", "pkcount"]].rename(
                columns={"name": "캐릭터명", "pkcount": "PK 수"}
            )
            st.dataframe(df_pk, use_container_width=True, hide_index=True)
        else:
            st.info("PK 데이터가 없습니다.")
    except Exception:
        st.caption("pkcount 컬럼이 없거나 조회할 수 없습니다.")

# ========== 탭 4: 서버 통계 ==========
with tab4:
    st.subheader("서버 통계")

    try:
        acc_count = db.fetch_one("SELECT COUNT(*) as c FROM accounts")
        char_count = db.fetch_one("SELECT COUNT(*) as c FROM characters")
        avg_level = db.fetch_one("SELECT AVG(level) as c FROM characters")
        max_level = db.fetch_one("SELECT MAX(level) as c FROM characters")
        min_level = db.fetch_one("SELECT MIN(level) as c FROM characters")
    except Exception as e:
        st.error(f"❌ 조회 실패: {e}")
        acc_count = char_count = avg_level = max_level = min_level = None

    if acc_count is not None and char_count is not None:
        gm_count = 0
        try:
            gm_row = db.fetch_one("SELECT COUNT(*) as c FROM accounts WHERE access_level >= 200")
            if gm_row:
                gm_count = gm_row.get("c") or 0
        except Exception:
            pass

        c1, c2, c3, c4 = st.columns(4)
        with c1:
            st.metric("총 계정 수", acc_count.get("c", 0))
        with c2:
            st.metric("총 캐릭터 수", char_count.get("c", 0))
        with c3:
            st.metric("GM 계정 수", gm_count)
        with c4:
            avg_val = avg_level.get("c") if avg_level else None
            st.metric("평균 레벨", f"{float(avg_val):.1f}" if avg_val is not None else "-")

        st.subheader("레벨 통계")
        d1, d2 = st.columns(2)
        with d1:
            st.metric("최고 레벨", max_level.get("c", "-") if max_level else "-")
        with d2:
            st.metric("최저 레벨", min_level.get("c", "-") if min_level else "-")

        st.subheader("접속 통계")
        try:
            today_login = db.fetch_one(
                "SELECT COUNT(*) as c FROM characters WHERE DATE(last_login) = CURDATE()"
            )
            if today_login is not None:
                st.metric("오늘 접속 캐릭터 수", today_login.get("c", 0))
            else:
                st.caption("접속 통계를 조회할 수 없습니다.")
        except Exception:
            st.caption("last_login 컬럼이 없어 접속 통계를 표시하지 않습니다.")
    else:
        st.info("데이터가 없습니다.")
