# -*- coding: utf-8 -*-
"""gm_npc_despawned 테이블 생성 (lin200). GM 툴 config 또는 환경변수 사용."""
import sys
import os

# gm_tool의 config 사용 가능하도록 경로 추가
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
        'database': os.environ.get('DB_NAME', 'lin200'),
        'charset': 'utf8mb4',
    }

def main():
    try:
        import pymysql
    except ImportError:
        print("pymysql이 없습니다. pip install pymysql 후 다시 실행하세요.")
        return 1
    sql = """
    CREATE TABLE IF NOT EXISTS `gm_npc_despawned` (
      `spawn_name` VARCHAR(64) NOT NULL COMMENT 'npc_spawnlist.name',
      `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
      PRIMARY KEY (`spawn_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    COMMENT='GM 툴에서 월드에서 제거한 NPC 스폰 목록(복구 드롭다운용)'
    """
    try:
        conn = pymysql.connect(
            host=db_config['host'],
            port=db_config.get('port', 3306),
            user=db_config['user'],
            password=db_config.get('password', ''),
            database=db_config.get('database', 'lin200'),
            charset=db_config.get('charset', 'utf8mb4'),
        )
        with conn.cursor() as cur:
            cur.execute(sql)
        conn.commit()
        conn.close()
        print("gm_npc_despawned 테이블 생성 완료 (lin200).")
        return 0
    except Exception as e:
        print("오류:", e)
        return 1

if __name__ == '__main__':
    sys.exit(main())
