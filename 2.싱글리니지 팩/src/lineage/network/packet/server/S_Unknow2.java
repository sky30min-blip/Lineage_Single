package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Unknow2 extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int idx){
		if(bp == null)
			bp = new S_Unknow2(idx);
		else
			((S_Unknow2)bp).toClone(idx);
		return bp;
	}
	
	public S_Unknow2(int idx){
		toClone(idx);
	}
	
	public void toClone(int idx){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		switch(idx){
			case 1:
				/** 우호도 UI 표시 
				 * + 욕망의 동굴
				 * - 그림자 신전 
				 */
//				public static final int KARMA = 87;
				writeC(0x57);
				writeD(0);
				break;
			case 2:
				/** 우호도 다음에 오는
				 * 불분명 패킷 2개 ↓
				 */
//				public static final int LOGIN_UNKNOWN1 = 88;
//				public static final int LOGIN_UNKNOWN2 = 101;
				writeC(0x58);
				writeH(0);
				break;
			case 3:
				writeC(0x65);
				writeC(0);
				break;
		}
	}

}
