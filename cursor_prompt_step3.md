# Cursor 3단계 프롬프트 - DB 관리

캐릭터 관리가 완성되면 DB 직접 관리 페이지를 만들어줘. (Navicat 대체!)

## 파일 생성
**경로**: D:\Lineage_Single\gm_tool\pages\5_💾_DB관리.py

## 요구사항

### 탭 1: SQL 직접 실행
```python
tab1, tab2, tab3 = st.tabs(["🔍 SQL 실행", "📋 테이블 목록", "🔧 테이블 생성"])

with tab1:
    st.subheader("SQL 쿼리 실행")
    
    # 쿼리 입력
    sql_query = st.text_area(
        "SQL 쿼리 입력",
        height=200,
        placeholder="SELECT * FROM characters LIMIT 10;"
    )
    
    col1, col2 = st.columns([1, 5])
    with col1:
        if st.button("🚀 실행"):
            # 쿼리 실행
            
    with col2:
        query_type = st.radio(
            "쿼리 타입",
            ["SELECT (조회)", "INSERT/UPDATE/DELETE (수정)"],
            horizontal=True
        )
    
    # 결과 표시
    if query_type == "SELECT (조회)":
        # fetch_all 사용
        # pandas DataFrame으로 변환
        # st.dataframe(df)
    else:
        # execute_query 사용
        # 성공/실패 메시지
```

### 탭 2: 테이블 목록
```python
with tab2:
    st.subheader("전체 테이블 목록")
    
    # 테이블 목록 가져오기
    tables = db.get_all_tables()
    
    # 각 테이블 선택 가능하게
    selected_table = st.selectbox("테이블 선택", tables)
    
    if selected_table:
        # 탭으로 구분
        t1, t2, t3 = st.tabs(["구조", "데이터", "통계"])
        
        with t1:
            # DESCRIBE 테이블명
            structure = db.get_table_structure(selected_table)
            st.dataframe(structure)
        
        with t2:
            # SELECT * FROM 테이블명 LIMIT 100
            limit = st.number_input("조회 개수", 10, 1000, 100)
            data = db.fetch_all(f"SELECT * FROM {selected_table} LIMIT {limit}")
            if data:
                df = pd.DataFrame(data)
                st.dataframe(df)
            else:
                st.info("데이터가 없습니다")
        
        with t3:
            # SELECT COUNT(*) FROM 테이블명
            count = db.get_table_count(selected_table)
            st.metric("총 레코드 수", count)
```

### 탭 3: 테이블 생성
```python
with tab3:
    st.subheader("누락 테이블 생성")
    
    from utils.table_schemas import (
        get_all_required_tables, 
        get_create_sql,
        get_initial_data_sql
    )
    
    required = get_all_required_tables()
    existing = db.get_all_tables()
    missing = [t for t in required if t not in existing]
    
    if missing:
        st.warning(f"누락된 테이블: {', '.join(missing)}")
        
        for table in missing:
            with st.expander(f"📄 {table} 테이블"):
                # SQL 미리보기
                sql = get_create_sql(table)
                st.code(sql, language='sql')
                
                # 생성 버튼
                if st.button(f"생성: {table}"):
                    success = db.execute_query(sql)
                    if success:
                        st.success(f"✅ {table} 생성 완료")
                        
                        # 초기 데이터 삽입 (있으면)
                        init_sql = get_initial_data_sql(table)
                        if init_sql:
                            db.execute_query(init_sql)
                            st.info("초기 데이터 삽입 완료")
                        
                        st.rerun()
                    else:
                        st.error(f"❌ 생성 실패")
    else:
        st.success("✅ 모든 필수 테이블이 존재합니다")
    
    st.divider()
    
    # 커스텀 테이블 생성
    st.subheader("커스텀 테이블 생성")
    custom_sql = st.text_area(
        "CREATE TABLE SQL 입력",
        height=150,
        placeholder="CREATE TABLE my_table (\n  id INT PRIMARY KEY,\n  name VARCHAR(50)\n);"
    )
    
    if st.button("커스텀 테이블 생성"):
        if custom_sql.strip():
            success = db.execute_query(custom_sql)
            if success:
                st.success("✅ 테이블 생성 완료")
                st.rerun()
            else:
                st.error("❌ 생성 실패")
```

## 보안 주의사항
⚠️ SQL Injection 위험:
- 사용자 입력을 직접 SQL에 넣지 말 것
- 파라미터 바인딩 사용: `db.execute_query(sql, params)`
- 테이블명 검증: 화이트리스트 방식

## 편의 기능 추가
```python
# 자주 쓰는 쿼리 템플릿
with st.sidebar:
    st.subheader("📌 쿼리 템플릿")
    
    templates = {
        "캐릭터 전체 조회": "SELECT * FROM characters LIMIT 100;",
        "레벨 99 캐릭터": "SELECT char_name, level FROM characters WHERE level = 99;",
        "계정별 캐릭터 수": "SELECT account_name, COUNT(*) as count FROM characters GROUP BY account_name;",
    }
    
    selected_template = st.selectbox("템플릿 선택", list(templates.keys()))
    if st.button("템플릿 적용"):
        st.session_state['sql_query'] = templates[selected_template]
```

이 페이지 완성하면 Navicat 없이도 모든 DB 작업이 가능해!
