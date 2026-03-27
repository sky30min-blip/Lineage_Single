package lineage.world.object.item.all_night;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Drop;
import lineage.database.MonsterDropDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ItemDropCheckWand extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ItemDropCheckWand();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if (cha.getInventory() != null) {
			ItemInstance item = cha.getInventory().value(cbp.readD());
			
			if (item != null && item.getItem() != null) {
				String itemName = item.getItem().getName();
				List<String> list = new ArrayList<String>();
				list.add(itemName);

				for (Drop d : MonsterDropDatabase.getDropList()) {
					if (list.size() >= 250)
						break;
					
					if (d.getItemName().equalsIgnoreCase(itemName)) {
						if (d.getItemBress() == 1) 
							list.add(String.format(" %s", d.getMonName()));
					}
				}
				
				if (list.size() < 2)
					cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), cha, "itemdrop1", null, list));
				else
					cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), cha, "itemdrop", null, list));
			}
		}
	}
}