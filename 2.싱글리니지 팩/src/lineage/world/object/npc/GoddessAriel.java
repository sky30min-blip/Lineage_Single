package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import kuberaitem.결혼반지;

public class GoddessAriel extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
	    pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ring1"));
	}
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		super.toTalk(pc, action, type, cbp);

		// 충전하는부분
		if (action.equalsIgnoreCase("chg")) {
			if (pc.getInventory().isAden(100000, false)) {
				for (ItemInstance item : pc.getInventory().getListColl()) {
					if (item instanceof 결혼반지) {
						if(((결혼반지) item).getUseCount() >= 100) { 
							ChattingController.toChatting(pc, "더 이상 충전할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						((결혼반지) item).initUseCount();
						pc.getInventory().isAden(100000, true);
						pc.toSender(new S_InventoryStatus(item));
						ChattingController.toChatting(pc, "충전 완료하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
				}
				ChattingController.toChatting(pc, "결혼반지를 가지고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(pc, "아데나가 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
