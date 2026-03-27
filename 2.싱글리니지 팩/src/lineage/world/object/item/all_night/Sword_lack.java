package lineage.world.object.item.all_night;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class Sword_lack extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new Sword_lack();
		return item;
	}

	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha.getInventory() != null) {
			PcInstance pc = (PcInstance) cha;
			pc.칼렉풀기();
		}
	}
}
