"""
서버 관리 - 원본 서버 관리 툴의 설정 > 명령어/이벤트 기능.
웹에서 누르면 gm_server_command 테이블에 요청을 넣고, 게임 서버가 폴링해 실행합니다.
"""
import os

import pandas as pd
import streamlit as st
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.season_reset import (
    ACCOUNT_PROGRESS_COLUMNS,
    RESET_TABLE_DESCRIPTIONS,
    SEASON_RESET_TABLES,
    auto_backup_before_reset,
    run_season_reset,
)
from utils.table_schemas import get_create_sql

db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()
show_pending_feedback()

# gm_server_command 테이블 없으면 생성
if not db.table_exists("gm_server_command"):
    sql = get_create_sql("gm_server_command")
    if sql:
        _ok_tbl, _err_tbl = db.execute_query_ex(sql)
        if not _ok_tbl:
            st.warning(f"gm_server_command 자동 생성 실패: {_err_tbl}")

st.subheader("⚙️ 서버 관리")
st.caption("버튼을 누르면 DB에 명령이 저장되고, **게임 서버**가 주기적으로 읽어 실행합니다. 서버 재시작 후 적용됩니다.")

with st.expander("⚠️ 서버 명령이 게임에서 동작하지 않을 때 확인할 것", expanded=False):
    st.markdown("""
    아래 조건이 맞아야 **서버 제어·플레이어 관리·이벤트 관리** 버튼이 게임에 반영됩니다.

    1. **게임 서버가 실행 중**이어야 합니다.  
       서버의 `TimeThread` → `GmDeliveryController.toTimer()` 가 주기적으로 **gm_server_command** 테이블을 조회합니다.

    2. **GM 툴과 게임 서버가 같은 DB**를 사용해야 합니다.  
       (config.py의 DB 설정 = 서버 lineage.conf의 DB 설정)

    3. **gm_server_command 테이블**이 있어야 하고, 컬럼은 `id`, `command`, `param`, `executed` 입니다.  
       이 페이지 로드 시 테이블이 없으면 생성 스크립트를 실행합니다.  
       이미 다른 구조로 만든 경우 [DB 관리]에서 `gm_server_command` 생성 SQL을 확인하세요.

    4. **명령 처리**는 서버 코드에 구현되어 있습니다.  
       - `server_open_wait` → CommandController.serverOpenWait()  
       - `server_open` → CommandController.serverOpen()  
       - `world_clear` → CommandController.toWorldItemClear()  
       - `character_save` → 캐릭터 저장  
       - `kingdom_war` → CommandController.setKingdomWar() (전 성 토글)  
       - `kingdom_war_start` / `kingdom_war_stop` → 성별 즉시 시작·종료 (`param`: uid 쉼표 또는 `all`)  
       - `all_buff` → CommandController.toBuffAll()  
       - 로봇(`robot_on` / `robot_off` / `reload_robot` / `reload_robot_one`) → 사이드바 **🤖 무인 PC (로봇) 관리** 페이지에서 처리  
       - `event_poly` → EventController.toPoly()  
       - `event_rank_poly` → EventController.toRankPoly()  
       - 스케줄 이벤트 8종 → DB **`gm_event_settings`** (서버 `GmEventSettings` 가 약 4초마다 로드)  
       - 공성 자동 시작 → DB **`gm_kingdom_war_schedule`** (서버 `GmKingdomWarSchedule` / `KingdomController`)

    버튼을 눌렀는데 반영이 안 되면: 서버 콘솔 로그에 `[gm_server_command]` 또는 오류가 찍히는지 확인하고, 위 1~3을 점검하세요.
    """)

# 서버 루트 경로 (lineage.conf 등)
def _server_base():
    base = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    return os.path.join(base, "2.싱글리니지 팩")

def _conf_path():
    return os.path.join(_server_base(), "lineage.conf")

def _read_conf_int(key: str, default: int) -> int:
    path = _conf_path()
    if not os.path.isfile(path):
        return default
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            for line in f:
                line = line.strip()
                if "=" in line and not line.startswith("#"):
                    k, v = line.split("=", 1)
                    if k.strip().lower() == key.lower():
                        return int(v.strip()) if v.strip().lstrip("-").isdigit() else default
    except Exception:
        pass
    return default

def _read_conf_float(key: str, default: float) -> float:
    path = _conf_path()
    if not os.path.isfile(path):
        return default
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            for line in f:
                line = line.strip()
                if "=" in line and not line.startswith("#"):
                    k, v = line.split("=", 1)
                    if k.strip().lower() == key.lower():
                        try:
                            return float(v.strip())
                        except ValueError:
                            return default
    except Exception:
        pass
    return default

def _read_conf_bool(key: str, default: bool = False) -> bool:
    """lineage.conf에서 boolean 값 읽기 (is_batpomet_system 등)."""
    path = _conf_path()
    if not os.path.isfile(path):
        return default
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            for line in f:
                line = line.strip()
                if "=" in line and not line.startswith("#"):
                    k, v = line.split("=", 1)
                    if k.strip().lower() == key.lower():
                        return v.strip().lower() == "true"
    except Exception:
        pass
    return default


def _read_conf_str(key: str, default: str = "") -> str:
    """lineage.conf에서 문자열 값(시간 목록 등). 파일 위에서 첫 일치 키만 사용."""
    path = _conf_path()
    if not os.path.isfile(path):
        return default
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            for line in f:
                line = line.strip()
                if "=" in line and not line.startswith("#"):
                    k, v = line.split("=", 1)
                    if k.strip().lower() == key.lower():
                        return v.strip()
    except Exception:
        pass
    return default


def _schedule_event_caption(event_key: str) -> str:
    """행 아래 한 줄 요약(현재 lineage.conf에서 읽은 시각 등)."""
    if event_key == "hell":
        a, b = _read_conf_str("hell_dungeon_time"), _read_conf_str("hell_dungeon_time2")
        return f"⏱ 평일: {a or '—'} · 토·일: {b or '—'}"
    if event_key == "treasure":
        t = _read_conf_str("Treasuress_dungeon_time")
        return f"⏱ {t or '—'} · 맵 **807** (NPC 텔레포트)"
    if event_key == "worldboss":
        t = _read_conf_str("world_dungeon_time")
        return f"⏱ {t or '—'} · 보스 맵 **1400** (월드보스 레이드)"
    if event_key == "icedungeon":
        a, b = _read_conf_str("ice_dungeon_time"), _read_conf_str("ice_dungeon_time2")
        return f"⏱ 평일: {a or '—'} · 토·일: {b or '—'} (얼던 NPC)"
    if event_key == "timeevent":
        t = _read_conf_str("time_event_time")
        pt = _read_conf_int("time_event_play_time", 0)
        pt_s = f"{pt}초" if pt else "—"
        return f"⏱ {t or '—'} · 진행 **{pt_s}** (메티스·주사위 이벤트)"
    if event_key == "devil":
        t = _read_conf_str("devil_dungeon_time")
        return f"⏱ {t or '—'} · 입장 맵 **5167** (악마왕 NPC)"
    if event_key == "dimension":
        t = _read_conf_str("dete_dungeon_time")
        return f"⏱ {t or '—'} · 맵 **410** (마족신전)"
    if event_key == "dollrace":
        t = _read_conf_str("bug_time")
        return f"⏱ {t or '—'} · 맵 **508** (자리 쟁탈·최종 보스)"
    return ""


