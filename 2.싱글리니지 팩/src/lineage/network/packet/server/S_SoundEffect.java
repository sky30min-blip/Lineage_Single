package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_SoundEffect extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, int effect){
		if(bp == null)
			bp = new S_SoundEffect(effect);
		else
			((S_SoundEffect)bp).toClone(effect);
		return bp;
	}
	static synchronized public BasePacket clone2(BasePacket bp, int effect){
		
			((S_SoundEffect)bp).toClone2(effect);
			return bp;
		
	}
	public S_SoundEffect() {
		//
	}

	public S_SoundEffect(int effect){
		toClone(effect);
	}

	public void toClone2(int effect){
		clear();

		
	}
	public void toClone(int effect){
		clear();

		writeC(Opcodes.S_OPCODE_SOUNDEFFECT);
		writeC(0);
		writeH(effect);
	}
}
