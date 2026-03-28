# -*- coding: utf-8 -*-
"""기존 DB: 언더/오버 쿠폰 NAMEID가 $1251/$1252면 HealingPotion으로 생성·표시가 물약과 겹침 → $1254/$1255.
농축 체력 회복제 3종 NAMEID를 $1251~$1253으로 복구(실수로 바뀐 경우)."""
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
        cur.execute("UPDATE `item` SET `NAMEID`='$1251' WHERE `아이템이름`='농축 체력 회복제'")
        u3 = cur.rowcount
        cur.execute("UPDATE `item` SET `NAMEID`='$1252' WHERE `아이템이름`='농축 고급 체력 회복제'")
        u4 = cur.rowcount
        cur.execute("UPDATE `item` SET `NAMEID`='$1253' WHERE `아이템이름`='농축 강력 체력 회복제'")
        u5 = cur.rowcount
    conn.commit()
    print("언더 쿠폰 NAMEID→$1254 반영 행:", u1)
    print("오버 쿠폰 NAMEID→$1255 반영 행:", u2)
    print("농축 체력 회복제 NAMEID→$1251 반영 행:", u3)
    print("농축 고급 체력 회복제 NAMEID→$1252 반영 행:", u4)
    print("농축 강력 체력 회복제 NAMEID→$1253 반영 행:", u5)
finally:
    conn.close()
print("Done. 서버 재시작(또는 아이템 리로드) 후 기존 인벤 아이템은 옛 객체일 수 있음 → 재지급·재구매 권장.")
