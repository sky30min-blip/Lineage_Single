package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Market_telepoter extends TeleportInstance {

	// 어느 마을에 시장텔레포터인지 구분용.
	private String type;

	public Market_telepoter(Npc npc) {
		super(npc);
	}

	@Override
	public void setTitle(String title) {
		type = title;
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (type.equalsIgnoreCase("giran")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telegrtz1"));
		} else if (type.equalsIgnoreCase("gludin")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telegltz1"));
		} else if (type.equalsIgnoreCase("silver")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telesktz1"));
		} else if (type.equalsIgnoreCase("oren")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "teleortz1"));
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("teleportURL")) {
			if (this.type.equalsIgnoreCase("giran"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telegrtz2", null, list.get(0)));
			else if (this.type.equalsIgnoreCase("gludin"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telegltz2", null, list.get(0)));
			else if (this.type.equalsIgnoreCase("silver"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telesktz2", null, list.get(0)));
			else if (this.type.equalsIgnoreCase("oren"))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "teleortz2", null, list.get(0)));
		}else{
			super.toTalk(pc, action, type, cbp);
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		}
	}
}
