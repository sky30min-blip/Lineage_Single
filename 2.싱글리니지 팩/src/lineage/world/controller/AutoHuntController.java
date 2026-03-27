package lineage.world.controller;

import java.util.Calendar;
import java.util.Date;

import lineage.bean.database.TeamBattleTime;
import lineage.database.CharactersDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class AutoHuntController {
	static private Calendar calendar;

	static public void init() {
		TimeLine.start("AutoHuntController..");
		calendar = Calendar.getInstance();

		TimeLine.end();
	}

	static public void toTimer(long time) {
		if (Lineage.is_auto_hunt_time) {
			for (PcInstance pc : World.getPcList()) {
				if (pc.isAutoHunt) {
					if (Lineage.is_auto_hunt_time_account) {
						if (--pc.auto_hunt_account_time < 1) {
							pc.endAutoHunt(true, true);
						}
					} else {
						if (--pc.auto_hunt_time < 1) {
							pc.endAutoHunt(true, true);
						}
					}
				}
			}
			
			calendar.setTimeInMillis(time);
			Date date = calendar.getTime();
			int hour = date.getHours();
			int min = date.getMinutes();
			int sec = date.getSeconds();

			for (TeamBattleTime t : Lineage.auto_hunt_reset_time) {
				// 자동 사냥 이용시간 초기화
				if (hour == t.getHour() && min == t.getMin() && sec == 0)
					resetAutoHuntTime();
			}
		}
	}

	static public void resetAutoHuntTime() {
		CharactersDatabase.updateAutoHuneTime();

		for (PcInstance pc : World.getPcList()) {
			pc.auto_hunt_time = Lineage.auto_hunt_time;
			pc.auto_hunt_account_time = Lineage.auto_hunt_time;
		}

		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("[알림] 자동 사냥 이용시간이 초기화 되었습니다.")));
	}
}
