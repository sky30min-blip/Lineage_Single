"""
파워볼 일일 통계·캐릭터 누적·클래스별 포상 정산 (GM 툴).
"""
from datetime import date, timedelta

import pandas as pd
import streamlit as st
from utils.db_manager import get_db
from utils.powerball_economy import (
    PAYOUT_RATE,
    build_reward_preview,
    execute_daily_rewards,
    fetch_character_lifetime_stats,
    fetch_daily_summary,
    reward_run_exists,
    split_four_class_pool,
    split_royal_pool,
)
from utils.table_schemas import get_create_sql

st.set_page_config(page_title="파워볼 관리", page_icon="🎱", layout="wide")
st.title("🎱 파워볼 관리")
st.caption(
    f"배당률 **{PAYOUT_RATE}배**는 서버 `PowerballController`와 동일하게 집계합니다. "
    "일자는 **한국 날짜**로 고르고, `powerball_results.created_at`이 **DB에 KST 그대로** 저장된다고 가정합니다."
)

db = get_db()
ok, msg = db.test_connection()
if not ok:
    st.error(f"DB 연결 실패: {msg}")
    st.stop()

tables = db.get_all_tables()
need_pb = ("powerball_bets" in tables) and ("powerball_results" in tables)
if not need_pb:
    st.warning("`powerball_bets` / `powerball_results` 테이블이 없습니다. 서버 DB에 파워볼 스키마를 먼저 적용하세요.")
    st.stop()

with st.expander("📌 설계·제한 사항 (요청하신 규칙과 구현 해석)", expanded=False):
    st.markdown(
        f"""
        **일일 서버 손익**  
        - 해당 날짜에 **결과가 기록된 회차**만 집계합니다 (`powerball_results.created_at` 구간).  
        - `배팅 합 − 당첨 지급액(배팅×{PAYOUT_RATE}, 반올림은 DB `ROUND` 기준)` = **서버 순이익**으로 봅니다.

        **22% / 12% 분배 해석**  
        - 기사·법사·요정·다크엘프 **네 직업이 합쳐 서버 순이익의 22%** 를 받고, 그중 **직업당 1/4씩** 나눕니다.  
        - 각 직업 풀 안에서 1·2·3위에게 **12 : 7 : 3** 비율로 나눕니다.  
        - 군주는 **순이익의 12%** 를 한 풀로 모아 1·2·3위에게 **7 : 3 : 2** 비율로 나눕니다.

        **순위 기준**  
        - 기본: 그날 **서버 이익에 기여한 양**(배팅합 − 당첨지급액)이 큰 순 — 동점이면 배팅 합이 큰 쪽.  
        - 옵션으로 **총 배팅액**만으로 순위 매기기도 가능합니다.

        **제외 직업**  
        - 용기사·환술사 등은 말씀에 없어 **포상 순위에서 제외**했습니다.

        **자정 자동 지급**  
        - 이 페이지는 **수동 실행**입니다. 매일 0시 자동 지급은 **OS 작업 스케줄러 + 스크립트** 또는 추후 서버 타이머 구현이 필요합니다.

        **아데나 지급**  
        - `characters_inventory`에서 **`이름='아데나'`인 첫 스택**에 수량을 더합니다.  
        - 아데나 스택이 없으면 해당 캐릭터는 실패로 남고, 원장(`powerball_reward_line`)은 이미 쌓일 수 있으니 수동 처리하세요.
        """
    )

# 보상 원장 테이블
if "powerball_reward_run" not in tables or "powerball_reward_line" not in tables:
    st.info("포상 중복 방지용 테이블이 없습니다. 아래에서 생성할 수 있습니다.")
    if st.button("powerball_reward_run / powerball_reward_line 생성"):
        sql1 = get_create_sql("powerball_reward_run")
        sql2 = get_create_sql("powerball_reward_line")
        if db.execute_query(sql1, None) and db.execute_query(sql2, None):
            st.success("생성 요청을 보냈습니다. 페이지를 새로고침하세요.")
            st.rerun()
        else:
            st.error("생성 실패 — DB 로그를 확인하세요.")

tab1, tab2, tab3, tab4 = st.tabs(
    ["📅 일일 손익", "👤 캐릭터 누적", "🏆 일일 포상 정산", "📜 정산 이력"]
)

if "pb_daily_date" not in st.session_state:
    st.session_state.pb_daily_date = date.today()

with tab1:
    st.subheader("일일 서버 vs 유저(이론 지급액 기준)")
    # key=pb_daily_date 위젯보다 먼저만 session_state를 바꿀 수 있음 (버튼은 플래그+rerun)
    if st.session_state.pop("_pb_go_yesterday", False):
        st.session_state["pb_daily_date"] = date.today() - timedelta(days=1)

    c1, c2 = st.columns(2)
    with c1:
        d1 = st.date_input("조회일 (KST 달력)", key="pb_daily_date")
    with c2:
        st.write("")  # 날짜 입력과 버튼 높이 맞춤
        if st.button("어제로", key="pb_btn_yesterday_daily"):
            st.session_state["_pb_go_yesterday"] = True
            st.rerun()

    sm = fetch_daily_summary(db, d1)
    if sm:
        m1, m2, m3, m4 = st.columns(4)
        m1.metric("총 배팅", f"{sm.total_bet:,}")
        m2.metric("당첨 지급액(추정)", f"{sm.total_payout:,}")
        m3.metric("서버 순이익", f"{sm.server_profit:,}")
        m4.metric("배팅 건수 / 인원", f"{sm.bet_rows} / {sm.unique_chars}")
        if sm.server_profit < 0:
            st.warning("이 날짜는 서버가 적자로 집계되었습니다. 포상 풀은 실행되지 않습니다.")
    else:
        st.error("집계에 실패했습니다.")

