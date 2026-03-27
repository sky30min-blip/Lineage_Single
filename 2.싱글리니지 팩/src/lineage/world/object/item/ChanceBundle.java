package lineage.world.object.item;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.ItemChanceBundle;
import lineage.database.ItemChanceBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcRobotInstance;

public class ChanceBundle extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ChanceBundle();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
//		ItemChanceBundleDatabase.reload();
		if (cha.getInventory() != null && cha.getInventory().getList().size() >= Lineage.inventory_max) {
			ChattingController.toChatting(cha, "인벤토리가 가득찼습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		
		// 아이템 지급.
		int random = 0;
		int randomCount = 0;
		//double probability = Math.random();
		List<ItemChanceBundle> list = new ArrayList<ItemChanceBundle>();
		ItemChanceBundleDatabase.find(list, getItem().getName());
		
		//야도란 찬스아이템 보정
//		if(list.get(random).getCount() > 0){
//			list.remove(list.get(random).getName());
//			ChattingController.toChatting(cha, String.format("나 많이 나와서 안나올거야"+list.size()), Lineage.CHATTING_MODE_MESSAGE);
//		}
		if (list.size() < 1)
			return;

		for (;;) {
			if (randomCount++ > 50)
				break;
			
//			if (randomCount++ > list.size())
//				probability = Math.random();
			
			random = Util.random(0, list.size() - 1);
			
		
			if (list.get(random).getItemCountMin() < 1)
				break;
			
			double probability = Math.random();
			if (probability < list.get(random).getItemChance()) {
				if (cha instanceof PcRobotInstance) {
					// 수량 하향.
					cha.getInventory().count(this, getCount() - 1, true);
					break;
				}
				
				ItemChanceBundle ib = list.get(random);
				Item i = ItemDatabase.find(ib.getItem());
				
			
				if (i != null) {
					ItemInstance temp = cha.getInventory().find(i.getName(), ib.getItemBless(), i.isPiles());
					int count = Util.random(ib.getItemCountMin(), ib.getItemCountMax());

					if (temp != null && (temp.getBless() != list.get(random).getItemBless() || temp.getEnLevel() != ib.getItemEnchant()))
						temp = null;

					if (temp == null) {
						// 겹칠수 있는 아이템이 존재하지 않을경우.
						if (i.isPiles()) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(ib.getItemBless());
							temp.setEnLevel(ib.getItemEnchant());
							temp.setCount(count);
							temp.setDefinite(true);
					
							cha.getInventory().append(temp, true);
						} else {
							for (int idx = 0; idx < count; idx++) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setBless(ib.getItemBless());
								temp.setEnLevel(ib.getItemEnchant());
								temp.setDefinite(true);

							
								cha.getInventory().append(temp, true);
							}
						}
					} else
						// 겹치는 아이템이 존재할 경우.

					cha.getInventory().count(temp, temp.getCount() + count, true);
					
					if (Lineage.is_item_drop_msg_item && i != null && this != null && getItem() != null) {
						ItemDropMessageDatabase.sendMessage(cha, i.getName(), getItem().getName());
					}
		
					
					// 알림.
					ChattingController.toChatting(cha, String.format("%s %s 획득하였습니다.",  Util.getStringWord(cha.getName(), "이", "가"), Util.getStringWord(temp.getItem().getName(), "을", "를")), Lineage.CHATTING_MODE_MESSAGE);
					
				
						 cha.getInventory().count(this, getCount() - 1, true);
					 // 수량 하향.
					
				}
				break;
			}
		}
	}
}
