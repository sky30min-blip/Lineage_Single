package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;

public class C_ObjectHeading extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectHeading(data, length);
		else
			((C_ObjectHeading)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectHeading(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || !isRead(1) || pc.isWorldDelete())
			return this;
		
		pc.setHeading( readC() );
		return this;
	}
}
