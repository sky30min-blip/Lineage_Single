"""
NPC 관리 페이지 (NPC 목록, NPC 추가, NPC 삭제, 상점 관리)
- 상점: 리니지 서버 npc_shop / item 테이블 사용 (l1jdb)
"""
import streamlit as st
from utils.db_manager import get_db

st.set_page_config(page_title="NPC 관리", page_icon="🏰", layout="wide")
st.title("🏰 NPC 관리")

# 반영 버튼 누른 뒤 rerun 되어도 피드백 유지 (전체 툴 공통)
if "npc_page_feedback" in st.session_state:
    msg_type, msg_text = st.session_state["npc_page_feedback"]
    if msg_type == "success":
        st.success(msg_text)
    else:
        st.error(msg_text)
    del st.session_state["npc_page_feedback"]

db = get_db()
tab1, tab2, tab3, tab4 = st.tabs(["📋 NPC 목록", "➕ NPC 추가", "🗑️ NPC 삭제", "🏪 상점 관리"])

# ========== tab1: NPC 목록 ==========
with tab1:
    st.subheader("📋 NPC 목록")
    try:
        # 리니지 npc 테이블 컬럼: name, nameid, type, gfxid, gfxMode, hp 등 (npcid 없음)
        npc_list = db.fetch_all("SELECT name, nameid, type FROM npc ORDER BY name LIMIT 500")
        if npc_list:
            st.dataframe(npc_list, height=400, use_container_width=True)
        else:
            st.info("NPC 목록을 불러오지 못했거나 비어 있습니다.")
    except Exception as e:
        st.warning(f"NPC 테이블 조회 실패 (테이블/컬럼 확인): {e}")

