# -*- coding: utf-8 -*-
"""
NPC 상점 가격 — 서버 ShopInstance / S_ShopBuy / S_ShopSell 과 동일한 규칙으로
npc_shop.price 가 0일 때의 '표시 가격'을 추정한다. (성 세금 kingdom.getTax() 는 0 으로 둠)
"""
from __future__ import annotations

import re
from pathlib import Path
from typing import Any, Dict, Optional


def _parse_line_key_value(path: Path) -> Dict[str, str]:
    out: Dict[str, str] = {}
    if not path.is_file():
        return out
    raw = path.read_text(encoding="utf-8", errors="replace")
    if raw.startswith("\ufeff"):
        raw = raw[1:]
    for line in raw.splitlines():
        s = line.strip()
        if not s or s.startswith("#"):
            continue
        if "=" not in s:
            continue
        k, _, v = s.partition("=")
        key = k.strip().lower()
        val = v.strip()
        if key:
            out[key] = val
    return out


def load_lineage_shop_conf(pack_dir: Path) -> Dict[str, Any]:
    """lineage.conf 에서 상점·매입 가격에 쓰이는 값만 읽는다."""
    conf_path = pack_dir / "lineage.conf"
    kv = _parse_line_key_value(conf_path)
    # Java: sell_item_rate = Double.valueOf(value) * 0.01
    try:
        sir = float(kv.get("sell_item_rate", "40")) * 0.01
    except (TypeError, ValueError):
        sir = 0.4
    try:
        sbless = float(kv.get("sell_bless_item_rate", "2"))
    except (TypeError, ValueError):
        sbless = 2.0
    try:
        scurse = float(kv.get("sell_curse_item_rate", "1.5"))
    except (TypeError, ValueError):
        scurse = 1.5
    add_tax = str(kv.get("add_tax", "false")).lower() == "true"
    return {
        "add_tax": add_tax,
        "sell_item_rate": sir,
        "sell_bless_item_rate": sbless,
        "sell_curse_item_rate": scurse,
        "scroll_dane_fools": (kv.get("scroll_dane_fools") or "").strip(),
        "scroll_zel_go_mer": (kv.get("scroll_zel_go_mer") or "").strip(),
        "scroll_orim": (kv.get("scroll_orim") or "").strip(),
        "scroll_tell": (kv.get("scroll_tell") or "").strip(),
        "scroll_poly": (kv.get("scroll_poly") or "").strip(),
    }


def tax_price_buy(base: float, add_tax: bool, tax_pct: float) -> int:
    """ShopInstance.getTaxPrice(price, false) — 필드 상점 tax=0 가정."""
    a = float(base)
    if add_tax and tax_pct:
        a += a * (tax_pct * 0.01)
    return int(round(a))


def tax_price_sell_item(shop_price_val: float, add_tax: bool, tax_pct: float, sell_item_rate: float) -> int:
    """ShopInstance.getTaxPrice(..., true) — 매입 시 아이템 shop_price 기준."""
    a = float(shop_price_val) * float(sell_item_rate)
    if add_tax and tax_pct:
        a -= a * (tax_pct * 0.01)
    return int(round(a))


def _parse_nameid_number(nameid: Any) -> Optional[int]:
    if nameid is None:
        return None
    s = str(nameid).strip().replace("$", "").strip()
    if not s:
        return None
    try:
        return int(re.sub(r"[^\d-]", "", s) or "0")
    except ValueError:
        return None


def fetch_item_row_by_display_name(db, item_name: str) -> Optional[Dict[str, Any]]:
    if not (item_name or "").strip():
        return None
    name = item_name.strip()
    try:
        r = db.fetch_one("SELECT * FROM item WHERE `아이템이름` = %s LIMIT 1", (name,))
        if r:
            return r
    except Exception:
        pass
    try:
        r = db.fetch_one("SELECT * FROM item WHERE name = %s LIMIT 1", (name,))
        if r:
            return r
    except Exception:
        pass
    return None


def fetch_shop_price_by_nameid(db, num: int) -> int:
    """ItemDatabase.find(nameIdNumber) 용 — NAMEID 가 $n 인 행의 shop_price."""
    if num <= 0:
        return 0
    for pat in (f"${num}", str(num)):
        try:
            r = db.fetch_one(
                "SELECT shop_price FROM item WHERE NAMEID = %s LIMIT 1",
                (pat,),
            )
            if r and r.get("shop_price") is not None:
                return int(r["shop_price"])
        except Exception:
            continue
    return 0


def _item_field(row: Dict[str, Any], *keys: str) -> Any:
    for k in keys:
        if k in row and row[k] is not None:
            return row[k]
    return None


def _is_scroll_poly_tell(name: str, conf: Dict[str, Any]) -> bool:
    n = (name or "").strip()
    if not n:
        return False
    for k in (
        "scroll_dane_fools",
        "scroll_zel_go_mer",
        "scroll_orim",
        "scroll_tell",
        "scroll_poly",
    ):
        v = (conf.get(k) or "").strip()
        if v and n.lower() == v.lower():
            return True
    return False


