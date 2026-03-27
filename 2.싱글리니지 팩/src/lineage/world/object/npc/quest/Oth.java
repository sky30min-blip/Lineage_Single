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

public class Oth extends QuestInstance {
	
	public Oth(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("민첩함의 투구");
		if(i != null){
			craft_list.put("request dex helmet of elven", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("두다 마라 오크 마법서"), 1) );
			l.add( new Craft(ItemDatabase.find("네루가 오크 마법서"), 1) );
			l.add( new Craft(ItemDatabase.find("간디 오크 마법서"), 1) );
			l.add( new Craft(ItemDatabase.find("아투바 오크 마법서"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("체력의 투구");
		if(i != null){
			craft_list.put("request con helmet of elven", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("두다 마라 오크 마법서"), 1) );
			l.add( new Craft(ItemDatabase.find("네루가 오크 마법서"), 1) );
			l.add( new Craft(ItemDatabase.find("간디 오크 마법서"), 1) );
			l.add( new Craft(ItemDatabase.find("아투바 오크 마법서"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ELF){
			if(pc.getLevel() < 15){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "oth6"));
			}else{
				Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV15);
				if(q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_ELF_LV15);
				switch(q.getQuestStep()){
					case 0:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "oth1"));
						break;
					default:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "oth5"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "oth2"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Item craft = craft_list.get(action);
		if(craft != null){
			Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV15);
			if(q!=null && q.getQuestStep()==0){
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
					toTalk(pc, null);
				}
			}
		}
	}

}
