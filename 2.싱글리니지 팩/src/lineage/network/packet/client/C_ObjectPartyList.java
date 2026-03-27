package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.PartyController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectPartyList extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectPartyList(data, length);
		else
			((C_ObjectPartyList)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectPartyList(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		PartyController.toInfo(pc);
		
		return this;
	}

}
