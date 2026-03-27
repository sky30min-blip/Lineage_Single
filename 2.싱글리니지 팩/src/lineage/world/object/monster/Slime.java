package lineage.world.object.monster;

import java.util.List;

import lineage.bean.database.Monster;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Slime extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Slime();
		return MonsterInstance.clone(mi, m);
	}
	
	private int remove_item_time;
	
	@Override
	public void toAi(long time){
		super.toAi(time);
		
		// 인벤토리에 있는 아이템 소화시키기.
		if(++remove_item_time >= 60){
			remove_item_time = 0;
			List<ItemInstance> list = inv.getList();
			if(list.size() > 0){
				ItemInstance temp = list.get(0);
				if(temp != null)
					inv.count(temp, 0, false);
			}
		}
	}
}
