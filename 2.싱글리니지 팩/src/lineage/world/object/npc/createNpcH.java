package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class createNpcH extends object {
	
	public class CreateItem {
		public String itemName;
		public boolean isCheckBless;
		public int bless;
		public boolean isCheckEnchant;
		public int enchant;
		public int count;
			
		/**
		 * @param itemName			: 재료 아이템 이름
		 * @param isCheckBless		: 재료 축여부 체크
		 * @param bless				: 축복(0~2)
		 * @param isCheckEnchant	: 재료 인첸트 체크
		 * @param enchant			: 인첸트
		 * @param count				: 수량
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
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ku5Create"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getInventory() != null) {
			List<CreateItem> createList = new ArrayList<CreateItem>();
			List<CreateItem> createList2 = new ArrayList<CreateItem>();
			List<ItemInstance> itemList = new ArrayList<ItemInstance>();
			
			if (action.equalsIgnoreCase("kuu1")) {

			    int count = 2;
			    String itemName = null;
			    int bless = 0;
			    int sacredCount = 0;
			    List<String> missingItems = new ArrayList<>();
			    for (ItemInstance item : pc.getInventory().getList()) {
			        if (item.getName().equalsIgnoreCase("신성한 조각")) {
			            sacredCount += item.getCount();
			        }
			        if (item instanceof ItemArmorInstance && !item.isEquipped()) {
			            if ((item.getItem().getName().equalsIgnoreCase("마법 방어 투구"))) {
			                if (item.getEnLevel() == 7) {
			                    if (item.getBless() == 0) {
			                        bless = 1;
			                    }
			                    itemList.add(item);
			                }
			            }
			        }
			    }

			    if (itemList.size() < count) {
			        missingItems.add("+7 마법 방어 투구 " + (count - itemList.size()) + "개");
			    }
			    if (sacredCount < 500) {
			        missingItems.add("신성한 조각 " + (500 - sacredCount) + "개");
			    }
			    if (!missingItems.isEmpty()) {
			        ChattingController.toChatting(pc, String.join(", ", missingItems) + " 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
			        return;
			    }
			    if (pc.getInventory().isAden("신성한 조각",500, true)) {
			    	 ChattingController.toChatting(pc, "신성한 조각 500개가 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
			    }

			    itemName = itemList.get(Util.random(0, itemList.size() - 1)).getItem().getName();
			    ChattingController.toChatting(pc, String.format(" %s", itemName), Lineage.CHATTING_MODE_MESSAGE);
			    ChattingController.toChatting(pc, "업그레이드 성공!", Lineage.CHATTING_MODE_MESSAGE);

			    Item item = ItemDatabase.find("신성한 마법 방어 투구");

			    if (item != null) {
			        ItemInstance temp = ItemDatabase.newInstance(item);
			        temp.setObjectId(ServerDatabase.nextItemObjId());

					temp.setBless(1);

			        temp.setEnLevel(6);
			        temp.setDefinite(true);
			        pc.getInventory().append(temp, true);

			        for (int i = 0; i < count; i++)
			            pc.getInventory().count(itemList.get(i), itemList.get(i).getCount() - 1, true);
			        
			    }
			}else if (action.equalsIgnoreCase("kuu2")) {

			    int count = 2;
			    String itemName = null;
			    int bless = 0;
			    int sacredCount = 0;
			    List<String> missingItems = new ArrayList<>();
			    for (ItemInstance item : pc.getInventory().getList()) {
			        if (item.getName().equalsIgnoreCase("신성한 조각")) {
			            sacredCount += item.getCount();
			        }
			        if (item instanceof ItemArmorInstance && !item.isEquipped()) {
			            if ((item.getItem().getName().equalsIgnoreCase("마법 방어 투구"))) {
			            	  if (item.getEnLevel() == 8) {
			                      bless = item.getBless();
			                      itemList.add(item);
			                  }
			            }
			        }
			    }

			    if (itemList.size() < count) {
			        missingItems.add("+8 마법 방어 투구 " + (count - itemList.size()) + "개");
			    }
			    if (sacredCount < 500) {
			        missingItems.add("신성한 조각 " + (500 - sacredCount) + "개");
			    }
			    if (!missingItems.isEmpty()) {
			        ChattingController.toChatting(pc, String.join(", ", missingItems) + " 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
			        return;
			    }
			    if (pc.getInventory().isAden("신성한 조각",500, true)) {
			    	 ChattingController.toChatting(pc, "신성한 조각 500개가 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
			    }
			    itemName = itemList.get(Util.random(0, itemList.size() - 1)).getItem().getName();
			    ChattingController.toChatting(pc, String.format(" %s", itemName), Lineage.CHATTING_MODE_MESSAGE);
			    ChattingController.toChatting(pc, "업그레이드 성공!", Lineage.CHATTING_MODE_MESSAGE);

			    Item item = ItemDatabase.find("신성한 마법 방어 투구");

			    if (item != null) {
			        ItemInstance temp = ItemDatabase.newInstance(item);
			        temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(1);
			        temp.setEnLevel(7);
			        temp.setDefinite(true);
			        pc.getInventory().append(temp, true);

			        for (int i = 0; i < count; i++)
			            pc.getInventory().count(itemList.get(i), itemList.get(i).getCount() - 1, true);
			    }
			} else if (action.equalsIgnoreCase("kuu3")) {

			    int count = 2;
			    String itemName = null;
			    int bless = 0;
			    int sacredCount = 0;
			    List<String> missingItems = new ArrayList<>();
			    for (ItemInstance item : pc.getInventory().getList()) {
			        if (item.getName().equalsIgnoreCase("신성한 조각")) {
			            sacredCount += item.getCount();
			        }
			        if (item instanceof ItemArmorInstance && !item.isEquipped()) {
			            if ((item.getItem().getName().equalsIgnoreCase("엘름의 축복"))) {
			            	  if (item.getEnLevel() == 8) {
			                      bless = item.getBless();
			                      itemList.add(item);
			                  }
			            }
			        }
			    }

			    if (itemList.size() < count) {
			        missingItems.add("+8 엘름의 축복 " + (count - itemList.size()) + "개");
			    }
			    if (sacredCount < 500) {
			        missingItems.add("신성한 조각 " + (500 - sacredCount) + "개");
			    }
			    if (!missingItems.isEmpty()) {
			        ChattingController.toChatting(pc, String.join(", ", missingItems) + " 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
			        return;
			    }
			    if (pc.getInventory().isAden("신성한 조각",500, true)) {
			    	 ChattingController.toChatting(pc, "신성한 조각 500개가 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
			    }
			    itemName = itemList.get(Util.random(0, itemList.size() - 1)).getItem().getName();
			    ChattingController.toChatting(pc, String.format(" %s", itemName), Lineage.CHATTING_MODE_MESSAGE);
			    ChattingController.toChatting(pc, "업그레이드 성공!", Lineage.CHATTING_MODE_MESSAGE);

			    Item item = ItemDatabase.find("신성한 엘름의 축복");

			    if (item != null) {
			        ItemInstance temp = ItemDatabase.newInstance(item);
			        temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(1);
			        temp.setEnLevel(7);
			        temp.setDefinite(true);
			        pc.getInventory().append(temp, true);

			        for (int i = 0; i < count; i++)
			            pc.getInventory().count(itemList.get(i), itemList.get(i).getCount() - 1, true);
			    }
			} else if (action.equalsIgnoreCase("kuu4")) {

			    int count = 2;
			    String itemName = null;
			    int bless = 0;
			    int sacredCount = 0;
			    List<String> missingItems = new ArrayList<>();
			    for (ItemInstance item : pc.getInventory().getList()) {
			        if (item.getName().equalsIgnoreCase("신성한 조각")) {
			            sacredCount += item.getCount();
			        }
			        if (item instanceof ItemArmorInstance && !item.isEquipped()) {
			            if ((item.getItem().getName().equalsIgnoreCase("엘름의 축복"))) {
			            	  if (item.getEnLevel() == 9) {
			                      bless = item.getBless();
			                      itemList.add(item);
			                  }
			            }
			        }
			    }

			    if (itemList.size() < count) {
			        missingItems.add("+9 엘름의 축복 " + (count - itemList.size()) + "개");
			    }
			    if (sacredCount < 500) {
			        missingItems.add("신성한 조각 " + (500 - sacredCount) + "개");
			    }
			    if (!missingItems.isEmpty()) {
			        ChattingController.toChatting(pc, String.join(", ", missingItems) + " 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
			        return;
			    }
			    if (pc.getInventory().isAden("신성한 조각",500, true)) {
			    	 ChattingController.toChatting(pc, "신성한 조각 500개가 부족합니다", Lineage.CHATTING_MODE_MESSAGE);
			    }
			    itemName = itemList.get(Util.random(0, itemList.size() - 1)).getItem().getName();
			    ChattingController.toChatting(pc, String.format(" %s", itemName), Lineage.CHATTING_MODE_MESSAGE);
			    ChattingController.toChatting(pc, "업그레이드 성공!", Lineage.CHATTING_MODE_MESSAGE);

			    Item item = ItemDatabase.find("신성한 엘름의 축복");

			    if (item != null) {
			        ItemInstance temp = ItemDatabase.newInstance(item);
			        temp.setObjectId(ServerDatabase.nextItemObjId());
					temp.setBless(1);
			        temp.setEnLevel(8);
			        temp.setDefinite(true);
			        pc.getInventory().append(temp, true);

			        for (int i = 0; i < count; i++)
			            pc.getInventory().count(itemList.get(i), itemList.get(i).getCount() - 1, true);
			    }
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
			                    }
			                } else {
			                    if (i.getBless() == list.bless) {
			                        itemList.add(i);
			                    }
			                }
			            } else {
			                // 인첸트 체크일 경우
			                if (list.isCheckEnchant) {
			                    if (i.getEnLevel() == list.enchant) {
			                        itemList.add(i);
			                    }
			                } else {
			                    itemList.add(i);
			                }
			            }
			        }
			    }
			}
		}
	}
	
	public void createItem(PcInstance pc, List<CreateItem> createList, List<CreateItem> createList2, List<ItemInstance> itemList, String createItemName, int bless, int enchant, int count) {
		if ((createList.size() > 0 && itemList.size() > 0 && createList.size() == itemList.size()) || 
			(createList2.size() > 0 && itemList.size() > 0 && createList2.size() == itemList.size())) {
			
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
						temp.setDefinite(true);
						pc.getInventory().append(temp, true);
					} else {
						for (int idx = 0; idx < count; idx++) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(bless);
							temp.setEnLevel(enchant);
							temp.setDefinite(true);
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

				ChattingController.toChatting(pc, String.format("'%s' 제작 완료!", createItemName), Lineage.CHATTING_MODE_MESSAGE);
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
					else  if (list.enchant == 0 && list.count > 1)
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
					else  if (list.enchant == 0 && list.count > 1)
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
					else  if (list.enchant == 0 && list.count > 1)
						msg += String.format("%s(%,d)", list.itemName, list.count);
					else if (list.enchant == 0 && list.count == 1)
						msg += String.format("%s", list.itemName);
					
					if (idx < createList.size())
						msg += ", ";
				}
			}
			
			ChattingController.toChatting(pc, String.format("[%s] %s 필요합니다.", createItemName, msg), Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
