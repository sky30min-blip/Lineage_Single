package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.PartyController;
import lineage.world.object.instance.PcInstance;

public class C_ObjectPartyOut extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectPartyOut(data, length);
		else
			((C_ObjectPartyOut)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectPartyOut(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;

		PartyController.close(pc);
		return this;
	}
	
}
