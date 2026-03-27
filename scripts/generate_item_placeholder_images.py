"""
DB의 item 테이블에서 인벤ID 목록을 조회한 뒤,
gm_tool/images/item 에 {인벤ID}.png 플레이스홀더 이미지를 생성합니다.
실제 아이콘으로 교체하려면 해당 경로의 PNG를 덮어쓰면 됩니다.
"""

import os
import sys

# gm_tool 루트를 path에 추가
GM_TOOL_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, GM_TOOL_ROOT)
os.chdir(GM_TOOL_ROOT)

import pymysql  # noqa: E402

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    print("Pillow 필요: pip install Pillow")
    sys.exit(1)

import config  # noqa: E402

OUTPUT_DIR = os.path.join(GM_TOOL_ROOT, "images", "item")
SIZE = 32  # 32x32 (게임 인벤 아이콘 크기)

# 게임 아이콘 느낌 색상 (리니지 스타일 슬롯)
BG_DARK = (45, 42, 35)       # 슬롯 배경
BORDER_LIGHT = (80, 75, 65)  # 테두리 밝은 쪽 (위·왼)
BORDER_DARK = (25, 22, 18)   # 테두리 어두운 쪽 (아래·오른)
TEXT_COLOR = (180, 165, 130) # 숫자 색 (은빛/금빛)
INNER_SHADOW = (30, 28, 22)  # 안쪽 그림자


def get_inv_ids_from_db():
    """item 테이블에서 고유 인벤ID 목록 반환."""
    conn = pymysql.connect(
        host=config.DB_CONFIG["host"],
        port=config.DB_CONFIG["port"],
        user=config.DB_CONFIG["user"],
        password=config.DB_CONFIG["password"],
        database=config.DB_CONFIG["database"],
        charset=config.DB_CONFIG["charset"],
    )
    inv_ids = set()
    try:
        with conn.cursor() as cur:
            # 컬럼명이 인벤ID 또는 다른 형태일 수 있음
            try:
                cur.execute("SELECT DISTINCT `인벤ID` FROM item WHERE `인벤ID` IS NOT NULL")
                for row in cur.fetchall():
                    if row[0] is not None:
                        inv_ids.add(int(row[0]))
            except Exception:
                try:
                    cur.execute("SELECT DISTINCT inv_id FROM item WHERE inv_id IS NOT NULL")
                    for row in cur.fetchall():
                        if row[0] is not None:
                            inv_ids.add(int(row[0]))
                except Exception:
                    pass
    finally:
        conn.close()
    return sorted(inv_ids)


def create_placeholder(inv_id: int, path: str, game_style: bool = True):
    """인벤ID용 32x32 아이콘 PNG 생성. game_style=True면 게임 슬롯처럼 보이게."""
    img = Image.new("RGB", (SIZE, SIZE), color=BG_DARK)
    draw = ImageDraw.Draw(img)

    if game_style:
        # 테두리: 위·왼쪽 밝게, 아래·오른쪽 어둡게 (슬롯 느낌)
        for i in range(2):
            draw.line([(i, 0), (SIZE - 1 - i, 0)], fill=BORDER_LIGHT, width=1)
            draw.line([(0, i), (0, SIZE - 1 - i)], fill=BORDER_LIGHT, width=1)
            draw.line([(SIZE - 1 - i, 2), (SIZE - 1 - i, SIZE - 1)], fill=BORDER_DARK, width=1)
            draw.line([(2, SIZE - 1 - i), (SIZE - 1, SIZE - 1 - i)], fill=BORDER_DARK, width=1)
        # 안쪽 살짝 어둡게 (들어간 느낌)
        draw.rectangle([2, 2, SIZE - 3, SIZE - 3], outline=INNER_SHADOW, fill=BG_DARK)

    text = str(inv_id)
    try:
        font = ImageFont.truetype("arial.ttf", 9)
    except Exception:
        try:
            font = ImageFont.truetype("C:\\Windows\\Fonts\\arial.ttf", 9)
        except Exception:
            font = ImageFont.load_default()
    bbox = draw.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = (SIZE - tw) // 2
    y = (SIZE - th) // 2
    draw.text((x, y), text, fill=TEXT_COLOR, font=font)
    img.save(path, "PNG")


def main():
    import argparse
    parser = argparse.ArgumentParser(description="GM툴용 아이템 아이콘 생성 (게임 슬롯 스타일)")
    parser.add_argument("--overwrite", action="store_true", help="기존 PNG도 새 스타일로 덮어쓰기")
    args = parser.parse_args()

    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print(f"대상 폴더: {OUTPUT_DIR}")

    inv_ids = get_inv_ids_from_db()
    if not inv_ids:
        print("DB에서 인벤ID를 가져오지 못했습니다. item 테이블에 '인벤ID' 컬럼이 있는지 확인하세요.")
        return

    print(f"인벤ID 개수: {len(inv_ids)} (예: {inv_ids[:5]} ...)")
    if args.overwrite:
        print("기존 파일을 게임 스타일로 덮어씁니다.")

    created = 0
    for inv_id in inv_ids:
        path = os.path.join(OUTPUT_DIR, f"{inv_id}.png")
        if os.path.isfile(path) and not args.overwrite:
            continue
        try:
            create_placeholder(inv_id, path, game_style=True)
            created += 1
        except Exception as e:
            print(f"  실패 {inv_id}: {e}")

    print(f"처리: {created}개.")


if __name__ == "__main__":
    main()
