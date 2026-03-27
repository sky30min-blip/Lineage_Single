# -*- coding: utf-8 -*-
"""
리니지 싱글 서버 GM 툴 - 데이터베이스·경로·Streamlit 설정
DB는 환경변수(GM_DB_*)로 덮어쓸 수 있습니다. 기본은 lin200.
"""
import os

DB_CONFIG = {
    "host": os.environ.get("GM_DB_HOST", "127.0.0.1"),
    "port": int(os.environ.get("GM_DB_PORT", "3306")),
    "user": os.environ.get("GM_DB_USER", "root"),
    "password": os.environ.get("GM_DB_PASSWORD", "1307"),
    "database": os.environ.get("GM_DB_NAME", "lin200"),
    "charset": os.environ.get("GM_DB_CHARSET", "utf8mb4"),
}

# 서버 경로
SERVER_PATH = r"D:\Lineage_Single\2.싱글리니지 팩\서버스타트.bat"
CLIENT_PATH = r"D:\Lineage_Single\3.싱글리니지 클라이언트\접속.bat"
ITEM_IMAGE_DIRS_EXTRA = [
    r"D:\Lineage_Single\2.싱글리니지 팩\images\item",
]

DOCKER_CONTAINER = "l1j-db"

# Streamlit 설정
PAGE_TITLE = "리니지 GM 툴"
PAGE_ICON = "🎮"
LAYOUT = "wide"

# 파워볼 일일 포상: 서버 순이익 대비 풀 비율 (0.0 ~ 1.0)
POWERBALL_PAYOUT_RATE = 1.95
POWERBALL_POOL_FOUR_CLASSES_TOTAL_RATE = 0.22
POWERBALL_POOL_ROYAL_TOTAL_RATE = 0.05
POWERBALL_ROYAL_DIVERT_TO_FOUR_RATE = 0.3

POWERBALL_REWARD_CLASS_DEFAULTS = {
    "knight": True,
    "wizard": True,
    "elf": True,
    "darkelf": False,
    "royal": True,
}

# characters.class 컬럼 값 → 표시명 (lineage.share.Lineage LINEAGE_CLASS_*)
CLASS_NAMES = {
    0: "군주",
    1: "기사",
    2: "요정",
    3: "마법사",
    4: "다크엘프",
    5: "용기사",
    6: "환술사",
    10: "몬스터",
}

# 위치 이동(캐릭터관리) — 짧은 이름 + maps-data 기준 마을명 병기
TOWN_COORDINATES = {
    "기란": {"x": 33936, "y": 32318, "map_id": 4},
    "아덴": {"x": 33430, "y": 32815, "map_id": 4},
    "하이네": {"x": 33605, "y": 33235, "map_id": 4},
    "글루딘": {"x": 32612, "y": 32734, "map_id": 4},
    "켄트": {"x": 33080, "y": 33390, "map_id": 4},
    "말하는 섬": {"x": 32620, "y": 32908, "map_id": 0},
    "기란 마을": {"x": 33441, "y": 32767, "map_id": 4},
    "글루딘 마을": {"x": 32615, "y": 32768, "map_id": 4},
    "윈다우드 마을": {"x": 32617, "y": 33194, "map_id": 4},
    "켄트 마을": {"x": 33055, "y": 32768, "map_id": 4},
    "은기사 마을": {"x": 33093, "y": 33382, "map_id": 4},
    "화전민 마을": {"x": 32737, "y": 32448, "map_id": 4},
}
