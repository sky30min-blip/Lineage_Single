package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import all_night.Lineage_Balance;
import kubera.AttackController;
import kubera.MovingController;
import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.PcShop;
import lineage.bean.database.PcTrade;
import lineage.bean.database.Poly;
import lineage.bean.database.Shop;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Agit;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Kingdom;
import lineage.bean.lineage.Summon;
import lineage.bean.lineage.Swap;
import lineage.database.AccountDatabase;
import lineage.database.BackgroundDatabase;
import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
import lineage.database.HackNoCheckDatabase;
import lineage.database.ItemDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcSpawnlistDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.database.TeleportResetDatabase;
import lineage.database.TimeDungeonDatabase;
import lineage.gui.GuiMain;
import lineage.network.LineageClient;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.ServerBasePacket;
import lineage.network.packet.server.S_Ability;
import lineage.network.packet.server.S_CharacterExp;
import lineage.network.packet.server.S_CharacterHp;
import lineage.network.packet.server.S_CharacterMp;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_InterfaceRead;
import lineage.network.packet.server.S_InventoryAdd;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryIdentify;
import lineage.network.packet.server.S_InventoryList;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectCriminal;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_ObjectInvis;
import lineage.network.packet.server.S_ObjectLawful;
import lineage.network.packet.server.S_ObjectLight;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_ObjectMoving;
import lineage.network.packet.server.S_ObjectName;
import lineage.network.packet.server.S_ObjectPoly;
import lineage.network.packet.server.S_ObjectRevival;
import lineage.network.packet.server.S_Potal;
import lineage.network.packet.server.S_Weather;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.Log;
import lineage.share.System;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.AStar;
import lineage.world.Node;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.AutoHuntCheckController;
import lineage.world.controller.BaphometSystemController;
import lineage.world.controller.TeamBattleController;
import lineage.world.controller.TebeController;
import lineage.world.controller.Thebes;
import lineage.world.controller.PenguinHuntingController;
import lineage.world.controller.BookController;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ClanController;
import lineage.world.controller.CommandController;
import lineage.world.controller.DamageController;
import lineage.world.controller.FishingController;
import lineage.world.controller.FriendController;
import lineage.world.controller.InventoryController;
import lineage.world.controller.KingdomController;
import lineage.world.controller.LetterController;
import lineage.world.controller.LocationController;
import lineage.world.controller.MagicDollController;
import lineage.world.controller.PartyController;
import lineage.world.controller.PcMarketController;
import lineage.world.controller.QuestController;
import lineage.world.controller.RankController;
import lineage.world.controller.SkillController;
import lineage.world.controller.SummonController;
import lineage.world.controller.TradeController;
import lineage.world.controller.UserShopController;
import lineage.world.controller.WantedController;
import lineage.world.controller.DollRaceController;
import lineage.world.controller.DollRaceController.EVENT_STATUS;
import lineage.world.controller.TreasureHuntController;
import lineage.world.controller.DevilController;
import lineage.world.controller.DimensionController;
import lineage.world.controller.WorldBossController;
import lineage.world.controller.HellController;
import lineage.world.controller.TimeEventController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.item.MagicDoll;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.magic.Berserks;
import lineage.world.object.magic.BloodToSoul;
import lineage.world.object.magic.BraveAvatar;
import lineage.world.object.magic.BraveMental;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.BurningSpirit;
import lineage.world.object.magic.BurningWeapon;
import lineage.world.object.magic.ChillTouch;
import lineage.world.object.magic.ClearMind;
import lineage.world.object.magic.CounterBarrier;
import lineage.world.object.magic.Criminal;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.DoubleBreak;
import lineage.world.object.magic.DressDexterity;
import lineage.world.object.magic.DressEvasion;
import lineage.world.object.magic.DressMighty;
import lineage.world.object.magic.EagleEye;
import lineage.world.object.magic.EarthBind;
import lineage.world.object.magic.ElementalFire;
import lineage.world.object.magic.EnchantVenom;
import lineage.world.object.magic.Eruption;
import lineage.world.object.magic.FrameSpeedOverStun;
import lineage.world.object.magic.GlowingWeapon;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.InvisiBility;
import lineage.world.object.magic.NaturesTouch;
import lineage.world.object.magic.ReductionArmor;
import lineage.world.object.magic.ResistElemental;
import lineage.world.object.magic.ResistMagic;
import lineage.world.object.magic.ShadowFang;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.ShiningShield;
import lineage.world.object.magic.ShockStun;
import lineage.world.object.magic.Slow;
import lineage.world.object.magic.SolidCarriage;
import lineage.world.object.magic.SoulOfFlame;
import lineage.world.object.magic.StormShot;
import lineage.world.object.magic.Sunburst;
import lineage.world.object.magic.TripleArrow;
import lineage.world.object.magic.TurnUndead;
import lineage.world.object.magic.UncannyDodge;
import lineage.world.object.magic.Venom;
import lineage.world.object.magic.movingacceleratic;
import lineage.world.object.npc.SpotCrown;
import lineage.world.object.npc.SpotTower;
import lineage.world.object.npc.background.BackgroundTile;
import lineage.world.object.npc.background.Cracker;
import lineage.world.object.npc.background.DeathEffect;
import lineage.world.object.npc.background.Firewall;
import lineage.world.object.npc.background.FishExp;
import lineage.world.object.npc.background.LifeStream;
import lineage.world.object.npc.background.RestCracker;

import lineage.world.object.npc.background.Switch;
import lineage.world.object.npc.background.door.Door;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomCrown;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.teleporter.Esmereld;
import lineage.bean.database.marketPrice;

public class PcInstance extends Character {

	private LineageClient client;
	private String accountId;
	private int accountUid;

	private int attribute; // 요정 클레스들 속성마법 값 1[물] 2[바람] 3[땅] 4[불]
	// 운영자 여부 판단 변수.
	private int gm;

	public int checkmenttime;
	public int trank;
	public int testTime;
	public int testTime2;
	public int gmTime;
	public long checkTime = 0;
	public long checkTimes = 0;
	public int checkaccess = 0;

	
	
	// public static object trankBronze1;
	// public static object trankBronze2;
	// public static object trankBronze3;

	public int PcMarket_Step;
	public int PcMarket_Count;
	public object tempObject;

	private object tempShop;

	public int chatcount;
	// 사운드 on/off
	private boolean isSound;
	// 파티 메세지 on/off
	private boolean isPartyMent;
	// 파티변수
	private long partyid;
	// 채팅 사용 유무
	private boolean chattingWhisper;
	private boolean chattingGlobal;
	private boolean chattingTrade;
	// 피케이 처리 변수
	private int PkCount; // 누적된 피케이 횟수.
	private long PkTime; // 최근에 실행한 피케이 시간.
	// 시간제 아이템을 표시하기위한 변수
	private long SupplyTime;
	public int SupplyCount;
	// 자동칼질관련
	private boolean isUsingTripleArrow;
	public boolean isAttacking;
	private boolean tripleArrowFinished = true;
	private boolean attackCancelled = false;
	private long tripleArrowEndTime = 0L;
	// 오만의탑 부적
	private boolean isOmanBuff1;
	private boolean isOmanBuff2;
	private boolean isOmanBuff3;
	private boolean isOmanBuff4;
	private boolean isOmanBuff5;
	private boolean isOmanBuff6;
	private boolean isOmanBuff7;
	private boolean isOmanBuff8;
	private boolean isOmanBuff9;
	private boolean isOmanBuff10;

	private boolean isSealBuff;
	private int Seal_Level;

	// 인벤토리
	private Inventory inv;
	private List<String> listBlockName;
	private Esmereld npc_esmereld; // 에스메랄다 npc 포인터

	private boolean is_save;
	

	
	
	// 딜레이용
	private long message_time; // 서버 메세지가 표현된 마지막 시간 저장.
	private long auto_save_time; // 자동저장 이전시간 기록용.
	private long premium_item_time; // 자동지급 시간 저장.
	private long open_wait_item_time; // 자동지급 시간 저장.
	// 로그 기록용
	private long register_date; // 케릭 생성 시간
	private long join_date; // 케릭 접속 시간
	// 죽으면서 착감된 경험치 실시간 기록용.
	private double lost_exp;
	// 오토루팅 처리할지 여부를 확인할 변수.
	private boolean auto_pickup;
	// 자신에 hp바를 머리위에 표현할지 여부.
	private boolean is_hpbar;
	// 인터페이스 및 인벤 정보.
	private byte[] db_interface;
	// 엘릭서 사용된 갯수
	private int elixir;
	// 추가 경험치 지급처리에 사용되는 변수(버프에서 증가 혹은 감사시킴)
	private double dynamicExp;
	// 나이 설정
	private int age;
	// 변신 주문서를 위한 임시 저장 변수
	private boolean tempPoly;
	private ItemInstance tempPolyScroll;
	// 고정 멤버 유무
	private boolean member;
	// 오토루팅 메세지 on/off
	private boolean isAutoPickMessage;
	// 칼렉 딜레이를 위한 변수
	private long lastLackTime;
	// 바포메트 시스템을 위한 변수
	private boolean isBaphomet;
	private int baphometLevel;

	// 출석알림 딜레이
	public double Levelexpname;

	// 랭킹 시스템을 위한 변수
	private int rank;
	public int lastRankClass;
	private int pvprank;
	private int lastpvpRankClass;
	// 마법인형을 위한 변수
	private MagicDoll magicDoll;
	private MagicDollInstance magicDollinstance;
	// exp 버프 아이콘을 위한 변수
	private boolean icon;
	// 팀대전 & 난투전 위한 변수
	private String tempName;
	private String tempClanName;
	private String tempTitle;
	private int tempClanId;
	private int tempClanGrade;
	private int battleTeam;
	private boolean isTeamBattleDead;

	// 시간제 던전을 위한 변수
	private int giran_dungeon_time;
	// 결투장을 위한 변수
	public boolean isBattlezone;

	// 자동사냥 카운터
	private int autoHuntMonsterCount;
	// 자동사냥 답
	private String autoHuntAnswer;
	// 자동사냥 방지 인증번호 받은 후 공격불가 까지 딜레이
	private long autoHuntAnswerTime;

	// 장비거래 정보
	private String infoName;
	private String infoPhoneNum;
	private String infoBankName;
	private String infoBankNum;
	private PcTrade pcTrade;
	// 기란감옥 시간 초기화 주문서 횟수 제한
	public int giran_dungeon_count;

	// 자동사냥 시간 초기화 주문서 횟수 제한
	public int auto_count;
	

	// 출석체크
	public int daycount;
	public int daycheck;
	public int dayptime;

	// 죽었을경우 시간체그
	public int playdead;

	// 시세검색
	public List<marketPrice> marketPrice;
	// 장비 스왑
	private Map<String, Swap[]> swap;
	public String[] swapIdx;
	public String selectSwap;
	public boolean isInsertSwap;

	static private int[] MAGIC_DOLL_GFX;

	public int dmglimitcheck;
	public int mdmglimitcheck;
	// 자동 사냥
	private AStar aStar; // 길찾기 변수
	private Node tail; // 길찾기 변수
	private int[] iPath; // 길찾기 변수
	public boolean isAutoHunt;
	public object autohunt_target;
	public int start_x;
	public int start_y;
	public int start_map;
	public int temp_x;
	public int temp_y;
	public int temp_map;
	public int auto_hunt_account_time;
	public int auto_hunt_time;
	public boolean is_auto_return_home;
	public int auto_return_home_hp;
	public boolean is_auto_buff;
	public long auto_buff_time;
	public boolean is_auto_potion_buy;
	public boolean is_auto_poly_select;
	public boolean is_auto_rank_poly;
	public boolean is_auto_rank_poly_buy;
	public boolean is_auto_poly;
	public boolean is_auto_poly_buy;
	public boolean is_auto_teleport;
	public boolean is_auto_teleport_buy;
	public boolean is_auto_haste;
	public boolean is_auto_haste_buy;
	public boolean is_auto_bravery;
	public boolean is_auto_bravery_buy;
	public boolean is_auto_arrow_buy;
	// 자동사냥 스킬 부분
	// 기사
	public boolean is_auto_skill;
	public boolean is_auto_trunundead;
	public boolean is_auto_reductionarmor;
	public boolean is_auto_solidcarriage;
	public boolean is_auto_counterbarrier;
	// 군주
	public boolean is_auto_glowingweapon;
	public boolean is_auto_shiningshieldon;
	public boolean is_auto_bravemental;
	public boolean is_auto_braveavatar;
	// 다크엘프
	public boolean is_enchantvenom;
	public boolean is_burningspirits;
	public boolean is_shadowarmor;
	public boolean is_doublebrake;
	public boolean is_shadowpong;
	public boolean is_uncannydodge;
	public boolean is_dressmighty;
	public boolean is_dressdexterity;
	public boolean is_dressevasion;
	// 법사
	public boolean is_turnundead;
	public boolean is_snakebite;
	public boolean is_eruption;
	public boolean is_sunburst;
	public boolean is_berserkers;
	public boolean is_Immunity;

	// 요정
	public long auto_hunt_teleport_time;
	public boolean is_auto_resistmagic;
	public boolean is_auto_clearmind;
	public boolean is_auto_resistelement;
	public boolean is_auto_bloodtosoul;
	public boolean is_auto_triplearrow;

	// 달성 선물
	public int pclevel_gift_check;

	static private int[] polygfx;
	// 렉개선
	public long now_Time;
	public long frame_Time;
	public long ai_Time;
	public long damage_action_Time;
	private int currentSkillMotion;
	private long attackTime;
	private long skillTime;
	private int lastAttackMotion;
	private int lastSkillMotion;
	private long skillAvailable; // 추가된 필드
	private int currentAttackMotion;
	private int currentSkillId;

	// 퀘스트
	private int questchapter;
	private int questkill;

	private int randomquest;
	private int randomquestkill;
	private int randomquestfinish;
	private int randomquestplay;
	
	//자동판매
	public boolean isAutoSell;
	public List<String> isAutoSellList = new ArrayList<String>();
	public boolean isAutoSellAdding;
	public boolean isAutoSellDeleting;
	public boolean isAutoSelluser;

	public PcInstance(LineageClient client) {
		this.client = client;
		listBlockName = new ArrayList<String>();
		marketPrice = new ArrayList<marketPrice>();
		swap = new HashMap<String, Swap[]>();
		swapIdx = new String[Lineage.SLOT_ARROW];
		autoPotionIdx = new String[20];

		aStar = new AStar();
		iPath = new int[2];

		isAutoSellList = new ArrayList<String>();
	}

	@Override
	public void close() {
		super.close();
		dmglimitcheck = 0;
		mdmglimitcheck = 0;
		dayptime = daycount = dayptime = checkmenttime = trank = 0;
		auto_save_time = 0;
		age = accountUid = tempClanId = tempClanGrade = battleTeam = lastRankClass = rank = giran_dungeon_time = lastpvpRankClass = pvprank = 0;
		tempPoly = member = isBaphomet = isTeamBattleDead = isBattlezone = isSealBuff = isOmanBuff1 = isOmanBuff2 = isOmanBuff3 = isOmanBuff4 = isOmanBuff5 = isOmanBuff6 = isOmanBuff7 = false;
		isOmanBuff8 = isOmanBuff9 = isOmanBuff10 = auto_pickup = is_hpbar = false;
		chattingWhisper = chattingGlobal = chattingTrade = isAutoPickMessage = isPartyMent = isSound = true;
		lost_exp = premium_item_time = register_date = join_date = message_time = PkTime = partyid = attribute = PkCount = gm = elixir = Seal_Level = SupplyCount = 0;
		dynamicExp = baphometLevel = autoHuntMonsterCount = 0;
		checkmenttime = 0;
		inv = null;

		npc_esmereld = null;
		db_interface = null;
		tempPolyScroll = null;
		accountId = null;
		magicDoll = null;
		magicDollinstance = null;
		tempObject = null;
		tempShop = null;
		pcTrade = null;
		tempName = tempClanName = tempTitle = autoHuntAnswer = null;
		icon = true;
		is_save = false;
		lastLackTime = autoHuntAnswerTime = 0L;
		infoName = infoPhoneNum = infoBankName = infoBankNum = null;
		giran_dungeon_count = 0;
		PcMarket_Step = 0;
		PcMarket_Count = 0;
		auto_count = 0;
		chatcount = 0;
		daycount = 0;
		playdead = 0;
		daycheck = 0;
		checkTimes = 0;
		checkTime = 0;
		checkaccess = 0;
		testTime2 = 0;
		testTime = 0;
		SupplyTime = 0;
		damage_action_Time = 0;
		pclevel_gift_check = 0;
		if (listBlockName != null)
			listBlockName.clear();
		if (marketPrice != null)
			marketPrice.clear();

		if (swap != null)
			swap.clear();
		swapIdx = null;
		selectSwap = null;
		isInsertSwap = false;
		isAutoSelluser = isAutoSell = isAutoSellAdding = isAutoSellDeleting = false;
		
		open_wait_item_time = 0;
		questchapter = questkill = randomquest = randomquestkill = randomquestfinish = randomquestplay = 0;
		auto_hunt_account_time = auto_hunt_time = 0;
		isAutoHunt = is_auto_return_home = is_auto_buff = is_auto_potion_buy = is_auto_poly_select = is_auto_rank_poly = is_auto_rank_poly_buy = is_auto_poly = is_auto_poly_buy = false;
		is_auto_teleport = is_auto_haste = is_auto_haste_buy = is_auto_bravery = is_auto_bravery_buy = false;
		is_auto_trunundead = is_auto_reductionarmor = is_auto_solidcarriage = is_auto_counterbarrier = is_auto_skill = is_auto_glowingweapon = is_auto_shiningshieldon = is_auto_bravemental = is_auto_braveavatar = false;
		is_enchantvenom = is_burningspirits = is_shadowarmor = is_doublebrake = is_shadowpong = is_uncannydodge = is_dressmighty = is_dressdexterity = is_dressevasion = false;
		is_turnundead = is_snakebite = is_eruption = is_sunburst = is_berserkers = is_Immunity= is_auto_triplearrow = is_auto_bloodtosoul = is_auto_resistelement = is_auto_clearmind = is_auto_resistmagic= false;
		is_auto_arrow_buy = false;
		autohunt_target = null;
		start_x = start_y = start_map = temp_x = temp_y = temp_map = auto_return_home_hp = 0;
		auto_buff_time = auto_hunt_teleport_time = 0L;

		if (isAutoSellList != null)
			isAutoSellList.clear();
		
		if (aStar != null)
			aStar.cleanTail();

		MotionClose();
	}


	public Map<String, Swap[]> getSwap() {
		synchronized (swap) {
			return swap;
		}
	}

	// f1상점
	public object getTempShop() {
		return tempShop;
	}

	public void setTempShop(object tempShop) {
		this.tempShop = tempShop;
	}

	public void setSwap(Map<String, Swap[]> swap) {
		this.swap = swap;
	}

	public int getGiran_dungeon_count() {
		return giran_dungeon_count;
	}

	public void setGiran_dungeon_count(int giran_dungeon_count) {
		this.giran_dungeon_count = giran_dungeon_count;
	}

	public int getAuto_count() {
		return auto_count;
	}

	public void setAuto_count(int auto_count) {
		this.auto_count = auto_count;
	}

	public int getCheckaccess() {
		return checkaccess;
	}

	public void setCheckaccess(int checkaccess) {
		this.checkaccess = checkaccess;
	}

	public int getDayptime() {
		return dayptime;
	}

	public void setDayptime(int dayptime) {
		this.dayptime = dayptime;
	}

	public int getDaycount() {
		return daycount;
	}

	public void setDaycount(int daycount) {
		this.daycount = daycount;
	}

	public int getDaycheck() {
		return daycheck;
	}

	public void setDaycheck(int daycheck) {
		this.daycheck = daycheck;
	}

	public int getPclevel_gift_check() {
		return pclevel_gift_check;
	}

	public void setPclevel_gift_check(int pclevel_gift_check) {
		this.pclevel_gift_check = pclevel_gift_check;
	}

	public PcTrade getPcTrade() {
		return pcTrade;
	}

	public void setPcTrade(PcTrade pcTrade) {
		this.pcTrade = pcTrade;
	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}

	public String getInfoPhoneNum() {
		return infoPhoneNum;
	}

	public void setInfoPhoneNum(String infoPhoneNum) {
		this.infoPhoneNum = infoPhoneNum;
	}

	public String getInfoBankName() {
		return infoBankName;
	}

	public void setInfoBankName(String infoBankName) {
		this.infoBankName = infoBankName;
	}

	public String getInfoBankNum() {
		return infoBankNum;
	}

	public void setInfoBankNum(String infoBankNum) {
		this.infoBankNum = infoBankNum;
	}

	public long getAutoHuntAnswerTime() {
		return autoHuntAnswerTime;
	}

	public void setAutoHuntAnswerTime(long autoHuntAnswerTime) {
		this.autoHuntAnswerTime = autoHuntAnswerTime;
	}

	public String getAutoHuntAnswer() {
		return autoHuntAnswer;
	}

	public void setAutoHuntAnswer(String autoHuntAnswer) {
		this.autoHuntAnswer = autoHuntAnswer;
	}

	public int getAutoHuntMonsterCount() {
		return autoHuntMonsterCount;
	}

	public void setAutoHuntMonsterCount(int autoHuntMonsterCount) {
		this.autoHuntMonsterCount = autoHuntMonsterCount;
	}

	@Override
	public int getGm() {
		return gm;
	}

	@Override
	public void setGm(int gm) {
		this.gm = gm;
	}

	public List<String> getListBlockName() {
		return listBlockName;
	}

	public int getPkCount() {
		return PkCount;
	}

	public void setPkCount(int pkCount) {
		PkCount = pkCount;
	}

	
	public int getSupplyCount() {
		return SupplyCount;
	}

	public void setSupplyCount(int supplycount) {
		this.SupplyCount = supplycount;
	}
	
	public double getlevelexp() {
		return Levelexpname;
	}

	public void setlevelexp(double levelexpname) {
		Levelexpname = levelexpname;
	}

	public long getPkTime() {
		return PkTime;
	}

	public void setPkTime(long pkTime) {
		PkTime = pkTime;
	}

	public long getSupplyTime() {
		return SupplyTime;
	}
	public void setSupplyTime(long supplytime) {
		SupplyTime = supplytime;
	}
	
	@Override
	public long getPartyId() {
		return partyid;
	}

	@Override
	public void setPartyId(long partyid) {
		this.partyid = partyid;
	}

