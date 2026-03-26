"""
리니지 싱글 서버 GM 툴 - 데이터베이스 관리자
"""

import pymysql
from typing import List, Dict, Any, Optional
import config


class DBManager:
    """데이터베이스 연결 및 쿼리 실행 관리"""
    
    def __init__(self):
        self.connection = None
        self.config = config.DB_CONFIG

    def _ensure_connection(self) -> None:
        """장시간 띄운 Streamlit에서 끊긴 소켓 대비."""
        if not self.connection:
            self.connect()
            return
        try:
            self.connection.ping(reconnect=True)
        except Exception:
            self.connect()
    
    def connect(self) -> bool:
        """DB 연결 (기존 연결은 ping 후 재사용, 끊겼을 때만 재연결)"""
        try:
            if self.connection is not None:
                try:
                    self.connection.ping(reconnect=True)
                    return True
                except Exception:
                    try:
                        self.connection.close()
                    except Exception:
                        pass
                    self.connection = None

            self.connection = pymysql.connect(
                host=self.config['host'],
                port=self.config['port'],
                user=self.config['user'],
                password=self.config['password'],
                database=self.config['database'],
                charset=self.config.get('charset', 'utf8mb4'),
                cursorclass=pymysql.cursors.DictCursor,
                init_command="SET SESSION time_zone = '+09:00'",
            )
            return True
        except Exception as e:
            print(f"DB 연결 실패: {e}")
            self.connection = None
            return False
    
    def close(self):
        """DB 연결 종료"""
        if self.connection:
            try:
                self.connection.close()
            except Exception:
                pass
            self.connection = None
    
    def execute_query_ex(self, sql: str, params: tuple = None) -> tuple[bool, str]:
        """
        INSERT, UPDATE, DELETE 등 실행. UI 피드백용으로 (성공 여부, 오류 메시지) 반환.
        성공 시 두 번째 값은 빈 문자열.
        """
        try:
            self._ensure_connection()
            with self.connection.cursor() as cursor:
                cursor.execute(sql, params)
                self.connection.commit()
            return True, ""
        except Exception as e:
            if self.connection:
                self.connection.rollback()
            err = str(e)
            print(f"쿼리 실행 실패: {err}")
            print(f"SQL: {sql}")
            print(f"Params: {params}")
            return False, err

    def execute_query(self, sql: str, params: tuple = None) -> bool:
        """
        INSERT, UPDATE, DELETE 등 실행
        
        Args:
            sql: 실행할 SQL 쿼리
            params: 쿼리 파라미터 (튜플)
        
        Returns:
            성공 여부
        """
        ok, _ = self.execute_query_ex(sql, params)
        return ok
    
    def fetch_all(self, sql: str, params: tuple = None) -> List[Dict[str, Any]]:
        """
        SELECT 쿼리 실행 (여러 행)
        
        Args:
            sql: 실행할 SQL 쿼리
            params: 쿼리 파라미터 (튜플)
        
        Returns:
            결과 리스트 (딕셔너리 리스트)
        """
        try:
            self._ensure_connection()
            with self.connection.cursor() as cursor:
                cursor.execute(sql, params)
                return cursor.fetchall()
        except Exception as e:
            print(f"쿼리 실행 실패: {e}")
            print(f"SQL: {sql}")
            return []
    
    def fetch_one(self, sql: str, params: tuple = None) -> Optional[Dict[str, Any]]:
        """
        SELECT 쿼리 실행 (단일 행)
        
        Args:
            sql: 실행할 SQL 쿼리
            params: 쿼리 파라미터 (튜플)
        
        Returns:
            결과 딕셔너리 또는 None
        """
        try:
            self._ensure_connection()
            with self.connection.cursor() as cursor:
                cursor.execute(sql, params)
                return cursor.fetchone()
        except Exception as e:
            print(f"쿼리 실행 실패: {e}")
            print(f"SQL: {sql}")
            return None
    
    def test_connection(self) -> tuple[bool, str]:
        """
        DB 연결 테스트 (매번 새 pymysql.connect 하지 않음 — 기존 연결로 SELECT 1).
        """
        try:
            if not self.connection:
                if not self.connect():
                    return False, "연결 실패"

            with self.connection.cursor() as cursor:
                cursor.execute("SELECT 1")

            result = self.fetch_one("SELECT VERSION() as version")
            if result:
                version = result.get("version", "Unknown")
                return True, f"MariaDB {version}"
            return True, "연결됨"
        except Exception as e:
            try:
                if not self.connect():
                    return False, f"재연결 실패: {e}"
                with self.connection.cursor() as cursor:
                    cursor.execute("SELECT 1")
                result = self.fetch_one("SELECT VERSION() as version")
                if result:
                    version = result.get("version", "Unknown")
                    return True, f"MariaDB {version}"
                return True, "재연결 성공"
            except Exception as e2:
                return False, f"연결 오류: {e2}"
    
    def get_all_tables(self) -> List[str]:
        """전체 테이블 목록 조회"""
        sql = """
        SELECT TABLE_NAME 
        FROM information_schema.TABLES 
        WHERE TABLE_SCHEMA = %s
        ORDER BY TABLE_NAME
        """
        results = self.fetch_all(sql, (self.config['database'],))
        return [row['TABLE_NAME'] for row in results]
    
    def table_exists(self, table_name: str) -> bool:
        """테이블 존재 여부 확인"""
        tables = self.get_all_tables()
        return table_name in tables
    
    def get_table_structure(self, table_name: str) -> List[Dict[str, Any]]:
        """테이블 구조 조회"""
        sql = f"DESCRIBE {table_name}"
        return self.fetch_all(sql)
    
    def get_table_count(self, table_name: str) -> int:
        """테이블 레코드 개수"""
        sql = f"SELECT COUNT(*) as count FROM {table_name}"
        result = self.fetch_one(sql)
        return result['count'] if result else 0


# 전역 DB 매니저 인스턴스 (싱글톤)
_db_instance = None

def get_db() -> DBManager:
    """DB 매니저 인스턴스 가져오기 (싱글톤 패턴)"""
    global _db_instance
    if _db_instance is None:
        _db_instance = DBManager()
        _db_instance.connect()
    return _db_instance
