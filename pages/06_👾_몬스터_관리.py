"""
몬스터 관리 - monster 테이블 조회/추가/수정 (나비켓과 동일한 상세 설정)
서버 MonsterDatabase가 읽는 컬럼 기준.
"""
import hashlib

import pandas as pd
import streamlit as st
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.field_help_ko import MONSTER_HELP as MH
from utils.gm_db_options import (
    CUSTOM_NUM_LABEL,
    CUSTOM_STR_LABEL,
    distinct_monster_ints,
    distinct_monster_strings,
    int_field_options,
    resolve_int_selection,
    resolve_string_selection,
    string_field_options,
)
from utils.boss_spawn_schedule import (
    BOSS_SPAWN_PRESETS,
    build_spawn_x_y_map,
    fetch_boss_row,
    get_notify_column_name,
    list_boss_spawn_columns,
    normalize_spawn_days,
    normalize_spawn_times,
    parse_first_xyz,
    row_to_form_defaults,
)

st.set_page_config(page_title="몬스터 관리", page_icon="🐉", layout="wide")
st.title("🐉 몬스터 관리")
st.caption("monster 테이블을 편집합니다. 서버 재시작 또는 몬스터 리로드 후 반영됩니다. 나비켓에서 보는 것과 동일한 항목입니다.")

with st.expander("💡 몬스터 스폰 관리 vs 여기(몬스터 관리) 차이", expanded=False):
    st.markdown("""
    - **몬스터 스폰 관리** (서버 프로그램 GUI): 서버 실행 중 **몬스터 스폰** 메뉴에서 쓰는 **커스텀 몬스터 추가**는  
      → **monster 테이블에 이름·gfx·level·hp·mp·exp 등 기본값만** 넣고, 바로 그 화면에서 **맵/좌표에 스폰 등록**할 때 사용합니다. (상세 설정은 나중에 나비켓이나 여기 수정 탭에서)
    - **여기(몬스터 관리)** 의 **몬스터 추가**: 같은 **monster 테이블**에 추가하지만, **스폰은 하지 않습니다**.  
      → **추가** 탭에서 수정 탭과 동일한 **상세 설정(스탯, 보스, 플래그, 속성 저항 등)** 을 한 번에 입력할 수 있습니다.  
      → 맵에 나오게 하려면 **몬스터 스폰 관리(웹 또는 서버 GUI)** 에서 해당 몬스터를 선택해 스폰을 등록해야 합니다.
    """)

db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()
show_pending_feedback()

# monster, monster_drop 테이블 존재 여부
try:
    tables = db.get_all_tables()
    if "monster" not in tables:
        st.warning("monster 테이블이 없습니다. 서버 DB 스키마를 확인하세요.")
        st.stop()
    has_monster_drop = "monster_drop" in tables
    has_boss_spawn = "monster_spawnlist_boss" in tables
except Exception as e:
    st.error(f"테이블 목록 조회 실패: {e}")
    st.stop()

tab1, tab2, tab3, tab4 = st.tabs(
    ["📋 몬스터 목록", "➕ 몬스터 추가", "✏️ 몬스터 수정", "⏰ 보스 리젠"]
)

# ========== 탭1: 목록 ==========
with tab1:
    st.subheader("📋 몬스터 목록")
    try:
        rows = db.fetch_all(
            "SELECT name, name_id, gfx, level, hp, mp, exp, boss, boss_class FROM monster ORDER BY name LIMIT 500"
        )
        if rows:
            import pandas as pd
            df = pd.DataFrame(rows)
            df = df.rename(columns={
                "name": "이름", "name_id": "NAME_ID", "gfx": "그래픽", "level": "레벨",
                "hp": "HP", "mp": "MP", "exp": "경험치", "boss": "보스", "boss_class": "보스등급"
            })
            search = st.text_input("🔍 이름/name_id 검색", key="mon_list_search")
            if search and search.strip():
                mask = df["이름"].astype(str).str.contains(search.strip(), case=False, na=False) | \
                       df["NAME_ID"].astype(str).str.contains(search.strip(), case=False, na=False)
                df = df.loc[mask]
            st.dataframe(df, height=400)
            st.caption(f"총 {len(df)}건 (최대 500건 표시)")
        else:
            st.info("몬스터가 없거나 조회에 실패했습니다.")
    except Exception as e:
        st.error(f"조회 실패: {e}")

