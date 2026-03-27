package lineage.world.object;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Monster;
import lineage.bean.database.Npc;
import lineage.bean.database.Poly;
import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Summon;
import lineage.bean.lineage.Trade;
import lineage.database.ItemDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Door;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectMap;
import lineage.network.packet.server.S_ObjectMoving;
import lineage.network.packet.server.S_ObjectRemove;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.EventController;
import lineage.world.controller.FightController;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.DwarfInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MagicDollInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.ShopInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.instance.TeleportInstance;
import lineage.world.object.item.all_night.CharacterSaveMarble;
import lineage.world.object.item.all_night.ClassChangeTicket;
import lineage.world.object.monster.FloatingEye;
import lineage.world.object.monster.event.JackLantern;
import lineage.world.object.npc.background.BackgroundTile;
import lineage.world.object.npc.background.DeathEffect;
import lineage.world.object.npc.background.Firewall;
import lineage.world.object.npc.background.LifeStream;
import lineage.world.object.npc.background.Racer;
import lineage.world.object.npc.background.RestCracker;
import lineage.world.object.npc.background.Switch;
import lineage.world.object.npc.background.door.Door;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomDoorman;
import lineage.world.object.npc.kingdom.KingdomGuard;

public class object {
	private List<object> insideList; // 12셀 내에있는 객체만 관리할 용도로 사용
	private List<object> allList; // 40셀 내에 있는 객체만 관리할 용도로 사용.
	protected long objectId;
	protected String name;
	protected String title;
	protected int clanId;
	protected int clanGrade;
	protected String clanName;
	protected object own; // 객체를 관리하고 있는 객체
	protected long own_objectId; // 객체를 관리하고 있는 객체의 아이디
	protected String own_name; // 객체를 관리하고 있는 객체의 이름
	protected int x;
	protected int y;
	protected int map;
	protected int homeX;
	protected int homeY;
	protected int homeMap;
	protected int homeLoc;
	protected int homeHeading;
	protected int homeTile[]; // Door 객체에서 사용중. 스폰된 위치에 고유 타일값 기록을 위해.
	protected int tempX;
	protected int tempY;
	protected int gfx;
	protected int gfxMode;
	protected int classGfx;
	protected int classGfxMode;
	private int lawful;
	protected int heading; // 0~7
	protected int light; // 0~15
	private int speed; // 0~2
	private boolean brave; // 용기 및 와퍼 상태
	protected boolean eva; // 에바의 축복 상태
	protected boolean wisdom; // 지혜의 물약 상태
	private long count;
	protected int tempCount; //
	protected int classSex; // 0~1
	protected int classType; // pc[0~3]
	private boolean dead;
	protected boolean fight;
	protected boolean moving;
	private boolean poison; // 독감염 여부.
	private boolean lock_high; // 굳은상태 여부. 데미지 입지 않음.
	private boolean lock_low; // 굳은상태 여부. 데미지 입음.
	private boolean invis; // 투명상태 여부.
	protected boolean transparent; // 절대적 투명상태 여부. 객체를 뚫을 수 있음. 몬스터도사용중(잭-오-랜턴)
	protected boolean worldDelete = true;//
	protected object temp_object_1; // 임시 저장용 변수.
	protected int temp_hp;
	protected int temp_mp;
	private int CurseParalyzeCounter; // 커스:패럴라이즈가 실제 동작되는 시점 체크를 위해 사용하는 변수.
	protected Summon summon; // 서먼 객체 관리 변수. 자신이 해당 서먼객체 소속일경우 사용하는 변수.
	private boolean nameHidden; // 이름 및 타이틀을 표현할지 여부.
	private boolean isMapHack; // 맵핵체크를 위한 변수.
	private boolean monhitdmg; // 몬스터 회피율 체크
	private boolean monexp; // 몬스터 경험치 체크
	public int lastRank; // 랭킹 채팅 시스템을 위한 변수
	public boolean isMark; // 마크를 위한 변수


	private boolean persnalShopSellInsert;
	private boolean persnalShopSellEdit;
	private boolean persnalShopSellPriceSetting;
	private boolean persnalShopSellPriceReSetting;
	private boolean persnalShopPurchasePriceIn;;

	private boolean persnalShopEdit;
	private boolean persnalShopInsert;
	private boolean persnalShopPriceSetting;
	private boolean persnalShopAddPriceSetting;
	private boolean persnalShopPriceReset;
	
	private long _delaytime = 0;
	
	public int trueTargetTime;
	
	// 락용.
	private Object sync_ai = new Object();
	protected Object sync_dynamic = new Object(); // 변수 조작시 동기화용.
	public Object sync_pickup = new Object();

	// 인공지능 처리 변수
	protected long ai_time;
	private long ai_start_time;
	protected int ai_status; // 인공지능제거[-1] 기본[0] 등등....
	protected long ai_showment_time; // 멘트 발생된 마지막 시간값 임시 저장용.
	protected int ai_showment_idx; // 출력된 멘트 위치 저장 변수.
	protected boolean ai_showment; // 멘트 발생여부.
	public double debug_idx;
	public int thread_uid; //

	// 버프관리 쪽
	private boolean BuffArmorBreak;
	private boolean BuffDecreaseWeight;
	private boolean BuffHolyWeapon;
	private boolean BuffEnchantWeapon;
	private boolean BuffMonsterEyeMeat;
	private boolean BuffCurseParalyze;
	private boolean BuffBlessWeapon;
	private boolean BuffInvisiBility;
	private boolean BuffImmuneToHarm;
	private boolean BuffDecayPotion;
	private boolean BuffAbsoluteBarrier;
	private boolean BuffGlowingAura;
	private boolean BuffFireWeapon;
	private boolean BuffWindShot;
	private boolean BuffEraseMagic;
	private boolean BuffBurningWeapon;
	private boolean BuffStormShot;
	private boolean BuffWisdom;
	private boolean BuffEva;
	private boolean BuffBlessOfFire;
	private boolean BuffCurseGhoul;
	private boolean BuffCurseFloatingEye;
	private boolean BuffChattingClose;
	private boolean BuffChattingClosetwo;
	private boolean BuffDisease;
	private boolean BuffSilence;
	private boolean BuffEyeOfStorm;
	private boolean BuffWeakness;
	private boolean BuffCounterMagic;
	private boolean BuffCriminal;
	private boolean BuffMeditation;
	private boolean BuffFogOfSleeping;
	private boolean BuffEnchantVenom;
	private boolean BuffBurningSpirit;
	private boolean BuffVenomResist;
	private boolean BuffDoubleBreak;
	private boolean BuffShadowFang;
	private boolean BuffBerserks;
	private boolean BuffNaturesTouch;
	private int BuffAdvanceSpiritHp;
	private int BuffAdvanceSpiritMp;
	private boolean BuffCounterBarrier;
	private boolean BuffCounterMirror;
	private boolean BuffBounceAttack;
	private boolean BuffCurseBlind;
	private boolean BuffExoticVitalize;
	private boolean BuffWaterLife;
	private boolean BuffElementalFire;
	private boolean BuffPolluteWater;
	private boolean BuffStrikerGale;
	private boolean BuffMfire;
	private boolean BuffSoulOfFlame;
	private boolean BuffAdditionalFire;
	private boolean BuffBraveMental;
	private boolean BuffBraveAvatar;
	private boolean BuffBluePotion;
	private boolean BuffExpPotion;
	private boolean BuffMaanFire;
	private boolean BuffMaanEarth;
	private boolean BuffMaanWatar;
	private boolean BuffMaanWind;
	private boolean BuffMaanLife;
	private boolean BuffMaanBirth;
	private boolean BuffMaanShape;

	// 1단계 마법인형
	private boolean MagicdollStoneGolem;
	private boolean MagicdollWerewolf;
	private boolean MagicdollBugBear;
	private boolean MagicdollHermitCrab;
	private boolean MagicdollYeti;
	private boolean MagicdollBasicWood;
	// 2단계 마법인형
	private boolean Magicdollsuccubus;
	private boolean MagicdollElder;
	private boolean MagicdollCockatrice;
	private boolean MagicdollSnowMan;
	private boolean MagicdollMermaid;
	private boolean MagicdollLavaGolem;
	// 3단계 마법인형
	private boolean MagicdollGiant;
	private boolean MagicdollBlackElder;
	private boolean MagicdollsuccubusQueen;
	private boolean MagicdollDrake;
	private boolean MagicdollKingBugBear;
	private boolean MagicdollDiamondGolem;
	// 4단계 마법인형
	private boolean MagicdollRich;
	private boolean MagicdollCyclops;
	private boolean MagicdollKnightVald;
	private boolean MagicdollSeer;
	private boolean MagicdollIris;
	private boolean MagicdollVampire;
	private boolean MagicdollMummylord;
	// 5단계 마법인형
	private boolean MagicdollDemon;
	private boolean MagicdollDeathKnight;
	private boolean MagicdollBaranka;
	private boolean MagicdollTarak;
	private boolean MagicdollBaphomet;
	private boolean MagicdollIceQueen;
	private boolean MagicdollKouts;
	private boolean MagicdollAntaras;
	private boolean MagicdollPapoorion;
	private boolean MagicdollLindvior;
	private boolean MagicdollValakas;

	// 메디테이션이 지속될수록 MP회복량 수치 증가를 위한 변수
	private int buffMeditaitonLevel;

	// 크리티컬 이팩트 
	private boolean criticalEffect;
	private boolean criticalMagicEffect;
	
	// 허수아비 대미지 멘트
	private boolean damageMassage;
	
	// 전투 멘트
	private boolean isWarMessage;

	// 디비처리에 사용되는 변수.
	private Object database_key; // npc_spawnlist(name), monster_spawnlist(uid)
	
	// 힐올, 네이쳐스 블레싱 중복 적용을 위한 변수
	public long lastHealTime;
	// 미티어 스트라이크 중복 대미지 적용을 위한 변수
	public long lastDamageMeteorStrike;
	// 디스인티그레이트 중복 대미지 적용을 위한 변수
	public long lastDamageThisTime;
	
	// 안타라스의 마갑주
	private boolean isAntarasArmor;
	// 파푸리온의 마갑주
	private boolean isFafurionArmor;
	// 린드비오르의 마갑주
	private boolean isLindviorArmor;
	// 발라카스의 마갑주
	private boolean isValakasArmor;
	
	// 운영자 귓말 켬, 끔
	public boolean isGmWhisper;
	
	// 자동 칼질
	public boolean isAutoAttack;
	public object autoAttackTarget;
	public long autoAttackTime;
	public int targetX;
	public int targetY;
	
	// 환생 횟수.
	private int evolutionCount;
	
	// 자동물약
	public boolean isAutoPotion;
	public String[] autoPotionIdx;
	public String autoPotionName;
	public int autoPotionPercent;
	//자동사냥 스킬
	public int autoMPPercent;
	public int autoMPPercent2;
	// DPS
	public long dps_attack_time;
	
	// 기사 스킬 적중
	private double knightSkillHit;

	// 요정 스킬 적중
	private double elfSkillHit;
	
	// 세트 변신 여부
	public boolean isSetPoly;
	
	// 잠수용 허수아비
	public boolean isRestCracker;
	public RestCracker restcCracker;
	public long restCrackerDelay;
	
	// 전체 채팅 매크로
	public boolean isMacro;
	public String macroMsg;
	public int macroDelay;
	
