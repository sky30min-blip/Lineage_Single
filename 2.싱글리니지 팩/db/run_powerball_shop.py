# -*- coding: utf-8 -*-
"""powerball_shop.sql 실행: item(홀/짝/언더/오버 쿠폰) + npc_shop"""
import os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
import pymysql

config = {'host': 'localhost', 'port': 3306, 'user': 'root', 'password': '1307',
          'database': 'l1jdb', 'charset': 'utf8mb4'}

conn = pymysql.connect(**config)
cur = conn.cursor()

# 1) powerball_shop.sql에서 INSERT INTO item 한 줄씩 실행 (ON DUPLICATE 제거)
with open("powerball_shop.sql", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if not line.startswith("INSERT INTO `item`"):
            continue
        if "ON DUPLICATE" in line:
            line = line.split("ON DUPLICATE")[0].strip().rstrip(";")
        if not line.endswith(";"):
            line += ";"
        try:
            cur.execute(line)
            conn.commit()
            print("Item OK")
        except Exception as e:
            conn.rollback()
            print("Item:", e)

# 2) npc_shop (구매 buy + NPC 매입 sell 둘 다 true)
for itemname in ["홀 쿠폰", "짝 쿠폰", "언더 쿠폰", "오버 쿠폰"]:
    try:
        cur.execute("""
            INSERT INTO npc_shop (name, itemname, itemcount, itembress, itemenlevel, itemtime, sell, buy, gamble, price, aden_type)
            VALUES (%s, %s, 1, 1, 0, 0, 'true', 'true', 'false', 0, '아데나')
        """, ("파워볼진행자", itemname))
        conn.commit()
        print("npc_shop OK:", itemname)
    except Exception as e:
        conn.rollback()
        try:
            cur.execute(
                "UPDATE npc_shop SET sell='true', buy='true', aden_type='아데나' "
                "WHERE name='파워볼진행자' AND itemname=%s",
                (itemname,),
            )
            conn.commit()
            print("npc_shop UPDATE OK:", itemname)
        except Exception as e2:
            conn.rollback()
            print("npc_shop:", itemname, e2)

# 3) 쿠폰 아이템 이미지: 인벤ID 151 (레이스표 아이콘, 지정대로)
try:
    cur.execute("UPDATE item SET `인벤ID` = 151 WHERE `아이템이름` IN ('홀 쿠폰', '짝 쿠폰', '언더 쿠폰', '오버 쿠폰')")
    conn.commit()
    print("item 인벤ID(151, 레이스표) OK")
except Exception as e:
    conn.rollback()
    print("item 인벤ID:", e)

cur.close()
conn.close()
print("Done.")
