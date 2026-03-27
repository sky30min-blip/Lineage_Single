package lineage.world.object.item;

import lineage.share.Lineage;
import lineage.world.object.instance.ItemInstance;

public class Lantern extends Lamp {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Lamp();
		item.setNowTime(60*30);
		item.setDynamicLight(Lineage.LANTERN_LIGHT);
		return item;
	}

}
