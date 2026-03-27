package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_BuffTrueTarget extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, String msg){
		if(bp == null)
			bp = new S_BuffTrueTarget(o, msg);
		else
			((S_BuffTrueTarget)bp).clone(o, msg);
		return bp;
	}
	
	public S_BuffTrueTarget(object o, String msg){
		clone(o, msg);
	}
	
	public void clone(object o, String msg){
		clear();
		writeC(Opcodes.S_OPCODE_TRUETARGET);
		writeD(o.getObjectId());
		writeH(o.getX());
		writeH(o.getY());
		writeS(msg);
	}
}
