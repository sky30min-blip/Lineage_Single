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
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.PcInstance;

public class FishLady extends CraftInstance {

	public FishLady(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );

		Item i = ItemDatabase.find("수리된 목걸이");
		if(i != null){
			craft_list.put("request not_broken amulet", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("붉은 빛 나는 물고기"), 1) );
			l.add( new Craft(ItemDatabase.find("깨진 목걸이"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("수리된 반지");
		if(i != null){
			craft_list.put("request not_broken ring left", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("초록 빛 나는 물고기"), 1) );
			l.add( new Craft(ItemDatabase.find("깨진 반지"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("수리된 귀걸이");
		if(i != null){
			craft_list.put("request not_broken earring", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("파란 빛 나는 물고기"), 1) );
			l.add( new Craft(ItemDatabase.find("깨진 귀걸이"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("갑옷 마법 주문서");
		if(i != null){
			craft_list.put("request scroll of enchant armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("반짝이는 비늘"), 15) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		// pc 쪽으로 방향 전환.
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "f_grandma1"));
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
				// 깨진 목걸이
				if (action.equalsIgnoreCase("request not_broken amulet")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "f_grandma2", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 깨진 반지
				if (action.equalsIgnoreCase("request not_broken ring left")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "f_grandma2", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 깨진 귀걸이
				if (action.equalsIgnoreCase("request not_broken earring")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "f_grandma2", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			
			if (craft != null) {
				// 반짝이는 비늘
				if (action.equalsIgnoreCase("request scroll of enchant armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "f_grandma3", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}


