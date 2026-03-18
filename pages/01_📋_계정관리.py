"""
리니지 싱글 서버 GM 툴 - 계정 관리 페이지
계정 생성, 계정 목록, GM 권한 관리
"""

import re
import streamlit as st
import pandas as pd
from utils.db_manager import get_db

# DB 연결 확인
db = get_db()
is_connected, msg = db.test_connection()
if not is_connected:
    st.error(f"❌ DB 연결 실패: {msg}")
    st.stop()

# 탭 구성
tab1, tab2, tab3 = st.tabs([
    "➕ 계정 생성",
    "📋 계정 목록",
    "🔐 GM 권한 관리",
])

# ========== 탭 1: 계정 생성 ==========
with tab1:
    st.subheader("계정 생성")

    account_id = st.text_input("계정 ID", max_chars=20, placeholder="영문/숫자 4~20자", key="new_account_id")
    password = st.text_input("비밀번호", type="password", placeholder="4자 이상", key="new_password")
    password_confirm = st.text_input("비밀번호 확인", type="password", placeholder="비밀번호 재입력", key="new_password_confirm")
    gm_checked = st.checkbox("GM 권한 부여", key="new_gm")

    def _validate_account_create():
        err = []
        if not account_id or len(account_id) < 4 or len(account_id) > 20:
            err.append("계정 ID는 4~20자여야 합니다.")
        elif not re.match(r"^[a-zA-Z0-9]+$", account_id):
            err.append("계정 ID는 영문·숫자만 사용 가능합니다.")
        if not password or len(password) < 4:
            err.append("비밀번호는 4자 이상이어야 합니다.")
        if password != password_confirm:
            err.append("비밀번호가 일치하지 않습니다.")
        return err

    if st.button("계정 생성", key="btn_create_account"):
        errs = _validate_account_create()
        if errs:
            for e in errs:
                st.error(e)
        else:
            # 서버 스키마: id/pw 사용 (account_name/password 아님), 중복은 LOWER(id)로 검사
            try:
                existing = db.fetch_one("SELECT 1 FROM accounts WHERE LOWER(id) = %s", (account_id.strip().lower(),))
                if existing:
                    st.error("❌ 이미 존재하는 계정 ID입니다.")
                else:
                    try:
                        with db.connection.cursor() as cur:
                            # 서버 스키마: id, pw 필수. register_date 있으면 넣음
                            try:
                                cur.execute(
                                    "INSERT INTO accounts (id, pw, register_date) VALUES (%s, %s, NOW())",
                                    (account_id.strip(), password),
                                )
                            except Exception:
                                cur.execute(
                                    "INSERT INTO accounts (id, pw) VALUES (%s, %s)",
                                    (account_id.strip(), password),
                                )
                        db.connection.commit()
                        if gm_checked:
                            try:
                                db.execute_query("UPDATE accounts SET access_level = 200 WHERE LOWER(id) = %s", (account_id.strip().lower(),))
                            except Exception:
                                pass
                        st.success("✅ 계정이 생성되었습니다.")
                        st.rerun()
                    except Exception as e:
                        if db.connection:
                            db.connection.rollback()
                        st.error(f"❌ 생성 실패: {e}")
            except Exception as e:
                st.error(f"❌ 조회/처리 실패: {e}")

# accounts: id, pw, uid, last_ip / characters: account_uid → accounts.uid, name
def _get_account_list_full(db):
    """계정 목록: id, pw, uid, last_ip + 소속 캐릭터명."""
    for q in [
        "SELECT uid, id, pw, last_ip FROM accounts ORDER BY id",
        "SELECT uid, id, pw FROM accounts ORDER BY id",
        "SELECT uid, id, pw FROM accounts ORDER BY id",
    ]:
        try:
            rows = db.fetch_all(q)
            if not rows:
                continue
            if "last_ip" not in rows[0]:
                for r in rows:
                    r["last_ip"] = ""
            # 소속 캐릭터명 (account_uid로 연결) — 목록용 문자열 + 줄별 표시용 리스트
            for r in rows:
                uid = r.get("uid")
                char_list = []
                if uid is not None:
                    try:
                        chars = db.fetch_all("SELECT name FROM characters WHERE account_uid = %s", (uid,))
                        char_list = [c.get("name") or "" for c in chars if c.get("name")]
                    except Exception:
                        try:
                            chars = db.fetch_all("SELECT name FROM characters WHERE account = %s", (r.get("id"),))
                            char_list = [c.get("name") or "" for c in chars if c.get("name")]
                        except Exception:
                            pass
                r["char_list"] = char_list
                # 테이블: "1. 이름1  2. 이름2  3. 이름3" 형태로 표시
                r["characters"] = "  ".join(f"{i}. {n}" for i, n in enumerate(char_list, 1)) if char_list else ""
            return rows
        except Exception:
            continue
    return []

