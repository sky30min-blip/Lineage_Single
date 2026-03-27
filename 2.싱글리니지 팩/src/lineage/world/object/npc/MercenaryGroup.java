package lineage.world.object.npc;

import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class MercenaryGroup extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// pc 쪽으로 방향 전환.
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "sdummy1"));
	}
}