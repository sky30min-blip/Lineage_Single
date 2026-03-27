package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Quest;
import lineage.database.DatabaseConnection;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;
import lineage.world.object.npc.quest.Dilong;
import lineage.world.object.npc.quest.FairyPrincess;
import lineage.world.object.npc.quest.Gatekeeper;

public final class QuestController {

	static private Map<Long, List<Quest>> list;
	// 퀘스트 npc들..
	static private Gatekeeper gatekeeper;		// 은기사마을 문지기.
	static private FairyPrincess fairyprincess;	// 요숲 페어리 프린세스
	static private Dilong dilong;				// 말섬던전1층 디롱.
	
	/**
	 * 초기화 함수.
	 * @param con
	 */
	static public void init(Connection con){
		TimeLine.start("QuestController..");
		
		list = new HashMap<Long, List<Quest>>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_quest ORDER BY objId");
			rs = st.executeQuery();
			while(rs.next()){
				Quest q = new Quest();
				q.setObjectId(rs.getInt("objId"));
				q.setName(rs.getString("name"));
				q.setNpcName(rs.getString("npc_name"));
				q.setQuestAction(rs.getString("quest_action"));
				q.setQuestStep(rs.getInt("quest_step"));
				
				List<Quest> l = list.get(q.getObjectId());
				if(l == null){
					l = new ArrayList<Quest>();
					list.put(q.getObjectId(), l);
				}
				l.add(q);
			}
		} catch (Exception e) {
			lineage.share.System.println(QuestController.class.toString()+" : init(Connection con)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 서버닫힐때 처리하는 디비저장 및 메모리 제거 함수.
	 * @param con
	 */
	static public void close(Connection con){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("DELETE FROM characters_quest");
			st.executeUpdate();
			st.close();
			synchronized (list) {
				for(List<Quest> q_list : list.values()){
					for(Quest q : q_list){
						st = con.prepareStatement("INSERT INTO characters_quest SET objId=?, name=?, npc_name=?, quest_action=?, quest_step=?");
						st.setLong(1, q.getObjectId());
						st.setString(2, q.getName());
						st.setString(3, q.getNpcName());
						st.setString(4, q.getQuestAction());
						st.setInt(5, q.getQuestStep());
						st.executeUpdate();
						st.close();
					}
				}
	
				list.clear();
			}
		} catch(Exception e) {
			lineage.share.System.println(QuestController.class.toString()+" : close(Connection con)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 사용자가 월드에 진입할때 한번 호출됨.
	 * @param pc
	 */
	static public void toWorldJoin(PcInstance pc){
		//
	}
	
	/**
	 * 사용자가 월드를 나갈때 한번 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		//
	}
	
	static public void setGateKeeper(Gatekeeper gk){
		gatekeeper = gk;
	}
	
	static public void setFairyPrincess(FairyPrincess fp){
		fairyprincess = fp;
	}
	
	static public void setDilong(Dilong d){
		dilong = d;
	}
	
	/**
	 * 타이머에서 지속적으로 호출.
	 * @param time
	 */
	static public void toTimer(long time){
		boolean d = false;
		// 수련동굴 확인.
		if(gatekeeper!=null && gatekeeper.isDungeon()){
			d = false;
			for(PcInstance pc : World.getPcList()){
				if(pc.getMap() == 22){
					d = true;
					break;
				}
			}
			gatekeeper.setDungeon(d);
		}
		// 다크엘프 던전 확인.
		if(fairyprincess!=null && fairyprincess.isDungeon()){
			d = false;
			for(PcInstance pc : World.getPcList()){
				if(pc.getMap() == 213){
					d = true;
					break;
				}
			}
			fairyprincess.setDungeon(d);
		}
		// 디롱쪽 언데드 던전 확인.
		if(dilong!=null && dilong.isDungeon()){
			d = false;
			for(PcInstance pc : World.getPcList()){
				if(pc.getMap() == 201){
					d = true;
					break;
				}
			}
			dilong.setDungeon(d);
		}
	}
	
	/**
	 * 퀘스트 객체 찾기.
	 * @param pc
	 * @param action
	 * @return
	 */
	static public Quest find(PcInstance pc, String action){
		List<Quest> list = find(pc);
		if(list != null){
			for(Quest q : list){
				if(q.getQuestAction().equalsIgnoreCase(action))
					return q;
			}
		}
		return null;
	}
	
	static public List<Quest> find(PcInstance pc){
		synchronized (list) {
			return list.get(pc.getObjectId());
		}
	}
	
	/**
	 * 새로운 퀘스트 등록처리 함수.
	 *  : 퀘스트 진행하려는 퀘스트처리 객체가 없을경우 해당 객체를 통해 생성처리 함.
	 * @param pc
	 * @param qi
	 * @return
	 */
	static public Quest newQuest(PcInstance pc, QuestInstance qi, String action){
		Quest q = new Quest();
		q.setObjectId(pc.getObjectId());
		q.setName(pc.getName());
		q.setNpcName(qi==null ? "" : (qi.getNpc()==null ? qi.getName() : qi.getNpc().getName()) );
		q.setQuestAction(action);
		
		List<Quest> q_list = find(pc);
		if(q_list == null){
			q_list = new ArrayList<Quest>();
			synchronized (list) {
				if(!list.containsKey(pc.getObjectId()))
					list.put(pc.getObjectId(), q_list);
			}
		}
		q_list.add(q);
		return q;
	}
	
}
