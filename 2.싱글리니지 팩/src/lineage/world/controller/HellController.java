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

public class HellController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static long hellEndTime;
	
	static public void init() {
		TimeLine.start("지옥컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
		hellEndTime = 0L;
		
		TimeLine.end();
	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		int nowday = getDayOfWeek ();
		int playSec = GmEventSettings.getPlayTimeSeconds(GmEventSettings.HELL, Lineage.hell_play_time);

		if (GmEventSettings.isEnabled(GmEventSettings.HELL)) {
			if(nowday == 1 || nowday == 7){
				for (TeamBattleTime tebeTime : Lineage.hell_dungeon_time_list2) {
					// 지옥 시작.
					if (!isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min) {
						isOpen = true;
						hellEndTime = time + (1000L * playSec);
						sendMessage();
					}
				}
			}else{
				for (TeamBattleTime tebeTime : Lineage.hell_dungeon_time_list) {
					// 지옥 시작.
					if (!isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min) {
						isOpen = true;
						hellEndTime = time + (1000L * playSec);
						sendMessage();
					}
				}
			}
		}
		
		// 지옥 종료.
		if (isOpen && hellEndTime > 0 && hellEndTime < time) {
			isOpen = false;
			sendMessage();
		}
	}
	
	static public void sendMessage() {
		if (!GmEventSettings.isEnabled(GmEventSettings.HELL))
			return;
		if (isOpen) {
			String msg = "\\fY      ***** 지옥으로 가는길이 열렸습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		} else {
			String msg = "\\fY      ***** 지옥으로 가는길이 닫혔습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
	public static int getDayOfWeek() {
		Calendar rightNow = Calendar.getInstance();
		int day_of_week = rightNow.get(Calendar.DAY_OF_WEEK);
		return day_of_week;
	}
}
