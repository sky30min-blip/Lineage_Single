package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BuffShield extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int time, int type){
		if(bp == null)
			bp = new S_BuffShield(time, type);
		else
			((S_BuffShield)bp).clone(time, type);
		return bp;
	}
	
	public S_BuffShield(int time, int type){
		clone(time, type);
	}
	
	public void clone(int time, int type){
		clear();
		writeC(Opcodes.S_OPCODE_SHIELD);
		writeH(time);	// 마법 딜레이
		writeC(type);	// 2:쉴드아이콘, 3:쉐도우아머, 6:어스스킨, 7:브레스오브어스, 10:아이언스킨
	}
}