def _schedule_event_help(event_key: str, title: str) -> str:
    """체크박스 help 툴팁 — 서버 로직·conf 키·맵 요약."""
    conf_path = _conf_path()
    conf_note = conf_path if os.path.isfile(conf_path) else "lineage.conf (파일 없음)"
    common = (
        f"[{title}] 이 체크는 DB gm_event_settings.enabled 만 바꿉니다. "
        f"몇 시·어느 맵은 아래 conf 키이며, 수정 후 서버 재시작(또는 conf 반영)이 필요합니다.\n"
        f"설정 파일: {conf_note}\n\n"
        "체크 해제 시: 해당 이벤트 자동 오픈·월드/파란 알림이 나가지 않습니다.\n\n"
    )
    if event_key == "hell":
        return common + (
            "[동작] HellController — 시각에 지옥 입장·월드 알림(열림/닫힘).\n"
            "[시각] 평일 hell_dungeon_time / 토·일 hell_dungeon_time2\n"
            "[진행] hell_play_time (초)\n"
            "[입장] 마을 HellTeleporter NPC (레벨·혈맹·수배는 conf)"
        )
    if event_key == "treasure":
        return common + (
            "[동작] TreasureHuntController — 시각에 보물찾기 오픈·월드 알림.\n"
            "[시각] Treasuress_dungeon_time\n"
            "[진행] Treasuress_play_time (초)\n"
            "[맵] 입장 시 맵 807 텔레포트 (TreasureHuntTeleporter)"
        )
    if event_key == "worldboss":
        return common + (
            "[동작] WorldBossController — world_dungeon_time 시각에 1분·30초 전 안내 후 보스 소환.\n"
            "[맵] 보스 맵 1400, 좌표 약 (32877, 32817)\n"
            "[진행] world_play_time (초) / 입장: 마을 월드보스 NPC\n"
            "[보스명] 서버 기본 월드보스 (이 페이지에서는 on/off만 조정)"
        )
    if event_key == "icedungeon":
        return common + (
            "[동작] IceDungeonController — 얼음 던전 오픈·월드 알림.\n"
            "[시각] 평일 ice_dungeon_time / 토·일 ice_dungeon_time2\n"
            "[진행] ice_play_time (초) / 입장: FrozenTeleporter NPC"
        )
    if event_key == "timeevent":
        return common + (
            "[동작] TimeEventController — 던전 입장 없이 전원 대상 메티스 멘트·이펙트·주사위(랜덤).\n"
            "[시각] time_event_time (쉼표로 여러 시:분)\n"
            "[진행] time_event_play_time (초)\n"
            "연출·초 단위 타이밍은 서버 TimeEventController 코드 기준"
        )
    if event_key == "devil":
        return common + (
            "[동작] DevilController — 악마왕의 영토 길 열림·닫힘 알림.\n"
            "[시각] devil_dungeon_time\n"
            "[진행] devil_play_time (초)\n"
            "[맵] 입장 5167 / 안내: EvilTeleporter NPC"
        )
    if event_key == "dimension":
        return common + (
            "[동작] DimensionController — 마족신전 길 열림·닫힘 알림.\n"
            "[시각] dete_dungeon_time (이 컨트롤러는 이 목록만 사용)\n"
            "[진행] dete_play_time (초)\n"
            "[맵] 410 등은 BossController·스폰과 연동 (conf에 dete_dungeon_time2 있어도 DimensionController는 미사용일 수 있음)"
        )
    if event_key == "dollrace":
        return common + (
            "[동작] DollRaceController2 — 맵 508 자리 쟁탈 라운드·최종 보스(기본 데스나이트).\n"
            "[시각] bug_time → 서버에서 bug_list 로 파싱\n"
            "[주의] conf에 bug_time 이 여러 줄이면 이 툴은 파일 위에서 첫 줄만 읽음\n"
            "[진행] bug_play_time (초) 등은 conf·코드 참고"
        )
    return common + "GmEventSettings 해당 키 on/off."


def _write_conf_key_values(updates: dict) -> tuple:
    """lineage.conf에서 지정한 키의 값을 갱신. (키=값 형식). 반환: (성공 여부, 에러 메시지)."""
    path = _conf_path()
    if not os.path.isfile(path):
        return False, "lineage.conf 파일을 찾을 수 없습니다."
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            lines = f.readlines()
        out = []
        replaced = set()
        for raw in lines:
            line = raw.rstrip("\n\r")
            if "=" in line and not line.strip().startswith("#"):
                parts = line.split("=", 1)
                k = parts[0].strip()
                match = next(((uk, uv) for uk, uv in updates.items() if uk.lower() == k.lower()), None)
                if match:
                    out.append(f"{match[0]}\t= {match[1]}\n")
                    replaced.add(k.lower())
                    continue
            out.append(raw if raw.endswith("\n") else raw + "\n")
        for k, v in updates.items():
            if k.lower() not in replaced:
                out.append(f"{k}\t= {v}\n")
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.writelines(out)
        return True, None
    except Exception as e:
        return False, str(e)

def _send_server_command(command: str, param: str = ""):
    """gm_server_command 테이블에 명령 삽입. 서버가 폴링해 실행."""
    ok, err = db.execute_query_ex(
        "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
        (command, param or ""),
    )
    return (True, None) if ok else (False, err)


# ---------- 공성전 GM 스케줄 (DB: gm_kingdom_war_schedule, 서버 GmKingdomWarSchedule.java) ----------
KINGDOM_WEEKDAY_OPTS = [("일", 0), ("월", 1), ("화", 2), ("수", 3), ("목", 4), ("금", 5), ("토", 6)]


