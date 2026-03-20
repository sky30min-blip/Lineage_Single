# -*- coding: utf-8 -*-
"""
파워볼 GM 통계·일일 포상 계산 (서버 PowerballController.PAYOUT_RATE=1.9 와 맞춤).
- 정산일: KST 달력 기준 powerball_results.created_at 구간으로 그날 순이익(풀 크기) 산출.
- 포상 수혜자: 파워볼 순위 아님. characters 직업(class)별 level 상위 3명 (실행 시점 스냅샷).
"""
from __future__ import annotations

from dataclasses import dataclass
from datetime import date, datetime, timedelta
from typing import Any, Optional

# lineage.share.Lineage 와 동일
CLASS_ROYAL = 0
CLASS_KNIGHT = 1
CLASS_ELF = 2
CLASS_WIZARD = 3
CLASS_DARKELF = 4

PAYOUT_RATE = 1.9

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
    """UI용: 네직업 총풀(가산 후), 직업당 풀(4분할 가정), 군주 실제 풀, 이전액, 군주 명목."""
    c4, pc, reff, div, rn = _pool_money_split(server_profit)
    return {
        "four_total_after_divert": c4,
        "per_class_pool": pc,
        "royal_pool_after_divert": reff,
        "divert_to_four": div,
        "royal_nominal": rn,
    }


def describe_pool_with_selection(
    server_profit: int, sel: RewardClassSelection
) -> dict[str, Any]:
    """선택 직업 반영 후 네직업 쪽 총액·직업당 풀·군주 분리 여부."""
    c4, _pc_legacy, reff, div, rn = _pool_money_split(server_profit)
    n = sel.count_four_enabled()
    four_total = c4 + (0 if sel.royal else reff)
    per = four_total // n if n else 0
    royal_pay = reff + (c4 if (n == 0 and sel.royal) else 0)
    return {
        "combined_four": c4,
        "royal_nominal": rn,
        "divert_to_four": div,
        "royal_effective": reff,
        "royal_payout_pool": royal_pay,
        "four_total_for_enabled": four_total,
        "four_enabled_count": n,
        "per_class_pool": per,
        "royal_separate": sel.royal,
        "four_folded_to_royal": n == 0 and sel.royal,
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
    combined_four, _, royal_eff, _, _ = _pool_money_split(server_profit)
    n = sel.count_four_enabled()
    four_total = combined_four + (0 if sel.royal else royal_eff)
    per = four_total // n if n > 0 else 0
    r1, r2, r3 = _split_12_7_3(per)
    out: list[dict[str, Any]] = []
    for prefix, _cid, label in FOUR_CLASS_POOL:
        if not _four_enabled_for_prefix(sel, prefix):
            continue
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
        royal_pool = royal_eff + (combined_four if n == 0 else 0)
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
    """DB datetime이 한국 현지 시각(naive)으로 적재된다고 가정."""
    start = f"{d.isoformat()} 00:00:00"
    end = (d + timedelta(days=1)).isoformat() + " 00:00:00"
    return start, end


def _sql_payout_expr() -> str:
    return f"CASE WHEN b.pick_type = r.result_type THEN ROUND(b.bet_amount * {PAYOUT_RATE}) ELSE 0 END"


@dataclass
class DailySummary:
    stat_date: date
    total_bet: int
    total_payout: int
    server_profit: int
    bet_rows: int
    unique_chars: int


def fetch_daily_summary(db, d: date) -> Optional[DailySummary]:
    start, end = kst_day_sql_window(d)
    payout_sql = _sql_payout_expr()
    row = db.fetch_one(
        f"""
        SELECT
          COALESCE(SUM(b.bet_amount), 0) AS total_bet,
          COALESCE(SUM({payout_sql}), 0) AS total_payout,
          COUNT(*) AS bet_rows,
          COUNT(DISTINCT b.char_id) AS uniq
        FROM powerball_bets b
        INNER JOIN powerball_results r ON r.round_id = b.round_id
        WHERE b.is_processed = 1
          AND r.created_at >= %s AND r.created_at < %s
        """,
        (start, end),
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
    )


def fetch_character_lifetime_stats(db, limit: int = 500) -> list[dict[str, Any]]:
    payout_sql = _sql_payout_expr()
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
        INNER JOIN powerball_results r ON r.round_id = b.round_id
        LEFT JOIN characters c ON c.objID = b.char_id
        WHERE b.is_processed = 1
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


def rank_top3_by_level_for_class(db, class_id: int) -> list[dict[str, Any]]:
    """
    characters 직업별 레벨 상위 3명. 동일 레벨이면:
    1) exp(경험치) 높은 순 — 같은 레벨 안에서 다음 레벨에 더 가까운 쪽
    2) 그래도 같으면 objID 오름차순 — 완전 동점 시에도 항상 1·2·3위가 서로 다른 한 명씩만
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
        ORDER BY c.`level` DESC, COALESCE(c.exp, 0) DESC, c.objID ASC
        LIMIT 3
        """,
        (class_id,),
    ) or []


