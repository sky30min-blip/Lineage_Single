# -*- coding: utf-8 -*-
"""
파워볼 GM 통계·일일 포상 계산 (서버 PowerballController.PAYOUT_RATE=1.9 와 맞춤).
- 정산일: KST 달력 기준으로, (powerball_results.created_at 이 해당일) **또는**
  (powerball_bets.created_at 이 해당일) 인 행을 집계한다.
  서버는 동일 회차에 대해 INSERT 대신 UPDATE 만 할 때 results.created_at 이 옛날로 남는 경우가 있어,
  배팅일 기준을 같이 쓰지 않으면 일일 손익이 0으로만 보일 수 있다.
- 포상 수혜자: 파워볼 순위 아님. characters 직업(class)별 level 상위 3명 (실행 시점 스냅샷).
"""
from __future__ import annotations

from dataclasses import dataclass
from datetime import date, datetime, timedelta
from typing import Any, Optional
import config as gm_config

# lineage.share.Lineage 와 동일
CLASS_ROYAL = 0
CLASS_KNIGHT = 1
CLASS_ELF = 2
CLASS_WIZARD = 3
CLASS_DARKELF = 4

PAYOUT_RATE = float(getattr(gm_config, "POWERBALL_PAYOUT_RATE", 1.9))

# PowerballController 게시판·자정 자동 정산과 동일 (Java 상수와 맞출 것)
BOARD_POOL_FOUR_CLASS_PERCENT = 22
BOARD_POOL_ROYAL_PERCENT = 12
BOARD_RANK_SPLIT_PERCENT = (50, 30, 20)

# (슬롯 접두어, class 컬럼 값, 표시명)
FOUR_CLASS_POOL: tuple[tuple[str, int, str], ...] = (
    ("knight", CLASS_KNIGHT, "기사"),
    ("wizard", CLASS_WIZARD, "법사"),
    ("elf", CLASS_ELF, "요정"),
    ("darkelf", CLASS_DARKELF, "다크엘프"),
)

_PREFIX_TO_SEL: dict[str, str] = {
    "knight": "knight",
    "wizard": "wizard",
    "elf": "elf",
    "darkelf": "darkelf",
}


@dataclass
class RewardClassSelection:
    """일일 포상에 참가하는 직업. False인 네 직업 몫은 참가한 네 직업 수로 균등 재분배."""

    knight: bool = True
    wizard: bool = True
    elf: bool = True
    darkelf: bool = True
    royal: bool = True

    def count_four_enabled(self) -> int:
        return sum(1 for x in (self.knight, self.wizard, self.elf, self.darkelf) if x)

    def any_participant(self) -> bool:
        return self.count_four_enabled() > 0 or self.royal


def default_reward_selection() -> RewardClassSelection:
    try:
        import config as _cfg

        d = getattr(_cfg, "POWERBALL_REWARD_CLASS_DEFAULTS", None)
        if isinstance(d, dict):
            return RewardClassSelection(
                knight=bool(d.get("knight", True)),
                wizard=bool(d.get("wizard", True)),
                elf=bool(d.get("elf", True)),
                darkelf=bool(d.get("darkelf", False)),
                royal=bool(d.get("royal", True)),
            )
    except Exception:
        pass
    return RewardClassSelection()


def selection_compact_note(sel: RewardClassSelection) -> str:
    on = [k for k in ("knight", "wizard", "elf", "darkelf") if getattr(sel, k)]
    return f"four={'+'.join(on) or '-'};royal={'1' if sel.royal else '0'}"


def _four_enabled_for_prefix(sel: RewardClassSelection, prefix: str) -> bool:
    return bool(getattr(sel, _PREFIX_TO_SEL[prefix], False))


def _split_12_7_3(per_class: int) -> tuple[int, int, int]:
    if per_class <= 0:
        return 0, 0, 0
    r1 = int(per_class * 12 / 22)
    r2 = int(per_class * 7 / 22)
    r3 = per_class - r1 - r2
    return r1, r2, r3


def _split_7_3_2(pool: int) -> tuple[int, int, int]:
    if pool <= 0:
        return 0, 0, 0
    r1 = int(pool * 7 / 12)
    r2 = int(pool * 3 / 12)
    r3 = pool - r1 - r2
    return r1, r2, r3


# 포상 수혜자 순위 기준 (powerball_reward_run.rank_metric 에 저장)
RANK_METRIC_LEVEL = "level"


def reward_pool_percent_of_profit() -> float:
    """서버 순이익 중 일일 포상 풀로 쓸 비율 (0~1)."""
    v = float(getattr(gm_config, "POWERBALL_REWARD_POOL_PERCENT_OF_PROFIT", 0.27))
    return max(0.0, min(1.0, v))


def reward_class_weights_from_config() -> dict[str, float]:
    """직업별 가중치(음수는 0). 키: knight, wizard, elf, darkelf, royal."""
    return {
        "knight": max(0.0, float(getattr(gm_config, "POWERBALL_REWARD_WEIGHT_KNIGHT", 100))),
        "wizard": max(0.0, float(getattr(gm_config, "POWERBALL_REWARD_WEIGHT_WIZARD", 100))),
        "elf": max(0.0, float(getattr(gm_config, "POWERBALL_REWARD_WEIGHT_ELF", 100))),
        "darkelf": max(0.0, float(getattr(gm_config, "POWERBALL_REWARD_WEIGHT_DARKELF", 100))),
        "royal": max(0.0, float(getattr(gm_config, "POWERBALL_REWARD_WEIGHT_ROYAL", 60))),
    }


