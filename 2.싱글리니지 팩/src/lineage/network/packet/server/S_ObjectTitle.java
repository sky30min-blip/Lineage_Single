package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectTitle extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectTitle(o);
		else
			((S_ObjectTitle)bp).clone(o);
		return bp;
	}
	
	public S_ObjectTitle(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_TITLECHANGE);
		writeD(o.getObjectId());
		writeS(o.getTitle());
	}
}
