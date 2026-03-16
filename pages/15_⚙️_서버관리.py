"""
서버 관리 - 원본 서버 관리 툴의 설정 > 명령어/이벤트 기능.
DB로 실행 가능한 것만 실행하고, 서버 메모리 조작이 필요한 것은 안내.
"""
import os
import streamlit as st
from utils.db_manager import get_db

db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

st.subheader("⚙️ 서버 관리")
st.caption("원본 서버 관리 툴(설정 > 명령어/이벤트) 기능. DB로 처리 가능한 항목만 실행됩니다.")

# 서버 루트 경로 (lineage.conf 등)
def _server_base():
    base = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    return os.path.join(base, "2.싱글리니지 팩")

def _read_conf_int(key: str, default: int) -> int:
    path = os.path.join(_server_base(), "lineage.conf")
    if not os.path.isfile(path):
        return default
    try:
        with open(path, "r", encoding="utf-8", errors="ignore") as f:
            for line in f:
                line = line.strip()
                if "=" in line and not line.startswith("#"):
                    k, v = line.split("=", 1)
                    if k.strip().lower() == key.lower():
                        return int(v.strip()) if v.strip().isdigit() else default
    except Exception:
        pass
    return default

# ---------- 탭 1: 서버 제어 ----------
tab1, tab2, tab3, tab4, tab5 = st.tabs(["🖥️ 서버 제어", "👥 플레이어 관리", "🎭 이벤트 관리", "🔄 사용 제한 초기화", "📝 SQL 생성"])

with tab1:
    st.write("**서버/월드 조작** (원본 툴은 서버 프로세스 내에서 실행)")
    st.info("서버 오픈대기, 서버 오픈, 월드맵 청소, 캐릭터 저장은 **게임 서버 메모리**를 바꾸므로 원본 서버 관리 툴(같은 프로세스)에서만 실행 가능합니다. GM 툴에서는 DB만 접근할 수 있습니다.")
    for label, desc in [
        ("🔓 서버 오픈대기", "배율 제한·레벨 제한 등. 서버에서 실행 필요."),
        ("✅ 서버 오픈", "오픈대기 해제. 서버에서 실행 필요."),
        ("🧹 월드맵 청소", "바닥 아이템 제거. 서버에서 실행 필요."),
        ("💾 캐릭터 저장", "접속 중인 캐릭터 즉시 저장. 서버에서 실행 필요."),
        ("⚔️ 공성전", "공성전 시작/종료 토글. 서버에서 실행 필요."),
    ]:
        st.button(label, key=f"sv_{label}", disabled=True, help=desc)

# ---------- 탭 2: 플레이어 관리 ----------
with tab2:
    st.write("**올버프**")
    st.button("⚡ 올버프", key="buf_all", disabled=True, help="접속 중인 전체 캐릭터에게 버프. 서버에서만 실행 가능.")
    st.write("**전체 밴 해제** (DB에서 실행 가능)")
    confirm_ban = st.checkbox("전체 밴 해제 실행 확인", key="confirm_ban")
    if st.button("🔓 전체 밴 해제", key="ban_remove", disabled=not confirm_ban, help="계정·캐릭터 block_date 초기화, bad_ip 테이블 비우기"):
        try:
            # bad_ip 비우기
            try:
                db.execute_query("DELETE FROM bad_ip")
            except Exception:
                pass
            # 계정/캐릭터 block 해제 (테이블명 accounts 또는 account 등)
            for tbl in ["accounts", "account"]:
                try:
                    db.execute_query(f"UPDATE `{tbl}` SET `block_date`='0000-00-00 00:00:00'")
                    st.success(f"✅ {tbl}.block_date 초기화 완료")
                    break
                except Exception as e:
                    continue
            for tbl in ["characters", "character"]:
                try:
                    db.execute_query(f"UPDATE `{tbl}` SET `block_date`='0000-00-00 00:00:00'")
                    st.success(f"✅ {tbl}.block_date 초기화 완료")
                    break
                except Exception as e:
                    continue
            st.success("전체 밴 해제 처리 완료. 서버의 bad_ip 메모리는 서버 재시작 시 비워집니다.")
        except Exception as e:
            st.error(str(e))
    st.write("**로봇**")
    st.button("🤖 로봇 전체 사용", key="robot_on", disabled=True, help="서버에서만 실행 가능.")
    st.button("로봇 전체 사용 안함", key="robot_off", disabled=True, help="서버에서만 실행 가능.")

