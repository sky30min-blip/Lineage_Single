-- ============================================
-- 파워볼 NPC 등록 (npc + npc_spawnlist)
-- - 파워볼진행자: 상점(홀/짝 쿠폰) + 진행 안내
-- - 일반볼 / 파워볼: 추첨 결과 발표용 (일반채팅 + 호칭)
-- ============================================
-- 실행 후 서버 재시작 또는 NPC 리로드 필요.

USE l1jdb;

-- 1. npc 테이블
-- 1-1. 파워볼진행자 (상점 NPC, 기존 파워볼 상점 대체)
INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
VALUES ('파워볼진행자', '파워볼진행자', '50999', 887, 0, 1, 0, 0, 'false', 0, 0)
ON DUPLICATE KEY UPDATE type = '파워볼진행자', nameid = '50999', gfxid = 887;

-- 1-2. 일반볼 (추첨 발표용, 일반채팅 + 호칭에 000차:11,12,13,14,15)
INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
VALUES ('일반볼', '일반볼', '일반볼', 887, 0, 1, 0, 0, 'false', 0, 0)
ON DUPLICATE KEY UPDATE type = '일반볼', nameid = '일반볼', gfxid = 887;

-- 1-3. 파워볼 (추첨 발표용, 일반채팅 + 호칭에 000차:7)
INSERT INTO npc (name, type, nameid, gfxid, gfxMode, hp, lawful, light, ai, areaatk, arrowGfx)
VALUES ('파워볼', '파워볼', '파워볼', 887, 0, 1, 0, 0, 'false', 0, 0)
ON DUPLICATE KEY UPDATE type = '파워볼', nameid = '파워볼', gfxid = 887;

-- 1-4. (중복 row 대응) name 컬럼 기준으로 nameid를 강제 업데이트
-- npcName 기준으로 스폰되는 NpcDatabase.find(npc)에서 오래된 row가 잡히는 경우를 방지
UPDATE npc SET type = '일반볼', nameid = '일반볼', gfxid = 887 WHERE name = '일반볼';
UPDATE npc SET type = '파워볼', nameid = '파워볼', gfxid = 887 WHERE name = '파워볼';
UPDATE npc SET type = '파워볼진행자', nameid = '50999', gfxid = 887 WHERE name = '파워볼진행자';

-- 2. npc_spawnlist
--    locX, locY, locMap = 게임 내 [명령어]맵 또는 [명령어]좌표 로 표시되는 값과 동일 (그대로 넣으면 해당 위치에 스폰됨)
--    기란 맵 4, 5시 방향 heading=3
-- 2-1. 파워볼진행자 (상점)
INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
VALUES ('powerball_1', '파워볼진행자', 33418, 32823, 4, 3, 0, '')
ON DUPLICATE KEY UPDATE npcName = '파워볼진행자', locX = 33418, locY = 32823, locMap = 4, heading = 3;

-- 2-2. 일반볼 (추첨 발표 NPC)
INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
VALUES ('powerball_일반볼', '일반볼', 33420, 32820, 4, 3, 0, '')
ON DUPLICATE KEY UPDATE npcName = '일반볼', locX = 33420, locY = 32820, locMap = 4, heading = 3;

-- 2-3. 파워볼 (추첨 발표 NPC)
INSERT INTO npc_spawnlist (name, npcName, locX, locY, locMap, heading, respawn, title)
VALUES ('powerball_파워볼', '파워볼', 33421, 32821, 4, 3, 0, '')
ON DUPLICATE KEY UPDATE npcName = '파워볼', locX = 33421, locY = 32821, locMap = 4, heading = 3;
