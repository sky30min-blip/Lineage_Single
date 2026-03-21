"""
서버 관리 - 원본 서버 관리 툴의 설정 > 명령어/이벤트 기능.
웹에서 누르면 gm_server_command 테이블에 요청을 넣고, 게임 서버가 폴링해 실행합니다.
"""
import os
import streamlit as st
from utils.db_manager import get_db
from utils.table_schemas import get_create_sql

db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

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
       - `kingdom_war` → CommandController.setKingdomWar()  
       - `all_buff` → CommandController.toBuffAll()  
       - 로봇(`robot_on` / `robot_off` / `reload_robot` / `reload_robot_one`) → 사이드바 **🤖 무인 PC (로봇) 관리** 페이지에서 처리  
       - `event_poly` → EventController.toPoly()  
       - `event_rank_poly` → EventController.toRankPoly()  

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

# ---------- 탭 1: 서버 제어 ----------
tab1, tab2, tab3, tab4, tab5, tab6 = st.tabs([
    "🖥️ 서버 제어", "👥 플레이어 관리", "🎭 이벤트 관리", "🔄 사용 제한 초기화", "📝 SQL 생성", "📊 서버 배율·최고레벨"
])

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
            st.success("저장되었습니다. **서버를 재시작**한 뒤 적용됩니다.")
            st.rerun()
        else:
            st.error(err)
    st.caption("효과: 라우풀 1~3단계 AC·MR 보너스 / 카오틱 1~3단계 추가대미지·SP 보너스")
    st.write("---")

    c_wait = st.checkbox("서버 오픈대기 실행 확인", key="c_sv_wait", help="배율·레벨 제한 적용됨")
    if st.button("🔓 서버 오픈대기", key="sv_wait", disabled=not c_wait):
        ok, err = _send_server_command("server_open_wait")
        if ok: st.success("명령 등록됨. 서버가 처리할 때까지 잠시 기다리세요."); st.rerun()
        else: st.error(err)
    if st.button("✅ 서버 오픈", key="sv_open"):
        ok, err = _send_server_command("server_open")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
    if st.button("🧹 월드맵 청소", key="sv_clear"):
        ok, err = _send_server_command("world_clear")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
    if st.button("💾 캐릭터 저장", key="sv_save"):
        ok, err = _send_server_command("character_save")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
    if st.button("⚔️ 공성전", key="sv_war"):
        ok, err = _send_server_command("kingdom_war")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)

# ---------- 탭 2: 플레이어 관리 ----------
with tab2:
    st.write("**올버프**")
    if st.button("⚡ 올버프", key="buf_all"):
        ok, err = _send_server_command("all_buff")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
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
                    st.success(f"✅ {tbl}.block_date 초기화 완료")
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
                    st.success(f"✅ {tbl}.block_date 초기화 완료")
                    char_done = True
                    break
            if not char_done:
                st.warning("characters/character 테이블에서 block_date 초기화에 실패했거나 테이블이 없습니다.")
            st.success("전체 밴 해제 처리를 마쳤습니다. 서버의 bad_ip 메모리는 서버 재시작 시 비워집니다.")
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
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
    if st.button("🎭 변신 이벤트 끄기", key="ev_poly_off"):
        ok, err = _send_server_command("event_poly", "0")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
    if st.button("🏆 랭킹 변신 이벤트 켜기", key="ev_rank_on"):
        ok, err = _send_server_command("event_rank_poly", "1")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)
    if st.button("🏆 랭킹 변신 이벤트 끄기", key="ev_rank_off"):
        ok, err = _send_server_command("event_rank_poly", "0")
        if ok: st.success("명령 등록됨."); st.rerun()
        else: st.error(err)

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
            st.success("✅ accounts.giran_dungeon_time 초기화 완료.")
        else:
            st.error(f"❌ 실패: {err_g}")

    # 기란감옥 초기화 주문서 사용횟수
    c2 = st.checkbox("기란감옥 초기화 주문서 사용횟수 초기화 확인", key="c_giran_scroll")
    if st.button("🔄 기란감옥 초기화 주문서 사용횟수 초기화", key="btn_giran_scroll", disabled=not c2):
        ok_s, err_s = db.execute_query_ex("UPDATE accounts SET giran_dungeon_count=0")
        if ok_s:
            st.success("✅ accounts.giran_dungeon_count 초기화 완료.")
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
                st.success("✅ 경험치 구슬 사용횟수 초기화 완료.")
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
            st.success("✅ 자동 사냥 이용시간 초기화 완료.")
            if not ok_ch and err_ch:
                st.caption(f"(characters 갱신 생략: {err_ch})")
            if not ok_ac and err_ac:
                st.caption(f"(accounts 갱신 생략: {err_ac})")
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
            cols = list(rows[0].keys())
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
                st.success("lineage.conf에 저장되었습니다. **서버를 재시작**한 뒤 적용됩니다.")
                st.rerun()
            else:
                st.error(err)

with st.expander("❓ 웹 GM 툴에서 서버 명령이 동작하는 방법"):
    st.markdown("""
    1. **gm_server_command 테이블**  
       웹에서 버튼을 누르면 이 테이블에 `(command, param, executed=0)` 행이 INSERT 됩니다.
    2. **게임 서버 폴링**  
       `GmDeliveryController.toTimer()` 가 주기적으로 `gm_server_command` 에서 `executed=0` 인 명령을 읽고,  
       `server_open_wait`, `server_open`, `world_clear`, `character_save`, `kingdom_war`, `all_buff`, `event_poly`, `event_rank_poly` 및 (로봇 페이지에서 넣는) `robot_on`, `robot_off`, `reload_robot`, `reload_robot_one` 등에 맞춰 실행한 뒤 `executed=1` 로 갱신합니다.
    3. **필수 조건**  
       - **게임 서버를 한 번 재시작**해서 수정된 `GmDeliveryController.java` 가 반영되어 있어야 합니다.  
       - GM 툴과 게임 서버가 **같은 DB**를 사용해야 합니다.  
       - **DB 관리** 페이지에서 누락 테이블 생성 시 `gm_server_command` 가 없으면 생성해 두세요.
    """)
