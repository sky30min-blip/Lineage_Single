package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;

public class S_CharacterHp extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha){
		if(bp == null)
			bp = new S_CharacterHp(cha);
		else
			((S_CharacterHp)bp).clone(cha);
		return bp;
	}
	
	public S_CharacterHp(Character cha){
		clone(cha);
	}
	
	public void clone(Character cha){
		clear();
		
		writeC(Opcodes.S_OPCODE_HPUPDATE);
		writeH(cha.getNowHp());
		writeH(cha.getTotalHp());
		
	}
}
