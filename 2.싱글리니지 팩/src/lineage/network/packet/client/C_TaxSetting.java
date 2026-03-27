package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_TaxSetting extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_TaxSetting(data, length);
		else
			((C_TaxSetting)bp).clone(data, length);
		return bp;
	}
	
	public C_TaxSetting(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete() || !isRead(5))
			return this;
		
		object o = pc.findInsideList(readD());
		if(o != null)
			o.toTaxSetting(pc, readC());
		return this;
	}

}
