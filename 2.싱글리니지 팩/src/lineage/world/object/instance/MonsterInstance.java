package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import all_night.Lineage_Balance;
import all_night.Npc_promotion;
import lineage.bean.database.Drop;
import lineage.bean.database.EnchantLostItem;
import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.bean.database.Item_add_log;
import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSkill;
import lineage.bean.database.MonsterSpawnlist;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Party;
import lineage.bean.lineage.Summon;
import lineage.database.CharactersDatabase;
import lineage.database.ExpDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemDropMessageDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.AutoHuntCheckController;
import lineage.world.controller.BossController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CommandController;
import lineage.world.controller.DamageController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.PartyController;
import lineage.world.controller.SummonController;
import lineage.world.controller.WorldBossController;
import lineage.world.controller.TimeEventController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.SummonInstance.SUMMON_MODE;
import lineage.world.object.item.potion.CurePoisonPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.magic.EarthBind;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.Slow;
import lineage.world.object.magic.monster.ShareMagic;
import lineage.world.object.monster.Faust_Ghost;

public class MonsterInstance extends Character {

	protected Monster mon; //
	protected Monster monOld; //
	private MonsterSpawnlist monSpawn; //
	private AStar aStar; // 길찾기 변수
	private Node tail; // 길찾기 변수
	private int[] iPath; // 길찾기 변수
	private double add_exp; // hp1당 지급될 경험치 값.
	private double dangerous_exp; // 공격자 우선처리를위한 변수로 경험치의 70% 값을 저장.
	private List<object> attackList; // 전투 목록
	private List<object> astarList; // astar 무시할 객체 목록.
	private List<Exp> expList; // 경험치 지급해야할 목록
	private int ai_walk_stay_count; // 랜덤워킹 중 잠시 휴식을 취하기위한 카운팅 값
	private int reSpawnTime; // 재스폰 하기위한 대기 시간값.
	protected boolean boss; // 보스 몬스터인지 여부. monster_spawnlist_boss 를 거쳐서 스폰도니것만
							// true가 됨.
	// 시체유지(toAiCorpse) 구간에서 사용중.
	// 재스폰대기(toAiSpawn) 구간에서 사용중.
	private long ai_time_temp_1;
	// 인벤토리
	protected Inventory inv;
	// 그룹몬스터에 사용되는 변수.
	private List<MonsterInstance> group_list; // 현재 관리되고있는 몬스터 목록.
	private MonsterInstance group_master; // 나를 관리하고있는 마스터객체 임시 저장용.
	// 동적 변환 값
	protected int dynamic_attack_area; // 공격 범위 값.
	private boolean pineWand; // 소막 및 gm명령어 소환으로 처리된 몬스터인지 여부.
	// 스킬 딜레이를 주기위한 변수
	public long lastSkillTime;
	// 보스 생존 시간.
	public int bossLiveTime;
	// 보스 텔레포트 시간
	public long lastTeleportTime;

	public String petweapon;
	public String petarmor;
	// 스팟 몬스터
	private boolean isSpotMonster;

	static synchronized public MonsterInstance clone(MonsterInstance mi, Monster m) {
		if (mi == null)
			mi = new MonsterInstance();
		mi.setMonster(m);
		mi.setAddExp((double) m.getExp() / (double) m.getHp());
		mi.setDangerousExp(m.getExp() * 0.7);
		return mi;
	}

	public MonsterInstance() {
		aStar = new AStar();
		astarList = new ArrayList<object>();
		attackList = new ArrayList<object>();
		expList = new ArrayList<Exp>();
		group_list = new ArrayList<MonsterInstance>();
		iPath = new int[2];
		// 인벤토리 활성화를 위해.
		InventoryController.toWorldJoin(this);
	}

	@Override
	public void close() {
		super.close();
		mon = monOld = null;
		classType = Lineage.LINEAGE_CLASS_MONSTER;
		ai_time_temp_1 = reSpawnTime = dynamic_attack_area = 0;
		lastSkillTime = 0L;
		lastTeleportTime = 0L;
		bossLiveTime = 0;
		pineWand = false;
		boss = false;
		monSpawn = null;
		petweapon = null;
		petarmor = null;
		group_master = null;
		if (attackList != null)
			clearAttackList();
		if (astarList != null)
			clearAstarList();
		if (expList != null) {
			synchronized (expList) {
				for (Exp e : expList)
					ExpDatabase.setPool(e);
				expList.clear();
			}
		}
		if (inv != null) {
			for (ItemInstance ii : inv.getList()) {
				ItemDatabase.setPool(ii);
			}
			inv.clearList();
		}
		if (group_list != null)
			group_list.clear();

		isSpotMonster = false;
		//
		CharacterController.toWorldOut(this);
	}

	public boolean isSpotMonster() {
		return isSpotMonster;
	}

	public void setSpotMonster(boolean isSpotMonster) {
		this.isSpotMonster = isSpotMonster;
	}

	public void setMonsterSpawnlist(MonsterSpawnlist monSpawn) {
		this.monSpawn = monSpawn;
	}

	public MonsterSpawnlist getMonsterSpawnlist() {
		return monSpawn;
	}

	public void setAiTimeTemp1(long ai_time_temp_1) {
		this.ai_time_temp_1 = ai_time_temp_1;
	}

	public long getAiTimeTemp1() {
		return ai_time_temp_1;
	}

	public MonsterInstance getGroupMaster() {
		return group_master;
	}

	public void setGroupMaster(MonsterInstance group_master) {
		this.group_master = group_master;
	}

	/**
	 * 그룹에 속한 몬스터중 마스터를 찾아 리턴한다. : 실제 마스터가 죽엇을경우 그룹에 속한 목록에서 살아있는 놈을 마스터로 잡는다. :
	 * 모두 죽거나 하면 null을 리턴.
	 * 
	 * @return
	 */
	public MonsterInstance getGroupMasterDynamic() {
		if (group_master != null) {
			if (group_master.isDead()) {
				for (MonsterInstance mi : group_master.getGroupList()) {
					if (!mi.isDead())
						return mi;
				}
			} else {
				return group_master;
			}
		}
		return null;
	}

