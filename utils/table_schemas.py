"""
리니지 싱글 서버 GM 툴 - 누락 테이블 생성 스크립트
"""

# 필수 테이블 목록
REQUIRED_TABLES = [
    'ban_word',
    'gm_item_delivery',
    'gm_adena_delivery',
    'gm_location_delivery',
    'gm_chat_log',
    'gm_chat_send',
    'gm_server_command',
    'gm_npc_despawned',
    'powerball_reward_run',
    'powerball_reward_line',
]

# 누락 테이블 생성 SQL
TABLE_CREATION_SQLS = {
    'ban_word': """
        CREATE TABLE IF NOT EXISTS `ban_word` (
          `id` INT NOT NULL AUTO_INCREMENT,
          `word` VARCHAR(50) NOT NULL,
          PRIMARY KEY (`id`),
          UNIQUE KEY `word` (`word`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='채팅 금지어 목록';
    """,
    'gm_item_delivery': """
        CREATE TABLE IF NOT EXISTS `gm_item_delivery` (
          `cha_objId` BIGINT NOT NULL,
          `objId` BIGINT NOT NULL,
          `delivered` TINYINT NOT NULL DEFAULT 0,
          PRIMARY KEY (`objId`),
          KEY `idx_delivered` (`delivered`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴 접속 중 지급용 대기 테이블(서버가 폴링)';
    """,
    'gm_adena_delivery': """
        CREATE TABLE IF NOT EXISTS `gm_adena_delivery` (
          `id` INT NOT NULL AUTO_INCREMENT,
          `cha_objId` BIGINT NOT NULL,
          `new_count` BIGINT NOT NULL,
          `delivered` TINYINT NOT NULL DEFAULT 0,
          PRIMARY KEY (`id`),
          KEY `idx_delivered` (`delivered`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴 아데나 변경 즉시 반영(서버가 폴링)';
    """,
    'gm_location_delivery': """
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
        COMMENT='GM 툴 좌표 이동 즉시 반영(서버가 폴링)';
    """,
    'gm_chat_log': """
        CREATE TABLE IF NOT EXISTS `gm_chat_log` (
          `id` BIGINT NOT NULL AUTO_INCREMENT,
          `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
          `channel` TINYINT NOT NULL COMMENT '0=일반,2=외침,3=전체,4=혈맹,9=귓말,11=파티,12=장사,20=시스템',
          `char_name` VARCHAR(45) NOT NULL DEFAULT '',
          `target_name` VARCHAR(45) NOT NULL DEFAULT '' COMMENT '귓말 시 상대방',
          `msg` VARCHAR(500) NOT NULL DEFAULT '',
          PRIMARY KEY (`id`),
          KEY `idx_created` (`created_at`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴 채팅 모니터링용(서버가 채팅 시 INSERT)';
    """,
    'gm_chat_send': """
        CREATE TABLE IF NOT EXISTS `gm_chat_send` (
          `id` INT NOT NULL AUTO_INCREMENT,
          `msg` VARCHAR(500) NOT NULL,
          `sent` TINYINT NOT NULL DEFAULT 0,
          PRIMARY KEY (`id`),
          KEY `idx_sent` (`sent`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴에서 전송할 전체 채팅(서버가 폴링 후 브로드캐스트)';
    """,
    'gm_server_command': """
        CREATE TABLE IF NOT EXISTS `gm_server_command` (
          `id` INT NOT NULL AUTO_INCREMENT,
          `command` VARCHAR(64) NOT NULL COMMENT 'server_open_wait, server_open, world_clear, character_save, kingdom_war, all_buff, robot_on, robot_off, event_poly, event_rank_poly, npc_despawn, npc_respawn',
          `param` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '0/1 등, npc_despawn/npc_respawn 시 스폰 name',
          `executed` TINYINT NOT NULL DEFAULT 0,
          `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
          PRIMARY KEY (`id`),
          KEY `idx_executed` (`executed`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='웹 GM 툴에서 서버 명령 요청(서버가 폴링 후 실행)';
    """,
    'gm_npc_despawned': """
        CREATE TABLE IF NOT EXISTS `gm_npc_despawned` (
          `spawn_name` VARCHAR(64) NOT NULL COMMENT 'npc_spawnlist.name',
          `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
          PRIMARY KEY (`spawn_name`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴에서 월드에서 제거한 NPC 스폰 목록(복구 드롭다운용)';
    """,
    'powerball_reward_run': """
        CREATE TABLE IF NOT EXISTS `powerball_reward_run` (
          `reward_date` DATE NOT NULL COMMENT 'KST 기준 정산일',
          `server_profit` BIGINT NOT NULL DEFAULT 0 COMMENT '당일 서버 순이익(배팅−당첨지급)',
          `pool_four_class` BIGINT NOT NULL DEFAULT 0 COMMENT '기사/법사/요정/다크엘프 풀 합(22%)',
          `pool_royal` BIGINT NOT NULL DEFAULT 0 COMMENT '군주 풀(12%)',
          `rank_metric` VARCHAR(32) NOT NULL DEFAULT 'contribution' COMMENT 'contribution|total_bet',
          `executed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
          `note` VARCHAR(255) DEFAULT NULL,
          PRIMARY KEY (`reward_date`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='파워볼 일일 포상 정산 실행 기록(중복 지급 방지)';
    """,
    'powerball_reward_line': """
        CREATE TABLE IF NOT EXISTS `powerball_reward_line` (
          `id` BIGINT NOT NULL AUTO_INCREMENT,
          `reward_date` DATE NOT NULL,
          `char_obj_id` INT NOT NULL,
          `char_name` VARCHAR(45) NOT NULL,
          `class_id` TINYINT NOT NULL,
          `class_label` VARCHAR(32) NOT NULL COMMENT '기사/법사/요정/다크엘프/군주',
          `rank_in_class` TINYINT NOT NULL COMMENT '1~3',
          `amount` BIGINT NOT NULL,
          `slot_key` VARCHAR(48) NOT NULL COMMENT '예: knight_r1, royal_r2',
          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
          PRIMARY KEY (`id`),
          UNIQUE KEY `uq_slot` (`reward_date`, `slot_key`),
          KEY `idx_rd` (`reward_date`),
          KEY `idx_char` (`char_obj_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='파워볼 일일 포상 지급 명세';
    """,
}

# 기본 데이터 삽입 SQL (선택)
TABLE_INITIAL_DATA = {
    'ban_word': """
        INSERT IGNORE INTO `ban_word` (`word`) VALUES
        ('시발'),
        ('개새끼'),
        ('병신');
    """,
}


def get_create_sql(table_name: str) -> str:
    """테이블 생성 SQL 가져오기"""
    return TABLE_CREATION_SQLS.get(table_name, "")


def get_initial_data_sql(table_name: str) -> str:
    """테이블 초기 데이터 SQL 가져오기"""
    return TABLE_INITIAL_DATA.get(table_name, "")


def get_all_required_tables() -> list:
    """필수 테이블 목록 반환"""
    return REQUIRED_TABLES.copy()
