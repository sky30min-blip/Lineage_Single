package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.ItemInstance;

public class S_MiniMap extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, ItemInstance ii, int id){
		if(bp == null)
			bp = new S_MiniMap(ii, id);
		else
			((S_MiniMap)bp).toClone(ii, id);
		return bp;
	}
	
	public S_MiniMap(ItemInstance ii, int id){
		toClone(ii, id);
	}
	
	public void toClone(ItemInstance ii, int id){
		clear();
		writeC(Opcodes.S_OPCODE_MINIMAP);
		writeD(ii.getObjectId());
		writeC(id);
	}
	
}
