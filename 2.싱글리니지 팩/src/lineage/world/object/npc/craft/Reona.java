package lineage.world.object.npc.craft;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Reona extends CraftInstance {

	public Reona(Npc npc){
		super(npc);
		
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "reona1"));
	}
}
