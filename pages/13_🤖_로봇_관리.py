"""
무인 PC(_robot) — DB에 행 추가 후 서버가 gm_server_command(reload robot)로 읽어 월드에 반영.
"""
from datetime import datetime, timedelta

import streamlit as st
import pandas as pd

import config
from utils.db_manager import get_db

st.set_page_config(page_title="로봇 관리", page_icon="🤖", layout="wide")
st.title("🤖 무인 PC (로봇) 관리")
st.caption("_robot 테이블과 `gm_server_command` 연동 (호두 서버 `GmDeliveryController`)")
st.caption(
    "사이드바에서 **같은 번호(13)가 겹치지 않도록** 창고 로그 페이지는 **20_🏦_창고로그** 로 분리해 두었습니다."
)

# PcRobotInstance: action 문자열을 equalsIgnoreCase 로만 비교 (오타 시 동작 안 함)
ROBOT_ACTION_OPTIONS = [
    "사냥 & PvP",
    "사냥",
    "PvP",
    "마을 대기",
    "허수아비 공격",
]

ROBOT_LIST_SQL = """
SELECT `objId`, `name`, `class`, `level`, `locMAP`, `locX`, `locY`,
       `스폰_여부`, `행동`, `title`, `clan_name`
FROM `_robot`
WHERE `objId` >= 1900000
ORDER BY `objId`
LIMIT 500
"""

ROBOT_COUNT_SQL = "SELECT COUNT(*) AS c FROM `_robot` WHERE `objId` >= 1900000"

GM_ROBOT_LIVE_DDL = """
CREATE TABLE IF NOT EXISTS `gm_robot_live` (
  `objId` INT NOT NULL PRIMARY KEY,
  `name` VARCHAR(45) NOT NULL DEFAULT '',
  `locMAP` INT NOT NULL DEFAULT 0,
  `locX` INT NOT NULL DEFAULT 0,
  `locY` INT NOT NULL DEFAULT 0,
  `행동` VARCHAR(64) NOT NULL DEFAULT '',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_gm_robot_live_updated` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='무인 PC 월드 스냅샷 (서버 RobotController)';
"""

GM_SERVER_STATUS_DDL = """
CREATE TABLE IF NOT EXISTS `gm_server_status` (
  `id` TINYINT NOT NULL PRIMARY KEY DEFAULT 1,
  `online` TINYINT NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='GM툴: 서버 가동/heartbeat';
"""

TARGET_ROBOT_OBJ_ID = 1900000


def _spawn_display_cell(raw) -> str:
    """`_robot`.`스폰_여부` 문자열을 표시용으로."""
    if raw is None:
        return "○ OFF"
    s = str(raw).strip().lower()
    if s in ("true", "1", "yes", "on", "y"):
        return "● ON"
    return "○ OFF"


def _fetch_server_live(db, tables: list) -> bool:
    """
    서버 JAR가 가동 중이면 `gm_server_status`에 heartbeat를 남김 (약 5초마다).
    테이블이 없거나 오래된 행이면 False → GM 툴은 월드 없음·스폰 표시 OFF로 본다.
    """
    if "gm_server_status" not in tables:
        return False
    try:
        row = db.fetch_one("SELECT `online`, `updated_at` FROM `gm_server_status` WHERE `id`=1")
        if not row:
            return False
        on = int(row.get("online") or 0)
        if on != 1:
            return False
        ut = row.get("updated_at")
        if ut is None:
            return False
        now = datetime.now()
        u0 = ut.replace(tzinfo=None) if getattr(ut, "tzinfo", None) else ut
        if now - u0 > timedelta(seconds=45):
            return False
        return True
    except Exception:
        return False

# 리니지(공식) 클래스별 베이스 스탯 — NC 리니지/클래식 안내(퍼플 라운지·plaync)에 공개된 표와 동일 순서: STR,DEX,CON,INT,WIS,CHA
NC_CLASS_BASE_STATS = {
    "기사": (16, 12, 14, 8, 9, 12),
    "요정": (11, 12, 12, 12, 12, 9),
    "마법사": (8, 7, 12, 12, 12, 8),
    # NC 공개 클래스 베이스(군주)
    "군주": (13, 10, 10, 10, 11, 13),
    # 다크엘프는 공식 표가 직업별 안내와 다를 수 있어, 흔한 1레벨 분포에 맞춘 봇용 베이스
    "다크엘프": (12, 15, 13, 11, 10, 9),
}

# L52 부근 일반적인 육성 방향 — 본섭 가이드 흐름을 반영한 봇용 목표치. 레벨은 베이스↔목표 선형 보간.
AUTO_STAT_TARGET_52 = {
    ("기사", "콘기사"): (22, 14, 24, 8, 9, 12),
    ("기사", "힘기사"): (25, 14, 20, 8, 9, 12),
    ("요정", "덱스요정"): (11, 24, 16, 12, 12, 9),
    ("요정", "콘요정"): (12, 16, 22, 12, 12, 9),
    ("마법사", "콘인트법사"): (8, 7, 18, 20, 12, 8),
    ("마법사", "콘위즈법사"): (8, 7, 18, 14, 18, 8),
    ("마법사", "위즈인트법사"): (8, 7, 12, 18, 18, 8),
    ("군주", "콘군주"): (14, 12, 22, 10, 11, 18),
    ("군주", "덱스군주"): (15, 22, 16, 10, 11, 14),
    ("다크엘프", "덱스다크엘프"): (12, 24, 17, 11, 10, 9),
    ("다크엘프", "콘다크엘프"): (15, 17, 23, 11, 10, 9),
}

# 무기 +8, `_robot.ac`는 방어구 인벤 없이도 방어력만 맞추기 위한 값(+6급 풀셋 느낌의 고정 AC).
_AUTO_W = 8
_AC_MELEE_6 = 78
_AC_RANGE_6 = 74
_AC_ROBE_6 = 64

# 서버 RobotController.getWeapon 과 동일 풀(다크엘프는 코드에 크로우/이도류 추가됨). item DB에 이름이 있어야 함.
AUTO_GEAR = {
    ("기사", "콘기사"): {
        "무기 이름": "일본도",
        "무기인챈트": _AUTO_W,
        "ac": _AC_MELEE_6,
        "마법 인형": "",
        "착용_안내": "+8 일본도. 방어구는 `_robot.ac`만 설정(인벤에 갑옷 아이템은 안 올림).",
    },
    ("기사", "힘기사"): {
        "무기 이름": "일본도",
        "무기인챈트": _AUTO_W,
        "ac": _AC_MELEE_6,
        "마법 인형": "",
        "착용_안내": "+8 일본도. 방어구는 `ac`로 근접 +6셋 수준만 반영.",
    },
    ("요정", "덱스요정"): {
        "무기 이름": "장궁",
        "무기인챈트": _AUTO_W,
        "ac": _AC_RANGE_6,
        "마법 인형": "",
        "착용_안내": "+8 장궁(화살은 로봇 스폰 시 서버가 자동 지급). 방어구는 `ac`로 원거리 +6셋 느낌.",
    },
    ("요정", "콘요정"): {
        "무기 이름": "장궁",
        "무기인챈트": _AUTO_W,
        "ac": _AC_MELEE_6 - 2,
        "마법 인형": "",
        "착용_안내": "+8 장궁. `ac`는 콘 위주에 맞춰 약간 높게.",
    },
    ("마법사", "콘인트법사"): {
        "무기 이름": "마나의 지팡이",
        "무기인챈트": _AUTO_W,
        "ac": _AC_ROBE_6,
        "마법 인형": "",
        "착용_안내": "+8 마나의 지팡이. 로브류는 `ac`로만 반영.",
    },
    ("마법사", "콘위즈법사"): {
        "무기 이름": "마나의 지팡이",
        "무기인챈트": _AUTO_W,
        "ac": _AC_ROBE_6,
        "마법 인형": "",
        "착용_안내": "+8 마나의 지팡이. 로브 `ac` 프리셋.",
    },
    ("마법사", "위즈인트법사"): {
        "무기 이름": "마나의 지팡이",
        "무기인챈트": _AUTO_W,
        "ac": _AC_ROBE_6,
        "마법 인형": "",
        "착용_안내": "+8 마나의 지팡이. 로브 `ac` 프리셋.",
    },
    ("군주", "콘군주"): {
        "무기 이름": "일본도",
        "무기인챈트": _AUTO_W,
        "ac": _AC_MELEE_6,
        "마법 인형": "",
        "착용_안내": "+8 일본도(군주 기본 풀). 방어구 `ac` 근접 +6셋 느낌.",
    },
    ("군주", "덱스군주"): {
        "무기 이름": "레이피어",
        "무기인챈트": _AUTO_W,
        "ac": _AC_RANGE_6,
        "마법 인형": "",
        "착용_안내": "+8 레이피어. 덱스형은 `ac`를 가죽/경갑 쪽으로 낮춤.",
    },
    ("다크엘프", "덱스다크엘프"): {
        "무기 이름": "파괴의 크로우",
        "무기인챈트": _AUTO_W,
        "ac": _AC_RANGE_6,
        "마법 인형": "",
        "착용_안내": "+8 파괴의 크로우. 방어구는 `ac`만.",
    },
    ("다크엘프", "콘다크엘프"): {
        "무기 이름": "파괴의 이도류",
        "무기인챈트": _AUTO_W,
        "ac": _AC_MELEE_6 - 4,
        "마법 인형": "",
        "착용_안내": "+8 파괴의 이도류. 콘형은 `ac` 조금 올림.",
    },
}

