-- 파워볼 진행자 멘트용 당일(KST) 홀/짝·언더/오버 누적 — 서버 재시작 후에도 이어짐
-- (서버는 최초 접속 시 CREATE IF NOT EXISTS 로도 생성 가능)

CREATE TABLE IF NOT EXISTS powerball_daily_counts (
  stat_date DATE NOT NULL PRIMARY KEY COMMENT 'KST 달력일',
  odd_count INT NOT NULL DEFAULT 0,
  even_count INT NOT NULL DEFAULT 0,
  under_count INT NOT NULL DEFAULT 0,
  over_count INT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 당일 결과 누적';
