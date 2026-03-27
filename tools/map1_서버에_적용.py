# -*- coding: utf-8 -*-
"""
클라이언트 폴더의 map1 (서버 형식: Maps.csv + Text/*.txt) 을
서버 maps 폴더에 적용합니다.

사용 (저장소 루트에서): python tools/map1_서버에_적용.py
"""
import os
import shutil
from pathlib import Path
from datetime import datetime

_REPO = Path(__file__).resolve().parent.parent
CLIENT_DIR = _REPO / "3.싱글리니지 클라이언트"
MAP1_DIR = CLIENT_DIR / "map1"
SERVER_MAPS = _REPO / "2.싱글리니지 팩" / "maps"


def main():
    print("[map1 -> 서버 적용]")
    if not MAP1_DIR.is_dir():
        print(f"map1 폴더가 없습니다: {MAP1_DIR}")
        print("3.싱글리니지 클라이언트 아래에 map1 폴더를 만들어 두세요.")
        return 1

    maps_csv = MAP1_DIR / "Maps.csv"
    if not maps_csv.is_file():
        maps_csv = MAP1_DIR / "maps.csv"
    if not maps_csv.is_file():
        print(f"Maps.csv 없음: {MAP1_DIR}")
        print("map1 안에 Maps.csv(또는 maps.csv)와 Text 폴더가 있어야 합니다.")
        print("클라이언트용(.map, .s32)만 있으면 서버에 직접 적용할 수 없습니다.")
        return 1

    text_dir = MAP1_DIR / "Text"
    if not text_dir.is_dir():
        print(f"Text 폴더 없음: {text_dir}")
        return 1

    # 백업
    backup_name = f"maps_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
    backup_dir = SERVER_MAPS.parent / backup_name
    print(f"백업 중: {SERVER_MAPS} -> {backup_dir}")
    shutil.copytree(SERVER_MAPS, backup_dir)
    print("백업 완료.")

    # Maps.csv: map1에 있는 맵 번호만 서버에 추가(병합). 기존 서버 맵은 유지.
    server_csv = SERVER_MAPS / "Maps.csv"
    existing_ids = set()
    if server_csv.is_file():
        with open(server_csv, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                parts = line.split(",", 1)
                if parts:
                    try:
                        existing_ids.add(int(parts[0]))
                    except ValueError:
                        pass
    new_lines = []
    with open(maps_csv, "r", encoding="utf-8") as f:
        for line in f:
            line_stripped = line.strip()
            if not line_stripped or line_stripped.startswith("#"):
                continue
            parts = line_stripped.split(",", 1)
            if parts:
                try:
                    mid = int(parts[0])
                    if mid not in existing_ids:
                        new_lines.append(line)
                        existing_ids.add(mid)
                except ValueError:
                    pass
    if new_lines:
        with open(server_csv, "a", encoding="utf-8") as f:
            for l in new_lines:
                f.write(l if l.endswith("\n") else l + "\n")
        print(f"Maps.csv에 맵 {len(new_lines)}줄 추가.")
    else:
        print("Maps.csv: 추가할 새 맵 없음 (이미 있거나 형식 다름).")

    # Text: map1/Text/*.txt 를 서버 maps/Text/ 로 복사 (덮어쓰기)
    for f in text_dir.glob("*.txt"):
        shutil.copy2(f, SERVER_MAPS / "Text" / f.name)
    print(f"복사: Text/*.txt -> {SERVER_MAPS / 'Text'}")

    # Cache 삭제 (서버 재시작 시 Text 기준으로 재생성)
    cache_dir = SERVER_MAPS / "Cache"
    if cache_dir.is_dir():
        for f in cache_dir.iterdir():
            if f.is_file():
                f.unlink()
        print("Cache 폴더 비움 (서버 재시작 시 재생성)")

    print("")
    print("적용 완료. 반드시 서버를 재시작하세요.")
    return 0


if __name__ == "__main__":
    exit(main())
