package lineage.world.object.item.yadolan;

import java.util.ArrayList;
import java.util.List;

import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.RankController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class ShopControllerItem extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ShopControllerItem();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		PcInstance pc = (PcInstance) cha;

		NpcSpawnlistDatabase.marketNpc.toTalk(pc, null);
	}
}
