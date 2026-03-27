package lineage.world.object.item;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.ItemBundle;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Bundle extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Bundle();
		return item;
	}

	static synchronized public ItemInstance clone(ItemInstance item, TYPE type) {
		if (item == null)
			item = new Bundle();
		((Bundle)item).loop_type = type;
		return item;
	}
	public Bundle() {}
	public Bundle(TYPE type) {
		loop_type = type;
	}
	static public enum TYPE {
		LOOP_1, // 1개만 나오면 종료.
		LOOP_2, // 나오든 안나오든 목록 순회 후 종료.
		LOOP_3 // 나올때까지 순회 후 종료.
	};
	
	private TYPE loop_type;
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isLvCheck(cha)) {
			if (cha.getInventory() != null && cha.getInventory().getList().size() >= Lineage.inventory_max) {
				ChattingController.toChatting(cha, "인벤토리가 가득찼습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
	
			// 아이템 지급.
			List<ItemBundle> list = new ArrayList<ItemBundle>();
			ItemBundleDatabase.find(list, getItem().getName());

			if (list.size() < 1)
				return;

			for (ItemBundle ib : list) {
				if (ib.getItemCountMin() > 0) {
					Item i = ItemDatabase.find(ib.getItem());

					if (i != null) {
						ItemInstance temp = cha.getInventory().find(i.getName(), ib.getItemBless(), i.isPiles());
						int count = Util.random(ib.getItemCountMin(), ib.getItemCountMax());

						if (temp != null && (temp.getBless() != ib.getItemBless() || temp.getEnLevel() != ib.getItemEnchant()))
							temp = null;

						if (temp == null) {
							// 겹칠수 있는 아이템이 존재하지 않을경우.
							if (i.isPiles()) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setBless(ib.getItemBless());
								temp.setEnLevel(ib.getItemEnchant());
								temp.setCount(count);
								temp.setDefinite(false);
								cha.getInventory().append(temp, true);
							} else {
								for (int idx = 0; idx < count; idx++) {
									temp = ItemDatabase.newInstance(i);
									temp.setObjectId(ServerDatabase.nextItemObjId());
									temp.setBless(ib.getItemBless());
									temp.setEnLevel(ib.getItemEnchant());
									temp.setDefinite(false);
									cha.getInventory().append(temp, true);
								}
							}
						} else {
							// 겹치는 아이템이 존재할 경우.
							cha.getInventory().count(temp, temp.getCount() + count, true);
						}

						if (Lineage.is_item_drop_msg_item && i != null && this != null && getItem() != null) {
							ItemDropMessageDatabase.sendMessage(cha, i.getName(), getItem().getName());
						}
						
						if(item.getEffect() > 0)
							cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, item.getEffect()), true);
						
						// 알림.
//						ChattingController.toChatting(cha, String.format("%s(%d) 획득: %s", i.getName(), count, getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(cha, String.format("아이템 획득: %s(%d)", i.getName(), count, getItem().getName()), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			}

			// 수량 하향.
			cha.getInventory().count(this, getCount() - 1, true);
		}
	}

		protected void toBundle(Character cha, int[][] db, TYPE type) {
			//
			int idx = 0;
			int cnt = 0;
			do {
				int[] dbs = db[idx++];
				if(idx >= db.length) {
					if(type == TYPE.LOOP_2)
						break;
					if(cnt>0 && type==TYPE.LOOP_3)
						break;
					idx = 0;
				}
				if(Util.random(0, 99) < dbs[3]) {
					CraftController.toCraft(cha, ItemDatabase.find(dbs[0]),	Util.random(dbs[1], dbs[2]), true);
					cnt += 1;
					if(type == TYPE.LOOP_1)
						break;
				}
			} while(true);
		}

	}

