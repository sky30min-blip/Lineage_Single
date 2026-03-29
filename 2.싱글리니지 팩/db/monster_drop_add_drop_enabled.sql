-- monster_drop: 드랍 행만 끄고 나중에 다시 켤 수 있게 (행 삭제와 동일하게 서버는 드랍 안 함)
ALTER TABLE `monster_drop`
  ADD COLUMN `drop_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1=드랍 0=비활성' AFTER `chance`;