AUTO_CLASS_OPTIONS = ["기사", "요정", "마법사", "군주", "다크엘프"]

# 서버 Lineage.java: CHAOTIC=32768, NEUTRAL=65536, LAWFUL=98303 — 옛 GM 기본 32767은 카오틱에 가까움
ROBOT_LAWFUL_NEUTRAL = 65536


INSERT_ROBOT_SQL = """
INSERT INTO `_robot` (
  `objId`, `name`, `행동`, `str`, `dex`, `con`, `wis`, `inter`, `cha`,
  `locX`, `locY`, `locMAP`, `title`, `lawful`, `clanID`, `clan_name`,
  `class`, `sex`, `level`, `exp`, `mr`, `sp`, `무기인챈트`, `ac`, `heading`,
  `무기 이름`, `마법 인형`, `스폰_여부`
) VALUES (
  %s,%s,%s,%s,%s,%s,%s,%s,%s,
  %s,%s,%s,%s,%s,%s,%s,
  %s,%s,%s,%s,%s,%s,%s,%s,%s,
  %s,%s,%s
)
"""


def _auto_stats_for_level(klass: str, tendency: str, level: int) -> tuple[int, int, int, int, int, int]:
    """베이스(NC)에서 목표(L52)까지 레벨에 비례 보간 후 8~25 클램프."""
    base = NC_CLASS_BASE_STATS[klass]
    target = AUTO_STAT_TARGET_52[(klass, tendency)]
    t = 0.0 if level <= 1 else min(1.0, (level - 1) / 51.0)
    out: list[int] = []
    for b, g in zip(base, target):
        out.append(int(round(b + (g - b) * t)))
    lo, hi = 8, 25
    return tuple(max(lo, min(hi, x)) for x in out)


ROBOT_CREATE_SQL = """
CREATE TABLE IF NOT EXISTS `_robot` (
  `objId` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `행동` VARCHAR(64) NOT NULL DEFAULT '사냥 & PvP',
  `str` INT NOT NULL DEFAULT 18,
  `dex` INT NOT NULL DEFAULT 18,
  `con` INT NOT NULL DEFAULT 18,
  `wis` INT NOT NULL DEFAULT 18,
  `inter` INT NOT NULL DEFAULT 18,
  `cha` INT NOT NULL DEFAULT 18,
  `locX` INT NOT NULL DEFAULT 33936,
  `locY` INT NOT NULL DEFAULT 32318,
  `locMAP` INT NOT NULL DEFAULT 4,
  `title` VARCHAR(45) NOT NULL DEFAULT '',
  `lawful` INT NOT NULL DEFAULT 32767,
  `clanID` INT NOT NULL DEFAULT 0,
  `clan_name` VARCHAR(45) NOT NULL DEFAULT '',
  `class` VARCHAR(20) NOT NULL DEFAULT '기사',
  `sex` VARCHAR(10) NOT NULL DEFAULT '남자',
  `level` INT NOT NULL DEFAULT 52,
  `exp` DOUBLE NOT NULL DEFAULT 0 COMMENT '누적 경험치 (서버가 사냥 시 갱신)',
  `mr` INT NOT NULL DEFAULT 0,
  `sp` INT NOT NULL DEFAULT 0,
  `무기인챈트` INT NOT NULL DEFAULT 0,
  `ac` INT NOT NULL DEFAULT 0,
  `heading` INT NOT NULL DEFAULT 0,
  `무기 이름` VARCHAR(64) NOT NULL DEFAULT '',
  `마법 인형` VARCHAR(64) NOT NULL DEFAULT '',
  `스폰_여부` VARCHAR(16) NOT NULL DEFAULT 'true',
  PRIMARY KEY (`objId`),
  KEY `idx_robot_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='무인 PC (RobotController)';
"""

# 서버 RobotController.readBook: `robot_objId`=0 이면 전 무인 PC 공통, 특정 objId 행은 그 봇에만 추가 로드
ROBOT_BOOK_DDL = """
CREATE TABLE IF NOT EXISTS `_robot_book` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `robot_objId` INT NOT NULL DEFAULT 0 COMMENT '0=전체 공통, 1900000대=해당 봇 전용',
  `location` VARCHAR(128) NOT NULL DEFAULT '',
  `locX` INT NOT NULL DEFAULT 0,
  `locY` INT NOT NULL DEFAULT 0,
  `locMAP` INT NOT NULL DEFAULT 0,
  `입장레벨` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `idx_robot_book_robot` (`robot_objId`),
  KEY `idx_robot_book_map` (`locMAP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='무인 PC 사냥 북 (ItemTeleport 맵과 맞춤)';
"""

# 서버 Lineage: 군주=1, 기사=2, 요정=4, 마법사=8, 다크엘프=16 — item_teleport.if_class 비트마스크
ROBOT_CLASS_BIT = {"군주": 1, "기사": 2, "요정": 4, "마법사": 8, "다크엘프": 16}

BOOK_INSERT_SQL = """
INSERT INTO `_robot_book` (`robot_objId`, `location`, `locX`, `locY`, `locMAP`, `입장레벨`)
VALUES (%s, %s, %s, %s, %s, %s)
"""


def _queue_server_robot_reload(db) -> tuple[bool, str]:
    """전체 _robot + book/poly/skill 리로드."""
    return db.execute_query_ex(
        "INSERT INTO `gm_server_command` (`command`, `param`, `executed`) VALUES ('reload_robot', '', 0)"
    )


def _queue_server_robot_reload_one(db, obj_id: int) -> tuple[bool, str]:
    """한 마리만 동기화 (서버에 reload_robot_one + 동일 book/poly/skill 처리)."""
    return db.execute_query_ex(
        "INSERT INTO `gm_server_command` (`command`, `param`, `executed`) VALUES ('reload_robot_one', %s, 0)",
        (str(int(obj_id)),),
    )


