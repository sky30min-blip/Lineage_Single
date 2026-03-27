package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.GmTeleport;
import lineage.share.TimeLine;

public final class GmTeleportDatabase {
	static private List<GmTeleport> list;

	static public void init(Connection con) {
		TimeLine.start("GmTeleportDatabase..");
		list = new ArrayList<GmTeleport>();

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = con.prepareStatement("SELECT * FROM gm_teleport");
			rs = st.executeQuery();

			while (rs.next()) {
				if (rs.getString("name").length() > 1 && rs.getInt("x") > 0 && rs.getInt("y") > 0) {
					GmTeleport gt = new GmTeleport();
					gt.setUid(rs.getInt("uid"));
					gt.setName(rs.getString("name"));
					gt.setX(rs.getInt("x"));
					gt.setY(rs.getInt("y"));
					gt.setMap(rs.getInt("map"));
					list.add(gt);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", GmTeleportDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("gm_teleport 테이블 리로드 - ");
		
		synchronized (list) {
			list = new ArrayList<GmTeleport>();

			list.clear();
			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM gm_teleport");
				rs = st.executeQuery();

				while (rs.next()) {
					if (rs.getString("name").length() > 1 && rs.getInt("x") > 0 && rs.getInt("y") > 0) {
						GmTeleport gt = new GmTeleport();
						gt.setUid(rs.getInt("uid"));
						gt.setName(rs.getString("name"));
						gt.setX(rs.getInt("x"));
						gt.setY(rs.getInt("y"));
						gt.setMap(rs.getInt("map"));
						list.add(gt);
					}
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", GmTeleportDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
		TimeLine.end();
	}
	
	static public List<GmTeleport> getList() {
		synchronized (list) {
			return list;
		}
	}
	
	static public GmTeleport find(int uid) {
		synchronized (list) {
			for (GmTeleport gt : list) {
				if (gt.getUid() == uid)
					return gt;
			}
		}
		
		return null;
	}
}
