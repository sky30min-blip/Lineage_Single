package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;

public class S_BuffStr extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha, int time, int str){
		if(bp == null)
			bp = new S_BuffStr(cha, time, str);
		else
			((S_BuffStr)bp).clone(cha, time, str);
		return bp;
	}
	
	public S_BuffStr(Character cha, int time, int str){
		clone(cha, time, str);
	}
	
	public void clone(Character cha, int time, int str){
		clear();
		writeC(Opcodes.S_OPCODE_MAGICSTR);
		writeH(time);								// 시간
		writeC(cha.getTotalStr());					// 힘
		writeC(cha.getInventory()==null ? 0 : (int)cha.getInventory().getWeightPercent());	// 무게게이지
		writeC(str);								// 증가한 값
	}
}
