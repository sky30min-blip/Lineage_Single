-- npc.face_player_on_talk: 1=GM에서 강제로 클릭한 PC 방향만 봄, 0(기본)=예전 서버처럼 클래스별 동작
ALTER TABLE `npc`
  ADD COLUMN `face_player_on_talk` TINYINT(1) NOT NULL DEFAULT 0
  COMMENT '1=GM force face 0=legacy'
  AFTER `arrowGfx`;
