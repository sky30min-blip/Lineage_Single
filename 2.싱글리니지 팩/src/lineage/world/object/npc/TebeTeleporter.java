package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.TebeController;
import lineage.world.controller.WantedController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class TebeTeleporter extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		int nowday = getDayOfWeek ();
		
		list.add(String.format("입장 레벨: %d이상 입장 가능", Lineage.tebe_level));
		list.add(String.format("수배 조건: %s", Lineage.tebe_wanted ? "수배자만 입장 가능" : "수배 필요없음"));
		list.add(String.format("혈맹 조건: %s", Lineage.tebe_clan ? "혈맹 필요" : "혈맹 필요없음"));
		if(nowday == 1 || nowday == 7){
		list.add(String.format("입장 시간: %s", Lineage.tebe_dungeon_time2));	
		}else{
			list.add(String.format("입장 시간: %s", Lineage.tebe_dungeon_time));		
		}
		list.add(String.format("진행 시간: %s", Lineage.tebe_play_time < 60 ? Lineage.tebe_play_time + "초" : (Lineage.tebe_play_time / 60) + "분"));
		list.add(String.format("입장 가능 여부: %s", TebeController.isOpen ? "현재 입장 가능" : "입장 불가"));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tebetel", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("tebe_teleport")) {
			if (pc.getGm() > 0 || TebeController.isOpen) {
				if (pc.getGm() > 0 || (Lineage.tebe_level <= pc.getLevel())) {
					if (pc.getGm() > 0 || !Lineage.tebe_wanted || (Lineage.tebe_wanted && WantedController.checkWantedPc(pc))) {
						if (pc.getGm() > 0 || !Lineage.tebe_clan || (Lineage.tebe_clan && pc.getClanId() > 0)) {
							pc.toPotal(Util.random(32742, 32744), Util.random(32801, 32803), 781);
						} else {
							ChattingController.toChatting(pc, "테베라스 사막은 혈맹 가입자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(pc, "테베라스 사막은 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(pc, String.format("테베라스 사막은 %d레벨 이상 입장 가능합니다.", Lineage.tebe_level), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(pc, "테베라스 사막으로 가는길이 닫혀있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	public static int getDayOfWeek() {
		Calendar rightNow = Calendar.getInstance();
		int day_of_week = rightNow.get(Calendar.DAY_OF_WEEK);
		return day_of_week;
	}
}
