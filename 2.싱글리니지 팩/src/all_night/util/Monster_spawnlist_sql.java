package all_night.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.database.DatabaseConnection;
import lineage.share.TimeLine;

public class Monster_spawnlist_sql {

	public static void writeSql() {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int uid = 1;
		try {
			TimeLine.start("monster_spawnlist 생성 완료 - ");
			
			// BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
			BufferedWriter fw = new BufferedWriter(new FileWriter("sql/monster_spawnlist.sql", false));
			
			fw.write("SET FOREIGN_KEY_CHECKS=0;\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("-- Table structure for `monster_spawnlist`\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("DROP TABLE IF EXISTS `monster_spawnlist`;\r\n");
			fw.write("CREATE TABLE `monster_spawnlist` (\r\n");
			fw.write("  `uid` int(10) NOT NULL,\r\n");
			fw.write("  `name` varchar(50) NOT NULL DEFAULT '',\r\n");
			fw.write("  `monster` varchar(50) NOT NULL DEFAULT '',\r\n");
			fw.write("  `random` enum('true','false') NOT NULL DEFAULT 'true',\r\n");		
			fw.write("  `count` int(10) unsigned NOT NULL,\r\n");
			fw.write("  `loc_size` int(10) unsigned NOT NULL,\r\n");
			fw.write("  `spawn_x` int(10) unsigned NOT NULL DEFAULT '0',\r\n");
			fw.write("  `spawn_y` int(10) unsigned NOT NULL DEFAULT '0',\r\n");
			fw.write("  `spawn_map` varchar(255) NOT NULL DEFAULT '',\r\n");
			fw.write("  `re_spawn_min` int(10) unsigned NOT NULL DEFAULT '60',\r\n");
			fw.write("  `re_spawn_max` int(10) unsigned NOT NULL DEFAULT '60',\r\n");
			fw.write("  PRIMARY KEY (`uid`)\r\n");
			fw.write(") ENGINE=MyISAM DEFAULT CHARSET=utf8;\r\n\r\n");
			fw.write("-- ----------------------------\r\n");
			fw.write("-- Records of monster_spawnlist\r\n");
			fw.write("-- ----------------------------\r\n");
			
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM monster_spawnlist;");
			rs = st.executeQuery();
			while (rs.next()) {			
				fw.write(String.format("INSERT INTO `monster_spawnlist` VALUES ('%d', '%s', '%s', '%s', '%d', '%d', '%d', '%d', '%s', '%d', '%d');\r\n",
						uid++, rs.getString("name"), rs.getString("monster"), rs.getString("random"), rs.getInt("count"), rs.getInt("loc_size"), rs.getInt("spawn_x"), rs.getInt("spawn_y"),
						rs.getString("spawn_map"), rs.getInt("re_spawn_min"), rs.getInt("re_spawn_max")));
			}
			
			// 객체 닫기
			fw.close();
			
			TimeLine.end();
		} catch (Exception e) {
			lineage.share.System.println("monster_spawnlist.sql 파일 생성 실패: " + e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
}
