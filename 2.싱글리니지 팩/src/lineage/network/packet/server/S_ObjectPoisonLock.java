package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectPoisonLock extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectPoisonLock(o);
		else
			((S_ObjectPoisonLock)bp).clone(o);
		return bp;
	}
	
	public S_ObjectPoisonLock(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		
		writeC(Opcodes.S_OPCODE_PoisonAndLock);
		writeD(o.getObjectId());
		// 독
		writeC(o.isPoison() || o.isBuffCurseGhoul() ? 1 : 0);
		// 락
		writeC(o.isLock() ? 1 : 0);
	}
}
