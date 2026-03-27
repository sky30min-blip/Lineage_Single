package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class FirstTeleporter extends TeleportInstance {

	public FirstTeleporter(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(getMap()==68)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "stel"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "htel"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			if(getMap()==68)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "stel1"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "htel1"));
		}else{
			if(action.equalsIgnoreCase("teleport hidden-velley-for-newbie"))
				pc.toPotal(33083, 33387, 4);
			else
				pc.toPotal(32597, 32916, 0);
		}
	}

}
