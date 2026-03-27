package lineage.world.object.item;

import java.util.ArrayList;
import java.util.List;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class LanternOil extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new LanternOil();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		List<ItemInstance> list_temp = new ArrayList<ItemInstance>();
		cha.getInventory().findDbName("랜턴", list_temp);
		for(ItemInstance ii : list_temp)
			ii.setNowTime(60*30);
		
		cha.getInventory().count(this, getCount()-1, true);
	}
	
}