with tab2:
    st.subheader("파워볼 참여 캐릭터 누적 (배팅 합 기준 상위)")
    lim = st.number_input("표시 상한", min_value=50, max_value=2000, value=500, step=50)
    q = st.text_input("캐릭터 이름 포함 검색 (비우면 전체)", "")
    rows = fetch_character_lifetime_stats(db, limit=lim)
    if q and q.strip():
        needle = q.strip().lower()
        rows = [r for r in rows if needle in str(r.get("char_name") or "").lower()]
    if rows:
        df = pd.DataFrame(rows)
        df = df.rename(
            columns={
                "char_obj_id": "objID",
                "char_name": "이름",
                "class_id": "class",
                "total_bet": "누적배팅",
                "total_payout": "누적당첨액",
                "server_side_net": "서버순이익기여",
                "player_net": "플레이어순손익",
                "bet_count": "배팅횟수",
            }
        )
        st.dataframe(
            df,
            hide_index=True,
            height=520,
            use_container_width=True,
            column_config={
                "이름": st.column_config.TextColumn(
                    "이름",
                    width="large",
                    help="캐릭터 이름",
                ),
            },
        )
    else:
        st.info("데이터가 없거나 아직 정산된 배팅(is_processed=1)이 없습니다.")

with tab3:
    st.subheader("클래스별 1~3위 포상 (수동 실행)")
    d2 = st.date_input("정산일 (KST)", value=date.today() - timedelta(days=1), key="pb_reward_date")
    metric = st.radio(
        "순위 기준",
        options=("contribution", "total_bet"),
        format_func=lambda x: "서버 이익 기여 (배팅−당첨)"
        if x == "contribution"
        else "총 배팅액",
        horizontal=True,
    )
    sm2 = fetch_daily_summary(db, d2)
    if sm2 and sm2.server_profit > 0:
        a, b, c = split_four_class_pool(sm2.server_profit)
        per_class_pool = a + b + c
        royal_total = sum(split_royal_pool(sm2.server_profit))
        st.caption(
            f"선택한 날짜 순이익 **{sm2.server_profit:,}** — "
            f"4직업 각 **{per_class_pool:,}** (전체 22%÷4), 군주 풀 **{royal_total:,}** (12%)"
        )
    elif sm2:
        st.caption(f"순이익 **{sm2.server_profit:,}** — 0 이하이면 포상 풀은 0입니다.")
    prev = build_reward_preview(db, d2, metric, sm2.server_profit if sm2 else 0)
    if prev:
        pdf = pd.DataFrame(
            {
                "슬롯": [p.slot_key for p in prev],
                "캐릭터": [p.char_name for p in prev],
                "직업": [p.class_label for p in prev],
                "순위": [p.rank_in_class for p in prev],
                "지급액": [p.amount for p in prev],
            }
        )
        st.dataframe(pdf, hide_index=True)
    else:
        st.caption("해당 날짜·순위 기준으로 포상 대상이 없습니다.")

    already = reward_run_exists(db, d2)
    if already:
        st.warning("이 날짜는 이미 `powerball_reward_run`에 기록되어 있습니다. 중복 실행은 막혀 있습니다.")

    if st.button("✅ 원장 기록 + 아데나 지급 실행", type="primary", disabled=already):
        ok2, msg2, _lines = execute_daily_rewards(db, d2, metric, dry_run=False)
        if ok2:
            st.success(msg2)
        else:
            st.error(msg2)

with tab4:
    st.subheader("정산 이력")
    d3 = st.date_input("조회일", value=date.today() - timedelta(days=1), key="pb_hist_date")
    if "powerball_reward_run" not in db.get_all_tables():
        st.warning("`powerball_reward_run` 테이블이 없습니다.")
    else:
        run = db.fetch_one(
            "SELECT * FROM powerball_reward_run WHERE reward_date = %s",
            (d3.isoformat(),),
        )
        if run:
            st.json({k: run[k] for k in run})
        else:
            st.caption("해당 날짜 기록 없음")
        lines = db.fetch_all(
            """
            SELECT slot_key, char_name, class_label, rank_in_class, amount, created_at
            FROM powerball_reward_line
            WHERE reward_date = %s
            ORDER BY slot_key
            """,
            (d3.isoformat(),),
        )
        if lines:
            st.dataframe(pd.DataFrame(lines), hide_index=True)
        elif run:
            st.caption("라인 상세가 없습니다.")
