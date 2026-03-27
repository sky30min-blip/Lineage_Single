package lineage.world.object.item.scroll;

import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class SealedTOITeleportCharm extends ItemInstance {

	private int f;
	
	static synchronized public ItemInstance clone(ItemInstance item, int f){
		if(item == null)
			item = new SealedTOITeleportCharm();
		((SealedTOITeleportCharm)item).f = f;
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 제거
		cha.getInventory().count(this, 0, true);
		// 지급.
		ItemInstance ii = null;
		switch(f){
			case 11:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2400));
				break;
			case 21:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2401));
				break;
			case 31:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2402));
				break;
			case 41:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2403));
				break;
			case 51:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2678));
				break;
			case 61:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2679));
				break;
			case 71:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2680));
				break;
			case 81:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2681));
				break;
			default:
				ii = ItemDatabase.newInstance(ItemDatabase.find(2682));
				break;
		}
		if(ii != null) {
			ii.setObjectId(ServerDatabase.nextItemObjId());
			cha.getInventory().append(ii, true);
		}
	}
	
}
