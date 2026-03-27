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

public class Karif extends CraftInstance {

	public Karif(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );

		Item i = ItemDatabase.find("카리프의 주머니(다이아몬드)");
		if(i != null){
			craft_list.put("request karif bag1", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("다이아몬드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 주머니(에메랄드)");
		if(i != null){
			craft_list.put("request karif bag2", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("에메랄드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 주머니(루비)");
		if(i != null){
			craft_list.put("request karif bag3", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("루비"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 주머니(사파이어)");
		if(i != null){
			craft_list.put("request karif bag4", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("사파이어"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 고급 주머니(다이아몬드)");
		if(i != null){
			craft_list.put("request karif bag5", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급 다이아몬드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 고급 주머니(에메랄드)");
		if(i != null){
			craft_list.put("request karif bag6", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급 에메랄드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 고급 주머니(루비)");
		if(i != null){
			craft_list.put("request karif bag7", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급 루비"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 고급 주머니(사파이어)");
		if(i != null){
			craft_list.put("request karif bag8", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("고급 사파이어"), 1) );
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("카리프의 최고급 주머니(다이아몬드)");
		if(i != null){
			craft_list.put("request karif bag9", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 다이아몬드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 최고급 주머니(에메랄드)");
		if(i != null){
			craft_list.put("request karif bag10", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 에메랄드"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 최고급 주머니(루비)");
		if(i != null){
			craft_list.put("request karif bag11", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 루비"), 1) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("카리프의 최고급 주머니(사파이어)");
		if(i != null){
			craft_list.put("request karif bag12", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("최고급 사파이어"), 1) );
			list.put(i, l);
		}
		
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "karif9"));
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
				// 카리프의 주머니(다이아몬드)
				if (action.equalsIgnoreCase("request karif bag1")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 카리프의 주머니(에메랄드)
				if (action.equalsIgnoreCase("request karif bag2")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 주머니(루비)
				if (action.equalsIgnoreCase("request karif bag3")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 주머니(사파이어)
				if (action.equalsIgnoreCase("request karif bag4")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 고급 주머니(다이아몬드)
				if (action.equalsIgnoreCase("request karif bag5")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 카리프의 고급 주머니(에메랄드)
				if (action.equalsIgnoreCase("request karif bag6")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 고급 주머니(루비)
				if (action.equalsIgnoreCase("request karif bag7")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 고급 주머니(사파이어)
				if (action.equalsIgnoreCase("request karif bag8")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			//
			if (craft != null) {
				// 카리프의 최고급 주머니(다이아몬드)
				if (action.equalsIgnoreCase("request karif bag9")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 카리프의 최고급 주머니(에메랄드)
				if (action.equalsIgnoreCase("request karif bag10")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 최고급 주머니(루비)
				if (action.equalsIgnoreCase("request karif bag11")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 카리프의 최고급 주머니(사파이어)
				if (action.equalsIgnoreCase("request karif bag12")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "karifs1", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}


