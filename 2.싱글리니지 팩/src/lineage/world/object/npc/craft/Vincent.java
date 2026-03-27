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

public class Vincent extends CraftInstance {

	public Vincent(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());

		// 제작 처리 초기화.
		Item i = ItemDatabase.find("고급피혁");
		if (i != null) {
			craft_list.put("request hard leather", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("동물가죽"), 20));
			list.put(i, l);
		}

		i = ItemDatabase.find("가죽모자");
		if (i != null) {
			craft_list.put("request leather cap", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("동물가죽"), 5));
			l.add(new Craft(ItemDatabase.find("철괴"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("가죽샌달");
		if (i != null) {
			craft_list.put("request leather sandal", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("동물가죽"), 6));
			l.add(new Craft(ItemDatabase.find("철괴"), 2));
			list.put(i, l);
		}

		i = ItemDatabase.find("가죽조끼");
		if (i != null) {
			craft_list.put("request leather vest", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("동물가죽"), 10));
			list.put(i, l);
		}

		i = ItemDatabase.find("가죽방패");
		if (i != null) {
			craft_list.put("request leather shield", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("동물가죽"), 7));
			list.put(i, l);
		}

		i = ItemDatabase.find("가죽부츠");
		if (i != null) {
			craft_list.put("request leather boots", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("징박은 가죽샌달"), 1));
			l.add(new Craft(ItemDatabase.find("고급피혁"), 10));
			l.add(new Craft(ItemDatabase.find("철괴"), 10));
			l.add(new Craft(ItemDatabase.find("아데나"), 300));
			list.put(i, l);
		}

		i = ItemDatabase.find("가죽투구");
		if (i != null) {
			craft_list.put("request leather helmet", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("투구"), 1));
			l.add(new Craft(ItemDatabase.find("가죽모자"), 1));
			l.add(new Craft(ItemDatabase.find("고급피혁"), 5));
			l.add(new Craft(ItemDatabase.find("철괴"), 15));
			list.put(i, l);
		}

		i = ItemDatabase.find("중갑가죽조끼");
		if (i != null) {
			craft_list.put("request hard leather vest", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("가죽조끼"), 1));
			l.add(new Craft(ItemDatabase.find("고급피혁"), 15));
			l.add(new Craft(ItemDatabase.find("철괴"), 15));
			list.put(i, l);
		}

		i = ItemDatabase.find("벨트달린 가죽조끼");
		if (i != null) {
			craft_list.put("request leather vest with belt", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("가죽조끼"), 1));
			l.add(new Craft(ItemDatabase.find("벨트"), 1));
			list.put(i, l);
		}

		i = ItemDatabase.find("벨트");
		if (i != null) {
			craft_list.put("request belt", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("고급피혁"), 5));
			l.add(new Craft(ItemDatabase.find("철괴"), 2));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		if (pc.getLawful() < Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "vincent2"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "vincent1"));
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
				// 고급피혁 제작
				if (action.equalsIgnoreCase("request hard leather")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC1", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 가죽모자
				if (action.equalsIgnoreCase("request leather cap")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC2", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 가죽샌달
				if (action.equalsIgnoreCase("request leather sandal")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC3", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 가죽조끼
				if (action.equalsIgnoreCase("request leather vest")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC4", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 가죽방패
				if (action.equalsIgnoreCase("request leather shield")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC5", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 가죽부츠
				if (action.equalsIgnoreCase("request leather boots")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 가죽투구
				if (action.equalsIgnoreCase("request leather helmet")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC7", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 중갑가죽조끼
				if (action.equalsIgnoreCase("request hard leather vest")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC8", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 벨트달린 가죽조끼
				if (action.equalsIgnoreCase("request leather vest with belt")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC9", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 벨트
				if (action.equalsIgnoreCase("request belt")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "vincentC10", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}
