package lineage.world.object.monster.quest;

import lineage.bean.database.Monster;
import lineage.world.object.instance.MonsterInstance;

public class OrcZombie extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new OrcZombie();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void readDrop(int map){
		// 퀘스트 맵에 잇는 녀석들은 드랍 무시.
//		if(getMap() != 201)
//			super.readDrop(map);
	}

}
