package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_TaxGet extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_TaxGet(data, length);
		else
			((C_TaxGet)bp).clone(data, length);
		return bp;
	}
	
	public C_TaxGet(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete() || !isRead(8))
			return this;
		
		object o = pc.findInsideList(readD());
		if(o != null)
			o.toTaxGet(pc, readD());
		return this;
	}

}
