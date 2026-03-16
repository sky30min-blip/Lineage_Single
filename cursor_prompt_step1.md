# Cursor 1단계 프롬프트

D:\Lineage_Single\gm_tool 폴더에 다음 파일들을 생성해줘:

## 1. 폴더 구조 생성
```
D:\Lineage_Single\gm_tool\
├── utils\
│   └── __init__.py (빈 파일)
└── pages\
    (빈 폴더)
```

## 2. 파일 복사
내가 제공한 다음 파일들을 해당 위치에 복사:
- config.py → D:\Lineage_Single\gm_tool\config.py
- requirements.txt → D:\Lineage_Single\gm_tool\requirements.txt
- db_manager.py → D:\Lineage_Single\gm_tool\utils\db_manager.py
- table_schemas.py → D:\Lineage_Single\gm_tool\utils\table_schemas.py

## 3. app.py 생성
메인 대시보드 파일 생성:

**경로**: D:\Lineage_Single\gm_tool\app.py

**요구사항**:
```python
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
    account_count = db.get_table_count('accounts')
    st.metric("👥 계정 수", account_count)

with col3:
    char_count = db.get_table_count('characters')
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
```

## 4. 패키지 설치
터미널에서 실행:
```bash
cd D:\Lineage_Single\gm_tool
pip install -r requirements.txt
```

## 5. 실행 테스트
```bash
streamlit run app.py
```

브라우저에서 http://localhost:8501 접속해서 확인!
