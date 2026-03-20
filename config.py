"""
리니지 싱글 서버 GM 툴 - 데이터베이스 설정
"""

# 데이터베이스 연결 정보
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '1307',
    'database': 'l1jdb',
    'charset': 'utf8mb4'
}

# 서버 경로
SERVER_PATH = r"D:\Lineage_Single\2.싱글리니지 팩\서버스타트.bat"
CLIENT_PATH = r"D:\Lineage_Single\3.싱글리니지 클라이언트\접속.bat"
# 아이템 이미지 검색 경로 (여러 폴더 순서대로 찾음). GM툴 페이지에서 gm_tool/images/item 을 앞에 붙여 사용
ITEM_IMAGE_DIRS_EXTRA = [
    r"D:\Lineage_Single\2.싱글리니지 팩\images\item",
]

# Docker 컨테이너 정보
DOCKER_CONTAINER = "l1j-db"

# Streamlit 설정
PAGE_TITLE = "리니지 GM 툴"
PAGE_ICON = "🎮"
LAYOUT = "wide"

# 파워볼 일일 포상: 서버 순이익 대비 풀 비율 (0.0 ~ 1.0)
# - 네 직업(기사·법사·요정·다크엘프) 기본 합산 비율 → 군주에서 떼는 금액 가산 후, **참가로 체크한 직업 수**로 나눔 → 직업당 12:7:3
# - 군주: ROYAL_TOTAL_RATE 로 먼저 풀을 잡고, 그중 DIVERT 비율만큼을 떼어 네 직업에 균등 가산 → 남은 금만 군주 7:3:2
POWERBALL_POOL_FOUR_CLASSES_TOTAL_RATE = 0.22
POWERBALL_POOL_ROYAL_TOTAL_RATE = 0.05
# 군주 명목 풀(int(순이익×ROYAL)) 중 이 비율(0~1)만큼을 네 직업 쪽 총풀에 합산 후, 참가한 네 직업 수로 나눔
POWERBALL_ROYAL_DIVERT_TO_FOUR_RATE = 0.3

# 일일 포상에 참가할 직업 (False면 해당 직업 풀은 나머지 참가 직업에 균등 재분배). 자정 스크립트 기본값.
# 군주 False 시 군주 실제 풀 전액도 네 직업 쪽 총풀에 합산 후 재분배.
POWERBALL_REWARD_CLASS_DEFAULTS = {
    "knight": True,
    "wizard": True,
    "elf": True,
    "darkelf": False,
    "royal": True,
}

# 직업 코드 매핑 (서버와 동일: 0~6)
CLASS_NAMES = {
    0: "군주",
    1: "기사",
    2: "요정",
    3: "마법사",
    4: "다크엘프",
    5: "용기사",
    6: "환술사"
}

# 주요 마을 좌표 (추정값 - 실제 값은 확인 필요)
TOWN_COORDINATES = {
    "기란": {"x": 33936, "y": 32318, "map_id": 4},
    "아덴": {"x": 33430, "y": 32815, "map_id": 4},
    "하이네": {"x": 33605, "y": 33235, "map_id": 4},
    "글루딘": {"x": 32612, "y": 32734, "map_id": 4},
    "켄트": {"x": 33080, "y": 33390, "map_id": 4}
}
