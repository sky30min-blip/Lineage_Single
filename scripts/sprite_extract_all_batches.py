"""
3단계를 모든 배치에 대해 순서대로 실행한 뒤 4단계를 한 번 실행합니다.
실행 시간이 길어질 수 있으니 터미널에서 직접 실행하세요.
"""
import os
import sys
import json
import subprocess

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
LIST_JSON = os.path.join(SCRIPT_DIR, "sprite_item_list.json")
BATCH_SIZE = 50


def main():
    if not os.path.isfile(LIST_JSON):
        print("먼저 2단계 실행: python sprite_extract_2_list_item_spr.py")
        return
    with open(LIST_JSON, "r", encoding="utf-8") as f:
        items = json.load(f)
    n = len(items)
    num_batches = (n + BATCH_SIZE - 1) // BATCH_SIZE
    for b in range(num_batches):
        print(f"--- 배치 {b}/{num_batches} ---")
        subprocess.run(
            [sys.executable, os.path.join(SCRIPT_DIR, "sprite_extract_3_extract_batch.py"),
             "--batch", str(b), "--size", str(BATCH_SIZE)],
            cwd=os.path.dirname(SCRIPT_DIR),
            check=True,
        )
    print("--- 4단계 변환 ---")
    subprocess.run(
        [sys.executable, os.path.join(SCRIPT_DIR, "sprite_extract_4_spr_to_png.py")],
        cwd=os.path.dirname(SCRIPT_DIR),
        check=True,
    )
    print("완료.")


if __name__ == "__main__":
    main()
