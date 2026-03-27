"""
서버 리로드 - GM 툴에서 DB 반영 후 서버에 리로드 명령 전송
서버가 gm_server_command 테이블을 주기적으로 확인해 reload 명령을 실행합니다.
"""
import streamlit as st
from utils.db_manager import get_db
import pandas as pd
import subprocess
from datetime import datetime
from pathlib import Path

st.set_page_config(page_title="서버 리로드", page_icon="🔄", layout="wide")
st.title("🔄 서버 리로드")

st.caption("DB를 수정한 뒤 **서버가 새 데이터를 읽도록** 리로드 명령을 보냅니다. 서버가 주기적으로 `gm_server_command`를 확인해 실행합니다.")

db = get_db()

# 계정은 유지하고 유저 플레이 데이터만 시즌 초기화
SEASON_RESET_TABLES = [
    # 캐릭터 본체/직결
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
    # 창고/혈맹
    "warehouse",
    "warehouse_elf",
    "warehouse_clan",
    "clan_list",
    "clan_agit",
    "warehouse_clan_log",
    "auto_clan_list",
    # 거래/상점/월드 진행
    "pc_shop",
    "pc_shop_robot",
    "pc_shop_history",
    "pc_trade",
    "boards",
    "boards_auction",
    "auto_fish_list",
    "wanted",
    # 파워볼
    "powerball_bets",
    "powerball_results",
    "powerball_reward_run",
    "powerball_reward_line",
    # 복구/GM 큐
    "dead_lost_item",
    "dead_lost_item_log",
    "enchant_lost_item",
    "gm_item_delivery",
    "gm_adena_delivery",
    "gm_location_delivery",
    # 공성/랭킹(시즌 리셋 권장)
    "kingdom",
    "kingdom_tax_log",
    "rank_log",
    "rank",
]

# accounts는 유지하고 아래 진행성 컬럼만(존재할 때만) 리셋
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


def _reset_account_progress_columns(db) -> tuple[int, str]:
    """
    accounts 진행성 컬럼만 존재하는 것들 기준으로 0 리셋.
    Returns: (적용 컬럼 수, 에러메시지)
    """
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


def _run_season_reset(db) -> tuple[bool, str]:
    """
    계정(accounts) 유지 + 유저 플레이 데이터 초기화.
    """
    try:
        db._ensure_connection()
        all_tables = set(db.get_all_tables())
        targets = [t for t in SEASON_RESET_TABLES if t in all_tables]
        skipped = [t for t in SEASON_RESET_TABLES if t not in all_tables]

        with db.connection.cursor() as cursor:
            cursor.execute("SET FOREIGN_KEY_CHECKS=0")
            for t in targets:
                cursor.execute(f"TRUNCATE TABLE `{t}`")

            cols_count, cols_err = _reset_account_progress_columns(db)
            if cols_err:
                raise RuntimeError(f"accounts 진행 컬럼 리셋 실패: {cols_err}")

            # 시즌 초기화 후 서버 명령 큐도 깨끗하게 정리(선택성, 존재할 때만)
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


