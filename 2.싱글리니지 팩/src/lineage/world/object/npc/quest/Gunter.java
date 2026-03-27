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

public class Gunter extends QuestInstance {

	public Gunter(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("마법서 (트루 타겟)");
		if(i != null){
			craft_list.put(Lineage.QUEST_ROYAL_LV15, i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("생명의 비밀"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("붉은 기사의 검");
		if(i != null){
			craft_list.put("request sword of red knights", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("웅골리언트의 발톱"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				if(pc.getLevel() < 15){
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterp12"));
				}else{
					Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV15);
					if(q == null)
						q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV15);
					switch(q.getQuestStep()){
						case 0:	// 군주 퀘스트 시작.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterp9"));
							break;
						default:	// 군주 15 퀘스트 완료 상태.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterp12"));
							break;
					}
				}
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if(pc.getLevel() < 30){
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterk12"));
				}else{
					Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
					if(q == null)
						q = QuestController.newQuest(pc, this, Lineage.QUEST_KNIGHT_LV30);
					switch(q.getQuestStep()){
						case 0:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterkE1"));
							break;
						case 1:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterkE2"));
							break;
						case 2:
						case 3:
						case 4:
						case 5:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterkE3"));
							break;
						case 6:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterkEcg"));
							break;
					}
				}
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "guntere1"));
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterw1"));
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterde1"));
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Item craft = null;
		Quest q = null;
		
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				// 트루 타겟 마법을 요구 한다
				if(action.equalsIgnoreCase("request spellbook112")){
					craft = craft_list.get(action);
					q = QuestController.find(pc, action);
					if(craft!=null && q!=null && q.getQuestStep()==0){
						List<Craft> l = list.get(craft);
						// 재료 확인.
						if(CraftController.isCraft(pc, l, true)){
							// 재료 제거
							CraftController.toCraft(pc, l);
							// 아이템 지급.
							CraftController.toCraft(this, pc, craft, 1, true);
							// 퀘스트 스탭 변경.
							q.setQuestStep( 1 );
							// 안내창 띄우기.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gunterp11"));
						}
					}
				}
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
				// 군터의 시험을 받는다
				if(action.equalsIgnoreCase("quest 14 gunterkE2")){
					if(q!=null && q.getQuestStep()==0){
						// 퀘스트 스탭 변경.
						q.setQuestStep( 1 );
						// 안내창 띄우기.
						toTalk(pc, null);
					}
					
				// 웅골리언트의 발톱을 건네 준다
				}else if(action.equalsIgnoreCase("request sword of red knights")){
					craft = craft_list.get("request sword of red knights");
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
				break;
		}
	}
}
