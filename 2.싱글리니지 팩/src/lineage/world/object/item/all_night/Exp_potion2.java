package lineage.world.object.item.all_night;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Exp_Potion;

public class Exp_potion2 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Exp_potion2();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			Skill s = null;

			s = SkillDatabase.find(211);

			if (s != null) {
				if (isLvCheck(cha)) {
					Exp_Potion.onBuff(cha, s, s.getBuffDuration(), false);
					// 아이템 수량 갱신
					cha.getInventory().count(this, getCount() - 1, true);
				}
			}
		}
	}
}
