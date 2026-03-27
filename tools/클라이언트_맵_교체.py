# -*- coding: utf-8 -*-
"""
클라이언트 맵만 교체: 다운로드 map -> 클라이언트 map
- 기존 클라이언트 map 백업 후, 다운로드 맵으로 덮어쓰기
"""
import os
import shutil
from pathlib import Path
from datetime import datetime

# 저장소 루트 (스크립트: tools/ 기준)
LINEAGE_SINGLE = Path(__file__).resolve().parent.parent
DOWNLOAD_MAP = Path(r"C:\Users\User\Downloads\map\map")

def find_client_folder():
    """3.싱글리니지 클라이언트 폴더 찾기"""
    for d in LINEAGE_SINGLE.iterdir():
        if not d.is_dir():
            continue
        if "클라이언트" in d.name or "3." in d.name:
            return d
    return LINEAGE_SINGLE / "3.싱글리니지 클라이언트"

def main():
    client_root = find_client_folder()
    client_map = client_root / "map"
    
    print("=" * 60)
    print("  클라이언트 맵 교체 (백업 후 복사)")
    print("=" * 60)
    print(f"  다운로드 맵: {DOWNLOAD_MAP}")
    print(f"  클라이언트 map 폴더: {client_map}")
    
    if not DOWNLOAD_MAP.exists():
        print("\n  [오류] 다운로드 맵 폴더가 없습니다.")
        return
    if not client_root.exists():
        print("\n  [오류] 클라이언트 폴더를 찾을 수 없습니다.")
        return
    
    # 기존 map 백업
    if client_map.exists():
        backup_name = f"map_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        backup_path = client_root / backup_name
        print(f"\n  기존 map 폴더를 백업합니다: {backup_name}")
        shutil.move(str(client_map), str(backup_path))
        print("  백업 완료.")
    else:
        client_map.mkdir(parents=True, exist_ok=True)
    
    # 다운로드 맵 복사 (map 폴더 내용을 클라이언트 map으로)
    print("\n  다운로드 맵을 클라이언트 map으로 복사합니다...")
    shutil.copytree(DOWNLOAD_MAP, client_map)
    print("  복사 완료.")
    
    print("\n  나중에 복구하려면:")
    if client_map.exists() and client_root.exists():
        backups = [d for d in client_root.iterdir() if d.is_dir() and d.name.startswith("map_backup_")]
        if backups:
            latest = max(backups, key=lambda x: x.stat().st_mtime)
            print(f"    - 클라이언트의 'map' 폴더 삭제 후")
            print(f"    - '{latest.name}' 폴더 이름을 'map'으로 바꾸면 됩니다.")
    print("=" * 60)

if __name__ == "__main__":
    main()
