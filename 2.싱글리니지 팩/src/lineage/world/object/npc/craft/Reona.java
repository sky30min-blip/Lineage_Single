package lineage.world.object.npc.craft;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.util.Util;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Reona extends CraftInstance {

	public Reona(Npc npc){
		super(npc);
		
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// pc 쪽으로 방향 전환.
		setHeading( Util.calcheading(this, pc.getX(), pc.getY()) );
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "reona1"));
	}
}
