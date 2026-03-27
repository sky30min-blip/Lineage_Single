package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectCriminal extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, int time){
		if(bp == null)
			bp = new S_ObjectCriminal(o, time);
		else
			((S_ObjectCriminal)bp).clone(o, time);
		return bp;
	}
	
	public S_ObjectCriminal(object o, int time){
		clone(o, time);
	}
	
	public void clone(object o, int time){
		clear();
		writeC(Opcodes.S_OPCODE_CRIMINAL);
		writeD(o.getObjectId());
		writeH(time);
	}

}
