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
import lineage.world.controller.WantedController;
import lineage.world.controller.HellController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class HellTeleporter extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		int nowday = getDayOfWeek ();
		
		list.add(String.format("입장 레벨: %d이상 입장 가능", Lineage.hell_level));
		list.add(String.format("수배 조건: %s", Lineage.hell_wanted ? "수배자만 입장 가능" : "수배 필요없음"));
		list.add(String.format("혈맹 조건: %s", Lineage.hell_clan ? "혈맹 필요" : "혈맹 필요없음"));
		if(nowday == 1 || nowday == 7){
			list.add(String.format("입장 시간: %s", Lineage.hell_dungeon_time2));	
		}else{
			list.add(String.format("입장 시간: %s", Lineage.hell_dungeon_time));	
		}
		list.add(String.format("진행 시간: %s", Lineage.hell_play_time < 60 ? Lineage.hell_play_time + "초" : (Lineage.hell_play_time / 60) + "분"));
		list.add(String.format("입장 가능 여부: %s", HellController.isOpen ? "현재 입장 가능" : "입장 불가"));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "helltel", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("hell_teleport")) {
			if (pc.getGm() > 0 || HellController.isOpen) {
				if (pc.getGm() > 0 || (Lineage.hell_level <= pc.getLevel())) {
					if (pc.getGm() > 0 || !Lineage.hell_wanted || (Lineage.hell_wanted && WantedController.checkWantedPc(pc))) {
						if (pc.getGm() > 0 || !Lineage.hell_clan || (Lineage.hell_clan && pc.getClanId() > 0)) {
							pc.toPotal(Util.random(32732, 32735), Util.random(32798, 32803), 666);
						} else {
							ChattingController.toChatting(pc, "지옥은 혈맹 가입자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(pc, "지옥은 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(pc, String.format("지옥은 %d레벨 이상 입장 가능합니다.", Lineage.hell_level), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(pc, "지옥으로 가는길이 닫혀있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	public static int getDayOfWeek() {
		Calendar rightNow = Calendar.getInstance();
		int day_of_week = rightNow.get(Calendar.DAY_OF_WEEK);
		return day_of_week;
	}
}
