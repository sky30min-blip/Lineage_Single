package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.world.object.instance.PcInstance;

public class C_PkCounter extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_PkCounter(data, length);
		else
			((C_PkCounter)bp).clone(data, length);
		return bp;
	}
	
	public C_PkCounter(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;
		
		// 현재 PK 횟수는 %0 입니다.
		pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 562, String.valueOf(pc.getPkCount())));
		
		return this;
	}

}
