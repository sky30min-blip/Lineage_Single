"""
파워볼 일일 통계·캐릭터 누적·클래스별 포상 정산 (GM 툴).
"""
from datetime import date, datetime, timedelta

import pandas as pd
import streamlit as st
import config as gm_config
from utils.db_manager import get_db
from utils.powerball_economy import (
    PAYOUT_RATE,
    RewardClassSelection,
    build_reward_preview,
    default_reward_selection,
    describe_pool_with_selection,
    execute_daily_rewards,
    fetch_character_lifetime_stats,
    fetch_daily_summary,
    payout_schedule_for_selection,
    reward_run_exists,
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

# `pb_daily_date` 위젯보다 **반드시** 먼저 적용 (그렇지 않으면 StreamlitAPIException)
if st.session_state.pop("_pb_go_yesterday", False):
    st.session_state["pb_daily_date"] = date.today() - timedelta(days=1)

# Streamlit 1.33+ fragment: 열려 있는 동안 주기적으로 DB 재조회 (구버전은 매 전체 rerun 시만 갱신)
# 주기(초)는 왼쪽 사이드바에서 바꿀 수 있음 — 코드 수정 없이 GM이 조절.
if "pb_live_interval" not in st.session_state:
    st.session_state.pb_live_interval = 5
st.sidebar.markdown("##### 🎱 이 페이지")
st.sidebar.number_input(
    "DB 자동 갱신 주기 (초)",
    min_value=3,
    max_value=600,
    step=1,
    key="pb_live_interval",
    help="일일 손익·포상 탭 숫자를 몇 초마다 DB에서 다시 읽을지. Streamlit 1.33 이상에서만 자동 주기 갱신이 됩니다.",
)


def _pb_refresh_seconds() -> float:
    return max(3.0, float(st.session_state.get("pb_live_interval", 5)))


def _pb_live_fragment(fn):
    frag = getattr(st, "fragment", None)
    if frag is not None:
        return frag(run_every=timedelta(seconds=_pb_refresh_seconds()))(fn)
    return fn


@_pb_live_fragment
def _pb_render_tab1_metrics() -> None:
    """일일 손익 탭: 선택한 조회일 기준으로 주기적으로 DB 재집계."""
    d1 = st.session_state.get("pb_daily_date", date.today())
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
    _sec = int(_pb_refresh_seconds())
    frag_note = (
        f"⏱ **{_sec}초마다** DB에서 다시 읽습니다. (마지막 갱신 {datetime.now():%H:%M:%S}, 주기는 왼쪽 사이드바)"
        if getattr(st, "fragment", None)
        else "⏱ Streamlit이 **1.33 미만**이면 자동 주기 갱신 없음 — 날짜·탭을 바꾸거나 새로고침하면 최신입니다."
    )
    st.caption(frag_note)


@_pb_live_fragment
def _pb_render_tab3_live_panel() -> None:
    """포상 탭: 정산일·체크 기준 실시간 집계, 직업별 금액표, 랭킹 미리보기, 실행 버튼."""
    d2 = st.session_state.get(
        "pb_reward_date", date.today() - timedelta(days=1)
    )
    sel = RewardClassSelection(
        knight=st.session_state.get("pb_chk_knight", True),
        wizard=st.session_state.get("pb_chk_wizard", True),
        elf=st.session_state.get("pb_chk_elf", True),
        darkelf=st.session_state.get("pb_chk_darkelf", False),
        royal=st.session_state.get("pb_chk_royal", True),
    )

    sm2 = fetch_daily_summary(db, d2)
    st.markdown("##### 실시간 집계 (정산일 기준)")
    if sm2:
        z1, z2, z3, z4 = st.columns(4)
        z1.metric("총 배팅", f"{sm2.total_bet:,}")
        z2.metric("당첨 지급액(추정)", f"{sm2.total_payout:,}")
        z3.metric("서버 순이익", f"{sm2.server_profit:,}")
        z4.metric("배팅 건수 / 인원", f"{sm2.bet_rows} / {sm2.unique_chars}")
        if sm2.server_profit < 0:
            st.warning("이 날짜는 서버 **적자**입니다. 포상 실행 시 풀은 없습니다.")
    else:
        st.error("집계에 실패했습니다.")

    if sm2 and sm2.server_profit > 0:
        pws = describe_pool_with_selection(sm2.server_profit, sel)
        _dv = float(getattr(gm_config, "POWERBALL_ROYAL_DIVERT_TO_FOUR_RATE", 0.3))
        n4 = int(pws["four_enabled_count"])
        if pws.get("four_folded_to_royal"):
            royal_note = (
                f"네 직업 미참가 → 네직업 총 **{pws['combined_four']:,}** 를 군주 풀에 합침 → 지급 **{pws['royal_payout_pool']:,}** (7:3:2)"
            )
        elif pws["royal_separate"]:
            royal_note = f"군주 실제 풀 **{pws['royal_effective']:,}** 별도 7:3:2"
        else:
            royal_note = f"군주 풀 **{pws['royal_effective']:,}** 네직업 총액에 합산"
        st.caption(
            f"순이익 **{sm2.server_profit:,}** — 네직업 기준 총 **{pws['combined_four']:,}** (군주→네직 이전 **`{_dv:.0%}`** = **{pws['divert_to_four']:,}**), "
            f"참가 **{n4}**직업 → 직업당 풀 **{pws['per_class_pool']:,}** | {royal_note}"
        )
    elif sm2:
        st.caption(f"순이익 **{sm2.server_profit:,}** — 0 이하이면 포상 풀은 0입니다.")

    st.markdown("##### 미리보기: 직업별 1~3위 지급액 (현재 체크 기준)")
    if not sel.any_participant():
        st.warning("직업을 하나 이상 선택하세요.")
        sched_rows: list = []
    elif not sm2:
        sched_rows = []
    elif sm2.server_profit <= 0:
        st.info("순이익이 없어 지급 스케줄은 0입니다.")
        sched_rows = []
    else:
        sched_rows = payout_schedule_for_selection(sm2.server_profit, sel)
    if sched_rows:
        sdf = pd.DataFrame(sched_rows)
        st.dataframe(sdf, hide_index=True, use_container_width=True)
        total_sched = int(sdf["직업 소계"].sum())
        st.caption(
            f"위 표 직업 소계 합: **{total_sched:,}** 아데나 (자리 3개 **가득 찼을 때** 이론 최대; "
            "아래 랭킹에 인원이 부족하면 실제 지급은 더 적습니다.)"
        )
    elif sel.any_participant() and sm2 and sm2.server_profit > 0:
        st.caption("표시할 직업이 없습니다.")

    st.markdown("##### 미리보기: 수혜자·지급액 (지금 DB 레벨 랭킹)")
    if sm2 and sm2.server_profit > 0 and not sel.any_participant():
        prev = []
    else:
        prev = build_reward_preview(db, sm2.server_profit if sm2 else 0, sel)
    if prev:
        pdf = pd.DataFrame(
            {
                "슬롯": [p.slot_key for p in prev],
                "캐릭터": [p.char_name for p in prev],
                "직업": [p.class_label for p in prev],
                "레벨": [p.level for p in prev],
                "exp": [p.exp for p in prev],
                "순위": [p.rank_in_class for p in prev],
                "지급액": [p.amount for p in prev],
            }
        )
        # 전체 너비 + 자동 갱신 시 컬럼 재계산으로 화면이 흔들리는 것을 줄이기 위해 고정 폭·컬럼 폭 지정
        _narrow, _ = st.columns([0.52, 0.48])
        with _narrow:
            st.dataframe(
                pdf,
                hide_index=True,
                use_container_width=True,
                height=320,
                column_config={
                    "슬롯": st.column_config.TextColumn("슬롯", width="small"),
                    "캐릭터": st.column_config.TextColumn("캐릭터", width="medium"),
                    "직업": st.column_config.TextColumn("직업", width="small"),
                    "레벨": st.column_config.NumberColumn("레벨", width="small", format="%d"),
                    "exp": st.column_config.NumberColumn("exp", width="small", format="%d"),
                    "순위": st.column_config.NumberColumn("순위", width="small", format="%d"),
                    "지급액": st.column_config.NumberColumn(
                        "지급액", width="small", format="%d", help="아데나"
                    ),
                },
            )
    else:
        st.caption("직업별 레벨 랭킹에서 포상 대상이 없습니다.")

    st.caption(f"마지막 갱신 **{datetime.now():%H:%M:%S}**")

    already = reward_run_exists(db, d2)
    if already:
        st.warning("이 날짜는 이미 `powerball_reward_run`에 기록되어 있습니다. 중복 실행은 막혀 있습니다.")

    if st.button("✅ 원장 기록 + 아데나 지급 실행", type="primary", disabled=already or not sel.any_participant()):
        ok2, msg2, _lines = execute_daily_rewards(db, d2, dry_run=False, selection=sel)
        if ok2:
            st.success(msg2)
        else:
            st.error(msg2)


with st.expander("📌 설계·제한 사항 (요청하신 규칙과 구현 해석)", expanded=False):
    _fr = float(getattr(gm_config, "POWERBALL_POOL_FOUR_CLASSES_TOTAL_RATE", 0.22))
    _rr = float(getattr(gm_config, "POWERBALL_POOL_ROYAL_TOTAL_RATE", 0.05))
    _dv = float(getattr(gm_config, "POWERBALL_ROYAL_DIVERT_TO_FOUR_RATE", 0.3))
    _live_sec = int(_pb_refresh_seconds())
    st.markdown(
        f"""
        **일일 서버 손익**  
        - 해당 날짜에 **결과가 기록된 회차**만 집계합니다 (`powerball_results.created_at` 구간).  
        - `배팅 합 − 당첨 지급액(배팅×{PAYOUT_RATE}, 반올림은 DB `ROUND` 기준)` = **서버 순이익**으로 봅니다.  
        - **일일 손익·포상 탭**의 숫자 블록은 Streamlit **1.33+** 에서 **{_live_sec}초마다**(사이드바에서 변경) DB를 다시 읽습니다 (구버전은 클릭·새로고침 시 갱신).

        **포상 풀 (`config.py`에서 변경)**  
        - 네 직업 기본 합 **`{_fr:.0%}`**, 군주 명목 합 **`{_rr:.0%}`** (순이익 기준).  
        - 군주 명목 풀의 **`{_dv:.0%}`** 를 떼어 **네 직업 총풀**에 가산한 뒤, **포상 탭에서 체크한 직업 수**로 나눠 직업당 **12 : 7 : 3**.  
        - **군주 미체크** 시 군주 실제 풀도 위 네 직업 총액에 **합쳐서** 다시 나눕니다. 체크 시 군주만 **7 : 3 : 2**.  
        - `POWERBALL_REWARD_CLASS_DEFAULTS` + 이 페이지 체크박스로 참가 직업 지정. 자정 스크립트는 config 기본값 사용.  
        - **정산일**은 파워볼 그날 순이익으로 위 금액만 정합니다.

        **포상 받는 사람 (순위)**  
        - 파워볼 배팅 순위가 **아닙니다**. `characters`에서 **직업(class)별** 서로 다른 캐릭터 **3명**만 1·2·3위로 나갑니다 (한 사람이 두 순위에 들어가지 않음).  
        - **동일 레벨**이면 **경험치(exp)가 더 많은 쪽**이 위 순위, exp까지 같으면 **objID가 작은 쪽**이 위 순위입니다.  
        - 순위는 **정산 실행 시점**의 DB 값입니다.

        **제외 직업**  
        - 용기사·환술사 등은 말씀에 없어 **포상 순위에서 제외**했습니다.

        **자정 자동 지급**  
        - `gm_tool/scripts/powerball_midnight_settle.py` 를 **매일 한국 시간 0시 직후**(예: 0:05)에 실행하세요.  
        - Windows: `scripts/run_powerball_midnight_settle.bat` 을 **작업 스케줄러**에 등록 (배치가 있는 폴더 기준으로 동작).  
        - 수동 테스트: `python scripts/powerball_midnight_settle.py --dry-run` / 특정일 `python scripts/powerball_midnight_settle.py --date 2025-03-19`

        **아데나 지급 (접속 불필요)**  
        - `gm_adena_delivery`가 아니라 **`characters_inventory`를 DB에서 직접** 고칩니다. **오프라인이어도** 지급되며, 다음 접속 시 인벤에 반영됩니다.  
        - 기존 **아데나 스택이 있으면** 수량만 합산하고, **없으면** 아데나 행을 새로 넣습니다. (접속 중이면 `gm_item_delivery`로 서버에 알려 동기화를 시도합니다.)
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

# 포상 직업 체크박스 초기값 (config 기본)
if "pb_reward_class_init" not in st.session_state:
    _d0 = getattr(gm_config, "POWERBALL_REWARD_CLASS_DEFAULTS", None) or {}
    st.session_state.pb_reward_class_init = True
    st.session_state.pb_chk_knight = bool(_d0.get("knight", True))
    st.session_state.pb_chk_wizard = bool(_d0.get("wizard", True))
    st.session_state.pb_chk_elf = bool(_d0.get("elf", True))
    st.session_state.pb_chk_darkelf = bool(_d0.get("darkelf", False))
    st.session_state.pb_chk_royal = bool(_d0.get("royal", True))

with tab1:
    st.subheader("일일 서버 vs 유저(이론 지급액 기준)")
    c1, c2 = st.columns(2)
    with c1:
        d1 = st.date_input("조회일 (KST 달력)", key="pb_daily_date")
    with c2:
        st.write("")  # 날짜 입력과 버튼 높이 맞춤
        if st.button("어제로", key="pb_btn_yesterday_daily"):
            st.session_state["_pb_go_yesterday"] = True
            st.rerun()

    _pb_render_tab1_metrics()

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
    st.subheader("클래스별 레벨 1~3위 포상 (수동 실행)")
    d2 = st.date_input("정산일 (KST)", value=date.today() - timedelta(days=1), key="pb_reward_date")
    st.markdown("**정산에 포함할 클래스** (체크 해제 시 그 직업 몫은 나머지 **체크한 네 직업**에 균등 재분배)")
    r1, r2, r3, r4, r5 = st.columns(5)
    with r1:
        st.checkbox("기사", key="pb_chk_knight")
    with r2:
        st.checkbox("법사", key="pb_chk_wizard")
    with r3:
        st.checkbox("요정", key="pb_chk_elf")
    with r4:
        st.checkbox("다크엘프", key="pb_chk_darkelf", help="업데이트 전엔 끄고 기사·법사·요정이 몫을 나눕니다.")
    with r5:
        st.checkbox("군주", key="pb_chk_royal", help="끄면 군주 실제 풀도 네 직업 쪽에 합산 후 재분배.")

    _cap_sec = int(_pb_refresh_seconds())
    st.caption(
        "**정산일** = 그날 파워볼 순이익(풀 크기). **수혜자** = 체크한 직업만, 레벨 상위 3명. "
        f"아래 블록은 **{_cap_sec}초마다** DB를 다시 읽어 이익·손실·미리보기를 맞춥니다. (주기는 왼쪽 사이드바)"
        + (" (Streamlit 1.33+ 필요)" if not getattr(st, "fragment", None) else "")
    )
    _pb_render_tab3_live_panel()

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
