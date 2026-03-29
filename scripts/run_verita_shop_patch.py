# -*- coding: utf-8 -*-
"""
베리타 상점 패치: npc_shop + item (구분2 teleport_N, 그래픽·가격 통일).
live DB의 item 컬럼 수가 덤프와 다를 수 있어, 기존 행은 UPDATE / 없으면 템플릿 행 복제 INSERT.
"""
from __future__ import annotations

import sys
from pathlib import Path

_ROOT = Path(__file__).resolve().parents[1]
if str(_ROOT) not in sys.path:
    sys.path.insert(0, str(_ROOT))

import pymysql.cursors

import config

NAMES = [
    "기란 마을 이동 부적",
    "요정족 마을 이동 주문서",
    "카오틱 신전 이동 주문서",
    "라우풀 신전 이동 주문서",
    "요정족 던전 입구 이동 주문서",
    "골밭 이동 주문서",
    "오우거밭 이동 주문서",
    "말섬 던전 1층 이동 주문서",
    "개미굴 입구 이동 주문서",
    "사막 던전 4층 이동 주문서",
    "좀비 엘모어 밭 이동 주문서",
    "용의 계곡 삼거리 이동 주문서",
    "화룡의 둥지 이동 주문서",
    "본토 던전 입구 이동 주문서",
    "본토 던전 3층 이동 주문서",
    "본토 던전 5층 이동 주문서",
    "용의 계곡 던전 3층 이동 주문서",
    "용의 계곡 던전 5층 이동 주문서",
    "기란 던전 3층 이동 주문서",
    "상아탑 4층 이동 주문서",
    "커츠 이동 주문서",
    "바포메트 이동 주문서",
    "여왕개미 이동 주문서",
    "피닉스 이동 주문서",
    "데몬 이동 주문서",
    "잊혀진섬 이동 주문서",
    "얼음여왕 이동 주문서",
    "베레스 이동 주문서",
    "수중 던전 이동 주문서",
    "오만의탑 1층 이동 부적",
    "오만의탑 2층 이동 부적",
    "오만의탑 3층 이동 부적",
    "오만의탑 4층 이동 부적",
    "오만의탑 5층 이동 부적",
    "오만의탑 6층 이동 부적",
    "오만의탑 7층 이동 부적",
    "오만의탑 8층 이동 부적",
    "오만의탑 9층 이동 부적",
    "오만의탑 10층 이동 부적",
    "오만의탑 정상 이동 부적",
    "오만의 탑 1층 지배 부적",
    "오만의 탑 2층 지배 부적",
    "오만의 탑 3층 지배 부적",
    "오만의 탑 4층 지배 부적",
    "오만의 탑 5층 지배 부적",
    "오만의 탑 6층 지배 부적",
    "오만의 탑 7층 지배 부적",
    "오만의 탑 8층 지배 부적",
    "오만의 탑 9층 지배 부적",
    "오만의 탑 10층 지배 부적",
    "오만의 탑 정상 지배 부적",
    "본토 던전 6층 이동 주문서",
    "본토 던전 7층 이동 주문서",
    "거인 모닝스타 이동 주문서",
    "안타라스 이동 주문서",
    "파푸리온 이동 주문서",
    "린드비오르 이동 주문서",
    "발라카스 이동 주문서",
    "윈던 1층 이동 주문서",
    "말하는 섬 이동 주문서",
    "이벤트 이동 주문서",
    "오만의탑 1층 이동 주문서",
    "오만의탑 2층 이동 주문서",
    "오만의탑 3층 이동 주문서",
    "오만의탑 4층 이동 주문서",
    "오만의탑 5층 이동 주문서",
    "오만의탑 6층 이동 주문서",
    "오만의탑 7층 이동 주문서",
    "오만의탑 8층 이동 주문서",
    "오만의탑 9층 이동 주문서",
    "오만의탑 10층 이동 주문서",
    "오만의탑 정상 이동 주문서",
    "시장 이동 부적",
    "테베라스 사막 이동 주문서",
    "악마왕의 영토 이동 주문서",
    "테스트",
    "은기사 이동 주문서",
    "하이네 이동 주문서",
    "글루딘 이동 주문서",
    "우드벡 이동 주문서",
    "켈트 이동 주문서",
    "아덴 이동 주문서",
    "오렌 이동 주문서",
    "화전민 이동 주문서",
    "악영 입장 티켓",
    "로테이션 던전 앞 이동",
    "결투장 이동",
    "망자의무덤 이동",
    "마일리지 적립존",
]


