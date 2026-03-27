"""
아이템명 + 인벤ID 전체표 (+ 아이콘)
"""

from pathlib import Path
import pandas as pd
import streamlit as st
from utils.db_manager import get_db
from utils.sprite_pak import discover_sprite_pairs, extract_item_icons_from_pak


st.set_page_config(page_title="아이템 이미지번호 전체표", page_icon="🧾", layout="wide")
st.title("🧾 아이템명 + 인벤ID 전체표")
st.caption("DB item 테이블의 아이템명/인벤ID를 전체 표시하고, 가능하면 아이콘도 함께 보여줍니다.")

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


@st.cache_data(show_spinner=False)
def auto_find_icon_root():
    candidates = [
        Path("D:/Lineage_Single"),
        Path("D:/Lineage_Single/3.싱글리니지 클라이언트"),
        Path("D:/zena2.0 클라이언트"),
    ]
    for c in candidates:
        if c.exists():
            # 숫자 BMP가 조금이라도 있으면 아이콘 루트 후보로 채택
            try:
                for p in c.rglob("*.bmp"):
                    if p.stem.isdigit():
                        return str(c)
            except Exception:
                pass
    return str(candidates[-1])


@st.cache_data(show_spinner=False)
def auto_find_client_root():
    candidates = [
        Path("D:/Lineage_Single"),
        Path("D:/Lineage_Single/3.싱글리니지 클라이언트"),
        Path("D:/zena2.0 클라이언트"),
    ]
    for c in candidates:
        if c.exists():
            return str(c)
    return str(candidates[-1])


@st.cache_data(show_spinner=False)
def auto_find_map_key_dir(client_root: str):
    root = Path(client_root)
    candidates = [
        root / "resource",
        root,
        root / "data",
        root / "bin",
        Path("D:/Lineage_Single/2.싱글리니지 팩/resource"),
        Path("D:/Lineage_Single/2.싱글리니지 팩"),
        Path("D:/Lineage_Single"),
    ]
    for c in candidates:
        if (c / "Map1").exists() and (c / "Map2").exists() and (c / "Map3").exists() and (c / "Map4").exists() and (c / "Map5").exists():
            return str(c)
    return str(root)


@st.cache_data(show_spinner=False)
def build_icon_map(icon_root: str):
    """
    인벤ID -> bmp 경로 매핑 생성.
    파일명 규칙:
    - inv.bmp
    - 1000000 + inv.bmp
    """
    root = Path(icon_root)
    mapping = {}
    if not root.exists():
        return mapping
    try:
        for p in root.rglob("*.bmp"):
            stem = p.stem
            if not stem.isdigit():
                continue
            raw = int(stem)
            cand = [raw]
            if raw >= 1000000:
                cand.append(raw - 1000000)
            for inv in cand:
                if inv < 0:
                    continue
                # 이미 있으면 먼저 찾은 값을 유지
                mapping.setdefault(inv, str(p))
    except Exception:
        pass
    return mapping


@st.cache_data(show_spinner=False)
def scan_sprite_pairs(client_root: str):
    root = Path(client_root)
    pairs = discover_sprite_pairs(root)
    return [(str(idx), str(pak)) for idx, pak in pairs]


@st.cache_data(show_spinner=False, ttl=3600)
def build_pak_icon_map(client_root: str, idx_path: str, pak_path: str, map_dir: str, inv_ids: tuple):
    out_dir = Path("D:/Lineage_Single/gm_tool/.cache/extracted_item_icons")
    return extract_item_icons_from_pak(
        idx_path=Path(idx_path),
        pak_path=Path(pak_path),
        map_dir=Path(map_dir),
        inv_ids=inv_ids,
        output_dir=out_dir,
    )


df_all = load_items_table()
if df_all.empty:
    st.warning("item 테이블 조회 결과가 없습니다.")
    st.stop()

st.subheader("Sprite.idx/pak 파싱 + 아이콘 추출")
st.caption("Map1~Map5 키 파일이 있는 경로를 지정하면 Sprite.pak에서 아이콘을 직접 추출해 인벤ID와 매핑합니다.")
client_root_default = auto_find_client_root()
client_root = st.text_input("클라이언트 루트 폴더", value=client_root_default)
pair_state_key = f"sprite_pairs::{client_root.strip()}"

if st.button("🔎 Sprite IDX/PAK 검색", help="필요할 때만 검색해서 페이지 초기 로딩을 빠르게 유지합니다."):
    st.session_state[pair_state_key] = scan_sprite_pairs(client_root.strip())

