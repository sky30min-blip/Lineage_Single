package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.MagicDoll;
import lineage.world.object.item.all_night.BuffMaan;

public class SealedCancelScroll extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new SealedCancelScroll();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		//
		ItemInstance target = cha.getInventory().value(cbp.readD());
		if (target == null)
			return;

		//
		if (target instanceof ItemWeaponInstance || target instanceof ItemArmorInstance || target instanceof MagicDoll || target instanceof BuffMaan) {
			//
			if (target.getBless() >= 0) {
				ChattingController.toChatting(cha, "해당 아이템은 이미 봉인 해제되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			//
			target.setBless(target.getBless() + 128);
			String name = target.toStringSearch();
			if (Lineage.server_version > 230) {
				cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), target));
				// %s 이(가) 봉인 해제되었습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 1015, target.toString()));
			} else {
				cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), target));
				// %s 이(가) 봉인 해제되었습니다.
				ChattingController.toChatting(cha, Util.getStringWord(name, "이", "가") + " 봉인 해제되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
			//
			cha.getInventory().count(this, getCount() - 1, true);
		}
	}

}
