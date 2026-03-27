package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectBlind extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectBlind(o);
		else
			((S_ObjectBlind)bp).clone(o);
		return bp;
	}
	
	public S_ObjectBlind(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_BlindPotion);
		// 0:보통 1:멀기 2:괴눈고기먹고멀기
		if(o.isBuffCurseBlind()) {
			if(o.isBuffMonsterEyeMeat())
				writeC(2);
			else
				writeC(1);
		} else {
			writeC(0);
		}
	}
}
