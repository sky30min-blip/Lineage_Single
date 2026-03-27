package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import lineage.bean.database.Skill;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public final class SkillDatabase {

	static private Map<Integer, Skill> list;
	
	static public void init(Connection con){
		TimeLine.start("SkillDatabase..");
		
		list = new HashMap<Integer, Skill>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM skill");
			rs = st.executeQuery();
			while(rs.next()){
				Skill s = new Skill();
				s.setUid(rs.getInt("마법아이디"));
				s.setName(rs.getString("마법이름"));
				s.setSkillLevel(rs.getInt("사용레벨"));
				s.setSkillNumber(rs.getInt("마법번호"));
				s.setMpConsume(rs.getInt("소모MP"));
				s.setHpConsume(rs.getInt("소모HP"));
				s.setItemConsume(rs.getInt("마법재료아이템아이디"));
				s.setItemConsumeCount(rs.getInt("마법재료필요아이템갯수"));
				s.setBuffDuration(rs.getInt("버프지속시간"));
				s.setMindmg(rs.getInt("최소데미지"));
				s.setMaxdmg(rs.getInt("최대데미지"));
				s.setId(rs.getInt("아이디"));
				s.setCastGfx(rs.getInt("이팩트"));
				s.setRange(rs.getInt("스킬공격범위"));
				s.setDistance(rs.getInt("스킬사거리"));
				s.setLawfulConsume(rs.getInt("라우풀감소"));
				s.setDelay(rs.getInt("delay"));
				s.setLock(rs.getString("if_lock"));
				
				if(s.getSkillLevel() == 1)
					s.setPrice(100);
				else if(s.getSkillLevel() == 2)
					s.setPrice(400);
				else
					s.setPrice(900);
				if(rs.getString("element").equalsIgnoreCase("none"))
					s.setElement( Lineage.ELEMENT_NONE );
				else if(rs.getString("element").equalsIgnoreCase("wind"))
					s.setElement( Lineage.ELEMENT_WIND );
				else if(rs.getString("element").equalsIgnoreCase("water"))
					s.setElement( Lineage.ELEMENT_WATER );
				else if(rs.getString("element").equalsIgnoreCase("earth"))
					s.setElement( Lineage.ELEMENT_EARTH );
				else if(rs.getString("element").equalsIgnoreCase("fire"))
					s.setElement( Lineage.ELEMENT_FIRE );
				else if(rs.getString("element").equalsIgnoreCase("laser"))
					s.setElement( Lineage.ELEMENT_LASER );
				else if(rs.getString("element").equalsIgnoreCase("poison"))
					s.setElement( Lineage.ELEMENT_POISON );
				
				list.put(s.getUid(), s);
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", SkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		// 스킬 디비 로딩 알리기.
		PluginController.init(SkillDatabase.class, "init", con, list);

		TimeLine.end();
	}
	
	static public void reLoadSkill(){
		TimeLine.start("skill 테이블 리로드 완료 - ");
		
		synchronized (list) {
			list.clear();
		}
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM skill");
			rs = st.executeQuery();
			while(rs.next()){
				Skill s = new Skill();
				s.setUid(rs.getInt("마법아이디"));
				s.setName(rs.getString("마법이름"));
				s.setSkillLevel(rs.getInt("사용레벨"));
				s.setSkillNumber(rs.getInt("마법번호"));
				s.setMpConsume(rs.getInt("소모MP"));
				s.setHpConsume(rs.getInt("소모HP"));
				s.setItemConsume(rs.getInt("마법재료아이템아이디"));
				s.setItemConsumeCount(rs.getInt("마법재료필요아이템갯수"));
				s.setBuffDuration(rs.getInt("버프지속시간"));
				s.setMindmg(rs.getInt("최소데미지"));
				s.setMaxdmg(rs.getInt("최대데미지"));
				s.setId(rs.getInt("아이디"));
				s.setCastGfx(rs.getInt("이팩트"));
				s.setRange(rs.getInt("스킬공격범위"));
				s.setDistance(rs.getInt("스킬사거리"));
				s.setLawfulConsume(rs.getInt("라우풀감소"));
				s.setDelay(rs.getInt("delay"));
				s.setLock(rs.getString("if_lock"));
				
				if(s.getSkillLevel() == 1)
					s.setPrice(100);
				else if(s.getSkillLevel() == 2)
					s.setPrice(400);
				else
					s.setPrice(900);
				if(rs.getString("element").equalsIgnoreCase("none"))
					s.setElement( Lineage.ELEMENT_NONE );
				else if(rs.getString("element").equalsIgnoreCase("wind"))
					s.setElement( Lineage.ELEMENT_WIND );
				else if(rs.getString("element").equalsIgnoreCase("water"))
					s.setElement( Lineage.ELEMENT_WATER );
				else if(rs.getString("element").equalsIgnoreCase("earth"))
					s.setElement( Lineage.ELEMENT_EARTH );
				else if(rs.getString("element").equalsIgnoreCase("fire"))
					s.setElement( Lineage.ELEMENT_FIRE );
				else if(rs.getString("element").equalsIgnoreCase("laser"))
					s.setElement( Lineage.ELEMENT_LASER );
				else if(rs.getString("element").equalsIgnoreCase("poison"))
					s.setElement( Lineage.ELEMENT_POISON );
				
				synchronized (list) {
					list.put(s.getUid(), s);
				}
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", SkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		// 스킬 디비 로딩 알리기.
		PluginController.init(SkillDatabase.class, "init", con, list);

		TimeLine.end();
	}
	
	static public Map<Integer, Skill> getList(){
		synchronized (list) {
			return list;
		}	
	}
	
	static public Skill find(final int uid){
		synchronized (list) {
			return list.get(uid);
		}	
	}
	
	static public Skill find(final int level, final int number){
		synchronized (list) {
			for(Skill s : list.values()){
				if(s.getSkillLevel()==level && s.getSkillNumber()==number)
					return s;
			}
			return null;
		}
	}
	
	static public int getSize(){
		synchronized (list) {
			return list.size();
		}
	}
}
