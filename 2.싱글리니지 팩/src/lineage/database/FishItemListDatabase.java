package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.FishList;
import lineage.share.TimeLine;

public final class FishItemListDatabase {

	static private List<FishList> list;

	static public void init(Connection con) {
		TimeLine.start("FishItemListDatabase..");

		list = new ArrayList<FishList>();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM fishing_item_list");
			rs = st.executeQuery();
			while (rs.next()) {
				FishList fishList = new FishList();
				fishList.setItemName(rs.getString("item_name"));
				fishList.setItemBless(rs.getInt("bless"));
				fishList.setItemEnchant(rs.getInt("enchant"));
				fishList.setItemCountMin(rs.getInt("min_count"));
				fishList.setItemCountMax(rs.getInt("max_count"));

				list.add(fishList);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", FishItemListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("FishItemListDatabase 테이블 리로드 완료 - ");

		list.clear();

		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM fishing_item_list");
			rs = st.executeQuery();
			while (rs.next()) {
				FishList fishList = new FishList();
				fishList.setItemName(rs.getString("item_name"));
				fishList.setItemBless(rs.getInt("bless"));
				fishList.setItemEnchant(rs.getInt("enchant"));
				fishList.setItemCountMin(rs.getInt("min_count"));
				fishList.setItemCountMax(rs.getInt("max_count"));

				list.add(fishList);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", FishItemListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();
	}

	static public List<FishList> getFishList() {
		return list;
	}

}
