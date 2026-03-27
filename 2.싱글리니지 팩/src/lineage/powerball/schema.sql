-- 파워볼 게임 데이터베이스 스키마

-- 1. 게임 회차 기록 테이블
CREATE TABLE IF NOT EXISTS powerball_rounds (
    round_id INT PRIMARY KEY AUTO_INCREMENT,
    normal_ball_1 INT NOT NULL,
    normal_ball_2 INT NOT NULL,
    normal_ball_3 INT NOT NULL,
    normal_ball_4 INT NOT NULL,
    normal_ball_5 INT NOT NULL,
    normal_sum INT NOT NULL,
    power_ball INT NOT NULL,
    total_sum INT NOT NULL,
    is_odd BOOLEAN NOT NULL,
    total_bets INT DEFAULT 0,
    total_amount BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_at (created_at),
    INDEX idx_is_odd (is_odd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 베팅 기록 테이블
CREATE TABLE IF NOT EXISTS powerball_bets (
    bet_id INT PRIMARY KEY AUTO_INCREMENT,
    round_id INT NOT NULL,
    char_name VARCHAR(50) NOT NULL,
    char_objid INT NOT NULL,
    bet_type ENUM('ODD', 'EVEN') NOT NULL,
    bet_amount BIGINT NOT NULL,
    win_amount BIGINT DEFAULT 0,
    is_win BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (round_id) REFERENCES powerball_rounds(round_id),
    INDEX idx_round_id (round_id),
    INDEX idx_char_name (char_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 통계 테이블
CREATE TABLE IF NOT EXISTS powerball_statistics (
    stat_id INT PRIMARY KEY AUTO_INCREMENT,
    stat_date DATE NOT NULL UNIQUE,
    total_rounds INT DEFAULT 0,
    odd_count INT DEFAULT 0,
    even_count INT DEFAULT 0,
    total_bets_count INT DEFAULT 0,
    total_bet_amount BIGINT DEFAULT 0,
    total_win_amount BIGINT DEFAULT 0,
    unique_players INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 플레이어 통계 테이블
CREATE TABLE IF NOT EXISTS powerball_player_stats (
    player_id INT PRIMARY KEY AUTO_INCREMENT,
    char_name VARCHAR(50) NOT NULL UNIQUE,
    total_bets INT DEFAULT 0,
    total_bet_amount BIGINT DEFAULT 0,
    total_wins INT DEFAULT 0,
    total_win_amount BIGINT DEFAULT 0,
    profit BIGINT DEFAULT 0,
    last_bet_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_char_name (char_name),
    INDEX idx_profit (profit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 초기 데이터 삽입 (오늘 날짜 통계)
INSERT INTO powerball_statistics (stat_date, total_rounds, odd_count, even_count)
VALUES (CURDATE(), 0, 0, 0)
ON DUPLICATE KEY UPDATE stat_date = stat_date;

-- 샘플 쿼리들

-- 최근 10회 결과 조회
-- SELECT round_id, total_sum, is_odd, created_at 
-- FROM powerball_rounds 
-- ORDER BY round_id DESC 
-- LIMIT 10;

-- 특정 유저의 베팅 기록
-- SELECT b.round_id, b.bet_type, b.bet_amount, b.win_amount, b.is_win, b.created_at
-- FROM powerball_bets b
-- WHERE b.char_name = '유저이름'
-- ORDER BY b.created_at DESC
-- LIMIT 20;

-- 오늘의 통계
-- SELECT * FROM powerball_statistics WHERE stat_date = CURDATE();

-- 수익률 TOP 10 플레이어
-- SELECT char_name, total_bets, total_bet_amount, total_win_amount, profit
-- FROM powerball_player_stats
-- ORDER BY profit DESC
-- LIMIT 10;

-- 특정 회차의 모든 베팅 정보
-- SELECT b.char_name, b.bet_type, b.bet_amount, b.win_amount, b.is_win
-- FROM powerball_bets b
-- WHERE b.round_id = 123
-- ORDER BY b.bet_amount DESC;
