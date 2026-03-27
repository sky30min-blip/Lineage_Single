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

public class Ricky extends QuestInstance {
	
	public Ricky(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("기사의 두건");
		if(i != null){
			craft_list.put(Lineage.QUEST_KNIGHT_LV15, i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("흑기사의 서약서"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT){
			if(pc.getLevel() < 15){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "riky6"));
			}else{
				Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV15);
				if(q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_KNIGHT_LV15);
				switch(q.getQuestStep()){
					case 0:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "riky1"));
						break;
					case 1:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "riky5"));
						break;
					default:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rikycg"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "riky2"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("request hood of knight")){
			// 흑기사의 서약서를 건네 준다
			Item craft = craft_list.get(Lineage.QUEST_KNIGHT_LV15);
			Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV15);
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
					toTalk(pc, null);
				}
			}
		}
	}

}
