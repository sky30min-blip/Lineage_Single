package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Marshall extends QuestInstance {
	
	public Marshall(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("수호자의 반지");
		if(i != null){
			craft_list.put(Lineage.QUEST_ROYAL_LV45, i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("[배신당한 오크대장] 왕가의 문장 조각"), 1) );
			l.add( new Craft(ItemDatabase.find("[맥] 왕가의 문장 조각"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLevel() < 45){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "masha"));
		}else{
			switch(pc.getClassType()){
				case Lineage.LINEAGE_CLASS_ROYAL:
					Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV45);
					if(q == null)
						q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV45);
					switch(q.getQuestStep()){
						case 0:	// 퀘스트 준비.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "masha1"));
							break;
						case 1:	// 퀘스트 시작.
						case 2:	// '[배신당한 오크대장] 왕가의 문장 조각 조각 획득' 상태.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "masha2"));
							break;
						default:	// 완료됨.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "masha4"));
							break;
					}
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mashak1"));
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mashae1"));
					break;
				default:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "masha"));
					break;
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("quest 15 masha2")){
			// 마샤의 시험을 받는다
			Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV45);
			if(q == null)
				q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV45);
			if(q.getQuestStep()==0)
				q.setQuestStep(1);
			toTalk(pc, null);
		}else if(action.equalsIgnoreCase("request ring of guardian")){
			// 왕가의 문장 조각을 건네 준다
			Item craft = craft_list.get(Lineage.QUEST_ROYAL_LV45);
			Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV45);
			if(craft!=null && q!=null && q.getQuestStep()==2){
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 2 );
					// 안내창 띄우기.
					toTalk(pc, null);
				}
			}
		}
	}

}
