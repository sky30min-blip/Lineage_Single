package kuberaitem;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BookController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class 희미한기억의구슬 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new 희미한기억의구슬();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){

		PcInstance pc = (PcInstance) cha;
		BookController.Bookmarkitem(pc);
		cha.getInventory().count(this, getCount()-1, true);
	}
}
