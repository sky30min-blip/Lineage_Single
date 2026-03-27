package lineage.world.object.item;

import lineage.database.BackgroundDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;

public class MagicFirewood extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new MagicFirewood();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		//
		cha.getInventory().count(this, getCount()-1, true);
		//
		int x = cha.getX();
		int y = cha.getY();
		switch(cha.getHeading()){
			case 0:
				y -= 1;
				break;
			case 1:
				x += 1;
				y -= 1;
				break;
			case 2:
				x += 1;
				break;
			case 3:
				x += 1;
				y += 1;
				break;
			case 4:
				y += 1;
				break;
			case 5:
				x -= 1;
				y += 1;
				break;
			case 6:
				x -= 1;
				break;
			case 7:
				x -= 1;
				y -= 1;
				break;
		}
		//
		BackgroundInstance firewood = lineage.world.object.npc.background.MagicFirewood.clone(BackgroundDatabase.getPool(MagicFirewood.class));
		firewood.setGfx(5943);
		firewood.setLight(13);
		firewood.setObjectId(ServerDatabase.nextNpcObjId());
		firewood.toTeleport(x, y, cha.getMap(), false);
	}

}
