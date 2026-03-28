-- 보스 시간 리젠 on/off (GM 도구 체크박스 · MonsterBossSpawnlistDatabase)
-- 기존 DB에 한 번만 실행하면 됩니다. 이미 컬럼이 있으면 오류 나므로 무시하세요.
ALTER TABLE `monster_spawnlist_boss`
  ADD COLUMN `spawn_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1=리젠활성 0=비활성' AFTER `group_monster`;
