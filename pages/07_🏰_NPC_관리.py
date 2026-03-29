"""
NPC 관리 페이지 (NPC 목록, NPC 추가, NPC 배치 제거, 상점 관리)
- 상점: 리니지 서버 npc_shop / item 테이블 사용 (lin200)
"""
from __future__ import annotations

import hashlib
from pathlib import Path

import streamlit as st
from utils.db_manager import get_db
from utils.gm_feedback import show_pending_feedback, queue_feedback
from utils.gm_tabs import gm_section_tabs
from utils.field_help_ko import NPC_HELP as NH
from utils.npc_shop_display_price import (
    calc_buy_display_price,
    calc_sell_display_price,
    fetch_item_row_by_display_name,
    invert_buy_display_to_db_price,
    load_lineage_shop_conf,
    resolve_pack_dir,
)

# 자주 쓰는 맵 번호 → 이름 (DB에 맵 테이블이 없을 때 보조). 서버팩마다 다를 수 있음.
_COMMON_MAP_NAMES = {
    0: "말하는섬",
    1: "숲",
    4: "기란",
    304: "글루디오",
    320: "말섬던전",
    340: "켄트",
    350: "윈다우드",
    360: "요정숲",
    370: "은기사 마을",
    430: "화전민 마을",
    440: "오렌",
    53: "기란감옥 1층",
    54: "기란감옥 2층",
    55: "기란감옥 3층",
    56: "기란감옥 4층",
}


def _try_merge_map_names_from_db(db, base: dict) -> dict:
    """mapids / maps 등 있으면 id→이름 병합 (컬럼명 자동 추정)."""
    out = dict(base)
    try:
        tables = set(db.get_all_tables())
        for cand in ("mapids", "maps"):
            if cand not in tables:
                continue
            rows = db.fetch_all(f"SELECT * FROM `{cand}` LIMIT 3000")
            if not rows or not isinstance(rows[0], dict):
                continue
            keys = {str(k).lower(): k for k in rows[0].keys()}
            id_key = next((keys[k] for k in ("id", "mapid", "map_id", "loc_id") if k in keys), None)
            name_key = next((keys[k] for k in ("name", "locationname", "map_name", "note", "title") if k in keys), None)
            if not id_key or not name_key:
                continue
            for r in rows:
                try:
                    mid = int(r[id_key])
                    nm = (r.get(name_key) or "").strip()
                    if nm:
                        out[mid] = nm
                except (TypeError, ValueError):
                    pass
            break
    except Exception:
        pass
    return out


def _map_label(map_id: int, names: dict) -> str:
    n = names.get(map_id)
    if n:
        return f"{n}(맵{map_id})"
    return f"맵{map_id}"


def _row_val(row: dict, *keys: str):
    for k in keys:
        if k in row and row[k] is not None:
            return row[k]
    return None


def _ensure_npc_face_player_on_talk_column(db) -> bool:
    """npc.face_player_on_talk 없으면 추가 (대화 시 PC 방향으로 heading)."""
    try:
        struct = db.get_table_structure("npc")
        fields = {str(r.get("Field") or "") for r in (struct or [])}
        if "face_player_on_talk" in fields:
            return True
        ok, _err = db.execute_query_ex(
            "ALTER TABLE npc ADD COLUMN face_player_on_talk TINYINT(1) NOT NULL DEFAULT 0 "
            "COMMENT '1=GM강제 바라보기 0=예전 클래스 동작' AFTER arrowGfx",
            (),
        )
        return ok
    except Exception:
        return False


def _ensure_npc_shop_enabled_column(db) -> bool:
    """npc_shop.shop_enabled 없으면 추가."""
    try:
        struct = db.get_table_structure("npc_shop")
        fields = {str(r.get("Field") or "") for r in (struct or [])}
        if "shop_enabled" in fields:
            return True
        ok, _err = db.execute_query_ex(
            "ALTER TABLE npc_shop ADD COLUMN shop_enabled TINYINT(1) NOT NULL DEFAULT 1 "
            "COMMENT '1=상점노출 0=비활성' AFTER aden_type",
            (),
        )
        return ok
    except Exception:
        return False


def _shop_enabled_int_from_row(d: dict) -> int:
    v = d.get("shop_enabled")
    if v is None:
        return 1
    try:
        return 0 if int(v) == 0 else 1
    except (TypeError, ValueError):
        return 1


def _npc_shop_price_int(v) -> int:
    try:
        return max(0, int(v))
    except (TypeError, ValueError):
        return 0


def _sync_streamlit_price_input(source_key: str, widget_key: str, source_tuple: tuple, prc_show: int) -> None:
    """
    Streamlit number_input 은 key 가 있으면 이후 rerun 에서 value= 를 무시한다.
    DB의 npc_shop.price 나 추정식 결과가 바뀌면 표(게임표시)와 입력란을 맞춘다.
    """
    if source_key not in st.session_state:
        st.session_state[source_key] = source_tuple
        st.session_state[widget_key] = int(prc_show)
    elif st.session_state[source_key] != source_tuple:
        st.session_state[source_key] = source_tuple
        st.session_state[widget_key] = int(prc_show)
    elif widget_key not in st.session_state:
        st.session_state[widget_key] = int(prc_show)


def _npc_spawn_index_by_npcname(db) -> dict[str, list[dict]]:
    """npc_spawnlist의 npcName 기준으로 스폰 행 목록 (같은 NPC 여러 스폰 허용)."""
    try:
        rows = db.fetch_all(
            "SELECT `name`, `npcName`, `locX`, `locY`, `locMap` FROM `npc_spawnlist` ORDER BY `npcName`, `locMap`, `locX`, `locY`"
        )
    except Exception:
        return {}
    idx: dict[str, list[dict]] = {}
    for r in rows or []:
        key = str(_row_val(r, "npcName", "npcname") or "").strip()
        if not key:
            continue
        idx.setdefault(key, []).append(r)
    return idx


def _search_shop_items(db, query: str, *, limit: int = 400) -> list:
    """상점 추가용 item 검색. 기존 LIMIT 20 + 무정렬 때문에 '이동' 검색 시 이동주문서가 빠지는 문제 보완."""
    q = (query or "").strip()
    if not q:
        return []
    lim = max(1, min(int(limit), 2000))
    try:
        return db.fetch_all(
            f"""
            SELECT `아이템이름`, `구분2` FROM `item`
            WHERE `아이템이름` LIKE CONCAT('%%', %s, '%%')
               OR IFNULL(`구분2`, '') LIKE CONCAT('%%', %s, '%%')
            ORDER BY `아이템이름`
            LIMIT {lim}
            """,
            (q, q),
        )
    except Exception:
        return db.fetch_all(
            f"""
            SELECT `아이템이름` FROM `item`
            WHERE `아이템이름` LIKE CONCAT('%%', %s, '%%')
            ORDER BY `아이템이름`
            LIMIT {lim}
            """,
            (q,),
        )


