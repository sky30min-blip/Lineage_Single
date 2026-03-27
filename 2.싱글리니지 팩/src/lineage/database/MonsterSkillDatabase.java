package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSkill;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.instance.MonsterInstance;

public class MonsterSkillDatabase {

	static public void init(Connection con) {
		TimeLine.start("MonsterSkillDatabase..");

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_skill ORDER BY uid");
			rs = st.executeQuery();
			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));
				if (m != null) {
					MonsterSkill ms = new MonsterSkill();

					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(rs.getString("monster"));
					ms.setActionNumber(rs.getInt("action_number"));
					ms.setCastGfx(rs.getInt("effect"));
					ms.setRange(rs.getInt("스킬공격범위"));
					ms.setDistance(rs.getInt("스킬사거리"));		
					ms.setType(rs.getString("type"));
					ms.setChance(rs.getInt("chance"));
					ms.setMsg(rs.getString("msg"));
					ms.setSkill(SkillDatabase.find(rs.getInt("skill_uid")));

					if (ms.getSkill() != null && rs.getInt("hpConsume") < 1)
						ms.setHpConsume(ms.getSkill().getHpConsume());
					else
						ms.setHpConsume(rs.getInt("hpConsume"));

					if (ms.getSkill() != null && rs.getInt("mpConsume") < 1)
						ms.setMpConsume(ms.getSkill().getMpConsume());
					else
						ms.setMpConsume(rs.getInt("mpConsume"));

					ms.setMindmg(rs.getInt("dmgMin"));
					ms.setMaxdmg(rs.getInt("dmgMax"));
					ms.setBuffDuration(rs.getInt("duration"));
					if (rs.getString("element").equalsIgnoreCase("none"))
						ms.setElement(Lineage.ELEMENT_NONE);
					else if (rs.getString("element").equalsIgnoreCase("wind"))
						ms.setElement(Lineage.ELEMENT_WIND);
					else if (rs.getString("element").equalsIgnoreCase("water"))
						ms.setElement(Lineage.ELEMENT_WATER);
					else if (rs.getString("element").equalsIgnoreCase("earth"))
						ms.setElement(Lineage.ELEMENT_EARTH);
					else if (rs.getString("element").equalsIgnoreCase("fire"))
						ms.setElement(Lineage.ELEMENT_FIRE);
					else if (rs.getString("element").equalsIgnoreCase("laser"))
						ms.setElement(Lineage.ELEMENT_LASER);
					ms.setOption(rs.getString("option"));

					m.list_skill.add(ms);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterSkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public void reload() {
		TimeLine.start("monster_skill 테이블 리로드 완료 - ");

		for (Monster mon : MonsterDatabase.list)
			mon.list_skill.clear();

		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM monster_skill ORDER BY uid");
			rs = st.executeQuery();
			while (rs.next()) {
				Monster m = MonsterDatabase.find(rs.getString("monster"));
				if (m != null) {
					MonsterSkill ms = new MonsterSkill();

					ms.setUid(rs.getInt("uid"));
					ms.setName(rs.getString("name"));
					ms.setMonster(rs.getString("monster"));
					ms.setActionNumber(rs.getInt("action_number"));
					ms.setCastGfx(rs.getInt("effect"));
					ms.setRange(rs.getInt("스킬공격범위"));
					ms.setDistance(rs.getInt("스킬사거리"));
					ms.setType(rs.getString("type"));
					ms.setChance(rs.getInt("chance"));
					ms.setMsg(rs.getString("msg"));
					ms.setSkill(SkillDatabase.find(rs.getInt("skill_uid")));

					if (ms.getSkill() != null && rs.getInt("hpConsume") < 1)
						ms.setHpConsume(ms.getSkill().getHpConsume());
					else
						ms.setHpConsume(rs.getInt("hpConsume"));

					if (ms.getSkill() != null && rs.getInt("mpConsume") < 1)
						ms.setMpConsume(ms.getSkill().getMpConsume());
					else
						ms.setMpConsume(rs.getInt("mpConsume"));

					ms.setMindmg(rs.getInt("dmgMin"));
					ms.setMaxdmg(rs.getInt("dmgMax"));
					ms.setBuffDuration(rs.getInt("duration"));
					if (rs.getString("element").equalsIgnoreCase("none"))
						ms.setElement(Lineage.ELEMENT_NONE);
					else if (rs.getString("element").equalsIgnoreCase("wind"))
						ms.setElement(Lineage.ELEMENT_WIND);
					else if (rs.getString("element").equalsIgnoreCase("water"))
						ms.setElement(Lineage.ELEMENT_WATER);
					else if (rs.getString("element").equalsIgnoreCase("earth"))
						ms.setElement(Lineage.ELEMENT_EARTH);
					else if (rs.getString("element").equalsIgnoreCase("fire"))
						ms.setElement(Lineage.ELEMENT_FIRE);
					else if (rs.getString("element").equalsIgnoreCase("laser"))
						ms.setElement(Lineage.ELEMENT_LASER);
					ms.setOption(rs.getString("option"));

					m.list_skill.add(ms);

					for (MonsterInstance mon : World.getMonsterList()) {
						if (mon.getMonster().getName().equalsIgnoreCase(m.getName()))
							mon.setMonster(m);
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : reload()\r\n", MonsterSkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}

		TimeLine.end();
	}

}
