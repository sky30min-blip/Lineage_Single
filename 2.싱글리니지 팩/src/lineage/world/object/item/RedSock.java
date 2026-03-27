package lineage.world.object.item;

import lineage.database.ItemDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class RedSock extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new RedSock();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(!(cha instanceof PcInstance))
			return;

		// 정보 초기화.
		String itemName = null;
		int count = 1;
		int bress = 1;
		int quantity = 0;
		double chance = Math.random();
		
		if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.005) {
			itemName = "";
		} else if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.001) {
			itemName = "";
		} else if (chance < 0.05) {
			itemName = "아데나";
			count = 100000;
		}  else if (chance < 0.009) {
			itemName = "아데나";
			count = 1000000;
		} else {
			itemName = "아데나";
			count = 10000;
		}
		
		// 아이템 지급.
		if(itemName != null){
			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(itemName));
			if(ii != null){
				ii.setCount(count);
				ii.setBless(bress);
				ii.setQuantity(quantity);
				cha.toGiveItem(this, ii, ii.getCount());
			}
		}
		
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
