package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.world.object.Character;

public class S_ObjectHitratio extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, Character cha, boolean visual) {
		if (bp == null)
			bp = new S_ObjectHitratio(cha, visual);
		else
			((S_ObjectHitratio) bp).clone(cha, visual);
		return bp;
	}

	public S_ObjectHitratio(Character cha, boolean visual) {
		clone(cha, visual);
	}

	public void clone(Character cha, boolean visual) {
		clear();
		writeC(Opcodes.S_OPCODE_HITRATIO);
		writeD(cha.getObjectId());
		if (visual)
			writeC(cha.getHpPercent());
		else
			writeC(0xFF);
	}
}
