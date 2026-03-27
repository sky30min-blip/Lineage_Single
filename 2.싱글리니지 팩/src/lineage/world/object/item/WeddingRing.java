package lineage.world.object.item;

import lineage.bean.database.Item;
import lineage.bean.lineage.Wedding;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.controller.WeddingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class WeddingRing extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new WeddingRing();
		return item;
	}

	@Override
	public ItemInstance clone(Item item) {
		switch (item.getNameIdNumber()) {
			case 2268:
				quantity = 1;
				break;
			case 2269:
				quantity = 5;
				break;
			default:
				quantity = Util.random(1, 5);
				break;
		}

		return super.clone(item);
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		Wedding w = WeddingController.find(cha.getObjectId());
		// 아무일도 일어나지 않았습니다.
		if (w == null) {
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			return;
		}
		//
		if (getQuantity() <= 0) {
			// 아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			return;
		}
		//
		if (!LocationController.isTeleportZone(cha, true, true))
			return;
		//
		PcInstance use = World.findPc(cha.getClassSex() == 1 ? w.getGirlObjectId() : w.getManObjectId());
		if (use == null) {
			ChattingController.toChatting(cha, "당신의 파트너는 지금 게임을 하고 있지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (!LocationController.isTeleportVerrYedHoraeZone(use, true))
				return;
			//
			setQuantity(getQuantity() - 1);
			cha.toSender(new S_InventoryEquipped( this));
			//
			cha.toPotal(use.getX(), use.getY(), use.getMap());
		}
	}
}
