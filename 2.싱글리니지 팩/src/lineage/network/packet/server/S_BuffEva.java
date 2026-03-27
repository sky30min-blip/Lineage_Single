package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_BuffEva extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, int time){
		if(bp == null)
			bp = new S_BuffEva(o, time);
		else
			((S_BuffEva)bp).clone(o, time);
		return bp;
	}
	
	public S_BuffEva(object o, int time){
		clone(o, time);
	}
	
	public void clone(object o, int time){
		clear();
		writeC(Opcodes.S_OPCODE_AQUABREATH);
		writeD(o.getObjectId());
		writeH(time);
	}

}
