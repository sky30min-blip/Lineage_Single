package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

import lineage.bean.database.TeamBattleTime;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class ExpMarbleController {
	static public void resetCount() {
		for (PcInstance pc : World.getPcList()) {
			try {
				pc.setExp_marble_save_count(0);
				pc.setExp_marble_use_count(0);
			} catch (Exception e) {
				
			}
		}
		
		PreparedStatement st = null;
		Connection con = null;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET 경험치저장구슬_사용횟수=0, 경험치구슬_사용횟수=0");
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : resetCount()\r\n", ExpMarbleController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
		
		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("[알림] 경험치 저장 구슬 사용 횟수가 초기화 되었습니다.")));
	}
	
	static public void resetCount(object o, String name) {
		PcInstance pc = World.findPc(name);
		
		try {
			if (pc != null) {
				pc.setExp_marble_save_count(0);
				pc.setExp_marble_use_count(0);
				ChattingController.toChatting(pc, "\\fR경험치 저장 구슬 사용 횟수가 초기화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {

		}

		PreparedStatement st = null;
		Connection con = null;
		int result = 0;
		
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("UPDATE characters SET 경험치저장구슬_사용횟수=0, 경험치구슬_사용횟수=0 WHERE LOWER(name)=?");
			st.setString(1, name);
			result = st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : resetCount(object o, String name)\r\n", ExpMarbleController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st);
		}
		
		if (pc == null && result == 0) {
			ChattingController.toChatting(o, String.format("'%s' 캐릭터는 존재하지 않습니다.", name), Lineage.CHATTING_MODE_MESSAGE);
		} else {
			ChattingController.toChatting(o, String.format("'%s' 캐릭터 경험치 저장 구슬 횟수 초기화 완료.", name), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	static public void toTimer(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int sec = date.getSeconds();
		
		for (TeamBattleTime tbt : Lineage.exp_marble_time_list) {
			if (tbt.getHour() == hour && tbt.getMin() == min && sec == 0) {
				resetCount();
			}
		}
	}
}
