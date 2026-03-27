package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Poly;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Jim extends QuestInstance {
	
	public Jim(Npc npc){
		super(npc);
		
		Item i = ItemDatabase.find("감사의 편지");
		if(i != null){
			craft_list.put("request letter of gratitude", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("환생의 물약"), 1) );
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Poly p = PolyDatabase.getPolyName("skeleton polymorph");
		if(p != null){
			if(p.getGfxId() == pc.getGfx()){
				Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
				if(q!=null){
					if(q.getQuestStep()<5){
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jim4"));
					}else if(q.getQuestStep()==5){
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jim2"));
					}else{
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jimcg"));
					}
				}else{
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jim4"));
				}
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "jim1"));
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 환생의 물약을 건네준다
		if(action.equalsIgnoreCase("request letter of gratitude")){
			Item craft = craft_list.get(action);
			Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
			if(craft!=null && q!=null && q.getQuestStep()==5){
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 6 );
					// 안내창 띄우기.
					toTalk(pc, null);
				}
			}
		}
	}

}