def _format_shop_item_row(row: dict) -> str:
    name = str(row.get("아이템이름") or "").strip()
    t2 = str(row.get("구분2") or "").strip()
    if t2:
        return f"{name}  [{t2}]"
    return name


def _format_world_placements(spawns: list[dict] | None, map_names: dict) -> str:
    """여러 스폰을 한 셀에 표시. 없으면 빈 문자열."""
    if not spawns:
        return ""
    parts = []
    for s in spawns:
        mid = int(_row_val(s, "locMap", "locmap") or 0)
        lx = int(_row_val(s, "locX", "locx") or 0)
        ly = int(_row_val(s, "locY", "locy") or 0)
        sk = str(_row_val(s, "name", "Name") or "").strip()
        ml = _map_label(mid, map_names)
        if sk:
            parts.append(f"{ml} X={lx} Y={ly} [스폰키:{sk}]")
        else:
            parts.append(f"{ml} X={lx} Y={ly}")
    return " · ".join(parts)


st.set_page_config(page_title="NPC 관리", page_icon="🏰", layout="wide")
st.title("🏰 NPC 관리")

db = get_db()
_db_ok, _db_msg = db.test_connection()
if not _db_ok:
    st.error(f"❌ DB 연결 실패: {_db_msg}")
    st.stop()
_ensure_npc_face_player_on_talk_column(db)
show_pending_feedback()
_NPC_TAB_LABELS = [
    "📋 NPC 목록",
    "➕ NPC 추가",
    "✏️ NPC 수정",
    "📍 NPC 배치",
    "🗑️ NPC 배치 제거",
    "🏪 상점 관리",
    "📍 스폰 위치 수정",
]
_npc_ti = gm_section_tabs("npc_admin", _NPC_TAB_LABELS)

# ========== tab1: NPC 목록 ==========
if _npc_ti == 0:
    st.subheader("📋 NPC 목록")
    try:
        # name이 숫자형이면 '0' 등이 정수로 오므로 str 통일. LIMIT 제거해 전체 조회
        npc_list = db.fetch_all("SELECT name, nameid, type, gfxid FROM npc ORDER BY name")
        if npc_list:
            import pandas as pd

            map_names = _try_merge_map_names_from_db(db, _COMMON_MAP_NAMES)
            spawn_by_npc = _npc_spawn_index_by_npcname(db)

            for r in npc_list:
                r["name"] = str(r["name"]) if r.get("name") is not None else ""
                sp = spawn_by_npc.get(r["name"])
                r["월드 배치 (맵·좌표)"] = _format_world_placements(sp, map_names)

            df_npc = pd.DataFrame(npc_list).rename(
                columns={"name": "이름", "nameid": "NAMEID", "type": "타입", "gfxid": "그래픽 ID"}
            )
            preferred = ["이름", "월드 배치 (맵·좌표)", "NAMEID", "타입", "그래픽 ID"]
            df_npc = df_npc[[c for c in preferred if c in df_npc.columns] + [c for c in df_npc.columns if c not in preferred]]

            search_npc = st.text_input("🔍 NPC 이름 검색 (이름·NAMEID 일부 입력)", placeholder="예: 0, 파워볼, 상인", key="npc_list_search")
            if search_npc and search_npc.strip():
                mask = df_npc["이름"].astype(str).str.contains(search_npc.strip(), case=False, na=False) | df_npc["NAMEID"].astype(str).str.contains(search_npc.strip(), case=False, na=False)
                df_npc = df_npc.loc[mask]
            st.caption(
                f"총 {len(df_npc)}건 · **월드 배치** 열은 `npc_spawnlist` 기준입니다. "
                "같은 NPC를 여러 곳에 두면 ` · ` 로 구분해 모두 표시합니다. 배치 행이 없으면 빈칸입니다. "
                "맵 이름은 DB(`mapids` 등) 또는 내장 사전으로 보강됩니다."
            )
            st.dataframe(df_npc, height=400, width='stretch')
        else:
            st.info("NPC 목록을 불러오지 못했거나 비어 있습니다.")
    except Exception as e:
        st.warning(f"NPC 테이블 조회 실패 (테이블/컬럼 확인): {e}")

