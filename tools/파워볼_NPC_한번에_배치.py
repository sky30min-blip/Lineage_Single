# -*- coding: utf-8 -*-
"""
파워볼 NPC를 DB에 한 번에 등록하는 스크립트.
실행만 하면 기란 마을(기본)에 배치됩니다.

사용법 (저장소 루트에서):
  python tools/파워볼_NPC_한번에_배치.py              → 기란 (33449, 32825, 맵4)
  python tools/파워볼_NPC_한번에_배치.py 기란        → 기란
  python tools/파워볼_NPC_한번에_배치.py 하이네      → 하이네
  python tools/파워볼_NPC_한번에_배치.py 아덴        → 아덴
  python tools/파워볼_NPC_한번에_배치.py 글루딘      → 글루딘
  python tools/파워볼_NPC_한번에_배치.py 켄트        → 켄트
"""
import sys
import os

# GM 툴 설정 불러오기 (같은 DB 사용)
_repo = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
sys.path.insert(0, os.path.join(_repo, "gm_tool"))
try:
    from config import DB_CONFIG
except ImportError:
    DB_CONFIG = {
        "host": "localhost",
        "port": 3306,
        "user": "root",
        "password": "",
        "database": "l1jdb",
        "charset": "utf8mb4",
    }

# 위치 프리셋 (gm_tool config 기반)
LOCATIONS = {
    "기란": (33449, 32825, 4),
    "아덴": (33430, 32815, 4),
    "하이네": (33605, 33235, 4),
    "글루딘": (32612, 32734, 4),
    "켄트": (33080, 33390, 4),
}


def main():
    loc_name = "기란"
    if len(sys.argv) >= 2 and sys.argv[1].strip() in LOCATIONS:
        loc_name = sys.argv[1].strip()
    x, y, m = LOCATIONS[loc_name]

    try:
        import pymysql
    except ImportError:
        print("pymysql 필요: pip install pymysql")
        return 1

    print(f"[파워볼 NPC] DB 등록 중... 위치: {loc_name} (x={x}, y={y}, 맵={m})")
    try:
        conn = pymysql.connect(**DB_CONFIG)
        with conn.cursor() as cur:
            # 1) npc 테이블
            cur.execute("""
                INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
                VALUES ('파워볼', '파워볼', '50999', 1216, 0, 1, 0, 0, 'false', 0, 0)
                ON DUPLICATE KEY UPDATE type = '파워볼', nameid = '50999', gfxid = 1216
            """)
            # 2) npc_spawnlist
            cur.execute("""
                INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
                VALUES ('powerball_1', '파워볼', %s, %s, %s, 0, 0, '파워볼')
                ON DUPLICATE KEY UPDATE npcName = '파워볼', locX = %s, locY = %s, locMap = %s
            """, (x, y, m, x, y, m))
        conn.commit()
        conn.close()
        print("[완료] 파워볼 NPC가 등록되었습니다. 서버를 재시작하면 맵에 나타납니다.")
        return 0
    except Exception as e:
        print(f"[오류] {e}")
        return 1


if __name__ == "__main__":
    sys.exit(main())
