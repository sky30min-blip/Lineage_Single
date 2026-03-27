-- 파워볼: 홀/짝 쿠폰 NAMEID $1249/$1250 (레이스표와 분리), 인벤ID 151 (레이스표 아이콘)
-- 실행: mysql -u계정 -p l1jdb < powerball_shop.sql

-- 1. 아이템: 홀 쿠폰 $1249, 짝 쿠폰 $1250, 인벤ID 151 (레이스표 모양)
INSERT INTO `item` VALUES ('홀 쿠폰', 'item', 'etc', '$1249', '기타', 'true', '0', '0', '8', '151', '151', '0', '0', 'true', 'false', 'true', 'true', 'true', 'true', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', 'none', 'false', 'false', 'false', 'false', 'false', '', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '')
ON DUPLICATE KEY UPDATE `아이템이름`='홀 쿠폰', `NAMEID`='$1249', `인벤ID`=151, `겹침`='false';

INSERT INTO `item` VALUES ('짝 쿠폰', 'item', 'etc', '$1250', '기타', 'true', '0', '0', '8', '151', '151', '0', '0', 'true', 'false', 'true', 'true', 'true', 'true', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', 'none', 'false', 'false', 'false', 'false', 'false', '', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '')
ON DUPLICATE KEY UPDATE `아이템이름`='짝 쿠폰', `NAMEID`='$1250', `인벤ID`=151, `겹침`='false';

-- 언더/오버 NAMEID는 1251/1252 금지: ItemDatabase.newInstance()가 해당 번호를 HealingPotion(농축 체력·고급 회복제)으로 고정함.
INSERT INTO `item` VALUES ('언더 쿠폰', 'item', 'etc', '$1254', '기타', 'true', '0', '0', '8', '151', '151', '0', '0', 'true', 'false', 'true', 'true', 'true', 'true', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', 'none', 'false', 'false', 'false', 'false', 'false', '', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '')
ON DUPLICATE KEY UPDATE `아이템이름`='언더 쿠폰', `NAMEID`='$1254', `인벤ID`=151, `겹침`='false';

INSERT INTO `item` VALUES ('오버 쿠폰', 'item', 'etc', '$1255', '기타', 'true', '0', '0', '8', '151', '151', '0', '0', 'true', 'false', 'true', 'true', 'true', 'true', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', 'none', 'false', 'false', 'false', 'false', 'false', '', 'true', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'false', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '')
ON DUPLICATE KEY UPDATE `아이템이름`='오버 쿠폰', `NAMEID`='$1255', `인벤ID`=151, `겹침`='false';

-- 2. 파워볼진행자 NPC 상점 목록 (구매 시 금액 입력창으로 처리됨)
--    buy=true: 플레이어가 NPC에게 삼 / sell=true: 플레이어가 NPC에게 팜(당첨 쿠폰 매입). 둘 다 true 여야 게임과 GM툴이 일치.
INSERT INTO `npc_shop` (`name`, `itemname`, `itemcount`, `itembress`, `itemenlevel`, `itemtime`, `sell`, `buy`, `gamble`, `price`, `aden_type`) VALUES
('파워볼진행자', '홀 쿠폰', 1, 1, 0, 0, 'true', 'true', 'false', 0, ''),
('파워볼진행자', '짝 쿠폰', 1, 1, 0, 0, 'true', 'true', 'false', 0, ''),
('파워볼진행자', '언더 쿠폰', 1, 1, 0, 0, 'true', 'true', 'false', 0, ''),
('파워볼진행자', '오버 쿠폰', 1, 1, 0, 0, 'true', 'true', 'false', 0, '');

-- 3. 기존 DB: 겹침이 true로 남아 있으면 인벤에서 쿠폰이 합쳐짐 → 무조건 비스택
UPDATE `item` SET `겹침`='false' WHERE `아이템이름` IN ('홀 쿠폰','짝 쿠폰','언더 쿠폰','오버 쿠폰');

-- 4. 언더/오버 npc_shop 행만 빠진 경우 보강 (서버는 item+npc_shop 둘 다 있어야 목록에 표시)
INSERT INTO `npc_shop` (`name`, `itemname`, `itemcount`, `itembress`, `itemenlevel`, `itemtime`, `sell`, `buy`, `gamble`, `price`, `aden_type`)
SELECT '파워볼진행자', '언더 쿠폰', 1, 1, 0, 0, 'true', 'true', 'false', 0, ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `npc_shop` WHERE `name`='파워볼진행자' AND `itemname`='언더 쿠폰' AND `itembress`=1 AND `itemenlevel`=0 LIMIT 1);
INSERT INTO `npc_shop` (`name`, `itemname`, `itemcount`, `itembress`, `itemenlevel`, `itemtime`, `sell`, `buy`, `gamble`, `price`, `aden_type`)
SELECT '파워볼진행자', '오버 쿠폰', 1, 1, 0, 0, 'true', 'true', 'false', 0, ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `npc_shop` WHERE `name`='파워볼진행자' AND `itemname`='오버 쿠폰' AND `itembress`=1 AND `itemenlevel`=0 LIMIT 1);

-- 5. 기존 행 보정: 언더/오버만 예전에 sell=false 로 들어간 경우 + 잘못된 itemname 접두어
UPDATE `npc_shop` SET `buy`='true', `sell`='true', `itemcount`=1
WHERE `name`='파워볼진행자' AND `itemname` IN ('홀 쿠폰','짝 쿠폰','언더 쿠폰','오버 쿠폰');
UPDATE `npc_shop` SET `itemname`='언더 쿠폰', `buy`='true', `sell`='true', `itemcount`=1
WHERE `name`='파워볼진행자' AND `itemname` IN ('파워볼: 언더 쿠폰','파워볼 : 언더 쿠폰');
UPDATE `npc_shop` SET `itemname`='오버 쿠폰', `buy`='true', `sell`='true', `itemcount`=1
WHERE `name`='파워볼진행자' AND `itemname` IN ('파워볼: 오버 쿠폰','파워볼 : 오버 쿠폰');
