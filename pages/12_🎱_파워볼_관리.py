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
    fetch_character_stats_by_day,
    fetch_character_stats_in_range,
    fetch_character_lifetime_stats,
    fetch_daily_summary,
    payout_schedule_for_selection,
    reward_run_exists,
)
from utils.table_schemas import get_create_sql


def _is_int_like(v) -> bool:
    if v is None or isinstance(v, bool):
        return False
    if isinstance(v, int):
        return True
    try:
        int(v)
        return True
    except (TypeError, ValueError):
        return False


def _comma_int(v) -> str:
    """천 단위 콤마 (표시용)."""
    if v is None:
        return "—"
    if isinstance(v, bool):
        return str(v)
    try:
        return f"{int(v):,}"
    except (TypeError, ValueError):
        return str(v)


# st.column_config.NumberColumn은 printf 스타일 사용 (예: %d, %.2f)
# {:,.0f}를 넣으면 셀에 문자열 그대로 보일 수 있어 %d로 고정
_NUM_COMMA = "%d"


st.set_page_config(page_title="파워볼 관리", page_icon="🎱", layout="wide")
st.title("🎱 파워볼 관리")
st.caption(
    f"배당률 **{PAYOUT_RATE}배**는 서버 `PowerballController`와 동일하게 집계합니다. "
    "일자는 **한국 날짜** 기준이며, 집계 행은 **그날 `powerball_results.created_at`에 들어온 회차**이거나 "
    "**그날 `powerball_bets.created_at`(배팅 시각)** 인 경우 포함합니다. "
    "NPC·쿠폰 구매만 한 경우도 `powerball_bets` 기준으로 포함하며, 결과 행이 아직 없으면 당첨 지급(추정)은 0으로 집계됩니다. "
    "(동일 회차만 `UPDATE` 되면 결과 `created_at`이 옛날로 남아 일일 손익이 0으로만 보이는 문제를 막기 위함.)"
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
    help="일일 손익·포상 탭 숫자를 몇 초마다 DB에서 다시 읽을지. "
    "Streamlit 1.33+ 의 fragment / experimental_fragment 또는 `streamlit-autorefresh` 폴백.",
)


def _pb_refresh_seconds() -> float:
    return max(3.0, float(st.session_state.get("pb_live_interval", 5)))


def _pb_fragment_decorator():
    """1.33+ `st.fragment` / `experimental_fragment` / 런타임 직접 import (스텁·래퍼 이슈 대비)."""
    for cand in (
        getattr(st, "fragment", None),
        getattr(st, "experimental_fragment", None),
    ):
        if callable(cand):
            return cand
    try:
        from streamlit.runtime.fragment import fragment as _runtime_fragment

        if callable(_runtime_fragment):
            return _runtime_fragment
    except Exception:
        pass
    return None


