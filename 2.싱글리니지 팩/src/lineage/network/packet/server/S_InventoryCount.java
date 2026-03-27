package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.ItemInstance;

public class S_InventoryCount extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance item){
		if(bp == null)
			bp = new S_InventoryCount(item);
		else
			((S_InventoryCount)bp).clone(item);
		return bp;
	}
	
	public S_InventoryCount(ItemInstance item){
		clone(item);
	}
	
	public void clone(ItemInstance item){
		clear();

		writeC(Opcodes.S_OPCODE_ITEMCOUNT);
		writeD(item.getObjectId());
		writeD((int)item.getCount());
		writeC(0x00);
	}
}
