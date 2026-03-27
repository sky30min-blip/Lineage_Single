package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.weapon.Arrow;

public class S_InventoryStatus extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance item){
		if(bp == null)
			bp = new S_InventoryStatus(item);
		else
			((S_InventoryStatus)bp).toClone(item);
		return bp;
	}
	
	public S_InventoryStatus() {
		//
	}
	
	public S_InventoryStatus(ItemInstance item){
		toClone(item);
	}
	
	public void toClone(ItemInstance item){
		clear();

		writeC(Opcodes.S_OPCODE_ITEMSTATUS);
		writeD(item.getObjectId());
		writeS(getName(item));
		writeD((int)item.getCount());
		if(item.isDefinite()){
			if(item instanceof ItemWeaponInstance || item instanceof Arrow){
				toWeapon(item);
			}else if(item instanceof ItemArmorInstance){
				toArmor(item);
			}else{
				toEtc(item);
			}
		}else{
			writeC(0x00);
		}
	}
}
