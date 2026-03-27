package lineage.world.object.npc.shop;

import kuberaitem.기마투구;
import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.item.armor.Turban;

public class HorseSeller extends ShopInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Turban();
		// 시간 설정 - 깃털 갯수
		// : 180개 3시간
		// : 240개 5시간
		// : 450개 10시간
		// 기본 시간을 3시간으로 설정.
		item.setNowTime(60 * 60 * 3);
		return item;
	}
	
	public HorseSeller(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL)
			if (getY() == 33360) {
				// 아덴기마단원 (좌) [판매]
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horseseller1"));
			} else {
				// 아덴기마단원 (우) [충전]
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horserestore"));
			}
		else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horseseller4"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		super.toTalk(pc, action, type, cbp);
		// 충전하는부분
		if (action.equalsIgnoreCase("C")) {
			if (pc.getInventory().isAden(100000, false)) {
				for (ItemInstance item : pc.getInventory().getListColl()) {
					if (item instanceof Turban) {
						if(item.getNowTime() >= 10800) { 
							ChattingController.toChatting(pc, "더 이상 충전할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}
						item.setNowTime(60 * 60 * 3);
						pc.getInventory().isAden(100000, true);
						ChattingController.toChatting(pc, "충전이 완료되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
						return;
					}
				}
				ChattingController.toChatting(pc, "기마용투구를 가지고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(pc, "아데나가 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
