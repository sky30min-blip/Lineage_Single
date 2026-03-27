-- 파워볼 현재 누적금 게시판 스폰
-- 좌표: X 33417 / Y 32821 / MAP 4

USE lin200;

-- 기존 같은 이름 스폰 정리
DELETE FROM background_spawnlist WHERE name = 'powerball_reward_board';

-- server 게시판 템플릿을 복사해서 파워볼 전용 게시판 생성
INSERT INTO background_spawnlist
(`name`,`nameid`,`gfx`,`gfx_mode`,`lawful`,`light`,`title`,`locX`,`locY`,`locMap`,`locSize`,`heading`,`item_nameid`,`item_count`,`item_remove`)
SELECT
  'powerball_reward_board',
  '게시판',
  `gfx`,
  `gfx_mode`,
  `lawful`,
  `light`,
  'powerball_reward',
  33417,
  32821,
  4,
  `locSize`,
  `heading`,
  `item_nameid`,
  `item_count`,
  `item_remove`
FROM background_spawnlist
WHERE title = 'server'
LIMIT 1;

