package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BookController;
import lineage.world.object.instance.PcInstance;

public class C_BookAppend extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BookAppend(data, length);
		else
			((C_BookAppend)bp).clone(data, length);
		return bp;
	}
	
	public C_BookAppend(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		if(pc.getGm()>0 || !pc.isTransparent())
			BookController.append(pc, readS().trim());
		return this;
	}
}
