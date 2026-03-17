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
    
    검색어 = st.text_input("아이템 이름 검색", placeholder="예: 다마스커스, 홀 쿠폰")
    
    if 검색어:
        # LIKE 패턴을 파라미터로 전달 (한글 컬럼명 사용)
        like_pattern = f"%{검색어}%"
        try:
            결과 = db.fetch_all("""
                SELECT 
                    `아이템이름`, `구분1`, `구분2`, NAMEID, `재질`, `무게`,
                    level_min, level_max, `작은 몬스터`, `큰 몬스터`, ac,
                    `공격성공율`, `추가타격치`, `군주`, `기사`, `요정`, `마법사`, `다크엘프`,
                    `거래`, `드랍`, `겹침`, `인벤ID`, GFXID
                FROM item 
                WHERE `아이템이름` LIKE %s
                ORDER BY `아이템이름`
                LIMIT 50
            """, (like_pattern,))
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
                with col2:
                    st.write(f"**재질**: {선택.get('재질', '')}")
                    st.write(f"**무게**: {선택.get('무게', '')}")
                    st.write(f"**레벨**: {선택.get('level_min', 0)}-{선택.get('level_max', 0)}")
                    st.write(f"**NAMEID**: {선택.get('NAMEID', '')}")
                with col3:
                    st.write(f"**AC**: {선택.get('ac', 0)}")
                    st.write(f"**데미지(소/대)**: {선택.get('작은 몬스터', 0)}/{선택.get('큰 몬스터', 0)}")
                    st.write(f"**명중**: {선택.get('공격성공율', 0)}")
                    st.write(f"**추뎀**: {선택.get('추가타격치', 0)}")
            
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
        
        st.markdown("#### 사용 가능 직업 (0/1)")
        col3, col4, col5, col6, col7 = st.columns(5)
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
        
        st.markdown("#### 아이템 속성")
        col8, col9, col10 = st.columns(3)
        with col8:
            거래가능 = "true" if st.checkbox("거래 가능", value=(str(item.get('거래') or '') == 'true'), key="edit_trade") else "false"
        with col9:
            드랍가능 = "true" if st.checkbox("드랍 가능", value=(str(item.get('드랍') or '') == 'true'), key="edit_drop") else "false"
        with col10:
            겹침가능 = "true" if st.checkbox("겹침 가능", value=(str(item.get('겹침') or '') == 'true'), key="edit_piles") else "false"
        
        if st.button("💾 수정 저장", type="primary"):
            성공 = db.execute_query("""
                UPDATE item SET
                    `아이템이름`=%s, `인벤ID`=%s, `구분1`=%s, `구분2`=%s, `재질`=%s, `무게`=%s,
                    level_min=%s, level_max=%s, ac=%s, `작은 몬스터`=%s, `큰 몬스터`=%s,
                    `공격성공율`=%s, `추가타격치`=%s,
                    `군주`=%s, `기사`=%s, `요정`=%s, `마법사`=%s, `다크엘프`=%s,
                    `거래`=%s, `드랍`=%s, `겹침`=%s
                WHERE `아이템이름`=%s
            """, (새이름, 인벤ID, 구분1, 구분2, 재질, 무게, 최소레벨, 최대레벨,
                  방어력, 소형데미지, 대형데미지, 명중, 추가데미지,
                  군주, 기사, 요정, 마법사, 다엘, 거래가능, 드랍가능, 겹침가능, 원본이름))
            
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
    st.caption("item 테이블은 컬럼이 많아, 기존 아이템을 조회·복사한 뒤 이름만 바꿔 추가하는 방식을 권장합니다. 아래는 최소 필드만 넣는 시도입니다.")
    
    col1, col2 = st.columns(2)
    
    with col1:
        신규이름 = st.text_input("아이템 이름 (필수)", placeholder="예: 테스트 아이템")
        신규구분1 = st.text_input("구분1", value="item")
        신규구분2 = st.text_input("구분2", value="etc")
        신규NAMEID = st.text_input("NAMEID", value="$50000")
        신규재질 = st.text_input("재질", value="기타")
        신규무게 = st.text_input("무게", value="0")
        신규인벤ID = st.number_input("인벤ID", min_value=0, value=0)
    
    with col2:
        신규AC = st.number_input("AC", value=0)
        신규소형 = st.number_input("작은 몬스터 데미지", min_value=0, value=0)
        신규대형 = st.number_input("큰 몬스터 데미지", min_value=0, value=0)
        신규명중 = st.number_input("공격성공율", value=0)
        신규추뎀 = st.number_input("추가타격치", value=0)
    
    if st.button("➕ 아이템 추가 시도", type="primary"):
        if 신규이름:
            # item 테이블 컬럼이 많으므로, 필수 컬럼만 넣으면 DB에 따라 실패할 수 있음
            st.warning("실제 item 테이블은 컬럼이 매우 많습니다. 서버의 item 스키마에 맞게 SQL을 직접 실행하거나, 기존 아이템 행을 복사해 이름만 바꿔 추가하세요.")
        else:
            st.warning("아이템 이름을 입력하세요")
