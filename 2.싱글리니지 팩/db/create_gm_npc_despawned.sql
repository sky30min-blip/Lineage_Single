-- GM 툴 NPC 삭제/복구 기능용 테이블 (서버 npc_despawn 시 사용)
-- 실행: MariaDB/MySQL에서 lin200 선택 후 이 파일 실행
-- 예: mysql -u root -p lin200 < create_gm_npc_despawned.sql

USE lin200;

CREATE TABLE IF NOT EXISTS `gm_npc_despawned` (
  `spawn_name` VARCHAR(64) NOT NULL COMMENT 'npc_spawnlist.name',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`spawn_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='GM 툴에서 월드에서 제거한 NPC 스폰 목록(복구 드롭다운용)';
