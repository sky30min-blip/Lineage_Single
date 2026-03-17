"""
[1단계] Sprite.idx 만 파싱해서 엔트리 목록(이름, 오프셋, 크기)을 JSON으로 저장합니다.
Sprite.pak 은 건드리지 않습니다. 실행이 가볍습니다.
"""
import os
import json

CLIENT_DIR = r"D:\Lineage_Single\3.싱글리니지 클라이언트"
IDX_PATH = os.path.join(CLIENT_DIR, "Sprite.idx")
OUT_JSON = os.path.join(os.path.dirname(__file__), "sprite_index.json")
ENTRY_SIZE = 32  # size(4) + offset(4) + name(24)


def main():
    if not os.path.isfile(IDX_PATH):
        print(f"파일 없음: {IDX_PATH}")
        return
    with open(IDX_PATH, "rb") as f:
        data = f.read()
    n = len(data) // ENTRY_SIZE
    entries = []
    for i in range(n):
        off = i * ENTRY_SIZE
        size = int.from_bytes(data[off : off + 4], "little")
        offset = int.from_bytes(data[off + 4 : off + 8], "little")
        name = data[off + 8 : off + 32].split(b"\x00")[0].decode("ascii", errors="ignore").strip()
        if name:
            entries.append({"name": name, "offset": offset, "size": size})
    with open(OUT_JSON, "w", encoding="utf-8") as f:
        json.dump(entries, f, ensure_ascii=False, indent=0)
    print(f"저장: {OUT_JSON} (엔트리 {len(entries)}개)")


if __name__ == "__main__":
    main()