	// 데미지 확인
	public boolean isDmgCheck;
	
	// 추가 드랍률
	private double addDropItemRate;
	// 추가 아덴 획득률
	private double addDropAdenRate;
	
	// 장인 주문서 사용 횟수.
	public int scrollWeaponCount;
	
	public boolean isAddHp;
	public boolean isAddMp;
	
	public boolean isShopMent = true;
	
	// 칼렉 변수
	public long lastDamageActionTime;
	
	// 마지막 움직인 시간
	public long lastMovingTime;
	
	// 무기 이펙트 명령어
	public boolean 무기이펙트 = true;
	
	// 동기화용 변수.
	protected Object sync_lawful = new Object();
	protected Object sync_dead = new Object();
	protected Object sync_speed = new Object();
	protected Object sync_brave = new Object();
	protected Object sync_count = new Object();
	protected Object sync_invis = new Object();
	protected Object sync_poison = new Object();
	protected Object sync_lock_low = new Object();
	protected Object sync_lock_high = new Object();
	
	public long tripleAttackObjId;
	public long tripleDamageTime;
	
	// 스피드핵 처리 참고용 변수
	public long AttackFrameTime;
	public long WalkFrameTime;
	public int SpeedhackWarningCounting;
	public int badPacketCount;
	public int goodPacketCount;
	public int gostHackCount;
	public int halfDelayCount;
	public long lastActionTime;
	public long lastMagicActionTime;
	public long lastMagicAttTime;
	// 촐기 용기 풀릴때 스핵감지 안되게 체크.
	public long speedCheck;
	
	private PcInstance itemCheckPc;
	
	private ClassChangeTicket classChangeScroll;
	private String classChangeType;

	// 인첸트 복구
	public long[] enchantRecovery;
	
	//눈빛어피치
	public long[] DeadRecovery;
	
	private CharacterSaveMarble characterMarble;

	private int exp_marble_save_count;
	private int exp_marble_use_count;
	
	public Trade trade;

	public object() {
		insideList = new ArrayList<object>();
		allList = new ArrayList<object>();
		homeTile = new int[2];

		close();
	}

	/**
	 * 사용 다된 객체에 대한 메모리 정리 함수.
	 */
	public void close() {
		name = title = clanName = own_name = null;
		temp_object_1 = null;
		summon = null;
		own_objectId = objectId = x = y = map = clanId = homeX = homeY = homeMap = gfx = gfxMode = classGfx = classGfxMode = lawful = heading = light = speed = classSex = classType = homeHeading = tempCount = buffMeditaitonLevel = 0;
		ai_start_time = ai_time = ai_status = 0;
		count = 1;
		wisdom = eva = brave = dead = fight = moving = poison = lock_low = lock_high = invis = transparent = damageMassage = isWarMessage = isMapHack = isAntarasArmor = isFafurionArmor = isLindviorArmor = isValakasArmor = isMark = monhitdmg=monexp=false;
		BuffDecreaseWeight = BuffHolyWeapon = BuffEnchantWeapon = BuffMonsterEyeMeat = BuffCurseParalyze = BuffBlessWeapon = BuffInvisiBility = BuffImmuneToHarm = BuffDecayPotion = BuffAbsoluteBarrier = BuffGlowingAura = BuffFireWeapon = BuffWindShot = BuffEraseMagic = BuffBurningWeapon = BuffStormShot = BuffWisdom = BuffEva = BuffBlessOfFire = BuffCurseGhoul = BuffCurseFloatingEye = BuffChattingClose = BuffChattingClosetwo = BuffDisease = BuffSilence = BuffEyeOfStorm = BuffWeakness = BuffCounterMagic = BuffCriminal = BuffMeditation = BuffFogOfSleeping = BuffEnchantVenom = BuffBurningSpirit = BuffVenomResist = BuffDoubleBreak = BuffShadowFang = BuffBerserks = BuffNaturesTouch = BuffCounterBarrier = BuffCounterMirror = nameHidden = BuffBounceAttack = BuffCurseBlind = BuffExoticVitalize = BuffWaterLife = BuffElementalFire = BuffPolluteWater = BuffStrikerGale = BuffMfire = BuffSoulOfFlame = BuffAdditionalFire = BuffBraveMental = BuffBraveAvatar  = BuffBluePotion = criticalEffect = criticalMagicEffect = BuffExpPotion = false;
		BuffAdvanceSpiritHp = BuffAdvanceSpiritMp = thread_uid = lastRank = 0;
		temp_hp = temp_mp = -1;
		worldDelete = true;
		own = null;
		database_key = null;
		MagicdollAntaras = MagicdollPapoorion = MagicdollLindvior = MagicdollValakas = MagicdollStoneGolem = MagicdollWerewolf = MagicdollBugBear = MagicdollHermitCrab = MagicdollYeti = MagicdollBasicWood = Magicdollsuccubus = MagicdollElder = MagicdollCockatrice = MagicdollSnowMan = MagicdollMermaid = MagicdollLavaGolem = MagicdollGiant = MagicdollBlackElder = MagicdollsuccubusQueen = MagicdollDrake = MagicdollKingBugBear = MagicdollDiamondGolem = MagicdollRich = MagicdollCyclops = MagicdollKnightVald = MagicdollSeer = MagicdollIris = MagicdollVampire = MagicdollMummylord = MagicdollDemon = MagicdollDeathKnight = MagicdollBaranka = MagicdollTarak = MagicdollBaphomet = MagicdollIceQueen = MagicdollKouts = false;
		if (insideList != null) {
			synchronized (insideList) {
				insideList.clear();
			}
		}
		if (allList != null) {
			synchronized (allList) {
				allList.clear();
			}
		}
		
		lastDamageThisTime = lastDamageMeteorStrike = lastHealTime = 0L;
		isGmWhisper = false;
		
		isAutoAttack = BuffArmorBreak = false;
		autoAttackTarget = null;
		autoAttackTime = 0L;
		targetX = targetY = 0;

		evolutionCount = 0;
		
		speedCheck = 0L;
		
		isAutoPotion = false;
		autoPotionIdx = null;
		autoPotionName = null;
		autoPotionPercent = 0;
		autoMPPercent= 0;
		autoMPPercent2= 0;
		dps_attack_time = 0L;
		
		knightSkillHit = elfSkillHit = 0;
		
		isSetPoly = false;
		
		isRestCracker = false;
		restcCracker = null;
		restCrackerDelay = 0L;
		
		isMacro = false;
		macroMsg = null;
		macroDelay = 0;
		
		isDmgCheck = false;
		
		BuffMaanFire = BuffMaanEarth = BuffMaanWatar = BuffMaanWind = BuffMaanLife = BuffMaanBirth = BuffMaanShape = false;
		
		addDropItemRate = addDropAdenRate = 0;
		
		scrollWeaponCount = 0;
		
		isAddHp = isAddMp = false;
		
		isShopMent = true;
		
		lastDamageActionTime = 0L;
		
		lastMovingTime = 0L;
		
		무기이펙트 = true;
		
		tripleAttackObjId = 0;
		tripleDamageTime = 0L;
		
		SpeedhackWarningCounting = 0;
		AttackFrameTime = 0;
		WalkFrameTime = 0;
		badPacketCount = 0;
		goodPacketCount = 0;
		gostHackCount = 0;
		halfDelayCount = 0;
		lastActionTime = 0L;
		lastMagicActionTime = 0L;
		itemCheckPc = null;
		
		enchantRecovery = null;
		
		DeadRecovery  = null;
		
		classChangeScroll = null;
		classChangeType = null;
		
		characterMarble = null;
		
		exp_marble_save_count = exp_marble_use_count = 0;
		
		_delaytime = 0L;
		
		trade = null;
		
		trueTargetTime = 0;
		
		// 적용된 버프가 있을수 있으므로.
		BuffController.toWorldOut(this);
	}
	
	public boolean isPersnalShopInsert() {
		return persnalShopInsert;
	}

	public void setPersnalShopInsert(boolean persnalShopInsert) {
		this.persnalShopInsert = persnalShopInsert;
	}

	public boolean isPersnalShopPriceSetting() {
		return persnalShopPriceSetting;
	}

	public void setPersnalShopPriceSetting(boolean persnalShopPriceSetting) {
		this.persnalShopPriceSetting = persnalShopPriceSetting;
	}

	public boolean isPersnalShopEdit() {
		return persnalShopEdit;
	}

	public void setPersnalShopEdit(boolean persnalShopEdit) {
		this.persnalShopEdit = persnalShopEdit;
	}

	public boolean isPersnalShopAddPriceSetting() {
		return persnalShopAddPriceSetting;
	}

	public void setPersnalShopAddPriceSetting(boolean persnalShopAddPriceSetting) {
		this.persnalShopAddPriceSetting = persnalShopAddPriceSetting;
	}

	public boolean isPersnalShopPriceReset() {
		return persnalShopPriceReset;
	}

	public void setPersnalShopPriceReset(boolean persnalShopPriceReset) {
		this.persnalShopPriceReset = persnalShopPriceReset;
	}

	public boolean isPersnalShopSellInsert() {
		return persnalShopSellInsert;
	}

	public void setPersnalShopSellInsert(boolean persnalShopSellInsert) {
		this.persnalShopSellInsert = persnalShopSellInsert;
	}

	public boolean isPersnalShopSellEdit() {
		return persnalShopSellEdit;
	}

	public void setPersnalShopSellEdit(boolean persnalShopSellEdit) {
		this.persnalShopSellEdit = persnalShopSellEdit;
	}

	public boolean isPersnalShopSellPriceSetting() {
		return persnalShopSellPriceSetting;
	}

	public void setPersnalShopSellPriceSetting(boolean persnalShopSellPriceSetting) {
		this.persnalShopSellPriceSetting = persnalShopSellPriceSetting;
	}

	public boolean isPersnalShopSellPriceReSetting() {
		return persnalShopSellPriceReSetting;
	}

	public void setPersnalShopSellPriceReSetting(boolean persnalShopSellPriceReSetting) {
		this.persnalShopSellPriceReSetting = persnalShopSellPriceReSetting;
	}

	public boolean isPersnalShopPurchasePriceIn() {
		return persnalShopPurchasePriceIn;
	}

	public void setPersnalShopPurchasePriceIn(boolean persnalShopPurchasePriceIn) {
		this.persnalShopPurchasePriceIn = persnalShopPurchasePriceIn;
	}

	public boolean isBuffArmorBreak() {
		return BuffArmorBreak;
	}

	public void setBuffArmorBreak(boolean buffArmorBreak) {
		BuffArmorBreak = buffArmorBreak;
	}
	public int getExp_marble_save_count() {
		return exp_marble_save_count;
	}

	public void setExp_marble_save_count(int exp_marble_save_count) {
		this.exp_marble_save_count = exp_marble_save_count;
	}
	
	public int getExp_marble_use_count() {
		return exp_marble_use_count;
	}

	public void setExp_marble_use_count(int exp_marble_use_count) {
		this.exp_marble_use_count = exp_marble_use_count;
	}

	public CharacterSaveMarble getCharacterMarble() {
		return characterMarble;
	}

	public void setCharacterMarble(CharacterSaveMarble characterMarble) {
		this.characterMarble = characterMarble;
	}
	
	public String getClassChangeType() {
		return classChangeType;
	}

