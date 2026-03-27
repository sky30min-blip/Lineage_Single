package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class FairyPrincess extends TeleportInstance {

	private boolean isDungeon;	// 1명만 접근 가능하도록 하기위한 변수.
	
	public FairyPrincess(Npc npc){
		super(npc);
		
		isDungeon = false;
		// 해당 npc 전역 변수 잡아주기. 해당 던전에는 1명만 허용가능 하기때문에 사용자가 월드를 나가거나 해당 던전을 나갈때 이 npc 에게 알리기 위한 용도로 사용하기 위해.
		QuestController.setFairyPrincess(this);
	}
	
	/**
	 * 수련동굴을 누군가 사용중인지 확인하는 함수.
	 * @return
	 */
	public boolean isDungeon(){
		return isDungeon;
	}
	
	public void setDungeon(boolean isDungeon){
		this.isDungeon = isDungeon;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV30);
		if(q==null || q.getQuestStep()>0){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fairyp3"));
		}else{
			if(isDungeon)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fairypn"));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, Util.random(0, 1)==0 ? "fairyp1" : "fairyp2"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV30);
		if(isDungeon || q==null){
			toTalk(pc, null);
		}else{
			// 209~212	다크엘프 맵
			// 213~216	다크마르 맵
			if(action.equalsIgnoreCase("teleport dark-elf-dungen")){
				pc.toPotal(32743, 32795, Util.random(209, 212));
			}else if(action.equalsIgnoreCase("teleport darkmar-dungen")){
				pc.toPotal(32729, 32808, 213);
				isDungeon = true;
			}
		}
	}

}
