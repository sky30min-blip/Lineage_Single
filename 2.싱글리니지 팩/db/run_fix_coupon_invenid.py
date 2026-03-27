# -*- coding: utf-8 -*-
"""홀/짝 쿠폰 인벤ID를 151(레이스표)로 수정. GM 툴 config 또는 환경변수 사용."""
import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..', 'gm_tool'))
try:
    import config
    db_config = config.DB_CONFIG
except Exception:
    db_config = {
        'host': os.environ.get('DB_HOST', '127.0.0.1'),
        'port': int(os.environ.get('DB_PORT', 3306)),
        'user': os.environ.get('DB_USER', 'root'),
        'password': os.environ.get('DB_PASSWORD', ''),
        'database': os.environ.get('DB_NAME', 'l1jdb'),
        'charset': 'utf8mb4',
    }

def main():
    try:
        import pymysql
    except ImportError:
        print("pymysql이 없습니다. pip install pymysql 후 다시 실행하세요.")
        return 1
    try:
        conn = pymysql.connect(
            host=db_config['host'],
            port=db_config.get('port', 3306),
            user=db_config['user'],
            password=db_config.get('password', ''),
            database=db_config.get('database', 'l1jdb'),
            charset=db_config.get('charset', 'utf8mb4'),
        )
        with conn.cursor() as cur:
            # item 테이블 컬럼명 확인 (아이템이름 vs name)
            cur.execute("SHOW COLUMNS FROM item LIKE '%이름%'")
            r = cur.fetchone()
            name_col = r[0] if r else '아이템이름'
            cur.execute("SHOW COLUMNS FROM item LIKE '인벤%'")
            inv_col = cur.fetchone()
            inv_col = inv_col[0] if inv_col else '인벤ID'
            cur.execute("UPDATE item SET `" + inv_col + "` = 151 WHERE `" + name_col + "` IN ('홀 쿠폰', '짝 쿠폰')")
            n = cur.rowcount
        conn.commit()
        conn.close()
        print("OK. 홀/짝 쿠폰 인벤ID = 151(레이스표)로 수정됨. 반영 행 수:", n)
        return 0
    except Exception as e:
        print("오류:", e)
        return 1

if __name__ == '__main__':
    sys.exit(main())
