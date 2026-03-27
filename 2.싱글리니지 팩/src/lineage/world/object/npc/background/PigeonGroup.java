package lineage.world.object.npc.background;

import lineage.database.SpriteFrameDatabase;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.instance.BackgroundInstance;

public class PigeonGroup extends BackgroundInstance {

	private int reSpawnTime;				// 재스폰 하기위한 대기 시간값.
	// 재스폰대기(toAiSpawn) 구간에서 사용중.
	private long ai_time_temp_1;
	
	public PigeonGroup(){
		reSpawnTime = 1000 * 10;
		
		AiThread.append(this);
	}
	
	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		setDead(true);
		setAiStatus(Lineage.AI_STATUS_DEAD);
		
		// 라우풀 깍기
		cha.setLawful(cha.getLawful() - 10);
	}

	@Override
	protected void toAiWalk(long time){
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode+Lineage.GFX_MODE_TYPE);
	}
	
	@Override
	protected void toAiDead(long time){
		super.toAiDead(time);
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_CORPSE);
	}
	
	@Override
	protected void toAiCorpse(long time){
		super.toAiCorpse(time);
		
		// 버프제거
		toReset(true);
		// 시체 제거
		clearList(true);
		World.remove(this);
		// 상태 변환.
		setAiStatus(Lineage.AI_STATUS_SPAWN);
	}
	
	@Override
	protected void toAiSpawn(long time){
		super.toAiSpawn(time);
		
		if(ai_time_temp_1 == 0)
			ai_time_temp_1 = time;
		// 스폰 대기.
		if(ai_time_temp_1+reSpawnTime > time){
			
		}else{
			ai_time_temp_1 = 0;
			// 상태 변환
			setDead(false);
			// 스폰
			toTeleport(homeX, homeY, homeMap, false);
			// 상태 변환.
			setAiStatus(Lineage.AI_STATUS_WALK);
		}
	}
	
}
