package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.WantedController;
import lineage.world.controller.DevilController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class EvilTeleporter extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		
		list.add(String.format("입장 레벨: %d이상 입장 가능", Lineage.devil_level));
		list.add(String.format("수배 조건: %s", Lineage.devil_wanted ? "수배자만 입장 가능" : "수배 필요없음"));
		list.add(String.format("혈맹 조건: %s", Lineage.devil_clan ? "혈맹 필요" : "혈맹 필요없음"));
		list.add(String.format("입장 시간: %s", Lineage.devil_dungeon_time));	
		list.add(String.format("진행 시간: %s", Lineage.devil_play_time < 60 ? Lineage.devil_play_time + "초" : (Lineage.devil_play_time / 60) + "분"));
		list.add(String.format("입장 가능 여부: %s", DevilController.isOpen ? "현재 입장 가능" : "입장 불가"));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "deviltel", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("devil_teleport")) {
			if (pc.getGm() > 0 || DevilController.isOpen) {
				if (pc.getGm() > 0 || (Lineage.devil_level <= pc.getLevel())) {
					if (pc.getGm() > 0 || !Lineage.devil_wanted || (Lineage.devil_wanted && WantedController.checkWantedPc(pc))) {
						if (pc.getGm() > 0 || !Lineage.devil_clan || (Lineage.devil_clan && pc.getClanId() > 0)) {
							pc.toPotal(Util.random(32722, 32725), Util.random(32798, 32801), 5167);
						} else {
							ChattingController.toChatting(pc, "악마왕의 영토는 혈맹 가입자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(pc, "악마왕의 영토는 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(pc, String.format("악마왕의 영토는 %d레벨 이상 입장 가능합니다.", Lineage.devil_level), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(pc, "악마왕의 영토로 가는길이 닫혀있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