def _enabled_reward_class_keys(sel: RewardClassSelection) -> list[str]:
    out: list[str] = []
    for prefix, _, __ in FOUR_CLASS_POOL:
        if _four_enabled_for_prefix(sel, prefix):
            out.append(prefix)
    if sel.royal:
        out.append("royal")
    return out


def allocate_reward_pools_for_selection(
    server_profit: int, sel: RewardClassSelection
) -> tuple[int, dict[str, int]]:
    """
    순이익 × `POWERBALL_REWARD_POOL_PERCENT_OF_PROFIT` = 총 풀.
    체크된 직업만 설정 가중치 비율로 나눔(정수, 최대 소수부 우선으로 나머지 1원 배분).
    """
    pct = reward_pool_percent_of_profit()
    total = max(0, int(server_profit * pct))
    keys_all = ("knight", "wizard", "elf", "darkelf", "royal")
    zero = {k: 0 for k in keys_all}
    enabled = _enabled_reward_class_keys(sel)
    if not enabled or total <= 0:
        return total, dict(zero)

    wmap = reward_class_weights_from_config()
    weights = [wmap[k] for k in enabled]
    sum_w = sum(weights)
    if sum_w <= 0:
        return total, dict(zero)

    raw = [total * w / sum_w for w in weights]
    floors = [int(x) for x in raw]
    rem = total - sum(floors)
    order = sorted(range(len(enabled)), key=lambda i: raw[i] - floors[i], reverse=True)
    for i in range(rem):
        floors[order[i]] += 1
    out = dict(zero)
    for i, k in enumerate(enabled):
        out[k] = floors[i]
    return total, out


def _pool_rates() -> tuple[float, float]:
    """(네직업 합산 비율, 군주 합산 비율). config 없으면 보수적 기본."""
    try:
        import config as _cfg

        four = float(getattr(_cfg, "POWERBALL_POOL_FOUR_CLASSES_TOTAL_RATE", 0.22))
        royal = float(getattr(_cfg, "POWERBALL_POOL_ROYAL_TOTAL_RATE", 0.05))
    except Exception:
        four, royal = 0.22, 0.05
    return max(0.0, four), max(0.0, royal)


def _royal_divert_fraction() -> float:
    """군주 명목 풀에서 네 직업으로 넘길 비율 (0~1)."""
    try:
        import config as _cfg

        d = float(getattr(_cfg, "POWERBALL_ROYAL_DIVERT_TO_FOUR_RATE", 0.3))
    except Exception:
        d = 0.3
    return max(0.0, min(1.0, d))


def _pool_money_split(server_profit: int) -> tuple[int, int, int, int, int]:
    """
    순이익 기준 금액 분해.
    반환: (네직업_총풀_가산후, 직업당_풀, 군주_실제풀, 군주에서_뗀금액, 군주_명목풀)
    """
    four_rate, royal_rate = _pool_rates()
    divert = _royal_divert_fraction()
    royal_nominal = max(0, int(server_profit * royal_rate))
    divert_total = int(royal_nominal * divert)
    royal_effective = royal_nominal - divert_total
    base_four = max(0, int(server_profit * four_rate))
    combined_four = base_four + divert_total
    per_class = combined_four // 4
    return combined_four, per_class, royal_effective, divert_total, royal_nominal


def describe_pool_amounts(server_profit: int) -> dict[str, int]:
    """UI·요약용: 전 직업 참가 가정 시 풀(레거시 키 일부 호환)."""
    sel_all = RewardClassSelection(
        knight=True, wizard=True, elf=True, darkelf=True, royal=True
    )
    total, pools = allocate_reward_pools_for_selection(server_profit, sel_all)
    four_sum = sum(pools[k] for k in ("knight", "wizard", "elf", "darkelf"))
    per_four = four_sum // 4 if four_sum else 0
    return {
        "four_total_after_divert": four_sum,
        "per_class_pool": per_four,
        "royal_pool_after_divert": pools.get("royal", 0),
        "divert_to_four": 0,
        "royal_nominal": pools.get("royal", 0),
        "total_reward_pool": total,
    }


def describe_pool_with_selection(
    server_profit: int, sel: RewardClassSelection
) -> dict[str, Any]:
    """선택 직업·가중치 반영 후 직업별 풀·총 풀."""
    total, pools = allocate_reward_pools_for_selection(server_profit, sel)
    n = sel.count_four_enabled()
    four_sum = sum(pools[k] for k in ("knight", "wizard", "elf", "darkelf"))
    per_even = four_sum // n if n else 0
    return {
        "total_reward_pool": total,
        "pool_percent": reward_pool_percent_of_profit(),
        "pools_by_prefix": dict(pools),
        "pools": pools,
        "four_enabled_count": n,
        "royal_separate": sel.royal,
        "four_folded_to_royal": n == 0 and sel.royal,
        "per_class_pool_even_if_equal": per_even,
        # 레거시 UI 키 (숫자만 유지)
        "combined_four": four_sum,
        "royal_effective": pools.get("royal", 0),
        "royal_payout_pool": pools.get("royal", 0),
        "royal_nominal": pools.get("royal", 0),
        "divert_to_four": 0,
        "four_total_for_enabled": four_sum,
        "per_class_pool": per_even,
    }


