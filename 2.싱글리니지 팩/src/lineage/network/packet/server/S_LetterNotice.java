package lineage.network.packet.server;

import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;

public class S_LetterNotice extends ServerBasePacket {
	public static synchronized BasePacket clone(BasePacket paramBasePacket,
			String[] paramArrayOfString) {
		if (paramBasePacket == null)
			paramBasePacket = new S_LetterNotice(paramArrayOfString);
		else
			((S_LetterNotice) paramBasePacket).toClone(paramArrayOfString);
		return paramBasePacket;
	}

	public S_LetterNotice(String[] paramArrayOfString) {
		toClone(paramArrayOfString);
	}

	public void toClone(String[] paramArrayOfString) {
		clear();
		writeC(Opcodes.S_OPCODE_BOARDREAD);
		writeD(0L);
		writeS(paramArrayOfString[0]);
		writeS(paramArrayOfString[1]);
		writeS("");
		writeS(paramArrayOfString[2]);
	}
}

