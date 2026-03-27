package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_CharacterDelete extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp){
		if(bp == null)
			bp = new S_CharacterDelete();
		else
			((S_CharacterDelete)bp).toClone();
		return bp;
	}
	
	public S_CharacterDelete(){
		toClone();
	}
	
	public void toClone(){
		clear();
		writeC(Opcodes.S_OPCODE_DETELECHAROK);
		writeC(0x05);
	}
	
}