def _auto_backup_before_reset(db) -> tuple[bool, str, str]:
    """
    시즌 초기화 직전 전체 DB 자동 백업.
    Returns: (성공여부, 메시지, 백업파일경로)
    """
    try:
        root_dir = Path(__file__).resolve().parents[2]
        backup_dir = root_dir / "backup"
        backup_dir.mkdir(parents=True, exist_ok=True)

        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
        db_name = db.config.get("database", "lineage")
        backup_path = backup_dir / f"{db_name}_full_{ts}.sql"

        cmd = [
            "mysqldump",
            "-h", str(db.config.get("host", "localhost")),
            "-P", str(db.config.get("port", 3306)),
            "-u", str(db.config.get("user", "root")),
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

# 리로드 종류 (command='reload', param=아래 키)
RELOAD_OPTIONS = [
    ("npc", "npc 테이블 리로드", "NPC 정의·스폰 목록. NPC 배치/위치 수정 후 사용"),
    ("item", "item 테이블 리로드", "아이템 정의. 아이템 추가/수정 후 사용"),
    ("monster", "monster 테이블 리로드", "몬스터 정의"),
    ("monster_spawnlist", "전체스폰 리로드", "monster_spawnlist 기준으로 월드 일반 몬스터 스폰을 재적용"),
    ("monster_drop", "monster_drop 테이블 리로드", "몬스터 드롭 테이블"),
    ("monster_skill", "monster_skill 테이블 리로드", "몬스터 스킬"),
    ("npc_shop", "npc_shop 테이블 리로드", "NPC 상점 목록"),
    ("background_spawnlist", "background_spawnlist 테이블 리로드", "배경 스폰"),
]

if "reload_feedback" in st.session_state:
    msg_type, msg_text = st.session_state["reload_feedback"]
    if msg_type == "success":
        st.success(msg_text)
    else:
        st.error(msg_text)
    del st.session_state["reload_feedback"]

st.subheader("리로드 실행")
for param_key, label, desc in RELOAD_OPTIONS:
    col1, col2 = st.columns([3, 1])
    with col1:
        st.markdown(f"**{label}**")
        st.caption(desc)
    with col2:
        if st.button("실행", key=f"reload_{param_key}", help=f"서버에 reload:{param_key} 요청을 넣습니다. {desc}"):
            ok, err = db.execute_query_ex(
                "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                ("reload", param_key),
            )
            if ok:
                st.session_state["reload_feedback"] = (
                    "success",
                    f"✅ '{label}' 리로드 요청이 큐에 등록되었습니다. 서버 콘솔에서 '[gm_server_command] reload: ...' 로그를 확인하세요.",
                )
            else:
                st.session_state["reload_feedback"] = ("error", f"❌ 명령 삽입 실패: {err}")
            st.rerun()
    st.divider()

st.subheader("안내")
st.info("""
- **서버가 실행 중**이어야 합니다. 서버 프로그램이 주기적으로 `gm_server_command`를 조회해 `command='reload'`, `param='npc'` 등으로 리로드합니다.
- 처리된 행은 `executed=1`로 갱신됩니다.
- `gm_server_command` 테이블이 없으면 서버에서 생성하거나, 서버 창 메뉴 **[명령어|이벤트|리로드] → [리로드]** 에서 수동으로 실행하세요.
""")

st.warning(
    "⚠️ `전체스폰 리로드(param=monster_spawnlist)`는 서버 코드가 해당 param을 지원해야 동작합니다. "
    "서버 로그에 `reload: 알 수 없는 param=monster_spawnlist`가 나오면, "
    "게임 내 `.리로드 전체스폰`을 사용하거나 서버 업데이트/재시작 후 다시 시도하세요."
)

st.divider()
st.subheader("🧨 시즌 원클릭 초기화 (계정 유지)")
st.error(
    "아래 기능은 유저 플레이 데이터를 삭제합니다. "
    "`accounts` 로그인 계정은 유지하고, 캐릭터/인벤/창고/거래/파워볼/복구큐 등을 초기화합니다."
)

with st.expander("초기화 대상 테이블 보기"):
    existing_tables = set(db.get_all_tables())
    exist_rows = []
    missing_rows = []

    for t in SEASON_RESET_TABLES:
        row = {
            "테이블명": t,
            "초기화 내용": RESET_TABLE_DESCRIPTIONS.get(t, "유저 플레이 데이터"),
        }
        if t in existing_tables:
            exist_rows.append(row)
        else:
            missing_rows.append(row)

    st.markdown("**✅ 현재 DB에 존재(초기화 대상)**")
    if exist_rows:
        st.dataframe(pd.DataFrame(exist_rows), hide_index=True, use_container_width=True)
    else:
        st.info("초기화 대상 중 현재 DB에 존재하는 테이블이 없습니다.")

    st.markdown("**⚠️ 문서 기준 목록이나 현재 DB에 없음(건너뜀)**")
    if missing_rows:
        st.dataframe(pd.DataFrame(missing_rows), hide_index=True, use_container_width=True)
    else:
        st.caption("현재 문서 기준 대상 테이블이 모두 DB에 존재합니다.")

    st.caption(
        "추가 동작: accounts 진행성 컬럼만 0으로 리셋 "
        f"({', '.join(ACCOUNT_PROGRESS_COLUMNS)})"
    )

confirm_check = st.checkbox("위 내용을 확인했고, 시즌 초기화를 진행합니다.")
confirm_text = st.text_input("확인 문구 입력", placeholder="시즌초기화")

btn_col, msg_col = st.columns([1, 2])
with btn_col:
    run_reset = st.button("🔥 원클릭 시즌 초기화 실행", type="primary")
with msg_col:
    st.caption("초기화를 실행하면 현재 정보는 자동으로 저장 됩니다.")

if run_reset:
    if not confirm_check:
        st.error("체크박스를 먼저 선택해주세요.")
    elif confirm_text.strip() != "시즌초기화":
        st.error("확인 문구가 일치하지 않습니다. `시즌초기화` 를 정확히 입력하세요.")
    else:
        b_ok, b_msg, b_path = _auto_backup_before_reset(db)
        if not b_ok:
            st.error("❌ " + b_msg)
            st.stop()
        else:
            st.success(f"✅ {b_msg}: `{b_path}`")

        ok, msg = _run_season_reset(db)
        if ok:
            st.success("✅ " + msg)
            st.info("다음 단계: 서버 재시작 후 필요 시 `전체스폰 리로드`를 실행하세요.")
        else:
            st.error("❌ 시즌 초기화 실패: " + msg)
