# -*- coding: utf-8 -*-
"""
서버 maps/Maps.csv 에 정의된 맵 ID 중,
클라이언트 map/*.map 이 없는 번호를 출력합니다.

  python tools/check_client_maps_vs_server.py
  python tools/check_client_maps_vs_server.py "D:\\path\\to\\client\\map"
"""
from __future__ import print_function
import sys
from pathlib import Path

_REPO = Path(__file__).resolve().parent.parent
SERVER_CSV = _REPO / "2.싱글리니지 팩" / "maps" / "Maps.csv"
DEFAULT_CLIENT_MAP = _REPO / "3.싱글리니지 클라이언트" / "map"


def load_map_ids_from_csv(path: Path):
    ids = []
    if not path.is_file():
        return ids
    with open(path, "r", encoding="utf-8", errors="replace") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split(",", 1)
            if parts and parts[0].strip().isdigit():
                ids.append(int(parts[0].strip()))
    return sorted(set(ids))


def main():
    client_map = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_CLIENT_MAP
    ids = load_map_ids_from_csv(SERVER_CSV)
    missing = []
    for mid in ids:
        if not (client_map / f"{mid}.map").is_file():
            missing.append(mid)

    print("서버 Maps.csv 맵 ID 수:", len(ids))
    print("클라이언트 map 폴더:", client_map)
    print(".map 존재:", len(ids) - len(missing))
    print(".map 없음:", len(missing))
    if missing:
        print("\n없는 맵 번호 (일부만 표시):")
        s = ",".join(str(x) for x in missing[:80])
        print(s + (" ..." if len(missing) > 80 else ""))
        print(
            "\n해결: 클라이언트에 '풀 맵 팩'(서버 맵 수에 맞는 .map)을 넣은 뒤,"
            " 아크네만 쓰고 싶은 번호는 그 위에 덮어쓰세요."
            "\n  python tools/merge_client_map_folders.py <베이스_풀맵> <아크네_map> [출력폴더]"
        )
        return 1
    print("\nOK: 서버에 정의된 맵 번호에 대해 클라이언트 .map이 모두 있습니다.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
