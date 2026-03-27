package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Letter extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int type){
		if(bp == null)
			bp = new S_Letter(type);
		else
			((S_Letter)bp).toClone(type);
		return bp;
	}
	
	public S_Letter(int type){
		toClone(type);
	}
	
	public void toClone(int type){
		clear();
		writeC(Opcodes.S_OPCODE_LETTERREAD);
		writeC(type);
		writeH(0x00);
		if(type==0x00)
			writeC(0xff);
		else
			writeC(0x01);
	}
}