	public void setClassChangeType(String classChangeType) {
		this.classChangeType = classChangeType;
	}
	
	public ClassChangeTicket getClassChangeScroll() {
		return classChangeScroll;
	}

	public void setClassChangeScroll(ClassChangeTicket classChangeScroll) {
		this.classChangeScroll = classChangeScroll;
	}
	
	public PcInstance getItemCheckPc() {
		return itemCheckPc;
	}

	public void setItemCheckPc(PcInstance itemCheckPc) {
		this.itemCheckPc = itemCheckPc;
	}
	
	public double getAddDropItemRate() {
		return addDropItemRate;
	}

	public void setAddDropItemRate(double addDropItemRate) {
		this.addDropItemRate = addDropItemRate;
	}

	public double getAddDropAdenRate() {
		return addDropAdenRate;
	}

	public void setAddDropAdenRate(double addDropAdenRate) {
		this.addDropAdenRate = addDropAdenRate;
	}
	
	public boolean isBuffMaanFire() {
		return BuffMaanFire;
	}

	public void setBuffMaanFire(boolean buffMaanFire) {
		BuffMaanFire = buffMaanFire;
	}

	public boolean isBuffMaanEarth() {
		return BuffMaanEarth;
	}

	public void setBuffMaanEarth(boolean buffMaanEarth) {
		BuffMaanEarth = buffMaanEarth;
	}

	public boolean isBuffMaanWatar() {
		return BuffMaanWatar;
	}

	public void setBuffMaanWatar(boolean buffMaanWatar) {
		BuffMaanWatar = buffMaanWatar;
	}

	public boolean isBuffMaanWind() {
		return BuffMaanWind;
	}

	public void setBuffMaanWind(boolean buffMaanWind) {
		BuffMaanWind = buffMaanWind;
	}

	public boolean isBuffMaanLife() {
		return BuffMaanLife;
	}

	public void setBuffMaanLife(boolean buffMaanLife) {
		BuffMaanLife = buffMaanLife;
	}
	
	public boolean isBuffMaanBirth() {
		return BuffMaanBirth;
	}

	public void setBuffMaanBirth(boolean buffMaanBirth) {
		BuffMaanBirth = buffMaanBirth;
	}

	public boolean isBuffMaanShape() {
		return BuffMaanShape;
	}

	public void setBuffMaanShape(boolean buffMaanShape) {
		BuffMaanShape = buffMaanShape;
	}
	
	public double getKnightSkillHit() {
		return knightSkillHit;
	}

	public void setKnightSkillHit(double knightSkillHit) {
		this.knightSkillHit = knightSkillHit;
	}

	public double getElfSkillHit() {
		return elfSkillHit;
	}

	public void setElfSkillHit(double elfSkillHit) {
		this.elfSkillHit = elfSkillHit;
	}
	
	public int getEvolutionCount() {
		return evolutionCount;
	}

	public void setEvolutionCount(int evolutionCount) {
		this.evolutionCount = evolutionCount;
	}

	public boolean isFafurionArmor() {
		return isFafurionArmor;
	}

	public void setFafurionArmor(boolean isFafurionArmor) {
		this.isFafurionArmor = isFafurionArmor;
	}

	public boolean isLindviorArmor() {
		return isLindviorArmor;
	}

	public void setLindviorArmor(boolean isLindviorArmor) {
		this.isLindviorArmor = isLindviorArmor;
	}

	public boolean isValakasArmor() {
		return isValakasArmor;
	}

	public void setValakasArmor(boolean isValakasArmor) {
		this.isValakasArmor = isValakasArmor;
	}
	
	public boolean isAntarasArmor() {
		return isAntarasArmor;
	}

	public void setAntarasArmor(boolean isAntarasArmor) {
		this.isAntarasArmor = isAntarasArmor;
	}
	
	// 추가 가상 함수
	public void setAge(int a) {
	}

	public int getAge() {
		return 0;
	}
	
	public boolean isMapHack() {
		return isMapHack;
	}

	public void setMapHack(boolean isMapHack) {
		this.isMapHack = isMapHack;
	}
	public boolean isMonhitdmg() {
		return monhitdmg;
	}

	public void setMonhitdmg(boolean monhitdmg) {
		this.monhitdmg = monhitdmg;
	}
	public boolean ismonExp() {
		return monexp;
	}

	public void setmonExp(boolean monexp) {
		this.monexp = monexp;
	}
	
	public boolean isDamageMassage() {
		return damageMassage;
	}

	public void setDamageMassage(boolean damageMassage) {
		this.damageMassage = damageMassage;
	}
	
	public boolean isWarMessage() {
		return isWarMessage;
	}

	public void setWarMessage(boolean isWarMessage) {
		this.isWarMessage = isWarMessage;
	}
	
	public boolean isCriticalMagicEffect() {
		return criticalMagicEffect;
	}

	public void setCriticalMagicEffect(boolean criticalMagicEffect) {
		this.criticalMagicEffect = criticalMagicEffect;
	}

	public boolean isCriticalEffect() {
		return criticalEffect;
	}

	public void setCriticalEffect(boolean criticalEffect) {
		this.criticalEffect = criticalEffect;
	}

	public int getBuffMeditaitonLevel() {
		return buffMeditaitonLevel;
	}

	public void setBuffMeditaitonLevel(int buffMeditaitonLevel) {
		this.buffMeditaitonLevel = buffMeditaitonLevel;
	}

	public boolean isMagicdollStoneGolem() {
		return MagicdollStoneGolem;
	}

	public void setMagicdollStoneGolem(boolean magicdollStoneGolem) {
		MagicdollStoneGolem = magicdollStoneGolem;
	}

	public boolean isMagicdollWerewolf() {
		return MagicdollWerewolf;
	}

	public void setMagicdollWerewolf(boolean magicdollWerewolf) {
		MagicdollWerewolf = magicdollWerewolf;
	}

	public boolean isMagicdollBugBear() {
		return MagicdollBugBear;
	}

	public void setMagicdollBugBear(boolean magicdollBugBear) {
		MagicdollBugBear = magicdollBugBear;
	}

	public boolean isMagicdollHermitCrab() {
		return MagicdollHermitCrab;
	}

	public void setMagicdollHermitCrab(boolean magicdollHermitCrab) {
		MagicdollHermitCrab = magicdollHermitCrab;
	}

	public boolean isMagicdollYeti() {
		return MagicdollYeti;
	}

	public void setMagicdollYeti(boolean magicdollYeti) {
		MagicdollYeti = magicdollYeti;
	}

	public boolean isMagicdollBasicWood() {
		return MagicdollBasicWood;
	}

	public void setMagicdollBasicWood(boolean magicdollBasicWood) {
		MagicdollBasicWood = magicdollBasicWood;
	}

	public boolean isMagicdollsuccubus() {
		return Magicdollsuccubus;
	}

	public void setMagicdollsuccubus(boolean magicdollsuccubus) {
		Magicdollsuccubus = magicdollsuccubus;
	}

	public boolean isMagicdollElder() {
		return MagicdollElder;
	}

	public void setMagicdollElder(boolean magicdollElder) {
		MagicdollElder = magicdollElder;
	}

	public boolean isMagicdollCockatrice() {
		return MagicdollCockatrice;
	}

	public void setMagicdollCockatrice(boolean magicdollCockatrice) {
		MagicdollCockatrice = magicdollCockatrice;
	}

	public boolean isMagicdollSnowMan() {
		return MagicdollSnowMan;
	}

	public void setMagicdollSnowMan(boolean magicdollSnowMan) {
		MagicdollSnowMan = magicdollSnowMan;
	}

	public boolean isMagicdollMermaid() {
		return MagicdollMermaid;
	}

	public void setMagicdollMermaid(boolean magicdollMermaid) {
		MagicdollMermaid = magicdollMermaid;
	}

	public boolean isMagicdollLavaGolem() {
		return MagicdollLavaGolem;
	}

	public void setMagicdollLavaGolem(boolean magicdollLavaGolem) {
		MagicdollLavaGolem = magicdollLavaGolem;
	}

	public boolean isMagicdollGiant() {
		return MagicdollGiant;
	}

	public void setMagicdollGiant(boolean magicdollGiant) {
		MagicdollGiant = magicdollGiant;
	}

	public boolean isMagicdollBlackElder() {
		return MagicdollBlackElder;
	}

	public void setMagicdollBlackElder(boolean magicdollBlackElder) {
		MagicdollBlackElder = magicdollBlackElder;
	}

	public boolean isMagicdollsuccubusQueen() {
		return MagicdollsuccubusQueen;
	}

	public void setMagicdollsuccubusQueen(boolean magicdollsuccubusQueen) {
		MagicdollsuccubusQueen = magicdollsuccubusQueen;
	}

	public boolean isMagicdollDrake() {
		return MagicdollDrake;
	}

	public void setMagicdollDrake(boolean magicdollDrake) {
		MagicdollDrake = magicdollDrake;
	}

	public boolean isMagicdollKingBugBear() {
		return MagicdollKingBugBear;
	}

	public void setMagicdollKingBugBear(boolean magicdollKingBugBear) {
		MagicdollKingBugBear = magicdollKingBugBear;
	}

	public boolean isMagicdollDiamondGolem() {
		return MagicdollDiamondGolem;
	}

	public void setMagicdollDiamondGolem(boolean magicdollDiamondGolem) {
		MagicdollDiamondGolem = magicdollDiamondGolem;
	}

	public boolean isMagicdollRich() {
		return MagicdollRich;
	}

	public void setMagicdollRich(boolean magicdollRich) {
		MagicdollRich = magicdollRich;
	}

	public boolean isMagicdollCyclops() {
		return MagicdollCyclops;
	}

	public void setMagicdollCyclops(boolean magicdollCyclops) {
		MagicdollCyclops = magicdollCyclops;
	}

	public boolean isMagicdollKnightVald() {
		return MagicdollKnightVald;
	}

	public void setMagicdollKnightVald(boolean magicdollKnightVald) {
		MagicdollKnightVald = magicdollKnightVald;
	}

	public boolean isMagicdollSeer() {
		return MagicdollSeer;
	}

	public void setMagicdollSeer(boolean magicdollSeer) {
		MagicdollSeer = magicdollSeer;
	}

	public boolean isMagicdollIris() {
		return MagicdollIris;
	}

	public void setMagicdollIris(boolean magicdollIris) {
		MagicdollIris = magicdollIris;
	}

	public boolean isMagicdollVampire() {
		return MagicdollVampire;
	}

	public void setMagicdollVampire(boolean magicdollVampire) {
		MagicdollVampire = magicdollVampire;
	}

	public boolean isMagicdollMummylord() {
		return MagicdollMummylord;
	}

	public void setMagicdollMummylord(boolean magicdollMummylord) {
		MagicdollMummylord = magicdollMummylord;
	}

	public boolean isMagicdollDemon() {
		return MagicdollDemon;
	}

	public void setMagicdollDemon(boolean magicdollDemon) {
		MagicdollDemon = magicdollDemon;
	}

	public boolean isMagicdollDeathKnight() {
		return MagicdollDeathKnight;
	}

	public void setMagicdollDeathKnight(boolean magicdollDeathKnight) {
		MagicdollDeathKnight = magicdollDeathKnight;
	}

	public boolean isMagicdollBaranka() {
		return MagicdollBaranka;
	}

