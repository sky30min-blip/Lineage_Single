# -*- coding: utf-8 -*-
r"""
아크네 서버 클라이언트의 map 폴더를 호두서버 클라(3.싱글리니지 클라이언트\map)에 적용.
기본 소스: Downloads\아크네 서버 클라이언트 1\아크네 서버\map

사용:
  python tools/apply_arkne_maps_to_hodu_client.py
  python tools/apply_arkne_maps_to_hodu_client.py "C:\\다른경로\\map"
"""
from __future__ import print_function
import shutil
import sys
from pathlib import Path
from datetime import datetime

_REPO = Path(__file__).resolve().parent.parent
TARGET_CLIENT = _REPO / "3.싱글리니지 클라이언트"
TARGET_MAP = TARGET_CLIENT / "map"

DEFAULT_ARKNE_MAP = Path(r"C:\Users\User\Downloads\아크네 서버 클라이언트 1\아크네 서버\map")


def find_arkne_map_folder():
    """Downloads 안에서 '클라이언트/하위폴더/map' 형태(중첩)로 .map 이 있는 경로."""
    dl = Path(r"C:\Users\User\Downloads")
    if not dl.is_dir():
        return None
    for client in sorted(dl.iterdir(), key=lambda p: p.name):
        if not client.is_dir():
            continue
        for sub in client.iterdir():
            if not sub.is_dir():
                continue
            mp = sub / "map"
            if mp.is_dir() and any(mp.glob("*.map")):
                return mp
    return None


def copy_tree_merge(src: Path, dst: Path):
    """dst 비우고 src 내용 전부 복사 (.map, .s32 등)."""
    if dst.exists():
        shutil.rmtree(dst)
    dst.mkdir(parents=True, exist_ok=True)
    n = 0
    for item in src.rglob("*"):
        if item.is_file():
            rel = item.relative_to(src)
            out = dst / rel
            out.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(item, out)
            n += 1
    return n


def main():
    if len(sys.argv) >= 2:
        src_map = Path(sys.argv[1])
    elif DEFAULT_ARKNE_MAP.is_dir() and any(DEFAULT_ARKNE_MAP.glob("*.map")):
        src_map = DEFAULT_ARKNE_MAP
    else:
        found = find_arkne_map_folder()
        if found is None:
            print("[오류] 아크네 map 폴더를 찾지 못했습니다.")
            print("  기본 경로:", DEFAULT_ARKNE_MAP)
            print("  또는: python tools/apply_arkne_maps_to_hodu_client.py \"C:\\경로\\map\"")
            return 1
        src_map = found

    if not src_map.is_dir():
        print("[오류] 소스 map 폴더가 없습니다:", src_map)
        return 1

    if not TARGET_CLIENT.is_dir():
        print("[오류] 호두 클라이언트 폴더가 없습니다:", TARGET_CLIENT)
        return 1

    print("=" * 60)
    print("아크네 클라이언트 map -> 호두서버 클라이언트 map")
    print("=" * 60)
    print("소스:", src_map)
    print("대상:", TARGET_MAP)

    if TARGET_MAP.exists():
        backup_name = f"map_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        backup_path = TARGET_CLIENT / backup_name
        print("\n기존 map 백업:", backup_path)
        shutil.move(str(TARGET_MAP), str(backup_path))
    else:
        TARGET_MAP.parent.mkdir(parents=True, exist_ok=True)

    TARGET_MAP.mkdir(parents=True, exist_ok=True)
    n = copy_tree_merge(src_map, TARGET_MAP)
    print("\n[완료] 복사한 파일 수:", n)
    print("클라이언트 map 경로:", TARGET_MAP)
    return 0


if __name__ == "__main__":
    sys.exit(main())
