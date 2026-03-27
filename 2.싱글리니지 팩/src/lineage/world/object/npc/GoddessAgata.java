package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class GoddessAgata extends object {

	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		List<String> list = new ArrayList<String>();
		
		list.add(String.valueOf(pc.getLevel() * Lineage.player_lost_exp_aden_rate));
		list.add(String.valueOf(Lineage.player_lost_exp_rate * 100));
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "restore", null, list));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("exp")){
			if(pc.getLostExp() > 0){
				pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 775, 
						String.valueOf(pc.getLevel() * Lineage.player_lost_exp_aden_rate)));
			}else{
				ChattingController.toChatting(pc, "회복할 경험치가 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
	public void toAsk(PcInstance pc, boolean yes){
		if (yes) {
			if (pc.getInventory().isAden(pc.getLevel() * Lineage.player_lost_exp_aden_rate, true)) {
				// 경험치90% 복구.
				pc.setExp(pc.getExp() + (pc.getLostExp() * Lineage.player_lost_exp_rate));
				pc.setLostExp(0);
				pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
				ChattingController.toChatting(pc, String.format("잃은 경험치의 %d%가 회복 되었습니다.", Lineage.player_lost_exp_rate * 100), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(pc, "아데나가 충분치 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
	
}
