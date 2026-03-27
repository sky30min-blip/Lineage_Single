package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Unknow3;
import lineage.world.object.instance.PcInstance;

public class C_Unknow extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Unknow(data, length);
		else
			((C_Unknow)bp).clone(data, length);
		return bp;
	}
	
	public C_Unknow(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		pc.toSender( S_Unknow3.clone(BasePacketPooling.getPool(S_Unknow3.class)) );
		return this;
	}
}
