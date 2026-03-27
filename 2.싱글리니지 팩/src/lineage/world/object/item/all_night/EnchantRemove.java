package lineage.world.object.item.all_night;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class EnchantRemove extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new EnchantRemove();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			ItemInstance item = cha.getInventory().value(cbp.readD());

			if (item.getEnLevel() > 0) {
				if (!item.isEquipped()) {
					item.setEnLevel(0);
					cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
					cha.getInventory().count(this, getCount() - 1, true);
				} else {
					ChattingController.toChatting(cha, "착용중인 장비에 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				ChattingController.toChatting(cha, "인첸트가 부여된 장비에 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}
}
