"""
리니지 싱글 서버 GM 툴 - 아이템 관리 페이지
아이템 지급, 인벤토리 조회/삭제
"""

import streamlit as st
import pandas as pd
from utils.db_manager import get_db
from utils.table_schemas import get_create_sql

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

# 검색 결과 세션에 유지 (탭 전환 시에도)
if "item_search_results" not in st.session_state:
    st.session_state["item_search_results"] = []
if "item_search_results_rows" not in st.session_state:
    st.session_state["item_search_results_rows"] = []
# 지급 성공 메시지 (rerun 후에도 보이도록)
if "item_grant_success" not in st.session_state:
    st.session_state["item_grant_success"] = None

# 아이템명으로 쓸 수 있는 컬럼 (대소문자 무관)
ITEM_NAME_COLUMN_LOWER = ("name", "item_name", "itemname")
# 아이템 검색에서 제외할 테이블 (캐릭터/계정 등)
EXCLUDE_TABLES = ("characters", "accounts")
# 검색 시 사용할 아이템 마스터 테이블 (일반 + 축복받은/저주받은 등)
ITEM_MASTER_TABLES = (
    "item", "etcitem", "weapon", "armor",
    "weapon_blessed", "armor_blessed", "etcitem_blessed",
    "weapon_cursed", "armor_cursed", "etcitem_cursed",
)
# 겹치지 않는 아이템(장비) = 무기/방어구 테이블 (일반·축복·저주)
EQUIPMENT_TABLES = (
    "weapon", "armor",
    "weapon_blessed", "armor_blessed",
    "weapon_cursed", "armor_cursed",
)

# 운영자 세트 아이템 (착용 시 GM 권한 부여) — 한 번에 지급용
OPERATOR_SET_ITEMS = [
    "오크족 망토",
    "오크족 투구",
    "오크족 고리 갑옷",
    "오크족 사슬 갑옷",
    "우럭하이 방패",
]


def _get_non_stackable_item_names(db):
    """
    장비(무기/방어구) = 겹치지 않는 아이템 이름 집합.
    인벤토리 표시용. 세션 캐시 사용.
    """
    cache_key = "item_non_stackable_set"
    if cache_key in st.session_state:
        return st.session_state[cache_key]
    table_cols = _get_item_tables_and_name_column(db)
    equipment_tables_lower = {t.lower() for t in EQUIPMENT_TABLES}
    names = set()
    for t, col in table_cols:
        if t.lower() in equipment_tables_lower:
            try:
                rows = db.fetch_all(f"SELECT `{col}` AS name FROM `{t}`")
                if rows:
                    for r in rows:
                        names.add(r["name"])
            except Exception:
                pass
    st.session_state[cache_key] = names
    return names


def _get_item_pile(db, item_name):
    """
    아이템 마스터 테이블의 '겹침' 컬럼 값을 조회.
    겹침=true → 겹치는 아이템(소비/재료) → quantity=1
    겹침=false → 겹치지 않는 아이템(장비) → quantity=0
    Returns: True(겹침), False(안 겹침), None(못 찾음).
    """
    if not item_name or not item_name.strip():
        return None
    name = item_name.strip()
    table_cols = _get_item_tables_and_name_column(db, item_master_only=False)
    for t, col in table_cols:
        try:
            row = db.fetch_one(
                f"SELECT `겹침` FROM `{t}` WHERE `{col}` = %s LIMIT 1",
                (name,),
            )
            if row and "겹침" in row:
                val = row["겹침"]
                if val is None:
                    return False
                if isinstance(val, (bool, int)):
                    return bool(val)
                return str(val).strip().lower() in ("true", "1", "y", "yes")
        except Exception:
            continue
    return None


def _is_equipment_item(db, item_name):
    """
    지급 시점에 해당 아이템이 장비 테이블(weapon/armor 등)에 있는지 직접 조회.
    '겹침' 컬럼이 없을 때만 사용하는 폴백.
    """
    if not item_name or not item_name.strip():
        return False
    table_cols = _get_item_tables_and_name_column(db)
    equipment_tables_lower = {t.lower() for t in EQUIPMENT_TABLES}
    for t, col in table_cols:
        if t.lower() not in equipment_tables_lower:
            continue
        try:
            row = db.fetch_one(
                f"SELECT 1 AS ok FROM `{t}` WHERE `{col}` = %s LIMIT 1",
                (item_name.strip(),),
            )
            if row:
                return True
        except Exception:
            pass
    return False


