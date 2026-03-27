package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_InterfaceRead extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, byte[] data){
		if(bp == null)
			bp = new S_InterfaceRead(data);
		else
			((S_InterfaceRead)bp).toClone(data);
		return bp;
	}
	
	public S_InterfaceRead(byte[] data){
		toClone(data);
	}
	
	public void toClone(byte[] data){
		clear();
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x29);
		writeB(data);
	}
	
}
