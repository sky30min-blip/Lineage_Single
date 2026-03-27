-- _robot_book INSERT 1062 (Duplicate entry '...이동 주문서' for key 'PRIMARY')
-- 원인: PRIMARY KEY가 `location`(장소 이름)만 잡혀 있으면, robot_objId가 달라도 같은 이름은 1행만 가능.
-- 해결: 복합 PK (robot_objId, location) 로 변경한 뒤 GM 툴에서 INSERT 재실행.
--
-- 실행 전 백업 권장. InnoDB + utf8 권장.

ALTER TABLE `_robot_book` DROP PRIMARY KEY;
ALTER TABLE `_robot_book` ADD PRIMARY KEY (`robot_objId`, `location`);

-- 같은 스크립트를 여러 번 돌릴 때(좌표만 수정) 예시:
-- INSERT INTO `_robot_book` (`robot_objId`,`location`,`locX`,`locY`,`locMAP`,`입장레벨`)
-- VALUES (1900000,'본토 던전 3층 이동 주문서',32798,32754,9,4)
-- ON DUPLICATE KEY UPDATE `locX`=VALUES(`locX`),`locY`=VALUES(`locY`),`locMAP`=VALUES(`locMAP`),`입장레벨`=VALUES(`입장레벨`);
