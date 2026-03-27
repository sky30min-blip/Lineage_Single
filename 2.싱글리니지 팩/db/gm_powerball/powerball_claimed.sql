-- 파워볼 쿠폰 매입 중복 수령 방지용 컬럼 (이미 있으면 에러 무시)
USE lin200;
ALTER TABLE powerball_bets ADD COLUMN claimed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0: 미수령, 1: NPC 매입 완료';