def _pb_streamlit_diag_lines() -> list[str]:
    """자동 갱신 실패 시 원인 추적용."""
    import sys

    lines = [f"Python 실행 파일: `{sys.executable}`"]
    try:
        import importlib.metadata as im

        lines.append(f"pip 기준 streamlit 버전: **{im.version('streamlit')}**")
    except Exception as e:
        lines.append(f"pip 버전 조회 실패: {e}")
    try:
        import streamlit as _st

        lines.append(f"`streamlit.__version__` (실제 앱에 적용): **{_st.__version__}**")
        lines.append(f"`streamlit` 로드 경로: `{getattr(_st, '__file__', '?')}`")
    except Exception as e:
        lines.append(f"streamlit import 실패: {e}")
    try:
        import importlib.metadata as im
        import streamlit as _st

        mv, iv = im.version("streamlit"), _st.__version__
        if mv != iv:
            lines.append(
                "### 버전 불일치 (원인)\n"
                f"pip 메타데이터는 **{mv}** 인데, import 된 코드는 **{iv}** 입니다. "
                "예전 `site-packages/streamlit` 폴더가 남았거나, 설치가 덮어쓰이지 않은 상태입니다."
            )
            lines.append(
                "### 조치\n"
                "0. **작업 관리자**에서 이 GM용 `python`/`streamlit` 프로세스를 **모두 종료** (옛 서버가 떠 있으면 화면만 새로고침해도 1.31로 남습니다).\n"
                "1. PowerShell에서 **`gm_tool\\scripts\\repair_streamlit.ps1 -Python \"위 경로와 동일한 python.exe\"`** "
                "(스크립트가 `pip uninstall` 후 **`site-packages\\streamlit` 폴더·`streamlit-*.dist-info`를 직접 삭제** 후 재설치합니다).\n"
                f"2. 또는 수동: `{sys.executable} -m pip uninstall streamlit -y` 를 **여러 번** → "
                f"`Lib\\site-packages\\streamlit` 폴더가 남아 있으면 **폴더 통째로 삭제** → "
                f"`{sys.executable} -m pip install --no-cache-dir \"streamlit>=1.55\"`\n"
                "3. 다시 **같은 `python.exe`로** `streamlit run gm_tool/app.py` (또는 `run-gm-tool.ps1`)."
            )
    except Exception:
        pass
    frag = _pb_fragment_decorator()
    lines.append(f"`st.fragment` 사용 가능: **{bool(frag)}**")
    try:
        import pyarrow as pa  # noqa: F401

        lines.append(f"PyArrow: **{pa.__version__}** (설치됨)")
    except Exception as e:
        lines.append(f"PyArrow: **없음** ({e})")
    return lines


def _pb_run_live_refresh_hooks() -> str:
    """
    반환: 'fragment' | 'autorefresh' | 'none'
    - fragment: 일부 UI만 주기 rerun (가장 가벼움)
    - autorefresh: 패키지로 전체 스크립트 주기 rerun
    - none: 수동 새로고침만
    """
    dec = _pb_fragment_decorator()
    if dec is not None:
        return "fragment"
    try:
        import pyarrow  # noqa: F401 — Streamlit 커스텀 컴포넌트(autorefresh) 전제
    except ImportError:
        return "none"
    try:
        from streamlit_autorefresh import st_autorefresh

        ms = max(3000, int(_pb_refresh_seconds() * 1000))
        st_autorefresh(interval=ms, limit=None, key="pb_gm_autorefresh")
        return "autorefresh"
    except ImportError:
        return "none"
    except Exception:
        # PyArrow/Streamlit 버전 불일치 등으로 컴포넌트 등록 실패
        return "none"


_PB_LIVE_MODE = _pb_run_live_refresh_hooks()
st.session_state["_pb_live_mode"] = _PB_LIVE_MODE

# pip 메타 vs 실제 import 버전 불일치 (1.55 기록 + 1.31 로드 같은 꼬임)
try:
    import importlib.metadata as _im
    import sys as _sys

    _pip_sv = _im.version("streamlit")
    _imp_sv = st.__version__
    if _pip_sv != _imp_sv:
        st.sidebar.error(
            f"**Streamlit 설치가 꼬였습니다.** pip 기록 `{_pip_sv}` ≠ 실제 로드 `{_imp_sv}` → "
            "`st.fragment` 없음. 아래 명령을 **이 PC의 이 Python**으로 실행하세요."
        )
        st.sidebar.code(
            f"{_sys.executable} -m pip uninstall streamlit -y\n"
            f"{_sys.executable} -m pip install --no-cache-dir \"streamlit>=1.55\"",
            language="bash",
        )
        st.sidebar.caption(
            "먼저 **Streamlit/GM용 Python 프로세스를 전부 종료**한 뒤, 저장소 루트에서 PowerShell로 "
            f"`.\\gm_tool\\scripts\\repair_streamlit.ps1 -Python \"{_sys.executable}\"` 실행 "
            "(위 경로가 진단과 동일한지 확인). `run-gm-tool.ps1` 은 시작 시 uninstall + **폴더 잔여물 삭제** + 재설치를 합니다."
        )
except Exception:
    pass