# ========== tab2: NPC 추가 ==========
elif _npc_ti == 1:
    st.subheader("➕ NPC 추가")
    st.caption("npc 테이블에 등록하고, 원하면 맵 좌표를 넣어 스폰까지 한 번에 등록할 수 있습니다. 서버 재시작 후 반영됩니다.")
    try:
        with st.form("npc_add_form"):
            # --- 기본 정보 (한글 설명은 라벨 옆 ? 에 마우스 올리면 표시) ---
            st.markdown("**기본 정보**")
            c1, c2 = st.columns(2)
            with c1:
                add_name = st.text_input(
                    "NPC 이름 *",
                    placeholder="예: 파워볼",
                    max_chars=64,
                    help="게임 내에서 참조하는 NPC 고유 이름. npc·npc_spawnlist에서 사용합니다."
                )
                add_type = st.text_input(
                    "타입 (type)",
                    value="default",
                    max_chars=32,
                    help="NPC 종류/클래스 구분. 서버 코드에서 특정 type일 때만 동작하는 NPC가 있습니다."
                )
                add_nameid = st.text_input(
                    "NAMEID (nameid)",
                    value="0",
                    placeholder="예: 50999 또는 $50999",
                    max_chars=32,
                    help="클라이언트 표시용 ID. 숫자 또는 $숫자 형식."
                )
                add_gfxid = st.number_input(
                    "그래픽 ID (gfxid)",
                    min_value=0,
                    value=0,
                    help="NPC 외형(모델) ID. 0이면 기본 모델."
                )
                add_gfxMode = st.number_input(
                    "그래픽 모드 (gfxMode)",
                    min_value=0,
                    value=0,
                    help="모델 애니메이션/모드. 보통 0."
                )
            with c2:
                add_hp = st.number_input(
                    "체력 (hp)",
                    min_value=1,
                    value=1,
                    help="NPC 체력. 1이면 즉사 불가(대화용) 등."
                )
                add_lawful = st.number_input(
                    "성향 (lawful)",
                    value=0,
                    help="성향 값. 0=중립 등. 일부 NPC는 이 값으로 대화 분기."
                )
                add_light = st.number_input(
                    "광원 (light)",
                    value=0,
                    help="주변 밝기/이펙트용. 보통 0."
                )
                add_ai = st.selectbox(
                    "AI 사용 (ai)",
                    ["false", "true"],
                    index=0,
                    help="true면 몬스터처럼 AI 동작. 대화/상점 NPC는 false."
                )
                add_areaatk = st.number_input(
                    "범위 공격 (areaatk)",
                    min_value=0,
                    value=0,
                    help="범위 공격 관련. 0=해당 없음."
                )
                add_arrowGfx = st.number_input(
                    "화살 그래픽 (arrowGfx)",
                    min_value=0,
                    value=0,
                    help="원거리 공격 시 화살 이펙트 ID. 0=없음."
                )
                add_face_talk = st.checkbox(
                    "클릭 시 플레이어 방향으로 바라보기 (GM 강제)",
                    value=False,
                    help=NH.get("face_player_on_talk", ""),
                )
            # --- 스폰 위치 (맵에 배치) ---
            st.markdown("---")
            st.markdown("**스폰 위치 (맵에 배치)**")
            st.caption("채우면 npc_spawnlist에 등록되어, 서버 재시작 후 해당 좌표에 NPC가 나타납니다.")
            spawn_col1, spawn_col2, spawn_col3 = st.columns(3)
            with spawn_col1:
                add_locX = st.number_input("X 좌표 (locX)", value=33449, help="맵 내 X 좌표. 기란 예시: 33449")
                add_locY = st.number_input("Y 좌표 (locY)", value=32825, help="맵 내 Y 좌표. 기란 예시: 32825")
            with spawn_col2:
                add_locMap = st.number_input("맵 ID (locMap)", value=4, min_value=0, help="맵 번호. 4=기란 등.")
                add_heading = st.number_input("방향 (heading)", value=0, min_value=0, max_value=7, help="NPC가 바라보는 방향. 0~7")
            with spawn_col3:
                add_respawn = st.number_input("리스폰 (respawn)", value=0, min_value=0, help="0=사라지지 않음, 1 등이면 리스폰")
                add_title = st.text_input("말풍선 제목 (title)", value="", placeholder="NPC 머리 위에 표시될 이름", max_chars=64, help="비우면 npc 이름 사용")
            add_do_spawn = st.checkbox("이 좌표로 스폰 등록 (npc_spawnlist에 추가)", value=True, help="체크 시 NPC 추가 시 위 좌표로 스폰도 함께 등록")

            submitted = st.form_submit_button("NPC 추가")
            if submitted and add_name and add_name.strip():
                name = add_name.strip()
                ok, ins_err = db.execute_query_ex(
                    """INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx, face_player_on_talk)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    (name, add_type.strip() or "default", add_nameid.strip() or "0",
                     add_gfxid, add_gfxMode, add_hp, add_lawful, add_light, add_ai, add_areaatk, add_arrowGfx,
                     1 if add_face_talk else 0)
                )
                if ok:
                    msg = f"✅ 반영되었습니다. NPC '{name}' 추가됨."
                    if add_do_spawn:
                        spawn_key = (name.replace(" ", "_") + "_1")[:64]
                        title_val = (add_title.strip() or name)[:64]
                        ok2, sp_err = db.execute_query_ex(
                            """INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
                               VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                               ON DUPLICATE KEY UPDATE npcName=%s, locX=%s, locY=%s, locMap=%s, heading=%s, respawn=%s, title=%s""",
                            (spawn_key, name, add_locX, add_locY, add_locMap, add_heading, add_respawn, title_val,
                             name, add_locX, add_locY, add_locMap, add_heading, add_respawn, title_val)
                        )
                        if ok2:
                            msg += f" 스폰 등록됨 (X={add_locX}, Y={add_locY}, 맵={add_locMap})."
                        else:
                            msg += f" (스폰 등록 실패: {sp_err})"
                    msg += " 서버 재시작 후 반영됩니다."
                    queue_feedback("success", msg)
                    st.rerun()
                else:
                    queue_feedback("error", f"❌ NPC 추가 실패: {ins_err}")
                    st.rerun()
            elif submitted and not (add_name and add_name.strip()):
                st.warning("NPC 이름을 입력하세요.")
    except Exception as e:
        st.error(f"NPC 추가 오류: {e}")

# ========== tab3: NPC 수정 (기존 NPC 이미지/세부정보 변경) ==========
elif _npc_ti == 2:
    st.subheader("✏️ NPC 수정")
    st.caption(
        "이미 등록된 NPC의 그래픽(gfxid), 타입, 체력, **클릭 시 플레이어 바라보기** 등을 변경합니다. "
        "DB 반영 후 서버 **재시작** 또는 게임 내 **npc 테이블 리로드**가 있으면 그때 적용됩니다."
    )
    try:
        npc_names = db.fetch_all("SELECT name FROM npc ORDER BY name")
        if not npc_names:
            st.info("수정할 NPC가 없습니다. NPC 추가 탭에서 먼저 등록하세요.")
        else:
            # name이 DB에서 숫자(0)로 오면 selectbox에서 누락될 수 있으므로 문자열로 통일
            edit_choices = [str(r["name"]) if r.get("name") is not None else "" for r in npc_names]
            edit_name = st.selectbox("수정할 NPC 선택", edit_choices, key="npc_edit_select")
            if edit_name:
                row = db.fetch_one(
                    "SELECT name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx, face_player_on_talk FROM npc WHERE name = %s",
                    (str(edit_name),),
                )
                if row:
                    # 선택 NPC가 바뀌어도 위젯 key가 같으면 session_state 가 이전 값을 유지함 → NPC별 고유 접미사
                    _ek = hashlib.md5(str(edit_name).encode("utf-8", errors="replace")).hexdigest()[:16]
                    with st.form(f"npc_edit_form_{_ek}"):
                        st.markdown("**기본 정보** (이름은 변경 불가)")
                        st.text_input("NPC 이름", value=row["name"], disabled=True, key=f"npc_edit_name_ro_{_ek}")
                        c1, c2 = st.columns(2)
                        with c1:
                            edit_type = st.text_input("타입 (type)", value=row.get("type") or "default", max_chars=32, key=f"npc_edit_type_{_ek}", help=NH["type"])
                            edit_nameid = st.text_input("NAMEID (nameid)", value=row.get("nameid") or "0", max_chars=32, key=f"npc_edit_nameid_{_ek}", help=NH["nameid"])
                            edit_gfxid = st.number_input("그래픽 ID (gfxid)", min_value=0, value=int(row.get("gfxid") or 0), key=f"npc_edit_gfxid_{_ek}", help="NPC 외형(이미지) 변경")
                            edit_gfxMode = st.number_input("그래픽 모드 (gfxMode)", min_value=0, value=int(row.get("gfxMode") or 0), key=f"npc_edit_gfxMode_{_ek}", help=NH["gfxMode"])
                        with c2:
                            edit_hp = st.number_input("체력 (hp)", min_value=1, value=int(row.get("hp") or 1), key=f"npc_edit_hp_{_ek}", help=NH["hp"])
                            edit_lawful = st.number_input("성향 (lawful)", value=int(row.get("lawful") or 0), key=f"npc_edit_lawful_{_ek}", help=NH["lawful"])
                            edit_light = st.number_input("광원 (light)", value=int(row.get("light") or 0), key=f"npc_edit_light_{_ek}", help=NH["light"])
                            edit_ai = st.selectbox("AI 사용 (ai)", ["false", "true"], index=1 if (row.get("ai") and str(row.get("ai")).lower() == "true") else 0, key=f"npc_edit_ai_{_ek}", help=NH["ai"])
                            edit_areaatk = st.number_input("범위 공격 (areaatk)", min_value=0, value=int(row.get("areaatk") or 0), key=f"npc_edit_areaatk_{_ek}", help=NH["areaatk"])
                            edit_arrowGfx = st.number_input("화살 그래픽 (arrowGfx)", min_value=0, value=int(row.get("arrowGfx") or 0), key=f"npc_edit_arrowGfx_{_ek}", help=NH["arrowGfx"])
                            _fpt = row.get("face_player_on_talk")
                            _fpt_on = False if _fpt is None else (int(_fpt) != 0)
                            edit_face_talk = st.checkbox(
                                "클릭 시 플레이어 방향으로 바라보기",
                                value=_fpt_on,
                                key=f"npc_edit_face_talk_{_ek}",
                                help=NH.get("face_player_on_talk", ""),
                            )
                        if st.form_submit_button("수정 반영"):
                            ok, uerr = db.execute_query_ex(
                                """UPDATE npc SET type=%s, nameid=%s, gfxid=%s, gfxMode=%s, hp=%s, lawful=%s, light=%s, ai=%s, areaatk=%s, arrowGfx=%s, face_player_on_talk=%s WHERE name=%s""",
                                (edit_type.strip() or "default", edit_nameid.strip() or "0", edit_gfxid, edit_gfxMode, edit_hp, edit_lawful, edit_light, edit_ai, edit_areaatk, edit_arrowGfx, 1 if edit_face_talk else 0, edit_name)
                            )
                            if ok:
                                queue_feedback("success", f"✅ NPC '{edit_name}' 수정 반영되었습니다. 서버 재시작 후 적용됩니다.")
                                st.rerun()
                            else:
                                queue_feedback("error", f"❌ NPC 수정 실패: {uerr}")
                                st.rerun()
                else:
                    st.warning("해당 NPC를 찾을 수 없습니다.")
    except Exception as e:
        st.error(f"NPC 수정 오류: {e}")

# ========== tab4: 기존 NPC 배치 (맵에 스폰 추가) ==========
elif _npc_ti == 3:
    st.subheader("📍 NPC 배치")
    st.caption("이미 npc 테이블에 있는 NPC를 원하는 맵·좌표에 배치합니다. npc_spawnlist에 스폰을 추가합니다. 서버 재시작 후 반영됩니다.")
    st.info("**좌표 통일**: 게임 내에서 `[명령어]맵` 또는 `[명령어]좌표` 로 표시되는 **X, Y, 맵번호**가 DB(locX, locY, locMap)와 **동일**합니다. 원하는 위치에 서서 좌표를 확인한 값을 그대로 입력하면 해당 위치에 NPC가 배치됩니다.")
    try:
        npc_for_spawn = db.fetch_all("SELECT name FROM npc ORDER BY name")
        if not npc_for_spawn:
            st.info("배치할 NPC가 없습니다. NPC 추가 탭에서 먼저 NPC를 등록하세요.")
        else:
            with st.form("npc_place_form"):
                # name이 '0' 등 숫자로 오면 문자열로 통일해 드롭다운에 표시
                place_choices = [str(r["name"]) if r.get("name") is not None else "" for r in npc_for_spawn]
                place_npc_name = st.selectbox("배치할 NPC 선택", place_choices, key="place_npc")
                st.markdown("**스폰 위치** (게임 내 좌표 명령으로 확인한 X, Y, 맵을 그대로 입력)")
                pc1, pc2, pc3 = st.columns(3)
                with pc1:
                    place_locX = st.number_input("X 좌표", value=33449, key="place_x", help="게임에서 [명령어]좌표 로 확인한 X")
                    place_locY = st.number_input("Y 좌표", value=32825, key="place_y", help="게임에서 [명령어]좌표 로 확인한 Y")
                with pc2:
                    place_locMap = st.number_input("맵 ID", value=4, min_value=0, key="place_map", help=NH["locMap"])
                    place_heading = st.number_input("방향 (0~7)", value=0, min_value=0, max_value=7, key="place_heading", help=NH["heading"])
                with pc3:
                    place_respawn = st.number_input("리스폰", value=0, min_value=0, key="place_respawn", help=NH["respawn"])
                    place_title = st.text_input("말풍선 제목 (비우면 NPC 이름)", value="", max_chars=64, key="place_title")
                place_spawn_key = st.text_input("스폰 이름 (고유키, 비우면 자동 생성)", value="", placeholder="예: 상인_기란_1", max_chars=64, help="npc_spawnlist의 name. 같은 NPC를 여러 곳에 배치할 때마다 서로 다른 값 사용")
                if st.form_submit_button("배치 추가"):
                    spawn_name = place_spawn_key.strip() if place_spawn_key and place_spawn_key.strip() else None
                    if not spawn_name:
                        existing = db.fetch_all("SELECT name FROM npc_spawnlist WHERE npcName = %s", (place_npc_name,))
                        n = len(existing) + 1
                        spawn_name = (place_npc_name.replace(" ", "_") + "_" + str(n))[:64]
                    title_val = (place_title.strip() or place_npc_name)[:64]
                    ok, perr = db.execute_query_ex(
                        """INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                           ON DUPLICATE KEY UPDATE npcName=%s, locX=%s, locY=%s, locMap=%s, heading=%s, respawn=%s, title=%s""",
                        (spawn_name, place_npc_name, place_locX, place_locY, place_locMap, place_heading, place_respawn, title_val,
                         place_npc_name, place_locX, place_locY, place_locMap, place_heading, place_respawn, title_val)
                    )
                    if ok:
                        queue_feedback(
                            "success",
                            f"✅ '{place_npc_name}' 배치 추가됨 (스폰명: {spawn_name}, X={place_locX}, Y={place_locY}, 맵={place_locMap}). 서버 재시작 후 반영됩니다.",
                        )
                        st.rerun()
                    else:
                        queue_feedback("error", f"❌ 배치 추가 실패: {perr}")
                        st.rerun()
    except Exception as e:
        st.error(f"NPC 배치 오류: {e}")

# ========== tab5: NPC 배치 제거 (npc_spawnlist 영구 삭제 + 실행 중 서버 디스폰) ==========
elif _npc_ti == 4:
    st.subheader("🗑️ NPC 배치 제거")
    st.caption(
        "선택한 스폰을 **배치 목록(npc_spawnlist)**에서 삭제합니다. **재시작 후에도** 해당 위치에 다시 나오지 않습니다. "
        "서버가 실행 중이면 **디스폰 명령**을 함께 보내 맵에서도 곧 사라지게 합니다."
    )
    try:
        spawn_list = db.fetch_all(
            "SELECT name, npcName, locX, locY, locMap FROM npc_spawnlist ORDER BY name"
        )
        if spawn_list:
            for r in spawn_list:
                r["name"] = str(r["name"]) if r.get("name") is not None else ""
                r["npcName"] = str(r["npcName"]) if r.get("npcName") is not None else ""
    except Exception as e:
        st.warning(f"테이블 조회 실패 (npc_spawnlist 확인): {e}")
        spawn_list = []

    if spawn_list:
        # DB가 컬럼명을 소문자로 반환할 수 있으므로 대소문자 무시하고 값 추출
        def _row_name(row):
            return str(row.get("name") or row.get("Name") or "").strip()
        def _row_npc_name(row):
            return str(row.get("npcName") or row.get("NpcName") or row.get("npcname") or "").strip()
        search_spawn = st.text_input("🔍 스폰 검색 (스폰 이름·NPC 이름 일부 입력)", placeholder="예: 청상어, 0, 상인", key="npc_del_search")
        if search_spawn and search_spawn.strip():
            q = search_spawn.strip().lower()
            spawn_filtered = [r for r in spawn_list if (q in _row_name(r).lower() or q in _row_npc_name(r).lower())]
        else:
            spawn_filtered = spawn_list
        st.caption(f"총 {len(spawn_filtered)}건 표시 (전체 {len(spawn_list)}건)" + (" — 위 검색 필터 적용" if (search_spawn and search_spawn.strip()) else ""))
        if search_spawn and search_spawn.strip() and len(spawn_filtered) == 0:
            st.info("검색 결과가 없습니다. NPC 목록에만 있고 아직 맵에 배치되지 않았을 수 있습니다. **NPC 배치** 탭에서 먼저 배치한 스폰만 여기 목록에 나옵니다.")
        choices = [f"{_row_name(r)} | {_row_npc_name(r)} (X={r.get('locX')}, Y={r.get('locY')}, 맵={r.get('locMap')})" for r in spawn_filtered]
        spawn_keys = [_row_name(r) for r in spawn_filtered]
        idx = st.selectbox(
            "배치에서 제거할 스폰 선택",
            range(len(choices)),
            format_func=lambda i: choices[i],
            key="npc_del_select"
        )
        del_spawn_name = spawn_keys[idx] if idx is not None and len(spawn_keys) else None

        row1 = st.columns([1, 1])
        with row1[0]:
            if st.button("🗑️ 선택 스폰 배치 제거", key="npc_remove_spawn_btn"):
                if del_spawn_name:
                    ok, del_err = db.execute_query_ex("DELETE FROM npc_spawnlist WHERE name = %s", (del_spawn_name,))
                    if ok:
                        ok_cmd, cmd_err = db.execute_query_ex(
                            "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                            ("npc_despawn", del_spawn_name)
                        )
                        msg = f"✅ 스폰 '{del_spawn_name}'을(를) 배치에서 제거했습니다. 재시작 후에도 해당 배치는 없습니다."
                        if not ok_cmd:
                            msg += f" (참고: 즉시 디스폰 명령 삽입 실패 — {cmd_err})"
                        queue_feedback("success", msg)
                        st.rerun()
                    else:
                        queue_feedback("error", f"❌ 배치 제거 실패: {del_err}")
                        st.rerun()
                else:
                    st.warning("스폰을 선택하세요.")
        with row1[1]:
            if st.button("🔄 목록 새로고침", key="npc_tab3_refresh"):
                queue_feedback("info", "목록을 새로고침했습니다.")
                st.rerun()
    else:
        st.info("npc_spawnlist에 스폰이 없거나 조회할 수 없습니다.")

# ========== tab6: 상점 관리 (npc_shop / item) ==========
elif _npc_ti == 5:
    st.subheader("🏪 NPC 상점 관리")
    st.caption(
        "베리타 등 **이동주문서 89종 패치**는 `2.싱글리니지 팩/db/patch_verita_item_teleport_shop.sql`을 "
        "**이 GM 툴과 같은 DB(MariaDB, `mysql.conf` / `config.py`에 맞는 쪽)** 에 직접 실행해야 "
        "`item`·`npc_shop`에 들어갑니다. (HeidiSQL·DBeaver·`mariadb`/`mysql` CLI 등 아무 클라이언트나 됩니다.) "
        "파일만 두고 실행 안 하면 게임·GM 모두 예전 데이터입니다."
    )
    try:
        _shop_en_ok = _ensure_npc_shop_enabled_column(db)
        if not _shop_en_ok:
            st.warning(
                "⚠️ `npc_shop.shop_enabled` 컬럼을 추가하지 못했습니다. "
                "DB에 ALTER 권한을 주거나 `2.싱글리니지 팩/db/npc_shop_add_shop_enabled.sql` 을 수동 실행하세요. "
                "그 전까지는 **비활성화** 버튼을 쓸 수 없습니다."
            )
        _shop_sel_extra = ", shop_enabled" if _shop_en_ok else ""
        _pack_dir = resolve_pack_dir()
        _shop_conf = load_lineage_shop_conf(_pack_dir)
        if not (_pack_dir.is_dir() and (_pack_dir / "lineage.conf").is_file()):
            st.caption("⚠️ `2.싱글리니지 팩/lineage.conf` 를 찾지 못해 상점 가격 추정에 기본값을 씁니다.")

        # 상점이 있는 NPC 목록 (npc_shop.name 기준)
        상점NPC목록 = db.fetch_all(
            "SELECT DISTINCT name FROM npc_shop ORDER BY name"
        )
        if not 상점NPC목록:
            st.info("등록된 상점 NPC가 없습니다. npc_shop 테이블에 데이터를 추가하세요.")
        else:
            선택목록 = [row["name"] for row in 상점NPC목록]
            선택이름 = st.selectbox("상점 NPC 선택", 선택목록, key="shop_npc_select")
            npc_name = 선택이름

            col1, col2 = st.columns(2)

            # 왼쪽: 판매 물품 (NPC가 파는 것 = buy='true')
            with col1:
                st.markdown("#### 💰 판매 물품 (플레이어 구매)")
                st.caption(
                    "**비활성**: DB 행은 남기고 서버가 상점에 안 올림(삭제와 동일 효과). **활성**으로 다시 켤 수 있습니다. "
                    "적용 후 **npc_shop 리로드** 또는 재시작."
                )
                판매목록 = db.fetch_all(
                    f"""SELECT uid, name, itemname, itemcount, price, sell, buy {_shop_sel_extra}
                       FROM npc_shop 
                       WHERE name = %s AND buy = 'true' 
                       ORDER BY itemname""",
                    (npc_name,),
                )
                if 판매목록:
                    _sell_item_cache: dict[str, dict | None] = {}
                    _sell_df_rows = []
                    for _r in 판매목록:
                        _inm = str(_r.get("itemname") or "")
                        if _inm not in _sell_item_cache:
                            _sell_item_cache[_inm] = fetch_item_row_by_display_name(db, _inm)
                        _ir = _sell_item_cache[_inm]
                        _dpy = calc_buy_display_price(db, _r, _ir, _shop_conf)
                        _row = dict(_r)
                        _row["게임표시(추정)"] = _dpy
                        _sell_df_rows.append(_row)
                    st.dataframe(_sell_df_rows, height=300, width="stretch")
                    st.markdown("**행별 가격·비활성** — 가격 저장 후 **npc_shop 리로드**")
                    st.caption(
                        "가격 입력란은 **게임 상점 창과 같은 금액(추정)** 으로 채웁니다. "
                        "`npc_shop.price` 가 0이면 `item.shop_price`·인챈·축복 등으로 계산합니다. "
                        "성(혈맹) **상점 세율**은 알 수 없어 **0%** 로 둔 값입니다."
                    )
                    st.caption("열: 물품(uid) · 가격 입력(아데나) · **가격 저장** · 비활성/활성")
                    for srow in 판매목록:
                        uid = int(srow.get("uid") or 0)
                        if uid <= 0:
                            continue
                        en = _shop_enabled_int_from_row(srow) if _shop_en_ok else 1
                        itn = str(srow.get("itemname") or "")
                        _ir = _sell_item_cache.get(itn) or fetch_item_row_by_display_name(db, itn)
                        prc_show = calc_buy_display_price(db, srow, _ir, _shop_conf)
                        _pdbc = int(srow.get("price") or 0)
                        _src_k = f"shop_sell_price_src_{npc_name}_{uid}"
                        _wid_k = f"shop_sell_price_{npc_name}_{uid}"
                        _sync_streamlit_price_input(
                            _src_k,
                            _wid_k,
                            (_pdbc, int(prc_show)),
                            int(prc_show),
                        )
                        if _shop_en_ok:
                            c_a, c_b, c_c, c_d = st.columns([2.1, 1.0, 0.65, 0.65])
                        else:
                            c_a, c_b, c_c = st.columns([2.4, 1.1, 0.85])
                            c_d = None
                        with c_a:
                            _off = _shop_en_ok and not en
                            st.caption(("⏸ " if _off else "") + f"`uid={uid}` · {itn}")
                        with c_b:
                            new_p = st.number_input(
                                "가격",
                                min_value=0,
                                step=1,
                                key=_wid_k,
                                label_visibility="collapsed",
                            )
                        with c_c:
                            if st.button("가격 저장", key=f"shop_sell_psave_{uid}"):
                                db_p = invert_buy_display_to_db_price(
                                    int(new_p),
                                    bool(_shop_conf.get("add_tax")),
                                    0.0,
                                )
                                ok_u, e_u = db.execute_query_ex(
                                    "UPDATE npc_shop SET price=%s WHERE uid=%s AND name=%s AND buy='true'",
                                    (db_p, uid, npc_name),
                                )
                                if ok_u:
                                    queue_feedback(
                                        "success",
                                        f"판매 가격 저장(uid={uid}, npc_shop.price={db_p}, 표시≈{int(new_p)}). npc_shop 리로드하세요.",
                                    )
                                else:
                                    queue_feedback("error", f"가격 저장 실패: {e_u}")
                                st.rerun()
                        if c_d is not None:
                            with c_d:
                                if en:
                                    if st.button("비활성", key=f"shop_sell_dis_{uid}"):
                                        ok_u, e_u = db.execute_query_ex(
                                            "UPDATE npc_shop SET shop_enabled=0 WHERE uid=%s",
                                            (uid,),
                                        )
                                        if ok_u:
                                            queue_feedback("success", f"판매 행 비활성(uid={uid}). npc_shop 리로드하세요.")
                                        else:
                                            queue_feedback("error", f"실패: {e_u}")
                                        st.rerun()
                                else:
                                    if st.button("활성", key=f"shop_sell_en_{uid}"):
                                        ok_u, e_u = db.execute_query_ex(
                                            "UPDATE npc_shop SET shop_enabled=1 WHERE uid=%s",
                                            (uid,),
                                        )
                                        if ok_u:
                                            queue_feedback("success", f"판매 행 활성(uid={uid}). npc_shop 리로드하세요.")
                                        else:
                                            queue_feedback("error", f"실패: {e_u}")
                                        st.rerun()
                else:
                    st.caption("판매 물품 없음")

                with st.expander("➕ 판매 물품 추가"):
                    아이템검색 = st.text_input(
                        "아이템 검색 (이름 또는 구분2, 예: 이동 / teleport / 오만)",
                        key="sell_search",
                    )
                    if 아이템검색:
                        아이템목록 = _search_shop_items(db, 아이템검색)
                        if 아이템목록 and len(아이템목록) >= 400:
                            st.caption("검색 결과가 많습니다. 더 좁은 키워드로 검색해 보세요 (최대 400건 표시).")
                        선택아이템 = st.selectbox(
                            "아이템",
                            아이템목록 if 아이템목록 else [],
                            format_func=_format_shop_item_row,
                            key="sell_item",
                        )
                    else:
                        선택아이템 = None
                    판매가격 = st.number_input("판매 가격", min_value=0, value=0, key="sell_price")
                    수량 = st.number_input("수량", min_value=1, value=1, key="sell_count")
                    if st.button("판매 물품 추가", key="add_sell"):
                        if 선택아이템:
                            itemname = 선택아이템.get("아이템이름")
                            if _shop_en_ok:
                                ok, sh_err = db.execute_query_ex(
                                    """INSERT INTO npc_shop 
                                       (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type, shop_enabled) 
                                       VALUES (%s, %s, %s, 1, 0, 0, 'false', 'true', 'false', %s, '', 1)""",
                                    (npc_name, itemname, 수량, 판매가격),
                                )
                            else:
                                ok, sh_err = db.execute_query_ex(
                                    """INSERT INTO npc_shop 
                                       (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type) 
                                       VALUES (%s, %s, %s, 1, 0, 0, 'false', 'true', 'false', %s, '')""",
                                    (npc_name, itemname, 수량, 판매가격),
                                )
                            if ok:
                                queue_feedback("success", "✅ 반영되었습니다. 판매 물품이 추가되었습니다.")
                                st.rerun()
                            else:
                                queue_feedback("error", f"❌ 판매 물품 추가 실패: {sh_err}")
                                st.rerun()
                        else:
                            st.warning("아이템을 선택하세요.")

                with st.expander("🗑️ 판매 물품 삭제"):
                    if 판매목록:
                        삭제선택 = st.multiselect(
                            "삭제할 물품",
                            판매목록,
                            format_func=lambda x: f"{x.get('itemname','')} - {x.get('price',0)} 아데나 (uid={x.get('uid')})",
                            key="del_sell"
                        )
                        if st.button("판매 물품 삭제", key="del_sell_btn") and 삭제선택:
                            failed = []
                            for x in 삭제선택:
                                uid = x.get("uid")
                                if uid is not None:
                                    ok_d, e_d = db.execute_query_ex(
                                        "DELETE FROM npc_shop WHERE uid = %s AND name = %s AND buy = 'true'",
                                        (int(uid), npc_name),
                                    )
                                else:
                                    ok_d, e_d = db.execute_query_ex(
                                        "DELETE FROM npc_shop WHERE name = %s AND itemname = %s AND buy = 'true'",
                                        (npc_name, x.get("itemname")),
                                    )
                                if not ok_d:
                                    failed.append(f"{x.get('itemname')}: {e_d}")
                            if failed:
                                queue_feedback("error", "❌ 일부 삭제 실패 — " + "; ".join(failed))
                            else:
                                queue_feedback("success", f"✅ 반영되었습니다. 판매 물품 {len(삭제선택)}개 삭제 완료.")
                            st.rerun()
                    else:
                        st.caption("삭제할 판매 물품이 없습니다.")

            # 오른쪽: 매입 물품 (NPC가 사는 것 = sell='true')
            with col2:
                st.markdown("#### 💵 매입 물품 (플레이어 판매)")
                st.caption(
                    "**비활성** / **활성**은 판매 탭과 동일합니다. 적용 후 **npc_shop 리로드**."
                )
                매입목록 = db.fetch_all(
                    f"""SELECT uid, name, itemname, itemcount, price, sell, buy {_shop_sel_extra}
                       FROM npc_shop 
                       WHERE name = %s AND sell = 'true' 
                       ORDER BY itemname""",
                    (npc_name,),
                )
                if 매입목록:
                    _buy_item_cache: dict[str, dict | None] = {}
                    _buy_df_rows = []
                    for _r in 매입목록:
                        _inm = str(_r.get("itemname") or "")
                        if _inm not in _buy_item_cache:
                            _buy_item_cache[_inm] = fetch_item_row_by_display_name(db, _inm)
                        _ir = _buy_item_cache[_inm]
                        _dpy = calc_sell_display_price(db, _r, _ir, _shop_conf)
                        _row = dict(_r)
                        _row["게임표시(추정)"] = _dpy
                        _buy_df_rows.append(_row)
                    st.dataframe(_buy_df_rows, height=300, width="stretch")
                    st.markdown("**행별 가격·비활성** — 가격 저장 후 **npc_shop 리로드**")
                    st.caption(
                        "매입: `npc_shop.price` 가 0이면 `sell_item_rate`·축복주문서 배율 등으로 **추정**합니다. "
                        "price 가 0이 아니면 서버는 그 값을 그대로 씁니다."
                    )
                    st.caption("열: 물품(uid) · 가격 입력(아데나) · **가격 저장** · 비활성/활성")
                    for brow in 매입목록:
                        uid = int(brow.get("uid") or 0)
                        if uid <= 0:
                            continue
                        en = _shop_enabled_int_from_row(brow) if _shop_en_ok else 1
                        itn = str(brow.get("itemname") or "")
                        _ir = _buy_item_cache.get(itn) or fetch_item_row_by_display_name(db, itn)
                        prc_show = calc_sell_display_price(db, brow, _ir, _shop_conf)
                        _pdbc = int(brow.get("price") or 0)
                        _src_k = f"shop_buy_price_src_{npc_name}_{uid}"
                        _wid_k = f"shop_buy_price_{npc_name}_{uid}"
                        _sync_streamlit_price_input(
                            _src_k,
                            _wid_k,
                            (_pdbc, int(prc_show)),
                            int(prc_show),
                        )
                        if _shop_en_ok:
                            c_a, c_b, c_c, c_d = st.columns([2.1, 1.0, 0.65, 0.65])
                        else:
                            c_a, c_b, c_c = st.columns([2.4, 1.1, 0.85])
                            c_d = None
                        with c_a:
                            _off = _shop_en_ok and not en
                            st.caption(("⏸ " if _off else "") + f"`uid={uid}` · {itn}")
                        with c_b:
                            new_p = st.number_input(
                                "가격",
                                min_value=0,
                                step=1,
                                key=_wid_k,
                                label_visibility="collapsed",
                            )
                        with c_c:
                            if st.button("가격 저장", key=f"shop_buy_psave_{uid}"):
                                ok_u, e_u = db.execute_query_ex(
                                    "UPDATE npc_shop SET price=%s WHERE uid=%s AND name=%s AND sell='true'",
                                    (int(new_p), uid, npc_name),
                                )
                                if ok_u:
                                    queue_feedback("success", f"매입 가격 저장(uid={uid} → {int(new_p)}). npc_shop 리로드하세요.")
                                else:
                                    queue_feedback("error", f"가격 저장 실패: {e_u}")
                                st.rerun()
                        if c_d is not None:
                            with c_d:
                                if en:
                                    if st.button("비활성", key=f"shop_buy_dis_{uid}"):
                                        ok_u, e_u = db.execute_query_ex(
                                            "UPDATE npc_shop SET shop_enabled=0 WHERE uid=%s",
                                            (uid,),
                                        )
                                        if ok_u:
                                            queue_feedback("success", f"매입 행 비활성(uid={uid}). npc_shop 리로드하세요.")
                                        else:
                                            queue_feedback("error", f"실패: {e_u}")
                                        st.rerun()
                                else:
                                    if st.button("활성", key=f"shop_buy_en_{uid}"):
                                        ok_u, e_u = db.execute_query_ex(
                                            "UPDATE npc_shop SET shop_enabled=1 WHERE uid=%s",
                                            (uid,),
                                        )
                                        if ok_u:
                                            queue_feedback("success", f"매입 행 활성(uid={uid}). npc_shop 리로드하세요.")
                                        else:
                                            queue_feedback("error", f"실패: {e_u}")
                                        st.rerun()
                else:
                    st.caption("매입 물품 없음")

                with st.expander("➕ 매입 물품 추가"):
                    아이템검색2 = st.text_input(
                        "아이템 검색 (이름 또는 구분2)",
                        key="buy_search",
                    )
                    if 아이템검색2:
                        아이템목록2 = _search_shop_items(db, 아이템검색2)
                        if 아이템목록2 and len(아이템목록2) >= 400:
                            st.caption("검색 결과가 많습니다. 더 좁은 키워드로 검색해 보세요 (최대 400건 표시).")
                        선택아이템2 = st.selectbox(
                            "아이템",
                            아이템목록2 if 아이템목록2 else [],
                            format_func=_format_shop_item_row,
                            key="buy_item",
                        )
                    else:
                        선택아이템2 = None
                    매입가격 = st.number_input("매입 가격", min_value=0, value=0, key="buy_price")
                    if st.button("매입 물품 추가", key="add_buy"):
                        if 선택아이템2:
                            itemname = 선택아이템2.get("아이템이름")
                            if _shop_en_ok:
                                ok, bh_err = db.execute_query_ex(
                                    """INSERT INTO npc_shop 
                                       (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type, shop_enabled) 
                                       VALUES (%s, %s, 1, 1, 0, 0, 'true', 'false', 'false', %s, '', 1)""",
                                    (npc_name, itemname, 매입가격),
                                )
                            else:
                                ok, bh_err = db.execute_query_ex(
                                    """INSERT INTO npc_shop 
                                       (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type) 
                                       VALUES (%s, %s, 1, 1, 0, 0, 'true', 'false', 'false', %s, '')""",
                                    (npc_name, itemname, 매입가격),
                                )
                            if ok:
                                queue_feedback("success", "✅ 반영되었습니다. 매입 물품이 추가되었습니다.")
                                st.rerun()
                            else:
                                queue_feedback("error", f"❌ 매입 물품 추가 실패: {bh_err}")
                                st.rerun()
                        else:
                            st.warning("아이템을 선택하세요.")

                with st.expander("🗑️ 매입 물품 삭제"):
                    if 매입목록:
                        삭제매입 = st.multiselect(
                            "삭제할 물품",
                            매입목록,
                            format_func=lambda x: f"{x.get('itemname','')} - {x.get('price',0)} 아데나 (uid={x.get('uid')})",
                            key="del_buy"
                        )
                        if st.button("매입 물품 삭제", key="del_buy_btn") and 삭제매입:
                            failed_b = []
                            for x in 삭제매입:
                                uid = x.get("uid")
                                if uid is not None:
                                    ok_b, e_b = db.execute_query_ex(
                                        "DELETE FROM npc_shop WHERE uid = %s AND name = %s AND sell = 'true'",
                                        (int(uid), npc_name),
                                    )
                                else:
                                    ok_b, e_b = db.execute_query_ex(
                                        "DELETE FROM npc_shop WHERE name = %s AND itemname = %s AND sell = 'true'",
                                        (npc_name, x.get("itemname")),
                                    )
                                if not ok_b:
                                    failed_b.append(f"{x.get('itemname')}: {e_b}")
                            if failed_b:
                                queue_feedback("error", "❌ 일부 삭제 실패 — " + "; ".join(failed_b))
                            else:
                                queue_feedback("success", f"✅ 반영되었습니다. 매입 물품 {len(삭제매입)}개 삭제 완료.")
                            st.rerun()
                    else:
                        st.caption("삭제할 매입 물품이 없습니다.")

    except Exception as e:
        st.error(f"상점 조회/수정 중 오류: {e}")
        st.caption("npc_shop, item 테이블이 lin200에 있는지 확인하세요.")

# ========== tab7: 스폰 위치 수정 (npc_spawnlist에 등록된 NPC만, 위치 편집) ==========
else:
    st.subheader("📍 스폰 위치 수정")
    st.caption("npc_spawnlist에 등록된(현재 월드에 리스폰되는) NPC만 표시됩니다. 좌표·맵·방향을 수정한 뒤 저장하면 DB에 반영됩니다. **반영 후 서버에서 NPC 리로드**가 필요합니다.")
    try:
        apply_immediately = st.checkbox(
            "저장 즉시 방향 반영 (해당 스폰만 npc_despawn/npc_respawn)",
            value=True,
            help="heading/좌표를 바꿔도 NPC가 이미 월드에 있으면 방향이 안 바뀔 수 있습니다. 이 옵션을 켜면 저장 직후 해당 스폰만 despawn/respawn 하여 즉시 반영됩니다."
        )
        spawn_list = db.fetch_all(
            "SELECT name, npcName, locX, locY, locMap, heading FROM npc_spawnlist ORDER BY name"
        )
        if not spawn_list:
            st.info("npc_spawnlist에 스폰이 없습니다. NPC 배치 탭에서 먼저 배치하세요.")
        else:
            for r in spawn_list:
                r["name"] = str(r.get("name") or "").strip()
                r["npcName"] = str(r.get("npcName") or "").strip()
            search_pos = st.text_input("🔍 스폰 검색 (스폰 이름·NPC 이름)", placeholder="예: 파워볼, powerball", key="pos_edit_search")
            if search_pos and search_pos.strip():
                q = search_pos.strip().lower()
                spawn_filtered = [r for r in spawn_list if q in (r["name"] + " " + r["npcName"]).lower()]
            else:
                spawn_filtered = spawn_list
            if not spawn_filtered:
                st.warning("검색 결과가 없습니다.")
            else:
                choices = [f"{r['name']} | {r['npcName']} (X={r.get('locX')}, Y={r.get('locY')}, 맵={r.get('locMap')}, 방향={r.get('heading')})" for r in spawn_filtered]
                idx = st.selectbox("위치를 수정할 스폰 선택", range(len(choices)), format_func=lambda i: choices[i], key="pos_edit_select")
                if idx is not None and 0 <= idx < len(spawn_filtered):
                    row = spawn_filtered[idx]
                    spawn_name = row["name"]
                    with st.form("spawn_pos_edit_form"):
                        st.markdown("**좌표·맵·방향** (게임 내 [명령어]좌표 로 확인한 값 그대로 입력)")
                        c1, c2 = st.columns(2)
                        with c1:
                            new_x = st.number_input("X 좌표", value=int(row.get("locX") or 0), key="pos_x")
                            new_y = st.number_input("Y 좌표", value=int(row.get("locY") or 0), key="pos_y")
                        with c2:
                            new_map = st.number_input("맵 ID", value=int(row.get("locMap") or 0), min_value=0, key="pos_map")
                            new_heading = st.number_input("방향 (0~7)", value=int(row.get("heading") or 0), min_value=0, max_value=7, key="pos_heading")
                        if st.form_submit_button("💾 위치 저장"):
                            ok, pos_err = db.execute_query_ex(
                                "UPDATE npc_spawnlist SET locX=%s, locY=%s, locMap=%s, heading=%s WHERE name=%s",
                                (new_x, new_y, new_map, new_heading, spawn_name)
                            )
                            if ok:
                                if apply_immediately:
                                    ok_ds, e_ds = db.execute_query_ex(
                                        "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                                        ("npc_despawn", spawn_name)
                                    )
                                    ok_rs, e_rs = db.execute_query_ex(
                                        "INSERT INTO gm_server_command (command, param, executed) VALUES (%s, %s, 0)",
                                        ("npc_respawn", spawn_name)
                                    )
                                    if ok_ds and ok_rs:
                                        queue_feedback(
                                            "success",
                                            f"✅ '{spawn_name}' 위치 저장됨 (X={new_x}, Y={new_y}, 맵={new_map}). "
                                            "즉시 적용 요청: npc_despawn → npc_respawn.",
                                        )
                                    else:
                                        queue_feedback(
                                            "error",
                                            f"❌ 좌표는 DB에 저장됐으나 서버 명령 큐 실패 — despawn: {e_ds or 'OK'}, respawn: {e_rs or 'OK'}",
                                        )
                                else:
                                    queue_feedback(
                                        "success",
                                        f"✅ '{spawn_name}' 위치 저장됨 (X={new_x}, Y={new_y}, 맵={new_map}). "
                                        f"**서버 리로드** 페이지에서 'npc 테이블 리로드'를 실행하세요.",
                                    )
                                st.rerun()
                            else:
                                queue_feedback("error", f"❌ 위치 저장 실패: {pos_err}")
                                st.rerun()
    except Exception as e:
        st.error(f"스폰 위치 수정 오류: {e}")
