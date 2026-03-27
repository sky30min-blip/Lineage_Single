package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.world.object.instance.ItemInstance;

public class S_InventoryEquipped extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance item) {
		if (bp == null)
			bp = new S_InventoryEquipped(item);
		else
			((S_InventoryEquipped) bp).clone(item);
		return bp;
	}

	public S_InventoryEquipped(ItemInstance item) {
		clone(item);
	}

	public void clone(ItemInstance item) {
		clear();

		writeC(Opcodes.S_OPCODE_ITEMEQUIP);
		writeD(item.getObjectId());
		writeS(getName(item));
	}
}
