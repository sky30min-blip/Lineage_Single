"""
[3단계] sprite_item_list.json 기준으로 Sprite.pak 에서 .spr 바이너리를 배치 단위로 추출합니다.
사용: python sprite_extract_3_extract_batch.py [--batch N] [--size 50]
기본 N=0, size=50 → 첫 50개만 추출. N=1 이면 다음 50개.
"""
import os
import sys
import json
import argparse

CLIENT_DIR = r"D:\Lineage_Single\3.싱글리니지 클라이언트"
PAK_PATH = os.path.join(CLIENT_DIR, "Sprite.pak")
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
LIST_JSON = os.path.join(SCRIPT_DIR, "sprite_item_list.json")
TEMP_DIR = os.path.join(SCRIPT_DIR, "temp_spr")
BATCH_SIZE = 50


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--batch", type=int, default=0, help="배치 번호 (0, 1, 2, ...)")
    ap.add_argument("--size", type=int, default=BATCH_SIZE, help="한 배치당 개수")
    args = ap.parse_args()

    with open(LIST_JSON, "r", encoding="utf-8") as f:
        items = json.load(f)
    start = args.batch * args.size
    end = min(start + args.size, len(items))
    batch = items[start:end]
    if not batch:
        print("해당 배치에 항목 없음")
        return

    os.makedirs(TEMP_DIR, exist_ok=True)
    pak_size = os.path.getsize(PAK_PATH)
    extracted = 0
    with open(PAK_PATH, "rb") as pak:
        for it in batch:
            inv_id = it["inv_id"]
            offset = int(it["offset"])
            size = int(it["size"])
            if offset + size > pak_size or size <= 0 or size > 10 * 1024 * 1024:
                continue
            pak.seek(offset)
            data = pak.read(size)
            out_path = os.path.join(TEMP_DIR, f"{inv_id}.spr")
            with open(out_path, "wb") as f:
                f.write(data)
            extracted += 1
    print(f"추출: {extracted}개 (배치 {args.batch}, {start}~{end}) → {TEMP_DIR}")


if __name__ == "__main__":
    main()
