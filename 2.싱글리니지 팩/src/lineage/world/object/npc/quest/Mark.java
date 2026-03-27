package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Mark extends QuestInstance {
	
	public Mark(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT){
			if(pc.getLevel() < 30){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mark3"));
			}else{
				Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
				if(q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_KNIGHT_LV30);
				switch(q.getQuestStep()){
					case 6:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "markcg"));
						break;
					default:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mark1"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mark3"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 짐에 대해서
		if(action.equalsIgnoreCase("quest 13 mark2"))
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mark2"));
	}

}
