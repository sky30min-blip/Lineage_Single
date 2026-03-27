package lineage.world.object.item.all_night;

import lineage.database.BackgroundDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class notice extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new notice();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null){
			BoardInstance b = BackgroundDatabase.getNoticeBoard();
			if (b != null)
				b.toClick(cha, null);
		}

	}
}