def split_four_class_pool(server_profit: int) -> tuple[int, int, int]:
    """네 직업 4개 모두 참가 가정 시 1·2·3위 금액 (레거시·요약용)."""
    c4, _, _, _, _ = _pool_money_split(server_profit)
    return _split_12_7_3(c4 // 4)


def split_royal_pool(server_profit: int) -> tuple[int, int, int]:
    """군주 실제 풀 7:3:2."""
    _, _, pool, _, _ = _pool_money_split(server_profit)
    return _split_7_3_2(pool)


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


def build_reward_preview(
    db,
    server_profit: int,
    sel: Optional[RewardClassSelection] = None,
) -> list[RewardPreviewLine]:
    """포상 금액은 server_profit·선택 직업, 수혜자는 레벨 랭킹."""
    if sel is None:
        sel = default_reward_selection()
    combined_four, _, royal_eff, _, _ = _pool_money_split(server_profit)
    n = sel.count_four_enabled()
    four_total = combined_four + (0 if sel.royal else royal_eff)
    per = four_total // n if n > 0 else 0
    r1, r2, r3 = _split_12_7_3(per)
    amounts_four = (r1, r2, r3)

    lines: list[RewardPreviewLine] = []
    for prefix, cid, label in FOUR_CLASS_POOL:
        if not _four_enabled_for_prefix(sel, prefix):
            continue
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
        royal_pool = royal_eff + (combined_four if n == 0 else 0)
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
    payout_sql = _sql_payout_expr()
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
          INNER JOIN powerball_results r ON r.round_id = b.round_id
          INNER JOIN characters c ON c.objID = b.char_id
          WHERE b.is_processed = 1
            AND r.created_at >= %s AND r.created_at < %s
            AND c.class = %s
          GROUP BY b.char_id, c.name, c.class, c.`level`, c.exp
        ) AS t
        WHERE t.stake > 0
        ORDER BY t.contribution DESC, t.stake DESC
        LIMIT 3
        """
    return db.fetch_all(sql, (start, end, class_id)) or []


def add_adena_inventory_delta(
    db, char_name: str, char_obj_id: int, delta: int
) -> tuple[bool, str]:
    """
    접속 여부와 무관하게 DB `characters_inventory`에 반영 (오프라인도 다음 접속 시 그대로 보임).
    기존 아데나 스택이 있으면 합산, 없으면 새 행 INSERT. 접속 중 캐릭터 동기화를 위해
    신규 INSERT 시 `gm_item_delivery`가 있으면 한 줄 넣어 시도한다.
    """
    if delta <= 0:
        return True, ""
    try:
        with db.connection.cursor() as cur:
            cur.execute(
                """
                UPDATE characters_inventory
                SET `count` = `count` + %s
                WHERE cha_name = %s AND `name` = %s
                ORDER BY objId ASC
                LIMIT 1
                """,
                (delta, char_name, "아데나"),
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
                  (objId, cha_objId, cha_name, name, count, en, quantity, equipped)
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

    combined_four, _, royal_eff, _, _ = _pool_money_split(profit)
    n_exec = sel.count_four_enabled()
    four_total = combined_four + (0 if sel.royal else royal_eff)
    pool_four_record = 0 if (n_exec == 0 and sel.royal) else four_total
    pool_royal_record = (
        (royal_eff + combined_four) if (n_exec == 0 and sel.royal) else (royal_eff if sel.royal else 0)
    )
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
