# -*- coding: utf-8 -*-
"""
로마 서버의 maps 폴더(Maps.csv, Text/*.txt)를
현재 서버(2.싱글리니지 팩\maps)로 복사하는 스크립트.

사용법:
  로마 "서버" 폴더 안에 maps 가 있을 때:
  python tools/copy_server_maps_from_roma.py "C:\경로\로마서버\maps"

  예: python tools/copy_server_maps_from_roma.py "C:\Users\User\Downloads\로마서버\maps"
"""
import shutil
import os
import sys
from pathlib import Path
from datetime import datetime

# 현재 서버 maps 경로 (저장소 루트 기준)
TARGET_DIR = Path(__file__).resolve().parent.parent / "2.싱글리니지 팩" / "maps"

def main():
    if len(sys.argv) < 2:
        print("Usage: python tools/copy_server_maps_from_roma.py <로마서버_maps_경로>")
        print("Example: python tools/copy_server_maps_from_roma.py \"C:\\roma_server\\maps\"")
        return 1

    source_dir = Path(sys.argv[1])
    if not source_dir.is_dir():
        print(f"[ERROR] 소스 폴더가 없습니다: {source_dir}")
        return 1

    maps_csv = source_dir / "Maps.csv"
    if not maps_csv.is_file():
        maps_csv = source_dir / "maps.csv"
    if not maps_csv.is_file():
        print(f"[ERROR] Maps.csv 를 찾을 수 없습니다: {source_dir}")
        return 1

    text_src = source_dir / "Text"
    if not text_src.is_dir():
        print(f"[WARN] Text 폴더 없음: {text_src} (Maps.csv 만 복사합니다)")

    # 백업
    backup_name = f"maps_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
    backup_dir = TARGET_DIR.parent / backup_name
    print(f"[1] 백업 중: {TARGET_DIR} -> {backup_dir}")
    shutil.copytree(TARGET_DIR, backup_dir)
    print("[OK] 백업 완료")

    # Maps.csv 복사
    target_csv = TARGET_DIR / "Maps.csv"
    if not TARGET_DIR.exists():
        TARGET_DIR.mkdir(parents=True, exist_ok=True)
    shutil.copy2(maps_csv, target_csv)
    print(f"[OK] 복사: Maps.csv")

    # Text 폴더 복사
    if text_src.is_dir():
        text_dst = TARGET_DIR / "Text"
        if text_dst.exists():
            shutil.rmtree(text_dst)
        shutil.copytree(text_src, text_dst)
        print(f"[OK] 복사: Text 폴더")

    # Cache 삭제 (서버 재시작 시 Text 기준으로 다시 생성됨)
    cache_dir = TARGET_DIR / "Cache"
    if cache_dir.is_dir():
        for f in cache_dir.iterdir():
            if f.is_file():
                f.unlink()
        print("[OK] Cache 폴더 비움 (서버 재시작 시 재생성)")

    print("")
    print("완료. 반드시 서버를 재시작하세요.")
    return 0

if __name__ == "__main__":
    sys.exit(main())
