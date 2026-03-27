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
import lineage.world.controller.WorldBossController;
import lineage.world.controller.HellController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class WorldBossTeleporter extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		
		list.add(String.format("입장 레벨: %d이상 입장 가능", Lineage.world_level));
		list.add(String.format("수배 조건: %s", Lineage.world_wanted ? "수배자만 입장 가능" : "수배 필요없음"));
		list.add(String.format("혈맹 조건: %s", Lineage.world_clan ? "혈맹 필요" : "혈맹 필요없음"));
		list.add(String.format("입장 시간: %s", Lineage.world_dungeon_time));	
		list.add(String.format("진행 시간: %s", Lineage.world_play_time < 60 ? Lineage.world_play_time + "초" : (Lineage.world_play_time / 60) + "분"));
		list.add(String.format("입장 가능 여부: %s", WorldBossController.isOpen ? "현재 입장 가능" : "입장 불가"));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "worldtel", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("world_teleport")) {
			if (pc.getGm() > 0 || WorldBossController.isOpen) {
				if (pc.getGm() > 0 || (Lineage.world_level <= pc.getLevel())) {
					if (pc.getGm() > 0 || !Lineage.world_wanted || (Lineage.world_wanted && WantedController.checkWantedPc(pc))) {
						if (pc.getGm() > 0 || !Lineage.world_clan || (Lineage.world_clan && pc.getClanId() > 0)) {
							pc.toPotal(Util.random(32867, 32870), Util.random(32815, 32818), 1400);
						} else {
							ChattingController.toChatting(pc, "월드보스 토벌은 혈맹 가입자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(pc, "월드보스 토벌은 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(pc, String.format("월드보스 토벌은 %d레벨 이상 입장 가능합니다.", Lineage.world_level), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(pc, "입장 가능한 시간이 아닙니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}