"""
리니지 싱글 서버 GM 툴 - 캐릭터 관리 페이지
캐릭터 목록, 수정, 아데나, 위치 이동
"""

import streamlit as st
import pandas as pd
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
import config

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()
show_pending_feedback()

# 탭 구성
tab1, tab2, tab3, tab4 = st.tabs([
    "📋 캐릭터 목록",
    "✏️ 캐릭터 수정",
    "💰 아데나 관리",
    "📍 위치 이동",
])

# ========== 탭 1: 캐릭터 목록 ==========
with tab1:
    st.subheader("캐릭터 목록")
    rows = db.fetch_all(
        "SELECT name, level, class, account FROM characters ORDER BY level DESC"
    )
    if rows:
        df = pd.DataFrame(rows)
        df["class"] = df["class"].map(
            lambda x: config.CLASS_NAMES.get(int(x) if x is not None else 0, str(x))
        )
        df = df.rename(columns={"name": "캐릭터명", "level": "레벨", "class": "직업", "account": "계정"})
        st.dataframe(df)
        st.caption(f"총 {len(df)}명")
    else:
        st.info("캐릭터가 없습니다.")

# ========== 탭 2: 캐릭터 수정 ==========
with tab2:
    st.subheader("캐릭터 수정")

    char_list = db.fetch_all("SELECT name FROM characters ORDER BY name")
    if not char_list:
        st.info("캐릭터가 없습니다.")
    else:
        names = [r["name"] for r in char_list]
        selected_name = st.selectbox("캐릭터 선택", names, key="edit_char")

        if selected_name:
            char = db.fetch_one(
                "SELECT * FROM characters WHERE name = %s", (selected_name,)
            )
            if not char:
                st.warning("캐릭터 정보를 불러올 수 없습니다.")
            else:
                st.caption(f"현재 레벨: {char.get('level')} | HP: {char.get('nowHP')}/{char.get('maxHP')} | MP: {char.get('nowMP')}/{char.get('maxMP')}")

                col1, col2 = st.columns(2)
                with col1:
                    level = st.number_input("레벨", min_value=1, max_value=99, value=int(char.get("level") or 1), key="edit_level")
                    st.write("**스탯**")
                    str_val = st.number_input("str", min_value=1, max_value=99, value=int(char.get("str") or 1), key="edit_str")
                    dex_val = st.number_input("dex", min_value=1, max_value=99, value=int(char.get("dex") or 1), key="edit_dex")
                    con_val = st.number_input("con", min_value=1, max_value=99, value=int(char.get("con") or 1), key="edit_con")
                    wis_val = st.number_input("wis", min_value=1, max_value=99, value=int(char.get("wis") or 1), key="edit_wis")
                    inter_val = st.number_input("inter", min_value=1, max_value=99, value=int(char.get("inter") or 1), key="edit_inter")
                    cha_val = st.number_input("cha", min_value=1, max_value=99, value=int(char.get("cha") or 1), key="edit_cha")
                with col2:
                    st.write("**HP / MP**")
                    nowHP = st.number_input("nowHP", min_value=0, value=int(char.get("nowHP") or 0), key="edit_nowHP")
                    maxHP = st.number_input("maxHP", min_value=1, value=int(char.get("maxHP") or 1), key="edit_maxHP")
                    nowMP = st.number_input("nowMP", min_value=0, value=int(char.get("nowMP") or 0), key="edit_nowMP")
                    maxMP = st.number_input("maxMP", min_value=0, value=int(char.get("maxMP") or 0), key="edit_maxMP")

                if st.button("저장", key="save_char"):
                    ok, err = db.execute_query_ex(
                        """UPDATE characters SET
                           level=%s, str=%s, dex=%s, con=%s, wis=%s, inter=%s, cha=%s,
                           nowHP=%s, maxHP=%s, nowMP=%s, maxMP=%s
                           WHERE name=%s""",
                        (level, str_val, dex_val, con_val, wis_val, inter_val, cha_val,
                         nowHP, maxHP, nowMP, maxMP, selected_name),
                    )
                    if ok:
                        queue_feedback("success", "✅ 캐릭터 정보가 저장되었습니다.")
                        st.rerun()
                    else:
                        st.error(f"❌ 저장 실패: {err}")

def _resolve_char_obj_id(char_row):
    """characters 행에서 objID 컬럼명 대소문자/별칭 차이 흡수 (PyMySQL·DB 설정별)."""
    if not char_row:
        return None
    for key in ("objID", "objId", "obj_id", "OBJID"):
        v = char_row.get(key)
        if v is not None:
            try:
                return int(v)
            except (TypeError, ValueError):
                continue
    for k, v in char_row.items():
        if k and str(k).lower() == "objid" and v is not None:
            try:
                return int(v)
            except (TypeError, ValueError):
                continue
    return None


