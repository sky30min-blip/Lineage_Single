package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Ribian extends TeleportInstance {

	public Ribian(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLevel() < 13)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ribian3"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ribian1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ribian4"));
		}else if(action.equalsIgnoreCase("teleport valley-in")){
			pc.toPotal(32692, 32853, 69);
		}
	}

}
