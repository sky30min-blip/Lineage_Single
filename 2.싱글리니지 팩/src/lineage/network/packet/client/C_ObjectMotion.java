package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.world.object.instance.PcInstance;

public class C_ObjectMotion extends ClientBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectMotion(data, length);
		else
			((C_ObjectMotion)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectMotion(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		pc.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), pc, readH()), true);

		return this;
	}

}
