package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BuffMagicDoll extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int time){
		if(bp == null)
			bp = new S_BuffMagicDoll(time);
		else
			((S_BuffMagicDoll)bp).clone(time);
		return bp;
	}
	
	public S_BuffMagicDoll(int time){
		clone(time);
	}
	
	public void clone(int time){
		clear();
		
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		// 인형 구분자.
		writeC(56);
		// 시간
		writeH(time);
	}
	
}