def _fetch_kingdom_list():
    """DB kingdom 테이블 또는 기본 1~7 성 목록.

    uid는 서버 Lineage.java 와 동일: KINGDOM_KENT=1 … KINGDOM_ADEN=7.
    폴백 이름은 팩 db/20260222.sql 의 kingdom INSERT 와 맞춤 (DB 없을 때만 사용).
    """
    try:
        rows = db.fetch_all("SELECT uid, name FROM kingdom ORDER BY uid ASC")
        if rows:
            out = []
            for r in rows:
                uid = int(r.get("uid") or 0)
                if uid <= 0:
                    continue
                nm = str(r.get("name") or "").strip() or f"성 {uid}"
                out.append((uid, nm))
            if out:
                return out
    except Exception:
        pass
    return [
        (1, "켄트성"),
        (2, "오크 요새"),
        (3, "윈다우드"),
        (4, "기란 성"),
        (5, "하이네 성"),
        (6, "지저 성"),
        (7, "아덴성"),
    ]


def _weekday_mask_to_labels(mask: int) -> list:
    m = int(mask) & 0x7F
    return [lab for lab, idx in KINGDOM_WEEKDAY_OPTS if (m >> idx) & 1]


def _labels_to_weekday_mask(labels: list) -> int:
    d = {lab: idx for lab, idx in KINGDOM_WEEKDAY_OPTS}
    out = 0
    for L in labels or []:
        if L in d:
            out |= 1 << d[L]
    return out


def _migrate_kingdom_war_second_slot_columns():
    """구버전 테이블에 2차 공성 시간대 컬럼 추가 (주 2회·서로 다른 시각)."""
    rows = db.fetch_all("SHOW COLUMNS FROM gm_kingdom_war_schedule")
    if not rows:
        return True, None
    names = {str(r.get("Field", "")).lower() for r in rows}
    alts = []
    if "weekdays_2" not in names:
        alts.append(
            "ALTER TABLE gm_kingdom_war_schedule ADD COLUMN weekdays_2 TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '2차 요일 비트'"
        )
    if "start_hour_2" not in names:
        alts.append(
            "ALTER TABLE gm_kingdom_war_schedule ADD COLUMN start_hour_2 TINYINT UNSIGNED NOT NULL DEFAULT 20"
        )
    if "start_min_2" not in names:
        alts.append(
            "ALTER TABLE gm_kingdom_war_schedule ADD COLUMN start_min_2 TINYINT UNSIGNED NOT NULL DEFAULT 0"
        )
    for sql in alts:
        ok, err = db.execute_query_ex(sql)
        if not ok and "1060" not in err and "Duplicate" not in err:
            return False, err
    return True, None


def _ensure_gm_kingdom_schedule_table():
    if not db.table_exists("gm_kingdom_war_schedule"):
        sql = get_create_sql("gm_kingdom_war_schedule")
        if sql:
            ok, err = db.execute_query_ex(sql)
            if not ok:
                return False, err
    else:
        ok_m, err_m = _migrate_kingdom_war_second_slot_columns()
        if not ok_m:
            return False, err_m
    have = db.fetch_all("SELECT kingdom_uid FROM gm_kingdom_war_schedule")
    have_u = {int(r.get("kingdom_uid", 0)) for r in have}
    uids_needed = sorted({u for u, _ in _fetch_kingdom_list()} | set(range(1, 8)))
    for uid in uids_needed:
        if uid not in have_u:
            ok, err = db.execute_query_ex(
                "INSERT INTO gm_kingdom_war_schedule (kingdom_uid, enabled, weekdays, start_hour, start_min, duration_minutes) "
                "VALUES (%s, 0, 0, 20, 0, 0)",
                (uid,),
            )
            if not ok:
                return False, err
    return True, None


def _fetch_kingdom_schedule_map():
    rows = db.fetch_all("SELECT * FROM gm_kingdom_war_schedule")
    return {int(r.get("kingdom_uid", 0)): r for r in rows}


# ---------- 스케줄 이벤트 8종 (DB: gm_event_settings, 서버 GmEventSettings.java) ----------
GM_SCHEDULE_EVENTS = [
    ("hell", "지옥 던전"),
    ("treasure", "보물찾기"),
    ("worldboss", "월드보스 레이드"),
    ("icedungeon", "얼음 던전(얼던)"),
    ("timeevent", "타임 이벤트 (주사위)"),
    ("devil", "악마왕의 영토"),
    ("dimension", "마족신전"),
    ("dollrace", "자리 쟁탈전 (맵508)"),
]


def _ensure_gm_event_settings_table():
    if not db.table_exists("gm_event_settings"):
        sql = get_create_sql("gm_event_settings")
        if sql:
            ok, err = db.execute_query_ex(sql)
            if not ok:
                return False, err
    have = db.fetch_all("SELECT event_key FROM gm_event_settings")
    keys = {str(r.get("event_key", "")).lower() for r in have}
    for key, _title in GM_SCHEDULE_EVENTS:
        if key not in keys:
            ok, err = db.execute_query_ex(
                "INSERT INTO gm_event_settings (event_key, enabled, min_level, play_time_seconds, monster_name, bonus_drop_item, bonus_drop_count) "
                "VALUES (%s, 1, 0, 0, '', '', 1)",
                (key,),
            )
            if not ok:
                return False, err
    return True, None


def _fetch_event_settings_map():
    rows = db.fetch_all("SELECT * FROM gm_event_settings")
    return {str(r.get("event_key", "")).lower(): r for r in rows}


def _event_row_enabled(row: dict) -> int:
    """gm_event_settings.enabled. DB 0=끔은 그대로 써야 함 — int(row.get(...) or 1) 는 0을 1로 바꿔 버림."""
    if not row:
        return 1
    v = row.get("enabled")
    if v is None:
        return 1
    try:
        return int(v)
    except (TypeError, ValueError):
        return 1


# ---------- 탭 1: 서버 제어 ----------
tab1, tab2, tab3, tab4, tab5, tab6, tab7 = st.tabs(
    [
        "🖥️ 서버 제어",
        "👥 플레이어 관리",
        "🎭 이벤트 관리",
        "🔄 사용 제한 초기화",
        "📝 SQL 생성",
        "📊 서버 배율·최고레벨",
        "🧨 시즌 초기화",
    ]
)

