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

public class Bamut extends CraftInstance {

	public Bamut(Npc npc) {
		super(npc);

		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add(npc.getNameId());
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("저주의 피혁(열화)");
		if (i != null) {
			craft_list.put("request red skin of curse", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("키메라의 가죽(사자)"), 5));
			list.put(i, l);
		}

		i = ItemDatabase.find("저주의 피혁(물결)");
		if (i != null) {
			craft_list.put("request blue skin of curse", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("키메라의 가죽(용)"), 5));
			list.put(i, l);
		}
		
		i = ItemDatabase.find("저주의 피혁(바람)");
		if (i != null) {
			craft_list.put("request yellow skin of curse", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("키메라의 가죽(산양)"), 5));
			list.put(i, l);
		}
		
		i = ItemDatabase.find("저주의 피혁(대지)");
		if (i != null) {
			craft_list.put("request green skin of curse", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("키메라의 가죽(뱀)"), 5));
			list.put(i, l);
		}
		

		i = ItemDatabase.find("열화의 망토");
		if (i != null) {
			craft_list.put("request cloak of flame", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 50));
			l.add(new Craft(ItemDatabase.find("루비"), 30));
			l.add(new Craft(ItemDatabase.find("저주의 피혁(열화)"), 100));
			l.add(new Craft(ItemDatabase.find("화룡 비늘"), 3));
			list.put(i, l);
		}

		i = ItemDatabase.find("물결의 망토");
		if (i != null) {
			craft_list.put("request cloak of water", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 50));
			l.add(new Craft(ItemDatabase.find("에메랄드"), 30));
			l.add(new Craft(ItemDatabase.find("저주의 피혁(물결)"), 100));
			l.add(new Craft(ItemDatabase.find("수룡 비늘"), 3));
			list.put(i, l);
		}
		
		i = ItemDatabase.find("바람의 망토");
		if (i != null) {
			craft_list.put("request cloak of wind", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 50));
			l.add(new Craft(ItemDatabase.find("사파이어"), 30));
			l.add(new Craft(ItemDatabase.find("저주의 피혁(바람)"), 100));
			l.add(new Craft(ItemDatabase.find("풍룡 비늘"), 3));
			list.put(i, l);
		}
		
		i = ItemDatabase.find("대지의 망토");
		if (i != null) {
			craft_list.put("request cloak of earth", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("미스릴 실"), 50));
			l.add(new Craft(ItemDatabase.find("다이아몬드"), 30));
			l.add(new Craft(ItemDatabase.find("저주의 피혁(대지)"), 100));
			l.add(new Craft(ItemDatabase.find("지룡 비늘"), 3));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "bamute1"));
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
				// 저주의 피혁(열화)
				if (action.equalsIgnoreCase("request red skin of curse")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute5", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 저주의 피혁(물결)
				if (action.equalsIgnoreCase("request blue skin of curse")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute5", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 저주의 피혁(바람)
				if (action.equalsIgnoreCase("request yellow skin of curse")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute5", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 저주의 피혁(대지)
				if (action.equalsIgnoreCase("request green skin of curse")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute5", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			// 망토 제작
			if (craft != null) {
				// 열화의 망토
				if (action.equalsIgnoreCase("request cloak of flame")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute6", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 물결의 망토
				if (action.equalsIgnoreCase("request cloak of water")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 바람의 망토
				if (action.equalsIgnoreCase("request cloak of wind")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 대지의 망토
				if (action.equalsIgnoreCase("request cloak of earth")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "bamute6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}

