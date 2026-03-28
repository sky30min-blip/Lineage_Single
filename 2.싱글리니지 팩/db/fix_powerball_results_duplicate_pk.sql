-- 파워볼 powerball_results INSERT 시 Duplicate entry '...' for key 'PRIMARY' (AUTO_INCREMENT 꼬임) 수정
-- MariaDB/MySQL에서 실행: mysql -u root -p lin200 < fix_powerball_results_duplicate_pk.sql

USE lin200;

-- id 컬럼이 있는 경우에만 (SHOW COLUMNS FROM powerball_results LIKE 'id' 로 확인)
SET @next_ai = (SELECT COALESCE(MAX(id), 0) + 1 FROM powerball_results);
SET @sql = CONCAT('ALTER TABLE powerball_results AUTO_INCREMENT = ', @next_ai);
PREPARE p FROM @sql;
EXECUTE p;
DEALLOCATE PREPARE p;

-- round_id만 PK이고 id가 없는 스키마면 위 구문이 실패합니다. 그때는 회차 중복이 원인이므로
-- powerball_results 테이블에서 중복 round_id 행을 정리하세요.
