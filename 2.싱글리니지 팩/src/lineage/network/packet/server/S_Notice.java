package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Notice extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, String msg){
		if(bp == null)
			bp = new S_Notice(msg);
		else
			((S_Notice)bp).toClone(msg);
		return bp;
	}
	
	public S_Notice(String msg){
		toClone(msg);
	}
	
	public void toClone(String msg){
		clear();
		writeC(Opcodes.S_OPCODE_NOTICE);
		writeS(msg);
	}
	
}
