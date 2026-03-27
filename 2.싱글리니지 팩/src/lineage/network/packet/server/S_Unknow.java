package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Unknow extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_Unknow();
		else
			((S_Unknow)bp).toClone();
		return bp;
	}
	
	public S_Unknow(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN1);
		writeC(0x03);
	}
	
}
