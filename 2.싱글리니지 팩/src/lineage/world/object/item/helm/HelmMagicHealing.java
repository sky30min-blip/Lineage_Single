package lineage.world.object.item.helm;

import lineage.bean.lineage.Inventory;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class HelmMagicHealing extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HelmMagicHealing();
		return item;
	}
	
	@Override
	public void toEquipped(Character cha, Inventory inv){
		super.toEquipped(cha, inv);

		// 힐, 익힐
		if(equipped){
			SkillController.append(cha, 1, true);
			SkillController.append(cha, 19, true);
			SkillController.sendList(cha);
		}else{
			SkillController.remove(cha, 1, true);
			SkillController.remove(cha, 19, true);
		}
	}

}
