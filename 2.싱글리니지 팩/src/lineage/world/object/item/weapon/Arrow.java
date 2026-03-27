package lineage.world.object.item.weapon;

import java.sql.Connection;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Arrow extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Arrow();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		ItemInstance arrow = cha.getInventory().getSlot(Lineage.SLOT_ARROW);

		if (arrow != null && arrow.isEquipped() && arrow.getObjectId() != this.getObjectId())
			arrow.toClick(cha, null);

		if (equipped) {
			setEquipped(false);
			cha.getInventory().setSlot(Lineage.SLOT_ARROW, null);
		} else {
			setEquipped(true);
			cha.getInventory().setSlot(Lineage.SLOT_ARROW, this);
			ChattingController.toChatting(cha, String.format("%s이 선택되었습니다.", toStringDB()), Lineage.CHATTING_MODE_MESSAGE);
		}

		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}

	@Override
	public void toWorldJoin(Connection con, PcInstance pc) {
		super.toWorldJoin(con, pc);
		if (equipped) {
			setEquipped(false);
			toClick(pc, null);
		}
	}

} 