def _adena_upsert(db, cha_name, cha_obj_id, new_count):
    """
    아데나 행이 있으면 UPDATE, 없으면 INSERT.
    Returns (success, error_message). error_message는 None이면 성공.
    에러 시 구체적인 예외 메시지를 반환하기 위해 connection 직접 사용.
    """
    try:
        adena_row = db.fetch_one(
            """SELECT count FROM characters_inventory
               WHERE cha_name = %s AND (name LIKE %s OR name LIKE %s) LIMIT 1""",
            (cha_name, "%아데나%", "%adena%"),
        )
        with db.connection.cursor() as cursor:
            if adena_row:
                cursor.execute(
                    """UPDATE characters_inventory SET count = %s
                       WHERE cha_name = %s AND (name LIKE %s OR name LIKE %s)""",
                    (new_count, cha_name, "%아데나%", "%adena%"),
                )
            else:
                if cha_obj_id is None:
                    return False, "캐릭터 objID를 찾을 수 없습니다."
                # objId 컬럼 필수: 테이블 내 고유 ID로 MAX+1 사용
                cursor.execute("SELECT IFNULL(MAX(objId), 0) + 1 AS next_id FROM characters_inventory")
                row = cursor.fetchone()
                next_obj_id = row["next_id"] if row else 1
                cursor.execute(
                    """INSERT INTO characters_inventory
                       (objId, cha_objId, cha_name, name, count, quantity, equipped)
                       VALUES (%s, %s, %s, '아데나', %s, 1, 0)""",
                    (next_obj_id, cha_obj_id, cha_name, new_count),
                )
        db.connection.commit()
        return True, None
    except Exception as e:
        if db.connection:
            db.connection.rollback()
        return False, str(e)


def _gm_adena_delivery_insert(db, cha_obj_id, new_count):
    """접속 중인 캐릭터에게 아데나 즉시 반영용 테이블에 삽입. (실패 시 오류 문자열 반환)"""
    if cha_obj_id is None:
        return None
    _ok, err = db.execute_query_ex(
        "INSERT INTO gm_adena_delivery (cha_objId, new_count, delivered) VALUES (%s, %s, 0)",
        (cha_obj_id, new_count),
    )
    return err if not _ok else None


def _gm_location_delivery_insert(db, cha_obj_id, loc_x, loc_y, loc_map):
    """접속 중인 캐릭터에게 좌표 이동 즉시 반영용 테이블에 삽입."""
    if cha_obj_id is None:
        return None
    _ok, err = db.execute_query_ex(
        "INSERT INTO gm_location_delivery (cha_objId, locX, locY, locMAP, delivered) VALUES (%s, %s, %s, %s, 0)",
        (cha_obj_id, loc_x, loc_y, loc_map),
    )
    return err if not _ok else None


# ========== 탭 3: 아데나 관리 ==========
with tab3:
    st.subheader("아데나 관리")

    char_list3 = db.fetch_all("SELECT name FROM characters ORDER BY name")
    if not char_list3:
        st.info("캐릭터가 없습니다.")
    else:
        names3 = [r["name"] for r in char_list3]
        selected_name3 = st.selectbox("캐릭터 선택", names3, key="adena_char")

        if selected_name3:
            # 1. 아데나 행 존재 여부 확인
            adena_row = db.fetch_one(
                """SELECT count FROM characters_inventory
                   WHERE cha_name = %s AND (name LIKE %s OR name LIKE %s) LIMIT 1""",
                (selected_name3, "%아데나%", "%adena%"),
            )
            current_adena = int(adena_row["count"]) if adena_row else 0

            # cha_objId (characters.objID) - INSERT 시 사용
            char_info = db.fetch_one("SELECT objID FROM characters WHERE name = %s", (selected_name3,))
            cha_obj_id = _resolve_char_obj_id(char_info)

            st.metric("현재 아데나", f"{current_adena:,}")

            amount = st.number_input("금액", min_value=0, value=0, key="adena_amount")

            col1, col2, col3 = st.columns(3)
            with col1:
                if st.button("지급", key="adena_add"):
                    new_count = current_adena + amount
                    ok, err = _adena_upsert(db, selected_name3, cha_obj_id, new_count)
                    if ok:
                        derr = _gm_adena_delivery_insert(db, cha_obj_id, new_count)
                        msg = "✅ 아데나가 지급되었습니다. (DB 반영 완료)"
                        if derr:
                            msg += f" — ⚠️ 즉시반영 큐 실패: {derr}"
                        queue_feedback("success" if not derr else "warning", msg)
                        st.rerun()
                    else:
                        st.error(f"❌ 지급 실패: {err}")
            with col2:
                if st.button("차감", key="adena_sub"):
                    new_count = max(0, current_adena - amount)
                    ok, err = _adena_upsert(db, selected_name3, cha_obj_id, new_count)
                    if ok:
                        derr = _gm_adena_delivery_insert(db, cha_obj_id, new_count)
                        msg = "✅ 아데나가 차감되었습니다. (DB 반영 완료)"
                        if derr:
                            msg += f" — ⚠️ 즉시반영 큐 실패: {derr}"
                        queue_feedback("success" if not derr else "warning", msg)
                        st.rerun()
                    else:
                        st.error(f"❌ 차감 실패: {err}")
            with col3:
                if st.button("설정", key="adena_set"):
                    new_count = amount
                    ok, err = _adena_upsert(db, selected_name3, cha_obj_id, new_count)
                    if ok:
                        derr = _gm_adena_delivery_insert(db, cha_obj_id, new_count)
                        msg = "✅ 아데나 수량이 설정되었습니다. (DB 반영 완료)"
                        if derr:
                            msg += f" — ⚠️ 즉시반영 큐 실패: {derr}"
                        queue_feedback("success" if not derr else "warning", msg)
                        st.rerun()
                    else:
                        st.error(f"❌ 설정 실패: {err}")