with tab1:
    st.write("**서버/월드 조작** — 버튼 누르면 서버가 곧 실행합니다 (폴링 간격 내).")

    # 라우풀/카오틱(바포메트) 시스템 켜기/끄기 — lineage.conf is_batpomet_system
    st.write("---")
    st.write("**라우풀·카오틱(바포메트) 시스템** — 켜면 lawful/chaotic 수치에 따라 방어·MR·추가대미지·SP 보너스가 적용됩니다. lineage.conf 수정 후 **서버 재시작** 시 적용.")
    baphomet_current = _read_conf_bool("is_batpomet_system", False)
    baphomet_on = st.checkbox("라우풀·카오틱 시스템 사용", value=baphomet_current, key="baphomet_system")
    if baphomet_on != baphomet_current:
        st.caption("변경 사항이 있습니다. 아래 저장 후 서버를 재시작하세요.")
    if st.button("💾 바포메트 설정 저장", key="btn_baphomet_save"):
        ok, err = _write_conf_key_values({"is_batpomet_system": "true" if baphomet_on else "false"})
        if ok:
            queue_feedback("success", "✅ 저장되었습니다. **서버를 재시작**한 뒤 적용됩니다.")
            st.rerun()
        else:
            st.error(err)
    st.caption("효과: 라우풀 1~3단계 AC·MR 보너스 / 카오틱 1~3단계 추가대미지·SP 보너스")
    st.write("---")

    c_wait = st.checkbox("서버 오픈대기 실행 확인", key="c_sv_wait", help="배율·레벨 제한 적용됨")
    if st.button("🔓 서버 오픈대기", key="sv_wait", disabled=not c_wait):
        ok, err = _send_server_command("server_open_wait")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 서버가 처리할 때까지 잠시 기다리세요.")
            st.rerun()
        else:
            st.error(err)
    if st.button("✅ 서버 오픈", key="sv_open"):
        ok, err = _send_server_command("server_open")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)
    if st.button("🧹 월드맵 청소", key="sv_clear"):
        ok, err = _send_server_command("world_clear")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)
    if st.button("💾 캐릭터 저장", key="sv_save"):
        ok, err = _send_server_command("character_save")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)

    st.write("---")
    with st.expander("⚔️ 공성전 (성별 스케줄·즉시 시작/종료)", expanded=False):
        st.caption(
            "**자동 공성:** `gm_kingdom_war_schedule` 를 서버가 약 **4초마다** 읽고, `KingdomController` 에서 **한국시간** 기준 해당 **시·분의 0초**에 시작합니다. "
            "`lineage.conf` 의 **`is_kingdom_war=true`** 가 있어야 공성이 열립니다. "
            "**진행 시간(분)** 은 서버 `Kingdom.toStartWar` 에서 `1000 * 60 * 분` 으로 종료 시각을 잡습니다. "
            "**0** 이면 전역 **`kingdom_war_time`** 을 씁니다. "
            "**주 2회·시각이 다르면** 아래 **2차 공성**에 두 번째 요일·시·분을 넣으세요. 같은 시각이면 **1차만** 쓰고 요일을 여러 개 고르면 됩니다."
        )
        is_kw = _read_conf_bool("is_kingdom_war", False)
        kw_time = _read_conf_int("kingdom_war_time", 60)
        kingdom_uid_conf = _read_conf_int("kingdom", 4)
        st.markdown(
            f"현재 **lineage.conf**: `is_kingdom_war` = **{'켜짐' if is_kw else '꺼짐'}** · "
            f"`kingdom_war_time` = **{kw_time}**분 · `kingdom`(기란용 conf 스케줄 대상 uid) = **{kingdom_uid_conf}**"
        )
        g1, g2 = st.columns(2)
        with g1:
            kw_en_conf = st.checkbox("is_kingdom_war (공성 시스템 사용)", value=is_kw, key="kw_global_is_en")
        with g2:
            kw_time_inp = st.number_input(
                "kingdom_war_time (전역 기본 분)",
                min_value=1,
                max_value=600,
                value=max(1, kw_time),
                key="kw_global_war_min",
            )
        if st.button("💾 공성 전역 conf 저장", key="kw_save_lineage_conf"):
            ok_c, err_c = _write_conf_key_values(
                {
                    "is_kingdom_war": "true" if kw_en_conf else "false",
                    "kingdom_war_time": int(kw_time_inp),
                }
            )
            if ok_c:
                queue_feedback("success", "✅ lineage.conf 에 저장했습니다. **서버 재시작** 후 반영되는 항목입니다.")
                st.rerun()
            else:
                st.error(err_c)
        st.caption("요일 비트는 서버와 동일하게 **Java `Date.getDay()`** (0=일 … 6=토) 기준입니다.")

        ok_tbl, err_tbl = _ensure_gm_kingdom_schedule_table()
        if not ok_tbl:
            st.error(f"gm_kingdom_war_schedule: {err_tbl}")
        else:
            sched_map = _fetch_kingdom_schedule_map()
            for uid, kname in _fetch_kingdom_list():
                row = sched_map.get(uid) or {}
                en_d = int(row.get("enabled", 0) or 0) != 0
                wdm = int(row.get("weekdays", 0) or 0)
                sh = int(row.get("start_hour", 20) or 20)
                sm = int(row.get("start_min", 0) or 0)
                dm = int(row.get("duration_minutes", 0) or 0)
                wdm2 = int(row.get("weekdays_2", 0) or 0)
                sh2 = int(row.get("start_hour_2", 20) or 20)
                sm2 = int(row.get("start_min_2", 0) or 0)
                with st.form(f"kw_form_{uid}"):
                    st.markdown(f"##### {kname} (`uid={uid}`)")
                    fen = st.checkbox("이 성 **자동 공성** 사용", value=en_d, key=f"kw_chk_{uid}")
                    st.markdown("**1차 공성**")
                    dsel = st.multiselect(
                        "요일 (한국시간)",
                        options=[x[0] for x in KINGDOM_WEEKDAY_OPTS],
                        default=_weekday_mask_to_labels(wdm),
                        key=f"kw_days_{uid}",
                    )
                    c1, c2, c3 = st.columns(3)
                    with c1:
                        hh = st.number_input("시", min_value=0, max_value=23, value=min(23, max(0, sh)), key=f"kwh_{uid}")
                    with c2:
                        mm = st.number_input("분", min_value=0, max_value=59, value=min(59, max(0, sm)), key=f"kwm_{uid}")
                    with c3:
                        dur = st.number_input("진행(분) 0=전역", min_value=0, max_value=600, value=max(0, dm), key=f"kwd_{uid}")
                    st.markdown("**2차 공성** (다른 요일·시각이 필요할 때만)")
                    dsel2 = st.multiselect(
                        "2차 요일",
                        options=[x[0] for x in KINGDOM_WEEKDAY_OPTS],
                        default=_weekday_mask_to_labels(wdm2),
                        key=f"kw_days2_{uid}",
                    )
                    c4, c5 = st.columns(2)
                    with c4:
                        hh2 = st.number_input("2차 시", min_value=0, max_value=23, value=min(23, max(0, sh2)), key=f"kwh2_{uid}")
                    with c5:
                        mm2 = st.number_input("2차 분", min_value=0, max_value=59, value=min(59, max(0, sm2)), key=f"kwm2_{uid}")
                    st.caption(
                        "conf의 `kingdom` 과 같은 uid 에서 DB 스케줄을 켜면, `giran_kingdom_war_day_list` 기반 **기존 자동 공성**은 끕니다."
                    )
                    if st.form_submit_button(f"💾 {kname} DB 저장"):
                        mask = _labels_to_weekday_mask(dsel)
                        mask2 = _labels_to_weekday_mask(dsel2)
                        sql = (
                            "INSERT INTO gm_kingdom_war_schedule (kingdom_uid, enabled, weekdays, start_hour, start_min, duration_minutes, "
                            "weekdays_2, start_hour_2, start_min_2) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s) ON DUPLICATE KEY UPDATE "
                            "enabled=VALUES(enabled), weekdays=VALUES(weekdays), start_hour=VALUES(start_hour), start_min=VALUES(start_min), "
                            "duration_minutes=VALUES(duration_minutes), weekdays_2=VALUES(weekdays_2), start_hour_2=VALUES(start_hour_2), "
                            "start_min_2=VALUES(start_min_2)"
                        )
                        ok_s, err_s = db.execute_query_ex(
                            sql,
                            (
                                uid,
                                1 if fen else 0,
                                mask,
                                int(hh),
                                int(mm),
                                int(dur),
                                mask2,
                                int(hh2),
                                int(mm2),
                            ),
                        )
                        if ok_s:
                            queue_feedback("success", f"✅ 「{kname}」공성 스케줄을 저장했습니다. 서버가 수 초 내 반영합니다.")
                            st.rerun()
                        else:
                            st.error(err_s)

        st.divider()
        st.markdown("**즉시 시작·종료** (`kingdom_war_start` / `kingdom_war_stop`)")
        km = _fetch_kingdom_list()
        lbl_map = {f"{nm} (uid={u})": u for u, nm in km}
        pick = st.multiselect("즉시 시작할 성 (복수 선택)", options=list(lbl_map.keys()), key="kw_pick_start")
        if st.button("▶ 선택 성 즉시 공성 시작", key="kw_btn_start"):
            uids = [lbl_map[x] for x in pick]
            if not uids:
                st.warning("성을 한 개 이상 선택하세요.")
            else:
                ok, err = _send_server_command("kingdom_war_start", ",".join(str(u) for u in uids))
                if ok:
                    queue_feedback("success", f"✅ 공성 시작 명령 등록: {uids}")
                    st.rerun()
                else:
                    st.error(err)
        pick_stop = st.multiselect("즉시 종료할 성", options=list(lbl_map.keys()), key="kw_pick_stop")
        c_stop1, c_stop2 = st.columns(2)
        with c_stop1:
            if st.button("⏹ 선택 성 공성 종료", key="kw_btn_stop_sel"):
                uids = [lbl_map[x] for x in pick_stop]
                if not uids:
                    st.warning("종료할 성을 선택하세요.")
                else:
                    ok, err = _send_server_command("kingdom_war_stop", ",".join(str(u) for u in uids))
                    if ok:
                        queue_feedback("success", f"✅ 공성 종료 명령 등록: {uids}")
                        st.rerun()
                    else:
                        st.error(err)
        with c_stop2:
            if st.button("⏹ 전체 성 공성 종료", key="kw_btn_stop_all"):
                ok, err = _send_server_command("kingdom_war_stop", "all")
                if ok:
                    queue_feedback("success", "✅ 전체 공성 종료 명령을 등록했습니다.")
                    st.rerun()
                else:
                    st.error(err)
        st.caption("레거시: 모든 성을 **토글** (진행 중이면 종료, 아니면 시작) — `kingdom_war`")
        if st.button("⚔️ 레거시 공성전 토글 (전 성)", key="sv_war"):
            ok, err = _send_server_command("kingdom_war")
            if ok:
                queue_feedback("success", "✅ kingdom_war 명령이 등록되었습니다.")
                st.rerun()
            else:
                st.error(err)

