package lineage.world.object.item;

import lineage.world.controller.ElvenforestController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ElementalStone extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ElementalStone();
		return item;
	}

	@Override
	public void toPickup(Character cha){
		super.toPickup(cha);
		
		// 요정숲 관리목록에서 제거.
		ElvenforestController.removeStone(this);
	}
}
