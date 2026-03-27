package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

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

public class Liri extends FirstQuest {

	public Liri(Npc npc){
		super(npc);
		
		List<Craft> l = new ArrayList<Craft>();
		l.add( new Craft(ItemDatabase.find("말하는 두루마리"), 1) );
		list.put(null, l);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
		if(q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_TALKINGSCROLL);
		
		if(q.getQuestStep() == 1)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orenc1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orenc" + toErrorHtml(0, q.getQuestStep())));
		// 2 = 노섬 혹은 숨계		[현재 위치에서 이전]
		// 3 = 켄트마을 시리아		[현재위치에서 다음]
		// 4 = 우드벡 오실리아
		// 5 = 화전민 호닌
		// 6 = 요숲 치코
		// 7 = 은기사 홉
		// 8 = 기란 터크
		// 9 = 하이네 갈리온
		// 10 = 오렌 길버트
		// 11 = 웰던 포리칸
		// 12 = 아덴 제릭
		// 13 = 침묵 자루만
		// 14 = 완료 메세지.
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("0")){
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if(q!=null && q.getQuestStep()==1){
				List<Craft> l = list.get(null);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 아이템 지급.
					toStep2(pc);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 2 );
					// 창 띄우기.
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orenc0"));
				}
			}
		}
	}

}