# ---------- 탭 2: 플레이어 관리 ----------
with tab2:
    st.write("**올버프**")
    if st.button("⚡ 올버프", key="buf_all"):
        ok, err = _send_server_command("all_buff")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)
    st.write("**전체 밴 해제** (DB에서 즉시 실행)")
    confirm_ban = st.checkbox("전체 밴 해제 실행 확인", key="confirm_ban")
    if st.button("🔓 전체 밴 해제", key="ban_remove", disabled=not confirm_ban, help="계정·캐릭터 block_date 초기화, bad_ip 테이블 비우기"):
        try:
            db.execute_query_ex("DELETE FROM bad_ip")
            acc_done = False
            for tbl in ["accounts", "account"]:
                ok_u, err_u = db.execute_query_ex(
                    f"UPDATE `{tbl}` SET `block_date`='0000-00-00 00:00:00'"
                )
                if ok_u:
                    acc_done = True
                    break
            if not acc_done:
                st.warning("accounts/account 테이블에서 block_date 초기화에 실패했거나 테이블이 없습니다.")
            char_done = False
            for tbl in ["characters", "character"]:
                ok_c, err_c = db.execute_query_ex(
                    f"UPDATE `{tbl}` SET `block_date`='0000-00-00 00:00:00'"
                )
                if ok_c:
                    char_done = True
                    break
            if not char_done:
                st.warning("characters/character 테이블에서 block_date 초기화에 실패했거나 테이블이 없습니다.")
            queue_feedback(
                "success",
                "✅ 전체 밴 해제 처리를 마쳤습니다. 서버의 bad_ip 메모리는 서버 재시작 시 비워집니다.",
            )
            st.rerun()
        except Exception as e:
            st.error(str(e))
    st.info(
        "무인 PC(`_robot`) **추가·삭제·게임 반영·전체 스폰 on/off** 는 사이드바 **🤖 무인 PC (로봇) 관리** 페이지에서만 하세요."
    )

