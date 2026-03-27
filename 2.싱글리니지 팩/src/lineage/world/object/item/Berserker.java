package lineage.world.object.item;

import lineage.bean.lineage.Inventory;
import lineage.database.SkillDatabase;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.magic.Haste;

public class Berserker extends ItemWeaponInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Berserker();
		return item;
	}

	@Override
	public void toEquipped(Character cha, Inventory inv){
		super.toEquipped(cha, inv);
		
		if (getItem().getNameIdNumber() == 418) {
			if(equipped){
				// 적용
				BuffController.append(cha, Haste.clone(BuffController.getPool(Haste.class), SkillDatabase.find(43), -1, false));
			}else{
				// 해제
				BuffController.remove(cha, Haste.class);
			}
		}
	}
}
