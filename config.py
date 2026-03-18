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