# ========== 탭2: 추가 (수정 탭과 동일한 상세 입력) ==========
with tab2:
    st.subheader("➕ 몬스터 추가")
    st.caption("수정 탭과 동일한 항목을 입력합니다. 한 번에 상세 설정까지 넣을 수 있습니다.")
    with st.form("monster_add_form"):
        st.markdown("**기본 정보**")
        c1, c2, c3 = st.columns(3)
        with c1:
            add_name = st.text_input("이름 (name) *", placeholder="예: 커스텀_늑대", key="add_mname", help=MH["name"])
            add_name_id = st.text_input("name_id", value="", placeholder="비우면 자동 생성", key="add_nameid", help=MH["name_id"])
            add_gfx = st.number_input("gfx", min_value=0, value=0, key="add_gfx", help=MH["gfx"])
            _add_gm_opts, _add_gm_i = int_field_options(distinct_monster_ints(db, "gfx_mode"), 0)
            add_gfx_mode_sel = st.selectbox(
                "gfx_mode (DB 목록)",
                _add_gm_opts,
                index=min(_add_gm_i, len(_add_gm_opts) - 1),
                key="add_gfx_mode_sel",
                help=MH["gfx_mode"],
            )
            add_gfx_mode_custom = 0
            if add_gfx_mode_sel == CUSTOM_NUM_LABEL:
                add_gfx_mode_custom = st.number_input("gfx_mode 직접 입력", value=0, key="add_gfx_mode_custom")
            add_gfx_mode = resolve_int_selection(add_gfx_mode_sel, add_gfx_mode_custom)
            add_level = st.number_input("level", min_value=1, max_value=99, value=1, key="add_level", help=MH["level"])
        with c2:
            add_hp = st.number_input("hp", min_value=1, value=50, key="add_hp", help=MH["hp"])
            add_mp = st.number_input("mp", min_value=0, value=10, key="add_mp", help=MH["mp"])
            add_exp = st.number_input("exp", min_value=0, value=0, key="add_exp", help=MH["exp"])
            add_boss = st.selectbox("boss", ["false", "true"], index=0, key="add_boss", help=MH["boss"])
            _add_bc_opts, _add_bc_i = string_field_options(distinct_monster_strings(db, "boss_class"), "")
            add_boss_class_sel = st.selectbox(
                "boss_class (DB 목록)",
                _add_bc_opts,
                index=min(_add_bc_i, len(_add_bc_opts) - 1),
                key="add_boss_class_sel",
                help=MH["boss_class"],
            )
            add_boss_class_custom = ""
            if add_boss_class_sel == CUSTOM_STR_LABEL:
                add_boss_class_custom = st.text_input(
                    "boss_class 직접 입력", value="", key="add_boss_class_custom", placeholder="하급/중급/상급 등"
                )
            add_boss_class = resolve_string_selection(add_boss_class_sel, add_boss_class_custom)
        with c3:
            add_lawful = st.number_input("lawful", value=0, key="add_lawful", help=MH["lawful"])
            add_mr = st.number_input("mr", value=0, key="add_mr", help=MH["mr"])
            add_ac = st.number_input("ac", value=0, key="add_ac", help=MH["ac"])
            add_size = st.selectbox("size", ["small", "medium", "large"], index=0, key="add_size", help=MH["size"])
            _add_fam_opts, _add_fam_i = string_field_options(distinct_monster_strings(db, "family"), "")
            add_family_sel = st.selectbox(
                "family (DB 목록)",
                _add_fam_opts,
                index=min(_add_fam_i, len(_add_fam_opts) - 1),
                key="add_family_sel",
                help=MH["family"],
            )
            add_family_custom = ""
            if add_family_sel == CUSTOM_STR_LABEL:
                add_family_custom = st.text_input("family 직접 입력", value="", key="add_family_custom")
            add_family = resolve_string_selection(add_family_sel, add_family_custom)

        st.markdown("**스탯 (str, dex, con, int, wis, cha)**")
        s1, s2, s3, s4, s5, s6 = st.columns(6)
        with s1: add_str = st.number_input("str", value=10, key="add_str", help=MH["str"])
        with s2: add_dex = st.number_input("dex", value=10, key="add_dex", help=MH["dex"])
        with s3: add_con = st.number_input("con", value=10, key="add_con", help=MH["con"])
        with s4: add_int = st.number_input("int", value=10, key="add_int", help=MH["int"])
        with s5: add_wis = st.number_input("wis", value=10, key="add_wis", help=MH["wis"])
        with s6: add_cha = st.number_input("cha", value=10, key="add_cha", help=MH["cha"])

        st.markdown("**공격/행동**")
        a1, a2, a3 = st.columns(3)
        with a1:
            _add_at_opts, _add_at_i = int_field_options(distinct_monster_ints(db, "atk_type"), 0)
            add_atk_type_sel = st.selectbox(
                "atk_type (DB 목록)",
                _add_at_opts,
                index=min(_add_at_i, len(_add_at_opts) - 1),
                key="add_atk_type_sel",
                help=MH["atk_type"],
            )
            add_atk_type_custom = 0
            if add_atk_type_sel == CUSTOM_NUM_LABEL:
                add_atk_type_custom = st.number_input("atk_type 직접 입력", value=0, key="add_atk_type_custom")
            add_atk_type = resolve_int_selection(add_atk_type_sel, add_atk_type_custom)
            _add_ar_opts, _add_ar_i = int_field_options(distinct_monster_ints(db, "atk_range"), 0)
            add_atk_range_sel = st.selectbox(
                "atk_range (DB 목록)",
                _add_ar_opts,
                index=min(_add_ar_i, len(_add_ar_opts) - 1),
                key="add_atk_range_sel",
                help=MH["atk_range"],
            )
            add_atk_range_custom = 0
            if add_atk_range_sel == CUSTOM_NUM_LABEL:
                add_atk_range_custom = st.number_input("atk_range 직접 입력", value=0, key="add_atk_range_custom")
            add_atk_range = resolve_int_selection(add_atk_range_sel, add_atk_range_custom)
        with a2:
            add_atk_invis = st.selectbox("atk_invis", ["false", "true"], index=0, key="add_atk_invis", help=MH["atk_invis"])
            add_atk_poly = st.selectbox("atk_poly", ["false", "true"], index=0, key="add_atk_poly", help=MH["atk_poly"])
        with a3:
            add_arrowGfx = st.number_input("arrowGfx", min_value=0, value=0, key="add_arrowGfx", help=MH["arrowGfx"])

        st.markdown("**플래그 (true/false)**")
        f1, f2, f3 = st.columns(3)
        with f1:
            add_is_pickup = st.selectbox("is_pickup", ["false", "true"], index=0, key="add_pickup", help=MH["is_pickup"])
            add_is_revival = st.selectbox("is_revival", ["false", "true"], index=0, key="add_revival", help=MH["is_revival"])
            add_is_toughskin = st.selectbox("is_toughskin", ["false", "true"], index=0, key="add_toughskin", help=MH["is_toughskin"])
        with f2:
            add_is_adendrop = st.selectbox("is_adendrop", ["false", "true"], index=0, key="add_adendrop", help=MH["is_adendrop"])
            add_is_taming = st.selectbox("is_taming", ["false", "true"], index=0, key="add_taming", help=MH["is_taming"])
            add_is_undead = st.selectbox("is_undead", ["false", "true"], index=0, key="add_undead", help=MH["is_undead"])
        with f3:
            add_is_turn_undead = st.selectbox("is_turn_undead", ["false", "true"], index=0, key="add_turn_undead", help=MH["is_turn_undead"])
            add_haste = st.selectbox("haste", ["false", "true"], index=0, key="add_haste", help=MH["haste"])
            add_bravery = st.selectbox("bravery", ["false", "true"], index=0, key="add_bravery", help=MH["bravery"])

        st.markdown("**속성 저항 (resistance)**")
        r1, r2, r3, r4 = st.columns(4)
        with r1: add_res_earth = st.number_input("resistance_earth", value=0, key="add_res_earth", help=MH["resistance_earth"])
        with r2: add_res_fire = st.number_input("resistance_fire", value=0, key="add_res_fire", help=MH["resistance_fire"])
        with r3: add_res_wind = st.number_input("resistance_wind", value=0, key="add_res_wind", help=MH["resistance_wind"])
        with r4: add_res_water = st.number_input("resistance_water", value=0, key="add_res_water", help=MH["resistance_water"])

        submitted = st.form_submit_button("몬스터 추가")

    if submitted:
        if not (add_name and add_name.strip()):
            st.warning("몬스터 이름을 입력하세요.")
        else:
            name = add_name.strip()
            name_id = add_name_id.strip() if add_name_id else ("$" + str(abs(hash(name)) % 100000))
            sql = """INSERT INTO monster (
                name, name_id, gfx, gfx_mode, boss, boss_class, level, hp, mp, tic_hp, tic_mp,
                str, dex, con, `int`, wis, cha, mr, ac, exp, lawful, size, family,
                atk_type, atk_range, atk_invis, atk_poly, is_pickup, is_revival, is_toughskin,
                is_adendrop, is_taming, resistance_earth, resistance_fire, resistance_wind, resistance_water,
                is_undead, is_turn_undead, arrowGfx, haste, bravery, faust_monster, chance, effect
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, 0, 0,
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,
                %s, %s, %s, %s,
                %s, %s, %s, %s, %s, %s,
                %s, %s, %s, %s, %s, %s,
                %s, %s, %s
            )"""
            params = (
                name, name_id, add_gfx, add_gfx_mode, add_boss, add_boss_class or "", add_level, add_hp, add_mp,
                add_str, add_dex, add_con, add_int, add_wis, add_cha, add_mr, add_ac, add_exp, add_lawful, add_size, add_family or "",
                add_atk_type, add_atk_range, add_atk_invis, add_atk_poly, add_is_pickup, add_is_revival, add_is_toughskin,
                add_is_adendrop, add_is_taming, add_res_earth, add_res_fire, add_res_wind, add_res_water,
                add_is_undead, add_is_turn_undead, add_arrowGfx, add_haste, add_bravery,
                "", 0, 0  # faust_monster, chance, effect
            )
            ok, err = db.execute_query_ex(sql, params)
            if ok:
                queue_feedback("success", f"✅ '{name}' 몬스터가 추가되었습니다. 서버 재시작 또는 몬스터 리로드 후 반영됩니다.")
                st.rerun()
            else:
                st.error(f"❌ 몬스터 추가 실패: {err}")

