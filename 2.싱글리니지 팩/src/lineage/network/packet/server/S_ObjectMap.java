package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.World;
import lineage.world.object.object;

public class S_ObjectMap extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o){
		if(bp == null)
			bp = new S_ObjectMap(o);
		else
			((S_ObjectMap)bp).clone(o);
		return bp;
	}
	
	public S_ObjectMap(object o){
		clone(o);
	}
	
	public void clone(object o){
		clear();
		writeC(Opcodes.S_OPCODE_OBJECTMAP);
		writeH(o.getMap());
		// 물속 표현 부분.
		writeC( World.isAquaMap(o) ? 1 : 0 );
	}
	
}
