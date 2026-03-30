"""
리니지 싱글 서버 GM 툴 - 누락 테이블 생성 스크립트
"""

# 필수 테이블 목록
REQUIRED_TABLES = [
    'gm_event_settings',
    'ban_word',
    'gm_item_delivery',
    'gm_adena_delivery',
    'gm_location_delivery',
    'gm_boss_status',
    'gm_boss_kill_log',
    'gm_boss_kill_participant',
    'gm_chat_log',
    'gm_chat_send',
    'gm_server_command',
    'gm_npc_despawned',
    'powerball_reward_run',
    'powerball_reward_line',
]

# 누락 테이블 생성 SQL
TABLE_CREATION_SQLS = {
    'gm_event_settings': """
        CREATE TABLE IF NOT EXISTS `gm_event_settings` (
          `event_key` VARCHAR(32) NOT NULL COMMENT 'hell,treasure,worldboss,icedungeon,timeevent,devil,dimension,dollrace',
          `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '1=일정·월드알림, 0=끔',
          `min_level` INT NOT NULL DEFAULT 0 COMMENT '0=lineage.conf 기본',
          `play_time_seconds` INT NOT NULL DEFAULT 0 COMMENT '0=lineage.conf 기본(초)',
          `monster_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '빈칸=서버 기본',
          `bonus_drop_item` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '월드보스 처치 보상 아이템명(빈칸=기본)',
          `bonus_drop_count` INT NOT NULL DEFAULT 1,
          `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
          PRIMARY KEY (`event_key`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴 스케줄 이벤트 런타임 설정(서버가 주기적으로 로드)';
    """,
    'ban_word': """
        CREATE TABLE IF NOT EXISTS `ban_word` (
          `uid` INT UNSIGNED NOT NULL AUTO_INCREMENT,
          `chat` VARCHAR(255) NOT NULL DEFAULT '',
          PRIMARY KEY (`uid`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='채팅 금지어 (서버 BanWordDatabase: 컬럼 chat)';
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
    'gm_boss_status': """
        CREATE TABLE IF NOT EXISTS `gm_boss_status` (
          `boss_name` VARCHAR(64) NOT NULL,
          `monster_name` VARCHAR(64) NOT NULL DEFAULT '',
          `map` INT NOT NULL DEFAULT 0,
          `x` INT NOT NULL DEFAULT 0,
          `y` INT NOT NULL DEFAULT 0,
          `alive` TINYINT NOT NULL DEFAULT 0 COMMENT '1=생존, 0=사망/미스폰',
          `last_spawn_at` DATETIME(3) NULL,
          `last_dead_at` DATETIME(3) NULL,
          `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
          `note` VARCHAR(64) NOT NULL DEFAULT '',
          PRIMARY KEY (`boss_name`),
          KEY `idx_alive` (`alive`),
          KEY `idx_updated` (`updated_at`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='서버가 주기적으로 기록하는 보스 생존 현황(GM툴 조회용)';
    """,
    'gm_boss_kill_log': """
        CREATE TABLE IF NOT EXISTS `gm_boss_kill_log` (
          `id` BIGINT NOT NULL AUTO_INCREMENT,
          `boss_name` VARCHAR(64) NOT NULL,
          `map` INT NOT NULL DEFAULT 0,
          `x` INT NOT NULL DEFAULT 0,
          `y` INT NOT NULL DEFAULT 0,
          `map_name` VARCHAR(128) NOT NULL DEFAULT '',
          `killer_name` VARCHAR(64) NOT NULL DEFAULT '',
          `killer_clan` VARCHAR(64) NOT NULL DEFAULT '',
          `participant_count` INT NOT NULL DEFAULT 0,
          `killed_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
          PRIMARY KEY (`id`),
          KEY `idx_boss_time` (`boss_name`, `killed_at`),
          KEY `idx_killed_at` (`killed_at`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='보스 처치 로그(킬러/좌표/시간)';
    """,
    'gm_boss_kill_participant': """
        CREATE TABLE IF NOT EXISTS `gm_boss_kill_participant` (
          `id` BIGINT NOT NULL AUTO_INCREMENT,
          `kill_id` BIGINT NOT NULL,
          `char_obj_id` BIGINT NOT NULL,
          `char_name` VARCHAR(64) NOT NULL DEFAULT '',
          `clan_name` VARCHAR(64) NOT NULL DEFAULT '',
          `is_killer` TINYINT NOT NULL DEFAULT 0,
          PRIMARY KEY (`id`),
          KEY `idx_kill_id` (`kill_id`),
          KEY `idx_char_obj_id` (`char_obj_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='보스 처치 참여자(레이드 참여 캐릭터 목록)';
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
          `command` VARCHAR(64) NOT NULL COMMENT 'server_open_wait, server_open, world_clear, character_save, kingdom_war, kingdom_war_start, kingdom_war_stop, all_buff, robot_on, robot_off, reload_robot, reload_robot_one, event_poly, event_rank_poly, npc_despawn, npc_respawn, reload',
          `param` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'reload_robot_one 시: objId 숫자 / reload 시: npc,item,monster,...robot / 기타 명령별 파라미터',
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
          `rank_metric` VARCHAR(32) NOT NULL DEFAULT 'level' COMMENT '수혜자 순위: level=직업별 레벨',
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
    'gm_kingdom_war_schedule': """
        CREATE TABLE IF NOT EXISTS `gm_kingdom_war_schedule` (
          `kingdom_uid` TINYINT UNSIGNED NOT NULL COMMENT '1~7 Lineage KINGDOM_*',
          `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '1=자동 공성 스케줄 사용',
          `weekdays` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '비트: Date.getDay 0=일..6=토',
          `start_hour` TINYINT UNSIGNED NOT NULL DEFAULT 20,
          `start_min` TINYINT UNSIGNED NOT NULL DEFAULT 0,
          `duration_minutes` SMALLINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0=lineage.conf kingdom_war_time',
          `weekdays_2` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '2차 시간대 요일 비트 0=비활성',
          `start_hour_2` TINYINT UNSIGNED NOT NULL DEFAULT 20,
          `start_min_2` TINYINT UNSIGNED NOT NULL DEFAULT 0,
          `updated_at` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
          PRIMARY KEY (`kingdom_uid`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        COMMENT='GM 툴 공성 자동 시작 스케줄(서버 KingdomController)';
    """,
}

# 기본 데이터 삽입 SQL (선택)
TABLE_INITIAL_DATA = {
    'ban_word': """
        INSERT IGNORE INTO `ban_word` (`chat`) VALUES
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
