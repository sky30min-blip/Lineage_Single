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

public class Gerard extends QuestInstance {

	public Gerard(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("환생의 물약");
		if(i != null){
			craft_list.put("request potion of rebirth", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("라미아의 비늘"), 1) );
			list.put(i, l);
		}

		i = ItemDatabase.find("붉은 기사의 방패");
		if(i != null){
			craft_list.put("request shield of red knights", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("감사의 편지"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardp1"));
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if(pc.getLevel() < 30){
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkEv1"));
				}else{
					Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
					if(q == null)
						q = QuestController.newQuest(pc, this, Lineage.QUEST_KNIGHT_LV30);
					switch(q.getQuestStep()){
						case 0:
						case 1:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkEv1"));
							break;
						case 2:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkE1"));
							break;
						case 3:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkE2"));
							break;
						case 4:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkE3"));
							break;
						case 5:
						case 6:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkE4"));
							break;
						case 7:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkEcg"));
							break;
					}
				}
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerarde1"));
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardw1"));
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardde1"));
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("quest 16 gerardkE2")){
			// 게라드의 시련을 받는다
			Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
			if(q!=null && q.getQuestStep()==2){
				// 퀘스트 스탭 변경.
				q.setQuestStep( 3 );
				// 안내창 띄우기.
				toTalk(pc, null);
			}
		}else if(action.equalsIgnoreCase("request potion of rebirth")){
			// 라미아의 비늘을 건네 주다
			Item craft = craft_list.get(action);
			Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
			if(craft!=null && q!=null && q.getQuestStep()==3){
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 4 );
					// 안내창 띄우기.
					toTalk(pc, null);
				}
			}
		}else if(action.equalsIgnoreCase("quest 18 gerardkE4")){
			// 환생의 물약의 진실
			Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
			if(q!=null && q.getQuestStep()==4){
				// 퀘스트 스탭 변경.
				q.setQuestStep( 5 );
				// 안내창 띄우기.
				toTalk(pc, null);
			}
		}else if(action.equalsIgnoreCase("request shield of red knights")){
			// 누군가에게 받은 감사의 편지를 건네 준다
			Item craft = craft_list.get(action);
			Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
			if(craft!=null && q!=null && q.getQuestStep()==6){
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 7 );
					// 안내창 띄우기.
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gerardkE5"));
				}
			}
		}
	}

}
