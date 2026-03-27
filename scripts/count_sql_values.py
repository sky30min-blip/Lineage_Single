# -*- coding: utf-8 -*-
import glob
import re

path = glob.glob(r"d:/Lineage_Single/*/db/gm_powerball/powerball_shop.sql")[0]
text = open(path, encoding="utf-8").read()


def parse_values(s):
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
            vals.append("str")
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


for idx, m in enumerate(
    re.finditer(r"VALUES\s*\((.*?)\)\s*\nON DUPLICATE", text, re.DOTALL)
):
    inner = m.group(1)
    c = parse_values(inner)
    print(f"INSERT {idx+1}: {c} values")

# columns in first INSERT
m0 = re.search(
    r"INSERT INTO `item` \(([^)]+)\)\s*VALUES", text, re.DOTALL
)
if m0:
    cols = [x.strip().strip("`") for x in m0.group(1).split(",")]
    print("columns in first INSERT:", len(cols))
