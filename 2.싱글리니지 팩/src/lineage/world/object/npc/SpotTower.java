package lineage.world.object.npc;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Monster;
import lineage.bean.lineage.Map;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BlueMessage;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;

public class SpotTower extends Character {
	public boolean isStart;
	private object crown;			// 실제 픽업될 면류관
	private object crown_visual;	// 보기주기용 면류관
	private int gfxModeTemp;
	public long endTime;
	private List<MonsterInstance> monsterList;
	private String[][] monsterNameList;
	private int[] hpStep;
	private int step;
	public int monKillCount;
	
	public SpotTower() {
		crown_visual = new object();
		crown_visual.setObjectId(ServerDatabase.nextEtcObjId());
		crown_visual.setGfx(1482);
		crown = new SpotCrown(this);
		crown.setObjectId(ServerDatabase.nextEtcObjId());
		//crown.setGfx(462);
		crown.setGfx(15363);
		crown.setName("면류관");
		
		monsterList = new ArrayList<MonsterInstance>();
		
		// 남은 체력이 몇% 이하일 경우 몬스터 스폰.
		// 높은 값부터 차례대로 입력.
		// 값이 4개일 경우 4단계로 스폰.
		hpStep = new int[] {80, 60, 40, 20};
		
		// 스텝 별로 스폰될 몬스터
		// 체력 값의 갯수만큼 몬스터 단계도 맞춰서 설정해줘야함.
		monsterNameList = new String[][] {{"카스파(1)", "세마(1)", "발터자르(1)", "메르키오르(1)"},					// 1단계 스폰 몬스터
										{"큰뼈 흑장로", "네크로맨서"},														// 2단계 스폰 몬스터
										{"바포메트", "베레스"},														// 3단계 스폰 몬스터
										{"데스나이트"}};															// 4단계 스폰 몬스터
	}
	
	public object getCrown() {
		return crown;
	}

	public object getCrownVisual() {
		return crown_visual;
	}
	
	public int getMonsterListSize() {
		return monsterList.size();
	}

	@Override
	public void setDead(boolean dead) {
		super.setDead(dead);

		if (dead) {
			// 면류관 그리기.
			getCrownVisual().toTeleport(x, y, map, false);
			// 픽업용 면류관 그리기.
			getCrown().toTeleport(x, y, map, false);
		}
	}
	
	@Override
	public int getGfxMode() {
		return isDead() ? 35 : gfxMode;
	}
	
	public void start(long time) {
		monKillCount = 0;
		setMaxHp(Lineage.spot_tower_hp);
		setNowHp(Lineage.spot_tower_hp);
		
		isStart = true;
		endTime = time + (1000 * Lineage.spot_tower_time);
		toTeleport(Lineage.spot_tower_x, Lineage.spot_tower_y, Lineage.spot_tower_map, false);
		
		String msg = "\\fY         ***** 스팟 쟁탈전이 시작되었습니다. *****";
		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
		if (Lineage.is_blue_message)
			World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
	}
	
	public void end(boolean isTimeOver) {
		toRevival(null);
		
		isStart = false;
		endTime = 0L; 
		step = 0;
		monKillCount = 0;
		monsterList.clear();
		// 타워 제거.
		clearList(true);
		World.remove(this);
		
		if (isTimeOver) {
			String msg = "\\fY         ***** 스팟 쟁탈전이 종료되었습니다. *****";
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
			if (Lineage.is_blue_message)
				World.toSender(S_BlueMessage.clone(BasePacketPooling.getPool(S_BlueMessage.class), 556, msg));
		}
	}
	
