package lineage.world.controller;

import java.util.Calendar;
import java.util.Date;

import lineage.bean.database.TeamBattleTime;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class TimeEventController {
	static private Calendar calendar;
	public static boolean isOpen;
	public static long event_timeEndTime;
	public static int num;
	static public void init() {
		TimeLine.start("타임이벤트컨트롤러..");
		
		calendar = Calendar.getInstance();
		isOpen = false;
		event_timeEndTime = 0L;
		num= 0;
		TimeLine.end();
	}
	
	static public void toTimer(long time) {
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int hour = date.getHours();
		int min = date.getMinutes();
		boolean en = GmEventSettings.isEnabled(GmEventSettings.TIMEEVENT);
		int playSec = GmEventSettings.getPlayTimeSeconds(GmEventSettings.TIMEEVENT, Lineage.time_event_play_time);
		
		for (TeamBattleTime tebeTime : Lineage.time_event_time_list) {
			
			int test = tebeTime.getMin() - 1;
			int sec = date.getSeconds();

			int effect = 0;
			
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 0) {
				num = (int)(Math.random() * 4 +1);
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fU메티스: 안녕하세요 잠시후 타임이벤트를 시작하겠습니다")));
			}
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 30) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fU메티스: 이벤트 추첨 주사위를 던지겠습니다")));
			}
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 40) {
				
				
				for (PcInstance pc : World.getPcList()) {
					
					if(num == 1){
						effect = 3204;
						

						pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, effect), true);
					}
					if(num == 2){
						effect = 3205;
	
						pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, effect), true);
					}
					if(num == 3){
						effect = 3206;
			
						pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, effect), true);
					}
					if(num == 4){
						effect = 3207;

						pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, effect), true);
					}
					if(num == 5){
						effect = 3208;
		
						pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, effect), true);
					}
			
			
				}
			}
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 44) {
				
				
				for (PcInstance pc : World.getPcList()) {
					
					if(num == 1){

						ChattingController.toChatting(pc, String.format("\\fU메티스: 1이 나왔군요 !"), Lineage.CHATTING_MODE_MESSAGE);

					}
					if(num == 2){

						ChattingController.toChatting(pc, String.format("\\fU메티스: 2가 나왔군요 ! "), Lineage.CHATTING_MODE_MESSAGE);

					}
					if(num == 3){

						ChattingController.toChatting(pc, String.format("\\fU메티스: 3이 나왔군요 !"), Lineage.CHATTING_MODE_MESSAGE);

					}
					if(num == 4){

						ChattingController.toChatting(pc, String.format("\\fU메티스: 4가 나왔군요 !"), Lineage.CHATTING_MODE_MESSAGE);

					}
					if(num == 5){

						ChattingController.toChatting(pc, String.format("\\fU메티스: 5가 나왔군요 !"), Lineage.CHATTING_MODE_MESSAGE);
					}
			
			
				}
			}
			
			if (en && !isOpen && tebeTime.getHour() == hour && test == min && sec == 50) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("\\fU메티스: 10초뒤  추첨된 이벤트가 시작됩니다.")));
			}
			
			if (en && !isOpen && tebeTime.getHour() == hour && tebeTime.getMin() == min && sec == 0) {
				isOpen = true;
				event_timeEndTime = time + (1000L * playSec);
				sendMessage();
			}
		}
		
		if(en && isOpen && num == 0){
			num = (int)(Math.random() * 4 +1);
		}
		
		if (isOpen && event_timeEndTime > 0 && event_timeEndTime < time) {
			isOpen = false;
			num = 0;
			sendMessage();
		}
	}
	
	static public void sendMessage() {
		if (!GmEventSettings.isEnabled(GmEventSettings.TIMEEVENT))
			return;
		if (isOpen) {
			String msg = "\\fY     ***** 이벤트가 시작 되었습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		} else {
			String msg = "\\fY     ***** 이벤트가 종료되었습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));

			// 화면 중앙에 메세지 알리기.
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
}
