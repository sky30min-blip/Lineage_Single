# -*- coding: utf-8 -*-
"""
파워볼 GM 통계·일일 포상 계산 (서버 PowerballController.PAYOUT_RATE=1.9 와 맞춤).
정산일: KST 달력 기준으로 powerball_results.created_at 구간을 잡되,
DB에 저장된 시각이 이미 KST라고 가정한다 (config 주석 참고).
"""
from __future__ import annotations

from dataclasses import dataclass
from datetime import date, datetime, timedelta
from typing import Any, Literal, Optional

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

RankMetric = Literal["contribution", "total_bet"]


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


def _order_clause(metric: RankMetric) -> str:
    if metric == "total_bet":
        return "stake DESC, (stake - paid) DESC"
    return "(stake - paid) DESC, stake DESC"


def rank_top3_for_class(
    db, d: date, class_id: int, metric: RankMetric
) -> list[dict[str, Any]]:
    start, end = kst_day_sql_window(d)
    payout_sql = _sql_payout_expr()
    oc = _order_clause(metric)
    return db.fetch_all(
        f"""
        SELECT
          b.char_id AS char_obj_id,
          c.name AS char_name,
          c.class AS class_id,
          SUM(b.bet_amount) AS stake,
          SUM({payout_sql}) AS paid,
          SUM(b.bet_amount) - SUM({payout_sql}) AS contribution
        FROM powerball_bets b
        INNER JOIN powerball_results r ON r.round_id = b.round_id
        INNER JOIN characters c ON c.objID = b.char_id
        WHERE b.is_processed = 1
          AND r.created_at >= %s AND r.created_at < %s
          AND c.class = %s
        GROUP BY b.char_id, c.name, c.class
        HAVING stake > 0
        ORDER BY {oc}
        LIMIT 3
        """,
        (start, end, class_id),
    ) or []


def split_four_class_pool(server_profit: int) -> tuple[int, int, int]:
    """한 클래스 풀(= 전체 22%의 1/4)을 12:7:3 비율로 3등분."""
    total_four = max(0, int(server_profit * 0.22))
    per_class = total_four // 4
    r1 = int(per_class * 12 / 22)
    r2 = int(per_class * 7 / 22)
    r3 = per_class - r1 - r2
    return r1, r2, r3


def split_royal_pool(server_profit: int) -> tuple[int, int, int]:
    """군주 풀 12%를 7:3:2 비율로."""
    pool = max(0, int(server_profit * 0.12))
    r1 = int(pool * 7 / 12)
    r2 = int(pool * 3 / 12)
    r3 = pool - r1 - r2
    return r1, r2, r3


@dataclass
class RewardPreviewLine:
    slot_key: str
    char_obj_id: int
    char_name: str
    class_id: int
    class_label: str
    rank_in_class: int
    amount: int


def build_reward_preview(
    db, d: date, metric: RankMetric, server_profit: int
) -> list[RewardPreviewLine]:
    r1, r2, r3 = split_four_class_pool(server_profit)
    amounts_four = (r1, r2, r3)
    lines: list[RewardPreviewLine] = []
    for prefix, cid, label in FOUR_CLASS_POOL:
        ranked = rank_top3_for_class(db, d, cid, metric)
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
                )
            )
    r1, r2, r3 = split_royal_pool(server_profit)
    royal_ranked = rank_top3_for_class(db, d, CLASS_ROYAL, metric)
    for i, row in enumerate(royal_ranked):
        rank = i + 1
        amt = (r1, r2, r3)[i]
        lines.append(
            RewardPreviewLine(
                slot_key=f"royal_r{rank}",
                char_obj_id=int(row["char_obj_id"]),
                char_name=str(row.get("char_name") or ""),
                class_id=int(row.get("class_id") if row.get("class_id") is not None else CLASS_ROYAL),
                class_label="군주",
                rank_in_class=rank,
                amount=amt,
            )
        )
    return lines


def reward_run_exists(db, d: date) -> bool:
    row = db.fetch_one(
        "SELECT 1 AS o FROM powerball_reward_run WHERE reward_date = %s LIMIT 1",
        (d.isoformat(),),
    )
    return bool(row)


def add_adena_inventory_delta(db, char_name: str, delta: int) -> tuple[bool, str]:
    """characters_inventory 첫 아데나 스택에 delta 합산."""
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
            rc = cur.rowcount
            db.connection.commit()
        if rc == 0:
            return False, "인벤에 '아데나' 스택 없음"
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
    metric: RankMetric,
    dry_run: bool = False,
) -> tuple[bool, str, list[RewardPreviewLine]]:
    sm = fetch_daily_summary(db, d)
    if sm is None:
        return False, "일별 집계 조회 실패", []
    profit = sm.server_profit
    if profit <= 0:
        return False, f"서버 순이익이 0 이하입니다 ({profit}). 포상 풀이 없습니다.", []

    if reward_run_exists(db, d):
        return False, "이미 해당 날짜에 정산 기록(powerball_reward_run)이 있습니다. 중복 지급을 막았습니다.", []

    preview = build_reward_preview(db, d, metric, profit)
    if dry_run:
        return True, "미리보기", preview

    if not preview:
        return False, "해당 날짜·조건에서 순위에 오를 캐릭터가 없습니다.", []
    if not any(ln.amount > 0 for ln in preview):
        return False, "계산된 지급액이 모두 0입니다. (순이익이 작거나 반올림으로 소멸)", preview

    pool_four = max(0, int(profit * 0.22))
    pool_royal = max(0, int(profit * 0.12))

    try:
        db.connection.autocommit(False)
        cur = db.connection.cursor()
        cur.execute(
            """
            INSERT INTO powerball_reward_run
              (reward_date, server_profit, pool_four_class, pool_royal, rank_metric, note)
            VALUES (%s, %s, %s, %s, %s, %s)
            """,
            (d.isoformat(), profit, pool_four, pool_royal, metric, "GM 툴 일괄 지급"),
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
        ok, err = add_adena_inventory_delta(db, ln.char_name, ln.amount)
        if not ok:
            errors.append(f"{ln.char_name}({ln.slot_key}): {err}")

    if errors:
        return (
            False,
            "원장은 저장되었으나 일부 아데나 지급에 실패했습니다: " + " | ".join(errors),
            preview,
        )
    return True, "정산·지급 완료", preview
