package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.share.TimeLine;

public final class ServerReloadDatabase {
	public static String manager_character_id;
	public static String manager_kakao_talk;
	public static String manager_nate_on;

	static public void init(Connection con) {
		TimeLine.start("ServerOptionDatabase..");

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM server_reload LIMIT 1");
			rs = st.executeQuery();
			
			if (rs.next()) {
				manager_character_id = rs.getString("manager_character_id");
				manager_kakao_talk = rs.getString("manager_kakao_talk");
				manager_nate_on = rs.getString("manager_nate_on");
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ServerReloadDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void reLoad() {
		TimeLine.start("server_reload 테이블 리로드 완료 - ");

		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM server_reload LIMIT 1");
			rs = st.executeQuery();
			
			if (rs.next()) {
				manager_character_id = rs.getString("manager_character_id");
				manager_kakao_talk = rs.getString("manager_kakao_talk");
				manager_nate_on = rs.getString("manager_nate_on");
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ServerReloadDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();
	}

}
