package lineage.world.object.instance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import kubera.AttackController;
import lineage.bean.database.Item;
import lineage.bean.database.ItemTeleport;
import lineage.bean.database.Poly;
import lineage.bean.database.Shop;
import lineage.bean.database.Skill;
import lineage.bean.database.SkillRobot;
import lineage.bean.lineage.Book;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.RobotPoly;
import lineage.bean.lineage.Summon;
import lineage.database.BackgroundDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemTeleportDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.SummonListDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.LocationController;
import lineage.world.controller.MagicDollController;
import lineage.world.controller.RobotController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.controller.SummonController.TYPE;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.EventInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.InnInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MagicDollInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetMasterInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.instance.TeleportInstance;
import lineage.world.object.item.Aden;
import lineage.world.object.item.weapon.Arrow;
import lineage.world.object.item.ElvenWafer;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.scroll.ScrollPolymorph;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.Criminal;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HastePotionMagic;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Wafer;
import lineage.world.object.npc.background.Cracker;

public class PcRobotInstance extends RobotInstance {

	private static int ADEN_LIMIT = 1000000; // 아데나 체크할 최소값 및 추가될 아데나 갯수.
	private static int HEALING_PERCENT = 80; // 체력 회복제를 복용할 시점 백분율
	private static int GOTOHOME_FERCENT = 20; // 체력이 해당퍼센트값보다 작으면 귀환함.

	private static enum PCROBOT_MODE {
		None, // 기본값
		HealingPotion, // 물약상점 이동.
		HastePotion, // 초록물약 상점 이동.
		BraveryPotion, // 용기물약 상점 이동.
		ScrollPolymorph, // 변신주문서 상점 이동.
		Arrow, // 화살 상점 이동.
		InventoryHeavy, // 마을로 이동.
		ElvenWafer, // 엘븐와퍼 상점 이동.
		Polymorph, // 변신하기위해 마을로 이동.
		Stay, // 휴식 모드.
		Cracker, // 허수아비 모드.
	}

	private AStar aStar; // 길찾기 변수
	private Node tail; // 길찾기 변수
	private int[] iPath; // 길찾기 변수
	private List<object> attackList; // 전투 목록
	private List<object> astarList; // astar 무시할 객체 목록.
	private Item weapon; // 무기
	private Item doll;
	private int weaponEn; // 무기 인첸
	private String weapon_name;
	private String doll_name;
	public PCROBOT_MODE pcrobot_mode; // 처리 모드.
	private int step; // 일렬에 동작처리중 사용되는 스탭변수.
	private int tempGfx; // 변신값 임시 저장용
	private ShopInstance shopTemp; // 상점 처리 임시 저장용
	// 시체유지(toAiCorpse) 구간에서 사용중.
	// 재스폰대기(toAiSpawn) 구간에서 사용중.
	private long ai_time_temp_1;
	private long polyTime;
	private long delayTime;
	private long shopTime;
	public long teleportTime;
	//
	private List<object> ai_list_temp;

	// 로봇 행동.
	public String action;
	// 리로드 확인용.
	public boolean isReload;

	public PcRobotInstance() {
		aStar = new AStar();
		iPath = new int[2];
		astarList = new ArrayList<object>();
		attackList = new ArrayList<object>();
		ai_list_temp = new ArrayList<object>();
	}

	@Override
	public void close() {
		super.close();
		//
		if (getInventory() != null) {
			for (ItemInstance ii : getInventory().getList())
				ItemDatabase.setPool(ii);
			getInventory().clearList();
		}

		weapon_name = doll_name = null;
		weapon = doll = null;
		shopTemp = null;
		action = null;
		teleportTime = shopTime = delayTime = polyTime = ai_time_temp_1 = weaponEn = step = tempGfx = 0;
		isReload = false;
		
		if (Util.random(0, 99) < 10)
			pcrobot_mode = PCROBOT_MODE.Stay;
		else
			pcrobot_mode = PCROBOT_MODE.None;
		
		if (aStar != null)
			aStar.cleanTail();
		if (attackList != null)
			clearAttackList();
		if (astarList != null)
			clearAstarList();
	}

	@Override
	public void toSave(Connection con) {
	}

