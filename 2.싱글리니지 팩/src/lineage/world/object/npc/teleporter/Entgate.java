package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Entgate extends TeleportInstance {

	public Entgate(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if(pc.getLevel() <= 44)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "entgate3"));
		else if(pc.getLevel() <= 59)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "entgate2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "entgate"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if(action.equalsIgnoreCase("1")) {
			pc.toTeleport(32676, 32960, 521, true);
		} else if(action.equalsIgnoreCase("3")) {
			pc.toTeleport(32537, 32955, 777, true);
		} else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, null));
	}
}