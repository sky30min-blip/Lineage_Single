package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class BombFlower extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new BombFlower();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void toMoving(object o, final int x, final int y, final int h, final boolean astar){
		// 이동 할필요 없음.
	}
	
	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple){
		super.toAttack(o, x, y, bow, this.gfxMode+Lineage.GFX_MODE_SPELL_DIRECTION, 0, isTriple);
	}
	
}