def _get_item_tables_and_name_column(db, item_master_only=False):
    """
    이름 컬럼(name/item_name 등)이 있는 테이블 목록 반환.
    item_master_only=True면 item, etcitem, weapon, armor만 사용(검색 중복 방지).
    Returns list of (table_name, column_name).
    """
    try:
        db_name = db.config["database"]
        c_placeholders = ", ".join(["%s"] * len(ITEM_NAME_COLUMN_LOWER))
        excl_placeholders = ", ".join(["%s"] * len(EXCLUDE_TABLES))
        sql = f"""
            SELECT c.TABLE_NAME, c.COLUMN_NAME
            FROM information_schema.COLUMNS c
            WHERE c.TABLE_SCHEMA = %s
              AND LOWER(c.COLUMN_NAME) IN ({c_placeholders})
              AND LOWER(c.TABLE_NAME) NOT IN ({excl_placeholders})
            ORDER BY c.TABLE_NAME, c.COLUMN_NAME
        """
        params = (db_name,) + tuple(ITEM_NAME_COLUMN_LOWER) + tuple(EXCLUDE_TABLES)
        rows = db.fetch_all(sql, params)
        if not rows:
            return []
        master_lower = {x.lower() for x in ITEM_MASTER_TABLES}
        seen = set()
        result = []
        for r in rows:
            t = r["TABLE_NAME"]
            if item_master_only and t.lower() not in master_lower:
                continue
            if t not in seen:
                seen.add(t)
                result.append((t, r["COLUMN_NAME"]))
        return result
    except Exception:
        return []


def _search_items_union(db, search_term, limit=50):
    """
    아이템 마스터 테이블(weapon, armor, etcitem, item)만 우선 검색.
    해당 테이블이 없으면 name 컬럼 있는 전체 테이블에서 검색(폴백).
    Returns (list of item names for selectbox, list of dict {name, tbl} for display, error_message).
    """
    table_cols = _get_item_tables_and_name_column(db, item_master_only=True)
    if not table_cols:
        table_cols = _get_item_tables_and_name_column(db, item_master_only=False)
    if not table_cols:
        return [], [], "아이템 테이블을 찾을 수 없습니다. DB에 name/item_name 컬럼이 있는 테이블이 없거나, characters/accounts만 있습니다."

    try:
        like_val = f"%{search_term}%"
        parts = []
        params_list = []
        for t, col in table_cols:
            parts.append(f"(SELECT `{col}` AS name, %s AS tbl FROM `{t}` WHERE `{col}` LIKE %s)")
            params_list.append(t)
            params_list.append(like_val)
        union_sql = " UNION ".join(parts) + " ORDER BY tbl, name LIMIT %s"
        params_list.append(limit)
        rows = db.fetch_all(union_sql, tuple(params_list))
        if not rows:
            return [], [], None
        # 이름 중복 제거하되 순서 유지 (선택용 리스트)
        seen = set()
        names = []
        for r in rows:
            n = r["name"]
            if n not in seen:
                seen.add(n)
                names.append(n)
        return names, rows, None
    except Exception as e:
        return [], [], str(e)


