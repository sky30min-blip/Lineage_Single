package lineage.world.object.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class SummonMonster extends Magic {

	private List<MonsterInstance> list;
	
	public SummonMonster(Skill skill){
		super(null, skill);
	}
	
	static synchronized public BuffInterface clone(BuffInterface bi, Skill skill, int time, List<MonsterInstance> list){
		if(bi == null)
			bi = new SummonMonster(skill);
		((SummonMonster)bi).setList(list);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		// 버그 방지.
		if (list==null || list.size()<=0) {
			return;
		}
		
		synchronized (list) {
			for(MonsterInstance mi : list){
				// 스폰 처리.
				mi.toTeleport(mi.getHomeX(), mi.getHomeY(), mi.getHomeMap(), false);
				// 이팩트 표현할게 있다면 그걸로 처리.
				if(skill.getCastGfx() > 0)
					mi.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, skill.getCastGfx()), false);
				// 인공지능 활성화.
				AiThread.append(mi);
			}
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}
	
	@Override
	public void toBuff(object o) {
		if (o instanceof MonsterInstance) {
			if (list != null) {
				int count = 0;
				for (MonsterInstance m : list) {
					if (m == null || m.isDead() || m.isWorldDelete() || m.getAiStatus() == Lineage.AI_STATUS_DELETE)
						count++;
				}

				if (count >= list.size())
					toBuffEnd(o);
			}
		}
	}

	@Override
	public void toBuffEnd(object o){
		// 버그 방지.
		if(list==null || list.size()<=0)
			return;
		
		synchronized (list) {
			// 소환된 객체들 제거 처리.
			for(MonsterInstance mi : list) {
				if (mi != null)
					mi.toAiThreadDelete();
			}
			list.clear();
			list = null;
		}
		
		BuffController.remove(o, SummonMonster.class);
	}
	
	public void setList(List<MonsterInstance> list){
		this.list = list;
	}

	static public void init(Character cha, Skill skill, int summon_auction){
		
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
	
		if (SkillController.isMagic(cha, skill, true)) {	
				SummonController.toSummonMonster(cha, skill.getBuffDuration(), summon_auction);
		}
	}
	
	/**
	 * 몬스터용
	 * @param mi
	 * @param o
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check
	 */
	static public void init(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		if(check && !SkillController.isMagic(mi, ms, true))
			return;
		
		Buff buff = BuffController.find(mi);		
		if(buff!=null && buff.find(SummonMonster.class)!=null)
			return;
		
		List<MonsterInstance> list = new ArrayList<MonsterInstance>();
		// 옵션 정보 분석하여 몬스터명 추출.
		StringTokenizer st = new StringTokenizer(ms.getOption(), ",");
		while (st.hasMoreTokens()) {
			// 몬스터 객체 추출.
			Monster mon = MonsterDatabase.find(st.nextToken());
			// 존재할경우만 처리.
			if (mon != null) {
				int count = (int) Util.random(ms.getMindmg(), ms.getMaxdmg());
				// 버그 방지.
				if (count <= 0)
					count = 1;
				// 갯수만큼 처리.
				for (int i = 0; i < count; ++i) {
					int loc = ms.getRange();
					int lx = Util.random(mi.getX() - loc, mi.getX() + loc);
					int ly = Util.random(mi.getY() - loc, mi.getY() + loc);
					int roop_cnt = 0;
					// 메모리 생성 처리.
					MonsterInstance monster = MonsterSpawnlistDatabase.newInstance(mon);
					// 있을경우만.
					if (monster != null) {						
						// 좌표 스폰
						if (ms.getRange() > 0){
							while(
									!World.isThroughObject(lx, ly+1, mi.getMap(), 0) || 
									!World.isThroughObject(lx, ly-1, mi.getMap(), 4) || 
									!World.isThroughObject(lx-1, ly, mi.getMap(), 2) || 
									!World.isThroughObject(lx+1, ly, mi.getMap(), 6) ||
									!World.isThroughObject(lx-1, ly+1, mi.getMap(), 1) || 
									!World.isThroughObject(lx+1, ly-1, mi.getMap(), 5) || 
									!World.isThroughObject(lx+1, ly+1, mi.getMap(), 7) || 
									!World.isThroughObject(lx-1, ly-1, mi.getMap(), 3)
								){
								lx = Util.random(lx - loc, lx + loc);
								ly = Util.random(ly - loc, ly + loc);

								if (roop_cnt++ > 100) {
									lx = mi.getX();
									ly = mi.getY();
									break;
								}
							}
						}
						
						// 기본 정보 세팅.
						monster.setHomeX(lx);
						monster.setHomeY(ly);
						monster.setHomeMap(mi.getMap());
//						monster.readDrop(mi.getMap());
						
						for (object cha : mi.getAttackList())
							monster.addAttackList(cha);
						// 관리목록에 등록.
						list.add(monster);
					}
				}
			}
		}
		
		// 중복 소환방지를 위해 버프에 등록.
		BuffController.append(mi, SummonMonster.clone(BuffController.getPool(SummonMonster.class), ms, ms.getBuffDuration(), list));
	}
}
