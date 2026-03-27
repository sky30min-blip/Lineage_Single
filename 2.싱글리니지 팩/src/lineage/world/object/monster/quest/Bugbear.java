package lineage.world.object.monster.quest;

import lineage.bean.database.Item;
import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Bugbear extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Bugbear();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void readDrop(int map) {
		// 본던 7층 버그베어 일경우 비밀방 열쇠 드랍하도록 하기.
		if (getMap() == 13) {
			Item item = ItemDatabase.find("비밀방 열쇠");
			if (item != null) {
				double chance = 0.1 + item.getDropChance();
				if (Math.random() < chance * Lineage.rate_drop) {
					ItemInstance ii = ItemDatabase.newInstance(item);
					if (ii != null)
						inv.append(ii, true);
				}
			}
		}
//		super.readDrop(map);
	}

}
