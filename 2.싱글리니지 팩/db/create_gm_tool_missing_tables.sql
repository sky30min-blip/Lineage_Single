-- GM 툴·서버에서 쓰는 누락 테이블 일괄 생성 (데이터베이스: lin200 — Windows 실플레이 DB)
--
-- Windows (mysql.conf 기본 3306):
--   mysql --protocol=TCP -h 127.0.0.1 -P 3306 -u root -p lin200 < create_gm_tool_missing_tables.sql
-- Docker만 쓸 때:
--   docker exec -i l1j-db mariadb -u root -p1307 --default-character-set=utf8mb4 lin200 < create_gm_tool_missing_tables.sql

USE lin200;

-- BanWordDatabase.java: rs.getString("chat")
CREATE TABLE IF NOT EXISTS `ban_word` (
  `uid` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `chat` VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='채팅 금지어';

CREATE TABLE IF NOT EXISTS `gm_item_delivery` (
  `cha_objId` BIGINT NOT NULL,
  `objId` BIGINT NOT NULL,
  `delivered` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`objId`),
  KEY `idx_delivered` (`delivered`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='GM 툴 접속 중 지급 대기(서버 폴링)';

CREATE TABLE IF NOT EXISTS `gm_adena_delivery` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cha_objId` BIGINT NOT NULL,
  `new_count` BIGINT NOT NULL,
  `delivered` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_delivered` (`delivered`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='GM 툴 아데나 즉시 반영(서버 폴링)';

CREATE TABLE IF NOT EXISTS `gm_location_delivery` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cha_objId` BIGINT NOT NULL,
  `locX` INT NOT NULL,
  `locY` INT NOT NULL,
  `locMAP` INT NOT NULL,
  `delivered` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_delivered` (`delivered`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='GM 툴 좌표 이동(서버 폴링)';

CREATE TABLE IF NOT EXISTS `gm_chat_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `channel` TINYINT NOT NULL COMMENT '0=일반,2=외침,3=전체,4=혈맹,9=귓말,11=파티,12=장사,20=시스템',
  `char_name` VARCHAR(45) NOT NULL DEFAULT '',
  `target_name` VARCHAR(45) NOT NULL DEFAULT '' COMMENT '귓말 상대',
  `msg` VARCHAR(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='채팅 모니터링(서버 INSERT)';

CREATE TABLE IF NOT EXISTS `gm_chat_send` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `msg` VARCHAR(500) NOT NULL,
  `sent` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_sent` (`sent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='GM 전체 채팅 전송 대기(서버 폴링)';

CREATE TABLE IF NOT EXISTS `gm_npc_despawned` (
  `spawn_name` VARCHAR(64) NOT NULL COMMENT 'npc_spawnlist.name',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`spawn_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='GM NPC 월드 제거 목록';

CREATE TABLE IF NOT EXISTS `powerball_reward_run` (
  `reward_date` DATE NOT NULL COMMENT 'KST 정산일',
  `server_profit` BIGINT NOT NULL DEFAULT 0,
  `pool_four_class` BIGINT NOT NULL DEFAULT 0,
  `pool_royal` BIGINT NOT NULL DEFAULT 0,
  `rank_metric` VARCHAR(32) NOT NULL DEFAULT 'level',
  `executed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `note` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`reward_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='파워볼 일일 포상 정산 기록';

CREATE TABLE IF NOT EXISTS `powerball_reward_line` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reward_date` DATE NOT NULL,
  `char_obj_id` INT NOT NULL,
  `char_name` VARCHAR(45) NOT NULL,
  `class_id` TINYINT NOT NULL,
  `class_label` VARCHAR(32) NOT NULL,
  `rank_in_class` TINYINT NOT NULL COMMENT '1~3',
  `amount` BIGINT NOT NULL,
  `slot_key` VARCHAR(48) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_slot` (`reward_date`, `slot_key`),
  KEY `idx_rd` (`reward_date`),
  KEY `idx_char` (`char_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='파워볼 일일 포상 명세';
