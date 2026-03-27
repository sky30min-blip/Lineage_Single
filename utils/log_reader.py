"""
서버 매니저창 로그 파일 읽기.
원본 툴은 log/매니저창/접속 로그 등에 YYYYMMDD.log 로 저장.
"""
import os
import re
from typing import List, Optional

# 서버 로그 기준 경로: Lineage_Single/2.싱글리니지 팩/log
def get_server_log_base():
    base = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    return os.path.join(base, "2.싱글리니지 팩", "log")

def get_manager_log_dir(subdir: str) -> str:
    """매니저창 하위 폴더 경로. subdir 예: '접속 로그', '창고 로그'."""
    return os.path.join(get_server_log_base(), "매니저창", subdir)


def get_chatting_log_dir() -> str:
    """서버 채팅 로그 폴더 (log/chatting/). 원본 툴과 동일 소스."""
    return os.path.join(get_server_log_base(), "chatting")


def list_chatting_log_dates() -> List[str]:
    """log/chatting/ 아래 .log 파일 날짜 목록, 최신순."""
    folder = get_chatting_log_dir()
    if not os.path.isdir(folder):
        return []
    out = [f[:-4] for f in os.listdir(folder) if f.endswith(".log")]
    out.sort(reverse=True)
    return out


def read_chatting_log_lines(date_str: Optional[str] = None, max_lines: int = 500) -> List[str]:
    """서버가 저장한 채팅 로그 한 줄씩. 형식: [시간]\\tIP\\t계정\\t캐릭명\\t채널번호\\t메시지"""
    folder = get_chatting_log_dir()
    if not os.path.isdir(folder):
        return []
    if date_str:
        path = os.path.join(folder, f"{date_str}.log")
    else:
        dates = list_chatting_log_dates()
        if not dates:
            return []
        path = os.path.join(folder, f"{dates[0]}.log")
    if not os.path.isfile(path):
        return []
    return _decode_log_file(path, max_lines)

def list_log_dates(subdir: str) -> List[str]:
    """해당 로그 폴더의 .log 파일명(날짜) 목록, 최신순."""
    folder = get_manager_log_dir(subdir)
    if not os.path.isdir(folder):
        return []
    out = []
    for f in os.listdir(folder):
        if f.endswith(".log"):
            out.append(f[:-4])  # YYYYMMDD
    out.sort(reverse=True)
    return out

def _decode_raw_bytes(raw: bytes) -> str:
    """
    윈도우 서버 로그는 보통 CP949(MS949), 최신은 UTF-8 일 수 있음.
    errors='replace' 로 먼저 읽으면 잘못된 인코딩이어도 예외가 안 나서 한글이 ĳ 같은 깨짐으로만 보임 → strict 순서로 판별.
    """
    if not raw:
        return ""
    if raw.startswith(b"\xef\xbb\xbf"):
        try:
            return raw.decode("utf-8-sig")
        except UnicodeDecodeError:
            pass
    for enc in ("utf-8", "cp949", "euc-kr"):
        try:
            return raw.decode(enc)
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="replace")


# 하루 로그가 비정상적으로 클 때 메모리 보호 (필요 시 앞부분만 디코딩)
_MAX_LOG_BYTES = 32 * 1024 * 1024


def _decode_log_file(path: str, max_lines: int) -> List[str]:
    """여러 인코딩으로 시도해 로그 파일 읽기. (CP949/UTF-8 자동 판별)"""
    try:
        with open(path, "rb") as f:
            raw = f.read(_MAX_LOG_BYTES)
    except OSError:
        return []
    text = _decode_raw_bytes(raw)
    lines: List[str] = []
    for line in text.splitlines():
        line = line.rstrip("\r")
        if line:
            line = _fix_garbled_time(line)
            lines.append(line)
        if len(lines) >= max_lines:
            break
    return lines