def payout_schedule_for_selection(
    server_profit: int, sel: RewardClassSelection
) -> list[dict[str, Any]]:
    """
    DB 없이 순이익·직업 선택만으로 직업별 1·2·3위 지급액.
    `build_reward_preview`와 동일한 분배식(12:7:3 / 군주 7:3:2).
    """
    if server_profit <= 0 or not sel.any_participant():
        return []
    _, pools = allocate_reward_pools_for_selection(server_profit, sel)
    out: list[dict[str, Any]] = []
    for prefix, _cid, label in FOUR_CLASS_POOL:
        if not _four_enabled_for_prefix(sel, prefix):
            continue
        per = pools.get(prefix, 0)
        r1, r2, r3 = _split_12_7_3(per)
        out.append(
            {
                "직업": label,
                "1위": r1,
                "2위": r2,
                "3위": r3,
                "직업 소계": r1 + r2 + r3,
            }
        )
    if sel.royal:
        royal_pool = pools.get("royal", 0)
        rk1, rk2, rk3 = _split_7_3_2(royal_pool)
        out.append(
            {
                "직업": "군주",
                "1위": rk1,
                "2위": rk2,
                "3위": rk3,
                "직업 소계": rk1 + rk2 + rk3,
            }
        )
    return out


def kst_day_sql_window(d: date) -> tuple[str, str]:
    """
    KST 달력 하루를 DB created_at 경계로 변환.
    현재 운영 DB가 UTC CURRENT_TIMESTAMP를 쓰므로 KST-9h를 적용한다.
    """
    start_utc = datetime(d.year, d.month, d.day) - timedelta(hours=9)
    end_utc = start_utc + timedelta(days=1)
    start = start_utc.strftime("%Y-%m-%d %H:%M:%S")
    end = end_utc.strftime("%Y-%m-%d %H:%M:%S")
    return start, end


def _sql_where_result_or_bet_in_window() -> str:
    """
    일일·기간 집계용. results.created_at 만 쓰면 UPDATE-only 결과 행이 영구히 과거 날짜에 묶일 수 있음.
    """
    return (
        "((r.created_at >= %s AND r.created_at < %s) "
        "OR (b.created_at >= %s AND b.created_at < %s))"
    )


def _params_result_or_bet_window(start: str, end: str) -> tuple[str, str, str, str]:
    return (start, end, start, end)


def _sql_under_over_resolve() -> str:
    """under_over_type 컬럼 없을 때를 대비해 total_sum으로 보조."""
    return "COALESCE(r.under_over_type, IF(r.total_sum <= 72, 0, 1))"


def _sql_payout_expr() -> str:
    """홀/짝 + 언더/오버 당첨 시 1.9배 지급액(행 단위)."""
    uo = _sql_under_over_resolve()
    r = str(PAYOUT_RATE)
    return (
        "CASE "
        f"WHEN b.pick_type IN (0, 1) AND b.pick_type = r.result_type THEN ROUND(b.bet_amount * {r}) "
        f"WHEN b.pick_type = 2 AND {uo} = 0 THEN ROUND(b.bet_amount * {r}) "
        f"WHEN b.pick_type = 3 AND {uo} = 1 THEN ROUND(b.bet_amount * {r}) "
        "ELSE 0 END"
    )


def _sql_payout_expr_guarded() -> str:
    """LEFT JOIN 시 r 없음(결과 미기록·회차 불일치)이어도 당첨액만 0으로 두고 배팅액은 집계."""
    inner = _sql_payout_expr()
    return f"(CASE WHEN r.round_id IS NULL THEN 0 ELSE ({inner}) END)"


def _sql_oe_payout_guarded(rrate: str) -> str:
    inner = (
        f"CASE WHEN b.pick_type IN (0,1) AND b.pick_type = r.result_type "
        f"THEN ROUND(b.bet_amount * {rrate}) ELSE 0 END"
    )
    return f"(CASE WHEN r.round_id IS NULL THEN 0 ELSE ({inner}) END)"


def _sql_uo_payout_guarded(rrate: str) -> str:
    uo = _sql_under_over_resolve()
    inner = (
        f"CASE WHEN b.pick_type = 2 AND {uo} = 0 THEN ROUND(b.bet_amount * {rrate}) "
        f"WHEN b.pick_type = 3 AND {uo} = 1 THEN ROUND(b.bet_amount * {rrate}) "
        "ELSE 0 END"
    )
    return f"(CASE WHEN r.round_id IS NULL THEN 0 ELSE ({inner}) END)"


@dataclass
class DailySummary:
    stat_date: date
    total_bet: int
    total_payout: int
    server_profit: int
    bet_rows: int
    unique_chars: int
    odd_even_bet: int = 0
    odd_even_payout: int = 0
    under_over_bet: int = 0
    under_over_payout: int = 0
    odd_even_rows: int = 0
    under_over_rows: int = 0


