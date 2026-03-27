package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Dilong extends QuestInstance {

	private boolean isDungeon;	// 1명만 접근 가능하도록 하기위한 변수.
	
	public Dilong(Npc n){
		super(n);
		
		isDungeon = false;
		// 해당 npc 전역 변수 잡아주기. 해당 던전에는 1명만 허용가능 하기때문에 사용자가 월드를 나가거나 해당 던전을 나갈때 이 npc 에게 알리기 위한 용도로 사용하기 위해.
		QuestController.setDilong(this);
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
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_WIZARD){
			Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
			if(q == null){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilong2"));
			}else{
				switch(q.getQuestStep()){
					case 1:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilong1"));
						break;
					case 4:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilong3"));
						break;
					default:
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilong2"));
						break;
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilong2"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
		if(q==null || q.getQuestStep()!=1 || isDungeon){
			toTalk(pc, null);
		}else{
			if(action.endsWith("teleport mage-quest-dungen")){
				// 마력의 문을 통과한다.
				pc.toPotal(32791, 32789, 201);
				isDungeon = true;
			}else if(action.equalsIgnoreCase("teleportURL")){
				// 언데드의 던전으로
				if(isDungeon)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilongn"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "dilongs"));
			}
		}
	}
	
}
