package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;

public class C_InterfaceSave extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_InterfaceSave(data, length);
		else
			((C_InterfaceSave)bp).clone(data, length);
		return bp;
	}
	
	public C_InterfaceSave(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		pc.setDbInterface( readB() );
		return this;
	}
	
}