# ========== 탭3: 수정 (상세 설정) ==========
with tab3:
    st.subheader("✏️ 몬스터 수정 (상세 설정)")
    st.caption("나비켓에서 보는 것과 동일한 항목을 여기서 수정할 수 있습니다.")
    try:
        mon_list = db.fetch_all("SELECT name FROM monster ORDER BY name")
        if not mon_list:
            st.info("수정할 몬스터가 없습니다. 몬스터 추가 탭에서 먼저 등록하세요.")
        else:
            names = [r["name"] for r in mon_list]
            selected = st.selectbox("수정할 몬스터 선택", names, key="mon_edit_select")
            if selected:
                row = db.fetch_one("SELECT * FROM monster WHERE name = %s", (selected,))
                if not row:
                    st.warning("해당 몬스터를 찾을 수 없습니다.")
                else:
                    # Streamlit은 위젯 key가 같으면 이전 세션 값을 유지하고 value=는 무시함.
                    # 선택 몬스터마다 key 접미사를 달아 폼이 DB 값으로 갱신되게 함.
                    _edit_sk = hashlib.md5(selected.encode("utf-8")).hexdigest()[:16]
                    with st.form(f"monster_edit_form_{_edit_sk}"):
                        st.markdown("**기본 정보**")
                        c1, c2, c3 = st.columns(3)
                        with c1:
                            edit_name = st.text_input("이름 (name)", value=str(row.get("name") or ""), key=f"edit_mname_{_edit_sk}", help=MH["name"])
                            edit_name_id = st.text_input("name_id", value=str(row.get("name_id") or ""), key=f"edit_nameid_{_edit_sk}", help=MH["name_id"])
                            edit_gfx = st.number_input("gfx", value=int(row.get("gfx") or 0), min_value=0, key=f"edit_gfx_{_edit_sk}", help=MH["gfx"])
                            _cur_gm = int(row.get("gfx_mode") or 0)
                            _edit_gm_opts, _edit_gm_i = int_field_options(distinct_monster_ints(db, "gfx_mode"), _cur_gm)
                            edit_gfx_mode_sel = st.selectbox(
                                "gfx_mode (DB 목록)",
                                _edit_gm_opts,
                                index=min(_edit_gm_i, len(_edit_gm_opts) - 1),
                                key=f"edit_gfx_mode_sel_{_edit_sk}",
                                help=MH["gfx_mode"],
                            )
                            edit_gfx_mode_custom = _cur_gm
                            if edit_gfx_mode_sel == CUSTOM_NUM_LABEL:
                                edit_gfx_mode_custom = st.number_input(
                                    "gfx_mode 직접 입력", value=_cur_gm, key=f"edit_gfx_mode_custom_{_edit_sk}"
                                )
                            edit_gfx_mode = resolve_int_selection(edit_gfx_mode_sel, edit_gfx_mode_custom)
                            edit_level = st.number_input("level", value=int(row.get("level") or 1), min_value=1, max_value=99, key=f"edit_level_{_edit_sk}", help=MH["level"])
                        with c2:
                            edit_hp = st.number_input("hp", value=int(row.get("hp") or 50), min_value=1, key=f"edit_hp_{_edit_sk}", help=MH["hp"])
                            edit_mp = st.number_input("mp", value=int(row.get("mp") or 10), min_value=0, key=f"edit_mp_{_edit_sk}", help=MH["mp"])
                            edit_exp = st.number_input("exp", value=int(row.get("exp") or 0), min_value=0, key=f"edit_exp_{_edit_sk}", help=MH["exp"])
                            edit_boss = st.selectbox("boss", ["false", "true"], index=1 if str(row.get("boss") or "").lower() == "true" else 0, key=f"edit_boss_{_edit_sk}", help=MH["boss"])
                            _cur_bc = str(row.get("boss_class") or "").strip()
                            _edit_bc_opts, _edit_bc_i = string_field_options(distinct_monster_strings(db, "boss_class"), _cur_bc)
                            edit_boss_class_sel = st.selectbox(
                                "boss_class (DB 목록)",
                                _edit_bc_opts,
                                index=min(_edit_bc_i, len(_edit_bc_opts) - 1),
                                key=f"edit_boss_class_sel_{_edit_sk}",
                                help=MH["boss_class"],
                            )
                            edit_boss_class_custom = _cur_bc
                            if edit_boss_class_sel == CUSTOM_STR_LABEL:
                                edit_boss_class_custom = st.text_input(
                                    "boss_class 직접 입력", value=_cur_bc, key=f"edit_boss_class_custom_{_edit_sk}"
                                )
                            edit_boss_class = resolve_string_selection(edit_boss_class_sel, edit_boss_class_custom)
                        with c3:
                            edit_lawful = st.number_input("lawful", value=int(row.get("lawful") or 0), key=f"edit_lawful_{_edit_sk}", help=MH["lawful"])
                            edit_mr = st.number_input("mr", value=int(row.get("mr") or 0), key=f"edit_mr_{_edit_sk}", help=MH["mr"])
                            edit_ac = st.number_input("ac", value=int(row.get("ac") or 0), key=f"edit_ac_{_edit_sk}", help=MH["ac"])
                            _size = str(row.get("size") or "small").lower()
                            if _size not in ("small", "medium", "large"):
                                _size = "small"
                            edit_size = st.selectbox("size", ["small", "medium", "large"], index=["small", "medium", "large"].index(_size), key=f"edit_size_{_edit_sk}", help=MH["size"])
                            _cur_fam = str(row.get("family") or "").strip()
                            _edit_fam_opts, _edit_fam_i = string_field_options(distinct_monster_strings(db, "family"), _cur_fam)
                            edit_family_sel = st.selectbox(
                                "family (DB 목록)",
                                _edit_fam_opts,
                                index=min(_edit_fam_i, len(_edit_fam_opts) - 1),
                                key=f"edit_family_sel_{_edit_sk}",
                                help=MH["family"],
                            )
                            edit_family_custom = _cur_fam
                            if edit_family_sel == CUSTOM_STR_LABEL:
                                edit_family_custom = st.text_input(
                                    "family 직접 입력", value=_cur_fam, key=f"edit_family_custom_{_edit_sk}"
                                )
                            edit_family = resolve_string_selection(edit_family_sel, edit_family_custom)

                        st.markdown("**스탯 (str, dex, con, int, wis, cha)**")
                        s1, s2, s3, s4, s5, s6 = st.columns(6)
                        with s1: edit_str = st.number_input("str", value=int(row.get("str") or 10), key=f"edit_str_{_edit_sk}", help=MH["str"])
                        with s2: edit_dex = st.number_input("dex", value=int(row.get("dex") or 10), key=f"edit_dex_{_edit_sk}", help=MH["dex"])
                        with s3: edit_con = st.number_input("con", value=int(row.get("con") or 10), key=f"edit_con_{_edit_sk}", help=MH["con"])
                        with s4: edit_int = st.number_input("int", value=int(row.get("int") or 10), key=f"edit_int_{_edit_sk}", help=MH["int"])
                        with s5: edit_wis = st.number_input("wis", value=int(row.get("wis") or 10), key=f"edit_wis_{_edit_sk}", help=MH["wis"])
                        with s6: edit_cha = st.number_input("cha", value=int(row.get("cha") or 10), key=f"edit_cha_{_edit_sk}", help=MH["cha"])

                        st.markdown("**공격/행동**")
                        a1, a2, a3 = st.columns(3)
                        with a1:
                            _cur_at = int(row.get("atk_type") or 0)
                            _edit_at_opts, _edit_at_i = int_field_options(distinct_monster_ints(db, "atk_type"), _cur_at)
                            edit_atk_type_sel = st.selectbox(
                                "atk_type (DB 목록)",
                                _edit_at_opts,
                                index=min(_edit_at_i, len(_edit_at_opts) - 1),
                                key=f"edit_atk_type_sel_{_edit_sk}",
                                help=MH["atk_type"],
                            )
                            edit_atk_type_custom = _cur_at
                            if edit_atk_type_sel == CUSTOM_NUM_LABEL:
                                edit_atk_type_custom = st.number_input(
                                    "atk_type 직접 입력", value=_cur_at, key=f"edit_atk_type_custom_{_edit_sk}"
                                )
                            edit_atk_type = resolve_int_selection(edit_atk_type_sel, edit_atk_type_custom)
                            _cur_ar = int(row.get("atk_range") or 0)
                            _edit_ar_opts, _edit_ar_i = int_field_options(distinct_monster_ints(db, "atk_range"), _cur_ar)
                            edit_atk_range_sel = st.selectbox(
                                "atk_range (DB 목록)",
                                _edit_ar_opts,
                                index=min(_edit_ar_i, len(_edit_ar_opts) - 1),
                                key=f"edit_atk_range_sel_{_edit_sk}",
                                help=MH["atk_range"],
                            )
                            edit_atk_range_custom = _cur_ar
                            if edit_atk_range_sel == CUSTOM_NUM_LABEL:
                                edit_atk_range_custom = st.number_input(
                                    "atk_range 직접 입력", value=_cur_ar, key=f"edit_atk_range_custom_{_edit_sk}"
                                )
                            edit_atk_range = resolve_int_selection(edit_atk_range_sel, edit_atk_range_custom)
                        with a2:
                            edit_atk_invis = st.selectbox("atk_invis", ["false", "true"], index=1 if str(row.get("atk_invis") or "").lower() == "true" else 0, key=f"edit_atk_invis_{_edit_sk}", help=MH["atk_invis"])
                            edit_atk_poly = st.selectbox("atk_poly", ["false", "true"], index=1 if str(row.get("atk_poly") or "").lower() == "true" else 0, key=f"edit_atk_poly_{_edit_sk}", help=MH["atk_poly"])
                        with a3:
                            edit_arrowGfx = st.number_input("arrowGfx", value=int(row.get("arrowGfx") or 0), min_value=0, key=f"edit_arrowGfx_{_edit_sk}", help=MH["arrowGfx"])

                        st.markdown("**플래그 (true/false)**")
                        f1, f2, f3 = st.columns(3)
                        with f1:
                            edit_is_pickup = st.selectbox("is_pickup", ["false", "true"], index=1 if str(row.get("is_pickup") or "").lower() == "true" else 0, key=f"edit_pickup_{_edit_sk}", help=MH["is_pickup"])
                            edit_is_revival = st.selectbox("is_revival", ["false", "true"], index=1 if str(row.get("is_revival") or "").lower() == "true" else 0, key=f"edit_revival_{_edit_sk}", help=MH["is_revival"])
                            edit_is_toughskin = st.selectbox("is_toughskin", ["false", "true"], index=1 if str(row.get("is_toughskin") or "").lower() == "true" else 0, key=f"edit_toughskin_{_edit_sk}", help=MH["is_toughskin"])
                        with f2:
                            edit_is_adendrop = st.selectbox("is_adendrop", ["false", "true"], index=1 if str(row.get("is_adendrop") or "").lower() == "true" else 0, key=f"edit_adendrop_{_edit_sk}", help=MH["is_adendrop"])
                            edit_is_taming = st.selectbox("is_taming", ["false", "true"], index=1 if str(row.get("is_taming") or "").lower() == "true" else 0, key=f"edit_taming_{_edit_sk}", help=MH["is_taming"])
                            edit_is_undead = st.selectbox("is_undead", ["false", "true"], index=1 if str(row.get("is_undead") or "").lower() == "true" else 0, key=f"edit_undead_{_edit_sk}", help=MH["is_undead"])
                        with f3:
                            edit_is_turn_undead = st.selectbox("is_turn_undead", ["false", "true"], index=1 if str(row.get("is_turn_undead") or "").lower() == "true" else 0, key=f"edit_turn_undead_{_edit_sk}", help=MH["is_turn_undead"])
                            edit_haste = st.selectbox("haste", ["false", "true"], index=1 if str(row.get("haste") or "").lower() == "true" else 0, key=f"edit_haste_{_edit_sk}", help=MH["haste"])
                            edit_bravery = st.selectbox("bravery", ["false", "true"], index=1 if str(row.get("bravery") or "").lower() == "true" else 0, key=f"edit_bravery_{_edit_sk}", help=MH["bravery"])

                        st.markdown("**속성 저항 (resistance)**")
                        r1, r2, r3, r4 = st.columns(4)
                        with r1: edit_res_earth = st.number_input("resistance_earth", value=int(row.get("resistance_earth") or 0), key=f"edit_res_earth_{_edit_sk}", help=MH["resistance_earth"])
                        with r2: edit_res_fire = st.number_input("resistance_fire", value=int(row.get("resistance_fire") or 0), key=f"edit_res_fire_{_edit_sk}", help=MH["resistance_fire"])
                        with r3: edit_res_wind = st.number_input("resistance_wind", value=int(row.get("resistance_wind") or 0), key=f"edit_res_wind_{_edit_sk}", help=MH["resistance_wind"])
                        with r4: edit_res_water = st.number_input("resistance_water", value=int(row.get("resistance_water") or 0), key=f"edit_res_water_{_edit_sk}", help=MH["resistance_water"])

                        if st.form_submit_button("수정 반영"):
                            sql = """UPDATE monster SET
                                name=%s, name_id=%s, gfx=%s, gfx_mode=%s, level=%s, hp=%s, mp=%s, exp=%s,
                                boss=%s, boss_class=%s, lawful=%s, mr=%s, ac=%s, size=%s, family=%s,
                                str=%s, dex=%s, con=%s, `int`=%s, wis=%s, cha=%s,
                                atk_type=%s, atk_range=%s, atk_invis=%s, atk_poly=%s, arrowGfx=%s,
                                is_pickup=%s, is_revival=%s, is_toughskin=%s, is_adendrop=%s, is_taming=%s,
                                is_undead=%s, is_turn_undead=%s, haste=%s, bravery=%s,
                                resistance_earth=%s, resistance_fire=%s, resistance_wind=%s, resistance_water=%s
                                WHERE name=%s"""
                            params = (
                                edit_name, edit_name_id, edit_gfx, edit_gfx_mode, edit_level, edit_hp, edit_mp, edit_exp,
                                edit_boss, edit_boss_class, edit_lawful, edit_mr, edit_ac, edit_size, edit_family,
                                edit_str, edit_dex, edit_con, edit_int, edit_wis, edit_cha,
                                edit_atk_type, edit_atk_range, edit_atk_invis, edit_atk_poly, edit_arrowGfx,
                                edit_is_pickup, edit_is_revival, edit_is_toughskin, edit_is_adendrop, edit_is_taming,
                                edit_is_undead, edit_is_turn_undead, edit_haste, edit_bravery,
                                edit_res_earth, edit_res_fire, edit_res_wind, edit_res_water,
                                selected
                            )
                            ok, err = db.execute_query_ex(sql, params)
                            if ok:
                                queue_feedback("success", "✅ 몬스터 정보가 수정 반영되었습니다. 서버 재시작 또는 몬스터 리로드 후 적용됩니다.")
                                st.rerun()
                            else:
                                st.error(f"❌ 수정 실패: {err}")

                    # ---------- 드랍 아이템 수정 (monster_drop) ----------
                    if has_monster_drop:
                        st.divider()
                        st.subheader("🎁 드랍 아이템 수정")
                        st.caption(
                            "아데나: 최소~최대 드랍량. 주문서 등: 최소~최대 개수. "
                            "**item_bress**: 서버가 드랍 시 `setBless`에 넣는 값 — **0=축복, 1=일반, 2=저주**. "
                            "무기 이름은 **`weapon`/`item`에 있는 그대로**(예: 일본도) 쓰면 되고, **축복 전용 행을 item에 추가할 필요는 없습니다.** "
                            "DB **PRIMARY KEY**는 `(monster_name, item_name, item_bress, item_en)` 이라 **같은 조합은 한 줄만** 가능합니다. "
                            "저장 후 **서버 리로드**에서 monster_drop 리로드."
                        )
                        drops = db.fetch_all(
                            "SELECT name, monster_name, item_name, item_bress, item_en, count_min, count_max, chance "
                            "FROM monster_drop WHERE monster_name = %s ORDER BY item_name, item_bress, item_en",
                            (selected,),
                        )
                        if drops:
                            st.markdown("**현재 드랍 목록**")
                            st.caption(
                                f"**총 {len(drops)}건** (DB `monster_name` 이 선택 몬스터와 일치하는 행만 표시됩니다.)"
                            )
                            _tbl = []
                            for d in drops:
                                _bn = d.get("item_bress")
                                try:
                                    _bn = int(_bn) if _bn is not None else 1
                                except (TypeError, ValueError):
                                    _bn = 1
                                try:
                                    _en = int(d.get("item_en") or 0)
                                except (TypeError, ValueError):
                                    _en = 0
                                _bl = "축복" if _bn == 0 else "저주" if _bn == 2 else "일반"
                                _tbl.append(
                                    {
                                        "아이템": d.get("item_name") or "",
                                        "축복구분": _bl,
                                        "bress": _bn,
                                        "인챈": _en,
                                        "최소": d.get("count_min"),
                                        "최대": d.get("count_max"),
                                        "확률(%)": d.get("chance"),
                                        "name(참고)": d.get("name") or "",
                                    }
                                )
                            st.dataframe(
                                pd.DataFrame(_tbl),
                                use_container_width=True,
                                height=min(420, max(120, 36 + len(_tbl) * 35)),
                            )
                            for d in drops:
                                item_name = d.get("item_name") or ""
                                cmin = d.get("count_min")
                                cmax = d.get("count_max")
                                ch = d.get("chance")
                                bress = d.get("item_bress")
                                if bress is None:
                                    bress = 1
                                try:
                                    bress = int(bress)
                                except (TypeError, ValueError):
                                    bress = 1
                                try:
                                    en = int(d.get("item_en") or 0)
                                except (TypeError, ValueError):
                                    en = 0
                                bress_lbl = "축복" if bress == 0 else "저주" if bress == 2 else "일반"
                                # item_name·특수문자·동일 제목 expander 충돌 방지: 행마다 고유 키·제목
                                _rk = hashlib.md5(
                                    f"{selected}\0{item_name}\0{bress}\0{en}".encode("utf-8")
                                ).hexdigest()[:16]
                                del_key = f"del_drop_{_rk}"
                                edit_key = f"edit_drop_{_rk}"
                                col1, col2 = st.columns([4, 1])
                                with col1:
                                    st.text(
                                        f"• {item_name}  |  {bress_lbl}(bress={bress})  |  인챈{en}  |  최소 {cmin} ~ 최대 {cmax}  |  확률 {ch}%"
                                    )
                                with col2:
                                    if st.button("삭제", key=del_key):
                                        ok, err = db.execute_query_ex(
                                            "DELETE FROM monster_drop WHERE monster_name=%s AND item_name=%s AND item_bress=%s AND item_en=%s",
                                            (selected, item_name, bress, en),
                                        )
                                        if ok:
                                            queue_feedback("success", "✅ 드랍 행이 삭제되었습니다.")
                                            st.rerun()
                                        else:
                                            st.error(f"❌ 삭제 실패: {err}")
                                with st.expander(
                                    f"✏️ 수정: {item_name} | {bress_lbl} | 인챈{en}",
                                    expanded=False,
                                ):
                                    _bress_idx = {1: 0, 0: 1, 2: 2}.get(bress, 0)
                                    new_bress_sel = st.selectbox(
                                        "드랍 시 축복·저주 (item_bress)",
                                        options=[(1, "일반 (1)"), (0, "축복 (0)"), (2, "저주 (2)")],
                                        format_func=lambda x: x[1],
                                        index=_bress_idx,
                                        key=f"ebress_{edit_key}",
                                        help="0=축복, 1=일반, 2=저주. 저장 시 이 행의 bress·인챈이 함께 갱신됩니다.",
                                    )
                                    new_en = st.number_input(
                                        "드랍 인챈 단계 (item_en)",
                                        min_value=0,
                                        max_value=20,
                                        value=en,
                                        key=f"een_{edit_key}",
                                    )
                                    new_cmin = st.number_input("최소 수량", min_value=1, value=int(cmin) if cmin is not None else 1, key=f"ecmin_{edit_key}")
                                    new_cmax = st.number_input("최대 수량", min_value=1, value=int(cmax) if cmax is not None else 1, key=f"ecmax_{edit_key}")
                                    new_chance = st.number_input(
                                        "드랍 확률 (DB chance 값)",
                                        min_value=0.0,
                                        max_value=100.0,
                                        value=float(ch) if ch is not None else 50.0,
                                        step=0.01,
                                        format="%.4g",
                                        key=f"ech_{edit_key}",
                                        help="서버: (이 값×0.01)×rate_drop 로 최종 확률. 예: 10→10%, 0.5→0.5% 기본. 배율 20배면 0.5→실제 약 10%.",
                                    )
                                    if st.button("수정 반영", key=f"apply_{edit_key}"):
                                        if new_cmin > new_cmax:
                                            st.warning("최소 수량이 최대 수량보다 클 수 없습니다.")
                                        else:
                                            new_bress = int(new_bress_sel[0])
                                            ok, err = db.execute_query_ex(
                                                "UPDATE monster_drop SET count_min=%s, count_max=%s, chance=%s, item_bress=%s, item_en=%s WHERE monster_name=%s AND item_name=%s AND item_bress=%s AND item_en=%s",
                                                (
                                                    new_cmin,
                                                    new_cmax,
                                                    str(new_chance),
                                                    new_bress,
                                                    int(new_en),
                                                    selected,
                                                    item_name,
                                                    bress,
                                                    en,
                                                ),
                                            )
                                            if ok:
                                                queue_feedback("success", "✅ 드랍 정보가 수정되었습니다.")
                                                st.rerun()
                                            else:
                                                st.error(f"❌ 수정 실패: {err}")
                        else:
                            st.info("등록된 드랍이 없습니다. 아래에서 아이템을 검색해 추가하세요.")

                        with st.expander("➕ 드랍 추가", expanded=len(drops) == 0):
                            search_item = st.text_input("아이템 이름 검색 (일부만 입력해도 됨)", key="drop_item_search", placeholder="예: 아데나, 주문서")
                            add_item_name = None
                            if search_item and search_item.strip():
                                try:
                                    items = db.fetch_all(
                                        "SELECT `아이템이름` FROM item WHERE `아이템이름` LIKE %s ORDER BY `아이템이름` LIMIT 80",
                                        (f"%{search_item.strip()}%",),
                                    )
                                except Exception:
                                    items = db.fetch_all(
                                        "SELECT name FROM item WHERE name LIKE %s ORDER BY name LIMIT 80",
                                        (f"%{search_item.strip()}%",),
                                    ) if "item" in (db.get_all_tables() or []) else []
                                if items:
                                    col = "아이템이름" if items and "아이템이름" in (items[0] or {}) else "name"
                                    item_options = [str(r[col]) for r in items if r.get(col)]
                                    if item_options:
                                        add_item_name = st.selectbox("추가할 아이템 선택", item_options, key="drop_add_item")
                                else:
                                    st.caption("검색 결과 없음")
                            drop_bress = st.selectbox(
                                "드랍 시 축복·저주 (item_bress)",
                                options=[(1, "일반 (1)"), (0, "축복 (0)"), (2, "저주 (2)")],
                                format_func=lambda x: x[1],
                                index=0,
                                key="drop_add_bress",
                                help="서버 MonsterInstance 가 드랍 아이템에 setBless(item_bress) 합니다. 무기·소모품 공통.",
                            )
                            add_item_en = st.number_input(
                                "드랍 인챈 단계 (item_en)",
                                min_value=0,
                                max_value=20,
                                value=0,
                                key="drop_add_en",
                            )
                            st.markdown("**수량·확률**")
                            c1, c2, c3 = st.columns(3)
                            with c1:
                                add_count_min = st.number_input("최소 수량 (아데나: 최소 금액, 주문서 등: 최소 개수)", min_value=1, value=1, key="drop_cmin")
                            with c2:
                                add_count_max = st.number_input("최대 수량 (아데나: 최대 금액, 주문서 등: 최대 개수)", min_value=1, value=1, key="drop_cmax")
                            with c3:
                                add_chance = st.number_input(
                                    "드랍 확률 (DB chance 값)",
                                    min_value=0.0,
                                    max_value=100.0,
                                    value=50.0,
                                    step=0.01,
                                    format="%.4g",
                                    key="drop_chance",
                                    help="서버: (이 값×0.01)×rate_drop. 소수 가능(예: 0.5).",
                                )
                            if st.button("드랍 추가", key="drop_add_btn"):
                                if not add_item_name:
                                    st.warning("위에서 아이템 이름을 검색한 뒤, 추가할 아이템을 선택하세요.")
                                elif add_count_min > add_count_max:
                                    st.warning("최소 수량이 최대 수량보다 클 수 없습니다.")
                                else:
                                    name_val = add_item_name
                                    ib = int(drop_bress[0])
                                    ie = int(add_item_en)
                                    dup = db.fetch_one(
                                        "SELECT 1 AS x FROM monster_drop WHERE monster_name=%s AND item_name=%s AND item_bress=%s AND item_en=%s LIMIT 1",
                                        (selected, add_item_name, ib, ie),
                                    )
                                    if dup:
                                        _bl = "축복" if ib == 0 else "저주" if ib == 2 else "일반"
                                        st.warning(
                                            f"**이미 등록된 드랍입니다 (중복).** "
                                            f"몬스터 `{selected}` · 아이템 `{add_item_name}` · {_bl}(bress={ib}) · 인챈{ie} 조합은 DB에 있습니다. "
                                            f"위 **현재 드랍 목록** 표에서 해당 행을 찾아 **✏️ 수정**으로 수량·확률을 바꾸세요."
                                        )
                                    else:
                                        ok, err = db.execute_query_ex(
                                            "INSERT INTO monster_drop (name, monster_name, item_name, item_bress, item_en, count_min, count_max, chance) "
                                            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)",
                                            (name_val, selected, add_item_name, ib, ie, add_count_min, add_count_max, str(add_chance)),
                                        )
                                        if ok:
                                            queue_feedback(
                                                "success",
                                                f"✅ '{add_item_name}' 드랍이 추가되었습니다. 서버 리로드 페이지에서 monster_drop 리로드를 실행하세요.",
                                            )
                                            st.rerun()
                                        else:
                                            st.error(f"❌ 드랍 추가 실패: {err}")
    except Exception as e:
        st.error(f"수정 오류: {e}")

