package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.controller.NpcTalkHeading;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class MercenaryGroup extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		NpcTalkHeading.legacyFacePlayerWhenDbOff(this, pc);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sdummy1"));
	}
}