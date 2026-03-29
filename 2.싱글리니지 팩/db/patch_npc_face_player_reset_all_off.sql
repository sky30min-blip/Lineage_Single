-- 이전에 DEFAULT 1 로 넣어서 전 NPC가 1이었던 DB를 한 번에 되돌릴 때 사용.
-- (이미 특정 NPC만 1로 써놨다면 실행하지 말 것)

UPDATE `npc` SET `face_player_on_talk` = 0 WHERE 1;

ALTER TABLE `npc`
  MODIFY COLUMN `face_player_on_talk` TINYINT(1) NOT NULL DEFAULT 0
  COMMENT '1=GM force face 0=legacy';
