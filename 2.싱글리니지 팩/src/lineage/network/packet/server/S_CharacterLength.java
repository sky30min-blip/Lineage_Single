package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_CharacterLength extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int length, int max){
		if(bp == null)
			bp = new S_CharacterLength(length, max);
		else
			((S_CharacterLength)bp).clone(length, max);
		return bp;
	}
	
	public S_CharacterLength(int length, int max){
		clone(length, max);
	}
	
	public void clone(int length, int max){
		clear();
		writeC(Opcodes.S_OPCODE_CHARAMOUNT);
		writeC(length);		// 케릭소유 갯수
		writeC(max);		// 최대 슬롯 1~8
	}
	
}
