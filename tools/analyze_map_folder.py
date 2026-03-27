# -*- coding: utf-8 -*-
"""
다운로드 map 폴더 vs 서버 maps 폴더 분석
- 파일 목록, 확장자별 개수, 처음 10개 파일명
- 리니지 서버용/클라이언트용 맵 여부 판단
- 두 폴더 형식 호환 여부
"""
import os
from pathlib import Path
from collections import Counter

DOWNLOAD_MAP = Path(r"C:\Users\User\Downloads\map\map")
SERVER_MAPS = Path(__file__).resolve().parent.parent / "2.싱글리니지 팩" / "maps"

def analyze_folder(folder: Path, name: str) -> dict:
    """폴더 내 모든 파일을 재귀 탐색하여 확장자별 개수, 파일 목록 수집"""
    if not folder.exists():
        return {"exists": False, "name": name, "path": str(folder)}
    
    ext_counter = Counter()
    all_files = []
    for root, dirs, files in os.walk(folder):
        for f in files:
            fp = Path(root) / f
            ext = fp.suffix.lower() if fp.suffix else "(no ext)"
            ext_counter[ext] += 1
            all_files.append(fp.relative_to(folder))
    
    return {
        "exists": True,
        "name": name,
        "path": str(folder),
        "ext_counter": ext_counter,
        "all_files": sorted(all_files),
        "total": len(all_files),
    }

def main():
    print("=" * 70)
    print("  맵 폴더 분석: 다운로드 map vs 서버 maps")
    print("=" * 70)
    
    # 1) 다운로드 폴더 분석
    print("\n[1] 다운로드 폴더: C:\\Users\\User\\Downloads\\map\\map")
    print("-" * 50)
    down = analyze_folder(DOWNLOAD_MAP, "다운로드 map")
    
    if not down["exists"]:
        print("  폴더가 존재하지 않습니다.")
    else:
        print(f"  전체 파일 수: {down['total']}")
        print("\n  확장자별 개수:")
        for ext, count in down["ext_counter"].most_common():
            print(f"    {ext}: {count}개")
        print("\n  처음 10개 파일:")
        for i, f in enumerate(down["all_files"][:10], 1):
            print(f"    {i}. {f}")
    
    # 2) 서버 maps 폴더 분석
    print("\n[2] 서버 maps 폴더: D:\\Lineage_Single\\2.싱글리니지 팩\\maps")
    print("-" * 50)
    srv = analyze_folder(SERVER_MAPS, "서버 maps")
    
    if not srv["exists"]:
        print("  폴더가 존재하지 않습니다.")
    else:
        print(f"  전체 파일 수: {srv['total']}")
        print("\n  확장자별 개수:")
        for ext, count in srv["ext_counter"].most_common():
            print(f"    {ext}: {count}개")
        print("\n  처음 10개 파일:")
        for i, f in enumerate(srv["all_files"][:10], 1):
            print(f"    {i}. {f}")
    
    # 3) 파일이 뭔지 판단 (서버용 vs 클라이언트용)
    print("\n[3] 파일 종류 판단 (리니지 서버용 vs 클라이언트용)")
    print("-" * 50)
    
    # 리니지 서버 maps: Maps.csv, Text/*.txt, Cache/*.data 등
    # 리니지 클라이언트 map: .map, .s32, .seg, .ini 등 (타일/지형 데이터)
    server_extensions = {".csv", ".txt", ".data"}
    client_extensions = {".map", ".s32", ".seg", ".ini"}
    
    if down["exists"]:
        down_exts = set(down["ext_counter"].keys())
        has_server = bool(down_exts & server_extensions)
        has_client = bool(down_exts & client_extensions)
        has_csv = ".csv" in down_exts
        has_txt = ".txt" in down_exts
        has_map = ".map" in down_exts
        has_s32 = ".s32" in down_exts
        
        print("  [다운로드 폴더]")
        print(f"    확장자: {sorted(down_exts)}")
        if has_csv and has_txt and not has_map:
            print("    판단: 리니지 **서버용** 맵 데이터 (Maps.csv + Text/*.txt 구조)")
        elif has_map or has_s32 or ".seg" in down_exts:
            print("    판단: 리니지 **클라이언트용** 맵 데이터 (.map, .s32, .seg 등 타일/지형)")
        elif has_csv or has_txt:
            print("    판단: 서버용 맵과 유사 (일부만 있으면 서버용일 수 있음)")
        else:
            print("    판단: 확장자만으로는 서버/클라이언트 구분 불명확")
    
    if srv["exists"]:
        srv_exts = set(srv["ext_counter"].keys())
        print("  [서버 maps 폴더]")
        print(f"    확장자: {sorted(srv_exts)}")
        if ".csv" in srv_exts and ".txt" in srv_exts:
            print("    판단: 리니지 **서버용** 맵 (Maps.csv, Text/*.txt, Cache/*.data)")
    
    # 4) 두 폴더 호환 여부
    print("\n[4] 두 폴더 형식 호환 여부")
    print("-" * 50)
    
    if not down["exists"]:
        print("  다운로드 폴더가 없어 비교 불가.")
    elif not srv["exists"]:
        print("  서버 maps 폴더가 없어 비교 불가.")
    else:
        down_exts = set(down["ext_counter"].keys())
        srv_exts = set(srv["ext_counter"].keys())
        
        # 서버 maps = 주로 .csv, .txt, .data
        # 클라이언트 map = .map, .s32, .seg, .ini
        if down_exts <= {".csv", ".txt", ".data"} or (".csv" in down_exts and ".txt" in down_exts):
            if srv_exts <= {".csv", ".txt", ".data"} or (".csv" in srv_exts and ".txt" in srv_exts):
                print("  호환: 예. 둘 다 서버용 맵 형식입니다.")
                print("  → 다운로드 폴더의 Maps.csv / Text/*.txt 를 서버 maps에 복사·병합하면 적용 가능.")
            else:
                print("  호환: 서버 maps는 서버용, 다운로드는 다른 형식일 수 있음.")
        elif ".map" in down_exts or ".s32" in down_exts:
            print("  호환: 아니오. 다운로드 폴더는 **클라이언트용** 맵(.map, .s32 등)입니다.")
            print("  서버 maps 폴더는 **서버용**(Maps.csv, Text/*.txt)이라 형식이 다릅니다.")
            print("  → 클라이언트 맵은 게임 클라이언트의 map 폴더에 넣고,")
            print("  → 서버 맵을 바꾸려면 서버용 Maps.csv + Text 폴더가 별도로 필요합니다.")
        else:
            print("  호환: 확장자 구성을 보면 서로 다른 용도일 수 있습니다.")
            print(f"  다운로드 확장자: {sorted(down_exts)}")
            print(f"  서버 확장자: {sorted(srv_exts)}")
    
    print("\n" + "=" * 70)

if __name__ == "__main__":
    main()
