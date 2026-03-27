package lineage.world.object.npc.quest;

import lineage.bean.database.Poly;
import lineage.bean.lineage.Quest;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class SearchAnt extends object {
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Poly p = PolyDatabase.getPolyName("giant ant");
		if(p != null){
			if(p.getGfxId() == pc.getGfx()){
				Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV30);
				if(q!=null && q.getQuestStep()==1)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ant1"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ant3"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ant2"));
			}
		}
	}
	
}
