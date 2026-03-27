package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.weapon.Arrow;

public class S_InventoryAdd extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance item) {
		if (bp == null)
			bp = new S_InventoryAdd(item);
		else
			((S_InventoryAdd) bp).clone(item);
		return bp;
	}

	public S_InventoryAdd(ItemInstance item) {
		clone(item);
	}

	public void clone(ItemInstance item) {
		clear();

		writeC(Opcodes.S_OPCODE_ITEMADD);
		writeD(item.getObjectId());
		if (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance || item instanceof Arrow) {
			writeH(item.getItem().getEquippedSlot());
			writeH(item.getItem().getInvGfx());
			writeC(item.getBressPacket());
			writeD((int) item.getCount());
			writeC(item.isDefinite() ? 1 : 0);
			writeS(getName(item));
			if (Lineage.server_version > 144 && item.isDefinite()) {
				if (item instanceof ItemWeaponInstance || item instanceof Arrow) {
					toWeapon(item);
				} else {
					toArmor(item);
				}
			}
		} else {
			if (item.getBless() == 0)
				writeC(item.getItem().getAction2());
			else
				writeC(item.getItem().getAction1());
			writeC(item.getQuantity());
			writeH(item.getItem().getInvGfx());
			writeC(item.getBressPacket());
			writeD((int) item.getCount());
			writeC(item.isDefinite() ? 1 : 0);
			writeS(getName(item));
			if (Lineage.server_version > 144 && item.isDefinite()) {
				toEtc(item);
			}
		}
		writeC(0x00);
	}
}
