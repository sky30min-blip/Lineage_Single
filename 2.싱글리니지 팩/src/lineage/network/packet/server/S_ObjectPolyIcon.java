package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ObjectPolyIcon extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int time){
		if(bp == null)
			bp = new S_ObjectPolyIcon(time);
		else
			((S_ObjectPolyIcon)bp).clone(time);
		return bp;
	}
	
	public S_ObjectPolyIcon(int time){
		clone(time);
	}
	
	public void clone(int time){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x23);	// ID
		writeH(time);	// 시간
	}
	
}
