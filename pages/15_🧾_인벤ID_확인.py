"""
인벤ID 확인 — DB item 테이블 아이템명·인벤ID·GFXID
"""

import pandas as pd
import streamlit as st
from utils.db_manager import get_db

st.set_page_config(page_title="인벤ID 확인", page_icon="🧾", layout="wide")
st.title("🧾 인벤ID 확인")
st.caption("DB `item` 테이블에서 **아이템명·인벤ID·GFXID** 를 조회합니다.")

db = get_db()


@st.cache_data(show_spinner=False, ttl=60)
def load_items_table():
    sql = """
    SELECT `아이템이름` AS item_name, `인벤ID` AS inv_id, `GFXID` AS gfx_id
    FROM `item`
    ORDER BY `인벤ID`, `아이템이름`
    """
    rows = db.fetch_all(sql)
    if not rows:
        return pd.DataFrame(columns=["아이템명", "인벤ID", "GFXID"])
    df = pd.DataFrame(rows)
    df = df.rename(columns={"item_name": "아이템명", "inv_id": "인벤ID", "gfx_id": "GFXID"})
    df["인벤ID"] = pd.to_numeric(df["인벤ID"], errors="coerce").fillna(-1).astype(int)
    return df


df_all = load_items_table()
if df_all.empty:
    st.warning("item 테이블 조회 결과가 없습니다.")
    st.stop()

col1, col2, col3 = st.columns([2, 1, 1])
with col1:
    keyword = st.text_input("아이템명 검색", placeholder="예: 아데나, 변신, 주문서")
with col2:
    inv_min = st.number_input("인벤ID 최소", min_value=0, value=0, step=1)
with col3:
    inv_max = st.number_input(
        "인벤ID 최대",
        min_value=0,
        value=int(df_all["인벤ID"].max()),
        step=1,
    )

df = df_all[(df_all["인벤ID"] >= int(inv_min)) & (df_all["인벤ID"] <= int(inv_max))].copy()
if keyword.strip():
    k = keyword.strip()
    df = df[df["아이템명"].astype(str).str.contains(k, case=False, na=False)]

st.success(f"조회 결과: {len(df):,}건 (전체 {len(df_all):,}건)")

st.dataframe(df, hide_index=True, width="stretch")

st.download_button(
    "⬇️ 현재 결과 CSV 다운로드",
    data=df.to_csv(index=False).encode("utf-8-sig"),
    file_name="item_inv_id_table.csv",
    mime="text/csv",
)
