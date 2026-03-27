package lineage.world.object.item.weapon;

import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class StaffMana extends ItemWeaponInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new StaffMana();
		return item;
	}

	@Override
	public boolean toDamage(Character cha, object o){
		// 마나 스틸하기
		if(o!=null && o.getNowMp()>0 && (o instanceof MonsterInstance || o instanceof PcInstance)){
			// 1~3랜덤 추출
			int steal_mp = Util.random(1, 3);
			// 인첸트 수치만큼 +@
			if(getEnLevel()>0){
				steal_mp += getEnLevel();
				// 만약 고인첸 일경우 7부터 +1 되도록 하기
				if(getEnLevel()>6)
					steal_mp += getEnLevel()-6;
			}
			// 타켓에 mp가 스틸할 값보다 작을경우 현재 가지고있는 mp값으로 변경
			if(o.getNowMp()<steal_mp)
				steal_mp = o.getNowMp();
			// mp제거하기.
			o.setNowMp(o.getNowMp()-steal_mp);
			// mp추가하기.
			cha.setNowMp(cha.getNowMp()+steal_mp);
		}
		return false;
	}
}
