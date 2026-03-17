"""
[2단계] sprite_index.json + DB 인벤ID 로 아이템 아이콘에 대응할 .spr 엔트리 목록을 만듭니다.
Sprite.pak 읽기는 하지 않습니다.
"""
import os
import sys
import json

GM_TOOL_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, GM_TOOL_ROOT)
INDEX_JSON = os.path.join(os.path.dirname(__file__), "sprite_index.json")
OUT_LIST_JSON = os.path.join(os.path.dirname(__file__), "sprite_item_list.json")


def get_inv_ids():
    import pymysql
    import config
    conn = pymysql.connect(
        host=config.DB_CONFIG["host"],
        port=config.DB_CONFIG["port"],
        user=config.DB_CONFIG["user"],
        password=config.DB_CONFIG["password"],
        database=config.DB_CONFIG["database"],
        charset=config.DB_CONFIG["charset"],
    )
    inv_ids = set()
    try:
        with conn.cursor() as cur:
            cur.execute("SELECT DISTINCT `인벤ID` FROM item WHERE `인벤ID` IS NOT NULL")
            for row in cur.fetchall():
                if row[0] is not None:
                    inv_ids.add(int(row[0]))
    finally:
        conn.close()
    return sorted(inv_ids)


def main():
    with open(INDEX_JSON, "r", encoding="utf-8") as f:
        entries = json.load(f)
    # "X-Y.spr" → 첫 숫자 X를 gfx로. "Z.spr" → Z를 gfx로. (아이콘은 보통 N-0.spr)
    gfx_to_entry = {}
    for e in entries:
        n = e.get("name", "").strip()
        if not n.endswith(".spr"):
            continue
        base = n[:-4]
        if "-" in base:
            parts = base.split("-", 1)
            if len(parts) == 2 and parts[0].isdigit() and parts[1].isdigit():
                gfx = int(parts[0])
                if gfx not in gfx_to_entry or parts[1] == "0":
                    gfx_to_entry[gfx] = {"name": n, **e}
        elif base.isdigit():
            gfx = int(base)
            if gfx not in gfx_to_entry:
                gfx_to_entry[gfx] = {"name": n, **e}
    inv_ids = get_inv_ids()
    matches = []
    for iid in inv_ids:
        if iid in gfx_to_entry:
            matches.append({"inv_id": iid, **gfx_to_entry[iid]})
    with open(OUT_LIST_JSON, "w", encoding="utf-8") as f:
        json.dump(matches, f, ensure_ascii=False, indent=0)
    print(f"저장: {OUT_LIST_JSON} (매칭 {len(matches)}개 / 인벤ID {len(inv_ids)}개)")


if __name__ == "__main__":
    main()