def _grant_operator_set(db, cha_name, cha_obj_id):
    """
    운영자 세트 5종을 해당 캐릭터에게 지급.
    Returns (success: bool, message: str).
    """
    failed = []
    delivery_ok = True
    try:
        with db.connection.cursor() as cursor:
            for item_name in OPERATOR_SET_ITEMS:
                pile = _get_item_pile(db, item_name)
                if pile is not None:
                    quantity = 1 if pile else 0
                else:
                    quantity = 0 if _is_equipment_item(db, item_name) else 1
                try:
                    cursor.execute(
                        "SELECT IFNULL(MAX(objId), 0) + 1 AS next_id FROM characters_inventory"
                    )
                    row = cursor.fetchone()
                    next_obj_id = row["next_id"] if row else 1
                    cursor.execute(
                        """INSERT INTO characters_inventory
                           (objId, cha_objId, cha_name, name, count, en, quantity, equipped)
                           VALUES (%s, %s, %s, %s, 1, 0, %s, 0)""",
                        (next_obj_id, cha_obj_id, cha_name, item_name, quantity),
                    )
                    try:
                        cursor.execute(
                            "INSERT INTO gm_item_delivery (cha_objId, objId, delivered) VALUES (%s, %s, 0)",
                            (cha_obj_id, next_obj_id),
                        )
                    except Exception:
                        delivery_ok = False
                except Exception as e:
                    failed.append(f"{item_name}: {e}")
        if failed:
            if db.connection:
                db.connection.rollback()
            return False, "; ".join(failed)
        db.connection.commit()
        return True, "no_delivery_table" if not delivery_ok else None
    except Exception as e:
        if db.connection:
            db.connection.rollback()
        return False, str(e)


def _grant_item(db, cha_name, cha_obj_id, item_name, count, en, force_equipment=False):
    """
    아이템 지급: INSERT.
    아이템 테이블의 '겹침' 컬럼으로 판단. 겹침=true → quantity=1, 겹침=false → quantity=0.
    '겹침'이 없으면 weapon/armor 테이블 여부 또는 force_equipment로 판단.
    """
    pile = _get_item_pile(db, item_name)
    if pile is not None:
        quantity = 1 if pile else 0  # 겹침=true → quantity 1, 겹침=false → quantity 0
    else:
        quantity = 0 if (force_equipment or _is_equipment_item(db, item_name)) else 1

    # 장비(quantity=0)는 겹치지 않으므로 개수만큼 각각 1개씩 INSERT. 소비/재료는 한 번에 count만큼.
    insert_count = count if quantity == 1 else 1
    insert_times = 1 if quantity == 1 else count

    try:
        with db.connection.cursor() as cursor:
            gm_delivery_ok = True
            for _ in range(insert_times):
                cursor.execute(
                    "SELECT IFNULL(MAX(objId), 0) + 1 AS next_id FROM characters_inventory"
                )
                row = cursor.fetchone()
                next_obj_id = row["next_id"] if row else 1
                cursor.execute(
                    """INSERT INTO characters_inventory
                       (objId, cha_objId, cha_name, name, count, en, quantity, equipped)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, 0)""",
                    (next_obj_id, cha_obj_id, cha_name, item_name, insert_count, en, quantity),
                )
                try:
                    cursor.execute(
                        "INSERT INTO gm_item_delivery (cha_objId, objId, delivered) VALUES (%s, %s, 0)",
                        (cha_obj_id, next_obj_id),
                    )
                except Exception:
                    gm_delivery_ok = False
        db.connection.commit()
        return (True, None) if gm_delivery_ok else (True, "no_delivery_table")
    except Exception as e:
        if db.connection:
            db.connection.rollback()
        return False, str(e)


def _delete_inventory_item(db, obj_id):
    """인벤토리 아이템 삭제. Returns (success, error_message)."""
    try:
        with db.connection.cursor() as cursor:
            cursor.execute(
                "DELETE FROM characters_inventory WHERE objId = %s", (obj_id,)
            )
        db.connection.commit()
        return True, None
    except Exception as e:
        if db.connection:
            db.connection.rollback()
        return False, str(e)


# 탭 구성
tab1, tab2 = st.tabs(["🎁 아이템 지급", "📦 인벤토리 조회"])