	public void setMagicdollBaranka(boolean magicdollBaranka) {
		MagicdollBaranka = magicdollBaranka;
	}

	public boolean isMagicdollTarak() {
		return MagicdollTarak;
	}

	public void setMagicdollTarak(boolean magicdollTarak) {
		MagicdollTarak = magicdollTarak;
	}

	public boolean isMagicdollBaphomet() {
		return MagicdollBaphomet;
	}

	public void setMagicdollBaphomet(boolean magicdollBaphomet) {
		MagicdollBaphomet = magicdollBaphomet;
	}

	public boolean isMagicdollIceQueen() {
		return MagicdollIceQueen;
	}

	public void setMagicdollIceQueen(boolean magicdollIceQueen) {
		MagicdollIceQueen = magicdollIceQueen;
	}

	public boolean isMagicdollKouts() {
		return MagicdollKouts;
	}

	public void setMagicdollKouts(boolean magicdollKouts) {
		MagicdollKouts = magicdollKouts;
	}
	
	public boolean isMagicdollAntaras() {
		return MagicdollAntaras;
	}

	public void setMagicdollAntaras(boolean magicdollAntaras) {
		MagicdollAntaras = magicdollAntaras;
	}

	public boolean isMagicdollPapoorion() {
		return MagicdollPapoorion;
	}

	public void setMagicdollPapoorion(boolean magicdollpapoorion) {
		MagicdollPapoorion = magicdollpapoorion;
	}

	public boolean isMagicdollLindvior() {
		return MagicdollLindvior;
	}

	public void setMagicdollLindvior(boolean magicdolllindvior) {
		MagicdollLindvior = magicdolllindvior;
	}

	public boolean isMagicdollValakas() {
		return MagicdollValakas;
	}

	public void setMagicdollValakas(boolean magicdollvalakas) {
		MagicdollValakas = magicdollvalakas;
	}

	public long getDelaytime() {
		return this._delaytime;
	}

	public void setDelaytime(long paramLong) {
		this._delaytime = paramLong;
	}
	
	public boolean isNameHidden() {
		return nameHidden;
	}

	public void setNameHidden(boolean nameHidden) {
		this.nameHidden = nameHidden;
	}

	public Object getDatabaseKey() {
		return database_key;
	}

	public void setDatabaseKey(Object database_key) {
		this.database_key = database_key;
	}

	public Summon getSummon() {
		return summon;
	}

	public void setSummon(Summon summon) {
		this.summon = summon;
	}

	public void setAiStatus(int ai_status) {
		this.ai_status = ai_status;
		// ai 상태 변경될때마다 멘트표현 변수 초기화.
		ai_showment = false;
		// 멘트 표현위치 초기화.
		ai_showment_idx = 0;
		// 멘트 표현시간 초기화.
		ai_showment_time = 0;
	}

	public int getAiStatus() {
		return ai_status;
	}

	public long getAiTime() {
		return ai_time;
	}

	public void setAiTime(long ai_time) {
		this.ai_time = ai_time;
	}

	public boolean isBuffBluePotion() {
		return BuffBluePotion;
	}

	public void setBuffBluePotion(boolean buffBluePotion) {
		BuffBluePotion = buffBluePotion;
	}
	
	public boolean isBuffExpPotion() {
		return BuffExpPotion;
	}

	public void setBuffExpPotion(boolean buffExpPotion) {
		BuffExpPotion = buffExpPotion;
	}

	public boolean isBuffBraveAvatar() {
		return BuffBraveAvatar;
	}

	public void setBuffBraveAvatar(boolean buffBraveAvatar) {
		BuffBraveAvatar = buffBraveAvatar;
	}

	public boolean isBuffBraveMental() {
		return BuffBraveMental;
	}

	public void setBuffBraveMental(boolean buffBraveMental) {
		BuffBraveMental = buffBraveMental;
	}

	public boolean isBuffCurseBlind() {
		return BuffCurseBlind;
	}

	public void setBuffCurseBlind(boolean buffCurseBlind) {
		BuffCurseBlind = buffCurseBlind;
	}

	public boolean isBuffBounceAttack() {
		return BuffBounceAttack;
	}

	public void setBuffBounceAttack(boolean buffBounceAttack) {
		BuffBounceAttack = buffBounceAttack;
	}

	public int getBuffAdvanceSpiritHp() {
		return BuffAdvanceSpiritHp;
	}

	public void setBuffAdvanceSpiritHp(int buffAdvanceSpiritHp) {
		BuffAdvanceSpiritHp = buffAdvanceSpiritHp;
	}

	public int getBuffAdvanceSpiritMp() {
		return BuffAdvanceSpiritMp;
	}

	public void setBuffAdvanceSpiritMp(int buffAdvanceSpiritMp) {
		BuffAdvanceSpiritMp = buffAdvanceSpiritMp;
	}

	public boolean isBuffExoticVitalize() {
		return BuffExoticVitalize;
	}

	public void setBuffExoticVitalize(boolean buffExoticVitalize) {
		BuffExoticVitalize = buffExoticVitalize;
	}

	public boolean isBuffWaterLife() {
		return BuffWaterLife;
	}

	public void setBuffWaterLife(boolean buffWaterLife) {
		BuffWaterLife = buffWaterLife;
	}

	public boolean isBuffElementalFire() {
		return BuffElementalFire;
	}

	public void setBuffElementalFire(boolean buffElementalFire) {
		BuffElementalFire = buffElementalFire;
	}

	public boolean isBuffPolluteWater() {
		return BuffPolluteWater;
	}

	public void setBuffPolluteWater(boolean buffPolluteWater) {
		BuffPolluteWater = buffPolluteWater;
	}

	public boolean isBuffStrikerGale() {
		return BuffStrikerGale;
	}

	public void setBuffStrikerGale(boolean buffStrikerGale) {
		BuffStrikerGale = buffStrikerGale;
	}
	public boolean isBuffMfire() {
		return BuffMfire;
	}

	public void setBuffMfire(boolean buffMfire) {
		BuffMfire = buffMfire;
	}
	

	public boolean isBuffSoulOfFlame() {
		return BuffSoulOfFlame;
	}

	public void setBuffSoulOfFlame(boolean buffSoulOfFlame) {
		BuffSoulOfFlame = buffSoulOfFlame;
	}

	public boolean isBuffAdditionalFire() {
		return BuffAdditionalFire;
	}

	public void setBuffAdditionalFire(boolean buffAdditionalFire) {
		BuffAdditionalFire = buffAdditionalFire;
	}

	public boolean isBuffCriminal() {
		return BuffCriminal;
	}

	public void setBuffCriminal(boolean buffCriminal) {
		BuffCriminal = buffCriminal;
	}

	public boolean isBuffMeditation() {
		return BuffMeditation;
	}

	public void setBuffMeditation(boolean buffMeditation) {
		BuffMeditation = buffMeditation;
	}

	public boolean isBuffFogOfSleeping() {
		return BuffFogOfSleeping;
	}

	public void setBuffFogOfSleeping(boolean buffFogOfSleeping) {
		BuffFogOfSleeping = buffFogOfSleeping;
	}

	public boolean isBuffCounterMagic() {
		return BuffCounterMagic;
	}

	public void setBuffCounterMagic(boolean buffCounterMagic) {
		BuffCounterMagic = buffCounterMagic;
	}

	public boolean isBuffWeakness() {
		return BuffWeakness;
	}

	public void setBuffWeakness(boolean buffWeakness) {
		BuffWeakness = buffWeakness;
	}

	public boolean isBuffEyeOfStorm() {
		return BuffEyeOfStorm;
	}

	public void setBuffEyeOfStorm(boolean buffEyeOfStorm) {
		BuffEyeOfStorm = buffEyeOfStorm;
	}

	public boolean isBuffSilence() {
		return BuffSilence;
	}

	public void setBuffSilence(boolean buffSilence) {
		BuffSilence = buffSilence;
	}

	public boolean isBuffDisease() {
		return BuffDisease;
	}

	public void setBuffDisease(boolean buffDisease) {
		BuffDisease = buffDisease;
	}

	public boolean isBuffChattingClose() {
		return BuffChattingClose;
	}

	public void setBuffChattingClose(boolean buffChattingClose) {
		BuffChattingClose = buffChattingClose;
	}
	
	public boolean isBuffChattingClosetwo() {
		return BuffChattingClosetwo;
	}

	public void setBuffChattingClosetwo(boolean buffChattingClosetwo) {
		BuffChattingClosetwo = buffChattingClosetwo;
	}

	public boolean isBuffCurseFloatingEye() {
		return BuffCurseFloatingEye;
	}

	public void setBuffCurseFloatingEye(boolean buffCurseFloatingEye) {
		BuffCurseFloatingEye = buffCurseFloatingEye;
	}

	public boolean isBuffCurseGhoul() {
		return BuffCurseGhoul;
	}

	public void setBuffCurseGhoul(boolean buffCurseGhoul) {
		BuffCurseGhoul = buffCurseGhoul;
	}

	public boolean isBuffBlessOfFire() {
		return BuffBlessOfFire;
	}

	public void setBuffBlessOfFire(boolean buffBlessOfFire) {
		BuffBlessOfFire = buffBlessOfFire;
	}

	public boolean isBuffEva() {
		return BuffEva;
	}

	public void setBuffEva(boolean buffEva) {
		BuffEva = buffEva;
	}

	public boolean isBuffWisdom() {
		return BuffWisdom;
	}

	public void setBuffWisdom(boolean buffWisdom) {
		BuffWisdom = buffWisdom;
	}

	public boolean isBuffStormShot() {
		return BuffStormShot;
	}

	public void setBuffStormShot(boolean buffStormShot) {
		BuffStormShot = buffStormShot;
	}

	public boolean isBuffBurningWeapon() {
		return BuffBurningWeapon;
	}

	public void setBuffBurningWeapon(boolean buffBurningWeapon) {
		BuffBurningWeapon = buffBurningWeapon;
	}

	public boolean isBuffEraseMagic() {
		return BuffEraseMagic;
	}

	public void setBuffEraseMagic(boolean buffEraseMagic) {
		BuffEraseMagic = buffEraseMagic;
	}

	public boolean isBuffWindShot() {
		return BuffWindShot;
	}

	public void setBuffWindShot(boolean buffWindShot) {
		BuffWindShot = buffWindShot;
	}

	public boolean isBuffFireWeapon() {
		return BuffFireWeapon;
	}

	public void setBuffFireWeapon(boolean buffFireWeapon) {
		BuffFireWeapon = buffFireWeapon;
	}

	public boolean isBuffGlowingAura() {
		return BuffGlowingAura;
	}

	public void setBuffGlowingAura(boolean buffGlowingAura) {
		BuffGlowingAura = buffGlowingAura;
	}

	public boolean isBuffAbsoluteBarrier() {
		return BuffAbsoluteBarrier;
	}

	public void setBuffAbsoluteBarrier(boolean buffAbsoluteBarrier) {
		BuffAbsoluteBarrier = buffAbsoluteBarrier;
	}

	public boolean isBuffDecayPotion() {
		return BuffDecayPotion;
	}

	public void setBuffDecayPotion(boolean buffDecayPotion) {
		BuffDecayPotion = buffDecayPotion;
	}

	public boolean isBuffImmuneToHarm() {
		return BuffImmuneToHarm;
	}

	public void setBuffImmuneToHarm(boolean buffImmuneToHarm) {
		BuffImmuneToHarm = buffImmuneToHarm;
	}

