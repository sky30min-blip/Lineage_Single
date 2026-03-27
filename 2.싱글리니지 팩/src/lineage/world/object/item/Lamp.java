package lineage.world.object.item;

import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;


public class Lamp extends Candle {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Lamp();
		item.setNowTime(60*30);
		item.setDynamicLight(Lineage.LAMP_LIGHT);
		return item;
	}

	@Override
	public void toBuffEnd(object o) {
		equipped = false;
		toOff();
	}
	
}
