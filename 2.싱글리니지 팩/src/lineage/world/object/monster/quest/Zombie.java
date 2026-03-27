package lineage.world.object.monster.quest;

import lineage.bean.database.Monster;
import lineage.database.ItemDatabase;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;

public class Zombie extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Zombie();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void readDrop(int map){
		// 퀘스트 맵에 잇는 녀석들은 퀘스트 아이템 등록.
		if(getMap() != 201){
//			super.readDrop(map);
		}else{
			ItemInstance ii = ItemDatabase.newInstance( ItemDatabase.find("좀비 열쇠") );
			if(ii != null)
				inv.append(ii, true);
		}
	}

}
