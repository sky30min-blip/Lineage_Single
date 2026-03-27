# -*- coding: utf-8 -*-
import re
import glob

paths = glob.glob(r"d:/Lineage_Single/*/db/gm_powerball/powerball_shop.sql")
path = paths[0]
text = open(path, encoding="utf-8").read()
m = re.search(r"INSERT INTO `item` VALUES \((.*)\)\s*ON DUPLICATE", text, re.DOTALL)
if not m:
    print("no match")
    raise SystemExit(1)
body = m.group(1)
vals = []
i = 0
while i < len(body):
    while i < len(body) and body[i] in " \n\t":
        i += 1
    if i >= len(body):
        break
    if body[i] == "'":
        i += 1
        s = ""
        while i < len(body):
            if body[i] == "'" and i + 1 < len(body) and body[i + 1] == "'":
                s += "'"
                i += 2
                continue
            if body[i] == "'":
                i += 1
                break
            s += body[i]
            i += 1
        vals.append(s)
    else:
        j = i
        while j < len(body) and body[j] not in ",)":
            j += 1
        token = body[i:j].strip()
        vals.append(token)
        i = j
    while i < len(body) and body[i] in " \n\t":
        i += 1
    if i < len(body) and body[i] == ",":
        i += 1
print("value count", len(vals))
