package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.world.object.instance.GuardInstance;

public class PatrolGuard extends GuardInstance {

	/**
	 * 순찰병
	 * @param npc
	 */
	public PatrolGuard(Npc npc){
		super(npc);
	}
	
	@Override
	protected void toAiWalk(long time){
		super.toAiWalk(time);
		
		// 피커 찾기.
		toSearchPKer();
	}

}
