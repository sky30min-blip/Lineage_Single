package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Quest;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Gatekeeper extends TeleportInstance {

	private boolean isDungeon;	// 수련동굴 1명만 접근 가능하도록 하기위한 변수.
	
	public Gatekeeper(Npc npc){
		super(npc);
		
		isDungeon = false;
		// 해당 npc 전역 변수 잡아주기. 해당 던전에는 1명만 허용가능 하기때문에 사용자가 월드를 나가거나 해당 던전을 나갈때 이 npc 에게 알리기 위한 용도로 사용하기 위해.
		QuestController.setGateKeeper(this);
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
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "qguard"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_KNIGHT_LV30);
		if(q!=null && q.getQuestStep()==3){
			if(action.equalsIgnoreCase("teleportURL")){
				if(isDungeon)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "qguardn"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "qguards"));
			}else if(action.equalsIgnoreCase("teleport gerard-dungen")){
				boolean tel = true;
				// 붉은기사의검 만 착용중인지 확인.
				for(int i=0 ; i<Lineage.SLOT_NONE ; ++i){
					ItemInstance ii = pc.getInventory().getSlot(i);
					if(ii!=null && i!=Lineage.SLOT_WEAPON){
						tel = false;
						break;
					}else if(i==Lineage.SLOT_WEAPON){
						if(ii==null){
							tel = false;
							break;
						}else{
							if(ii.getItem().getNameIdNumber() != 313){
								tel = false;
								break;
							}
						}
					}
				}
				if(tel){
					if(isDungeon){
						pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "qguardn"));
					}else{
						pc.toPotal(32769, 32768, 22);
						isDungeon = true;
					}
				}else{
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "qguards"));
				}
			}
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "qguard1"));
		}
	}
}
