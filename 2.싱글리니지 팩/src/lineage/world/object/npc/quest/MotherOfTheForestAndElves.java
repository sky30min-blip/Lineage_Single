package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
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

public class MotherOfTheForestAndElves extends QuestInstance {
	
	public MotherOfTheForestAndElves(){
		super(null);
		
		Item i = ItemDatabase.find("요정족 보물");
		if(i != null){
			craft_list.put("request questitem2", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("저주 받은 정령서"), 1) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ELF:
				if(pc.getLevel() < 30){
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "mothere1"));
				}else{
					Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV30);
					if(q == null){
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEE1"));
					}else{
						switch(q.getQuestStep()){
							case 0:
								pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEE2"));
								break;
							case 1:
								pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEE3"));
								break;
						}
					}
				}
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherm2"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherm1"));
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV30);
		if(action.equalsIgnoreCase(Lineage.QUEST_ELF_LV30)){
			if(q == null)
				QuestController.newQuest(pc, this, Lineage.QUEST_ELF_LV30);
			toTalk(pc, null);
		}else if(action.equalsIgnoreCase("request questitem2")){
			// 저주 받은 정령서를 건네 준다
			Item craft = craft_list.get(action);
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
