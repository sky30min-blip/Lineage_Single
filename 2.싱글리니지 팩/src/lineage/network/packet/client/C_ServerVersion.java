package lineage.network.packet.client;

import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ServerVersion;

public class C_ServerVersion extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ServerVersion(data, length);
		else
			((C_ServerVersion)bp).clone(data, length);
		return bp;
	}
	
	public C_ServerVersion(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		c.toSender( S_ServerVersion.clone(BasePacketPooling.getPool(S_ServerVersion.class)).init(c) );
		return this;
	}
}
