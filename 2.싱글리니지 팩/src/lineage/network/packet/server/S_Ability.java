package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Ability extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int type, boolean ck){
		if(bp == null)
			bp = new S_Ability(type, ck);
		else
			((S_Ability)bp).toClone(type, ck);
		return bp;
	}
	
	public S_Ability(int type, boolean ck){
		toClone(type, ck);
	}
	
	public void toClone(int type, boolean ck){
		clear();
		writeC(Opcodes.S_OPCODE_ABILITY);
		writeC(type);	// 1-이반, 4-인프라비전, 5-소반
		writeC(ck ? 0x01 : 0x00);
	}
}