def _queue_gm_command(db, command: str, param: str = "") -> tuple[bool, str]:
    """robot_on / robot_off 등 임의 gm_server_command."""
    return db.execute_query_ex(
        "INSERT INTO `gm_server_command` (`command`, `param`, `executed`) VALUES (%s, %s, 0)",
        (command, param or ""),
    )


def _set_robot_spawn_one(db, obj_id: int, spawn_on: bool) -> tuple[bool, str]:
    """한 마리 `_robot.스폰_여부` 만 갱신 (true / false 문자열)."""
    v = "true" if spawn_on else "false"
    return db.execute_query_ex(
        "UPDATE `_robot` SET `스폰_여부`=%s WHERE `objId`=%s",
        (v, int(obj_id)),
    )


def _ensure_robot_exp_column(db) -> None:
    """구버전 `_robot`에 `exp`가 없으면 추가 (서버·GM 툴 INSERT 공통)."""
    ok, err = db.execute_query_ex(
        "ALTER TABLE `_robot` ADD COLUMN `exp` DOUBLE NOT NULL DEFAULT 0 COMMENT '누적 경험치' AFTER `level`"
    )
    if ok:
        return
    em = (err or "").lower()
    if "1060" in (err or "") or "duplicate" in em:
        return
    st.warning(
        f"⚠️ `_robot.exp` 컬럼 자동 추가 실패. MySQL에서 직접 실행하세요:\n"
        f"`ALTER TABLE _robot ADD COLUMN exp DOUBLE NOT NULL DEFAULT 0 AFTER level`\n\n오류: {err}"
    )


def _ensure_robot_book_robot_obj_id_column(db) -> None:
    """구버전 `_robot_book`에 `robot_objId` 없으면 추가 (기본 0 = 전역 공통)."""
    if "_robot_book" not in db.get_all_tables():
        return
    ok, err = db.execute_query_ex(
        "ALTER TABLE `_robot_book` ADD COLUMN `robot_objId` INT NOT NULL DEFAULT 0 "
        "COMMENT '0=전체 무인 PC 공통, 특정 objId=해당 봇만'"
    )
    if ok:
        return
    em = (err or "").lower()
    if "1060" in (err or "") or "duplicate" in em:
        return
    st.warning(
        "⚠️ `_robot_book.robot_objId` 컬럼 자동 추가 실패. MySQL에서 직접 실행하세요:\n"
        "`ALTER TABLE _robot_book ADD COLUMN robot_objId INT NOT NULL DEFAULT 0`\n\n"
        f"오류: {err}"
    )


def _merge_robot_live_columns(db, tables, rows: list, server_live: bool) -> list:
    """`gm_server_status`가 살아 있을 때만 `gm_robot_live`를 신뢰해 월드 반영 컬럼을 붙입니다."""
    if not rows:
        return rows
    if not server_live:
        for r in rows:
            r["월드상태"] = "○ 월드 없음 (서버 정지)"
            r["월드맵"] = None
            r["실행행동"] = ""
            r["스냅샷시각"] = ""
            r["스폰_표시"] = "○ OFF (서버 정지)"
        return rows

    live_map: dict = {}
    if "gm_robot_live" in tables:
        try:
            for x in db.fetch_all(
                "SELECT `objId`, `locMAP`, `locX`, `locY`, `행동` AS live_행동, `updated_at` FROM `gm_robot_live`"
            ) or []:
                live_map[int(x["objId"])] = x
        except Exception:
            live_map = {}
    now = datetime.now()
    for r in rows:
        r["스폰_표시"] = _spawn_display_cell(r.get("스폰_여부"))
        oid = int(r["objId"])
        liv = live_map.get(oid)
        if liv:
            r["월드상태"] = "● 월드 온라인"
            r["월드맵"] = liv.get("locMAP")
            r["실행행동"] = (liv.get("live_행동") or "").strip()
            ut = liv.get("updated_at")
            if ut is not None and hasattr(ut, "strftime"):
                r["스냅샷시각"] = ut.strftime("%Y-%m-%d %H:%M:%S")
                try:
                    u0 = ut.replace(tzinfo=None) if getattr(ut, "tzinfo", None) else ut
                    if now - u0 > timedelta(seconds=45):
                        r["월드상태"] = "● 월드 온라인 (동기화 지연?)"
                except Exception:
                    pass
            else:
                r["스냅샷시각"] = str(ut) if ut else ""
        else:
            r["월드상태"] = "○ 월드 없음"
            r["월드맵"] = None
            r["실행행동"] = ""
            r["스냅샷시각"] = ""
    return rows


db = get_db()
ok, msg = db.test_connection()
if not ok:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

tables = db.get_all_tables()
has_robot = "_robot" in tables
has_gm_cmd = "gm_server_command" in tables

with st.expander("💡 닉네임·서버 다운 시 동작", expanded=True):
    st.markdown("""
    **닉네임(`name`)**  
    - 게임 안에서 보이는 **로봇 캐릭터 이름**이며 `_robot.name` 컬럼입니다.  
    - **실제 유저 캐릭터 `characters.name`과 겹치지 않게** 쓰는 것을 권장합니다. (같은 이름이면 혼선·버그 가능)  
    - 로봇끼리도 이름이 중복이면 서버 기동/리로드 시 **앞줄만 살고 뒤쪽은 스폰에서 빠질 수** 있습니다.

    **서버를 끄면**  
    - `_robot` **DB 데이터는 그대로** 남습니다. (설정용 `스폰_여부` 컬럼은 **변하지 않습니다**.)  
    - 서버는 **`gm_server_status`** 를 오프라인으로 바꾸고 **`gm_robot_live`** 를 비웁니다.  
    - 이 페이지의 **「스폰_표시」** 는 서버가 꺼졌을 때 **OFF (서버 정지)** 로만 보이게 해, 월드에 실제로 안 올라온 상태를 나타냅니다.  
    - 다시 켜면 `lineage.conf`의 **`robot_auto_pc`** 가 켜져 있으면 시작 시 `readPcRobot()`으로 일부가 올라올 수 있고,  
      꺼져 있으면 **아래 「게임 서버 반영」** 또는 게임 내 **`.리로드 로봇`** 으로 다시 읽어야 합니다.
    """)

if not has_gm_cmd:
    st.error(
        "❌ `gm_server_command` 테이블이 없습니다. "
        "메인 앱「테이블 상태 확인」에서 필수 테이블을 생성한 뒤 다시 오세요."
    )
    st.stop()

if not has_robot:
    st.warning("⚠️ `_robot` 테이블이 없습니다. 아래에서 생성할 수 있습니다.")
    if st.button("🔨 `_robot` 테이블 생성 (추정 스키마)"):
        ok_t, err_t = db.execute_query_ex(ROBOT_CREATE_SQL.strip())
        if ok_t:
            st.success("✅ `_robot` 생성 완료. 페이지를 새로고침하세요.")
            st.rerun()
        else:
            st.error(f"❌ 생성 실패: {err_t}")
    st.stop()

_ensure_robot_exp_column(db)
if "_robot_book" in tables:
    _ensure_robot_book_robot_obj_id_column(db)

