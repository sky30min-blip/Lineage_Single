package lineage.world.object.item.cloak;

import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class ElvenCloak extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ElvenCloak();
		return item;
	}
	
}
