package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemTeambattle;
import lineage.share.TimeLine;

public class TeamBattleDatabase {
	static private List<ItemTeambattle> list;

	static public void init(Connection con){
		TimeLine.start("TeamBattleDatabase..");
		
		list = new ArrayList<ItemTeambattle>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = con.prepareStatement("SELECT * FROM team_battle_item");
			rs = st.executeQuery();
			while (rs.next()) {
				ItemTeambattle ib = new ItemTeambattle();
				ib.setType(rs.getString("type"));
				ib.setItem(rs.getString("item"));
				ib.setItemBless(rs.getInt("bless"));
				ib.setItemEnchant(rs.getInt("enchant"));
				ib.setItemCountMin(rs.getInt("count_min"));
				ib.setItemCountMax(rs.getInt("count_max"));

				list.add(ib);
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", TeamBattleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void reload(){
		TimeLine.start("team_battle_item 테이블 리로드 완료 - ");
		
		list.clear();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM team_battle_item");
			rs = st.executeQuery();
			while(rs.next()){
				ItemTeambattle ib = new ItemTeambattle();
				ib.setType(rs.getString("type"));
				ib.setItem(rs.getString("item"));
				ib.setItemBless(rs.getInt("bless"));
				ib.setItemEnchant(rs.getInt("enchant"));
				ib.setItemCountMin(rs.getInt("count_min"));
				ib.setItemCountMax(rs.getInt("count_max"));
				
				list.add(ib);
			}		
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", TeamBattleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void find(List<ItemTeambattle> tempList, String type){
		tempList.clear();
		
		for(ItemTeambattle ib : list){
			if(ib.getType().equalsIgnoreCase(type))
				tempList.add(ib);
		}
	}
}