with st.expander("📖 사냥터 북 (`_robot_book`) — 마을에서 나가려면 필수", expanded=False):
    st.markdown("""
    서버는 **`robot_objId` = 0** 인 행은 **모든 무인 PC**에, **특정 objId** 행은 **그 봇에만** 추가로 붙입니다.  
    **`BookController.find` 목록이 비면** 마을(안전지)에서 **사냥터로 텔레포트하지 않습니다.**  
    마을에서 나가려면 **로봇 레벨 ≥ `입장레벨`**, `locMAP`이 **`item_teleport.goto_map`** 과 맞고 **`ItemTeleport.toTeleport`** 조건(직업·레벨 등)을 통과해야 합니다.  
    **봇별로 사냥지를 고르는 편집**은 아래 **「📖 로봇북」** 탭을 사용하세요.
    """)
    if "_robot_book" not in tables:
        st.error("❌ `_robot_book` 테이블이 DB에 없습니다. 「📖 로봇북」 탭에서 생성하거나 스키마를 넣으세요.")
    else:
        try:
            cnt_bk = db.fetch_one("SELECT COUNT(*) AS c FROM `_robot_book`")
            book_total = int(cnt_bk["c"]) if cnt_bk and cnt_bk.get("c") is not None else 0
            bk_rows = db.fetch_all(
                "SELECT * FROM `_robot_book` ORDER BY `robot_objId`, `locMAP`, `locX` LIMIT 200"
            ) or []
            st.metric("`_robot_book` 전체 행 수", f"{book_total}건")
            if book_total > len(bk_rows):
                st.caption(f"아래 표는 **최대 200행**만 표시합니다. (전체 **{book_total}건** 중 **{len(bk_rows)}행** 표시)")
            if bk_rows:
                st.success("✅ 데이터가 있습니다.")
                st.dataframe(pd.DataFrame(bk_rows), width='stretch', hide_index=True)
            else:
                st.warning("⚠️ **0건**입니다. 이 상태면 마을에서 사냥터로 **나가지 않습니다.**")
        except Exception as ex:
            st.error(f"❌ `_robot_book` 조회 실패: {ex}")

tab_list, tab_book, tab_add, tab_reload = st.tabs(
    ["📋 로봇 목록", "📖 로봇북", "➕ 로봇 추가", "🔄 게임 서버 반영"]
)

with tab_list:
    cnt_row = db.fetch_one(ROBOT_COUNT_SQL)
    robot_total = int(cnt_row["c"]) if cnt_row and cnt_row.get("c") is not None else 0
    st.subheader(f"등록된 무인 PC ({robot_total}건)")
    server_live = _fetch_server_live(db, tables)
    m1, m2, m3 = st.columns(3)
    with m1:
        st.metric("게임 서버 (heartbeat)", "● 가동" if server_live else "○ 정지")
    with m2:
        st.metric("`gm_server_status` 테이블", "있음" if "gm_server_status" in tables else "없음")
    with m3:
        st.metric("`gm_robot_live` 테이블", "있음" if "gm_robot_live" in tables else "없음")
    st.caption(
        "`gm_server_status` heartbeat 기준(45초 이상 없으면 정지). "
        "체크 삭제·**스폰 on/off**·월드 반영은 「🔄 게임 서버 반영」 탭."
    )
    if "gm_server_status" not in tables:
        with st.expander("📡 서버 가동 표시용 `gm_server_status` 테이블", expanded=False):
            st.markdown(
                "게임 서버 **최신 JAR**는 약 **5초마다** heartbeat를 남깁니다. 없으면 이 페이지는 항상 **서버 정지**처럼 표시합니다."
            )
            if st.button("🔨 `gm_server_status` 테이블 생성"):
                ok_ss, err_ss = db.execute_query_ex(GM_SERVER_STATUS_DDL.strip())
                ok_ins, err_ins = (True, "")
                if ok_ss:
                    ok_ins, err_ins = db.execute_query_ex(
                        "INSERT IGNORE INTO `gm_server_status` (`id`, `online`) VALUES (1, 0)"
                    )
                if ok_ss and ok_ins:
                    st.success("✅ 생성 완료. 페이지를 새로고침하세요.")
                    st.rerun()
                else:
                    st.error(f"❌ {err_ss or err_ins}")
    if "gm_robot_live" not in tables:
        with st.expander("📡 월드 온라인 표시용 `gm_robot_live` 테이블", expanded=False):
            st.markdown(
                "게임 서버 **JAR**에 `RobotController` 스냅샷 동기화가 포함되어 있으면 약 **10초마다** 이 테이블이 갱신됩니다. "
                "없으면 목록의 「월드상태」는 항상 오프라인으로만 보입니다."
            )
            if st.button("🔨 `gm_robot_live` 테이블 생성"):
                ok_lt, err_lt = db.execute_query_ex(GM_ROBOT_LIVE_DDL.strip())
                if ok_lt:
                    st.success("✅ 생성 완료. 페이지를 새로고침하세요.")
                    st.rerun()
                else:
                    st.error(f"❌ {err_lt}")
    else:
        st.caption(
            "서버 **가동 중**일 때만 「월드상태」·「실행행동」이 `gm_robot_live`를 반영합니다. "
            "정지 중에는 stale 데이터를 숨깁니다."
        )
    confirm_delete = st.checkbox("DB에서 로봇 행 삭제에 동의합니다 (복구는 백업 필요)", value=False, key="robot_confirm_delete")
    try:
        rows = db.fetch_all(ROBOT_LIST_SQL)
        rows = _merge_robot_live_columns(db, tables, rows or [], server_live)
        if rows:
            preferred = [
                "삭제선택",
                "objId",
                "name",
                "월드상태",
                "월드맵",
                "실행행동",
                "스냅샷시각",
                "class",
                "level",
                "locMAP",
                "locX",
                "locY",
                "스폰_표시",
                "스폰_여부",
                "행동",
                "title",
                "clan_name",
            ]
            df = pd.DataFrame(rows)
            if "삭제선택" in df.columns:
                df = df.drop(columns=["삭제선택"])
            cols_order = [c for c in preferred if c in df.columns] + [c for c in df.columns if c not in preferred]
            df = df[cols_order]
            df.insert(0, "삭제선택", False)
            disabled_cols = [c for c in df.columns if c != "삭제선택"]
            edited = st.data_editor(
                df,
                column_config={
                    "삭제선택": st.column_config.CheckboxColumn("삭제", default=False, help="체크 후 아래 버튼으로 `_robot`에서 삭제"),
                    "월드맵": st.column_config.NumberColumn("월드맵", help="서버가 기록한 현재 맵 ID"),
                },
                disabled=disabled_cols,
                hide_index=True,
                width='stretch',
                height=420,
                num_rows="fixed",
                key="robot_list_editor",
            )
            c_del, _ = st.columns([1, 3])
            with c_del:
                if st.button("🗑️ 체크한 로봇 DB에서 삭제", type="primary", disabled=not confirm_delete):
                    sel = edited["삭제선택"].fillna(False).astype(bool)
                    picked = edited.loc[sel, "objId"]
                    ids = [int(x) for x in picked.tolist() if x is not None]
                    if not ids:
                        st.warning("삭제할 로봇을 체크하세요.")
                    else:
                        ph = ",".join(["%s"] * len(ids))
                        sql_del = f"DELETE FROM `_robot` WHERE `objId` IN ({ph})"
                        ok_del, err_del = db.execute_query_ex(sql_del, tuple(ids))
                        if ok_del:
                            st.success(f"✅ {len(ids)}건을 `_robot`에서 삭제했습니다. 서버 반영은 「게임 서버 반영」에서 하세요.")
                            st.rerun()
                        else:
                            st.error(f"❌ 삭제 실패: {err_del}")
        else:
            st.info("objId ≥ 1900000 인 로봇 행이 없습니다.")
    except Exception as e:
        st.error(f"조회 실패: {e}")

