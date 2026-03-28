-- 파워볼 일일 포상 원장 (GM 툴 / 수동 정산용)
-- mysql -u... lin200 < powerball_reward_tables.sql

USE lin200;

CREATE TABLE IF NOT EXISTS `powerball_reward_run` (
  `reward_date` DATE NOT NULL COMMENT 'KST 기준 정산일',
  `server_profit` BIGINT NOT NULL DEFAULT 0 COMMENT '당일 서버 순이익(배팅−당첨지급)',
  `pool_four_class` BIGINT NOT NULL DEFAULT 0 COMMENT '기사/법사/요정/다크엘프 풀 합(22%)',
  `pool_royal` BIGINT NOT NULL DEFAULT 0 COMMENT '군주 풀(12%)',
  `rank_metric` VARCHAR(32) NOT NULL DEFAULT 'level' COMMENT '수혜자: 직업별 레벨 순위',
  `executed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `note` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`reward_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 일일 포상 정산 실행 기록';

CREATE TABLE IF NOT EXISTS `powerball_reward_line` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reward_date` DATE NOT NULL,
  `char_obj_id` INT NOT NULL,
  `char_name` VARCHAR(45) NOT NULL,
  `class_id` TINYINT NOT NULL,
  `class_label` VARCHAR(32) NOT NULL,
  `rank_in_class` TINYINT NOT NULL COMMENT '1~3',
  `amount` BIGINT NOT NULL,
  `slot_key` VARCHAR(48) NOT NULL COMMENT '예: knight_r1, royal_r2',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_slot` (`reward_date`, `slot_key`),
  KEY `idx_rd` (`reward_date`),
  KEY `idx_char` (`char_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='파워볼 일일 포상 지급 명세';
