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

public class Fivelviin extends CraftInstance {

	public Fivelviin(Npc npc) {
		super(npc);

		// 완력
		Item i = ItemDatabase.find("발라카스의 완력");
		if (i != null) {
			craft_list.put("request elite valakas plate armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("발라카스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("발라카스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("파아그리오의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 500000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("파푸리온의 완력");
		if (i != null) {
			craft_list.put("request elite fafurion plate armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("파푸리온의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("파푸리온의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("에바의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("린드비오르의 완력");
		if (i != null) {
			craft_list.put("request elite lindvior plate armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("린드비오르의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("린드비오르의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("사이하의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("안타라스의 완력");
		if (i != null) {
			craft_list.put("request elite antharas plate armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("안타라스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("안타라스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("마프르의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		// 마력
		i = ItemDatabase.find("발라카스의 마력");
		if (i != null) {
			craft_list.put("request elite valakas robe", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("발라카스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("발라카스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("파아그리오의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 500000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("파푸리온의 마력");
		if (i != null) {
			craft_list.put("request elite fafurion robe", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("파푸리온의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("파푸리온의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("에바의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("린드비오르의 마력");
		if (i != null) {
			craft_list.put("request elite lindvior robe", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("린드비오르의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("린드비오르의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("사이하의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("안타라스의 마력");
		if (i != null) {
			craft_list.put("request elite antharas robe", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("안타라스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("안타라스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("마프르의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		// 인내력
		i = ItemDatabase.find("발라카스의 인내력");
		if (i != null) {
			craft_list.put("request elite valakas leather armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("발라카스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("발라카스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("파아그리오의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 500000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("파푸리온의 인내력");
		if (i != null) {
			craft_list.put("request elite fafurion leather armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("파푸리온의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("파푸리온의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("에바의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("린드비오르의 인내력");
		if (i != null) {
			craft_list.put("request elite lindvior leather armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("린드비오르의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("린드비오르의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("사이하의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("안타라스의 인내력");
		if (i != null) {
			craft_list.put("request elite antharas leather armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("안타라스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("안타라스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("마프르의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		// 예지력
		i = ItemDatabase.find("발라카스의 예지력");
		if (i != null) {
			craft_list.put("request elite valakas scale armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("발라카스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("발라카스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("파아그리오의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 500000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("파푸리온의 예지력");
		if (i != null) {
			craft_list.put("request elite fafurion scale armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("파푸리온의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("파푸리온의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("에바의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("린드비오르의 예지력");
		if (i != null) {
			craft_list.put("request elite lindvior scale armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("린드비오르의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("린드비오르의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("사이하의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
		i = ItemDatabase.find("안타라스의 예지력");
		if (i != null) {
			craft_list.put("request elite antharas scale armor", i);
			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("안타라스의 숨결"), 2));
			l.add(new Craft(ItemDatabase.find("안타라스의 마갑주"), 1));
			l.add(new Craft(ItemDatabase.find("마프르의 유산"), 50));
			l.add(new Craft(ItemDatabase.find("아데나"), 300000000));
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch(getNpc().getNameIdNumber()) {
			case 7740: // 강인한 하이오스 == 완력
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fivelviin1"));
				break;
			case 7741: // 세심한 슈누 == 마력
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fivelviin2"));
				break;
			case 7742: // 끈질긴 도오호 == 인내력
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fivelviin3"));
				break;
			case 7743: // 찬란한 바에미 == 예지력
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "fivelviin4"));
				break;
		}
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
				// 발라카스의 완력
				if (action.equalsIgnoreCase("request elite valakas plate armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin6", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 파푸리온의 완력
				if (action.equalsIgnoreCase("request elite fafurion plate armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 린드비오르의 완력
				if (action.equalsIgnoreCase("request elite lindvior plate armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 안타라스의 완력
				if (action.equalsIgnoreCase("request elite antharas plate armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin6", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			//마력
			if (craft != null) {
				// 발라카스의 마력
				if (action.equalsIgnoreCase("request elite valakas robe")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin7", action, 0, 1, 1, max, temp_request_list));

					}
				}
			}
			if (craft != null) {
				// 파푸리온의 마력
				if (action.equalsIgnoreCase("request elite fafurion robe")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin7", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 린드비오르의 마력
				if (action.equalsIgnoreCase("request elite lindvior robe")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin7", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 안타라스의 마력
				if (action.equalsIgnoreCase("request elite antharas robe")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin7", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			//인내력
			if (craft != null) {
				// 발라카스의 인내력
				if (action.equalsIgnoreCase("request elite valakas leather armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin8", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 파푸리온의 인내력
				if (action.equalsIgnoreCase("request elite fafurion leather armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin8", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 린드비오르의 인내력
				if (action.equalsIgnoreCase("request elite lindvior leather armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin8", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 안타라스의 인내력
				if (action.equalsIgnoreCase("request elite antharas leather armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin8", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			//예지력
			if (craft != null) {
				// 발라카스의 예지력
				if (action.equalsIgnoreCase("request elite valakas scale armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin9", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 파푸리온의 예지력
				if (action.equalsIgnoreCase("request elite fafurion scale armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin9", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				// 린드비오르의 예지력
				if (action.equalsIgnoreCase("request elite lindvior scale armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin9", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			if (craft != null) {
				//  안타라스의 예지력
				if (action.equalsIgnoreCase("request elite antharas scale armor")) {
					// 재료 확인.
					if (CraftController.isCraft(pc, list.get(craft), true)) {
						// 제작 가능한 최대값 추출.
						int max = CraftController.getMax(pc, list.get(craft));
						if (Lineage.server_version <= 144)
							toLeather(pc, action, max);
						else
							// 패킷 처리.
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "fivelviin9", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
		}
	}
}