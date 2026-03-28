package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.IceDungeonController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Giran_dungeon_Telepoter extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		String time = null;
		List<String> msg = new ArrayList<String>();
			
		if (Lineage.giran_dungeon_inti_time < 12)
			time = "오전 " + String.valueOf(Lineage.giran_dungeon_inti_time) + "시";
		else
			time = "오후 "+ String.valueOf(Lineage.giran_dungeon_inti_time - 12) + "시";
		
		msg.add(String.valueOf(Lineage.giran_dungeon_time / 3600));
		msg.add(time);
		msg.add(String.format("%s", Lineage.giran_dungeon_level2));
		msg.add(String.format("%s", Lineage.giran_dungeon_level3));

		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "girantel", null, msg));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){

		if (action.equalsIgnoreCase("teleport giran dungeon")) {
		    if (pc.getLevel() < Lineage.giran_dungeon_level) {
		        ChattingController.toChatting(pc, String.format("기란감옥은 %d레벨 이상 입장가능합니다.", Lineage.giran_dungeon_level), Lineage.CHATTING_MODE_MESSAGE);
		        return;
		    }
		    // 시간제 사용 시 2·3층 이동과 동일하게 잔여 시간 없으면 653 직행 입장 차단
		    if (Lineage.is_giran_dungeon_time && pc.getGiran_dungeon_time() < 1 && pc.getGm() == 0) {
		    	ChattingController.toChatting(pc, "기란감옥 이용시간을 모두 사용하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
		    	return;
		    }
		    
		    int i = Util.random(0, 2);
		    switch (i) {
		        case 0:
		            pc.toTeleport(32810, 32731, 653, true);
		            break;
		        case 1:
		            pc.toTeleport(32807, 32727, 653, true);
		            break;
		        case 2:
		            pc.toTeleport(32807, 32734, 653, true);
		            break;
		    }
		}
		if (action.equalsIgnoreCase("teleport giran dungeon2")) {
			if (pc.getLevel() < Lineage.giran_dungeon_level2) {
				ChattingController.toChatting(pc, String.format("기란감옥은 %d레벨 이상 입장가능합니다.", Lineage.giran_dungeon_level2), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!Lineage.is_giran_dungeon_time || pc.getGiran_dungeon_time() > 0)
				pc.toPotal(32790, 32800, 54);
			else
				ChattingController.toChatting(pc, "기란감옥 이용시간을 모두 사용하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
		if (action.equalsIgnoreCase("teleport giran dungeon3")) {
			if (pc.getLevel() < Lineage.giran_dungeon_level3) {
				ChattingController.toChatting(pc, String.format("기란감옥은 %d레벨 이상 입장가능합니다.", Lineage.giran_dungeon_level3), Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			
			if (!Lineage.is_giran_dungeon_time || pc.getGiran_dungeon_time() > 0)
				pc.toPotal(32736, 32809, 55);
			else
				ChattingController.toChatting(pc, "기란감옥 이용시간을 모두 사용하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
