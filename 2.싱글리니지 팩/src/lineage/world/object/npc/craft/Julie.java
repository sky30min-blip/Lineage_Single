package lineage.world.object.npc.craft;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_HyperText;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.object;
import lineage.world.object.instance.CraftInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Julie extends CraftInstance {

	public Julie(Npc npc) {
		super(npc);

		Item i = ItemDatabase.find("고급피혁");
		if (i != null) {
			craft_list.put("request hard leather", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("동물가죽"), 20));
			list.put(i, l);
		}
	}

	public class CreateItem {
		public String itemName;
		public boolean isCheckBless;
		public int bless;
		public boolean isCheckEnchant;
		public int enchant;
		public int count;
		public int yitem = 0;

		/**
		 * @param itemName
		 *            : 재료 아이템 이름
		 * @param isCheckBless
		 *            : 재료 축여부 체크
		 * @param bless
		 *            : 축복(0~2)
		 * @param isCheckEnchant
		 *            : 재료 인첸트 체크
		 * @param enchant
		 *            : 인첸트
		 * @param count
		 *            : 수량
		 */
		public CreateItem(String itemName, boolean isCheckBless, int bless, boolean isCheckEnchant, int enchant, int count) {
			this.itemName = itemName;
			this.isCheckBless = isCheckBless;
			this.bless = bless;
			this.isCheckEnchant = isCheckEnchant;
			this.enchant = enchant;
			this.count = count;
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		// pc 쪽으로 방향 전환.
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);

		if (pc.getLawful() < Lineage.NEUTRAL)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "julieC1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "julie1"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		pc.setTempShop(null);
		if (action.equalsIgnoreCase("sell_2")) {
			if (pc.getInventory().find("동물가죽") != null) {
				object shop = NpcSpawnlistDatabase.동물가죽;
				if (shop != null) {
					shop.toTalk(pc, null);
					pc.setTempShop(shop);
				}
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "julieC3"));
			}
		}
		if (action.equalsIgnoreCase("sell_30")) {
			if (pc.getInventory().find("고급피혁") != null) {
				object shop = NpcSpawnlistDatabase.고급피혁;
				if (shop != null) {
					shop.toTalk(pc, null);
					pc.setTempShop(shop);
				}
			} else {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "julieC3"));
			}
		}
		if (pc.getInventory() != null) {
			List<CreateItem> createList = new ArrayList<CreateItem>();
			List<CreateItem> createList2 = new ArrayList<CreateItem>();
			List<ItemInstance> itemList = new ArrayList<ItemInstance>();

			Item craft = craft_list.get(action);

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
							pc.toSender(S_HyperText.clone(BasePacketPooling.getPool(S_HyperText.class), this, "julieC2", action, 0, 1, 1, max, temp_request_list));
					}
				}
			}
			// 가죽모자 제작
			if (action.equalsIgnoreCase("request leather cap")) {

				createList.add(new CreateItem("동물가죽", false, 1, false, 0, 5));
				createList.add(new CreateItem("철괴", false, 1, false, 0, 1));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "가죽모자", 1, 0, 1);

			}
			// 가죽샌달 제작
			if (action.equalsIgnoreCase("request leather sandal")) {

				createList.add(new CreateItem("동물가죽", false, 1, false, 0, 6));
				createList.add(new CreateItem("철괴", false, 1, false, 0, 2));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "가죽샌달", 1, 0, 1);

			}
			// 가죽조끼 제작
			if (action.equalsIgnoreCase("request leather vest")) {

				createList.add(new CreateItem("동물가죽", false, 1, false, 0, 10));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "가죽조끼", 1, 0, 1);

			}
			// 가죽방패 제작
			if (action.equalsIgnoreCase("request leather shield")) {

				createList.add(new CreateItem("동물가죽", false, 1, false, 0, 7));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "가죽방패", 1, 0, 1);

			}
			// 가죽부츠 제작
			if (action.equalsIgnoreCase("request leather boots")) {

				createList.add(new CreateItem("징박은 가죽샌달", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급피혁", false, 1, false, 0, 10));
				createList.add(new CreateItem("철괴", false, 1, false, 0, 10));
				createList.add(new CreateItem("아데나", false, 1, false, 0, 300));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "가죽부츠", 1, 0, 1);

			}
			// 가죽투구 제작
			if (action.equalsIgnoreCase("request leather helmet")) {

				createList.add(new CreateItem("투구", false, 1, false, 0, 1));
				createList.add(new CreateItem("가죽모자", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급피혁", false, 1, false, 0, 5));
				createList.add(new CreateItem("철괴", false, 1, false, 0, 5));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "가죽투구", 1, 0, 1);

			}
			// 중갑가죽조끼 제작
			if (action.equalsIgnoreCase("request hard leather vest")) {

				createList.add(new CreateItem("징박은 가죽조끼", false, 1, false, 0, 1));
				createList.add(new CreateItem("고급피혁", false, 1, false, 0, 15));
				createList.add(new CreateItem("철괴", false, 1, false, 0, 15));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "중갑가죽조끼", 1, 0, 1);

			}
			// 벨트달린 가죽조끼 제작
			if (action.equalsIgnoreCase("request leather vest with belt")) {

				createList.add(new CreateItem("가죽조끼", false, 1, false, 0, 1));
				createList.add(new CreateItem("벨트", false, 1, false, 0, 1));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "벨트달린 가죽조끼", 1, 0, 1);

			}
			// 벨트 제작
			if (action.equalsIgnoreCase("request belt")) {

				createList.add(new CreateItem("고급피혁", false, 1, false, 0, 5));
				createList.add(new CreateItem("철괴", false, 1, false, 0, 2));
				checkItem(pc, createList, itemList);

				createItem(pc, createList, createList2, itemList, "벨트", 1, 0, 1);

			}

		}
	}

	public void checkItem(PcInstance pc, List<CreateItem> createList, List<ItemInstance> itemList) {
		if (createList != null && itemList != null) {
			if (itemList.size() > 0)
				itemList.clear();

			for (CreateItem list : createList) {
				for (ItemInstance i : pc.getInventory().getList()) {
					if (i.getItem() != null && i.getItem().getName().equalsIgnoreCase(list.itemName) && i.getCount() >= list.count && !i.isEquipped()) {
						// 축여부 체크일 경우
						if (list.isCheckBless) {
							// 인첸트 체크일 경우
							if (list.isCheckEnchant) {
								if (i.getBless() == list.bless && i.getEnLevel() == list.enchant) {
									itemList.add(i);
									break;
								}
							} else {
								if (i.getBless() == list.bless) {
									itemList.add(i);
									break;
								}
							}
						} else {
							// 인첸트 체크일 경우
							if (list.isCheckEnchant) {
								if (i.getEnLevel() == list.enchant) {
									itemList.add(i);
									break;
								}
							} else {
								itemList.add(i);
								break;
							}
						}
					}
				}
			}
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

	public void createItem(PcInstance pc, List<CreateItem> createList, List<CreateItem> createList2, List<ItemInstance> itemList, String createItemName, int bless, int enchant, int count) {
		if ((createList.size() > 0 && itemList.size() > 0 && createList.size() == itemList.size()) || (createList2.size() > 0 && itemList.size() > 0 && createList2.size() == itemList.size())) {

			Item i = ItemDatabase.find(createItemName);

			if (i != null) {
				ItemInstance temp = pc.getInventory().find(i.getName(), bless, i.isPiles());

				if (temp != null && (temp.getBless() != bless || temp.getEnLevel() != enchant))
					temp = null;

				if (temp == null) {
					// 겹칠수 있는 아이템이 존재하지 않을경우.
					if (i.isPiles()) {
						temp = ItemDatabase.newInstance(i);
						temp.setObjectId(ServerDatabase.nextItemObjId());
						temp.setBless(bless);
						temp.setEnLevel(enchant);
						temp.setCount(count);
						temp.setDefinite(false);
						pc.getInventory().append(temp, true);
					} else {
						for (int idx = 0; idx < count; idx++) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(bless);
							temp.setEnLevel(enchant);
							temp.setDefinite(false);
							pc.getInventory().append(temp, true);
						}
					}
				} else {
					// 겹치는 아이템이 존재할 경우.
					pc.getInventory().count(temp, temp.getCount() + count, true);
				}

				if (createList2.size() == 0) {
					for (CreateItem list : createList) {
						for (ItemInstance item : itemList) {
							if (item != null && item.getItem() != null && list.itemName.equalsIgnoreCase(item.getItem().getName()))
								pc.getInventory().count(item, item.getCount() - list.count, true);
						}
					}
				} else {
					for (CreateItem list : createList2) {
						for (ItemInstance item : itemList) {
							if (item != null && item.getItem() != null && list.itemName.equalsIgnoreCase(item.getItem().getName()))
								pc.getInventory().count(item, item.getCount() - list.count, true);
						}
					}
				}

				ChattingController.toChatting(pc, String.format("%s을 제작하였습니다.", createItemName), Lineage.CHATTING_MODE_MESSAGE);
				// 창 제거
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
			}
		} else {
			String msg = "";

			if (createList2.size() > 0) {
				int idx = 0;

				for (CreateItem list : createList) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList.size())
						msg += ", ";
				}

				idx = 0;
				msg += " 또는 ";

				for (CreateItem list : createList2) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList2.size())
						msg += ", ";
				}
			} else {
				int idx = 0;

				for (CreateItem list : createList) {
					idx++;

					if (list.enchant > 0 && list.count > 1)
						msg += String.format("+%d %s(%,d)", list.enchant, list.itemName, list.count);
					else if (list.enchant > 0 && list.count == 1)
						msg += String.format("+%d %s", list.enchant, list.itemName);
					else if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);

					if (idx < createList.size())
						msg += ", ";
				}
			}

			ChattingController.toChatting(pc, String.format("[%s] 제작에 필요한 재료가 부족합니다.", createItemName), Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(pc, String.format("재료: %s", msg), Lineage.CHATTING_MODE_MESSAGE);
			// 창 제거
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, ""));
		}
	}
}