	public boolean isBuffInvisiBility() {
		return BuffInvisiBility;
	}

	public void setBuffInvisiBility(boolean buffInvisiBility) {
		BuffInvisiBility = buffInvisiBility;
	}

	public boolean isBuffBlessWeapon() {
		return BuffBlessWeapon;
	}

	public void setBuffBlessWeapon(boolean buffBlessWeapon) {
		BuffBlessWeapon = buffBlessWeapon;
	}

	public boolean isBuffCurseParalyze() {
		return BuffCurseParalyze;
	}

	public void setBuffCurseParalyze(boolean buffCurseParalyze) {
		BuffCurseParalyze = buffCurseParalyze;
	}

	public int getCurseParalyzeCounter() {
		return CurseParalyzeCounter;
	}

	public void setCurseParalyzeCounter(int curseParalyzeCounter) {
		CurseParalyzeCounter = curseParalyzeCounter;
	}

	public boolean isBuffDecreaseWeight() {
		return BuffDecreaseWeight;
	}

	public void setBuffDecreaseWeight(boolean BuffDecreaseWeight) {
		this.BuffDecreaseWeight = BuffDecreaseWeight;
	}

	public boolean isBuffHolyWeapon() {
		return BuffHolyWeapon;
	}

	public void setBuffHolyWeapon(boolean buffHolyWeapon) {
		BuffHolyWeapon = buffHolyWeapon;
	}

	public void setWorldDelete(boolean worldDelete) {
		this.worldDelete = worldDelete;
	}

	public boolean isBuffEnchantWeapon() {
		return BuffEnchantWeapon;
	}

	public void setBuffEnchantWeapon(boolean buffEnchantWeapon) {
		BuffEnchantWeapon = buffEnchantWeapon;
	}

	public boolean isBuffMonsterEyeMeat() {
		return BuffMonsterEyeMeat;
	}

	public void setBuffMonsterEyeMeat(boolean buffMonsterEyeMeat) {
		BuffMonsterEyeMeat = buffMonsterEyeMeat;
	}

	public boolean isBuffEnchantVenom() {
		return BuffEnchantVenom;
	}

	public void setBuffEnchantVenom(boolean buffEnchantVenom) {
		BuffEnchantVenom = buffEnchantVenom;
	}

	public boolean isBuffBurningSpirit() {
		return BuffBurningSpirit;
	}

	public void setBuffBurningSpirit(boolean buffBurningSpirit) {
		BuffBurningSpirit = buffBurningSpirit;
	}

	public boolean isBuffVenomResist() {
		return BuffVenomResist;
	}

	public void setBuffVenomResist(boolean buffVenomResist) {
		BuffVenomResist = buffVenomResist;
	}

	public boolean isBuffDoubleBreak() {
		return BuffDoubleBreak;
	}

	public void setBuffDoubleBreak(boolean buffDoubleBreak) {
		BuffDoubleBreak = buffDoubleBreak;
	}

	public boolean isBuffShadowFang() {
		return BuffShadowFang;
	}

	public void setBuffShadowFang(boolean buffShadowFang) {
		BuffShadowFang = buffShadowFang;
	}

	public boolean isBuffBerserks() {
		return BuffBerserks;
	}

	public void setBuffBerserks(boolean buffBerserks) {
		BuffBerserks = buffBerserks;
	}

	public boolean isBuffNaturesTouch() {
		return BuffNaturesTouch;
	}

	public void setBuffNaturesTouch(boolean buffNaturesTouch) {
		BuffNaturesTouch = buffNaturesTouch;
	}

	public boolean isBuffCounterBarrier() {
		return BuffCounterBarrier;
	}

	public void setBuffCounterBarrier(boolean buffCounterBarrier) {
		BuffCounterBarrier = buffCounterBarrier;
	}

	public boolean isBuffCounterMirror() {
		return BuffCounterMirror;
	}

	public void setBuffCounterMirror(boolean buffCounterMirror) {
		BuffCounterMirror = buffCounterMirror;
	}

