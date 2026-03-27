package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectLight extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectLight(o);
		else
			((S_ObjectLight)bp).clone(o);
		return bp;
	}
	
	public S_ObjectLight(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();

		writeC(Opcodes.S_OPCODE_OBJLIGHT);
		writeD(o.getObjectId());
		writeC(o.getLight());
	}
}
