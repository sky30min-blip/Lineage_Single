package lineage.world.object.item;

import lineage.world.object.instance.ItemInstance;

public class Aden extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Aden();
		return item;
	}

}
