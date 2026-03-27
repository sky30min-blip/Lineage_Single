-- 서버 이름을 "호두서버"로 변경
-- 실행: MariaDB/MySQL에서 source 또는 클라이언트로 이 파일 실행

-- server 테이블의 name 컬럼이 PK이므로 UPDATE로 변경
UPDATE `server` SET `name` = '호두서버' WHERE `name` = '서버';

-- 현재 서버 이름이 '서버'가 아닌 경우 아래처럼 기존 이름으로 WHERE 조건을 바꾸세요.
-- UPDATE `server` SET `name` = '호두서버' WHERE `name` = '기존서버이름';