def _fix_garbled_time(line: str) -> str:
    """시간 부분의 깨진 문자(��)를 콜론(:)으로 복구. e.g. 00��26��57�� -> 00:26:57"""
    # 숫자 뒤의 깨진 문자를 : 로 (다음 문자가 숫자일 때만)
    line = re.sub(r"(\d)[\ufffd\u3000\s��]+(?=\d)", r"\1:", line)
    # 끝난 시간 뒤의 깨진 문자 제거 (e.g. 57�� -> 57)
    line = re.sub(r"(\d)[\ufffd\u3000\s��]+", r"\1", line)
    return line


def clear_log_file(subdir: str, date_str: Optional[str] = None) -> tuple:
    """
    해당 로그 폴더의 파일 내용을 비웁니다. (파일은 유지, 서버가 계속 쓸 수 있음)
    date_str=None이면 최신 날짜 파일을 비움.
    반환: (성공 여부, 메시지)
    """
    folder = get_manager_log_dir(subdir)
    if not os.path.isdir(folder):
        return False, "로그 폴더가 없습니다."
    if date_str is None:
        dates = list_log_dates(subdir)
        if not dates:
            return False, "비울 로그 파일이 없습니다."
        date_str = dates[0]
    path = os.path.join(folder, f"{date_str}.log")
    if not os.path.isfile(path):
        return False, f"해당 날짜 파일이 없습니다: {date_str}.log"
    try:
        with open(path, "w", encoding="utf-8"):
            pass
        return True, f"{date_str}.log 내용을 비웠습니다."
    except Exception as e:
        return False, str(e)


def read_log_lines(subdir: str, date_str: Optional[str] = None, max_lines: int = 5000) -> List[str]:
    """
    로그 폴더에서 한 줄씩 읽어 리스트로 반환.
    date_str=None이면 가장 최신 날짜 파일 사용.
    CP949/UTF-8 등 여러 인코딩 시도, 시간 깨짐(��) 자동 복구.
    """
    folder = get_manager_log_dir(subdir)
    if not os.path.isdir(folder):
        return []
    if date_str:
        path = os.path.join(folder, f"{date_str}.log")
    else:
        dates = list_log_dates(subdir)
        if not dates:
            return []
        path = os.path.join(folder, f"{dates[0]}.log")
    if not os.path.isfile(path):
        return []
    return _decode_log_file(path, max_lines)


def parse_chatting_line(line: str) -> Optional[dict]:
    """서버 채팅 로그 한 줄 파싱. [시간]\\tIP\\t계정\\t캐릭명\\t채널번호\\t메시지 -> dict. (메시지에 탭 있으면 split 최대 6개)"""
    parts = line.split("\t", 5)
    if len(parts) < 6:
        return None
    time_part = parts[0].strip()
    if time_part.startswith("[") and time_part.endswith("]"):
        time_part = time_part[1:-1].strip()
    return {
        "created_at": time_part,
        "channel": int(parts[4]) if parts[4].strip().isdigit() else 0,
        "char_name": (parts[3] or "").strip() or "******",
        "target_name": "",
        "msg": (parts[5] or "").strip(),
    }


def parse_connect_line(line: str) -> dict:
    """접속 로그: [시간]\t [IP: x]\t [계정: x]\t [캐릭명: x]\t [접속/종료/...]"""
    d = {"raw": line, "시간": "", "IP": "", "계정": "", "캐릭명": "", "구분": ""}
    # [xxx] 블록 추출
    for m in re.finditer(r"\[([^\]]*)\]", line):
        inner = m.group(1).strip()
        if inner in ("접속", "종료", "생성", "삭제", "클라종료"):
            d["구분"] = inner
        elif inner.startswith("IP:"):
            d["IP"] = inner[3:].strip()
        elif inner.startswith("계정:"):
            d["계정"] = inner.replace("계정:", "").strip()
        elif inner.startswith("캐릭명:"):
            d["캐릭명"] = inner.replace("캐릭명:", "").strip()
        elif not d["시간"] and len(inner) >= 10 and ("-" in inner or ":" in inner):
            d["시간"] = inner
    return d
