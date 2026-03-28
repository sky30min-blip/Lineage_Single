# -*- coding: utf-8 -*-
"""
monster_spawnlist_boss 편집용 — 서버 MonsterBossSpawnlistDatabase / BossController 와 동일 테이블.
"""
from __future__ import annotations

import re
from typing import Any, Dict, List, Optional, Tuple

import config

# (표시명, DB name PK, monster 컬럼, 기본 spawn_x_y_map, spawn_time, spawn_day)
# spawn_x_y_map: 서버는 '&'로 다중 좌표 구간 — 단일 좌표는 "x, y, map &" 또는 "x, y, map" 모두 허용
BOSS_SPAWN_PRESETS: List[Dict[str, Any]] = [
    {
        "label": "데스나이트",
        "name": "데스나이트",
        "monster": "데스나이트",
        "spawn_x_y_map": "0, 0, 13",
        "spawn_time": "10:00, 22:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "바포메트",
        "name": "바포메트",
        "monster": "바포메트",
        "spawn_x_y_map": "32707, 32846, 2 &",
        "spawn_time": "08:30, 16:30",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "여왕개미",
        "name": "거대 여왕개미",
        "monster": "거대 여왕개미",
        "spawn_x_y_map": "32727, 32810, 51 &",
        "spawn_time": "23:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "베레스",
        "name": "베레스",
        "monster": "베레스",
        "spawn_x_y_map": "32770, 32767, 24 &",
        "spawn_time": "08:00, 16:00, 00:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "커츠",
        "name": "커츠",
        "monster": "커츠",
        "spawn_x_y_map": "32515, 32821, 0 &",
        "spawn_time": "08:00, 20:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "드레이크",
        "name": "드레이크",
        "monster": "드레이크",
        "spawn_x_y_map": "33346, 32351, 4 &",
        "spawn_time": "12:00, 00:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "이프리트",
        "name": "이프리트",
        "monster": "이프리트",
        "spawn_x_y_map": "33727, 32262, 4 &",
        "spawn_time": "12:00, 18:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "피닉스",
        "name": "피닉스",
        "monster": "피닉스",
        "spawn_x_y_map": "33726, 32250, 4 &",
        "spawn_time": "06:00, 18:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "네크로맨서",
        "name": "네크로맨서",
        "monster": "네크로맨서",
        "spawn_x_y_map": "32749, 32786, 12 &",
        "spawn_time": "00:30, 05:30, 10:30, 15:30, 20:30",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
    {
        "label": "카스파",
        "name": "카스파",
        "monster": "카스파",
        "spawn_x_y_map": "0, 0, 10",
        "spawn_time": "01:30, 10:00, 15:00, 20:00",
        "spawn_day": "월, 화, 수, 목, 금, 토, 일",
        "group_monster": "",
        "notify": 1,
    },
]


def list_boss_spawn_columns(db) -> List[str]:
    schema = config.DB_CONFIG.get("database") or "lin200"
    rows = db.fetch_all(
        """
        SELECT COLUMN_NAME AS cn FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = %s AND TABLE_NAME = 'monster_spawnlist_boss'
        ORDER BY ORDINAL_POSITION
        """,
        (schema,),
    )
    return [(r.get("cn") or r.get("COLUMN_NAME") or "").strip() for r in rows if r]


def get_notify_column_name(db) -> str:
    """information_schema에서 스폰 알림 컬럼명 확인 (한글/깨짐 대비)."""
    schema = config.DB_CONFIG.get("database") or "lin200"
    rows = db.fetch_all(
        """
        SELECT COLUMN_NAME AS cn FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = %s AND TABLE_NAME = 'monster_spawnlist_boss'
        ORDER BY ORDINAL_POSITION
        """,
        (schema,),
    )
    if not rows:
        return "스폰알림여부"
    known = {"name", "monster", "spawn_x_y_map", "spawn_time", "spawn_day", "group_monster"}
    for r in rows:
        cn = (r.get("cn") or r.get("COLUMN_NAME") or "").strip()
        if cn and cn not in known:
            return cn
    return "스폰알림여부"


def parse_first_xyz(spawn_raw: str) -> Tuple[int, int, int]:
    """첫 번째 좌표 구간 x,y,map (맵 던전 0,0,map 랜덤 스폰 지원)."""
    if not spawn_raw:
        return 0, 0, 0
    chunk = spawn_raw.split("&")[0].strip()
    parts = [p.strip() for p in chunk.split(",")]
    if len(parts) < 3:
        return 0, 0, 0
    try:
        return int(parts[0]), int(parts[1]), int(parts[2])
    except ValueError:
        return 0, 0, 0


def build_spawn_x_y_map(x: int, y: int, map_id: int) -> str:
    """단일 스폰 지점. x=y=0 이면 맵 내 랜덤 좌표(서버 BossController). 그 외는 ' &' 접미사 관례."""
    base = f"{x}, {y}, {map_id}"
    if x == 0 and y == 0:
        return base
    return f"{base} &"


_RE_TIME = re.compile(r"^\s*(\d{1,2}):(\d{1,2})\s*$")


def normalize_spawn_times(text: str) -> str:
    """쉼표 구분 H:MM — 공백 정리. 비우면 ''(서버에서 스폰 시각 없음 = 스폰 안 됨)."""
    if not text or not str(text).strip():
        return ""
    out: List[str] = []
    for part in str(text).split(","):
        p = part.strip()
        if not p:
            continue
        m = _RE_TIME.match(p)
        if m:
            h, mi = int(m.group(1)), int(m.group(2))
            out.append(f"{h}:{mi:02d}")
        else:
            out.append(p)
    return ", ".join(out) if out else ""


def normalize_spawn_days(text: str) -> str:
    """쉼표 구분 요일 (월, 화, … 또는 월요일). 비우면 ''(요일 없음 = 스폰 안 됨)."""
    if not text or not str(text).strip():
        return ""
    parts = [p.strip() for p in str(text).split(",") if p.strip()]
    return ", ".join(parts) if parts else ""


def fetch_boss_row(db, name: str) -> Optional[Dict[str, Any]]:
    return db.fetch_one("SELECT * FROM monster_spawnlist_boss WHERE name = %s", (name,))


def row_to_form_defaults(row: Dict[str, Any], preset: Dict[str, Any]) -> Dict[str, Any]:
    if not row:
        return {
            "monster": preset["monster"],
            "spawn_x_y_map": preset["spawn_x_y_map"],
            "spawn_time": preset["spawn_time"],
            "spawn_day": preset["spawn_day"],
            "group_monster": preset.get("group_monster") or "",
            "notify": bool(int(preset.get("notify", 1))),
        }
    notify_val = None
    for k, v in row.items():
        if k not in ("name", "monster", "spawn_x_y_map", "spawn_time", "spawn_day", "group_monster"):
            notify_val = v
            break
    try:
        n = int(notify_val) if notify_val is not None else 1
    except (TypeError, ValueError):
        n = 1
    return {
        "monster": str(row.get("monster") or preset["monster"]),
        "spawn_x_y_map": str(row.get("spawn_x_y_map") or preset["spawn_x_y_map"]),
        "spawn_time": str(row.get("spawn_time") or preset["spawn_time"]),
        "spawn_day": str(row.get("spawn_day") or preset["spawn_day"]),
        "group_monster": str(row.get("group_monster") or ""),
        "notify": n == 1,
    }