# ========== 탭 1: 아이템 지급 ==========
with tab1:
    st.subheader("아이템 지급")
    st.caption("캐릭터가 접속 중이어도 지급됩니다. 약 1~2초 안에 인벤에 자동 반영됩니다.")
    with st.expander("❓ 접속 중인데 지급이 안 될 때"):
        st.markdown("""
        1. **gm_item_delivery 테이블**이 있어야 합니다.  
           → 메인 화면 또는 **DB 관리** 페이지에서 **누락 테이블 자동 생성** 버튼을 눌러 주세요.
        2. **게임 서버를 재시작**했는지 확인하세요. (Java 수정 사항 반영)
        3. 위 두 가지를 한 뒤에도 안 되면, 캐릭터를 **한 번 로그아웃 후 재접속**하면 DB에 저장된 아이템이 인벤에 나타납니다.
        """)

    if st.session_state.get("item_grant_success"):
        st.success(st.session_state["item_grant_success"])
        st.session_state["item_grant_success"] = None
    if st.session_state.get("item_grant_warning") == "no_delivery_table":
        st.warning(
            "⚠️ 접속 중 반영용 테이블(gm_item_delivery)이 없습니다. "
            "아래 버튼으로 생성한 뒤 **게임 서버를 재시작**하세요. 재접속 시에는 인벤에 정상 표시됩니다."
        )
        if st.button("🔨 gm_item_delivery 테이블 지금 생성", key="create_gm_delivery_here"):
            sql = get_create_sql("gm_item_delivery")
            if sql:
                try:
                    ok = db.execute_query(sql)
                    if ok:
                        st.success("✅ gm_item_delivery 테이블이 생성되었습니다. 게임 서버를 재시작하면 접속 중 지급이 반영됩니다.")
                    else:
                        st.error("❌ 테이블 생성 실패. DB 관리 페이지에서 SQL 실행 탭으로 직접 CREATE 문을 실행해 보세요.")
                except Exception as e:
                    st.error(f"❌ 오류: {e}")
            else:
                st.error("테이블 스키마를 찾을 수 없습니다.")
        st.session_state["item_grant_warning"] = None

    char_list = db.fetch_all("SELECT name FROM characters ORDER BY name")
    if not char_list:
        st.info("캐릭터가 없습니다.")
    else:
        names = [r["name"] for r in char_list]
        selected_char = st.selectbox("캐릭터 선택", names, key="item_char")

        # 운영자 세트 일괄 지급 (기본 펼침으로 노출)
        st.markdown("---")
        st.markdown("#### 👑 운영자 세트 지급")
        with st.expander("운영자 세트 5종 한 번에 지급 (펼치기/접기)", expanded=True):
            st.caption("아래 5종 장비를 착용하면 게임 내 운영자(GM) 권한이 부여됩니다. 캐릭터 선택 후 버튼을 누르면 해당 캐릭터 인벤에 한 번에 지급됩니다.")
            for i, item in enumerate(OPERATOR_SET_ITEMS, 1):
                st.markdown(f"{i}. {item}")
            if st.button("운영자 세트 지급", key="grant_operator_set"):
                char_info = db.fetch_one(
                    "SELECT objID FROM characters WHERE name = %s", (selected_char,)
                )
                cha_obj_id = char_info.get("objID") or char_info.get("obj_id") if char_info else None
                if not cha_obj_id:
                    st.error("❌ 캐릭터 objID를 찾을 수 없습니다.")
                else:
                    ok, err = _grant_operator_set(db, selected_char, cha_obj_id)
                    if ok:
                        st.session_state["item_grant_success"] = "✅ 운영자 세트 5종이 지급되었습니다. 착용 후 운영자 권한이 적용됩니다."
                        if err == "no_delivery_table":
                            st.session_state["item_grant_warning"] = "no_delivery_table"
                        st.rerun()
                    else:
                        st.error(f"❌ 지급 실패: {err}")

        st.write("**아이템 검색**")
        search_term = st.text_input("아이템명 입력", placeholder="검색어 입력", key="item_search")
        if st.button("검색", key="item_search_btn"):
            if search_term.strip():
                names_found, rows_display, err = _search_items_union(db, search_term.strip(), limit=50)
                if err:
                    st.error(f"❌ 검색 오류: {err}")
                    st.session_state["item_search_results"] = []
                    st.session_state["item_search_results_rows"] = []
                else:
                    st.session_state["item_search_results"] = names_found
                    st.session_state["item_search_results_rows"] = rows_display or []
                    if not names_found:
                        st.warning("검색 결과가 없습니다.")
            else:
                st.session_state["item_search_results"] = []
                st.session_state["item_search_results_rows"] = []
                st.warning("검색어를 입력하세요.")

        if st.session_state["item_search_results_rows"]:
            df_display = pd.DataFrame(st.session_state["item_search_results_rows"])
            df_display = df_display.rename(columns={"tbl": "구분", "name": "아이템명"})
            df_display = df_display[["구분", "아이템명"]]
            st.dataframe(df_display, use_container_width=True, hide_index=True)
        elif st.session_state["item_search_results"]:
            st.dataframe(
                pd.DataFrame({"아이템명": st.session_state["item_search_results"]}),
                use_container_width=True,
                hide_index=True,
            )

        with st.expander("🔧 DB 테이블/컬럼 확인 (검색이 안 될 때)"):
            table_cols = _get_item_tables_and_name_column(db)
            if table_cols:
                st.caption("아이템 검색에 사용 중인 테이블:")
                st.write([f"{t}.{c}" for t, c in table_cols])
            else:
                st.caption("name/item_name 컬럼이 있는 테이블이 없습니다. 전체 테이블 목록:")
                all_tables = db.get_all_tables()
                st.write(all_tables)
                if all_tables:
                    sample = all_tables[0]
                    st.caption(f"예시: [{sample}] 컬럼 구조")
                    try:
                        cols = db.get_table_structure(sample)
                        if cols:
                            st.dataframe(pd.DataFrame(cols), use_container_width=True, hide_index=True)
                    except Exception:
                        pass

        st.write("**지급 정보**")
        selected_item = None
        if st.session_state["item_search_results"]:
            selected_item = st.selectbox(
                "선택한 아이템",
                st.session_state["item_search_results"],
                key="item_select",
            )
        else:
            st.caption("위에서 아이템을 검색한 뒤 선택하세요.")

        count = st.number_input("개수", min_value=1, max_value=999999, value=1, key="item_count")
        en = st.number_input("인챈트", min_value=0, max_value=10, value=0, key="item_en")
        force_equipment = st.checkbox(
            "장비로 지급 (게임에서 수량 표시 안 함, 무기/방어구용)",
            value=False,
            key="item_force_equipment",
        )
        st.caption("아이템 테이블에 '겹침' 컬럼이 있으면 자동으로 적용됩니다. 없을 때만 위 체크 또는 weapon/armor 테이블로 판단합니다.")

        if st.button("지급", key="item_grant_btn") and selected_char and selected_item:
            char_info = db.fetch_one(
                "SELECT objID FROM characters WHERE name = %s", (selected_char,)
            )
            cha_obj_id = None
            if char_info:
                cha_obj_id = char_info.get("objID") or char_info.get("obj_id")
            if not cha_obj_id:
                st.error("❌ 캐릭터 objID를 찾을 수 없습니다.")
            else:
                ok, err = _grant_item(
                    db, selected_char, cha_obj_id, selected_item, count, en,
                    force_equipment=force_equipment,
                )
                if ok:
                    st.session_state["item_grant_success"] = "✅ 아이템이 지급되었습니다."
                    if err == "no_delivery_table":
                        st.session_state["item_grant_warning"] = "no_delivery_table"
                    st.rerun()
                else:
                    st.error(f"❌ 지급 실패: {err}")

