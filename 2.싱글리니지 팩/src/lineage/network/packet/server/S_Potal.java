package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_Potal extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int currentMap, int nextMap){
		if(bp == null)
			bp = new S_Potal(currentMap, nextMap);
		else
			((S_Potal)bp).toClone(currentMap, nextMap);
		return bp;
	}
	
	public S_Potal(int currentMap, int nextMap){
		toClone(currentMap, nextMap);
	}
	
	public void toClone(int currentMap, int nextMap){
		clear();
		
		writeC(Opcodes.S_OPCODE_POTAL);
		writeH(nextMap);
		switch(currentMap){
			case 0:
				writeC(0xFC);
				writeC(0xFD);
				writeC(0x60);
				writeC(0x00);
				break;
			case 3:
				writeC(0x46);
				writeC(0xFF);
				writeC(0x2E);
				writeC(0x00);
				break;
			case 4:
				writeC(0xf4);
				writeC(0x09);
				writeC(0x9b);
				writeC(0xfd);
				break;
			case 63:
				writeC(0x4c);
				writeC(0x00);
				writeC(0x64);
				writeC(0x00);
				break;
			case 75:
				writeC(0xf6);
				writeC(0xff);
				writeC(0x86);
				writeC(0x00);
				break;
			default:
				writeC(0xf4);
				writeC(0x09);
				writeC(0x9b);
				writeC(0xfd);
				break;
		}
	}
}