# ---------- 탭 3: 이벤트 관리 ----------
with tab3:
    st.write("변신 이벤트·랭킹 변신 이벤트는 서버 메모리 플래그(Lineage.event_poly 등)를 바꾸므로 **원본 툴**에서 실행하세요.")
    st.button("🎭 변신 이벤트", key="ev_poly", disabled=True, help="서버에서만 토글 가능.")
    st.button("🏆 랭킹 변신 이벤트", key="ev_rank_poly", disabled=True, help="서버에서만 토글 가능.")

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
        try:
            db.execute_query("UPDATE accounts SET giran_dungeon_time=%s", (giran_val,))
            st.success("accounts.giran_dungeon_time 초기화 완료.")
        except Exception as e:
            st.error(str(e))

    # 기란감옥 초기화 주문서 사용횟수
    c2 = st.checkbox("기란감옥 초기화 주문서 사용횟수 초기화 확인", key="c_giran_scroll")
    if st.button("🔄 기란감옥 초기화 주문서 사용횟수 초기화", key="btn_giran_scroll", disabled=not c2):
        try:
            db.execute_query("UPDATE accounts SET giran_dungeon_count=0")
            st.success("accounts.giran_dungeon_count 초기화 완료.")
        except Exception as e:
            st.error(str(e))

    # 경험치 저장 구슬
    c3 = st.checkbox("경험치 저장 구슬 사용횟수 초기화 확인", key="c_exp_marble")
    if st.button("🔄 경험치 저장 구슬 사용횟수 초기화", key="btn_exp_marble", disabled=not c3):
        try:
            for sql in [
                "UPDATE characters SET 경험치저장구슬_사용횟수=0, 경험치구슬_사용횟수=0",
                "UPDATE characters SET 경험치저장구슬_사용횟수=0",
                "UPDATE characters SET 경험치구슬_사용횟수=0",
            ]:
                try:
                    db.execute_query(sql)
                    st.success("경험치 구슬 사용횟수 초기화 완료.")
                    break
                except Exception:
                    continue
            else:
                st.error("characters에 경험치저장구슬_사용횟수/경험치구슬_사용횟수 컬럼이 없습니다.")
        except Exception as e:
            st.error(str(e))

    # 자동 사냥 이용시간
    c4 = st.checkbox("자동 사냥 이용시간 초기화 확인", key="c_auto_hunt")
    if st.button("🔄 자동 사냥 이용시간 초기화", key="btn_auto_hunt", disabled=not c4):
        try:
            ok = False
            try:
                db.execute_query("UPDATE characters SET 자동사냥_남은시간=%s", (auto_val,))
                ok = True
            except Exception:
                pass
            try:
                db.execute_query("UPDATE accounts SET 자동사냥_이용시간=%s", (auto_val,))
                ok = True
            except Exception:
                pass
            if ok:
                st.success("자동 사냥 이용시간 초기화 완료.")
            else:
                st.error("characters/accounts에 자동사냥 관련 컬럼이 없습니다.")
        except Exception as e:
            st.error(str(e))

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

with st.expander("❓ 서버 전용 기능이 비활성화된 이유"):
    st.markdown("""
    - **서버 오픈대기/오픈, 월드맵 청소, 캐릭터 저장, 올버프, 로봇, 변신 이벤트, 공성전** 등은 게임 서버 **프로세스 안의 메모리/객체**를 바꿉니다.
    - GM 툴(Streamlit)은 서버와 **별도 프로세스**라서 이 메모리에 직접 접근할 수 없습니다.
    - 따라서 이런 기능은 **원본 서버 관리 툴**(서버와 같은 프로세스에서 실행되는 Java GUI)에서만 사용할 수 있습니다.
    - **DB만 수정하면 되는 기능**(전체 밴 해제, 기란감옥/경험치 구슬/자동 사냥 초기화)과 **DB에서 읽어 SQL 파일을 만드는 기능**은 이 페이지에서 실행할 수 있습니다.
    """)