# ========== 탭4: 보스 리젠 (monster_spawnlist_boss) ==========
with tab4:
    st.subheader("⏰ 보스 리젠 (monster_spawnlist_boss)")
    st.caption(
        "요일·시각·좌표(X,Y,map)를 바꿉니다. **스폰 시각** 또는 **요일**을 비우면 해당 보스는 **시간 스폰되지 않습니다.** "
        "저장 후 서버에서 **monster_spawnlist_boss 리로드** 또는 **재기동**이 필요합니다."
    )
    if not has_boss_spawn:
        st.warning("monster_spawnlist_boss 테이블이 없습니다. DB 스키마를 확인하세요.")
    else:
        all_cols = list_boss_spawn_columns(db)
        notify_col = get_notify_column_name(db)
        if notify_col not in all_cols:
            notify_col = "스폰알림여부"

        with st.expander("카스파 일행 (메르키오르 · 발터자르 · 세마)", expanded=False):
            st.markdown(
                """
**카스파**가 스폰될 때 서버 `BossController`가 **세마·발터자르·메르키오르**를 같은 맵에 자동 소환합니다.  
이 몬스터들은 **별도 `monster_spawnlist_boss` 행이 없습니다.** 리젠 시간·맵은 아래 **카스파**만 수정하면 됩니다.
"""
            )
        with st.expander("커츠 군단 (해적선 · 흑기사)", expanded=False):
            st.markdown(
                """
**커츠**가 스폰될 때 서버 `BossController`가 **해적선·흑기사** 등을 추가로 소환합니다.  
시간·좌표는 아래 **커츠** 행만 수정하면 됩니다.
"""
            )

        for idx, preset in enumerate(BOSS_SPAWN_PRESETS):
            pk = preset["name"]
            row = fetch_boss_row(db, pk)
            fd = row_to_form_defaults(row, preset)
            x0, y0, m0 = parse_first_xyz(fd["spawn_x_y_map"])
            mark = "✅" if row else "⚪"
            with st.expander(f"{mark} {preset['label']} (`{pk}`)", expanded=False):
                st.caption("DB에 등록됨" if row else "DB에 없음 — 저장 시 **INSERT** 됩니다.")
                with st.form(f"boss_spawn_{idx}_{pk}"):
                    f_monster = st.text_input(
                        "monster (monster 테이블 이름)",
                        value=fd["monster"],
                        key=f"bs_mon_{idx}",
                    )
                    cx1, cx2, cx3 = st.columns(3)
                    with cx1:
                        f_x = st.number_input("X", value=int(x0), step=1, key=f"bs_x_{idx}")
                    with cx2:
                        f_y = st.number_input("Y", value=int(y0), step=1, key=f"bs_y_{idx}")
                    with cx3:
                        f_map = st.number_input("map", value=int(m0), step=1, key=f"bs_map_{idx}")
                    f_time = st.text_input(
                        "스폰 시각 (쉼표 구분)",
                        value=fd["spawn_time"],
                        help="예: 10:00, 22:00. **비우면 스폰 시각이 없어 스폰되지 않습니다.**",
                        key=f"bs_t_{idx}",
                    )
                    f_day = st.text_input(
                        "요일 (쉼표 구분)",
                        value=fd["spawn_day"],
                        help="예: 월, 화, 수, 목, 금, 토, 일. **비우면 요일이 없어 스폰되지 않습니다.**",
                        key=f"bs_d_{idx}",
                    )
                    f_group = st.text_input(
                        "group_monster (고급, 비우면 미사용)",
                        value=fd["group_monster"],
                        help="서버 Boss 그룹 스폰 문법이 있을 때만 입력",
                        key=f"bs_g_{idx}",
                    )
                    f_notify = st.checkbox(
                        "스폰 알림 사용",
                        value=fd["notify"],
                        key=f"bs_n_{idx}",
                        help="켜면 스폰 직후 월드 전체 채팅에 `[보스이름]가 소환되었습니다.` 가 나갑니다. 끄면 스폰만 됩니다.",
                    )

                    save = st.form_submit_button("💾 저장 (없으면 추가)")
                    if save:
                        if not (f_monster or "").strip():
                            st.warning("monster 이름을 입력하세요.")
                        elif notify_col not in all_cols:
                            st.error("스폰 알림 컬럼을 스키마에서 찾지 못했습니다.")
                        else:
                            sxym = build_spawn_x_y_map(int(f_x), int(f_y), int(f_map))
                            st_norm = normalize_spawn_times(f_time)
                            sd_norm = normalize_spawn_days(f_day)
                            nval = 1 if f_notify else 0
                            qn = notify_col.replace("`", "")
                            if row:
                                sql = (
                                    f"UPDATE monster_spawnlist_boss SET monster=%s, spawn_x_y_map=%s, "
                                    f"spawn_time=%s, spawn_day=%s, group_monster=%s, `{qn}`=%s WHERE name=%s"
                                )
                                params = (
                                    f_monster.strip(),
                                    sxym,
                                    st_norm,
                                    sd_norm,
                                    (f_group or "").strip(),
                                    nval,
                                    pk,
                                )
                            else:
                                sql = (
                                    f"INSERT INTO monster_spawnlist_boss "
                                    f"(name, monster, spawn_x_y_map, spawn_time, spawn_day, group_monster, `{qn}`) "
                                    f"VALUES (%s,%s,%s,%s,%s,%s,%s)"
                                )
                                params = (
                                    pk,
                                    f_monster.strip(),
                                    sxym,
                                    st_norm,
                                    sd_norm,
                                    (f_group or "").strip(),
                                    nval,
                                )
                            ok, err = db.execute_query_ex(sql, params)
                            if ok:
                                queue_feedback(
                                    "success",
                                    f"✅ `{pk}` 보스 스폰 설정을 저장했습니다. 서버에서 리로드하세요.",
                                )
                                st.rerun()
                            else:
                                st.error(f"저장 실패: {err}")
