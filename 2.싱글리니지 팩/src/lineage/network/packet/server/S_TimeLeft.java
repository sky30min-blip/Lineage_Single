package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_TimeLeft extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_TimeLeft();
		else
			((S_TimeLeft)bp).toClone();
		return bp;
	}
	
	public S_TimeLeft(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.S_OPCODE_TIMELEFT);
		writeC(0xb5);
		writeC(0x01);
	}
	
}
