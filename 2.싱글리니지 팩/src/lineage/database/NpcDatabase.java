package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Npc;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public final class NpcDatabase {

	static private List<Npc> list;

	static public void init(Connection con) {
		TimeLine.start("NpcDatabase..");

		list = new ArrayList<Npc>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM npc");
			rs = st.executeQuery();
			while (rs.next()) {
				Npc n = new Npc();
				n.setName(rs.getString("name"));
				n.setType(rs.getString("type"));
				n.setNameId(rs.getString("nameid"));
				n.setGfx(rs.getInt("gfxid"));
				n.setGfxMode(rs.getInt("gfxMode"));
				n.setHp(rs.getInt("hp"));
				n.setLawful(rs.getInt("lawful"));
				n.setLight(rs.getInt("light"));
				n.setAi(rs.getString("ai").equalsIgnoreCase("true"));
				n.setAreaAtk(rs.getInt("areaatk"));
				n.setArrowGfx(rs.getInt("arrowGfx"));

				// 라우풀 값 확인.
				if (n.getLawful() < Lineage.CHAOTIC)
					n.setLawful(n.getLawful() + Lineage.NEUTRAL);

				try {
					StringBuffer sb = new StringBuffer();
					StringTokenizer stt = new StringTokenizer(rs.getString("nameid"), " $ ");
					while (stt.hasMoreTokens())
						sb.append(stt.nextToken());
					n.setNameIdNumber(Integer.valueOf(sb.toString().trim()));
				} catch (Exception e) {
				}

				list.add(n);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", NpcDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("npc 테이블 리로드 - ");

		synchronized (list) {
			list.clear();
			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM npc");
				rs = st.executeQuery();
				while (rs.next()) {
					Npc n = new Npc();
					n.setName(rs.getString("name"));
					n.setType(rs.getString("type"));
					n.setNameId(rs.getString("nameid"));
					n.setGfx(rs.getInt("gfxid"));
					n.setGfxMode(rs.getInt("gfxMode"));
					n.setHp(rs.getInt("hp"));
					n.setLawful(rs.getInt("lawful"));
					n.setLight(rs.getInt("light"));
					n.setAi(rs.getString("ai").equalsIgnoreCase("true"));
					n.setAreaAtk(rs.getInt("areaatk"));
					n.setArrowGfx(rs.getInt("arrowGfx"));

					// 라우풀 값 확인.
					if (n.getLawful() < Lineage.CHAOTIC)
						n.setLawful(n.getLawful() + Lineage.NEUTRAL);

					try {
						StringBuffer sb = new StringBuffer();
						StringTokenizer stt = new StringTokenizer(rs.getString("nameid"), " $ ");
						while (stt.hasMoreTokens())
							sb.append(stt.nextToken());
						n.setNameIdNumber(Integer.valueOf(sb.toString().trim()));
					} catch (Exception e) {
					}

					list.add(n);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", NpcDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}

		TimeLine.end();
		
		NpcShopDatabase.reload();
		NpcSpawnlistDatabase.reload();
	}

	/**
	 * 이름으로 원하는 객체 찾기.
	 * 
	 * @param name
	 * @return
	 */
	static public Npc find(String name) {
		synchronized (list) {
			for (Npc n : list) {
				if (n.getName().equalsIgnoreCase(name))
					return n;
			}
			return null;
		}
	}

	static public Npc findNameid(String nameid) {
		synchronized (list) {
			for (Npc n : list) {
				if (n.getNameId().equalsIgnoreCase(nameid))
					return n;
			}
			return null;
		}
	}

	static public int getSize() {
		synchronized (list) {
			return list.size();
		}
	}

	static public List<Npc> getList() {
		synchronized (list) {
			return list;
		}
	}
}
