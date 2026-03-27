package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Drop;
import lineage.bean.database.Monster;
import lineage.share.TimeLine;

public final class MonsterDropDatabase {
	// 전체 드랍리스트
	static private List<Drop> drop_list;

	static public void init(Connection con) {
		TimeLine.start("MonsterDropDatabase..");

		drop_list = new ArrayList<Drop>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_drop");
			rs = st.executeQuery();
			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster_name"));
				if (m != null) {
					Drop d = new Drop();

					d.setName(rs.getString("name"));
					d.setMonName(rs.getString("monster_name"));
					d.setItemName(rs.getString("item_name"));
					d.setItemBress(rs.getInt("item_bress"));
					d.setItemEn(rs.getInt("item_en"));
					d.setCountMin(rs.getInt("count_min"));
					d.setCountMax(rs.getInt("count_max"));
					d.setChance(Double.valueOf(rs.getString("chance")) * 0.01);

					m.getDropList().add(d);
					drop_list.add(d);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterDropDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public void reload() {
		TimeLine.start("monster_drop 테이블 리로드 완료 - ");
		
		synchronized (drop_list) {
			PreparedStatement st = null;
			ResultSet rs = null;
			Connection con = null;
			
			drop_list.clear();
			
			try {
				con = DatabaseConnection.getLineage();
				st = con.prepareStatement("SELECT * FROM monster");
				rs = st.executeQuery();
				while (rs.next()) {
					Monster m = MonsterDatabase.find(rs.getString("name"));
					
					if (m != null)
						m.getDropList().clear();
				}
				
				st.close();
				rs.close();
				
				st = con.prepareStatement("SELECT * FROM monster_drop");
				rs = st.executeQuery();
				while (rs.next()) {
					Monster m = MonsterDatabase.find(rs.getString("monster_name"));
					if (m != null) {
						Drop d = new Drop();

						d.setName(rs.getString("name"));
						d.setMonName(rs.getString("monster_name"));
						d.setItemName(rs.getString("item_name"));
						d.setItemBress(rs.getInt("item_bress"));
						d.setItemEn(rs.getInt("item_en"));
						d.setCountMin(rs.getInt("count_min"));
						d.setCountMax(rs.getInt("count_max"));
						d.setChance(rs.getDouble("chance") * 0.01);

						m.getDropList().add(d);
						drop_list.add(d);
					}
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : reload()\r\n", MonsterDropDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}

		TimeLine.end();
	}

	/**
	 * 아이템이름을 드랍하는 드랍객체 찾아서 리턴.
	 * 
	 * @param itemName
	 * @return
	 */
	public static List<Drop> find(String itemName) {
		List<Drop> list = new ArrayList<Drop>();
		for (Monster m : MonsterDatabase.getList()) {
			for (Drop d : m.getDropList()) {
				if (d.getItemName().indexOf(itemName) >= 0) {
					list.add(d);
					break;
				}
			}
		}
		return list;
	}
	
	static public List<Drop> getDropList() {
		return new ArrayList<Drop>(drop_list);
	}
}
