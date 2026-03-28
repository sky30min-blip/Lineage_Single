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

public class TreasureHuntController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static long TreasuressEndTime;
	
	static public void init() {
		TimeLine.start("보물찾기컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
		TreasuressEndTime = 0L;
		
		TimeLine.end();
	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		boolean en = GmEventSettings.isEnabled(GmEventSettings.TREASURE);
		int playSec = GmEventSettings.getPlayTimeSeconds(GmEventSettings.TREASURE, Lineage.Treasuress_play_time);

		for (TeamBattleTime tebeTime : Lineage.Treasuress_dungeon_time_list) {
			
			int test = tebeTime.getMin() - 1;
			int sec = date.getSeconds();

			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 0) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fU메티스: 1분뒤 보물찾기가 시작됩니다.")));
			}
			
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 50) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fU메티스: 10초뒤  보물찾기가 시작됩니다.")));
			}
			
			if (en && !isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min) {
				isOpen = true;
				TreasuressEndTime = time + (1000L * playSec);
				sendMessage();
			}
		}

		if (isOpen && TreasuressEndTime > 0 && TreasuressEndTime < time) {
			isOpen = false;
			sendMessage();
		}
	}
	
	static public void sendMessage() {
		if (!GmEventSettings.isEnabled(GmEventSettings.TREASURE))
			return;
		if (isOpen) {
			String msg = "\\fY      ***** 보물찾기가 시작되었습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		} else {
			String msg = "\\fY      ***** 보물찾기가 마감 되었습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
}
