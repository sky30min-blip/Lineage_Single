package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.PcInstance;

public class C_KingdomWarTimeSelect extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_KingdomWarTimeSelect(data, length);
		else
			((C_KingdomWarTimeSelect)bp).clone(data, length);
		return bp;
	}
	
	public C_KingdomWarTimeSelect(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(pc==null || pc.isWorldDelete())
			return this;
		
		KingdomController.toWarTimeSelect(pc);
		
		return this;
	}

}
