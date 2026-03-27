package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectInvis extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectInvis(o);
		else
			((S_ObjectInvis)bp).clone(o);
		return bp;
	}
	
	public S_ObjectInvis(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();

		writeC(Opcodes.S_OPCODE_OBJECTINVISA);
		writeD(o.getObjectId());
		writeC(o.isInvis() ? 1 : 0);
	}
}