def fetch_daily_summary(db, d: date) -> Optional[DailySummary]:
    start, end = kst_day_sql_window(d)
    payout_sql = _sql_payout_expr_guarded()
    rrate = str(PAYOUT_RATE)
    oe_pay = _sql_oe_payout_guarded(rrate)
    uo_pay = _sql_uo_payout_guarded(rrate)
    row = db.fetch_one(
        f"""
        SELECT
          COALESCE(SUM(b.bet_amount), 0) AS total_bet,
          COALESCE(SUM({payout_sql}), 0) AS total_payout,
          COUNT(*) AS bet_rows,
          COUNT(DISTINCT b.char_id) AS uniq,
          COALESCE(SUM(CASE WHEN b.pick_type IN (0, 1) THEN b.bet_amount ELSE 0 END), 0) AS oe_bet,
          COALESCE(SUM({oe_pay}), 0) AS oe_payout,
          COALESCE(SUM(CASE WHEN b.pick_type IN (2, 3) THEN b.bet_amount ELSE 0 END), 0) AS uo_bet,
          COALESCE(SUM({uo_pay}), 0) AS uo_payout,
          COALESCE(SUM(CASE WHEN b.pick_type IN (0, 1) THEN 1 ELSE 0 END), 0) AS oe_rows,
          COALESCE(SUM(CASE WHEN b.pick_type IN (2, 3) THEN 1 ELSE 0 END), 0) AS uo_rows
        FROM powerball_bets b
        LEFT JOIN powerball_results r ON r.round_id = b.round_id
        WHERE {_sql_where_result_or_bet_in_window()}
        """,
        _params_result_or_bet_window(start, end),
    )
    if row is None:
        return None
    tb = int(row.get("total_bet") or 0)
    tp = int(row.get("total_payout") or 0)
    return DailySummary(
        stat_date=d,
        total_bet=tb,
        total_payout=tp,
        server_profit=tb - tp,
        bet_rows=int(row.get("bet_rows") or 0),
        unique_chars=int(row.get("uniq") or 0),
        odd_even_bet=int(row.get("oe_bet") or 0),
        odd_even_payout=int(row.get("oe_payout") or 0),
        under_over_bet=int(row.get("uo_bet") or 0),
        under_over_payout=int(row.get("uo_payout") or 0),
        odd_even_rows=int(row.get("oe_rows") or 0),
        under_over_rows=int(row.get("uo_rows") or 0),
    )


def fetch_character_lifetime_stats(db, limit: int = 500) -> list[dict[str, Any]]:
    payout_sql = _sql_payout_expr_guarded()
    rows = db.fetch_all(
        f"""
        SELECT
          b.char_id AS char_obj_id,
          c.name AS char_name,
          c.class AS class_id,
          COALESCE(SUM(b.bet_amount), 0) AS total_bet,
          COALESCE(SUM({payout_sql}), 0) AS total_payout,
          COALESCE(SUM(b.bet_amount), 0) - COALESCE(SUM({payout_sql}), 0) AS server_side_net,
          COUNT(*) AS bet_count
        FROM powerball_bets b
        LEFT JOIN powerball_results r ON r.round_id = b.round_id
        LEFT JOIN characters c ON c.objID = b.char_id
        GROUP BY b.char_id, c.name, c.class
        ORDER BY total_bet DESC
        LIMIT %s
        """,
        (limit,),
    )
    out = []
    for r in rows or []:
        tb = int(r.get("total_bet") or 0)
        tp = int(r.get("total_payout") or 0)
        d = dict(r)
        d["player_net"] = tp - tb
        out.append(d)
    return out


def fetch_character_stats_in_range(
    db,
    start_date: date,
    end_date: date,
    limit: int = 500,
    name_query: str = "",
) -> list[dict[str, Any]]:
    """기간(시작일~종료일, KST 달력) 기준 캐릭터별 누적 손익."""
    if start_date is None or end_date is None:
        return []
    d1, d2 = (start_date, end_date) if start_date <= end_date else (end_date, start_date)
    start, _ = kst_day_sql_window(d1)
    _, end = kst_day_sql_window(d2)
    payout_sql = _sql_payout_expr_guarded()

    sql = f"""
        SELECT
          b.char_id AS char_obj_id,
          c.name AS char_name,
          c.class AS class_id,
          COALESCE(SUM(b.bet_amount), 0) AS total_bet,
          COALESCE(SUM({payout_sql}), 0) AS total_payout,
          COALESCE(SUM(b.bet_amount), 0) - COALESCE(SUM({payout_sql}), 0) AS server_side_net,
          COUNT(*) AS bet_count
        FROM powerball_bets b
        LEFT JOIN powerball_results r ON r.round_id = b.round_id
        LEFT JOIN characters c ON c.objID = b.char_id
        WHERE {_sql_where_result_or_bet_in_window()}
    """
    params: list[Any] = list(_params_result_or_bet_window(start, end))

    q = (name_query or "").strip()
    if q:
        sql += " AND c.name LIKE %s"
        params.append(f"%{q}%")

    sql += """
        GROUP BY b.char_id, c.name, c.class
        ORDER BY total_bet DESC
        LIMIT %s
    """
    params.append(int(limit))

    rows = db.fetch_all(sql, tuple(params))
    out: list[dict[str, Any]] = []
    for r in rows or []:
        tb = int(r.get("total_bet") or 0)
        tp = int(r.get("total_payout") or 0)
        d = dict(r)
        d["player_net"] = tp - tb
        out.append(d)
    return out