# ---------- 탭 3: 이벤트 관리 ----------
with tab3:
    st.write("**변신 이벤트** · **랭킹 변신 이벤트** — 켜기/끄기를 서버에 요청합니다.")
    if st.button("🎭 변신 이벤트 켜기", key="ev_poly_on"):
        ok, err = _send_server_command("event_poly", "1")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)
    if st.button("🎭 변신 이벤트 끄기", key="ev_poly_off"):
        ok, err = _send_server_command("event_poly", "0")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)
    if st.button("🏆 랭킹 변신 이벤트 켜기", key="ev_rank_on"):
        ok, err = _send_server_command("event_rank_poly", "1")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)
    if st.button("🏆 랭킹 변신 이벤트 끄기", key="ev_rank_off"):
        ok, err = _send_server_command("event_rank_poly", "0")
        if ok:
            queue_feedback("success", "✅ 명령이 등록되었습니다. 게임 서버가 곧 실행합니다.")
            st.rerun()
        else:
            st.error(err)

    st.divider()
    st.subheader("⏱️ 스케줄 던전·이벤트 8종")
    st.caption(
        "아래 **스케줄 실행**을 끄거나 켜면 **즉시 DB(`gm_event_settings.enabled`)에 저장**됩니다. "
        "게임 서버의 `GmEventSettings`가 약 **4초마다** DB를 읽어 적용합니다. "
        "**몇 시에 열리는지** 등은 던전·이벤트마다 `lineage.conf` 및 서버 코드를 따르며, 여기서는 **돌릴지 말지만** 제어합니다."
    )
    ok_ev, err_ev = _ensure_gm_event_settings_table()
    if not ok_ev:
        st.error(f"gm_event_settings 준비 실패: {err_ev}")
    else:
        ev_map = _fetch_event_settings_map()
        _gmev_sql = (
            "INSERT INTO gm_event_settings (event_key, enabled, min_level, play_time_seconds, monster_name, bonus_drop_item, bonus_drop_count) "
            "VALUES (%s, %s, 0, 0, '', '', 1) "
            "ON DUPLICATE KEY UPDATE enabled=VALUES(enabled)"
        )
        _gmev_need_rerun = False
        for idx, (key, title) in enumerate(GM_SCHEDULE_EVENTS):
            row = ev_map.get(key) or {}
            en = _event_row_enabled(row)
            sk = f"gmev_run_{key}"
            sync_key = f"gmev_sync_{key}"
            revert_pending = f"gmev_revert_{key}"

            # 저장 실패 시 다음 실행에서 체크 상태를 마지막으로 맞춘 값으로 되돌림 (위젯 생성 전에만 session 수정)
            if st.session_state.pop(revert_pending, False):
                st.session_state[sk] = st.session_state[sync_key]

            if sk not in st.session_state:
                st.session_state[sk] = bool(en != 0)
            if sync_key not in st.session_state:
                st.session_state[sync_key] = st.session_state[sk]

            col_a, col_b = st.columns([3, 1])
            with col_a:
                st.markdown(f"**{title}**  \n`{key}`")
                _cap = _schedule_event_caption(key)
                if _cap:
                    st.caption(_cap)
            with col_b:
                st.checkbox(
                    "스케줄 실행",
                    key=sk,
                    help=_schedule_event_help(key, title),
                )

            if st.session_state[sk] != st.session_state[sync_key]:
                run_on = st.session_state[sk]
                enabled = 1 if run_on else 0
                ok_s, err_s = db.execute_query_ex(_gmev_sql, (key, enabled))
                if ok_s:
                    st.session_state[sync_key] = run_on
                    queue_feedback(
                        "success",
                        f"✅ 「{title}」스케줄 실행: **{'켜짐' if run_on else '꺼짐'}**. DB에 저장되었고, 게임 서버가 수 초 내 반영합니다.",
                    )
                    _gmev_need_rerun = True
                else:
                    st.session_state[revert_pending] = True
                    queue_feedback("error", f"「{title}」저장 실패: {err_s}")
                    _gmev_need_rerun = True

            if idx < len(GM_SCHEDULE_EVENTS) - 1:
                st.divider()
        if _gmev_need_rerun:
            st.rerun()

# ---------- 탭 4: 사용 제한 초기화 ----------
with tab4:
    st.write("DB 업데이트로 초기화합니다. 서버에 접속 중인 캐릭터는 서버 재시작 또는 재접속 후 반영됩니다.")
    giran_min = _read_conf_int("giran_dungeon_time", 60)
    auto_min = _read_conf_int("auto_hunt_time", 1440)
    col_a, col_b = st.columns(2)
    with col_a:
        giran_val = st.number_input("기란감옥 초기화 시 부여 시간(분)", min_value=1, value=giran_min, key="giran_val")
    with col_b:
        auto_val = st.number_input("자동 사냥 초기화 시 부여 시간(분)", min_value=1, value=auto_min, key="auto_val")

    # 기란감옥 이용시간
    c1 = st.checkbox("기란감옥 이용시간 초기화 실행 확인", key="c_giran_time")
    if st.button("🔄 기란감옥 이용시간 초기화", key="btn_giran_time", disabled=not c1):
        ok_g, err_g = db.execute_query_ex("UPDATE accounts SET giran_dungeon_time=%s", (giran_val,))
        if ok_g:
            queue_feedback("success", "✅ accounts.giran_dungeon_time 초기화 완료.")
            st.rerun()
        else:
            st.error(f"❌ 실패: {err_g}")

    # 기란감옥 초기화 주문서 사용횟수
    c2 = st.checkbox("기란감옥 초기화 주문서 사용횟수 초기화 확인", key="c_giran_scroll")
    if st.button("🔄 기란감옥 초기화 주문서 사용횟수 초기화", key="btn_giran_scroll", disabled=not c2):
        ok_s, err_s = db.execute_query_ex("UPDATE accounts SET giran_dungeon_count=0")
        if ok_s:
            queue_feedback("success", "✅ accounts.giran_dungeon_count 초기화 완료.")
            st.rerun()
        else:
            st.error(f"❌ 실패: {err_s}")

    # 경험치 저장 구슬
    c3 = st.checkbox("경험치 저장 구슬 사용횟수 초기화 확인", key="c_exp_marble")
    if st.button("🔄 경험치 저장 구슬 사용횟수 초기화", key="btn_exp_marble", disabled=not c3):
        last_err = ""
        for sql in [
            "UPDATE characters SET 경험치저장구슬_사용횟수=0, 경험치구슬_사용횟수=0",
            "UPDATE characters SET 경험치저장구슬_사용횟수=0",
            "UPDATE characters SET 경험치구슬_사용횟수=0",
        ]:
            ok_e, err_e = db.execute_query_ex(sql)
            if ok_e:
                queue_feedback("success", "✅ 경험치 구슬 사용횟수 초기화 완료.")
                st.rerun()
                break
            last_err = err_e
        else:
            st.error(f"❌ characters에 해당 컬럼이 없거나 UPDATE 실패: {last_err}")

    # 자동 사냥 이용시간
    c4 = st.checkbox("자동 사냥 이용시간 초기화 확인", key="c_auto_hunt")
    if st.button("🔄 자동 사냥 이용시간 초기화", key="btn_auto_hunt", disabled=not c4):
        ok_ch, err_ch = db.execute_query_ex("UPDATE characters SET 자동사냥_남은시간=%s", (auto_val,))
        ok_ac, err_ac = db.execute_query_ex("UPDATE accounts SET 자동사냥_이용시간=%s", (auto_val,))
        if ok_ch or ok_ac:
            msg = "✅ 자동 사냥 이용시간 초기화 완료."
            if not ok_ch and err_ch:
                msg += f" (characters 생략: {err_ch})"
            if not ok_ac and err_ac:
                msg += f" (accounts 생략: {err_ac})"
            queue_feedback("success", msg)
            st.rerun()
        else:
            st.error(f"❌ characters/accounts 모두 실패 — characters: {err_ch} | accounts: {err_ac}")

