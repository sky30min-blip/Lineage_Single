package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lineage.bean.database.Dungeon;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public final class TimeDungeonDatabase {
	static private Calendar calendar;
	static private List<Integer> list;
	
	static public void init(Connection con){
		TimeLine.start("TimeDungeonDatabase..");
		calendar = Calendar.getInstance();
		list = new ArrayList<Integer>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = con.prepareStatement("SELECT * FROM time_dungeon");
			rs = st.executeQuery();
			
			while(rs.next())
				list.add(rs.getInt("map"));
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", TimeDungeonDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void reload(){
		TimeLine.start("time_dungeon 테이블 리로드 완료 - ");
		
		list.clear();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM time_dungeon");
			rs = st.executeQuery();
			
			while(rs.next())
				list.add(rs.getInt("map"));
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", TimeDungeonDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 맵번호로 시간제 던전인지 체크하는 함수
	 * 2017-10-07
	 * by all-night
	 */
	static public boolean isTimeDungeon(int map){
		for (int m : list) {
			if (map == m)
				return true;
		}
		return false;
	}
	
	/**
	 * 시간제 던전의 이용시간을 모두 사용하였을 경우 처리하는 함수
	 * 2017-10-09
	 * by all-night
	 */
	static public void isTimeDungeonFinal(PcInstance pc, int type){
		if (pc != null && !pc.isWorldDelete() && !pc.isDead() && !pc.isLock()) {
			switch (type) {
			// 기란감옥
			case 0:
				ChattingController.toChatting(pc, "기란감옥 이용시간을 모두 사용하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(pc, 
						String.format("던전 이용시간은 %s %d시 초기화 됩니다.", 
								Lineage.giran_dungeon_inti_time < 12 ? "오전" : "오후", 
								Lineage.giran_dungeon_inti_time > 12 ? Lineage.giran_dungeon_inti_time - 12 : Lineage.giran_dungeon_inti_time), 
								Lineage.CHATTING_MODE_MESSAGE);
				break;
			}
			
			if (pc.isAutoHunt) {
				pc.endAutoHunt(false, false);
			}
			
			int[] loc = Lineage.getHomeXY();
			pc.toPotal(loc[0], loc[1],loc[2]);
		}

	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int sec = date.getSeconds();
		
		// 기란감옥 이용시간 초기화
		if (hour == Lineage.giran_dungeon_inti_time && min == 0 && sec == 0)
			resetGiranDungeonTime();
		
		// 기란감옥 초기화 주문서 사용횟수 초기화
		if (hour == Lineage.giran_dungeon_reset_hour && min == 0 && sec == 0)
			resetGiranDungeonScrollCount();
		
		if (hour == Lineage.giran_dungeon_reset_hour && min == 0 && sec == 0)
			resetAutoTime();
		
		if (hour == Lineage.giran_dungeon_reset_hour && min == 0 && sec == 0)
			resetCheck();
		if (hour == Lineage.giran_dungeon_reset_hour && min == 0 && sec == 0)
			resetauto();
		if (hour == Lineage.giran_dungeon_reset_hour && min == 0 && sec == 0)
			Lineage.init(true);

	
	}
	
	static public void resetQuest() {
		
		AccountDatabase.updateRQuestCount();
		AccountDatabase.updateRQuestKill();
		AccountDatabase.updateRQuest();
		
		
		for (PcInstance pc : World.getPcList()){
			pc.setRadomQuest(0);
			pc.setRandomQuestkill(0);
			pc.setRandomQuestCount(0);
		}
	}
	static public void resetCheck() {
		
		AccountDatabase.updateDayc();
		AccountDatabase.updateDaycheck2();
		
		for (PcInstance pc : World.getPcList()){
			pc.setDaycheck(0);
			pc.setDayptime(0);
		}
		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("[알림] 컨텐츠 이용횟수가 초기화 되었습니다")));
	}
	static public void resetGiranDungeonTime() {
		CharactersDatabase.updateGiranDungeonTime();
		
		for (PcInstance pc : World.getPcList())
			pc.setGiran_dungeon_time(Lineage.giran_dungeon_time);
		
		
	}
	static public void resetAutoTime() {
		CharactersDatabase.updateAutoCount();
		
		for (PcInstance pc : World.getPcList())
			pc.auto_count = 0;
		
		
	}
	
	static public void resetauto() {
		
		CharactersDatabase.updateAutoHuneTime();
		
		for (PcInstance pc : World.getPcList()) {
			pc.auto_hunt_time = Lineage.auto_hunt_time;
			pc.auto_hunt_account_time = Lineage.auto_hunt_time;
		}

	
	}
	static public void resetGiranDungeonScrollCount() {
		CharactersDatabase.updateGiranDungeonScrollCount();
		
		for (PcInstance pc : World.getPcList())
			pc.giran_dungeon_count = 0;
		

	}
	


	static public void toMovingDungeon(object o) {
		if(o instanceof PcInstance) {
			PcInstance pc = (PcInstance)o;
			
			if (pc.isAutoHunt) {
				return;
			}
			
			Dungeon d = DungeonDatabase.find(o);
			
			if(d != null) {
				List<ItemInstance> search_list = new ArrayList<ItemInstance>();
				// 성공적으로 텔레포트를 이행할지를 판단용으로 사용하는 변수.
				boolean tel = true;
				// 아이템이 필요한 던전일경우 확인하기.
				if(d.getItemCount()>0 && o instanceof PcInstance) {
					// 인벤에 존재하는지 확인해야하므로 false로 기본 설정.
					tel = false;
					// 인벤에 가지고있는 아이템목록에서 해당하는 아이템 전체 불러오기.
					o.getInventory().findDbNameId(d.getItemNameid(), search_list);
					if(d.getItemNameid()==954){
						// 여관키 일경우 전체 순회하기
//						for( ItemInstance item : search_list ){
							// 해당 여관키와 연결된 현재 방이 존재하는지 확인.
							// 근데 어떤 여관인지 확인도 해야되네.. 좌표 확인해야할듯.
	//						tel = InnController::isRoom(o, *p);
	//						if(tel)
	//							break;
//						}
					}else{
						// 그외에는 1개 이상일경우 무조건 이동.
						tel = search_list.size()>0;
					}
				}
				if(tel) {
					o.setHeading(d.getGotoH());
					o.toPotal(d.getGotoX(), d.getGotoY(), d.getGotoM());
				}
			}
		}
	}
	
}
