package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Paul extends TeleportInstance {
	
	public Paul(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
//		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telegludin1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
//		if(action.equalsIgnoreCase("teleportURL")){
//			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telegludin2", null, list));
//		}else{
//			super.toTalk(pc, action, type, cbp);
//		}
	}

}
