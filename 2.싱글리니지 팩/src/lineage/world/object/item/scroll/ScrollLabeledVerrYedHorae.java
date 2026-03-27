package lineage.world.object.item.scroll;

import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.LocationController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollLabeledVerrYedHorae extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollLabeledVerrYedHorae();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.getInventory().count(this, getCount()-1, true);
		
		if(LocationController.isTeleportVerrYedHoraeZone(cha, true)){
			TeleportHomeDatabase.toLocation(cha);
            cha.toPotal(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap());
		}
	}

}
