package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.database.TeleportResetDatabase;
import lineage.world.World;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class Baphomet extends MonsterInstance {
	
	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Baphomet();
		return MonsterInstance.clone(mi, m);
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		// 보스몬스터 일경우 말섬던전2층에 사용자들 리스폰위치로 강제 이동 시키기.
//		if(boss){
//			for(PcInstance pc : World.getPcList()){
//				if(pc.getMap() == 2){
//					TeleportResetDatabase.toLocation(pc);
//					pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), false);
//				}
//			}
//		}
	}

}
