package lineage.world.object.item.all_night;

import all_night.Lineage_Balance;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollOfChangeBless extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ScrollOfChangeBless();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			ItemInstance item = cha.getInventory().value(cbp.readD());

			if ((item.getItem().getType1().equals("weapon") || item.getItem().getType1().equals("armor")) && !item.getItem().getType2().equalsIgnoreCase("arrow") &&
					item.getItem().getNameIdNumber() != 1206 && item.getItem().getNameIdNumber() != 1207) {
				if (item.getBless() != 0 && item.getBless() != -128) {
					
			
						
						if (Util.random(1, 100) < getItem().getSmallDmg()) {
							if (item.isEquipped()) {
								item.setEquipped(false);
								item.toOption(cha, false);
								
								item.setBless(0);
								
								item.setEquipped(true);
								item.toOption(cha, true);
							} else {
								item.setBless(0);
							}
							
							cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
							cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
							cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 13534), true);
							ChattingController.toChatting(cha, "축복 부여에 성공하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
						} else {
							ChattingController.toChatting(cha, "축복 부여에 실패하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
						
	
					
					cha.getInventory().count(this, getCount() - 1, true);
					
				} else {
					ChattingController.toChatting(cha, "이미 축복이 부여된 아이템입니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(cha, "무기, 방어구, 장신구에 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
