"""
[4단계] temp_spr/*.spr 를 이미지로 변환해 gm_tool/images/item/*.png 로 저장합니다.
.spr 이 BMP면 그대로 변환, 아니면 헤더 건너뛰고 32x32 픽셀 추정 후 시도.
"""
import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
GM_TOOL_ROOT = os.path.dirname(SCRIPT_DIR)
TEMP_DIR = os.path.join(SCRIPT_DIR, "temp_spr")
OUT_DIR = os.path.join(GM_TOOL_ROOT, "images", "item")

try:
    from PIL import Image
except ImportError:
    print("Pillow 필요: pip install Pillow")
    sys.exit(1)


def try_bmp(data):
    if len(data) < 54 or data[:2] != b"BM":
        return None
    try:
        from io import BytesIO
        return Image.open(BytesIO(data)).convert("RGBA")
    except Exception:
        return None


def try_raw_rgb565(data, header_skip=28, w=32, h=32):
    need = header_skip + w * h * 2
    if len(data) < need:
        return None
    try:
        pixels = data[header_skip:need]
        img = Image.new("RGB", (w, h))
        for y in range(h):
            for x in range(w):
                i = (y * w + x) * 2
                if i + 2 > len(pixels):
                    break
                val = pixels[i] | (pixels[i + 1] << 8)
                r = (val >> 11) & 0x1F
                g = (val >> 5) & 0x3F
                b = val & 0x1F
                img.putpixel((x, y), (r << 3, g << 2, b << 3))
        return img
    except Exception:
        return None


def main():
    os.makedirs(OUT_DIR, exist_ok=True)
    converted = 0
    for name in os.listdir(TEMP_DIR):
        if not name.endswith(".spr"):
            continue
        inv_id = name[:-4]
        if not inv_id.isdigit():
            continue
        path = os.path.join(TEMP_DIR, name)
        with open(path, "rb") as f:
            data = f.read()
        img = try_bmp(data)
        if img is None:
            img = try_raw_rgb565(data)
        if img is not None:
            out_path = os.path.join(OUT_DIR, f"{inv_id}.png")
            img.save(out_path, "PNG")
            converted += 1
    print(f"변환: {converted}개 → {OUT_DIR}")


if __name__ == "__main__":
    main()
