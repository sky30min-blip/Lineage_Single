-- ============================================
-- 파워볼 전광판 NPC 6개 (일반 NPC로 DB 등록)
-- ============================================
-- 서버는 npc_spawnlist의 name이 '파워볼전광판_1' ~ '파워볼전광판_6' 인 NPC를
-- 전광판으로 인식해 숫자 표시용으로 사용합니다. 적용 후 서버 재시작 필요.

USE lin200;

-- 1. npc 테이블에 전광판용 NPC 6개 추가 (type=default, gfxid=887)
INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
VALUES
  ('파워볼전광판1', 'default', '0', 887, 0, 1, 0, 0, 'false', 0, 0),
  ('파워볼전광판2', 'default', '0', 887, 0, 1, 0, 0, 'false', 0, 0),
  ('파워볼전광판3', 'default', '0', 887, 0, 1, 0, 0, 'false', 0, 0),
  ('파워볼전광판4', 'default', '0', 887, 0, 1, 0, 0, 'false', 0, 0),
  ('파워볼전광판5', 'default', '0', 887, 0, 1, 0, 0, 'false', 0, 0),
  ('파워볼전광판6', 'default', '0', 887, 0, 1, 0, 0, 'false', 0, 0)
ON DUPLICATE KEY UPDATE type = 'default', gfxid = 887;

-- 2. npc_spawnlist에 전광판 6개 스폰 (맵 4, 좌표 1번~6번, 6시 방향)
--    name이 파워볼전광판_1 ~ _6 이어야 서버가 전광판으로 인식합니다.
INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
VALUES
  ('파워볼전광판_1', '파워볼전광판1', 33440, 32795, 4, 4, 0, ''),
  ('파워볼전광판_2', '파워볼전광판2', 33441, 32796, 4, 4, 0, ''),
  ('파워볼전광판_3', '파워볼전광판3', 33442, 32797, 4, 4, 0, ''),
  ('파워볼전광판_4', '파워볼전광판4', 33443, 32798, 4, 4, 0, ''),
  ('파워볼전광판_5', '파워볼전광판5', 33444, 32799, 4, 4, 0, ''),
  ('파워볼전광판_6', '파워볼전광판6', 33445, 32800, 4, 4, 0, '')
ON DUPLICATE KEY UPDATE npcName = VALUES(npcName), locX = VALUES(locX), locY = VALUES(locY), locMap = VALUES(locMap), heading = VALUES(heading);
