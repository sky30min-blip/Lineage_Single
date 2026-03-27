package lineage.world.controller;

import java.util.Calendar;
import java.util.Date;

import lineage.bean.database.TeamBattleTime;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;

public class PenguinHuntingController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static long phuntEndTime;
	
	static public void init() {
		TimeLine.start("펭귄 사냥터 컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
		phuntEndTime = 0L;
		
		TimeLine.end();
	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		
		for (TeamBattleTime tebeTime : Lineage.phunt_dungeon_time_list) {
			// 지옥 시작.
			if (!isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min) {
				isOpen = true;
				phuntEndTime = time + (1000 * Lineage.phunt_play_time);
				sendMessage();
			}
		}
		
		// 지옥 종료.
		if (isOpen && phuntEndTime > 0 && phuntEndTime < time) {
			isOpen = false;
			sendMessage();
		}
	}
	
	static public void sendMessage() {
		if (isOpen) {
			String msg = "\\fY      ***** 펭귄 서식지로 가는길이 열렸습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		} else {
			String msg = "\\fY      ***** 펭귄 서식지로 가는길이 닫혔습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
}
