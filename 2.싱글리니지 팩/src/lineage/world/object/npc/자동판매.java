package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lineage.bean.database.Drop;
import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.ItemDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharAutoShop;
import lineage.network.packet.server.S_CharAutoShop2;
import lineage.network.packet.server.S_CharAutoShopSell;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.potion.HealingPotion;

public class 자동판매 extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		List<String> msg = new ArrayList<String>();
		msg.clear();
		msg.add(pc.isAutoSell ? "활성화" : "비활성화");
		msg.add(pc.isAutoSelluser ? "활성화" : "비활성화");
		msg.add(""+pc.isAutoSellList.size());
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autosell", null, msg));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (action.equalsIgnoreCase("on")) {
			pc.isAutoSell = true;
			toTalk(pc, null);
		}

		if (action.equalsIgnoreCase("off")) {
			pc.isAutoSell = false;
			toTalk(pc, null);
		}
		if (action.equalsIgnoreCase("on2")) {
			pc.isAutoSelluser = true;
			toTalk(pc, null);
		}

		if (action.equalsIgnoreCase("off2")) {
			pc.isAutoSelluser = false;
			toTalk(pc, null);
		}

		if (action.equalsIgnoreCase("list")) {

			if (pc.isAutoSellList.size() > 0) {
				List<ItemInstance> matchingItems2 = new ArrayList<>();

				for (String itemName : pc.isAutoSellList) {
					Item item = ItemDatabase.find(itemName);
					if (item != null) {
						ItemInstance ii = ItemDatabase.newInstance(item);
						matchingItems2.add(ii);
					}
				}

				pc.toSender(S_CharAutoShop.clone(BasePacketPooling.getPool(S_CharAutoShop.class), pc, matchingItems2));
			} else {
				ChattingController.toChatting(pc, "\\fY [자동판매 알림] 현재 자동판매에 등록된 아이템이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}

		}
		if (action.equalsIgnoreCase("add")) {
	
			pc.isAutoSellAdding = true;
			
			Npc n = NpcDatabase.find("매입 상인");
			List<ItemInstance> matchingItems = new ArrayList<>();

			for (Shop s : n.getShop_list()) {
				for (ItemInstance inventoryItem : pc.getInventory().getList()) {
			
						if (s.getItemName().equalsIgnoreCase(inventoryItem.getItem().getName())) {

							if (!matchingItems.contains(inventoryItem)) {
								
								matchingItems.add(inventoryItem);
							}
						
					}
			
				}
			}

			pc.toSender(S_CharAutoShop.clone(BasePacketPooling.getPool(S_CharAutoShop.class), (PcInstance) pc, matchingItems));

			ChattingController.toChatting(pc, "\\fU [자동판매 알림] 등록하려는 아이템을 1개씩 선택하여 주세요", 20);

		}

		if (action.equalsIgnoreCase("delete")) {
			pc.isAutoSellDeleting = true;
			if (pc.isAutoSellList.size() > 0) {
				List<String> shopList = new ArrayList<String>();

				int idx = 0;
				for (String itemName : pc.isAutoSellList) {
					shopList.add(String.format("%d. %s", idx++, itemName));
				}

				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), pc, "kShopList", null, shopList));
			} else {
				ChattingController.toChatting(pc, "\\fY [자동판매 알림] 현재 자동판매에 등록된 아이템이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}

		}

		if (action.equalsIgnoreCase("reset")) {
			if (pc.isAutoSellList.size() > 0) {
			pc.isAutoSellList.clear();
			ChattingController.toChatting(pc, "\\fR [자동판매 알림] 자동판매 목록이 전체 초기화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
			toTalk(pc, null);
			} else {
				ChattingController.toChatting(pc, "\\fY [자동판매 알림] 현재 자동판매에 등록된 아이템이 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}

	}

	public static void addShopItem(PcInstance pc, ClientBasePacket cbp) {

		try {
			cbp.readC(); // type
			int count = cbp.readH();
			ItemInstance item = null;
	
			if (count > 0) {
				long item_id = 0;
				int item_count = 0;

				for (int i = 0; i < count; ++i) {
					item_id = cbp.readD();
					item_count = (int) cbp.readD();

					item = pc.getInventory().findByObjId(item_id);

				}

				if (pc.isAutoSellAdding) {
					Item i = ItemDatabase.find(item.getItem().getName());
					boolean check = false;
					if (i != null) {

						Npc n = NpcDatabase.find("매입 상인");
						if (n != null) {
							for (Shop s : n.getShop_list()) {
								if (s.getItemName().equalsIgnoreCase(item.getItem().getName()))
									check = true;
							}
						}
						if (!check) {
							ChattingController.toChatting(pc, "\\fY [자동판매 알림] 해당 아이템은 매입이 되지 않는 아이템 입니다.", 20);
							pc.isAutoSellAdding = false;
							return;
						}
						if (!pc.isAutoSellList.contains(i.getName())) {
							
							if(pc.getInventory().find(i.getName()) != null){
								pc.isAutoSellList.add(i.getName());
							}else{
								ChattingController.toChatting(pc, "\\fY [자동판매 알림] 아이템이 인벤토리에 없습니다.", 20);
								pc.isAutoSellAdding = false;
							}
					
							ChattingController.toChatting(pc, String.format("\\fY [자동판매 알림] '%s' 자동판매 목록에 추가되었습니다", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
							pc.isAutoSellAdding = false;
						} else {
							ChattingController.toChatting(pc, "\\fY [자동판매 알림] 이미 자동판매에 등록된 아이템입니다.", 20);
							pc.isAutoSellAdding = false;

						}

					} else {
						ChattingController.toChatting(pc, "\\fY [자동판매 알림] 해당아이템은 불가능합니다." + item.getItem().getName(), 20);
					}
					pc.isAutoSellAdding = false;
					return;
				}



			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void deletShopItem(PcInstance pc, ClientBasePacket cbp) {

	}
}
