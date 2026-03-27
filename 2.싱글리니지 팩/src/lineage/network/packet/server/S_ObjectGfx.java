package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectGfx extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectGfx(o);
		else
			((S_ObjectGfx)bp).clone(o);
		return bp;
	}
	
	public S_ObjectGfx(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_POLY);
		writeD(o.getObjectId());
		writeH(o.getGfx());
		writeC(o.getGfxMode());
		writeC(0xFF);
		writeC(0xFF);
	}
	
}
