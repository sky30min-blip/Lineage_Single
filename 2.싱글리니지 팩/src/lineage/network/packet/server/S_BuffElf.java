package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_BuffElf extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int type, int time){
		if(bp == null)
			bp = new S_BuffElf(type, time);
		else
			((S_BuffElf)bp).clone(type, time);
		return bp;
	}
	
	public S_BuffElf(int type, int time){
		clone(type, time);
	}
	
	public void clone(int type, int time){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x16);	// 엘프 아이콘 표현용
		writeC(type);	// 147:파이어웨폰, 148:윈드샷, 154:블레스오브파이어, 155:아이오브스톰, 162:버닝웨폰, 165:스톰샷
		writeH(time);;	// 시간
	}

}
