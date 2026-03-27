package lineage.network.packet.server;

import java.util.List;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.instance.ItemInstance;

public class S_SmithList extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, List<ItemInstance> list){
		if(bp == null)
			bp = new S_SmithList(list);
		else
			((S_SmithList)bp).toClone(list);
		return bp;
	}
	
	public S_SmithList(List<ItemInstance> list){
		toClone(list);
	}
	
	public void toClone(List<ItemInstance> list){
		clear();
		
		writeC(Opcodes.S_OPCODE_SMITH);
		writeC(0x64);
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(list.size());
		for(ItemInstance item : list){
			writeD(item.getObjectId());
			writeC(item.getDurability());
		}
	}
	
}
