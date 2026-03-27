package lineage.world.object.item.helm;

import lineage.bean.lineage.Inventory;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class HelmMagicPower extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HelmMagicPower();
		return item;
	}
	
	@Override
	public void toEquipped(Character cha, Inventory inv){
		super.toEquipped(cha, inv);

		// 인챈트 웨폰, 디텍션, 인챈트 마이티
		if(equipped){
			SkillController.append(cha, 12, true);
			SkillController.append(cha, 13, true);
			SkillController.append(cha, 42, true);
			SkillController.sendList(cha);
		}else{
			SkillController.remove(cha, 12, true);
			SkillController.remove(cha, 13, true);
			SkillController.remove(cha, 42, true);
		}
	}

}
