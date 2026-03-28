"""
Streamlit: 버튼 처리 직후 st.rerun() 하면 같은 턴의 st.success()가 화면에 안 남는 경우가 많음.
세션에 메시지를 넣고 다음 렌더에서 상단에 표시 + 토스트로 즉시 알림.
"""

from __future__ import annotations

import streamlit as st

SESSION_KEY = "_gm_feedback_pending"


def queue_feedback(kind: str, message: str) -> None:
    """
    kind: success | error | warning | info
    rerun 직전에 호출하면 다음 로드 시 show_pending_feedback()에서 박스로 표시됨.
    """
    st.session_state[SESSION_KEY] = {"kind": kind, "message": str(message)}
    try:
        icons = {"success": "✅", "error": "❌", "warning": "⚠️", "info": "ℹ️"}
        st.toast(f"{icons.get(kind, 'ℹ️')} {message}")
    except Exception:
        pass


def show_pending_feedback() -> None:
    """각 페이지에서 DB 연결 확인(st.stop) 직후 한 번 호출."""
    if SESSION_KEY not in st.session_state:
        return
    data = st.session_state.pop(SESSION_KEY)
    kind = data.get("kind") or "info"
    msg = data.get("message") or ""
    if kind == "success":
        st.success(msg)
    elif kind == "error":
        st.error(msg)
    elif kind == "warning":
        st.warning(msg)
    else:
        st.info(msg)


def feedback_from_ok(ok: bool, err: str, success_msg: str, fail_prefix: str = "실패") -> None:
    """execute_query_ex 등 (ok, err) 패턴용."""
    if ok:
        queue_feedback("success", success_msg)
    else:
        queue_feedback("error", f"❌ {fail_prefix}: {err}" if err else f"❌ {fail_prefix}")
