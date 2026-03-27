package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;

public class S_CharacterSpMr extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha){
		if(bp == null)
			bp = new S_CharacterSpMr(cha);
		else
			((S_CharacterSpMr)bp).clone(cha);
		return bp;
	}
	
	public S_CharacterSpMr(Character cha){
		clone(cha);
	}
	
	public void clone(Character cha){
		clear();

		writeC(Opcodes.S_OPCODE_ChangeSpMr);
		writeC(SkillController.getSp(cha));	// + sp
		writeC(SkillController.getMr(cha, true));	// + mr
	}
}
