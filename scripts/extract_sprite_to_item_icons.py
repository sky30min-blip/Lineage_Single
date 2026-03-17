"""
Sprite.idx + Sprite.pak 에서 아이템 아이콘(gfx id에 해당하는 .spr)을 읽어
gm_tool/images/item/{인벤ID}.png 로 저장합니다.

리니지 클라이언트 경로는 config 또는 환경변수 L1_CLIENT_PATH 로 지정.
"""
import os
import sys
import struct

GM_TOOL_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, GM_TOOL_ROOT)
os.chdir(GM_TOOL_ROOT)

try:
    import config
    CLIENT_ROOT = os.path.dirname(config.CLIENT_PATH) if getattr(config, "CLIENT_PATH", None) else None
except Exception:
    CLIENT_ROOT = None

CLIENT_ROOT = os.environ.get("L1_CLIENT_PATH", CLIENT_ROOT or r"D:\Lineage_Single\3.싱글리니지 클라이언트")
IDX_PATH = os.path.join(CLIENT_ROOT, "Sprite.idx")
PAK_PATH = os.path.join(CLIENT_ROOT, "Sprite.pak")
OUTPUT_DIR = os.path.join(GM_TOOL_ROOT, "images", "item")

ENTRY_SIZE = 32  # size(4) + offset(4) + name(24)


def parse_idx():
    """Sprite.idx 파싱. yield (size, offset, name_clean)."""
    with open(IDX_PATH, "rb") as f:
        data = f.read()
    n = len(data) // ENTRY_SIZE
    for i in range(n):
        off = i * ENTRY_SIZE
        size = struct.unpack_from("<I", data, off)[0]
        offset = struct.unpack_from("<I", data, off + 4)[0]
        name_raw = data[off + 8 : off + 32]
        name = name_raw.split(b"\x00")[0].decode("ascii", errors="ignore").strip()
        if not name or not name.endswith(".spr"):
            continue
        yield size, offset, name


def read_spr_from_pak(offset, size):
    """Sprite.pak 에서 지정 오프셋/크기만큼 읽기."""
    with open(PAK_PATH, "rb") as f:
        f.seek(offset)
        return f.read(size)


def spr_to_image(raw):
    """.spr raw bytes 를 PIL Image 로 변환 시도. 실패 시 None."""
    try:
        from PIL import Image
        import io
    except ImportError:
        return None
    if len(raw) < 2:
        return None
    # BMP 매직
    if raw[:2] == b"BM":
        try:
            return Image.open(io.BytesIO(raw)).copy()
        except Exception:
            pass
    # 그 외: 리니지 spr 헤더 가정 (헤더 뒤에 비트맵이 있을 수 있음)
    if len(raw) < 0x436 + 32 * 32:
        return None
    # 간단 시도: 오프셋 0x436 부터 32x32 8bpp (1024 bytes) 등
    for skip in (0x36, 0x436, 0x76, 54, 0):
        for w, h in ((32, 32), (64, 64), (32, 64)):
            size = w * h
            if skip + size > len(raw):
                continue
            try:
                pix = bytearray(raw[skip : skip + size])
                img = Image.new("P", (w, h))
                img.putdata(pix)
                img = img.convert("RGB")
                return img
            except Exception:
                pass
    return None


def main():
    if not os.path.isfile(IDX_PATH) or not os.path.isfile(PAK_PATH):
        print(f"Sprite.idx 또는 Sprite.pak 없음: {CLIENT_ROOT}")
        return

    # 인벤ID 목록 (DB 또는 1~2000 범위)
    try:
        import pymysql
        import config
        conn = pymysql.connect(**config.DB_CONFIG)
        cur = conn.cursor()
        cur.execute("SELECT DISTINCT `인벤ID` FROM item WHERE `인벤ID` IS NOT NULL")
        inv_ids = set(int(r[0]) for r in cur if r[0] is not None)
        conn.close()
    except Exception:
        inv_ids = set(range(1, 2500))

    # idx 에서 이름 -> (offset, size) 매핑. "143-0.spr" 또는 "143.spr" 형태
    name_to_pos = {}
    for size, offset, name in parse_idx():
        base = name.replace(".spr", "").strip()
        # "143-0" -> 143, "0-0" -> 0
        if "-" in base:
            part = base.split("-")[0]
            if part.isdigit():
                gfx = int(part)
                if gfx not in name_to_pos or "0.spr" in name or name == f"{gfx}-0.spr":
                    name_to_pos[gfx] = (offset, size)
        elif base.isdigit():
            name_to_pos[int(base)] = (offset, size)

    os.makedirs(OUTPUT_DIR, exist_ok=True)
    saved = 0
    for gfx in sorted(inv_ids):
        if gfx not in name_to_pos:
            continue
        offset, size = name_to_pos[gfx]
        try:
            raw = read_spr_from_pak(offset, size)
            img = spr_to_image(raw)
            if img is not None:
                out = os.path.join(OUTPUT_DIR, f"{gfx}.png")
                img.save(out, "PNG")
                saved += 1
        except Exception as e:
            pass
    print(f"저장: {saved}개 → {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
