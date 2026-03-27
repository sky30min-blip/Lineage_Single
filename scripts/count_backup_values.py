# -*- coding: utf-8 -*-
import re

path = r"d:/Lineage_GM_Powerball_backup_20260326_105713/server_overlay/db/powerball_shop.sql"
text = open(path, encoding="utf-8").read()
m = re.search(r"VALUES\s*\((.*?)\)\s*\nON DUPLICATE", text, re.DOTALL)
body = m.group(1)
vals = []
i = 0
n = len(body)
while i < n:
    while i < n and body[i] in " \t\r\n":
        i += 1
    if i >= n:
        break
    if body[i] == "'":
        i += 1
        while i < n:
            if body[i] == "'" and i + 1 < n and body[i + 1] == "'":
                i += 2
                continue
            if body[i] == "'":
                i += 1
                break
            i += 1
        vals.append("q")
    else:
        j = i
        while j < n and body[j] != ",":
            j += 1
        vals.append(body[i:j].strip())
        i = j
    while i < n and body[i] in " \t\r\n":
        i += 1
    if i < n and body[i] == ",":
        i += 1
print("backup first INSERT value count:", len(vals))
for idx, m in enumerate(
    re.finditer(r"VALUES\s*\((.*?)\)\s*\nON DUPLICATE", text, re.DOTALL)
):
    body = m.group(1)
    vals = []
    i = 0
    n = len(body)
    while i < n:
        while i < n and body[i] in " \t\r\n":
            i += 1
        if i >= n:
            break
        if body[i] == "'":
            i += 1
            while i < n:
                if body[i] == "'" and i + 1 < n and body[i + 1] == "'":
                    i += 2
                    continue
                if body[i] == "'":
                    i += 1
                    break
                i += 1
            vals.append(1)
        else:
            j = i
            while j < n and body[j] != ",":
                j += 1
            vals.append(body[i:j].strip())
            i = j
        while i < n and body[i] in " \t\r\n":
            i += 1
        if i < n and body[i] == ",":
            i += 1
    print(f"INSERT {idx+1}: {len(vals)} values")
