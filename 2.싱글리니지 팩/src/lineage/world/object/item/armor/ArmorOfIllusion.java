package lineage.world.object.item.armor;

import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.EventController;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemIllusionInstance;
import lineage.world.object.instance.ItemInstance;

public class ArmorOfIllusion extends ItemArmorInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ArmorOfIllusion();
		// 시간 설정
		item.setNowTime(60*60*Util.random(5, 10));
		// 버프 등록.
		BuffController.append(item, item);
		// 환상아이템 관리목록에 등록.
		EventController.appendIllusion((ItemIllusionInstance)item);
		return item;
	}
	
}
