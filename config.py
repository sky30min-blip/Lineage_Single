# -*- coding: utf-8 -*-
"""
리니지 싱글 서버 GM 툴 - 데이터베이스·경로·Streamlit 설정
- DB는 MariaDB(MySQL 호환 프로토콜). 연결은 pymysql 로 처리( MariaDB 공식 호환 ).
- 싱글 팩 mysql.conf(이름만 mysql, 내용은 MariaDB jdbc url) 의 url / id / pw 를 읽어 PHP API 와 동일.
- 환경변수 GM_DB_* 가 있으면 최우선.
"""
import os
import re
from pathlib import Path
from typing import Optional


def _find_pack_mysql_conf() -> Optional[Path]:
    """Lineage_Single/2.*.../mysql.conf"""
    root = Path(__file__).resolve().parent.parent
    explicit = root / "2.싱글리니지 팩" / "mysql.conf"
    if explicit.is_file():
        return explicit
    try:
        for p in root.iterdir():
            if p.is_dir() and not p.name.startswith(".") and p.name != "gm_tool":
                cand = p / "mysql.conf"
                if cand.is_file():
                    return cand
    except OSError:
        pass
    return None


def _parse_mysql_conf(path: Path) -> dict:
    """jdbc url + id + pw 파싱 (api/config.php 와 동일 규칙)."""
    out: dict = {}
    try:
        raw = path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return out
    if raw.startswith("\ufeff"):
        raw = raw[1:]
    url = ""
    user = ""
    password = ""
    for line in raw.splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        m = re.match(r"^\s*url\s*=\s*(.+)$", line, re.I)
        if m:
            url = m.group(1).strip()
            continue
        m = re.match(r"^\s*id\s*=\s*(.+)$", line, re.I)
        if m:
            user = m.group(1).strip()
            continue
        m = re.match(r"^\s*pw\s*=\s*(.+)$", line, re.I)
        if m:
            password = m.group(1).strip()
            continue
    # jdbc:mariadb://host:port/dbname?...
    if url:
        mm = re.search(r"(?:mysql|mariadb)://([^:/]+):(\d+)/([^?]+)", url, re.I)
        if mm:
            out["host"] = mm.group(1).strip()
            out["port"] = int(mm.group(2))
            out["database"] = mm.group(3).strip()
    if user:
        out["user"] = user
    if password:
        out["password"] = password
    return out


def _build_db_config() -> dict:
    # Docker MariaDB(l1j-db) 기본값. 윈도우 MariaDB 직접 쓰면 mysql.conf 에 포트·비번·DB 맞춤.
    base = {
        "host": "127.0.0.1",
        "port": 3308,
        "user": "root",
        "password": "1307",
        "database": "l1jdb",
        "charset": "utf8mb4",
    }
    mc = _find_pack_mysql_conf()
    if mc is not None:
        parsed = _parse_mysql_conf(mc)
        base.update(parsed)
    if os.environ.get("GM_DB_HOST"):
        base["host"] = os.environ["GM_DB_HOST"].strip()
    if os.environ.get("GM_DB_PORT"):
        base["port"] = int(os.environ["GM_DB_PORT"])
    if os.environ.get("GM_DB_USER"):
        base["user"] = os.environ["GM_DB_USER"].strip()
    if os.environ.get("GM_DB_PASSWORD") is not None:
        base["password"] = os.environ["GM_DB_PASSWORD"]
    if os.environ.get("GM_DB_NAME"):
        base["database"] = os.environ["GM_DB_NAME"].strip()
    if os.environ.get("GM_DB_CHARSET"):
        base["charset"] = os.environ["GM_DB_CHARSET"].strip()
    return base


DB_CONFIG = _build_db_config()

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

# 파워볼 배당(이론 당첨 추정). GM 포상 탭(체크박스)은 아래 풀 비율·분배식 사용.
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
