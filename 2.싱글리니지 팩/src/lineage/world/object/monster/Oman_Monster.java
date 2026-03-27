package lineage.world.object.monster;

import all_night.Lineage_Balance;
import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.world.controller.BossController;
import lineage.world.object.instance.MonsterInstance;

public class Oman_Monster extends MonsterInstance {

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Oman_Monster();

		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void setNowHp(int nowHp) {
		if (nowHp <= 0 && Math.random() < Lineage_Balance.grimreaper_spawn_probability && !BossController.isSpawn("감시자 리퍼", getMap()) && getMap() >= 101 && getMap() <= 200) {
			Monster monster = MonsterDatabase.find("감시자 리퍼");

			if (monster != null && MonsterSpawnlistDatabase.toSpawnMonster(monster, x, y, map, heading, true, this)) {
				return;
			}
		}

		super.setNowHp(nowHp);
	}
}
