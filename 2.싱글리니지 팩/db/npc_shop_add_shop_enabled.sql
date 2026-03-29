-- npc_shop: 상점 한 줄만 끄고 나중에 다시 켤 수 있게 (서버는 shop_enabled=0 행을 로드하지 않음)
ALTER TABLE `npc_shop`
  ADD COLUMN `shop_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1=노출 0=비활성' AFTER `aden_type`;
