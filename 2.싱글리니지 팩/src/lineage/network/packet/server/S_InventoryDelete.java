package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.ItemInstance;

public class S_InventoryDelete extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance item){
		if(bp == null)
			bp = new S_InventoryDelete(item);
		else
			((S_InventoryDelete)bp).clone(item);
		return bp;
	}
	
	public S_InventoryDelete(ItemInstance item){
		clone(item);
	}
	
	public void clone(ItemInstance item){
		clear();
		
		writeC(Opcodes.S_OPCODE_ITEMDELETE);
		writeD(item.getObjectId());
	}
}
