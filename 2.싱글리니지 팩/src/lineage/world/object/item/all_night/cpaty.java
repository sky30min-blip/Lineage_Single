package lineage.world.object.item.all_night;

import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Party;
import lineage.database.BackgroundDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.CommandController;
import lineage.world.controller.PartyController;
import lineage.world.object.Character;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class cpaty extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new cpaty();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		CommandController.혈맹파티(cha);
	}
}
