package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class GateKeeper extends TeleportInstance {

	public GateKeeper(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (getNpc().getNameIdNumber()) {
		case 17593: // 켄트성 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "teleckent1"));
			break;
		case 17594: // 윈다우드성 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecwoods1"));
			break;
		case 17595: // 기란성 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecgiran1"));
			break;
		case 17596: // 지저성 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecdwarf1"));
			break;
		case 17597: // 아덴성 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecaden1"));
			break;
		case 17598: // 하이네성 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecheine1"));
			break;
		case 17599: // 오크요새 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecorc1"));
			break;
		case 17600: // 디아드요새 게이트키퍼
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telecdiad1"));
			break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		int nameIdNumber = getNpc().getNameIdNumber();
		if (action.equalsIgnoreCase("teleportURL")) {
			String Template = "";
			switch (nameIdNumber) {
			case 17593:
				Template = "teleckent2";
				break;
			case 17594:
				Template = "telecwoods2";
				break;
			case 17595:
				Template = "telecgiran2";
				break;
			case 17596:
				Template = "telecdwarf2";
				break;
			case 17597:
				Template = "telecaden2";
				break;
			case 17598:
				Template = "telecheine2";
				break;
			case 17599:
				Template = "telecorc2";
				break;
			case 17600:
				Template = "telecdiad2";
				break;
			default:
				break;
			}
			if (!Template.isEmpty()) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, Template, null, list.get(0)));
				return;
			}
		}
		super.toTalk(pc, action, type, cbp);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	}
}