def _template_row(cur) -> dict:
    cur.execute(
        "SELECT * FROM `item` WHERE `구분2` = %s LIMIT 1",
        ("teleport_62",),
    )
    r = cur.fetchone()
    if r:
        return dict(r)
    cur.execute(
        "SELECT * FROM `item` WHERE `아이템이름` LIKE %s LIMIT 1",
        ("%이동 주문서",),
    )
    r = cur.fetchone()
    if not r:
        raise RuntimeError("템플릿용 item 행이 없습니다 (teleport_62 또는 '%이동 주문서').")
    return dict(r)


def _apply_row(cur, tpl: dict, pk_col: str, name: str, uid: int) -> None:
    row = dict(tpl)
    row[pk_col] = name
    if "NAMEID" in row:
        row["NAMEID"] = name
    if "구분2" in row:
        row["구분2"] = f"teleport_{uid}"
    if "재질" in row:
        row["재질"] = "종이"
    if "인벤ID" in row:
        row["인벤ID"] = 5487
    if "GFXID" in row:
        row["GFXID"] = 22
    if "shop_price" in row:
        row["shop_price"] = 20000

    cur.execute(
        f"SELECT COUNT(*) AS c FROM `item` WHERE `{pk_col}` = %s",
        (name,),
    )
    n = int(cur.fetchone()["c"])
    if n:
        sets = [f"`{k}` = %s" for k in row.keys() if k != pk_col]
        vals = [row[k] for k in row.keys() if k != pk_col]
        sql = f"UPDATE `item` SET {', '.join(sets)} WHERE `{pk_col}` = %s"
        cur.execute(sql, (*vals, name))
    else:
        cols = list(row.keys())
        ph = ", ".join(["%s"] * len(cols))
        sql = f"INSERT INTO `item` (`{'`, `'.join(cols)}`) VALUES ({ph})"
        cur.execute(sql, tuple(row[c] for c in cols))


def main() -> int:
    assert len(NAMES) == 89
    cfg = dict(config.DB_CONFIG)
    cfg["cursorclass"] = pymysql.cursors.DictCursor
    conn = pymysql.connect(**cfg)
    pk = "아이템이름"
    try:
        with conn.cursor() as cur:
            tpl = _template_row(cur)
            if pk not in tpl:
                raise RuntimeError(f"템플릿에 PK `{pk}` 없음")

            cur.execute("DELETE FROM `npc_shop` WHERE `name` = %s", ("베리타",))

            for i, name in enumerate(NAMES, start=1):
                _apply_row(cur, tpl, pk, name, i)

            for i, name in enumerate(NAMES, start=1):
                cur.execute(
                    """INSERT INTO `npc_shop`
                       (`uid`, `name`, `itemname`, `itemcount`, `itembress`, `itemenlevel`, `itemtime`,
                        `sell`, `buy`, `gamble`, `price`, `aden_type`)
                       VALUES (%s, %s, %s, 1, 1, 0, 0, 'false', 'true', 'false', %s, '아데나')""",
                    (i, "베리타", name, 20000),
                )

            cur.execute(
                "SELECT COUNT(*) AS c FROM `npc_shop` WHERE `name` = %s",
                ("베리타",),
            )
            cnt = cur.fetchone()["c"]
        conn.commit()
        print("OK: 베리타 npc_shop 행 수 =", cnt)
    except Exception as e:
        conn.rollback()
        print("FAIL:", e)
        raise
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
