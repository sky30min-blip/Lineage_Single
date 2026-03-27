package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemPinewand;
import lineage.bean.database.Monster;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.instance.MonsterInstance;

public class ItemPinewandDatabase {

	static private List<ItemPinewand> list;
	
	static public void init(Connection con){
		TimeLine.start("ItemPinewandDatabase..");
		
		list = new ArrayList<ItemPinewand>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_pinewand");
			rs = st.executeQuery();
			while(rs.next()){
				Monster monster = MonsterDatabase.find(rs.getString("name"));
				if(monster == null)
					continue;
				
				ItemPinewand i = new ItemPinewand();
				i.setName(monster.getName());
				i.setMonster(monster);
				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemPinewandDatabase.class.toString());
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
	static public MonsterInstance newPineWandMonsterInstance(){
		ItemPinewand i = list.get(Util.random(0, list.size()-1));
		if(i == null)
			return null;
		
		return MonsterSpawnlistDatabase.newInstance( i.getMonster() );
	}
	
	static public List<ItemPinewand> getList() {
		return list;
	}
	
}
