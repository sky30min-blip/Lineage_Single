package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.object;

public class S_ObjectRevival extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, object t){
		if(bp == null)
			bp = new S_ObjectRevival(o, t);
		else
			((S_ObjectRevival)bp).clone(o, t);
		return bp;
	}
	
	public S_ObjectRevival(object o, object t){
		clone(o, t);
	}
	
	public void clone(object o, object t){
		clear();
		writeC(Opcodes.S_OPCODE_RESTORE);
		writeD(t.getObjectId());				// 부활될 오브젝트
		writeC(t.getGfxMode());					// 모드 부분
		if(o != null)
			writeD(o.getObjectId());			// 부활시킨 오브젝트
		else
			writeD(t.getObjectId());			// 부활시킨 오브젝트
		writeH(t.getGfx());						// 부활될 오브젝트의 클레스아이디
	}
}
