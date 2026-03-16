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
