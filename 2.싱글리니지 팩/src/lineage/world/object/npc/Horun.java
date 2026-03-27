package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Horun extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch(pc.getClassType()) {
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if(pc.getLevel() < 50)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horunev2"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horunev1"));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horun1"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "horunev1"));
				break;
		}
	}

}