with tab_book:
    st.subheader("봇별 사냥 북 (`_robot_book`)")
    st.markdown(
        """
        - **`robot_objId` = 0** → 모든 무인 PC에 공통. **특정 objId** → 그 봇에만 추가로 붙습니다 (서버 `readBook` 합집합).
        - 마을 안전지에서 서버가 북 목록을 **무작위로 고른 뒤** `item_teleport` 조건을 통과하면 해당 맵으로 텔합니다.
        - DB 저장 후 **`reload_robot_one`(선택 봇)** 또는 **`reload_robot`** 으로 서버 메모리에 반영하세요.
        """
    )

    if "_robot_book" not in tables:
        st.warning("`_robot_book` 테이블이 없습니다. 생성 후 다시 오세요.")
        if st.button("🔨 `_robot_book` 테이블 생성", key="robot_book_create_tbl"):
            ok_tb, err_tb = db.execute_query_ex(ROBOT_BOOK_DDL.strip())
            if ok_tb:
                st.success("✅ 생성했습니다.")
                st.rerun()
            else:
                st.error(f"❌ {err_tb}")
    else:
        _ensure_robot_book_robot_obj_id_column(db)

        robot_rows = db.fetch_all(ROBOT_LIST_SQL) or []
        if not robot_rows:
            st.info("`_robot`에 objId ≥ 1900000 인 로봇이 없습니다. 「로봇 추가」에서 먼저 등록하세요.")
        else:
            labels = [f"{int(r['objId'])} — {r.get('name') or ''}" for r in robot_rows]
            pick = st.selectbox("무인 PC 선택", labels, key="robot_book_pick_bot")
            oid_sel = int(pick.split(" — ", 1)[0])
            row_sel = next((r for r in robot_rows if int(r["objId"]) == oid_sel), robot_rows[0])
            klass = (row_sel.get("class") or "").strip()
            lvl = int(row_sel.get("level") or 1)
            c_a, c_b = st.columns(2)
            with c_a:
                st.caption(f"직업: **{klass or '?'}** · 레벨: **{lvl}**")
            with c_b:
                apply_reload = st.checkbox(
                    "저장 후 이 봇 `reload_robot_one` 큐에 넣기",
                    value=True,
                    key="robot_book_apply_reload",
                )

            st.markdown("##### 이 봇에 붙어 있는 북 (전역 0 + 전용)")
            try:
                cur_books = db.fetch_all(
                    "SELECT * FROM `_robot_book` WHERE `robot_objId` = 0 OR `robot_objId` = %s "
                    "ORDER BY `robot_objId`, `locMAP`",
                    (oid_sel,),
                ) or []
                if cur_books:
                    st.dataframe(pd.DataFrame(cur_books), width='stretch', hide_index=True)
                else:
                    st.warning("이 봇 기준으로 보이는 북이 **없습니다** (전역 0 + 전용 행 모두 없음).")
            except Exception as exb:
                st.error(f"북 조회 실패: {exb}")

            st.divider()
            del_ded = st.checkbox(
                "다음 추가 전에 **이 봇 전용 행만** 삭제 (`robot_objId` = 선택 objId)",
                value=False,
                key="robot_book_clear_dedicated",
            )
            if st.button("🗑️ 이 봇 전용 북만 지금 삭제", key="robot_book_del_ded_btn"):
                ok_d, err_d = db.execute_query_ex(
                    "DELETE FROM `_robot_book` WHERE `robot_objId` = %s",
                    (oid_sel,),
                )
                if ok_d:
                    st.success(f"✅ objId **{oid_sel}** 전용 `_robot_book` 행을 삭제했습니다.")
                    if apply_reload:
                        ok_q, err_q = _queue_server_robot_reload_one(db, oid_sel)
                        if ok_q:
                            st.success("✅ `reload_robot_one` 큐 등록.")
                        else:
                            st.warning(err_q)
                    st.rerun()
                else:
                    st.error(f"❌ {err_d}")

            st.markdown("##### `item_teleport`에서 사냥 맵 후보 고르기")
            if "item_teleport" not in tables:
                st.error("`item_teleport` 테이블이 없습니다. 순간이동 주문서 DB를 확인하세요.")
            else:
                tel_all = (
                    db.fetch_all(
                        "SELECT `uid`, `name`, `goto_x`, `goto_y`, `goto_map`, `if_level`, `if_class` "
                        "FROM `item_teleport` ORDER BY `goto_map`, `uid`"
                    )
                    or []
                )
                by_map: dict[int, dict] = {}
                for t in tel_all:
                    gm = int(t.get("goto_map") or 0)
                    if gm <= 0:
                        continue
                    if gm not in by_map:
                        by_map[gm] = t
                mask = ROBOT_CLASS_BIT.get(klass, 0)
                filter_ok = st.checkbox(
                    "이 봇 직업·레벨로 `toTeleport`에 걸리기 쉬운 행만 표시 (if_class / if_level)",
                    value=True,
                    key="robot_book_filter_tel",
                )

                def _tel_eligible(t: dict) -> bool:
                    if_lvl = int(t.get("if_level") or 0)
                    if_cls = int(t.get("if_class") or 0)
                    if if_lvl > 0 and lvl < if_lvl:
                        return False
                    if if_cls == 0:
                        # 서버 ItemTeleport: if_class 가 0이면 직업 마스크와 AND 가 항상 0 → 사실상 사용 불가
                        return False
                    if mask == 0:
                        return True
                    return (if_cls & mask) != 0

                cand = [by_map[m] for m in sorted(by_map.keys())]
                if filter_ok:
                    cand = [t for t in cand if _tel_eligible(t)]
                if not cand:
                    st.warning("조건에 맞는 `item_teleport` 행이 없습니다. 필터를 끄거나 DB를 확인하세요.")
                else:
                    opt_labels: list[str] = []
                    opt_rows: list[dict] = []
                    for t in cand:
                        gm = int(t.get("goto_map") or 0)
                        nm = (t.get("name") or "")[:80]
                        lab = f"map {gm} | uid {int(t.get('uid') or 0)} | {nm}"
                        opt_labels.append(lab)
                        opt_rows.append(t)
                    chosen_labs = st.multiselect(
                        "추가할 목적지 (맵당 대표 1행 — 첫 `uid`)",
                        opt_labels,
                        key="robot_book_multiselect_tel",
                    )
                    min_lv = st.number_input(
                        "북 `입장레벨` (로봇 레벨 ≥ 이 값이어야 후보로 쓰임)",
                        min_value=1,
                        max_value=99,
                        value=1,
                        key="robot_book_min_lv",
                    )
                    if min_lv > lvl:
                        st.warning(f"선택한 봇 레벨({lvl})이 `입장레벨`({min_lv})보다 낮으면 마을에서 이 북은 쓰이지 않습니다.")

                    if st.button("➕ 선택한 맵을 **이 봇 전용** 북에 INSERT", type="primary", key="robot_book_ins_btn"):
                        picked = [opt_rows[opt_labels.index(lb)] for lb in chosen_labs if lb in opt_labels]
                        if not picked:
                            st.warning("맵을 하나 이상 고르세요.")
                        else:
                            if del_ded:
                                db.execute_query_ex(
                                    "DELETE FROM `_robot_book` WHERE `robot_objId` = %s",
                                    (oid_sel,),
                                )
                            n_ok = 0
                            err_last = ""
                            for t in picked:
                                loc = (t.get("name") or f"map_{t.get('goto_map')}").strip()[:128] or "teleport"
                                gx = int(t.get("goto_x") or 0)
                                gy = int(t.get("goto_y") or 0)
                                gmap = int(t.get("goto_map") or 0)
                                ok_i, err_i = db.execute_query_ex(
                                    BOOK_INSERT_SQL.strip(),
                                    (oid_sel, loc, gx, gy, gmap, int(min_lv)),
                                )
                                if ok_i:
                                    n_ok += 1
                                else:
                                    err_last = err_i or ""
                            if n_ok:
                                st.success(f"✅ **{n_ok}**건 INSERT (`robot_objId`={oid_sel}).")
                                if apply_reload:
                                    ok_q, err_q = _queue_server_robot_reload_one(db, oid_sel)
                                    if ok_q:
                                        st.success("✅ `reload_robot_one` 큐 등록.")
                                    else:
                                        st.warning(err_q)
                                st.rerun()
                            else:
                                st.error(f"❌ INSERT 실패: {err_last}")

            st.divider()
            st.markdown("##### 전역 공통 북 (`robot_objId` = 0)")
            st.caption("모든 무인 PC가 공유합니다. 직접 SQL로 넣거나, 아래에서 한 번에 추가할 수 있습니다.")
            if "item_teleport" in tables and st.checkbox(
                "전역 북에도 같은 방식으로 추가 (robot_objId=0)", value=False, key="robot_book_global_add"
            ):
                tel_all_g = (
                    db.fetch_all(
                        "SELECT `uid`, `name`, `goto_x`, `goto_y`, `goto_map`, `if_level`, `if_class` "
                        "FROM `item_teleport` ORDER BY `goto_map`, `uid`"
                    )
                    or []
                )
                by_map_g: dict[int, dict] = {}
                for t in tel_all_g:
                    gm = int(t.get("goto_map") or 0)
                    if gm > 0 and gm not in by_map_g:
                        by_map_g[gm] = t
                cand_g = [by_map_g[m] for m in sorted(by_map_g.keys())]
                labs_g = [
                    f"map {int(t.get('goto_map') or 0)} | uid {int(t.get('uid') or 0)} | {(t.get('name') or '')[:60]}"
                    for t in cand_g
                ]
                ch_g = st.multiselect("전역에 넣을 맵", labs_g, key="robot_book_multiselect_global")
                min_g = st.number_input("전역 북 입장레벨", min_value=1, max_value=99, value=1, key="robot_book_min_g")
                reload_all_after_global = st.checkbox(
                    "저장 후 `reload_robot` 큐에 넣기 (전 무인 PC·북 메모리 갱신)",
                    value=False,
                    key="robot_book_reload_all_global",
                )
                if st.button("➕ 전역(0) 북에 INSERT", key="robot_book_ins_global"):
                    picked_g = [cand_g[labs_g.index(lb)] for lb in ch_g if lb in labs_g]
                    if not picked_g:
                        st.warning("맵을 고르세요.")
                    else:
                        ng = 0
                        eg = ""
                        for t in picked_g:
                            loc = (t.get("name") or f"map_{t.get('goto_map')}").strip()[:128] or "teleport"
                            ok_g, err_g = db.execute_query_ex(
                                BOOK_INSERT_SQL.strip(),
                                (0, loc, int(t.get("goto_x") or 0), int(t.get("goto_y") or 0), int(t.get("goto_map") or 0), int(min_g)),
                            )
                            if ok_g:
                                ng += 1
                            else:
                                eg = err_g or ""
                        if ng:
                            st.success(f"✅ 전역 북 **{ng}**건 추가했습니다.")
                            if reload_all_after_global:
                                ok_ra, err_ra = _queue_server_robot_reload(db)
                                if ok_ra:
                                    st.success("✅ `reload_robot` 큐 등록.")
                                else:
                                    st.warning(err_ra)
                            else:
                                st.info("「게임 서버 반영」에서 `reload_robot` 또는 봇별 리로드를 실행하세요.")
                            st.rerun()
                        else:
                            st.error(f"❌ {eg}")