if _PB_LIVE_MODE == "none":
    st.sidebar.warning(
        "자동 갱신이 꺼져 있습니다. 위에 **버전 불일치** 안내가 있으면 그걸 먼저 해결하세요. "
        "그 외에는 `pip` 한 Python과 `streamlit run` Python이 다른 경우가 많습니다."
    )
    st.sidebar.info(
        "**해결:** 저장소 루트에서 **`run-gm-tool.ps1`** (자동으로 streamlit 정리·재설치) 또는 "
        "**`gm_tool\\scripts\\repair_streamlit.ps1`** 후 **같은 Python**으로 `streamlit run` 을 다시 실행하세요. "
        "탭 안 **「DB 새로고침」** 으로 수동 갱신도 가능합니다."
    )
    with st.sidebar.expander("🔧 자동 갱신 진단 (복사해 두기)", expanded=True):
        st.markdown("\n\n".join(_pb_streamlit_diag_lines()))


def _pb_live_fragment(fn):
    dec = _pb_fragment_decorator()
    if dec is not None:
        return dec(run_every=timedelta(seconds=_pb_refresh_seconds()))(fn)
    return fn


@_pb_live_fragment
def _pb_render_tab1_metrics() -> None:
    """일일 손익 탭: 선택한 조회일 기준으로 주기적으로 DB 재집계."""
    d1 = st.session_state.get("pb_daily_date", date.today())
    sm = fetch_daily_summary(db, d1)
    if sm:
        st.markdown("###### 홀 / 짝")
        h1, h2, h3, h4 = st.columns(4)
        oe_profit = sm.odd_even_bet - sm.odd_even_payout
        h1.metric("배팅 합", f"{sm.odd_even_bet:,}")
        h2.metric("당첨 지급(추정)", f"{sm.odd_even_payout:,}")
        h3.metric("서버 손익", f"{oe_profit:,}")
        h4.metric("건수", f"{sm.odd_even_rows:,}")
        st.markdown("###### 언더 / 오버")
        u1, u2, u3, u4 = st.columns(4)
        uo_profit = sm.under_over_bet - sm.under_over_payout
        u1.metric("배팅 합", f"{sm.under_over_bet:,}")
        u2.metric("당첨 지급(추정)", f"{sm.under_over_payout:,}")
        u3.metric("서버 손익", f"{uo_profit:,}")
        u4.metric("건수", f"{sm.under_over_rows:,}")
        st.markdown("###### 종합 (홀·짝 + 언더·오버)")
        m1, m2, m3, m4 = st.columns(4)
        m1.metric("총 배팅", f"{sm.total_bet:,}")
        m2.metric("당첨 지급액(추정)", f"{sm.total_payout:,}")
        m3.metric("서버 순이익", f"{sm.server_profit:,}")
        m4.metric("배팅 건수 / 인원", f"{sm.bet_rows:,} / {sm.unique_chars:,}")
        if sm.server_profit < 0:
            st.warning("이 날짜는 서버가 적자로 집계되었습니다. 포상 풀은 실행되지 않습니다.")
    else:
        st.error("집계에 실패했습니다.")
    _sec = int(_pb_refresh_seconds())
    _lm = st.session_state.get("_pb_live_mode", "none")
    _ts = datetime.now().strftime("%H:%M:%S")
    if _lm == "fragment":
        frag_note = f"⏱ **{_sec}초마다** DB 재조회 (`st.fragment`). 마지막 갱신 {_ts} · 주기는 사이드바"
    elif _lm == "autorefresh":
        frag_note = f"⏱ **{_sec}초마다** 전체 새로고침 (`streamlit-autorefresh`). 마지막 갱신 {_ts}"
    else:
        frag_note = f"⏱ 자동 갱신 **없음** (마지막 표시 {_ts}) — 아래 **DB 새로고침** 또는 브라우저 새로고침"
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
        z4.metric("배팅 건수 / 인원", f"{sm2.bet_rows:,} / {sm2.unique_chars:,}")
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
        total_sched = int(pd.to_numeric(sdf.get("직업 소계"), errors="coerce").fillna(0).sum())
        for col in ("1위", "2위", "3위", "직업 소계"):
            if col in sdf.columns:
                sdf[col] = sdf[col].map(_comma_int)
        # 전체 너비 dataframe 은 fragment 갱신 시 컬럼 재계산으로 화면이 흔들림 → 좁은 영역 + 고정 폭
        _sched_narrow, _sched_rest = st.columns([0.38, 0.62])
        with _sched_narrow:
            st.dataframe(
                sdf,
                hide_index=True,
                width="stretch",
                column_config={
                    "직업": st.column_config.TextColumn("직업", width="small"),
                    "1위": st.column_config.TextColumn("1위", width="small"),
                    "2위": st.column_config.TextColumn("2위", width="small"),
                    "3위": st.column_config.TextColumn("3위", width="small"),
                    "직업 소계": st.column_config.TextColumn("소계", width="small"),
                },
            )
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
        for col in ("레벨", "exp", "지급액"):
            if col in pdf.columns:
                pdf[col] = pdf[col].map(_comma_int)
        # 전체 너비 + 자동 갱신 시 컬럼 재계산으로 화면이 흔들리는 것을 줄이기 위해 고정 폭·컬럼 폭 지정
        _narrow, _ = st.columns([0.52, 0.48])
        with _narrow:
            st.dataframe(
                pdf,
                hide_index=True,
                width="stretch",
                height=320,
                column_config={
                    "슬롯": st.column_config.TextColumn("슬롯", width="small"),
                    "캐릭터": st.column_config.TextColumn("캐릭터", width="medium"),
                    "직업": st.column_config.TextColumn("직업", width="small"),
                    "레벨": st.column_config.TextColumn("레벨", width="small"),
                    "exp": st.column_config.TextColumn("exp", width="small"),
                    "순위": st.column_config.NumberColumn("순위", width="small", format="%d"),
                    "지급액": st.column_config.TextColumn("지급액", width="small", help="아데나"),
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
        - 해당 날짜에 **`powerball_results.created_at`이 속한 회차**이거나, **`powerball_bets.created_at`(배팅일)** 이 그날인 행을 집계합니다. (쿠폰만 구매해도 배팅 행 포함; 결과 미기록 시 당첨 추정 0.)  
        - 서버가 동일 회차에 `UPDATE`만 하면 `created_at`이 갱신되지 않는 경우가 있어, 배팅일 조건을 같이 둡니다.  
        - 서버가 `is_processed`를 아직 1로 안 올려도 **결과만 있으면 GM 집계에 포함**합니다 (정산 지연과 무관하게 최신 숫자).  
        - 당첨 지급(추정): **홀/짝**은 `pick_type`과 `result_type` 일치 시, **언더/오버**는 `pick_type` 2·3과 `under_over_type`(없으면 `total_sum≤72`) 일치 시 `배팅×{PAYOUT_RATE}` (`ROUND`).  
        - **일일 손익** 탭: **홀/짝**·**언더/오버**·**종합** 순으로 표시합니다. **자정 포상 풀**은 **종합 서버 순이익**과 동일합니다.  
        - 숫자 블록은 Streamlit **1.33+** 에서 **{_live_sec}초마다**(사이드바에서 변경) DB를 다시 읽습니다 (구버전은 클릭·새로고침 시 갱신).

        **포상 풀 (`config.py`에서 변경)**  
        - 네 직업 기본 합 **`{_fr:.0%}`**, 군주 명목 합 **`{_rr:.0%}`** (순이익 기준).  
        - 군주 명목 풀의 **`{_dv:.0%}`** 를 떼어 **네 직업 총풀**에 가산한 뒤, **포상 탭에서 체크한 직업 수**로 나눠 직업당 **12 : 7 : 3**.  
        - **군주 미체크** 시 군주 실제 풀도 위 네 직업 총액에 **합쳐서** 다시 나눕니다. 체크 시 군주만 **7 : 3 : 2**.  
        - `POWERBALL_REWARD_CLASS_DEFAULTS` + 이 페이지 체크박스로 참가 직업 지정. 자정 스크립트는 config 기본값 사용.  
        - **정산일**은 파워볼 그날 순이익으로 위 금액만 정합니다.

        **포상 받는 사람 (순위)**  
        - 파워볼 배팅 순위가 **아닙니다**. `characters`에서 **직업(class)별** 서로 다른 캐릭터 **3명**만 1·2·3위로 나갑니다 (한 사람이 두 순위에 들어가지 않음).  
        - **GM 캐릭터**(`characters.gm` ≠ 0)는 **1·2·3위 후보에서 제외**됩니다.  
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
        ok_a, err_a = db.execute_query_ex(sql1, None)
        ok_b, err_b = db.execute_query_ex(sql2, None)
        if ok_a and ok_b:
            st.success("✅ 테이블 생성이 완료되었습니다. 페이지를 새로고침하세요.")
            st.rerun()
        else:
            parts = []
            if not ok_a:
                parts.append(f"powerball_reward_run: {err_a}")
            if not ok_b:
                parts.append(f"powerball_reward_line: {err_b}")
            st.error("❌ 생성 실패 — " + " | ".join(parts))

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
    c1, c2, c3 = st.columns([2, 1, 1])
    with c1:
        d1 = st.date_input("조회일 (KST 달력)", key="pb_daily_date")
    with c2:
        st.write("")  # 날짜 입력과 버튼 높이 맞춤
        if st.button("어제로", key="pb_btn_yesterday_daily"):
            st.session_state["_pb_go_yesterday"] = True
            st.rerun()
    with c3:
        st.write("")
        if st.session_state.get("_pb_live_mode") == "none" and st.button(
            "🔄 DB 새로고침", key="pb_manual_refresh_tab1", help="일일 손익 숫자만 DB에서 다시 읽기"
        ):
            st.rerun()

    _pb_render_tab1_metrics()

with tab2:
    st.subheader("파워볼 참여 캐릭터 누적 (기간별 손익 조회)")
    c21, c22, c23 = st.columns(3)
    with c21:
        start_d = st.date_input("시작일", value=date.today() - timedelta(days=7), key="pb_char_start")
    with c22:
        end_d = st.date_input("종료일", value=date.today(), key="pb_char_end")
    with c23:
        lim = st.number_input("표시 상한", min_value=50, max_value=2000, value=500, step=50)

    view_mode = st.radio(
        "집계 방식",
        ("기간 합산 (캐릭터당 1행)", "일별 (캐릭터·날짜마다 1행)"),
        key="pb_char_view_mode",
        horizontal=True,
    )
    q = st.text_input("캐릭터 이름 포함 검색 (비우면 전체)", "", key="pb_char_name_q")

    used_lifetime_fallback = False
    if view_mode.startswith("일별"):
        rows = fetch_character_stats_by_day(
            db, start_d, end_d, limit=int(lim), name_query=q
        )
    else:
        rows = fetch_character_stats_in_range(db, start_d, end_d, limit=int(lim), name_query=q)
        if not rows and not q.strip():
            rows = fetch_character_lifetime_stats(db, limit=int(lim))
            used_lifetime_fallback = True

    only_loss = st.checkbox("손실(플레이어순손익<0)만 보기", key="pb_only_loss")
    only_profit = st.checkbox("이익(플레이어순손익>0)만 보기", key="pb_only_profit")
    if only_loss and only_profit:
        st.warning("손실만/이익만을 동시에 선택했습니다. 둘 중 하나만 선택하세요.")
    elif only_loss:
        rows = [r for r in rows if int(r.get("player_net") or 0) < 0]
    elif only_profit:
        rows = [r for r in rows if int(r.get("player_net") or 0) > 0]

    if rows:
        total_bet = sum(int(r.get("total_bet") or 0) for r in rows)
        total_payout = sum(int(r.get("total_payout") or 0) for r in rows)
        total_server = sum(int(r.get("server_side_net") or 0) for r in rows)
        total_player = sum(int(r.get("player_net") or 0) for r in rows)
        m1, m2, m3, m4 = st.columns(4)
        m1.metric("기간 배팅 합", f"{total_bet:,}")
        m2.metric("기간 당첨 지급 합", f"{total_payout:,}")
        m3.metric("기간 서버 순이익", f"{total_server:,}")
        m4.metric("기간 플레이어 순손익", f"{total_player:,}")

        d1, d2 = (start_d, end_d) if start_d <= end_d else (end_d, start_d)
        if used_lifetime_fallback:
            st.warning(
                "선택한 기간에 **집계 행이 없어** 아래는 **전체 기간 누적**입니다. "
                "날짜를 넓히거나 **일별** 모드를 써 보세요."
            )
            st.caption("표시 데이터: **전체 기간** (기간 합산 모드 폴백)")
        else:
            st.caption(f"조회 기간: {d1.isoformat()} ~ {d2.isoformat()} (KST, 종료일 포함)")

    if rows:
        df = pd.DataFrame(rows)
        rename_map = {
            "char_obj_id": "objID",
            "char_name": "이름",
            "class_id": "class",
            "total_bet": "누적배팅",
            "total_payout": "누적당첨액",
            "server_side_net": "서버순이익기여",
            "player_net": "플레이어순손익",
            "bet_count": "배팅횟수",
        }
        if view_mode.startswith("일별") and "stat_day" in df.columns:
            rename_map["stat_day"] = "집계일"
        df = df.rename(columns=rename_map)
        # 누적 표시는 가독성을 위해 천단위 콤마 문자열로 변환
        comma_cols = [
            c
            for c in ("objID", "누적배팅", "누적당첨액", "서버순이익기여", "플레이어순손익", "배팅횟수")
            if c in df.columns
        ]
        for col in comma_cols:
            df[col] = df[col].map(_comma_int)
        _cc = {
            "이름": st.column_config.TextColumn(
                "이름",
                width="large",
                help="캐릭터 이름",
            ),
            "objID": st.column_config.TextColumn("objID"),
            "class": st.column_config.NumberColumn("class", format="%d"),
            "누적배팅": st.column_config.TextColumn("누적배팅"),
            "누적당첨액": st.column_config.TextColumn("누적당첨액"),
            "서버순이익기여": st.column_config.TextColumn("서버순이익기여"),
            "플레이어순손익": st.column_config.TextColumn("플레이어순손익"),
            "배팅횟수": st.column_config.TextColumn("배팅횟수"),
        }
        if "집계일" in df.columns:
            _cc["집계일"] = st.column_config.TextColumn("집계일", width="small", help="KST 날짜 (결과 시각 기준)")
        st.dataframe(
            df,
            hide_index=True,
            height=520,
            width="stretch",
            column_config=_cc,
        )
    else:
        st.info(
            "선택한 조건에 맞는 데이터가 없습니다. "
            "해당 기간에 `powerball_results`가 있는 회차 배팅이 있는지, 날짜 범위를 확인하세요."
        )

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

    if st.session_state.get("_pb_live_mode") == "none":
        if st.button("🔄 DB 새로고침 (포상·미리보기)", key="pb_manual_refresh_tab3"):
            st.rerun()

    _cap_sec = int(_pb_refresh_seconds())
    _lm3 = st.session_state.get("_pb_live_mode", "none")
    _extra = ""
    if _lm3 == "fragment":
        _extra = f" **{_cap_sec}초마다** fragment로 DB 재조회."
    elif _lm3 == "autorefresh":
        _extra = f" **{_cap_sec}초마다** 페이지 자동 새로고침으로 DB 재조회."
    else:
        _extra = " 자동 갱신 없음 — **일일 손익** 탭의 「DB 새로고침」 또는 브라우저 새로고침."
    st.caption(
        "**정산일** = 그날 파워볼 순이익(풀 크기). **수혜자** = 체크한 직업만, 레벨 상위 3명."
        + _extra
        + " (주기는 왼쪽 사이드바)"
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
            _kv = []
            for k in run:
                v = run[k]
                if _is_int_like(v):
                    disp = _comma_int(v)
                else:
                    disp = "—" if v is None else str(v)
                _kv.append(f"**{k}**  {disp}")
            st.markdown("\n\n".join(_kv))
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
            st.dataframe(
                pd.DataFrame(lines),
                hide_index=True,
                column_config={
                    "rank_in_class": st.column_config.NumberColumn("순위", format="%d"),
                    "amount": st.column_config.NumberColumn("지급액", format=_NUM_COMMA),
                },
            )
        elif run:
            st.caption("라인 상세가 없습니다.")
