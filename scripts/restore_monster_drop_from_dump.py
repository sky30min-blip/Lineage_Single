# -*- coding: utf-8 -*-
"""
monster_drop 테이블을 저장소 덤프(기본: 2.싱글리니지 팩/db/20260222.sql) 기준으로 되돌립니다.
- 현재 DB의 monster_drop 전부 삭제 후, 덤프에 있는 INSERT만 다시 넣습니다.
- 서버는 반드시 GM 툴 「서버 리로드」에서 monster_drop 리로드 또는 재시작.
"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

_ROOT = Path(__file__).resolve().parents[1]
if str(_ROOT) not in sys.path:
    sys.path.insert(0, str(_ROOT))

import pymysql
from pymysql.constants import CLIENT

import config

_DEFAULT_DUMP = _ROOT.parent / "2.싱글리니지 팩" / "db" / "20260222.sql"
_PREFIX = "INSERT INTO `monster_drop`"


def _load_inserts(dump_path: Path) -> list[str]:
    if not dump_path.is_file():
        raise FileNotFoundError(str(dump_path))
    out: list[str] = []
    with dump_path.open("r", encoding="utf-8", errors="replace") as f:
        for line in f:
            s = line.strip()
            if s.startswith(_PREFIX):
                out.append(s.rstrip(";") + ";")
    if not out:
        raise RuntimeError(f"{dump_path} 에 {_PREFIX} 행이 없습니다.")
    return out


def main() -> int:
    ap = argparse.ArgumentParser(description="Restore monster_drop from SQL dump")
    ap.add_argument(
        "--dump",
        type=Path,
        default=_DEFAULT_DUMP,
        help="전체 DB 덤프 SQL 경로 (monster_drop INSERT 포함)",
    )
    ap.add_argument(
        "--batch",
        type=int,
        default=80,
        help="한 번에 실행할 INSERT 문 개수 (max_allowed_packet 대비)",
    )
    ap.add_argument("--dry-run", action="store_true", help="DB에 쓰지 않고 행 수만 출력")
    args = ap.parse_args()

    inserts = _load_inserts(args.dump)
    print(f"덤프에서 monster_drop INSERT {len(inserts)}건 추출: {args.dump}")

    if args.dry_run:
        return 0

    cfg = dict(config.DB_CONFIG)
    cfg["client_flag"] = CLIENT.MULTI_STATEMENTS
    conn = pymysql.connect(**cfg)
    try:
        with conn.cursor() as cur:
            cur.execute("SET NAMES utf8mb4")
            cur.execute("DELETE FROM `monster_drop`")
            deleted = cur.rowcount
            print(f"DELETE FROM monster_drop 완료 (영향 행: {deleted})")

            batch = max(1, min(args.batch, 500))
            for i in range(0, len(inserts), batch):
                chunk = inserts[i : i + batch]
                sql = "\n".join(chunk)
                cur.execute(sql)
            conn.commit()

            cur.execute("SELECT COUNT(*) AS c FROM `monster_drop`")
            cnt = cur.fetchone()[0]
            print(f"복구 후 monster_drop 행 수: {cnt}")
    except Exception as e:
        conn.rollback()
        print("FAIL:", e)
        raise
    finally:
        conn.close()

    print("서버에서 monster_drop 리로드(또는 재시작) 하세요.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
