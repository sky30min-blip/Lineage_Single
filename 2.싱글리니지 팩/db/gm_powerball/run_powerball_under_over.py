# -*- coding: utf-8 -*-
"""powerball_results에 under_over_type 추가 및 기존 행 백필 (한 번만 실행)"""
import os
os.chdir(os.path.dirname(os.path.abspath(__file__)))
import pymysql

config = {'host': 'localhost', 'port': 3306, 'user': 'root', 'password': '1307',
          'database': 'l1jdb', 'charset': 'utf8mb4'}

conn = None
cur = None
try:
    conn = pymysql.connect(**config)
    cur = conn.cursor()
    try:
        cur.execute("""
            ALTER TABLE powerball_results
              ADD COLUMN under_over_type TINYINT UNSIGNED NOT NULL DEFAULT 0
              COMMENT '0:언더(총합<=72) 1:오버(총합>72)'
              AFTER result_type
        """)
        conn.commit()
        print("under_over_type 컬럼 추가됨.")
    except pymysql.err.OperationalError as e:
        if e.args[0] == 1060:
            print("under_over_type 컬럼이 이미 있습니다.")
        else:
            raise
    cur.execute(
        "UPDATE powerball_results SET under_over_type = IF(total_sum <= 72, 0, 1)")
    conn.commit()
    print("기존 행 under_over_type 백필 완료.")
finally:
    if cur is not None:
        cur.close()
    if conn is not None:
        conn.close()
print("Done.")
