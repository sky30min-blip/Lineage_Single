package lineage.world.object.npc.pet;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetMasterInstance;

public class Dick extends PetMasterInstance {

	public Dick(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dick1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("depositnpc")){
			// 맡기기
			if(!toPush(pc))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dick4"));
			
		}else if(action.equalsIgnoreCase("withdrawnpc")){
			// 찾기
			if(!toGet(pc))
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dick3"));
		}
	}
}