def fetch_character_stats_by_day(
    db,
    start_date: date,
    end_date: date,
    limit: int = 2000,
    name_query: str = "",
) -> list[dict[str, Any]]:
    """
    캐릭터별·일별(KST 달력) 배팅·당첨·플레이어 순손익.
    일자 버킷은 **배팅일** `DATE(b.created_at)` (같은 행이 OR 조건으로 들어올 때 플레이어가 돈 건 날과 맞춤).
    결과 행이 없어도 배팅일 기준으로 포함(당첨액 0). `is_processed`와 무관.
    """
    if start_date is None or end_date is None:
        return []
    d1, d2 = (start_date, end_date) if start_date <= end_date else (end_date, start_date)
    start, _ = kst_day_sql_window(d1)
    _, end = kst_day_sql_window(d2)
    payout_sql = _sql_payout_expr_guarded()
    sql = f"""
        SELECT
          DATE(b.created_at) AS stat_day,
          b.char_id AS char_obj_id,
          c.name AS char_name,
          c.class AS class_id,
          COALESCE(SUM(b.bet_amount), 0) AS total_bet,
          COALESCE(SUM({payout_sql}), 0) AS total_payout,
          COALESCE(SUM(b.bet_amount), 0) - COALESCE(SUM({payout_sql}), 0) AS server_side_net,
          COUNT(*) AS bet_count
        FROM powerball_bets b
        LEFT JOIN powerball_results r ON r.round_id = b.round_id
        LEFT JOIN characters c ON c.objID = b.char_id
        WHERE {_sql_where_result_or_bet_in_window()}
    """
    params: list[Any] = list(_params_result_or_bet_window(start, end))
    q = (name_query or "").strip()
    if q:
        sql += " AND c.name LIKE %s"
        params.append(f"%{q}%")
    sql += """
        GROUP BY DATE(b.created_at), b.char_id, c.name, c.class
        ORDER BY stat_day DESC, total_bet DESC
        LIMIT %s
    """
    params.append(int(limit))
    rows = db.fetch_all(sql, tuple(params))
    out: list[dict[str, Any]] = []
    for rw in rows or []:
        tb = int(rw.get("total_bet") or 0)
        tp = int(rw.get("total_payout") or 0)
        d = dict(rw)
        d["player_net"] = tp - tb
        sd = d.get("stat_day")
        if sd is not None:
            d["stat_day"] = sd.isoformat() if hasattr(sd, "isoformat") else str(sd)
        out.append(d)
    return out


def rank_top3_by_level_for_class(db, class_id: int) -> list[dict[str, Any]]:
    """
    characters 직업별 레벨 상위 3명. 동일 레벨이면:
    1) exp(경험치) 높은 순 — 같은 레벨 안에서 다음 레벨에 더 가까운 쪽
    2) 그래도 같으면 objID 오름차순 — 완전 동점 시에도 항상 1·2·3위가 서로 다른 한 명씩만

    GM 캐릭터(characters.gm != 0)는 순위·정산 대상에서 제외 (서버와 동일하게 캐릭터 단위 gm).
    """
    return db.fetch_all(
        """
        SELECT
          c.objID AS char_obj_id,
          c.name AS char_name,
          c.class AS class_id,
          CAST(c.`level` AS SIGNED) AS cha_level,
          CAST(COALESCE(c.exp, 0) AS SIGNED) AS cha_exp
        FROM characters c
        WHERE c.class = %s
          AND COALESCE(c.gm, 0) = 0
          AND (c.block_date = '0000-00-00 00:00:00' OR c.block_date IS NULL)
        ORDER BY c.`level` DESC, COALESCE(c.exp, 0) DESC, c.objID ASC
        LIMIT 3
        """,
        (class_id,),
    ) or []


def split_pool_by_board_rank(pool_amount: int) -> tuple[int, int, int]:
    """PowerballController.splitByRank — 50/30/20, 나머지는 1위에 가산."""
    if pool_amount <= 0:
        return (0, 0, 0)
    p50 = pool_amount * BOARD_RANK_SPLIT_PERCENT[0] // 100
    p30 = pool_amount * BOARD_RANK_SPLIT_PERCENT[1] // 100
    p20 = pool_amount * BOARD_RANK_SPLIT_PERCENT[2] // 100
    used = p50 + p30 + p20
    return (p50 + (pool_amount - used), p30, p20)


def rank_top3_merged_classes_board(db, class_ids: tuple[int, ...]) -> list[dict[str, Any]]:
    """
    PowerballController.getTopTargetsByClass 와 동일:
    `class IN (...)`, `block_date='0000-00-00 00:00:00'`, level·exp 상위 3명.
    (게시판 미리보기·실제 자정 지급과 수혜자 기준을 맞춤; gm 컬럼은 서버 SQL에 없음)
    """
    if not class_ids:
        return []
    ph = ",".join(["%s"] * len(class_ids))
    return db.fetch_all(
        f"""
        SELECT
          c.objID AS char_obj_id,
          c.name AS char_name,
          c.class AS class_id,
          CAST(c.`level` AS SIGNED) AS cha_level,
          CAST(COALESCE(c.exp, 0) AS SIGNED) AS cha_exp
        FROM characters c
        WHERE c.class IN ({ph})
          AND c.block_date = '0000-00-00 00:00:00'
        ORDER BY c.`level` DESC, c.exp DESC, c.objID ASC
        LIMIT 3
        """,
        tuple(class_ids),
    ) or []


