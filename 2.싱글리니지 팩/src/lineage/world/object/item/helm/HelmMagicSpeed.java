package lineage.world.object.item.helm;

import lineage.bean.lineage.Inventory;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;

public class HelmMagicSpeed extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HelmMagicSpeed();
		return item;
	}
	
	@Override
	public void toEquipped(Character cha, Inventory inv){
		super.toEquipped(cha, inv);

		// 인챈트 덱스터리티, 헤이스트
		if(equipped){
			SkillController.append(cha, 26, true);
			SkillController.append(cha, 43, true);
			SkillController.sendList(cha);
		}else{
			SkillController.remove(cha, 26, true);
			SkillController.remove(cha, 43, true);
		}
	}

}
