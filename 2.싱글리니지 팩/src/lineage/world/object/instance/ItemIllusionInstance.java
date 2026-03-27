package lineage.world.object.instance;

import lineage.database.ItemDatabase;
import lineage.world.World;
import lineage.world.controller.EventController;
import lineage.world.object.object;


public class ItemIllusionInstance extends ItemInstance {
	
	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ItemIllusionInstance();
		return item;
	}

	@Override
	public void close(){
		super.close();
		// 환상아이템 관리목록에 제거.
		EventController.removeIllusion(this);
	}

	@Override
	public void toBuffEnd(object o) {
		if(cha!=null){
			if(cha.getInventory()!=null){
				// 착용중이라면 해제.
				if(isEquipped())
					toClick(cha, null);
				// 제거.
				cha.getInventory().count(this, 0, true);
				return;
			}
		}
		// 월드 드랍된 아이템 제거 처리.
		clearList(true);
		World.remove(this);
		ItemDatabase.setPool(this);
	}
	
}
