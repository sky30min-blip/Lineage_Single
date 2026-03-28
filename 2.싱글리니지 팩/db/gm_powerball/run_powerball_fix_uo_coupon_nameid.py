# -*- coding: utf-8 -*-
"""기존 DB: 언더/오버 쿠폰 NAMEID가 $1251/$1252면 HealingPotion으로 생성되어 더블클릭 시 물약처럼 소모됨 → $1254/$1255로 수정."""
import os
import pymysql

os.chdir(os.path.dirname(os.path.abspath(__file__)))
config = dict(
    host="localhost",
    port=3306,
    user="root",
    password="1307",
    database="lin200",
    charset="utf8mb4",
)

conn = pymysql.connect(**config)
try:
    with conn.cursor() as cur:
        cur.execute("UPDATE `item` SET `NAMEID`='$1254' WHERE `아이템이름`='언더 쿠폰'")
        u1 = cur.rowcount
        cur.execute("UPDATE `item` SET `NAMEID`='$1255' WHERE `아이템이름`='오버 쿠폰'")
        u2 = cur.rowcount
    conn.commit()
    print("언더 쿠폰 NAMEID→$1254 반영 행:", u1)
    print("오버 쿠폰 NAMEID→$1255 반영 행:", u2)
finally:
    conn.close()
print("Done. 서버 재시작 후 기존 인벤 쿠폰은 여전히 옛 객체일 수 있음 → 재지급 또는 버리고 재구매 권장.")
