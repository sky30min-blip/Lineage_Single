package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class OrcfbuWoo extends TeleportInstance {

	public OrcfbuWoo(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(getMap() == 4)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orcfbuwoo1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orcfbawoo1"));
	}

}
