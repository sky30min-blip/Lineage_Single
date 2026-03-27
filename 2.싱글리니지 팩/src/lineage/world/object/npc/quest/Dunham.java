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

public class Dunham extends QuestInstance {

	public Dunham(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("기름 망토");
		if (i != null) {
			craft_list.put(Lineage.QUEST_OILSKINMANT, i);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_OILSKINMANT);
		if(q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_OILSKINMANT);
		
		if(q.getQuestStep() == 0)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dunham1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dunham2"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
	    if(action.equalsIgnoreCase("get")){
	    	Item craft = craft_list.get(Lineage.QUEST_OILSKINMANT);
	        Quest q = QuestController.find(pc, Lineage.QUEST_OILSKINMANT);
	        if(q != null && q.getQuestStep() == 0){
	            // 아이템 지급.
	            CraftController.toCraft(this, pc, craft, 1, true);
	            // 퀘스트 스탭 변경.
	            q.setQuestStep(1);
	            //창 제거
	            pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
	        }
	    }
	}
}