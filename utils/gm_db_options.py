# -*- coding: utf-8 -*-
"""
GM 툴: DB에 실제 존재하는 값 목록을 조회해 드롭다운 옵션으로 쓰기 위한 헬퍼.
"""
from __future__ import annotations

# 드롭다운 특수 항목
EMPTY_LABEL = "(비움)"
CUSTOM_STR_LABEL = "— 직접 입력 —"
CUSTOM_NUM_LABEL = "— 숫자 직접 입력 —"

# item.재질이 비었을 때 보조 목록 (DB에 없을 수 있는 일반값)
_FALLBACK_MATERIALS = (
    "기타", "가죽", "뼈", "나무", "철", "강철", "금속", "천", "미스릴", "오리하루콘",
    "용의 비늘", "은", "금", "구리", "유리", "종이", "아데나", "보석", "머리카락",
)


def distinct_item_materials(db, limit: int = 400) -> list[str]:
    """item 테이블에서 DISTINCT 재질. 실패 시 빈 리스트."""
    try:
        rows = db.fetch_all(
            """
            SELECT DISTINCT `재질` AS v FROM item
            WHERE `재질` IS NOT NULL AND TRIM(`재질`) <> ''
            ORDER BY `재질`
            LIMIT %s
            """,
            (limit,),
        )
        out = [str(r["v"]).strip() for r in rows if r.get("v") is not None and str(r["v"]).strip()]
    except Exception:
        out = []
    merged = list(dict.fromkeys(out + list(_FALLBACK_MATERIALS)))
    return sorted(merged, key=lambda x: (x != "기타", x))


def string_field_options(distinct_values: list[str], current: str | None) -> tuple[list[str], int]:
    """
    (비움) + DB 목록 + (직접 입력).
    current가 목록에 없으면 그 값을 한 줄 삽입해 기본 선택 가능.
    반환: (옵션 리스트, 현재값에 맞는 기본 index)
    """
    cur = (current or "").strip()
    base = sorted({str(x).strip() for x in distinct_values if x is not None and str(x).strip()})
    opts: list[str] = [EMPTY_LABEL]
    if cur and cur not in base:
        opts.append(cur)
    opts.extend(base)
    opts.append(CUSTOM_STR_LABEL)
    # 중복 제거(순서 유지)
    seen: set[str] = set()
    out: list[str] = []
    for o in opts:
        if o not in seen:
            seen.add(o)
            out.append(o)
    if not cur:
        default_i = 0
    elif cur in out:
        default_i = out.index(cur)
    else:
        default_i = out.index(CUSTOM_STR_LABEL)
    return out, default_i


def resolve_string_selection(selected: str, custom_text: str) -> str:
    if selected == EMPTY_LABEL:
        return ""
    if selected == CUSTOM_STR_LABEL:
        return (custom_text or "").strip()
    return (selected or "").strip()


def distinct_monster_strings(db, column: str, limit: int = 300) -> list[str]:
    if column not in ("boss_class", "family"):
        return []
    try:
        rows = db.fetch_all(
            f"""
            SELECT DISTINCT `{column}` AS v FROM monster
            WHERE `{column}` IS NOT NULL AND TRIM(`{column}`) <> ''
            ORDER BY `{column}`
            LIMIT %s
            """,
            (limit,),
        )
        return [str(r["v"]).strip() for r in rows if r.get("v") is not None and str(r["v"]).strip()]
    except Exception:
        return []


def distinct_monster_ints(db, column: str, limit: int = 150) -> list[int]:
    if column not in ("gfx_mode", "atk_type", "atk_range"):
        return []
    try:
        rows = db.fetch_all(
            f"SELECT DISTINCT `{column}` AS v FROM monster ORDER BY `{column}` LIMIT %s",
            (limit,),
        )
        xs: list[int] = []
        for r in rows:
            v = r.get("v")
            if v is None:
                continue
            try:
                xs.append(int(v))
            except (TypeError, ValueError):
                continue
        return sorted(set(xs))
    except Exception:
        return []


def int_field_options(distinct_ints: list[int], current: int) -> tuple[list[str], int]:
    """
    DB에 나온 정수들 + 직접 입력.
    라벨은 str(정수), 마지막에 CUSTOM_NUM_LABEL.
    """
    xs = sorted({int(x) for x in distinct_ints})
    labels = [str(x) for x in xs]
    labels.append(CUSTOM_NUM_LABEL)
    cur_s = str(int(current))
    if cur_s in labels:
        idx = labels.index(cur_s)
    else:
        # 현재값이 목록에 없으면 앞에 넣고 직접 입력도 가능
        if current is not None:
            labels = [cur_s] + [lb for lb in labels if lb != cur_s]
        idx = 0
    return labels, idx


def resolve_int_selection(selected_label: str, custom_value: int) -> int:
    if selected_label == CUSTOM_NUM_LABEL:
        return int(custom_value)
    try:
        return int(selected_label)
    except (TypeError, ValueError):
        return int(custom_value)
