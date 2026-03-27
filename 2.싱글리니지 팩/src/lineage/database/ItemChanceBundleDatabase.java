package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemChanceBundle;
import lineage.share.TimeLine;

public class ItemChanceBundleDatabase {
	static private List<ItemChanceBundle> list;
	
	static public void init(Connection con){
		TimeLine.start("ItemChanceBundleDatabase..");		
		//
		list = new ArrayList<ItemChanceBundle>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_chance_bundle");
			rs = st.executeQuery();
			while(rs.next()){
				ItemChanceBundle ib = new ItemChanceBundle();
				ib.setName(rs.getString("bundle_item_name"));
				ib.setItem(rs.getString("item_name"));
				ib.setItemBless(rs.getInt("item_bless"));
				ib.setItemEnchant(rs.getInt("item_enchant"));
				ib.setItemCountMin(rs.getInt("item_count_min"));
				ib.setItemCountMax(rs.getInt("item_count_max"));
				ib.setCount(rs.getInt("count"));
				ib.setDefine(rs.getString("item_define").equalsIgnoreCase("true"));
				ib.setItemChance(Double.valueOf(rs.getString("item_chance").trim().equalsIgnoreCase("") ? 100 : Double.valueOf(rs.getString("item_chance").trim())) * 0.01);

				list.add(ib);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemChanceBundleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 해당하는 아이템을 찾아서 목록을 만든후 리턴함.
	 * @param r_list
	 * @param name
	 */
	static public void find(List<ItemChanceBundle> r_list, String name){
		r_list.clear();
		
		for(ItemChanceBundle ib : list){
			if(ib.getName().equalsIgnoreCase(name))
//			if(ib.getCount() > 0 ){
				r_list.add(ib);
//			}
			
		}
	}
	static public void updateCount(String name) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE  item_chance_bundle  SET count=  count + 1  WHERE item_name=? ");
			st.setString(1, name);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLocation(Connection con, String name, int x, int y, int map)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st);
		}
	}

	static public void updateCh(String name) {
		Connection con = null;
		PreparedStatement st = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE  item_chance_bundle  SET item_chance = 0  WHERE item_name = ? ");
			st.setString(1, name);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLocation(Connection con, String name, int x, int y, int map)\r\n", CharactersDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con,st);
		}
	}

	/**
	 * 
	 * @param
	 * @return
	 * 2017-09-03
	 * by all_night.
	 */
	static public void reload() {
		TimeLine.start("item_chance_bundle 테이블 리로드 완료 - ");
		
		list.clear();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM item_chance_bundle");
			rs = st.executeQuery();
			while(rs.next()){
				ItemChanceBundle ib = new ItemChanceBundle();
				ib.setName(rs.getString("bundle_item_name"));
				ib.setItem(rs.getString("item_name"));
				ib.setItemBless(rs.getInt("item_bless"));
				ib.setItemEnchant(rs.getInt("item_enchant"));
				ib.setItemCountMin(rs.getInt("item_count_min"));
				ib.setItemCountMax(rs.getInt("item_count_max"));
				ib.setDefine(rs.getString("item_define").equalsIgnoreCase("true"));
				ib.setCount(rs.getInt("count"));
				ib.setItemChance(Double.valueOf(rs.getString("item_chance").trim().equalsIgnoreCase("") ? 100 : Double.valueOf(rs.getString("item_chance").trim())) * 0.01);
			
				list.add(ib);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", ItemChanceBundleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		TimeLine.end();
	}
}