with tab2:
    st.subheader("계정 목록")

    search_term = st.text_input("계정명 검색 (필터)", placeholder="계정명 일부 입력", key="account_search")
    rows = _get_account_list_full(db)

    if rows:
        if search_term and search_term.strip():
            term = search_term.strip().lower()
            rows = [r for r in rows if term in (str(r.get("id") or "").lower())]
        if rows:
            # 테이블 헤더: 선택(체크박스) | 아이디 | 비밀번호 | 캐릭터명 | 접속 IP
            h0, h1, h2, h3, h4 = st.columns([0.4, 1.5, 1.5, 3, 1.5])
            with h0:
                st.markdown("**선택**")
            with h1:
                st.markdown("**아이디**")
            with h2:
                st.markdown("**비밀번호**")
            with h3:
                st.markdown("**캐릭터명**")
            with h4:
                st.markdown("**접속 IP**")

            for i, r in enumerate(rows):
                c0, c1, c2, c3, c4 = st.columns([0.4, 1.5, 1.5, 3, 1.5])
                with c0:
                    st.checkbox("선택", key=f"del_cb_{r.get('uid')}_{i}", label_visibility="collapsed")
                with c1:
                    st.text(r.get("id") or "")
                with c2:
                    st.text(r.get("pw") or "")
                with c3:
                    st.text(r.get("characters") or "")
                with c4:
                    st.text(r.get("last_ip") or "")

            st.caption("캐릭터명은 한 계정당 여러 명일 경우 번호로 표시됩니다.")
            st.caption(f"총 {len(rows)}개 계정")

            # 선택한 계정 삭제
            if st.button("선택한 계정 삭제", type="primary", key="batch_del_accounts"):
                to_delete = []
                for i, r in enumerate(rows):
                    uid = r.get("uid")
                    if uid is not None and st.session_state.get(f"del_cb_{uid}_{i}", False):
                        to_delete.append((uid, r.get("id") or "?"))
                if not to_delete:
                    st.warning("삭제할 계정을 체크해 주세요.")
                else:
                    try:
                        with db.connection.cursor() as cur:
                            uids = [uid for uid, _ in to_delete]
                            for uid in uids:
                                try:
                                    cur.execute("DELETE FROM characters WHERE account_uid = %s", (uid,))
                                except Exception:
                                    pass
                                cur.execute("DELETE FROM accounts WHERE uid = %s", (uid,))
                            db.connection.commit()
                        st.success(f"✅ {len(to_delete)}개 계정이 삭제되었습니다.")
                        st.rerun()
                    except Exception as e:
                        if db.connection:
                            db.connection.rollback()
                        st.error(f"❌ 삭제 실패: {e}")

            st.divider()
            st.subheader("계정 수정")
            ids = [r["id"] for r in rows]
            choice_idx = st.selectbox("수정할 계정 선택", range(len(ids)), format_func=lambda i: ids[i], key="account_edit_choice")
            if choice_idx is not None and 0 <= choice_idx < len(rows):
                acc = rows[choice_idx]
                # 선택한 계정이 바뀌면 입력값이 갱신되도록 key에 choice_idx 포함
                pk = f"{choice_idx}_{acc.get('uid')}"
                with st.form("account_edit_form", clear_on_submit=False):
                    new_id = st.text_input("아이디", value=acc.get("id") or "", key=f"edit_id_{pk}")
                    new_pw = st.text_input("비밀번호", value=acc.get("pw") or "", type="password", key=f"edit_pw_{pk}")
                    char_list = acc.get("char_list") or []
                    if char_list:
                        st.markdown("**캐릭터명 (참고)**")
                        for i, name in enumerate(char_list, 1):
                            st.markdown(f"- {name}")
                    else:
                        st.caption("캐릭터 없음")
                    new_ip = st.text_input("접속 IP", value=acc.get("last_ip") or "", key=f"edit_ip_{pk}")
                    submitted = st.form_submit_button("저장")
                    if submitted:
                        uid = acc.get("uid")
                        if uid is None:
                            st.error("uid를 찾을 수 없습니다.")
                        elif not new_id or not new_id.strip():
                            st.error("아이디를 입력하세요.")
                        else:
                            try:
                                with db.connection.cursor() as cur:
                                    cur.execute("UPDATE accounts SET id = %s, pw = %s WHERE uid = %s", (new_id.strip(), new_pw, uid))
                                    if new_ip is not None and "last_ip" in acc:
                                        try:
                                            cur.execute("UPDATE accounts SET last_ip = %s WHERE uid = %s", (new_ip.strip(), uid))
                                        except Exception:
                                            pass
                                    try:
                                        cur.execute("UPDATE characters SET account = %s WHERE account_uid = %s", (new_id.strip(), uid))
                                    except Exception:
                                        pass
                                db.connection.commit()
                                st.success("✅ 수정되었습니다.")
                                st.rerun()
                            except Exception as e:
                                if db.connection:
                                    db.connection.rollback()
                                st.error(f"❌ 수정 실패: {e}")
        else:
            st.info("검색 결과가 없습니다.")
    else:
        st.info("등록된 계정이 없습니다.")

