-- 금칙어 테이블 (BanWordDatabase.java: SELECT * FROM ban_word, 컬럼 chat)
-- lin200 DB에 적용: mysql -uroot -p lin200 < fix_ban_word.sql

USE lin200;

CREATE TABLE IF NOT EXISTS `ban_word` (
  `uid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `chat` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`uid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
