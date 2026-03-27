package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Inventory;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.DamageController;
import lineage.world.controller.InventoryController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class NpcInstance extends Character {

	protected Npc npc;
	protected List<object> attackList; // 전투 목록
	protected int ai_walk_stay_count; // 랜덤워킹 중 잠시 휴식을 취하기위한 카운팅 값
	protected boolean ai_talk; // 대화를 걸었는지 여부 확인용.
	private int reSpawnTime; // 재스폰 하기위한 대기 시간값.
	// 시체유지(toAiCorpse) 구간에서 사용중.
	// 재스폰대기(toAiSpawn) 구간에서 사용중.
	private long ai_time_temp_1;
	// 인벤토리
	private Inventory inv;
	//
	private List<object> temp_list; // 주변셀 검색에 임시 담기용으로 사용.

	public NpcInstance(Npc npc) {
		this.npc = npc;
		attackList = new ArrayList<object>();
		temp_list = new ArrayList<object>();
		// 인벤토리 활성화를 위해.
		InventoryController.toWorldJoin(this);
	}

	@Override
	public void close() {
		super.close();

		ai_time_temp_1 = reSpawnTime = 0;
		if (attackList != null) {
			synchronized (attackList) {
				attackList.clear();
			}
		}
		if (temp_list != null)
			temp_list.clear();
	}

	private void removeAttackList(object o) {
		synchronized (attackList) {
			attackList.remove(o);
		}
	}

	private void appendAttackList(object o) {
		synchronized (attackList) {
			if (!attackList.contains(o))
				attackList.add(o);
		}
	}

	private List<object> getAttackList() {
		List<object> r_list = new ArrayList<object>();
		synchronized (attackList) {
			r_list.addAll(attackList);
		}
		return r_list;
	}

	public Npc getNpc() {
		return npc;
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

	@Override
	public void setInventory(Inventory inv) {
		this.inv = inv;
	}

	@Override
	public void setDead(boolean dead) {
		super.setDead(dead);
		if (dead) {
			ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_DEAD);
			setAiStatus(Lineage.AI_STATUS_DEAD);
		}
	}

	public int getReSpawnTime() {
		return reSpawnTime;
	}

	@Override
	public void setReSpawnTime(int reSpawnTime) {
		this.reSpawnTime = reSpawnTime;
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		// 공격목록에 추가.
		addAttackList(cha);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		// ai_talk true로 변경해야 도망가지 않고 해당 stay_count를 0까지 완료함.
		ai_talk = true;
		ai_walk_stay_count = Lineage.npc_talk_stay_time;
		// pc 쪽으로 방향 전환.
		setHeading(Util.calcheading(this, pc.getX(), pc.getY()));
		toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
	}

	@Override
	public void setNowHp(int nowHp) {
		super.setNowHp(nowHp);
	}

	/**
	 * 공격자 목록에 등록처리 함수.
	 * 
	 * @param o
	 */
	public void addAttackList(object o) {
		if (o == null)
			return;

		if (!o.isDead() && o.getObjectId() != getObjectId())
			appendAttackList(o);
	}

	/**
	 * 전투목록에서 원하는 위치에있는 객체 찾아서 리턴.
	 * 
	 * @param index
	 * @return
	 */
	protected object getAttackList(int index) {
		if (attackList.size() > index) {
			synchronized (attackList) {
				return attackList.get(index);
			}
		} else {
			return null;
		}
	}

	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple) {
		int effect = bow ? npc.getArrowGfx() != 0 ? npc.getArrowGfx() : 66 : 0;
		int dmg = DamageController.getDamage(this, o, bow, null, null, alpha_dmg);
		DamageController.toDamage(this, o, dmg, bow ? Lineage.ATTACK_TYPE_BOW : Lineage.ATTACK_TYPE_WEAPON);

		// 칼렉
		if (!Lineage.is_sword_lack_check || (Lineage.is_sword_lack_check && lastDamageActionTime < System.currentTimeMillis()))
			toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, gfxMode, dmg, effect, bow, true, 0, 0), false);

		setHeading(Util.calcheading(this, o.getX(), o.getY()));
	}

	/**
	 * 해당객체를 공격해도 되는지 분석하는 함수.
	 * 
	 * @param o
	 * @param walk
	 *            : 랜덤워킹 상태 체크인지 구분용
	 * @return
	 */
	protected boolean isAttack(object o, boolean walk) {
		if (o == null)
			return false;
		if (o.isDead())
			return false;
		if (o.isWorldDelete())
			return false;
		if (o.getGm() > 0)
			return false;
		if (o.isInvis())
			return false;
		if (o.isTransparent())
			return false;
		if (!Util.isDistance(this, o, Lineage.SEARCH_MONSTER_TARGET_LOCATION))
			return false;
		return true;
	}

	@Override
	public void toAi(long time) {
		switch (ai_status) {
		// 공격목록이 발생하면 공격모드로 변경. 대신 대화걸린 상태가 아닐때만.
		case Lineage.AI_STATUS_WALK:
			if (ai_talk == false && attackList.size() > 0)
				setAiStatus(Lineage.AI_STATUS_ATTACK);
			break;

		// 전투 처리부분은 항상 타켓들이 공격가능한지 확인할 필요가 있음.
		case Lineage.AI_STATUS_ATTACK:
		case Lineage.AI_STATUS_ESCAPE:
			// 공격자 목록 체크해서 목록에서 제거하는 구간인데..
			// 피케이들은 여기서 제거가 안되서 계속 경비병이 피커를 공격함.(마을이고, 12셀 안에 있을때.)
			// 개선해야함 - psjump
			for (object o : getAttackList()) {
				if (!isAttack(o, false))
					removeAttackList(o);
			}
			// 전투목록이 없을경우 랜덤워킹으로 변경. 누군가 나에게 대화를 걸었을경우 랜덤워킹으로 변경.
			if (ai_talk || attackList.size() == 0)
				setAiStatus(Lineage.AI_STATUS_WALK);
			break;
		}

		super.toAi(time);
	}

	@Override
	protected void toAiWalk(long time) {
		super.toAiWalk(time);

		// 아직 휴식카운팅값이 남앗을경우 리턴.
		if (ai_walk_stay_count-- > 0)
			return;

		// 대화를 걸었는지 여부 확인하는 변수 초기화.
		if (ai_talk)
			ai_talk = false;

		do {
			switch (Util.random(0, 5)) {
			case 0:
				ai_walk_stay_count = Util.random(5, 10);
				break;
			case 1:
			case 2:
				setHeading(getHeading() + 1);
				break;
			case 3:
			case 4:
				setHeading(getHeading() - 1);
				break;
			default:
				setHeading(Util.random(0, 7));
				break;
			}
			// 이동 좌표 추출.
			int x = Util.getXY(heading, true) + this.x;
			int y = Util.getXY(heading, false) + this.y;
			// 스폰된 위치에서 너무 벗어낫을경우 스폰쪽으로 유도하기.
			if (!Util.isDistance(x, y, map, homeX, homeY, homeMap, Lineage.SEARCH_LOCATIONRANGE)) {
				heading = Util.calcheading(this, homeX, homeY);
				x = Util.getXY(heading, true) + this.x;
				y = Util.getXY(heading, false) + this.y;
			}
			// 해당 좌표 이동가능한지 체크.
			boolean tail = World.isThroughObject(this.x, this.y, this.map, heading);
			// 해당좌표에 객체 있는지 확인.
			boolean obj = false;
			temp_list.clear();
			findInsideList(x, y, temp_list);
			for (object o : temp_list) {
				if (o instanceof Character) {
					obj = true;
					break;
				}
			}
			if (tail && !obj) {
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				super.toMoving(x, y, heading);
			} else {
				if (Util.random(0, 3) != 0)
					continue;
			}
		} while (false);
	}

	@Override
	public void toAiAttack(long time) {
		// 전투상태로 전환되면 도망모드로 다시 변경.
		setAiStatus(Lineage.AI_STATUS_ESCAPE);
	}

	@Override
	public void toAiEscape(long time) {
		super.toAiEscape(time);

		// 전투목록에서 가장 근접한 사용자 찾기.
		object o = null;
		for (object oo : getAttackList()) {
			if (o == null)
				o = oo;
			else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
				o = oo;
		}

		// 못찾앗을경우 무시. 가끔생길수 잇는 현상이기에..
		if (o == null) {
			setAiStatus(Lineage.AI_STATUS_WALK);
			return;
		}

		// 반대방향 이동처리.
		heading = Util.oppositionHeading(this, o);
		int temp_heading = heading;
		do {
			// 이동 좌표 추출.
			int x = Util.getXY(heading, true) + this.x;
			int y = Util.getXY(heading, false) + this.y;
			// 해당 좌표 이동가능한지 체크.
			boolean tail = World.isThroughObject(this.x, this.y, this.map, heading);
			// 해당좌표에 객체 있는지 확인.
			boolean obj = false;
			temp_list.clear();
			findInsideList(x, y, temp_list);
			for (object oo : temp_list) {
				if (oo instanceof Character) {
					obj = true;
					break;
				}
			}
			if (tail && !obj) {
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				super.toMoving(x, y, heading);
				break;
			} else {
				setHeading(heading + 1);
				if (temp_heading == heading)
					break;
			}
		} while (true);
	}

	@Override
	protected void toAiDead(long time) {
		super.toAiDead(time);

		// 아이템 드랍
		int x = 0;
		int y = 0;
		for (ItemInstance ii : inv.getList()) {
			if (ii.getObjectId() == 0)
				ii.setObjectId(ServerDatabase.nextItemObjId());
			x = Util.random(getX() - 1, getX() + 1);
			y = Util.random(getY() - 1, getY() + 1);

			if (World.isThroughObject(x, y + 1, map, 0))
				ii.toTeleport(x, y, map, false);
			else
				ii.toTeleport(this.x, this.y, map, false);
			// 드랍됫다는거 알리기.
			ii.toDrop(this);
		}
		inv.clearList();
		for (object o : getAttackList()) {
			o.setLawful(o.getLawful() - 2000);
		}
		// 전투 관련 변수 초기화.
		synchronized (attackList) {
			attackList.clear();
		}
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_CORPSE);
	}

	@Override
	protected void toAiCorpse(long time) {
		super.toAiCorpse(time);

		if (ai_time_temp_1 == 0)
			ai_time_temp_1 = time;

		// 시체 유지
		if (ai_time_temp_1 + Lineage.ai_corpse_time > time)
			return;

		ai_time_temp_1 = 0;
		// 버프제거
		toReset(true);
		// 시체 제거
		clearList(true);
		World.remove(this);
		// 상태 변환.
		setAiStatus(Lineage.AI_STATUS_SPAWN);
	}

	@Override
	protected void toAiSpawn(long time) {
		super.toAiSpawn(time);

		// 리스폰값이 정의되어 있지않다면 재스폰 할 필요 없음.
		if (reSpawnTime == 0) {
			toAiThreadDelete();
			return;
		}

		if (ai_time_temp_1 == 0)
			ai_time_temp_1 = time;
		// 스폰 대기.
		if (ai_time_temp_1 + reSpawnTime > time) {

		} else {
			ai_time_temp_1 = 0;
			// 상태 변환
			setDead(false);
			setNowHp(getMaxHp());
			setNowMp(getMaxMp());
			// 스폰
			toTeleport(homeX, homeY, homeMap, false);
			// 상태 변환.
			setAiStatus(Lineage.AI_STATUS_WALK);
		}
	}

}
