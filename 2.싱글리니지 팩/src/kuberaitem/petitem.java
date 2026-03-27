package kuberaitem;

import lineage.database.AccountDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class petitem extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new petitem();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		PcInstance pc = (PcInstance)cha;

	
	}
}
