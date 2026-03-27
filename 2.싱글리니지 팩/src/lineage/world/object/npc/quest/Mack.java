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

public class Mack extends QuestInstance {
	
	public Mack(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL){
			if(pc.getLevel() < 45){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meg4"));
			}else{
				Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV45);
				if(q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV45);
				switch(q.getQuestStep()){
					case 0:	// 퀘스트 준비.
					case 1:	// 퀘스트 시작.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meg4"));
						break;
					case 2:	// '[배신당한 오크대장] 왕가의 문장 조각 조각 획득' 상태.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meg1"));
						break;
					case 3:	// 빼앗긴 영혼 요청 상태.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meg2"));
						break;
					default:	// 완료됨.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "masha4"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "meg4"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("quest 17 meg2")){
			// 빼앗긴 영혼
			Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV45);
			if(q == null)
				q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV45);
			if(q.getQuestStep()==2)
				q.setQuestStep(3);
			toTalk(pc, null);
		}else if(action.equalsIgnoreCase("request royal family piece b")){
			// 영혼의 편지를 건네 준다.
			
		}
	}
}
