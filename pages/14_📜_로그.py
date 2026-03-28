"""
로그 — 접속·거래·인챈트·명령어·드랍 (서버 log/매니저창 파일)
"""
import streamlit as st
import pandas as pd
import config
from utils.log_reader import (
    get_manager_log_dir,
    list_log_dates,
    read_log_lines,
    parse_connect_line,
    clear_log_file,
)

st.set_page_config(page_title="로그", page_icon="📜", layout="wide")
st.title("📜 로그")
st.caption(
    "서버가 `log/매니저창/` 아래에 쌓는 로그를 한 메뉴에서 봅니다. "
    "날짜·검색·초기화는 탭마다 따로 적용됩니다."
)

tab_conn, tab_trade, tab_enc, tab_cmd, tab_drop = st.tabs(
    [
        "📋 접속 로그",
        "🤝 거래 로그",
        "⚡ 인챈트 로그",
        "⚙️ 명령어 로그",
        "💎 드랍 로그",
    ]
)

# ---------- 접속 ----------
with tab_conn:
    st.subheader("📋 접속 로그")
    st.caption(
        "접속/종료 기록 · **DB가 아니라** 서버가 디스크에 쌓는 텍스트 로그입니다. "
        "캐릭터 관리(MariaDB)와 이름이 다르면, 서버가 예전에 다른 DB에 붙었거나 로그만 남은 경우일 수 있습니다. "
        f"경로: `{get_manager_log_dir('접속 로그')}` · GM DB: `{config.DB_CONFIG['host']}:{config.DB_CONFIG['port']}/{config.DB_CONFIG['database']}`"
    )
    dates = list_log_dates("접속 로그")
    date_opt = ["(최신)"] + dates[:31]
    c1, c2 = st.columns(2)
    with c1:
        selected = st.selectbox("날짜", date_opt, key="log_connect_date")
    with c2:
        search = st.text_input("검색 (IP/계정/캐릭명)", key="log_connect_search")
    with st.expander("🗑️ 로그 초기화"):
        st.caption("선택한 날짜 로그 파일 내용을 비웁니다.")
        date_str = None if selected == "(최신)" else selected
        confirm_reset = st.checkbox("초기화 실행 확인", key="log_connect_confirm_reset")
        if st.button("로그 초기화", key="log_connect_reset", disabled=not confirm_reset):
            ok, msg = clear_log_file("접속 로그", date_str)
            if ok:
                st.success(msg)
            else:
                st.error(msg)
            st.rerun()

    date_str = None if selected == "(최신)" else selected
    lines = read_log_lines("접속 로그", date_str=date_str, max_lines=3000)
    rows = [parse_connect_line(ln) for ln in lines]
    if search and search.strip():
        q = search.strip().lower()
        rows = [
            r
            for r in rows
            if q in (r.get("IP") or "").lower()
            or q in (r.get("계정") or "").lower()
            or q in (r.get("캐릭명") or "").lower()
            or q in (r.get("구분") or "").lower()
        ]

    if not rows:
        st.info("로그가 없거나 경로가 다릅니다.")
    else:
        df = pd.DataFrame(rows)
        df = df[["시간", "IP", "계정", "캐릭명", "구분"]].replace("", None)
        page_size = 100
        total = len(df)
        max_page = max(1, (total + page_size - 1) // page_size)
        page = st.number_input("페이지", min_value=1, max_value=max_page, value=1, key="log_connect_page")
        start = (page - 1) * page_size
        slice_df = df.iloc[start : start + page_size]
        st.dataframe(slice_df, hide_index=True)
        st.caption(f"총 {total}건 (페이지당 {page_size}건)")
        col1, col2, _ = st.columns([1, 1, 2])
        with col1:
            fn = f"connect_log_{selected.replace('(', '').replace(')', '').strip() or 'latest'}.csv"
            st.download_button(
                "CSV 다운로드",
                slice_df.to_csv(index=False).encode("utf-8-sig"),
                file_name=fn,
                mime="text/csv",
                key="log_connect_csv",
            )
        with col2:
            if st.button("🔄 새로고침", key="log_connect_refresh"):
                st.rerun()
    st.caption("목록을 갱신하려면 **새로고침** 버튼 또는 브라우저 새로고침을 사용하세요.")


def _simple_log_tab(
    title: str,
    log_name: str,
    caption: str,
    search_label: str,
    key_prefix: str,
    file_name: str,
    extra_metrics=None,
):
    st.subheader(title)
    st.caption(caption)
    dates = list_log_dates(log_name)
    date_opt = ["(최신)"] + dates[:31]
    c1, c2 = st.columns(2)
    with c1:
        selected = st.selectbox("날짜", date_opt, key=f"{key_prefix}_date")
    with c2:
        search = st.text_input(search_label, key=f"{key_prefix}_search")
    with st.expander("🗑️ 로그 초기화"):
        date_str_e = None if selected == "(최신)" else selected
        confirm_reset = st.checkbox("초기화 실행 확인", key=f"{key_prefix}_confirm_reset")
        if st.button("로그 초기화", key=f"{key_prefix}_reset", disabled=not confirm_reset):
            ok, msg = clear_log_file(log_name, date_str_e)
            if ok:
                st.success(msg)
            else:
                st.error(msg)
            st.rerun()

    date_str = None if selected == "(최신)" else selected
    lines = read_log_lines(log_name, date_str=date_str, max_lines=3000)
    if search and search.strip():
        q = search.strip()
        lines = [ln for ln in lines if q in ln]

    if not lines:
        st.info("로그가 없거나 경로가 다릅니다.")
        return

    if extra_metrics:
        extra_metrics(lines)

    df = pd.DataFrame({"로그": lines})
    page_size = 100
    total = len(df)
    page = st.number_input(
        "페이지",
        min_value=1,
        max_value=max(1, (total + page_size - 1) // page_size),
        value=1,
        key=f"{key_prefix}_page",
    )
    start = (page - 1) * page_size
    slice_df = df.iloc[start : start + page_size]
    st.dataframe(slice_df, width="stretch", hide_index=True)
    st.caption(f"총 {total}건")
    st.download_button(
        "CSV 다운로드",
        slice_df.to_csv(index=False).encode("utf-8-sig"),
        file_name=file_name,
        mime="text/csv",
        key=f"{key_prefix}_csv",
    )
    if st.button("🔄 새로고침", key=f"{key_prefix}_refresh"):
        st.rerun()


with tab_trade:
    _simple_log_tab(
        "🤝 거래 로그",
        "거래 로그",
        "플레이어 간 거래 기록입니다.",
        "검색",
        "log_trade",
        "trade_log.csv",
    )

with tab_enc:

    def _enc_metrics(lines):
        success_count = sum(1 for ln in lines if "성공" in ln)
        st.metric("성공 건수", success_count)
        st.caption(f"성공률: {100 * success_count / len(lines):.1f}%" if lines else "")

    _simple_log_tab(
        "⚡ 인챈트 로그",
        "인첸트 로그",
        "인챈트 성공/실패 기록입니다.",
        "검색 (캐릭터/아이템)",
        "log_enchant",
        "enchant_log.csv",
        extra_metrics=_enc_metrics,
    )

with tab_cmd:
    _simple_log_tab(
        "⚙️ 명령어 로그",
        "명령어 로그",
        "GM 명령어 사용 기록입니다.",
        "검색 (계정/명령어)",
        "log_cmd",
        "command_log.csv",
    )

with tab_drop:
    _simple_log_tab(
        "💎 아이템 드랍 로그",
        "아이템 드랍 로그",
        "몬스터/아이템 드랍 기록입니다.",
        "검색 (캐릭터/아이템/몬스터)",
        "log_drop",
        "drop_log.csv",
    )
