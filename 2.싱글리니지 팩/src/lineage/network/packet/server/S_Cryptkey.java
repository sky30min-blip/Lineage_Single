package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Cryptkey extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, long key){
		if(bp == null)
			bp = new S_Cryptkey(key);
		else
			((S_Cryptkey)bp).clone(key);
		return bp;
	}
	
	public S_Cryptkey(long key){
		clone(key);
	}
	
	public void clone(long key){
		clear();
		writeC(Opcodes.S_OPCODE_CRYPTKEY);
		writeL(key);
	}
	
}
