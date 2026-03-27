package lineage.network.packet.client;

import lineage.network.LineageClient;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;

public class C_TimeLeft extends ClientBasePacket {
	
	static synchronized public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_TimeLeft(data, length);
		else
			((C_TimeLeft)bp).clone(data, length);
		return bp;
	}
	
	public C_TimeLeft(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(LineageClient c){
		if(c.getAccountUid()>0)
			BasePacketPooling.setPool( C_NoticeOk.clone(BasePacketPooling.getPool(C_NoticeOk.class), null, 0).init(c) );
		return this;
	}
}
