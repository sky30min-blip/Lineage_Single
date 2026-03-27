package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Cash_Market_telepoter extends object {
	@Override
	public void toTalk (PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 779));
	}
	
	public void toAsk (PcInstance pc, boolean yes) {
		if (pc != null && yes) {
			pc.toPotal(Util.random(32798, 32804), Util.random(32796, 32802), 5001);
		}
	}
}
