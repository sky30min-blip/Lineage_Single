import streamlit as st
from utils.db_manager import get_db

st.set_page_config(page_title="아이템 관리", page_icon="💎", layout="wide")
st.title("💎 아이템 관리")

db = get_db()

tab1, tab2, tab3 = st.tabs(["🔍 아이템 조회", "✏️ 아이템 수정", "➕ 아이템 추가"])

with tab1:
    st.subheader("🔍 아이템 검색")

    # 연결·테이블 확인용 (에러 시 원인 파악)
    with st.expander("🔧 DB 연결 및 item 테이블 확인"):
        try:
            rows = db.fetch_all("SELECT COUNT(*) AS c FROM item", ())
            if rows and len(rows) > 0:
                cnt = rows[0].get('c', rows[0].get('C', 0))
                st.success(f"item 테이블 행 수: **{cnt}**")
                if cnt == 0:
                    st.warning("item 테이블이 비어 있습니다.")
            else:
                st.warning("COUNT 조회 결과 없음")
        except Exception as e:
            st.error(f"조회 실패 (컬럼명이 다를 수 있음): {e}")
        # 실제 컬럼명 확인용: 1행만 조회해 키 목록 표시
        try:
            sample = db.fetch_all("SELECT * FROM item LIMIT 1", ())
            if sample and len(sample) > 0:
                st.caption("현재 item 테이블 컬럼 예시: " + ", ".join(list(sample[0].keys())[:15]) + (" ..." if len(sample[0]) > 15 else ""))
        except Exception:
            pass
    
    검색어 = st.text_input("아이템 이름 검색", placeholder="예: 다마스커스, 홀 쿠폰, 축복, 저주, 무기 마법 주문서")
    st.caption("💡 **축복**, **저주** 검색 시 '축복 받은/저주 받은 무기·갑옷 마법 주문서' 등도 함께 표시됩니다. 이름·구분1·구분2·NAMEID 모두 검색합니다.")
    
    if 검색어:
        검색어_strip = 검색어.strip()
        like_pattern = f"%{검색어_strip}%"
        # 축복/저주 검색 시 '무기 마법 주문서', '갑옷 마법 주문서' 등 주문서 계열도 함께 노출 (DB에 풀네임이 없을 수 있음)
        보조_축저주 = 검색어_strip in ("축복", "저주")
        try:
            if 보조_축저주:
                결과 = db.fetch_all("""
                    SELECT 
                        `아이템이름`, `구분1`, `구분2`, NAMEID, `재질`, `무게`,
                        level_min, level_max, `작은 몬스터`, `큰 몬스터`, ac,
                        `공격성공율`, `추가타격치`, `군주`, `기사`, `요정`, `마법사`, `다크엘프`,
                        `용기사`, `환술사`,
                        `거래`, `드랍`, `겹침`, `판매`, `창고`, `창고_혈맹`, `창고_요숲`, `현금거래`, `손상`,
                        `인첸트`, `안전인첸트`, `최고인챈`,
                        `attribute_crystal`, `HP증가`, `MP증가`, `MR증가`, `SP증가`,
                        waterress, windress, earthress, fireress, `인벤ID`, GFXID, `이펙트ID`, delay
                    FROM item 
                    WHERE (
                        `아이템이름` LIKE %s OR `구분1` LIKE %s OR `구분2` LIKE %s OR NAMEID LIKE %s
                    ) OR (
                        (`아이템이름` LIKE %s AND (`아이템이름` LIKE %s OR `아이템이름` LIKE %s))
                    )
                    ORDER BY `아이템이름`
                    LIMIT 80
                """, (like_pattern, like_pattern, like_pattern, like_pattern, "%주문서%", "%무기%", "%갑옷%"))
            else:
                결과 = db.fetch_all("""
                    SELECT 
                        `아이템이름`, `구분1`, `구분2`, NAMEID, `재질`, `무게`,
                        level_min, level_max, `작은 몬스터`, `큰 몬스터`, ac,
                        `공격성공율`, `추가타격치`, `군주`, `기사`, `요정`, `마법사`, `다크엘프`,
                        `용기사`, `환술사`,
                        `거래`, `드랍`, `겹침`, `판매`, `창고`, `창고_혈맹`, `창고_요숲`, `현금거래`, `손상`,
                        `인첸트`, `안전인첸트`, `최고인챈`,
                        `attribute_crystal`, `HP증가`, `MP증가`, `MR증가`, `SP증가`,
                        waterress, windress, earthress, fireress, `인벤ID`, GFXID, `이펙트ID`, delay
                    FROM item 
                    WHERE `아이템이름` LIKE %s OR `구분1` LIKE %s OR `구분2` LIKE %s OR NAMEID LIKE %s
                    ORDER BY `아이템이름`
                    LIMIT 50
                """, (like_pattern, like_pattern, like_pattern, like_pattern))
        except Exception as e:
            st.error(f"검색 쿼리 오류: {e}")
            결과 = None
        
        if 결과 and len(결과) > 0:
            선택 = st.selectbox("수정할 아이템 선택", 결과, format_func=lambda x: f"{x.get('아이템이름', x)}")
            
            with st.expander("📋 상세 정보", expanded=True):
                col1, col2, col3 = st.columns(3)
                with col1:
                    st.write(f"**이름**: {선택.get('아이템이름', '-')}")
                    st.write(f"**인벤ID**: {선택.get('인벤ID', '-')}")
                    st.write(f"**GFXID**: {선택.get('GFXID', '-')}")
                    st.write(f"**구분1/구분2**: {선택.get('구분1', '')} / {선택.get('구분2', '')}")
                    st.write(f"**인챈트**: {선택.get('인첸트', '')} | **안전인첸트**: {선택.get('안전인첸트', '')} | **최고인챈**: {선택.get('최고인챈', '')}")
                    st.write(f"**속성(attribute_crystal)**: {선택.get('attribute_crystal', 'none')}")
                with col2:
                    st.write(f"**재질**: {선택.get('재질', '')}")
                    st.write(f"**무게**: {선택.get('무게', '')}")
                    st.write(f"**레벨**: {선택.get('level_min', 0)}-{선택.get('level_max', 0)}")
                    st.write(f"**NAMEID**: {선택.get('NAMEID', '')}")
                    st.write(f"**HP/MP/MR/SP증가**: {선택.get('HP증가', 0)}/{선택.get('MP증가', 0)}/{선택.get('MR증가', 0)}/{선택.get('SP증가', 0)}")
                with col3:
                    st.write(f"**AC**: {선택.get('ac', 0)}")
                    st.write(f"**데미지(소/대)**: {선택.get('작은 몬스터', 0)}/{선택.get('큰 몬스터', 0)}")
                    st.write(f"**명중**: {선택.get('공격성공율', 0)}")
                    st.write(f"**추뎀**: {선택.get('추가타격치', 0)}")
                    st.write(f"**속성저항(수/풍/지/화)**: {선택.get('waterress', 0)}/{선택.get('windress', 0)}/{선택.get('earthress', 0)}/{선택.get('fireress', 0)}")
            
            if st.button("✏️ 이 아이템 수정하기"):
                st.session_state['edit_item'] = 선택
                st.rerun()
        else:
            if 결과 is not None:
                st.info("검색 결과가 없습니다. 다른 검색어로 시도하거나, 위 'DB 연결 및 item 테이블 확인'을 펼쳐 행 수를 확인하세요.")
            else:
                st.info("검색을 실행하지 못했습니다. 위 오류 메시지를 확인하세요.")