def calc_buy_base_before_tax(
    db,
    npc_row: Dict[str, Any],
    item_row: Dict[str, Any],
    conf: Dict[str, Any],
) -> int:
    """
    npc_shop.price 가 0일 때 S_ShopBuy.toShop / ShopInstance.toBuy 과 같은 '세전 기준가'.
    """
    npc_p = int(_item_field(npc_row, "price") or 0)
    if npc_p != 0:
        return npc_p

    t1 = str(_item_field(item_row, "구분1", "type1") or "").lower()
    t2 = str(_item_field(item_row, "구분2", "type2") or "").lower()
    iname = str(_item_field(item_row, "아이템이름", "name") or "")
    bress = int(_item_field(npc_row, "itembress", "item_bress") or 1)
    enlv = int(_item_field(npc_row, "itemenlevel", "item_enlevel") or 0)
    icnt = int(_item_field(npc_row, "itemcount") or 1)
    isp = int(_item_field(item_row, "shop_price") or 0)

    if t1 in ("weapon", "armor") and t2 not in ("necklace", "ring", "belt"):
        if t1 == "weapon":
            en_add = enlv * fetch_shop_price_by_nameid(db, 244)
            return isp * icnt + en_add
        en_add = enlv * fetch_shop_price_by_nameid(db, 249)
        return isp * icnt + en_add

    if _is_scroll_poly_tell(iname, conf) and bress in (0, 2):
        nid = _parse_nameid_number(_item_field(item_row, "NAMEID", "nameid"))
        tpl = fetch_shop_price_by_nameid(db, nid) if nid else isp
        if tpl <= 0:
            tpl = isp
        rate = conf["sell_bless_item_rate"] if bress == 0 else conf["sell_curse_item_rate"]
        return int(round(tpl * rate))

    return isp * icnt


def calc_buy_display_price(
    db,
    npc_row: Dict[str, Any],
    item_row: Optional[Dict[str, Any]],
    conf: Dict[str, Any],
    tax_pct: float = 0.0,
) -> int:
    """플레이어가 NPC에게 살 때 창에 나오는 금액(추정, 성주 세율 0)."""
    if not item_row:
        raw = max(0, int(_item_field(npc_row, "price") or 0))
        return max(0, tax_price_buy(raw, conf.get("add_tax", False), tax_pct))
    base = calc_buy_base_before_tax(db, npc_row, item_row, conf)
    return max(0, tax_price_buy(base, conf.get("add_tax", False), tax_pct))


def calc_sell_display_price(
    db,
    npc_row: Dict[str, Any],
    item_row: Optional[Dict[str, Any]],
    conf: Dict[str, Any],
    tax_pct: float = 0.0,
) -> int:
    """플레이어가 NPC에게 팔 때 창에 나오는 금액(추정). price!=0 이면 DB 값 그대로."""
    p = int(_item_field(npc_row, "price") or 0)
    if p != 0:
        return max(0, p)
    if not item_row:
        return 0
    iname = str(_item_field(item_row, "아이템이름", "name") or "")
    isp = int(_item_field(item_row, "shop_price") or 0)
    bless = int(_item_field(npc_row, "itembress", "item_bress") or 1)

    if _is_scroll_poly_tell(iname, conf) and bless in (0, 2):
        nid = _parse_nameid_number(_item_field(item_row, "NAMEID", "nameid"))
        tpl = fetch_shop_price_by_nameid(db, nid) if nid else isp
        if tpl <= 0:
            tpl = isp
        rate = conf["sell_bless_item_rate"] if bless == 0 else conf["sell_curse_item_rate"]
        base = tpl * rate
    else:
        base = float(isp)

    return max(
        0,
        tax_price_sell_item(
            base,
            conf.get("add_tax", False),
            tax_pct,
            conf.get("sell_item_rate", 0.4),
        ),
    )


def resolve_pack_dir() -> Path:
    """Lineage_Single / 2.싱글리니지 팩 (없으면 존재하지 않는 경로 — conf 기본값만 사용)."""
    here = Path(__file__).resolve()
    for p in here.parents:
        cand = p / "2.싱글리니지 팩"
        if cand.is_dir() and (cand / "lineage.conf").is_file():
            return cand
    return here.parent / "_no_lineage_pack_"


def invert_buy_display_to_db_price(display: int, add_tax: bool, tax_pct: float) -> int:
    """사용자가 입력한 '표시 가격'을 npc_shop.price(세전 기준)로 되돌림. 세금 0이면 동일."""
    d = max(0, int(display))
    if not add_tax or not tax_pct:
        return d
    # display = round(base * (1 + tax/100))
    factor = 1.0 + (tax_pct * 0.01)
    if factor <= 0:
        return d
    return max(0, int(round(d / factor)))
