-- 자리 쟁탈(dollrace) 최종 몹이 데스나이트로 지정된 경우에만 교체.
-- 데스나이트는 monster_spawnlist_boss(본토 던전 5~7층 랜덤)에서만 리젠되게 분리.
UPDATE `gm_event_settings`
SET `monster_name` = '킹 버그베어'
WHERE `event_key` = 'dollrace' AND `monster_name` = '데스나이트';
