package lineage.world.object.item.quest;

import lineage.world.object.instance.ItemInstance;

public class BlackKey extends RedKey {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new BlackKey();
		return item;
	}

}
