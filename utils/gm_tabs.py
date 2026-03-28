"""
Streamlit st.tabs는 rerun 시 항상 첫 탭이 활성화됩니다.
st.pills + 고정 key로 동일한 섹션 전환 UX를 유지합니다.
"""

from __future__ import annotations

import streamlit as st


def gm_section_tabs(
    page_id: str,
    labels: list[str],
    *,
    default_index: int = 0,
    width: str = "stretch",
) -> int:
    """
    페이지 상단 섹션 선택. st.rerun() 후에도 선택 인덱스가 유지됩니다.

    Args:
        page_id: 페이지별 고유 ID (세션 키 접두사).
        labels: 탭 제목과 동일한 문자열 목록.
        default_index: 최초 진입 시 선택 인덱스.
        width: st.pills width ("content" | "stretch" | 픽셀).

    Returns:
        0 .. len(labels)-1
    """
    if not labels:
        return 0
    di = max(0, min(int(default_index), len(labels) - 1))
    default_val = labels[di]
    key = f"gm_sec_{page_id}"
    sel = st.pills(
        "section",
        labels,
        selection_mode="single",
        default=default_val,
        key=key,
        label_visibility="collapsed",
        width=width,
    )
    if sel is None:
        return di
    try:
        return labels.index(sel)
    except ValueError:
        return di
