package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BoardController;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public class C_BoardDelete extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardDelete(data, length);
		else
			((C_BoardDelete)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardDelete(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(!isRead(8) || pc==null || pc.isWorldDelete())
			return this;

		object o = pc.findInsideList(readD());
		
		if(o!=null && o instanceof BoardInstance) {
			BoardInstance b = (BoardInstance) o;
			BoardController.toDelete(pc, b, readD());	
		}
		
		return this;
	}

}