sprite_pairs = st.session_state.get(pair_state_key, [])

if not sprite_pairs:
    st.info("아직 Sprite 파일 검색을 실행하지 않았습니다. 위 버튼을 눌러 주세요.")
    selected_idx = ""
    selected_pak = ""
else:
    options = [f"{idx} | {pak}" for idx, pak in sprite_pairs]
    selected = st.selectbox("Sprite IDX/PAK 파일 쌍", options=options, index=0)
    selected_idx, selected_pak = selected.split(" | ", 1)

map_dir_default = str(Path(selected_idx).parent) if selected_idx else client_root_default
map_dir_default = auto_find_map_key_dir(client_root.strip()) if client_root.strip() else map_dir_default
map_dir = st.text_input("Pak decode 키 경로 (Map1~Map5, 없으면 평문 idx 자동시도)", value=map_dir_default)

default_root = auto_find_icon_root()
icon_root = st.text_input("아이콘 루트 폴더", value=default_root)
icon_map = build_icon_map(icon_root.strip())
pak_icon_map = {}
use_pak_extract = st.checkbox("Sprite.pak 추출 아이콘 우선 사용", value=True)

if use_pak_extract and selected_idx and selected_pak:
    if st.button("🧩 Sprite.pak에서 아이콘 추출/매핑"):
        inv_tuple = tuple(sorted(set(df_all["인벤ID"].astype(int).tolist())))
        try:
            pak_icon_map = build_pak_icon_map(
                client_root=client_root.strip(),
                idx_path=selected_idx,
                pak_path=selected_pak,
                map_dir=map_dir.strip(),
                inv_ids=inv_tuple,
            )
            st.success(f"Sprite.pak 추출 매핑 완료: {len(pak_icon_map):,}건")
            if len(pak_icon_map) == 0:
                st.warning("추출 결과가 0건입니다. idx/pak 쌍이 맞는지, 또는 Map1~Map5 경로가 맞는지 확인해 주세요.")
        except Exception as e:
            st.error(f"Sprite.pak 추출 실패: {e}")

if use_pak_extract:
    # 캐시에서 최근 결과 재사용
    try:
        inv_tuple = tuple(sorted(set(df_all["인벤ID"].astype(int).tolist())))
        pak_icon_map = build_pak_icon_map(
            client_root=client_root.strip(),
            idx_path=selected_idx or "none",
            pak_path=selected_pak or "none",
            map_dir=map_dir.strip(),
            inv_ids=inv_tuple,
        ) if selected_idx and selected_pak else {}
    except Exception:
        pak_icon_map = {}

merged_icon_map = dict(icon_map)
merged_icon_map.update(pak_icon_map)
show_image = st.checkbox("표에 아이콘 같이 보기", value=True)

col1, col2, col3 = st.columns([2, 1, 1])
with col1:
    keyword = st.text_input("아이템명 검색", placeholder="예: 아데나, 변신, 주문서")
with col2:
    inv_min = st.number_input("인벤ID 최소", min_value=0, value=0, step=1)
with col3:
    inv_max = st.number_input("인벤ID 최대", min_value=0, value=int(df_all["인벤ID"].max()), step=1)

df = df_all[(df_all["인벤ID"] >= int(inv_min)) & (df_all["인벤ID"] <= int(inv_max))].copy()
if keyword.strip():
    k = keyword.strip()
    df = df[df["아이템명"].astype(str).str.contains(k, case=False, na=False)]

if show_image:
    df.insert(0, "아이콘", df["인벤ID"].map(lambda x: merged_icon_map.get(int(x), None)))
    matched = int(df["아이콘"].notna().sum())
    st.info(f"아이콘 매칭: {matched:,} / {len(df):,} (BMP 스캔 + Sprite.pak 추출)")

st.success(f"조회 결과: {len(df):,}건 (전체 {len(df_all):,}건)")

if show_image:
    st.dataframe(
        df,
        hide_index=True,
        use_container_width=True,
        column_config={
            "아이콘": st.column_config.ImageColumn("아이콘", help="아이콘 파일이 있으면 표시됩니다."),
        },
    )
else:
    st.dataframe(df, hide_index=True, use_container_width=True)

csv_data = df.drop(columns=["아이콘"], errors="ignore").to_csv(index=False).encode("utf-8-sig")
st.download_button(
    "⬇️ 현재 결과 CSV 다운로드",
    data=csv_data,
    file_name="item_inv_id_table.csv",
    mime="text/csv",
)

