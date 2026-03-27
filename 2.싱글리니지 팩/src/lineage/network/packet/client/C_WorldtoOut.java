package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ReturnToCharacterSelect;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

public class C_WorldtoOut extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_WorldtoOut(data, length);
		else
			((C_WorldtoOut)bp).clone(data, length);
		return bp;
	}
	
	public C_WorldtoOut(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete())
			return this;

		pc.toWorldOut();
		if(Lineage.server_version > 230)
			pc.toSender(S_ReturnToCharacterSelect.clone(BasePacketPooling.getPool(S_ReturnToCharacterSelect.class)));
		
		return this;
	}

}
