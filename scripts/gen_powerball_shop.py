# -*- coding: utf-8 -*-
"""백업 1번 INSERT를 템플릿으로 4종 쿠폰 INSERT 생성 (값 개수 동일)."""
import glob
import re

backup = r"d:/Lineage_GM_Powerball_backup_20260326_105713/server_overlay/db/powerball_shop.sql"
text = open(backup, encoding="utf-8").read().splitlines()
line5 = text[4]
m = re.match(r"INSERT INTO `item` VALUES \((.*)\)\s*$", line5)
if not m:
    raise SystemExit("parse line5 failed")
vals_template = m.group(1)

# 검증: 값 개수
def count_vals(s):
    vals = []
    i, n = 0, len(s)
    while i < n:
        while i < n and s[i] in " \t\r\n":
            i += 1
        if i >= n:
            break
        if s[i] == "'":
            i += 1
            while i < n:
                if s[i] == "'" and i + 1 < n and s[i + 1] == "'":
                    i += 2
                    continue
                if s[i] == "'":
                    i += 1
                    break
                i += 1
            vals.append(1)
        else:
            j = i
            while j < n and s[j] != ",":
                j += 1
            vals.append(s[i:j].strip())
            i = j
        while i < n and s[i] in " \t\r\n":
            i += 1
        if i < n and s[i] == ",":
            i += 1
    return len(vals)


n0 = count_vals(vals_template)
if n0 != 96:
    raise SystemExit(f"template value count expected 96, got {n0}")

rows = [
    ("홀 쿠폰", "$1249"),
    ("짝 쿠폰", "$1250"),
    ("언더 쿠폰", "$1254"),
    ("오버 쿠폰", "$1255"),
]

out = []
out.append("-- 파워볼: 홀/짝 쿠폰 NAMEID $1249/$1250 (레이스표와 분리), 인벤ID 151 (레이스표 아이콘)")
out.append("-- 실행: mysql -u계정 -p lin200 < powerball_shop.sql")
out.append("")
out.append("-- 1. 아이템: 홀 쿠폰 $1249, 짝 쿠폰 $1250, 인벤ID 151 (레이스표 모양)")
for i, (name, nid) in enumerate(rows):
    v = vals_template
    v = v.replace("'홀 쿠폰'", f"'{name}'", 1)
    v = v.replace("'$1249'", f"'{nid}'", 1)
    out.append(f"INSERT INTO `item` VALUES ({v})")
    out.append(
        f"ON DUPLICATE KEY UPDATE `아이템이름`='{name}', `NAMEID`='{nid}', `인벤ID`=151, `겹침`='false';"
    )
    if i == 1:
        out.append("")
        out.append(
            "-- 언더/오버 NAMEID는 1251/1252 금지: ItemDatabase.newInstance()가 해당 번호를 HealingPotion(농축 체력·고급 회복제)으로 고정함."
        )
    if i < 3:
        out.append("")

# 나머지는 백업에서 (npc_shop ~)
rest_start = None
for j, ln in enumerate(text):
    if ln.startswith("-- 2. 파워볼진행자"):
        rest_start = j
        break
if rest_start is None:
    raise SystemExit("rest not found")
out.append("")
out.extend(text[rest_start:])

dest = glob.glob(r"d:/Lineage_Single/*/db/gm_powerball/powerball_shop.sql")[0]
open(dest, "w", encoding="utf-8", newline="\n").write("\n".join(out) + "\n")
print("written:", dest)
body = "\n".join(out)
for idx, m in enumerate(
    re.finditer(r"INSERT INTO `item` VALUES \((.*?)\)\s*\nON DUPLICATE", body, re.DOTALL)
):
    print(f"INSERT {idx+1} values:", count_vals(m.group(1)))
