"""
GM 툴 전용 메모장 — 수정할 일·할 일 등을 적어 둡니다. UTF-8 텍스트 파일로 저장됩니다.
"""
from __future__ import annotations

from pathlib import Path

import streamlit as st

_MEMO_DIR = Path(__file__).resolve().parents[1] / "data"
_MEMO_PATH = _MEMO_DIR / "gm_memo.txt"


def _read_memo() -> str:
    try:
        if _MEMO_PATH.is_file():
            return _MEMO_PATH.read_text(encoding="utf-8")
    except OSError:
        pass
    return ""


def _write_memo(text: str) -> tuple[bool, str]:
    try:
        _MEMO_DIR.mkdir(parents=True, exist_ok=True)
        _MEMO_PATH.write_text(text, encoding="utf-8", newline="\n")
        return True, ""
    except OSError as e:
        return False, str(e)


st.set_page_config(page_title="GM 메모장", page_icon="📝", layout="wide")
st.title("📝 GM 메모장")
st.caption(
    f"내용은 **`{_MEMO_PATH}`** 에 저장됩니다. 서버·DB와 무관하며, 이 PC에서만 보입니다."
)

# `text_area(key="gm_memo_text")` 생성 이후에는 같은 실행에서 이 키를 대입할 수 없음.
# 디스크 다시 읽기는 플래그 + rerun 후, 여기(위젯보다 먼저)에서만 반영.
if st.session_state.pop("_gm_memo_reload_next", False):
    st.session_state["gm_memo_text"] = _read_memo()
elif "gm_memo_text" not in st.session_state:
    st.session_state["gm_memo_text"] = _read_memo()

st.text_area(
    "메모",
    height=520,
    key="gm_memo_text",
    label_visibility="collapsed",
    placeholder="여기에 수정할 항목, 할 일, 메모 등을 자유롭게 적으세요…",
)

c1, c2 = st.columns([1, 1])
with c1:
    save_clicked = st.button("💾 저장", type="primary", help="현재 편집 내용을 파일에 덮어씁니다.")
with c2:
    reload_clicked = st.button(
        "🔄 디스크에서 불러오기",
        help="파일 내용으로 다시 채웁니다. 저장하지 않은 편집은 사라집니다.",
    )

if reload_clicked:
    st.session_state["_gm_memo_reload_next"] = True
    st.rerun()

if save_clicked:
    body = st.session_state.get("gm_memo_text", "")
    ok, err = _write_memo(body if isinstance(body, str) else "")
    if ok:
        st.success("저장했습니다.")
    else:
        st.error(f"저장 실패: {err}")

_text = st.session_state.get("gm_memo_text") or ""
_lines = len(_text.splitlines()) if _text else 0
_chars = len(_text)
st.caption(f"줄 수: **{_lines}** · 글자 수: **{_chars:,}**")
