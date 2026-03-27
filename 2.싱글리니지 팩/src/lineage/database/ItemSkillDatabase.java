package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemSkill;
import lineage.share.TimeLine;

public class ItemSkillDatabase {

	static private List<ItemSkill> list;
	
	static public void init(Connection con){
		TimeLine.start("ItemSkillDatabase..");
		
		list = new ArrayList<ItemSkill>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_skill");
			rs = st.executeQuery();
			while(rs.next()){
				ItemSkill i = new ItemSkill();
				i.setName(rs.getString("name"));
				i.setItem(rs.getString("item"));
				i.setSkillUid(rs.getInt("skill_uid"));
				i.setEnLevel(rs.getInt("enchant_level"));
				i.setDefaultProbability(rs.getInt("default_probability"));
				i.setAddEnchantProbability(rs.getInt("add_enchant_probability"));
				i.setSetInt(rs.getString("set_int").equalsIgnoreCase("true"));
				i.setEffectTarget(rs.getString("effect_target").equalsIgnoreCase("target"));
				i.setRateDmg(rs.getInt("대미지 조절") * 0.01);
				
				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemSkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 
	 * @param
	 * @return
	 * 2017-09-03
	 * by all_night.
	 */
	static public void reload(){
		TimeLine.start("item_skill 테이블 리로드 완료 - ");
		
		list.clear();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM item_skill");
			rs = st.executeQuery();
			while(rs.next()){
				ItemSkill i = new ItemSkill();
				i.setName(rs.getString("name"));
				i.setItem(rs.getString("item"));
				i.setSkillUid(rs.getInt("skill_uid"));
				i.setEnLevel(rs.getInt("enchant_level"));
				i.setDefaultProbability(rs.getInt("default_probability"));
				i.setAddEnchantProbability(rs.getInt("add_enchant_probability"));
				i.setSetInt(rs.getString("set_int").equalsIgnoreCase("true"));
				i.setEffectTarget(rs.getString("effect_target").equalsIgnoreCase("target"));
				i.setRateDmg(rs.getInt("대미지 조절") * 0.01);
				
				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemSkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		TimeLine.end();
	}
	
	static public ItemSkill find(String item){
		for( ItemSkill is : list ){
			if(is.getItem().equalsIgnoreCase(item))
				return is;
		}
		return null;
	}
	
}
