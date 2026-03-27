package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Unknow3 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_Unknow3();
		else
			((S_Unknow3)bp).toClone();
		return bp;
	}
	
	public S_Unknow3(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN3);
		writeC(0xe8);
		writeC(0x0c);
		writeC(0x01);
		writeC(0x00);
		writeC(0xa7);
		writeC(0x7f);
		writeC(0x5b);
		writeC(0x80);
		writeC(0x02);
		writeC(0x6c);
		writeC(0x01);
		writeC(0x00);
		writeC(0x07);
		writeC(0x08);
		writeC(0x00);
	}
}
