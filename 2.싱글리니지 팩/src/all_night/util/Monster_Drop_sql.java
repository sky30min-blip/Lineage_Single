package all_night.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import lineage.share.TimeLine;

public class Monster_Drop_sql {
	
	public static void writeSql() {
		String drop;
		StringTokenizer dropList;
		
		try {
			TimeLine.start("monster_drop.sql 생성 완료 - ");

			try (BufferedReader data = new BufferedReader(new FileReader("sql/monster_drop.txt"));
					BufferedWriter fw = new BufferedWriter(new FileWriter("sql/monster_drop.sql", false))) {

			fw.write("SET FOREIGN_KEY_CHECKS=0;\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("-- Table structure for `monster_drop`\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("DROP TABLE IF EXISTS `monster_drop`;\r\n");
			fw.write("CREATE TABLE `monster_drop` (\r\n");
			fw.write("  `name` varchar(50) NOT NULL DEFAULT '',\r\n");
			fw.write("  `monster_name` varchar(50) NOT NULL DEFAULT '',\r\n");
			fw.write("  `item_name` varchar(50) NOT NULL DEFAULT '',\r\n");
			fw.write("  `item_bress` int(10) unsigned NOT NULL DEFAULT '1',\r\n");

			fw.write("  `item_en` tinyint(10) NOT NULL DEFAULT '0',\r\n");
			fw.write("  `count_min` int(10) unsigned NOT NULL DEFAULT '1',\r\n");
			fw.write("  `count_max` int(10) unsigned NOT NULL DEFAULT '1',\r\n");
			fw.write("  `chance` varchar(5) NOT NULL DEFAULT '0',\r\n");
			
			fw.write("  PRIMARY KEY (`monster_name`,`item_name`,`item_bress`,`item_en`),\r\n");
			fw.write("  KEY `monster_name` (`monster_name`),\r\n");
			fw.write("  KEY `item_name` (`item_name`),\r\n");
			fw.write("  KEY `item_bress` (`item_bress`),\r\n");
			fw.write("  KEY `item_en` (`item_en`)\r\n");
			fw.write(") ENGINE=MyISAM DEFAULT CHARSET=utf8;\r\n\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("-- Records of monster_drop\r\n");
			fw.write("-- ----------------------------\r\n");

			while ((drop = data.readLine()) != null) {
				if (drop.contains("#")) {
					continue;
				}
				dropList = new StringTokenizer(drop, ",");

				fw.write(String.format("INSERT INTO `monster_drop` VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');", 
						dropList.nextToken(), dropList.nextToken(), dropList.nextToken(), dropList.nextToken(), dropList.nextToken(), 
						dropList.nextToken(), dropList.nextToken(), dropList.nextToken()) + "\r\n");
			}

			}

			TimeLine.end();

		} catch (Exception e) {
			lineage.share.System.println("monster_drop.sql 생성 실패: " + e);
		}
	}
	
}