	public boolean isWorldDelete() {
		return worldDelete;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMap() {
		return map;
	}

	public void setMap(int map) {
		this.map = map;
	}

	public int getHomeX() {
		return homeX;
	}

	public void setHomeX(int homeX) {
		this.homeX = homeX;
	}

	public int getHomeY() {
		return homeY;
	}

	public void setHomeY(int homeY) {
		this.homeY = homeY;
	}

	public int getHomeMap() {
		return homeMap;
	}

	public void setHomeMap(int homeMap) {
		this.homeMap = homeMap;
	}

	public int getHomeLoc() {
		return homeLoc;
	}

	public void setHomeLoc(int homeLoc) {
		this.homeLoc = homeLoc;
	}

	public int getHomeHeading() {
		return homeHeading;
	}

	public void setHomeHeading(int homeHeading) {
		this.homeHeading = homeHeading;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public void setClassSex(int classSex) {
		this.classSex = classSex;
	}

	public int getClassSex() {
		return classSex;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public int getClassType() {
		return classType;
	}

	public void setGfx(int gfx) {
		this.gfx = gfx;
	}

	public int getGfx() {
		return gfx;
	}

	public int getGfxMode() {
		return dead ? 8 : gfxMode;
	}

	public void setGfxMode(int gfxMode) {
		this.gfxMode = gfxMode;
	}

	public int getClassGfx() {
		return classGfx;
	}

	public void setClassGfx(int classGfx) {
		this.classGfx = classGfx;
	}

	public int getClassGfxMode() {
		return classGfxMode;
	}

	public void setClassGfxMode(int classGfxMode) {
		this.classGfxMode = classGfxMode;
	}
	
	public int getTrueTargetTime() {
		return trueTargetTime;
	}

	public void setTrueTargetTime(int trueTargetTime) {
		this.trueTargetTime = trueTargetTime;
	}
	
	public int getLawful() {
		if (Lineage.is_sync) {
			synchronized (sync_lawful) {
				return lawful;
			}
		} else {
			synchronized (sync_dynamic) {
				return lawful;
			}
		}
	}

	public void setLawful(int lawful) {
		if (Lineage.is_sync) {
			synchronized (sync_lawful) {
				if (lawful >= 0) {
					if (lawful > Lineage.LAWFUL) {
						lawful = Lineage.LAWFUL;
					} else if (lawful < Lineage.CHAOTIC) {
						lawful = Lineage.CHAOTIC;
					}
					this.lawful = lawful;
				}
			}
		} else {
			synchronized (sync_dynamic) {
				if (lawful >= 0) {
					if (lawful > Lineage.LAWFUL) {
						lawful = Lineage.LAWFUL;
					} else if (lawful < Lineage.CHAOTIC) {
						lawful = Lineage.CHAOTIC;
					}
					this.lawful = lawful;
				}
			}
		}
	}

	public boolean isDead() {
		if (Lineage.is_sync) {
			synchronized (sync_dead) {
				return dead;
			}
		} else {
			synchronized (sync_dynamic) {
				return dead;
			}
		}
	}

	/**
	 * 객체가 죽은 상태인지 설정하는 메서드.
	 * 
	 * @param dead
	 */
	public void setDead(boolean dead) {
		if (Lineage.is_sync) {
			synchronized (sync_dead) {
				if (!this.dead && dead && !worldDelete) {
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 8), this instanceof PcInstance);
					// 동적값 갱신. 죽은 객체는 해당좌표에 객체가 없는것으로 판단해야함.
					if (isDynamicUpdate())
						World.update_mapDynamic(x, y, map, false);
				}
				this.dead = dead;
			}
		} else {
			synchronized (sync_dynamic) {
				if (!this.dead && dead && !worldDelete) {
					toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, 8), this instanceof PcInstance);
					// 동적값 갱신. 죽은 객체는 해당좌표에 객체가 없는것으로 판단해야함.
					if (isDynamicUpdate())
						World.update_mapDynamic(x, y, map, false);
				}
				this.dead = dead;
			}
		}
	}

	public int getHeading() {
		return heading;
	}

	public void setHeading(int heading) {
		if (heading < 0 || heading > 7)
			heading = 0;
		this.heading = heading;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
	}

	public int getSpeed() {
		if (Lineage.is_sync) {
			synchronized (sync_speed) {
				return speed;
			}
		} else {
			synchronized (sync_dynamic) {
				return speed;
			}
		}
	}

	public void setSpeed(int speed) {
		if (Lineage.is_sync) {
			synchronized (sync_speed) {
				this.speed = speed;
			}
		} else {
			synchronized (sync_dynamic) {
				this.speed = speed;
			}
		}
	}

	public boolean isBrave() {
		if (Lineage.is_sync) {
			synchronized (sync_brave) {
				return brave;
			}
		} else {
			synchronized (sync_dynamic) {
				return brave;
			}
		}
	}

	public void setBrave(boolean brave) {
		if (Lineage.is_sync) {
			synchronized (sync_brave) {
				this.brave = brave;
			}
		} else {
			synchronized (sync_dynamic) {
				this.brave = brave;
			}
		}
	}

	public long getCount() {
		if (Lineage.is_sync) {
			synchronized (sync_count) {
				return count;
			}
		} else {
			synchronized (sync_dynamic) {
				return count;
			}
		}
	}

	public void setCount(long count) {
		if (Lineage.is_sync) {
			synchronized (sync_count) {
				this.count = count;
			}
		} else {
			synchronized (sync_dynamic) {
				this.count = count;
			}
		}
	}

	public int getTempCount() {
		return tempCount;
	}

	public void setTempCount(int tempCount) {
		this.tempCount = tempCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		if (clanName == null) {
			this.clanName = "";
		} else {
			this.clanName = clanName;
		}
	}

	public int getClanGrade() {
		return clanGrade;
	}

	public void setClanGrade(int clanGrade) {
		this.clanGrade = clanGrade;
	}

	public long getOwnObjectId() {
		return own_objectId;
	}

	public void setOwnObjectId(long own_objectId) {
		this.own_objectId = own_objectId;
	}

	public String getOwnName() {
		return own_name;
	}

	public void setOwnName(String own_name) {
		this.own_name = own_name;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setFight(boolean fight) {
		this.fight = fight;
	}

	public boolean isInvis() {
		if (Lineage.is_sync) {
			synchronized (sync_invis) {
				return invis;
			}
		} else {
			synchronized (sync_dynamic) {
				return invis;
			}
		}
	}

	public void setInvis(boolean invis) {
		if (Lineage.is_sync) {
			synchronized (sync_invis) {
				this.invis = invis;
			}
		} else {
			synchronized (sync_dynamic) {
				this.invis = invis;
			}
		}
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isPoison() {
		if (Lineage.is_sync) {
			synchronized (sync_poison) {
				return poison;
			}
		} else {
			synchronized (sync_dynamic) {
				return poison;
			}
		}
	}

	public void setPoison(boolean poison) {
		if (Lineage.is_sync) {
			synchronized (sync_poison) {
				this.poison = poison;
			}
		} else {
			synchronized (sync_dynamic) {
				this.poison = poison;
			}
		}
	}

	public void removeAllList(object o) {
		synchronized (allList) {
			allList.remove(o);
		}
	}

	public void removeInsideList(object o) {
		synchronized (insideList) {
			insideList.remove(o);
		}
	}

	public void appendAllList(object o) {
		synchronized (allList) {
			if (!allList.contains(o))
				allList.add(o);
		}
	}

	public void appendInsideList(object o) {
		synchronized (insideList) {
			if (!insideList.contains(o))
				insideList.add(o);
		}
	}

	public boolean isContainsAllList(object o) {
		synchronized (allList) {
			return allList.contains(o);
		}
	}

	public void clearAllList() {
		synchronized (allList) {
			allList.clear();
		}
	}

	public boolean isContainsInsideList(object o) {
		synchronized (insideList) {
			return insideList.contains(o);
		}
	}
	public List<object> getInsideList(boolean isNew) {
		synchronized (insideList) {
			return isNew ? new ArrayList<object>(insideList) : insideList;
		}
	}

	public object findInsideList(long object_id) {
		for (object o : getInsideList()) {
			if (!FightController.isFightMonster(o) && o.getObjectId() == object_id)
				return o;
		}
		return null;
	}

	public void findInsideList(int x, int y, List<object> r_list) {
		for (object o : getInsideList()) {
			if (o.getX() == x && o.getY() == y)
				r_list.add(o);
		}
	}

	public List<object> getInsideList() {
		synchronized (insideList) {
			return new ArrayList<object>(insideList);
		}
	}

	public List<object> getAllList() {
		synchronized (allList) {
			return new ArrayList<object>(allList);
		}
	}

	public void setTempHp(int hp) {
		temp_hp = hp;
	}

	public void setTempMp(int mp) {
		temp_mp = mp;
	}

	public void setLockLow(boolean lock) {
		if (Lineage.is_sync) {
			synchronized (sync_lock_low) {
				lock_low = lock;
			}
		} else {
			synchronized (sync_dynamic) {
				lock_low = lock;
			}
		}
	}

	public void setLockHigh(boolean lock) {
		if (Lineage.is_sync) {
			synchronized (sync_lock_high) {
				lock_high = lock;
			}
		} else {
			synchronized (sync_dynamic) {
				lock_high = lock;
			}
		}
	}

	public boolean isLockLow() {
		if (Lineage.is_sync) {
			synchronized (sync_lock_low) {
				return lock_low;
			}
		} else {
			synchronized (sync_dynamic) {
				return lock_low;
			}
		}
	}

	public boolean isLockHigh() {
		if (Lineage.is_sync) {
			synchronized (sync_lock_high) {
				return lock_high;
			}
		} else {
			synchronized (sync_dynamic) {
				return lock_high;
			}
		}
	}

	public boolean isLock() {
		if (Lineage.is_sync) {
			return lock_low || lock_high;
		} else {
			synchronized (sync_dynamic) {
				return lock_low || lock_high;
			}
		}
	}

	public void setNowHp(int nowhp) {
	}

	public void setMaxHp(int maxhp) {
	}

	public int getMaxHp() {
		return 0;
	}

	public void setNowMp(int nowmp) {
	}

	public void setMaxMp(int maxmp) {
	}

	public int getMaxMp() {
		return 0;
	}

	public int getLevel() {
		return 0;
	}

	public int getNowHp() {
		return 0;
	}

	public int getNowMp() {
		return 0;
	}

	public int getGm() {
		return 0;
	}

	public void setGm(int gm) {
	}

	public void setInventory(Inventory inv) {
	}

	public Inventory getInventory() {
		return null;
	}

	public long getPartyId() {
		return 0;
	}

	public void setPartyId(long partyId) {
	}

	public void setReSpawnTime(int reSpawnTime) {
	}

	/**
	 * 객체 그리는 패킷에서 호출해서 사용. SObjectAdd
	 * 
	 * @param o
	 */
	public int getStatus(object o) {
		int status = 0;
		if (isPoison())
			status += 1;
		if (isBrave()) {
			if (Lineage.server_version > 200) {
				switch (getClassType()) {
				case Lineage.LINEAGE_CLASS_ELF:
					status += Lineage.elven_wafer_frame ? 48 : 16;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
				case Lineage.LINEAGE_CLASS_DARKELF:
					status += Lineage.holywalk_frame ? 64 : 16;
					break;
				default: // 군주 + 기사 + 용기사 + 환술사 + 기타객체
					status += Lineage.bravery_potion_frame ? 16 : 48;
					break;
				}
			} else {
				status += 16;
			}
		}
		if (isLock())
			status += 8;
		if (isInvis()) {
			status += 2;
			
			if (getObjectId() != o.getObjectId() && o.getGm() > 0) {
				status -= 10;
			}
		}
		if (isTransparent()) {
			status += 128;
			// 잭렌턴일경우 상대방이 호박 가면을 착용중인지에 따라 상태 변경.
			if (this instanceof JackLantern && o instanceof PcInstance && o.getInventory() != null) {
				ItemInstance helm = o.getInventory().getSlot(Lineage.SLOT_HELM);
				if (helm != null && helm.getItem().getNameIdNumber() == 2067)
					status -= 128;
			}
			// 마법인형은 표현해야 하기 때문에
			if (this instanceof MagicDollInstance)
				status -= 128;
		}
		if (this instanceof PcInstance)
			status += 4;

		return status;
	}

	/**
	 * 사용자 객체 정령속성 타입값 확인하는 함수.
	 * 
	 * @return
	 */
	public int getAttribute() {
		return Lineage.ELEMENT_NONE;
	}

	/**
	 * 문 객체가 오버리드해서 사용하며, 문이 닫혀있는지 알려주는 함수.
	 * 
	 * @return
	 */
	public boolean isDoorClose() {
		return false;
	}

	/**
	 * 월드에있는 객체를 클릭했을때 호출되는 메서드. : door클릭하면 호출됨. : 아이템 더블클릭하면 호출됨.
	 * 
	 * @param cha
	 * @param cbp
	 */
	public void toClick(Character cha, ClientBasePacket cbp) {
	}

	/**
	 * 아이템 클릭후 따로 연결구현을 할때 사용. 클라가 호출 안함.
	 * 
	 * @param cha
	 */
	public void toClickFinal(Character cha, Object... opt) {
	}

	/**
	 * 매개변수 객체 에게 근접 및 장거리 물리공격을 가할때 처리하는 메서드.
	 * 
	 * @param o
	 * @param x
	 * @param y
	 * @param bow
	 */
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple) {
	}

	/**
	 * 특정 npc를 클릭했을경우 대화를 요청처리하는 메서드.
	 * 
	 * @param pc
	 * @param cbp
	 */
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
	}

	/**
	 * 특정 npc를 클릭했을경우 대화를 요청처리하는 메서드.
	 * 
	 * @param pc
	 * @param action
	 * @param type
	 * @param cbp
	 */
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
	}

	/**
	 * 성에 세율 변경요청 처리 함수.
	 * 
	 * @param pc
	 * @param tax_rate
	 */
	public void toTaxSetting(PcInstance pc, int tax_rate) {
	}

	/**
	 * 공금 입금 요청 처리 함수.
	 * 
	 * @param pc
	 * @param count
	 */
	public void toTaxPut(PcInstance pc, int count) {
	}

	/**
	 * 공금 출금 요청 처리 함수.
	 * 
	 * @param pc
	 * @param count
	 */
	public void toTaxGet(PcInstance pc, int count) {
	}

	/**
	 * 상점 및 창고에서 물품 처리시 호출하는 메서드.
	 * 
	 * @param pc
	 * @param cbp
	 */
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp) {
	}

	/**
	 * 던전 이동 및 npc를 이용한 텔레포트시 호출해서 사용함.
	 * 
	 * @param x
	 * @param y
	 * @param map
	 */
	public void toPotal(int x, int y, int map) {
	}
	/**
	 * 다른 객체가 마법을 시전하면 호출되는 함수. : 디텍션을 시전시 그에 따른 처리를위해 만들어짐.
	 * 
	 * @param cha
	 * @param c
	 */
	public void toMagic(Character cha, Class<?> c) {
	}
	
	/**
	 * 공격 가능여부 판단하여 리턴함.<br/>
	 * : 물리 및 마법공격 데미지를 추출할때 확인함.<br/>
	 * : 추가적인 조건이 필요할때 확장에서 사용.<br/>
	 * 
	 * @param cha
	 * @param magic
	 * @return
	 */
	public boolean isAttack(Character cha, boolean magic) {
		return true;
	}

	/**
	 * 객체정보를 초기화할때 사용하는 메서드. : pc에서는 케릭터가 죽고 리스하거나 종료할때 호출해서 상태변환용으로도 사용.
	 * 
	 * @param world_out
	 */
	public void toReset(boolean world_out) {
	}

	/**
	 * 부활 처리 메서드.
	 */
	public void toRevival(object o) {
	}

	/**
	 * 부활 처리 메서드.
	 */
	public void toRevivalFinal(object o) {
	}

	/**
	 * 경험치 등록처리 함수.
	 * 
	 * @param o
	 * @param exp
	 */
	public void toExp(object o, double exp) {
	}

	/**
	 * 오토루팅 리능 사용여부 처리 함수.
	 * 
	 * @return
	 */
	public boolean isAutoPickup() {
		return false;
	}

	/**
	 * 오토루팅 리능 사용여부 처리 함수.
	 * 
	 * @param is
	 */
	public void setAutoPickup(boolean is) {
	}

	/**
	 * hp바를 표현할지 여부를 리턴함.
	 * 
	 * @return
	 */
	public boolean isHpbar() {
		return false;
	}

	/**
	 * hp바를 머리위에 표현할지를 설정처리하는 함수.
	 * 
	 * @param is
	 */
	public void setHpbar(boolean is) {
	}

	/**
	 * 물약 멘트 기능 사용여부 처리 함수.
	 * 
	 */
	private boolean autoPotionMent = true;

	public boolean isAutoPotionMent() {
		return autoPotionMent;
	}

	public void setAutoPotionMent(boolean autoPotionMent) {
		this.autoPotionMent = autoPotionMent;
	}
	
	/**
	 * 장사 멘트 기능 사용여부 처리 함수.
	 * 
	 */
	private boolean SaleMent = true;

	public boolean isSaleMent() {
		return SaleMent;
	}

	public void setSaleMent(boolean SaleMent) {
		this.SaleMent = SaleMent;
	}
	
	/**
	 * 교환 처리가 취소됫다면 호출됨. : lineage.bean.lineage.Trade.toCancel() 에서 호출함.
	 */
	public void toTradeCancel(Character cha) {
	}

	/**
	 * 교환 처리가 성공했다면 호출됨. : lineage.bean.lineage.Trade.toOk() 에서 호출함.
	 */
	public void toTradeOk(Character cha) {
	}

	/**
	 * 죽엇을때 호출됨.
	 * 
	 * @param cha
	 *            : 날 죽인 객체 정보.
	 */
	public void toDead(Character cha) {
	}

	/**
	 * 최근에 피케이한 시간값 리턴.
	 * 
	 * @return
	 */
	public long getPkTime() {
		return 0;
	}

	/**
	 * 다른 사용자가 강제적으로 아이템을 넘기려할때 호출되는 메서드.
	 * 
	 * @param o
	 * @param item
	 * @param count
	 */
	public void toGiveItem(object o, ItemInstance item, long count) {
		if (getInventory() == null || item.isEquipped() || count <= 0 || item.getCount() < count || (o != null && o.getObjectId() == getObjectId()))
			return;

		//
		String item_name = item.getName();
		// 아이템 던진거 알리기용.
		if (o != null && o instanceof Character && this instanceof Character)
			item.toGiveItem((Character) o, (Character) this);
		// 인벤에서 겹칠수 있는 아이템 찾기
		ItemInstance temp = getInventory().find(item);
		if (temp != null) {
			// 존재하면 갯수 갱신
			getInventory().count(temp, temp.getCount() + count, true);
			if (o != null && o.getInventory() != null) {
				// 던진놈의 아이템 갯수도 갱신
				o.getInventory().count(item, item.getCount() - count, true);
			} else {
				ItemDatabase.setPool(item);
			}
		} else {
			// 객체아이디값이 설정안된 상태일경우 세팅.
			if (item.getObjectId() <= 0) {
				item.setObjectId(ServerDatabase.nextItemObjId());
			}
			if (item.getCount() - count <= 0) {
				// 전체 이동
				temp = item;
				if (o != null && o.getInventory() != null) {
					// 던진놈의 인벤에서 제거.
					o.getInventory().remove(item, true);
				}
			} else {
				// 일부분 이동
				temp = ItemDatabase.newInstance(item);
				temp.setObjectId(ServerDatabase.nextItemObjId());
				temp.setCount(count);
				if (o != null && o.getInventory() != null) {
					// 던진놈의 아이템 갯수 갱신
					o.getInventory().count(item, item.getCount() - count, true);
				} else {
					ItemDatabase.setPool(item);
				}
			}

			// 처리할 아이템 새로 등록.
			getInventory().append(temp, true);
		}

		if (o == null || o instanceof ItemInstance)
			// %0%o 얻었습니다.
			toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 403, count == 1 ? item_name : String.format("%s (%d)", item_name, count)));
		else
			// \f1%0%s 당신에게 %1%o 주었습니다.
			toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, o.getName(), count == 1 ? item_name : String.format("%s (%d)", item_name, count)));
	}

	/**
	 * 아이템이 강제로 넘어간걸 알리는 용
	 * 
	 * @param cha
	 *            : 던진 객체
	 * @param target
	 *            : 받은 객체
	 */
	public void toGiveItem(Character cha, Character target) {
	}

	/**
	 * 다른 객체로부터 데미지를 입었을때 호출됨.
	 * 
	 * @param cha
	 *            : 가격자.
	 * @param dmg
	 *            : 입혀진 데미지.
	 * @param type
	 *            : 공격 방식.
	 */
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
	}

	/**
	 * HyperText 대한 요청 처리.
	 * 
	 * @param pc
	 * @param count
	 * @param a
	 * @param request
	 */
	public void toHyperText(PcInstance pc, ClientBasePacket cbp) {
	}

	/**
	 * CharacterController에 등록된 객체는 해당 함수가 주기적으로 호출됨.
	 * 
	 * @param time
	 */
	public void toTimer(long time) {
	}

	/**
	 * 해당 객체가 픽업됫을때 호출되는 메서드.
	 * 
	 * @param cha
	 */
	public void toPickup(Character cha) {
	}
	public boolean isPickup(Character cha) {
		return true;
	}

	/**
	 * 매개변수인 pc가 일반 채팅을 하게되면 주변 객체를 검색하게되고<br>
	 * 주변객체들은 해당 메서드를 호출받게됨.<br>
	 * 해당 메서드를 이용해서 대화처리를 하면됨.
	 * 
	 * @param pc
	 * @param msg
	 */
	public void toChatting(object o, String msg) {
	}

	/**
	 * 문짝 센드처리함수. : 객체가 이동중 해당 객체가 닫혀있는지 여부를 확인. 닫혀잇다면 해당 함수를 호출하여 사용자가 해당 필드에 위치를 이동 불가능하도록 처리. : Door이나 KingdomDoor 객체에 오버리드되서 사용할것이며, 1픽셀 값이상으로 이동 불가능하게 해야할 경우가 있음.
	 * 
	 * @param o
	 */
	public void toDoorSend(object o) {
		if (getHomeLoc() > 0) {
			for (int i = 0; i < getHomeLoc(); ++i) {
				switch (getHeading()) {
				case 2: // 4방향으로 증가.
				case 6:
					if (o == null) {
						toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y + i, heading, isDoorClose()), false);
					} else {
						o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x, y + i, heading, isDoorClose()));
					}
					// 타일 변경.
					World.set_map(x, y + i, map, isDoorClose() ? 16 : homeTile[0]);
					World.set_map(x - 1, y + i, map, isDoorClose() ? 16 : homeTile[1]);
					break;
				case 4: // 6방향으로 증가.
					if (o == null) {
						toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x - i, y, heading, isDoorClose()), false);
					} else {
						o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), x - i, y, heading, isDoorClose()));
					}
					// 타일 변경.
					World.set_map(x - i, y, map, isDoorClose() ? 16 : homeTile[0]);
					World.set_map(x - i, y + 1, map, isDoorClose() ? 16 : homeTile[1]);
					break;
				}
			}
		} else {
			if (o == null) {
				toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), this), false);
			} else {
				o.toSender(S_Door.clone(BasePacketPooling.getPool(S_Door.class), this));
			}
			// 타일 변경.
			switch (getHeading()) {
			case 2:
			case 6:
				World.set_map(x, y, map, isDoorClose() ? 16 : homeTile[0]);
				World.set_map(x - 1, y, map, isDoorClose() ? 16 : homeTile[1]);
				break;
			case 4: // 6방향으로 증가.
				World.set_map(x, y, map, isDoorClose() ? 16 : homeTile[0]);
				World.set_map(x, y + 1, map, isDoorClose() ? 16 : homeTile[1]);
				break;
			}
		}
	}

	/**
	 * 인공지능 활성화된 객체를 인공지능 처리목록에서 제거를 원할때 외부에서 호출해서 사용할수 있도록 함수 제공. : toAiSpawn함수를 이용할경우 재스폰값이 0일때만 제거처리함. : 해당 함수를 이용하면 재스폰값이 0이상이더라도 그냥 제거함.
	 */
	public void toAiThreadDelete() {
		// 주변객체 관리 제거
		clearList(true);
		// 월드에서 제거
		World.remove(this);
		// 상태 변경. 그래야 인공지능 쓰레드에서 제거됨.
		// 그후 풀에 등록함.
		setAiStatus(Lineage.AI_STATUS_DELETE);
	}

	/**
	 * 랜덤워킹 처리 함수.
	 */
	protected void toAiWalk(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
	}

	/**
	 * 전투 처리 함수.
	 */
	protected void toAiAttack(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_ATTACK);
	}

	/**
	 * 죽은 객체 처리 함수.
	 */
	protected void toAiDead(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_DEAD);
	}

	/**
	 * 시체 유지 및 제거 처리 함수.
	 */
	protected void toAiCorpse(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_DEAD);
	}

	/**
	 * 재스폰 처리 함수.
	 */
	protected void toAiSpawn(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_DEAD);
	}

	/**
	 * 도망가기 처리 함수.
	 */
	protected void toAiEscape(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
	}

	/**
	 * 아이템 줍기 처리 함수.
	 */
	protected void toAiPickup(long time) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_GET);
	}

	/**
	 * 마법인형 모션 처리 함수.
	 */
	protected void toAiMagicDollAction(int gfxMode) {
		ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode);
	}

	/**
	 * 인공지능 처리 요청 함수.
	 * 
	 * @param time
	 */
	public void toAi(long time) {
		synchronized (sync_ai) {
			// 몬스터일경우 각 몬스터별로 인공지능 처리하기위해 확인.
			MonsterInstance mi = null;
			if (this instanceof MonsterInstance)
				mi = (MonsterInstance) this;

			// 일반 적인 인공지능 패턴
			// 랜덤워킹, 죽은거체크, 시체유지, 도망가기, 스폰멘트, 죽을때멘트, 공격할때멘트
			switch (getAiStatus()) {
			case Lineage.AI_STATUS_DELETE:
				break;
			case Lineage.AI_STATUS_WALK:
				try {
					toAiWalk(time);
				} catch (Exception e) {
				}
				break;
			case Lineage.AI_STATUS_ATTACK:
				if (mi != null) {
					switch (mi.getMonster().getNameIdNumber()) {
					case 6: // 괴물 눈
						FloatingEye.toAiAttack(mi, time);
						break;
					default:
						toAiAttack(time);
						break;
					}
				} else {
					toAiAttack(time);
				}
				break;
			case Lineage.AI_STATUS_DEAD:
				toAiDead(time);
				break;
			case Lineage.AI_STATUS_CORPSE:
				toAiCorpse(time);
				break;
			case Lineage.AI_STATUS_SPAWN:
				toAiSpawn(time);
				break;
			case Lineage.AI_STATUS_ESCAPE:
				if (mi != null) {
					switch (mi.getMonster().getNameIdNumber()) {
					case 6: // 괴물 눈
						FloatingEye.toAiEscape(mi, time);
						break;
					default:
						toAiEscape(time);
						break;
					}
				} else {
					toAiEscape(time);
				}
				break;
			case Lineage.AI_STATUS_PICKUP:
				toAiPickup(time);
				break;
			default:
				ai_time = SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK);
				break;
			}
		}
	}

	/**
	 * 주변객체에게 전송하면서 나에게도 전송할지 여부
	 * 
	 * @param bp
	 * @param me
	 */
	public void toSender(BasePacket bp, boolean me) {
		if (bp instanceof ServerBasePacket) {
			ServerBasePacket sbp = (ServerBasePacket) bp;
			for (object o : getInsideList()) {
				if (o instanceof PcInstance)
					o.toSender(ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()));
			}
			if (me)
				toSender(bp);
			else
				BasePacketPooling.setPool(bp);
		} else {
			BasePacketPooling.setPool(bp);
		}
	}

	/**
	 * 패킷 전송 처리 자신에게만 전송
	 * 
	 * @param bp
	 */
	public void toSender(BasePacket bp) {
		// 풀에 다시 넣기
		BasePacketPooling.setPool(bp);
	}

	/**
	 * 이동 요청 처리 함수.
	 * 
	 * @param x
	 * @param y
	 * @param h
	 */
