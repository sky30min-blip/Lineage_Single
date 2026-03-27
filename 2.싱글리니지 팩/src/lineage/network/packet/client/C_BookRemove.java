package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BookController;
import lineage.world.object.instance.PcInstance;

public class C_BookRemove extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BookRemove(data, length);
		else
			((C_BookRemove)bp).clone(data, length);
		return bp;
	}
	
	public C_BookRemove(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		if(pc.getGm()>0 || !pc.isTransparent())
			BookController.remove(pc, readS());
		return this;
	}
}