# ---------- 탭 5: SQL 생성 ----------
with tab5:
    st.write("DB 테이블을 읽어 SQL 파일 내용을 생성한 뒤 다운로드합니다.")
    sql_placeholder = st.empty()

    def gen_monster_spawnlist():
        try:
            rows = db.fetch_all("SELECT * FROM monster_spawnlist")
            if not rows:
                return None, "monster_spawnlist 테이블이 비어 있거나 없습니다."
            lines = [
                "SET FOREIGN_KEY_CHECKS=0;",
                "-- Table structure for `monster_spawnlist`",
                "DROP TABLE IF EXISTS `monster_spawnlist`;",
                "CREATE TABLE `monster_spawnlist` (",
                "  `uid` int(10) NOT NULL,",
                "  `name` varchar(50) NOT NULL DEFAULT '',",
                "  `monster` varchar(50) NOT NULL DEFAULT '',",
                "  `random` enum('true','false') NOT NULL DEFAULT 'true',",
                "  `count` int(10) unsigned NOT NULL,",
                "  `loc_size` int(10) unsigned NOT NULL,",
                "  `spawn_x` int(10) unsigned NOT NULL DEFAULT '0',",
                "  `spawn_y` int(10) unsigned NOT NULL DEFAULT '0',",
                "  `spawn_map` varchar(255) NOT NULL DEFAULT '',",
                "  `re_spawn_min` int(10) unsigned NOT NULL DEFAULT '60',",
                "  `re_spawn_max` int(10) unsigned NOT NULL DEFAULT '60',",
                "  PRIMARY KEY (`uid`)",
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8;",
                "",
                "-- Records of monster_spawnlist",
            ]
            uid = 1
            for r in rows:
                name = str(r.get("name", "")).replace("'", "\\'")
                monster = str(r.get("monster", "")).replace("'", "\\'")
                random_val = str(r.get("random", "true"))
                count = int(r.get("count", 0))
                loc_size = int(r.get("loc_size", 0))
                spawn_x = int(r.get("spawn_x", 0))
                spawn_y = int(r.get("spawn_y", 0))
                spawn_map = str(r.get("spawn_map", "")).replace("'", "\\'")
                re_min = int(r.get("re_spawn_min", 60))
                re_max = int(r.get("re_spawn_max", 60))
                lines.append(f"INSERT INTO `monster_spawnlist` VALUES ({uid}, '{name}', '{monster}', '{random_val}', {count}, {loc_size}, {spawn_x}, {spawn_y}, '{spawn_map}', {re_min}, {re_max});")
                uid += 1
            return "\n".join(lines), None
        except Exception as e:
            return None, str(e)

    def gen_monster_drop():
        try:
            rows = db.fetch_all("SELECT * FROM monster_drop")
            if not rows:
                return None, "monster_drop 테이블이 비어 있거나 없습니다."
            lines = [
                "SET FOREIGN_KEY_CHECKS=0;",
                "-- Table structure for `monster_drop`",
                "DROP TABLE IF EXISTS `monster_drop`;",
                "CREATE TABLE `monster_drop` (",
                "  `name` varchar(50) NOT NULL DEFAULT '',",
                "  `monster_name` varchar(50) NOT NULL DEFAULT '',",
                "  `item_name` varchar(50) NOT NULL DEFAULT '',",
                "  `item_bress` int(10) unsigned NOT NULL DEFAULT '1',",
                "  `item_en` tinyint(10) NOT NULL DEFAULT '0',",
                "  `count_min` int(10) unsigned NOT NULL DEFAULT '1',",
                "  `count_max` int(10) unsigned NOT NULL DEFAULT '1',",
                "  `chance` varchar(5) NOT NULL DEFAULT '0',",
                "  PRIMARY KEY (`monster_name`,`item_name`,`item_bress`,`item_en`)",
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8;",
                "",
                "-- Records of monster_drop",
            ]
            for r in rows:
                name = str(r.get("name", "")).replace("'", "\\'")
                mon = str(r.get("monster_name", "")).replace("'", "\\'")
                item = str(r.get("item_name", "")).replace("'", "\\'")
                bress = int(r.get("item_bress", 1))
                en = int(r.get("item_en", 0))
                cmin = int(r.get("count_min", 1))
                cmax = int(r.get("count_max", 1))
                chance = str(r.get("chance", "0")).replace("'", "\\'")
                lines.append(f"INSERT INTO `monster_drop` VALUES ('{name}', '{mon}', '{item}', {bress}, {en}, {cmin}, {cmax}, '{chance}');")
            return "\n".join(lines), None
        except Exception as e:
            return None, str(e)

    with sql_placeholder.container():
        if st.button("📝 monster_spawnlist.sql 생성", key="gen_spawnlist"):
            content, err = gen_monster_spawnlist()
            if err:
                st.error(err)
            else:
                st.download_button("다운로드 monster_spawnlist.sql", content.encode("utf-8"), file_name="monster_spawnlist.sql", mime="text/plain", key="dl_spawnlist")
                st.success("생성 완료. 위에서 다운로드하세요.")
        if st.button("📝 monster_drop.sql 생성", key="gen_drop"):
            content, err = gen_monster_drop()
            if err:
                st.error(err)
            else:
                st.download_button("다운로드 monster_drop.sql", content.encode("utf-8"), file_name="monster_drop.sql", mime="text/plain", key="dl_drop")
                st.success("생성 완료. 위에서 다운로드하세요.")
        st.caption("spr_action.sql(spr_frame)은 서버의 sql/list.spr 파일을 읽어 생성하므로, 원본 툴에서 실행하세요.")

