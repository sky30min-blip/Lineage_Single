package lineage.world.controller;

import java.util.Calendar;
import java.util.Date;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;

public final class WorldClearController {
	static private Calendar calendar;
	static long cleanTime;
	static boolean clean;
	
	static public void init() {
		TimeLine.start("WorldClearController..");
		calendar = Calendar.getInstance();
		cleanTime = System.currentTimeMillis() + Lineage.world_clean_time;
		TimeLine.end();
	}

	static public void toTimer(long time){
		calendar.setTimeInMillis(time);
		Date date = calendar.getTime();
		int min = date.getMinutes();
		int sec = date.getSeconds();
		
		if (Lineage.is_world_clean && Lineage.is_world_clean_message && cleanTime - (Lineage.world_clean_message_time) < time && (cleanTime - time) / 1000 > 0) {
			clean = true;
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fR[알림] %d초 후 월드맵이 청소됩니다.", (cleanTime - time) / 1000)));
		}
		
		if (Lineage.is_world_clean && cleanTime < time && clean) {
			clean = false;
			cleanTime = System.currentTimeMillis() + Lineage.world_clean_time;
			
			World.clearWorldItem();
			
			if (Lineage.is_world_clean_message) {
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "\\fR[알림] 월드맵이 청소되었습니다."));
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fR[알림] 월드맵은 %d분 간격으로 청소됩니다.", Lineage.world_clean_time / 1000 / 60)));
			}
		}
	}
}
