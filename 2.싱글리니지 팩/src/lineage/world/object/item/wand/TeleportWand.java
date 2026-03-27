package lineage.world.object.item.wand;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class TeleportWand extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new TeleportWand();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		int obj_id = cbp.readD();
		int x = cbp.readH();
		int y = cbp.readH();
		
		PcInstance pc = (PcInstance) cha;
		pc.toTeleport(x, y, cha.getMap(), false);
	}
}
