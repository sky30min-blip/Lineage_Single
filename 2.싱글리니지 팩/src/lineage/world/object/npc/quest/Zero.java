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

public class Zero extends QuestInstance {
	
	public Zero(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("붉은 망토");
		if(i != null){
			craft_list.put(Lineage.QUEST_ZERO_ROYAL, i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("수색문서"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL){
			if(pc.getLevel() < 15){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "zero6"));
			}else{
				Quest q = QuestController.find(pc, Lineage.QUEST_ZERO_ROYAL);
				if(q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_ZERO_ROYAL);
				switch(q.getQuestStep()){
					case 0:	// 제로 퀘스트 시작.
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "zero1"));
						break;
					case 1:	// 제로퀘스트 완료됨.
						q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV15);
						if(q == null)
							q = QuestController.newQuest(pc, this, Lineage.QUEST_ROYAL_LV15);
						if(q.getQuestStep() == 0)
							// 군터에게 가보라는 창 띄우기.
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "zero5"));
						else
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "zero6"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "zero2"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 수색문서를 건네 준다
		Item craft = craft_list.get(action);
		Quest q = QuestController.find(pc, action);
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
