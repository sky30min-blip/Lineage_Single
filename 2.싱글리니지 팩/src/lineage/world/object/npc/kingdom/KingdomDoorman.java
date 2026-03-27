package lineage.world.object.npc.kingdom;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class KingdomDoorman extends object {

	private Npc npc;
	private Kingdom kingdom;
	private List<String> list_html;
	private boolean side;				// 외성쪽인지 내성쪽인지 구분용. [true:외성 false:내성]
	
	public KingdomDoorman(Npc npc, Kingdom kingdom){
		this.npc = npc;
		this.kingdom = kingdom;
		list_html = new ArrayList<String>();
		side = npc.getName().indexOf("외성 문지기") >= 0;
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// 성 소속이 아닐경우.
		if(kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId()){
			if(pc.getGm()==0){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gatekeeperop"));
				return;
			}
		}
		
		if(side){
			list_html.clear();
			list_html.add(pc.getName());
			if(pc.getClassType()==Lineage.LINEAGE_CLASS_ROYAL)
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gatekeeper", null, list_html));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gatekeeper", null, list_html));
		}else{
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gatekeeper2"));
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		// 성 소속이 아닐경우.
		if(kingdom.getClanId()==0 || kingdom.getClanId()!=pc.getClanId()){
			if(pc.getGm()==0){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gatekeeperop"));
				return;
			}
		}
		
		// 외성문 열기
		if(action.equalsIgnoreCase("openigate")){
			toOpen();
			
		// 외성문 닫기
		}else if(action.equalsIgnoreCase("closeigate")){
			toClose();
			
		// 내성문 열기
		}else if(action.endsWith("openegate")){
			toOpen();
			
		// 내성문 닫기
		}else if(action.endsWith("closeegate")){
			toClose();
			
		}
	}
	
	/**
	 * 근처에있는 성문 닫기.
	 */
	private void toClose(){
		for(object o : getInsideList()){
			if(o instanceof KingdomDoor){
				KingdomDoor kd = (KingdomDoor)o;
				kd.toClose();
			}
		}
	}
	
	/**
	 * 근처에있는 성문 열기.
	 */
	private void toOpen(){
		for(object o : getInsideList()){
			if(o instanceof KingdomDoor){
				KingdomDoor kd = (KingdomDoor)o;
				kd.toOpen();
			}
		}
	}
}
