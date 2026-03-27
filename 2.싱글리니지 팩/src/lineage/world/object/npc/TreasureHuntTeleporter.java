package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.TreasureHuntController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class TreasureHuntTeleporter extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		
		list.add(String.format("입장 레벨: %d이상 입장 가능", Lineage.Treasuress_level));
		list.add(String.format("입장 시간: %s", Lineage.Treasuress_dungeon_time));	
		list.add(String.format("진행 시간: %s", Lineage.Treasuress_play_time < 60 ? Lineage.Treasuress_play_time + "초" : (Lineage.Treasuress_play_time / 60) + "분"));
		list.add(String.format("입장 가능 여부: %s", TreasureHuntController.isOpen ? "현재 입장 가능" : "입장 불가"));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tboxtel", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("tbox_teleport")) {
			if (pc.getGm() > 0 || TreasureHuntController.isOpen) {
				if (pc.getGm() > 0 || (Lineage.Treasuress_level <= pc.getLevel())) {
				
						pc.toPotal(Util.random(32801, 32804), Util.random(32803, 32805), 807);
			
				} else {
					ChattingController.toChatting(pc, String.format("보물찾기는 %d레벨 이상 입장 가능합니다.", Lineage.Treasuress_level), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(pc, "보물찾기가 진행중이지 않습니다..", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
