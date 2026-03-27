package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;

public class S_BuffDex extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha, int time, int dex){
		if(bp == null)
			bp = new S_BuffDex(cha, time, dex);
		else
			((S_BuffDex)bp).clone(cha, time, dex);
		return bp;
	}
	
	public S_BuffDex(Character cha, int time, int dex){
		clone(cha, time, dex);
	}
	
	public void clone(Character cha, int time, int dex){
		clear();
		writeC(Opcodes.S_OPCODE_MAGICDEX);
		writeH(time);				// 시간
		writeC(cha.getTotalDex());	// 덱스
		writeC(dex);				// 증가한 값
	}
}
