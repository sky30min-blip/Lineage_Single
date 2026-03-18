# -*- coding: utf-8 -*-
"""
파워볼 NPC 3종을 요청한 좌표(기란 맵 4)로 DB에 적용합니다.
GM 툴과 같은 DB 설정을 사용합니다. 실행 후 반드시 서버에서 'npc 테이블 리로드' 또는 서버 재시작을 하세요.
"""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import config
import pymysql

# 요청한 좌표 (기란 맵 4, 5시 방향 heading=3)
SPAWNS = [
    ("powerball_1", "파워볼진행자", 33418, 32823, 4, 3),
    ("powerball_일반볼", "일반볼", 33419, 32820, 4, 3),
    ("powerball_파워볼", "파워볼", 33421, 32822, 4, 3),
]

def main():
    try:
        conn = pymysql.connect(**config.DB_CONFIG)
    except Exception as e:
        print(f"DB 연결 실패: {e}")
        return 1
    cur = conn.cursor()
    try:
        for name, npc_name, x, y, map_id, heading in SPAWNS:
            cur.execute(
                """INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
                   VALUES (%s, %s, %s, %s, %s, %s, 0, '')
                   ON DUPLICATE KEY UPDATE npcName=%s, locX=%s, locY=%s, locMap=%s, heading=%s""",
                (name, npc_name, x, y, map_id, heading, npc_name, x, y, map_id, heading)
            )
            print(f"  적용: {npc_name} -> ({x}, {y}) 맵{map_id}")
        conn.commit()
        print("DB 적용 완료. 이제 서버에서 [명령어|이벤트|리로드] -> [리로드] -> [npc 테이블 리로드] 를 실행하거나 서버를 재시작하세요.")
    except Exception as e:
        conn.rollback()
        print(f"오류: {e}")
        return 1
    finally:
        cur.close()
        conn.close()
    return 0

if __name__ == "__main__":
    sys.exit(main())
