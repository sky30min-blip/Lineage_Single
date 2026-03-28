"""
리니지 싱글 서버 GM 툴 - DB 관리 페이지
SQL 실행, 테이블 목록/구조/데이터, 테이블 생성
"""

import streamlit as st
import pandas as pd
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.gm_tabs import gm_section_tabs
from utils.table_schemas import (
    get_all_required_tables,
    get_create_sql,
    get_initial_data_sql,
)

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()
show_pending_feedback()

# 사이드바 - 쿼리 템플릿
with st.sidebar:
    st.subheader("📌 쿼리 템플릿")
    templates = {
        "캐릭터 전체 조회": "SELECT * FROM characters LIMIT 100;",
        "레벨 99 캐릭터": "SELECT char_name, level FROM characters WHERE level = 99;",
        "계정별 캐릭터 수": "SELECT account_name, COUNT(*) as count FROM characters GROUP BY account_name;",
    }
    selected_template = st.selectbox("템플릿 선택", list(templates.keys()))
    if st.button("템플릿 적용"):
        if "sql_query" not in st.session_state:
            st.session_state["sql_query"] = ""
        st.session_state["sql_query"] = templates[selected_template]
        queue_feedback("info", "✅ 쿼리 템플릿이 입력란에 적용되었습니다.")
        st.rerun()

# 탭 구성
_DB_TAB_LABELS = ["🔍 SQL 실행", "📋 테이블 목록", "🔧 테이블 생성"]
_db_ti = gm_section_tabs("db_admin", _DB_TAB_LABELS)

# ========== 탭 1: SQL 직접 실행 ==========
if _db_ti == 0:
    st.subheader("SQL 쿼리 실행")

    if "sql_query" not in st.session_state:
        st.session_state["sql_query"] = ""

    sql_query = st.text_area(
        "SQL 쿼리 입력",
        height=200,
        placeholder="SELECT * FROM characters LIMIT 10;",
        key="sql_query",
        help="실행할 SQL 한 덩어리를 입력합니다. SELECT는 조회만, INSERT/UPDATE/DELETE는 아래에서 '수정' 타입을 고른 뒤 실행하세요. 운영 DB이므로 백업 후 사용을 권장합니다.",
    )

    col1, col2 = st.columns([1, 5])
    with col1:
        run_clicked = st.button("🚀 실행")
    with col2:
        query_type = st.radio(
            "쿼리 타입",
            ["SELECT (조회)", "INSERT/UPDATE/DELETE (수정)"],
            horizontal=True,
            help="SELECT만 결과 테이로 보입니다. 데이터를 바꾸는 문은 '수정'을 선택해야 실행됩니다.",
        )

    if run_clicked and sql_query.strip():
        if query_type == "SELECT (조회)":
            result = db.fetch_all(sql_query.strip())
            if result is not None and len(result) > 0:
                df = pd.DataFrame(result)
                st.dataframe(df, width='stretch')
                st.caption(f"총 {len(df)}건 조회됨")
            elif result is not None:
                st.info("조회 결과가 없습니다.")
            else:
                st.error("쿼리 실행 중 오류가 발생했습니다.")
        else:
            ok_q, err_q = db.execute_query_ex(sql_query.strip())
            if ok_q:
                st.success("✅ 쿼리 실행 완료")
            else:
                st.error(f"❌ 쿼리 실행 실패: {err_q}")

# ========== 탭 2: 테이블 목록 ==========
elif _db_ti == 1:
    st.subheader("전체 테이블 목록")

    tables = db.get_all_tables()
    if not tables:
        st.info("테이블이 없습니다.")
    else:
        selected_table = st.selectbox("테이블 선택", tables)

        if selected_table:
            _dsub = gm_section_tabs("db_table_detail", ["구조", "데이터", "통계"])

            if _dsub == 0:
                structure = db.get_table_structure(selected_table)
                if structure:
                    st.dataframe(pd.DataFrame(structure), width='stretch')
                else:
                    st.info("구조 정보를 불러올 수 없습니다.")

            elif _dsub == 1:
                limit = st.number_input("조회 개수", min_value=10, max_value=1000, value=100, step=10)
                # 테이블명은 get_all_tables() 화이트리스트로 검증됨
                data = db.fetch_all(f"SELECT * FROM `{selected_table}` LIMIT %s", (int(limit),))
                if data:
                    df = pd.DataFrame(data)
                    st.dataframe(df, width='stretch')
                else:
                    st.info("데이터가 없습니다.")

            else:
                count = db.get_table_count(selected_table)
                st.metric("총 레코드 수", count)

# ========== 탭 3: 테이블 생성 ==========
else:
    st.subheader("누락 테이블 생성")

    required = get_all_required_tables()
    existing = db.get_all_tables()
    missing = [t for t in required if t not in existing]

    if missing:
        st.warning(f"누락된 테이블: {', '.join(missing)}")

        for table in missing:
            with st.expander(f"📄 {table} 테이블"):
                sql = get_create_sql(table)
                st.code(sql, language="sql")

                if st.button(f"생성: {table}", key=f"create_{table}"):
                    ok_c, err_c = db.execute_query_ex(sql)
                    if ok_c:
                        init_sql = get_initial_data_sql(table)
                        extra = ""
                        if init_sql:
                            ok_i, err_i = db.execute_query_ex(init_sql)
                            if ok_i:
                                extra = " 초기 데이터 삽입 완료."
                            else:
                                extra = f" (초기 데이터 삽입 실패: {err_i})"
                        queue_feedback("success", f"✅ `{table}` 테이블 생성 완료.{extra}")
                        st.rerun()
                    else:
                        st.error(f"❌ 생성 실패: {err_c}")
    else:
        st.success("✅ 모든 필수 테이블이 존재합니다")

    st.divider()
    st.subheader("커스텀 테이블 생성")
    custom_sql = st.text_area(
        "CREATE TABLE SQL 입력",
        height=150,
        placeholder="CREATE TABLE my_table (\n  id INT PRIMARY KEY,\n  name VARCHAR(50)\n);",
    )

    if st.button("커스텀 테이블 생성", key="create_custom"):
        if custom_sql.strip():
            ok_cc, err_cc = db.execute_query_ex(custom_sql.strip())
            if ok_cc:
                queue_feedback("success", "✅ 커스텀 테이블 생성이 완료되었습니다.")
                st.rerun()
            else:
                st.error(f"❌ 생성 실패: {err_cc}")
        else:
            st.warning("SQL을 입력해 주세요.")