# ========== tab2: NPC 추가 ==========
with tab2:
    st.subheader("➕ NPC 추가")
    st.caption("npc 테이블에 등록하고, 원하면 맵 좌표를 넣어 스폰까지 한 번에 등록할 수 있습니다. 서버 재시작 후 반영됩니다.")
    try:
        with st.form("npc_add_form"):
            # --- 기본 정보 (한글 설명은 라벨 옆 ? 에 마우스 올리면 표시) ---
            st.markdown("**기본 정보**")
            c1, c2 = st.columns(2)
            with c1:
                add_name = st.text_input(
                    "NPC 이름 *",
                    placeholder="예: 파워볼",
                    max_chars=64,
                    help="게임 내에서 참조하는 NPC 고유 이름. npc·npc_spawnlist에서 사용합니다."
                )
                add_type = st.text_input(
                    "타입 (type)",
                    value="default",
                    max_chars=32,
                    help="NPC 종류/클래스 구분. 서버 코드에서 특정 type일 때만 동작하는 NPC가 있습니다."
                )
                add_nameid = st.text_input(
                    "NAMEID (nameid)",
                    value="0",
                    placeholder="예: 50999 또는 $50999",
                    max_chars=32,
                    help="클라이언트 표시용 ID. 숫자 또는 $숫자 형식."
                )
                add_gfxid = st.number_input(
                    "그래픽 ID (gfxid)",
                    min_value=0,
                    value=0,
                    help="NPC 외형(모델) ID. 0이면 기본 모델."
                )
                add_gfxMode = st.number_input(
                    "그래픽 모드 (gfxMode)",
                    min_value=0,
                    value=0,
                    help="모델 애니메이션/모드. 보통 0."
                )
            with c2:
                add_hp = st.number_input(
                    "체력 (hp)",
                    min_value=1,
                    value=1,
                    help="NPC 체력. 1이면 즉사 불가(대화용) 등."
                )
                add_lawful = st.number_input(
                    "성향 (lawful)",
                    value=0,
                    help="성향 값. 0=중립 등. 일부 NPC는 이 값으로 대화 분기."
                )
                add_light = st.number_input(
                    "광원 (light)",
                    value=0,
                    help="주변 밝기/이펙트용. 보통 0."
                )
                add_ai = st.selectbox(
                    "AI 사용 (ai)",
                    ["false", "true"],
                    index=0,
                    help="true면 몬스터처럼 AI 동작. 대화/상점 NPC는 false."
                )
                add_areaatk = st.number_input(
                    "범위 공격 (areaatk)",
                    min_value=0,
                    value=0,
                    help="범위 공격 관련. 0=해당 없음."
                )
                add_arrowGfx = st.number_input(
                    "화살 그래픽 (arrowGfx)",
                    min_value=0,
                    value=0,
                    help="원거리 공격 시 화살 이펙트 ID. 0=없음."
                )
            # --- 스폰 위치 (맵에 배치) ---
            st.markdown("---")
            st.markdown("**스폰 위치 (맵에 배치)**")
            st.caption("채우면 npc_spawnlist에 등록되어, 서버 재시작 후 해당 좌표에 NPC가 나타납니다.")
            spawn_col1, spawn_col2, spawn_col3 = st.columns(3)
            with spawn_col1:
                add_locX = st.number_input("X 좌표 (locX)", value=33449, help="맵 내 X 좌표. 기란 예시: 33449")
                add_locY = st.number_input("Y 좌표 (locY)", value=32825, help="맵 내 Y 좌표. 기란 예시: 32825")
            with spawn_col2:
                add_locMap = st.number_input("맵 ID (locMap)", value=4, min_value=0, help="맵 번호. 4=기란 등.")
                add_heading = st.number_input("방향 (heading)", value=0, min_value=0, max_value=7, help="NPC가 바라보는 방향. 0~7")
            with spawn_col3:
                add_respawn = st.number_input("리스폰 (respawn)", value=0, min_value=0, help="0=사라지지 않음, 1 등이면 리스폰")
                add_title = st.text_input("말풍선 제목 (title)", value="", placeholder="NPC 머리 위에 표시될 이름", max_chars=64, help="비우면 npc 이름 사용")
            add_do_spawn = st.checkbox("이 좌표로 스폰 등록 (npc_spawnlist에 추가)", value=True, help="체크 시 NPC 추가 시 위 좌표로 스폰도 함께 등록")

            submitted = st.form_submit_button("NPC 추가")
            if submitted and add_name and add_name.strip():
                name = add_name.strip()
                ok = db.execute_query(
                    """INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    (name, add_type.strip() or "default", add_nameid.strip() or "0",
                     add_gfxid, add_gfxMode, add_hp, add_lawful, add_light, add_ai, add_areaatk, add_arrowGfx)
                )
                if ok:
                    msg = f"✅ 반영되었습니다. NPC '{name}' 추가됨."
                    if add_do_spawn:
                        spawn_key = (name.replace(" ", "_") + "_1")[:64]
                        title_val = (add_title.strip() or name)[:64]
                        ok2 = db.execute_query(
                            """INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
                               VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                               ON DUPLICATE KEY UPDATE npcName=%s, locX=%s, locY=%s, locMap=%s, heading=%s, respawn=%s, title=%s""",
                            (spawn_key, name, add_locX, add_locY, add_locMap, add_heading, add_respawn, title_val,
                             name, add_locX, add_locY, add_locMap, add_heading, add_respawn, title_val)
                        )
                        if ok2:
                            msg += f" 스폰 등록됨 (X={add_locX}, Y={add_locY}, 맵={add_locMap})."
                        else:
                            msg += " (스폰 등록 실패 시 npc_spawnlist 확인)"
                    msg += " 서버 재시작 후 반영됩니다."
                    st.session_state["npc_page_feedback"] = ("success", msg)
                    st.rerun()
                else:
                    st.session_state["npc_page_feedback"] = ("error", "❌ 추가 실패 (이미 같은 name이 있거나 DB 오류)")
                    st.rerun()
            elif submitted and not (add_name and add_name.strip()):
                st.warning("NPC 이름을 입력하세요.")
    except Exception as e:
        st.error(f"NPC 추가 오류: {e}")

# ========== tab3: NPC 삭제 (월드에서 제거 / 복구) ==========
with tab3:
    # rerun 후에도 피드백 유지 (삭제/복구 버튼 반응이 안 보이던 문제 해결)
    if "npc_tab3_feedback" in st.session_state:
        msg_type, msg_text = st.session_state["npc_tab3_feedback"]
        if msg_type == "success":
            st.success(msg_text)
        else:
            st.error(msg_text)
        del st.session_state["npc_tab3_feedback"]

    st.subheader("🗑️ NPC 삭제")
    st.caption("스폰 단위로 선택하면 **월드에서 즉시 사라집니다.** 복구하면 다시 나타납니다. (npc_spawnlist·npc 테이블은 삭제하지 않음)")
    try:
        # 스폰 목록: npc_spawnlist 기준 (서버가 name으로 디스폰/리스폰)
        spawn_list = db.fetch_all(
            "SELECT name, npcName, locX, locY, locMap FROM npc_spawnlist ORDER BY name LIMIT 500"
        )
        despawned_list = db.fetch_all(
            "SELECT spawn_name FROM gm_npc_despawned ORDER BY spawn_name"
        )
    except Exception as e:
        st.warning(f"테이블 조회 실패 (npc_spawnlist·gm_npc_despawned 확인): {e}")
        spawn_list = []
        despawned_list = []

    # 삭제 요청 직후 서버가 gm_npc_despawned 갱신 전이어도 복구 목록에 바로 표시
    if "npc_pending_despawn" not in st.session_state:
        st.session_state["npc_pending_despawn"] = []
    db_despawned_names = {r["spawn_name"] for r in despawned_list}
    pending = [p for p in st.session_state["npc_pending_despawn"] if p not in db_despawned_names]
    st.session_state["npc_pending_despawn"] = pending
    restore_choices_all = sorted(set([r["spawn_name"] for r in despawned_list]) | set(pending))

    if spawn_list:
        choices = [f"{r['name']} | {r.get('npcName','')} (X={r.get('locX')}, Y={r.get('locY')}, 맵={r.get('locMap')})" for r in spawn_list]
        spawn_keys = [r["name"] for r in spawn_list]
        idx = st.selectbox(
            "삭제할 스폰 선택 (월드에서 제거)",
            range(len(choices)),
            format_func=lambda i: choices[i],
            key="npc_del_select"
        )
        del_spawn_name = spawn_keys[idx] if idx is not None else None

        row1 = st.columns([1, 1])
        with row1[0]:
            if st.button("🗑️ 선택 스폰 삭제 (월드에서 제거)", key="npc_del_btn"):
                if del_spawn_name:
                    ok = db.execute_query(
                        "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                        ("npc_despawn", del_spawn_name)
                    )
                    if ok:
                        if del_spawn_name not in st.session_state["npc_pending_despawn"]:
                            st.session_state["npc_pending_despawn"].append(del_spawn_name)
                        st.session_state["npc_tab3_feedback"] = ("success", f"✅ 반영되었습니다. '{del_spawn_name}' 삭제 요청됨 — 서버가 처리하면 월드에서 사라집니다.")
                        st.rerun()
                    else:
                        st.session_state["npc_tab3_feedback"] = ("error", "❌ 명령 삽입 실패 (gm_server_command 테이블·param 길이 확인)")
                        st.rerun()
                else:
                    st.warning("스폰을 선택하세요.")
        with row1[1]:
            pass  # 복구는 아래 블록에

        st.divider()
        st.markdown("**복구** — 월드에서 제거했던 NPC를 다시 스폰합니다.")
        st.caption("서버가 삭제/복구 명령을 처리하면 DB가 갱신됩니다. 목록을 최신으로 보려면 **목록 새로고침**을 누르세요.")
        if st.button("🔄 목록 새로고침", key="npc_tab3_refresh"):
            st.rerun()
        if restore_choices_all:
            restore_name = st.selectbox("복구할 스폰 선택", restore_choices_all, key="npc_restore_select")
            if st.button("🔄 복구", key="npc_restore_btn"):
                ok = db.execute_query(
                    "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                    ("npc_respawn", restore_name)
                )
                if ok:
                    if restore_name in st.session_state["npc_pending_despawn"]:
                        st.session_state["npc_pending_despawn"] = [p for p in st.session_state["npc_pending_despawn"] if p != restore_name]
                    st.session_state["npc_tab3_feedback"] = ("success", f"✅ 반영되었습니다. '{restore_name}' 복구 요청됨 — 서버가 처리하면 월드에 다시 나타납니다.")
                    st.rerun()
                else:
                    st.session_state["npc_tab3_feedback"] = ("error", "❌ 명령 삽입 실패")
                    st.rerun()
        else:
            st.caption("현재 디스폰된 스폰이 없습니다. 위에서 삭제한 스폰만 여기 목록에 표시됩니다.")
    else:
        st.info("npc_spawnlist에 스폰이 없거나 조회할 수 없습니다.")

# ========== tab4: 상점 관리 (npc_shop / item) ==========
with tab4:
    st.subheader("🏪 NPC 상점 관리")
    try:
        # 상점이 있는 NPC 목록 (npc_shop.name 기준)
        상점NPC목록 = db.fetch_all(
            "SELECT DISTINCT name FROM npc_shop ORDER BY name"
        )
        if not 상점NPC목록:
            st.info("등록된 상점 NPC가 없습니다. npc_shop 테이블에 데이터를 추가하세요.")
        else:
            선택목록 = [row["name"] for row in 상점NPC목록]
            선택이름 = st.selectbox("상점 NPC 선택", 선택목록, key="shop_npc_select")
            npc_name = 선택이름

            col1, col2 = st.columns(2)

            # 왼쪽: 판매 물품 (NPC가 파는 것 = buy='true')
            with col1:
                st.markdown("#### 💰 판매 물품 (플레이어 구매)")
                판매목록 = db.fetch_all(
                    """SELECT name, itemname, itemcount, price, sell, buy 
                       FROM npc_shop 
                       WHERE name = %s AND buy = 'true' 
                       ORDER BY itemname""",
                    (npc_name,)
                )
                if 판매목록:
                    st.dataframe(판매목록, height=300, use_container_width=True)
                else:
                    st.caption("판매 물품 없음")

                with st.expander("➕ 판매 물품 추가"):
                    아이템검색 = st.text_input("아이템 이름 검색", key="sell_search")
                    if 아이템검색:
                        아이템목록 = db.fetch_all(
                            "SELECT `아이템이름` FROM item WHERE `아이템이름` LIKE CONCAT('%', %s, '%') LIMIT 20",
                            (아이템검색,)
                        )
                        선택아이템 = st.selectbox(
                            "아이템",
                            아이템목록 if 아이템목록 else [],
                            format_func=lambda x: x.get("아이템이름", ""),
                            key="sell_item"
                        )
                    else:
                        선택아이템 = None
                    판매가격 = st.number_input("판매 가격", min_value=0, value=0, key="sell_price")
                    수량 = st.number_input("수량", min_value=1, value=1, key="sell_count")
                    if st.button("판매 물품 추가", key="add_sell"):
                        if 선택아이템:
                            itemname = 선택아이템.get("아이템이름")
                            ok = db.execute_query(
                                """INSERT INTO npc_shop 
                                   (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type) 
                                   VALUES (%s, %s, %s, 1, 0, 0, 'false', 'true', 'false', %s, '')""",
                                (npc_name, itemname, 수량, 판매가격)
                            )
                            if ok:
                                st.session_state["npc_page_feedback"] = ("success", "✅ 반영되었습니다. 판매 물품이 추가되었습니다.")
                                st.rerun()
                            else:
                                st.session_state["npc_page_feedback"] = ("error", "❌ 추가 실패 (중복 또는 제약조건 확인)")
                                st.rerun()
                        else:
                            st.warning("아이템을 선택하세요.")

                with st.expander("🗑️ 판매 물품 삭제"):
                    if 판매목록:
                        삭제선택 = st.multiselect(
                            "삭제할 물품",
                            판매목록,
                            format_func=lambda x: f"{x.get('itemname','')} - {x.get('price',0)} 아데나",
                            key="del_sell"
                        )
                        if st.button("판매 물품 삭제", key="del_sell_btn") and 삭제선택:
                            for x in 삭제선택:
                                db.execute_query(
                                    "DELETE FROM npc_shop WHERE name = %s AND itemname = %s AND buy = 'true'",
                                    (npc_name, x.get("itemname"))
                                )
                            st.session_state["npc_page_feedback"] = ("success", f"✅ 반영되었습니다. 판매 물품 {len(삭제선택)}개 삭제 완료.")
                            st.rerun()
                    else:
                        st.caption("삭제할 판매 물품이 없습니다.")

            # 오른쪽: 매입 물품 (NPC가 사는 것 = sell='true')
            with col2:
                st.markdown("#### 💵 매입 물품 (플레이어 판매)")
                매입목록 = db.fetch_all(
                    """SELECT name, itemname, itemcount, price, sell, buy 
                       FROM npc_shop 
                       WHERE name = %s AND sell = 'true' 
                       ORDER BY itemname""",
                    (npc_name,)
                )
                if 매입목록:
                    st.dataframe(매입목록, height=300, use_container_width=True)
                else:
                    st.caption("매입 물품 없음")

                with st.expander("➕ 매입 물품 추가"):
                    아이템검색2 = st.text_input("아이템 이름 검색", key="buy_search")
                    if 아이템검색2:
                        아이템목록2 = db.fetch_all(
                            "SELECT `아이템이름` FROM item WHERE `아이템이름` LIKE CONCAT('%', %s, '%') LIMIT 20",
                            (아이템검색2,)
                        )
                        선택아이템2 = st.selectbox(
                            "아이템",
                            아이템목록2 if 아이템목록2 else [],
                            format_func=lambda x: x.get("아이템이름", ""),
                            key="buy_item"
                        )
                    else:
                        선택아이템2 = None
                    매입가격 = st.number_input("매입 가격", min_value=0, value=0, key="buy_price")
                    if st.button("매입 물품 추가", key="add_buy"):
                        if 선택아이템2:
                            itemname = 선택아이템2.get("아이템이름")
                            ok = db.execute_query(
                                """INSERT INTO npc_shop 
                                   (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type) 
                                   VALUES (%s, %s, 1, 1, 0, 0, 'true', 'false', 'false', %s, '')""",
                                (npc_name, itemname, 매입가격)
                            )
                            if ok:
                                st.session_state["npc_page_feedback"] = ("success", "✅ 반영되었습니다. 매입 물품이 추가되었습니다.")
                                st.rerun()
                            else:
                                st.session_state["npc_page_feedback"] = ("error", "❌ 추가 실패")
                                st.rerun()
                        else:
                            st.warning("아이템을 선택하세요.")

                with st.expander("🗑️ 매입 물품 삭제"):
                    if 매입목록:
                        삭제매입 = st.multiselect(
                            "삭제할 물품",
                            매입목록,
                            format_func=lambda x: f"{x.get('itemname','')} - {x.get('price',0)} 아데나",
                            key="del_buy"
                        )
                        if st.button("매입 물품 삭제", key="del_buy_btn") and 삭제매입:
                            for x in 삭제매입:
                                db.execute_query(
                                    "DELETE FROM npc_shop WHERE name = %s AND itemname = %s AND sell = 'true'",
                                    (npc_name, x.get("itemname"))
                                )
                            st.session_state["npc_page_feedback"] = ("success", f"✅ 반영되었습니다. 매입 물품 {len(삭제매입)}개 삭제 완료.")
                            st.rerun()
                    else:
                        st.caption("삭제할 매입 물품이 없습니다.")

    except Exception as e:
        st.error(f"상점 조회/수정 중 오류: {e}")
        st.caption("npc_shop, item 테이블이 l1jdb에 있는지 확인하세요.")