	public LineageClient getClient() {
		return client;
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

	@Override
	public void setInventory(Inventory inv) {
		this.inv = inv;
	}

	public boolean isChattingTrade() {
		return chattingTrade;
	}

	public void setChattingTrade(boolean chattingTrade) {
		this.chattingTrade = chattingTrade;
	}

	public boolean isChattingWhisper() {
		return chattingWhisper;
	}

	public void setChattingWhisper(boolean chattingWhisper) {
		this.chattingWhisper = chattingWhisper;
	}

	public boolean isChattingGlobal() {
		return chattingGlobal;
	}

	public void setChattingGlobal(boolean chattingGlobal) {
		this.chattingGlobal = chattingGlobal;
	}

	@Override
	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(final int attribute) {
		this.attribute = attribute;
	}

	public void setNpcEsmereld(Esmereld esmereld) {
		npc_esmereld = esmereld;
	}

	public Esmereld getNpcEsmereld() {
		return npc_esmereld;
	}

	public boolean isTeamBattleDead() {
		return isTeamBattleDead;
	}

	public void setTeamBattleDead(boolean isBattleRoyalDead) {
		this.isTeamBattleDead = isBattleRoyalDead;
	}

	public int getBattleTeam() {
		return battleTeam;
	}

	public void setBattleTeam(int battleTeam) {
		this.battleTeam = battleTeam;
	}

	public int getTempClanId() {
		return tempClanId;
	}

	public void setTempClanId(int tempClanId) {
		this.tempClanId = tempClanId;
	}

	public String getTempClanName() {
		return tempClanName;
	}

	public void setTempClanName(String tempClanName) {
		this.tempClanName = tempClanName;
	}

	public int getTempClanGrade() {
		return tempClanGrade;
	}

	public void setTempClanGrade(int tempClanGrade) {
		this.tempClanGrade = tempClanGrade;
	}

	public String getTempTitle() {
		return tempTitle;
	}

	public void setTempTitle(String tempTitle) {
		this.tempTitle = tempTitle;
	}

	public boolean isIcon() {
		return icon;
	}

	public void setIcon(boolean icon) {
		this.icon = icon;
	}

	public MagicDollInstance getMagicDollinstance() {
		return magicDollinstance;
	}

	public void setMagicDollinstance(MagicDollInstance magicDollinstance) {
		this.magicDollinstance = magicDollinstance;
	}

	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	public MagicDoll getMagicDoll() {
		return magicDoll;
	}

	public void setMagicDoll(MagicDoll magicDoll) {
		this.magicDoll = magicDoll;
	}

	public int getBaphometLevel() {
		return baphometLevel;
	}

	public void setBaphometLevel(int baphometLevel) {
		this.baphometLevel = baphometLevel;
	}

	public boolean isBaphomet() {
		return isBaphomet;
	}

	public void setBaphomet(boolean isBaphomet) {
		this.isBaphomet = isBaphomet;
	}

	public long getLastLackTime() {
		return lastLackTime;
	}

	public void setLastLackTime(long lastLackTime) {
		this.lastLackTime = lastLackTime;
	}

	public boolean isPartyMent() {
		return this.isPartyMent;
	}

	public void setPartyMent(boolean paramBoolean) {
		this.isPartyMent = paramBoolean;
	}
	
	public boolean isSound() {
		return isSound;
	}

	public void setSound(boolean isSound) {
		this.isSound = isSound;
	}
	
	public boolean isAutoPickMessage() {
		return isAutoPickMessage;
	}

	public void setAutoPickMessage(boolean isAutoPickMessage) {
		this.isAutoPickMessage = isAutoPickMessage;
	}

	public int getAccountUid() {
		return accountUid;
	}

	public void setAccountUid(int accountUid) {
		this.accountUid = accountUid;
	}

	public boolean isMember() {
		return member;
	}

	public void setMember(boolean member) {
		this.member = member;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public ItemInstance getTempPolyScroll() {
		return tempPolyScroll;
	}

	public void setTempPolyScroll(ItemInstance tempPolyScroll) {
		this.tempPolyScroll = tempPolyScroll;
	}

	public boolean isTempPoly() {
		return tempPoly;
	}

	public void setTempPoly(boolean tempPoly) {
		this.tempPoly = tempPoly;
	}

	public double getDynamicExp() {
		return dynamicExp;
	}

	public void setDynamicExp(double dynamicExp) {
		this.dynamicExp = dynamicExp;
	}

	public long getRegisterDate() {
		return register_date;
	}

	public void setRegisterDate(long register_date) {
		this.register_date = register_date;
	}

	public long getJoinDate() {
		return join_date;
	}

	public void setJoinDate(long join_date) {
		this.join_date = join_date;
	}

	public double getLostExp() {
		return lost_exp;
	}

	public void setLostExp(double lost_exp) {
		this.lost_exp = lost_exp;
	}

	@Override
	public void setAge(int a) {
		age = a;
	}

	@Override
	public int getAge() {
		return age;
	}

	public int getGiran_dungeon_time() {
		return giran_dungeon_time;
	}

	public void setGiran_dungeon_time(int giran_dungeon_time) {
		this.giran_dungeon_time = giran_dungeon_time;
	}

	@Override
	public void setLawful(int lawful) {
		super.setLawful(lawful);
		if (!worldDelete)
			toSender(S_ObjectLawful.clone(BasePacketPooling.getPool(S_ObjectLawful.class), this), true);
	}

	@Override
	public void setNowHp(int nowhp) {
		super.setNowHp(nowhp);
		if (!worldDelete) {
			toSender(S_CharacterHp.clone(BasePacketPooling.getPool(S_CharacterHp.class), this));

			PartyController.toUpdate(this);

			if (TeamBattleController.checkList(this) && !isTeamBattleDead())
				TeamBattleController.hpUpdate(this);
		}
	}

	@Override
	public void setNowMp(int nowmp) {
		super.setNowMp(nowmp);
		if (!worldDelete)
			toSender(S_CharacterMp.clone(BasePacketPooling.getPool(S_CharacterMp.class), this));
	}

	@Override
	public void setLight(int light) {
		super.setLight(light);
		if (!worldDelete)
			toSender(S_ObjectLight.clone(BasePacketPooling.getPool(S_ObjectLight.class), this), true);
	}

	@Override
	public void setInvis(boolean invis) {
		super.setInvis(invis);
		if (!worldDelete)
			toSender(S_ObjectInvis.clone(BasePacketPooling.getPool(S_ObjectInvis.class), this), true);

		if (getMagicDollinstance() != null)
			getMagicDollinstance().setInvis(invis);
	}

	@Override
	public void setFood(int food) {
		super.setFood(food);
		if (!worldDelete)
			toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public void setAttacking(boolean attacking) {
		if (isAttacking != attacking) {
			isAttacking = attacking;
			if (!isAttacking && !isUsingTripleArrow()) { // 트리플 애로우 사용 중이 아닐 때만
															// 자동 공격을 중지합니다.
				resetAutoAttack(); // 자동 공격 중지
			}
		}
	}

	public void setUsingTripleArrow(boolean usingTripleArrow) {
		isUsingTripleArrow = usingTripleArrow;
	}

	public boolean isUsingTripleArrow() {
		return isUsingTripleArrow;
	}

	public void setTripleArrowFinished(boolean value) {
		this.tripleArrowFinished = value;
	}

	public boolean isTripleArrowFinished() {
		return this.tripleArrowFinished;
	}

	public boolean isAttackCancelled() {
		return attackCancelled;
	}

	public void setAttackCancelled(boolean attackCancelled) {
		this.attackCancelled = attackCancelled;
	}

	private void MotionClose() {
		attackTime = 0;
		skillTime = 0;
		lastAttackMotion = 0;
		lastSkillMotion = 0;
	}

	public int getLastAttackMotion() {
		return lastAttackMotion;
	}

	public void setLastAttackMotion(int lastAttackMotion) {
		this.lastAttackMotion = lastAttackMotion;
	}

	public int getLastSkillMotion() {
		return lastSkillMotion;
	}

	public void setLastSkillMotion(int lastSkillMotion) {
		this.lastSkillMotion = lastSkillMotion;
	}

	public long getSkillTime() {
		return skillTime;
	}

	public void setSkillTime(long skillTime) {
		this.skillTime = skillTime;
	}

	public long getAttackTime() {
		return attackTime;
	}

	public void setAttackTime(long attackTime) {
		this.attackTime = attackTime;
	}

	public void setCurrentSkillMotion(int motion) {
		currentSkillMotion = motion;
	}

	public int getCurrentSkillMotion() {
		return currentSkillMotion;
	}

	public int getCurrentAttackMotion() {
		return currentAttackMotion;
	}

	public void setCurrentAttackMotion(int currentAttackMotion) {
		this.currentAttackMotion = currentAttackMotion;
	}

	public boolean isSkillAvailable() {
		long currentTime = System.currentTimeMillis();
		return !isLock() && (skillTime <= currentTime || lastSkillMotion != currentSkillMotion);
	}

	public long getSkillAvailable() {
		return skillAvailable;
	}

	public void setSkillAvailable(long skillAvailable) {
		this.skillAvailable = skillAvailable;
	}

	public int getCurrentSkillId() {
		return currentSkillId;
	}

	public void setCurrentSkillId(int skillId) {
		this.currentSkillId = skillId;
	}

	public void setAutoAttackTime(long autoAttackTime) {
		this.autoAttackTime = autoAttackTime;
	}

	public long getAutoAttackTime() {
		return autoAttackTime;
	}

	public void useShockStunSkill() {
		long currentTime = System.currentTimeMillis();
		setSkillTime(currentTime); // 스킬 사용 시간을 현재 시간으로 설정

		// 공격 시간이 스킬 사용 시간보다 이전이라면, 공격 시간을 스킬 사용 시간으로 조정하여 공격이 바로 계속될 수 있도록 함
		if (getAttackTime() < currentTime) {
			setAttackTime(currentTime);
		}
	}

	public int getChatCount() {
		return chatcount;
	}

	public void setChatCount(int chatcount) {
		this.chatcount = chatcount;
	}

	// 쿠베라 몬스터 퀘스트
	public int getQuestChapter() {
		return questchapter;
	}

	public void setQuestChapter(int questchapter) {
		this.questchapter = questchapter;
	}

	public int getQuestKill() {
		return questkill;
	}

	public void setQuestKill(int questkill) {
		this.questkill = questkill;
	}

	public int getRadomQuest() {
		return randomquest;
	}

	public void setRadomQuest(int randomquest) {
		this.randomquest = randomquest;
	}

	public int getRandomQuestkill() {
		return randomquestkill;
	}

	public void setRandomQuestkill(int randomquestkill) {
		this.randomquestkill = randomquestkill;
	}

	public int getRandomQuestCount() {
		return randomquestfinish;
	}

	public void setRandomQuestCount(int randomquestfinish) {
		this.randomquestfinish = randomquestfinish;
	}

	public int getRandomQuestPlay() {
		return randomquestplay;
	}

	public void setRandomQuestPlay(int randomquestplay) {
		this.randomquestplay = randomquestplay;
	}
	public int getElixir() {
		return elixir;
	}

	public void setElixir(int elixir) {
		this.elixir = elixir;
	}

	@Override
	public void toDamage(Character cha, int dmg, int type, Object... opt) {
		// 버그 방지 및 자기자신이 공격햇을경우 무시.
		if (cha == null || cha.getObjectId() == getObjectId() || dmg <= 0)
			return;

		checkAutoPotionPvP(cha);

		// 소환객체에게 알리기.
		SummonController.toDamage(this, cha, dmg);

		// 공격자가 사용자일때 구간.
		if (cha instanceof PcInstance) {
			// 보라도리로 변경하기.
			if (Lineage.server_version > 160 && World.isNormalZone(getX(), getY(), getMap()))
				Criminal.init(cha, this);
			// 경비병에게 도움요청.
			DamageController.toGuardHelper(this, cha);
		}
	}

	@Override
	public void setExp(double exp) {
		if (isDead()) {
			// 경험치 하향시키려 할때만.
			if (getExp() > exp)
				super.setExp(exp);
			return;
		}

		// 경험치 버그 처리
		Exp tempExp = ExpDatabase.find(getLevel() - 1);

		if (tempExp != null && exp < tempExp.getBonus()) {
			exp = tempExp.getBonus();
		}

		Exp max = ExpDatabase.find(Lineage.level_max);
		Exp max_prev = ExpDatabase.find(Lineage.level_max);

		if (max != null && max_prev != null && exp > 0 && level <= max.getLevel()) {
			Exp e = ExpDatabase.find(level);

			if (max_prev.getBonus() > exp)
				super.setExp(exp);
			else
				super.setExp(max_prev.getBonus() - 0.01);

			if (e != null) {
				boolean lvUp = e.getBonus() <= getExp();
				if (lvUp) {
					int hp = CharacterController.toStatusUP(this, true);
					int mp = CharacterController.toStatusUP(this, false);
					for (int i = 1; i <= Lineage.level_max; i++) {
						e = ExpDatabase.find(i);
						if (getExp() < e.getBonus())
							break;
					}
					for (int i = e.getLevel() - level; i > 1; i--) {
						hp += CharacterController.toStatusUP(this, true);
						mp += CharacterController.toStatusUP(this, false);
					}
					int new_hp = getMaxHp() + hp;
					int new_mp = getMaxMp() + mp;

					switch (classType) {
					case Lineage.LINEAGE_CLASS_ROYAL:
						if (new_hp >= Lineage.royal_max_hp)
							new_hp = Lineage.royal_max_hp;
						if (new_mp >= Lineage.royal_max_mp)
							new_mp = Lineage.royal_max_mp;
						break;
					case Lineage.LINEAGE_CLASS_KNIGHT:
						if (new_hp >= Lineage.knight_max_hp)
							new_hp = Lineage.knight_max_hp;
						if (new_mp >= Lineage.knight_max_mp)
							new_mp = Lineage.knight_max_mp;
						break;
					case Lineage.LINEAGE_CLASS_ELF:
						if (new_hp >= Lineage.elf_max_hp)
							new_hp = Lineage.elf_max_hp;
						if (new_mp >= Lineage.elf_max_mp)
							new_mp = Lineage.elf_max_mp;
						break;
					case Lineage.LINEAGE_CLASS_WIZARD:
						if (new_hp >= Lineage.wizard_max_hp)
							new_hp = Lineage.wizard_max_hp;
						if (new_mp >= Lineage.wizard_max_mp)
							new_mp = Lineage.wizard_max_mp;
						break;
					case Lineage.LINEAGE_CLASS_DARKELF:
						if (new_hp >= Lineage.darkelf_max_hp)
							new_hp = Lineage.darkelf_max_hp;
						if (new_mp >= Lineage.darkelf_max_mp)
							new_mp = Lineage.darkelf_max_mp;
						break;
					case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
						if (new_hp >= Lineage.dragonknight_max_hp)
							new_hp = Lineage.dragonknight_max_hp;
						if (new_mp >= Lineage.dragonknight_max_mp)
							new_mp = Lineage.dragonknight_max_mp;
						break;
					case Lineage.LINEAGE_CLASS_BLACKWIZARD:
						if (new_hp >= Lineage.blackwizard_max_hp)
							new_hp = Lineage.blackwizard_max_hp;
						if (new_mp >= Lineage.blackwizard_max_mp)
							new_mp = Lineage.blackwizard_max_mp;
						break;
					}

					setMaxHp(new_hp);
					setMaxMp(new_mp);
					setNowHp(getNowHp() + hp);
					setNowMp(getNowMp() + mp);
					setLevel(e.getLevel());

					toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
					PluginController.init(PcInstance.class, "toLevelup", this);
				} else {
					toSender(S_CharacterExp.clone(BasePacketPooling.getPool(S_CharacterExp.class), this));
				}
			}
		}
	}

	@Override
	public void toDead(Character cha) {

		ClanController.toDead(this);
		MagicDollController.toDead(this);
		WantedController.toDead(cha, this);
	}
	/**
	 * 베이스 스탯이 75를 충족하지 못할경우 스탯찍는창을 띄움.
	 * 
	 * @param packet
	 * @return
	 */
	public boolean toBaseStat(boolean packet) {
		//
		if (Lineage.server_version < 163)
			return false;
		// 베이스 스탯 확인.
		int total = getStr() + getDex() + getCon() + getInt() + getCha() + getWis();
		if (total < 75) {
			if (packet)
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "RaiseAttr"));
			return true;
		}
		//
		return false;
	}
	/**
	 * 50이상 레벨 보너스스탯 지급해야 하는지, 체크용 메서드
	 */


public boolean toLvStat(boolean packet) {
		
		if (Lineage.server_version < 163)
			return false;
		
		if (getLevelUpStat() > 0 || getResetBaseStat() > 0 || getResetLevelStat() > 0) {
			if (packet) {
				List<String> point = new ArrayList<String>();
				
				point.add(String.format("초기화 기초 스탯 [%d]개", getResetBaseStat()));
				point.add(String.format("초기화 레벨업 스탯 [%d]개", getResetLevelStat()));
				point.add(String.format("레벨업 스탯 [%d]개", getLevelUpStat()));
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "RaiseAttr", null, point));
			}
			return true;
		}

