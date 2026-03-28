"""
몬스터 관리 - monster 테이블 조회/추가/수정 (나비켓과 동일한 상세 설정)
서버 MonsterDatabase가 읽는 컬럼 기준.
"""
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
except Exception as e:
    st.error(f"테이블 목록 조회 실패: {e}")
    st.stop()

tab1, tab2, tab3 = st.tabs(["📋 몬스터 목록", "➕ 몬스터 추가", "✏️ 몬스터 수정"])

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
                    with st.form("monster_edit_form"):
                        st.markdown("**기본 정보**")
                        c1, c2, c3 = st.columns(3)
                        with c1:
                            edit_name = st.text_input("이름 (name)", value=str(row.get("name") or ""), key="edit_mname", help=MH["name"])
                            edit_name_id = st.text_input("name_id", value=str(row.get("name_id") or ""), key="edit_nameid", help=MH["name_id"])
                            edit_gfx = st.number_input("gfx", value=int(row.get("gfx") or 0), min_value=0, key="edit_gfx", help=MH["gfx"])
                            _cur_gm = int(row.get("gfx_mode") or 0)
                            _edit_gm_opts, _edit_gm_i = int_field_options(distinct_monster_ints(db, "gfx_mode"), _cur_gm)
                            edit_gfx_mode_sel = st.selectbox(
                                "gfx_mode (DB 목록)",
                                _edit_gm_opts,
                                index=min(_edit_gm_i, len(_edit_gm_opts) - 1),
                                key="edit_gfx_mode_sel",
                                help=MH["gfx_mode"],
                            )
                            edit_gfx_mode_custom = _cur_gm
                            if edit_gfx_mode_sel == CUSTOM_NUM_LABEL:
                                edit_gfx_mode_custom = st.number_input(
                                    "gfx_mode 직접 입력", value=_cur_gm, key="edit_gfx_mode_custom"
                                )
                            edit_gfx_mode = resolve_int_selection(edit_gfx_mode_sel, edit_gfx_mode_custom)
                            edit_level = st.number_input("level", value=int(row.get("level") or 1), min_value=1, max_value=99, key="edit_level", help=MH["level"])
                        with c2:
                            edit_hp = st.number_input("hp", value=int(row.get("hp") or 50), min_value=1, key="edit_hp", help=MH["hp"])
                            edit_mp = st.number_input("mp", value=int(row.get("mp") or 10), min_value=0, key="edit_mp", help=MH["mp"])
                            edit_exp = st.number_input("exp", value=int(row.get("exp") or 0), min_value=0, key="edit_exp", help=MH["exp"])
                            edit_boss = st.selectbox("boss", ["false", "true"], index=1 if str(row.get("boss") or "").lower() == "true" else 0, key="edit_boss", help=MH["boss"])
                            _cur_bc = str(row.get("boss_class") or "").strip()
                            _edit_bc_opts, _edit_bc_i = string_field_options(distinct_monster_strings(db, "boss_class"), _cur_bc)
                            edit_boss_class_sel = st.selectbox(
                                "boss_class (DB 목록)",
                                _edit_bc_opts,
                                index=min(_edit_bc_i, len(_edit_bc_opts) - 1),
                                key="edit_boss_class_sel",
                                help=MH["boss_class"],
                            )
                            edit_boss_class_custom = _cur_bc
                            if edit_boss_class_sel == CUSTOM_STR_LABEL:
                                edit_boss_class_custom = st.text_input(
                                    "boss_class 직접 입력", value=_cur_bc, key="edit_boss_class_custom"
                                )
                            edit_boss_class = resolve_string_selection(edit_boss_class_sel, edit_boss_class_custom)
                        with c3:
                            edit_lawful = st.number_input("lawful", value=int(row.get("lawful") or 0), key="edit_lawful", help=MH["lawful"])
                            edit_mr = st.number_input("mr", value=int(row.get("mr") or 0), key="edit_mr", help=MH["mr"])
                            edit_ac = st.number_input("ac", value=int(row.get("ac") or 0), key="edit_ac", help=MH["ac"])
                            _size = str(row.get("size") or "small").lower()
                            if _size not in ("small", "medium", "large"):
                                _size = "small"
                            edit_size = st.selectbox("size", ["small", "medium", "large"], index=["small", "medium", "large"].index(_size), key="edit_size", help=MH["size"])
                            _cur_fam = str(row.get("family") or "").strip()
                            _edit_fam_opts, _edit_fam_i = string_field_options(distinct_monster_strings(db, "family"), _cur_fam)
                            edit_family_sel = st.selectbox(
                                "family (DB 목록)",
                                _edit_fam_opts,
                                index=min(_edit_fam_i, len(_edit_fam_opts) - 1),
                                key="edit_family_sel",
                                help=MH["family"],
                            )
                            edit_family_custom = _cur_fam
                            if edit_family_sel == CUSTOM_STR_LABEL:
                                edit_family_custom = st.text_input(
                                    "family 직접 입력", value=_cur_fam, key="edit_family_custom"
                                )
                            edit_family = resolve_string_selection(edit_family_sel, edit_family_custom)

                        st.markdown("**스탯 (str, dex, con, int, wis, cha)**")
                        s1, s2, s3, s4, s5, s6 = st.columns(6)
                        with s1: edit_str = st.number_input("str", value=int(row.get("str") or 10), key="edit_str", help=MH["str"])
                        with s2: edit_dex = st.number_input("dex", value=int(row.get("dex") or 10), key="edit_dex", help=MH["dex"])
                        with s3: edit_con = st.number_input("con", value=int(row.get("con") or 10), key="edit_con", help=MH["con"])
                        with s4: edit_int = st.number_input("int", value=int(row.get("int") or 10), key="edit_int", help=MH["int"])
                        with s5: edit_wis = st.number_input("wis", value=int(row.get("wis") or 10), key="edit_wis", help=MH["wis"])
                        with s6: edit_cha = st.number_input("cha", value=int(row.get("cha") or 10), key="edit_cha", help=MH["cha"])

                        st.markdown("**공격/행동**")
                        a1, a2, a3 = st.columns(3)
                        with a1:
                            _cur_at = int(row.get("atk_type") or 0)
                            _edit_at_opts, _edit_at_i = int_field_options(distinct_monster_ints(db, "atk_type"), _cur_at)
                            edit_atk_type_sel = st.selectbox(
                                "atk_type (DB 목록)",
                                _edit_at_opts,
                                index=min(_edit_at_i, len(_edit_at_opts) - 1),
                                key="edit_atk_type_sel",
                                help=MH["atk_type"],
                            )
                            edit_atk_type_custom = _cur_at
                            if edit_atk_type_sel == CUSTOM_NUM_LABEL:
                                edit_atk_type_custom = st.number_input(
                                    "atk_type 직접 입력", value=_cur_at, key="edit_atk_type_custom"
                                )
                            edit_atk_type = resolve_int_selection(edit_atk_type_sel, edit_atk_type_custom)
                            _cur_ar = int(row.get("atk_range") or 0)
                            _edit_ar_opts, _edit_ar_i = int_field_options(distinct_monster_ints(db, "atk_range"), _cur_ar)
                            edit_atk_range_sel = st.selectbox(
                                "atk_range (DB 목록)",
                                _edit_ar_opts,
                                index=min(_edit_ar_i, len(_edit_ar_opts) - 1),
                                key="edit_atk_range_sel",
                                help=MH["atk_range"],
                            )
                            edit_atk_range_custom = _cur_ar
                            if edit_atk_range_sel == CUSTOM_NUM_LABEL:
                                edit_atk_range_custom = st.number_input(
                                    "atk_range 직접 입력", value=_cur_ar, key="edit_atk_range_custom"
                                )
                            edit_atk_range = resolve_int_selection(edit_atk_range_sel, edit_atk_range_custom)
                        with a2:
                            edit_atk_invis = st.selectbox("atk_invis", ["false", "true"], index=1 if str(row.get("atk_invis") or "").lower() == "true" else 0, key="edit_atk_invis", help=MH["atk_invis"])
                            edit_atk_poly = st.selectbox("atk_poly", ["false", "true"], index=1 if str(row.get("atk_poly") or "").lower() == "true" else 0, key="edit_atk_poly", help=MH["atk_poly"])
                        with a3:
                            edit_arrowGfx = st.number_input("arrowGfx", value=int(row.get("arrowGfx") or 0), min_value=0, key="edit_arrowGfx", help=MH["arrowGfx"])

                        st.markdown("**플래그 (true/false)**")
                        f1, f2, f3 = st.columns(3)
                        with f1:
                            edit_is_pickup = st.selectbox("is_pickup", ["false", "true"], index=1 if str(row.get("is_pickup") or "").lower() == "true" else 0, key="edit_pickup", help=MH["is_pickup"])
                            edit_is_revival = st.selectbox("is_revival", ["false", "true"], index=1 if str(row.get("is_revival") or "").lower() == "true" else 0, key="edit_revival", help=MH["is_revival"])
                            edit_is_toughskin = st.selectbox("is_toughskin", ["false", "true"], index=1 if str(row.get("is_toughskin") or "").lower() == "true" else 0, key="edit_toughskin", help=MH["is_toughskin"])
                        with f2:
                            edit_is_adendrop = st.selectbox("is_adendrop", ["false", "true"], index=1 if str(row.get("is_adendrop") or "").lower() == "true" else 0, key="edit_adendrop", help=MH["is_adendrop"])
                            edit_is_taming = st.selectbox("is_taming", ["false", "true"], index=1 if str(row.get("is_taming") or "").lower() == "true" else 0, key="edit_taming", help=MH["is_taming"])
                            edit_is_undead = st.selectbox("is_undead", ["false", "true"], index=1 if str(row.get("is_undead") or "").lower() == "true" else 0, key="edit_undead", help=MH["is_undead"])
                        with f3:
                            edit_is_turn_undead = st.selectbox("is_turn_undead", ["false", "true"], index=1 if str(row.get("is_turn_undead") or "").lower() == "true" else 0, key="edit_turn_undead", help=MH["is_turn_undead"])
                            edit_haste = st.selectbox("haste", ["false", "true"], index=1 if str(row.get("haste") or "").lower() == "true" else 0, key="edit_haste", help=MH["haste"])
                            edit_bravery = st.selectbox("bravery", ["false", "true"], index=1 if str(row.get("bravery") or "").lower() == "true" else 0, key="edit_bravery", help=MH["bravery"])

                        st.markdown("**속성 저항 (resistance)**")
                        r1, r2, r3, r4 = st.columns(4)
                        with r1: edit_res_earth = st.number_input("resistance_earth", value=int(row.get("resistance_earth") or 0), key="edit_res_earth", help=MH["resistance_earth"])
                        with r2: edit_res_fire = st.number_input("resistance_fire", value=int(row.get("resistance_fire") or 0), key="edit_res_fire", help=MH["resistance_fire"])
                        with r3: edit_res_wind = st.number_input("resistance_wind", value=int(row.get("resistance_wind") or 0), key="edit_res_wind", help=MH["resistance_wind"])
                        with r4: edit_res_water = st.number_input("resistance_water", value=int(row.get("resistance_water") or 0), key="edit_res_water", help=MH["resistance_water"])

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
                        st.caption("아데나: 최소~최대 드랍량. 주문서 등: 최소~최대 개수 (예: 1~1 = 1개만 드랍). 저장 후 **서버 리로드** 페이지에서 monster_drop 리로드를 실행하세요.")
                        drops = db.fetch_all(
                            "SELECT name, monster_name, item_name, item_bress, item_en, count_min, count_max, chance FROM monster_drop WHERE monster_name = %s ORDER BY item_name",
                            (selected,),
                        )
                        if drops:
                            st.markdown("**현재 드랍 목록**")
                            for d in drops:
                                item_name = d.get("item_name") or ""
                                cmin = d.get("count_min")
                                cmax = d.get("count_max")
                                ch = d.get("chance")
                                bress = d.get("item_bress") or 0
                                en = d.get("item_en") or 0
                                del_key = f"del_drop_{selected}_{item_name}_{bress}_{en}"
                                edit_key = f"edit_drop_{selected}_{item_name}_{bress}_{en}"
                                col1, col2 = st.columns([4, 1])
                                with col1:
                                    st.text(f"• {item_name}  |  최소 {cmin} ~ 최대 {cmax}  |  확률 {ch}%")
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
                                with st.expander(f"✏️ 수정: {item_name}", expanded=False):
                                    new_cmin = st.number_input("최소 수량", min_value=1, value=int(cmin) if cmin is not None else 1, key=f"ecmin_{edit_key}")
                                    new_cmax = st.number_input("최대 수량", min_value=1, value=int(cmax) if cmax is not None else 1, key=f"ecmax_{edit_key}")
                                    new_chance = st.number_input("드랍 확률 (%)", min_value=0, max_value=100, value=int(float(ch)) if ch is not None else 50, key=f"ech_{edit_key}")
                                    if st.button("수정 반영", key=f"apply_{edit_key}"):
                                        if new_cmin > new_cmax:
                                            st.warning("최소 수량이 최대 수량보다 클 수 없습니다.")
                                        else:
                                            ok, err = db.execute_query_ex(
                                                "UPDATE monster_drop SET count_min=%s, count_max=%s, chance=%s WHERE monster_name=%s AND item_name=%s AND item_bress=%s AND item_en=%s",
                                                (new_cmin, new_cmax, str(new_chance), selected, item_name, bress, en),
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
                            st.markdown("**수량·확률**")
                            c1, c2, c3 = st.columns(3)
                            with c1:
                                add_count_min = st.number_input("최소 수량 (아데나: 최소 금액, 주문서 등: 최소 개수)", min_value=1, value=1, key="drop_cmin")
                            with c2:
                                add_count_max = st.number_input("최대 수량 (아데나: 최대 금액, 주문서 등: 최대 개수)", min_value=1, value=1, key="drop_cmax")
                            with c3:
                                add_chance = st.number_input("드랍 확률 (%)", min_value=0, max_value=100, value=50, key="drop_chance")
                            if st.button("드랍 추가", key="drop_add_btn"):
                                if not add_item_name:
                                    st.warning("위에서 아이템 이름을 검색한 뒤, 추가할 아이템을 선택하세요.")
                                elif add_count_min > add_count_max:
                                    st.warning("최소 수량이 최대 수량보다 클 수 없습니다.")
                                else:
                                    name_val = add_item_name
                                    ok, err = db.execute_query_ex(
                                        "INSERT INTO monster_drop (name, monster_name, item_name, item_bress, item_en, count_min, count_max, chance) VALUES (%s, %s, %s, 0, 0, %s, %s, %s)",
                                        (name_val, selected, add_item_name, add_count_min, add_count_max, str(add_chance)),
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
