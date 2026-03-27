package lineage.world.object.item.weapon;

import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class StaffIceQueen extends ItemWeaponInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new StaffIceQueen();
		return item;
	}

	@Override
	public boolean toDamage(Character cha, object o){
		return Util.random(0, 100)<20;
	}

	@Override
	public int toDamage(int dmg){
		return Util.random(50, 100);
	}
	
	@Override
	public int toDamageEffect(){
		return 1809;
	}

}
