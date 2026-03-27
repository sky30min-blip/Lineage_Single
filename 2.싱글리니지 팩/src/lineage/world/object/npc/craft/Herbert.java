package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class Herbert extends CraftInstance {
	
	public Herbert(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("티셔츠");
		if(i != null){
			craft_list.put("request t-shirt", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 3) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 2) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 30000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("마법 망토");
		if(i != null){
			craft_list.put("request cloak of magic resistance", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 2) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 1000) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("보호 망토");
		if(i != null){
			craft_list.put("request cloak of protection", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 옷감"), 5) );
			l.add( new Craft(ItemDatabase.find("파란 옷감"), 5) );
			l.add( new Craft(ItemDatabase.find("하얀 옷감"), 10) );
			l.add( new Craft(ItemDatabase.find("아데나"), 20000) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "herbert2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "herbert1"));
	}
	
	/**
	 * 제작처리 마지막 부분. : 중복코드 방지용
	 * 
	 * @param pc
	 * @param action
	 * @param count
	 */
	private void toLeather(PcInstance pc, String action, long count) {
		Item craft = craft_list.get(action);

		if (craft != null) {
			int max = CraftController.getMax(pc, list.get(craft));
			if (count > 0 && max > 0 && count <= max) {
				// 재료 제거
				for (int i = 0; i < count; ++i)
					CraftController.toCraft(pc, list.get(craft));
				// 제작 아이템 지급.
				int jegop = craft.getListCraft().get(action) == null ? 0 : craft.getListCraft().get(action);
				if (jegop == 0)
					CraftController.toCraft(this, pc, craft, count, true);
				else
					CraftController.toCraft(this, pc, craft, count * jegop, true);
			}
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {

		if (pc.getInventory() != null) {
			Item craft = craft_list.get(action);
			// 재료가 부족 할 시 창 닫기
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));

			if (craft != null) {
				// 티셔츠
				if (action.equalsIgnoreCase("request t-shirt")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "herbertC1", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 마법 망토
				if (action.equalsIgnoreCase("request cloak of magic resistance")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "herbertC2", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}

			if (craft != null) {
				// 보호 망토
				if (action.equalsIgnoreCase("request cloak of protection")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "herbertC3", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}

