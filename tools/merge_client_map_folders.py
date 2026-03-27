# -*- coding: utf-8 -*-
"""
클라이언트 map 폴더 병합: (1) 풀 맵으로 빈칸 채우기 (2) 아크네 등 원하는 폴더로 덮어쓰기

  python tools/merge_client_map_folders.py "D:\\풀맵\\map" "D:\\아크네\\map"
  python tools/merge_client_map_folders.py "D:\\풀맵\\map" "D:\\아크네\\map" "D:\\출력\\map"

출력을 생략하면 3.싱글리니지 클라이언트\\map 백업 후 병합 결과를 그곳에 넣습니다.

예: 풀맵 200개 + 아크네 76개(같은 번호는 아크네 우선) → 서버에 정의된 맵 번호 대부분 커버.
"""
from __future__ import print_function
import shutil
import sys
from pathlib import Path
from datetime import datetime

_REPO = Path(__file__).resolve().parent.parent
DEFAULT_TARGET = _REPO / "3.싱글리니지 클라이언트"


def copy_maps(src: Path, dst: Path, overwrite: bool):
    """src 의 *.map 를 dst 로 복사. overwrite=False 이면 이미 있으면 건너뜀."""
    if not src.is_dir():
        return 0, 0
    copied = skipped = 0
    for f in src.glob("*.map"):
        out = dst / f.name
        if out.is_file() and not overwrite:
            skipped += 1
            continue
        shutil.copy2(f, out)
        copied += 1
    return copied, skipped


def main():
    if len(sys.argv) < 3:
        print(
            "Usage:\n"
            "  python tools/merge_client_map_folders.py <베이스_풀맵_폴더> <덮어쓰기_폴더> [출력_폴더]\n"
            "  베이스 폴더의 map 을 먼저 모두 복사한 뒤, 두 번째 폴더로 같은 이름 덮어쓰기."
        )
        return 1

    base = Path(sys.argv[1])
    overlay = Path(sys.argv[2])
    if len(sys.argv) >= 4:
        out_root = Path(sys.argv[3])
        out_map = out_root if out_root.suffix.lower() == ".map" else out_root / "map"
    else:
        client = DEFAULT_TARGET
        out_map = client / "map"

    if not base.is_dir():
        print("[오류] 베이스 폴더 없음:", base)
        return 1
    if not overlay.is_dir():
        print("[오류] 덮어쓰기 폴더 없음:", overlay)
        return 1

    out_map.parent.mkdir(parents=True, exist_ok=True)
    if out_map.exists() and out_map.is_dir():
        backup_name = f"map_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        backup_path = out_map.parent / backup_name
        print("기존 map 백업:", backup_path)
        shutil.move(str(out_map), str(backup_path))

    out_map.mkdir(parents=True, exist_ok=True)

    c1, s1 = copy_maps(base, out_map, overwrite=True)
    print(f"[1] 베이스 복사: {c1}개 (대상 폴더 비어 있었음)")
    c2, _ = copy_maps(overlay, out_map, overwrite=True)
    print(f"[2] 덮어쓰기: {c2}개 (같은 번호는 두 번째 폴더 내용)")
    print("완료:", out_map)
    print("다음: python tools/check_client_maps_vs_server.py")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
