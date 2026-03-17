■ 아이템 이미지 (인벤ID 아이콘) 안내

1) GM 툴(웹) 6번 아이템 관리
   - gm_tool/images/item/ 에 있는 {인벤ID}.png 가 상세 정보에 표시됩니다.
   - 현재는 숫자만 그린 플레이스홀더 이미지입니다.
   - 실제 게임 아이콘으로 바꾸려면 이 폴더의 PNG를 같은 파일명으로 덮어쓰면 됩니다.

2) 게임 클라이언트(인게임)에서 아이콘이 안 나올 때
   - 클라이언트는 보통 Sprite.pak 에서 아이콘을 읽습니다.
   - 954개 플레이스홀더 PNG를 "3.싱글리니지 클라이언트\icon\" 폴더에 복사해 두었습니다.
   - 게임 실행 후에도 숫자만 보이면:
     * 클라이언트가 icon 폴더를 보지 않고 Sprite.pak 만 쓰는 경우일 수 있습니다.
     * 이 경우 Sprite.pak 용 아이콘 팩을 구하거나, 클라이언트가 icon 폴더를 읽도록 하는 방법을 찾아야 합니다.
   - 일부 클라이언트는 .bmp 만 지원합니다. 그럴 경우 icon 폴더의 PNG를 BMP로 변환해 두면 됩니다.

3) 플레이스홀더 다시 만들기
   cd D:\Lineage_Single\gm_tool
   python scripts/generate_item_placeholder_images.py