with tab_add:
    mx = db.fetch_one(
        "SELECT COALESCE(MAX(`objId`), 1899999) AS m FROM `_robot` WHERE `objId` >= 1900000"
    )
    suggest_id = int(mx["m"]) + 1 if mx and mx.get("m") is not None else 1900000

    _h1, _h2 = st.columns([5, 1])
    with _h1:
        st.subheader("새 로봇 행 추가")
    with _h2:
        use_auto = st.checkbox("자동생성", value=False, key="robot_tab_auto")

    c1, c2, c3 = st.columns(3)
    with c1:
        obj_id = st.number_input("objId (≥1900000)", min_value=1900000, max_value=1999999, value=int(suggest_id))
    with c2:
        name = st.text_input("닉네임 (name)", max_chars=45, help="게임에 보이는 이름. 유저 캐릭터와 중복 비권장.")
    with c3:
        spawn_on = st.selectbox("스폰_여부", ["true", "false"], index=0)

    if use_auto:
        st.markdown("**빠른 자동 생성** — 스탯·무기·방어(`ac`)는 프리셋, 아래 항목만 입력하세요.")
        st.caption(
            "스탯: NC **클래스별 베이스**에서 시작해 성향별 **52레벨 목표**까지 보간(각 능력치 **8~25**). "
            "무기 **+8**(`무기인챈트`). 방어구는 `_robot`에 슬롯이 없어 **`ac` 숫자만** +6급 풀셋 느낌으로 넣습니다(실제 갑옷 아이템 착용 아님). "
            "무기 이름은 `RobotController.getWeapon` 풀과 맞춤(군주·다크엘프 포함)."
        )
        _ac1, _ac2 = st.columns(2)
        with _ac1:
            klass_auto = st.selectbox("직업 (class)", AUTO_CLASS_OPTIONS, key="robot_auto_class")
            tendency_by_class = {
                "기사": ["콘기사", "힘기사"],
                "요정": ["덱스요정", "콘요정"],
                "마법사": ["콘인트법사", "콘위즈법사", "위즈인트법사"],
                "군주": ["콘군주", "덱스군주"],
                "다크엘프": ["덱스다크엘프", "콘다크엘프"],
            }
            tendency = st.selectbox(
                "스탯 성향",
                tendency_by_class[klass_auto],
                key="robot_auto_tendency",
            )
            sex_auto = st.selectbox("성별 (sex)", ["남자", "여자"], key="robot_auto_sex")
            level_auto = st.number_input("레벨", min_value=1, max_value=99, value=52, key="robot_auto_level")
            action_auto = st.selectbox(
                "행동 (_robot.행동)",
                ROBOT_ACTION_OPTIONS,
                index=0,
                key="robot_auto_action",
            )
        with _ac2:
            town_auto = st.selectbox(
                "좌표 프리셋",
                list(config.TOWN_COORDINATES.keys()),
                key="robot_auto_town",
            )
            tc_a = config.TOWN_COORDINATES[town_auto]
            loc_x_a = st.number_input("locX", value=int(tc_a["x"]), key="robot_auto_x")
            loc_y_a = st.number_input("locY", value=int(tc_a["y"]), key="robot_auto_y")
            loc_map_a = st.number_input("locMAP", value=int(tc_a["map_id"]), key="robot_auto_map")

        str_a, dex_a, con_a, int_a, wis_a, cha_a = _auto_stats_for_level(klass_auto, tendency, int(level_auto))
        gear_a = AUTO_GEAR[(klass_auto, tendency)]
        wn_a = str(gear_a["무기 이름"])
        we_a = int(gear_a["무기인챈트"])
        ac_a = int(gear_a.get("ac") or 0)
        dn_a = str(gear_a.get("마법 인형") or "")
        wear_note = str(gear_a.get("착용_안내") or "")

        st.markdown("**적용될 스탯·장비 (미리보기)**")
        _pv1, _pv2 = st.columns(2)
        with _pv1:
            st.write(
                f"STR {str_a} · DEX {dex_a} · CON {con_a} · INT {int_a} · WIS {wis_a} · CHA {cha_a}"
            )
        with _pv2:
            st.write(f"무기: `{wn_a}` (+{we_a})  ·  AC: `{ac_a}`  ·  인형: `{dn_a or '(없음)'}`")
        with st.expander("착용·방어구가 어떻게 반영되나요?", expanded=False):
            st.markdown(
                wear_note
                + "\n\n`_robot` 테이블은 **무기 한 종류 + 인형 문자열 + `ac` 정수**만 저장합니다. "
                "망토·투구 등 개별 슬롯은 없고, 방어력은 **`ac` 한 칸**으로만 서버에 넘어갑니다."
            )

        apply_auto = st.checkbox(
            "저장 직후 이 로봇만 게임에 반영 (`reload_robot_one`)",
            value=True,
            key="robot_auto_apply_reload",
        )

        warn_a: list[str] = []
        if name.strip():
            dup_r = db.fetch_one("SELECT 1 AS x FROM `_robot` WHERE LOWER(`name`)=LOWER(%s) LIMIT 1", (name.strip(),))
            if dup_r:
                warn_a.append("동일 이름 로봇이 이미 있습니다.")
            if "characters" in tables:
                dup_c = db.fetch_one("SELECT 1 AS x FROM `characters` WHERE `name`=%s LIMIT 1", (name.strip(),))
                if dup_c:
                    warn_a.append("⚠️ 실제 캐릭터 `characters`와 이름이 겹칩니다.")
        for w in warn_a:
            st.warning(w)

        if st.button("⚡ 자동 구성으로 DB에 INSERT", type="primary", key="robot_auto_insert_btn"):
            if not name or not name.strip():
                st.error("닉네임(name)을 입력하세요.")
            else:
                params_a = (
                    int(obj_id),
                    name.strip(),
                    action_auto.strip() or "사냥 & PvP",
                    int(str_a),
                    int(dex_a),
                    int(con_a),
                    int(wis_a),
                    int(int_a),
                    int(cha_a),
                    int(loc_x_a),
                    int(loc_y_a),
                    int(loc_map_a),
                    "",
                    ROBOT_LAWFUL_NEUTRAL,
                    0,
                    "",
                    klass_auto,
                    sex_auto,
                    int(level_auto),
                    0.0,
                    0,
                    0,
                    int(we_a),
                    int(ac_a),
                    0,
                    wn_a,
                    dn_a,
                    spawn_on,
                )
                ok_ins, err_ins = db.execute_query_ex(INSERT_ROBOT_SQL.strip(), params_a)
                if ok_ins:
                    st.success("✅ `_robot`에 자동 프리셋으로 저장했습니다.")
                    if apply_auto:
                        ok_q, err_q = _queue_server_robot_reload_one(db, int(obj_id))
                        if ok_q:
                            st.success("✅ `reload_robot_one` 명령을 큐에 넣었습니다.")
                        else:
                            st.warning(f"DB 저장은 됐으나 반영 큐 실패: {err_q}")
                    else:
                        st.info("「게임 서버 반영」 탭에서 해당 로봇만 리로드하세요.")
                else:
                    st.error(f"❌ INSERT 실패: {err_ins}")

    else:
        c4, c5 = st.columns(2)
        with c4:
            klass = st.selectbox("직업 (class)", ["기사", "군주", "요정", "마법사", "다크엘프"])
            sex = st.selectbox("성별 (sex)", ["남자", "여자"])
            level = st.number_input("레벨", min_value=1, max_value=99, value=52)
            action_pick = st.selectbox(
                "행동 (_robot.행동)",
                ROBOT_ACTION_OPTIONS,
                index=0,
                help=(
                    "서버 `PcRobotInstance` 가 이 문자열을 그대로 비교합니다. "
                    "사냥=몬스터만, PvP=유저 위주, 사냥 & PvP=혼합, 마을 대기=철수, 허수아비 공격=연습."
                ),
            )
            with st.expander("직접 입력으로 덮어쓰기 (고급)", expanded=False):
                action_override = st.text_input(
                    "커스텀 행동 문자열",
                    value="",
                    help="비우면 위 드롭다운 값이 DB에 저장됩니다. 서버 코드에 없는 문자열은 로봇이 멈출 수 있습니다.",
                )
            action = action_override.strip() if action_override.strip() else action_pick
        with c5:
            town = st.selectbox("좌표 프리셋", list(config.TOWN_COORDINATES.keys()))
            tc = config.TOWN_COORDINATES[town]
            loc_x = st.number_input("locX", value=int(tc["x"]))
            loc_y = st.number_input("locY", value=int(tc["y"]))
            loc_map = st.number_input("locMAP", value=int(tc["map_id"]))

        st.markdown("**스탯 (기본 18)**")
        s1, s2, s3, s4, s5, s6 = st.columns(6)
        with s1:
            str_v = st.number_input("str", value=18)
        with s2:
            dex_v = st.number_input("dex", value=18)
        with s3:
            con_v = st.number_input("con", value=18)
        with s4:
            wis_v = st.number_input("wis", value=18)
        with s5:
            int_v = st.number_input("inter", value=18)
        with s6:
            cha_v = st.number_input("cha", value=18)

        title = st.text_input("호칭 (title)", value="")
        lawful = st.number_input(
            "lawful (성향)",
            value=ROBOT_LAWFUL_NEUTRAL,
            help="65536=네츄럴(중립), 32768=카오틱, 98303=라우풀 (서버 Lineage 상수와 동일)",
        )
        clan_id = st.number_input("clanID", value=0)
        clan_name = st.text_input("clan_name", value="")
        mr = st.number_input("mr", value=0)
        sp = st.number_input("sp", value=0)
        weapon_en = st.number_input("무기인챈트", value=0)
        ac = st.number_input("ac", value=0)
        heading = st.number_input("heading", value=0)
        weapon_name = st.text_input("무기 이름 (비우면 NULL 처리)", value="")
        doll_name = st.text_input("마법 인형 (비우면 NULL 처리)", value="")
        apply_after_insert = st.checkbox(
            "저장 직후 이 로봇만 게임에 반영 (`reload_robot_one`)",
            value=True,
            help="서버가 실행 중이어야 월드에 바로 올라옵니다.",
        )

        warn = []
        if name.strip():
            dup_r = db.fetch_one("SELECT 1 AS x FROM `_robot` WHERE LOWER(`name`)=LOWER(%s) LIMIT 1", (name.strip(),))
            if dup_r:
                warn.append("동일 이름 로봇이 이미 있습니다.")
            if "characters" in tables:
                dup_c = db.fetch_one("SELECT 1 AS x FROM `characters` WHERE `name`=%s LIMIT 1", (name.strip(),))
                if dup_c:
                    warn.append("⚠️ 실제 캐릭터 `characters`와 이름이 겹칩니다.")
        for w in warn:
            st.warning(w)

        if st.button("💾 DB에 INSERT", type="primary"):
            if not name or not name.strip():
                st.error("닉네임(name)을 입력하세요.")
            else:
                wn = weapon_name.strip() or None
                dn = doll_name.strip() or None
                params = (
                    int(obj_id),
                    name.strip(),
                    action.strip() or "사냥 & PvP",
                    int(str_v),
                    int(dex_v),
                    int(con_v),
                    int(wis_v),
                    int(int_v),
                    int(cha_v),
                    int(loc_x),
                    int(loc_y),
                    int(loc_map),
                    title.strip(),
                    int(lawful),
                    int(clan_id),
                    clan_name.strip(),
                    klass,
                    sex,
                    int(level),
                    0.0,
                    int(mr),
                    int(sp),
                    int(weapon_en),
                    int(ac),
                    int(heading),
                    wn if wn else "",
                    dn if dn else "",
                    spawn_on,
                )
                ok_ins, err_ins = db.execute_query_ex(INSERT_ROBOT_SQL.strip(), params)
                if ok_ins:
                    st.success("✅ `_robot`에 저장했습니다.")
                    if apply_after_insert:
                        ok_q, err_q = _queue_server_robot_reload_one(db, int(obj_id))
                        if ok_q:
                            st.success("✅ `reload_robot_one` 명령을 큐에 넣었습니다. 잠시 후 월드를 확인하세요.")
                        else:
                            st.warning(f"DB 저장은 됐으나 반영 큐 실패: {err_q}")
                    else:
                        st.info("「게임 서버 반영」 탭에서 해당 로봇만 또는 전체 리로드하세요.")
                else:
                    st.error(f"❌ INSERT 실패: {err_ins}")

