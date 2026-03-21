"""
명령어 로그 - GM 명령어 사용 기록 (서버 log/매니저창/명령어 로그 파일)
"""
import time
import streamlit as st
import pandas as pd
from utils.log_reader import list_log_dates, read_log_lines, clear_log_file

st.subheader("⚙️ 명령어 로그")
st.caption("서버가 기록하는 GM 명령어 사용 로그 파일을 읽습니다.")

with st.sidebar:
    dates = list_log_dates("명령어 로그")
    date_opt = ["(최신)"] + dates[:31]
    selected = st.selectbox("날짜", date_opt, key="cmd_date")
    date_str = None if selected == "(최신)" else selected
    search = st.text_input("검색 (계정/명령어)", key="cmd_search")
    with st.expander("🗑️ 로그 초기화"):
        st.caption("선택한 날짜 로그 파일 내용을 비웁니다.")
        confirm_reset = st.checkbox("초기화 실행 확인", key="cmd_confirm_reset")
        if st.button("로그 초기화", key="cmd_reset", disabled=not confirm_reset):
            ok, msg = clear_log_file("명령어 로그", date_str)
            if ok:
                st.success(msg)
            else:
                st.error(msg)
            st.rerun()

lines = read_log_lines("명령어 로그", date_str=date_str, max_lines=3000)
if search and search.strip():
    q = search.strip()
    lines = [ln for ln in lines if q in ln]

if not lines:
    st.info("로그가 없거나 경로가 다릅니다.")
else:
    df = pd.DataFrame({"로그": lines})
    page_size = 100
    total = len(df)
    page = st.number_input("페이지", min_value=1, max_value=max(1, (total + page_size - 1) // page_size), value=1, key="cmd_page")
    start = (page - 1) * page_size
    slice_df = df.iloc[start: start + page_size]
    st.dataframe(slice_df, width='stretch', hide_index=True)
    st.caption(f"총 {total}건")
    st.download_button("CSV 다운로드", slice_df.to_csv(index=False).encode("utf-8-sig"), file_name="command_log.csv", mime="text/csv", key="cmd_csv")

st.caption("약 1초마다 자동 새로고침됩니다.")
time.sleep(1)
st.rerun()
