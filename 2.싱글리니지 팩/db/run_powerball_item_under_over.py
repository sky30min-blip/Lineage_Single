# -*- coding: utf-8 -*-
"""item 테이블에 언더 쿠폰·오버 쿠폰 행이 없으면 상점에 절대 안 뜸 — NpcShopDatabase가 ItemDatabase.find(itemname) 필수."""
import os
import pymysql

os.chdir(os.path.dirname(os.path.abspath(__file__)))
config = dict(
    host="localhost",
    port=3306,
    user="root",
    password="1307",
    database="l1jdb",
    charset="utf8mb4",
)

# powerball_shop.sql 과 동일. NAMEID 1251/1252 는 서버에서 HealingPotion 전용이므로 1254/1255 사용.
ROW_UNDER = (
    "INSERT INTO `item` VALUES ('언더 쿠폰', 'item', 'etc', '$1254', '기타', 'true', '0', '0', '8', '151', '151', '0', '0', "
    "'true', 'false', 'true', 'true', 'true', 'true', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', "
    "'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', 'false', '0', '0', '0', '0', '0', '0', "
    "'0', '0', '0', '0', '0', '1', '0', '0', 'none', 'false', 'false', 'false', 'false', 'false', '', 'true', 'false', "
    "'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', "
    "'0', '0', '0', '') "
    "ON DUPLICATE KEY UPDATE `아이템이름`='언더 쿠폰', `NAMEID`='$1254', `인벤ID`=151, `겹침`='false'"
)
ROW_OVER = (
    "INSERT INTO `item` VALUES ('오버 쿠폰', 'item', 'etc', '$1255', '기타', 'true', '0', '0', '8', '151', '151', '0', '0', "
    "'true', 'false', 'true', 'true', 'true', 'true', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', "
    "'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', 'false', '0', '0', '0', '0', '0', '0', "
    "'0', '0', '0', '0', '0', '1', '0', '0', 'none', 'false', 'false', 'false', 'false', 'false', '', 'true', 'false', "
    "'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', "
    "'0', '0', '0', '') "
    "ON DUPLICATE KEY UPDATE `아이템이름`='오버 쿠폰', `NAMEID`='$1255', `인벤ID`=151, `겹침`='false'"
)

conn = pymysql.connect(**config)
try:
    with conn.cursor() as cur:
        cur.execute(ROW_UNDER)
        print("언더 쿠폰 item:", cur.rowcount)
        cur.execute(ROW_OVER)
        print("오버 쿠폰 item:", cur.rowcount)
    conn.commit()
finally:
    conn.close()
print("Done. 서버에서 item 리로드 또는 재시작.")