# ========== 탭 4: 위치 이동 ==========
with tab4:
    st.subheader("위치 이동")

    char_list4 = db.fetch_all("SELECT name FROM characters ORDER BY name")
    if not char_list4:
        st.info("캐릭터가 없습니다.")
    else:
        names4 = [r["name"] for r in char_list4]
        selected_name4 = st.selectbox("캐릭터 선택", names4, key="loc_char")

        if selected_name4:
            loc = db.fetch_one(
                "SELECT locX, locY, locMAP FROM characters WHERE name = %s",
                (selected_name4,),
            )
            if loc:
                st.caption(f"현재 위치: X={loc.get('locX')}, Y={loc.get('locY')}, MAP={loc.get('locMAP')}")

            # 좌표 이동 시 접속 중 즉시 반영용 cha_objId
            char_info4 = db.fetch_one("SELECT objID FROM characters WHERE name = %s", (selected_name4,))
            cha_obj_id_loc = _resolve_char_obj_id(char_info4)

            st.write("**방법 1: 주요 마을로 이동**")
            town_names = list(config.TOWN_COORDINATES.keys())
            town = st.selectbox("마을 선택", town_names, key="town_select")
            if st.button("마을로 이동", key="move_town"):
                co = config.TOWN_COORDINATES[town]
                ok, err = db.execute_query_ex(
                    "UPDATE characters SET locX=%s, locY=%s, locMAP=%s WHERE name=%s",
                    (co["x"], co["y"], co["map_id"], selected_name4),
                )
                if ok:
                    derr = _gm_location_delivery_insert(db, cha_obj_id_loc, co["x"], co["y"], co["map_id"])
                    msg = "✅ 좌표가 마을로 변경되었습니다. (DB 반영 완료)"
                    if derr:
                        msg += f" — ⚠️ 즉시반영 큐 실패: {derr}"
                    queue_feedback("success" if not derr else "warning", msg)
                    st.rerun()
                else:
                    st.error(f"❌ 이동 처리 실패: {err}")

            st.divider()
            st.write("**방법 2: 좌표 직접 입력**")
            locX = st.number_input("locX", value=int(loc.get("locX", 0)) if loc else 0, key="input_locX")
            locY = st.number_input("locY", value=int(loc.get("locY", 0)) if loc else 0, key="input_locY")
            locMAP = st.number_input("locMAP", value=int(loc.get("locMAP", 0)) if loc else 0, key="input_locMAP")
            if st.button("좌표로 이동", key="move_xy"):
                ok, err = db.execute_query_ex(
                    "UPDATE characters SET locX=%s, locY=%s, locMAP=%s WHERE name=%s",
                    (locX, locY, locMAP, selected_name4),
                )
                if ok:
                    derr = _gm_location_delivery_insert(db, cha_obj_id_loc, locX, locY, locMAP)
                    msg = "✅ 위치가 변경되었습니다. (DB 반영 완료)"
                    if derr:
                        msg += f" — ⚠️ 즉시반영 큐 실패: {derr}"
                    queue_feedback("success" if not derr else "warning", msg)
                    st.rerun()
                else:
                    st.error(f"❌ 이동 처리 실패: {err}")
