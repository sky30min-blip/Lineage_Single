package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Ryumiel extends CraftInstance {

	public Ryumiel(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("강화 초록 물약");
		if (i != null) {
			craft_list.put("request potion of greater haste self", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("초록 물약"), 6));
			l.add(new Craft(ItemDatabase.find("아데나"), 500));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("달빛의 눈물");
		if (i != null) {
			craft_list.put("request tear of moon", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("빛나는 비늘"), 5));
			l.add(new Craft(ItemDatabase.find("에메랄드"), 1));
			l.add(new Craft(ItemDatabase.find("아데나"), 1000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("얼음여왕의 숨결");
		if (i != null) {
			craft_list.put("request water gem", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("에메랄드"), 10));
			l.add(new Craft(ItemDatabase.find("에바의 유산"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 5000));
			list.put(i, l);
		}
		i = ItemDatabase.find("골렘의 숨결");
		if (i != null) {
			craft_list.put("request earth gem", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("다이아몬드"), 10));
			l.add(new Craft(ItemDatabase.find("마프르의 유산"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 5000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("불새의 숨결");
		if (i != null) {
			craft_list.put("request fire gem", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("루비"), 10));
			l.add(new Craft(ItemDatabase.find("파아그리오의 유산"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 5000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("드레이크의 숨결");
		if (i != null) {
			craft_list.put("request wind gem", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("사파이어"), 10));
			l.add(new Craft(ItemDatabase.find("사이하의 유산"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 5000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("빛나는 신체의 벨트");
		if (i != null) {
			craft_list.put("request bright belt of body", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("낡은 신체의 벨트"), 1));
			l.add(new Craft(ItemDatabase.find("고급 루비"), 30));
			l.add(new Craft(ItemDatabase.find("빛나는 비늘"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 100000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("빛나는 정신의 벨트");
		if (i != null) {
			craft_list.put("request bright belt of mind", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("낡은 정신의 벨트"), 1));
			l.add(new Craft(ItemDatabase.find("고급 사파이어"), 30));
			l.add(new Craft(ItemDatabase.find("빛나는 비늘"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 100000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("빛나는 영혼의 벨트");
		if (i != null) {
			craft_list.put("request bright belt of soul", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("낡은 영혼의 벨트"), 1));
			l.add(new Craft(ItemDatabase.find("고급 루비"), 20));
			l.add(new Craft(ItemDatabase.find("고급 사파이어"), 20));
			l.add(new Craft(ItemDatabase.find("빛나는 비늘"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 100000));
			list.put(i, l);
		}
		//
		i = ItemDatabase.find("진화의 열매");
		if (i != null) {
			craft_list.put("request petfood", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("용의 심장"), 1));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ryumiel1"));

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
				// 강화 초록 물약
				if (action.equalsIgnoreCase("request potion of greater haste self")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 달빛의 눈물
				if (action.equalsIgnoreCase("request tear of moon")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 얼음여왕의 숨결
				if (action.equalsIgnoreCase("request water gem")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 골렘의 숨결
				if (action.equalsIgnoreCase("request earth gem")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 불새의 숨결
				if (action.equalsIgnoreCase("request fire gem")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 드레이크의 숨결
				if (action.equalsIgnoreCase("request wind gem")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 빛나는 신체의 벨트
				if (action.equalsIgnoreCase("request bright belt of body")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 빛나는 정신의 벨트
				if (action.equalsIgnoreCase("request bright belt of mind")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 빛나는 영혼의 벨트
				if (action.equalsIgnoreCase("request bright belt of soul")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel66", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 진화의 열매
				if (action.equalsIgnoreCase("request petfood")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ryumiel67", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}


