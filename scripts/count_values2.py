# -*- coding: utf-8 -*-
import glob
import re

path = glob.glob(r"d:/Lineage_Single/*/db/gm_powerball/powerball_shop.sql")[0]
lines = open(path, encoding="utf-8").readlines()
def split_values(s):
    vals = []
    i = 0
    n = len(s)
    while i < n:
        while i < n and s[i] in " \t\r\n":
            i += 1
        if i >= n:
            break
        if s[i] == "'":
            i += 1
            buf = []
            while i < n:
                if s[i] == "'" and i + 1 < n and s[i + 1] == "'":
                    buf.append("'")
                    i += 2
                    continue
                if s[i] == "'":
                    i += 1
                    break
                buf.append(s[i])
                i += 1
            vals.append("'" + "".join(buf) + "'")
        else:
            j = i
            while j < n and s[j] not in ",":
                j += 1
            vals.append(s[i:j].strip())
            i = j
        while i < n and s[i] in " \t\r\n":
            i += 1
        if i < n and s[i] == ",":
            i += 1
    return vals


for i, line in enumerate(lines):
    if line.startswith("INSERT INTO `item`"):
        inner = line.split("VALUES (", 1)[1].rsplit(") ON DUPLICATE", 1)[0]
        print("inner tail", repr(inner[-40:]))
        vals = split_values(inner)
        print("line", i + 1, "value count", len(vals))
        print("last 8:", vals[-8:])
        break

cols_line = [l for l in lines if l.startswith("INSERT INTO `item` (`")][0]
colpart = cols_line.split("(", 1)[1].split(") VALUES", 1)[0]
cols = re.findall(r"`([^`]+)`", colpart)
print("column count", len(cols))
