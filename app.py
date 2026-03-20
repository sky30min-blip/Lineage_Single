"""
리니지 싱글 서버 GM 툴 - 메인 대시보드
"""

import streamlit as st
import config
from utils.db_manager import get_db
from utils.table_schemas import get_all_required_tables, get_create_sql

# 페이지 설정
st.set_page_config(
    page_title=config.PAGE_TITLE,
    page_icon=config.PAGE_ICON,
    layout=config.LAYOUT
)

# 타이틀
st.title("🎮 리니지 싱글 서버 GM 툴")

# 사이드바
with st.sidebar:
    st.caption("💡 입력란 옆 **?** 에 마우스를 올리면 한글 설명이 표시됩니다.")
    st.header("📡 서버 상태")
    
    # DB 연결 테스트
    db = get_db()
    is_connected, message = db.test_connection()
    
    if is_connected:
        st.success(f"✅ DB 연결됨")
        st.caption(message)
    else:
        st.error(f"❌ DB 연결 실패")
        st.caption(message)
        st.stop()
    st.divider()
    st.caption("몬스터 스폰(웹):")
    st.link_button("몬스터 스폰 관리 (웹)", "http://localhost:8765/pages/monster-spawn-manager.html", type="secondary")

# 메인 영역
col1, col2, col3 = st.columns(3)

with col1:
    st.metric("📊 전체 테이블", len(db.get_all_tables()))

with col2:
    try:
        account_count = db.get_table_count('accounts')
    except Exception:
        account_count = 0
    st.metric("👥 계정 수", account_count)

with col3:
    try:
        char_count = db.get_table_count('characters')
    except Exception:
        char_count = 0
    st.metric("🎮 캐릭터 수", char_count)

st.divider()

with st.expander("🔄 GM 툴에서 수정한 내용이 게임에 언제 반영되나요?", expanded=True):
    st.markdown("""
    **즉시 반영 (접속 중인 캐릭터에게)**  
    - **아이템 지급**, **아데나 변경**, **위치 이동** → 서버가 주기적으로 DB를 확인해 **접속 중인 캐릭터에게 곧바로** 적용합니다.

    **서버 재시작 또는 리로드 후 반영**  
    - **아이템 추가/수정** (item 테이블), **몬스터 추가/수정** (monster 테이블), **NPC 추가/수정/배치** (npc, npc_spawnlist)  
    → 서버는 이 데이터를 **시작할 때만** 메모리로 읽어옵니다.  
    → **서버를 재시작**하거나, 서버 프로그램 메뉴 **리로드 → item 테이블 리로드 / monster 테이블 리로드 / npc 리로드** 등을 실행해야 게임에 반영됩니다.

    **정리**: GM 툴에서 저장하면 **DB에는 바로 저장**되지만, **아이템/몬스터/NPC 정의**는 서버가 다시 읽어야 하므로 **리로드 또는 재시작**이 필요합니다.
    """)

st.divider()

# 누락 테이블 체크
st.subheader("🔧 테이블 상태 확인")

required_tables = get_all_required_tables()
existing_tables = db.get_all_tables()
missing_tables = [t for t in required_tables if t not in existing_tables]

if missing_tables:
    st.warning(f"⚠️ 누락된 테이블: {', '.join(missing_tables)}")
    
    if st.button("🔨 누락 테이블 자동 생성"):
        for table in missing_tables:
            create_sql = get_create_sql(table)
            if create_sql:
                success = db.execute_query(create_sql)
                if success:
                    st.success(f"✅ {table} 테이블 생성 완료")
                else:
                    st.error(f"❌ {table} 테이블 생성 실패")
        st.rerun()
else:
    st.success("✅ 모든 필수 테이블이 존재합니다")

# 전체 테이블 목록
with st.expander("📋 전체 테이블 목록 보기"):
    st.write(existing_tables)
