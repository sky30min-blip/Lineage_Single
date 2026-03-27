package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Cook;
import lineage.world.controller.CookController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class CookBook extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new CookBook();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cbp.readC() == 0) {
			cha.toSender(S_Cook.clone(BasePacketPooling.getPool(S_Cook.class), cha, this));
		} else {
			CookController.toCook(cha, "request cook " + cbp.readC());
		}
	}
}