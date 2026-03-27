package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectRemove extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectRemove(o.getObjectId());
		else
			((S_ObjectRemove)bp).clone(o.getObjectId());
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, long object_id){
		if(bp == null)
			bp = new S_ObjectRemove(object_id);
		else
			((S_ObjectRemove)bp).clone(object_id);
		return bp;
	}
	
	public S_ObjectRemove(long object_id){
		clone(object_id);
	}
	
	public void clone(long object_id){
		clear();
		writeC(Opcodes.S_OPCODE_DELETEOBJECT);
		writeD(object_id);
	}
	
}
