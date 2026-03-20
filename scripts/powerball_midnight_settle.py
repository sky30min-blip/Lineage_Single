# -*- coding: utf-8 -*-
"""
파워볼 일일 포상 자동 정산 (CLI)

한국 시간(KST, UTC+9) 기준 **어제** 날짜의 파워볼 순이익으로 풀 크기를 정하고,
수혜자는 **실행 시점** characters 직업별 레벨 1~3위.

Windows 작업 스케줄러 예시 (관리자 PowerShell):
  schtasks /Create /TN "Lineage_PowerballReward" /TR "D:\\Lineage_Single\\gm_tool\\scripts\\run_powerball_midnight_settle.bat" /SC DAILY /ST 00:05 /F
"""
from __future__ import annotations

import argparse
import sys
from datetime import date, datetime, timedelta, timezone
from pathlib import Path

# gm_tool 루트를 import 경로에 추가
_ROOT = Path(__file__).resolve().parent.parent
if str(_ROOT) not in sys.path:
    sys.path.insert(0, str(_ROOT))

from utils.db_manager import get_db  # noqa: E402
from utils.powerball_economy import (  # noqa: E402
    default_reward_selection,
    execute_daily_rewards,
    fetch_daily_summary,
    reward_run_exists,
    selection_compact_note,
)

KST = timezone(timedelta(hours=9))


def _kst_yesterday() -> date:
    return datetime.now(KST).date() - timedelta(days=1)


def main() -> int:
    p = argparse.ArgumentParser(description="파워볼 일일 포상 정산 (KST 어제, 레벨 랭킹 수혜)")
    p.add_argument(
        "--date",
        type=str,
        default=None,
        help="파워볼 순이익 집계일 YYYY-MM-DD (미지정 시 KST 기준 어제)",
    )
    p.add_argument(
        "--dry-run",
        action="store_true",
        help="DB 원장·지급 없이 미리보기만",
    )
    args = p.parse_args()

    if args.date:
        try:
            settle_date = date.fromisoformat(args.date)
        except ValueError:
            print("ERROR: --date 형식은 YYYY-MM-DD 여야 합니다.", file=sys.stderr)
            return 1
    else:
        settle_date = _kst_yesterday()

    print(f"[powerball_settle] KST now={datetime.now(KST)} profit_date={settle_date} dry_run={args.dry_run}")

    db = get_db()
    ok, test_msg = db.test_connection()
    if not ok:
        print(f"ERROR: DB 연결 실패: {test_msg}", file=sys.stderr)
        return 1

    tables = db.get_all_tables()
    for t in ("powerball_bets", "powerball_results", "characters", "powerball_reward_run", "powerball_reward_line"):
        if t not in tables:
            print(f"ERROR: 테이블 없음: {t} — GM 툴에서 테이블 생성 후 다시 실행하세요.", file=sys.stderr)
            return 1

    if reward_run_exists(db, settle_date):
        print(f"SKIP: {settle_date} 는 이미 정산됨 (powerball_reward_run).")
        return 0

    sm = fetch_daily_summary(db, settle_date)
    if sm is None:
        print("ERROR: 일별 집계 실패", file=sys.stderr)
        return 1

    print(
        f"  powerball_day: bet={sm.total_bet:,} payout={sm.total_payout:,} profit={sm.server_profit:,} chars={sm.unique_chars}"
    )

    if sm.server_profit <= 0:
        print(f"SKIP: {settle_date} 순이익 {sm.server_profit:,} — 포상 풀 없음.")
        return 0

    _sel = default_reward_selection()
    print(f"  reward_classes: {selection_compact_note(_sel)}")

    if args.dry_run:
        ok2, msg2, lines = execute_daily_rewards(
            db, settle_date, dry_run=True, selection=_sel
        )
        print(f"DRY_RUN: ok={ok2} msg={msg2} preview_lines={len(lines)}")
        return 0 if ok2 else 1

    ok2, msg2, _lines = execute_daily_rewards(
        db, settle_date, dry_run=False, selection=_sel
    )
    print(f"RESULT: ok={ok2} msg={msg2}")
    return 0 if ok2 else 1


if __name__ == "__main__":
    raise SystemExit(main())