with tab_reload:
    st.subheader("게임 서버에 로봇 반영")
    st.markdown("""
    실행 중인 **게임 서버**가 `GmDeliveryController`로 `gm_server_command`를 폴링할 때 처리됩니다.

    - **월드에 봇 올리기 / 갱신** = 아래 각 행의 **「월드에 올리기 (리로드)」** 또는 **objId 1900000** 블록의 반영 버튼 → `reload_robot_one` (DB `_robot` 내용을 서버 메모리에 다시 읽음).
    - **전체 리로드** (`reload_robot`): `_robot` 전체 + 사냥 북·변신·스킬 설정을 다시 읽습니다. (DB에서 삭제된 로봇은 월드에서도 정리됩니다.)
    - **한 마리만** (`reload_robot_one`): 해당 `objId`만 동기화합니다. **서버 JAR에 `reload_robot_one` 처리가 포함되어 있어야** 합니다.
    - **스폰 ON / 스폰 OFF** (각 행): `_robot.스폰_여부`만 바꾼 뒤 곧바로 그 봇 `reload_robot_one`을 큐에 넣습니다. (`OFF`면 월드에서 빠짐)

    서버가 **꺼져 있으면** 큐만 쌓이고, **다음 기동 후 폴링 시** 실행될 수 있습니다 (`executed=1` 처리됨).
    """)

    st.markdown(f"#### objId {TARGET_ROBOT_OBJ_ID} — 행동 선택 후 반영")
    st.caption("DB의 `_robot.행동`을 바꾼 뒤 `reload_robot_one`으로 서버에 넘깁니다. (사냥 / 허수아비 공격 등은 서버 `PcRobotInstance` 문자열과 정확히 일치해야 합니다.)")
    row_1900 = db.fetch_one(
        "SELECT `objId`, `name`, `행동` FROM `_robot` WHERE `objId`=%s",
        (TARGET_ROBOT_OBJ_ID,),
    )
    if row_1900:
        cur = (row_1900.get("행동") or "").strip() or "사냥 & PvP"
        act_idx = ROBOT_ACTION_OPTIONS.index(cur) if cur in ROBOT_ACTION_OPTIONS else 0
        act_1900 = st.selectbox(
            "이 로봇의 행동",
            ROBOT_ACTION_OPTIONS,
            index=act_idx,
            key="reload_tab_action_1900000",
        )
        if st.button("행동 저장 + 월드 반영", type="primary", key="reload_tab_apply_action_1900000"):
            ok_u, err_u = db.execute_query_ex(
                "UPDATE `_robot` SET `행동`=%s WHERE `objId`=%s",
                (act_1900, TARGET_ROBOT_OBJ_ID),
            )
            if not ok_u:
                st.error(f"❌ DB UPDATE 실패: {err_u}")
            else:
                ok_one, err_one = _queue_server_robot_reload_one(db, TARGET_ROBOT_OBJ_ID)
                if ok_one:
                    st.success(
                        f"✅ `_robot.행동` → `{act_1900}` 저장 후 `reload_robot_one` 큐에 넣었습니다."
                    )
                else:
                    st.warning(f"DB는 갱신됐으나 큐 실패: {err_one}")
    else:
        st.info(f"`_robot`에 objId **{TARGET_ROBOT_OBJ_ID}** 행이 없습니다. 「로봇 추가」에서 먼저 등록하세요.")

    st.divider()

    rows_r = db.fetch_all(ROBOT_LIST_SQL)
    if rows_r:
        st.markdown("**등록된 로봇 (행별 — 스폰 ON·OFF / 월드 반영)**")
        for r in rows_r:
            oid = int(r["objId"])
            nm = r.get("name") or ""
            spawn_s = (str(r.get("스폰_여부") or "true")).strip().lower()
            spawn_on = spawn_s != "false"
            c1, c2, c3, c4, c5, c6, c7 = st.columns((1.1, 1.9, 0.85, 0.65, 0.75, 1.35, 1.5))
            with c1:
                st.write(f"**{oid}**")
            with c2:
                st.write(nm)
            with c3:
                st.caption(str(r.get("class") or ""))
            with c4:
                st.caption(f"Lv.{r.get('level')}")
            with c5:
                st.caption("스폰 **ON**" if spawn_on else "스폰 **OFF**")
            with c6:
                s_on, s_off = st.columns(2)
                with s_on:
                    b_on = st.button("스폰 ON", key=f"robot_spawn_on_{oid}")
                with s_off:
                    b_off = st.button("스폰 OFF", key=f"robot_spawn_off_{oid}")
            with c7:
                b_reload = st.button("월드에 올리기 (리로드)", key=f"robot_apply_row_{oid}")
            if b_on:
                ok_u, err_u = _set_robot_spawn_one(db, oid, True)
                if not ok_u:
                    st.error(f"❌ DB 실패: {err_u}")
                else:
                    ok_one, err_one = _queue_server_robot_reload_one(db, oid)
                    if ok_one:
                        st.success(f"✅ objId {oid} 스폰 ON + 리로드 큐 등록.")
                        st.rerun()
                    else:
                        st.warning(f"DB는 갱신됐으나 큐 실패: {err_one}")
            if b_off:
                ok_u, err_u = _set_robot_spawn_one(db, oid, False)
                if not ok_u:
                    st.error(f"❌ DB 실패: {err_u}")
                else:
                    ok_one, err_one = _queue_server_robot_reload_one(db, oid)
                    if ok_one:
                        st.success(f"✅ objId {oid} 스폰 OFF + 리로드 큐 등록.")
                        st.rerun()
                    else:
                        st.warning(f"DB는 갱신됐으나 큐 실패: {err_one}")
            if b_reload:
                ok_one, err_one = _queue_server_robot_reload_one(db, oid)
                if ok_one:
                    st.success(f"✅ objId {oid} 단건 리로드를 큐에 넣었습니다. 잠시 후 월드를 확인하세요.")
                else:
                    st.error(f"❌ 큐 INSERT 실패: {err_one}")
        st.divider()

    if st.button("📡 전체 로봇 리로드 (`reload_robot`) 넣기", type="primary"):
        ok_all, err_all = _queue_server_robot_reload(db)
        if ok_all:
            st.success("✅ 전체 리로드 요청을 큐에 넣었습니다. 잠시 후 게임 월드를 확인하세요.")
        else:
            st.error(f"❌ INSERT 실패: {err_all}")

    st.divider()
    st.markdown("**전체 스폰 on/off** (`_robot` 모든 행의 `스폰_여부` + 리로드)")
    c_on, c_off, c_cf = st.columns([1, 1, 2])
    with c_cf:
        bulk_robot = st.checkbox("로봇 전체 켜기/끄기 실행 확인", value=False, key="robot_bulk_confirm")
    with c_on:
        if st.button("🤖 로봇 전체 사용 (`robot_on`)", disabled=not bulk_robot):
            ok_on, err_on = _queue_gm_command(db, "robot_on", "")
            if ok_on:
                st.success("✅ `robot_on` 큐 등록됨.")
                st.rerun()
            else:
                st.error(err_on)
    with c_off:
        if st.button("⏹️ 로봇 전체 사용 안함 (`robot_off`)", disabled=not bulk_robot):
            ok_off, err_off = _queue_gm_command(db, "robot_off", "")
            if ok_off:
                st.success("✅ `robot_off` 큐 등록됨.")
                st.rerun()
            else:
                st.error(err_off)
