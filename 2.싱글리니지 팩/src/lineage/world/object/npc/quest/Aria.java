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

public class Aria extends QuestInstance {
	
	public Aria(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("아리아의 보답");
		if(i != null){
			craft_list.put(Lineage.QUEST_ROYAL_LV30, i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("마을 주민들의 유품"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL){
			if(pc.getLevel() < 30){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aria4"));
			}else{
				Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV30);
				if(q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV30);
				switch(q.getQuestStep()){
					case 0:	// 퀘스트 준비.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aria1"));
						break;
					case 1:	// 퀘스트 시작.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aria2"));
						break;
					default:	// 완료됨.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aria3"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "aria4"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("quest 13 aria2")){
			// 우드백 마을을 돕는다.
			Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV30);
			if(q == null)
				q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV30);
			if(q.getQuestStep()==0)
				q.setQuestStep(1);
			toTalk(pc, null);
			
		}else if(action.equalsIgnoreCase("request questitem")){
			// 마을 주민들의 유품을 건네 준다
			Item craft = craft_list.get(Lineage.QUEST_ROYAL_LV30);
			Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV30);
			if(craft!=null && q!=null && q.getQuestStep()==1){
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