_BOARD_CLASS_LABEL = {
    CLASS_ROYAL: "군주",
    CLASS_KNIGHT: "기사",
    CLASS_ELF: "요정",
    CLASS_WIZARD: "법사",
    CLASS_DARKELF: "다크엘프",
}


def split_four_class_pool(server_profit: int) -> tuple[int, int, int]:
    """네 직업 4개만 참가·군주 제외 가정 시 기사 풀 기준 1·2·3위 금액 (요약용)."""
    sel = RewardClassSelection(
        knight=True, wizard=True, elf=True, darkelf=True, royal=False
    )
    _, pools = allocate_reward_pools_for_selection(server_profit, sel)
    return _split_12_7_3(pools.get("knight", 0))


def split_royal_pool(server_profit: int) -> tuple[int, int, int]:
    """군주만 참가 가정 시 7:3:2 (요약용)."""
    sel = RewardClassSelection(
        knight=False, wizard=False, elf=False, darkelf=False, royal=True
    )
    _, pools = allocate_reward_pools_for_selection(server_profit, sel)
    return _split_7_3_2(pools.get("royal", 0))


@dataclass
class RewardPreviewLine:
    slot_key: str
    char_obj_id: int
    char_name: str
    class_id: int
    class_label: str
    rank_in_class: int
    amount: int
    level: int = 0
    exp: int = 0


def build_reward_preview_board_style(db, server_profit: int) -> list[RewardPreviewLine]:
    """
    게임 파워볼 게시판(`refreshRewardBoardPost`)·`runAutoRewardSettlementIfDue` 와 동일:
    순이익 ×22% 풀을 기사·요정·마법사 **통합** 레벨 TOP3 에 50:30:20,
    ×12% 풀을 군주 TOP3 에 50:30:20.
    """
    lines: list[RewardPreviewLine] = []
    if server_profit <= 0:
        return lines

    pool_four = server_profit * BOARD_POOL_FOUR_CLASS_PERCENT // 100
    pool_royal = server_profit * BOARD_POOL_ROYAL_PERCENT // 100
    am_four = split_pool_by_board_rank(pool_four)
    am_royal = split_pool_by_board_rank(pool_royal)

    merged_four = rank_top3_merged_classes_board(db, (CLASS_KNIGHT, CLASS_ELF, CLASS_WIZARD))
    for i, row in enumerate(merged_four):
        rank = i + 1
        cid = int(row.get("class_id") or 0)
        label = _BOARD_CLASS_LABEL.get(cid, f"class{cid}")
        lines.append(
            RewardPreviewLine(
                slot_key=f"four_r{rank}",
                char_obj_id=int(row["char_obj_id"]),
                char_name=str(row.get("char_name") or ""),
                class_id=cid,
                class_label=label,
                rank_in_class=rank,
                amount=am_four[i] if i < len(am_four) else 0,
                level=int(row.get("cha_level") or 0),
                exp=int(row.get("cha_exp") or 0),
            )
        )

    merged_royal = rank_top3_merged_classes_board(db, (CLASS_ROYAL,))
    for i, row in enumerate(merged_royal):
        rank = i + 1
        lines.append(
            RewardPreviewLine(
                slot_key=f"royal_r{rank}",
                char_obj_id=int(row["char_obj_id"]),
                char_name=str(row.get("char_name") or ""),
                class_id=CLASS_ROYAL,
                class_label="군주",
                rank_in_class=rank,
                amount=am_royal[i] if i < len(am_royal) else 0,
                level=int(row.get("cha_level") or 0),
                exp=int(row.get("cha_exp") or 0),
            )
        )

    return lines


def build_reward_preview(
    db,
    server_profit: int,
    sel: Optional[RewardClassSelection] = None,
) -> list[RewardPreviewLine]:
    """포상 금액은 server_profit·설정 풀%·가중치·선택 직업, 수혜자는 레벨 랭킹."""
    if sel is None:
        sel = default_reward_selection()
    _, pools = allocate_reward_pools_for_selection(server_profit, sel)

    lines: list[RewardPreviewLine] = []
    for prefix, cid, label in FOUR_CLASS_POOL:
        if not _four_enabled_for_prefix(sel, prefix):
            continue
        per = pools.get(prefix, 0)
        r1, r2, r3 = _split_12_7_3(per)
        amounts_four = (r1, r2, r3)
        ranked = rank_top3_by_level_for_class(db, cid)
        for i, row in enumerate(ranked):
            rank = i + 1
            amt = amounts_four[i]
            lines.append(
                RewardPreviewLine(
                    slot_key=f"{prefix}_r{rank}",
                    char_obj_id=int(row["char_obj_id"]),
                    char_name=str(row.get("char_name") or ""),
                    class_id=int(row.get("class_id") if row.get("class_id") is not None else cid),
                    class_label=label,
                    rank_in_class=rank,
                    amount=amt,
                    level=int(row.get("cha_level") or row.get("level") or 0),
                    exp=int(row.get("cha_exp") or row.get("exp") or 0),
                )
            )

    if sel.royal:
        royal_pool = pools.get("royal", 0)
        rk1, rk2, rk3 = _split_7_3_2(royal_pool)
        royal_ranked = rank_top3_by_level_for_class(db, CLASS_ROYAL)
        for i, row in enumerate(royal_ranked):
            rank = i + 1
            amt = (rk1, rk2, rk3)[i]
            lines.append(
                RewardPreviewLine(
                    slot_key=f"royal_r{rank}",
                    char_obj_id=int(row["char_obj_id"]),
                    char_name=str(row.get("char_name") or ""),
                    class_id=int(row.get("class_id") if row.get("class_id") is not None else CLASS_ROYAL),
                    class_label="군주",
                    rank_in_class=rank,
                    amount=amt,
                    level=int(row.get("cha_level") or row.get("level") or 0),
                    exp=int(row.get("cha_exp") or row.get("exp") or 0),
                )
            )
    return lines


