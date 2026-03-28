# -*- coding: utf-8 -*-
"""
파워볼: 아이템 이름(홀 쿠폰/짝 쿠폰) + 레이스표 이미지(인벤ID 151) 수정, npc_shop 중복 제거
"""
import os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
import pymysql

config = {'host': 'localhost', 'port': 3306, 'user': 'root', 'password': '1307',
          'database': 'lin200', 'charset': 'utf8mb4'}

conn = pymysql.connect(**config)
cur = conn.cursor()

# 1) 홀/짝 쿠폰: NAMEID $1249/$1250 (RaceTicket 1247과 분리 → 일반 아이템으로 인벤 표시), 인벤ID 151 (레이스표 아이콘)
for display_name, nameid, inv_gfx in [('홀 쿠폰', '$1249', 151), ('짝 쿠폰', '$1250', 151)]:
    try:
        cur.execute("""
            UPDATE item SET `NAMEID` = %s, `인벤ID` = %s, `is_inventory_save` = 'true' WHERE `아이템이름` = %s
        """, (nameid, inv_gfx, display_name))
        conn.commit()
        print("item OK:", display_name, "-> NAMEID", nameid, "인벤ID", inv_gfx, "(레이스표), 반영 행 수:", cur.rowcount)
    except Exception as e:
        conn.rollback()
        print("item:", display_name, e)

# 2) npc_shop 파워볼 중복 제거: (name, itemname) 당 uid 가장 작은 것만 남기기
try:
    # MySQL/MariaDB: 중복 중 uid가 큰 것 삭제
    cur.execute("""
        DELETE t1 FROM npc_shop t1
        INNER JOIN npc_shop t2
        WHERE t1.name = '파워볼' AND t2.name = '파워볼'
          AND t1.itemname = t2.itemname AND t1.uid > t2.uid
    """)
    conn.commit()
    print("npc_shop 중복 제거 OK, 삭제 행:", cur.rowcount)
except Exception as e:
    conn.rollback()
    print("npc_shop 중복 제거:", e)

# 3) 파워볼 상점은 홀 쿠폰/짝 쿠폰 각 1개만, itemcount=1, sell/buy 설정
for itemname in ['홀 쿠폰', '짝 쿠폰']:
    try:
        cur.execute("""
            UPDATE npc_shop SET itemcount = 1, sell = 'true', buy = 'true', aden_type = '아데나'
            WHERE name = '파워볼' AND itemname = %s
        """, (itemname,))
        conn.commit()
        if cur.rowcount:
            print("npc_shop 업데이트 OK:", itemname)
    except Exception as e:
        conn.rollback()
        print("npc_shop 업데이트:", itemname, e)

cur.close()
conn.close()
print("Done.")