with tab2:
    st.subheader("✏️ 아이템 수정")
    
    if 'edit_item' in st.session_state:
        item = st.session_state['edit_item']
        원본이름 = item['아이템이름']
        
        st.info(f"수정 중: **{원본이름}**")
        
        col1, col2 = st.columns(2)
        
        with col1:
            st.markdown("#### 기본 정보")
            새이름 = st.text_input("아이템 이름", value=원본이름, key="edit_name")
            인벤ID = st.number_input("인벤ID (아이콘)", value=int(item.get('인벤ID') or 0), min_value=0, key="edit_invgfx")
            구분1 = st.text_input("구분1", value=str(item.get('구분1') or ''), key="edit_type1")
            구분2 = st.text_input("구분2", value=str(item.get('구분2') or ''), key="edit_type2")
            재질 = st.text_input("재질", value=str(item.get('재질') or ''), key="edit_material")
            무게 = st.text_input("무게", value=str(item.get('무게') or '0'), key="edit_weight")
            최소레벨 = st.number_input("최소 레벨", value=int(item.get('level_min') or 0), min_value=0, max_value=99, key="edit_minlvl")
            최대레벨 = st.number_input("최대 레벨", value=int(item.get('level_max') or 0), min_value=0, max_value=99, key="edit_maxlvl")
        
        with col2:
            st.markdown("#### 능력치")
            방어력 = st.number_input("AC (방어력)", value=int(item.get('ac') or 0), key="edit_ac")
            소형데미지 = st.number_input("작은 몬스터 데미지", value=int(item.get('작은 몬스터') or 0), min_value=0, key="edit_small")
            대형데미지 = st.number_input("큰 몬스터 데미지", value=int(item.get('큰 몬스터') or 0), min_value=0, key="edit_large")
            명중 = st.number_input("공격성공율", value=int(item.get('공격성공율') or 0), key="edit_hit")
            추가데미지 = st.number_input("추가타격치", value=int(item.get('추가타격치') or 0), key="edit_dmg")
        
        st.markdown("#### 인챈트 설정 (무기/방어구 등)")
        enc_col1, enc_col2, enc_col3 = st.columns(3)
        with enc_col1:
            인첸트가능 = "true" if st.checkbox("인챈트 가능", value=(str(item.get('인첸트') or '') == 'true'), key="edit_enchant") else "false"
        with enc_col2:
            안전인첸트 = st.number_input("안전인첸트 (단계)", value=int(item.get('안전인첸트') or 0), min_value=0, max_value=20, key="edit_safe_enc")
        with enc_col3:
            최고인챈 = st.number_input("최고인챈 (최대 인챈)", value=int(item.get('최고인챈') or 0), min_value=0, max_value=255, key="edit_max_enc")
        
        st.markdown("#### 속성 (attribute_crystal)")
        _attr_val = str(item.get('attribute_crystal') or 'none').lower()
        if _attr_val not in ("none", "earth", "fire", "wind", "water"):
            _attr_val = "none"
        _attr_idx = ("none", "earth", "fire", "wind", "water").index(_attr_val)
        attribute_crystal = st.selectbox(
            "속성 (없음/땅/불/바람/물)",
            ["none", "earth", "fire", "wind", "water"],
            index=_attr_idx,
            key="edit_attr"
        )
        
        st.markdown("#### 사용 가능 직업 (0/1)")
        col3, col4, col5, col6, col7, col7b, col7c = st.columns(7)
        with col3:
            군주 = 1 if st.checkbox("군주", value=bool(int(item.get('군주') or 0)), key="edit_royal") else 0
        with col4:
            기사 = 1 if st.checkbox("기사", value=bool(int(item.get('기사') or 0)), key="edit_knight") else 0
        with col5:
            요정 = 1 if st.checkbox("요정", value=bool(int(item.get('요정') or 0)), key="edit_elf") else 0
        with col6:
            마법사 = 1 if st.checkbox("마법사", value=bool(int(item.get('마법사') or 0)), key="edit_mage") else 0
        with col7:
            다엘 = 1 if st.checkbox("다크엘프", value=bool(int(item.get('다크엘프') or 0)), key="edit_darkelf") else 0
        with col7b:
            용기사 = 1 if st.checkbox("용기사", value=bool(int(item.get('용기사') or 0)), key="edit_dragon") else 0
        with col7c:
            환술사 = 1 if st.checkbox("환술사", value=bool(int(item.get('환술사') or 0)), key="edit_illusion") else 0
        
        st.markdown("#### 스탯/능력 증가")
        stat_col1, stat_col2, stat_col3, stat_col4 = st.columns(4)
        with stat_col1:
            HP증가 = st.number_input("HP증가", value=int(item.get('HP증가') or 0), key="edit_add_hp")
        with stat_col2:
            MP증가 = st.number_input("MP증가", value=int(item.get('MP증가') or 0), key="edit_add_mp")
        with stat_col3:
            MR증가 = st.number_input("MR증가", value=int(item.get('MR증가') or 0), key="edit_add_mr")
        with stat_col4:
            SP증가 = st.number_input("SP증가", value=int(item.get('SP증가') or 0), key="edit_add_sp")
        
        st.markdown("#### 속성 저항 (수/풍/지/화)")
        res_col1, res_col2, res_col3, res_col4 = st.columns(4)
        with res_col1:
            waterress = st.number_input("waterress(수)", value=int(item.get('waterress') or 0), key="edit_water")
        with res_col2:
            windress = st.number_input("windress(풍)", value=int(item.get('windress') or 0), key="edit_wind")
        with res_col3:
            earthress = st.number_input("earthress(지)", value=int(item.get('earthress') or 0), key="edit_earth")
        with res_col4:
            fireress = st.number_input("fireress(화)", value=int(item.get('fireress') or 0), key="edit_fire")
        
        st.markdown("#### 기타 옵션")
        opt_col1, opt_col2, opt_col3 = st.columns(3)
        with opt_col1:
            이펙트ID = st.number_input("이펙트ID", value=int(item.get('이펙트ID') or 0), min_value=0, key="edit_effect")
            delay = st.number_input("delay", value=int(item.get('delay') or 0), min_value=0, key="edit_delay")
        with opt_col2:
            거래가능 = "true" if st.checkbox("거래 가능", value=(str(item.get('거래') or '') == 'true'), key="edit_trade") else "false"
            드랍가능 = "true" if st.checkbox("드랍 가능", value=(str(item.get('드랍') or '') == 'true'), key="edit_drop") else "false"
            겹침가능 = "true" if st.checkbox("겹침 가능", value=(str(item.get('겹침') or '') == 'true'), key="edit_piles") else "false"
        with opt_col3:
            판매가능 = "true" if st.checkbox("판매", value=(str(item.get('판매') or '') == 'true'), key="edit_sell") else "false"
            창고가능 = "true" if st.checkbox("창고", value=(str(item.get('창고') or '') == 'true'), key="edit_warehouse") else "false"
            손상가능 = "true" if st.checkbox("손상 가능", value=(str(item.get('손상') or '') == 'true'), key="edit_canbedmg") else "false"
        
        # 이동 주문서인 경우: item_teleport 목적지 수정 (x, y, 맵번호 등)
        구분2_val = str(item.get('구분2') or '')
        if 구분2_val.startswith("teleport_"):
            try:
                teleport_uid = int(구분2_val.replace("teleport_", "").strip())
            except ValueError:
                teleport_uid = None
            if teleport_uid is not None and db.table_exists("item_teleport"):
                tp_row = db.fetch_one("SELECT uid, name, goto_x, goto_y, goto_map, `range`, goto_heading FROM item_teleport WHERE uid = %s", (teleport_uid,))
                if tp_row:
                    st.markdown("#### 📜 이동 주문서 목적지 수정")
                    st.caption("이동 주문서의 도착 좌표/맵을 수정합니다. 저장 시 item_teleport만 갱신됩니다.")
                    tp_name_edit = st.text_input("목적지 이름 (item_teleport.name)", value=str(tp_row.get("name") or ""), key="edit_tp_name")
                    tpc1, tpc2, tpc3 = st.columns(3)
                    with tpc1:
                        tp_x_edit = st.number_input("X (goto_x)", value=int(tp_row.get("goto_x") or 32768), key="edit_tp_x")
                    with tpc2:
                        tp_y_edit = st.number_input("Y (goto_y)", value=int(tp_row.get("goto_y") or 32768), key="edit_tp_y")
                    with tpc3:
                        tp_map_edit = st.number_input("맵 번호 (goto_map)", value=int(tp_row.get("goto_map") or 0), key="edit_tp_map")
                    tp_range_edit = st.number_input("도착지 랜덤 범위 (range)", value=int(tp_row.get("range") or 0), min_value=0, key="edit_tp_range")
                    tp_heading_edit = st.number_input("goto_heading", value=int(tp_row.get("goto_heading") or 0), key="edit_tp_heading")
                    if st.button("💾 이동 목적지만 저장", key="save_teleport_only"):
                        ok = db.execute_query(
                            "UPDATE item_teleport SET name=%s, goto_x=%s, goto_y=%s, goto_map=%s, `range`=%s, goto_heading=%s WHERE uid=%s",
                            (tp_name_edit, tp_x_edit, tp_y_edit, tp_map_edit, tp_range_edit, tp_heading_edit, teleport_uid)
                        )
                        if ok:
                            st.success("✅ 이동 목적지가 수정되었습니다. 서버 재시작 또는 item_teleport 리로드 후 반영됩니다.")
                            st.rerun()
                        else:
                            st.error("목적지 수정 실패")
                else:
                    st.warning(f"item_teleport에서 uid={teleport_uid} 항목을 찾을 수 없습니다. 목적지를 DB에 추가한 뒤 구분2를 teleport_{teleport_uid}로 맞추세요.")
        
        if st.button("💾 수정 저장", type="primary"):
            성공 = db.execute_query("""
                UPDATE item SET
                    `아이템이름`=%s, `인벤ID`=%s, `구분1`=%s, `구분2`=%s, `재질`=%s, `무게`=%s,
                    level_min=%s, level_max=%s, ac=%s, `작은 몬스터`=%s, `큰 몬스터`=%s,
                    `공격성공율`=%s, `추가타격치`=%s,
                    `군주`=%s, `기사`=%s, `요정`=%s, `마법사`=%s, `다크엘프`=%s, `용기사`=%s, `환술사`=%s,
                    `인첸트`=%s, `안전인첸트`=%s, `최고인챈`=%s, attribute_crystal=%s,
                    `HP증가`=%s, `MP증가`=%s, `MR증가`=%s, `SP증가`=%s,
                    waterress=%s, windress=%s, earthress=%s, fireress=%s,
                    `거래`=%s, `드랍`=%s, `겹침`=%s, `판매`=%s, `창고`=%s, `손상`=%s,
                    `이펙트ID`=%s, delay=%s
                WHERE `아이템이름`=%s
            """, (새이름, 인벤ID, 구분1, 구분2, 재질, 무게, 최소레벨, 최대레벨,
                  방어력, 소형데미지, 대형데미지, 명중, 추가데미지,
                  군주, 기사, 요정, 마법사, 다엘, 용기사, 환술사,
                  인첸트가능, 안전인첸트, 최고인챈, attribute_crystal,
                  HP증가, MP증가, MR증가, SP증가,
                  waterress, windress, earthress, fireress,
                  거래가능, 드랍가능, 겹침가능, 판매가능, 창고가능, 손상가능,
                  이펙트ID, delay, 원본이름))
            
            if 성공:
                st.success("✅ 수정 완료!")
                del st.session_state['edit_item']
                st.rerun()
            else:
                st.error("❌ 수정 실패 (DB 컬럼명 확인)")
    else:
        st.info("'아이템 조회' 탭에서 수정할 아이템을 선택하세요")

