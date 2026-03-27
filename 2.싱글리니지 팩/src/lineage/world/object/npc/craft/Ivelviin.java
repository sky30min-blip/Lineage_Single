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

public class Ivelviin extends CraftInstance {

	public Ivelviin(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("싸울아비 장검");
		if (i != null) {
			craft_list.put("request tsurugi", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("오리하루콘"), 500));
			l.add(new Craft(ItemDatabase.find("최고급 다이아몬드"), 5));
			l.add(new Craft(ItemDatabase.find("최고급 사파이어"), 5));
			l.add(new Craft(ItemDatabase.find("최고급 에메랄드"), 5));
			l.add(new Craft(ItemDatabase.find("최고급 루비"), 5));
			l.add(new Craft(ItemDatabase.find("아시타지오의 재"), 30));
			list.put(i, l);
		}
		i = ItemDatabase.find("화룡 비늘 갑옷");
		if (i != null) {
			craft_list.put("request red dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("오리하루콘"), 1000));
			l.add(new Craft(ItemDatabase.find("화룡 비늘"), 15));
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 500));
			l.add(new Craft(ItemDatabase.find("최고급 루비"), 5));
			l.add(new Craft(ItemDatabase.find("아시타지오의 재"), 10));
			list.put(i, l);
		}
		i = ItemDatabase.find("수룡 비늘 갑옷");
		if (i != null) {
			craft_list.put("request blue dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("오리하루콘"), 1000));
			l.add(new Craft(ItemDatabase.find("수룡 비늘"), 15));
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 500));
			l.add(new Craft(ItemDatabase.find("최고급 에메랄드"), 5));
			l.add(new Craft(ItemDatabase.find("아시타지오의 재"), 10));
			list.put(i, l);
		}
		i = ItemDatabase.find("풍룡 비늘 갑옷");
		if (i != null) {
			craft_list.put("request azure dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("오리하루콘"), 1000));
			l.add(new Craft(ItemDatabase.find("풍룡 비늘"), 15));
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 500));
			l.add(new Craft(ItemDatabase.find("최고급 사파이어"), 5));
			l.add(new Craft(ItemDatabase.find("아시타지오의 재"), 10));
			list.put(i, l);
		}
		i = ItemDatabase.find("지룡 비늘 갑옷");
		if (i != null) {
			craft_list.put("request green dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("오리하루콘"), 1000));
			l.add(new Craft(ItemDatabase.find("지룡 비늘"), 15));
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 500));
			l.add(new Craft(ItemDatabase.find("최고급 다이아몬드"), 5));
			l.add(new Craft(ItemDatabase.find("아시타지오의 재"), 10));
			list.put(i, l);
		}
		// 마갑주
		i = ItemDatabase.find("발라카스의 마갑주");
		if (i != null) {
			craft_list.put("request ancient red dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("화룡 비늘 갑옷"), 1));
			l.add(new Craft(ItemDatabase.find("발라카스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("최고급 루비"), 1));
			l.add(new Craft(ItemDatabase.find("불새의 숨결"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 50000));
			list.put(i, l);
		}
		i = ItemDatabase.find("파푸리온의 마갑주");
		if (i != null) {
			craft_list.put("request ancient blue dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("수룡 비늘 갑옷"), 1));
			l.add(new Craft(ItemDatabase.find("파푸리온의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("최고급 에메랄드"), 1));
			l.add(new Craft(ItemDatabase.find("얼음여왕의 숨결"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 50000));
			list.put(i, l);
		}
		i = ItemDatabase.find("린드비오르의 마갑주");
		if (i != null) {
			craft_list.put("request ancient azure dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("풍룡 비늘 갑옷"), 1));
			l.add(new Craft(ItemDatabase.find("린드비오르의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("최고급 사파이어"), 1));
			l.add(new Craft(ItemDatabase.find("드레이크의 숨결"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 50000));
			list.put(i, l);
		}
		i = ItemDatabase.find("안타라스의 마갑주");
		if (i != null) {
			craft_list.put("request ancient green dragon armor", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("지룡 비늘 갑옷"), 1));
			l.add(new Craft(ItemDatabase.find("안타라스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("최고급 다이아몬드"), 1));
			l.add(new Craft(ItemDatabase.find("골렘의 숨결"), 5));
			l.add(new Craft(ItemDatabase.find("아데나"), 50000));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ivelviin1"));
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
				// 싸울아비 장검
				if (action.equalsIgnoreCase("request tsurugi")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 화룡 비늘갑옷
				if (action.equalsIgnoreCase("request red dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 수룡 비늘갑옷
				if (action.equalsIgnoreCase("request blue dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 풍룡 비늘갑옷
				if (action.equalsIgnoreCase("request azure dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 지룡 비늘갑옷
				if (action.equalsIgnoreCase("request green dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 발라카스 마갑주
				if (action.equalsIgnoreCase("request ancient red dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 파푸리온 마갑주
				if (action.equalsIgnoreCase("request ancient blue dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 린드비오르 마갑주
				if (action.equalsIgnoreCase("request ancient azure dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 안타라스 마갑주
				if (action.equalsIgnoreCase("request ancient green dragon armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "ivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}



