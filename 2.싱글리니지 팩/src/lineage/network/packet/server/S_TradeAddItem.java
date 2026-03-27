package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.weapon.Arrow;

public class S_TradeAddItem extends S_Inventory {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance item, boolean me){
		if(bp == null)
			bp = new S_TradeAddItem(item, me);
		else
			((S_TradeAddItem)bp).toClone(item, me);
		return bp;
	}

	public S_TradeAddItem(ItemInstance item, boolean me){
		toClone(item, me);
	}

	public void toClone(ItemInstance item, boolean me){
		clear();

		writeC(Opcodes.S_OPCODE_TRADEADDITEM);
		writeC(me ? 0 : 1);							// 0:자기창 1:상대창
		writeH(item.getItem().getInvGfx());
		writeS(getName(item));
		writeC(item.getBressPacket());

		if(Lineage.server_version>144 && item.isDefinite()){
			if(item instanceof ItemWeaponInstance || item instanceof Arrow){
				toWeapon(item);
			}else if(item instanceof ItemArmorInstance){
				toArmor(item);
			}else{
				toEtc(item);
			}
		}else{
			writeC(0x00);	// 바이트수
		}
	}
}
