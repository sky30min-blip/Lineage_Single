# -*- coding: utf-8 -*-
"""
동행복권 파워볼 결과 수집기 (24시간 대응)
- 06:00 ~ 24:00: 크롤링 수행 후 DB powerball_results 에 INSERT
- 00:00 ~ 06:00: 크롤링 중단, sleep (사이트 차단 방지)
"""
import time
import logging
from datetime import datetime

try:
    import pymysql
    import requests
    from bs4 import BeautifulSoup
except ImportError as e:
    print("pip install pymysql requests beautifulsoup4 필요:", e)
    raise

# DB 설정 (환경에 맞게 수정)
DB_CONFIG = {
    "host": "127.0.0.1",
    "port": 3306,
    "user": "root",
    "password": "",
    "database": "lin200",
    "charset": "utf8mb4",
}

# 한국 시간 기준 00:00~05:59 이면 크롤링 중단
def is_sleep_hour():
    now = datetime.utcnow()
    # KST = UTC+9
    kst_hour = (now.hour + 9) % 24
    return 0 <= kst_hour < 6


def wait_until_6am():
    """다음 오전 6시(KST)까지 대기"""
    now = datetime.utcnow()
    kst_hour = (now.hour + 9) % 24
    kst_min = now.minute
    # 06:00 KST 까지 남은 분
    if kst_hour < 6:
        minutes_left = (6 - kst_hour) * 60 - kst_min
    else:
        minutes_left = (24 - kst_hour + 6) * 60 - kst_min
    sec = max(60, minutes_left * 60)
    logging.info("00:00~06:00 구간: %d분 후 06:00까지 대기 (차단 방지)", sec // 60)
    time.sleep(min(sec, 6 * 3600))  # 최대 6시간


def fetch_and_save():
    """동행복권 사이트에서 파워볼 결과 조회 후 DB INSERT (실제 URL/셀렉터는 사이트에 맞게 수정 필요)"""
    # 예시: 실제 사이트 구조에 맞게 수정
    url = "https://dhlottery.co.kr/gameResult.do?method=byWin"  # 예시
    try:
        r = requests.get(url, timeout=10, headers={"User-Agent": "Mozilla/5.0"})
        r.raise_for_status()
        soup = BeautifulSoup(r.text, "html.parser")
        # TODO: 실제 파워볼 결과 테이블/셀렉터로 round_id, total_sum, result_type 추출
        # round_id = ...
        # total_sum = 5개 숫자 합
        # result_type = 0 (짝) or 1 (홀)
        # conn = pymysql.connect(**DB_CONFIG)
        # with conn.cursor() as cur:
        #     cur.execute("INSERT IGNORE INTO powerball_results (round_id, total_sum, result_type) VALUES (%s,%s,%s)", (round_id, total_sum, result_type))
        #     conn.commit()
        # conn.close()
        logging.debug("크롤링 완료 (셀렉터/INSERT 로직은 사이트에 맞게 구현)")
    except Exception as e:
        logging.warning("크롤링/DB 오류: %s", e)


def main():
    logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
    logging.info("파워볼 수집기 시작 (00:00~06:00 크롤링 중단)")
    while True:
        try:
            if is_sleep_hour():
                wait_until_6am()
                continue
            fetch_and_save()
        except KeyboardInterrupt:
            logging.info("종료")
            break
        except Exception as e:
            logging.exception("루프 오류: %s", e)
        time.sleep(60 * 5)  # 5분 간격 (조정 가능)


if __name__ == "__main__":
    main()
