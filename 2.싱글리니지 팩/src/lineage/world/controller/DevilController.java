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

public class DevilController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static long devilEndTime;
	
	static public void init() {
		TimeLine.start("악마왕의영토컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
		devilEndTime = 0L;
		
		TimeLine.end();
	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int playSec = GmEventSettings.getPlayTimeSeconds(GmEventSettings.DEVIL, Lineage.devil_play_time);

		if (GmEventSettings.isEnabled(GmEventSettings.DEVIL)) {
			for (TeamBattleTime tebeTime : Lineage.devil_dungeon_time_list) {
				if (!isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min) {
					isOpen = true;
					devilEndTime = time + (1000L * playSec);
					sendMessage();
				}
			}
		}
		
		// 악마왕의 영토 종료.
		if (isOpen && devilEndTime > 0 && devilEndTime < time) {
			isOpen = false;
			sendMessage();
		}
	}
	
	static public void sendMessage() {
		if (!GmEventSettings.isEnabled(GmEventSettings.DEVIL))
			return;
		if (isOpen) {
			String msg = "\\fY     ***** 악마왕의 영토로 가는길이 열렸습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		} else {
			String msg = "\\fY     ***** 악마왕의 영토로 가는길이 닫혔습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
}
