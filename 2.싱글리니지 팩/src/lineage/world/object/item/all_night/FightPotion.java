package lineage.world.object.item.all_night;

import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.BuffFight;

public class FightPotion extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new FightPotion();
		return item;
	}
	
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			BuffFight.onBuff(cha, SkillDatabase.find(601));
			// 아이템 수량 갱신
			if (getItem() != null && !getItem().getName().contains("코인"))
				// 아이템 수량 갱신
				cha.getInventory().count(this, getCount() - 1, true);
		}
	}
}
