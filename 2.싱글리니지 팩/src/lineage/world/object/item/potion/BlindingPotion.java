package lineage.world.object.item.potion;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.CurseBlind;

public class BlindingPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new BlindingPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;
		
		// 스킬 적용.
		Skill skill = SkillDatabase.find(3, 3);
		if(skill != null)
			BuffController.append(cha, CurseBlind.clone(BuffController.getPool(CurseBlind.class), skill, 10*60));
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
