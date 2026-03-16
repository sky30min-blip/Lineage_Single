"""
접속 로그 - 캐릭터 접속/종료 기록 (서버 log/매니저창/접속 로그 파일)
"""
import time
import streamlit as st
import pandas as pd
from utils.log_reader import get_manager_log_dir, list_log_dates, read_log_lines, parse_connect_line

st.subheader("📋 접속 로그")
st.caption("서버가 기록하는 접속/종료 로그 파일을 읽습니다. (log/매니저창/접속 로그) · 서버가 주기적으로 저장하므로 최대 약 5분 지연될 수 있습니다.")

log_dir = get_manager_log_dir("접속 로그")
dates = list_log_dates("접속 로그")

with st.sidebar:
    st.write("**필터**")
    date_opt = ["(최신)"] + dates[:31]
    selected = st.selectbox("날짜", date_opt, key="connect_date")
    date_str = None if selected == "(최신)" else selected
    search = st.text_input("검색 (IP/계정/캐릭명)", key="connect_search")
    st.caption(f"경로: {log_dir}")

lines = read_log_lines("접속 로그", date_str=date_str, max_lines=3000)
rows = [parse_connect_line(ln) for ln in lines]
if search and search.strip():
    q = search.strip().lower()
    rows = [r for r in rows if q in (r.get("IP") or "").lower() or q in (r.get("계정") or "").lower() or q in (r.get("캐릭명") or "").lower() or q in (r.get("구분") or "").lower()]

if not rows:
    st.info("로그가 없거나 경로가 다릅니다. 서버가 log/매니저창/접속 로그 에 저장하는지 확인하세요.")
else:
    df = pd.DataFrame(rows)
    df = df[["시간", "IP", "계정", "캐릭명", "구분"]].replace("", None)
    page_size = 100
    total = len(df)
    max_page = max(1, (total + page_size - 1) // page_size)
    page = st.number_input("페이지", min_value=1, max_value=max_page, value=1, key="connect_page")
    start = (page - 1) * page_size
    slice_df = df.iloc[start: start + page_size]
    st.dataframe(slice_df, use_container_width=True, hide_index=True)
    st.caption(f"총 {total}건 (페이지당 {page_size}건)")

    col1, col2, _ = st.columns([1, 1, 2])
    with col1:
        if st.download_button("CSV 다운로드", slice_df.to_csv(index=False).encode("utf-8-sig"), file_name=f"connect_log_{selected.replace('(', '').replace(')', '')}.csv", mime="text/csv", key="connect_csv"):
            st.success("다운로드됨")
    with col2:
        if st.button("새로고침", key="connect_refresh"):
            st.rerun()

st.caption("약 1초마다 자동 새로고침됩니다.")
time.sleep(1)
st.rerun()