with tab3:
    st.subheader("➕ 새 아이템 추가")
    st.caption("수정 탭과 동일한 상세 설정으로 새 아이템을 추가합니다. item 테이블에 없는 컬럼이 있으면 오류가 날 수 있습니다.")

    # ----- 이동 주문서 빠른 추가: x, y, 맵번호 + 주문서 이름만 입력하면 item_teleport + item 한 번에 생성 -----
    with st.expander("📜 이동 주문서 빠른 추가 (x, y, 맵번호 입력 → 게임에서 해당 좌표로 이동)", expanded=True):
        st.caption("이동 주문서 이름과 목적지 좌표만 입력하면 item_teleport와 item을 한 번에 만듭니다. 게임에서 주문서 사용 시 입력한 좌표로 이동합니다.")
        if db.table_exists("item_teleport"):
            with st.form("teleport_quick_add"):
                tp_quick_name = st.text_input("주문서 이름 *", placeholder="예: 기란 마을 이동", key="tp_quick_name")
                c1, c2, c3 = st.columns(3)
                with c1:
                    tp_quick_x = st.number_input("X 좌표", value=32768, key="tp_quick_x")
                with c2:
                    tp_quick_y = st.number_input("Y 좌표", value=32768, key="tp_quick_y")
                with c3:
                    tp_quick_map = st.number_input("맵 번호", value=0, key="tp_quick_map")
                tp_quick_range = st.number_input("도착지 랜덤 범위 (0=정확한 좌표, 1 이상=주변 랜덤)", value=0, min_value=0, key="tp_quick_range")
                tp_quick_heading = st.number_input("goto_heading (방향)", value=0, key="tp_quick_heading")
                if st.form_submit_button("이동 주문서 추가 (목적지 + 아이템 한 번에 생성)"):
                    if not (tp_quick_name and tp_quick_name.strip()):
                        st.warning("주문서 이름을 입력하세요.")
                    else:
                        try:
                            db.execute_query(
                                "INSERT INTO item_teleport (name, goto_x, goto_y, goto_map, `range`, goto_heading) VALUES (%s, %s, %s, %s, %s, %s)",
                                (tp_quick_name.strip(), tp_quick_x, tp_quick_y, tp_quick_map, tp_quick_range, tp_quick_heading)
                            )
                            r = db.fetch_one("SELECT LAST_INSERT_ID() AS id")
                            new_uid = int((r.get("id") or r.get("ID") or 0)) if r else 0
                            if new_uid <= 0:
                                st.error("item_teleport 추가 후 uid를 가져오지 못했습니다.")
                            else:
                                구분2_teleport = f"teleport_{new_uid}"
                                # item 테이블에 이동주문서 행 추가 (기본값으로)
                                ins_ok = db.execute_query("""
                                    INSERT INTO item (
                                        `아이템이름`, `구분1`, `구분2`, NAMEID, `재질`, `무게`, level_min, level_max,
                                        `인벤ID`, GFXID, ac, `작은 몬스터`, `큰 몬스터`, `공격성공율`, `추가타격치`,
                                        `군주`, `기사`, `요정`, `마법사`, `다크엘프`, `용기사`, `환술사`,
                                        `인첸트`, `안전인첸트`, `최고인챈`, attribute_crystal,
                                        `HP증가`, `MP증가`, `MR증가`, `SP증가`,
                                        waterress, windress, earthress, fireress,
                                        `거래`, `드랍`, `겹침`, `판매`, `창고`, `손상`,
                                        `이펙트ID`, delay
                                    ) VALUES (
                                        %s, 'item', %s, '$50000', '기타', '0', 0, 0,
                                        0, 0, 0, 0, 0, 0, 0,
                                        1, 1, 1, 1, 1, 0, 0,
                                        'false', 0, 0, 'none',
                                        0, 0, 0, 0,
                                        0, 0, 0, 0,
                                        'true', 'true', 'true', 'false', 'true', 'false',
                                        0, 0
                                    )
                                """, (tp_quick_name.strip(), 구분2_teleport))
                                if ins_ok:
                                    st.success(f"✅ 이동 주문서 추가됨: **{tp_quick_name.strip()}** → ({tp_quick_x}, {tp_quick_y}) 맵 {tp_quick_map}. 구분2=**{구분2_teleport}**")
                                    st.rerun()
                                else:
                                    st.error("item_teleport는 추가되었으나 item 추가에 실패했습니다. 수동으로 item에 구분2=teleport_{} 로 추가하세요.".format(new_uid))
                        except Exception as e:
                            st.error(f"오류: {e}")
        else:
            st.warning("item_teleport 테이블이 없습니다.")

    # 아이템 종류별 구분1·구분2 (서버 ItemDatabase 구분과 맞춤). 종류를 바꾸면 세부 종류가 바뀌도록 폼 밖에 둠.
    ITEM_KIND_OPTIONS = {
        "무기": ("weapon", {"한손검": "sword", "양손검": "tohandsword", "도끼": "axe", "활": "bow", "창": "spear", "완드": "wand", "스태프": "staff", "단검": "dagger", "둔기": "blunt", "에도류": "edoryu", "클로": "claw", "투척단검": "throwingknife", "화살": "arrow", "건틀릿": "gauntlet", "채찍": "chainsword", "키링크": "keyrink", "낚시대": "fishing_rod"}),
        "방어구": ("armor", {"갑옷": "armor", "투구": "helm", "방패": "shield", "망토": "cloak", "장갑": "glove", "부츠": "boot", "티셔츠": "t"}),
        "악세사리": ("armor", {"반지": "ring", "목걸이": "necklace", "벨트": "belt", "귀걸이": "earring"}),
        "주문서·물약·기타": ("item", {
            "기타": "etc", "물약": "potion", "스크롤": "scroll", "퀘스트": "quest",
            "무기 마법 주문서": "scroll_orim_weapon", "갑옷 마법 주문서": "scroll_orim_armor", "장신구 주문서": "accessory_scroll",
            "축복 부여 주문서": "change_bless", "축복 부여 주문서(인형)": "change_bless2", "축복제거 주문서": "축복제거",
            "이동주문서(uid)": "teleport_0", "레벨업 주문서": "scroll_levelup", "레벨다운 주문서": "scroll_leveldown",
            "변신 주문서": "polymorph", "순간이동 주문서": "venzar borgavve", "귀환 주문서": "verr yed horae",
        }),
    }
    # 폼 밖에서 선택하면 종류 변경 시 rerun 되어 세부 종류 목록이 올바르게 바뀜
    kind_choice = st.selectbox(
        "아이템 종류 *",
        list(ITEM_KIND_OPTIONS.keys()),
        help="서버가 무기/방어구/악세사리 구분에 사용하는 구분1·구분2가 자동 설정됩니다. 종류를 바꾸면 아래 세부 종류가 바뀝니다.",
        key="add_kind"
    )
    구분1_기본, 세부_딕셔너리 = ITEM_KIND_OPTIONS[kind_choice]
    세부_목록 = list(세부_딕셔너리.keys())
    세부_choice = st.selectbox("세부 종류", 세부_목록, key="add_subtype")
    신규구분1 = 구분1_기본
    신규구분2 = 세부_딕셔너리[세부_choice]
    # 이동주문서는 구분2가 teleport_uid. item_teleport 테이블의 uid와 맞춰야 함
    if 신규구분2 == "teleport_0":
        이동uid = st.number_input("이동주문서 목적지 uid (item_teleport 테이블의 uid와 일치해야 함)", min_value=0, value=0, key="add_teleport_uid")
        신규구분2 = f"teleport_{이동uid}"
        st.caption("아래 '이동주문서 생성 방법' 익스팬더에서 item_teleport에 목적지를 추가한 뒤, 여기서 그 uid를 입력하세요.")
    st.caption(f"→ 구분1 = **{신규구분1}**, 구분2 = **{신규구분2}**")

    with st.expander("📜 이동주문서 상세 설정 (고급)"):
        st.markdown("""
        **일반적으로는 위 '이동 주문서 빠른 추가'만 사용하면 됩니다.**  
        수동으로 하려면:  
        1. **item_teleport** 테이블에 목적지 한 행을 추가 (name, goto_x, goto_y, goto_map 등)  
        2. **item** 추가 시 **구분2**를 `teleport_{uid}` 로 넣기 (예: uid가 29면 `teleport_29`)  
        서버는 구분2에서 uid를 읽어 item_teleport의 좌표로 이동시킵니다.
        """)
        if db.table_exists("item_teleport"):
            st.write("**item_teleport 목적지 추가** (uid는 자동 증가일 수 있음, DB에 따라 다름)")
            with st.form("item_teleport_form"):
                tp_name = st.text_input("name (주문서 이름과 연동)", value="", key="tp_name")
                tp_x = st.number_input("goto_x", value=32768, key="tp_x")
                tp_y = st.number_input("goto_y", value=32768, key="tp_y")
                tp_map = st.number_input("goto_map", value=0, key="tp_map")
                tp_range = st.number_input("range", value=0, key="tp_range")
                tp_heading = st.number_input("goto_heading", value=0, key="tp_heading")
                if st.form_submit_button("item_teleport 행 추가"):
                    try:
                        db.execute_query(
                            "INSERT INTO item_teleport (name, goto_x, goto_y, goto_map, `range`, goto_heading) VALUES (%s, %s, %s, %s, %s, %s)",
                            (tp_name or "이동", tp_x, tp_y, tp_map, tp_range, tp_heading)
                        )
                        r = db.fetch_one("SELECT LAST_INSERT_ID() AS id")
                        lid = (r.get("id") or r.get("ID") or 0) if r else 0
                        st.success(f"추가됨. 새 uid는 DB에 따라 다를 수 있음. SELECT * FROM item_teleport 로 확인 후, 아이템 추가 시 구분2=teleport_{lid} 로 넣으세요.")
                    except Exception as e:
                        st.error(str(e))
            st.caption("item_teleport에 if_level, if_class, if_remove 컬럼이 있으면 직접 SQL로 0으로 넣거나, 서버 기본값을 사용하세요.")
        else:
            st.warning("item_teleport 테이블이 없습니다. 서버 DB에 해당 테이블이 있는지 확인하세요.")

    with st.expander("📌 축복/저주·인챈트 주문서 안내"):
        st.markdown("""
        - **축복/저주**: item 테이블에는 **템플릿**만 있고, **축복(0=저주, 1=일반, 2=축복)** 은 **인벤토리(캐릭터 소유)** 단위 값입니다.  
          아이템 **추가**는 템플릿 등록이므로 축복 선택이 없고, **GM 지급** 시 서버에서 bless 값을 줄 수 있습니다.
        - **무기/갑옷 마법 주문서**: 구분2는 `scroll_orim_weapon`, `scroll_orim_armor`. 인챈트 상승/하락 수치는 서버 코드 또는 별도 설정에서 처리될 수 있어, item 테이블에 컬럼이 없을 수 있습니다.
        - **축복받은 주문서·저주받은 주문서**: 같은 아이템 이름으로 DB에 있고, 인벤에 지급될 때 축/저주가 구분됩니다. 검색 시 이름에 '축복' '저주'를 넣어 보세요.
        """)

    with st.form("item_add_form"):
        st.markdown("#### 기본 정보")
        col1, col2 = st.columns(2)
        with col1:
            신규이름 = st.text_input("아이템 이름 (필수) *", placeholder="예: 테스트 아이템", key="add_name")
            신규NAMEID = st.text_input("NAMEID", value="$50000", key="add_nameid")
            신규재질 = st.text_input("재질", value="기타", key="add_material")
            신규무게 = st.text_input("무게", value="0", key="add_weight")
            신규인벤ID = st.number_input("인벤ID", min_value=0, value=0, key="add_invgfx")
            신규GFXID = st.number_input("GFXID", min_value=0, value=0, key="add_gfxid")
            신규최소레벨 = st.number_input("최소 레벨", min_value=0, max_value=99, value=0, key="add_minlvl")
            신규최대레벨 = st.number_input("최대 레벨", min_value=0, max_value=99, value=0, key="add_maxlvl")
        with col2:
            신규AC = st.number_input("AC (방어력)", value=0, key="add_ac")
            신규소형 = st.number_input("작은 몬스터 데미지", min_value=0, value=0, key="add_small")
            신규대형 = st.number_input("큰 몬스터 데미지", min_value=0, value=0, key="add_large")
            신규명중 = st.number_input("공격성공율", value=0, key="add_hit")
            신규추뎀 = st.number_input("추가타격치", value=0, key="add_dmg")
        
        st.markdown("#### 인챈트 설정")
        enc1, enc2, enc3 = st.columns(3)
        with enc1:
            신규인첸트가능 = st.checkbox("인챈트 가능", value=False, key="add_enchant")
        with enc2:
            신규안전인첸트 = st.number_input("안전인첸트", min_value=0, max_value=20, value=0, key="add_safe_enc")
        with enc3:
            신규최고인챈 = st.number_input("최고인챈", min_value=0, max_value=255, value=0, key="add_max_enc")
        신규인첸트 = "true" if 신규인첸트가능 else "false"
        
        st.markdown("#### 속성 (attribute_crystal)")
        신규속성 = st.selectbox("속성", ["none", "earth", "fire", "wind", "water"], index=0, key="add_attr")
        
        st.markdown("#### 사용 가능 직업 (0/1)")
        j1, j2, j3, j4, j5, j6, j7 = st.columns(7)
        with j1: 신규군주 = 1 if st.checkbox("군주", value=True, key="add_royal") else 0
        with j2: 신규기사 = 1 if st.checkbox("기사", value=True, key="add_knight") else 0
        with j3: 신규요정 = 1 if st.checkbox("요정", value=True, key="add_elf") else 0
        with j4: 신규마법사 = 1 if st.checkbox("마법사", value=True, key="add_mage") else 0
        with j5: 신규다엘 = 1 if st.checkbox("다크엘프", value=True, key="add_darkelf") else 0
        with j6: 신규용기사 = 1 if st.checkbox("용기사", value=False, key="add_dragon") else 0
        with j7: 신규환술사 = 1 if st.checkbox("환술사", value=False, key="add_illusion") else 0
        
        st.markdown("#### 스탯/능력 증가")
        s1, s2, s3, s4 = st.columns(4)
        with s1: 신규HP증가 = st.number_input("HP증가", value=0, key="add_add_hp")
        with s2: 신규MP증가 = st.number_input("MP증가", value=0, key="add_add_mp")
        with s3: 신규MR증가 = st.number_input("MR증가", value=0, key="add_add_mr")
        with s4: 신규SP증가 = st.number_input("SP증가", value=0, key="add_add_sp")
        
        st.markdown("#### 속성 저항 (수/풍/지/화)")
        r1, r2, r3, r4 = st.columns(4)
        with r1: 신규waterress = st.number_input("waterress", value=0, key="add_water")
        with r2: 신규windress = st.number_input("windress", value=0, key="add_wind")
        with r3: 신규earthress = st.number_input("earthress", value=0, key="add_earth")
        with r4: 신규fireress = st.number_input("fireress", value=0, key="add_fire")
        
        st.markdown("#### 기타 옵션")
        o1, o2, o3 = st.columns(3)
        with o1:
            신규이펙트ID = st.number_input("이펙트ID", min_value=0, value=0, key="add_effect")
            신규delay = st.number_input("delay", min_value=0, value=0, key="add_delay")
        with o2:
            신규거래 = "true" if st.checkbox("거래 가능", value=True, key="add_trade") else "false"
            신규드랍 = "true" if st.checkbox("드랍 가능", value=True, key="add_drop") else "false"
            신규겹침 = "true" if st.checkbox("겹침 가능", value=True, key="add_piles") else "false"
        with o3:
            신규판매 = "true" if st.checkbox("판매", value=False, key="add_sell") else "false"
            신규창고 = "true" if st.checkbox("창고", value=True, key="add_warehouse") else "false"
            신규손상 = "true" if st.checkbox("손상 가능", value=False, key="add_canbedmg") else "false"
        
        submitted = st.form_submit_button("➕ 아이템 추가")
    
    if submitted:
        if not (신규이름 and 신규이름.strip()):
            st.warning("아이템 이름을 입력하세요.")
        else:
            try:
                sql = """INSERT INTO item (
                    `아이템이름`, `구분1`, `구분2`, NAMEID, `재질`, `무게`, level_min, level_max,
                    `인벤ID`, GFXID, ac, `작은 몬스터`, `큰 몬스터`, `공격성공율`, `추가타격치`,
                    `군주`, `기사`, `요정`, `마법사`, `다크엘프`, `용기사`, `환술사`,
                    `인첸트`, `안전인첸트`, `최고인챈`, attribute_crystal,
                    `HP증가`, `MP증가`, `MR증가`, `SP증가`,
                    waterress, windress, earthress, fireress,
                    `거래`, `드랍`, `겹침`, `판매`, `창고`, `손상`,
                    `이펙트ID`, delay
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s,
                    %s, %s, %s, %s, %s, %s, %s,
                    %s, %s, %s, %s, %s, %s, %s,
                    %s, %s, %s, %s,
                    %s, %s, %s, %s,
                    %s, %s, %s, %s,
                    %s, %s, %s, %s, %s, %s,
                    %s, %s
                )"""
                params = (
                    신규이름.strip(), 신규구분1, 신규구분2, 신규NAMEID, 신규재질, 신규무게, 신규최소레벨, 신규최대레벨,
                    신규인벤ID, 신규GFXID, 신규AC, 신규소형, 신규대형, 신규명중, 신규추뎀,
                    신규군주, 신규기사, 신규요정, 신규마법사, 신규다엘, 신규용기사, 신규환술사,
                    신규인첸트, 신규안전인첸트, 신규최고인챈, 신규속성,
                    신규HP증가, 신규MP증가, 신규MR증가, 신규SP증가,
                    신규waterress, 신규windress, 신규earthress, 신규fireress,
                    신규거래, 신규드랍, 신규겹침, 신규판매, 신규창고, 신규손상,
                    신규이펙트ID, 신규delay
                )
                성공 = db.execute_query(sql, params)
                if 성공:
                    st.success("✅ 아이템이 추가되었습니다. 서버 재시작 후 반영될 수 있습니다.")
                    st.rerun()
                else:
                    st.error("❌ 추가 실패 (이름 중복 또는 DB 컬럼/제약 확인)")
            except Exception as e:
                st.error(f"❌ 추가 중 오류: {e}")
