package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_ObjectEffectLocation extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int effect, int x, int y){
		if(bp == null)
			bp = new S_ObjectEffectLocation(effect, x, y);
		else
			((S_ObjectEffectLocation)bp).clone(effect, x, y);
		return bp;
	}
	
	public S_ObjectEffectLocation(int effect, int x, int y){
		clone(effect, x, y);
	}
	
	public void clone(int effect, int x, int y){
		clear();
		writeC(Opcodes.S_OPCODE_EFFECTLOC);
		writeH(x);
		writeH(y);
		writeH(effect);
	}

}