# ========== 탭 3: GM 권한 관리 ==========
with tab3:
    st.subheader("GM 권한 관리")
    st.caption("게임 서버는 로그인 시 accounts.uid로 access_level을 읽어 캐릭터에 적용합니다. 권한 변경 후 **캐릭터를 한 번 로그아웃 후 재접속**해야 반영됩니다.")
    with st.expander("❓ GM 권한이 게임에 안 먹힐 때"):
        st.markdown("""
        1. **access_level 컬럼**이 accounts 테이블에 있는지 확인 (없으면 위 '컬럼 추가' 버튼 사용).
        2. GM 권한 부여 후 해당 계정으로 **캐릭터 선택 → 월드 입장**까지 한 뒤, 한 번 **로그아웃 후 다시 접속**해 보세요.
        3. 서버 코드(CharactersDatabase 등)를 수정했다면 **게임 서버 재시작**이 필요합니다.
        4. 이 서버가 **운영자 세트 아이템**(오크족 망토 등) 착용으로 GM을 주는 방식이면, 아이템 관리에서 세트를 지급한 뒤 착용해야 합니다.
        """)

    # 서버 스키마: id 컬럼 사용 (account_name 아님)
    acc_list = db.fetch_all("SELECT id FROM accounts ORDER BY id")
    if not acc_list:
        acc_list = db.fetch_all("SELECT account_name FROM accounts ORDER BY account_name")
        id_col = "account_name"
    else:
        id_col = "id"

    if not acc_list:
        st.info("등록된 계정이 없습니다.")
    else:
        acc_names = [r[id_col] for r in acc_list]
        selected = st.selectbox("계정 선택", acc_names, key="gm_select_account")

        if selected:
            # 현재 권한 표시 (access_level 없어도 아래 버튼은 항상 표시)
            level_text = "확인 불가"
            try:
                if id_col == "id":
                    info = db.fetch_one("SELECT id, access_level FROM accounts WHERE id = %s", (selected,))
                else:
                    info = db.fetch_one("SELECT account_name, access_level FROM accounts WHERE account_name = %s", (selected,))
                if info is not None:
                    level = info.get("access_level")
                    level_int = int(level) if level is not None else 0
                    level_text = "GM 계정" if level_int >= 200 else "일반 계정"
            except Exception:
                level_text = "확인 불가 (access_level 컬럼 없을 수 있음)"

            st.info("**현재 권한:** " + level_text)

            if "확인 불가" in level_text or "access_level" in level_text:
                if st.button("📌 accounts 테이블에 access_level 컬럼 추가", key="btn_add_access_level"):
                    try:
                        db.execute_query(
                            "ALTER TABLE accounts ADD COLUMN access_level INT NOT NULL DEFAULT 0 COMMENT '0=일반, 200=GM'"
                        )
                        st.success("✅ access_level 컬럼이 추가되었습니다. 페이지를 새로고침해 주세요.")
                        st.rerun()
                    except Exception as e:
                        err = str(e).lower()
                        if "duplicate" in err or "already exists" in err:
                            st.info("ℹ️ access_level 컬럼이 이미 있습니다. 계정을 다시 선택해 보세요.")
                        else:
                            st.error(f"❌ 추가 실패: {e}")

            st.write("**GM 권한 부여**")
            if st.button("GM 권한 부여", key="btn_grant_gm"):
                try:
                    # 서버는 LOWER(id)로 조회하므로 UPDATE도 LOWER(id)로 일치시킴
                    if id_col == "id":
                        ok = db.execute_query(
                            "UPDATE accounts SET access_level = 200 WHERE LOWER(id) = LOWER(%s)",
                            (selected.strip(),),
                        )
                    else:
                        ok = db.execute_query("UPDATE accounts SET access_level = 200 WHERE account_name = %s", (selected,))
                    if ok:
                        # 반영 확인: uid, access_level 재조회 (서버는 uid로 access_level 읽음)
                        verify = db.fetch_one(
                            "SELECT uid, access_level FROM accounts WHERE LOWER(" + ("id" if id_col == "id" else "account_name") + ") = LOWER(%s)",
                            (selected.strip(),),
                        )
                        if verify and verify.get("access_level") == 200:
                            uid_val = verify.get("uid", "?")
                            st.success(f"✅ GM 권한을 부여했습니다. (DB 반영: uid={uid_val}, access_level=200) **캐릭터 재접속 후** 적용됩니다.")
                        else:
                            st.success("✅ GM 권한을 부여했습니다. **캐릭터 재접속 후** 적용됩니다.")
                        st.rerun()
                    else:
                        st.warning("access_level 컬럼이 없을 수 있습니다. DB에 access_level 컬럼을 추가해 주세요.")
                except Exception as e:
                    st.error(f"❌ 실패: {e}")

            st.write("**GM 권한 해제**")
            if st.button("GM 권한 해제", key="btn_revoke_gm", type="secondary"):
                try:
                    if id_col == "id":
                        ok = db.execute_query(
                            "UPDATE accounts SET access_level = 0 WHERE LOWER(id) = LOWER(%s)",
                            (selected.strip(),),
                        )
                    else:
                        ok = db.execute_query("UPDATE accounts SET access_level = 0 WHERE account_name = %s", (selected,))
                    if ok:
                        try:
                            uid_row = db.fetch_one(
                                "SELECT uid FROM accounts WHERE LOWER(" + ("id" if id_col == "id" else "account_name") + ") = LOWER(%s)",
                                (selected.strip(),),
                            )
                            if uid_row and uid_row.get("uid") is not None:
                                db.execute_query("UPDATE characters SET gm = 0 WHERE account_uid = %s", (uid_row["uid"],))
                        except Exception:
                            pass
                        st.success("✅ GM 권한을 해제했습니다. **캐릭터 재접속 시** 반영됩니다.")
                        st.rerun()
                    else:
                        st.warning("access_level 컬럼이 없을 수 있습니다. DB에 access_level 컬럼을 추가해 주세요.")
                except Exception as e:
                    st.error(f"❌ 실패: {e}")

            st.divider()
            st.write("**계정 삭제**")
            confirm_delete = st.checkbox("계정 삭제를 확인합니다 (위험: 복구 불가)", key="confirm_delete_account")
            if st.button("계정 삭제", key="btn_delete_account", type="secondary"):
                if not confirm_delete:
                    st.warning("⚠️ 위 확인 체크박스를 선택한 뒤 삭제해 주세요.")
                else:
                    try:
                        if id_col == "id":
                            db.execute_query("DELETE FROM accounts WHERE id = %s", (selected,))
                        else:
                            db.execute_query("DELETE FROM accounts WHERE account_name = %s", (selected,))
                        st.success("✅ 계정이 삭제되었습니다.")
                        st.rerun()
                    except Exception as e:
                        st.error(f"❌ 삭제 실패: {e}")
