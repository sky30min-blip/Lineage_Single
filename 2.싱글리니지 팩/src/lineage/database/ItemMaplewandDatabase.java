package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemMaplewand;
import lineage.bean.database.Poly;
import lineage.share.TimeLine;
import lineage.util.Util;

public class ItemMaplewandDatabase {

	static private List<ItemMaplewand> list;
	
	static public void init(Connection con){
		TimeLine.start("ItemMaplewandDatabase..");
		
		list = new ArrayList<ItemMaplewand>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_maplewand");
			rs = st.executeQuery();
			while(rs.next()){
				Poly poly = PolyDatabase.getName(rs.getString("name"));
				if(poly == null)
					continue;
				
				ItemMaplewand i = new ItemMaplewand();
				i.setName(poly.getName());
				i.setPoly(poly);
				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemMaplewandDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 소나무막대에서 호출해서 사용함.
	 *  : 디비에 정의된 몬스터들중 하나를 선택하여 그값을 참고로 몬스터 객체를 만들고 리턴함.
	 * @return
	 */
	static public Poly randomPoly(){
		ItemMaplewand i = list.get(Util.random(0, list.size()-1));
		if(i == null)
			return null;
		
		return i.getPoly();
	}
}
