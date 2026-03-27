package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Lawful_potion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Lawful_potion();
		return item;
	}
	
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			cha.setLawful(Lineage.LAWFUL);
			cha.getInventory().count(this, getCount()-1, true);
		}
	}
}