public void toMoving(final int x, final int y, final int h) {
		

		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(this.x, this.y, this.map, false);
		// 좌표 변경.
		this.x = x;
		this.y = y;
		this.heading = h;
		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(x, y, map, true);
		// 주변객체 갱신
		if (!Util.isDistance(tempX, tempY, map, x, y, map, Lineage.SEARCH_LOCATIONRANGE)) {
			tempX = x;
			tempY = y;
			List<object> temp = new ArrayList<object>();
			// 이전에 관리중이던 목록 갱신
			synchronized (allList) {
				temp.addAll(allList);
				allList.clear();
			}
			for (object o : temp)
				o.removeAllList(this);
			// 객체 갱신
			temp.clear();
			World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp);
			for (object o : temp) {
				if (isList(o)) {
					// 전체 관리목록에 등록.
					appendAllList(o);
					o.appendAllList(this);
				}
			}
			// 이벤트 처리 요청.
			if (this instanceof PcInstance)
				EventController.toUpdate((PcInstance) this);
		}
		// 주변객체 패킷 및 갱신 처리
		for (object o : getAllList()) {
			if (Util.isDistance(this, o, Lineage.SEARCH_LOCATIONRANGE)) {
				if (isContainsInsideList(o)) {
					if (o instanceof PcInstance)
						o.toSender(S_ObjectMoving.clone(BasePacketPooling.getPool(S_ObjectMoving.class), this));
				} else {
					appendInsideList(o);
					o.appendInsideList(this);
					if (this instanceof PcInstance) {
						toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), o, this));
						if (o.isDoorClose())
							o.toDoorSend(this);
					}
					if (o instanceof PcInstance) {
						o.toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, o));
					}
				}				
			} else {
				if (isContainsInsideList(o)) {
					removeInsideList(o);
					o.removeInsideList(this);
					if (this instanceof PcInstance)
						toSender(S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), o));
					if (o instanceof PcInstance)
						o.toSender(S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), this));
				}
			}
		}
	}

	/**
	 * 텔레포트 처리 함수.
	 * 
	 * @param x
	 * @param y
	 * @param map
	 * @param effect
	 */
	public void toTeleport(final int x, final int y, final int map, final boolean effect) {
		if (effect) {
			if (this instanceof MagicDollInstance)
				toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.doll_teleport_effect), false);
			else
				toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.object_teleport_effect), this instanceof PcInstance);
		}

		// 이전에 관리중이던 목록 제거
		clearList(true);
		// 월드에 갱신
		World.remove(this);
		// 동적값 갱신.
		if (isDynamicUpdate())	
			World.update_mapDynamic(this.x, this.y, this.map, false);

		// 좌표 변경.
		this.x = x;
		this.y = y;
		this.map = map;
		tempX = x;
		tempY = y;

		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(x, y, map, true);
		// 월드에 갱신.
		World.append(this);
		// 패킷 처리
		if (this instanceof PcInstance) {
			toSender(S_ObjectMap.clone(BasePacketPooling.getPool(S_ObjectMap.class), this));
			toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this));
		}
		// 객체 갱신
		List<object> temp_list = new ArrayList<object>();
		World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp_list);
		// 순회
		for (object o : temp_list) {
			if (isList(o)) {
				// 전체 관리목록에 등록.
				appendAllList(o);
				o.appendAllList(this);
				// 화면내에 있을경우
				if (Util.isDistance(this, o, Lineage.SEARCH_LOCATIONRANGE)) {
					// 화면내에 관리 목록에 등록
					appendInsideList(o);
					o.appendInsideList(this);
					// 사용자들 패킷 처리
					if (this instanceof PcInstance) {
						// 텔레포트한 객체가 유저일 경우 유저에게 주위 객체를 추가.
						toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), o, this));
						
						if (o.isDoorClose())
							o.toDoorSend(this);
					}
					
					if (o instanceof PcInstance) {
						// 주위 객체에게 텔레포트한 유저를 추가.
						o.toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, o));
					}
				}
			}
		}
		temp_list.clear();
		temp_list = null;
	}
	
	
	public void toTeleportRange(final int x, final int y, final int map, final boolean effect, int range) {
		if (effect) {
			if (this instanceof MagicDollInstance)
				toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.doll_teleport_effect), false);
			else
				toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.object_teleport_effect), this instanceof PcInstance);
		}

		// 이전에 관리중이던 목록 제거
		clearList(true);
		// 월드에 갱신
		World.remove(this);
		// 동적값 갱신.
		if (isDynamicUpdate())	
			World.update_mapDynamic(this.x, this.y, this.map, false);
		
		if (range > 0) {
			int roop_cnt = 0;
			int lx = x;
			int ly = y;
			int loc = range;
			
			// 랜덤 좌표 스폰
			do {
				lx = Util.random(x - loc, x + loc);
				ly = Util.random(y - loc, y + loc);
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
			
			// 좌표 변경.
			this.x = lx;
			this.y = ly;
			this.map = map;
		} else {
			// 좌표 변경.
			this.x = x;
			this.y = y;
			this.map = map;
		}
		
		tempX = x;
		tempY = y;

		// 동적값 갱신.
		if (isDynamicUpdate())
			World.update_mapDynamic(x, y, map, true);
		// 월드에 갱신.
		World.append(this);
		// 패킷 처리
		if (this instanceof PcInstance) {
			toSender(S_ObjectMap.clone(BasePacketPooling.getPool(S_ObjectMap.class), this));
			toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, this));
		}
		// 객체 갱신
		List<object> temp_list = new ArrayList<object>();
		World.getLocationList(this, Lineage.SEARCH_WORLD_LOCATION, temp_list);
		// 순회
		for (object o : temp_list) {
			if (isList(o)) {
				// 전체 관리목록에 등록.
				appendAllList(o);
				o.appendAllList(this);
				// 화면내에 있을경우
				if (Util.isDistance(this, o, Lineage.SEARCH_LOCATIONRANGE)) {
					// 화면내에 관리 목록에 등록
					appendInsideList(o);
					o.appendInsideList(this);
					// 사용자들 패킷 처리
					if (this instanceof PcInstance) {
						// 텔레포트한 객체가 유저일 경우 유저에게 주위 객체를 추가.
						toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), o, this));
						
						if (o.isDoorClose())
							o.toDoorSend(this);
					}
					
					if (o instanceof PcInstance) {
						// 주위 객체에게 텔레포트한 유저를 추가.
						o.toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), this, o));
					}
				}
			}
		}
		temp_list.clear();
		temp_list = null;
	}

	/**
	 * 관리중이던 객체 초기화처리 하는 함수.
	 */
	public void clearList(boolean packet) {
		List<object> temp_list = new ArrayList<object>();
		// LOCATIONRANGE 셀 내에 있는 목록들 둘러보면서 나를 제거.
		synchronized (insideList) {
			temp_list.addAll(insideList);
			insideList.clear();
		}
		for (object o : temp_list) {
			o.removeInsideList(this);
			if (packet && o instanceof PcInstance)
				o.toSender(S_ObjectRemove.clone(BasePacketPooling.getPool(S_ObjectRemove.class), this));
		}
		temp_list.clear();
		synchronized (allList) {
			temp_list.addAll(allList);
			allList.clear();
		}
		for (object o : temp_list) {
			o.removeAllList(this);
		}
		temp_list.clear();
		temp_list = null;

		if (isDynamicUpdate() && !isDead())
			World.update_mapDynamic(getX(), getY(), getMap(), false);
	}

	/**
	 * 인공지능. 객체의 인공지능이 활성화해야 할 시간이 됬는지 확인하는 메서드.
	 */
	public boolean isAi(long time) {
		long speed = ai_time <= 0 ? SpriteFrameDatabase.getGfxFrameTime(this, gfx, gfxMode + Lineage.GFX_MODE_WALK) : ai_time;
		long temp = time - ai_start_time;
		
		if (this instanceof Racer)
			speed /= 2;

		if (time == 0 || temp >= speed) {
			ai_start_time = time;
			// 락걸린 상태라면 true가 리턴되며, ai를 활성화 하면 안되기때문에 false로 변환해야됨.
			return !isLock();
		}

		return false;
	}

	/**
	 * 월드 타일에 누적카운팅처리를 할 객체인지 여부를 리턴함.
	 * 
	 * @return
	 */
	protected boolean isDynamicUpdate() {
		return !isTransparent() && (this instanceof Character || this instanceof TeleportInstance || this instanceof DwarfInstance || this instanceof ShopInstance || this instanceof BoardInstance ||
				(this instanceof BackgroundInstance && !(this instanceof Switch) && !(this instanceof Firewall) && !(this instanceof LifeStream) && !(this instanceof BackgroundTile) && !(this instanceof DeathEffect)));
	}

	/**
	 * 월드에서 객체를 추출한후 해당 객체를 관리목록에 등록할지 여부를 이 함수가 판단. : 텔레포트후, 이동후 등에서 호출해서 사용중.
	 * 
	 * @param o
	 * @return
	 */
	public boolean isList(object o) {
		if (getObjectId() == o.getObjectId())
			return false;

		// 사용자일경우 무조건 등록.
		if (this instanceof PcInstance || o instanceof PcInstance)
			return true;
		// 소환객체일 경우.
		if (this instanceof SummonInstance)
			// 아이템
			return o instanceof ItemInstance;
		// 몬스터일 경우.
		if (this instanceof MonsterInstance) {
			MonsterInstance mon = (MonsterInstance) this;
			// 아이템
			if (o instanceof ItemInstance)
				return mon.getMonster().isPickup();
			// 서먼객체
			if (o instanceof SummonInstance)
				return true;
			// 동족
			if (o instanceof MonsterInstance)
				return ((MonsterInstance) o).isFamily((MonsterInstance) o, mon.getMonster().getFamily());
		}
		// 스위치 일경우. (법사30퀘 스위치)
		if (this instanceof Switch) {
			// 문
			return o instanceof Door;
		}
		// 아이템일 경우
		if (this instanceof ItemInstance) {
			if (o instanceof PetInstance)
				return true;
			if (o instanceof MonsterInstance)
				return ((MonsterInstance) o).getMonster().isPickup();
		}
		// 성문 일경우
		if (this instanceof KingdomDoor)
			return o instanceof KingdomGuard || o instanceof KingdomDoorman;
		// 성경비 일경우
		if (this instanceof KingdomGuard)
			return o instanceof KingdomDoor || o instanceof KingdomGuard || o instanceof KingdomCastleTop;
		// 경비 일경우
		if (this instanceof GuardInstance)
			return o instanceof GuardInstance && !(o instanceof KingdomGuard);
		// 수호탑일 경우
		if (this instanceof KingdomCastleTop)
			return o instanceof KingdomGuard;
		// 성 문지기 일경우
		if (this instanceof KingdomDoorman)
			return o instanceof KingdomDoor;
		// 배경에쓰이는 객체일 경우
		if (this instanceof BackgroundInstance) {
			// 파이어월 일경우 hp 처리하는 객체는 관리목록에 등록.
			if (this instanceof Firewall)
				return o instanceof Character;
			// 라이프 스트림역시~
			if (this instanceof LifeStream)
				return o instanceof Character;
		}
		return false;
	}

	/*
	 * 오만 맵인지 확인하는 함수
	 */
	public boolean isOman() {
		if (getMap() == 101 || getMap() == 102 || getMap() == 103 || getMap() == 104 || getMap() == 105 || getMap() == 106 || getMap() == 107 || getMap() == 108 || getMap() == 109 || getMap() == 110
				|| getMap() == 200)
			return true;
		return false;
	}
	
	public boolean checkSpear() {
		if (this instanceof PcInstance) {
			if (getInventory() != null && getGfx() != getClassGfx()) {
				Poly p = PolyDatabase.getPolyGfx(getGfx());

				if (p != null && !p.isActionSpear()) {
					ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

					if (weapon != null && weapon.getItem() != null && weapon.getItem().getType2().equalsIgnoreCase("spear")) {
						setGfxMode(p.getGfxMode());
						return true;
					}
				}
			}
		}

		return false;
	}
	
	public boolean isSpearAction(ItemInstance weapon) {
		if (this instanceof PcInstance) {
			if (getInventory() != null && getGfx() != getClassGfx()) {
				Poly p = PolyDatabase.getPolyGfx(getGfx());

				if (p != null && !p.isActionSpear()) {
					if (weapon != null && weapon.getItem() != null && weapon.getItem().getType2().equalsIgnoreCase("spear")) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	/**
	 * 엔피시 객체 리턴.
	 * 
	 * @return
	 */
	public Npc getNpc() {
		return null;
	}

	public Monster getMonster() {
		return null;
	}

}

