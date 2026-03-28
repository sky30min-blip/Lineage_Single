# -*- coding: utf-8 -*-
"""powerball_npc.sql 적용: npc(파워볼진행자, 일반볼, 파워볼) + npc_spawnlist"""
import os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
import pymysql

config = {'host': 'localhost', 'port': 3306, 'user': 'root', 'password': '1307',
          'database': 'lin200', 'charset': 'utf8mb4'}

conn = pymysql.connect(**config)
cur = conn.cursor()

# nameid = 게임 내 표시 이름 (일반볼/파워볼은 각각 '일반볼'/'파워볼'로 표시)
for name, ntype, nameid in [('파워볼진행자', '파워볼진행자', '파워볼진행자'), ('일반볼', '일반볼', '일반볼'), ('파워볼', '파워볼', '파워볼')]:
    try:
        cur.execute("""
            INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
            VALUES (%s, %s, %s, 887, 0, 1, 0, 0, 'false', 0, 0)
            ON DUPLICATE KEY UPDATE type = %s, nameid = %s, gfxid = 887
        """, (name, ntype, nameid, ntype, nameid))
        conn.commit()
        print("npc OK:", name)
    except Exception as e:
        conn.rollback()
        print("npc:", name, e)

    # (중복 row 대응) name 컬럼 기준으로 nameid를 강제 업데이트
    try:
        cur.execute("""
            UPDATE npc SET type = %s, nameid = %s, gfxid = 887
            WHERE name = %s
        """, (ntype, nameid, name))
        conn.commit()
    except Exception as e:
        conn.rollback()
        print("npc FORCE UPDATE:", name, e)

# powerball_1 = 파워볼진행자(상점), powerball_일반볼, powerball_파워볼 = 추첨 발표용
spawns = [
    ('powerball_1', '파워볼진행자', 33418, 32823, 4, 3, ''),
    ('powerball_일반볼', '일반볼', 33420, 32820, 4, 3, ''),
    ('powerball_파워볼', '파워볼', 33421, 32821, 4, 3, ''),
]
for name, npc_name, x, y, m, head, title in spawns:
    try:
        cur.execute("""
            INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
            VALUES (%s, %s, %s, %s, %s, %s, 0, %s)
            ON DUPLICATE KEY UPDATE npcName = %s, locX = %s, locY = %s, locMap = %s, heading = %s, title = %s
        """, (name, npc_name, x, y, m, head, title, npc_name, x, y, m, head, title))
        conn.commit()
        print("npc_spawnlist OK:", name)
    except Exception as e:
        conn.rollback()
        print("npc_spawnlist:", name, e)

cur.close()
conn.close()
print("Done.")
