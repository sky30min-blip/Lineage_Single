package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;

public class S_CharacterMp extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha){
		if(bp == null)
			bp = new S_CharacterMp(cha);
		else
			((S_CharacterMp)bp).clone(cha);
		return bp;
	}
	
	public S_CharacterMp(Character cha){
		clone(cha);
	}
	
	public void clone(Character cha){
		clear();
		
		writeC(Opcodes.S_OPCODE_MPUPDATE);
		writeH(cha.getNowMp());
		writeH(cha.getTotalMp());
	}
}
