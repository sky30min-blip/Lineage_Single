package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.world.controller.Thebes;
import lineage.world.object.instance.MonsterInstance;

public class 테베아누비스 extends MonsterInstance{

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new 테베아누비스();
		return MonsterInstance.clone(mi, m);
	}
	@Override
	protected void toAiDead(long time) {
		Thebes.getInstance().보스처치();
		super.toAiDead(time);
	}
}
