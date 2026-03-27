package lineage.world.object.item.all_night;

import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class ItemSwap extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemSwap();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null)
			NpcSpawnlistDatabase.itemSwap.toTalk((PcInstance) cha, null);
	}
}
