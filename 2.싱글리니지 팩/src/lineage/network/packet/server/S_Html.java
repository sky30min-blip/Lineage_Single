package lineage.network.packet.server;

import java.util.List;

import lineage.bean.database.NpcTeleport;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.object;

public class S_Html extends ServerBasePacket {

	static synchronized public BasePacket clone(BasePacket bp, object o, String html) {
		if (bp == null)
			bp = new S_Html(o, html);
		else
			((S_Html) bp).clone(o, html);
		return bp;
	}

	static synchronized public BasePacket clone(BasePacket bp, object o, String html, String request, List<?> list) {
		if (bp == null)
			bp = new S_Html(o, html, request, list);
		else
			((S_Html) bp).clone(o, html, request, list);
		return bp;
	}

	public S_Html(object o, String html) {
		clone(o, html);
	}

	public S_Html(object o, String html, String request, List<?> list) {
		clone(o, html, request, list);
	}

	public void clone(object o, String html) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(o.getObjectId());
		writeS(html);
	}

	public void clone(object o, String html, String request, List<?> list) {
		clear();

		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(o.getObjectId());
		writeS(html);
		if (Lineage.server_version > 144)
			writeS(request);
		if (list == null) {
			writeH(0);
		} else {
			writeH(list.size());
			for (Object obj : list) {
				if (obj instanceof NpcTeleport)
					writeS(String.valueOf(((NpcTeleport) obj).getPrice()));
				else
					writeS(String.valueOf(obj));
			}
		}
	}
}
