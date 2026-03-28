-- 언더/오버 쿠폰은 NAMEID $1254/$1255 전용 (ItemDatabase: 1251~1253 = 농축 회복제 계열 HealingPotion)
UPDATE `item` SET `NAMEID`='$1254' WHERE `아이템이름`='언더 쿠폰';
UPDATE `item` SET `NAMEID`='$1255' WHERE `아이템이름`='오버 쿠폰';
UPDATE `item` SET `NAMEID`='$1251' WHERE `아이템이름`='농축 체력 회복제';
UPDATE `item` SET `NAMEID`='$1252' WHERE `아이템이름`='농축 고급 체력 회복제';
UPDATE `item` SET `NAMEID`='$1253' WHERE `아이템이름`='농축 강력 체력 회복제';
