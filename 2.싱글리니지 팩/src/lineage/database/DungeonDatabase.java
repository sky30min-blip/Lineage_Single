package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Dungeon;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.object;

public final class DungeonDatabase {

	static private List<Dungeon> list;

	static public void init(Connection con) {
		TimeLine.start("DungeonDatabase..");

		list = new ArrayList<Dungeon>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM dungeon");
			rs = st.executeQuery();
			while (rs.next()) {
				Dungeon d = new Dungeon();
				d.setUid(rs.getInt(1));
				d.setName(rs.getString(2));
				d.setLocX(rs.getInt(3));
				d.setLocY(rs.getInt(4));
				d.setLocM(rs.getInt(5));
				d.setGotoX(rs.getInt(6));
				d.setGotoY(rs.getInt(7));
				d.setGotoM(rs.getInt(8));
				d.setGotoH(rs.getInt(9));
				d.setItemNameid(rs.getInt(10));
				d.setItemCount(rs.getInt(11));
				// 등록
				list.add(d);
				if (Lineage.server_version > 138)
					// 맵 필드값 변경.
					World.set_map(d.getLocX(), d.getLocY(), d.getLocM(), 127);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", DungeonDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public Dungeon find(final int x, final int y, final int map) {
		for (Dungeon d : list) {
			if (d.getLocM() == map && d.getLocX() == x && d.getLocY() == y)
				return d;
		}
		return null;
	}

	static public Dungeon find(object o) {
		return find(o.getX(), o.getY(), o.getMap());
	}

	static public Dungeon find(int uid) {
		for (Dungeon d : list) {
			if (d.getUid() == uid)
				return d;
		}
		return null;
	}

	static public int getSize() {
		return list.size();
	}
}
