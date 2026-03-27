package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Drist extends TeleportInstance {

	public Drist(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLevel() < 13) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "drist"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "drist1"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "drist2"));
		}else if(action.equalsIgnoreCase("teleport hidden-valley")){
			pc.toPotal(32675, 32861, 69);
		}
	}

}
