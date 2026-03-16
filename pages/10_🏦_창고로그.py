"""
창고 로그 - 창고 입출금 기록 (서버 log/매니저창/창고 로그 파일)
"""
import time
import streamlit as st
import pandas as pd
from utils.log_reader import get_manager_log_dir, list_log_dates, read_log_lines

st.subheader("🏦 창고 로그")
st.caption("서버가 기록하는 창고 맡기기/찾기 로그 파일을 읽습니다.")

with st.sidebar:
    dates = list_log_dates("창고 로그")
    date_opt = ["(최신)"] + dates[:31]
    selected = st.selectbox("날짜", date_opt, key="wh_date")
    date_str = None if selected == "(최신)" else selected
    search = st.text_input("검색", key="wh_search")

lines = read_log_lines("창고 로그", date_str=date_str, max_lines=3000)
if search and search.strip():
    q = search.strip()
    lines = [ln for ln in lines if q in ln]

if not lines:
    st.info("로그가 없거나 경로가 다릅니다.")
else:
    df = pd.DataFrame({"로그": lines})
    page_size = 100
    total = len(df)
    page = st.number_input("페이지", min_value=1, max_value=max(1, (total + page_size - 1) // page_size), value=1, key="wh_page")
    start = (page - 1) * page_size
    slice_df = df.iloc[start: start + page_size]
    st.dataframe(slice_df, use_container_width=True, hide_index=True)
    st.caption(f"총 {total}건")
    st.download_button("CSV 다운로드", slice_df.to_csv(index=False).encode("utf-8-sig"), file_name="warehouse_log.csv", mime="text/csv", key="wh_csv")

st.caption("약 1초마다 자동 새로고침됩니다.")
time.sleep(1)
st.rerun()
