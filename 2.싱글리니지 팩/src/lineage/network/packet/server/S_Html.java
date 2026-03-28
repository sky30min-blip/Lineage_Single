package lineage.network.packet.server;

import java.util.List;

import lineage.bean.database.NpcTeleport;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.object;

public class S_Html extends ServerBasePacket {

	public S_Html() {
		super();
	}

	/**
	 * 대화 OID 0 + 인라인 HTML. 혈맹({@link S_ClanInfo})용 {@code writeC(0)} 꼬리는
	 * <b>짧은 다이얼로그 이름</b> 전용이라 긴 HTML 뒤에 붙이면 클라 파싱이 깨질 수 있음.
	 * {@link Lineage#autohunt_showhtml_extended} 가 true 이고 {@code server_version > 144} 이면
	 * 빈 request + {@code writeH(0)} 까지 붙인다. false 이면 문자열만 보낸다(2인자에 가깝게).
	 */
	static synchronized public BasePacket cloneHtmlOidZero(BasePacket bp, String html) {
		if (bp == null)
			bp = new S_Html();
		((S_Html) bp).cloneHtmlOidZero(html);
		return bp;
	}

	public void cloneHtmlOidZero(String html) {
		clear();
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(0);
		writeS(html);
		if (!Lineage.autohunt_showhtml_extended)
			return;
		if (Lineage.server_version > 144)
			writeS("");
		writeH(0);
	}

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
