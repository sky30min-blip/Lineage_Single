package lineage.world.object.monster;

import all_night.Lineage_Balance;
import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.util.Util;
import lineage.world.controller.BossController;
import lineage.world.object.instance.MonsterInstance;

public class Grimreaper extends MonsterInstance {
	private boolean isCheck = false;
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new Grimreaper();

		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void setNowHp(int nowHp) {
		if (!isCheck && getTotalHp() * (Util.random(Lineage_Balance.oman_spawn_hp_min, Lineage_Balance.oman_spawn_hp_max)) > nowHp && getMap() != 200) {
			isCheck = true;
			
			if (Math.random() < Lineage_Balance.oman_spawn_probability) {
				Monster monster = null;

				switch (getMap()) {
				case 101:
					monster = MonsterDatabase.find("왜곡의 제니스 퀸");
					break;
				case 102:
					monster = MonsterDatabase.find("불신의 시어");
					break;
				case 103:
					monster = MonsterDatabase.find("공포의 뱀파이어");
					break;
				case 104:
					monster = MonsterDatabase.find("죽음의 좀비 로드");
					break;
				case 105:
					monster = MonsterDatabase.find("지옥의 쿠거");
					break;
				case 106:
					monster = MonsterDatabase.find("불사의 좀비 머미 로드");
					break;
				case 107:
					monster = MonsterDatabase.find("잔혹한 아이리스");
					break;
				case 108:
					monster = MonsterDatabase.find("어둠의 나이트발드");
					break;
				case 109:
					monster = MonsterDatabase.find("불멸의 리치");
					break;
				case 110:
					monster = MonsterDatabase.find("오만한 우그누스");
					break;
				}

				if (monster != null && !BossController.isSpawn(monster.getName(), getMap())) {
					if (MonsterSpawnlistDatabase.toSpawnMonster(monster, x, y, map, heading, true, this)) {
						BossController.toWorldOut(this);
						isCheck = false;
						return;
					}
				}
			}
		}
		super.setNowHp(nowHp);
		
		if (isDead()) {
			isCheck = false;
		}
	}
}
