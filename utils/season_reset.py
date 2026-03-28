# -*- coding: utf-8 -*-
"""시즌 원클릭 초기화 — 계정(accounts) 유지, 플레이 데이터 TRUNCATE (GM 툴 서버 관리 탭)."""
import subprocess
from datetime import datetime
from pathlib import Path

SEASON_RESET_TABLES = [
    "characters",
    "characters_book",
    "characters_buff",
    "characters_friend",
    "characters_inventory",
    "characters_quest",
    "characters_skill",
    "characters_swap",
    "characters_block_list",
    "characters_pvp",
    "characters_pet",
    "characters_letter",
    "characters_wedding",
    "character_marble",
    "warehouse",
    "warehouse_elf",
    "warehouse_clan",
    "clan_list",
    "clan_agit",
    "warehouse_clan_log",
    "auto_clan_list",
    "pc_shop",
    "pc_shop_robot",
    "pc_shop_history",
    "pc_trade",
    "boards",
    "boards_auction",
    "auto_fish_list",
    "wanted",
    "powerball_bets",
    "powerball_results",
    "powerball_reward_run",
    "powerball_reward_line",
    "dead_lost_item",
    "dead_lost_item_log",
    "enchant_lost_item",
    "gm_item_delivery",
    "gm_adena_delivery",
    "gm_location_delivery",
    "kingdom",
    "kingdom_tax_log",
    "rank_log",
    "rank",
]

ACCOUNT_PROGRESS_COLUMNS = [
    "giran_dungeon_time",
    "giran_dungeon_count",
    "auto_count",
    "자동사냥_이용시간",
    "daycount",
    "daycheck",
    "daytime",
    "레벨달성체크",
]

RESET_TABLE_DESCRIPTIONS = {
    "characters": "캐릭터 본체(레벨/스탯/좌표 등)",
    "characters_book": "캐릭터 북마크",
    "characters_buff": "캐릭터 버프 저장",
    "characters_friend": "친구 목록",
    "characters_inventory": "캐릭터 인벤토리",
    "characters_quest": "퀘스트 진행",
    "characters_skill": "습득 스킬",
    "characters_swap": "스왑/단축 설정",
    "characters_block_list": "차단 목록",
    "characters_pvp": "PVP 기록",
    "characters_pet": "펫 정보",
    "characters_letter": "우편",
    "characters_wedding": "결혼 정보",
    "character_marble": "경험치 구슬(마블) 연동",
    "warehouse": "일반 창고",
    "warehouse_elf": "요정 창고",
    "warehouse_clan": "혈맹 창고",
    "clan_list": "혈맹 목록/정보",
    "clan_agit": "혈맹 아지트",
    "warehouse_clan_log": "혈맹 창고 로그",
    "auto_clan_list": "자동 혈맹 관련",
    "pc_shop": "개인상점 진열",
    "pc_shop_robot": "개인상점 로봇",
    "pc_shop_history": "개인상점 거래 이력",
    "pc_trade": "거래소 등록",
    "boards": "게시판 글",
    "boards_auction": "경매 게시판",
    "auto_fish_list": "자동 낚시 등록",
    "wanted": "수배 데이터",
    "powerball_bets": "파워볼 배팅",
    "powerball_results": "파워볼 회차 결과",
    "powerball_reward_run": "파워볼 포상 정산 메타",
    "powerball_reward_line": "파워볼 포상 지급 내역",
    "dead_lost_item": "사망 분실 복구 대기",
    "dead_lost_item_log": "사망 분실 복구 로그",
    "enchant_lost_item": "인챈트 실패 복구 대기",
    "gm_item_delivery": "GM 아이템 지급 대기",
    "gm_adena_delivery": "GM 아데나 지급 대기",
    "gm_location_delivery": "GM 위치이동 대기",
    "kingdom": "공성 성 소유권/공성 상태",
    "kingdom_tax_log": "공성 세금 로그",
    "rank_log": "랭킹 로그(존재 시)",
    "rank": "랭킹 캐시/테이블(존재 시)",
}


def reset_account_progress_columns(db) -> tuple[int, str]:
    try:
        cols = db.fetch_all(
            """
            SELECT COLUMN_NAME
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = %s AND TABLE_NAME = 'accounts'
            """,
            (db.config["database"],),
        )
        existing = {r["COLUMN_NAME"] for r in cols} if cols else set()
        targets = [c for c in ACCOUNT_PROGRESS_COLUMNS if c in existing]
        if not targets:
            return 0, ""

        set_clause = ", ".join([f"`{c}`=0" for c in targets])
        sql = f"UPDATE `accounts` SET {set_clause}"
        with db.connection.cursor() as cursor:
            cursor.execute(sql)
        return len(targets), ""
    except Exception as e:
        return 0, str(e)


def run_season_reset(db) -> tuple[bool, str]:
    try:
        db._ensure_connection()
        all_tables = set(db.get_all_tables())
        targets = [t for t in SEASON_RESET_TABLES if t in all_tables]
        skipped = [t for t in SEASON_RESET_TABLES if t not in all_tables]

        with db.connection.cursor() as cursor:
            cursor.execute("SET FOREIGN_KEY_CHECKS=0")
            for t in targets:
                cursor.execute(f"TRUNCATE TABLE `{t}`")

            cols_count, cols_err = reset_account_progress_columns(db)
            if cols_err:
                raise RuntimeError(f"accounts 진행 컬럼 리셋 실패: {cols_err}")

            if "gm_server_command" in all_tables:
                cursor.execute("TRUNCATE TABLE `gm_server_command`")

            cursor.execute("SET FOREIGN_KEY_CHECKS=1")

        db.connection.commit()
        msg = (
            f"초기화 완료: {len(targets)}개 테이블 비움"
            + (f", accounts 진행 컬럼 {cols_count}개 리셋" if cols_count > 0 else "")
        )
        if skipped:
            msg += f" / 미존재 테이블 {len(skipped)}개는 건너뜀"
        return True, msg
    except Exception as e:
        try:
            if db.connection:
                with db.connection.cursor() as cursor:
                    cursor.execute("SET FOREIGN_KEY_CHECKS=1")
                db.connection.rollback()
        except Exception:
            pass
        return False, str(e)


def auto_backup_before_reset(db) -> tuple[bool, str, str]:
    try:
        root_dir = Path(__file__).resolve().parents[2]
        backup_dir = root_dir / "backup"
        backup_dir.mkdir(parents=True, exist_ok=True)

        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
        db_name = db.config.get("database", "lineage")
        backup_path = backup_dir / f"{db_name}_full_{ts}.sql"

        cmd = [
            "mysqldump",
            "-h",
            str(db.config.get("host", "localhost")),
            "-P",
            str(db.config.get("port", 3306)),
            "-u",
            str(db.config.get("user", "root")),
            f"-p{db.config.get('password', '')}",
            "--default-character-set=utf8mb4",
            "--routines",
            "--triggers",
            "--single-transaction",
            str(db_name),
        ]

        with open(backup_path, "w", encoding="utf-8", newline="") as f:
            result = subprocess.run(cmd, stdout=f, stderr=subprocess.PIPE, text=True)

        if result.returncode != 0:
            try:
                backup_path.unlink(missing_ok=True)
            except Exception:
                pass
            err = (result.stderr or "").strip()
            return False, f"자동 백업 실패: {err if err else 'mysqldump 실행 오류'}", ""

        return True, "자동 백업 완료", str(backup_path)
    except FileNotFoundError:
        return False, "자동 백업 실패: `mysqldump` 명령을 찾을 수 없습니다.", ""
    except Exception as e:
        return False, f"자동 백업 실패: {e}", ""
