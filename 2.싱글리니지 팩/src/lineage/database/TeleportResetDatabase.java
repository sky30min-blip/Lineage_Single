package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.TeleportHome;
import lineage.bean.database.TeleportHome.Location;
import lineage.bean.database.TeleportHome.LocationRnd;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.object;

public class TeleportResetDatabase {

	static private List<TeleportHome> list;

	static public void init(Connection con) {
		TimeLine.start("TeleportResetDatabase..");

		list = new ArrayList<TeleportHome>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM teleport_reset");
			rs = st.executeQuery();
			while (rs.next()) {
				TeleportHome g = new TeleportHome();
				g.setName(rs.getString("name"));
				g.setClassType(rs.getInt("classtype"));
				g.setX1(rs.getInt("x1"));
				g.setX2(rs.getInt("x2"));
				g.setY1(rs.getInt("y1"));
				g.setY2(rs.getInt("y2"));
				g.setMap(rs.getInt("map"));
				g.appendLocation(rs.getString("goto1"));
				g.appendLocation(rs.getString("goto2"));
				g.appendLocation(rs.getString("goto3"));
				g.appendLocation(rs.getString("goto4"));
				g.appendLocation(rs.getString("goto5"));
				g.appendLocation(rs.getString("goto6"));
				g.appendLocationRnd(rs.getString("goto_rnd1"));

				list.add(g);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", TeleportResetDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	/**
	 * 리스존 확인하여 좌표 변경하는 함수. : home 값을 변경함.
	 * 
	 * @param o
	 */
	static public boolean toLocation(object o) {
		//
		if (PluginController.init(TeleportResetDatabase.class, "toLocation", o) != null)
			return true;

		TeleportHome find = null;
		Location l = null;
		LocationRnd lr = null;

		// 검색
		for (TeleportHome g : list) {
			if (g.getX1() == 0) {
				if (g.getMap() == o.getMap()) {
					if (g.getClassType() == -1) {
						find = g;
						break;
					}
					if (g.getClassType() == o.getClassType()) {
						find = g;
						break;
					}
				}
			} else {
				if ((o.getX() < g.getX2()) && (o.getX() > g.getX1()) && (o.getY() < g.getY2()) && (o.getY() > g.getY1())) {
					find = g;
					break;
				}
			}
		}

		// 처리
		if (find != null) {
			if (find.getListGoto().size() > 0)
				l = find.getListGoto().get(Util.random(0, find.getListGoto().size() - 1));
			if (find.getListGotoRnd().size() > 0)
				lr = find.getListGotoRnd().get(Util.random(0, find.getListGotoRnd().size() - 1));
			
			if ((l == null && lr == null) || (Lineage.is_restart_giran_home && o.getMap() != 70)) {
				int[] loc = Lineage.getHomeXY();
				o.setHomeX(loc[0]);
				o.setHomeY(loc[1]);
				o.setHomeMap(loc[2]);
				return true;
			}
		}
		if (l != null) {
			o.setHomeX(l.x);
			o.setHomeY(l.y);
			o.setHomeMap(l.map);
			return true;
		}
		if (lr != null) {
			o.setHomeX(Util.random(lr.x1, lr.x2));
			o.setHomeY(Util.random(lr.y1, lr.y2));
			o.setHomeMap(lr.map);
			return true;
		}

		return false;
	}

}
