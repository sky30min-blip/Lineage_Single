-- 파워볼: 회차 결과에 언더/오버(총합 ≤72 → 언더) 컬럼 추가
-- 실행: mysql -u계정 -p lin200 < powerball_under_over_migration.sql
-- (신규 설치: powerball_tables.sql에 이미 under_over_type 포함 시 이 파일은 생략)

USE lin200;

ALTER TABLE powerball_results
  ADD COLUMN under_over_type TINYINT UNSIGNED NOT NULL DEFAULT 0
  COMMENT '0:언더(총합<=72) 1:오버(총합>72)'
  AFTER result_type;

UPDATE powerball_results SET under_over_type = IF(total_sum <= 72, 0, 1);
