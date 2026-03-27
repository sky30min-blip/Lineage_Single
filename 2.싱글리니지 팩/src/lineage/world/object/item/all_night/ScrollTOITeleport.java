package lineage.world.object.item.all_night;

import lineage.bean.database.ItemTeleport;
import lineage.database.ItemTeleportDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.item.scroll.ScrollTeleport;

public class ScrollTOITeleport extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollTOITeleport();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		try {
			// 초기화
			int uid = Integer.valueOf( getItem().getType2().substring( getItem().getType2().indexOf("_")+1 ) );
			ItemTeleport it = ItemTeleportDatabase.find(uid);
	
			// 제거
			if(it.isRemove())
				cha.getInventory().count(this, getCount()-1, true);
			// 델레포트
			ItemTeleportDatabase.toTeleport(it, cha, true);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toClick(Character cha, ClientBasePacket cbp)\r\n", ScrollTeleport.class.toString());
			lineage.share.System.println(e);
		}
	}

}