	public List<MonsterInstance> getGroupList() {
		return group_list;
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

	@Override
	public void setInventory(Inventory inv) {
		this.inv = inv;
	}

	public int getReSpawnTime() {
		return reSpawnTime;
	}

	@Override
	public void setReSpawnTime(int reSpawnTime) {
		this.reSpawnTime = reSpawnTime;
	}

	@Override
	public int getHpTime() {
		return Lineage.ai_monster_tic_time;
	}

	@Override
	public int hpTic() {
		return mon.getTicHp();
	}

	@Override
	public int mpTic() {
		return mon.getTicMp();
	}

	public void setAddExp(double add_exp) {
		this.add_exp = add_exp;
	}

	public void setDangerousExp(double dangerous_exp) {
		this.dangerous_exp = dangerous_exp;
	}

	public void setMonster(Monster m) {
		if (m == null)
			return;
		this.mon = m;
		if (monOld == null)
			monOld = m;
		// 시전되는 스킬목록에서 거리범위가 세팅된게 잇을경우 그것으로 거리변경함.
		// 이렇게 해야 몬스터 인공지능 발동시 공격거리가 최상위로 잡혀서 리얼해짐.
		for (MonsterSkill ms : m.getSkillList()) {
			if (ms.getDistance() > 0 && m.getAtkRange() < ms.getDistance() && dynamic_attack_area < ms.getDistance())
				dynamic_attack_area = ms.getDistance();
		}
		// 틱값이 존재한다면 관리를위해 등록.
		if (m.getTicHp() > 0 || m.getTicMp() > 0)
			CharacterController.toWorldJoin(this);
	}

	public Monster getMonster() {
		return mon;
	}

	protected int getAtkRange() {
		return dynamic_attack_area > 0 ? dynamic_attack_area : mon.getAtkRange();
	}

	public boolean isBoss() {
		return boss;
	}

	public void setBoss(boolean boss) {
		this.boss = boss;
	}

	private void appendAttackList(object o) {
		synchronized (attackList) {
			if (!attackList.contains(o))
				attackList.add(o);
		}
	}

	private void removeAttackList(object o) {
		synchronized (attackList) {
			attackList.remove(o);
		}
	}

	public List<object> getAttackList() {
		synchronized (attackList) {
			return new ArrayList<object>(attackList);
		}
	}

	private boolean containsAttackList(object o) {
		synchronized (attackList) {
			return attackList.contains(o);
		}
	}

	protected void clearAttackList() {
		synchronized (attackList) {
			attackList.clear();
		}
	}

	public boolean containsAstarList(object o) {
		synchronized (astarList) {
			return astarList.contains(o);
		}
	}

	private void appendAstarList(object o) {
		synchronized (astarList) {
			if (!astarList.contains(o))
				astarList.add(o);
		}
	}

	private void removeAstarList(object o) {
		synchronized (astarList) {
			astarList.remove(o);
		}
	}

	protected void clearAstarList() {
		synchronized (astarList) {
			astarList.clear();
		}
	}

	protected List<Exp> getExpList() {
		synchronized (expList) {
			return new ArrayList<Exp>(expList);
		}
	}

	private void appendExpList(Exp e) {
		synchronized (expList) {
			if (!expList.contains(e))
				expList.add(e);
		}
	}

	private void removeExpList(Exp e) {
		synchronized (expList) {
			expList.remove(e);
		}
	}

	private Exp getExpList(int index) {
		if (expList.size() > index)
			synchronized (expList) {
				return expList.get(index);
			}
		else
			return null;
	}

	public void removeExpList(object o) {
		for (Exp e : getExpList()) {
			if (e.getObject() == null || o.getObjectId() == e.getObject().getObjectId() || o.getObjectId() == e.getObject().getOwnObjectId())
				removeExpList(e);
		}
	}

	public void clearExpList() {
		synchronized (expList) {
			for (Exp e : expList)
				ExpDatabase.setPool(e);
			expList.clear();
		}
	}

	public String getPetWeapon() {
		return petweapon;
	}

	public void setPetWeapon(String petweapon) {
		this.petweapon = petweapon;
	}

	public String getPetArmor() {
		return petarmor;
	}

	public void setPetArmor(String petarmor) {
		this.petarmor = petarmor;
	}

	/**
	 * 전투목록에서 원하는 위치에있는 객체 찾아서 리턴.
	 * 
	 * @param index
	 * @return
	 */
	protected object getAttackList(int index) {
		if (getAttackListSize() > index) {
			synchronized (attackList) {
				return attackList.get(index);
			}
		} else {
			return null;
		}
	}

	protected int getAttackListSize() {
		return attackList.size();
	}

	public boolean isPineWand() {
		return pineWand;
	}

	public void setPineWand(boolean pineWand) {
		this.pineWand = pineWand;
	}

	/**
	 * 해당 객체와 연결된 경험치 객체 찾기.
	 * 
	 * @param o
	 * @return
	 */
	private Exp findExp(object o) {
		for (Exp e : getExpList()) {
			if (e.getObject() != null && e.getObject().getObjectId() == o.getObjectId())
				return e;
		}
		return null;
	}

	/**
	 * 몬스터와 전투중인 객체들중 가장 위험수위에 있는 객체를 찾아서 리턴. 몬스터를 공격한 객체목록(경험치지급목록) 에서 지급된 경험치의
	 * 70% 이상값을 가진 사용자가 최우선.
	 * 
	 * @return
	 */
	protected object findDangerousObject() {
		object o = null;

		// 경험치의 70% 이상값을 가진 사용자 찾기.
		for (Exp e : getExpList()) {
			if (e.getObject() != null && !containsAstarList(e.getObject())) {

				// 70% 달하는 경험치 받아가는 놈 발견하면 무조건 공격하기.
				if (e.getExp() >= dangerous_exp)
					return e.getObject();
				// 가장근접한 객체 찾기.
				else if (o == null)
					o = e.getObject();
				else if (Util.getDistance(this, e.getObject()) < Util.getDistance(this, o))
					o = e.getObject();
			}
		}
		if (o != null)
			return o;

		// 찾지 못했다면 공격자목록에 등록된 사용자에서 찾기.
		for (object oo : getAttackList()) {
			if (!containsAstarList(oo)) {
				if (o == null)
					o = oo;
				else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
					o = oo;
			}
		}
		return o;
	}

	@Override
	public void toRevival(object o) {
		// 사용자가 부활을 시키는거라면 디비상에 부활가능 할때만 처리.
		if (o instanceof PcInstance && !mon.isRevival() && !(this instanceof SummonInstance) && !(this instanceof PetInstance))
			return;

		if (isDead()) {
			super.toReset(false);
			// 다이상태 풀기.
			setDead(false);
			// 체력 채우기.
			setNowHp(level);
			// 패킷 처리.
			toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), o, this), false);
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 230), true);
			// 상태 변경.
			ai_time_temp_1 = 0;
			setAiStatus(Lineage.AI_STATUS_WALK);
		}
	}

	@Override
	public void setNowHp(int nowHp) {

		if (nowHp <= 0 && getMonster().getFaust() != null && getMonster().getFaust().length() > 0) {
			Monster m = MonsterDatabase.find(getMonster().getFaust());
			if (m != null) {
				MonsterSpawnlistDatabase.changeMonster(this, m);
				return;
			}
		}

		if (nowHp <= 0) {
			switch (getMonster().getNameIdNumber()) {
			case 22403: // 몽둥이
				Monster m = MonsterDatabase.find("파우스트의 악령(악랄)");
				if (m != null && getMap() >= 653 && getMap() <= 654 && Util.random(0, 99) <= Lineage.giran_dungeon) {
					toSender(new S_ObjectEffect(this, 5483), false);
					MonsterSpawnlistDatabase.changeMonster(this, m);
					return;
				}
				break;
			}
		}
		if (nowHp <= 0) {
			switch (getMonster().getNameIdNumber()) {
			case 22404: // 검
				Monster m = MonsterDatabase.find("파우스트의 악령(잔인)");
				if (m != null && getMap() >= 653 && getMap() <= 654 && Util.random(0, 99) <= Lineage.giran_dungeon) {
					toSender(new S_ObjectEffect(this, 5483), false);
					MonsterSpawnlistDatabase.changeMonster(this, m);
					return;
				}
				break;
			}
		}
		if (nowHp <= 0) {
			switch (getMonster().getNameIdNumber()) {
			case 22405: // 도끼
				Monster m = MonsterDatabase.find("파우스트의 악령(포악)");
				if (m != null && getMap() >= 653 && getMap() <= 654 && Util.random(0, 99) <= Lineage.giran_dungeon) {
					toSender(new S_ObjectEffect(this, 5483), false);
					MonsterSpawnlistDatabase.changeMonster(this, m);
					return;
				}
				break;
			}
		}
		if (nowHp <= 0) {
			switch (getMonster().getNameIdNumber()) {
			case 22338:
				Monster m = MonsterDatabase.find("파우스트의 악령(악독)");
				if (m != null && getMap() >= 653 && getMap() <= 654 && Util.random(0, 99) <= Lineage.giran_dungeon) {
					toSender(new S_ObjectEffect(this, 5483), false);
					MonsterSpawnlistDatabase.changeMonster(this, m);
					return;
				}
				break;
			}
		}
		if (nowHp <= 0) {
			switch (getMonster().getNameIdNumber()) {
			case 22339:
				Monster m = MonsterDatabase.find("파우스트");
				if (m != null && getMap() >= 653 && getMap() <= 654 && Util.random(0, 99) < Lineage_Balance.faust_spawn_probability) {
					toSender(new S_ObjectEffect(this, 5483), false);
					MonsterSpawnlistDatabase.changeMonsterBoss(this, m);
					return;
				}
				break;
			}
		}
		super.setNowHp(nowHp);
		// 요청사항: 몬스터 머리 위 HP바 전역 비표시.
	}

	/**
	 * 인공지능에 사용된 모든 변수 초기화. 객체를 스폰되기전 상태로 전환하는 함수. : 펫을 길들인후 뒷처리 함수로 사용함. : 펫
	 * 맡길때 제거용으로도 사용함. : 테이밍몬스터후 뒷처리함수로 사용함.
	 * 
	 * @param packet
	 *            : 패킷 처리 할지 여부.
	 */
	public void toAiClean(boolean packet) {
		// 인벤토리 제거.
		for (ItemInstance ii : inv.getList())
			ItemDatabase.setPool(ii);
		inv.clearList();
		// 경험치 지급 제거.
		synchronized (expList) {
			for (Exp e : expList)
				ExpDatabase.setPool(e);
			expList.clear();
		}
		// 전투 관련 변수 초기화.
		clearAttackList();
		// 버프제거
		toReset(true);
		// 객체관리목록 제거.
		World.remove(this);
		clearList(packet);

		setAiStatus(Lineage.AI_STATUS_SPAWN);
		ai_time_temp_1 = 0;
	}

	@Override
	public void setDead(boolean dead) {
		super.setDead(dead);
		if (dead) {
			ai_time = 100;
			setAiStatus(Lineage.AI_STATUS_DEAD);
		}
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		if (cha == null)
			return;

		if (Lineage.is_DmgViewer) {
			DmgViewer(cha, this, (int) dmg);
		}

		// 서먼몬스터가 아닐때만.
		if (ai_status != Lineage.AI_STATUS_ATTACK && summon == null) {
			// 전투상태가 아니엿을때 공격받으면 처리하기. 첫전투전환 시점으로 생각하면됨.
			// 일정 확률로 인벤에서 초록물약 찾아서 복용하기.
			// 아직 복용상태가 아닐때만
			if (getSpeed() == 0 && Util.random(0, 10) == 0) {
				ItemInstance ii = getInventory().findDbNameId(234);
				if (ii != null)
					ii.toClick(this, null);
			}
		}
		// 경험치 지급될 목록에 추가.
		if (dmg > 0)
			appendExp(cha, dmg);
		// 공격목록에 추가.
		addAttackList(cha);
		// 동족인식.
		if (mon.getFamily().length() > 0 && group_master == null) {
			for (object inside_o : getInsideList()) {
				if (inside_o instanceof MonsterInstance && !(inside_o instanceof SummonInstance)) {
					MonsterInstance inside_mon = (MonsterInstance) inside_o;
					if (isFamily(inside_mon, mon.getFamily()) && inside_mon.getGroupMaster() == null)
						inside_mon.addAttackList(cha);
				}
			}
		}
		// 그룹 알림.
		for (MonsterInstance mi : group_list)
			mi.addAttackList(cha);
		if (group_master != null && group_master.getObjectId() != getObjectId())
			group_master.toDamage(cha, 0, type);

		// 손상 처리.
		if (mon.isToughskin() && type == Lineage.ATTACK_TYPE_WEAPON && cha.isBuffSoulOfFlame() == false) {
			if (PluginController.init(MonsterInstance.class, "toDamage.손상처리", cha) == null) {
				ItemInstance weapon = cha.getInventory().getSlot(Lineage.SLOT_WEAPON);
				if (weapon != null && weapon.getItem().isCanbedmg() && Util.random(0, 100) < 10) {
					weapon.setDurability(weapon.getDurability() + 1);
					if (Lineage.server_version >= 160)
						cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), weapon));
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 268, weapon.toString()));
				}
			}
		}

		// 길찾기 무시할 목록에서 제거.
		removeAstarList(cha);
	}

	/**
	 * 경험치 지급목록 처리 함수.
	 * 
	 * @param cha
	 * @param dmg
	 */
	protected void appendExp(Character cha, int dmg) {
		if (dmg == 0)
			return;
		Exp e = findExp(cha);
		if (e == null) {
			e = ExpDatabase.getPool();
			e.setObject(cha);
			appendExpList(e);
		}

		// 연산
		double exp = add_exp;
		if (getNowHp() < dmg)
			exp *= getNowHp();
		else
			exp *= dmg;
		// 축적.
		e.setExp(e.getExp() + exp);
		e.setDmg(e.getDmg() + dmg);
	}

	protected void DmgViewer(Character cha, object target, int dmg) {
		if (dmg == 0)
			return;
		PcInstance.DmgViewer(target, (int) dmg);
	}

	@Override
	public void toGiveItem(object o, ItemInstance item, long count) {
		// 죽은상태라면 무시.
		if (isDead())
			return;
		long time = System.currentTimeMillis();

		if (o.getGm() == 0) {
			if (!getInventory().isAppendItem(item, count)) {
				ChattingController.toChatting(o, "아이템을 더 이상 줄 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			if (item != null && item.getItem() != null && item.getItem().getName().equalsIgnoreCase("아데나")) {
				ChattingController.toChatting(o, "아데나는 줄 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}

			if (item != null && item.getItem() != null && !(item instanceof HastePotion || item instanceof HealingPotion || item instanceof CurePoisonPotion)) {
				ChattingController.toChatting(o, "해당 몬스터에게 아이템을 줄 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
		}
		int bress = item.getBless();

		String name = item.getItem().getName();

		// 펫 로그 기록
		if (o instanceof PetInstance) {

			String msg = String.format("[펫아이템 넣기] %s -> %s (아이템: %s 갯수: %d)", summon.getMaster().getName(), getName(), item.getItem().getName(), count);
			String timeString = Util.getLocaleString(time, true);
			String log = String.format("[%s]\t %s", timeString, msg);

			GuiMain.display.asyncExec(new Runnable() {
				public void run() {
					GuiMain.getViewComposite().getpetComposite().toLog(log);
				}
			});
		}
		super.toGiveItem(o, item, count);

		// 넘겨받은 아이템 복용 처리. 1개일경우 바로 처리.
		if (count == 1) {
			ItemInstance temp = getInventory().find(name, bress, item.getItem().isPiles());

			if (temp instanceof HastePotion || temp instanceof HealingPotion || temp instanceof CurePoisonPotion)
				temp.toClick(this, null);

		}
	}

	/**
	 * 공격자 목록에 등록처리 함수.
	 * 
	 * @param o
	 */
	public void addAttackList(object o) {
		if (!isDead() && !o.isDead() && o.getObjectId() != getObjectId() && !group_list.contains(o)) {
			// 공격목록에 추가.
			appendAttackList(o);
		}
	}

	/**
	 * 몬스터별 drop_list 정보를 참고해서 확률 연산후 인벤토리에 갱신.
	 */
	public void readDrop(int spawnMap) {
		if (inv == null)
			return;

		// 인벤토리에 드랍아이템 등록.
		for (Drop d : mon.getDropList()) {
			Item item = ItemDatabase.find(d.getItemName());

			if (item != null && d.getChance() > 0) {
				// 기본 찬스
				double chance = d.getChance();

				// 배율 적용.
				if (d.getChance() < 1)
					chance *= Lineage.rate_drop;
				// 체크.
				if (Math.random() < chance) {
					ItemInstance ii = ItemDatabase.newInstance(item);
					if (ii != null) {
						ii.setBless(d.getItemBress());
						ii.setEnLevel(d.getItemEn());
						ii.setCount(Util.random(d.getCountMin() == 0 ? 1 : d.getCountMin(), d.getCountMax() == 0 ? 1 : d.getCountMax()));
						inv.append(ii, true);
					}
				}
			}
		}
		// 아덴 등록.
		if (mon.isAdenDrop()) {
			for (ItemInstance i : getInventory().getList()) {
				if (i.getItem().getName().equalsIgnoreCase("아데나"))
					return;
			}

			long count = 0;

			count = (int) Math.round(Util.random(level * 1, level * 2) * Lineage.rate_aden);

			if (mon.isBoss()) {
				if (getMonster().getBossClass().equalsIgnoreCase("하급 보스"))
					count = Util.random(Lineage.class_1_boss_aden_min, Lineage.class_1_boss_aden_max);
				else if (getMonster().getBossClass().equalsIgnoreCase("중급 보스"))
					count = Util.random(Lineage.class_2_boss_aden_min, Lineage.class_2_boss_aden_max);
				else if (getMonster().getBossClass().equalsIgnoreCase("상급 보스"))
					count = Util.random(Lineage.class_3_boss_aden_min, Lineage.class_3_boss_aden_max);
				else if (getMonster().getBossClass().equalsIgnoreCase("최상급 보스"))
					count = Util.random(Lineage.class_4_boss_aden_min, Lineage.class_4_boss_aden_max);
			}

			if (count > 0) {
				ItemInstance aden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
				if (aden != null) {
					aden.setObjectId(ServerDatabase.nextItemObjId());
					aden.setCount(count);
					inv.append(aden, true);
				}
			}
		}
	}

	/**
	 * 몬스터별 drop_list 정보를 참고해서 확률 연산후 인벤토리에 갱신.
	 */
	public void readDrop(int spawnMap, PcInstance pc) {
		if (inv == null)
			return;

		// 불멸의 가호
		boolean 불멸의가호 = false;
		if (pc != null && pc.getInventory() != null)
			불멸의가호 = pc.getInventory().find(Lineage.immortality_item_name) == null ? false : true;

		// 인벤토리에 드랍아이템 등록.
		for (Drop d : mon.getDropList()) {
			Item item = ItemDatabase.find(d.getItemName());

			if (item != null && d.getChance() > 0) {
				// 기본 찬스
				double chance = d.getChance();

				// 드랍 추가 확률
				if (pc.getAddDropItemRate() > 0)
					chance = chance + (chance * pc.getAddDropItemRate());

				// 불멸의 가호 추가 드랍 확률
				if (Lineage.is_immortality && Lineage.immortality_item_percent > 0 && 불멸의가호)
					chance = chance + (chance * Lineage.immortality_item_percent);

				if (TimeEventController.isOpen && TimeEventController.num == 2) {
					chance = chance + (chance * 0.2);
				}
				if (TimeEventController.isOpen && TimeEventController.num == 5) {
					chance = chance + (chance * 0.4);
				}

				if (d.getChance() < 1)
					// 배율 적용.
					chance *= Lineage.rate_drop;

				if (pc.isAutoHunt) {
					chance = chance * Lineage.is_auto_hunt_item_drop_percent;
				}

				// 체크.
				if (Math.random() < chance) {
					ItemInstance ii = ItemDatabase.newInstance(item);
					if (ii != null) {
						ii.setBless(d.getItemBress());
						ii.setEnLevel(d.getItemEn());
						ii.setCount(Util.random(d.getCountMin() == 0 ? 1 : d.getCountMin(), d.getCountMax() == 0 ? 1 : d.getCountMax()));
						inv.append(ii, true);
					}
				}
			}
		}
		// 아덴 등록.
		if (mon.isAdenDrop()) {
			for (ItemInstance i : getInventory().getList()) {
				if (i.getItem().getName().equalsIgnoreCase("아데나"))
					return;
			}

			long count = (int) Math.round(Util.random(level * 1, level * 2) * Lineage.rate_aden);

			// 아덴 추가 획득
			if (pc.getAddDropAdenRate() > 0)
				count = count + (long) (count * pc.getAddDropAdenRate());

			// 불멸의 가호 추가 아데나
			if (Lineage.is_immortality && Lineage.immortality_aden_percent > 0 && 불멸의가호)
				count = (long) (count + (count * Lineage.immortality_aden_percent));

			if (mon.isBoss()) {
				if (getMonster().getBossClass().equalsIgnoreCase("하급 보스"))
					count = Util.random(Lineage.class_1_boss_aden_min, Lineage.class_1_boss_aden_max);
				else if (getMonster().getBossClass().equalsIgnoreCase("중급 보스"))
					count = Util.random(Lineage.class_2_boss_aden_min, Lineage.class_2_boss_aden_max);
				else if (getMonster().getBossClass().equalsIgnoreCase("상급 보스"))
					count = Util.random(Lineage.class_3_boss_aden_min, Lineage.class_3_boss_aden_max);
				else if (getMonster().getBossClass().equalsIgnoreCase("최상급 보스"))
					count = Util.random(Lineage.class_4_boss_aden_min, Lineage.class_4_boss_aden_max);
			}
			if (TimeEventController.isOpen && TimeEventController.num == 3) {
				count = (long) (count + (count * 0.3));
			}

			if (pc.isAutoHunt) {
				count = (long) (count * Lineage.is_auto_hunt_aden_drop_percent);
			}

			if (count > 0) {
				ItemInstance aden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
				if (aden != null) {
					aden.setObjectId(ServerDatabase.nextItemObjId());
					aden.setCount(count);
					inv.append(aden, true);
				}
			}
		}
	}

	/**
	 * 해당객체를 공격해도 되는지 분석하는 함수.
	 * 
	 * @param o
	 * @param walk
	 *            : 랜덤워킹 상태 체크인지 구분용
	 * @return
	 */
	public boolean isAttack(object o, boolean walk) {
		if (o == null)
			return false;
		if (o.isDead())
			return false;
		if (o.isWorldDelete())
			return false;
		// if(o.getGm()>0)
		// return false;
		if (o instanceof PcInstance) {
			if (((PcInstance) o).isFishing() || ((PcInstance) o).isFishingZone())
				return false;
		}
		if (o.isTransparent())
			return false;
		if (!Util.isDistance(this, o, Lineage.SEARCH_MONSTER_TARGET_LOCATION))
			return false;
		if (walk) {
			if (o.getGfx() != o.getClassGfx() && !mon.isAtkPoly())
				return false;
		}
		// 특정 몬스터들은 굳은상태를 확인해서 무시하기.
		switch (mon.getNameIdNumber()) {
		case 962: // 바실리스크
		case 969: // 코카트리스
			if (o.isLockHigh())
				return false;
		}
		if (mon.getName().equalsIgnoreCase("오크 창고 근무자a")) {
			if (o.isLockHigh())
				return false;
		}
		// -- 몬스터 인공지능 설정에서 변경하도록 하는게 좋을듯. 지금은 투망일때 무조건 무시하도록 설정.
		if (!mon.isAtkInvis() && o.isInvis())
			return false;
		// // 투망상태일경우 공격목록에 없으면 무시.
		// if(!mon.isAtkInvis() && o.isInvis())
		// // 동기화 할필요 없음. 동기화 한 상태로 들어옴.
		// return containsAttackList(o);

		if (this instanceof SummonInstance) {
			// 소환및 펫은 거의다..
			return true;
		} else {
			// 몬스터는 pc, sum, pet
			// 랜덤워킹중에 선인식 체크함. 랜덤워킹이라면 서먼몬스터는 제외.
			if (walk)
				return o instanceof PcInstance;
			else
				return o instanceof PcInstance || o instanceof SummonInstance;
		}
	}

	/**
	 * 픽업아이템인지 여부를 리턴.
	 * 
	 * @param o
	 * @return
	 */
	protected boolean isPickupItem(object o) {
		return o instanceof ItemInstance && !containsAstarList(o);
	}

	/**
	 * 매개변수 좌표로 A스타를 발동시켜 이동시키기. 객체가 존재하는 지역은 패스하도록 함. 이동할때마다 aStar가 새로 그려지기때문에
	 * 과부하가 심함.
	 */
	public void toMoving(object o, final int x, final int y, final int h, final boolean astar) {
		if (astar) {
			aStar.cleanTail();
			tail = aStar.searchTail(this, x, y, true);
			if (tail != null) {
				while (tail != null) {
					// 현재위치 라면 종료
					if (tail.x == getX() && tail.y == getY())
						break;
					//
					iPath[0] = tail.x;
					iPath[1] = tail.y;
					tail = tail.prev;
				}
				toMoving(iPath[0], iPath[1], Util.calcheading(this.x, this.y, iPath[0], iPath[1]));
			} else {
				if (o != null) {
					// o 객체가 마스터일경우 휴식모드로 전환.
					if (this instanceof SummonInstance) {
						SummonInstance si = (SummonInstance) this;
						if (si.getSummon() != null && si.getSummon().getMasterObjectId() == o.getObjectId()) {
							si.setSummonMode(SUMMON_MODE.Rest);
							return;
						}
					}
					// 그외엔 에이스타 무시목록에 등록.
					appendAstarList(o);
				}
			}
		} else {
			toMoving(x, y, h);
		}
		// 잊섬 몬스터 세이프티존 패치
		if (World.isMonsterTeleport(getX(), getY(), getMap()) && !(this instanceof PetInstance || this instanceof SummonInstance)) {
			toTeleport(getHomeX(), getHomeY(), getHomeMap(), true);
		}
	}

	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple) {
		// 일반적인 공격mode와 다를경우 프레임값 재 설정.
		if (this.gfxMode + Lineage.GFX_MODE_ATTACK != gfxMode)
			ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode);

		int effect = bow ? mon.getArrowGfx() != 0 ? mon.getArrowGfx() : 66 : 0;
		int dmg = DamageController.getDamage(this, o, bow, null, null, alpha_dmg);
		DamageController.toDamage(this, o, dmg, Lineage.ATTACK_TYPE_WEAPON);

		// 칼렉
		if (!Lineage.is_sword_lack_check || (Lineage.is_sword_lack_check && lastDamageActionTime < System.currentTimeMillis()))
			toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, gfxMode, dmg, effect, bow, true, 0, 0), false);
	}

	@Override
	public void toAi(long time) {
		switch (ai_status) {
		// 공격목록이 발생하면 공격모드로 변경
		case Lineage.AI_STATUS_WALK:
		case Lineage.AI_STATUS_PICKUP:
			if (getAttackListSize() > 0)
				setAiStatus(Lineage.AI_STATUS_ATTACK);
			break;

		// 전투 처리부분은 항상 타켓들이 공격가능한지 확인할 필요가 있음.
		case Lineage.AI_STATUS_ATTACK:
		case Lineage.AI_STATUS_ESCAPE:
			// 보스일경우 스폰위치에서 너무 벗어나면 스폰위치로 강제 텔레포트.
			if (isBoss() && !Util.isDistance(x, y, map, homeX, homeY, homeMap, Lineage.SEARCH_MONSTER_TARGET_LOCATION)) {
				// setNowHp(getTotalHp());
				// setNowMp(getTotalMp());
				toTeleport(homeX, homeY, homeMap, true);
			}
			if (map == 807 && !Util.isDistance(x, y, map, homeX, homeY, homeMap, 10)) {
				toTeleport(homeX, homeY, homeMap, true);
			}
			//
			for (object o : getAttackList()) {
				if (!isAttack(o, false) || containsAstarList(o)) {
					removeAttackList(o);
					Exp e = findExp(o);
					if (e != null) {
						removeExpList(e);
						ExpDatabase.setPool(e);
					}
				}
			}

			// 전투목록이 없을경우 랜덤워킹으로 변경.
			if (getAttackListSize() < 1) {
				setAiStatus(Lineage.AI_STATUS_WALK);
			}
			break;
		}

		super.toAi(time);
	}

	@Override
	protected void toAiWalk(long time) {
		if (!SpriteFrameDatabase.findGfxMode(getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK))
			return;

		super.toAiWalk(time);

		List<object> insideList = getInsideList();

		if (getMonster() != null && !getMonster().getBossClass().contains("보스")) {
			boolean isWalk = false;
			// 주변에 유저가 있는지 검색.
			for (object o : insideList) {
				if (o instanceof PcInstance) {
					isWalk = true;
					break;
				}
			}
			// 주변에 유저가 없을경우 움직이지 않음.
			if (!isWalk)
				return;
		}

		// 선인식 체크
		if (mon.getAtkType() == 1) {
			for (object o : insideList) {
				if (isAttack(o, true)) {
					// 공격목록에 등록.
					if (!containsAstarList(o)) {
						addAttackList(o);
						clearAstarList();
						setAiStatus(Lineage.AI_STATUS_ATTACK);
						toAiAttack(time);
						return;
					}
					// 그룹 알림.
					if (group_list != null) {
						for (MonsterInstance mi : group_list) {
							if (!mi.containsAstarList(o))
								mi.addAttackList(o);
						}
						if (group_master != null && group_master.getObjectId() != getObjectId() && o instanceof Character)
							group_master.toDamage((Character) o, 0, Lineage.ATTACK_TYPE_DIRECT);
					}
				}
			}
		}

		// 아이템 줍기 체크
		if (getAttackListSize() == 0 && mon.isPickup() && group_master == null) {
			for (object o : getInsideList(true)) {
				if (isPickupItem(o)) {
					// 아이템이 발견되면 아이템줍기 모드로 전환.
					setAiStatus(Lineage.AI_STATUS_PICKUP);
					return;
				}
			}
		}
		// 멘트
		toMent(time);

		// 아직 휴식카운팅값이 남앗을경우 리턴.
		if (ai_walk_stay_count-- > 0)
			return;

		// Astar 발동처리하다가 길이막혀서 이동못하던 객체를 모아놓은 변수를 일정주기마다 클린하기.
		if (Util.random(0, 4) == 0)
			clearAstarList();

		do {
			switch (Util.random(0, 10)) {
			case 0:
			case 1:
				ai_walk_stay_count = Util.random(1, 4);
				break;
			case 2:
			case 3:
				setHeading(getHeading() + 1);
				break;
			case 4:
			case 5:
				setHeading(getHeading() - 1);
				break;
			case 6:
			case 7:
				setHeading(Util.random(0, 7));
				break;
			}
			// 이동 좌표 추출.
			int x = Util.getXY(heading, true) + this.x;
			int y = Util.getXY(heading, false) + this.y;
			// 그룹 마스터로 지정된놈 추출.
			MonsterInstance master = getGroupMasterDynamic();
			// 스폰된 위치에서 너무 벗어낫을경우 스폰쪽으로 유도하기.
			if (master == null || master.getObjectId() == getObjectId() || group_master == null) {
				if (!Util.isDistance(x, y, map, homeX, homeY, homeMap, Lineage.SEARCH_LOCATIONRANGE)) {
					heading = Util.calcheading(this, homeX, homeY);
					x = Util.getXY(heading, true) + this.x;
					y = Util.getXY(heading, false) + this.y;
				}
				// 마스터가 존재하는 그룹몬스터일경우 마스터가 있는 좌표 내에서 왓다갓다 하기.
			} else {
				// 이동 범위는 마스터 목록에 등록된 순번+3으로 범위지정. 등록순번이 뒤에잇을수록 이동반경은 넓어짐.
				if (!Util.isDistance(x, y, map, master.getX(), master.getY(), master.getMap(), group_master.getGroupList().indexOf(this) + 3)) {
					heading = Util.calcheading(this, master.getX(), master.getY());
					x = Util.getXY(heading, true) + this.x;
					y = Util.getXY(heading, false) + this.y;
				}
			}
			// 해당 좌표 이동가능한지 체크.
			boolean tail = World.isThroughObject(this.x, this.y, this.map, heading) && (World.isMapdynamic(x, y, map) == false) && !World.isNotAttackTile(x, y, map);
			if (tail)
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				toMoving(null, x, y, heading, false);
		} while (false);

	}

	@Override
	public void toAiAttack(long time) {
		super.toAiAttack(time);
		// 스폰된 위치에서 멀리 떠러졋을경우 스폰된 위치로 텔레포트.isSafetyZone
		if ((isBoss() || isSpotMonster()) && !Util.isDistance(getX(), getY(), getMap(), getHomeX(), getHomeY(), getHomeMap(), Lineage.SEARCH_WORLD_LOCATION)) {
			toTeleport(homeX, homeY, homeMap, true);
			return;
		}
		// 잊섬 몬스터 못들어오게
		if (World.isForgetSafetyZone(getX(), getY(), getMap())) {
			toTeleport(homeX, homeY, homeMap, true);
			return;
		}
		// 멘트
		toMent(time);
		// 공격자 확인.
		object o = findDangerousObject();

		// 객체를 찾지못했다면 무시.
		if (o == null)
			return;

		boolean blind = isBuffCurseBlind() && !Util.isDistance(this, o, 2);
		MonsterSkill skill = null;
		Monster mon = getMonster();
		if (mon != null && (mon.getNameIdNumber() == 1000 || mon.getNameIdNumber() == 1019 || mon.getNameIdNumber() == 1746 || mon.getNameIdNumber() == 1745)) {
			skill = findMagic();
			if (o != null && skill != null) {
				if (!blind && Util.getDistance(this, o) > 3 && !Util.isAreaAttack(this, o)) {
					long current = System.currentTimeMillis();
					if (lastSkillTime < current) {
						if (ShareMagic.init(this, o, skill, 0, 0, skill.getDistance() > 2)) {
							lastSkillTime = current + 2000L;
							return;
						}
					}
				}
			}
		}

		// 공격 시전했는지 확인용.
		boolean is_attack = false;
		// 객체 거리 확인
		if (Util.isDistance(this, o, getAtkRange()) && Util.isAreaAttack(this, o) && Util.isAreaAttack(o, this) && !blind) {
			// 스킬 확인하기.
			if (lastSkillTime < System.currentTimeMillis()) {
				for (MonsterSkill ms : mon.getSkillList()) {
					if (ms == null) {
						continue;
					}
					// 마법시전 시도.
					if (ShareMagic.init(this, o, ms, 0, 0, ms.getDistance() > 2)) {
						// 시전 성공시 루프종료.
						is_attack = true;
						break;
					}
				}
			}
			// 마법 시전이 실패됫을때.
			if (!is_attack) {
				if (Util.isDistance(this, o, mon.getAtkRange())) {

					// 물리공격 범위내로 잇을경우 처리.
					toAttack(o, 0, 0, mon.getAtkRange() > 2, gfxMode + Lineage.GFX_MODE_ATTACK, 0, false);
				} else {
					// 객체에게 접근.
					ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
					toMoving(o, o.getX(), o.getY(), 0, true);
				}
			}
		} else {
			ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);

			// 객체 이동
			if (blind) {
				if (Util.random(0, 2) == 0)
					toMoving(null, o.getX() + Util.random(0, 1), o.getY() + Util.random(0, 1), 0, true);
			} else {
				if (!World.isNotAttackTile(o.getX(), o.getY(), o.getMap()))
					toMoving(o, o.getX(), o.getY(), 0, true);

			}
		}
		// 일정확률로 인벤에 있는 체력회복물약 복용하기.
		if (Util.random(0, 5) == 0) {
			// 현재 hp의 백분율값 추출.
			int now_percent = (int) (((double) getNowHp() / (double) getTotalHp()) * 100);
			// 복용해야한다면
			if (Lineage.ai_auto_healingpotion_percent >= now_percent) {
				// 찾고
				ItemInstance ii = getInventory().find(HealingPotion.class);
				// 복용
				if (ii != null)
					ii.toClick(this, null);
			}
		}
	}

	@Override
	protected void toAiDead(long time) {
		Random random = new Random(); // 랜덤 객체 생
		random.setSeed(System.currentTimeMillis());

		if (Util.random(1, 100) < 80 && !getMonster().getName().equalsIgnoreCase("맘보토끼") && !mon.isBoss() && (getMap() == 2004)) {

			Monster monster = MonsterDatabase.find("맘보토끼");

			this.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 6082), true);

			if (monster != null && MonsterSpawnlistDatabase.toSpawnMonster2(monster, x, y, map, heading, false, this)) {
				return;
			}

		}

		if (getMonster().getName().equalsIgnoreCase("맘보토끼")) {

			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("\\fY 하늘정원에서 맘보토끼가 토벌 되었습니다.")));

		}
		super.toAiDead(time);
		// 멘트
		toMent(time);

		object o = getAttackList(0);
		PcInstance tempPc = null;
		for (object oo : getAttackList()) {
			if (oo instanceof PcInstance) {
				tempPc = (PcInstance) oo;
				break;
			}
		}

		if (!(this instanceof SummonInstance)) {
			if (tempPc != null)
				readDrop(getHomeMap(), tempPc);
			else
				readDrop(getHomeMap());
		}

		// 경험치 지급 리스트에 로봇만 존재할 경우 바닥에 아이템 드랍 안함.
		int tempCount = 0;
		boolean isDrop = true;

		for (Exp e : getExpList()) {
			if (e != null && e.getObject() != null) {
				Character cha = (Character) e.getObject();

				if (cha instanceof PcRobotInstance)
					tempCount++;

				if (cha instanceof GuardInstance)
					tempCount++;
			}
		}

		if (tempCount == getExpList().size())
			isDrop = false;

		for (ItemInstance ii : inv.getList()) {
			boolean add = false;
			// 오토루팅 처리.
			if (isAutoPickup(ii) && (!Lineage.monster_item_drop || (Lineage.monster_item_drop && getAttackListSize() <= 1))) {
				if (Lineage.is_damage_item_drop) {
					// 경험치 지급될 목록을 순회하면서 확률 확인하기.
					for (object oo : getAttackList()) {
						Exp e = findExp(oo); // 화면안에 있고, 경험치 지급할게 있을때
						if (e != null && e.getObject() != null && isAutoPickupItem(e.getObject()) && (mon.getExp() * 0.3) <= e.getExp()) {
							Character cha = (Character) oo; // 확률 체크.
							if (Util.random(0, Lineage.auto_pickup_percent) <= (e.getExp() / mon.getExp()) * Lineage.auto_pickup_percent) { // 지급
																																			// 처리.
								if (toAutoPickupItem(cha, ii)) {
									add = true;
									break;
								}
							}
						}
					}
				}

				if (!add) {
					// 지급하지 못햇을경우 랜덤으로 아무에게나 주기.
					for (int i = 0; i < 100; ++i) {
						Exp e = getExpList(Util.random(0, expList.size() - 1));

						if (e != null && e.getObject() != null && isAutoPickupItem(e.getObject())) {
							Character cha = (Character) e.getObject();

							if (cha instanceof SummonInstance) {
								SummonInstance summon = (SummonInstance) cha;

								if (summon.getSummon().getMaster() != null)
									cha = (Character) summon.getSummon().getMaster();
							}

							if (toAutoPickupItem(cha, ii)) {
								add = true;
								break;
							}
						}
					}
				}
			}
			// 땅에 드랍.
			if (isDrop && !add && ii.getItem().isDrop()) {

				if (ii.getObjectId() == 0)
					ii.setObjectId(ServerDatabase.nextItemObjId());
				int x = Util.random(getX() - 1, getX() + 1);
				int y = Util.random(getY() - 1, getY() + 1);

				if (World.isThroughObject(x, y + 1, map, 0))
					ii.toTeleport(x, y, map, false);
				else
					ii.toTeleport(this.x, this.y, map, false);

				// 드랍됫다는거 알리기.
				ii.toDrop(this);

				// 쿠베라펫 버그 인벤토리 제거
				getInventory().remove(ii, true);
			}
		}

		inv.clearList();
		// 경험치 지급
		double total_dmg = 0;
		for (Exp e : getExpList())
			total_dmg += e.getDmg();
		for (object oo : getAttackList()) {
			Exp e = findExp(oo);
			if (e != null) {
				// 데미지를 준만큼 경험치 설정.
				double percent = e.getDmg() / total_dmg;
				e.setExp(getMonster().getExp() * percent);
				//
				if (oo instanceof Character) {
					Character cha = (Character) oo;
					// 화면안에 존재할 경우에만 경험치 지급.
					if (Util.isDistance(this, cha, Lineage.SEARCH_LOCATIONRANGE)) {
						// 파티 경험치 처리. 실패하면 혼자 독식.
						if (!PartyController.toExp(cha, this, e.getExp()) && !cha.isDead()) {
							// 유저와 펫,로봇 만 처리.
							if (cha instanceof PcInstance || cha instanceof PetInstance) {
								// 경험치 지급.
								cha.toExp(this, e.getExp());
								// 라우풀 지급.
								double lawful = Math.round(((getLevel() * 3) / 2) * Lineage.rate_lawful);

								if (getMonster().getLawful() - Lineage.NEUTRAL > 0) {
									int tempLawful = (getMonster().getLawful() - Lineage.NEUTRAL) * -1;
									lawful = Util.random(tempLawful * 0.8, tempLawful);
								}

								cha.setLawful(cha.getLawful() + (int) lawful);
							}
						}
						// 자동사냥 방지
						if (Lineage.is_auto_hunt_check && oo.getGm() == 0 && oo instanceof PcInstance)
							AutoHuntCheckController.addCount((PcInstance) oo);

						// 쿠베라 몬스터 퀘스트
						if (oo instanceof PcInstance) {
							PcInstance pc = (PcInstance) oo;
							int questChapter = pc.getQuestChapter();
							int rquestChapter = pc.getRadomQuest();
							int map = pc.getMap();
							switch (questChapter) {
							case 1:
								if (map == 32) {
									((PcInstance) oo).setQuestKill(((PcInstance) oo).getQuestKill() + 1);
								}
								break;
							case 2:
								if (map == 33) {
									((PcInstance) oo).setQuestKill(((PcInstance) oo).getQuestKill() + 1);
								}
								break;
							case 3:
								if (map == 35) {
									((PcInstance) oo).setQuestKill(((PcInstance) oo).getQuestKill() + 1);
								}
								break;
							case 4:
								if (map == 19 && mon.getName().equalsIgnoreCase("잭 오랜 턴")) {
									((PcInstance) oo).setQuestKill(((PcInstance) oo).getQuestKill() + 1);
								}
								break;
							}
							switch (rquestChapter) {
							case 1:
								if (map == 32) {
									((PcInstance) oo).setRandomQuestkill(((PcInstance) oo).getRandomQuestkill() + 1);
								}
								break;
							case 2:
								if (map == 33) {
									((PcInstance) oo).setRandomQuestkill(((PcInstance) oo).getRandomQuestkill() + 1);
								}
								break;
							case 3:
								if (map == 35) {
									((PcInstance) oo).setRandomQuestkill(((PcInstance) oo).getRandomQuestkill() + 1);
								}
								break;
							}

						}
					}
				}
				ExpDatabase.setPool(e);
			}
		}
		// 크리스마스 이벤트 양말 지급.
		if (Lineage.event_christmas && Util.random(0, 100) < 10 && getAttackListSize() > 0) {
			if (o != null && o instanceof PcInstance && !(o instanceof RobotInstance)) {
				ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("빨간 양말"));
				if (!toAutoPickupItem((PcInstance) o, ii))
					ItemDatabase.setPool(ii);
			}
		}
		if (getMonster().getName().equalsIgnoreCase("월드보스")) {
			for (PcInstance pc : World.getPcList()) {
				if (pc.getMap() == 1400) {
					ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("월드보스 보상"));
					;
					ii.setCount(Lineage.world_result);
					ii.setBless(1);
					ii.setDefinite(true);
					WorldBossController.isOpen = false;
					WorldBossController.isWait = false;
					pc.toGiveItem(null, ii, ii.getCount());
				}

			}

		}

		if (Lineage.monster_boss_dead_message && isBoss()) {
			String msg = null;

			if (getAttackList().size() > 0) {
				if (o != null && o instanceof PcInstance) {
					if (getAttackList().size() > 1)
						msg = String.format("%s  %s", Util.getMapName(this), getMonster().getName());
					else
						msg = String.format("%s %s", Util.getMapName(this), getMonster().getName());
				}
			} else {
				msg = Util.getMapName(this) + " " + getMonster().getName();
			}

			BossController.toWorldOut(this);
			World.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 781, msg));
		}

		ai_time_temp_1 = 0;
		// 전투 관련 변수 초기화.
		clearExpList();
		clearAttackList();
		clearAstarList();
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_CORPSE);
	}

	private static void sleep() {
		try {
			// 스레드 1초 대기
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void toAiCorpse(long time) {
		super.toAiCorpse(time);

		if (ai_time_temp_1 == 0)
			ai_time_temp_1 = time;

		// 시체 유지
		if (summon != null) {
			if (this instanceof PetInstance && ai_time_temp_1 + Lineage.ai_pet_corpse_time > time)
				return;
			if (this instanceof SummonInstance) {
				SummonInstance si = (SummonInstance) this;
				// 요정이 소환한 정령이라면 바로 소멸.
				// 그외엔 시체 유지.
				if (si.isElemental() == false && ai_time_temp_1 + Lineage.ai_summon_corpse_time > time)
					return;
			}
		} else {
			if (this instanceof MonsterInstance && ai_time_temp_1 + Lineage.ai_corpse_time > time && !(this instanceof Faust_Ghost))
				return;
		}

		ai_time_temp_1 = 0;
		// 버프제거
		toReset(true);
		// 시체 제거

		World.remove(this);
		clearList(true);
		// 상태 변환.
		setAiStatus(Lineage.AI_STATUS_SPAWN);
	}

	public void toAiThreadRespawn() {
		// 전투 관련 변수 초기화.
		clearExpList();
		clearAttackList();
		clearAstarList();
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_CORPSE);
		World.removeMonster(this);
		// 버프제거
		toReset(true);
		// 시체 제거
		clearList(true);
		World.remove(this);
	}

	protected void toEventMonsterDead(long time) {
		// 멘트
		toMent(time);
		// 아이템 드랍
		for (ItemInstance ii : inv.getList()) {
			boolean add = false;
			// 오토루팅 처리.
			if (isAutoPickup(ii) && (!Lineage.monster_item_drop || (Lineage.monster_item_drop && getAttackListSize() <= 1))) {
				// 경험치 지급될 목록을 순회하면서 확률 확인하기.
				/*
				 * for(object o : getAttackList()){ Exp e = findExp(o); // 화면안에
				 * 있고, 경험치 지급할게 있을때 if(e!=null && e.getObject()!=null &&
				 * isAutoPickupItem(e.getObject())){ Character cha =
				 * (Character)o; // 확률 체크. if( Util.random(0,
				 * Lineage.auto_pickup_percent) <=
				 * (e.getExp()/mon.getExp())*Lineage.auto_pickup_percent ){ //
				 * 지급 처리. if(toAutoPickupItem(cha, ii)){ add = true; break; } }
				 * } }
				 */
				if (!add) {
					// 지급하지 못햇을경우 랜덤으로 아무에게나 주기.
					for (int i = 0; i < 100; ++i) {
						Exp e = getExpList(Util.random(0, expList.size() - 1));
						if (e != null && e.getObject() != null && isAutoPickupItem(e.getObject())) {
							Character cha = (Character) e.getObject();
							if (toAutoPickupItem(cha, ii)) {
								add = true;
								break;
							}
						}
					}
				}
			}
			// 땅에 드랍.
			if (!add) {
				if (ii.getObjectId() == 0)
					ii.setObjectId(ServerDatabase.nextItemObjId());
				int x = Util.random(getX() - 1, getX() + 1);
				int y = Util.random(getY() - 1, getY() + 1);

				if (World.isThroughObject(x, y + 1, map, 0))
					ii.toTeleport(x, y, map, false);
				else
					ii.toTeleport(this.x, this.y, map, false);
				// 드랍됫다는거 알리기.
				ii.toDrop(this);
			}
		}
		inv.clearList();
		// 경험치 지급
		double total_dmg = 0;
		for (Exp e : getExpList())
			total_dmg += e.getDmg();
		for (object o : getAttackList()) {
			Exp e = findExp(o);
			if (e != null) {
				// 데미지를 준만큼 경험치 설정.
				double percent = e.getDmg() / total_dmg;
				e.setExp(getMonster().getExp() * percent);
				//
				if (o instanceof Character) {
					Character cha = (Character) o;
					// 화면안에 존재할 경우에만 경험치 지급.
					if (Util.isDistance(this, cha, Lineage.SEARCH_LOCATIONRANGE)) {
						// 파티 경험치 처리. 실패하면 혼자 독식.
						if (!PartyController.toExp(cha, this, e.getExp()) && !cha.isDead()) {
							// 유저와 펫,로봇 만 처리.
							if (cha instanceof PcInstance || cha instanceof PetInstance) {
								// 경험치 지급.
								cha.toExp(this, e.getExp());
								// 라우풀 지급.
								double lawful = Math.round(((getLevel() * 3) / 2) * Lineage.rate_lawful);
								if (getMonster().getLawful() < 0)
									lawful = ~(int) lawful + 1;
								cha.setLawful(cha.getLawful() + (int) lawful);
							}
						}
					}
				}
				ExpDatabase.setPool(e);
			}
		}
		// 크리스마스 이벤트 양말 지급.
		if (Lineage.event_christmas && Util.random(0, 100) < 10 && getAttackListSize() > 0) {
			object o = getAttackList(0);

			if (o != null) {
				if (o instanceof PcInstance && !(o instanceof RobotInstance)) {
					ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("빨간 양말"));
					if (!toAutoPickupItem((PcInstance) o, ii))
						ItemDatabase.setPool(ii);
				}
			}
		}
		//
		if (Lineage.monster_boss_dead_message && isBoss()) {
			List<object> list = getAttackList();
			object o = list.get(0);

			if (o != null) {
				if (o instanceof SummonInstance)
					name = ((SummonInstance) o).getOwnName();
			}

			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), String.format("사망: %s", getMonster().getName())));
		}

		ai_time_temp_1 = 0;
		// 전투 관련 변수 초기화.
		clearExpList();
		clearAttackList();
		clearAstarList();

		if (ai_time_temp_1 == 0)
			ai_time_temp_1 = time;

		// 시체 유지
		if (summon != null) {
			if (this instanceof PetInstance && ai_time_temp_1 + Lineage.ai_pet_corpse_time > time)
				return;
			if (this instanceof SummonInstance) {
				SummonInstance si = (SummonInstance) this;
				// 요정이 소환한 정령이라면 바로 소멸.
				// 그외엔 시체 유지.
				if (si.isElemental() == false && ai_time_temp_1 + Lineage.ai_summon_corpse_time > time)
					return;
			}
		} else {
			if (this instanceof MonsterInstance && ai_time_temp_1 + Lineage.ai_corpse_time > time && !(this instanceof Faust_Ghost))
				return;
		}

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

		// 스폰 유지딜레이 값 초기화.
		if (ai_time_temp_1 == 0)
			ai_time_temp_1 = time;
		// 그룹몬스터쪽에 그룹원들이 스폰할 상태인지 확인. 아닐경우 딜레이 시키기.
		if (group_master != null) {
			if (group_master.getObjectId() != getObjectId()) {
				if (ai_time_temp_1 != 1)
					ai_time_temp_1 = time;
			} else {
				if (getGroupMasterDynamic() != null)
					ai_time_temp_1 = time;
			}
		}
		// 스폰 대기.
		if (ai_time_temp_1 + reSpawnTime > time) {

		} else {
			// 리스폰값이 정의되어 있지않다면 재스폰 할 필요 없음.
			// 서먼몬스터에서도 이걸 호출함.
			if (reSpawnTime == 0) {
				toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.object_teleport_effect), true);
				toAiThreadDelete();

				if (monSpawn != null)
					MonsterSpawnlistDatabase.toSpawnMonster(this, World.get_map(homeMap), monSpawn.isRandom(), monSpawn.getX(), monSpawn.getY(), map, monSpawn.getLocSize(), monSpawn.getReSpawn(),
							monSpawn.getReSpawnMax(), false, false);
			} else {

				if (group_master != null && group_master.getObjectId() == getObjectId()) {
					for (MonsterInstance mi : group_master.getGroupList())
						mi.setAiTimeTemp1(1);
				}
				mon = monOld;

				setDead(false);
				setGfx(getMonster().getGfx());
				setGfxMode(getMonster().getGfxMode());
				setClassGfx(getMonster().getGfx());
				setClassGfxMode(getMonster().getGfxMode());
				setName(getMonster().getNameId());
				setLevel(getMonster().getLevel());
				setExp(getMonster().getExp());
				setMaxHp(getMonster().getHp());
				setMaxMp(getMonster().getMp());
				setNowHp(getMonster().getHp());
				setNowMp(getMonster().getMp());
				setLawful(getMonster().getLawful());
				setEarthress(getMonster().getResistanceEarth());
				setFireress(getMonster().getResistanceFire());
				setWindress(getMonster().getResistanceWind());
				setWaterress(getMonster().getResistanceWater());

				if (monSpawn != null) {
					MonsterSpawnlistDatabase.toSpawnMonster(this, World.get_map(homeMap), monSpawn.isRandom(), monSpawn.getX(), monSpawn.getY(), map, monSpawn.getLocSize(), monSpawn.getReSpawn(),
							monSpawn.getReSpawnMax(), false, false);
				} else {
					if (group_master == null)
						toTeleport(homeX, homeY, homeMap, false);
					else
						toTeleport(group_master.getX(), group_master.getY(), group_master.getMap(), false);
				}
				toMent(time);
				setAiStatus(0);
			}
		}
	}

	@Override
	public void toAiEscape(long time) {
		super.toAiEscape(time);
		// 멘트
		toMent(time);

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
		setHeading(Util.oppositionHeading(this, o));
		int temp_heading = getHeading();
		do {
			// 이동 좌표 추출.
			int x = Util.getXY(getHeading(), true) + getX();
			int y = Util.getXY(getHeading(), false) + getY();
			// 해당 좌표 이동가능한지 체크.
			boolean tail = World.isThroughObject(getX(), getY(), getMap(), getHeading()) && (World.isMapdynamic(x, y, getMap()) == false || isBoss());
			if (tail) {
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				toMoving(null, x, y, getHeading(), false);
				break;
			} else {
				setHeading(getHeading() + 1);
				if (temp_heading == getHeading())
					break;
			}
		} while (true);
	}

	@Override
	protected void toAiPickup(long time) {
		// 가장 근접한 아이템 찾기.
		object o = null;
		for (object oo : getInsideList(true)) {
			if (isPickupItem(oo)) {
				if (o == null)
					o = oo;
				else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
					o = oo;
			}
		}
		// 못찾앗을경우 다시 랜덤워킹으로 전환.
		if (o == null) {
			setAiStatus(0);
			// 소환객체라면 아이템 줍기 완료 상태로 전환.
			if (this instanceof SummonInstance)
				((SummonInstance) this).setSummonMode(SUMMON_MODE.ItemPickUpFinal);
			return;
		}

		// 객체 거리 확인
		if (Util.isDistance(this, o, 0)) {

			if (((ItemInstance) o).getItem().getName().equalsIgnoreCase("아데나")) {

				setAiStatus(0);
				// 소환객체라면 아이템 줍기 완료 상태로 전환.
				if (this instanceof SummonInstance)
					((SummonInstance) this).setSummonMode(SUMMON_MODE.ItemPickUpFinal);

				toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), ((SummonInstance) this), 0x02, "아데나는 주울수없습니다."), false);
				ai_time = SpriteFrameDatabase.find(gfx, gfxMode + 0);
				toMoving(this, this.getX(), this.getY(), 0, true);
				return;
			}
			if (this instanceof SummonInstance || this instanceof PetInstance) {
				// 수집 제한 추가
				if (!getInventory().isAppendItem((ItemInstance) o, o.getCount())) {
					setAiStatus(0);
					// 소환객체라면 아이템 줍기 완료 상태로 전환.
					if (this instanceof SummonInstance)
						((SummonInstance) this).setSummonMode(SUMMON_MODE.ItemPickUpFinal);

					toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), ((SummonInstance) this), 0x02, "무게 초과로 주울수없습니다."), false);
					ai_time = SpriteFrameDatabase.find(gfx, gfxMode + 0);
					toMoving(this, this.getX(), this.getY(), 0, true);
					return;
				}
			}
			super.toAiPickup(time);
			// 줍기
			synchronized (o.sync_pickup) {
				if (o.isWorldDelete() == false && o.isPickup(this))
					inv.toPickup(o, o.getCount());
			}
		} else {
			ai_time = SpriteFrameDatabase.find(gfx, gfxMode + 0);
			// 아이템쪽으로 이동.
			toMoving(o, o.getX(), o.getY(), 0, true);
		}
	}

	/**
	 * 오토루팅 처리시 처리해도 되는 객체인지 확인하는 함수. : 중복코드 방지용.
	 * 
	 * @param o
	 * @return
	 */
	private boolean isAutoPickupItem(object o) {
		// 죽지않았고, 케릭터면서, 범위안에 있을경우.
		return !o.isDead() && (o instanceof PcInstance || o instanceof SummonInstance) && Util.isDistance(o, this, Lineage.SEARCH_LOCATIONRANGE);
	}

	/**
	 * 자동루팅 상황에서 아이템 지급처리 담당하는 함수. : 중복 코드 방지용.
	 * 
	 * @param cha
	 * @param ii
	 * @return
	 */
	private boolean toAutoPickupItem(Character cha, ItemInstance ii) {
		int bonusb = 0;
		// 소환객체는 무시.
		if (this instanceof SummonInstance)
			return false;
		//
		if (cha.isAutoPickup() && cha.getInventory().isAppend(ii, ii.getCount(), false)) {
			// 성혈맹원이라면 자동픽업될 아이템 량 증가.
			if (cha instanceof PcInstance && KingdomController.find((PcInstance) cha) != null)
				ii.setCount(Math.round(ii.getCount() * Lineage.kingdom_item_count_rate));
			// 메세지 변수.
			String msg = ii.toString();
			String msg_db = ii.toStringDB();
			// 혹시모를 처리변수가 있을지 모르기때문에 아래와같이 패턴 적용.
			ii.toDrop(this);
			ii.toPickup(cha);
			// 지급.
			Item i = ItemDatabase.find(ii.getItem().getName());

			if (i != null) {
				// 파티시 아덴 분배
				if (i.getNameId().equalsIgnoreCase("$4") && Lineage.is_party_aden_share && cha instanceof PcInstance && cha.getPartyId() > 0) {
					PcInstance pc = (PcInstance) cha;
					Party p = PartyController.find(pc);

					if (p != null && p.isParty(pc, p)) {
						int tempCount = 0;
						for (PcInstance pt : p.getListTemp()) {
							if (Util.isDistance(pc, pt, Lineage.SEARCH_LOCATIONRANGE))
								tempCount += 1;
						}

						for (PcInstance pt : p.getListTemp()) {
							if (Util.isDistance(pc, pt, Lineage.SEARCH_LOCATIONRANGE)) {
								long aden = ii.getCount() / tempCount;
								ItemInstance temp = pt.getInventory().find(i.getName(), ii.getBless(), i.isPiles());

								if (temp != null && (temp.getBless() != ii.getBless() || temp.getEnLevel() != ii.getEnLevel()))
									temp = null;

								if (temp == null) {
									// 겹칠수 있는 아이템이 존재하지 않을경우.
									temp = ItemDatabase.newInstance(i);
									temp.setObjectId(ServerDatabase.nextItemObjId());
									temp.setBless(ii.getBless());
									temp.setEnLevel(ii.getEnLevel());
									temp.setCount(aden);
									temp.setDefinite(true);
									pt.getInventory().append(temp, true);
								} else {
									// 겹치는 아이템이 존재할 경우.
									pt.getInventory().count(temp, temp.getCount() + aden, true);
								}
							}
						}
					} else {
						ItemInstance temp = cha.getInventory().find(i.getName(), ii.getBless(), i.isPiles());

						if (temp != null && (temp.getBless() != ii.getBless() || temp.getEnLevel() != ii.getEnLevel()))
							temp = null;

						if (temp == null) {
							// 겹칠수 있는 아이템이 존재하지 않을경우.
							if (i.isPiles()) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setBless(ii.getBless());
								temp.setEnLevel(ii.getEnLevel());
								temp.setCount(ii.getCount());
								temp.setDefinite(true);
								cha.getInventory().append(temp, true);
							} else {
								for (int idx = 0; idx < ii.getCount(); idx++) {
									temp = ItemDatabase.newInstance(i);
									temp.setObjectId(ServerDatabase.nextItemObjId());
									temp.setBless(ii.getBless());
									temp.setEnLevel(ii.getEnLevel());
									temp.setDefinite(true);
									cha.getInventory().append(temp, true);
								}
							}
						} else {
							// 겹치는 아이템이 존재할 경우.
							cha.getInventory().count(temp, temp.getCount() + ii.getCount(), true);
						}
					}
				} else {
					ItemInstance temp = cha.getInventory().find(i.getName(), ii.getBless(), i.isPiles());

					if (temp != null && (temp.getBless() != ii.getBless() || temp.getEnLevel() != ii.getEnLevel()))
						temp = null;

					if (temp == null) {
						// 겹칠수 있는 아이템이 존재하지 않을경우.
						if (i.isPiles()) {
							temp = ItemDatabase.newInstance(i);
							temp.setObjectId(ServerDatabase.nextItemObjId());
							temp.setBless(ii.getBless());
							temp.setEnLevel(ii.getEnLevel());
							temp.setCount(ii.getCount());
							temp.setDefinite(true);
							cha.getInventory().append(temp, true);

						} else {
							for (int idx = 0; idx < ii.getCount(); idx++) {
								temp = ItemDatabase.newInstance(i);
								temp.setObjectId(ServerDatabase.nextItemObjId());
								temp.setBless(ii.getBless());
								temp.setEnLevel(ii.getEnLevel());
								temp.setDefinite(true);
								cha.getInventory().append(temp, true);

							}
						}
					} else {
						// 겹치는 아이템이 존재할 경우.
						cha.getInventory().count(temp, temp.getCount() + ii.getCount(), true);

					}

					if (Lineage.is_item_drop_msg_monster && this != null && getMonster() != null && temp != null && temp.getItem().getName() != null) {
						ItemDropMessageDatabase.sendMessage(cha, temp.getItem().getName(), getMonster().getName());
					}
				}
			}

			if (Lineage.party_autopickup_item_print && cha instanceof PcInstance) {
				PcInstance pc = (PcInstance) cha;
				Party p = PartyController.find((PcInstance) cha);
				if (p != null) {
					if (pc.isPartyMent()) {
						msg = String.format("%s %s 에게서 %s 얻었습니다.", Util.getStringWord(cha.getName(), "이", "가"), getMonster().getName(), Util.getStringWord(msg_db + "", "을", "를"));
						p.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, msg));
						return true;
					}
				}
			}
			// \f1%0%s 당신에게 %1%o 주었습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), msg));
			return true;
		}
		return false;
	}

	/**
	 * 인공지능 처리구간에서 주기적으로 호출됨. : 상태에 맞는 멘트를 구사할지 여부 확인하여 표현 처리하는 함수.
	 * 
	 * @param time
	 *            : 진행되고있는 시간.
	 */
	private void toMent(long time) {
		int msgTime = -1;
		int msgSize = 0;
		List<String> msgList = null;
		switch (ai_status) {
		case Lineage.AI_STATUS_WALK:
			msgTime = mon.getMsgWalkTime();
			msgSize = mon.getMsgWalk().size();
			msgList = mon.getMsgWalk();
			break;
		case Lineage.AI_STATUS_ATTACK:
			msgTime = mon.getMsgAtkTime();
			msgSize = mon.getMsgAtk().size();
			msgList = mon.getMsgAtk();
			break;
		case Lineage.AI_STATUS_DEAD:
			msgTime = mon.getMsgDieTime();
			msgSize = mon.getMsgDie().size();
			msgList = mon.getMsgDie();
			break;
		case Lineage.AI_STATUS_SPAWN:
			msgTime = mon.getMsgSpawnTime();
			msgSize = mon.getMsgSpawn().size();
			msgList = mon.getMsgSpawn();
			break;
		case Lineage.AI_STATUS_ESCAPE:
			msgTime = mon.getMsgEscapeTime();
			msgSize = mon.getMsgEscape().size();
			msgList = mon.getMsgEscape();
			break;
		}

		// 멘트를 표현할 수 있는 디비상태일경우.
		if (msgTime != -1 && msgSize > 0 && msgList != null) {
			if (msgTime == 0) {
				// 한번만 처리하기.
				if (!ai_showment) {
					toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), this, 0x02, msgList.get(0)), false);
					ai_showment = true;
				}
			} else {
				// 시간마다 표현하기.
				if (time - ai_showment_time >= msgTime) {
					ai_showment_time = time;
					if (ai_showment_idx >= msgSize)
						ai_showment_idx = 0;
					toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), this, 0x02, msgList.get(ai_showment_idx++)), false);
				}
			}
		}
	}

	/**
	 * 해당 아이템 오토루팅 할지 여부.
	 * 
	 * @param ii
	 * @return
	 */
	private boolean isAutoPickup(ItemInstance ii) {
		// 아데나 오토루팅 여부
		if (ii.getItem().getNameIdNumber() == 4) {
			return Lineage.auto_pickup_aden;
		} else {
			return Lineage.auto_pickup;
		}
	}

	@Override
	public int getTotalAc() {
		return super.getTotalAc() + mon.getAc();
	}

	@Override
	public int getDynamicMr() {
		return super.getDynamicMr() + mon.getMr();
	}

	public MonsterSkill findMagic() {
		Monster monTemp = mon;
		if (monTemp == null) {
			return null;
		}

		for (MonsterSkill temp : mon.getSkillList()) {
			if (temp == null) {
				continue;
			} else if (temp.getUid() == 78 || temp.getUid() == 93 || temp.getUid() == 136 || temp.getUid() == 168 || temp.getUid() == 178) {
				return temp;
			}
		}
		return null;
	} 

	/**
	 * 동족 확인 2017-10-26 by all-night
	 */
	public boolean isFamily(MonsterInstance o, String name) {
		StringTokenizer st = new StringTokenizer(name, ",");

		while (st.hasMoreTokens()) {
			if (o.getMonster().getFamily().contains(st.nextToken()))
				return true;
		}

		return false;
	}

	public void readDrop() {
	}
}
