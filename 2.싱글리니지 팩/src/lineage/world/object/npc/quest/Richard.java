package lineage.world.object.npc.quest;

import lineage.bean.lineage.Quest;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Richard extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL){
			Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV45);
			if(q==null || q.getQuestStep()==0)
				// 퀘스트가 진행중이지 않거나 마샤의 시험을 받지 않은상태.
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "richard3"));
			else if(q.getQuestStep()==1)
				// 마샤의시험을 받은 초기상태.
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "richard1"));
			else
				// 왕가의 문장을 획득한 상태.
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "richard4"));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "richard3"));
		}
	}
}
