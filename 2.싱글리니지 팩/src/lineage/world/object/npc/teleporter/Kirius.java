package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Kirius extends TeleportInstance {
	
	public Kirius(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kirius1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kirius2", null, list.get(0)));
		}else if(action.equalsIgnoreCase("teleportURLA")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kirius3", null, list.get(1)));
		}else{
			super.toTalk(pc, action, type, cbp);
		}
	}

}