	@Override
	public void setNowHp(int nowHp) {	
		// 스팟진행중일때 타워 hp 세팅. 그리고 죽지 않앗을때.
		if (checkMonster() && (isWorldDelete() || (!isDead() && isStart))) {
			super.setNowHp(nowHp);

			// 부서졌을경우.
			if (isDead()) {
				toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, getGfxMode()), false);
			} else {
				toGfxMode();
				// gfxmode값이 변경됫을경우 전송.
				if (getGfxMode() != gfxModeTemp) {
					setGfxMode(gfxModeTemp);
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, getGfxMode()), false);
				}
			}
		}
		
		// 일정 체력 이하일 경우 몬스터 스폰.
		if (isStart && !isDead())
			spawnMonster();
	}
	
	/**
	 * 스텝마다 몬스터를 제거하였는지 확인.
	 * 2019-10-23
	 * by connector12@nate.com
	 */
	public boolean checkMonster() {
		synchronized (monsterList) {
			if (monsterList.size() > 0)
				return false;
		}

		return true;
	}
	
	public void toTimer() {
		if (isStart) {
			synchronized (monsterList) {
				List<MonsterInstance> list = new ArrayList<MonsterInstance>(monsterList);
				
				for (MonsterInstance mi : list) {
					if (mi == null || mi.isWorldDelete() || mi.isDead() || mi.getNowHp() < 1 || mi.getX() == 0 || mi.getY() == 0) {
						monKillCount--;
						monsterList.remove(mi);
					}
				}
			}
		}
	}
	
	@Override
	public void toRevival(object o){
		if(o != null)
			return;
		
		super.toReset(false);
		
		// 다이상태 풀기.
		setDead(false);
		// 체력 채우기.
		setMaxHp(Lineage.spot_tower_hp);
		setNowHp(Lineage.spot_tower_hp);
		// 패킷 처리.
		setGfxMode(getClassGfxMode());
		toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), temp_object_1, this), false);
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object...opt){
		if (cha.getGm() == 0 && cha.getClanId() < 1)
			return;
		// 처리.
		super.toDamage(cha, dmg, type);
	}
	
	/**
	 * hp 값에 따른 gfxmode 변경 처리 함수.
	 */
	private void toGfxMode() {
		int hp = (int) Math.round(((double) getNowHp() / (double) getTotalHp()) * 100);
		
		if (hp > 50)
			gfxModeTemp = 32;
		else if (hp > 20)
			gfxModeTemp = 33;
		else
			gfxModeTemp = 34;
	}
	
	/**
	 * 체력을 확인하여 스텝마다 몬스터 스폰.
	 * 2019-10-23
	 * by connector12@nate.com
	 */
	public void spawnMonster() {
		if (hpStep.length <= step || monsterNameList.length <= step || !checkMonster())
			return;

		int hp = (int) Math.round(((double) getNowHp() / (double) getTotalHp()) * 100);

		if (hp <= hpStep[step]) {
			boolean isSpawn = false;

			for (int i = 0; i < monsterNameList[step].length; i++) {
				int count = 1;
				String monsterName = null;

				if (monsterNameList[step][i].contains("(")) {
					// 몬스터 이름에서 '('가 포함되어있으면 뒤에 몹개체수를 설정하고 없을경우 기본 1마리로 설정.
					monsterName = monsterNameList[step][i].substring(0, monsterNameList[step][i].indexOf("("));
					count = Integer.valueOf(monsterNameList[step][i].substring(monsterNameList[step][i].indexOf("(") + 1, monsterNameList[step][i].indexOf(")")));
				} else {
					monsterName = monsterNameList[step][i];
				}

				// 몬스터 스폰
				Monster monster = MonsterDatabase.find(monsterName);
				if (monster != null)
					isSpawn = toMonster(this, monster, count, 5);
			}

			if (isSpawn)
				ChattingController.toChatting(this, "몬스터를 모두 처지해야 타워 공격이 가능합니다.", Lineage.CHATTING_MODE_SHOUT);

			step++;
		}
	}
	
	private boolean toMonster(object o, Monster monster, int count, int range) {
		boolean result = false;
		Map m = World.get_map(o.getMap());
		
		if (m != null) {
			int x1 = m.locX1;
			int x2 = m.locX2;
			int y1 = m.locY1;
			int y2 = m.locY2;
		
			for (int i = 0; i < count; ++i) {
				MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(monster);

				if (mi != null) {
					mi.setHomeX(o.getX());
					mi.setHomeY(o.getY());
					mi.setHomeMap(o.getMap());
					mi.setHeading(Util.random(0, 7));

					if (range > 1) {					
						int roop_cnt = 0;
						int x = o.getX();
						int y = o.getY();
						int map = o.getMap();
						int lx = x;
						int ly = y;
						int loc = range;
						// 랜덤 좌표 스폰
						do {
							lx = Util.random(x - loc < x1 ? x1 : x - loc, x + loc > x2 ? x2 : x + loc);
							ly = Util.random(y - loc < y1 ? y1 : y - loc, y + loc > y2 ? y2 : y + loc);
							if (roop_cnt++ > 100) {
								lx = x;
								ly = y;
								break;
							}
						}while(
								!World.isThroughObject(lx, ly+1, map, 0) || 
								!World.isThroughObject(lx, ly-1, map, 4) || 
								!World.isThroughObject(lx-1, ly, map, 2) || 
								!World.isThroughObject(lx+1, ly, map, 6) ||
								!World.isThroughObject(lx-1, ly+1, map, 1) ||
								!World.isThroughObject(lx+1, ly-1, map, 5) || 
								!World.isThroughObject(lx+1, ly+1, map, 7) || 
								!World.isThroughObject(lx-1, ly-1, map, 3) ||
								World.isNotMovingTile(lx, ly, map)
							);
						
						mi.toTeleport(lx, ly, o.getMap(), false);
					} else {
						mi.toTeleport(o.getX(), o.getY(), o.getMap(), false);
					}			

					AiThread.append(mi);
					World.appendMonster(mi);
					monsterList.add(mi);
					monKillCount++;
					result = true;
					mi.setSpotMonster(true);
				}
			}
		}
		
		return result;
	}
}
