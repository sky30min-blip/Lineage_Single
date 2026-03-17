"""
추출해 둔 아이템 아이콘 이미지를 GM 툴에서 쓰는 폴더로 복사합니다.

사용법:
  python copy_item_icons.py "C:\경로\추출한아이콘폴더"
  python copy_item_icons.py "D:\Lineage_Single\3.싱글리니지 클라이언트\extracted_icons"

- 인벤ID로 쓸 수 있도록, 파일명이 숫자만 있는 파일(예: 143.png, 1.bmp)만 복사합니다.
- gm_tool/images/item/ 에 {숫자}.png 로 저장합니다. (BMP는 PNG로 변환 시도)
"""

import os
import sys
import shutil

GM_TOOL_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUTPUT_DIR = os.path.join(GM_TOOL_ROOT, "images", "item")
EXTENSIONS = (".png", ".bmp", ".gif", ".jpg", ".jpeg")


def main():
    if len(sys.argv) < 2:
        print("사용법: python copy_item_icons.py <추출한_아이콘_폴더_경로>")
        print("예: python copy_item_icons.py \"C:\\icons\"")
        sys.exit(1)

    src_dir = os.path.abspath(sys.argv[1].strip().strip('"'))
    if not os.path.isdir(src_dir):
        print(f"폴더가 없습니다: {src_dir}")
        sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print(f"대상 폴더: {OUTPUT_DIR}")
    print(f"소스 폴더: {src_dir}")

    try:
        from PIL import Image
        has_pil = True
    except ImportError:
        has_pil = False

    copied = 0
    for name in os.listdir(src_dir):
        base, ext = os.path.splitext(name)
        if ext.lower() not in EXTENSIONS:
            continue
        if not base.isdigit():
            continue
        src_path = os.path.join(src_dir, name)
        if not os.path.isfile(src_path):
            continue
        dst_path = os.path.join(OUTPUT_DIR, f"{base}.png")
        try:
            if has_pil and ext.lower() != ".png":
                img = Image.open(src_path)
                img.save(dst_path, "PNG")
            else:
                shutil.copy2(src_path, dst_path)
            copied += 1
        except Exception as e:
            print(f"  실패 {name}: {e}")

    print(f"복사 완료: {copied}개 → gm_tool/images/item/")


if __name__ == "__main__":
    main()
