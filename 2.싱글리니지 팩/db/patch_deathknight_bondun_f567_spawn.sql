-- 데스나이트 보스: 본토 던전 5층(맵11)·6층(맵12)·7층(맵13) 중 랜덤 스폰
-- BossController + MonsterBossSpawnlistDatabase 가 spawn_x_y_map 을 & 로 분리해 후보 중 랜덤 선택
UPDATE `monster_spawnlist_boss`
SET `spawn_x_y_map` = '0, 0, 11 & 0, 0, 12 & 0, 0, 13'
WHERE `monster` = '데스나이트' AND `name` = '데스나이트';
