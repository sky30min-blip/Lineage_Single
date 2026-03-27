# -*- coding: utf-8 -*-
"""파워볼 쿠폰: 겹침 해제 + npc_shop buy/sell 보정 + 언더/오버 행 보강.

npc_shop 컬럼 의미(서버 NpcShopDatabase와 동일):
  buy=true  → 플레이어가 NPC 상점에서 구매(S_ShopBuy)
  sell=true → 플레이어가 NPC에게 매도(S_ShopSell, 당첨 쿠폰 팔기)
쿠폰 4종은 둘 다 true 여야 함. itemname 은 item.아이템이름 과 정확히 일치해야 함(예: '언더 쿠폰', 접두어 없음).
"""
import os
import subprocess
import sys

import pymysql

os.chdir(os.path.dirname(os.path.abspath(__file__)))
# 상점 npc_shop 은 있어도 item 테이블에 행이 없으면 서버가 목록에서 빼버림 → 먼저 보강
_item_script = os.path.join(os.path.dirname(os.path.abspath(__file__)), "run_powerball_item_under_over.py")
if os.path.isfile(_item_script):
    subprocess.check_call([sys.executable, _item_script])

config = {'host': 'localhost', 'port': 3306, 'user': 'root', 'password': '1307',
          'database': 'l1jdb', 'charset': 'utf8mb4'}

conn = pymysql.connect(**config)
try:
    with conn.cursor() as cur:
        cur.execute(
            "UPDATE `item` SET `겹침`='false' WHERE `아이템이름` IN ('홀 쿠폰','짝 쿠폰','언더 쿠폰','오버 쿠폰')"
        )
        print("item.겹침=false (4종 쿠폰):", cur.rowcount, "행")
        # 잘못된 표시용 이름 → 서버가 인식하는 이름
        for wrong, right in (
            ("파워볼: 언더 쿠폰", "언더 쿠폰"),
            ("파워볼 : 언더 쿠폰", "언더 쿠폰"),
            ("파워볼: 오버 쿠폰", "오버 쿠폰"),
            ("파워볼 : 오버 쿠폰", "오버 쿠폰"),
        ):
            cur.execute(
                "UPDATE `npc_shop` SET `itemname`=%s, `buy`='true', `sell`='true', `itemcount`=1 "
                "WHERE `name`='파워볼진행자' AND `itemname`=%s",
                (right, wrong),
            )
            if cur.rowcount:
                print("npc_shop itemname 수정:", wrong, "->", right)
        cur.execute(
            "UPDATE `npc_shop` SET `buy`='true', `sell`='true', `itemcount`=1 "
            "WHERE `name`='파워볼진행자' AND `itemname` IN ('홀 쿠폰','짝 쿠폰','언더 쿠폰','오버 쿠폰')"
        )
        print("npc_shop buy+sell=true (4종 쿠폰):", cur.rowcount, "행")
        for itemname in ('언더 쿠폰', '오버 쿠폰'):
            cur.execute(
                """INSERT INTO `npc_shop` (`name`, `itemname`, `itemcount`, `itembress`, `itemenlevel`, `itemtime`, `sell`, `buy`, `gamble`, `price`, `aden_type`)
                   SELECT '파워볼진행자', %s, 1, 1, 0, 0, 'true', 'true', 'false', 0, ''
                   FROM DUAL WHERE NOT EXISTS (
                     SELECT 1 FROM `npc_shop` WHERE `name`='파워볼진행자' AND `itemname`=%s AND `itembress`=1 AND `itemenlevel`=0 LIMIT 1
                   )""",
                (itemname, itemname),
            )
            if cur.rowcount:
                print("npc_shop 추가:", itemname)
            else:
                print("npc_shop 이미 있음:", itemname)
    conn.commit()
finally:
    conn.close()
print("Done. 서버에서 npc_shop 리로드 또는 재시작 하세요.")
