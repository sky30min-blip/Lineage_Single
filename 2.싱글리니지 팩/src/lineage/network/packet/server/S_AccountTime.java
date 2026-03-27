package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_AccountTime extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int atime){
		if(bp == null)
			bp = new S_AccountTime(atime);
		else
			((S_AccountTime)bp).toClone(atime);
		return bp;
	}
	
	public S_AccountTime(int atime){
		toClone(atime);
	}
	
	public void toClone(int atime){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x3D);	// 구분자
		writeD(atime);	// 남은 계정 시간
		writeC(0x00);	// 예약된 결제 건수
		writeC(0x00);
	}

}