	public int getAttackListSize() {
		return attackList.size();
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

	private List<object> getAttackList() {
		synchronized (attackList) {
			return new ArrayList<object>(attackList);
		}
	}

	private boolean containsAttackList(object o) {
		synchronized (attackList) {
			return attackList.contains(o);
		}
	}

	private void clearAttackList() {
		synchronized (attackList) {
			attackList.clear();
		}
	}

	private boolean containsAstarList(object o) {
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

	private void clearAstarList() {
		synchronized (astarList) {
			astarList.clear();
		}
	}

	public int getWeaponEn() {
		return weaponEn;
	}
	
	public void setWeaponEn(int weaponEn) {
		this.weaponEn = weaponEn;
	}

	public int getTempGfx() {
		return tempGfx;
	}

	public void setTempGfx(int tempGfx) {
		this.tempGfx = tempGfx;
	}

	public String getWeapon_name() {
		return weapon_name;
	}

	public void setWeapon_name(String weapon_name) {
		this.weapon_name = weapon_name;
	}
	
	public String getDoll_name() {
		return doll_name;
	}

	public void setDoll_name(String doll_name) {
		this.doll_name = doll_name;
	}
	
	public void toWorldJoin(Connection con) {
		super.toWorldJoin();
		// 인공지능 상태 변경.
		setAiStatus(Lineage.AI_STATUS_WALK);
		// 메모리 세팅
		setAutoPickup(Lineage.auto_pickup);
		BookController.toWorldJoin(this);
		RobotController.readBook(con, this);
		CharacterController.toWorldJoin(this);
		BuffController.toWorldJoin(this);
		SkillController.toWorldJoin(this);
		RobotController.readSkill(con, this);
		SummonController.toWorldJoin(this);
		MagicDollController.toWorldJoin(this);

		// 인벤토리 셋팅.
		setInventory();
		// 인공지능 활성화를위해 등록.
		AiThread.append(this);
	}

	@Override
	public void toWorldOut() {
		//
		super.toWorldOut();
		//
		setAiStatus(Lineage.AI_STATUS_DELETE);
		// 죽어있을경우에 처리를 위해.
		toReset(true);
		// 사용된 메모리 제거
		World.removePc(this);
		SummonController.toWorldOut(this);
		BookController.toWorldOut(this);
		SkillController.toWorldOut(this);
		CharacterController.toWorldOut(this);
		MagicDollController.toWorldOut(this);
		// 메모리 초기화
		close();
	}
	
	public void setPcBobot_mode(String mode) {
		if (mode.equalsIgnoreCase("사냥 & PvP") || mode.equalsIgnoreCase("사냥") || mode.equalsIgnoreCase("PvP")) {
			if (action.equalsIgnoreCase("허수아비 공격") || action.equalsIgnoreCase("마을 대기")) {
				setAiStatus(Lineage.AI_STATUS_WALK);
				pcrobot_mode = PCROBOT_MODE.None;
				clearAttackList();
				clearAstarList();
			}
		} else if (mode.equalsIgnoreCase("허수아비 공격")) {
			if (pcrobot_mode != PCROBOT_MODE.Cracker || getAttackListSize() < 1)
				attackCracker();
		} else if (mode.equalsIgnoreCase("마을 대기")) {
			if (action.equalsIgnoreCase("허수아비 공격"))
				gotoHome(true);
			else
				gotoHome(false);
		}
	}

	@Override
	public void toRevival(object o) {
		if (isDead()) {
			super.toReset(false);
			
			clearAttackList();
			clearAstarList();
			
			int[] home = null;
			home = Lineage.getHomeXY();
			setHomeX(home[0]);
			setHomeY(home[1]);
			setHomeMap(home[2]);
			
			toTeleport(getHomeX(), getHomeY(), getHomeMap(), isDead() == false);
			
			// 다이상태 풀기.
			setDead(false);
			// 체력 채우기.
			setNowHp(level);
			// 패킷 처리.
			toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), o, this), false);
			// 상태 변경.
			ai_time_temp_1 = 0;
			setAiStatus(Lineage.AI_STATUS_WALK);
		}
	}

	@Override
	public void setDead(boolean dead) {
		super.setDead(dead);
		if (dead) {
			ai_time = 0;
			setAiStatus(Lineage.AI_STATUS_DEAD);
		}
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		super.toDamage(cha, dmg, type);
		// 버그 방지 및 자기자신이 공격햇을경우 무시.
		if (cha == null || cha.getObjectId() == getObjectId() || dmg <= 0 || cha.getGm() > 0)
			return;
		
		if (cha instanceof PcInstance && action.equalsIgnoreCase("사냥")) {
			if (Util.random(1, 100) < 20)
				randomTeleport();
			return;
		}
		
		if (cha instanceof MonsterInstance && action.equalsIgnoreCase("PvP")) {
			if (Util.random(1, 100) < 20)
				randomTeleport();
			return;
		}

		// 공격목록에 추가.
		addAttackList(cha);
		// 길찾기 무시할 목록에서 제거.
		removeAstarList(cha);
		setAiStatus(Lineage.AI_STATUS_ATTACK);
	}

	@Override
	public void toAiThreadDelete() {
		super.toAiThreadDelete();
		// 사용된 메모리 제거
		World.removePc(this);
		BookController.toWorldOut(this);
		CharacterController.toWorldOut(this);
	}

	@Override
	public void toAi(long time) {
		// 죽엇을 경우
		if (isDead()) {
			if (ai_time_temp_1 == 0)
				ai_time_temp_1 = time;

			// 시체 유지
			if (ai_time_temp_1 + Lineage.ai_robot_corpse_time > time)
				return;
			else {
				gotoHome(false);
				toRevival(this);
			}
		}
		
		if (action.equalsIgnoreCase("마을 대기")) {
			if (!World.isSafetyZone(getX(), getY(), getMap()))
				gotoHome(false);		
			return;
		}
		
		// 허수아비 공격.
		if (action.equalsIgnoreCase("허수아비 공격")) {
			if (pcrobot_mode != PCROBOT_MODE.Cracker) {
				if (weapon.getType2().equalsIgnoreCase("bow")) {
					if (getInventory().find(Arrow.class) != null) {
						attackCracker();
						return;
					}					
				} else {
					attackCracker();
					return;
				}
			}
		}

		// 무기 착용.
		if (getInventory().getSlot(Lineage.SLOT_WEAPON) == null
				|| (this.getInventory().getSlot(Lineage.SLOT_WEAPON) != null && !getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getName().equalsIgnoreCase(this.getWeapon_name()))) {
			if (getInventory().find(weapon) == null) {
				if (RobotController.getWeapon(getClassType()) != null) {
					weapon = RobotController.getWeapon(getClassType());
					
					ItemInstance item = ItemDatabase.newInstance(weapon);
					item.setObjectId(ServerDatabase.nextEtcObjId());
					item.setEnLevel(weaponEn);
					getInventory().append(item, false);
					
					item.toClick(this, null);
				}
				return;
			}
			
			// 인형.
			if (this.getInventory() != null) {
				if (this.getDoll_name() != null && doll != null) {
					doll = ItemDatabase.find(this.getDoll_name());
					ItemInstance item = ItemDatabase.newInstance(doll);
					if (item != null) {
						item.setObjectId(ServerDatabase.nextEtcObjId());
						getInventory().append(item, false);
						item.toClick(this, null);
					}
					return;
				}
		} else {
			getInventory().find(doll).toClick(this, null);
		}
	}

		// 필드에서만 체력 확인해서 귀환하기.
		if (World.isSafetyZone(getX(), getY(), getMap()) == false && getHpPercent() <= GOTOHOME_FERCENT) {
			// 너무 도망을 잘 치기 때문에 확률적으로 처리.
			if (getMap() == 4) {
				if (Util.random(0, 99) <= 60) {
					pcrobot_mode = PCROBOT_MODE.Stay;
					gotoHome(false);
					ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
					return;
				}
			} else {
				if (Util.random(0, 99) <= 10) {
					pcrobot_mode = PCROBOT_MODE.Stay;
					gotoHome(false);
					ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
					return;
				} else if (Util.random(0, 99) <= 20 && randomTeleport()) {
					pcrobot_mode = PCROBOT_MODE.None;
					return;
				}
			}
		}

		// 화살 장착
		if (weapon.getType2().equalsIgnoreCase("bow"))
			setArrow();

		switch (getAiStatus()) {
		// 공격목록이 발생하면 공격모드로 변경
		case Lineage.AI_STATUS_WALK:
			if (getAttackListSize() > 0)
				setAiStatus(Lineage.AI_STATUS_ATTACK);
			break;
		// 전투 처리부분은 항상 타켓들이 공격가능한지 확인할 필요가 있음.
		case Lineage.AI_STATUS_ATTACK:
			if (pcrobot_mode != PCROBOT_MODE.Cracker) {
				for (object o : getAttackList()) {
					if (!Util.isAreaAttack(this, o) || !isAttack(o, false))
						removeAttackList(o);
				}
			}

			// 전투목록이 없을경우 랜덤워킹으로 변경.
			if ((getAttackListSize() < 1 || getInsideListAttackCount() < 1) && pcrobot_mode != PCROBOT_MODE.Cracker)
				randomTeleport();
			break;
		}

		// 힐링 포션이 없다면.
		if (pcrobot_mode == PCROBOT_MODE.None && getInventory().find(HealingPotion.class) == null)
			pcrobot_mode = PCROBOT_MODE.HealingPotion;
		// 속도향상 물약이 없다면.
		if (pcrobot_mode == PCROBOT_MODE.None && getInventory().find(HastePotion.class) == null)
			pcrobot_mode = PCROBOT_MODE.HastePotion;
		// 용기물약이 없다면.
		if (pcrobot_mode == PCROBOT_MODE.None && (getClassType() == Lineage.LINEAGE_CLASS_ROYAL || getClassType() == Lineage.LINEAGE_CLASS_KNIGHT) && getInventory().find(BraveryPotion.class) == null)
			pcrobot_mode = PCROBOT_MODE.BraveryPotion;
		// 엘븐 와퍼가 없다면.
		if (pcrobot_mode == PCROBOT_MODE.None && getClassType() == Lineage.LINEAGE_CLASS_ELF && getInventory().find(ElvenWafer.class) == null)
			pcrobot_mode = PCROBOT_MODE.ElvenWafer;
		// 변신주문서가 없다면.
		if (pcrobot_mode == PCROBOT_MODE.None && getInventory().find(ScrollPolymorph.class) == null)
			pcrobot_mode = PCROBOT_MODE.ScrollPolymorph;
		// 화살이 없다면.
		if ((pcrobot_mode == PCROBOT_MODE.None || pcrobot_mode == PCROBOT_MODE.Cracker) && weapon.getType2().equalsIgnoreCase("bow") && getInventory().find(Arrow.class) == null)
			pcrobot_mode = PCROBOT_MODE.Arrow;
		// 무게가 무거울경우.
		if (pcrobot_mode == PCROBOT_MODE.None && getInventory().isWeightPercent(82) == false)
			pcrobot_mode = PCROBOT_MODE.InventoryHeavy;
		// 변신 하기.
		if (pcrobot_mode == PCROBOT_MODE.None && getGfx() == getClassGfx() && RobotController.isPoly(this))
			pcrobot_mode = PCROBOT_MODE.Polymorph;
		// 모드가 변경되면
		if (pcrobot_mode != PCROBOT_MODE.None && pcrobot_mode != PCROBOT_MODE.Cracker) {
			setAiStatus(Lineage.AI_STATUS_WALK);
			// 아데나 없다면 갱신.
			ItemInstance aden = getInventory().findAden();
			if (aden == null || aden.getCount() < ADEN_LIMIT) {
				if (aden == null) {
					aden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
					aden.setObjectId(ServerDatabase.nextEtcObjId());
					getInventory().append(aden, false);
				}
				aden.setCount(aden.getCount() + ADEN_LIMIT);
			}
		}
		//
		super.toAi(time);
	}
	
	/**
	 * 랜덤 텔레포트
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	protected boolean randomTeleport() {
		if (teleportTime < System.currentTimeMillis()) {
			if (isPossibleMap()) {
				teleportTime = System.currentTimeMillis() + Util.random(500, 1500);
				clearAttackList();
				clearAstarList();
				
				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
				setAiStatus(Lineage.AI_STATUS_WALK);
				
				if (!LocationController.isTeleportZone(this, true, false) || (getMap() == 4 && !World.isSafetyZone(getX(), getY(), getMap())))
					return false;
				
				// 랜덤 텔레포트
				Util.toRndLocation(this);
				toTeleport(getHomeX(), getHomeY(), getHomeMap(), true);
				toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
				return true;
			}
		}
		return false;
	}

	@Override
	protected void toAiWalk(long time) {
		super.toAiWalk(time);
		
		// 오픈대기 확인
		if (Lineage.open_wait && pcrobot_mode != PCROBOT_MODE.Stay && pcrobot_mode != PCROBOT_MODE.Cracker && isWait())
			return;

		//
		switch (pcrobot_mode) {
		case HealingPotion:
			// 물약 상점 으로 이동 및 구매.
			// 구매상점도 다양한 마을 이동을 통해 성에 세금을 많이 주도록 유도하는것도 나쁘지 않을듯.
			toShop(RobotController.getHealingPotion(this), RobotController.getHealingPotionCnt());
			return;
		case HastePotion:
			// 속도향상물약 상점으로 이동 및 구매.
			toShop(RobotController.getHastePotion(this), RobotController.getHastePotionCnt());
			return;
		case BraveryPotion:
			// 용기물약 상점으로 이동 및 구매.
			toShop(RobotController.getBraveryPotion(this), RobotController.getBraveryPotionCnt());
			return;
		case ElvenWafer:
			// 엘븐 와퍼 상점으로 이동 및 구매.
			toShop(RobotController.getElvenWafer(this), RobotController.getElvenWaferCnt());
			return;
		case ScrollPolymorph:
			// 변신주문서 상점으로 이동 및 구매.
			toShop(RobotController.getScrollPolymorph(this), RobotController.getScrollPolymorphCnt());
			return;
		case Arrow:
			toShop(RobotController.getArrow(this), RobotController.getArrowCnt());
			return;
		case InventoryHeavy:
			toInventoryHeavy();
			return;
		case Polymorph:
			toPolymorph();
			return;
		case Stay:
			toStay(time);
			return;
		}

		// 물약 복용.
		if (pcrobot_mode != PCROBOT_MODE.Cracker || pcrobot_mode != PCROBOT_MODE.Stay) {
			toHealingPotion();
			
			if (isPossibleMap())
				toBuffPotion();
			
			// 버프 시전.
			List<Skill> skill_list = SkillController.find(this);
			if (toSkillHealMp(skill_list) || toSkillHealHp(skill_list) || toSkillBuff(skill_list) || toSkillSummon(skill_list)) {
				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION);
				return;
			}

			// 서먼객체에게 버프 시전.
			if (toBuffSummon()) {
				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION);
				return;
			}
		}	

		// 잊섬은 제외
		if (getMap() != 70 && World.isSafetyZone(getX(), getY(), getMap())) {
			if (delayTime == 0)
				delayTime = System.currentTimeMillis() + (1000 * (Util.random(3, 10)));

			if (delayTime > 0 && delayTime <= System.currentTimeMillis())
				delayTime = 0;
			else
				return;

			// 보라돌이된 상태 제거하기.
			if (isBuffCriminal())
				BuffController.remove(this, Criminal.class);
			// 사냥터 이동.
			List<Book> list = BookController.find(this);
			// 등록된 사냥터 없으면 무시.
			if (list.size() == 0)
				return;
			//
			Book b = null;
			ItemTeleport it = null;

			for (;;) {
				b = list.get(Util.random(0, list.size() - 1));

				if (b == null)
					return;

				it = ItemTeleportDatabase.find2(b.getMap());

				if (it == null)
					return;

				if (it != null && b.getMinLevel() <= getLevel() && ItemTeleportDatabase.toTeleport(it, this))
					break;
				else if (it != null && b.getMinLevel() <= getLevel())
					return;
			}

			if (b != null) {
				setHomeX(b.getX());
				setHomeY(b.getY());
				setHomeMap(b.getMap());
				toTeleport(b.getX(), b.getY(), b.getMap(), true);
			}
		} else {
			// 타켓 찾기.
			for (object o : getInsideList()) {
				if (Util.isAreaAttack(this, o) && isAttack(o, true)) {
					// 공격목록에 등록.
					if (!containsAstarList(o)) {
						if (o instanceof PcInstance && (action.equalsIgnoreCase("PvP") || (action.equalsIgnoreCase("사냥 & PvP") && Util.random(0, 99) < 20))) {
							addAttackList(o);
						} else if (o instanceof MonsterInstance && (action.equalsIgnoreCase("사냥") || action.equalsIgnoreCase("사냥 & PvP"))) {
							addAttackList(o);
						}
					}
				}
			}
			
			if (getInsideListAttackCount() < 1)
				randomTeleport();
				
			// Astar 발동처리하다가 길이막혀서 이동못하던 객체를 모아놓은 변수를 일정주기마다 클린하기.
			if (Util.random(0, 3) == 0)
				clearAstarList();
		}
	}
	
	/**
	 * 로봇 주위에 공격 가능한 객체수 확인.
	 * 2019-04-29
	 * by connector12@nate.com
	 */
	public int getInsideListAttackCount() {
		int count = 0;
		
		for (object o : getInsideList()) {
			if (Util.isAreaAttack(this, o) && isAttack(o, true) && !containsAstarList(o))
				count++;
		}
		
		return count;
	}

	@Override
	protected void toAiAttack(long time) {
		super.toAiAttack(time);

		// 오픈대기 확인
		if (Lineage.open_wait && pcrobot_mode != PCROBOT_MODE.Cracker && isWait())
			return;

		// 물약 복용.
		if (action.equalsIgnoreCase("사냥 & PvP") || action.equalsIgnoreCase("사냥") || action.equalsIgnoreCase("PvP")) {
			toHealingPotion();
			toBuffPotion();
		}

		// 공격자 확인.
		object o = findDangerousObject();

		// 객체를 찾지못했다면 무시.
		if (pcrobot_mode != PCROBOT_MODE.Cracker && (o == null || getInsideListAttackCount() < 1)) {
			randomTeleport();
			return;
		}
		
		// 허수아비 공격 모드인데 공격할 허수아비가 없을 경우.
		if (pcrobot_mode == PCROBOT_MODE.Cracker && o == null)
			return;

		// 타켓이 사용자일때
		if (o instanceof PcInstance) {
			// 근처 경비병잇으면 마을로 귀환.
			for (object oo : getInsideList()) {
				if (oo instanceof GuardInstance) {
					gotoHome(false);
					return;

				}
			}
		}

		// 공격 마법
		if (o instanceof Character && toSkillAttack(o))
			return;

		// 활공격인지 판단.
		boolean bow = getInventory().getSlot(Lineage.SLOT_WEAPON) == null ? false : getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow");
		int atkRange = bow ? 8 : 1;
		// 객체 거리 확인
		if (Util.isDistance(this, o, atkRange) && Util.isAreaAttack(this, o) && Util.isAreaAttack(o, this)) {
			// 공격 시전했는지 확인용.
			if (Util.isDistance(this, o, atkRange)) {
				// 물리공격 범위내로 잇을경우 처리.
				
				if ((AttackController.isAttackTime(this, getGfxMode() + Lineage.GFX_MODE_ATTACK, false) || AttackController.isMagicTime(this, getCurrentSkillMotion()))) {
					int frame = (int) (SpriteFrameDatabase.getSpeedCheckGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_ATTACK) + 40);
					ai_time = frame;

					toAttack(o, o.getX(), o.getY(), bow, getGfxMode() + Lineage.GFX_MODE_ATTACK, 0, false);
				}
				
			} else {
				// 객체에게 접근.
				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
				if (!toMoving(this, o.getX(), o.getY(), 0, true))
					removeAttackList(o);
				if (pcrobot_mode == PCROBOT_MODE.Cracker && getAttackListSize() == 0)
					gotoHome(true);
			}
		} else {
			ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
			// 객체 이동 - 여기
			if (!toMoving(this, o.getX(), o.getY(), 0, true))
				removeAttackList(o);
			if (pcrobot_mode == PCROBOT_MODE.Cracker && getAttackListSize() == 0)
				gotoHome(true);
		}
	}

	@Override
	protected void toAiDead(long time) {
		super.toAiDead(time);

		ai_time_temp_1 = 0;
		// 전투 관련 변수 초기화.
		clearAttackList();
		clearAstarList();
		// 상태 변환
		setAiStatus(Lineage.AI_STATUS_CORPSE);
	}

	@Override
	protected void toAiCorpse(long time) {
		super.toAiCorpse(time);

		if (ai_time_temp_1 == 0)
			ai_time_temp_1 = time;

		// 시체 유지
		if (ai_time_temp_1 + Lineage.ai_robot_corpse_time > time)
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
		gotoHome(false);
		// 부활 뒷 처리.
		toRevival(this);
		// 상태 변환.
		setAiStatus(Lineage.AI_STATUS_WALK);
	}

	@Override
	protected void toAiPickup(long time) {
		// 가장 근접한 아이템 찾기. (아데나 만)
		object o = null;
		
		for (object oo : getInsideList()) {
			if (oo instanceof Aden) {
				if (o == null)
					o = oo;
				else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
					o = oo;
			}
		}
		
		// 못찾앗을경우 다시 랜덤워킹으로 전환.
		if (o == null) {
			setAiStatus(Lineage.AI_STATUS_WALK);
			return;
		}

		// 객체 거리 확인
		if (Util.isDistance(this, o, 1)) {
			super.toAiPickup(time);
			// 줍기 - 간혹 줍지못하고 멈춤. 픽업기능 제거해야 겟음. (원인을 모르겟음 현재는.)
			synchronized (o.sync_pickup) {
				if (o.isWorldDelete() == false)
					getInventory().toPickup(o, o.getCount());
			}
		} else {
			ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
			// 아이템쪽으로 이동.
			toMoving(o, o.getX(), o.getY(), 0, true);
		}
	}

	private void toStay(long time) {
		switch (step) {
		case 0:
			// 마을로 이동.
			gotoHome(false);
			step += 1;
			break;
		case 1:
			// 창고 근처로 이동.
			if (ai_list_temp.size() == 0)
				World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, ai_list_temp);
			boolean isFind = false;
			for (object o : ai_list_temp) {
				if (o instanceof DwarfInstance) {
					isFind = true;
					// 거리 확인.
					if (Util.isDistance(this, o, Util.random(1, 8))) {
						ai_list_temp.clear();
						step += 1;
						setHeading(Util.random(0, 7));
					} else {
						isFind = toMoving(o, o.getX(), o.getY(), 0, true);
					}
					break;
				}
			}
			// 창고를 찾지 못했거나 길을 못찾았다면 휴식모드 취소.
			if (isFind == false) {
				ai_list_temp.clear();
				// 초기화.
				step = 0;
				// 기본 모드로 변경.
				pcrobot_mode = PCROBOT_MODE.None;
			}
			break;
		case 2:
			// 휴식.
			if (ai_time_temp_1 == 0)
				ai_time_temp_1 = time;
			if (ai_time_temp_1 + (Util.random(1000 * 20, 1000 * 120)) > time)
				return;
			// 휴식 종료.
			ai_time_temp_1 = 0;
			// 초기화.
			step = 0;
			
			// 기본 모드로 변경.
			if (Util.random(1, 100) < 3)
				pcrobot_mode = PCROBOT_MODE.Stay;
			else
				pcrobot_mode = PCROBOT_MODE.None;
			
			
			break;
		}
	}

	private void toPolymorph() {
		switch (step) {
		case 0:
			if (polyTime == 0)
				polyTime = System.currentTimeMillis() + (1000 * (Util.random(1, 5)));
			
			if (polyTime > 0 && polyTime <= System.currentTimeMillis())
				step = 1;
			break;
		case 1:
			ItemInstance poly = getInventory().find(ScrollPolymorph.class);
			
			if (poly != null && poly.getCount() > 0) {
				// 변신.
				Poly p = PolyDatabase.getName(getPolymorph());
				
				if (p != null) {
					
					// 장비 해제.
					PolyDatabase.toEquipped(this, p);
					// 변신
					setGfx(p.getGfxId());

					if (Lineage.is_weapon_speed) {
						if (getInventory().getSlot(Lineage.SLOT_WEAPON) != null && SpriteFrameDatabase.findGfxMode(getGfx(), getGfxMode() + Lineage.GFX_MODE_ATTACK))
							setGfxMode(getGfxMode());
						else
							setGfxMode(getGfxMode());
					} else {
						setGfxMode(getGfxMode());
					}
					// 버프등록
					BuffController.append(this, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), 7200));
					toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 6082), true);
					toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), this), true);
					// 수량 변경
					getInventory().count(poly, poly.getCount() - 1, false);
				}
			}

			// 초기화.
			step = 0;
			polyTime = 0;
			// 기본 모드로 변경.
			pcrobot_mode = PCROBOT_MODE.None;
			break;
		}
	}

	private void toInventoryHeavy() {
		switch (step++) {
		case 0:
			// 마을로 이동.
			gotoHome(false);
			break;
		case 1:
			// 인벤에 아이템 삭제.
			for (ItemInstance ii : getInventory().getList()) {
				// 아데나는 무시.
				if (ii.getItem().getNameIdNumber() == 4)
					continue;
				// 착용중인 아이템 무시.
				if (ii.isEquipped())
					continue;
				// 그 외엔 다 제거.
				getInventory().remove(ii, false);
			}
			break;
		case 2:
			// 초기화.
			step = 0;
			// 기본 모드로 변경.
			pcrobot_mode = PCROBOT_MODE.None;
			break;
		}
	}

	private void toShop(String item_name, long count) {
		switch (step) {
		case 0: // 이동
			// npc 찾기.
			if (shopTemp == null) {
				gotoHome(false);
				shopTemp = RobotController.findShop(this, item_name);
				
				if (shopTemp == null)
					step = 4;
			} else {
				if (Util.isDistance(this, shopTemp, Util.random(1, 5)))
					step = 1;
				else
					toMoving(this, shopTemp.getX(), shopTemp.getY(), 0, true);
			}
			break;
		case 1: // npc 클릭
			if (shopTemp == null)
				step = 4;
			else
				shopTemp.toTalk(this, null);
			
			step = 2;
			break;
		case 2: // buy 클릭
			shopTemp.toTalk(this, "buy", null, null);
			
			step = 3;
			break;
		case 3: // 아이템선택 구매클릭
			Shop s = shopTemp.getNpc().findShopItemId(item_name, 1);
			ServerBasePacket sbp = (ServerBasePacket) ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), null);
			sbp.writeC(0); // opcode
			sbp.writeC(0); // 상점구입
			sbp.writeH(1); // 구매할 전체 갯수
			sbp.writeD(s.getUid()); // 상점 아이템 고유값
			sbp.writeD(count); // 구매 갯수.
			byte[] data = sbp.getBytes();
			BasePacketPooling.setPool(sbp);
			BasePacket bp = ClientBasePacket.clone(BasePacketPooling.getPool(ClientBasePacket.class), data, data.length);
			// 처리 요청.
			shopTemp.toDwarfAndShop(this, (ClientBasePacket) bp);
			// 메모리 재사용.
			BasePacketPooling.setPool(bp);
			
			step = 4;
			break;
		case 4:
			// 초기화.
			step = 0;
			shopTemp = null;
			// 기본 모드로 변경.
			if (Util.random(0, 99) < 10)
				pcrobot_mode = PCROBOT_MODE.Stay;
			else
				pcrobot_mode = PCROBOT_MODE.None;
			break;
		}
	}

	/**
	 * 공격자 목록에 등록처리 함수.
	 * 
	 * @param o
	 */
	public void addAttackList(object o) {
		if (!isDead() && !o.isDead() && o.getObjectId() != getObjectId()) {
			if (getClanId() > 0 && o.getClanId() > 0 && getClanId() != o.getClanId())
				// 공격목록에 추가.
				appendAttackList(o);
			else if (getClanId() == 0 || o.getClanId() == 0)
				// 공격목록에 추가.
				appendAttackList(o);
		}
	}

	/**
	 * 해당객체를 공격해도 되는지 분석하는 함수.
	 * 
	 * @param o
	 * @param walk
	 * @return
	 */
	private boolean isAttack(object o, boolean walk) {
		if (o == null)
			return false;
		if (o.getGm() > 0)
			return false;
		if (o.isDead())
			return false;
		if (o.isWorldDelete())
			return false;
		if (o.isTransparent())
			return false;
		if (!Util.isDistance(this, o, Lineage.SEARCH_WORLD_LOCATION))
			return false;
		if (o instanceof Cracker)
			return true;
		if (o instanceof TeleportInstance || o instanceof EventInstance || o instanceof InnInstance || o instanceof ShopInstance || o instanceof DwarfInstance|| o instanceof PetMasterInstance)
			return false;
		if (o instanceof PcInstance && !(o instanceof RobotInstance))
			return containsAttackList(o) || o.isBuffCriminal() || o.getLawful() < Lineage.NEUTRAL;
		if (o instanceof SummonInstance || o instanceof NpcInstance)
			return containsAttackList(o);
		if (o instanceof ItemInstance || o instanceof BackgroundInstance || o instanceof MagicDollInstance)
			return false;
		if (!(o instanceof MonsterInstance) && World.isSafetyZone(getX(), getY(), getMap()) || World.isSafetyZone(o.getX(), o.getY(), o.getMap()))
			return false;
		if (!(o instanceof MonsterInstance) && getX() == o.getX() && getY() == o.getY() && getMap() == o.getMap())
			return false;
		if (getClanId() > 0 && o.getClanId() > 0 && getClanId() == o.getClanId())
			return false;
		//
		if (o.getName().equals("$607"))
			return false;
		//
		return true;
	}

	/**
	 * 매개변수 좌표로 A스타를 발동시켜 이동시키기. 객체가 존재하는 지역은 패스하도록 함. 이동할때마다 aStar가 새로 그려지기때문에
	 * 과부하가 심함.
	 */
	private boolean toMoving(object o, final int x, final int y, final int h, final boolean astar) {
		if (o == null)
			return false;
		
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
				return true;
			} else {
				// 그외엔 에이스타 무시목록에 등록.
				if (o != null)
					appendAstarList(o);
				return false;
			}
		} else {
			toMoving(x, y, h);
			return true;
		}
	}

	/**
	 * 버프 물약 복용
	 * 
	 * @return
	 */
	private boolean toBuffPotion() {
		//
		Buff b = BuffController.find(this);
		if (b == null)
			return false;
		// 촐기 복용.
		if (b.find(HastePotionMagic.class) == null) {
			ItemInstance item = getInventory().find(HastePotion.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}
		// 용기 복용.
		if ((getClassType() == Lineage.LINEAGE_CLASS_KNIGHT || getClassType() == Lineage.LINEAGE_CLASS_ROYAL) && b.find(Bravery.class) == null) {
			ItemInstance item = getInventory().find(BraveryPotion.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}
		// 엘븐와퍼 복용.
		if (getClassType() == Lineage.LINEAGE_CLASS_ELF && b.find(Wafer.class) == null) {
			ItemInstance item = getInventory().find(ElvenWafer.class);
			if (item != null && item.isClick(this)) {
				item.toClick(this, null);
				return true;
			}
		}
		return false;
	}

	/**
	 * 체력 물약 복용.
	 */
	private boolean toHealingPotion() {
		//
		if (getHpPercent() > HEALING_PERCENT)
			return false;
		//
		ItemInstance item = getInventory().find(HealingPotion.class);
		if (item != null && item.isClick(this))
			item.toClick(this, null);
		return true;
	}
	
	/**
	 * 공격 마법.
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	private boolean toSkillAttack(object o) {
		if (o == null)
			return false;
		
		List<Skill> list = SkillController.find(this);
		ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);
		//
		if (list == null)
			return false;
		
		if (System.currentTimeMillis() < delay_magic)
			return false;
		
		for (Skill s : list) {
			SkillRobot sr = (SkillRobot) s;
			if (sr == null)
				continue;
			if (sr.getType().equalsIgnoreCase("단일공격마법") == false && sr.getType().equalsIgnoreCase("범위공격마법") == false && sr.getType().equalsIgnoreCase("디버프") == false)
				continue;
			
			if (sr.getLevel() > getLevel())
				continue;
			
			if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
				if (weapon == null)
					continue;
				
				switch (sr.getWeaponType()) {
				case "한손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
						continue;
					break;
				case "양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
						continue;
					break;
				case "한손검&양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") && !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
						continue;
					break;
				case "활":
					if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
						continue;
					break;
				}
			}
			
			if (!sr.getTarget().equalsIgnoreCase("유저&몬스터")) {
				switch (sr.getTarget()) {
				case "유저":
					if (o instanceof MonsterInstance)
						continue;
					break;
				case "몬스터":
					if (o instanceof PcInstance)
						continue;
					break;
				}
			}
			
			if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
				continue;

			if (sr.getMpConsume() > getNowMp())
				continue;
			//
			if (Math.random() < sr.getProbability()) {
				toSkill(s, o);
				return true;
			}
		}
		return false;
	}

	/**
	 * 버프스킬 시전처리.
	 * 
	 * @return
	 */
	private boolean toSkillBuff(List<Skill> list) {
		if (list == null)
			return false;
		
		ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

		for (Skill s : list) {
			SkillRobot sr = (SkillRobot) s;
			if (sr.getType().equalsIgnoreCase("버프마법") == false)
				continue;
			
			if (sr.getLevel() > getLevel())
				continue;

			if (sr.getMpConsume() > getNowMp())
				continue;

			if (sr.getUid() == 43 && BuffController.find(this, SkillDatabase.find(311)) != null)
				continue;
			//
			if (BuffController.find(this, s) != null)
				continue;
			
			if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
				if (weapon == null)
					continue;
				
				switch (sr.getWeaponType()) {
				case "한손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
						continue;
					break;
				case "양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
						continue;
					break;
				case "한손검&양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") && !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
						continue;
					break;
				case "활":
					if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
						continue;
					break;
				}
			}
			
			if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
				continue;

			if (Math.random() < sr.getProbability()) {
				toSkill(s, this);
				return true;
			}
		}
		//
		return false;
	}

	/**
	 * 소울 스킬 시전
	 * 
	 * @return
	 */
	private boolean toSkillHealMp(List<Skill> list) {
		//
		if (getNowMp() == getTotalMp())
			return false;
		//
		if (list == null)
			return false;
		
		ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);
		
		for (Skill s : list) {
			SkillRobot sr = (SkillRobot) s;
			if (sr.getType().equalsIgnoreCase("mp회복마법") == false)
				continue;
			
			if (sr.getLevel() > getLevel())
				continue;
			
			if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
				if (weapon == null)
					continue;
				
				switch (sr.getWeaponType()) {
				case "한손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
						continue;
					break;
				case "양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
						continue;
					break;
				case "한손검&양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") && !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
						continue;
					break;
				case "활":
					if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
						continue;
					break;
				}
			}
			
			if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
				continue;
			
			if (Math.random() < sr.getProbability())
				toSkill(s, this);
			return true;
		}

		return false;
	}

	/**
	 * 힐 스킬 시전
	 * 
	 * @return
	 */
	private boolean toSkillHealHp(List<Skill> list) {
		//
		if (getHpPercent() > HEALING_PERCENT)
			return false;
		//
		if (list == null)
			return false;
		
		ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);
		
		for (Skill s : list) {
			SkillRobot sr = (SkillRobot) s;
			if (sr.getType().equalsIgnoreCase("힐") == false)
				continue;
			
			if (sr.getLevel() > getLevel())
				continue;

			if (sr.getMpConsume() > getNowMp())
				continue;
			
			if (!sr.getWeaponType().equalsIgnoreCase("모든무기")) {
				if (weapon == null)
					continue;
				
				switch (sr.getWeaponType()) {
				case "한손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") || weapon.getItem().isTohand())
						continue;
					break;
				case "양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("tohandsword") || !weapon.getItem().isTohand())
						continue;
					break;
				case "한손검&양손검":
					if (!weapon.getItem().getType2().equalsIgnoreCase("sword") && !weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
						continue;
					break;
				case "활":
					if (!weapon.getItem().getType2().equalsIgnoreCase("bow"))
						continue;
					break;
				}
			}
			
			if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
				continue;
			
			if (Math.random() < sr.getProbability())
				toSkill(s, this);
			return true;
		}
		return false;
	}

	/**
	 * 서먼 스킬 시전.
	 * 
	 * @return
	 */
	private boolean toSkillSummon(List<Skill> list) {
		//
		if (list == null)
			return false;

		for (Skill s : list) {
			SkillRobot sr = (SkillRobot) s;
			if (sr.getType().equalsIgnoreCase("서먼몬스터") == false)
				continue;
			
			if (sr.getLevel() > getLevel())
				continue;
			
			if (sr.getMpConsume() > getNowMp())
				continue;
			
			if (sr.getAttribute() > 0 && getAttribute() != sr.getAttribute())
				continue;
			
			if (Math.random() < sr.getProbability() && SummonController.isAppend(SummonListDatabase.summon(this, 0), this, getClassType() == Lineage.LINEAGE_CLASS_WIZARD ? TYPE.MONSTER : TYPE.ELEMENTAL)) {
				toSkill(s, this);
				SummonController.find(this).setMode(SummonInstance.SUMMON_MODE.AggressiveMode);
				return true;
			}
		}
		return false;
	}

	/**
	 * 서먼한 객체에게 버프를 시전함.
	 * 
	 * @return
	 */
	private boolean toBuffSummon() {
		//
		Summon s = SummonController.find(this);
		if (s == null || s.getSize() == 0)
			return false;
		//
		for (object o : s.getList()) {
			Buff b = BuffController.find(o);
			// 헤이스트
			if (b == null || b.find(Haste.class) == null) {

				Skill haste = SkillController.find(this, 6, 2);
				if (haste != null && haste.getMpConsume() <= getNowMp()) {
					toSkill(haste, o);
					return true;
				}
			}
			// 힐
			Character cha = (Character) o;
			if (cha.getHpPercent() <= HEALING_PERCENT) {
				int[][] heal_list = { { 1, 0 }, // 힐
						{ 3, 2 }, // 익스트라 힐
						{ 5, 2 }, // 그레이터 힐
						{ 7, 0 }, // 힐 올
						{ 8, 0 }, // 풀 힐
						{ 20, 5 }, // 네이처스 터치
				};
				for (int[] data : heal_list) {
					Skill heal = SkillController.find(this, data[0], data[1]);
					if (heal != null && heal.getMpConsume() <= getNowMp()) {
						toSkill(heal, o);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 중복코드 방지용.
	 * 
	 * @param s
	 */
	private void toSkill(Skill s, object o) {
		ServerBasePacket sbp = (ServerBasePacket) ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), null);
		sbp.writeC(0); // opcode
		sbp.writeC(s.getSkillLevel() - 1); // level
		sbp.writeC(s.getSkillNumber()); // number
		sbp.writeD(o.getObjectId()); // objId
		sbp.writeH(o.getX()); // x좌표
		sbp.writeH(o.getY()); // y좌표
		byte[] data = sbp.getBytes();
		BasePacketPooling.setPool(sbp);
		BasePacket bp = ClientBasePacket.clone(BasePacketPooling.getPool(ClientBasePacket.class), data, data.length);
		SkillController.toSkill(this, (ClientBasePacket) bp);
		// 메모리 재사용.
		BasePacketPooling.setPool(bp);
	}

	/**
	 * 근처 마을로 귀환.
	 */
	private void gotoHome(boolean isCracker) {
		if (!LocationController.isTeleportVerrYedHoraeZone(this, true))
			return;
		
		// 이미 마을일경우 무시.
		if (!isCracker && World.isGiranHome(getX(), getY(), getMap()))
			return;
	
		clearAttackList();
		clearAstarList();
		
		int[] home = null;
		home = Lineage.getHomeXY();
		setHomeX(home[0]);
		setHomeY(home[1]);
		setHomeMap(home[2]);
		
		toTeleport(getHomeX(), getHomeY(), getHomeMap(), isDead() == false);
	}

	/**
	 * 로봇과 전투중인 객체 목록에서 위험순위 높은거를 먼저 찾아서 리턴. pc를 1순위 나머지는 가까운 거리대로
	 * 
	 * @return
	 */
	private object findDangerousObject() {
		object o = null;

		// pc사용자를 1순위.
		for (object oo : getAttackList()) {
			if (oo == null)
				continue;
			if (oo instanceof PcInstance) {
				if (!containsAstarList(oo)) {
					if (!Util.isAreaAttack(this, oo) || !isAttack(oo, false))
						continue;
					if (o == null)
						o = oo;
					else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
						o = oo;
				}
			}
		}

		if (o != null)
			return o;

		if (pcrobot_mode == PCROBOT_MODE.Cracker) {
			// 찾지 못했다면 공격자목록에 등록된 객체에서 찾기.
			for (object oo : getAttackList()) {
				if (oo == null)
					continue;
				if (o == null)
					o = oo;
				else if (Util.getDistance(this, oo) < Util.getDistance(this, o) && Util.isAreaAttack(this, oo) && Util.isAreaAttack(oo, this))
					o = oo;
			}
		} else {
			// 찾지 못했다면 공격자목록에 등록된 객체에서 찾기.
			for (object oo : getAttackList()) {
				if (oo == null)
					continue;
				if (!containsAstarList(oo)) {
					if (!Util.isAreaAttack(this, oo) || !isAttack(oo, false))
						continue;
					if (o == null)
						o = oo;
					else if (Util.getDistance(this, oo) < Util.getDistance(this, o))
						o = oo;
				}
			}
		}	
		
		return o;
	}

	/**
	 * 클레스별로 변신할 이름 리턴.
	 * 
	 * @return
	 */
	private String getPolymorph() {
		RobotPoly rp = null;

		if (RobotController.getPolyList().size() < 1)
			return "";
		
		for (int i = 0; i < 200; i++) {
			rp = RobotController.getPolyList().get(Util.random(0, RobotController.getPolyList().size() - 1));

			if (rp != null && rp.getPoly().getMinLevel() <= getLevel() && SpriteFrameDatabase.findGfxMode(rp.getPoly().getGfxId(), getGfxMode() + Lineage.GFX_MODE_ATTACK)) {
				switch (rp.getPolyClass()) {
				case "모든클래스":
					return rp.getPoly().getName();
				case "군주":
					if (getClassType() == Lineage.LINEAGE_CLASS_ROYAL)
						return rp.getPoly().getName();
					else
						continue;
				case "기사":
					if (getClassType() == Lineage.LINEAGE_CLASS_KNIGHT)
						return rp.getPoly().getName();
					else
						continue;
				case "요정":
					if (getClassType() == Lineage.LINEAGE_CLASS_ELF)
						return rp.getPoly().getName();
					else
						continue;
				case "마법사":
					if (getClassType() == Lineage.LINEAGE_CLASS_WIZARD)
						return rp.getPoly().getName();
					else
						continue;
				case "군주&기사&마법사":
					if (getClassType() == Lineage.LINEAGE_CLASS_ROYAL || getClassType() == Lineage.LINEAGE_CLASS_KNIGHT || getClassType() == Lineage.LINEAGE_CLASS_WIZARD)
						return rp.getPoly().getName();
					else
						continue;
				}
			}
			continue;
		}
		
		return "";
	}
	
	/**
	 * 화살 장착 메소드.
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	private void setArrow() {
		if (getInventory() != null && getInventory().find(Arrow.class) != null) {
			if (!getInventory().find(Arrow.class).equipped)
				getInventory().find(Arrow.class).toClick(this, null);
		}
	}
	
	/**
	 * 인벤토리 셋팅 메소드.
	 * 2018-08-11
	 * by connector12@nate.com
	 */
	private void setInventory() {
		if (Lineage.robot_auto_pc && (this.getWeapon_name() != null || RobotController.getWeapon(getClassType()) != null)) {
			if (this.getWeapon_name() != null)
				weapon = ItemDatabase.find(this.getWeapon_name());
			else
				weapon = RobotController.getWeapon(getClassType());
			
			ItemInstance item = ItemDatabase.newInstance(weapon);
			item.setObjectId(ServerDatabase.nextEtcObjId());
			item.setEnLevel(weaponEn);
			getInventory().append(item, false);
			
			item.toClick(this, null);
		}
		
		if (Lineage.robot_auto_pc && this.getDoll_name() != null) {
			doll = ItemDatabase.find(this.getDoll_name());

			ItemInstance item = ItemDatabase.newInstance(doll);
			if (item != null) {
				item.setObjectId(ServerDatabase.nextEtcObjId());
				getInventory().append(item, false);
				item.toClick(this, null);
			}
		}
		
		if (Lineage.robot_auto_pc) {
			Item i = ItemDatabase.find(RobotController.getHealingPotion(this));
			
			if (i != null) {
				ItemInstance item = ItemDatabase.newInstance(i);
				item.setObjectId(ServerDatabase.nextEtcObjId());
				item.setCount(RobotController.getHealingPotionCnt());
				getInventory().append(item, false);
			}
		}
		
		if (Lineage.robot_auto_pc) {
			Item i = ItemDatabase.find(RobotController.getHastePotion(this));
			
			if (i != null) {
				ItemInstance item = ItemDatabase.newInstance(i);
				item.setObjectId(ServerDatabase.nextEtcObjId());
				item.setCount(RobotController.getHastePotionCnt());
				getInventory().append(item, false);
			}
		}
		
		if (Lineage.robot_auto_pc) {
			Item i = ItemDatabase.find(RobotController.getBraveryPotion(this));
			
			if (i != null) {
				ItemInstance item = ItemDatabase.newInstance(i);
				item.setObjectId(ServerDatabase.nextEtcObjId());
				item.setCount(RobotController.getBraveryPotionCnt());
				getInventory().append(item, false);
			}
		}
		
		if (Lineage.robot_auto_pc) {
			Item i = ItemDatabase.find(RobotController.getElvenWafer(this));
			
			if (i != null) {
				ItemInstance item = ItemDatabase.newInstance(i);
				item.setObjectId(ServerDatabase.nextEtcObjId());
				item.setCount(RobotController.getElvenWaferCnt());
				getInventory().append(item, false);
			}
		}
		
		if (Lineage.robot_auto_pc) {
			Item i = ItemDatabase.find(RobotController.getScrollPolymorph(this));
			
			if (i != null) {
				ItemInstance item = ItemDatabase.newInstance(i);
				item.setObjectId(ServerDatabase.nextEtcObjId());
				item.setCount(RobotController.getScrollPolymorphCnt());
				getInventory().append(item, false);
			}
		}
		
		if (Lineage.robot_auto_pc) {
			Item i = ItemDatabase.find(RobotController.getArrow(this));
			
			if (i != null) {
				ItemInstance item = ItemDatabase.newInstance(i);
				item.setObjectId(ServerDatabase.nextEtcObjId());
				item.setCount(RobotController.getArrowCnt());
				getInventory().append(item, false);
			}
		}
	}
	
	/**
	 * 서버 오픈대기일 경우 처리.
	 * 2018-08-12
	 * by connector12@nate.com
	 */
	private boolean isWait() {
		gotoHome(false);

		if (Util.random(0, 99) < 50) {
			pcrobot_mode = PCROBOT_MODE.Stay;
		} else {
			do {
				// 이동 좌표 추출.
				int x = Util.getXY(getHeading(), true) + getX();
				int y = Util.getXY(getHeading(), false) + getY();

				// 해당 좌표 이동가능한지 체크.
				boolean tail = World.isThroughObject(getX(), getY(), getMap(), getHeading()) && World.isMapdynamic(x, y, map) == false;
				// 타일이 이동가능하고 객체가 방해안하면 이동처리.
				if (tail && Util.random(0, 99) < 5) {
					toMoving(null, x, y, getHeading(), false);
				} else {
					if (Util.random(0, 99) < 10)
						setHeading(Util.random(0, 7));

					continue;
				}
			} while (false);
		}
		return true;
	}
	
	/**
	 * 허수아비 공격 또는 마을 대기.
	 * 2018-09-14
	 * by connector12@nate.com
	 */
	private void attackCracker() {
		gotoHome(false);

		pcrobot_mode = PCROBOT_MODE.Cracker;
		clearAttackList();
		clearAstarList();

		boolean isCracker = false;
		for (object cracker : BackgroundDatabase.getCrackerList()) {
			addAttackList(cracker);
			isCracker = true;
		}
		
		if (isCracker)
			setAiStatus(Lineage.AI_STATUS_WALK);

		if (getAttackListSize() < 1)
			isWait();
	}
	
	/**
	 * 사냥 가능한 맵 체크.
	 * 2018-09-14
	 * by connector12@nate.com
	 */
	public boolean isPossibleMap() {
		List<Book> list = BookController.find(this);
		
		if (list.size() < 1)
			return false;
		
		for (Book b : list) {
			if (b.getMinLevel() <= getLevel())
				return true;
		}
		
		return false;
	}
}
