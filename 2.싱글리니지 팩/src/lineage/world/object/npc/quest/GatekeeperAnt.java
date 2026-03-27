package lineage.world.object.npc.quest;

import lineage.bean.database.Poly;
import lineage.bean.lineage.Quest;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.QuestController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class GatekeeperAnt extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Poly p = PolyDatabase.getPolyName("giant ant soldier");
		if(p != null){
			if(p.getGfxId() == pc.getGfx()){
				Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV30);
				if(q!=null && q.getQuestStep()==1)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ants1"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ants3"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ants2"));
			}
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_ROYAL_LV30);
		if(q==null || q.getQuestStep()!=1)
			return;
		
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "antss"));
		}else if(action.equalsIgnoreCase("teleport mutant-dungen")){
			pc.toPotal(32662, 32796, 217);
			// 군주 주변 3셀의 혈맹원도 함께 텔레포트
			for(object o : pc.getInsideList()){
				if(o.getClanId()>0 && o.getClanId()==pc.getClanId() && Util.isDistance(pc, o, 3))
					o.toPotal(32662, 32796, 217);
			}
		}
	}
}