# ========== 탭 2: 인벤토리 조회 ==========
with tab2:
    st.subheader("인벤토리 조회")

    char_list2 = db.fetch_all("SELECT name FROM characters ORDER BY name")
    if not char_list2:
        st.info("캐릭터가 없습니다.")
    else:
        names2 = [r["name"] for r in char_list2]
        selected_char2 = st.selectbox("캐릭터 선택", names2, key="inv_char")

        if selected_char2:
            rows = db.fetch_all(
                """SELECT objId, name, count, en, equipped
                   FROM characters_inventory
                   WHERE cha_name = %s
                   ORDER BY objId DESC""",
                (selected_char2,),
            )
            if rows:
                # weapon, armor 테이블에서 장비 아이템명 목록 조회 (개수 없이 표시할 대상)
                weapon_names = set()
                armor_names = set()
                try:
                    for r in db.fetch_all("SELECT name FROM weapon"):
                        weapon_names.add(r["name"])
                except Exception:
                    pass
                try:
                    for r in db.fetch_all("SELECT name FROM armor"):
                        armor_names.add(r["name"])
                except Exception:
                    pass
                equipment_names = weapon_names | armor_names

                for r in rows:
                    if r["name"] in equipment_names:
                        r["display_name"] = r["name"]
                    else:
                        r["display_name"] = f"{r['name']} ({r['count']})"
                df = pd.DataFrame(rows)
                df = df.rename(
                    columns={
                        "objId": "아이템ID",
                        "display_name": "아이템명",
                        "count": "개수",
                        "en": "인챈트",
                        "equipped": "장착여부",
                    }
                )
                df = df.reindex(columns=["아이템ID", "아이템명", "개수", "인챈트", "장착여부"])
                st.dataframe(df, use_container_width=True, hide_index=True)
            else:
                st.info("인벤토리에 아이템이 없습니다.")

            st.write("**장비 (수량) 표시 수정**")
            st.caption("게임에서 장비가 '레이피어(1)'처럼 보이면, 아래 버튼으로 DB의 quantity를 0으로 바꿀 수 있습니다. 적용 후 재접속하면 됩니다.")
            if st.button("이 캐릭터 인벤의 장비 quantity → 0으로 수정", key="fix_equipment_qty"):
                weapon_names = set()
                armor_names = set()
                try:
                    for r in db.fetch_all("SELECT name FROM weapon"):
                        weapon_names.add(r["name"])
                except Exception:
                    pass
                try:
                    for r in db.fetch_all("SELECT name FROM armor"):
                        armor_names.add(r["name"])
                except Exception:
                    pass
                equipment_names = weapon_names | armor_names
                if not equipment_names:
                    st.warning("weapon/armor 테이블을 찾을 수 없습니다. 아래에 수동으로 아이템명을 입력해 수정하세요.")
                else:
                    try:
                        with db.connection.cursor() as cur:
                            for name in equipment_names:
                                cur.execute(
                                    "UPDATE characters_inventory SET quantity=0 WHERE cha_name=%s AND name=%s",
                                    (selected_char2, name),
                                )
                        db.connection.commit()
                        st.success("수정했습니다. 게임에서 재접속하면 장비에 (수량)이 안 붙습니다.")
                        st.rerun()
                    except Exception as e:
                        st.error(f"수정 실패: {e}")

            manual_names = st.text_input(
                "또는 수동 수정: 장비로 볼 아이템명 (쉼표 구분, 예: 레이피어, 진 레이피어)",
                key="manual_equipment_names",
                placeholder="레이피어, 상아탑 가죽 장갑",
            )
            if st.button("입력한 아이템 quantity → 0으로 수정", key="fix_manual_qty") and manual_names.strip():
                names_list = [n.strip() for n in manual_names.split(",") if n.strip()]
                if not names_list:
                    st.warning("아이템명을 입력하세요.")
                else:
                    try:
                        with db.connection.cursor() as cur:
                            for name in names_list:
                                cur.execute(
                                    "UPDATE characters_inventory SET quantity=0 WHERE cha_name=%s AND name=%s",
                                    (selected_char2, name),
                                )
                        db.connection.commit()
                        st.success(f"수정했습니다. ({', '.join(names_list)}) 재접속 후 반영됩니다.")
                        st.rerun()
                    except Exception as e:
                        st.error(f"수정 실패: {e}")

            st.write("**아이템 삭제**")
            del_obj_id = st.number_input(
                "삭제할 아이템 ID (objId)",
                min_value=0,
                value=0,
                key="del_obj_id",
            )
            if st.button("삭제", key="inv_del_btn"):
                if del_obj_id <= 0:
                    st.warning("유효한 objId를 입력하세요.")
                else:
                    ok, err = _delete_inventory_item(db, del_obj_id)
                    if ok:
                        st.success("✅ 삭제되었습니다.")
                        st.rerun()
                    else:
                        st.error(f"❌ 삭제 실패: {err}")
