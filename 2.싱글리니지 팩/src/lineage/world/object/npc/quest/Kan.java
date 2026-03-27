package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Kan extends QuestInstance {

	public Kan(Npc npc){
		super(npc);
		
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_DARKELF){
			// 15레벨 퀘스트
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kanguard5"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "kanguard4"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		
	}

}