# ---------- 탭 6: 서버 배율·최고레벨 ----------
with tab6:
    st.write("**lineage.conf** 에서 배율·최고레벨을 읽어 표시하고, 저장 시 해당 파일을 수정합니다. **서버 재시작 후** 적용됩니다.")
    conf_path = _conf_path()
    if not os.path.isfile(conf_path):
        st.warning(f"lineage.conf를 찾을 수 없습니다: `{conf_path}`")
    else:
        st.caption(f"설정 파일: `{conf_path}`")
        level_max = _read_conf_int("level_max", 85)
        pet_level_max = _read_conf_int("pet_level_max", 85)
        rate_exp = _read_conf_float("rate_exp", 1.0)
        rate_drop = _read_conf_float("rate_drop", 1.0)
        rate_aden = _read_conf_float("rate_aden", 1.0)
        rate_enchant = _read_conf_float("rate_enchant", 1.0)
        rate_party = _read_conf_float("rate_party", 1.0)
        rate_exp_pet = _read_conf_float("rate_exp_pet", 1.0)

        col1, col2 = st.columns(2)
        with col1:
            st.subheader("최고 레벨")
            new_level_max = st.number_input("캐릭터 최고 레벨 (level_max)", min_value=1, max_value=99, value=level_max, key="cfg_level_max")
            new_pet_level_max = st.number_input("펫 최고 레벨 (pet_level_max)", min_value=1, max_value=99, value=pet_level_max, key="cfg_pet_level_max")
        with col2:
            st.subheader("배율")
            new_rate_exp = st.number_input("경험치 배율 (rate_exp)", min_value=0.0, max_value=1000.0, value=float(rate_exp), step=0.5, format="%.1f", key="cfg_rate_exp")
            new_rate_drop = st.number_input("드랍 배율 (rate_drop)", min_value=0.0, max_value=1000.0, value=float(rate_drop), step=0.5, format="%.1f", key="cfg_rate_drop")
            new_rate_aden = st.number_input("아데나 배율 (rate_aden)", min_value=0.0, max_value=1000.0, value=float(rate_aden), step=0.5, format="%.1f", key="cfg_rate_aden")
            new_rate_enchant = st.number_input("인챈트 배율 (rate_enchant)", min_value=0.0, max_value=1000.0, value=float(rate_enchant), step=0.5, format="%.1f", key="cfg_rate_enchant")
            new_rate_party = st.number_input("파티 경험치 배율 (rate_party)", min_value=0.0, max_value=1000.0, value=float(rate_party), step=0.5, format="%.1f", key="cfg_rate_party")
            new_rate_exp_pet = st.number_input("펫 경험치 배율 (rate_exp_pet)", min_value=0.0, max_value=1000.0, value=float(rate_exp_pet), step=0.5, format="%.1f", key="cfg_rate_exp_pet")

        c_save = st.checkbox("lineage.conf 저장 실행 확인", key="c_cfg_save", help="설정 파일을 덮어씁니다. 서버 재시작 후 적용됩니다.")
        if st.button("💾 배율·최고레벨 저장", key="btn_cfg_save", disabled=not c_save):
            ok, err = _write_conf_key_values({
                "level_max": new_level_max,
                "pet_level_max": new_pet_level_max,
                "rate_exp": new_rate_exp,
                "rate_drop": new_rate_drop,
                "rate_aden": new_rate_aden,
                "rate_enchant": new_rate_enchant,
                "rate_party": new_rate_party,
                "rate_exp_pet": new_rate_exp_pet,
            })
            if ok:
                queue_feedback("success", "✅ lineage.conf에 저장되었습니다. **서버를 재시작**한 뒤 적용됩니다.")
                st.rerun()
            else:
                st.error(err)

# ---------- 탭 7: 시즌 원클릭 초기화 (계정 유지) ----------
with tab7:
    st.subheader("🧨 시즌 원클릭 초기화 (계정 유지)")
    st.error(
        "유저 **플레이 데이터**를 삭제합니다. `accounts` 로그인 계정은 유지하고, "
        "캐릭터·인벤·창고·거래·파워볼·복구 큐 등을 초기화합니다. 실행 전 **반드시 백업**을 확인하세요."
    )
    st.caption("자동으로 `mysqldump` 전체 백업을 시도한 뒤 TRUNCATE 를 진행합니다.")

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

        st.markdown("**⚠️ 문서 기준이나 현재 DB에 없음(건너뜀)**")
        if missing_rows:
            st.dataframe(pd.DataFrame(missing_rows), hide_index=True, use_container_width=True)
        else:
            st.caption("문서 기준 대상 테이블이 모두 DB에 존재합니다.")

        st.caption(
            "추가 동작: accounts 진행성 컬럼만 0으로 리셋 "
            f"({', '.join(ACCOUNT_PROGRESS_COLUMNS)})"
        )

    confirm_check = st.checkbox("위 내용을 확인했고, 시즌 초기화를 진행합니다.", key="season_confirm_check")
    confirm_text = st.text_input("확인 문구 입력", placeholder="시즌초기화", key="season_confirm_text")

    btn_col, msg_col = st.columns([1, 2])
    with btn_col:
        run_reset = st.button("🔥 원클릭 시즌 초기화 실행", type="primary", key="season_run_btn")
    with msg_col:
        st.caption("실행 시 mysqldump 백업 후 테이블을 비웁니다.")

    if run_reset:
        if not confirm_check:
            st.error("체크박스를 먼저 선택해주세요.")
        elif confirm_text.strip() != "시즌초기화":
            st.error("확인 문구가 일치하지 않습니다. `시즌초기화` 를 정확히 입력하세요.")
        else:
            b_ok, b_msg, b_path = auto_backup_before_reset(db)
            if not b_ok:
                st.error("❌ " + b_msg)
                st.stop()
            st.success(f"✅ {b_msg}: `{b_path}`")

            ok, msg = run_season_reset(db)
            if ok:
                st.success("✅ " + msg)
                st.info("다음 단계: 서버 재시작 후 필요 시 **서버 리로드**에서 `전체스폰 리로드`를 실행하세요.")
            else:
                st.error("❌ 시즌 초기화 실패: " + msg)

with st.expander("❓ 웹 GM 툴에서 서버 명령이 동작하는 방법"):
    st.markdown("""
    1. **gm_server_command 테이블**  
       웹에서 버튼을 누르면 이 테이블에 `(command, param, executed=0)` 행이 INSERT 됩니다.
    2. **게임 서버 폴링**  
       `GmDeliveryController.toTimer()` 가 주기적으로 `gm_server_command` 에서 `executed=0` 인 명령을 읽고,  
       `server_open_wait`, `server_open`, `world_clear`, `character_save`, `kingdom_war`, `kingdom_war_start`, `kingdom_war_stop`, `all_buff`, `event_poly`, `event_rank_poly` 및 (로봇 페이지에서 넣는) `robot_on`, `robot_off`, `reload_robot`, `reload_robot_one` 등에 맞춰 실행한 뒤 `executed=1` 로 갱신합니다.
    3. **필수 조건**  
       - **게임 서버를 한 번 재시작**해서 수정된 `GmDeliveryController.java` 가 반영되어 있어야 합니다.  
       - GM 툴과 게임 서버가 **같은 DB**를 사용해야 합니다.  
       - **DB 관리** 페이지에서 누락 테이블 생성 시 `gm_server_command` 가 없으면 생성해 두세요.
    """)
