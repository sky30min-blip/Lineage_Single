-- ============================================
-- 파워볼 미니게임 테이블 (MariaDB l1jdb)
-- ============================================

USE l1jdb;

-- 1. 회차별 결과
CREATE TABLE IF NOT EXISTS powerball_results (
  round_id INT UNSIGNED NOT NULL PRIMARY KEY COMMENT '회차 ID',
  total_sum INT UNSIGNED NOT NULL COMMENT '총합',
  result_type TINYINT UNSIGNED NOT NULL COMMENT '0: 짝, 1: 홀',
  under_over_type TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0:언더(총합<=72) 1:오버(총합>72)',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 회차 결과';

-- 2. 유저 배팅 내역
CREATE TABLE IF NOT EXISTS powerball_bets (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '배팅 ID',
  char_id INT UNSIGNED NOT NULL COMMENT '캐릭터 objID',
  round_id INT UNSIGNED NOT NULL COMMENT '회차 ID',
  bet_amount BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '배팅 아데나',
  pick_type TINYINT UNSIGNED NOT NULL COMMENT '0:짝 1:홀 2:언더 3:오버',
  is_processed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0: 미처리, 1: 처리됨',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '배팅일시',
  INDEX idx_round (round_id),
  INDEX idx_char_round (char_id, round_id),
  INDEX idx_processed (is_processed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 배팅 내역';

-- 3. 당일(KST) 홀/짝·언더/오버 누적 — 서버 재시작 후 진행자 멘트 횟수 이어짐
CREATE TABLE IF NOT EXISTS powerball_daily_counts (
  stat_date DATE NOT NULL PRIMARY KEY COMMENT 'KST 달력일',
  odd_count INT NOT NULL DEFAULT 0,
  even_count INT NOT NULL DEFAULT 0,
  under_count INT NOT NULL DEFAULT 0,
  over_count INT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 당일 결과 누적';
