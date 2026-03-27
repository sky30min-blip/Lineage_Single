# -*- coding: utf-8 -*-
import subprocess

cmd = [
    "docker",
    "exec",
    "l1j-db",
    "mariadb",
    "-u",
    "root",
    "-p1307",
    "-N",
    "-e",
    "SELECT COLUMN_NAME FROM information_schema.COLUMNS "
    "WHERE TABLE_SCHEMA='lin200' AND TABLE_NAME='item' "
    "AND ORDINAL_POSITION<=79 ORDER BY ORDINAL_POSITION",
    "lin200",
]
r = subprocess.run(cmd, capture_output=True)
r.check_returncode()
names = [line.decode("utf-8") for line in r.stdout.splitlines() if line.strip()]
assert len(names) == 79, len(names)
cols = ", ".join("`" + n.replace("`", "``") + "`" for n in names)
open(r"d:\Lineage_Single\_item79_cols_sql.txt", "w", encoding="utf-8").write(cols)
print("ok", len(names), "columns")
