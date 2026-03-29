package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.NpcTalkHeading;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Rozen extends ShopInstance {

	public Rozen(Npc npc) {
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		super.toTalk(pc, cbp);
		NpcTalkHeading.legacyFacePlayerWhenDbOff(this, pc);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rozen1"));
	}
}