		return false;
	}

	/**
	 * 사용자 정보 저장 처리 함수.
	 */
	private void toSave() {
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			toSave(con);
		} catch (Exception e) {
			lineage.share.System.println(PcInstance.class.toString() + " : toSave()");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}

	/**
	 * gui에서 사용중
	 * 
	 * @param
	 * @return 2017-09-06 by all_night.
	 */
	public void toCharacterSave() {
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			toSave(con);
		} catch (Exception e) {
			lineage.share.System.println(PcInstance.class.toString() + " : toSave()");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}

	/**
	 * CharacterThread에서 주기적으로 모든 캐릭터 저장하는 함수. 2018-05-10 by all-night.
	 */

	public void autoSave(Connection con, long time) {
		if (Lineage.auto_save_time > 0 && auto_save_time <= time && getMap() != Lineage.teamBattleMap && !is_save) {
			// 월드 접속하고 타이머에 등록된후 해당 함수가 호출됫을때 auto_save_time 값이 0 임.
			// 접속하자마자 저장하는걸 방지하기위해 아래 코드 삽입.
			if (auto_save_time != 0)
				toSave(con);
			auto_save_time = time + Lineage.auto_save_time;
		}
	}

	/**
	 * 자동 저장 버그 방지. 2019-07-09 by connector12@nate.com
	 */
	public void setAutoSaveTime(boolean isWorldJoin) {
		if (isWorldJoin)
			auto_save_time = System.currentTimeMillis() + (1000 * 5);
		else
			auto_save_time = 0;
	}

	/**
	 * 팀대전 오류시 캐릭터 정보 날아가는 현상 방지. 2019-06-28 by connector12@nate.com
	 */
	public void checkTeamBattle(boolean isRead) {
		if (isRead) {
			// 캐릭터 접속시 체크.
			if (getBattleTeam() != 0) {
				setBattleTeam(0);

				if (getTempName() != null)
					setName(getTempName());

				if (getTempClanName() != null)
					setClanName(getTempClanName());

				if (getTempTitle() != null)
					setTitle(getTempTitle());

				setClanId(getTempClanId());
				setClanGrade(getTempClanGrade());

				setGfx(getClassGfx());

				if (getInventory() != null && getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
					setGfxMode(getClassGfxMode() + getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
				else
					setGfxMode(getClassGfxMode());

				int[] loc = Lineage.getHomeXY();
				setX(loc[0]);
				setY(loc[1]);
				setMap(loc[2]);
			}
		} else {
			// 팀대전이 정상 종료 되지않았을 경우 체크.
			if (getBattleTeam() != 0 && !TeamBattleController.startTeamBattle && !TeamBattleController.askTeamBattle) {
				setBattleTeam(0);

				if (getTempName() != null)
					setName(getTempName());

				if (getTempClanName() != null)
					setClanName(getTempClanName());

				if (getTempTitle() != null)
					setTitle(getTempTitle());

				setClanId(getTempClanId());
				setClanGrade(getTempClanGrade());

				setGfx(getClassGfx());

				if (getInventory() != null && getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
					setGfxMode(getClassGfxMode() + getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
				else
					setGfxMode(getClassGfxMode());

				int[] loc = Lineage.getHomeXY();
				toPotal(loc[0], loc[1], loc[2]);
			}
		}
	}

	/**
	 * 사용자 정보 저장 처리 함수.
	 */
	public void toSave(Connection con) {

		// 저장
		try {
			CharactersDatabase.saveInventory(con, this);

		} catch (Exception e) {
			lineage.share.System.println("saveInventory | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			CharactersDatabase.saveSkill(con, this);
		} catch (Exception e) {
			lineage.share.System.println("saveSkill | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			CharactersDatabase.saveBuff(con, this);
		} catch (Exception e) {
			lineage.share.System.println("saveBuff | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			CharactersDatabase.saveBook(con, this);
		} catch (Exception e) {
			lineage.share.System.println("saveBook | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			CharactersDatabase.saveCharacter(con, this);
		} catch (Exception e) {
			lineage.share.System.println("saveCharacter | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			CharactersDatabase.saveMember(con, this);
		} catch (Exception e) {
			lineage.share.System.println("saveMember | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			CharactersDatabase.saveBlockList(con, this);
		} catch (Exception e) {
			lineage.share.System.println("saveBlockList | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			SummonController.toSave(con, this);
		} catch (Exception e) {
			lineage.share.System.println("SummonController.toSave | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		try {
			FriendController.toSave(con, this);
		} catch (Exception e) {
			lineage.share.System.println("FriendController.toSave | 캐릭명: " + getName());
			lineage.share.System.println(e);
		}

		saveSwap(con);
	}

	/**
	 * 월드 진입할때 호출됨.
	 */
	public void toWorldJoin() {
		// 버그 방지.
		if (!isWorldDelete())
			return;

		// 인터페이스 전송. db_interface
		if (db_interface != null)
			toSender(S_InterfaceRead.clone(BasePacketPooling.getPool(S_InterfaceRead.class), db_interface));

		FishingController.toWorldJoin(this);

		// 메모리 세팅
		setAutoPickup(Lineage.auto_pickup);
		World.appendPc(this);
		BookController.toWorldJoin(this);
		ClanController.toWorldJoin(this);
		InventoryController.toWorldJoin(this);
		SkillController.toWorldJoin(this);
		CharacterController.toWorldJoin(this);
		TradeController.toWorldJoin(this);
		BuffController.toWorldJoin(this);
		SummonController.toWorldJoin(this);
		QuestController.toWorldJoin(this);
		ChattingController.toWorldJoin(this);
		MagicDollController.toWorldJoin(this);
		// 운영자일때 정보 갱신
		// 기억리스트 추출 및 전송
		CharactersDatabase.readBook(this);
		// 스킬 추출 및 전송
		CharactersDatabase.readSkill(this);
		// 인벤토리 추출 및 전송
		CharactersDatabase.readInventory(this);
		// 인벤 기준 무기 모션 동기화 (DB 저장 gfxMode 잔존값으로 무장 모션 고착되는 현상 방지)
		if (getInventory() != null && getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
			setGfxMode(getClassGfxMode() + getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode());
		else
			setGfxMode(getClassGfxMode());
		// 차단 리스트 추출 및 전송
		CharactersDatabase.readBlockList(this);
		// 팀대전 오류 확인.
		checkTeamBattle(true);
		// 케릭터 정보 전송
		toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
		// 월드 스폰.
		super.toTeleport(x, y, map, false);
		// 상태에따라 패킷전송 [광전사의 도끼를 착용하고 있을수 있기때문에 추가됨.]
		// 버프 잡아주기
		CharactersDatabase.readBuff(this);
		// 날씨 보내기
		toSender(S_Weather.clone(BasePacketPooling.getPool(S_Weather.class), LineageServer.weather));
		// 맵핵 자동켜짐
		//setMapHack(true);
		//toSender(new S_Ability(3, true));
		// 피바 자동켜짐
		//setHpbar(true);
		//toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), this, isHpbar()));
		// 성처리
		KingdomController.toWorldJoin(this);
		// 편지 확인
		LetterController.toWorldJoin(this);
		// 친구 목록 갱신.
		FriendController.toWorldJoin(this);

		WantedController.toWorldJoin(this);

		// 서버 상태에 따른 처리
		if (Lineage.event_buff)
			CommandController.toBuff(this);
		// %0님께서 방금 게임에 접속하셨습니다.
		if (Lineage.world_message_join)
			World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), null, Lineage.CHATTING_MODE_MESSAGE, String.format("%s님께서 방금 게임에 접속하셨습니다.", getName())));
		//
		PluginController.init(PcInstance.class, "toWorldJoin", this);

		// 고정 멤버 버프
		고정멤버버프(false);

		if (KingdomController.find(this) != null) {
			setDynamicAddDmg(getDynamicAddDmg() + 1);
			setDynamicAddDmgBow(getDynamicAddDmgBow() + 1);
			setDynamicAddHit(getDynamicAddHit() + 1);
			setDynamicAddHitBow(getDynamicAddHitBow() + 1);
			setDynamicSp(getDynamicSp() + 1);
			setDynamicExp(getDynamicExp() + Lineage.kingdom_clan_rate_exp);
			setAddDropAdenRate(getAddDropAdenRate() + Lineage.kingdom_clan_rate_aden);
			setAddDropItemRate(getAddDropItemRate() + Lineage.kingdom_clan_rate_drop);
			ChattingController.toChatting(this, "성혈맹: 추가 대미지+1, 추가 명중+1, SP+1, 경험치/아데나/드랍률+20%", Lineage.CHATTING_MODE_MESSAGE);
		} else {
			if (getClanId() > 3) {
				setDynamicExp(getDynamicExp() + Lineage.clan_rate_exp);
				setAddDropAdenRate(getAddDropAdenRate() + Lineage.clan_rate_aden);
				setAddDropItemRate(getAddDropItemRate() + Lineage.clan_rate_drop);
				ChattingController.toChatting(this, "혈맹: 경험치/아데나/드랍률+10%", Lineage.CHATTING_MODE_MESSAGE);
			}

			if (getGm() == 0 && KingdomController.isKingdomInsideLocation(this)) {
				int[] loc = Lineage.getHomeXY();
				toTeleport(loc[0], loc[1], loc[2], true);
			}
		}

		if (getGm() == 0) {
			Kingdom k = KingdomController.findKingdomLocation(this);
			if (k != null && k.isWar()) {
				int[] loc = Lineage.getHomeXY();
				toTeleport(loc[0], loc[1], loc[2], true);
			}
		}

		AutoHuntCheckController.toWorldJoin(this);

		// sp, mr 갱신.
		toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));

		for (PcInstance master : World.getPcList()) {
			if (master.getGm() > 0 && !master.isWorldDelete() && getObjectId() != master.getObjectId()) {
				ChattingController.toChatting(master, String.format("\\fY[계정: %s   Lv.%d %s] \\fR접속", getAccountId(), getLevel(), getName()), Lineage.CHATTING_MODE_MESSAGE);
				break;
			}
		}

		// 오픈 대기 중일때 메세지 전송
		if (Lineage.open_wait)
			ChattingController.toChatting(this, String.format("%s [오픈 대기중] 입니다.", ServerDatabase.getName()), Lineage.CHATTING_MODE_MESSAGE);

		// 장비 스왑 데이터 로드.
		readSwap();

		// 로그 기록.
		if (Log.isLog(this))
			Log.appendConnect(getRegisterDate(), client.getAccountIp(), client.getAccountId(), getName(), "접속");

	}

	/**
	 * 월드에서 나갈때 호출됨.
	 */
	public void toWorldOut() {

		// 버그 방지.
		if (isWorldDelete())
			return;
		
		restCrackerWorldOut();

		is_save = true;

	

		// 낚시중에 종료시 낚싯대 장착 해제
		if (isFishing()) {
			getInventory().getSlot(Lineage.SLOT_WEAPON).toClick(this, null);
		}
		// 팀대전 중 리스할 경우
		if (TeamBattleController.checkList(this))
			TeamBattleController.removeList(this);

		PluginController.init(PcInstance.class, "toWorldOut", this);

		for (PcInstance master : World.getPcList()) {
			if (master.getGm() > 0 && !master.isWorldDelete()) {
				ChattingController.toChatting(master, String.format("\\fY[계정: %s   Lv.%d %s] \\fR종료", getAccountId(), getLevel(), getName()), Lineage.CHATTING_MODE_MESSAGE);
				break;
			}
		}
		// 로그 기록.
		if (Log.isLog(this))
			Log.appendConnect(getRegisterDate(), client.getAccountIp(), client.getAccountId(), getName(), "종료");
		// 사용자 정보 저장전에 처리해야될 메모리 처리
		TradeController.toWorldOut(this); // 거래중인 아이템 때문에 저장전에 처리.
		// 죽어있을경우에 처리를 위해.
		toReset(true);
		// 에스메랄다 미래보기 상태라면
		if (npc_esmereld != null)
			npc_esmereld.toTeleport(this);
		// 근처 마을로 좌표 변경.
		if (TeleportResetDatabase.toLocation(this) && getGm() == 0) {
			
			World.remove(this);
			if (isDynamicUpdate())
				World.update_mapDynamic(x, y, map, false);
			x = homeX;
			y = homeY;
			map = homeMap;
			
			if (isDynamicUpdate())
				World.update_mapDynamic(x, y, map, true);
			World.append(this);
		}
		
		
		// 성주면에 있는지 확인. 있을경우 근처마을로 좌표 변경.
		Kingdom k = KingdomController.findKingdomLocation(this);
		if (k != null && k.getClanId() > 0 && k.getClanId() != getClanId()) {
			TeleportHomeDatabase.toLocation(this);
			x = homeX;
			y = homeY;
			map = homeMap;
		}

		// 사용자 정보 저장
		toSave();
		savePet();
		
		// 월드에 갱신
		World.remove(this);
		
		CharacterController.toWorldOut(this);
		SummonController.toWorldOut(this);
		PartyController.toWorldOut(this, false);
		QuestController.toWorldOut(this);
		MagicDollController.toWorldOut(this);
		
		// 이전에 관리중이던 목록 제거
		clearList(true);

		// 사용된 메모리 제거
		World.removePc(this);
		
		BookController.toWorldOut(this);
		ClanController.toWorldOut(this);
		SkillController.toWorldOut(this);
		UserShopController.toStop(this);
		ChattingController.toWorldOut(this);
		FriendController.toWorldOut(this);
		InventoryController.toWorldOut(this);
		
//		CharactersDatabase.tosaveInventory(this);

		// 메모리 초기화
		close();
	}

	@Override
	public void toReset(boolean world_out) {
		super.toReset(world_out);
		if (isDead()) {
			// 죽을 경우 이팩트
			//setDeathEffect(false);

			try {
				if (world_out) {
					// 죽은상태로 월드를 나갈경우 좌표를 근처 마을로..
					// 성이 존재한다면 내성으로 (잊섬 제외)
					Kingdom k = KingdomController.find(this);
					// 아지트 확인.
					Agit a = AgitController.find(this);

					if (k != null && map != 70) {
						homeX = k.getX();
						homeY = k.getY();
						homeMap = k.getMap();
					} else if (a != null) {
						homeX = a.getAgitX();
						homeY = a.getAgitY();
						homeMap = a.getAgitMap();
					} else {
						TeleportHomeDatabase.toLocation(this);
					}

					x = homeX;
					y = homeY;
					map = homeMap;
				}

				// 다이상태 풀기.
				setDead(false);

				if (World.isBattleZone(getX(), getY(), getMap())) {
					setNowHp(getTotalHp());
				} else {
					// 체력 채우기.
					setNowHp(level);
					// food게이지 하향.
					if (getFood() > Lineage.MIN_FOOD)
						setFood(Lineage.MIN_FOOD);

					// gfx 복구
					gfx = classGfx;
					if (inv.getSlot(Lineage.SLOT_WEAPON) != null) {
						gfxMode = classGfxMode + inv.getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode();
					} else {
						gfxMode = classGfxMode;
					}
				}
				//
				PluginController.init(PcInstance.class, "toReset", this, world_out);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void toRevival(object o) {
		if (isDead()) {
			// 공성전중 부활이 불가능한지 확인.
			if (!Lineage.kingdom_war_revival) {
				Kingdom k = KingdomController.findKingdomLocation(this);
				if (k != null && k.isWar())
					return;
			}

			temp_object_1 = o;
			toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 321));
		}
	}

	@Override
	public void toRevivalFinal(object o) {
		if (isDead() && temp_object_1 != null) {
			//setDeathEffect(false);
			// 리셋처리.
			super.toReset(false);
			// 다이상태 풀기.
			setDead(false);
			// 체력 채우기.
			setNowHp(temp_hp != -1 ? temp_hp : level);
			setNowMp(temp_mp != -1 ? temp_mp : getNowMp());
			// gfx_mode 복원
			if (inv.getSlot(Lineage.SLOT_WEAPON) != null) {
				gfxMode = classGfxMode + inv.getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode();
			} else {
				gfxMode = classGfxMode;
			}
			// 패킷 처리.
			toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), temp_object_1, this), true);
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 230), true);
			//
			temp_object_1 = null;
			temp_hp = temp_mp = -1;
		}
	}

	/**
	 * 기란감옥 자정 초기화·이용시간 종료 시: 사망/낚시 등을 정리한 뒤 기란 마을 근처로 이동하고 귀환 좌표를 맞춤.
	 */
	public void toReviveAndTeleportForGiranDungeonReset(int x, int y, int map) {
		if (isWorldDelete())
			return;
		if (isAutoHunt)
			endAutoHunt(false, false);
		if (isFishing() && getInventory() != null && getInventory().getSlot(Lineage.SLOT_WEAPON) != null)
			getInventory().getSlot(Lineage.SLOT_WEAPON).toClick(this, null);
		if (isDead()) {
			super.toReset(false);
			setDead(false);
			int hp = Math.max(1, Math.min(getMaxHp(), Math.max(getLevel(), 1)));
			setNowHp(hp);
			if (getMaxMp() > 0)
				setNowMp(getMaxMp());
			try {
				if (inv != null && inv.getSlot(Lineage.SLOT_WEAPON) != null && inv.getSlot(Lineage.SLOT_WEAPON).getItem() != null)
					gfxMode = classGfxMode + inv.getSlot(Lineage.SLOT_WEAPON).getItem().getGfxMode();
				else
					gfxMode = classGfxMode;
			} catch (Exception e) {
				gfxMode = classGfxMode;
			}
			toSender(S_ObjectRevival.clone(BasePacketPooling.getPool(S_ObjectRevival.class), this, this), true);
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 230), true);
		}
		toTeleport(x, y, map, true);
		setHomeX(x);
		setHomeY(y);
		setHomeMap(map);
	}

	// 트리플 애로우 자동칼질 관련부분
	public void triplepart1() {
		if (isAttacking || isUsingTripleArrow()) {
			this.setAttackCancelled(true);
			resetAutoAttack();
			setAttacking(false);
		}

		// 이동 처리 시작 전에 공격 중단 및 자동 공격 리셋을 처리합니다.
		this.setAttackCancelled(true);
		resetAutoAttack();
		setAttacking(false);

	}

	@Override
	public void toMoving(final int x, final int y, final int h) {

		lastMovingTime = System.currentTimeMillis();

		if (isAttacking || isUsingTripleArrow()) {
			// 자동공격중이면 공격 취소
			resetAutoAttack();
			setAttacking(false);
			setUsingTripleArrow(false);
			setTripleArrowFinished(true);
			ai_Time = 0;

		}
		if (Thebes.getInstance().테베문체크(this)) {
			int[] end = Thebes.getInstance().도착점정보();
			toTeleport(end[0], end[1], end[2], true);
			return;
		}
		
		if (!MovingController.isMoveValid(this, lastMovingTime, x, y)) {
			return;
		}
		setMoving(true);
		CharacterController.toMoving(this);

		if (!isAttacking) { // 자동공격중이 아니면 이동 처리
			if (Util.isDistance(this.x, this.y, map, x, y, map, 1) || getGfx() == 369 || isTransparent()) {
				isFrameSpeed(Lineage.GFX_MODE_WALK);

				super.toMoving(x, y, h);

				if (World.get_map(x, y, map) == 127) {
					// 던전 이동 처리
					TimeDungeonDatabase.toMovingDungeon(this);
					if (Lineage.server_version <= 163) {
						// 현재 위치에 성정보 추출.
						Kingdom k = KingdomController.findKingdomLocation(this);
						// 공성중이면서, 옥좌라면 면류관 픽업처리를 통해 성주 변경처리하기.
						if (k != null && k.isWar() && k.isThrone(this)) {
							k.getCrown().toPickup(this);
	                    }
	                }
	            }
			} else {
				toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x04));
				toTeleport(this.x, this.y, map, false);
				toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x05));
			}
		}
	}

	@Override
	public synchronized void toSender(BasePacket bp) {
		if (client != null) {
			client.toSender(bp);
		} else {
			BasePacketPooling.setPool(bp);
		}
	}

	@Override
	public void toPotal(int x, int y, int map) {
		resetAutoAttack();
		// 버그방지.
		if (World.get_map(map) == null) {
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			if (getGm() > 0)
				ChattingController.toChatting(this, map + "맵이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (isFishing()) {
			ChattingController.toChatting(this, "낚시중엔 텔레포트가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		if (LocationController.isBlockedEpisode8TeleportDestination(this, x, y, map, true)) {
			return;
		}
		homeX = x;
		homeY = y;
		homeMap = map;
		toSender(S_Potal.clone(BasePacketPooling.getPool(S_Potal.class), this.map, map));
		// 소환객체 텔레포트
		SummonController.toTeleport(this);
		MagicDollController.toTeleport(this);


	}

	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect) {
		resetAutoAttack();
		// 버그방지.
		if (World.get_map(map) == null) {
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			if (getGm() > 0)
				ChattingController.toChatting(this, map + "맵이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (isFishing()) {
			ChattingController.toChatting(this, "낚시중엔 텔레포트가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		if (LocationController.isBlockedEpisode8TeleportDestination(this, x, y, map, true)) {
			return;
		}
		// 2.00이하 버전에서 텔레포트후 sp, mr 이 정상표현 안되는 문제로 인해 추가.
		// if (Lineage.server_version <= 200)
		// toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class),
		// this));

		super.toTeleport(x, y, map, effect);
		// 소환객체 텔레포트
		SummonController.toTeleport(this);
		MagicDollController.toTeleport(this);
	}

	@Override
	public void toTeleportRange(final int x, final int y, final int map, final boolean effect, int range) {
		resetAutoAttack();
		// 버그방지.
		if (World.get_map(map) == null) {
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			if (getGm() > 0)
				ChattingController.toChatting(this, map + "맵이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (isFishing()) {
			ChattingController.toChatting(this, "낚시중엔 텔레포트가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		if (LocationController.isBlockedEpisode8TeleportDestination(this, x, y, map, true)) {
			return;
		}
		// 2.00이하 버전에서 텔레포트후 sp, mr 이 정상표현 안되는 문제로 인해 추가.
		// if (Lineage.server_version <= 200)
		// toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class),
		// this));

		super.toTeleportRange(x, y, map, effect, range);
		// 소환객체 텔레포트
		SummonController.toTeleport(this);
		MagicDollController.toTeleport(this);
	}

	public void 칼렉풀기() {
		if (isDead()) {
			ChattingController.toChatting(this, "죽은 상태에선 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		} else if (isLock()) {
			ChattingController.toChatting(this, "기절 상태에선 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (lastMovingTime + 1000 > System.currentTimeMillis()) {
			ChattingController.toChatting(this, "이동중에 칼렉풀기는 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (getLastLackTime() < System.currentTimeMillis()) {
			setLastLackTime(System.currentTimeMillis() + (1000 * Lineage.sword_rack_delay));
		} else {
			ChattingController.toChatting(this, String.format("\\fR칼렉풀기의 딜레이는 %d초 입니다.", Lineage.sword_rack_delay), Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		resetAutoAttack();
		// 버그방지.
		if (World.get_map(map) == null) {
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			if (getGm() > 0)
				ChattingController.toChatting(this, map + "맵이 존재하지 않습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		if (isFishing()) {
			ChattingController.toChatting(this, "낚시중엔 텔레포트가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		super.toTeleport(super.getX(), super.getY(), super.getMap(), false);
		// 소환객체 텔레포트
		SummonController.toTeleport(this);
		MagicDollController.toTeleport(this);

		ChattingController.toChatting(this, "칼렉이 풀렸습니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 매개변수 객체 에게물리공격을 가할때 처리하는 메서드.
	 */
	@Override
	public void toAttack(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple) {
		long time = System.currentTimeMillis();

		setAttackCancelled(false);

		if (this.isBuffInvisiBility()) {
			BuffController.remove(this, InvisiBility.class);
		}

		// 인챈트베놈
		Skill skill = SkillDatabase.find(656);
		BuffInterface b = BuffController.find(o, SkillDatabase.find(656));
		if (this.isBuffEnchantVenom()) {

			if (b == null) {

				if (SkillController.isFigure(this, o, skill, true, false)) {
					Venom.init(this, skill, (int) o.getObjectId());
				}

			}
		}

		if (isAttackCancelled()) {
			return; // attackCancelled 변수가 true일 경우 공격을 취소
		}
		if (!isTriple && autoAttackTime > time) {
			return;
		}
		//스핵 
		int gfxmo = 0;
		if(bow)
			gfxmo = 21;
		else
			gfxmo = this.getGfxMode()+1;
		// 자동 칼질
		if (o != null && isAutoAttack && !isTriple) {
			autoAttackTarget = o;
			targetX = o.getX();
			targetY = o.getY();
			autoAttackTime = (long) (System.currentTimeMillis() + (SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), gfxmo)));
		}

		if (!isTriple && !isActionCheck(false)) {
			return;
		}

		int attackAction = 0;
		int effect = 0;
		double dmg = 0;
		ItemInstance weapon = inv.getSlot(Lineage.SLOT_WEAPON);
		ItemInstance arrow = null;
		List<object> insideList = getInsideList();

		setFight(true);

		if (bow && weapon != null) {
			// 잠수용 허수아비 체크.
			if (isRestCracker) {
				arrow = setRestCrackerArrow();

				if (weapon.getItem().getEffect() > 0 && arrow == null) {
					effect = weapon.getItem().getEffect();
				} else {
					if (arrow != null)
						effect = arrow.getItem().getEffect();
				}

				if (arrow == null) {
					if (o instanceof RestCracker)
						endRestCracker();
				}
			} else {
				arrow = weapon.getItem().getType2().equalsIgnoreCase("gauntlet") ? inv.findThrowingKnife() : inv.findArrow();

				if (weapon.getItem().getEffect() > 0 && arrow == null) {
					effect = weapon.getItem().getEffect();
				} else {
					if (arrow != null)
						effect = arrow.getItem().getEffect();
				}
			}
		}

		// 좌표에따라 방향전환.
		heading = Util.calcheading(this, x, y);

		// 자동사냥 방지 확인.
		if (AutoHuntCheckController.checkMonster(o) && !AutoHuntCheckController.checkCount(this))
			return;

		// 장로 변신중이면 콜라이트닝 이팩트 표현.
		if (Lineage.server_version <= 200 && getGfx() == 3879)
			effect = Lineage.call_lighting_effect;

		// 공속 확인.
		if (isTriple || (AttackController.isAttackTime(this, getGfxMode() + Lineage.GFX_MODE_ATTACK, isTriple) || AttackController.isMagicTime(this, getCurrentSkillMotion())) && ((this.isFrameSpeed(gfxmo) && !isTransparent()))) {
			// 죽엇는지 확인.
			if (!isDead()) {
				// 무게 확인. 82% 미만
				if (inv.isWeightPercent(82)) {
					if (isRestCracker) {
						dmg = getRestCrackerDmg(weapon, arrow, o);
					} else {
						if (o instanceof RestCracker) {
							ChattingController.toChatting(this, String.format("\\fY[%s] 소유자만 공격가능.", Lineage.rest_cracker_name), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							// 데미지 추출
							dmg = DamageController.getDamage(this, o, bow, weapon, arrow, 0);
						}
					}

					attackAction = getGfxMode() + Lineage.GFX_MODE_ATTACK;

					// 치명타 이팩트 처리
					if (isCriticalEffect() && weapon != null) {

						if (weapon.getItem().getType2().equalsIgnoreCase("dagger"))
							effect = Lineage.CRITICAL_EFFECT_DAGGER;
						else if (weapon.getItem().getType2().equalsIgnoreCase("sword"))
							effect = Lineage.CRITICAL_EFFECT_SWORD;
						else if (weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
							effect = Lineage.CRITICAL_EFFECT_TWOHAND_SWORD;
						else if (weapon.getItem().getType2().equalsIgnoreCase("edoryu"))
							effect = Lineage.CRITICAL_EFFECT_TWOHAND_AXE;
						else if (weapon.getItem().getType2().equalsIgnoreCase("claw"))
							effect = Lineage.CRITICAL_EFFECT_NONE;
						else if (weapon.getItem().getType2().equalsIgnoreCase("axe") || weapon.getItem().getType2().equalsIgnoreCase("blunt"))
							effect = weapon.getItem().isTohand() ? Lineage.CRITICAL_EFFECT_TWOHAND_AXE : Lineage.CRITICAL_EFFECT_AXE;
						else if (weapon.getItem().getType2().equalsIgnoreCase("spear"))
							effect = Lineage.CRITICAL_EFFECT_SPEAR;
						else if (weapon.getItem().getType2().equalsIgnoreCase("wand") || weapon.getItem().getType2().equalsIgnoreCase("staff"))
							effect = Lineage.CRITICAL_EFFECT_STAFF;
						else
							effect = bow ? 374 : Lineage.CRITICAL_EFFECT_NONE;

//						 this.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 13555), this instanceof PcInstance);
						// 치명타시 특정모션 발동
						if (!isTriple && extraAttackMotion(0, getGfx()) && Util.random(0, 99) < 30) {
							if (!bow) {
								// 랭커 변신은 무기의 종류에 따라서 특정모션이 다르기때문에 다르게 처리
								if (extraAttackMotion(1, getGfx())) {
									if (effect == Lineage.CRITICAL_EFFECT_DAGGER)
										attackAction = 74;
									if (effect == Lineage.CRITICAL_EFFECT_SWORD || effect == Lineage.CRITICAL_EFFECT_STAFF)
										attackAction = 76;
									if (effect == Lineage.CRITICAL_EFFECT_TWOHAND_SWORD || effect == Lineage.CRITICAL_EFFECT_AXE || effect == Lineage.CRITICAL_EFFECT_TWOHAND_AXE
											|| effect == Lineage.CRITICAL_EFFECT_SPEAR) {
										if (getGfx() == 411 || getGfx() == 419)
											attackAction = 75;
										else
											attackAction = 79;
									}
								} else {
									// 군주
									if ((getGfx() == Lineage.royal_male_gfx || getGfx() == Lineage.royal_female_gfx) && effect == Lineage.CRITICAL_EFFECT_SWORD)
										attackAction = 55;
									// 기사
									if ((getGfx() == Lineage.knight_male_gfx || getGfx() == Lineage.knight_female_gfx) && effect == Lineage.CRITICAL_EFFECT_SWORD)
										attackAction = 55;
									if ((getGfx() == Lineage.knight_male_gfx || getGfx() == Lineage.knight_female_gfx) && effect == Lineage.CRITICAL_EFFECT_TWOHAND_SWORD)
										attackAction = 56;
								}
							} else if (bow && extraAttackMotion(5, getGfx())) {
								attackAction = 75;
							}
						}
					}

					if (dmg > 0) {
						// 무기에 따른 처리.
						if (weapon != null) {
							// 무기에게 공격했다는거 알리기.
							if (weapon.toDamage(this, o)) {
								dmg += weapon.toDamage((int) Math.round(dmg));
								effect = weapon.toDamageEffect();
								setCriticalEffect(false);

								if (bow) {
									if (effect > 0 && effect != 6288) {
										ServerBasePacket sbp = (ServerBasePacket) (S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect));

										if (무기이펙트)
											toSender(sbp);

										if (insideList != null) {
											// 주위의 유저에게 패킷 보냄.
											for (object oo : insideList) {
												if (oo instanceof PcInstance) {
													if (oo.무기이펙트)
														oo.toSender(ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()));
												}
											}
										}
									}

									if (bow && weapon != null) {
										arrow = weapon.getItem().getType2().equalsIgnoreCase("gauntlet") ? inv.findThrowingKnife() : inv.findArrow();

										if (weapon.getItem().getEffect() > 0 && arrow == null) {
											effect = weapon.getItem().getEffect();
										} else {
											if (effect != 6288) {
												if (arrow != null)
													effect = arrow.getItem().getEffect();
											}
										}
									}
								}
							}

							if (o != null && o instanceof PcInstance && weapon != null && weapon.getItem() != null && !isAutoAttack) {
								int range = 1;

								switch (weapon.getItem().getType2()) {
								case "bow":
									range = Lineage.SEARCH_LOCATIONRANGE;
									break;
								case "spear":
									range = 2;
									break;
								}

								if (!Util.isDistance(this, o, range)) {
									if (effect != 66)
										effect = 0;

									dmg = 1;
								}
							}

							// 트리플 중첩시 데미지 감소.
							if (dmg > 0 && isTriple && Lineage_Balance.triple_damage_reduction > 0 && o != null && o instanceof PcInstance) {
								if (0 < o.tripleAttackObjId) {
									if (System.currentTimeMillis() < o.tripleDamageTime) {
										if (o.tripleAttackObjId != getObjectId())
											dmg = dmg * Lineage_Balance.triple_damage_reduction;
									} else {
										o.tripleAttackObjId = 0;
									}
								} else {
									o.tripleAttackObjId = getObjectId();
									o.tripleDamageTime = System.currentTimeMillis() + (int) (Lineage_Balance.triple_damage_reduction_time);
								}
							}
						}
						// 데미지 처리하는 구간

						DamageController.toDamage(this, o, isTriple ? (int) Math.round(dmg * Lineage_Balance.triple_arrow_damage) : (int) Math.round(dmg), bow ? Lineage.ATTACK_TYPE_BOW : Lineage.ATTACK_TYPE_WEAPON);
					}
					// 현재 시간
					now_Time = System.currentTimeMillis();

					// 프레임 시간
					frame_Time = SpriteFrameDatabase.getGfxFrameTime(this, this.getGfx(), isTriple ? (attackAction == Lineage.ACTION_TRIPLE_ARROW_2 ? attackAction : gfxMode) : attackAction);

					// 트리플 에로우 밀림 현상 개선
					boolean shouldAttack = false;
					if (getClassType() == Lineage.LINEAGE_CLASS_ELF && isTriple) {
						if (now_Time > ai_Time || now_Time - ai_Time < -345) {
							shouldAttack = true;
						}
					} else {
						if (ai_Time <= now_Time) {
							shouldAttack = true;
							ai_Time = System.currentTimeMillis() + (frame_Time - 5);
						}
					}

					if (shouldAttack) {
						toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, isTriple ? (attackAction == Lineage.ACTION_TRIPLE_ARROW_2 ? attackAction : gfxMode) : attackAction,
								(int) Math.round(dmg), effect, bow, effect > 0, x, y), true);
					}

					if (Lineage_Balance.dmg_limit && getGm() == 0) {
						boolean dmglimit = false;

						switch (getClassType()) {
						case Lineage.LINEAGE_CLASS_ROYAL:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						case Lineage.LINEAGE_CLASS_KNIGHT:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						case Lineage.LINEAGE_CLASS_ELF:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						case Lineage.LINEAGE_CLASS_WIZARD:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						}
						if (dmglimit) {

							String log = String.format("[데미지 초과] -> [캐릭터: %s]  [데미지: %.0f]", getName(), dmg);

							GuiMain.display.asyncExec(new Runnable() {
								public void run() {
									GuiMain.getViewComposite().getDamageCheckComposite().toLog(log);
								}
							});

							FrameSpeedOverStun.init(this, Lineage_Balance.dmg_limit_sturn);
							ChattingController.toChatting(this, String.format("불법적인 행위로 운영자로 부터 %d 초간 스턴을 당하였습니다.", Lineage_Balance.dmg_limit_sturn), Lineage.CHATTING_MODE_MESSAGE);
							dmglimitcheck++;

							if (Lineage_Balance.dmg_limit_out && Lineage_Balance.dmg_limit_count < dmglimitcheck) {

								String log2 = String.format("[데미지 검출 횟수 초과] -> [검출횟수: %d] [캐릭터: %s] [데미지: %.0f]", dmglimitcheck, getName(), dmg);

								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getDamageCheckComposite().toLog(log2);
									}
								});
								// 사용자 강제종료 시키기.
								toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
								LineageServer.close(getClient());

								return;
							}
						}
					}
					

					if (!isCriticalEffect() && effect > 66)
						effect = 0;

					// 화살 갯수 하향
					if (bow && arrow != null && !arrow.getName().contains("무한"))

						inv.count(arrow, arrow.getCount() - 1, true);

					// 로봇에게 알리기.
					if (o instanceof RobotInstance && Util.isDistance(this, o, 1))
						o.toDamage(this, 0, Lineage.ATTACK_TYPE_WEAPON);

					// 크리티컬 이팩트 초기화
					setCriticalEffect(false);
					setCriticalMagicEffect(false);
				} else {
					// \f1소지품이 너무 무거워서 전투를 할 수 없습니다.
					toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 110));

				}

			}
		}
	}

	public void toAttack2(object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg, boolean isTriple) {
		long time = System.currentTimeMillis();


		if (this.isBuffInvisiBility()) {
			BuffController.remove(this, InvisiBility.class);
		}

		// 인챈트베놈
		Skill skill = SkillDatabase.find(656);
		BuffInterface b = BuffController.find(o, SkillDatabase.find(656));
		if (this.isBuffEnchantVenom()) {

			if (b == null) {

				if (SkillController.isFigure(this, o, skill, true, false)) {
					Venom.init(this, skill, (int) o.getObjectId());
				}

			}
		}

	
		if (!isTriple && autoAttackTime > time) {
			return;
		}
		//스핵 
		int gfxmo = 0;
		if(bow)
			gfxmo = 21;
		else
			gfxmo = this.getGfxMode()+1;
		// 자동 칼질
		if (o != null && isAutoAttack && !isTriple) {
			autoAttackTarget = o;
			targetX = o.getX();
			targetY = o.getY();
			autoAttackTime = (long) (System.currentTimeMillis() + (SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), gfxmo)));
		}

		if (!isTriple && !isActionCheck(false)) {
			return;
		}

		int attackAction = 0;
		int effect = 0;
		double dmg = 0;
		ItemInstance weapon = inv.getSlot(Lineage.SLOT_WEAPON);
		ItemInstance arrow = null;
		List<object> insideList = getInsideList();

		setFight(true);

		if (bow && weapon != null) {
			// 잠수용 허수아비 체크.
			if (isRestCracker) {
				arrow = setRestCrackerArrow();

				if (weapon.getItem().getEffect() > 0 && arrow == null) {
					effect = weapon.getItem().getEffect();
				} else {
					if (arrow != null)
						effect = arrow.getItem().getEffect();
				}

				if (arrow == null) {
					if (o instanceof RestCracker)
						endRestCracker();
				}
			} else {
				arrow = weapon.getItem().getType2().equalsIgnoreCase("gauntlet") ? inv.findThrowingKnife() : inv.findArrow();

				if (weapon.getItem().getEffect() > 0 && arrow == null) {
					effect = weapon.getItem().getEffect();
				} else {
					if (arrow != null)
						effect = arrow.getItem().getEffect();
				}
			}
		}

		// 좌표에따라 방향전환.
		heading = Util.calcheading(this, x, y);

		// 자동사냥 방지 확인.
		if (AutoHuntCheckController.checkMonster(o) && !AutoHuntCheckController.checkCount(this))
			return;

		// 장로 변신중이면 콜라이트닝 이팩트 표현.
		if (Lineage.server_version <= 200 && getGfx() == 3879)
			effect = Lineage.call_lighting_effect;

		// 공속 확인.
		if (isTriple  && ((this.isFrameSpeed(gfxmo) && !isTransparent()))) {
			// 죽엇는지 확인.
			if (!isDead()) {
				// 무게 확인. 82% 미만
				if (inv.isWeightPercent(82)) {
					if (isRestCracker) {
						dmg = getRestCrackerDmg(weapon, arrow, o);
					} else {
						if (o instanceof RestCracker) {
							ChattingController.toChatting(this, String.format("\\fY[%s] 소유자만 공격가능.", Lineage.rest_cracker_name), Lineage.CHATTING_MODE_MESSAGE);
						} else {
							// 데미지 추출
							dmg = DamageController.getDamage(this, o, bow, weapon, arrow, 0);
						}
					}

					attackAction = getGfxMode() + Lineage.GFX_MODE_ATTACK;

					// 치명타 이팩트 처리
					if (isCriticalEffect() && weapon != null) {

						if (weapon.getItem().getType2().equalsIgnoreCase("dagger"))
							effect = Lineage.CRITICAL_EFFECT_DAGGER;
						else if (weapon.getItem().getType2().equalsIgnoreCase("sword"))
							effect = Lineage.CRITICAL_EFFECT_SWORD;
						else if (weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
							effect = Lineage.CRITICAL_EFFECT_TWOHAND_SWORD;
						else if (weapon.getItem().getType2().equalsIgnoreCase("edoryu"))
							effect = Lineage.CRITICAL_EFFECT_TWOHAND_AXE;
						else if (weapon.getItem().getType2().equalsIgnoreCase("claw"))
							effect = Lineage.CRITICAL_EFFECT_NONE;
						else if (weapon.getItem().getType2().equalsIgnoreCase("axe") || weapon.getItem().getType2().equalsIgnoreCase("blunt"))
							effect = weapon.getItem().isTohand() ? Lineage.CRITICAL_EFFECT_TWOHAND_AXE : Lineage.CRITICAL_EFFECT_AXE;
						else if (weapon.getItem().getType2().equalsIgnoreCase("spear"))
							effect = Lineage.CRITICAL_EFFECT_SPEAR;
						else if (weapon.getItem().getType2().equalsIgnoreCase("wand") || weapon.getItem().getType2().equalsIgnoreCase("staff"))
							effect = Lineage.CRITICAL_EFFECT_STAFF;
						else
							effect = bow ? 374 : Lineage.CRITICAL_EFFECT_NONE;

						 this.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 13555), this instanceof PcInstance);
					}

					if (dmg > 0) {
						// 무기에 따른 처리.
						if (weapon != null) {
							// 무기에게 공격했다는거 알리기.
							if (weapon.toDamage(this, o)) {
								dmg += weapon.toDamage((int) Math.round(dmg));
								effect = weapon.toDamageEffect();
								setCriticalEffect(false);

								if (bow) {
									if (effect > 0 && effect != 6288) {
										ServerBasePacket sbp = (ServerBasePacket) (S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, effect));

										if (무기이펙트)
											toSender(sbp);

										if (insideList != null) {
											// 주위의 유저에게 패킷 보냄.
											for (object oo : insideList) {
												if (oo instanceof PcInstance) {
													if (oo.무기이펙트)
														oo.toSender(ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), sbp.getBytes()));
												}
											}
										}
									}

									if (bow && weapon != null) {
										arrow = weapon.getItem().getType2().equalsIgnoreCase("gauntlet") ? inv.findThrowingKnife() : inv.findArrow();

										if (weapon.getItem().getEffect() > 0 && arrow == null) {
											effect = weapon.getItem().getEffect();
										} else {
											if (effect != 6288) {
												if (arrow != null)
													effect = arrow.getItem().getEffect();
											}
										}
									}
								}
							}

							if (o != null && o instanceof PcInstance && weapon != null && weapon.getItem() != null && !isAutoAttack) {
								int range = 1;

								switch (weapon.getItem().getType2()) {
								case "bow":
									range = Lineage.SEARCH_LOCATIONRANGE;
									break;
								case "spear":
									range = 2;
									break;
								}

								if (!Util.isDistance(this, o, range)) {
									if (effect != 66)
										effect = 0;

									dmg = 1;
								}
							}

							// 트리플 중첩시 데미지 감소.
							if (dmg > 0 && isTriple && Lineage_Balance.triple_damage_reduction > 0 && o != null && o instanceof PcInstance) {
								if (0 < o.tripleAttackObjId) {
									if (System.currentTimeMillis() < o.tripleDamageTime) {
										if (o.tripleAttackObjId != getObjectId())
											dmg = dmg * Lineage_Balance.triple_damage_reduction;
									} else {
										o.tripleAttackObjId = 0;
									}
								} else {
									o.tripleAttackObjId = getObjectId();
									o.tripleDamageTime = System.currentTimeMillis() + (int) (Lineage_Balance.triple_damage_reduction_time);
								}
							}
						}
						// 데미지 처리하는 구간

						DamageController.toDamage(this, o, isTriple ? (int) Math.round(dmg * Lineage_Balance.triple_arrow_damage) : (int) Math.round(dmg), bow ? Lineage.ATTACK_TYPE_BOW : Lineage.ATTACK_TYPE_WEAPON);
					}
					// 현재 시간
					now_Time = System.currentTimeMillis();

					// 프레임 시간
					frame_Time = SpriteFrameDatabase.getGfxFrameTime(this, this.getGfx(), isTriple ? (attackAction == Lineage.ACTION_TRIPLE_ARROW_2 ? attackAction : gfxMode) : attackAction);

					// 트리플 에로우 밀림 현상 개선
					boolean shouldAttack = false;
					if (getClassType() == Lineage.LINEAGE_CLASS_ELF && isTriple) {
						if (now_Time > ai_Time || now_Time - ai_Time < -345) {
							shouldAttack = true;
						}
					} else {
						if (ai_Time <= now_Time) {
							shouldAttack = true;
							ai_Time = System.currentTimeMillis() + (frame_Time - 5);
						}
					}

					if (shouldAttack) {
						toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), this, o, isTriple ? (attackAction == Lineage.ACTION_TRIPLE_ARROW_2 ? attackAction : gfxMode) : attackAction,
								(int) Math.round(dmg), effect, bow, effect > 0, x, y), true);
					}

					if (Lineage_Balance.dmg_limit && getGm() == 0) {
						boolean dmglimit = false;

						switch (getClassType()) {
						case Lineage.LINEAGE_CLASS_ROYAL:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						case Lineage.LINEAGE_CLASS_KNIGHT:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						case Lineage.LINEAGE_CLASS_ELF:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						case Lineage.LINEAGE_CLASS_WIZARD:
							if (dmg >= Lineage_Balance.royalmaxdmg) {
								dmglimit = true;
							}
							break;
						}
						if (dmglimit) {

							String log = String.format("[데미지 초과] -> [캐릭터: %s]  [데미지: %.0f]", getName(), dmg);

							GuiMain.display.asyncExec(new Runnable() {
								public void run() {
									GuiMain.getViewComposite().getDamageCheckComposite().toLog(log);
								}
							});

							FrameSpeedOverStun.init(this, Lineage_Balance.dmg_limit_sturn);
							ChattingController.toChatting(this, String.format("불법적인 행위로 운영자로 부터 %d 초간 스턴을 당하였습니다.", Lineage_Balance.dmg_limit_sturn), Lineage.CHATTING_MODE_MESSAGE);
							dmglimitcheck++;

							if (Lineage_Balance.dmg_limit_out && Lineage_Balance.dmg_limit_count < dmglimitcheck) {

								String log2 = String.format("[데미지 검출 횟수 초과] -> [검출횟수: %d] [캐릭터: %s] [데미지: %.0f]", dmglimitcheck, getName(), dmg);

								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getDamageCheckComposite().toLog(log2);
									}
								});
								// 사용자 강제종료 시키기.
								toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
								LineageServer.close(getClient());

								return;
							}
						}
					}

					if (!isCriticalEffect() && effect > 66)
						effect = 0;

					// 화살 갯수 하향
					if (bow && arrow != null && !arrow.getName().contains("무한"))

						inv.count(arrow, arrow.getCount() - 1, true);

					// 로봇에게 알리기.
					if (o instanceof RobotInstance && Util.isDistance(this, o, 1))
						o.toDamage(this, 0, Lineage.ATTACK_TYPE_WEAPON);

					// 크리티컬 이팩트 초기화
					setCriticalEffect(false);
					setCriticalMagicEffect(false);
				} else {
					// \f1소지품이 너무 무거워서 전투를 할 수 없습니다.
					toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 110));

				}

			}
		}
	}
	/**
	 * extra 액션이 존재하는 gfx인지 확인하는 함수
	 * 
	 * @param
	 * @return 2017-09-06 by all_night.
	 */
	public boolean extraAttackMotion(int mode, int gfx) {
		if (mode == 0) {
			if (gfx == Lineage.royal_male_gfx || gfx == Lineage.royal_female_gfx || gfx == Lineage.knight_male_gfx || gfx == Lineage.knight_female_gfx)
				return true;
		}

		Poly poly = PolyDatabase.getPolyGfx(gfx);

		if (poly == null)
			return false;

		switch (mode) {
		case 0:
			if (poly.getPolyName().contains("랭커"))
				return false;
			else if (poly.getPolyName().contains("군터"))
				return true;
			else if (poly.getPolyName().contains("켄라우헬"))
				return true;
			else if (poly.getPolyName().contains("질리언"))
				return true;
			else if (poly.getPolyName().contains("헬바인"))
				return true;
			else if (poly.getPolyName().contains("조우"))
				return true;
			else if (poly.getPolyName().contains("케레니스"))
				return true;
			else if (poly.getPolyName().contains("진 다크엘프"))
				return true;
			else if (poly.getPolyName().contains("하이엘프"))
				return true;
			else if (poly.getPolyName().contains("반왕 세트"))
				return true;
			else if (poly.getPolyName().contains("케레니스 세트"))
				return true;
			else if (poly.getPolyName().contains("유니크 레인져"))
				return true;
			if (poly.getPolyName().contains("유니크 매지션"))
				return true;
			break;
		case 1:
			if ((!poly.getPolyName().contains("랭커") || !poly.getPolyName().contains("유니크")))
				return true;
			break;
		case 2:
			if (poly.getPolyName().contains("왕자 랭커"))
				return true;
			if (poly.getPolyName().contains("공주 랭커"))
				return true;

			break;
		case 3:
			if (poly.getPolyName().contains("남자요정 랭커"))
				return true;
			if (poly.getPolyName().contains("여자요정 랭커"))
				return true;
			break;
		case 4:
			if (poly.getPolyName().contains("남자법사 랭커"))
				return true;
			if (poly.getPolyName().contains("여자법사 랭커"))
				return true;
			if (poly.getPolyName().contains("유니크 매지션"))
				return true;
			break;
		case 5:
			if (poly.getPolyName().contains("진 다크엘프"))
				return true;
			else if (poly.getPolyName().contains("하이엘프"))
				return true;
			else if (poly.getPolyName().contains("질리언"))
				return true;
			else if (poly.getPolyName().contains("헬바인"))
				return true;
			else if (poly.getPolyName().contains("남자요정 랭커"))
				return true;
			else if (poly.getPolyName().contains("여자요정 랭커"))
				return true;
			else if (poly.getPolyName().contains("유니크 레인져"))
				return true;
			break;
		case 6:
			if (poly.getPolyName().contains("남자기사 랭커"))
				return true;
			if (poly.getPolyName().contains("여자기사 랭커"))
				return true;
			if (poly.getPolyName().contains("유니크 나이트"))
				return true;
			break;
		}

		return false;
	}

	@Override
	public synchronized void toExp(object o, double exp) {
		//
//		if(o instanceof Cracker){
//			
//		}else if (Lineage.open_wait) {
//			ChattingController.toChatting(this, "[오픈대기] 오픈대기에는 레벨업이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
//			return;
//		}
		if (Lineage.open_wait) {
		ChattingController.toChatting(this, "[오픈대기] 오픈대기에는 레벨업이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		return;
		}
		if (PluginController.init(PcInstance.class, "", this, o, exp) != null)
			return;

		// 경험치 배율
		// 구간마다 경험치 반으로 감소
		if (level > 39) {
			switch (level) {
			case 40:
				exp /= Lineage.lv_40_exp_rate;
				break;
			case 41:
				exp /= Lineage.lv_41_exp_rate;
				break;
			case 42:
				exp /= Lineage.lv_42_exp_rate;
				break;
			case 43:
				exp /= Lineage.lv_43_exp_rate;
				break;
			case 44:
				exp /= Lineage.lv_44_exp_rate;
				break;
			case 45:
				exp /= Lineage.lv_45_exp_rate;
				break;
			case 46:
				exp /= Lineage.lv_46_exp_rate;
				break;
			case 47:
				exp /= Lineage.lv_47_exp_rate;
				break;
			case 48:
				exp /= Lineage.lv_48_exp_rate;
				break;
			case 49:
				exp /= Lineage.lv_49_exp_rate;
				break;
			case 50:
				exp /= Lineage.lv_50_exp_rate;
				break;
			case 51:
				exp /= Lineage.lv_51_exp_rate;
				break;
			case 52:
				exp /= Lineage.lv_52_exp_rate;
				break;
			case 53:
				exp /= Lineage.lv_53_exp_rate;
				break;
			case 54:
				exp /= Lineage.lv_54_exp_rate;
				break;
			case 55:
				exp /= Lineage.lv_55_exp_rate;
				break;
			case 56:
				exp /= Lineage.lv_56_exp_rate;
				break;
			case 57:
				exp /= Lineage.lv_57_exp_rate;
				break;
			case 58:
				exp /= Lineage.lv_58_exp_rate;
				break;
			case 59:
				exp /= Lineage.lv_59_exp_rate;
				break;
			case 60:
				exp /= Lineage.lv_60_exp_rate;
				break;
			case 61:
				exp /= Lineage.lv_61_exp_rate;
				break;
			case 62:
				exp /= Lineage.lv_62_exp_rate;
				break;
			case 63:
				exp /= Lineage.lv_63_exp_rate;
				break;
			case 64:
				exp /= Lineage.lv_64_exp_rate;
				break;
			case 65:
				exp /= Lineage.lv_65_exp_rate;
				break;
			case 66:
				exp /= Lineage.lv_66_exp_rate;
				break;
			case 67:
				exp /= Lineage.lv_67_exp_rate;
				break;
			case 68:
				exp /= Lineage.lv_68_exp_rate;
				break;
			case 69:
				exp /= Lineage.lv_69_exp_rate;
				break;
			case 70:
				exp /= Lineage.lv_70_exp_rate;
				break;
			case 71:
				exp /= Lineage.lv_71_exp_rate;
				break;
			case 72:
				exp /= Lineage.lv_72_exp_rate;
				break;
			case 73:
				exp /= Lineage.lv_73_exp_rate;
				break;
			case 74:
				exp /= Lineage.lv_74_exp_rate;
				break;
			case 75:
				exp /= Lineage.lv_75_exp_rate;
				break;
			case 76:
				exp /= Lineage.lv_76_exp_rate;
				break;
			case 77:
				exp /= Lineage.lv_77_exp_rate;
				break;
			case 78:
				exp /= Lineage.lv_78_exp_rate;
				break;
			case 79:
				exp /= Lineage.lv_79_exp_rate;
				break;
			case 80:
				exp /= Lineage.lv_80_exp_rate;
				break;
			case 81:
				exp /= Lineage.lv_81_exp_rate;
				break;
			case 82:
				exp /= Lineage.lv_82_exp_rate;
				break;
			case 83:
				exp /= Lineage.lv_83_exp_rate;
				break;
			case 84:
				exp /= Lineage.lv_84_exp_rate;
				break;
			case 85:
				exp /= Lineage.lv_85_exp_rate;
				break;
			case 86:
				exp /= Lineage.lv_86_exp_rate;
				break;
			case 87:
				exp /= Lineage.lv_87_exp_rate;
				break;
			case 88:
				exp /= Lineage.lv_88_exp_rate;
				break;
			case 89:
				exp /= Lineage.lv_89_exp_rate;
				break;
			default:
				exp /= Lineage.lv_90_exp_rate;
				break;
			}
		}

		// 동적 변경된 추가경험치 증가.
		if (getDynamicExp() > 0 && !(o instanceof FishExp))
			exp += exp * getDynamicExp();

		if (isBuffExpPotion() && !(o instanceof FishExp))
			exp *= 0.3;

		if (TimeEventController.isOpen && TimeEventController.num == 1 && !(o instanceof FishExp)) {
			exp *= 1.5;
		}
		if (TimeEventController.isOpen && TimeEventController.num == 4 && !(o instanceof FishExp)) {
			exp *= 2;
		}
		exp *= Lineage.rate_exp;

		// 패널티 적용
		if (level >= Lineage.penalty_level)
			exp *= Lineage.penalty_exp;

		if (isAutoHunt) {
			exp = exp * Lineage.is_auto_hunt_exp_percent;
		}

		setExp(getExp() + exp);

		if (getGm() > 0) {
			if(this.ismonExp()){
			ChattingController.toChatting(this, String.format("경험치 획득량 %f.", exp), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		

		Levelexpname = exp;
		// 로그 기록.
		if (Log.isLog(this)) {
			int o_lv = 0;
			String o_name = null;
			int o_exp = 0;
			if (o instanceof MonsterInstance) {
				MonsterInstance mon = (MonsterInstance) o;
				o_lv = mon.getMonster().getLevel();
				o_name = mon.getMonster().getName();
				o_exp = mon.getMonster().getExp();
			}
			if (o instanceof Cracker) {
				o_name = "허수아비";
			}
			if (o instanceof FishExp)
				o_name = "낚시";
			if (o instanceof NpcInstance) {
				NpcInstance npc = (NpcInstance) o;
				o_name = npc.getNpc().getName();
			}
			if (Log.isLog(this))
				Log.appendExp(getRegisterDate(), getLevel(), (int) exp, (int) getExp(), o_lv, o_name, o_exp);
		}
	}

	/**
	 * 랭크시스템 (쿠베라) 2023-03-08 by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	public void rankSystem() {
		int rank = RankController.getAllRank(getObjectId());

		this.rank = rank;

		lastRank = rank;

		// 랭커였다가 랭커가 풀렸을시
		if (lastRankClass > 0 && lastRankClass != this.rank) {
			if (lastRankClass == 1) {
				setDynamicHp(getDynamicHp() - 100);
				setDynamicAc(getDynamicAc() - 3);
				setDynamicSp(getDynamicSp() - 3);
				setDynamicAddDmg(getDynamicAddDmg() - 3);
				setDynamicAddDmgBow(getDynamicAddDmgBow() - 3);
			} else if (lastRankClass == 2) {
				setDynamicHp(getDynamicHp() - 70);
				setDynamicAc(getDynamicAc() - 2);
				setDynamicSp(getDynamicSp() - 2);
				setDynamicAddDmg(getDynamicAddDmg() - 2);
				setDynamicAddDmgBow(getDynamicAddDmgBow() - 2);
			} else if (lastRankClass == 3) {
				setDynamicHp(getDynamicHp() - 50);
				setDynamicAc(getDynamicAc() - 1);
				setDynamicSp(getDynamicSp() - 1);
				setDynamicAddDmg(getDynamicAddDmg() - 1);
				setDynamicAddDmgBow(getDynamicAddDmgBow() - 1);
			}
			ChattingController.toChatting(this, String.format("[랭킹 시스템]:랭킹시스템이 갱신되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
			lastRankClass = 0;
			toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
			toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
		}

		if (this.rank > 0 && rank > 0 && rank <= 3 && lastRankClass != this.rank) {
			lastRankClass = this.rank;

			ChattingController.toChatting(this, String.format("[랭킹 시스템]: %d위 버프 효과 적용", lastRankClass), Lineage.CHATTING_MODE_MESSAGE);

			if (lastRankClass == 1) {
				setDynamicHp(getDynamicHp() + 100);
				setDynamicAc(getDynamicAc() + 3);
				setDynamicSp(getDynamicSp() + 3);
				setDynamicAddDmg(getDynamicAddDmg() + 3);
				setDynamicAddDmgBow(getDynamicAddDmgBow() + 3);
				ChattingController.toChatting(this, String.format("[랭킹 시스템]: HP + 100 , AC - 3 , SP + 3, 추타 + 3 "), Lineage.CHATTING_MODE_MESSAGE);
			} else if (lastRankClass == 2) {
				setDynamicHp(getDynamicHp() + 70);
				setDynamicAc(getDynamicAc() + 2);
				setDynamicSp(getDynamicSp() + 2);
				setDynamicAddDmg(getDynamicAddDmg() + 2);
				setDynamicAddDmgBow(getDynamicAddDmgBow() + 2);
				ChattingController.toChatting(this, String.format("[랭킹 시스템]: HP + 70 , AC - 2 , SP + 2, 추타 + 2 "), Lineage.CHATTING_MODE_MESSAGE);
			} else if (lastRankClass == 3) {
				setDynamicHp(getDynamicHp() + 50);
				setDynamicAc(getDynamicAc() + 1);
				setDynamicSp(getDynamicSp() + 1);
				setDynamicAddDmg(getDynamicAddDmg() + 1);
				setDynamicAddDmgBow(getDynamicAddDmgBow() + 1);
				ChattingController.toChatting(this, String.format("[랭킹 시스템]: HP + 50 , AC -1 , SP + 1, 추타 + 1 "), Lineage.CHATTING_MODE_MESSAGE);
			}

			toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
			toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
		}
	}

	public void rankPvPSystem() {
		int rank = RankController.getPvPRankAll(this);

		this.pvprank = rank;
		lastRank = rank;
		// 랭커였다가 랭커가 풀렸을시
		if (lastpvpRankClass > 0 && lastpvpRankClass != this.pvprank) {
			if (lastpvpRankClass == 1) {
				setDynamicHp(getDynamicHp() - 100);
				setDynamicAc(getDynamicAc() - 3);
				setDynamicSp(getDynamicSp() - 3);
				setDynamicAddDmg(getDynamicAddDmg() - 3);
				setDynamicAddDmgBow(getDynamicAddDmgBow() - 3);
			} else if (lastpvpRankClass == 2) {
				setDynamicHp(getDynamicHp() - 70);
				setDynamicAc(getDynamicAc() - 2);
				setDynamicSp(getDynamicSp() - 2);
				setDynamicAddDmg(getDynamicAddDmg() - 2);
				setDynamicAddDmgBow(getDynamicAddDmgBow() - 2);
			} else if (lastpvpRankClass == 3) {
				setDynamicHp(getDynamicHp() - 50);
				setDynamicAc(getDynamicAc() - 1);
				setDynamicSp(getDynamicSp() - 1);
				setDynamicAddDmg(getDynamicAddDmg() - 1);
				setDynamicAddDmgBow(getDynamicAddDmgBow() - 1);
			}
			ChattingController.toChatting(this, String.format("[PvP 랭킹 시스템] 시스템이 갱신되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
			lastpvpRankClass = 0;
			toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
			toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
		}

		if (this.pvprank > 0 && rank > 0 && rank <= 3 && lastpvpRankClass != this.pvprank) {
			lastpvpRankClass = this.pvprank;

			ChattingController.toChatting(this, String.format("[PvP 랭킹 시스템] %d단계 효과 적용", lastpvpRankClass), Lineage.CHATTING_MODE_MESSAGE);

			if (lastpvpRankClass == 1) {
				setDynamicHp(getDynamicHp() + 100);
				setDynamicAc(getDynamicAc() + 3);
				setDynamicSp(getDynamicSp() + 3);
				setDynamicAddDmg(getDynamicAddDmg() + 3);
				setDynamicAddDmgBow(getDynamicAddDmgBow() + 3);
				ChattingController.toChatting(this, String.format("[PvP 랭킹 시스템]: HP+ 100 , ac-3 , sp + 3, 추타3 "), Lineage.CHATTING_MODE_MESSAGE);
			} else if (lastpvpRankClass == 2) {
				setDynamicHp(getDynamicHp() + 70);
				setDynamicAc(getDynamicAc() + 2);
				setDynamicSp(getDynamicSp() + 2);
				setDynamicAddDmg(getDynamicAddDmg() + 2);
				setDynamicAddDmgBow(getDynamicAddDmgBow() + 2);
				ChattingController.toChatting(this, String.format("[PvP 랭킹 시스템]: HP+ 70 , ac-2 , sp + 2, 추타2 "), Lineage.CHATTING_MODE_MESSAGE);
			} else if (lastpvpRankClass == 3) {
				setDynamicHp(getDynamicHp() + 50);
				setDynamicAc(getDynamicAc() + 1);
				setDynamicSp(getDynamicSp() + 1);
				setDynamicAddDmg(getDynamicAddDmg() + 1);
				setDynamicAddDmgBow(getDynamicAddDmgBow() + 1);
				ChattingController.toChatting(this, String.format("[PvP 랭킹 시스템]: HP+ 50 , ac-1 , sp + 1, 추타1 "), Lineage.CHATTING_MODE_MESSAGE);
			}

			toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
			toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
		}
	}
	private boolean isNameInRankFilter(String playerName) {
	    String[] filterNames = Lineage.rank_filter_names_query.split(" AND ");
	    for (String filter : filterNames) {
	        if (filter.startsWith("name!='")) {
	            String name = filter.substring(7, filter.length() - 1); // Extract the name
	            if (playerName.equalsIgnoreCase(name)) {
	                return true; // Name is in the filter, return true
	            }
	        }
	    }
	    return false; // Name is not in the filter, return false
	}
	@Override
	public void toTimer(long time) {


		if (isWorldDelete()) {
			return;
		}
		if (!isWorldDelete()) {
			try {


				if (Lineage.is_batpomet_system && !isWorldDelete())
					BaphometSystemController.setBaphomet(this);

				// 타임이벤트 진행중일경우 무슨이벤트 진행중인지 뿌려줌
				timeEventment();

				// 타임컨트롤러 시간관리
				timerSystem();

				// 룬시스템
				runSystem();

				// 잠수유저 정리
				ghostusercut();

				// 시간제 아이템 관리
				itemTime();

				//자동판매
				autosell();
				
				//아이템 체크관리
				itemcheck();

				// 출석체크
				if (Lineage.attendance_check_enabled)
					attendancecheck();


				// 랭크시스템 달성보상
				if (getLevel() >= Lineage.rank_min_level) {
					rankSystem();
				}
				if (getLevel() >= Lineage.rank_min_level) {
					rankPvPSystem();
				}

			} catch (Exception e) {
				// 예외 정보 출력
				System.println("An exception occurred: " + e.getMessage());
				e.printStackTrace();
			}
		}

		// 신규 혈맹 자동탈퇴
		try {
			if (getClanId() != 0 && getClanName().equalsIgnoreCase(Lineage.new_clan_name) && Lineage.is_new_clan_auto_out && level >= Lineage.new_clan_max_level && !this.isWorldDelete()) {
				ClanController.toOut(this);
				ChattingController.toChatting(this, String.format("신규 혈맹은 %d레벨 이상 자동탈퇴 됩니다.", Lineage.new_clan_max_level), Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {

		}

		try {
			// 성혈이 아닐 경우 내성 입장 못함
			Kingdom k = KingdomController.findKingdomInsideLocation(this);
			if (getGm() == 0 && k != null) {
				Kingdom temp = KingdomController.find(this);
				if (temp == null || temp.getUid() != k.getUid()) {
					int[] loc = Lineage.getHomeXY();
					toTeleport(loc[0], loc[1], loc[2], true);
				}
			}
		} catch (Exception e) {

		}

		try {
			// 낚시 컨트롤러
			if (isFishing() && !isWorldDelete() && isFishingZone())
				FishingController.startFishing(this);
		} catch (Exception e) {

		}

		try {
			// 서버 메세지 표현 처리.
			if (Common.SERVER_MESSAGE && message_time <= time) {
				message_time = time + Common.SERVER_MESSAGE_TIME;
				for (String msg : Common.SERVER_MESSAGE_LIST)
					ChattingController.toChatting(this, msg, Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {

		}
		try {
			// 현재 진행된 time값과 비교하여 24시간이 오바됫을경우 0으로 초기화
			if (getPkTime() > 0 && getPkTime() + (1000 * 60 * 60 * 24) <= time)
				setPkTime(0);
		} catch (Exception e) {

		}
		try {
			if (Lineage.open_wait && !isWorldDelete()) {
				// 오픈대기시 아이템 자동 지급.
				if (Lineage.is_world_open_wait_item && open_wait_item_time <= time && !this.isWorldDelete()) {
					if (open_wait_item_time != 0) {
						// 아이템 지급
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(Lineage.world_open_wait_item));
						if (ii != null) {
							ii.setCount(Util.random(Lineage.world_open_wait_item_min, Lineage.world_open_wait_item_max));
							super.toGiveItem(null, ii, ii.getCount());
						}
					}
					open_wait_item_time = time + Lineage.world_open_wait_item_delay;
				}
			} else {
				// 아이템 자동 지급.
				if (Lineage.world_premium_item_is && premium_item_time <= time && !this.isWorldDelete()) {
					if (premium_item_time != 0) {
						// 아이템 지급
						ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(Lineage.world_premium_item));
						if (ii != null) {
							ii.setCount(Util.random(Lineage.world_premium_item_min, Lineage.world_premium_item_max));
							super.toGiveItem(null, ii, ii.getCount());
						}
					}
					premium_item_time = time + Lineage.world_premium_item_delay;
				}
			}
		} catch (Exception e) {

		}
		// 팀대전 에러 처리.
		checkTeamBattle(false);

		if (getMap() != Lineage.teamBattleMap && getGm() < 1 && (getGfx() == 369 || isTransparent())) {
			setTransparent(false);
			setGfx(getClassGfx());
			toTeleport(getX(), getY(), getMap(), false);
		}

		// 전체 채팅 매크로
		if (isMacro && macroMsg != null && macroMsg.length() > 0 && !this.isWorldDelete()) {
			if (--macroDelay < 1) {
				macroDelay = Lineage.chatting_global_macro_delay;
				ChattingController.toChatting(this, macroMsg, Lineage.CHATTING_MODE_GLOBAL);
			}
		}

		PluginController.init(PcInstance.class, "toTimer", this, time);
	}

	public void autosell() {
		
		if (this.isAutoSell) {
			for (String value : this.isAutoSellList) {
				Npc n = NpcDatabase.find("매입 상인");
				if (n != null) {
					for (Shop s : n.getShop_list()) {
						if (s.getItemName().equalsIgnoreCase(value)) {
							for (ItemInstance item : this.getInventory().getList()) {
								if (item != null && item.getItem().getName().equalsIgnoreCase(value) && !item.isEquipped() && item.getEnLevel() == s.getItemEnLevel() && item.getBless() ==s.getItemBress()) {
									int item_count = (int) item.getCount();
									long sellPrice = Math.round(item.getItem().getShopPrice() * Lineage.sell_item_rate);

									Item i = ItemDatabase.find("아데나");

									if (i != null) {
										ItemInstance temp = this.getInventory().find(i.getName(), i.isPiles());

										if (temp == null) {
											temp = ItemDatabase.newInstance(i);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											temp.setBless(1);
											temp.setCount((long) sellPrice * item.getCount());
											temp.setDefinite(true);
											this.getInventory().append(temp, true);
										} else {
											this.getInventory().count(temp, (long) (temp.getCount() + sellPrice * item.getCount()), true);
										}

										if (this.isAutoSelluser) {
											ChattingController.toChatting(this, String.format("\\fW [자동판매] '%s'아이템을 %d개 판매하여", item.getItem().getName(), item_count),
													Lineage.CHATTING_MODE_MESSAGE);
											ChattingController.toChatting(this, String.format("\\ %d 아데나를 획득 하였습니다.", (sellPrice * item_count)),
													Lineage.CHATTING_MODE_MESSAGE);
										}

										this.getInventory().count(item, item_count - item_count, true); 
									}
								}
							}
						}
					}
				}
			}

		}
		
	}
	public void itemcheck() {
		if (!isWorldDelete()) {
			try {
				if (getMap() != 63 && getInventory().find("메테오 스트라이크") != null) {

					ItemInstance item = getInventory().find("메테오 스트라이크", 0, 1);
					getInventory().count(item, 0, true);

				}
			} catch (Exception e) {
				// 예외 정보 출력
				System.println(e.getMessage());
				e.printStackTrace();
			}
			try {
				if (this instanceof PcInstance) {
					String playerName = this.getName();
					if (!isNameInRankFilter(playerName)) {
						ItemInstance item = getInventory().find("절대 반지", 0, 1);
						getInventory().count(item, 0, true);
					}
				}
			} catch (Exception e) {
				// 예외 정보 출력
				System.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public void itemTime() {
		if (!isWorldDelete()) {
			try {
				for (ItemInstance item : inv.getList()) {
					if (item.getItemTimek() != null && item.getItemTimek().length() > 0) {
						toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));


					}

				}
			} catch (Exception e) {
				// 예외 정보 출력
				System.println(e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			if (!this.isWorldDelete()) {
			    for (PcInstance pc : World.getPcList()) {
			        if (!this.isWorldDelete()) {
			            for (ItemInstance i : pc.getInventory().getList()) {
			                String itemTimek = i.getItemTimek();
			                if (itemTimek != null && !itemTimek.isEmpty() && !itemTimek.equals("0") && !i.getItem().getName().equalsIgnoreCase("양초")) {
			                    try {
			                        // 아이템에 설정된 날짜와 시간
			                        LocalDateTime itemDateTime = LocalDateTime.parse(itemTimek, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

			                        // 현재 시간 (KST - Korea Standard Time, UTC+9)
			                        ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

			                        // 비교
			                        if (itemDateTime.isBefore(currentZonedDateTime.toLocalDateTime())) {
			                            if (i.isEquipped()) {
			                                // 착용되어 있을 경우 해제.
			                                i.toClick(pc, null);
			                            }

			                            // 날짜 포맷팅
			                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
			                            String formattedDate = itemDateTime.format(formatter);

			                            // 삭제 메시지에 날짜 정보 추가
			                            String message = i.getItem().getName() + " 기간이 만료하여 아이템이 삭제됩니다.";
			                            String message2 = "만료일: " + formattedDate;
			                            ChattingController.toChatting(pc, message, Lineage.CHATTING_MODE_MESSAGE);
			                            ChattingController.toChatting(pc, message2, Lineage.CHATTING_MODE_MESSAGE);
			                            pc.getInventory().count(i, getCount() - 1, true);
			                        }
			                    } catch (DateTimeParseException e) {
			                        // 날짜 문자열 파싱 오류 처리
			                        // 날짜 문자열이 유효하지 않을 때 이 부분이 실행됩니다.
			                        // 오류를 기록하거나 처리 방법을 결정합니다.
			                    }
			                }
			            }
			        }
			    }
			}



		} catch (Exception e) {
			// Exception 처리
		}
	}

	/**
	 * 결투장 관련 메서드. 2018-05-02 by all-night.
	 */
	public void battleZone() {
		// 결투장 입장
		if (World.isBattleZone(getX(), getY(), getMap()) && !isBattlezone) {
			isBattlezone = true;
			ChattingController.toChatting(this, "결투장에 입장하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		}

		// 결투장 피바
		if (!World.isBattleZone(getX(), getY(), getMap()) && isBattlezone) {
			ChattingController.toChatting(this, "결투장을 퇴장하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
			toSender(S_ObjectCriminal.clone(BasePacketPooling.getPool(S_ObjectCriminal.class), this, 0), true);

			isBattlezone = false;
		}

		if (World.isBattleZone(getX(), getY(), getMap()) && isBattlezone)
			toSender(S_ObjectCriminal.clone(BasePacketPooling.getPool(S_ObjectCriminal.class), this, 1), true);

		if (Lineage.is_battle_zone_hp_bar && isBattlezone) {
			for (object o : getInsideList()) {
				if (o instanceof PcInstance && ((PcInstance) o).isBattlezone) {
					toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), (Character) o, true));
				}
			}
		}
	}

	public void updateOmanAmuletEffect() {
		try {
			if (this.getInventory() != null && !this.isWorldDelete()) {
				ItemInstance omanAmulet1 = this.getInventory().find(ItemDatabase.find("오만의 탑 1층 지배 부적"));
				boolean hasOmanAmulet1 = omanAmulet1 != null;

				ItemInstance omanAmulet2 = this.getInventory().find(ItemDatabase.find("오만의 탑 2층 지배 부적"));
				boolean hasOmanAmulet2 = omanAmulet2 != null;

				ItemInstance omanAmulet3 = this.getInventory().find(ItemDatabase.find("오만의 탑 3층 지배 부적"));
				boolean hasOmanAmulet3 = omanAmulet3 != null;

				ItemInstance omanAmulet4 = this.getInventory().find(ItemDatabase.find("오만의 탑 4층 지배 부적"));
				boolean hasOmanAmulet4 = omanAmulet4 != null;

				ItemInstance omanAmulet5 = this.getInventory().find(ItemDatabase.find("오만의 탑 5층 지배 부적"));
				boolean hasOmanAmulet5 = omanAmulet5 != null;

				ItemInstance omanAmulet6 = this.getInventory().find(ItemDatabase.find("오만의 탑 6층 지배 부적"));
				boolean hasOmanAmulet6 = omanAmulet6 != null;

				ItemInstance omanAmulet7 = this.getInventory().find(ItemDatabase.find("오만의 탑 7층 지배 부적"));
				boolean hasOmanAmulet7 = omanAmulet7 != null;

				ItemInstance omanAmulet8 = this.getInventory().find(ItemDatabase.find("오만의 탑 8층 지배 부적"));
				boolean hasOmanAmulet8 = omanAmulet8 != null;

				ItemInstance omanAmulet9 = this.getInventory().find(ItemDatabase.find("오만의 탑 9층 지배 부적"));
				boolean hasOmanAmulet9 = omanAmulet9 != null;

				ItemInstance omanAmulet10 = this.getInventory().find(ItemDatabase.find("오만의 탑 10층 지배 부적"));
				boolean hasOmanAmulet10 = omanAmulet10 != null;

				if (hasOmanAmulet1 && !isOmanBuff1) {
					isOmanBuff1 = true;

					this.setDynamicAddDmg(this.getDynamicAddDmg() + 1);
					this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + 1);
					this.setDynamicSp(this.getDynamicSp() + 1);

					ChattingController.toChatting(this, "오만의 탑 1층 지배 부적 효과가 적용되었습니다.", 20);

				} else if (!hasOmanAmulet1 && isOmanBuff1) {
					isOmanBuff1 = false;
					this.setDynamicAddDmg(this.getDynamicAddDmg() - 1);
					this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - 1);
					this.setDynamicSp(this.getDynamicSp() - 1);

					ChattingController.toChatting(this, "오만의 탑 1층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet2 && !isOmanBuff2) {
					isOmanBuff2 = true;
					this.setDynamicHp(this.getDynamicHp() + 100);

					ChattingController.toChatting(this, "오만의 탑 2층 지배 부적 효과가 적용되었습니다.", 20);

				} else if (!hasOmanAmulet2 && isOmanBuff2) {
					isOmanBuff2 = false;
					this.setDynamicHp(this.getDynamicHp() - 100);

					ChattingController.toChatting(this, "오만의 탑 2층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet3 && !isOmanBuff3) {
					isOmanBuff3 = true;

					this.setDynamicMp(this.getDynamicMp() + 100);

					ChattingController.toChatting(this, "오만의 탑 3층 지배 부적 효과가 적용되었습니다.", 20);
				} else if (!hasOmanAmulet3 && isOmanBuff3) {
					isOmanBuff3 = false;

					this.setDynamicMp(this.getDynamicMp() - 100);

					ChattingController.toChatting(this, "오만의 탑 3층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet4 && !isOmanBuff4) {
					isOmanBuff4 = true;

					this.setDynamicReduction(this.getDynamicReduction() + 1);

					ChattingController.toChatting(this, "오만의 탑 4층 지배 부적 효과가 적용되었습니다.", 20);
				} else if (!hasOmanAmulet4 && isOmanBuff4) {
					isOmanBuff4 = false;

					this.setDynamicReduction(this.getDynamicReduction() - 1);

					ChattingController.toChatting(this, "오만의 탑 4층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet5 && !isOmanBuff5) {
					isOmanBuff5 = true;

					this.setDynamicAddDmg(this.getDynamicAddDmg() + 1);
					this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + 1);
					this.setDynamicSp(this.getDynamicSp() + 1);

					ChattingController.toChatting(this, "오만의 탑 5층 지배 부적 효과가 적용되었습니다.", 20);
				} else if (!hasOmanAmulet5 && isOmanBuff5) {
					isOmanBuff5 = false;

					this.setDynamicAddDmg(this.getDynamicAddDmg() - 1);
					this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - 1);
					this.setDynamicSp(this.getDynamicSp() - 1);

					ChattingController.toChatting(this, "오만의 탑 5층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet6 && !isOmanBuff6) {
					isOmanBuff6 = true;

					this.setDynamicHp(this.getDynamicHp() + 100);

					ChattingController.toChatting(this, "오만의 탑 6층 지배 부적 효과가 적용되었습니다.", 20);
				} else if (!hasOmanAmulet6 && isOmanBuff6) {
					isOmanBuff6 = false;

					this.setDynamicHp(this.getDynamicHp() - 100);

					ChattingController.toChatting(this, "오만의 탑 6층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet7 && !isOmanBuff7) {
					isOmanBuff7 = true;

					this.setDynamicMp(this.getDynamicMp() + 100);
					ChattingController.toChatting(this, "오만의 탑 7층 지배 부적 효과가 적용되었습니다.", 20);

				} else if (!hasOmanAmulet7 && isOmanBuff7) {
					isOmanBuff7 = false;

					this.setDynamicMp(this.getDynamicMp() - 100);

					ChattingController.toChatting(this, "오만의 탑 7층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet8 && !isOmanBuff8) {
					isOmanBuff8 = true;

					this.setDynamicReduction(this.getDynamicReduction() + 1);

					ChattingController.toChatting(this, "오만의 탑 8층 지배 부적 효과가 적용되었습니다.", 20);

				} else if (!hasOmanAmulet8 && isOmanBuff8) {
					isOmanBuff8 = false;

					this.setDynamicReduction(this.getDynamicReduction() - 1);

					ChattingController.toChatting(this, "오만의 탑 8층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet9 && !isOmanBuff9) {
					isOmanBuff9 = true;

					this.setDynamicMr(this.getDynamicMr() + 15);

					ChattingController.toChatting(this, "오만의 탑 9층 지배 부적 효과가 적용되었습니다.", 20);

				} else if (!hasOmanAmulet9 && isOmanBuff9) {
					isOmanBuff9 = false;

					this.setDynamicMr(this.getDynamicMr() - 15);

					ChattingController.toChatting(this, "오만의 탑 9층 지배 부적 효과가 해제되었습니다.", 20);
				}

				if (hasOmanAmulet10 && !isOmanBuff10) {
					isOmanBuff10 = true;

					this.setDynamicAddDmg(this.getDynamicAddDmg() + 2);
					this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + 2);
					this.setDynamicSp(this.getDynamicSp() + 2);

					ChattingController.toChatting(this, "오만의 탑 10층 지배 부적 효과가 적용되었습니다.", 20);

				} else if (!hasOmanAmulet10 && isOmanBuff10) {
					isOmanBuff10 = false;

					this.setDynamicAddDmg(this.getDynamicAddDmg() - 2);
					this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - 2);
					this.setDynamicSp(this.getDynamicSp() - 2);

					ChattingController.toChatting(this, "오만의 탑 10층 지배 부적 효과가 해제되었습니다.", 20);
				}
				toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
				toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
			}
		} catch (Exception e) {
			// Handle exception if needed
		}
	}

	/**
	 * 잠숭저 절단 (쿠베라) 2023-03-08 by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	public void ghostusercut() {
		try {
			// 죽고 잠수타는 유저 절단
			if (Lineage.user_ghost) {
				if (isDead() && !this.isWorldDelete()) {
					if (++playdead > Lineage.user_ghost_time) {
						playdead = 0;
						lineage.share.System.println(" : " + this.getName() + " 강제종료 처리");
						this.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
						LineageServer.close(this.getClient());

						return;
					}
				}
			}

		} catch (Exception e) {
			// 예외 정보 출력
			System.println("잠수유저 절단 부분 체크: " + e.getMessage());
			e.printStackTrace();

			// 예외를 상위 호출자로 전파
			throw e;
		}
	}

	/**
	 * 출석체크 (쿠베라) 2023-03-08 by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	public void attendancecheck() {
		// 출석체크 시간 카운팅
		try {
			if (!this.isWorldDelete()) {
				if (this.getDaycount() < Lineage.lastday) {

					if (this.getDaycheck() == 0) {
						if (++dayptime >= Lineage.dayc && this.getDaycheck() == 0) {
							if (++checkmenttime >= Lineage.checkment) {
								checkmenttime = 0;
								ChattingController.toChatting(this, String.format("\\fU 출석체크 시간을 채우셧습니다 보상을 받아주세요."), Lineage.CHATTING_MODE_MESSAGE);
							}

						}

						AccountDatabase.updateptime(dayptime, this.accountUid);

					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 보물찾기 (쿠베라) 2023-03-08 by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	public void treasure() {
		if (TreasureHuntController.isOpen && getMap() == 807 && !this.isWorldDelete()) {

			if (this.getGfx() != 54) {

				ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);

				if (weapon != null && weapon.isEquipped())
					weapon.toClick(this, null);

				setGfx(54);
				setGfxMode(0);

				super.toTeleport(getX(), getY(), getMap(), false);
			}

		}

		if (!TreasureHuntController.isOpen && getInventory().find("보물상자 획득 점수") != null && !this.isWorldDelete()) {

			ItemInstance item = getInventory().find("보물상자 획득 점수", true);

			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("보물 사냥꾼 주머니"));

			if (item.getCount() >= 10) {

				if (item != null && item.getCount() >= 30 && !this.isWorldDelete()) {
					ii.setCount(3);
				}
				if (item != null && item.getCount() >= 50 && !this.isWorldDelete()) {
					ii.setCount(5);
				}
				if (item != null && item.getCount() >= 70 && !this.isWorldDelete()) {

					ii.setCount(7);

				}

				super.toGiveItem(null, ii, ii.getCount());

			}
			setGfx(getClassGfx());
			setGfxMode(0);
			getInventory().count(item, 0, true);

		}
	}

	/**
	 * 인형레이스 (쿠베라) 2023-03-08 by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	public void dollrace() {
		// 인형경주가 끝나고 변신이 안풀렸을경우
		if (this.getMap() != 5143) {
			if (this.getGfx() == 5919 || this.getGfx() == 6096 || this.getGfx() == 6100 || this.getGfx() == 6443 || this.getGfx() == 6449 || this.getGfx() == 6452 || this.getGfx() == 6480 || this.getGfx() == 8650
					|| this.getGfx() == 7047 || this.getGfx() == 7053 || this.getGfx() == 12539 || this.getGfx() == 13516 || this.getGfx() == 14534 || this.getGfx() == 13520 || this.getGfx() == 15975
					|| this.getGfx() == 13464 || this.getGfx() == 15978) {
				this.setGfx(this.getClassGfx());
				this.toTeleport(this.getX(), this.getY(), this.getMap(), true);
			}
		}
		// 인형경주가 시작전에 귀환한유저
		if (DollRaceController.startTeamBattle) {
			if (this.getMap() != 5143 && DollRaceController.checkList(this)) {
				DollRaceController.joinList.remove(this);
				ChattingController.toChatting(this, String.format("\\fU 인형경주중 도중에 귀환하여 탈락하셨습니다."), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		// 인형경주중에 접속 종료하거나 팅겨서 끝나고나서 접속한유저 혹시몰라서 한번 더 지워줌
		if (DollRaceController.checkList(this)) {

			if (this.getMap() != 5143 && DollRaceController.checkList(this)) {
				DollRaceController.removeList(this);
			}
		}
		// 인형경주에 입장햇을때 변신이 안된유저
		if (this.getMap() == 5143 && this.getX() >= 32766 && this.getX() <= 32776 && this.getY() >= 32845 && this.getY() <= 32852) {
			if (this.getGfx() != 5919 && this.getGfx() != 6096 && this.getGfx() != 6100 && this.getGfx() != 6443 && this.getGfx() != 6449 && this.getGfx() != 6452 && this.getGfx() != 6480 && this.getGfx() != 8650
					&& this.getGfx() != 7047 && this.getGfx() != 7053 && this.getGfx() != 12539 && this.getGfx() != 13516 && this.getGfx() != 14534 && this.getGfx() != 13520 && this.getGfx() != 15975
					&& this.getGfx() != 13464 && this.getGfx() != 15978) {

				double random = Math.random();
				int num = (int) Math.round(random * (MAGIC_DOLL_GFX.length - 1));

				// 입장전 무기 해제
				if (this.getInventory().getSlot(Lineage.SLOT_WEAPON) != null) {
					this.getInventory().getSlot(Lineage.SLOT_WEAPON).toClick(this, null);
				}

				this.setGfx(MAGIC_DOLL_GFX[num]);
				this.setGfxMode(0);

				this.toSender(S_ObjectPoly.clone(BasePacketPooling.getPool(S_ObjectPoly.class), this), true);

				// 버프등록
				BuffController.append(this, ShapeChange.clone(BuffController.getPool(ShapeChange.class), SkillDatabase.find(208), 120));

				this.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 6082), true);

			}

		}

		if (++testTime2 >= 8 && DollRaceController.status == EVENT_STATUS.PLAY) {

			testTime2 = 0;

			if (this.getMap() == 5143 && !World.isRace(getX(), getY(), getMap())) {

				Random random = new Random();

				int i = random.nextInt(100) + 1;
				this.setNowMp(0);

				// 헤이스트
				if (i > 60 && i <= 100) {
					Haste.init(this, 5, false);
				}
				// 슬로우
				if (i > 40 && i <= 60) {
					Slow.init(this, 3);
				}

				// 스턴
				if (i > 20 && i <= 40) {
					Skill skill = SkillDatabase.find(134);
					EarthBind.init2(this, skill);
				}

				// 용기
				if (i > 1 && i <= 20) {
					Bravery.init(this, 4, false);
				}
			}
		}
	}

	/**
	 * 타임이벤트 멘트 (쿠베라) 2023-03-08 by 오픈카톡 https://open.kakao.com/o/sbONOzMd
	 */
	public void timeEventment() {
		// 타임이벤트 시간마다 무슨 이벤트인지 알려주기
		try {

			if (++testTime >= Lineage.time_ment && TimeEventController.isOpen && !this.isWorldDelete()) {

				testTime = 0;

				if (TimeEventController.isOpen && TimeEventController.num == 1) {

					ChattingController.toChatting(this, String.format("\\fU메티스: [타임이벤트 1시간] 경험치 1.5배 증가"), Lineage.CHATTING_MODE_MESSAGE);

				}
				if (TimeEventController.isOpen && TimeEventController.num == 2) {

					ChattingController.toChatting(this, String.format("\\fU메티스: [타임이벤트 1시간] 드랍률 20퍼 증가"), Lineage.CHATTING_MODE_MESSAGE);

				}
				if (TimeEventController.isOpen && TimeEventController.num == 3) {

					ChattingController.toChatting(this, String.format("\\fU메티스: [타임이벤트 1시간] 아데나 드랍률 30퍼 증가"), Lineage.CHATTING_MODE_MESSAGE);

				}

				if (TimeEventController.isOpen && TimeEventController.num == 4) {

					ChattingController.toChatting(this, String.format("\\fU메티스: [타임이벤트 1시간] 경험치 2배 증가"), Lineage.CHATTING_MODE_MESSAGE);

				}
				if (TimeEventController.isOpen && TimeEventController.num == 5) {

					ChattingController.toChatting(this, String.format("\\fU메티스:  [타임이벤트 1시간] 드랍률 40퍼 증가"), Lineage.CHATTING_MODE_MESSAGE);

				}
			}
		} catch (Exception e) {

		}

	}

	public void timerSystem() {
		try {
			// 기란감옥 던전 시간 확인
			int map = getMap();
			if (Lineage.is_giran_dungeon_time && TimeDungeonDatabase.isGiranDungeonMap(map) && !isWorldDelete()) {
				if (--giran_dungeon_time < 1 && getGm() == 0)
					TimeDungeonDatabase.isTimeDungeonFinal(this, 0);
			}
		} catch (Exception e) {

		}

		// 월드보스
		if (!WorldBossController.isOpen && getMap() == 1400 && !WorldBossController.isWait) {

			int[] loc = Lineage.getHomeXY();
			toTeleport(loc[0], loc[1], loc[2], true);
		}
		// 지옥
		if (!HellController.isOpen && getGm() == 0 && getMap() == 666) {
			if (isAutoHunt) {
				endAutoHunt(false, false);
			}

			int[] loc = Lineage.getHomeXY();
			toTeleport(loc[0], loc[1], loc[2], true);
		}
		// 보물찾기
		if (!TreasureHuntController.isOpen && getGm() == 0 && getMap() == 807) {
			if (isAutoHunt) {
				endAutoHunt(false, false);
			}

			int[] loc = Lineage.getHomeXY();
			toTeleport(loc[0], loc[1], loc[2], true);
		}
		// 마족신전
		if (!DimensionController.isOpen && getGm() == 0 && getMap() == 410) {
			if (isAutoHunt) {
				endAutoHunt(false, false);
			}

			int[] loc = Lineage.getHomeXY();
			toTeleport(loc[0], loc[1], loc[2], true);
		}
		// 악마왕의 영토
		if (!DevilController.isOpen && getGm() == 0 && getMap() == 5167) {
			if (isAutoHunt) {
				endAutoHunt(false, false);
			}

			int[] loc = Lineage.getHomeXY();
			toTeleport(loc[0], loc[1], loc[2], true);
		}
		

	}

	public void levelUpgift() {
		// 52레벨
		try {
			if (level >= 52 && this.getPclevel_gift_check() == 0 && !this.isWorldDelete()) {
				ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find("52레벨 달성 보상"));
				this.setPclevel_gift_check(1);
				ii.setCount(1);
				super.toGiveItem(null, ii, ii.getCount());
				AccountDatabase.uplevelcheck(1, this.accountUid);
			}
		} catch (Exception e) {

		}
	}

//	public void runSystem() {
//		try {
//			if (this.getInventory() != null && !this.isWorldDelete()) {
//				ItemInstance item = this.getInventory().find(ItemDatabase.find("데우스의 문장"));
//				int check = 0;
//
//				// 수량 체크
//				for (ItemInstance check_item : this.getInventory().getList()) {
//					if (check_item != null && check_item.getItem().getName().equalsIgnoreCase("데우스의 문장") && check_item.getEnLevel() > 0)
//						check = check + 1;
//				}
//
//				if (check == 0 && isSealBuff && Seal_Level > 0) {
//
//					if (Seal_Level >= 6) {
//						this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
//						this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
//						this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
//						this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
//						this.setDynamicHp(this.getDynamicHp() - (Seal_Level * 40));
//						this.setDynamicSp(this.getDynamicSp() - Seal_Level);
//						this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
//						this.setDynamicAc(this.getDynamicAc() - Seal_Level);
//					} else {
//						this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
//						this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
//						this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
//						this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
//						this.setDynamicSp(this.getDynamicSp() - Seal_Level);
//						this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
//						this.setDynamicAc(this.getDynamicAc() - Seal_Level);
//					}
//					isSealBuff = false;
//					Seal_Level = 0;
//					toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
//					toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
//					ChattingController.toChatting(this, "[알림] 데우스의 문장 효과가 해제됩니다.", Lineage.CHATTING_MODE_MESSAGE);
//				}
//
//				if (item != null && item.getEnLevel() > 0) {
//					// 정상적으로 효과 적용할 경우
//					if (check == 1 && !isSealBuff && Seal_Level == 0) {
//						isSealBuff = true;
//						Seal_Level = item.getEnLevel();
//
//						if (Seal_Level >= 6) {
//							this.setDynamicAddDmg(this.getDynamicAddDmg() + Seal_Level);
//							this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + Seal_Level);
//							this.setDynamicAddHit(this.getDynamicAddHit() + Seal_Level);
//							this.setDynamicAddHitBow(this.getDynamicAddHitBow() + Seal_Level);
//							this.setDynamicHp(this.getDynamicHp() + (Seal_Level * 40));
//							this.setDynamicSp(this.getDynamicSp() + Seal_Level);
//							this.setDynamicExp(this.getDynamicExp() + (Seal_Level * 0.01));
//							this.setDynamicAc(this.getDynamicAc() + Seal_Level);
//							ChattingController.toChatting(this, String.format("[알림] +%d %s : 근/원 대미지 +%d, 근/원 명중 +%d,", item.getEnLevel(), item.getItem().getName(), Seal_Level, Seal_Level),
//									Lineage.CHATTING_MODE_MESSAGE);
//							ChattingController.toChatting(this, String.format("SP +%d, hp+ %d , 경험치획득량 + %.0f퍼 증가", Seal_Level, Seal_Level * 40, (Seal_Level * 0.01) * 100), Lineage.CHATTING_MODE_MESSAGE);
//						} else {
//							this.setDynamicAddDmg(this.getDynamicAddDmg() + Seal_Level);
//							this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + Seal_Level);
//							this.setDynamicAddHit(this.getDynamicAddHit() + Seal_Level);
//							this.setDynamicAddHitBow(this.getDynamicAddHitBow() + Seal_Level);
//							this.setDynamicSp(this.getDynamicSp() + Seal_Level);
//							this.setDynamicExp(this.getDynamicExp() + (Seal_Level * 0.01));
//							this.setDynamicAc(this.getDynamicAc() + Seal_Level);
//							ChattingController.toChatting(this, String.format("[알림] +%d %s : 근/원 대미지 +%d, 근/원 명중 +%d,", item.getEnLevel(), item.getItem().getName(), Seal_Level, Seal_Level),
//									Lineage.CHATTING_MODE_MESSAGE);
//							ChattingController.toChatting(this, String.format("SP +%d, 경험치획득량 + %.0f퍼 증가", Seal_Level, (Seal_Level * 0.01) * 100), Lineage.CHATTING_MODE_MESSAGE);
//						}
//
//						toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
//						toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
//
//						// 정상적으로 효과 적용 중이면서 인첸트 레벨이 변경 될 경우
//					} else if (check == 1 && isSealBuff && Seal_Level > 0 && Seal_Level != item.getEnLevel()) {
//						if (Seal_Level >= 6) {
//							this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
//							this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
//							this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
//							this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
//							this.setDynamicHp(this.getDynamicHp() - (Seal_Level * 40));
//							this.setDynamicSp(this.getDynamicSp() - Seal_Level);
//							this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
//							this.setDynamicAc(this.getDynamicAc() - Seal_Level);
//
//						} else {
//							this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
//							this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
//							this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
//							this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
//							this.setDynamicSp(this.getDynamicSp() - Seal_Level);
//							this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
//							this.setDynamicAc(this.getDynamicAc() - Seal_Level);
//						}
//						isSealBuff = false;
//						Seal_Level = 0;
//						toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
//						toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
//						ChattingController.toChatting(this, "[알림] 데우스의 문장 인첸트 변경되어 효과 적용해제 합니다.", Lineage.CHATTING_MODE_MESSAGE);
//
//					} else if (check > 1) {
//
//						if (isSealBuff && Seal_Level > 0) {
//							if (Seal_Level >= 6) {
//								this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
//								this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
//								this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
//								this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
//								this.setDynamicHp(this.getDynamicHp() - (Seal_Level * 40));
//								this.setDynamicSp(this.getDynamicSp() - Seal_Level);
//								this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
//								this.setDynamicAc(this.getDynamicAc() - Seal_Level);
//							} else {
//								this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
//								this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
//								this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
//								this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
//								this.setDynamicSp(this.getDynamicSp() - Seal_Level);
//								this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
//								this.setDynamicAc(this.getDynamicAc() - Seal_Level);
//							}
//							isSealBuff = false;
//							Seal_Level = 0;
//							toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
//							toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
//
//						} else {
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//
//		}
//	}
	public void runSystem() {
	    try {
	        if (this.getInventory() != null && !this.isWorldDelete()) {
	            // Find the highest Seal_Level
	            int highestSealLevel = 0;

	            for (ItemInstance check_item : this.getInventory().getList()) {
	                if (check_item != null && check_item.getItem().getName().equalsIgnoreCase("전장의 가호") && check_item.getEnLevel() > highestSealLevel) {
	                    highestSealLevel = check_item.getEnLevel();
	                }
	            }

	            if (highestSealLevel > 0) {
	                // If Seal buff is already active, remove the previous effects
	                if (isSealBuff) {
	                    if (Seal_Level >= 6) {
	                        this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
	                        this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
	                        this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
	                        this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
	                        this.setDynamicHp(this.getDynamicHp() - (Seal_Level * 20));
	                        this.setDynamicSp(this.getDynamicSp() - Seal_Level);
	                        this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
	                        this.setDynamicAc(this.getDynamicAc() - Seal_Level);
	                    } else {
	                        this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
	                        this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
	                        this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
	                        this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
	                        this.setDynamicSp(this.getDynamicSp() - Seal_Level);
	                        this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
	                        this.setDynamicAc(this.getDynamicAc() - Seal_Level);
	                    }
	                }

	                // Apply the effects of the highest Seal_Level
	                isSealBuff = true;
	                Seal_Level = highestSealLevel;

	                if (Seal_Level >= 6) {
	                    this.setDynamicAddDmg(this.getDynamicAddDmg() + Seal_Level);
	                    this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + Seal_Level);
	                    this.setDynamicAddHit(this.getDynamicAddHit() + Seal_Level);
	                    this.setDynamicAddHitBow(this.getDynamicAddHitBow() + Seal_Level);
	                    this.setDynamicHp(this.getDynamicHp() + (Seal_Level * 20));
	                    this.setDynamicSp(this.getDynamicSp() + Seal_Level);
	                    this.setDynamicExp(this.getDynamicExp() + (Seal_Level * 0.01));
	                    this.setDynamicAc(this.getDynamicAc() + Seal_Level);
	                } else {
	                    this.setDynamicAddDmg(this.getDynamicAddDmg() + Seal_Level);
	                    this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() + Seal_Level);
	                    this.setDynamicAddHit(this.getDynamicAddHit() + Seal_Level);
	                    this.setDynamicAddHitBow(this.getDynamicAddHitBow() + Seal_Level);
	                    this.setDynamicSp(this.getDynamicSp() + Seal_Level);
	                    this.setDynamicExp(this.getDynamicExp() + (Seal_Level * 0.01));
	                    this.setDynamicAc(this.getDynamicAc() + Seal_Level);
	                }

	                toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
	                toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));

	              
	            } else if (isSealBuff) {
	                // If there is no Seal_Level but Seal buff is active, remove the effects
	                if (Seal_Level >= 6) {
	                    this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
	                    this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
	                    this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
	                    this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
	                    this.setDynamicHp(this.getDynamicHp() - (Seal_Level * 20));
	                    this.setDynamicSp(this.getDynamicSp() - Seal_Level);
	                    this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
	                    this.setDynamicAc(this.getDynamicAc() - Seal_Level);
	                } else {
	                    this.setDynamicAddDmg(this.getDynamicAddDmg() - Seal_Level);
	                    this.setDynamicAddDmgBow(this.getDynamicAddDmgBow() - Seal_Level);
	                    this.setDynamicAddHit(this.getDynamicAddHit() - Seal_Level);
	                    this.setDynamicAddHitBow(this.getDynamicAddHitBow() - Seal_Level);
	                    this.setDynamicSp(this.getDynamicSp() - Seal_Level);
	                    this.setDynamicExp(this.getDynamicExp() - (Seal_Level * 0.01));
	                    this.setDynamicAc(this.getDynamicAc() - Seal_Level);
	                }

	                isSealBuff = false;
	                Seal_Level = 0;
	                toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
	                toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
	                ChattingController.toChatting(this, "[알림] 전장의 가호 효과가 해제됩니다.", Lineage.CHATTING_MODE_MESSAGE);
	            }
	        }
	    } catch (Exception e) {
	        // Handle the exception
	    }
	}
	
	
	/**
	 * 51레벨 이상 스탯 보너스 계산 메서드. 2018-05-02 by all-night.
	 */
	public void setStat() {
		// 레벨 51이상시 스탯보너스 부분
		if (level > 50 && 50 + getLvStr() + getLvDex() + getLvCon() + getLvInt() + getLvWis() + getLvCha() + getLevelUpStat() - getElixir() < level && getResetBaseStat() == 0 && getResetLevelStat() == 0)
			setLevelUpStat(getLevelUpStat() + 1);
	}

	@Override
	public void toGiveItem(object o, ItemInstance item, long count) {
		// 같은 혈맹이외에 다른 대상에게 아이템 줄수 없음.
		if (o != null && !(o instanceof RobotInstance) && o instanceof PcInstance) {
			if (o.getClanId() == 0 || getClanId() == 0 || o.getClanId() != getClanId())
				return;
		}

		if (!getInventory().isWeightPercent(82)) {
			ChattingController.toChatting(o, "대상에게 아이템을 더이상 줄 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}

		super.toGiveItem(o, item, count);
	}

	@Override
	public boolean isAutoPickup() {
		return auto_pickup;
	}

	@Override
	public void setAutoPickup(boolean is) {
		auto_pickup = is;
	}

	@Override
	public boolean isHpbar() {
		return is_hpbar;
	}

	@Override
	public void setHpbar(boolean is_hpbar) {
		this.is_hpbar = is_hpbar;
	}

	public byte[] getDbInterface() {
		return db_interface;
	}

	public void setDbInterface(byte[] dbInterface) {
		db_interface = dbInterface;
	}


	public int getSpeedHackWarningCounting() {
		return SpeedhackWarningCounting;
	}

	public void setSpeedHackWarningCounting(int SpeedhackWarningCounting) {
		this.SpeedhackWarningCounting = SpeedhackWarningCounting;
	}

	public void savePet() {
		Summon s = SummonController.find(this);
		if (s != null) {
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				// 모든 펫 저장.
				SummonController.toSave(con, this);
				// 모든 펫 제거 하면서 펫목걸이도 갱신.
				s.removeAllPet();
			} catch (Exception e) {
				lineage.share.System.println(PetMasterInstance.class.toString() + " : toPush(PcInstance pc)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}

	/**
	 * 자동물약 함수. 2018-05-10 by all-night.
	 */
	public void autoPotion() {
		if (getInventory() != null && !isWorldDelete() && !isInvis() && !isTransparent() && !isDead() && !isLock() && !isLockHigh() && !isLockLow()) {
			ItemInstance item = null;

			for (ItemInstance potion : getInventory().getList()) {
				if (potion instanceof HealingPotion) {
					if (potion.getItem() != null && potion.getItem().getName().equalsIgnoreCase(autoPotionName)) {
						item = potion;
						break;
					}
				}
			}

			if (item != null) {
				if (item.isClick(this))
					item.toClick(this, null);
			} else {
				ChattingController.toChatting(this, "[자동 물약] 설정된 물약이 모두 소모되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
				autoPotionName = null;
			}
		}
	}

	/**
	 * 자동칼질 타겟 정보 초기화. 2019-02-14 by connector12@nate.com
	 */
	public void resetAutoAttack() {
		autoAttackTarget = null;
		autoAttackTime = 0L;
		targetX = 0;
		targetY = 0;
	}

	/**
	 * 자동칼질 비활성화 메소드. 2019-07-07 by connector12@nate.com
	 */
	public void cancelAutoAttack() {
		isAutoAttack = false;
		autoAttackTarget = null;
		autoAttackTime = 0L;
		targetX = 0;
		targetY = 0;
		ChattingController.toChatting(this, "자동칼질이 비활성화 되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 자동물약 PvP 여부 확인 메소드. 2019-07-04 by connector12@nate.com
	 */
	public void checkAutoPotionPvP(Character cha) {
		if (cha instanceof PcInstance && !Lineage.is_pvp_auto_potion) {
			PcInstance use = (PcInstance) cha;

			if (isAutoPotion && (World.isNormalZone(getX(), getY(), getMap()) || World.isTeamBattleMap(this))) {
				isAutoPotion = false;
				ChattingController.toChatting(this, "PK 또는 팀대전시 자동물약이 비활성화 됩니다.", Lineage.CHATTING_MODE_MESSAGE);
			}

			if (use.isAutoPotion && (World.isNormalZone(use.getX(), use.getY(), use.getMap()) || World.isTeamBattleMap(use))) {
				use.isAutoPotion = false;
				ChattingController.toChatting(use, "PK 또는 팀대전시 자동물약이 비활성화 됩니다.", Lineage.CHATTING_MODE_MESSAGE);
			}
		}
	}

	public void readSwap() {
		if (swap != null) {
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;

			try {
				synchronized (swap) {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("SELECT * FROM characters_swap WHERE cha_objId=?");
					st.setLong(1, getObjectId());
					rs = st.executeQuery();

					while (rs.next()) {
						if (swap == null)
							swap = new HashMap<String, Swap[]>();

						String key = rs.getString("swap_name");
						Swap[] item = swap.get(key);

						if (item == null)
							item = new Swap[Lineage.SLOT_ARROW + 1];
						else
							swap.remove(key);

						Swap temp = null;
						if (!rs.getString("swap_item_name").equalsIgnoreCase("null")) {
							temp = new Swap();
							temp.setObjId(rs.getLong("swap_item_objId"));
							temp.setItem(rs.getString("swap_item_name"));
							temp.setBless(rs.getInt("swap_item_bless"));
							temp.setEnLevel(rs.getInt("swap_item_en"));
						}

						item[rs.getInt("swap_idx")] = temp;
						swap.put(key, item);
					}
				}
			} catch (Exception e) {
				lineage.share.System.println("readSwap 에러. 캐릭터: " + getName());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}
	}

	public void saveSwap(Connection con) {
		if (swap != null) {
			PreparedStatement st = null;

			try {
				synchronized (swap) {
					st = con.prepareStatement("DELETE FROM characters_swap WHERE cha_objId=?");
					st.setLong(1, getObjectId());
					st.executeUpdate();
					st.close();

					Iterator<Map.Entry<String, Swap[]>> entries = swap.entrySet().iterator();
					while (entries.hasNext()) {
						Entry<String, Swap[]> entry = (Entry<String, Swap[]>) entries.next();
						String key = entry.getKey();
						Swap[] s = entry.getValue();

						for (int i = 0; i < s.length; i++) {
							boolean isNull = s[i] == null ? true : false;
							st = con.prepareStatement("INSERT INTO characters_swap SET cha_objId=?, swap_name=?, swap_idx=?, swap_item_objId=?, swap_item_name=?, swap_item_bless=?, swap_item_en=?");
							st.setLong(1, getObjectId());
							st.setString(2, key);
							st.setInt(3, i);
							st.setLong(4, isNull ? 0 : s[i].getObjId());
							st.setString(5, isNull ? "null" : s[i].getItem());
							st.setInt(6, isNull ? 0 : s[i].getBless());
							st.setInt(7, isNull ? 0 : s[i].getEnLevel());
							st.executeUpdate();
							st.close();
						}
					}
				}
			} catch (Exception e) {
				lineage.share.System.println("saveSwap 에러. 캐릭터: " + getName());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st);
			}
		}
	}

	public void removeSwap(String key) {
		if (swap != null) {
			Connection con = null;
			PreparedStatement st = null;

			try {
				synchronized (swap) {
					con = DatabaseConnection.getLineage();
					st = con.prepareStatement("DELETE FROM characters_swap WHERE cha_objId=? AND swap_name=?");
					st.setLong(1, getObjectId());
					st.setString(2, key);
					st.executeUpdate();

					swap.remove(key);
				}
			} catch (Exception e) {
				lineage.share.System.println("removeSwap 에러. 캐릭터: " + getName());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}

	public void putSwap(String key, Swap[] swapList) {
		synchronized (swap) {
			swap.put(key, swapList);
		}
	}

	public Swap[] getSwap(String key) {
		synchronized (swap) {
			return swap.get(key);
		}
	}

	public boolean insertSwap(String key) {
		if (isInsertSwap) {
			synchronized (swap) {
				boolean insert = true;
				Inventory inv = getInventory();
				Swap[] item = new Swap[Lineage.SLOT_ARROW + 1];

				if (swap.get(key) != null) {
					insert = false;
					swap.remove(key);
				}

				if (inv != null) {
					for (int i = 0; i <= Lineage.SLOT_ARROW; i++) {
						ItemInstance slot = inv.getSlot(i);

						if (slot == null) {
							item[i] = null;
						} else {
							Swap temp = new Swap();
							temp.setObjId(slot.getObjectId());
							temp.setItem(slot.getItem().getName());
							temp.setEnLevel(slot.getEnLevel());
							temp.setBless(slot.getBless());

							item[i] = temp;
						}
					}

					swap.put(key, item);
				}

				if (insert)
					ChattingController.toChatting(this, String.format("[%s] 장비 스왑 등록 완료.", key), Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(this, String.format("[%s] 장비 스왑 수정 완료.", key), Lineage.CHATTING_MODE_MESSAGE);

				NpcSpawnlistDatabase.itemSwap.toTalk(this, null);
				isInsertSwap = false;
				return true;
			}
		}
		return false;
	}

	/**
	 * 스피드핵 체크. 2019-08-29 by connector12@nate.com
	 */
	public boolean isFrameSpeed(int action) {
		// 스피드핵을 체크 안할경우 무시.
		if (Lineage.speedhack == false)
			return true;
		// 로봇은 무시.
		if (this instanceof RobotInstance)
			return true;
		if (speedCheck > System.currentTimeMillis())
			return true;
		if (HackNoCheckDatabase.isHackCheck(this))
			return true;
		if (this.getGm() > 0) {
			return true;
		}
		double frame = 0;
		long FrameTime = 0;
		long time = System.currentTimeMillis();

		switch (action) {
		case Lineage.GFX_MODE_WALK:
			frame = SpriteFrameDatabase.getSpeedCheckGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK);
			FrameTime = WalkFrameTime;
			WalkFrameTime = time;
			break;
		case Lineage.GFX_MODE_ATTACK:
			frame = SpriteFrameDatabase.getSpeedCheckGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_ATTACK);
			FrameTime = AttackFrameTime;
			AttackFrameTime = time;
			break;
		default:
			frame = SpriteFrameDatabase.getSpeedCheckGfxFrameTime(this, getGfx(), action);
			break;
		}

		lastActionTime = time + (long) frame;

		switch (action) {
		case Lineage.GFX_MODE_WALK:
			frame = Math.round(frame * Lineage.speed_check_walk_frame_rate);
			break;
		case Lineage.GFX_MODE_ATTACK:
			frame = Math.round(frame * Lineage.speed_check_attack_frame_rate);
			break;
		}

		long 유저프레임 = time - FrameTime;
		long 정상프레임 = (long) frame;

		// 스핵 체크
		if (유저프레임 < 정상프레임) {
			if (action == Lineage.GFX_MODE_ATTACK) {
				if (Lineage.is_attack_count_packet)
					badPacketCount++;
				// 비정상적인 패킷이 공격 패킷일 경우 공격 못하게함.
				return false;
			} else if (action == Lineage.GFX_MODE_SPELL_DIRECTION || action == Lineage.GFX_MODE_SPELL_NO_DIRECTION) {
				// 반딜 노딜일 경우 배드패킷 누적횟수+2.
				badPacketCount++;
			} else {
				// 비정상적인 패킷이 들어올 경우.
				badPacketCount++;
			}
		} else {
			if (action == Lineage.GFX_MODE_ATTACK) {
				if (Lineage.is_attack_count_packet) {
					// 정상적인 패킷이 들어올 경우.
					if (badPacketCount > 0)
						badPacketCount--;

					if (++goodPacketCount >= Lineage.speed_good_packet_count) {
						goodPacketCount = 0;

						if (--SpeedhackWarningCounting < 0)
							SpeedhackWarningCounting = 0;
					}
				}
			} else {
				// 정상적인 패킷이 들어올 경우.
				if (badPacketCount > 0)
					badPacketCount--;

				if (++goodPacketCount >= Lineage.speed_good_packet_count) {
					goodPacketCount = 0;

					if (--SpeedhackWarningCounting < 0)
						SpeedhackWarningCounting = 0;
				}
			}
		}

		// 스핵 횟수 체크.
		if (badPacketCount >= Lineage.speed_bad_packet_count) {
			speedCheck(action, 유저프레임, 정상프레임);
			badPacketCount = 0;
		}
		return true;
	}

	/**
	 * 액션 가능한지 확인 2020-09-27 by connector12@nate.com
	 */
	public boolean isActionCheck(boolean isWalk) {
		long time = System.currentTimeMillis();

		if (isWalk) {
			if (time < lastMagicActionTime) {
				halfDelayCount++;
				FrameSpeedOverStun.init(this, 2);

				if (!Common.system_config_console) {
					String timeString = Util.getLocaleString(time, true);
					String log = String.format("[%s]\t [반딜, 노딜]\t [캐릭터: %s]\t [경고 횟수: %d회]\t [GFX: %d]\t [GFX MODE: %d]\t [프레임: %d]", timeString, getName(), halfDelayCount, getGfx(), getGfxMode(),
							lastMagicActionTime - time);

					GuiMain.display.asyncExec(new Runnable() {
						public void run() {
							GuiMain.getViewComposite().getSpeedHackComposite().toLog(log);
						}
					});
				}
				return false;
			}
		} else {
			if (Lineage.attackAndMagic_delay > 0 && time < lastActionTime && lastActionTime - time > Lineage.attackAndMagic_delay) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 스피드핵 경고. 2019-08-28 by connector12@nate.com
	 */
	public void speedCheck(int mode, long 유저프레임, long 정상프레임) {
		++SpeedhackWarningCounting;

		if (Lineage.speedhack_stun) {
			if (SpeedhackWarningCounting >= Lineage.speed_hack_message_count && SpeedhackWarningCounting < Lineage.speedhack_warning_count) {
				if (Lineage.speed_hack_block_time > 0) {
					if (Lineage.speed_hack_block_time >= 60)
						ChattingController.toChatting(this, String.format("[스피드핵 경고 %d회] %d회 이상 경고받을 경우 %d분간 마비.", SpeedhackWarningCounting, Lineage.speedhack_warning_count, (Lineage.speed_hack_block_time / 60)),
								Lineage.CHATTING_MODE_MESSAGE);
					else
						ChattingController.toChatting(this, String.format("[스피드핵 경고 %d회] %d회 이상 경고받을 경우 %d초간 마비.", SpeedhackWarningCounting, Lineage.speedhack_warning_count, Lineage.speed_hack_block_time),
								Lineage.CHATTING_MODE_MESSAGE);
				}
			}

			if (SpeedhackWarningCounting >= Lineage.speedhack_warning_count) {
				if (!Common.system_config_console) {
					long time = System.currentTimeMillis();
					String timeString = Util.getLocaleString(time, true);

					String log = String.format("[%s]\t [스피드핵]\t [캐릭터: %s]\t [경고 횟수: %d회]\t [GFX: %d]\t [GFX MODE: %d]\t [유저 프레임: %d]\t [정상 프레임: %d]", timeString, getName(), SpeedhackWarningCounting, getGfx(), mode,
							유저프레임, 정상프레임);

					GuiMain.display.asyncExec(new Runnable() {
						public void run() {
							GuiMain.getViewComposite().getSpeedHackComposite().toLog(log);
						}
					});
				}

				if (Lineage.speed_hack_block_time > 0) {
					FrameSpeedOverStun.init(this, true);

					if (Lineage.speed_hack_block_time >= 60)
						ChattingController.toChatting(this, String.format("스피드핵 경고 횟수 초과 %d분간 캐릭터 마비.", (Lineage.speed_hack_block_time / 60)), Lineage.CHATTING_MODE_MESSAGE);
					else
						ChattingController.toChatting(this, String.format("스피드핵 경고 횟수 초과 %d초간 캐릭터 마비.", Lineage.speed_hack_block_time), Lineage.CHATTING_MODE_MESSAGE);
				}

				SpeedhackWarningCounting = 0;
			}
		}
	}

	public boolean 뚫어핵체크객체(object o) {
		if (o == null)
			return false;

		return o instanceof object;
	}

	public boolean 뚫어핵체크제외객체(object o) {
		if (o == null)
			return true;

		return o instanceof ItemInstance || o instanceof Door || o instanceof KingdomDoor || o instanceof KingdomCastleTop || o instanceof KingdomCrown || o instanceof SpotTower || o instanceof SpotCrown
				|| o instanceof Switch || o instanceof Firewall || o instanceof LifeStream || o instanceof BackgroundTile || o instanceof DeathEffect || o instanceof MagicDollInstance;
	}

	public boolean 뚫어핵체크제외Gfx(object o) {
		if (o == null)
			return true;

		return o.getGfx() == 1284 || o.getGfx() == 1036;
	}

	public boolean 뚫어핵체크(int x, int y) {
		boolean result = false;
		object target = null;

		if (getGm() == 0) {
			for (object o : getInsideList()) {
				if (o != null && o.getX() == x && o.getY() == y && o.getMap() == getMap()) {
					if (!o.isWorldDelete() && !o.isDead() && !o.isTransparent() && !뚫어핵체크제외Gfx(o) && !뚫어핵체크제외객체(o) && 뚫어핵체크객체(o)) {
						result = true;
						target = o;
						gostHackCount++;
						break;
					}
				}
			}
		}

		if (result && target != null) {
			if (Lineage.gost_hack_block_time > 0) {
				FrameSpeedOverStun.init(this, false);

				if (Lineage.gost_hack_block_time >= 60)
					ChattingController.toChatting(this, String.format("[뚫어핵 의심] %d분간 캐릭터 마비.", (Lineage.gost_hack_block_time / 60)), Lineage.CHATTING_MODE_MESSAGE);
				else
					ChattingController.toChatting(this, String.format("[뚫어핵 의심] %d초간 캐릭터 마비.", Lineage.gost_hack_block_time), Lineage.CHATTING_MODE_MESSAGE);
			}

			if (!Common.system_config_console) {
				long time = System.currentTimeMillis();
				String timeString = Util.getLocaleString(time, true);

				String log = String.format("[%s]\t [뚫어핵 의심]\t [캐릭터: %s]\t [뚤어핵 횟수: %d회]\t [GFX: %d]\t [object GFX: %d]\t [%s]", timeString, getName(), gostHackCount, getGfx(), target.getGfx(), target.getClass());

				GuiMain.display.asyncExec(new Runnable() {
					public void run() {
						GuiMain.getViewComposite().getSpeedHackComposite().toLog(log);
					}
				});
			}
			return false;
		}
		return true;
	}

	/**
	 * 장비 확인 막대 2020-10-21 by connector12@nate.com
	 */
	public void pcItemCheck(boolean yes) {
		if (getInventory() != null) {
			if (yes) {
				PcInstance use = getItemCheckPc();
				if (use != null && use.getInventory() != null) {
					if (getGm() > 0 || getInventory().isAden(Lineage.item_check_name, Lineage.item_check_count, true)) {
						List<String> list = new ArrayList<String>();
						list.add(use.getName());

						int idx = 0;
						for (ItemInstance i : use.getInventory().getList()) {
							if (i != null && i.getItem() != null) {
								String name = i.getItem().getName();
								int bless = i.getBless();
								int en = i.getEnLevel();
								long count = i.getCount();

								if (i.isEquipped()) {
									if (i.getEnLevel() > 0) {
										if (bless == 1)
											list.add(String.format("%d. [착용] +%d %s(%,d)", ++idx, en, name, count));
										else
											list.add(String.format("%d. [착용] (%s) +%d %s(%,d)", ++idx, bless == 0 ? "축" : "저주", en, name, count));
									} else {
										if (bless == 1)
											list.add(String.format("%d. [착용] %s(%,d)", ++idx, name, count));
										else
											list.add(String.format("%d. [착용] %s(%,d)", ++idx, String.format("(%s) %s", bless == 0 ? "축" : "저주", name), count));
									}
								} else {
									if (getGm() > 0) {
										if (i.getEnLevel() > 0) {
											if (bless == 1)
												list.add(String.format("%d. +%d %s(%,d)", ++idx, en, name, count));
											else
												list.add(String.format("%d. (%s) +%d %s(%,d)", ++idx, bless == 0 ? "축" : "저주", en, name, count));
										} else {
											if (bless == 1)
												list.add(String.format("%d. %s(%,d)", ++idx, name, count));
											else
												list.add(String.format("%d. %s(%,d)", ++idx, String.format("(%s) %s", bless == 0 ? "축" : "저주", name), count));
										}
									}
								}
							}
						}

						if (getGm() == 0) {
							String msg = String.format("\\fY[장비 확인] %s 님이 당신의 장비를 확인하였습니다.", getName());
							ChattingController.toChatting(use, msg, Lineage.CHATTING_MODE_MESSAGE);
						}

						if (list.size() < 2)
							toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "pcitemch1", null, list));
						else
							toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "pcitemch", null, list));
					} else {
						toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 776, Lineage.item_check_name));
					}
				}
			}
		}

		setItemCheckPc(null);
	}

	/**
	 * 고정 멤버 버프 2020-11-19 by connector12@nate.com
	 */
	public void 고정멤버버프(boolean isPacket) {
		if (isMember()) {
			setDynamicCritical(getDynamicCritical() + 2);
			setDynamicBowCritical(getDynamicBowCritical() + 2);
			setDynamicMagicCritical(getDynamicMagicCritical() + 2);
			ChattingController.toChatting(this, "\\fY고정 멤버: 근거리 치명타, 원거리 치명타, 마법 치명타 +2%", Lineage.CHATTING_MODE_MESSAGE);

			if (isPacket) {
				toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), this));
				toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), this));
			}
		}
	}

	public void showAutoHuntHtml() {
		try {
			List<String> autoHunt = new ArrayList<String>();
			autoHunt.clear();

			String msg = null;
			if (Lineage.is_auto_hunt_time) {
				long time = 0;

				if (Lineage.is_auto_hunt_time_account) {
					time = auto_hunt_account_time;
				} else {
					time = auto_hunt_time;
				}

				if (time > 0) {
					if (time / 3600 > 0) {
						msg = String.format("[남은 시간: %d시간 %d분 %d초]", time / 3600, time % 3600 / 60, time % 3600 % 60);
					} else if (time % 3600 / 60 > 0) {
						msg = String.format("[남은 시간: %d분 %d초]", time % 3600 / 60, time % 3600 % 60);
					} else {
						msg = String.format("[남은 시간: %d초]", time % 3600 % 60);
					}
				} else {
					msg = "[남은 시간: 없음]";
				}
			} else {
				msg = "[남은 시간: 무제한]";
			}

			autoHunt.add(msg == null ? " " : msg);
			autoHunt.add(isAutoHunt ? "ON" : "OFF");
			autoHunt.add(auto_return_home_hp < 1 ? "설정 X" : String.format("%d%%", auto_return_home_hp));

			for (int i = 0; i < Lineage.auto_hunt_home_hp_list.size(); i++) {
				autoHunt.add(String.format("%d%%", Lineage.auto_hunt_home_hp_list.get(i)));

				if (i > 6) {
					break;
				}
			}

			for (int i = 0; i < 7 - Lineage.auto_hunt_home_hp_list.size(); i++) {
				autoHunt.add(" ");
			}

			autoHunt.add(is_auto_buff ? "ON" : "OFF");

			autoHunt.add(isAutoPotion ? "ON" : "OFF");
			autoHunt.add(is_auto_potion_buy ? "ON" : "OFF");
			autoHunt.add(autoPotionPercent < 1 ? "설정 X" : String.format("%d%%", autoPotionPercent));
			autoHunt.add(autoPotionName == null || autoPotionName.length() < 1 ? "설정 X" : autoPotionName);

			autoHunt.add(is_auto_poly_select ? "랭변" : "일반");

			autoHunt.add(is_auto_rank_poly ? "ON" : "OFF");
			autoHunt.add(is_auto_rank_poly_buy ? "ON" : "OFF");

			autoHunt.add(is_auto_poly ? "ON" : "OFF");
			autoHunt.add(is_auto_poly_buy ? "ON" : "OFF");
			autoHunt.add(getQuickPolymorph() == null || getQuickPolymorph().length() < 1 ? "설정 X" : getQuickPolymorph());

			autoHunt.add(is_auto_teleport ? "ON" : "OFF");

			autoHunt.add(is_auto_bravery ? "ON" : "OFF");
			autoHunt.add(is_auto_bravery_buy ? "ON" : "OFF");

			autoHunt.add(is_auto_haste ? "ON" : "OFF");
			autoHunt.add(is_auto_haste_buy ? "ON" : "OFF");

			autoHunt.add(is_auto_arrow_buy ? "ON" : "OFF");

			autoHunt.add(auto_return_home_hp < 1 ? "설정 X" : String.format("%d%%", auto_return_home_hp));

			for (int i = 0; i < Lineage.auto_hunt_home_hp_list.size(); i++) {
				autoHunt.add(String.format("%d%%", Lineage.auto_hunt_home_hp_list.get(i)));

				if (i > 6) {
					break;
				}
			}

			for (int i = 0; i < 7 - Lineage.auto_hunt_home_hp_list.size(); i++) {
				autoHunt.add(" ");
			}

			if (getClassType() == Lineage.LINEAGE_CLASS_WIZARD) {
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt3", null, autoHunt));
			}
			if (getClassType() == Lineage.LINEAGE_CLASS_ELF) {
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt2", null, autoHunt));

			}
			if (getClassType() == Lineage.LINEAGE_CLASS_KNIGHT) {
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt", null, autoHunt));

			}
			if (getClassType() == Lineage.LINEAGE_CLASS_ROYAL) {
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt4", null, autoHunt));

			}
			if (getClassType() == Lineage.LINEAGE_CLASS_DARKELF) {
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt5", null, autoHunt));
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] showAutoHuntHtml()\r\n : %s\r\n", e.toString());
		}
	}

	public void checkPotion() {
		try {
			// 인벤토리에서 설정한 물약 찾기.
			boolean isPotion = false;
			for (ItemInstance potion : getInventory().getList()) {
				if (potion != null && potion.getItem() != null && potion instanceof HealingPotion && potion.getItem().getName().equalsIgnoreCase(autoPotionName)) {
					isPotion = true;
					break;
				}
			}

			// 설정한 물약이 인벤토리에 존재하지 않으면 설정 초기화.
			if (!isPotion)
				autoPotionName = null;
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] checkPotion()\r\n : %s\r\n", e.toString());
		}
	}

	public void showAutoPotionHtml() {
		try {
			if (getInventory() != null) {
				if (autoPotionIdx == null)
					autoPotionIdx = new String[20];

				checkPotion();

				List<String> autoPotion = new ArrayList<String>();
				autoPotion.clear();
				autoPotion.add(autoPotionPercent < 1 ? "설정 X" : String.format("%d%% 이하 물약 복용", autoPotionPercent));

				for (int i = 0; i < Lineage.auto_hunt_potion_hp_list.size(); i++) {
					autoPotion.add(String.format("%d%%", Lineage.auto_hunt_potion_hp_list.get(i)));

					if (i > 6) {
						break;
					}
				}

				for (int i = 0; i < 7 - Lineage.auto_hunt_potion_hp_list.size(); i++) {
					autoPotion.add(" ");
				}

				autoPotion.add(autoPotionName == null || autoPotionName.length() < 2 ? "설정 X" : autoPotionName);

				// 인벤토리에서 물약종류를 선택.
				int idx = 0;
				for (ItemInstance potion : getInventory().getList()) {
					if (potion != null && potion.getItem() != null && potion instanceof HealingPotion) {
						autoPotion.add(String.format("%s (%s)", potion.getItem().getName(), Util.changePrice(potion.getCount())));
						autoPotionIdx[idx] = potion.getItem().getName();
						idx++;
					}
				}

				for (int i = 0; i < autoPotionIdx.length; i++) {
					if (idx == 0 && i == 0) {
						autoPotion.add("인벤토리에 물약이 존재하지 않습니다.");
					} else {
						autoPotion.add(" ");
					}
				}

				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "autohunt1", null, autoPotion));
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] showAutoPotionHtml()\r\n : %s\r\n", e.toString());
		}
	}

	public void showAutoSkillHtml() {
		try {

			List<String> Manachekc = new ArrayList<String>();
			Manachekc.clear();
			Manachekc.add(is_auto_skill ? "ON" : "OFF");
			switch (getClassType()) {
			
			case Lineage.LINEAGE_CLASS_KNIGHT:
			case Lineage.LINEAGE_CLASS_ROYAL:
			case Lineage.LINEAGE_CLASS_DARKELF:
			case Lineage.LINEAGE_CLASS_WIZARD:
			    List<String> Info = addAutoHuntInfo(Lineage.auto_hunt_mp_list, autoMPPercent);
			    Manachekc.addAll(Info); // Manachekc에 wizardInfo를 추가
				break;
			case Lineage.LINEAGE_CLASS_ELF:
			    List<String> Info2 = addAutoHuntInfo(Lineage.auto_hunt_mp_list, autoMPPercent);
			    Manachekc.addAll(Info2); // Manachekc에 wizardInfo를 추가
			    List<String> Info3 = addAutoHuntInfo(Lineage.auto_hunt_mp_list2, autoMPPercent2);
			    Manachekc.addAll(Info3); // Manachekc에 wizardInfo를 추가
				break;
			
			}
		
			switch (getClassType()) {
			case Lineage.LINEAGE_CLASS_KNIGHT:
				Manachekc.add(is_auto_reductionarmor ? "ON" : "OFF");
				Manachekc.add(is_auto_solidcarriage ? "ON" : "OFF");
				Manachekc.add(is_auto_counterbarrier ? "ON" : "OFF");
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill", null, Manachekc));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if(this.getAttribute() == 0){
					
					Manachekc.add(is_auto_bloodtosoul ? "ON" : "OFF");
					Manachekc.add(is_auto_triplearrow ? "ON" : "OFF");
					Manachekc.add(is_auto_resistmagic ? "ON" : "OFF");
					Manachekc.add(is_auto_clearmind ? "ON" : "OFF");
					Manachekc.add(is_auto_resistelement ? "ON" : "OFF");
	
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill2", null, Manachekc));
				}
				if (this.getAttribute() == Lineage.ELEMENT_WATER) {
					Manachekc.add(is_auto_bloodtosoul ? "ON" : "OFF");
					Manachekc.add(is_auto_triplearrow ? "ON" : "OFF");
					Manachekc.add(is_auto_resistmagic ? "ON" : "OFF");
					Manachekc.add(is_auto_clearmind ? "ON" : "OFF");
					Manachekc.add(is_auto_resistelement ? "ON" : "OFF");
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill2", null, Manachekc));
				}
				if (this.getAttribute() == Lineage.ELEMENT_EARTH) {
					Manachekc.add(is_auto_bloodtosoul ? "ON" : "OFF");
					Manachekc.add(is_auto_triplearrow ? "ON" : "OFF");
					Manachekc.add(is_auto_resistmagic ? "ON" : "OFF");
					Manachekc.add(is_auto_clearmind ? "ON" : "OFF");
					Manachekc.add(is_auto_resistelement ? "ON" : "OFF");
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill2", null, Manachekc));
				}
				if (this.getAttribute() == Lineage.ELEMENT_FIRE) {
					Manachekc.add(is_auto_bloodtosoul ? "ON" : "OFF");
					Manachekc.add(is_auto_triplearrow ? "ON" : "OFF");
					Manachekc.add(is_auto_resistmagic ? "ON" : "OFF");
					Manachekc.add(is_auto_clearmind ? "ON" : "OFF");
					Manachekc.add(is_auto_resistelement ? "ON" : "OFF");
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill2", null, Manachekc));
				}
				if (this.getAttribute() == Lineage.ELEMENT_WIND) {
					Manachekc.add(is_auto_bloodtosoul ? "ON" : "OFF");
					Manachekc.add(is_auto_triplearrow ? "ON" : "OFF");
					Manachekc.add(is_auto_resistmagic ? "ON" : "OFF");
					Manachekc.add(is_auto_clearmind ? "ON" : "OFF");
					Manachekc.add(is_auto_resistelement ? "ON" : "OFF");
					toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill2", null, Manachekc));
				}
		
				break;
			case Lineage.LINEAGE_CLASS_ROYAL:
				Manachekc.add(is_auto_glowingweapon ? "ON" : "OFF");
				Manachekc.add(is_auto_shiningshieldon ? "ON" : "OFF");
				Manachekc.add(is_auto_bravemental ? "ON" : "OFF");
				Manachekc.add(is_auto_braveavatar ? "ON" : "OFF");
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill4", null, Manachekc));
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				Manachekc.add(is_enchantvenom ? "ON" : "OFF");
				Manachekc.add(is_burningspirits ? "ON" : "OFF");
				Manachekc.add(is_shadowarmor ? "ON" : "OFF");
				Manachekc.add(is_doublebrake ? "ON" : "OFF");
				Manachekc.add(is_shadowpong ? "ON" : "OFF");
				Manachekc.add(is_uncannydodge ? "ON" : "OFF");
				Manachekc.add(is_dressmighty ? "ON" : "OFF");
				Manachekc.add(is_dressdexterity ? "ON" : "OFF");
				Manachekc.add(is_dressevasion ? "ON" : "OFF");
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill5", null, Manachekc));
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				Manachekc.add(is_turnundead ? "ON" : "OFF");
				Manachekc.add(is_snakebite ? "ON" : "OFF");
				Manachekc.add(is_eruption ? "ON" : "OFF");
				Manachekc.add(is_sunburst ? "ON" : "OFF");
				Manachekc.add(is_berserkers ? "ON" : "OFF");
				Manachekc.add(is_Immunity ? "ON" : "OFF");
				toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "huntskill3", null, Manachekc));
				break;

			}

		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] showAutoSkillHtml()\r\n : %s\r\n", e.toString());
		}
	}
	public List<String> addAutoHuntInfo(List<Integer> autoHuntMpList, int autoMPPercent) {
	    List<String> Manachekc = new ArrayList<String>();
	    
	    Manachekc.add(autoMPPercent < 1 ? "설정 X" : String.format("%d%% 이상 스킬 사용", autoMPPercent));

	    for (int i = 0; i < autoHuntMpList.size(); i++) {
	        Manachekc.add(String.format("%d%%", autoHuntMpList.get(i)));

	        if (i > 6) {
	            break;
	        }
	    }

	    for (int i = 0; i < 7 - autoHuntMpList.size(); i++) {
	        Manachekc.add(" ");
	    }
	    
	    return Manachekc; // Manachekc 리스트를 반환
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		try {
			if (action.contains("autohunt-")) {
				action = action.replace("autohunt-", "");

				if (action.contains("skill-")) {

					action = action.replace("skill-", "");

					if (action.equalsIgnoreCase("on")) {
						pc.is_auto_skill = true;
					} else if (action.equalsIgnoreCase("off")) {

						pc.is_auto_skill = false;
					}

					if (action.contains("mana-")) {
						action = action.replace("mana-", "");

						if (action.equalsIgnoreCase("1")) {
							if (Lineage.auto_hunt_mp_list.size() > 0) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(0);
							}
						} else if (action.equalsIgnoreCase("2")) {
							if (Lineage.auto_hunt_mp_list.size() > 1) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(1);
							}
						} else if (action.equalsIgnoreCase("3")) {
							if (Lineage.auto_hunt_mp_list.size() > 2) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(2);
							}
						} else if (action.equalsIgnoreCase("4")) {
							if (Lineage.auto_hunt_mp_list.size() > 3) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(3);
							}
						} else if (action.equalsIgnoreCase("5")) {
							if (Lineage.auto_hunt_mp_list.size() > 4) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(4);
							}
						} else if (action.equalsIgnoreCase("6")) {
							if (Lineage.auto_hunt_mp_list.size() > 5) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(5);
							}
						} else if (action.equalsIgnoreCase("7")) {
							if (Lineage.auto_hunt_mp_list.size() > 6) {
								autoMPPercent = Lineage.auto_hunt_mp_list.get(6);
							}
						}
						if (action.equalsIgnoreCase("8")) {
							if (Lineage.auto_hunt_mp_list2.size() > 0) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(0);
							}
						} else if (action.equalsIgnoreCase("9")) {
							if (Lineage.auto_hunt_mp_list2.size() > 1) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(1);
							}
						} else if (action.equalsIgnoreCase("10")) {
							if (Lineage.auto_hunt_mp_list2.size() > 2) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(2);
							}
						} else if (action.equalsIgnoreCase("11")) {
							if (Lineage.auto_hunt_mp_list2.size() > 3) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(3);
							}
						} else if (action.equalsIgnoreCase("12")) {
							if (Lineage.auto_hunt_mp_list2.size() > 4) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(4);
							}
						} else if (action.equalsIgnoreCase("13")) {
							if (Lineage.auto_hunt_mp_list2.size() > 5) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(5);
							}
						} else if (action.equalsIgnoreCase("14")) {
							if (Lineage.auto_hunt_mp_list2.size() > 6) {
								autoMPPercent2 = Lineage.auto_hunt_mp_list2.get(6);
							}
						}

					} else if (action.equalsIgnoreCase("reductionarmoron")) {
						pc.is_auto_reductionarmor = true;
					} else if (action.equalsIgnoreCase("reductionarmoroff")) {
						pc.is_auto_reductionarmor = false;
					} else if (action.equalsIgnoreCase("solidcarriageon")) {
						pc.is_auto_solidcarriage = true;
					} else if (action.equalsIgnoreCase("solidcarriageoff")) {
						pc.is_auto_solidcarriage = false;
					} else if (action.equalsIgnoreCase("counterbarrieron")) {
						pc.is_auto_counterbarrier = true;
					} else if (action.equalsIgnoreCase("counterbarrieroff")) {
						pc.is_auto_counterbarrier = false;
					} else if (action.equalsIgnoreCase("glowingweaponon")) {
						pc.is_auto_glowingweapon = true;
					} else if (action.equalsIgnoreCase("glowingweaponoff")) {
						pc.is_auto_glowingweapon = false;
					} else if (action.equalsIgnoreCase("shiningshieldon")) {
						pc.is_auto_shiningshieldon = true;
					} else if (action.equalsIgnoreCase("shiningshieldoff")) {
						pc.is_auto_shiningshieldon = false;
					} else if (action.equalsIgnoreCase("bravementalon")) {
						pc.is_auto_bravemental = true;
					} else if (action.equalsIgnoreCase("bravementaloff")) {
						pc.is_auto_bravemental = false;
					} else if (action.equalsIgnoreCase("braveavataron")) {
						pc.is_auto_braveavatar = true;
					} else if (action.equalsIgnoreCase("braveavataroff")) {
						pc.is_auto_braveavatar = false;
					} else if (action.equalsIgnoreCase("enchantvenomon")) {
						pc.is_enchantvenom = true;
					} else if (action.equalsIgnoreCase("enchantvenomoff")) {
						pc.is_enchantvenom = false;
					} else if (action.equalsIgnoreCase("burningspiritson")) {
						pc.is_burningspirits = true;
					} else if (action.equalsIgnoreCase("burningspiritsoff")) {
						pc.is_burningspirits = false;
					} else if (action.equalsIgnoreCase("shadowpongon")) {
						pc.is_shadowpong = true;
					} else if (action.equalsIgnoreCase("shadowpongoff")) {
						pc.is_shadowpong = false;
					} else if (action.equalsIgnoreCase("shadowarmoron")) {
						pc.is_shadowarmor = true;
					} else if (action.equalsIgnoreCase("shadowarmoroff")) {
						pc.is_shadowarmor = false;
					} else if (action.equalsIgnoreCase("uncannydodgeon")) {
						pc.is_uncannydodge = true;
					} else if (action.equalsIgnoreCase("uncannydodgeoff")) {
						pc.is_uncannydodge = false;
					} else if (action.equalsIgnoreCase("doublebrakeon")) {
						pc.is_doublebrake = true;
					} else if (action.equalsIgnoreCase("doublebrakeoff")) {
						pc.is_doublebrake = false;
					} else if (action.equalsIgnoreCase("dressmightyon")) {
						pc.is_dressmighty = true;
					} else if (action.equalsIgnoreCase("dressmightyoff")) {
						pc.is_dressmighty = false;
					} else if (action.equalsIgnoreCase("dressdexterityon")) {
						pc.is_dressdexterity = true;
					} else if (action.equalsIgnoreCase("dressdexterityoff")) {
						pc.is_dressdexterity = false;
					} else if (action.equalsIgnoreCase("dressevasionon")) {
						pc.is_dressevasion = true;
					} else if (action.equalsIgnoreCase("dressevasionoff")) {
						pc.is_dressevasion = false;
						
					} else if (action.equalsIgnoreCase("turnundeadon")) {
						pc.is_turnundead = true;
						pc.is_snakebite = false;
						pc.is_eruption = false;
						pc.is_sunburst = false;
					} else if (action.equalsIgnoreCase("turnundeadoff")) {
						pc.is_turnundead = false;
					} else if (action.equalsIgnoreCase("snakebiteon")) {
						pc.is_turnundead = false;
						pc.is_snakebite = true;
						pc.is_eruption = false;
						pc.is_sunburst = false;
					} else if (action.equalsIgnoreCase("snakebiteoff")) {
						pc.is_snakebite = false;
					} else if (action.equalsIgnoreCase("eruptionon")) {
						pc.is_turnundead = false;
						pc.is_snakebite = false;
						pc.is_eruption = true;
						pc.is_sunburst = false;
					} else if (action.equalsIgnoreCase("eruptionoff")) {
						pc.is_eruption = false;
					} else if (action.equalsIgnoreCase("sunburston")) {
						pc.is_turnundead = false;
						pc.is_snakebite = false;
						pc.is_eruption = false;
						pc.is_sunburst = true;
					} else if (action.equalsIgnoreCase("sunburstoff")) {
						pc.is_sunburst = false;
					} else if (action.equalsIgnoreCase("berserkerson")) {
						pc.is_berserkers = true;
					} else if (action.equalsIgnoreCase("berserkersoff")) {
						pc.is_berserkers = false;
					} else if (action.equalsIgnoreCase("Immunityon")) {
						pc.is_Immunity = true;
					} else if (action.equalsIgnoreCase("Immunityoff")) {
						pc.is_Immunity = false;
					} else if (action.equalsIgnoreCase("triplearrowon")) {
						pc.is_auto_triplearrow = true;
					} else if (action.equalsIgnoreCase("triplearrowoff")) {
						pc.is_auto_triplearrow = false;
					} else if (action.equalsIgnoreCase("resistmagicon")) {
						pc.is_auto_resistmagic = true;
					} else if (action.equalsIgnoreCase("resistmagicoff")) {
						pc.is_auto_resistmagic = false;
					} else if (action.equalsIgnoreCase("clearmindon")) {
						pc.is_auto_clearmind = true;
					} else if (action.equalsIgnoreCase("clearmindoff")) {
						pc.is_auto_clearmind = false;
					} else if (action.equalsIgnoreCase("resistelementon")) {
						pc.is_auto_resistelement = true;
					} else if (action.equalsIgnoreCase("resistelementoff")) {
						pc.is_auto_resistelement = false;
					} else if (action.equalsIgnoreCase("bloodtosoulon")) {
						pc.is_auto_bloodtosoul = true;
					} else if (action.equalsIgnoreCase("bloodtosouloff")) {
						pc.is_auto_bloodtosoul = false;
					}
					showAutoSkillHtml();

				} else if (action.contains("potion-")) {
					action = action.replace("potion-", "");

					if (action.contains("percent-")) {
						action = action.replace("percent-", "");

						if (action.equalsIgnoreCase("1")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 0) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(0);
							}
						} else if (action.equalsIgnoreCase("2")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 1) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(1);
							}
						} else if (action.equalsIgnoreCase("3")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 2) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(2);
							}
						} else if (action.equalsIgnoreCase("4")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 3) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(3);
							}
						} else if (action.equalsIgnoreCase("5")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 4) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(4);
							}
						} else if (action.equalsIgnoreCase("6")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 5) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(5);
							}
						} else if (action.equalsIgnoreCase("7")) {
							if (Lineage.auto_hunt_potion_hp_list.size() > 6) {
								autoPotionPercent = Lineage.auto_hunt_potion_hp_list.get(6);
							}
						}
					}

					if (action.contains("item-")) {
						try {
							int idx = Integer.valueOf(action.replace("item-", "").trim());
							pc.autoPotionName = pc.autoPotionIdx[idx];
						} catch (Exception e) {
							ChattingController.toChatting(pc, "[자동 물약] 물약 설정이 잘못되었습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					}

					showAutoPotionHtml();
				} else {
					if (action.equalsIgnoreCase("on")) {
						if (Lineage.is_auto_hunt_time) {
							long time = 0;
							if (Lineage.is_auto_hunt_time_account) {
								time = auto_hunt_account_time;
							} else {
								time = auto_hunt_time;
							}

							if (time < 1) {
								ChattingController.toChatting(this, "              \\fY자동 사냥 이용 시간이 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
								return;
							}
						}
						
					
						if (!isAutoHuntCheck()) {
							ChattingController.toChatting(this, "             \\fY자동 사냥을 사용할 수 없는 상태입니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (!Lineage.is_auto_hunt) {
							ChattingController.toChatting(this, "                \\fY자동 사냥은 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (Lineage.is_auto_hunt_member && !isMember()) {
							ChattingController.toChatting(this, "                \\fY고정 멤버만 사용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (World.isSafetyZone(getX(), getY(), getMap()) && getMap() != 70) {
							ChattingController.toChatting(this, "              \\fY세이프티존에서 시작할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (auto_return_home_hp < 1) {
							ChattingController.toChatting(this, "          \\fY자동 귀환 체력 설정을 해주시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (!isAutoPotion || autoPotionPercent < 1 || autoPotionName == null || autoPotionName.length() < 1) {
							ChattingController.toChatting(this, "             \\fY자동 물약 설정을 해주시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						boolean isMap = false;
						for (int map : Lineage.auto_hunt_map_list) {
							if (map == getMap()) {
								isMap = true;
								break;
							}
						}

						if (!isMap) {
							ChattingController.toChatting(this, "           \\fY자동 사냥을 사용할 수 없는 위치입니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (lastMovingTime + 500 > System.currentTimeMillis()) {
							ChattingController.toChatting(this, "                \\fY이동 중에 시작할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (!isAutoHunt) {
							autohunt_target = null;
							autoAttackTarget = null;
							autoAttackTime = 0L;
							targetX = 0;
							targetY = 0;
							this.isAutoAttack = false;
							is_auto_return_home = false;
							isAutoHunt = true;
							ai_time = 0;
							ChattingController.toChatting(this, "                   \\fR자동 사냥을 시작합니다.", Lineage.CHATTING_MODE_MESSAGE);

							start_x = getX();
							start_y = getY();
							start_map = getMap();
							temp_x = getX();
							temp_y = getY();
							temp_map = getMap();
						}
					} else if (action.equalsIgnoreCase("off")) {
						if (isAutoHunt) {
							endAutoHunt(false, false);
						}
					} else if (action.equalsIgnoreCase("1")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 0) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(0);
						}
					} else if (action.equalsIgnoreCase("2")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 1) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(1);
						}
					} else if (action.equalsIgnoreCase("3")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 2) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(2);
						}
					} else if (action.equalsIgnoreCase("4")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 3) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(3);
						}
					} else if (action.equalsIgnoreCase("5")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 4) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(4);
						}
					} else if (action.equalsIgnoreCase("6")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 5) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(5);
						}
					} else if (action.equalsIgnoreCase("7")) {
						if (Lineage.auto_hunt_home_hp_list.size() > 6) {
							auto_return_home_hp = Lineage.auto_hunt_home_hp_list.get(6);
						}
					} else if (action.equalsIgnoreCase("buffon")) {
						pc.is_auto_buff = true;
					} else if (action.equalsIgnoreCase("buffoff")) {
						pc.is_auto_buff = false;
					} else if (action.equalsIgnoreCase("potionon")) {
						pc.isAutoPotion = true;
					} else if (action.equalsIgnoreCase("potionoff")) {
						pc.isAutoPotion = false;
					} else if (action.equalsIgnoreCase("potionbuy")) {
						if (Lineage.is_auto_potion_buy) {
							pc.is_auto_potion_buy = true;
						} else {
							ChattingController.toChatting(this, "           \\fY자동 물약 구매는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("potionnobuy")) {
						pc.is_auto_potion_buy = false;
					} else if (action.equalsIgnoreCase("poly-nomal")) {
						pc.is_auto_poly_select = false;
					} else if (action.equalsIgnoreCase("poly-rank")) {
						pc.is_auto_poly_select = true;
					} else if (action.equalsIgnoreCase("poly-rank-on")) {
						if (Lineage.is_auto_poly_rank) {
							pc.is_auto_rank_poly = true;
						} else {
							ChattingController.toChatting(this, " \\fY자동 랭변은 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("poly-rank-off")) {
						pc.is_auto_rank_poly = false;
					} else if (action.equalsIgnoreCase("poly-rank-buy")) {
						if (Lineage.is_auto_poly_rank_buy) {
							pc.is_auto_rank_poly_buy = true;
						} else {
							ChattingController.toChatting(this, "             \\fY자동 랭변 구매는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("poly-rank-nobuy")) {
						pc.is_auto_rank_poly_buy = false;
					} else if (action.equalsIgnoreCase("polyon")) {
						if (Lineage.is_auto_poly) {
							Poly p = PolyDatabase.getName(pc.getQuickPolymorph());

							if (p != null) {
								pc.is_auto_poly = true;
							} else {
								ChattingController.toChatting(this, "      \\fY변신 주문서로 사용할 변신을 선택하시기 바랍니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
						} else {
							ChattingController.toChatting(this, "              \\fY자동 변신은 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("polyoff")) {
						pc.is_auto_poly = false;
					} else if (action.equalsIgnoreCase("polybuy")) {
						if (Lineage.is_auto_poly_buy) {
							pc.is_auto_poly_buy = true;
						} else {
							ChattingController.toChatting(this, "             \\fY자동 변줌 구매는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("polynobuy")) {
						pc.is_auto_poly_buy = false;
					} else if (action.equalsIgnoreCase("teleporton")) {
						if (Lineage.is_auto_teleport) {
							pc.is_auto_teleport = true;

							if (!isAutoHuntTeleportMap()) {
								ChattingController.toChatting(this, "           \\fY자동 텔레포트를 사용할 수 없는 맵입니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
						} else {
							ChattingController.toChatting(this, "              \\fY자동 텔레포트는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("teleportoff")) {
						pc.is_auto_teleport = false;
					} else if (action.equalsIgnoreCase("braveryon")) {
						if (Lineage.is_auto_bravery) {
							pc.is_auto_bravery = true;
						} else {
							ChattingController.toChatting(this, "              \\fY자동 용기는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("braveryoff")) {
						pc.is_auto_bravery = false;
					} else if (action.equalsIgnoreCase("braverybuy")) {
						if (Lineage.is_auto_bravery_buy) {
							pc.is_auto_bravery_buy = true;
						} else {
							ChattingController.toChatting(this, "             \\fY자동 용기 구매는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("braverynobuy")) {
						pc.is_auto_bravery_buy = false;
					} else if (action.equalsIgnoreCase("hasteon")) {
						if (Lineage.is_auto_haste) {
							pc.is_auto_haste = true;
						} else {
							ChattingController.toChatting(this, "              \\fY자동 촐기는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("hasteoff")) {
						pc.is_auto_haste = false;
					} else if (action.equalsIgnoreCase("hastebuy")) {
						if (Lineage.is_auto_haste_buy) {
							pc.is_auto_haste_buy = true;
						} else {
							ChattingController.toChatting(this, "             \\fY자동 촐기 구매는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
						}
					} else if (action.equalsIgnoreCase("hastenobuy")) {
						pc.is_auto_haste_buy = false;
					} else if (action.equalsIgnoreCase("arrowbuy")) {
						pc.is_auto_arrow_buy = true;
					} else if (action.equalsIgnoreCase("arrowbuynobuy")) {
						pc.is_auto_arrow_buy = false;
					}
					showAutoHuntHtml();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp)\r\n : %s\r\n", e.toString());
		}
	}
	public void wantedmap() {
		int map = getMap();
		boolean found = false;
		for (int value : Lineage.w_map_list) {
		    if (value == map) {
		        found = true;
		        break;
		    }
		}

		if (found && !WantedController.checkWantedPc(this)) {
		    ChattingController.toChatting(this, "수배자만 이용 가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
		    int[] loc = Lineage.getHomeXY();
		    toTeleport(loc[0], loc[1], loc[2], true);
		}
	}
	public void endAutoHunt(boolean isHome, boolean isTimeOver) {
		try {
			isAutoHunt = false;
			autohunt_target = null;
			autoAttackTarget = null;
			autoAttackTime = 0L;
			targetX = 0;
			targetY = 0;
			this.isAutoAttack = false;
			is_auto_return_home = false;
			ai_time= 0;

			if (isTimeOver) {
				ChattingController.toChatting(this, "      \\fY이용 가능 시간이 부족하여 자동 사냥을 종료합니다.", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(this, "                   \\fY자동 사냥을 종료합니다.", Lineage.CHATTING_MODE_MESSAGE);
			}

			toTeleport(getX(), getY(), getMap(), false);

			if (isHome) {
				int[] home = null;
				home = Lineage.getHomeXY();
				toTeleport(home[0], home[1], home[2], true);
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] endAutoHunt()\r\n : %s\r\n", e.toString());
		}
	}

	/**
	 * 자동 사냥 가능한 상태인지 확인
	 * 
	 * @return
	 */
	public boolean isAutoHuntCheck() {
		try {

			if (this != null && !this.isDead() && !this.isWorldDelete() && !this.isLock() && !this.isInvis() && !this.isTransparent()) {
				return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] isAutoHuntCheck()\r\n : %s\r\n", e.toString());
		}

		return false;
	}

	/**
	 * 자동 사냥 ai부분
	 * 
	 * @param time
	 */
	public void toAutoHunt(long time) {
		try {

			if (Lineage.is_auto_hunt && isAutoHunt) {
				
				if(isDead()) {
					this.toReset(true);
					endAutoHunt(false, false);
				}
				
				
				if (isWorldDelete() || isInvis() || isTransparent()) {
					endAutoHunt(false, false);
					return;
				}
				
				if (this.isAutoAttack) {
					this.isAutoAttack = false;
					this.resetAutoAttack();
				} 
				if (this.getLevel() < Lineage.auto_level) {
					ChattingController.toChatting(this, String.format("자동사냥은 %d레벨 부터 가능합니다.", Lineage.auto_level), Lineage.CHATTING_MODE_MESSAGE);

					endAutoHunt(false, false);
					return;
				}
				if (!getInventory().isAutoHuntInventory()) {
					endAutoHunt(true, false);
					return;
				}

				if (isAutoHuntCheck()) {
					if (isAi(time)) {
						if (Lineage.is_auto_hunt_member && !isMember()) {
							return;
						}

						Clan clan = ClanController.find((PcInstance) this);

						if (this.isMark) {
							this.isMark = false;

							for (Clan c : ClanController.getClanList().values()) {
								if (c != null && !c.getName().equalsIgnoreCase(clan.getName()) && !c.getName().equalsIgnoreCase(Lineage.new_clan_name) && !c.getName().equalsIgnoreCase(Lineage.teamBattle_A_team)
										&& !c.getName().equalsIgnoreCase(Lineage.teamBattle_B_team)) {
									this.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, clan.getName(), c.getName()));
								}
							}
						}



						// 자동 귀환
						autoHuntGotoHome(time);
						// 자동 아이템 구매
						autoHuntItemBuy();

						// 자동 버프
						autoHuntBuff(time);
						autoHuntbuff2();

						// 자동 변신
						autoHuntPoly();

						if (!is_auto_return_home) {
							if (World.isSafetyZone(getX(), getY(), getMap()) && getMap() != 70) {
								endAutoHunt(false, false);
								ChattingController.toChatting(this, "              \\fY세이프티존에서 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);

								return;
							}
							// 몬스터 검색
							findMonster();
							// 화면 갱신
							autoHuntStartLocation();

							if (autohunt_target != null) {
								if (autohunt_target.isDead() || autohunt_target.isWorldDelete() || !World.isAttack(this, autohunt_target) || !Util.isAreaAttack(this, autohunt_target)
										|| !Util.isAreaAttack(autohunt_target, this)) {
									autohunt_target = null;
									return;
								}
								toAiAttack(time);
							} else {
								if (!autoHuntTeleport(time)) {
									toAiWalk(time);
								} else {
									autoHuntTeleport(time);
								}
							}

						}
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] toAutoHunt(long time)\r\n : %s\r\n", e.toString());
		}
	}

	@Override
	protected void toAiAttack(long time) {
		try {
			// 마법 사용 시도
			boolean magicUsed = autoHuntSkill();

			// 마법 사용에 실패한 경우에만 물리 공격 시도
			if (!magicUsed) {
				super.toAiAttack(time);
			}

			if (this instanceof PcRobotInstance) {
				return;
			}

			auto_hunt_teleport_time = time + (Lineage.auto_hunt_telpeport_delay);

			object o = autohunt_target;

			boolean bow = getInventory().활장착여부();
			int atkRange = bow ? 8 : 1;

			if (Util.isDistance(this, o, atkRange) && Util.isAreaAttack(this, o) && Util.isAreaAttack(o, this)) {
				// 마법 사용에 실패한 경우에만 물리 공격 시도
				if (!magicUsed && (AttackController.isAttackTime(this, getGfxMode() + Lineage.GFX_MODE_ATTACK, false) || AttackController.isMagicTime(this, getCurrentSkillMotion()))) {
					int frame = (int) (SpriteFrameDatabase.getSpeedCheckGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_ATTACK) + 40);
					ai_time = frame;

					toAttack(o, o.getX(), o.getY(), bow, getGfxMode() + Lineage.GFX_MODE_ATTACK, 0, false);
				}
			} else {
				int frame = (int) (SpriteFrameDatabase.getSpeedCheckGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_WALK) + 50);
				ai_time = frame;
				autoHuntMoving(this, o.getX(), o.getY(), 0, true);
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] toAiAttack(long time)\r\n : %s\r\n", e.toString());
		}
	}

	@Override
	protected void toAiWalk(long time) {
		try {
			super.toAiWalk(time);

			if (this instanceof PcRobotInstance) {
				return;
			}

			do {
				switch (Util.random(0, 7)) {
				case 0:
				case 1:
					break;
				case 2:
					setHeading(getHeading() + 1);
					break;
				case 3:
				case 4:
				case 5:
					break;
				case 6:
					setHeading(getHeading() - 1);
					break;
				case 7:
					setHeading(Util.random(0, 7));
					break;
				}
				// 이동 좌표 추출.
				int x = Util.getXY(heading, true) + this.x;
				int y = Util.getXY(heading, false) + this.y;

				if ((getMap() == 0 || getMap() == 4) && !Util.isDistance(x, y, map, start_x, start_y, map, 60) && auto_hunt_teleport_time < time) {
					toTeleport(start_x, start_y, start_map, true);
				}

				// 해당 좌표 이동가능한지 체크.
				boolean tail = World.isThroughObject(this.x, this.y, this.map, heading) && (World.isMapdynamic(x, y, map) == false) && !World.isNotMovingTile(x, y, map);
				if (tail) {
					// 타일이 이동가능하고 객체가 방해안하면 이동처리.
					autoHuntMoving(this, x, y, heading, true);
					auto_hunt_teleport_time = time + (Lineage.auto_hunt_telpeport_delay);
				}
			} while (false);
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] toAiWalk(long time)\r\n : %s\r\n", e.toString());
		}
	}

	/**
	 * 자동 사냥 몬스터 검색
	 */
	private void findMonster() {
		try {
			if (autohunt_target == null) {
				object temp = null;

				for (object o : getInsideList()) {
					if (o != null && o instanceof MonsterInstance && !o.isDead() && !o.isWorldDelete() && World.isAttack(this, o) && Util.isAreaAttack(this, o) && Util.isAreaAttack(o, this)) {
						if (o instanceof SummonInstance) {
							SummonInstance s = (SummonInstance) o;

							if (s.getSummon() != null && s.getSummon().getMaster() != null && s.getSummon().getMaster() instanceof PcInstance) {
								continue;
							}
						}

						MonsterInstance m = (MonsterInstance) o;

						if (m.getAttackList() == null || m.getAttackListSize() == 0 || (m.getAttackList(0) != null && m.getAttackList(0).getObjectId() == getObjectId())) {
							if (temp == null) {
								temp = o;
								continue;
							} else {
								int range_1 = Util.getDistance(this, o);
								int range_2 = Util.getDistance(this, temp);

								if (range_1 < range_2) {
									temp = o;
								}
							}
						}
					}
				}

				autohunt_target = temp;
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] findMonster()\r\n : %s\r\n", e.toString());
		}

	}

	/**
	 * 시작 위치에서 벗어났을경우 화면 갱신
	 */
	public void autoHuntStartLocation() {
		try {
			if (!Util.isDistance(temp_x, temp_y, temp_map, getX(), getY(), getMap(), Lineage.BOW_ATTACK_LOCATIONRANGE)) {
				temp_x = getX();
				temp_y = getY();
				temp_map = getMap();
				toTeleport(getX(), getY(), getMap(), false);
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntStartLocation()\r\n : %s\r\n", e.toString());
		}
	}

	/**
	 * 설정한 체력 이하로 내려갈 경우 자동 귀환
	 */
	public void autoHuntGotoHome(long time) {

		try {
			if (isAutoHuntCheck()) {
				if (!is_auto_return_home && getHpPercent() < auto_return_home_hp) {
					auto_hunt_teleport_time = time + 2000;
					autohunt_target = null;
					is_auto_return_home = true;
					int[] home = null;
					home = Lineage.getHomeXY();
					toTeleport(home[0], home[1], home[2], true);
				}

				if (is_auto_return_home && getHpPercent() >= Lineage.auto_hunt_go_min_hp && auto_hunt_teleport_time < time) {
					is_auto_return_home = false;
					toTeleport(start_x, start_y, start_map, true);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntGotoHome()\r\n : %s\r\n", e.toString());
		}
	}

	/**
	 * 매개변수 좌표로 A스타를 발동시켜 이동시키기. 객체가 존재하는 지역은 패스하도록 함. 이동할때마다 aStar가 새로 그려지기때문에
	 * 과부하가 심함.
	 */
	private boolean autoHuntMoving(object o, final int x, final int y, final int h, final boolean astar) {
		try {
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
					toSender(S_ObjectMoving.clone(BasePacketPooling.getPool(S_ObjectMoving.class), this));
					return true;
				} else {
					return false;
				}
			} else {
				toMoving(x, y, h);
				return true;
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntMoving(object o, final int x, final int y, final int h, final boolean astar)\r\n : %s\r\n", e.toString());
		}
		
		return false;
	}
	

	/**
	 * 자동 스킬
	 */
	public boolean autoHuntSkill() {
		try {
			if (this instanceof PcRobotInstance) {
				return false;
			}
			int rand = new Random().nextInt(100) + 1;

			if (getInventory() != null && autohunt_target != null && !isDead() && !isWorldDelete()) {
				switch (getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL:

					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:

					break;
				case Lineage.LINEAGE_CLASS_ELF:
					if (getInventory().활장착여부()) {
						Skill s = SkillDatabase.find(115);
						Skill s1 = SkillDatabase.find(116);
						if (is_auto_skill) {
							if(is_auto_bloodtosoul){
								if (getMpPercent() < autoMPPercent2) {
									if (s1 != null && SkillController.find(this, s1.getUid()) != null) {
										ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION);
										ai_time = 1500;
										BloodToSoul.init(this, s1);
										return true;
									}
								}
							}
				
						}
						if (is_auto_skill) {
							if (this.is_auto_triplearrow) {
								if (rand < Lineage.is_auto_hunt_skill_percent) {
									if (s != null && SkillController.find(this, s.getUid()) != null) {
										if (SkillController.isHpMpCheck(this, s.getHpConsume(), s.getMpConsume()) && Util.isDistance(this, autohunt_target, 8)) {
											ai_time = 800;
											TripleArrow.init(this, s, (int) autohunt_target.getObjectId(), autohunt_target.getX(), autohunt_target.getY());
											return true;
										}
									}
								}
							}
						}
					}

					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					if (getInventory().getSlot(Lineage.SLOT_WEAPON) != null) {
						Skill s = SkillDatabase.find(46);
						Skill s19 = SkillDatabase.find(18);
						Skill s29 = SkillDatabase.find(28);
						Skill s39 = SkillDatabase.find(45);
						MonsterInstance mon = (MonsterInstance) autohunt_target;
						if (autohunt_target.isDead()) {
							autohunt_target = null;
							return false;
						}
						if (is_auto_skill) {
							
							if (this.is_turnundead) {
								if (getMpPercent() > autoMPPercent) {
									if (rand < Lineage.is_auto_hunt_skill_percent) {
										if (s19 != null && SkillController.find(this, s19.getUid()) != null) {
											if (SkillController.isFigure(this, mon, s19, true, false)) {
												if (SkillController.isDelay(this, SkillDatabase.find(18))) {
													TurnUndead.init(this, s19, (int) autohunt_target.getObjectId(), (int) autohunt_target.getX(), (int) autohunt_target.getY());
													return true;
												}
											}
										}
									}
								}

							}
						}
						if (is_auto_skill) {
							if (this.is_snakebite) {

								if (getMpPercent() > autoMPPercent) {
									if (rand < Lineage.is_auto_hunt_skill_percent) {

										if (s29 != null && SkillController.find(this, s29.getUid()) != null) {
											if (SkillController.isHpMpCheck(this, s29.getHpConsume(), s29.getMpConsume()) && Util.isDistance(this, autohunt_target, 5)) {
												if (SkillController.isDelay(this, SkillDatabase.find(28))) {
													ChillTouch.init(this, s29, (int) autohunt_target.getObjectId());
													return true;
												}
											}
										}
									}
								}
							}
						}

						if (is_auto_skill) {
							if (this.is_eruption) {
								if (getMpPercent() > autoMPPercent) {
									if (rand < Lineage.is_auto_hunt_skill_percent) {
										if (s39 != null && SkillController.find(this, s39.getUid()) != null) {
											if (SkillController.isHpMpCheck(this, s39.getHpConsume(), s39.getMpConsume()) && Util.isDistance(this, autohunt_target, 8)) {
												if (SkillController.isDelay(this, SkillDatabase.find(45))) {
													Eruption.init(this, s39, (int) autohunt_target.getObjectId());
													return true;
												}
											}
										}
									}
								}
							}
						}
						if (is_auto_skill) {
							if (this.is_sunburst) {
								if (getMpPercent() > autoMPPercent) {
									if (rand < Lineage.is_auto_hunt_skill_percent) {

										if (s != null && SkillController.find(this, s.getUid()) != null) {
											if (SkillController.isHpMpCheck(this, s.getHpConsume(), s.getMpConsume()) && Util.isDistance(this, autohunt_target, 3)) {
												if (SkillController.isDelay(this, SkillDatabase.find(46))) {
													Sunburst.init(this, s, (int) autohunt_target.getObjectId());
													return true;
												}
											}
										}
									}
								}
							}
						}

					}
					break;

				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntSkill()\r\n : %s\r\n", e.toString());
		}

		return false;
	}

	public boolean autoHuntbuff2() {
		try {
			if (this instanceof PcRobotInstance) {
				return false;
			}
			if (getInventory() == null || isDead() || isWorldDelete()) {
				return false;
			}

			switch (getClassType()) {

			case Lineage.LINEAGE_CLASS_ROYAL:
				// 브레이브멘탈
				Skill s15 = SkillDatabase.find(100);
				Skill s16 = SkillDatabase.find(307);
				Skill s17 = SkillDatabase.find(309);
				Skill s18 = SkillDatabase.find(101);

				if (is_auto_skill) {

					if (getMpPercent() > autoMPPercent) {

						if (is_auto_glowingweapon) {
							if (s15 != null && SkillController.find(this, s15.getUid()) != null && SkillController.isHpMpCheck(this, s15.getHpConsume(), s15.getMpConsume())) {

								if (BuffController.find(this, s15) == null) {
							
									GlowingWeapon.init(this, s15);
									return true;
								}
							}
						}
						if (is_auto_shiningshieldon) {
							if (s16 != null && SkillController.find(this, s16.getUid()) != null && SkillController.isHpMpCheck(this, s16.getHpConsume(), s16.getMpConsume())) {

								if (BuffController.find(this, s16) == null) {
									BraveAvatar.init(this, s16);
									return true;
								}
							}
						}
						if (is_auto_bravemental) {
							if (s17 != null && SkillController.find(this, s17.getUid()) != null && SkillController.isHpMpCheck(this, s17.getHpConsume(), s17.getMpConsume())) {

								if (BuffController.find(this, s17) == null) {
									BraveMental.init(this, s17);
									return true;
								}
							}
						}
						if (is_auto_braveavatar) {
							if (s18 != null && SkillController.find(this, s18.getUid()) != null && SkillController.isHpMpCheck(this, s18.getHpConsume(), s18.getMpConsume())) {

								if (BuffController.find(this, s18) == null) {
									ShiningShield.init(this, s18);
									return true;
								}
							}
						}
					}
				}
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				Skill s12 = SkillDatabase.find(55);
				Skill s13 = SkillDatabase.find(72);
				Skill s14 = SkillDatabase.find(80);

				if (is_auto_skill) {

				
						if (is_auto_reductionarmor) {

							if (getMpPercent() > autoMPPercent) {
							if (s12 != null && SkillController.find(this, s12.getUid()) != null && SkillController.isHpMpCheck(this, s12.getHpConsume(), s12.getMpConsume())) {

								if (BuffController.find(this, s12) == null) {
									ReductionArmor.init(this, s12);
									return true;
								}
							}
							}
						}
						if (is_auto_solidcarriage) {
							if (getMpPercent() > autoMPPercent) {
							if (s13 != null && SkillController.find(this, s13.getUid()) != null && SkillController.isHpMpCheck(this, s13.getHpConsume(), s13.getMpConsume())
									&& (this.getInventory().getSlot(Lineage.SLOT_SHIELD) != null || this.getInventory().getSlot(Lineage.SLOT_GUARDER) != null)) {

								if (BuffController.find(this, s13) == null) {
									SolidCarriage.init(this, s13);
									return true;
								}
							}
							}
						}
						if (is_auto_counterbarrier) {
							if (getMpPercent() > autoMPPercent) {
							if (s14 != null && SkillController.find(this, s14.getUid()) != null && SkillController.isHpMpCheck(this, s14.getHpConsume(), s14.getMpConsume())
									&& this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("tohandsword")) {

								if (BuffController.find(this, s14) == null) {
									CounterBarrier.init(this, s14);
									return true;
								}
							}
							}
						}
					
				}

				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (getInventory().getSlot(Lineage.SLOT_WEAPON) != null) {

					// 스톰샷
					Skill s3 = SkillDatabase.find(135);
					// 이글아이
					Skill s4 = SkillDatabase.find(117);
					// 레지스트매직
					Skill s5 = SkillDatabase.find(107);
					// 클리어마인드
					Skill s6 = SkillDatabase.find(113);
					// 레지스트 엘리멘트
					Skill s7 = SkillDatabase.find(114);
					// 버닝웨펀
					Skill s8 = SkillDatabase.find(124);
					// 엘리멘탈 파이어
					Skill s9 = SkillDatabase.find(125);
					// 소울오프프레임
					Skill s10 = SkillDatabase.find(136);
					// 네이쳐스
					Skill s11 = SkillDatabase.find(128);
					ItemInstance item = getInventory().find("정령옥", 0, 1);

					if (item == null || (item != null && item.getCount() <= 10)) {

						autoHuntBuyShop("잡화 상인", "정령옥", 100);

					}
					if (is_auto_skill) {
					if (s3 != null && SkillController.find(this, s3.getUid()) != null && SkillController.isHpMpCheck(this, s3.getHpConsume(), s3.getMpConsume())
							&& this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow")) {

						if (BuffController.find(this, s3) == null) {
							StormShot.init(this, s3, (int) this.getObjectId());
							return true;
						}

					}
					if (s4 != null && SkillController.find(this, s4.getUid()) != null && SkillController.isHpMpCheck(this, s4.getHpConsume(), s4.getMpConsume())
							&& this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("bow")) {

						if (BuffController.find(this, s4) == null) {
							EagleEye.init(this, s4);
							return true;
						}

					}
						if (this.is_auto_resistmagic) {
							if (getMpPercent() > autoMPPercent) {
								if (s5 != null && SkillController.find(this, s5.getUid()) != null && SkillController.isHpMpCheck(this, s5.getHpConsume(), s5.getMpConsume())) {

									if (BuffController.find(this, s5) == null) {
										ResistMagic.init(this, s5);
										return true;
									}

								}
							}
						}
						if (this.is_auto_clearmind) {
							if (getMpPercent() > autoMPPercent) {
								if (s6 != null && SkillController.find(this, s6.getUid()) != null && SkillController.isHpMpCheck(this, s6.getHpConsume(), s6.getMpConsume())) {

									if (BuffController.find(this, s6) == null) {
										ClearMind.init(this, s6);
										return true;
									}

								}
							}
						}
						if (this.is_auto_resistelement) {
							if (getMpPercent() > autoMPPercent) {
								if (s7 != null && SkillController.find(this, s7.getUid()) != null && SkillController.isHpMpCheck(this, s7.getHpConsume(), s7.getMpConsume())) {

									if (BuffController.find(this, s7) == null) {
										ResistElemental.init(this, s7);
										return true;
									}
								}
							}
						}
					if (s8 != null && SkillController.find(this, s8.getUid()) != null && SkillController.isHpMpCheck(this, s8.getHpConsume(), s8.getMpConsume())
							&& (this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("sword")
									|| this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("dagger"))) {

						if (BuffController.find(this, s8) == null) {
							BurningWeapon.init(this, s8);
							return true;
						}
					}
					if (s9 != null && SkillController.find(this, s9.getUid()) != null && SkillController.isHpMpCheck(this, s9.getHpConsume(), s9.getMpConsume())
							&& (this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("sword")
									|| this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("dagger"))) {

						if (BuffController.find(this, s9) == null) {
							ElementalFire.init(this, s9);
							return true;
						}
					}
					if (s10 != null && SkillController.find(this, s10.getUid()) != null && SkillController.isHpMpCheck(this, s10.getHpConsume(), s10.getMpConsume())
							&& (this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("sword")
									|| this.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("dagger"))) {

						if (BuffController.find(this, s10) == null) {
							SoulOfFlame.init(this, s10);
							return true;
						}
					}
					if (s11 != null && SkillController.find(this, s11.getUid()) != null && SkillController.isHpMpCheck(this, s11.getHpConsume(), s11.getMpConsume())) {

						if (BuffController.find(this, s11) == null) {
							NaturesTouch.init(this, s11, (int) this.getObjectId());
							return true;
						}
					}

				}
				}
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				// 이뮨투함
				Skill s41 = SkillDatabase.find(23);
				Skill s21 = SkillDatabase.find(68);
				// ItemInstance item = getInventory().find("마력의 돌", 0, 1);
				//
				// if (item == null || (item != null && item.getCount() <= 10))
				// {
				//
				// autoHuntBuyShop("잡화 상인", "마력의 돌", 100);
				//
				// }
				if(is_berserkers  ){
					if (getMpPercent() > autoMPPercent) {
					if (s41 != null && SkillController.find(this, s41.getUid()) != null && SkillController.isHpMpCheck(this, s41.getHpConsume(), s41.getMpConsume())) {

						if (BuffController.find(this, s41) == null) {
							Berserks.init(this, s41, (int) this.getObjectId());
							return true;
						}
					}
					}
				} 
				if(is_Immunity ){
					if (getMpPercent() > autoMPPercent) {
					if (s21 != null && SkillController.find(this, s21.getUid()) != null && SkillController.isHpMpCheck(this, s21.getHpConsume(), s21.getMpConsume())) {

						if (BuffController.find(this, s21) == null) {
							ImmuneToHarm.init(this, s21, (int) this.getObjectId());
							return true;
						}
					}
					}
				}


				break;

			case Lineage.LINEAGE_CLASS_DARKELF:
				if (!isDead() && !this.isWorldDelete()) {
					 ItemInstance item10 = getInventory().find("흑요석", 0, 1);
//					
//					 if (item10 == null || (item10 != null &&
//					 item10.getCount() <= 10)) {
//					
//					 autoHuntBuyShop("잡화 상인", "흑요석", 30);
//					
//					 }

					if (is_auto_skill) {
					
							// 버닝 스프릿츠
							Skill s90 = SkillDatabase.find(634);
							ItemInstance item1 = this.getInventory().find(ItemDatabase.find("버닝 스프릿츠"));
							if (getMpPercent() > autoMPPercent) {
								if (is_burningspirits) {
									if (item1 != null) {
										if (BuffController.find(this, s90) == null) {
											BurningSpirit.init(this, s90);
											return true;
										}
									}
								}
							}

							Skill s98 = SkillDatabase.find(654);
							ItemInstance item8 = this.getInventory().find(ItemDatabase.find("쉐도우 아머"));
							if (getMpPercent() > autoMPPercent) {
								if (is_shadowarmor) {
									if (item8 != null) {
										if (BuffController.find(this, s98) == null) {
											BurningSpirit.init(this, s98);
											return true;
										}
									}
								}
							}
							
							// 더블브레이크
							Skill s91 = SkillDatabase.find(640);
							ItemInstance item11 = this.getInventory().find(ItemDatabase.find("더블 브레이크"));

							if (getMpPercent() > autoMPPercent) {
								if (is_doublebrake) {
									if (item11 != null) {
										if (BuffController.find(this, s91) == null) {
											DoubleBreak.init(this, s91);
											return true;
										}
									}
								}
							}
							// 언케니 닷지
							Skill s92 = SkillDatabase.find(641);
							ItemInstance item2 = this.getInventory().find(ItemDatabase.find("언케니 닷지"));
							if (getMpPercent() > autoMPPercent) {
								if (is_uncannydodge) {
									if (item2 != null) {
										if (BuffController.find(this, s92) == null) {
											UncannyDodge.init(this, s92);
											return true;
										}
									}
								}
							}
							// 쉐도우팽
							Skill s93 = SkillDatabase.find(642);
							ItemInstance item3 = this.getInventory().find(ItemDatabase.find("쉐도우 팽"));
							if (getMpPercent() > autoMPPercent) {
								if (is_shadowpong) {
									if (item3 != null) {
										if (BuffController.find(this, s93) == null) {
											if (this.getInventory().isAden("흑요석", 1, true) && item10 != null) {
												ShadowFang.init(this, s93);
												return true;
											}
										}
	
									}
								}
							}
							// 인챈트 베놈
							Skill s94 = SkillDatabase.find(656);
							ItemInstance item4 = this.getInventory().find(ItemDatabase.find("인챈트 베놈"));
							if (getMpPercent() > autoMPPercent) {
								if (is_enchantvenom) {
									if (item4 != null) {
	
										if (BuffController.find(this, s94) == null) {
											if (this.getInventory().isAden("흑요석", 1, true)  && item10 != null) {
												EnchantVenom.init(this, s94);
												return true;
											}
										}
									}
								}
							}
							// 드레스 마이티
							Skill s95 = SkillDatabase.find(643);
							ItemInstance item5 = this.getInventory().find(ItemDatabase.find("드레스 마이티"));
							if (getMpPercent() > autoMPPercent) {
								if ( is_dressmighty) {
									if (item5 != null) {
										if (BuffController.find(this, s95) == null) {
											DressMighty.init(this, s95);
											return true;
										}
	
									}
								}
							}
							// 드레스 덱스터리티
							Skill s96 = SkillDatabase.find(644);
							ItemInstance item6 = this.getInventory().find(ItemDatabase.find("드레스 덱스터리티"));
							if (getMpPercent() > autoMPPercent) {
								if (  is_dressdexterity) {
									if (item6 != null) {
										if (BuffController.find(this, s96) == null) {
											DressDexterity.init(this, s96);
											return true;
										}
	
									}
								}
							}
							// 드레스 이베이젼
							Skill s97 = SkillDatabase.find(645);
							ItemInstance item7 = this.getInventory().find(ItemDatabase.find("드레스 이베이젼"));
							if (getMpPercent() > autoMPPercent) {
								if ( is_dressevasion) {
									if (item7 != null) {
										if (BuffController.find(this, s97) == null) {
											DressEvasion.init(this, s97);
											return true;
										}
	
									}
								}
							}
						
					}

					break;
				}

			}

		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntbuff()\r\n : %s\r\n", e.toString());
		}

		return false;
	}

	/**
	 * 자동 버프, 촐기, 용기
	 */
	public void autoHuntBuff(long time) {
		try {
			// 2023 05 25 쿠베라 autoHuntBuff null 오류 수정
			if (getInventory() != null && !this.isWorldDelete()) {
				if (is_auto_buff && auto_buff_time < time) {
					if (getLevel() < Lineage.buff_max_level) {
						auto_buff_time = time + (Lineage.auto_buff_delay * 1000);
						CommandController.toBuff(this);
					} else {
						if (getInventory().isAden(Lineage.buff_aden, true)) {
							auto_buff_time = time + (Lineage.auto_buff_delay * 1000);
							CommandController.toBuff(this);
							ChattingController.toChatting(this, String.format("\\fR자동 버프: %,d아데나 소모", Lineage.buff_aden), Lineage.CHATTING_MODE_MESSAGE);
						}
					}
				}

				if (is_auto_haste && getSpeed() == 0) {
					ItemInstance item = getInventory().find(Lineage.auto_haste_item_name, 0, 1);

					if (item != null && item.getCount() > 0) {
						ai_time = 500;
						item.toClick(this, null);
					}
				}

				if (is_auto_bravery && !isBrave()) {
					String itemName = null;

					switch (getClassType()) {
					case Lineage.LINEAGE_CLASS_ROYAL:
						itemName = Lineage.auto_bravery_item_name_royal;
						break;
					case Lineage.LINEAGE_CLASS_KNIGHT:
						itemName = Lineage.auto_bravery_item_name_knight;
						break;
					case Lineage.LINEAGE_CLASS_ELF:
						itemName = Lineage.auto_bravery_item_name_elf;
						break;
					case Lineage.LINEAGE_CLASS_DARKELF:

						try {
							Skill s = SkillDatabase.find(638);

							ItemInstance item2 = this.getInventory().find(ItemDatabase.find("무빙 악셀레이션"));

							if (item2 != null) {
								if (BuffController.find(this, s) == null) {
									movingacceleratic.init(this, s);
									ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION);
								}

							}

						} catch (Exception e) {

						}
						break;
					case Lineage.LINEAGE_CLASS_WIZARD:
						if (!Lineage.is_auto_bravery_wizard_magic) {
							itemName = Lineage.auto_bravery_item_name_wizard;
						} else {
							try {
								Skill s = SkillDatabase.find(Integer.valueOf(Lineage.auto_bravery_item_name_wizard));

								if (s != null && SkillController.find(this, s.getUid()) != null) {
									ai_time = SpriteFrameDatabase.getGfxFrameTime(this, getGfx(), getGfxMode() + Lineage.GFX_MODE_SPELL_NO_DIRECTION);
									HolyWalk.init(this, s);
								}
							} catch (Exception e) {

							}
						}
						break;
					}

					if (itemName != null) {
						ItemInstance item = getInventory().find(itemName, 0, 1);

						if (item != null && item.getCount() > 0) {
							ai_time = 500;
							item.toClick(this, null);
						}
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntBuff(long time)\r\n : %s\r\n", e.toString());
		}
	}

	/**
	 * 자동 텔레포트 사용 가능한 맵 확인
	 * 
	 * @return
	 */
	public boolean isAutoHuntTeleportMap() {
		try {
			for (int map : Lineage.auto_hunt_teleport_map_list) {
				if (map == getMap()) {
					return true;
				}
				if (getMap() == 101 && (this.getInventory().find("오만의 탑 1층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 102 && (this.getInventory().find("오만의 탑 2층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 103 && (this.getInventory().find("오만의 탑 3층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 104 && (this.getInventory().find("오만의 탑 4층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 105 && (this.getInventory().find("오만의 탑 5층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 106 && (this.getInventory().find("오만의 탑 6층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 107 && (this.getInventory().find("오만의 탑 7층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 108 && (this.getInventory().find("오만의 탑 8층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 109 && (this.getInventory().find("오만의 탑 9층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
				if (getMap() == 110 && (this.getInventory().find("오만의 탑 10층 지배 부적") != null || this.getInventory().find("환상의 오만의 탑 지배 부적") != null)) {
					return true;
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] isAutoHuntTeleportMap()\r\n : %s\r\n", e.toString());
		}

		return false;
	}

	/**
	 * 자동 텔레포트
	 */
	public boolean autoHuntTeleport(long time) {

		try {

			if (is_auto_teleport && isAutoHuntTeleportMap() && auto_hunt_teleport_time < time) {

				auto_hunt_teleport_time = time + (Lineage.auto_hunt_telpeport_delay);
				autohunt_target = null;
				// Util.toRndLocation(this);
				// toTeleport(this.x, this.y, map, false);
				Util.toRndLocation(this);
				toTeleport(getHomeX(), getHomeY(), getHomeMap(), true);
				toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));

			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntTeleport()\r\n : %s\r\n", e.toString());
		}

		return false;
	}

	/**
	 * 자동 변신
	 */
	public void autoHuntPoly() {
		try {
			if (getInventory() != null) {
				if (is_auto_poly_select) {
					if (is_auto_rank_poly) {
						Poly p = PolyDatabase.getName(getRankPolyName());

						if (p != null) {
							if (getGfx() != p.getGfxId()) {
								ItemInstance item = getInventory().find("무한 " + Lineage.auto_poly_rank_item_name, 0, 1);

								if (item != null) {
									if (item.getCount() > 0) {
										item.toClick(this, null);
										return;
									}
								}

								item = getInventory().find(Lineage.auto_poly_rank_item_name, 0, 1);

								if (item == null || (item != null && item.getCount() <= Lineage.auto_poly_rank_buy_min_count)) {
									if (Lineage.is_auto_poly_rank_buy && is_auto_rank_poly_buy) {
										autoHuntBuyShop(Lineage.auto_poly_rank_buy_npc, Lineage.auto_poly_rank_item_name, Lineage.auto_poly_rank_buy_count);
										item = getInventory().find(Lineage.auto_poly_rank_item_name, 0, 1);
									}
								}

								if (item != null) {
									if (item.getCount() > 0) {
										item.toClick(this, null);
										return;
									}
								}
							}
						}
					}

					if (is_auto_poly && getQuickPolymorph() != null) {
						Poly temp = PolyDatabase.getName(getRankPolyName());
						if (temp != null && getGfx() == temp.getGfxId()) {
							return;
						}

						Poly p = PolyDatabase.getName(getQuickPolymorph());

						if (p != null) {
							if (getGfx() != p.getGfxId() && (Lineage.event_poly || p.getMinLevel() <= getLevel())) {
								ItemInstance item = getInventory().find(Lineage.auto_poly_item_name, 0, 1);

								if (item == null || (item != null && item.getCount() <= Lineage.auto_poly_buy_min_count)) {
									if (Lineage.is_auto_poly_buy && is_auto_poly_buy) {
										autoHuntBuyShop(Lineage.auto_poly_buy_npc, Lineage.auto_poly_item_name, Lineage.auto_poly_buy_count);
										item = getInventory().find(Lineage.auto_poly_item_name, 0, 1);
									}
								}

								if (item != null) {
									if (item.getCount() > 0) {
										setTempPoly(true);
										setTempPolyScroll(item);

										ShapeChange.init(this, this, PolyDatabase.getPolyName(getQuickPolymorph()), 1800, getTempPolyScroll().getBless());
										return;
									}
								}
							}
						}
					}
				} else {
					if (is_auto_poly && getQuickPolymorph() != null) {
						Poly p = PolyDatabase.getName(getQuickPolymorph());

						if (p != null) {
							if (getGfx() != p.getGfxId()) {
								ItemInstance item = getInventory().find(Lineage.auto_poly_item_name, 0, 1);

								if (item == null || (item != null && item.getCount() <= Lineage.auto_poly_buy_min_count)) {
									if (Lineage.is_auto_poly_buy && is_auto_poly_buy) {
										autoHuntBuyShop(Lineage.auto_poly_buy_npc, Lineage.auto_poly_item_name, Lineage.auto_poly_buy_count);
										item = getInventory().find(Lineage.auto_poly_item_name, 0, 1);
									}
								}

								if (item != null) {
									if (item.getCount() > 0) {
										setTempPoly(true);
										setTempPolyScroll(item);

										ShapeChange.init(this, this, PolyDatabase.getPolyName(getQuickPolymorph()), 1800, getTempPolyScroll().getBless());
										return;
									}
								}
							}
						}
					}

					if (is_auto_rank_poly) {
						Poly temp = PolyDatabase.getName(getQuickPolymorph());
						if (temp != null && getGfx() == temp.getGfxId()) {
							return;
						}

						Poly p = PolyDatabase.getName(getRankPolyName());

						if (p != null) {
							if (getGfx() != p.getGfxId() && (Lineage.event_rank_poly || p.getMinLevel() <= getLevel())) {
								ItemInstance item = getInventory().find("무한 " + Lineage.auto_poly_rank_item_name, 0, 1);

								if (item != null) {
									if (item.getCount() > 0) {
										item.toClick(this, null);
										return;
									}
								}

								item = getInventory().find(Lineage.auto_poly_rank_item_name, 0, 1);

								if (item == null || (item != null && item.getCount() <= Lineage.auto_poly_rank_buy_min_count)) {
									if (Lineage.is_auto_poly_rank_buy && is_auto_rank_poly_buy) {
										autoHuntBuyShop(Lineage.auto_poly_rank_buy_npc, Lineage.auto_poly_rank_item_name, Lineage.auto_poly_rank_buy_count);
										item = getInventory().find(Lineage.auto_poly_rank_item_name, 0, 1);
									}
								}

								if (item != null) {
									if (item.getCount() > 0) {
										item.toClick(this, null);
										return;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntPoly()\r\n : %s\r\n", e.toString());
		}
	}

	/**
	 * 랭커 변신 이름 리턴
	 * 
	 * @return
	 */
	public String getRankPolyName() {
		String polyName = null;

		switch (getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			if (getClassSex() == 0)
				polyName = "왕자 랭커";
			else
				polyName = "공주 랭커";
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			if (getClassSex() == 0)
				polyName = "남자기사 랭커";
			else
				polyName = "여자기사 랭커";
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			if (getClassSex() == 0)
				polyName = "남자요정 랭커";
			else
				polyName = "여자요정 랭커";
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			if (getClassSex() == 0)
				polyName = "남자법사 랭커";
			else
				polyName = "여자법사 랭커";
			break;
		}

		return polyName;
	}

	/**
	 * 구매하려는 아이템을 판매하는 상점 찾은 후 구매
	 * 
	 * @param item_name
	 * @return
	 */
	public void autoHuntBuyShop(String npc_name, String item_name, long item_count) {
		try {
			if (npc_name != null) {
				ShopInstance si = null;

				for (ShopInstance temp : NpcSpawnlistDatabase.getShopList()) {
					if (temp != null && temp.getNpc() != null && temp.getNpc().getName() != null && temp.getNpc().getName().equalsIgnoreCase(npc_name)) {
						for (Shop s : temp.getNpc().getShop_list()) {
							if (s.getItemName().equalsIgnoreCase(item_name) && s.getItemBress() == 1 && s.getAdenType().equalsIgnoreCase("아데나")) {
								si = temp;
								break;
							}
						}

						if (si != null) {
							break;
						}
					}
				}

				if (si != null) {
					Shop s = si.getNpc().findShopItemId(item_name, 1);

					if (s != null) {
						Item i = ItemDatabase.find(s.getItemName());

						if (i != null) {
							int shop_price = 0;

							if (s.getPrice() != 0) {
								shop_price = si.getTaxPrice(s.getPrice(), false);
							} else {
								shop_price = si.getTaxPrice(i.getShopPrice() * s.getItemCount(), false);
							}

							long new_item_count = item_count * s.getItemCount();
							long price = shop_price * item_count;

							if (getInventory().isAppend(i, item_count, i.isPiles() ? 1 : new_item_count, false)) {
								if (getInventory().isAden(s.getAdenType(), price, false)) {
									ServerBasePacket sbp = (ServerBasePacket) ServerBasePacket.clone(BasePacketPooling.getPool(ServerBasePacket.class), null);
									sbp.writeC(0); // opcode
									sbp.writeC(0); // 상점구입
									sbp.writeH(1); // 구매할 전체 갯수
									sbp.writeD(s.getUid()); // 상점 아이템 고유값
									sbp.writeD(item_count); // 구매 갯수.
									byte[] data = sbp.getBytes();
									BasePacketPooling.setPool(sbp);
									BasePacket bp = ClientBasePacket.clone(BasePacketPooling.getPool(ClientBasePacket.class), data, data.length);
									// 처리 요청.
									si.toDwarfAndShop(this, (ClientBasePacket) bp);
									// 메모리 재사용.
									BasePacketPooling.setPool(bp);

									ChattingController.toChatting(this, String.format("\\fY자동 구매: %s(%,d) -> %,d아데나 소모", item_name, item_count, price), Lineage.CHATTING_MODE_MESSAGE);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntBuyShop(String npc_name, String item_name, long item_count)\r\n : %s\r\n", e.toString());
		}
	}


	public static void DmgViewer(object target, int _damage) {
	    int baseSkillSound = 14860;

	    int[] digits = getDigits(_damage);

	    if (_damage >= 999) {
	        int[] specialEffects = {14889, 14879, 14869};
	        for (int effect : specialEffects) {
	        	target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, effect), target instanceof PcInstance);
	        }
	    }

	    int d1Calculation = baseSkillSound + digits[0] ; // Multiply units by 10
	    int d2Calculation = baseSkillSound + digits[1] + 10; // Adding 10 to the final value of d2
	    int d3Calculation = baseSkillSound + digits[2] + 20;
	    
	
	    sendObjectEffect(target, d1Calculation);
	    sendObjectEffect(target, d2Calculation);
	    sendObjectEffect(target, d3Calculation);

//	    printValues(digits[0], digits[1], digits[2], d1Calculation, d2Calculation, d3Calculation);
	}

	private static int[] getDigits(int number) {
	    int[] digits = new int[3];
	    for (int i = 0; i < 3; i++) {
	        digits[i] = number % 10;
	        number /= 10;
	    }
	    return digits;
	}

	private static void sendObjectEffect(object o, int calculation) {
	    o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, calculation), o instanceof PcInstance);
	}

//	private static void printValues(int units, int tens, int hundreds, int d1Calculation, int d2Calculation, int d3Calculation) {
//	    System.println("일의자리: " + units);
//	    System.println("십의자리: " + tens);
//	    System.println("백의자리: " + hundreds);
//	    System.println("일의자리: " + d1Calculation);
//	    System.println("십의자리: " + d2Calculation);
//	    System.println("백의자리: " + d3Calculation);
//	}
	/**
	 * 자동 사냥시 아이템 자동 구매
	 */
	public void autoHuntItemBuy() {
		try {
			if (getInventory() != null) {
				if (Lineage.is_auto_potion_buy && isAutoPotion && is_auto_potion_buy && autoPotionName != null) {
					ItemInstance item = getInventory().find(autoPotionName, 0, 1);

					if (item == null || (item != null && item.getCount() <= Lineage.auto_potion_buy_min_count)) {
						autoHuntBuyShop(Lineage.auto_potion_buy_npc, autoPotionName, Lineage.auto_potion_buy_count);
					}
				}

				if (Lineage.is_auto_haste_buy && is_auto_haste && is_auto_haste_buy) {
					ItemInstance item = getInventory().find(Lineage.auto_haste_item_name, 0, 1);

					if (item == null || (item != null && item.getCount() <= Lineage.auto_haste_buy_min_count)) {
						autoHuntBuyShop(Lineage.auto_haste_buy_npc, Lineage.auto_haste_item_name, Lineage.auto_haste_buy_count);
					}
				}

				if (Lineage.is_auto_bravery && is_auto_bravery && is_auto_bravery_buy) {
					String itemName = null;

					switch (getClassType()) {
					case Lineage.LINEAGE_CLASS_ROYAL:
						itemName = Lineage.auto_bravery_item_name_royal;
						break;
					case Lineage.LINEAGE_CLASS_KNIGHT:
						itemName = Lineage.auto_bravery_item_name_knight;
						break;
					case Lineage.LINEAGE_CLASS_ELF:
						itemName = Lineage.auto_bravery_item_name_elf;
						break;
					case Lineage.LINEAGE_CLASS_WIZARD:
						if (!Lineage.is_auto_bravery_wizard_magic) {
							itemName = Lineage.auto_bravery_item_name_wizard;
						}
						break;
					}

					if (itemName != null) {
						ItemInstance item = getInventory().find(itemName, 0, 1);

						if (item == null || (item != null && item.getCount() <= Lineage.auto_bravery_buy_min_count)) {
							autoHuntBuyShop(Lineage.auto_bravery_buy_npc, itemName, Lineage.auto_bravery_buy_count);
						}
					}
				}

				if (Lineage.is_auto_arrow_buy && is_auto_arrow_buy && getInventory().활장착여부()) {
					if (getInventory().getSlot(Lineage.SLOT_ARROW) == null) {
						ItemInstance item = getInventory().find(Lineage.auto_arrow_item_name, 0, 1);

						if (item == null) {
							autoHuntBuyShop(Lineage.auto_arrow_buy_npc, Lineage.auto_arrow_item_name, Lineage.auto_arrow_buy_count);

							item = getInventory().find(Lineage.auto_arrow_item_name, 0, 1);
							if (item != null) {
								item.toClick(this, null);
							}
						} else {
							item.toClick(this, null);
						}

						if (getInventory().getSlot(Lineage.SLOT_ARROW) == null) {
							endAutoHunt(true, false);
						}
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("[자동 사냥] autoHuntItemBuy()\r\n : %s\r\n", e.toString());
		}
	}
}
