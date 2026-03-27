package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_HyperText;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;

public class CraftInstance extends NpcInstance {
	protected Map<Item, List<Craft>> list;			// 제작될아이템(item) 과 연결된 재료 목록
	protected Map<String, Item> craft_list;			// 요청청 문자(action)와 연결될 제작될아이템(item)
	protected List<String> temp_request_list;		// hyper_text 패킷 그릴때 이용되는 변수.
	
	public CraftInstance(Npc npc){
		super(npc);
		craft_list = new HashMap<String, Item>();
		list = new HashMap<Item, List<Craft>>();
		temp_request_list = new ArrayList<String>();
	}
	
	@Override
	public Npc getNpc(){
		return npc;
	}
	
	@Override
	public void setNowHp(int nowHp){
		if(this instanceof GuardInstance)
			super.setNowHp(nowHp);
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		Item craft = craft_list.get(action);

		if(craft != null){
			// 재료 확인.
			if(CraftController.isCraft(pc, list.get(craft), true)){
				// 제작 가능한 최대값 추출.
				int max = CraftController.getMax(pc, list.get(craft));
				if(Lineage.server_version <= 144)
					toFinal(pc, action, max);
				else
					// 패킷 처리.
					pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "request", action, 0, 1, 1, max, temp_request_list));
			}
		}
	}
	
	@Override
	public void toHyperText(PcInstance pc, ClientBasePacket cbp){
		long count = cbp.readD();
		cbp.readC();
		String action = cbp.readS();
		
		toFinal(pc, action, count);
	}
	
	/**
	 * 제작처리 마지막 부분.
	 *  : 중복코드 방지용
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toFinal(PcInstance pc, String action, long count){
		Item craft = craft_list.get(action);
		
		if(craft != null){
			int max = CraftController.getMax(pc, list.get(craft));
			if(count>0 && max>0 && count<=max){
				// 재료 제거
				for(int i=0 ; i<count ; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action)==null ? 0 : craft.getListCraft().get(action);
				if(jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count*jegop, true);
			}
		}
	}
}
