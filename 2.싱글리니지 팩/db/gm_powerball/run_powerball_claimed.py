# -*- coding: utf-8 -*-
"""powerball_claimed.sql 실행: powerball_bets 테이블에 claimed 컬럼 추가 (한 번만 실행)"""
import os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
import pymysql

config = {'host': 'localhost', 'port': 3306, 'user': 'root', 'password': '1307',
          'database': 'l1jdb', 'charset': 'utf8mb4'}

try:
    conn = pymysql.connect(**config)
    cur = conn.cursor()
    cur.execute("""
        ALTER TABLE powerball_bets
        ADD COLUMN claimed TINYINT(1) NOT NULL DEFAULT 0
        COMMENT '0: 미수령, 1: NPC 매입 완료'
    """)
    conn.commit()
    print("powerball_bets.claimed 컬럼 추가 완료.")
except pymysql.err.OperationalError as e:
    if e.args[0] == 1060:  # Duplicate column name
        print("claimed 컬럼이 이미 존재합니다. 건너뜁니다.")
    else:
        raise
finally:
    if 'cur' in dir():
        cur.close()
    if 'conn' in dir():
        conn.close()
print("Done.")
