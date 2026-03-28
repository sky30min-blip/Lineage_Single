# -*- coding: utf-8 -*-
"""Docker l1j-db 컨테이너의 lin200에 파워볼 SQL 적용 (UTF-8 파이프, docker cp 미사용)."""
import glob
import subprocess
import sys

ROOT = r"d:\Lineage_Single"
PACK = glob.glob(ROOT + r"\*\db\gm_powerball")[0]
ORDERED = [
    "powerball_tables.sql",
    "powerball_reward_tables.sql",
    "powerball_claimed.sql",
    "powerball_shop.sql",
    "powerball_npc.sql",
]


def run_sql(path: str, ignore_error: bool = False) -> None:
    sql = open(path, encoding="utf-8").read()
    data = sql.encode("utf-8")
    cmd = [
        "docker",
        "exec",
        "-i",
        "l1j-db",
        "mariadb",
        "-u",
        "root",
        "-p1307",
        "--default-character-set=utf8mb4",
        "lin200",
    ]
    r = subprocess.run(cmd, input=data, capture_output=True)
    if r.returncode != 0:
        err = r.stderr.decode("utf-8", errors="replace")
        if ignore_error and "Duplicate column name 'claimed'" in err:
            print("skip claimed (already exists)")
            return
        print(err, file=sys.stderr)
        raise SystemExit(r.returncode)


def main() -> None:
    for name in ORDERED:
        path = PACK + "\\" + name
        print(">>", name)
        run_sql(path, ignore_error=(name == "powerball_claimed.sql"))
    verify = subprocess.run(
        [
            "docker",
            "exec",
            "l1j-db",
            "mariadb",
            "-u",
            "root",
            "-p1307",
            "lin200",
            "-e",
            "SHOW TABLES LIKE 'powerball_%';",
        ],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    print(verify.stdout)
    if verify.returncode != 0:
        raise SystemExit(verify.returncode)
    print("완료.")


if __name__ == "__main__":
    main()
