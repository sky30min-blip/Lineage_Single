■ GM 툴 실제 게임 아이콘 넣기 (나눠서 실행)

1단계: Sprite.idx 파싱 → sprite_index.json 생성
  python sprite_extract_1_parse_idx.py

2단계: DB 인벤ID와 매칭 목록 생성 → sprite_item_list.json
  python sprite_extract_2_list_item_spr.py

3단계: Sprite.pak에서 .spr 추출 (배치 단위, 한 번에 50개씩)
  python sprite_extract_3_extract_batch.py --batch 0 --size 50
  python sprite_extract_3_extract_batch.py --batch 1 --size 50
  ... (배치 번호 올려가며 반복)

4단계: temp_spr/*.spr → 이미지 변환 → gm_tool/images/item/*.png
  python sprite_extract_4_spr_to_png.py

한 번에 전부 돌리기:
  python sprite_extract_all_batches.py
  (3단계 모든 배치 + 4단계를 순서대로 실행)

※ 4단계에서 변환 안 되는 .spr은 리니지 전용 포맷이라 LineageSpr 등으로 BMP 추출 후
  copy_item_icons.py 로 images/item 에 넣으면 됩니다.
