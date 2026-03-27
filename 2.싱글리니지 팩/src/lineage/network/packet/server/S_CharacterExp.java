package lineage.network.packet.server;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.Character;

public class S_CharacterExp extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha){
		if(bp == null)
			bp = new S_CharacterExp(cha);
		else
			((S_CharacterExp)bp).toClone(cha);
		return bp;
	}
	
	public S_CharacterExp(Character cha){
		toClone(cha);
	}
	
	public void toClone(Character cha){
		clear();
		writeC(Opcodes.S_OPCODE_EXP);
		writeC(cha.getLevel());
		if(Lineage.server_version<=144){
			int exp = 0;
			Exp e = ExpDatabase.find(cha.getLevel());
			if(e!=null && e.getBonus()-cha.getExp()>0){
				double a = e.getExp()-(e.getBonus()-cha.getExp());
				double b = e.getExp();
				exp = (int)((a/b)*30);
			}
			writeC(exp);	// 경험치게이지바 30이 100%
		}else{
			writeD((int)cha.getExp());
		}
	}
}