def reward_run_exists(db, d: date) -> bool:
    """테이블이 없으면 False (콘솔에 SQL 오류 반복 출력 방지)."""
    try:
        with db.connection.cursor() as cur:
            cur.execute(
                "SELECT 1 AS o FROM powerball_reward_run WHERE reward_date = %s LIMIT 1",
                (d.isoformat(),),
            )
            return cur.fetchone() is not None
    except Exception:
        return False


def fetch_top3_powerball_by_class_for_day(
    db, d: date, class_id: int
) -> list[dict[str, Any]]:
    """
    특정 일(KST)·직업별 파워볼 '서버 기여(배팅−당첨)' 상위 3명.
    MariaDB 구버전은 SELECT 별칭을 HAVING/ORDER BY에 쓰면 1247 오류 → 서브쿼리로 감쌈.
    """
    start, end = kst_day_sql_window(d)
    payout_sql = _sql_payout_expr_guarded()
    sql = f"""
        SELECT
          t.char_obj_id,
          t.char_name,
          t.class_id,
          t.cha_level,
          t.cha_exp
        FROM (
          SELECT
            b.char_id AS char_obj_id,
            c.name AS char_name,
            c.class AS class_id,
            CAST(c.`level` AS SIGNED) AS cha_level,
            CAST(COALESCE(c.exp, 0) AS SIGNED) AS cha_exp,
            COALESCE(SUM(b.bet_amount), 0) AS stake,
            COALESCE(SUM({payout_sql}), 0) AS paid,
            COALESCE(SUM(b.bet_amount), 0) - COALESCE(SUM({payout_sql}), 0) AS contribution
          FROM powerball_bets b
          LEFT JOIN powerball_results r ON r.round_id = b.round_id
          INNER JOIN characters c ON c.objID = b.char_id
          WHERE {_sql_where_result_or_bet_in_window()}
            AND c.class = %s
          GROUP BY b.char_id, c.name, c.class, c.`level`, c.exp
        ) AS t
        WHERE t.stake > 0
        ORDER BY t.contribution DESC, t.stake DESC
        LIMIT 3
        """
    p = _params_result_or_bet_window(start, end) + (class_id,)
    return db.fetch_all(sql, p) or []


def remove_auto_negative_profit_reward_run(db, d: date) -> None:
    """
    서버 `runAutoRewardSettlementIfDue` 가 순이익 0 이하일 때 남기는
    `note` 에 `AUTO_0005_NEGATIVE_OR_ZERO` 가 있으면 해당 원장만 삭제한다.
    (이 행이 남아 있으면 GM 수동 정산이 '이미 정산됨'으로 막힌다.)
    """
    iso = d.isoformat()
    row = db.fetch_one(
        "SELECT note FROM powerball_reward_run WHERE reward_date = %s",
        (iso,),
    )
    if not row:
        return
    note = str(row.get("note") or "")
    if "AUTO_0005_NEGATIVE_OR_ZERO" not in note:
        return
    try:
        db._ensure_connection()
        with db.connection.cursor() as cur:
            cur.execute(
                "DELETE FROM powerball_reward_line WHERE reward_date = %s", (iso,)
            )
            cur.execute("DELETE FROM powerball_reward_run WHERE reward_date = %s", (iso,))
        db.connection.commit()
    except Exception:
        try:
            db.connection.rollback()
        except Exception:
            pass


def repay_adena_from_saved_reward_lines(db, d: date) -> tuple[bool, str]:
    """
    이미 저장된 `powerball_reward_line` 만 보고 아데나를 다시 넣는다.
    (원장 기록은 성공했는데 인벤 UPDATE/INSERT 가 실패했을 때용. **중복 지급** 주의.)
    """
    iso = d.isoformat()
    lines = db.fetch_all(
        """
        SELECT char_obj_id, char_name, amount
        FROM powerball_reward_line
        WHERE reward_date = %s AND amount > 0
        ORDER BY id
        """,
        (iso,),
    )
    if not lines:
        return False, f"{iso} 에 해당하는 지급 라인(powerball_reward_line)이 없습니다."
    errs: list[str] = []
    for ln in lines:
        oid = int(ln.get("char_obj_id") or 0)
        nm = str(ln.get("char_name") or "")
        amt = int(ln.get("amount") or 0)
        ok_a, err_a = add_adena_inventory_delta(db, nm, oid, amt)
        if not ok_a:
            errs.append(f"{nm}({oid}): {err_a}")
    if errs:
        return False, "아데나 지급 실패 — " + " | ".join(errs)
    return True, f"원장 {len(lines)}건 기준 아데나 지급을 반영했습니다."


