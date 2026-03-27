package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.WantedController;
import lineage.world.controller.HellController;
import lineage.world.controller.PenguinHuntingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class PenguinTeleporter extends object {
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> list = new ArrayList<String>();
		
		list.add(String.format("입장 레벨: %d이상 입장 가능", Lineage.phunt_level));
		list.add(String.format("수배 조건: %s", Lineage.phunt_wanted ? "수배자만 입장 가능" : "수배 필요없음"));
		list.add(String.format("혈맹 조건: %s", Lineage.phunt_clan ? "혈맹 필요" : "혈맹 필요없음"));
		list.add(String.format("입장 시간: %s", Lineage.phunt_dungeon_time));	
		list.add(String.format("진행 시간: %s", Lineage.phunt_play_time < 60 ? Lineage.phunt_play_time + "초" : (Lineage.phunt_play_time / 60) + "분"));
		list.add(String.format("입장 가능 여부: %s", PenguinHuntingController.isOpen ? "현재 입장 가능" : "입장 불가"));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "phunttel", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("phunt_teleport")) {
			if (pc.getGm() > 0 || PenguinHuntingController.isOpen) {
				if (pc.getGm() > 0 || (Lineage.phunt_level <= pc.getLevel())) {
					if (pc.getGm() > 0 || !Lineage.phunt_wanted || (Lineage.phunt_wanted && WantedController.checkWantedPc(pc))) {
						if (pc.getGm() > 0 || !Lineage.phunt_clan || (Lineage.phunt_clan && pc.getClanId() > 0)) {
							if(pc.getInventory().find("메테오 스트라이크") == null){
								
								ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("메테오 스트라이크"));					
								ii.setCount(1);
								ii.setBless(1);
								ii.setDefinite(true);
								pc.toGiveItem(null, ii, ii.getCount());
							}
							
							pc.toPotal(Util.random(32644, 32647), Util.random(32867, 32870), 63);
						} else {
							ChattingController.toChatting(pc, "펭귄서식지는 혈맹 가입자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(pc, "펭귄서식지는 수배자만 입장 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					ChattingController.toChatting(pc, String.format("펭귄서식지는 %d레벨 이상 입장 가능합니다.", Lineage.phunt_level), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(pc, "펭귄서식지는로 가는길이 닫혀있습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
