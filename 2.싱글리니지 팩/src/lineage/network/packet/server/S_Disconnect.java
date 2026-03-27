package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Disconnect extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int type){
		if(bp == null)
			bp = new S_Disconnect(type);
		else
			((S_Disconnect)bp).clone(type);
		return bp;
	}
	
	public S_Disconnect(int type){
		clone(type);
	}
	
	public void clone(int type){
		clear();
		writeC(Opcodes.S_OPCODE_DISCONNECT);
		writeC(type);	// 0A:강제종료 16:누군가접속시도
	}
}