def add_adena_inventory_delta(
    db, char_name: str, char_obj_id: int, delta: int
) -> tuple[bool, str]:
    """
    접속 여부와 무관하게 DB `characters_inventory`에 반영 (오프라인도 다음 접속 시 그대로 보임).
    기존 아데나 스택이 있으면 합산, 없으면 새 행 INSERT. 접속 중 캐릭터 동기화를 위해
    신규 INSERT 시 `gm_item_delivery`가 있으면 한 줄 넣어 시도한다.

    UPDATE는 **cha_objId** 기준(이름만 쓰면 동명·SQL_SAFE_UPDATES 등으로 실패하기 쉬움).
    """
    if delta <= 0:
        return True, ""
    try:
        with db.connection.cursor() as cur:
            cur.execute(
                """
                UPDATE characters_inventory
                SET `count` = `count` + %s
                WHERE cha_objId = %s AND `name` = %s
                ORDER BY objId ASC
                LIMIT 1
                """,
                (delta, char_obj_id, "아데나"),
            )
            if cur.rowcount:
                db.connection.commit()
                return True, ""

            cur.execute(
                "SELECT IFNULL(MAX(objId), 0) + 1 AS next_id FROM characters_inventory"
            )
            row = cur.fetchone()
            next_id = int(row["next_id"]) if row else 1
            cur.execute(
                """
                INSERT INTO characters_inventory
                  (objId, cha_objId, cha_name, `name`, `count`, en, quantity, equipped)
                VALUES (%s, %s, %s, %s, %s, 0, 1, 0)
                """,
                (next_id, char_obj_id, char_name, "아데나", delta),
            )
            try:
                cur.execute(
                    "INSERT INTO gm_item_delivery (cha_objId, objId, delivered) VALUES (%s, %s, 0)",
                    (char_obj_id, next_id),
                )
            except Exception:
                pass
            db.connection.commit()
        return True, ""
    except Exception as e:
        try:
            db.connection.rollback()
        except Exception:
            pass
        return False, str(e)


def execute_daily_rewards(
    db,
    d: date,
    dry_run: bool = False,
    selection: Optional[RewardClassSelection] = None,
) -> tuple[bool, str, list[RewardPreviewLine]]:
    sm = fetch_daily_summary(db, d)
    if sm is None:
        return False, "일별 집계 조회 실패", []
    remove_auto_negative_profit_reward_run(db, d)
    profit = sm.server_profit
    if profit <= 0:
        return False, f"서버 순이익이 0 이하입니다 ({profit}). 포상 풀이 없습니다.", []

    if reward_run_exists(db, d):
        return False, "이미 해당 날짜에 정산 기록(powerball_reward_run)이 있습니다. 중복 지급을 막았습니다.", []

    sel = selection if selection is not None else default_reward_selection()
    if not sel.any_participant():
        return False, "정산에 포함할 직업을 하나 이상 선택하세요.", []

    preview = build_reward_preview(db, profit, sel)
    if dry_run:
        return True, "미리보기", preview

    if not preview:
        return False, "직업별 레벨 랭킹에서 가져올 캐릭터가 없습니다.", []
    if not any(ln.amount > 0 for ln in preview):
        return False, "계산된 지급액이 모두 0입니다. (순이익이 작거나 반올림으로 소멸)", preview

    _, pools_exec = allocate_reward_pools_for_selection(profit, sel)
    pool_four_record = sum(pools_exec[k] for k in ("knight", "wizard", "elf", "darkelf"))
    pool_royal_record = int(pools_exec.get("royal", 0))
    note = f"GM툴|{selection_compact_note(sel)}"

    try:
        db.connection.autocommit(False)
        cur = db.connection.cursor()
        cur.execute(
            """
            INSERT INTO powerball_reward_run
              (reward_date, server_profit, pool_four_class, pool_royal, rank_metric, note)
            VALUES (%s, %s, %s, %s, %s, %s)
            """,
            (d.isoformat(), profit, pool_four_record, pool_royal_record, RANK_METRIC_LEVEL, note),
        )
        for ln in preview:
            if ln.amount <= 0:
                continue
            cur.execute(
                """
                INSERT INTO powerball_reward_line
                  (reward_date, char_obj_id, char_name, class_id, class_label, rank_in_class, amount, slot_key)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """,
                (
                    d.isoformat(),
                    ln.char_obj_id,
                    ln.char_name,
                    ln.class_id,
                    ln.class_label,
                    ln.rank_in_class,
                    ln.amount,
                    ln.slot_key,
                ),
            )
        db.connection.commit()
    except Exception as e:
        db.connection.rollback()
        return False, f"원장 기록 실패: {e}", preview
    finally:
        db.connection.autocommit(True)

    errors: list[str] = []
    for ln in preview:
        if ln.amount <= 0:
            continue
        ok, err = add_adena_inventory_delta(db, ln.char_name, ln.char_obj_id, ln.amount)
        if not ok:
            errors.append(f"{ln.char_name}({ln.slot_key}): {err}")

    if errors:
        return (
            False,
            "원장은 저장되었으나 일부 아데나 지급에 실패했습니다: " + " | ".join(errors),
            preview,
        )
    return True, "정산·지급 완료", preview
