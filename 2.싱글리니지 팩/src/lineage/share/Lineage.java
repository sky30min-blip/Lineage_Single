package lineage.share;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.FirstInventory;
import lineage.bean.database.FirstSpawn;
import lineage.bean.database.FirstSpell;
import lineage.bean.database.TeamBattleTime;
import lineage.database.BackgroundDatabase;
import lineage.util.Util;
import lineage.world.controller.ClanController;
import lineage.world.object.npc.BuffNpc;

public final class Lineage {
	// 동기화 방식 변경 여부
	static public boolean is_sync = true;
	// 칼렉 감소 적용 여부
	static public boolean is_sword_lack_check;
	// 서버 버전 : 144, 161, 230, 300 등..
	static public int server_version;
	// 시장 맵번호
	static public int market_map = 800;
	//static public int market_map = 350;
	// 시장 맵번호
	static public int market_map1 = 340;
	static public int market_map2 = 350;
	static public int market_map3 = 360;
	static public int market_map4 = 370;
	// 자동사냥 레벨제한
	static public int auto_level;
	// 잠수 유저 자동절단
	static public boolean user_ghost = true;
	static public int user_ghost_time;
	// 서버 명령어 필터
	static public String command;
	// 운영자 모드일 경우 전체채팅 따로 처리 여부
	// true: /채팅 끔 해도 보임
	// false: /채팅 끔 하면 안보임
	static public boolean is_gm_global_chat = true;
	// 화면 중앙에 메세지 띄우기 사용 여부.
	static public boolean is_blue_message;
	// 유저가 바닥에 아이템드랍 가능 여부. (복사버그 방지)
	static public boolean is_drop_item;
	// 칼질 & 휠 마법 안될시 카운트
	static public int attackAndMagic_delay;
	// 무기에 따른 공속 적용 여부
	static public boolean is_weapon_speed;
	/**
	 * 커스텀 방어구·장신구 세트 효과 (축복 6종·4종, +8~+10 인챈 6슬롯).
	 * false면 미적용. 무기 전용 커스텀 세트 보너스는 본 팩에 별도 구현 없음.
	 */
	static public boolean enable_custom_armor_accessory_set_bonus = false;
	// 칼렉 딜레이(명령어 아이템 전부)
	static public int sword_rack_delay;
	// PvP시 자동물약 사용 여부
	static public boolean is_pvp_auto_potion;
	// 채금시 명령어 사용 여부
	static public boolean is_chatting_close_command;

	// 기란 공성전 진행 시간
	static public int giran_kingdom_war_hour;
	// 기란 공성전 진행 분
	static public int giran_kingdom_war_min;
	// 면류관 주울시 몇초 뒤에 공성전 종료.
	static public int giran_kingdom_crown_min;
	// 면류관 주웠을 시 몇초마다 메세지 날릴지(초)
	static public int giran_kingdom_crown_msg_count;

	static public double speed_check_no_dir_magic_frame_rate;
	static public double speed_check_dir_magic_frame_rate;
	
	// 채금
	static public int ChatTime;
	static public int ChatTimetwo;
	
	// 반딜 사용시 실패 처리 시키기 여부
	static public boolean bandel_bug;
	// 반딜 타임
	static public int bandel_bug_check_time;

	// 출석체크 마지막 요일

	static public int lastday;

	// 퀘스트 마지막
	static public int lastquest;
	// 랜덤퀘스트 하루제한
	static public int dayquest;


	// 출석 알람 딜레이
	static public int checkment;

	// 출석체크 완료시간
	static public int dayc;
	// 출석체크
	static public String dayc1 = null;
	static public int daycc1;
	static public String dayc2 = null;
	static public int daycc2;
	static public String dayc3 = null;
	static public int daycc3;
	static public String dayc4 = null;
	static public int daycc4;
	static public String dayc5 = null;
	static public int daycc5;
	static public String dayc6 = null;
	static public int daycc6;
	static public String dayc7 = null;
	static public int daycc7;
	static public String dayc8 = null;
	static public int daycc8;
	static public String dayc9 = null;
	static public int daycc9;
	static public String dayc10 = null;
	static public int daycc10;
	static public String dayc11 = null;
	static public int daycc11;
	static public String dayc12 = null;
	static public int daycc12;
	static public String dayc13 = null;
	static public int daycc13;
	static public String dayc14 = null;
	static public int daycc14;
	static public String dayc15 = null;
	static public int daycc15;
	static public String dayc16 = null;
	static public int daycc16;
	static public String dayc17 = null;
	static public int daycc17;
	static public String dayc18 = null;
	static public int daycc18;
	static public String dayc19 = null;
	static public int daycc19;
	static public String dayc20 = null;
	static public int daycc20;
	static public String dayc21 = null;
	static public int daycc21;
	static public String dayc22 = null;
	static public int daycc22;
	static public String dayc23 = null;
	static public int daycc23;
	static public String dayc24 = null;
	static public int daycc24;
	static public String dayc25 = null;
	static public int daycc25;
	static public String dayc26 = null;
	static public int daycc26;
	static public String dayc27 = null;
	static public int daycc27;
	static public String dayc28 = null;
	static public int daycc28;
	static public String dayc29 = null;
	static public int daycc29;
	static public String dayc30 = null;
	static public int daycc30;
	
	//몬스터 퀘스트
	static public String q1 = null;
	static public int qc1;
	static public String q2 = null;
	static public int qc2;
	static public String q3 = null;
	static public int qc3;
	
	//몬스터 랜덤퀘스트
	static public String rq1 = null;
	static public int rqc1;
	static public String rq2 = null;
	static public int rqc2;
	static public String rq3 = null;
	static public int rqc3;
	
	// 결투장 사용 여부
	static public boolean is_battle_zone;
	// 결투장 좌표
	static public int battle_zone_x1;
	static public int battle_zone_y1;
	static public int battle_zone_x2;
	static public int battle_zone_y2;
	static public int battle_zone_map;

	static public int world_result = 1;

	// 결투장에서 상대방 피바 보일지 여부
	static public boolean is_battle_zone_hp_bar;
	// 매입상인에게 팔경우 아이템 가격의 몇%로 팔지 여부
	static public double sell_item_rate;
	// 축복받은 아이템(젤, 데이, 오림의 장신구 마법주문서)은 일반 아이템의 몇배 가격으로 팔지 여부
	static public double sell_bless_item_rate;
	// 저주받은 아이템(젤, 데이, 오림의 장신구 마법주문서)은 일반 아이템의 몇배 가격으로 팔지 여부
	static public double sell_curse_item_rate;
	// 축, 저주 주문서 종류 (상점에 사고 팔때 가격의 영향을 받을 아이템.
	static public String scroll_poly;
	static public String scroll_tell;
	static public String scroll_dane_fools;
	static public String scroll_zel_go_mer;
	static public String scroll_orim;
	// 힐을 상대방에게 사용하게 할지 여부
	static public boolean is_heal_target;
	// 그레이터 힐을 상대방에게 사용하게 할지 여부
	static public boolean is_greater_heal_target;
	// 어드밴스 스피릿을 상대방에게 사용하게 할지 여부
	static public boolean is_advance_spirit_target;
	// 아이언 스킨을 상대방에게 사용하게 할지 여부
	static public boolean is_iron_skin_target;
	// 스톰샷을 상대방에게 사용하게 할지 여부
	static public boolean is_storm_shot_target;
	// 일정 시간마다 월드맵을 청소할지 여부
	static public boolean is_world_clean;
	static public boolean memory_recycle = false;
	// 월드맵 청소시 알림 여부
	static public boolean is_world_clean_message;
	// 월드맵 청소 시간(분)
	static public int world_clean_time;
	// 월드맵 청소전 알림 시간(초)
	static public int world_clean_message_time;
	// 레벨업 지원 아이템 사용 가능 여부
	static public boolean is_exp_support;
	// 서버 최고 레벨과 몇렙차이 까지 사용 가능한지 여부.
	static public int exp_support_level_gap;
	// 레벨업 지원 아이템의 최대 사용 레벨
	static public int exp_support_max_level;
	// 팀대전 맵
	static public int teamBattleMap = 509;
	// 팀대전 입장 인원
	static public int teamBattle_max_pc;
	// 팀대전 입장 최소 레벨
	static public int teamBattle_level;
	// 팀대전 혈맹 이름
	static public String teamBattle_A_team;
	static public String teamBattle_B_team;
	// 팀대전 시작 몇분전 월드 메시지 보낼지 여부(분)
	static public int team_battle_world_message;
	// 팀대전 시간
	static public List<TeamBattleTime> team_battle_time = new ArrayList<TeamBattleTime>();
	// 팀배틀에서 같은팀 채팅 가능한지 여부
	static public boolean is_teamBattle_chatting;
	// 신규, 팀대전 혈맹 이외에 다른혈맹 가입시 2중(스파이) 혈맹을 가질수 있는지 여부
	static public boolean is_two_clan_join;
	// 난투전 맵
	static public int BattleRoyalMap = 89;
	// 스피드핵 패널티 스턴 변수
	static public int frame_speed_stun_uid = 310;
	// 버프 최대 중첩 시간(초)
	static public int buff_add_max_time = 7200;
	// 트리플 애로우 액션번호
	static public final int ACTION_TRIPLE_ARROW_1 = 97;
	static public final int ACTION_TRIPLE_ARROW_2 = 96;
	// 자동낚시 사용 여부
	static public boolean is_auto_fishing;
	// 낚시 딜레이(초)
	static public int fish_delay;
	// 낚시 100%확률로 무조건 획득
	static public String fish_exp;
	// 낚시 밑밥
	static public String fish_rice;
	// 자동낚시 가능한 레벨
	static public int auto_fish_level;
	// 자동낚시에 필요한 화폐종류
	static public String auto_fish_coin;
	// 자동낚시 한번당 드는 화폐 비용
	static public long auto_fish_expense;
	// 혈맹 최대 가입인원
	static public int clan_max;
	// 신규 혈맹 혈맹명
	static public String new_clan_name;
	static public String new_clan_name_temp;
	// 신규 혈맹 제한 레벨 이상 자동 탈퇴
	static public boolean is_new_clan_auto_out;
	// 신규 혈맹 레벨 제한
	static public int new_clan_max_level;
	// 신규 혈맹 PvP 가능 여부
	static public boolean is_new_clan_pvp;
	// 신규 혈맹 보스 공격 가능 여부
	static public boolean is_new_clan_attack_boss;
	// 신규 혈맹 오만의 탑 정상 이용 가능 여부
	static public boolean is_new_clan_oman_top;
	// 무혈 PvP 가능 여부
	static public boolean is_no_clan_pvp;
	// 무혈 보스 공격 가능 여부
	static public boolean is_no_clan_attack_boss;

	static public int tr_gift = 1;
	// 성혈맹 가입 여부
	static public boolean kingdom_clan_join = false;
	// 성혈맹 가입 여부
	static public boolean is_restart_giran_home;
	// 공격 방식 구분용
	static public final int ATTACK_TYPE_WEAPON = 0;
	static public final int ATTACK_TYPE_BOW = 1;
	static public final int ATTACK_TYPE_MAGIC = 2;
	static public final int ATTACK_TYPE_DIRECT = 3; // 경비에게 데미지 당햇다고 요청하는구간에
													// 필요해서 만듬.

	// 베릴소모갯수
	static public int doll_drogon = 1;

	// 변신 이팩
	static public int poly_effect = 13537;
	// 마법인형 소환가능한 최대갯수 설정.
	static public int item_magicdoll_max = 1;
	// 마법인형이 소환자와 유지할 거리.
	static public int magicdoll_location = 2;
	// 마법인형 텔레포트 이팩트 값.
	static public int doll_teleport_effect = 5935;
	static public int doll_teleport_effect1 = 5936;
	// 마법인형 이팩트
	static public int doll_addDmg_effect_white = 6319;
	static public int doll_addDmg_effect_black = 13931;
	static public int doll_defence_effect = 6320;
	static public int doll_mana_effect = 6321;
	// 마법인형 액션 표현
	static public final int[] magicDollAction = { 9, 66, 67, 68, 95, 98, 99 };
	static public final int[] AanonAction = { 4, 30, 4 };
	static public final int[] Anton = { 17, 17, 17 };
	static public final int[] Jason = { 18, 3, 18, 3 };
	static public final int[] Hector = { 17, 18, 17, 18 };
	static public final int[] Touma = { 4, 30, 4 };
	
	
	// 헬파이어 이팩트
	static public int hell_fire_effect = 11660;
	// 콜라이트닝 이팩트
	static public int call_lighting_effect = 10;
	// MISS 이팩트
	static public int miss_effect = 13418;
	static public int critical_effect = 13406;
	/** 치명타 시 모션 이펙트 표시 (GM/파워볼 오버레이 연동) */
	static public boolean view_critical_motion = true;

	
	// 마을일 경우 틱 +@
	static public int home_hp_tic;
	static public int home_mp_tic;

	// 기란감옥 시간제한 사용여부.
	static public boolean is_giran_dungeon_time;
	// 기란감옥 이용시간(분)
	static public int giran_dungeon_time;
	// 기란감옥 초기화 시간
	static public int giran_dungeon_inti_time;
	/** 기란감옥(수상한 감옥) 층 맵 — TimeDungeonDatabase 등과 동일 분기 */
	static public boolean isGiranDungeonMap(int map) {
		return (map >= 53 && map <= 56) || (map >= 653 && map <= 656);
	}
	// 던전 이용 시간 초기화 알림 여부
	static public boolean dungeon_inti_time_message;

	// 사망시 이팩트
	static public int pc_dead_effect = 439;
	static public int boss_monster_dead_effect = 384;
	static public int monster_dead_effect = 386;

	// 창고 처리 구분 종류
	static public final int DWARF_TYPE_NONE = 0;
	static public final int DWARF_TYPE_CLAN = 1;
	static public final int DWARF_TYPE_ELF = 2;

	// 채팅 구분 종류
	static public final int CHATTING_MODE_NORMAL = 0;
	static public final int CHATTING_MODE_SHOUT = 2;
	static public final int CHATTING_MODE_GLOBAL = 3;
	static public final int CHATTING_MODE_CLAN = 4;
	static public final int CHATTING_MODE_WHISPER = 9;
	static public final int CHATTING_MODE_PARTY = 11;
	static public final int CHATTING_MODE_PARTY_MESSAGE = 13;
	static public final int CHATTING_MODE_TRADE = 12;
	static public final int CHATTING_MODE_MESSAGE = 20;

	// 각 성별 고유 아이디
	static public final int KINGDOM_KENT = 1;
	static public final int KINGDOM_ORCISH = 2;
	static public final int KINGDOM_WINDAWOOD = 3;
	static public final int KINGDOM_GIRAN = 4;
	static public final int KINGDOM_HEINE = 5;
	static public final int KINGDOM_ABYSS = 6;
	static public final int KINGDOM_ADEN = 7;

	// 인공지능 상태 변수
	static public final int AI_STATUS_DELETE = -1; // 죽은상태 쓰레드에서 제거처리됨.
	static public final int AI_STATUS_WALK = 0; // 랜덤워킹 상태
	static public final int AI_STATUS_ATTACK = 1; // 공격 상태
	static public final int AI_STATUS_DEAD = 2; // 죽은 상태
	static public final int AI_STATUS_CORPSE = 3; // 시체 상태
	static public final int AI_STATUS_SPAWN = 4; // 스폰 상태
	static public final int AI_STATUS_ESCAPE = 5; // 도망 상태
	static public final int AI_STATUS_PICKUP = 6; // 아이템 줍기 상태

	// 시체 유지 시간
	static public int ai_robot_corpse_time = 5 * 1000;
	static public int ai_corpse_time = 40 * 1000;
	static public int ai_summon_corpse_time = 40 * 1000;
	static public int ai_pet_corpse_time = 120 * 1000;
	// 몬스터 체력회복제 복용 타이밍에 퍼센트값.
	static public int ai_auto_healingpotion_percent;
	// 몬스터 자연회복 틱타임 주기값.
	static public int ai_monster_tic_time;
	// 몬스터 레벨에따른 경험치 지급연산 처리 사용 여부.
	static public boolean monster_level_exp;
	// 소환된 몬스터가 아이템을 드랍할지 여부.
	static public boolean monster_summon_item_drop;
	// 몬스터에게 입힌 데미지에 비례하여 아이템 획득 여부.
	static public boolean is_damage_item_drop;
	// 몬스터 아이템 드랍 방식 부분.
	static public boolean monster_item_drop;
	// 보스몬스터스폰시 메세지 표현할지 여부.
	static public boolean monster_boss_spawn_message;
	// 보스몬스터스폰시 화면 중앙에 메세지 표현할지 여부.
	static public boolean monster_boss_spawn_blue_message;
	// 보스 죽을때 메세지를 표현할지 여부.
	static public boolean monster_boss_dead_message;
	// 보스가 스폰된 후 정해진 시간이내에 못잡을 경우 소멸.
	static public int boss_live_time;

	// 치명타 이팩트 여부
	static public boolean is_critical_effect;
	// 마법 치명타 이팩트 여부
	static public boolean is_skill_critical_effect;
	// 캐릭터 사망 이팩트 여부
	static public boolean is_character_dead_effect;
	// 몬스터 사망 이팩트 여부
	static public boolean is_monster_dead_effect;
	// miss 이팩트 여부
	static public boolean is_miss_effect;
	// 데미지 이팩트 여부
	static public boolean is_DmgViewer;
	// 기란 감옥 입장 레벨
	static public int giran_dungeon_level;

	static public int giran_dungeon_level2;
	static public int giran_dungeon_level3;

	// 펫 최대 렙업값.
	static public int pet_level_max;

	// 버프 마법 종료전 알림
	static public int buff_magic_time_min = 5;
	static public int buff_magic_time_max = 10;

	// 아이템 별 밝기 값.
	static public final int CANDLE_LIGHT = 8;
	static public final int LAMP_LIGHT = 10;
	static public final int LANTERN_LIGHT = 12;

	// 메모리상에 아이템 장착 슬롯 아이디
	static public final int SLOT_HELM = 0x00;
	static public final int SLOT_EARRING = 0x01;
	static public final int SLOT_NECKLACE = 0x02;
	static public final int SLOT_SHIRT = 0x03;
	static public final int SLOT_ARMOR = 0x04;
	static public final int SLOT_CLOAK = 0x05;
	static public final int SLOT_RING_LEFT = 0x06;
	static public final int SLOT_RING_RIGHT = 0x07;
	static public final int SLOT_BELT = 0x08;
	static public final int SLOT_GLOVE = 0x09;
	static public final int SLOT_SHIELD = 0x0A;
	static public final int SLOT_WEAPON = 0x0B;
	static public final int SLOT_BOOTS = 0x0C;
	static public final int SLOT_GUARDER = 0x0D;
	static public final int SLOT_NONE = 0x0E;
	static public final int SLOT_ARROW = 0x0F;

	// 해당 무기 착용시 변화되는 gfxmode값
	static public final int WEAPON_NONE = 0x00;
	static public final int WEAPON_SWORD = 0x04;
	static public final int WEAPON_TOHANDSWORD = 0x32;
	static public final int WEAPON_AXE = 0x0B;
	static public final int WEAPON_BOW = 0x14;
	static public final int WEAPON_SPEAR = 0x18;
	static public final int WEAPON_WAND = 0x28;
	static public final int WEAPON_DAGGER = 0x2E;
	static public final int WEAPON_BLUNT = 0x0B;
	static public final int WEAPON_CLAW = 0x28;
	static public final int WEAPON_EDORYU = 0x0B;
	static public final int WEAPON_THROWINGKNIFE = 0x0B6A;
	static public final int WEAPON_ARROW = 0x42;
	static public final int WEAPON_GAUNTLET = 0x3E;
	static public final int WEAPON_CHAINSWORD = 0x18;
	static public final int WEAPON_KEYRINK = 0x3a;
	static public final int FISHING_ROD = 0x1C;

	// 해당 무기 치명타 이팩트
	static public final int CRITICAL_EFFECT_SPEAR = 13402;
	static public final int CRITICAL_EFFECT_TWOHAND_SWORD = 13410;
	static public final int CRITICAL_EFFECT_SWORD = 13411;
	static public final int CRITICAL_EFFECT_DAGGER = 13412;
	static public final int CRITICAL_EFFECT_STAFF = 13413;
	static public final int CRITICAL_EFFECT_AXE = 13414;
	static public final int CRITICAL_EFFECT_TWOHAND_AXE = 13415;
	static public final int CRITICAL_EFFECT_NONE = 13411;

	// 클레스별 종류
	static public final int LINEAGE_CLASS_ROYAL = 0x00;
	static public final int LINEAGE_CLASS_KNIGHT = 0x01;
	static public final int LINEAGE_CLASS_ELF = 0x02;
	static public final int LINEAGE_CLASS_WIZARD = 0x03;
	static public final int LINEAGE_CLASS_DARKELF = 0x04;
	static public final int LINEAGE_CLASS_DRAGONKNIGHT = 0x05;
	static public final int LINEAGE_CLASS_BLACKWIZARD = 0x06;
	static public final int LINEAGE_CLASS_MONSTER = 0x0A;

	static public final int LINEAGE_ROYAL = 1;
	static public final int LINEAGE_KNIGHT = 2;
	static public final int LINEAGE_ELF = 4;
	static public final int LINEAGE_WIZARD = 8;
	static public final int LINEAGE_DARKELF = 16;

	// 필드 존 체크용
	static public final int NORMAL_ZONE = 0x00;
	static public final int SAFETY_ZONE = 0x10;
	static public final int COMBAT_ZONE = 0x20;

	// 만 카오틱 값 32768
	static public final int CHAOTIC = 32768;
	// 뉴트럴
	static public final int NEUTRAL = 65536;
	// 만 라이풀 값 32767
	static public final int LAWFUL = 98303;

	// 오브젝트 gfx별 모드 값
	static public final int GFX_MODE_WALK = 0;
	static public final int GFX_MODE_ATTACK = 1;
	static public final int GFX_MODE_DAMAGE = 2;
	static public final int GFX_MODE_BREATH = 3;
	static public final int GFX_MODE_RISE = 4;
	static public final int GFX_MODE_ATTACK_R_CLAW = 5;
	static public final int GFX_MODE_DEAD = 8;
	static public final int GFX_MODE_ATTACK_L_CLAW = 12;
	static public final int GFX_MODE_GET = 15;
	static public final int GFX_MODE_THROW = 16;
	static public final int GFX_MODE_WAND = 17;
	static public final int GFX_MODE_ZAP = 17;
	static public final int GFX_MODE_SPELL_DIRECTION = 18;
	static public final int GFX_MODE_SPELL_NO_DIRECTION = 19;
	static public final int GFX_MODE_OPEN = 28;
	static public final int GFX_MODE_CLOSE = 29;
	static public final int GFX_MODE_ALT_ATTACK = 30;
	static public final int GFX_MODE_SEPLL_DIRECTION_EXTRA = 31;
	static public final int GFX_MODE_DOORACTION = 32;
	static public final int GFX_MODE_SWITCH = 100;
	static public final int GFX_MODE_TYPE = 102;
	static public final int GFX_MODE_ATTR = 104;
	static public final int GFX_MODE_CLOTHES = 105;
	static public final int GFX_MODE_EFFECT = 109;
	static public final int GFX_MODE_EFFECT_NONE = 220; // 몬스터 고유 gfxmode로
														// 공격취햇을때 이팩트를 표현하면
														// 안되기때문에 표현업는 effect 값
														// 설정.

	// 글루딘 해골밭
	static public final int CHAOTICZONE1_X1 = 32884;
	static public final int CHAOTICZONE1_X2 = 32891;
	static public final int CHAOTICZONE1_Y1 = 32647;
	static public final int CHAOTICZONE1_Y2 = 32656;

	// 화전민 오크숲
	static public final int CHAOTICZONE2_X1 = 32664;
	static public final int CHAOTICZONE2_X2 = 32669;
	static public final int CHAOTICZONE2_Y1 = 32298;
	static public final int CHAOTICZONE2_Y2 = 32308;

	// 켄트숲
	static public final int LAWFULLZONE1_X1 = 33117;
	static public final int LAWFULLZONE1_X2 = 33128;
	static public final int LAWFULLZONE1_Y1 = 32931;
	static public final int LAWFULLZONE1_Y2 = 32942;

	// 요정숲
	static public final int LAWFULLZONE2_X1 = 33136;
	static public final int LAWFULLZONE2_X2 = 33146;
	static public final int LAWFULLZONE2_Y1 = 32236;
	static public final int LAWFULLZONE2_Y2 = 32245;

	// 요정숲
	static public final int TREEX1 = 33050;
	static public final int TREEX2 = 33058;
	static public final int TREEY1 = 32333;
	static public final int TREEY2 = 32341;

	// 낚시터
	static public final int FISHZONEX1 = 32788;
	static public final int FISHZONEX2 = 32815;
	static public final int FISHZONEY1 = 32783;
	static public final int FISHZONEY2 = 32813;


	// 기란마을 중앙
	static public final int HOMEX1 = 33423;
	static public final int HOMEX2 = 33435;
	static public final int HOMEY1 = 32807;
	static public final int HOMEY2 = 32820;

	// 퀘스트 종류
	static public final String QUEST_ZERO_ROYAL = "request cloak of red";
	static public final String QUEST_ROYAL_LV15 = "request spellbook112";
	static public final String QUEST_ROYAL_LV30 = "quest 13 aria2";
	static public final String QUEST_ROYAL_LV45 = "quest 15 masha2";
	static public final String QUEST_KNIGHT_LV15 = "request hood of knight";
	static public final String QUEST_KNIGHT_LV30 = "quest 14 gunterkE2";
	static public final String QUEST_ELF_LV15 = "quest elf 15";
	static public final String QUEST_ELF_LV30 = "quest 12 motherEE2";
	static public final String QUEST_WIZARD_LV15 = "quest wizard 15";
	static public final String QUEST_WIZARD_LV30 = "quest wizard 30";
	static public final String QUEST_TALKINGSCROLL = "quest talking scroll";
	static public final String QUEST_NOVICE = "quest novice";
	static public final String QUEST_LYRA = "quest lyra";
	static public final String QUEST_TIO = "quest amulet of valley";
	static public final String QUEST_RUBA = "amulet of island";
	static public final String QUEST_SP = "sp ";
	static public final String QUEST_OILSKINMANT = "dunham";

	// 공성전 이벤트 종류
	static public final int KINGDOM_WARSTATUS_START = 0; // 시작
	static public final int KINGDOM_WARSTATUS_STOP = 1; // 종료
	static public final int KINGDOM_WARSTATUS_PLAY = 2; // 진행중
	static public final int KINGDOM_WARSTATUS_3 = 3; // 주도권
	static public final int KINGDOM_WARSTATUS_4 = 4; // 차지

	// 속성 계열 정보
	static public final int ELEMENT_NONE = 0; // 공통
	static public final int ELEMENT_EARTH = 1; // 땅
	static public final int ELEMENT_FIRE = 2; // 불
	static public final int ELEMENT_WIND = 3; // 바람
	static public final int ELEMENT_WATER = 4; // 물
	static public final int ELEMENT_LASER = 5; // 레이저
	static public final int ELEMENT_POISON = 6; // 독

	// 요숲 채집npc쪽 재채집이 가능한 주기적인 시간 설정.
	static public int elf_gatherup_time;

	// 공성전 사용 여부
	static public boolean is_kingdom_war;
	// 공성전 진행 주기 일 단위
	static public int kingdom_war_day;
	static public List<Integer> giran_kingdom_war_day_list = new ArrayList<Integer>();
	// 공성전 진행시간
	static public int kingdom_war_time;
	// 공성중 공성존에서 사용자가 죽엇을때 경험치 떨굴지 여부.
	static public boolean kingdom_player_dead_expdown;
	// 공성중 공성존에서 사용자가 죽엇을때 아이템을 드랍할지 여부.
	static public boolean kingdom_player_dead_itemdrop;
	// 공성전 시작 및 종료후 면류관 아이템 처리를 할지 여부.
	static public boolean kingdom_crown;
	// 면류관 지급시 축여부
	static public int kingdom_crown_bless;
	// 면류관 지급시 인챈트
	static public int kingdom_crown_enchant;
	// 공성전 중 전쟁선포된 혈맹원들끼리 pvp 시 카오처리를 할지 여부.
	static public boolean kingdom_pvp_pk;
	//
	static public boolean kingdom_war_revival;
	//
	static public boolean kingdom_war_callclan;
	//
	static public double kingdom_item_count_rate;
	//
	static public int kingdom_soldier_price;

	// npc 대화요청된후 잠깐 휴식되는 시간값.
	static public int npc_talk_stay_time;
	// 암닭
	static public int eggs_spawn_time;
	static public int eggs_min_count;
	static public int eggs_max_count;
	// 요정숲 정령의돌 최대스폰 갯수.
	static public int elvenforest_elementalstone_spawn_count;
	// 요정숲 정령의돌 스폰 주기 시간값.
	static public int elvenforest_elementalstone_spawn_time;
	// 요정숲 정령의돌 스폰갯수 지정값.
	static public int elvenforest_elementalstone_min_count;
	static public int elvenforest_elementalstone_max_count;

	// 군주
	static public List<FirstSpawn> royal_spawn = new ArrayList<FirstSpawn>();
	static public int royal_male_gfx;
	static public int royal_female_gfx;
	static public int royal_hp;
	static public int royal_mp;
	static public int royal_max_hp;
	static public int royal_max_mp;
	static public List<FirstSpell> royal_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> royal_first_inventory = new ArrayList<FirstInventory>();
	static public int royal_stat_str;
	static public int royal_stat_con;
	static public int royal_stat_dex;
	static public int royal_stat_wis;
	static public int royal_stat_cha;
	static public int royal_stat_int;
	static public int royal_stat_dice;

	// 기사
	static public List<FirstSpawn> knight_spawn = new ArrayList<FirstSpawn>();
	static public int knight_male_gfx;
	static public int knight_female_gfx;
	static public int knight_hp;
	static public int knight_mp;
	static public int knight_max_hp;
	static public int knight_max_mp;
	static public List<FirstSpell> knight_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> knight_first_inventory = new ArrayList<FirstInventory>();
	static public int knight_stat_str;
	static public int knight_stat_con;
	static public int knight_stat_dex;
	static public int knight_stat_wis;
	static public int knight_stat_cha;
	static public int knight_stat_int;
	static public int knight_stat_dice;

	// 요정
	static public List<FirstSpawn> elf_spawn = new ArrayList<FirstSpawn>();
	static public int elf_male_gfx;
	static public int elf_female_gfx;
	static public int elf_hp;
	static public int elf_mp;
	static public int elf_max_hp;
	static public int elf_max_mp;
	static public List<FirstSpell> elf_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> elf_first_inventory = new ArrayList<FirstInventory>();
	static public int elf_stat_str;
	static public int elf_stat_con;
	static public int elf_stat_dex;
	static public int elf_stat_wis;
	static public int elf_stat_cha;
	static public int elf_stat_int;
	static public int elf_stat_dice;

	// 마법사 정보
	static public List<FirstSpawn> wizard_spawn = new ArrayList<FirstSpawn>();
	static public int wizard_male_gfx;
	static public int wizard_female_gfx;
	static public int wizard_hp;
	static public int wizard_mp;
	static public int wizard_max_hp;
	static public int wizard_max_mp;
	static public List<FirstSpell> wizard_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> wizard_first_inventory = new ArrayList<FirstInventory>();
	static public int wizard_stat_str;
	static public int wizard_stat_con;
	static public int wizard_stat_dex;
	static public int wizard_stat_wis;
	static public int wizard_stat_cha;
	static public int wizard_stat_int;
	static public int wizard_stat_dice;

	// 다크엘프 정보
	static public List<FirstSpawn> darkelf_spawn = new ArrayList<FirstSpawn>();
	static public int darkelf_male_gfx;
	static public int darkelf_female_gfx;
	static public int darkelf_hp;
	static public int darkelf_mp;
	static public int darkelf_max_hp;
	static public int darkelf_max_mp;
	static public List<FirstSpell> darkelf_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> darkelf_first_inventory = new ArrayList<FirstInventory>();

	// 용기사 정보
	static public List<FirstSpawn> dragonknight_spawn = new ArrayList<FirstSpawn>();
	static public int dragonknight_male_gfx;
	static public int dragonknight_female_gfx;
	static public int dragonknight_hp;
	static public int dragonknight_mp;
	static public int dragonknight_max_hp;
	static public int dragonknight_max_mp;
	static public List<FirstSpell> dragonknight_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> dragonknight_first_inventory = new ArrayList<FirstInventory>();

	// 환술사 정보
	static public List<FirstSpawn> blackwizard_spawn = new ArrayList<FirstSpawn>();
	static public int blackwizard_male_gfx;
	static public int blackwizard_female_gfx;
	static public int blackwizard_hp;
	static public int blackwizard_mp;
	static public int blackwizard_max_hp;
	static public int blackwizard_max_mp;
	static public List<FirstSpell> blackwizard_first_spell = new ArrayList<FirstSpell>();
	static public List<FirstInventory> blackwizard_first_inventory = new ArrayList<FirstInventory>();

	// 아이템 제거 막대로 제거 불가능한 아이템
	static public List<String> no_remove_item = new ArrayList<String>();
	
	// 뽑기 아이템 설정
	static public List<String> set_item = new ArrayList<String>();
	static public List<Integer> set_item_count = new ArrayList<Integer>();
	static public List<Integer> set_item_p = new ArrayList<Integer>();
	static public String set_check_itemname;
	static public int set_check_count;
	
	
	// 인벤토리 최대 갯수
	static public int inventory_max;

	// 인벤토리에 아이템 무게게이지 최대값
	static public int inventory_weight_max;

	// 최대 렙업값
	static public int level_max;

	// 파티원 최대수
	static public int party_max;

	// 창고이용 레벨
	static public int warehouse_level;
	// 창고 아이템 찾을때 비용
	static public int warehouse_price;
	// 창고에 등록가능한 최대값
	static public int warehouse_max;
	// 창고 아이템 찾을때 비용 - 요정숲
	static public int warehouse_price_elf;

	// 펫 찾을때 비용
	static public int warehouse_pet_price;
	// 펫 사용자에게 데미지 가할때 1/3으로 들어가게 할지 여부.
	static public boolean pet_damage_to_player;
	// 펫 길들일 수 있게 할지 여부.
	static public boolean pet_tame_is = true;

	// 순수스탯 최대값
	static public int stat_str;
	static public int stat_dex;
	static public int stat_con;
	static public int stat_int;
	static public int stat_wis;
	static public int stat_cha;

	// 여관방 최대갯수
	static public int inn_max;
	// 여관방 최대접근 인원수
	static public int inn_in_max;
	// 여관방 대여 비용
	static public int inn_price;
	// 여관 대여 시간
	static public int inn_time;
	// 여관 홀 최대갯수
	static public int inn_hall_max;
	// 여관 홀 최대접근 인원수
	static public int inn_hall_in_max;
	// 여관 홀 대여 비용
	static public int inn_hall_price;

	// 게시판 글 작성 가격.
	static public int board_write_price;
	// 게시판 글 작성 최소 레벨.
	static public int board_write_min_level;
	// 랭킹 게시판 업데이트 딜레이.
	static public int rank_update_delay;
	// 랭킹이 적용될 최소 레벨
	static public int rank_min_level;
	// 랭킹 순위에 따른 등급
	static public int rank_class_1;
	static public int rank_class_2;
	static public int rank_class_3;
	static public int rank_class_4;
	// 랭킹 변신 사용 여부
	static public boolean is_rank_poly;
	// 랭커 변신을 할수있는 순위
	static public int rank_poly_all;
	static public int rank_poly_class;
	// 강아지 레이스표 가격 설정
	static public int dog_race_price;
	// 슬라임 레이스표 가격 설정
	static public int slime_race_price;
	// 마법인형 레이스 사용 여부
	static public boolean is_magic_doll_race;
	// 마법인형 레이스 다음 경기까지 대기 시간(분)
	static public int magic_doll_race_delay;
	// 마법인형 레이스표 가격 설정
	static public int magic_doll_race_price;

	// 배율
	static public double rate_enchant = 1;
	static public double rate_drop = 1;
	static public double rate_exp = 1;
	static public double rate_lawful = 1;
	static public double rate_aden = 1;
	static public double rate_party = 1;
	static public double rate_exp_pet = 1;
	// 파워볼 배당률 (기본 1.9배)
	static public double powerball_payout_rate = 1.9;
	// 혈맹 추가 배율
	static public double clan_rate_drop = 1;
	static public double clan_rate_exp = 1;
	static public double clan_rate_aden = 1;
	// 성혈맹 추가 배율
	static public double kingdom_clan_rate_drop = 1;
	static public double kingdom_clan_rate_exp = 1;
	static public double kingdom_clan_rate_aden = 1;

	// 같은화면 안에 존재하는 파티원과 아덴 분배 여부
	static public boolean is_party_aden_share;

	// 레벨에 따른 경험치 감소
	static public double lv_40_exp_rate;
	static public double lv_41_exp_rate;
	static public double lv_42_exp_rate;
	static public double lv_43_exp_rate;
	static public double lv_44_exp_rate;
	static public double lv_45_exp_rate;
	static public double lv_46_exp_rate;
	static public double lv_47_exp_rate;
	static public double lv_48_exp_rate;
	static public double lv_49_exp_rate;
	static public double lv_50_exp_rate;
	static public double lv_51_exp_rate;
	static public double lv_52_exp_rate;
	static public double lv_53_exp_rate;
	static public double lv_54_exp_rate;
	static public double lv_55_exp_rate;
	static public double lv_56_exp_rate;
	static public double lv_57_exp_rate;
	static public double lv_58_exp_rate;
	static public double lv_59_exp_rate;
	static public double lv_60_exp_rate;
	static public double lv_61_exp_rate;
	static public double lv_62_exp_rate;
	static public double lv_63_exp_rate;
	static public double lv_64_exp_rate;
	static public double lv_65_exp_rate;
	static public double lv_66_exp_rate;
	static public double lv_67_exp_rate;
	static public double lv_68_exp_rate;
	static public double lv_69_exp_rate;
	static public double lv_70_exp_rate;
	static public double lv_71_exp_rate;
	static public double lv_72_exp_rate;
	static public double lv_73_exp_rate;
	static public double lv_74_exp_rate;
	static public double lv_75_exp_rate;
	static public double lv_76_exp_rate;
	static public double lv_77_exp_rate;
	static public double lv_78_exp_rate;
	static public double lv_79_exp_rate;
	static public double lv_80_exp_rate;
	static public double lv_81_exp_rate;
	static public double lv_82_exp_rate;
	static public double lv_83_exp_rate;
	static public double lv_84_exp_rate;
	static public double lv_85_exp_rate;
	static public double lv_86_exp_rate;
	static public double lv_87_exp_rate;
	static public double lv_88_exp_rate;
	static public double lv_89_exp_rate;
	static public double lv_90_exp_rate;

	// 보스 추가 아데나
	static public int class_1_boss_aden_min;
	static public int class_1_boss_aden_max;
	static public int class_2_boss_aden_min;
	static public int class_2_boss_aden_max;
	static public int class_3_boss_aden_min;
	static public int class_3_boss_aden_max;
	static public int class_4_boss_aden_min;
	static public int class_4_boss_aden_max;
	// 패널티 줄 레벨
	static public int penalty_level;
	// 패널티 경험치 배율
	static public double penalty_exp;
	// 경험치 지급단 경험치
	static public int exp_marble_min;
	static public int exp_marble_max;
	// 버프 명령어 공짜 사용 레벨
	static public int buff_max_level;
	// 버프 명령어 사용 레벨
	static public long buff_aden;
	// 오픈 대기 임시 저장 변수
	static public boolean open_wait;

	// 채팅 레벨설정
	static public int chatting_level_global;
	static public int chatting_level_normal;
	static public int chatting_level_whisper;

	// 전체 채팅 매크로 딜레이(초)
	static public int chatting_global_macro_delay;

	// 무혈, 신규 혈맹을 제외하고 같은 혈맹이 아닐경우 채팅 보일지 여부
	static public boolean is_chatting_clan;

	// pvp 설정
	static public boolean nonpvp;

	// 계정 자동생성 여부.
	static public boolean account_auto_create;
	// ip 당 소유가능한 계정 값.
	static public int account_ip_count;
	// 아이피당 케릭터를 몇개까지 허용할지 여부.
	static public int ip_character_count;
	// 아이피당 동시에 몇캐릭까지 접속 허용할지 여부.
	static public int ip_in_game_count;
	// 정액제 활성화 여부.
	static public boolean flat_rate;
	// 신규생성 계정에 대한 정액시간값. 분단위
	static public int flat_rate_price;

	// 상점에 세금을 추가할지 여부
	static public boolean add_tax;
	// 성을 차지한 혈맹에게 지정한 보상을 줄지 여부
	static public boolean kingdom_war_win_gift;
	// 성을 차지한 혈맹에게 보상할 아이템 리스트 (bundle 아이템으로 보상)
	static public List<String> kingdom_war_win_item_list = new ArrayList<String>();
	// 최소 몇인 이상 접속중인 혈맹원이 있을경우 면류관 먹을수 있는지 여부
	static public int crown_clan_min_people;
	// 공성 지정 성 번호
	static public int KINGDOM;
	// 기본 세율 값
	static public int min_tax;
	static public int max_tax;

	// /누구 명령어 구성표
	static public String object_who;

	// 드랍된 아이템 유지시간값. 해당시간이 오버되면 제거됨.
	static public int world_item_delay;

	// 오토루팅 활성화 여부.
	static public boolean auto_pickup;
	// 오토루팅 아데나 활성화 여부.
	static public boolean auto_pickup_aden;
	// 오토루팅 퍼센트 범위.
	static public int auto_pickup_percent;
	// 자동저장 시간. 분단위
	static public int auto_save_time;

	// 덱방 활성화 여부
	static public boolean is_dex_ac;

	// 죽엇을때 아이템 떨굴지 여부.
	static public boolean player_dead_itemdrop;
	// 죽엇을때 경험치 떨굴지 여부.
	static public boolean player_dead_expdown;
	// 죽엇을때 경험치를 가격자들에게 줄지 여부.
	static public boolean player_dead_exp_gift;
	//
	static public int player_dead_expdown_level;
	static public double player_dead_expdown_rate;
	static public double player_lost_exp_rate;
	static public int player_lost_exp_aden_rate;
	static public int player_dead_itemdrop_level;

	static public int BOW_ATTACK_LOCATIONRANGE = 8; // 활 공격 범위
	static public int SEARCH_LOCATIONRANGE = 14; // 주변셀 검색 범위
	static public int SEARCH_MONSTER_TARGET_LOCATION = 20; // 몬스터가 타겟을 쫒아가는 검색
															// 범위
	static public int SEARCH_WORLD_LOCATION = 40; // 전체 객체 검색을 시도할 범위
	static public int SEARCH_WORLD_LOCATION_SHOP = 60; // 로봇이 주위에 상점 검색을 시도할 범위

	// 배고품게이지 최대값
	static public final int MAX_FOOD = 225;
	// 사용자 케릭 죽엇을때 세팅할 배고품게이지 값
	public final static int MIN_FOOD = 40;

	// 혈맹처리 에 참고 정보
	static public final int CLAN_MAKE_LEV = 5;
	static public final int CLAN_NAME_MIN_SIZE = 1;
	static public final int CLAN_NAME_MAX_SIZE = 9;

	// 최대 ac
	static public final int MAX_AC = 190; // -150

	// door 오픈 유지값.
	static public int door_open_delay = 4;

	// 몬스터 hp 바 보게할지 여부.
	static public boolean monster_interface_hpbar;
	// npc hp bar 보이게할지 여부.
	static public boolean npc_interface_hpbar;

	// 무기 손상도 최대치값.
	static public int item_durability_max;
	// 아이템 착용 처리 변수 (old(false), new(true))
	static public boolean item_equipped_type;
	// 축복받은 악세서리들에 아이템은 인첸트가 가능하도록할지 여부.
	static public boolean item_accessory_bless_enchant;
	// 축복받은 변신 주문서일경우 레벨제한을 해제할지 여부.
	static public boolean item_polymorph_bless;
	// 엘릭서 복용 최대 갯수.
	static public int item_elixir_max = 5;
	// 엘릭서 사용 최소 레벨
	static public int elixir_min_level;

	// 인첸 최대값 설정.
	static public int item_enchant_armor_max = 0;
	static public int item_enchant_weapon_max = 0;
	static public int item_enchant_accessory_max = 0;

	// 쓰레드 설정
	// 이벤트 쓰레드 갯수.
	static public int thread_event = 6;
	static public int thread_ai = 4;

	// 클라 설정
	// 클라이언트 핑체크 시간값
	static public int client_ping_time = 70;

	// 공지사항 표현 주기.
	static public int notice_delay;
	// 공성전 날짜 알림 여부
	static public boolean is_kingdom_war_notice;
	// 공성전 표현 주기.
	static public int kingdom_war_notice_delay;
	/** 출석체크 NPC·접속 시간 자동 알림 (false면 비활성) */
	static public boolean attendance_check_enabled = false;

	// 이벤트 들
	static public boolean event_poly; // 변신 이벤트
	static public boolean event_rank_poly; // 변신 이벤트
	static public boolean event_buff; // 버프 자동지급 이벤트
	static public boolean event_illusion; // 환상 이벤트
	static public boolean event_christmas; // 크리스마스 이벤트
	static public boolean event_halloween; // 할로윈 이벤트
	static public boolean event_lyra; // 라이라 토템 이벤트

	// 가속 아이템 프레임
	static public boolean bravery_potion_frame;
	static public boolean elven_wafer_frame;
	static public boolean holywalk_frame;

	// 프리미엄 아이템 자동지급
	static public boolean world_premium_item_is;
	static public String world_premium_item;
	static public int world_premium_item_min;
	static public int world_premium_item_max;
	static public int world_premium_item_delay;

	// 사용자들이 월드에 접속시 접속했다는 메세지를 전체 유저에게 알릴지 여부.
	static public boolean world_message_join;

	// mr 최대치값 설정.
	static public int max_mr;

	// 메모리 재사용에 사용될 객체 적재 최대 갯수 값.
	static public int pool_max = 50000;
	// 메모리 재사용 기능 사용 여부.
	static public boolean pool_basePacket = false;
	static public boolean pool_astar = false;
	static public boolean pool_eventthread = false;
	static public boolean pool_itemInstance = false;
	static public boolean pool_client = false;

	// 콜롯세움 설정 값들
	static public boolean colosseum_talkingisland = false;
	static public boolean colosseum_silverknighttown = false;
	static public boolean colosseum_gludin = false;
	static public boolean colosseum_windawood = false;
	static public boolean colosseum_kent = false;
	static public boolean colosseum_giran = false;
	// 접속자수 표현시 사용되는 변수.
	static public double world_player_count = 1.0d;
	// 플레이어 수 임의 지정 값
	static public int world_player_count_init = 0;

	//
	static public boolean party_autopickup_item_print = false;
	static public boolean party_pickup_item_print = false;
	static public int party_exp_level_range = 0;
	static public boolean party_autopickup_item_print_on_screen = false;

	// 전투멘트
	static public String war_ment = null;

	// 전투멘트
	static public String done_ment1 = null;
	static public String done_ment2 = null;
	static public String done_ment3 = null;
	static public String done_ment4 = null;
	static public String done_ment5 = null;
	static public String done_ment6 = null;
	static public String done_ment7 = null;

	// 현상수배 변수.
	static public boolean wanted_clan = false;
	static public String wanted_name = "[수배자]";
	static public int wanted_level_min = 0;
	static public int wanted_level_max = 0;
	static public int wanted_price_min = 0;
	static public int wanted_price_max = 0;
	static public int wanted_price_min52 = 0;
	static public int wanted_price_max52 = 0;
	static public int wanted_price_min55 = 0;
	static public int wanted_price_max55 = 0;
	static public int wanted_price_min60 = 0;
	static public int wanted_price_max60 = 0;
	static public int wanted_price_min65 = 0;
	static public int wanted_price_max65 = 0;

	static public boolean user_command = true;

	// 보물찾기 상자 재생성 시간
	static public int boxspawn = 1;

	//힐링포션 복용시 메세지를 표현할 지 여부.
	static public boolean healingpotion_message = true;
	
	// 경매 남은시간 값. 3일로 설정됨.
	static public long auction_delay = 259200 * 1000;

	// pvp 변수.
	static public boolean pvp_print_message = false;

	// 속성강화제한
	static public int danlevel;
	// 속성 확률
	static public int dan1;
	static public int dan2;
	static public int dan3;
	static public int dan4;
	static public int dan5;

	// 스피드핵
	static public boolean speedhack = true;
	static public int speedhack_warning_count = 10;
	static public boolean speedhack_stun;
	static public int speed_bad_packet_count;
	static public int speed_good_packet_count;
	static public boolean is_attack_count_packet;
	static public int speed_hack_block_time;
	static public int speed_hack_message_count;
	static public double speed_check_walk_frame_rate;
	static public double speed_check_attack_frame_rate;
	static public int delay_hack_count;
	static public double delay_hack_frame_rate;
	static public int gost_hack_block_time;

	// 기본 객체 텔레포트시 사용될 이팩트 값.
	static public int object_teleport_effect = 169;
	// 허수아비 대미지 알림 여부
	static public boolean view_cracker_damage = false;
	// 허수아비 대미지 DPS 알림 여부
	static public boolean view_cracker_dps = false;
	// 허수아비 경험치 적용 최대 레벨
	static public int cracker_exp_max_level;
	//
	static public boolean clan_warehouse_message = false;
	//
	static public boolean character_delete = true;

	// gui
	static public boolean chatting_all_lock = false;
	static public boolean chatting_global_lock = false;

	//
	static public String rank_filter_names_query = null;

	// 죽어도 경험치를 잃어버리지 않는 맵
	static public int MAP_EXP_NOT_LOSE[] = { 89, 303, 509, 1400 };

	// 죽어도 아이템을 드랍하지 않는 맵
	static public int MAP_ITEM_NOT_DROP[] = { 89, 303, 509, 1400 };

	// 수중 맵 목록
	static public int MAP_AQUA[] = {};

	// 서먼객체가 이동 불가능한 맵. 또는 소환 불가능한 맵
	static public int SummonTeleportImpossibleMap[] = { 63, 65, 509, 807, 653, 654, 655, 656 };

	// 펫이 이동 불가능한 맵. (사용자가 텔레포트할때 해당 값을 확인해서 처리할지 여부 구분함.)
	static public int PetTeleportImpossibleMap[] = { 5, 6, 22, 63, 65, 70, 83, 84, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 201, 202, 203, 204, 209, 210, 211, 212, 213, 214, 215, 216, 221, 222, 223, 224, 225, 226,
			227, 228, 509, 653, 654, 655, 656 };

	// 마법인형 이동 또는 소환 불가능한 맵
	static public int MagicDollTeleportImpossibleMap[] = { 509, 807 };

	// 랜덤 텔레포트 가능한 맵
	static public int TeleportPossibleMap[] = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 24, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
			49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 68, 69, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 85, 86, 209, 210, 211, 212, 213, 214, 215, 216, 240, 241, 242, 243, 248, 249, 250, 280, 281, 282, 283, 284, 300, 301, 304, 400, 401, 410,
			420, 430, 440, 441, 445, 480, 522, 523, 524, 604, 653, 654, 655, 656, 666, 777, 780, 781, 783, 807, 808, 809, 810, 811, 812, 813, 814, 812, 1002, 1180, 1700, 5167 };
	static public int TeleportPossibleMapLength = TeleportPossibleMap.length;

	// 귀환 및 축순, 이반 불가능한 맵
	static public final int TeleportHomeImpossibilityMap[] = { 70, 89, 200, 303, 509, 1400 };
	static public final int TeleportHomeImpossibilityMapLength = TeleportHomeImpossibilityMap.length;

	// 각 성별 외성 내부 좌표값
	static public final int KINGDOMLOCATION[][] = { { 0, 0, 0, 0, 0, 0 }, { 33089, 33219, 32717, 32827, 4, KINGDOM_KENT }, { 32750, 32850, 32250, 32350, 4, KINGDOM_ORCISH },
			{ 32571, 32721, 33350, 33460, 4, KINGDOM_WINDAWOOD }, { 33559, 33686, 32615, 32755, 4, KINGDOM_GIRAN }, { 33458, 33583, 33315, 33490, 4, KINGDOM_HEINE }, { 32755, 32870, 32790, 32920, 66, KINGDOM_ABYSS },
			{ 34007, 34162, 33172, 33332, 4, KINGDOM_ADEN }, { 32888, 33070, 32839, 32953, 320, 0 }, };

	// 아지트 좌표값
	static public final int AGITLOCATION[][] = { { 33368, 33375, 32651, 32654 }, // 기란,
																					// 1번,
																					// 78평
			{ 33373, 33375, 32655, 32657 }, { 33381, 33387, 32653, 32656 }, // 기란,
																			// 2번,
																			// 45평
			{ 33392, 33404, 32650, 32657 }, // 기란, 3번, 120평
			{ 33427, 33430, 32656, 32662 }, // 기란, 4번, 45평
			{ 33442, 33445, 32665, 32672 }, // 기란, 5번, 78평
			{ 33439, 33441, 32665, 32667 }, { 33454, 33466, 32648, 32655 }, // 기란,
																			// 6번,
																			// 120평
			{ 33476, 33479, 32665, 32671 }, // 기란, 7번, 45평
			{ 33474, 33477, 32678, 32685 }, // 기란, 8번, 78평
			{ 33471, 33473, 32678, 32680 }, { 33453, 33460, 32694, 32697 }, // 기란,
																			// 9번,
																			// 78평
			{ 33458, 33460, 32698, 32700 }, { 33421, 33433, 32685, 32692 }, // 기란,
																			// 10번,
																			// 120평
			{ 33412, 33415, 32674, 32681 }, // 기란, 11번, 78평
			{ 33409, 33411, 32674, 32676 }, { 33414, 33421, 32703, 32706 }, // 기란,
																			// 12번,
																			// 78평
			{ 33419, 33421, 32707, 32709 }, { 33372, 33384, 32692, 32699 }, // 기란,
																			// 13번,
																			// 120평
			{ 33362, 33365, 32681, 32687 }, // 기란, 14번, 45평
			{ 33363, 33366, 32669, 32676 }, // 기란, 15번, 78평
			{ 33360, 33362, 32669, 32671 }, { 33344, 33347, 32660, 32667 }, // 기란,
																			// 16번,
																			// 78평
			{ 33341, 33343, 32660, 32662 }, { 33345, 33348, 32672, 32678 }, // 기란,
																			// 17번,
																			// 45평
			{ 33338, 33350, 32704, 32711 }, // 기란, 18번, 120평
			{ 33349, 33356, 32728, 32731 }, // 기란, 19번, 78평
			{ 33354, 33356, 32732, 32734 }, { 33369, 33372, 32713, 32720 }, // 기란,
																			// 20번,
																			// 78평
			{ 33366, 33368, 32713, 32715 }, { 33380, 33383, 32712, 32718 }, // 기란,
																			// 21번,
																			// 45평
			{ 33401, 33413, 32733, 32740 }, // 기란, 22번, 120평
			{ 33427, 33430, 32717, 32724 }, // 기란, 23번, 78평
			{ 33424, 33426, 32717, 32719 }, { 33448, 33451, 32729, 32735 }, // 기란,
																			// 24번,
																			// 45평
			{ 33404, 33407, 32754, 32760 }, // 기란, 25번, 45평
			{ 33363, 33375, 32755, 32762 }, // 기란, 26번, 120평
			{ 33354, 33357, 32774, 32781 }, // 기란, 27번, 78평
			{ 33351, 33353, 32774, 32776 }, { 33355, 33361, 32787, 32790 }, // 기란,
																			// 28번,
																			// 45평
			{ 33366, 33373, 32786, 32789 }, // 기란, 29번, 78평
			{ 33371, 33373, 32790, 32792 }, { 33383, 33386, 32773, 32779 }, // 기란,
																			// 30번,
																			// 45평
			{ 33397, 33404, 32788, 32791 }, // 기란, 31번, 78평
			{ 33402, 33404, 32792, 32794 }, { 33479, 33486, 32788, 32791 }, // 기란,
																			// 32번,
																			// 78평
			{ 33484, 33486, 32792, 32794 }, { 33498, 33501, 32801, 32807 }, // 기란,
																			// 33번,
																			// 45평
			{ 33379, 33385, 32802, 32805 }, // 기란, 34번, 45평
			{ 33373, 33385, 32822, 32829 }, // 기란, 35번, 120평
			{ 33398, 33401, 32810, 32816 }, // 기란, 36번, 45평
			{ 33400, 33403, 32821, 32828 }, // 기란, 37번, 78평
			{ 33397, 33399, 32821, 32823 }, { 33431, 33438, 32838, 32841 }, // 기란,
																			// 38번,
																			// 78평
			{ 33436, 33438, 32842, 32844 }, { 33457, 33463, 32832, 32835 }, // 기란,
																			// 39번,
																			// 45평
			{ 33385, 33392, 32845, 32848 }, // 기란, 40번, 78평
			{ 33390, 33392, 32849, 32851 }, { 33402, 33405, 32589, 32866 }, // 기란,
																			// 41번,
																			// 78평
			{ 33399, 33401, 32859, 32861 }, { 33414, 33417, 32850, 32856 }, // 기란,
																			// 42번,
																			// 45평
			{ 33372, 33384, 32867, 32874 }, // 기란, 43번, 120평
			{ 33425, 33437, 32865, 32872 }, // 기란, 44번, 120평
			{ 33446, 33449, 32869, 32876 }, // 기란, 45번, 78평
			{ 33443, 33445, 32869, 32871 } };

	// 마법인형 리스트
	static public final String[][] magicDoll = {
			// 1단계 마법인형
			{ "마법인형: 돌 골렘", "마법인형: 늑대인간", "마법인형: 버그베어", "마법인형: 크러스트시안", "마법인형: 에티", "마법인형: 목각" },
			// 2단계 마법인형
			{ "마법인형: 서큐버스", "마법인형: 장로", "마법인형: 코카트리스", "마법인형: 눈사람", "마법인형: 인어", "마법인형: 라바 골렘" },
			// 3단계 마법인형
			{ "마법인형: 자이언트", "마법인형: 흑장로", "마법인형: 서큐버스 퀸", "마법인형: 드레이크", "마법인형: 킹 버그베어", "마법인형: 다이아몬드 골렘" },
			// 4단계 마법인형
			{ "마법인형: 리치", "마법인형: 사이클롭스", "마법인형: 나이트발드", "마법인형: 시어", "마법인형: 아이리스", "마법인형: 뱀파이어", "마법인형: 머미로드" },
			// 5단계 마법인형
			{ "마법인형: 데몬", "마법인형: 데스나이트", "마법인형: 바란카", "마법인형: 타락", "마법인형: 바포메트", "마법인형: 얼음여왕", "마법인형: 커츠" },
			// 5단계 특수 마법인형
			{ "마법인형: 안타라스", "마법인형: 파푸리온", "마법인형: 린드비오르", "마법인형: 발라카스" } };

	static public final String[][] polyCard = {
			// 일반
			{ "[일반]변신카드:버그베어", "[일반]변신카드:가스트", "[일반]변신카드:라이칸스로프", "[일반]변신카드:스파토이", "[일반]변신카드:구울", "[일반]변신카드:좀비", "[일반]변신카드:해골도끼병", "[일반]변신카드:해골궁수", "[일반]변신카드:해골창병", "[일반]변신카드:해골", "[일반]변신카드:네루가오크", "[일반]변신카드:두다-마라오크",
					"[일반]변신카드:아투바오크", "[일반]변신카드:로바오크", "[일반]변신카드:간디오크" },
			// 고급
//			{ "[고급]변신카드:오크스카우트", "[고급]변신카드:킹 버그베어", "[고급]변신카드:장로", "[고급]변신카드:트롤", "[고급]변신카드:오우거", "[고급]변신카드:에틴", "[고급]변신카드:코카트리스", "[고급]변신카드:그리폰", "[고급]변신카드:사이클롭스" },
			
			{ "[고급]변신카드:오크스카우트"},
			// 희귀
			{ "[희귀]변신카드:카스파", "[희귀]변신카드:발터자르", "[희귀]변신카드:세마", "[희귀]변신카드:메르키오르", "[희귀]변신카드:흑장로", "[희귀]변신카드:베레스", "[희귀]변신카드:바포메트", "[희귀]변신카드:불타는 궁수", "[희귀]변신카드:데몬", "[희귀]변신카드:다크 매지스터", "[희귀]변신카드:다크 나이트",
					"[희귀]변신카드:다크 레인저" },
			// 영웅
			{ "[영웅]변신카드:실버 레인저", "[영웅]변신카드:실버 매지스터", "[영웅]변신카드:실버 나이트", "[영웅]변신카드:소드 마스터", "[영웅]변신카드:위자드리 마스터", "[영웅]변신카드:애로우 마스터", "[영웅]변신카드:아크 나이트", "[영웅]변신카드:아크 위자드", "[영웅]변신카드:아크 스카우터", "[영웅]변신카드:다크엘프",
					"[영웅]변신카드:데스나이트" },
			// 전설
			{ "[전설]변신카드:팬텀 나이트", "[전설]변신카드:드래곤 슬레이어", "[전설]변신카드:게렝", "[전설]변신카드:가드리아", "[전설]변신카드:불패의 군터", "[전설]변신카드:경비병(활)대장", "[전설]변신카드:증오의 데스나이트", "[전설]변신카드:군단장 엑스터", "[전설]변신카드:지옥의 데스나이트", "[전설]변신카드:칠흑의 데스나이트",
					"[전설]변신카드:다크 하딘", "[전설]변신카드:암살의 아리아 울프", "[전설]변신카드:과거의 나이트발드", "[전설]변신카드:베리스", "[전설]변신카드:로데마이", "[전설]변신카드:화염의 데스나이트", "[전설]변신카드:국왕 에오딘", "[전설]변신카드:나이트 슬레이셔", "[전설]변신카드:1세대 드루가", "[전설]변신카드:다크스타 조우",
					"[전설]변신카드:달의 질리언", "[전설]변신카드:철의 아툰", "[전설]변신카드:암흑룡 조우", "[전설]변신카드:암흑룡 질리언", "[전설]변신카드:암흑룡 아툰" },
			// 신화
			{ "[신화]변신카드:신화 암흑기사", "[신화]변신카드:신화 신성검사", "[신화]변신카드:신화 뇌신", "[신화]변신카드:신화 사신", "[신화]변신카드:신화 변신" },
			// 유일
			{ "[유일]변신카드:지배자 기르타스" } };

	// 개인상점 군터의 인장 사용 여부
	static public boolean is_market_only_aden;
	// 개인상점 최대 등록 갯수
	static public int market_max_count;

	// 자동사냥 방지 사용 여부
	static public boolean is_auto_hunt_check;
	// 자동사냥 체크를 위한 몬스터 킬 수
	static public int auto_hunt_monster_kill_count;
	// 자동사냥 문자 입력안할시 마법 사용여부(true: 유저에게도 마법 사용 불가 / false: 마법은 체크안함)
	static public boolean is_auto_hunt_check_skill;
	// 인증번호 받을 시 몇초 이내에 답변을 해야 공격가능
	static public int auto_hunt_answer_time;

	// 현금거래 게시판 등록시 판매자 캐릭터명 노출 여부
	static public boolean is_pc_trade_sell_name;
	// 판매 등록 최소 레벨
	static public int pc_trade_sell_level;
	// 구매 신청 최소 레벨
	static public int pc_trade_buy_level;
	// 현금 거래 게시판 아데나만 등록 가능 여부.
	static public boolean is_pc_trade_sell_only_aden;
	// 아데나 등록시 고정시세 적용 여부.
	static public boolean is_aden_static_price;
	// 고정 시세 적용시 아데나 단위
	static public long aden_static_unit;
	// 아데나 단위당 현금 시세
	static public int aden_static_price;
	// 거래 완료시 해당 글 삭제 여부
	static public boolean is_pc_trade_success_delete;
	// 계정의 판매등록 최대 갯수
	static public int pc_trade_sale_max_count;
	// 계정 구매신청 최대 갯수
	static public int pc_trade_buy_max_count;
	// 구매신청 후 입금완료까지 대기시간
	static public int pc_trade_buy_deposit_delay;

	// 버프 관리사 리스트
	static public List<BuffNpc> buffNpcList = new ArrayList<BuffNpc>();

	// 켄트성 소모되지 않는 아이템 적용 여부
	static public boolean is_kent_kingdom_war_no_remove;
	// 켄트성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_kent = new ArrayList<String>();
	// 오크성 소모되지 않는 아이템 적용 여부
	static public boolean is_orcish_kingdom_war_no_remove;
	// 오크성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_orcish = new ArrayList<String>();
	// 윈다우드성 소모되지 않는 아이템 적용 여부
	static public boolean is_windawood_kingdom_war_no_remove;
	// 윈다우드성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_windawood = new ArrayList<String>();
	// 기란성 소모되지 않는 아이템 적용 여부
	static public boolean is_giran_kingdom_war_no_remove;
	// 기란성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_giran = new ArrayList<String>();
	// 하이네성 소모되지 않는 아이템 적용 여부
	static public boolean is_heine_kingdom_war_no_remove;
	// 하이네성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_heine = new ArrayList<String>();
	// 지저성 소모되지 않는 아이템 적용 여부
	static public boolean is_abyss_kingdom_war_no_remove;
	// 지저성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_abyss = new ArrayList<String>();
	// 아덴성 소모되지 않는 아이템 적용 여부
	static public boolean is_aden_kingdom_war_no_remove;
	// 아덴성 소모되지 않는 아이템 리스트
	static public List<String> kingdom_war_no_remove_item_aden = new ArrayList<String>();
	// 무인케릭 사용유무
	static public boolean robot_auto_pc;
	// 기란감옥 초기화 주문서 사용 횟수
	static public int giran_dungeon_scroll_count;
	// 기란감옥 초기화 주문서 초기화 시간
	static public int giran_dungeon_reset_hour;

	// 기란감옥 초기화 주문서 사용 횟수
	static public int auto_scroll_count;
	// 기란감옥 초기화 주문서 초기화 시간
	static public int auto_dungeon_reset_hour;

	// 기란감옥 초기화 주문서 사용횟수 초기화 알림 메시지 알림여부
	static public boolean is_giran_dungeon_scroll_message;
	// 쌍드레이크
	static public int Double_Drake  = 1;
	// 파우스트 악령 변신 확율
	static public int giran_dungeon  = 1;
	
	// 바포메트 시스템 적용 여부.
	static public boolean is_batpomet_system;
	// 경험치 저장 구슬 / 경험치 구슬 사용 허용 (lineage.conf is_exp_marble_system, 기본 false).
	static public boolean is_exp_marble_system;
	// 화둥 랜덤텔레포트 여부.
	static public boolean is_fire_nest_teleport;
	// 오렌 랜덤텔레포트 여부.
	static public boolean is_oren_teleport;
	// 하이네 랜덤텔레포트 여부.
	static public boolean is_heine_teleport;
	// 아덴 랜덤텔레포트 여부.
	static public boolean is_aden_teleport;
	// 웰던 랜덤텔레포트 여부.
	static public boolean is_welldone_teleport;
	// 기란 상아탑 일대(map4) 랜덤/범위 텔 허용 여부.
	static public boolean is_ivory_tower_teleport;
	// 성 외성·기란 아지트·잊혀진 섬 텔 허용 여부는 TeleportPolicy (IDE/분석기 호환용 소형 클래스)

	// 잠수용 허수아비 사용여부
	static public boolean is_rest_cracker;
	// 잠수용 허수아비 사용 레벨
	static public int rest_cracker_level;
	// 잠수용 허수아비 재사용 딜레이(초)
	static public int rest_cracker_delay;
	// 잠수용 허수아비
	static public String rest_cracker_name;
	// 잠수용 활
	static public String rest_cracker_weapon;
	// 잠수용 화살
	static public String rest_cracker_arrow;
	// 잠수용 허수아비 체력
	static public int rest_cracker_hp;
	// 잠수용 허수아비 경험치 최소값
	static public int rest_cracker_exp_min;
	// 잠수용 허수아비 경험치 최대값
	static public int rest_cracker_exp_max;
	// 잠수용 허수아비 아데나 최소값
	static public int rest_cracker_aden_min;
	// 잠수용 허수아비 아데나 최대값
	static public int rest_cracker_aden_max;
	// 잠수용 활 착용시 헤이스트 여부
	static public boolean is_rest_cracker_haste;
	// 잠수용 활 착용시 용기 효과 여부
	static public boolean is_rest_cracker_bravery;
	// 잠수용 허수아비존 좌표
	static public int rest_cracker_x1 = 0;
	static public int rest_cracker_y1 = 0;
	static public int rest_cracker_x2 = 0;
	static public int rest_cracker_y2 = 0;
	// 잠수용 허수아비 맵번호
	static public int rest_cracker_map = 0;

	// 스팟 사용 여부
	static public boolean is_spot;
	// 스팟 타워 체력
	static public int spot_tower_hp;
	// 스팟 타워 스폰 위치 X좌표
	static public int spot_tower_x;
	// 스팟 타워 스폰 위치 Y좌표
	static public int spot_tower_y;
	// 스팟 타워 스폰 위치 맵번호
	static public int spot_tower_map;
	// 스팟 시작 시간(시간)
	static public int spot_tower_start_hour;
	// 스팟 시작 시간(분)
	static public int spot_tower_start_min;
	// 스팟 시작 진행시간(초)
	static public int spot_tower_time;
	// 스팟 승리 보상
	static public List<FirstInventory> spot_item = new ArrayList<FirstInventory>();

	// 투견
	static public boolean is_fight;
	static public String fight_aden = "아데나";
	static public int fight_ticket_price = 500;
	static public double fight_rate = 1.95;
	static public long fight_max_ticket = 1000;

	// 마안 이팩트
	static public boolean 마안이팩트여부;
	static public final int 수룡의마안_이팩트 = 7672;
	static public final int 풍룡의마안_이팩트 = 7673;
	static public final int 지룡의마안_이팩트 = 7671;
	static public final int 화룡의마안_이팩트 = 7674;
	static public final int 탄생의마안_이팩트 = 7675;
	static public final int 형상의마안_이팩트 = 7676;
	static public final int 생명의마안_이팩트 = 7678;

	static public double 탄생의마안_확률;
	static public String 탄생의마안_제작_아덴;
	static public long 탄생의마안_제작_아덴_수량;

	static public double 형상의마안_확률;
	static public String 형상의마안_제작_아덴;
	static public long 형상의마안_제작_아덴_수량;

	static public double 생명의마안_확률;
	static public String 생명의마안_제작_아덴;
	static public long 생명의마안_제작_아덴_수량;

	// 불멸의 가호 시스템 사용 여부
	static public boolean is_immortality = false;
	// 불멸의 가호 시스템 PvP시에만 적용 여부
	static public boolean is_immortality_pvp;
	// 불멸의 가호 아이템 이름
	static public String immortality_item_name = "";
	// 불멸의 가호 소지시 아덴 획득 증가량
	static public double immortality_aden_percent = 0;
	// 불멸의 가호 소지시 아이템 드랍 확률 증가량
	static public double immortality_item_percent = 0;
	// 불멸의 가호 소지 캐릭터 죽일시 타버린 불멸의 가호 지급 여부
	static public boolean is_immortality_kill_item = false;
	// 타버린 불멸의 가호 아이템 이름
	static public String immortality_kill_item_name = "";
	// 타버린 불멸의 가호 드랍 여부
	static public boolean is_immortality_kill_item_drop;
	// 몬스터에게 사망시 타버린 불멸의 가호 드랍 여부
	static public boolean is_immortality_kill_item_drop_monster;

	// 펫 배고픔 사용 여부(true: 사용 / false: 사용안함)
	static public boolean is_pet_hungry = true;

	// 나비켓에 skill테이블의 복수 쿨타임 마법아이디(uid)번호
	static public int revenge_uid;
	// 복수 시스템 사용 여부
	static public boolean is_revenge;
	// 복수 사용 레벨
	static public int revenge_level;
	// 같은 혈맹원에 복수 사용 가능 여부
	static public boolean is_clan_revenge;
	// 복수 사용 불가능한 맵
	static public List<Integer> revenge_not_map_list = new ArrayList<Integer>();
	// 복수 사용시 필요한 물품
	static public String revenge_need_item;
	static public List<FirstInventory> revenge_need_item_list = new ArrayList<FirstInventory>();
	// 복수 명령어 쿨타임
	static public int revenge_delay;

	// 레이스 입장
	static public List<TeamBattleTime> bug_list = new ArrayList<TeamBattleTime>();
	static public String bug_time = "";
	static public int bug_play_time = 600;

	// 테베라스 입장 레벨
	static public int tebe_level = 1;
	// 테베라스 수배자만 입장가능 여부
	static public boolean tebe_wanted = false;
	// 테베라스 혈맹가입자만 입장가능 여부
	static public boolean tebe_clan = false;
	// 테베라스 입장 시간
	static public List<TeamBattleTime> tebe_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String tebe_dungeon_time = "";

	static public List<TeamBattleTime> tebe_dungeon_time_list2 = new ArrayList<TeamBattleTime>();
	static public String tebe_dungeon_time2 = "";
	// 테베라스 진행 시간(초)
	static public int tebe_play_time = 600;

	// 캐쉬사냥터 입장 레벨
	static public int cash_level = 1;
	// 캐쉬사냥터 수배자만 입장가능 여부
	static public boolean cash_wanted1 = false;
	// 캐쉬사냥터 혈맹가입자만 입장가능 여부
	static public boolean cash_clan = false;
	// 캐쉬사냥터 입장 시간
	static public List<TeamBattleTime> cash_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String cash_dungeon_time = "";
	// 캐쉬사냥터 진행 시간(초)
	static public int cash_play_time = 600;

	// 마족신전 입장 레벨
	static public int dete_level = 1;
	// 마족신전 수배자만 입장가능 여부
	static public boolean dete_wanted = false;
	// 마족신전 혈맹가입자만 입장가능 여부
	static public boolean dete_clan = false;
	// 마족신전 입장 평일시간
	static public List<TeamBattleTime> dete_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String dete_dungeon_time = "";
	// 마족신전 입장 주말시간
	static public List<TeamBattleTime> dete_dungeon_time_list2 = new ArrayList<TeamBattleTime>();
	static public String dete_dungeon_time2 = "";
	// 마족신전 진행 시간(초)
	static public int dete_play_time = 600;

	static public int hell_level = 1;
	// 지옥 수배자만 입장가능 여부
	static public boolean hell_wanted = false;
	// 지옥 혈맹가입자만 입장가능 여부
	static public boolean hell_clan = false;
	// 지옥 입장 시간
	static public List<TeamBattleTime> hell_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String hell_dungeon_time = "";

	// 지옥 입장 시간
	static public List<TeamBattleTime> hell_dungeon_time_list2 = new ArrayList<TeamBattleTime>();
	static public String hell_dungeon_time2 = "";
	// 지옥 진행 시간(초)
	static public int hell_play_time = 600;

	// 보물찾기
	static public int Treasuress_level = 1;
	static public List<TeamBattleTime> Treasuress_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String Treasuress_dungeon_time = "";
	static public int Treasuress_play_time = 600;

	// 펭귄사냥터
	static public int phunt_level = 1;
	// 지옥 수배자만 입장가능 여부
	static public boolean phunt_wanted = false;
	// 지옥 혈맹가입자만 입장가능 여부
	static public boolean phunt_clan = false;
	// 지옥 입장 시간
	static public List<TeamBattleTime> phunt_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String phunt_dungeon_time = "";
	// 지옥 진행 시간(초)
	static public int phunt_play_time = 600;

	// 월드보스 입장 레벨
	static public int world_level = 1;
	// 월드보스 수배자만 입장가능 여부
	static public boolean world_wanted = false;
	// 월드보스 혈맹가입자만 입장가능 여부
	static public boolean world_clan = false;
	// 월드보스 입장 시간
	static public List<TeamBattleTime> world_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String world_dungeon_time = "";
	// 월드보스 진행 시간(초)
	static public int world_play_time = 600;

	// 얼던 입장 레벨
	static public int ice_level = 1;
	// 얼던 수배자만 입장가능 여부
	static public boolean ice_wanted = false;
	// 얼던 혈맹가입자만 입장가능 여부
	static public boolean ice_clan = false;
	// 얼던 입장 시간
	static public List<TeamBattleTime> ice_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String ice_dungeon_time = "";

	static public List<TeamBattleTime> ice_dungeon_time_list2 = new ArrayList<TeamBattleTime>();
	static public String ice_dungeon_time2 = "";
	// 얼던 진행 시간(초)
	static public int ice_play_time = 600;

	// 방랑상인
	static public List<TeamBattleTime> wdr_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String wdr_dungeon_time = "";
	static public int wdr_play_time = 600;

	// 지하수로 입장 레벨
	static public int wg_level = 1;
	// 지하수로 수배자만 입장가능 여부
	static public boolean wg_wanted = false;
	// 지하수로 혈맹가입자만 입장가능 여부
	static public boolean wg_clan = false;
	// 지하수로 입장 시간
	static public List<TeamBattleTime> wg_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String wg_dungeon_time = "";

	static public List<TeamBattleTime> wg_dungeon_time_list2 = new ArrayList<TeamBattleTime>();
	static public String wg_dungeon_time2 = "";
	// 지하수로 진행 시간(초)
	static public int wg_play_time = 600;

	// 악마왕의 영토 입장 레벨
	static public int devil_level = 1;
	// 악마왕의 영토 수배자만 입장가능 여부
	static public boolean devil_wanted = false;
	// 악마왕의 영토 혈맹가입자만 입장가능 여부
	static public boolean devil_clan = false;
	// 악마왕의 영토 입장 시간
	static public List<TeamBattleTime> devil_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String devil_dungeon_time = "";
	// 악마왕의 영토 진행 시간(초)
	static public int devil_play_time = 600;

	// 용의안식처 입장 레벨
	static public int dragon_level = 1;
	// 용의안식처 수배자만 입장가능 여부
	static public boolean dragon_wanted = false;
	// 용의안식처 혈맹가입자만 입장가능 여부
	static public boolean dragon_clan = false;
	// 용의안식처 입장 시간
	static public List<TeamBattleTime> dragon_dungeon_time_list = new ArrayList<TeamBattleTime>();
	static public String dragon_dungeon_time = "";
	// 용의안식처 진행 시간(초)
	static public int dragon_play_time = 600;

	// 수룡의둥지 입장 레벨
	static public int wh_level = 1;
	// 수룡의둥지 수배자만 입장가능 여부
	static public boolean wh_wanted = false;
	// 수룡의둥지 혈맹가입자만 입장가능 여부
	static public boolean wh_clan = false;

	// 망자의무덤 입장 레벨
	static public int gr_level = 1;
	// 망자의무덤 수배자만 입장가능 여부
	static public boolean gr_wanted = false;
	// 망자의무덤 혈맹가입자만 입장가능 여부
	static public boolean gr_clan = false;

	// 타임이벤트 시간
	static public List<TeamBattleTime> time_event_time_list = new ArrayList<TeamBattleTime>();
	static public String time_event_time = "";

	static public int time_ment;

	// 타임이벤트 시간(초)
	static public int time_event_play_time = 600;

	static public String item_check_name;
	static public int item_check_count;

	static public boolean is_item_drop_msg_name;
	static public boolean is_item_drop_msg_en;
	static public boolean is_item_drop_msg_monster;
	static public boolean is_item_drop_msg_item;
	static public boolean is_item_drop_msg_doll;
	static public boolean is_item_drop_msg_life;
	static public boolean is_item_drop_msg_create;

	// 캐릭터 저장 구슬 인벤 확인 여부
	static public boolean is_character_marble_inventory;
	// 캐릭터 저장 구슬 무인 상점 확인 여부
	static public boolean is_character_marble_pc_shop;
	// 캐릭터 저장 구슬에 캐릭터 이름 표시 여부
	static public boolean is_character_marble_name;

	// 경험치 저장 구슬 저장 경험치(%)
	static public double exp_marble_percent;
	// 경험치 저장 구슬 하루 사용 횟수
	static public int exp_marble_save_count;
	// 경험치 구슬 하루 사용 횟수
	static public int exp_marble_use_count;
	// 경험치 저장 구슬 / 경험치 구슬 횟수 초기화 시간
	static public List<TeamBattleTime> exp_marble_time_list = new ArrayList<TeamBattleTime>();
	static public String exp_marble_time = "";

	static public boolean only_clan_boss_class_royal;

	static public int recovery_weapon_safe_0_en_min;
	static public int recovery_weapon_safe_6_en_min;
	static public int recovery_armor_safe_0_en_min;
	static public int recovery_armor_safe_4_en_min;
	static public int recovery_armor_safe_6_en_min;
	static public int recovery_acc_en_min;
	static public int recovery_time;
	static public boolean is_recovery_scroll;

	static public String weapon_safe_0_0_recovery_item;
	static public int weapon_safe_0_0_recovery_item_count;
	static public String weapon_safe_0_1_recovery_item;
	static public int weapon_safe_0_1_recovery_item_count;
	static public String weapon_safe_0_2_recovery_item;
	static public int weapon_safe_0_2_recovery_item_count;
	static public String weapon_safe_0_3_recovery_item;
	static public int weapon_safe_0_3_recovery_item_count;
	static public String weapon_safe_0_4_recovery_item;
	static public int weapon_safe_0_4_recovery_item_count;
	static public String weapon_safe_0_5_recovery_item;
	static public int weapon_safe_0_5_recovery_item_count;
	static public String weapon_safe_0_6_recovery_item;
	static public int weapon_safe_0_6_recovery_item_count;
	static public String weapon_safe_0_7_recovery_item;
	static public int weapon_safe_0_7_recovery_item_count;
	static public String weapon_safe_0_8_recovery_item;
	static public int weapon_safe_0_8_recovery_item_count;
	static public String weapon_safe_0_9_recovery_item;
	static public int weapon_safe_0_9_recovery_item_count;
	static public String weapon_safe_0_10_recovery_item;
	static public int weapon_safe_0_10_recovery_item_count;

	static public String weapon_safe_6_6_recovery_item;
	static public int weapon_safe_6_6_recovery_item_count;
	static public String weapon_safe_6_7_recovery_item;
	static public int weapon_safe_6_7_recovery_item_count;
	static public String weapon_safe_6_8_recovery_item;
	static public int weapon_safe_6_8_recovery_item_count;
	static public String weapon_safe_6_9_recovery_item;
	static public int weapon_safe_6_9_recovery_item_count;
	static public String weapon_safe_6_10_recovery_item;
	static public int weapon_safe_6_10_recovery_item_count;
	static public String weapon_safe_6_11_recovery_item;
	static public int weapon_safe_6_11_recovery_item_count;
	static public String weapon_safe_6_12_recovery_item;
	static public int weapon_safe_6_12_recovery_item_count;
	static public String weapon_safe_6_13_recovery_item;
	static public int weapon_safe_6_13_recovery_item_count;
	static public String weapon_safe_6_14_recovery_item;
	static public int weapon_safe_6_14_recovery_item_count;
	static public String weapon_safe_6_15_recovery_item;
	static public int weapon_safe_6_15_recovery_item_count;

	static public String armor_safe_0_0_recovery_item;
	static public int armor_safe_0_0_recovery_item_count;
	static public String armor_safe_0_1_recovery_item;
	static public int armor_safe_0_1_recovery_item_count;
	static public String armor_safe_0_2_recovery_item;
	static public int armor_safe_0_2_recovery_item_count;
	static public String armor_safe_0_3_recovery_item;
	static public int armor_safe_0_3_recovery_item_count;
	static public String armor_safe_0_4_recovery_item;
	static public int armor_safe_0_4_recovery_item_count;
	static public String armor_safe_0_5_recovery_item;
	static public int armor_safe_0_5_recovery_item_count;
	static public String armor_safe_0_6_recovery_item;
	static public int armor_safe_0_6_recovery_item_count;
	static public String armor_safe_0_7_recovery_item;
	static public int armor_safe_0_7_recovery_item_count;
	static public String armor_safe_0_8_recovery_item;
	static public int armor_safe_0_8_recovery_item_count;
	static public String armor_safe_0_9_recovery_item;
	static public int armor_safe_0_9_recovery_item_count;
	static public String armor_safe_0_10_recovery_item;
	static public int armor_safe_0_10_recovery_item_count;

	static public String armor_safe_4_4_recovery_item;
	static public int armor_safe_4_4_recovery_item_count;
	static public String armor_safe_4_5_recovery_item;
	static public int armor_safe_4_5_recovery_item_count;
	static public String armor_safe_4_6_recovery_item;
	static public int armor_safe_4_6_recovery_item_count;
	static public String armor_safe_4_7_recovery_item;
	static public int armor_safe_4_7_recovery_item_count;
	static public String armor_safe_4_8_recovery_item;
	static public int armor_safe_4_8_recovery_item_count;
	static public String armor_safe_4_9_recovery_item;
	static public int armor_safe_4_9_recovery_item_count;
	static public String armor_safe_4_10_recovery_item;
	static public int armor_safe_4_10_recovery_item_count;
	static public String armor_safe_4_11_recovery_item;
	static public int armor_safe_4_11_recovery_item_count;
	static public String armor_safe_4_12_recovery_item;
	static public int armor_safe_4_12_recovery_item_count;

	static public String armor_safe_6_6_recovery_item;
	static public int armor_safe_6_6_recovery_item_count;
	static public String armor_safe_6_7_recovery_item;
	static public int armor_safe_6_7_recovery_item_count;
	static public String armor_safe_6_8_recovery_item;
	static public int armor_safe_6_8_recovery_item_count;
	static public String armor_safe_6_9_recovery_item;
	static public int armor_safe_6_9_recovery_item_count;
	static public String armor_safe_6_10_recovery_item;
	static public int armor_safe_6_10_recovery_item_count;
	static public String armor_safe_6_11_recovery_item;
	static public int armor_safe_6_11_recovery_item_count;
	static public String armor_safe_6_12_recovery_item;
	static public int armor_safe_6_12_recovery_item_count;

	static public String acc_0_recovery_item;
	static public int acc_0_recovery_item_count;
	static public String acc_1_recovery_item;
	static public int acc_1_recovery_item_count;
	static public String acc_2_recovery_item;
	static public int acc_2_recovery_item_count;
	static public String acc_3_recovery_item;
	static public int acc_3_recovery_item_count;
	static public String acc_4_recovery_item;
	static public int acc_4_recovery_item_count;
	static public String acc_5_recovery_item;
	static public int acc_5_recovery_item_count;
	static public String acc_6_recovery_item;
	static public int acc_6_recovery_item_count;
	static public String acc_7_recovery_item;
	static public int acc_7_recovery_item_count;
	static public String acc_8_recovery_item;
	static public int acc_8_recovery_item_count;
	static public String acc_9_recovery_item;
	static public int acc_9_recovery_item_count;
	static public String acc_10_recovery_item;
	static public int acc_10_recovery_item_count;

	static public String shop_no_tax_npc;

	// 오픈대기 아이템 자동지급
	static public boolean is_world_open_wait_item;
	static public String world_open_wait_item;
	static public int world_open_wait_item_min;
	static public int world_open_wait_item_max;
	static public int world_open_wait_item_delay;

	static public boolean is_clan_point;
	static public int clan_point_monster;
	static public int clan_point_boss_monster;
	static public List<Integer> clan_point_map_list = new ArrayList<Integer>();
	public static boolean cash_wanted;

	static public boolean is_auto_hunt;
	static public boolean is_auto_hunt_pvp;
	static public boolean is_auto_hunt_member;
	static public double is_auto_hunt_exp_percent;
	static public double is_auto_hunt_item_drop_percent;
	static public double is_auto_hunt_aden_drop_percent;
	static public boolean is_auto_hunt_time;
	static public boolean is_auto_hunt_time_account;
	static public int auto_hunt_time;
	static public int auto_hunt_time2;
	static public List<TeamBattleTime> auto_hunt_reset_time = new ArrayList<TeamBattleTime>();
	static public List<Integer> auto_hunt_map_list = new ArrayList<Integer>();
	static public int auto_hunt_go_min_hp;
	static public List<Integer> auto_hunt_home_hp_list = new ArrayList<Integer>();
	static public boolean is_auot_buff;
	static public int auto_buff_delay;
	static public List<Integer> auto_hunt_potion_hp_list = new ArrayList<Integer>();
	static public List<Integer> auto_hunt_mp_list = new ArrayList<Integer>();
	static public List<Integer> auto_hunt_mp_list2 = new ArrayList<Integer>();
	static public boolean is_auto_potion_buy;
	static public String auto_potion_buy_npc;
	static public long auto_potion_buy_min_count;
	static public long auto_potion_buy_count;
	static public boolean is_auto_poly_rank;
	static public boolean is_auto_poly_rank_buy;
	static public String auto_poly_rank_buy_npc;
	static public String auto_poly_rank_item_name;
	static public long auto_poly_rank_buy_min_count;
	static public long auto_poly_rank_buy_count;
	static public boolean is_auto_poly;
	static public boolean is_auto_poly_buy;
	static public String auto_poly_buy_npc;
	static public String auto_poly_item_name;
	static public long auto_poly_buy_min_count;
	static public long auto_poly_buy_count;
	static public boolean is_auto_teleport;
	static public boolean is_auto_teleport_buy;
	static public String auto_teleport_buy_npc;
	static public String auto_teleport_item_name;
	static public long auto_teleport_buy_min_count;
	static public long auto_teleport_buy_count;
	static public List<Integer> auto_hunt_teleport_map_list = new ArrayList<Integer>();
	static public boolean is_auto_haste;
	static public boolean is_auto_haste_buy;
	static public String auto_haste_buy_npc;
	static public String auto_haste_item_name;
	static public long auto_haste_buy_min_count;
	static public long auto_haste_buy_count;
	static public boolean is_auto_bravery;
	static public boolean is_auto_bravery_buy;
	static public String auto_bravery_buy_npc;
	static public String auto_bravery_item_name_royal;
	static public String auto_bravery_item_name_knight;
	static public String auto_bravery_item_name_elf;
	static public boolean is_auto_bravery_wizard_magic;
	static public String auto_bravery_item_name_wizard;
	static public long auto_bravery_buy_min_count;
	static public long auto_bravery_buy_count;
	static public boolean is_auto_arrow_buy;
	static public String auto_arrow_buy_npc;
	static public String auto_arrow_item_name;
	static public long auto_arrow_buy_count;
	static public boolean is_auto_hunt_skill;
	static public double is_auto_hunt_skill_percent;
	static public int auto_hunt_telpeport_delay;
	/** pc(본인 OID, 권장) | item | zero | minimal(초소형 HTML) | extended_item(=item) */
	static public String autohunt_html_dialog_mode = "pc";
	/** false=SHOWHTML 2인자만. true=server_version>144 일 때 4인자(빈request+writeH) */
	static public boolean autohunt_showhtml_extended = false;
	/** false={@code <a action=bypass>} (2.0 계열 권장). true=button 태그(일부 클라에서 불안정) */
	static public boolean autohunt_html_use_button = false;
	/** true=메인은 autohunt.htm+치환(클라 pak 필수). false=서버 인라인 HTML(기본, 빈 창 방지) */
	static public boolean autohunt_use_client_htm = false;
	/** true=인라인 메인을 3페이지로 나눔(한 패킷 HTML 길이 축소·팅김 완화). htm 모드·minimal 은 무관 */
	static public boolean autohunt_html_paginate = true;

	//수배
	static public List<Integer> w_map_list = new ArrayList<Integer>();

	// 룬확률
	static public int runup0 = 1;
	static public int runup1 = 1;
	static public int runup2 = 1;
	static public int runup3 = 1;
	static public int runup4 = 1;
	static public int runup5 = 1;
	static public int runup6 = 1;
	static public int runup7 = 1;
	/**
	 * 리니지에 사용되는 변수 초기화 함수.
	 */
	static public void init(boolean reload) {
		TimeLine.start("lineage.conf file loading...");
		String line = null;

		try {
			royal_spawn.clear();
			royal_first_spell.clear();
			royal_first_inventory.clear();

			knight_spawn.clear();
			knight_first_spell.clear();
			knight_first_inventory.clear();

			elf_spawn.clear();
			elf_first_spell.clear();
			elf_first_inventory.clear();

			wizard_spawn.clear();
			wizard_first_spell.clear();
			wizard_first_inventory.clear();

			darkelf_spawn.clear();
			darkelf_first_spell.clear();
			darkelf_first_inventory.clear();

			dragonknight_spawn.clear();
			dragonknight_first_spell.clear();
			dragonknight_first_inventory.clear();

			blackwizard_spawn.clear();
			blackwizard_first_spell.clear();
			blackwizard_first_inventory.clear();

			kingdom_war_win_item_list.clear();

			no_remove_item.clear();
			
			set_item.clear();

			team_battle_time.clear();
			giran_kingdom_war_day_list.clear();

			kingdom_war_no_remove_item_kent.clear();
			kingdom_war_no_remove_item_orcish.clear();
			kingdom_war_no_remove_item_windawood.clear();
			kingdom_war_no_remove_item_giran.clear();
			kingdom_war_no_remove_item_heine.clear();
			kingdom_war_no_remove_item_abyss.clear();
			kingdom_war_no_remove_item_aden.clear();

			spot_item.clear();

			revenge_not_map_list.clear();
			revenge_need_item_list.clear();

			tebe_dungeon_time_list.clear();
			bug_list.clear();
			tebe_dungeon_time_list2.clear();
			hell_dungeon_time_list.clear();
			hell_dungeon_time_list2.clear();
			Treasuress_dungeon_time_list.clear();
			phunt_dungeon_time_list.clear();
			ice_dungeon_time_list.clear();
			ice_dungeon_time_list2.clear();
			wdr_dungeon_time_list.clear();
			world_dungeon_time_list.clear();
			devil_dungeon_time_list.clear();
			wg_dungeon_time_list.clear();
			wg_dungeon_time_list2.clear();
			dragon_dungeon_time_list.clear();
			time_event_time_list.clear();
			dete_dungeon_time_list.clear();
			dete_dungeon_time_list2.clear();

			exp_marble_time_list.clear();

			clan_point_map_list.clear();

			auto_hunt_reset_time.clear();
			auto_hunt_map_list.clear();
			
			set_item_p.clear();
			set_item_count.clear();
			auto_hunt_home_hp_list.clear();
			auto_hunt_potion_hp_list.clear();
			auto_hunt_mp_list.clear();
			w_map_list.clear();
			auto_hunt_mp_list2.clear();
			auto_hunt_teleport_map_list.clear();

			BufferedReader lnrr = new BufferedReader(new FileReader("lineage.conf"));
			while ((line = lnrr.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				int pos = line.indexOf("=");
				if (pos > 0) {
					String key = line.substring(0, pos).trim();
					String value = line.substring(pos + 1, line.length()).trim();

					if (key.equalsIgnoreCase("royal_spawn"))
						toFirstSpawn(royal_spawn, value);
					else if (key.equalsIgnoreCase("royal_male_gfx"))
						royal_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_female_gfx"))
						royal_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_hp"))
						royal_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_mp"))
						royal_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_max_hp"))
						royal_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_max_mp"))
						royal_max_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_spell"))
						toFirstSpell(royal_first_spell, value);
					else if (key.equalsIgnoreCase("royal_first_inventory"))
						toFirstInventory(royal_first_inventory, value);
					else if (key.equalsIgnoreCase("royal_stat_str"))
						royal_stat_str = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_stat_con"))
						royal_stat_con = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_stat_dex"))
						royal_stat_dex = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_stat_wis"))
						royal_stat_wis = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_stat_cha"))
						royal_stat_cha = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_stat_int"))
						royal_stat_int = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("royal_stat_dice"))
						royal_stat_dice = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_spawn"))
						toFirstSpawn(knight_spawn, value);
					else if (key.equalsIgnoreCase("knight_male_gfx"))
						knight_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_female_gfx"))
						knight_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_hp"))
						knight_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_mp"))
						knight_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_max_hp"))
						knight_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_max_mp"))
						knight_max_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_spell"))
						toFirstSpell(knight_first_spell, value);
					else if (key.equalsIgnoreCase("knight_first_inventory"))
						toFirstInventory(knight_first_inventory, value);
					else if (key.equalsIgnoreCase("knight_stat_str"))
						knight_stat_str = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_stat_con"))
						knight_stat_con = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_stat_dex"))
						knight_stat_dex = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_stat_wis"))
						knight_stat_wis = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_stat_cha"))
						knight_stat_cha = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_stat_int"))
						knight_stat_int = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("knight_stat_dice"))
						knight_stat_dice = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("elf_spawn"))
						toFirstSpawn(elf_spawn, value);
					else if (key.equalsIgnoreCase("elf_male_gfx"))
						elf_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_female_gfx"))
						elf_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_hp"))
						elf_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_mp"))
						elf_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_max_hp"))
						elf_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_max_mp"))
						elf_max_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_spell"))
						toFirstSpell(elf_first_spell, value);
					else if (key.equalsIgnoreCase("elf_first_inventory"))
						toFirstInventory(elf_first_inventory, value);
					else if (key.equalsIgnoreCase("elf_stat_str"))
						elf_stat_str = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_stat_con"))
						elf_stat_con = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_stat_dex"))
						elf_stat_dex = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_stat_wis"))
						elf_stat_wis = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_stat_cha"))
						elf_stat_cha = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_stat_int"))
						elf_stat_int = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_stat_dice"))
						elf_stat_dice = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("wizard_spawn"))
						toFirstSpawn(wizard_spawn, value);
					else if (key.equalsIgnoreCase("wizard_male_gfx"))
						wizard_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_female_gfx"))
						wizard_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_hp"))
						wizard_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_mp"))
						wizard_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_max_hp"))
						wizard_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_max_mp"))
						wizard_max_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_spell"))
						toFirstSpell(wizard_first_spell, value);
					else if (key.equalsIgnoreCase("wizard_first_inventory"))
						toFirstInventory(wizard_first_inventory, value);
					else if (key.equalsIgnoreCase("wizard_stat_str"))
						wizard_stat_str = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_stat_con"))
						wizard_stat_con = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_stat_dex"))
						wizard_stat_dex = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_stat_wis"))
						wizard_stat_wis = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_stat_cha"))
						wizard_stat_cha = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_stat_int"))
						wizard_stat_int = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wizard_stat_dice"))
						wizard_stat_dice = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("Giran_Dungeon"))
						giran_dungeon = Integer.valueOf(value);
					else if(key.equalsIgnoreCase("drake"))
						Double_Drake = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_spawn"))
						toFirstSpawn(darkelf_spawn, value);
					else if (key.equalsIgnoreCase("darkelf_male_gfx"))
						darkelf_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_female_gfx"))
						darkelf_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_hp"))
						darkelf_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_mp"))
						darkelf_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_max_hp"))
						darkelf_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_max_mp"))
						darkelf_max_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("darkelf_spell"))
						toFirstSpell(darkelf_first_spell, value);
					else if (key.equalsIgnoreCase("darkelf_first_inventory"))
						toFirstInventory(darkelf_first_inventory, value);

					else if (key.equalsIgnoreCase("dragonknight_spawn"))
						toFirstSpawn(dragonknight_spawn, value);
					else if (key.equalsIgnoreCase("dragonknight_male_gfx"))
						dragonknight_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dragonknight_female_gfx"))
						dragonknight_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dragonknight_hp"))
						dragonknight_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dragonknight_mp"))
						dragonknight_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dragonknight_max_hp"))
						dragonknight_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dragonknight_spell"))
						toFirstSpell(dragonknight_first_spell, value);
					else if (key.equalsIgnoreCase("dragonknight_first_inventory"))
						toFirstInventory(dragonknight_first_inventory, value);
					else if (key.equalsIgnoreCase("dragonknight_max_mp"))
						blackwizard_max_mp = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("blackwizard_spawn"))
						toFirstSpawn(blackwizard_spawn, value);
					else if (key.equalsIgnoreCase("blackwizard_male_gfx"))
						blackwizard_male_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("blackwizard_female_gfx"))
						blackwizard_female_gfx = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("blackwizard_hp"))
						blackwizard_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("blackwizard_mp"))
						blackwizard_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("blackwizard_max_hp"))
						blackwizard_max_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("blackwizard_max_mp"))
						blackwizard_max_mp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("blackwizard_spell"))
						toFirstSpell(blackwizard_first_spell, value);
					else if (key.equalsIgnoreCase("blackwizard_first_inventory"))
						toFirstInventory(blackwizard_first_inventory, value);

					else if (key.equalsIgnoreCase("inventory_max"))
						inventory_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inventory_weight_max"))
						inventory_weight_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("level_max"))
						level_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("party_max"))
						party_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("warehouse_level"))
						warehouse_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("warehouse_price"))
						warehouse_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("warehouse_price_elf"))
						warehouse_price_elf = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("warehouse_max"))
						warehouse_max = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("stat_str"))
						stat_str = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("stat_dex"))
						stat_dex = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("stat_con"))
						stat_con = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("stat_int"))
						stat_int = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("stat_wis"))
						stat_wis = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("stat_cha"))
						stat_cha = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("rate_enchant"))
						rate_enchant = Double.valueOf(value);
					else if (key.equalsIgnoreCase("penalty_level"))
						penalty_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("penalty_exp"))
						penalty_exp = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("exp_marble_min"))
						exp_marble_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("exp_marble_max"))
						exp_marble_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rate_drop"))
						rate_drop = Double.valueOf(value);
					else if (key.equalsIgnoreCase("rate_exp"))
						rate_exp = Double.valueOf(value);
					else if (key.equalsIgnoreCase("rate_lawful"))
						rate_lawful = Double.valueOf(value);
					else if (key.equalsIgnoreCase("rate_aden"))
						rate_aden = Double.valueOf(value);
					else if (key.equalsIgnoreCase("rate_party"))
						rate_party = Double.valueOf(value);
					else if (key.equalsIgnoreCase("rate_exp_pet"))
						rate_exp_pet = Double.valueOf(value);
					else if (key.equalsIgnoreCase("powerball_payout_rate"))
						powerball_payout_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("chatting_level_global"))
						chatting_level_global = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("chatting_level_normal"))
						chatting_level_normal = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("chatting_level_whisper"))
						chatting_level_whisper = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("chatting_global_macro_delay"))
						chatting_global_macro_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_chatting_clan"))
						is_chatting_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("nonpvp"))
						nonpvp = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("server_version"))
						server_version = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("ai_corpse_time"))
						ai_corpse_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("ai_summon_corpse_time"))
						ai_summon_corpse_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("ai_pet_corpse_time"))
						ai_pet_corpse_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("ai_auto_healingpotion_percent"))
						ai_auto_healingpotion_percent = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("warehouse_pet_price"))
						warehouse_pet_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("flat_rate"))
						flat_rate = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("flat_rate_price"))
						flat_rate_price = Integer.valueOf(value) * 60;
					else if (key.equalsIgnoreCase("account_auto_create"))
						account_auto_create = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("inn_max"))
						inn_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inn_in_max"))
						inn_in_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inn_price"))
						inn_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inn_time"))
						inn_time = Integer.valueOf(value) * 1000 * 60;
					else if (key.equalsIgnoreCase("board_write_price"))
						board_write_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("board_write_min_level"))
						board_write_min_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rank_update_delay"))
						rank_update_delay = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("rank_min_level"))
						rank_min_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rank_class_1"))
						rank_class_1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rank_class_2"))
						rank_class_2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rank_class_3"))
						rank_class_3 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rank_class_4"))
						rank_class_4 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_rank_poly"))
						is_rank_poly = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("rank_poly_all"))
						rank_poly_all = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rank_poly_class"))
						rank_poly_class = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inn_hall_max"))
						inn_hall_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inn_hall_in_max"))
						inn_hall_in_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("inn_hall_price"))
						inn_hall_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("add_tax"))
						add_tax = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_kingdom_war"))
						is_kingdom_war = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_war_win_gift"))
						kingdom_war_win_gift = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("crown_clan_min_people"))
						crown_clan_min_people = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom"))
						KINGDOM = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_war_win_item_list"))
						kingdomWarWinItemList(kingdom_war_win_item_list, value);
					else if (key.equalsIgnoreCase("min_tax"))
						min_tax = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("max_tax"))
						max_tax = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_kingdom_war_day_list"))
						kingdomDay(giran_kingdom_war_day_list, value);
					else if (key.equalsIgnoreCase("kingdom_war_time"))
						kingdom_war_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_player_dead_expdown"))
						kingdom_player_dead_expdown = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_player_dead_itemdrop"))
						kingdom_player_dead_itemdrop = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_crown"))
						kingdom_crown = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_crown_bless"))
						kingdom_crown_bless = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_crown_enchant"))
						kingdom_crown_enchant = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_pvp_pk"))
						kingdom_pvp_pk = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_war_revival"))
						kingdom_war_revival = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_war_callclan"))
						kingdom_war_callclan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_item_count_rate"))
						kingdom_item_count_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_clan_join"))
						kingdom_clan_join = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_restart_giran_home"))
						is_restart_giran_home = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_soldier_price"))
						kingdom_soldier_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elf_gatherup_time"))
						elf_gatherup_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("npc_talk_stay_time"))
						npc_talk_stay_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("ChatTime"))
						ChatTime = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("ChatTimetwo"))
						ChatTimetwo = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elvenforest_elementalstone_spawn_count"))
						elvenforest_elementalstone_spawn_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elvenforest_elementalstone_spawn_time"))
						elvenforest_elementalstone_spawn_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("elvenforest_elementalstone_min_count"))
						elvenforest_elementalstone_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elvenforest_elementalstone_max_count"))
						elvenforest_elementalstone_max_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("door_open_delay"))
						door_open_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("Eggs_spawn_time"))
						eggs_spawn_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("Eggs_min_count"))
						eggs_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("Eggs_max_count"))
						eggs_max_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("object_who"))
						object_who = value;
					else if (key.equalsIgnoreCase("world_item_delay"))
						world_item_delay = Integer.valueOf(value) * 1000 * 60;
					else if (key.equalsIgnoreCase("auto_pickup"))
						auto_pickup = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_pickup_aden"))
						auto_pickup_aden = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_pickup_percent"))
						auto_pickup_percent = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_dex_ac"))
						is_dex_ac = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("player_dead_itemdrop"))
						player_dead_itemdrop = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("player_dead_expdown"))
						player_dead_expdown = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("player_dead_exp_gift"))
						player_dead_exp_gift = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("player_dead_expdown_level"))
						player_dead_expdown_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("player_dead_expdown_rate"))
						player_dead_expdown_rate = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("player_lost_exp_rate"))
						player_lost_exp_rate = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("player_lost_exp_aden_rate"))
						player_lost_exp_aden_rate = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("player_dead_itemdrop_level"))
						player_dead_itemdrop_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("monster_interface_hpbar"))
						monster_interface_hpbar = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("npc_interface_hpbar"))
						npc_interface_hpbar = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("ai_monster_tic_time"))
						ai_monster_tic_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("item_durability_max"))
						item_durability_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("pet_level_max"))
						pet_level_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("notice_delay"))
						notice_delay = Integer.valueOf(value) * 1000 * 60;
					else if (key.equalsIgnoreCase("is_kingdom_war_notice"))
						is_kingdom_war_notice = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_war_notice_delay"))
						kingdom_war_notice_delay = Integer.valueOf(value) * 1000 * 60;
					else if (key.equalsIgnoreCase("attendance_check_enabled"))
						attendance_check_enabled = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("event_poly"))
						event_poly = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("account_ip_count"))
						account_ip_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("ip_character_count"))
						ip_character_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("ip_in_game_count"))
						ip_in_game_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("event_buff"))
						event_buff = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("event_illusion"))
						event_illusion = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("event_christmas"))
						event_christmas = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("event_halloween"))
						event_halloween = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("event_lyra"))
						event_lyra = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_save_time"))
						auto_save_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("slime_race_price"))
						slime_race_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dog_race_price"))
						dog_race_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_magic_doll_race"))
						is_magic_doll_race = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("magic_doll_race_delay"))
						magic_doll_race_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("magic_doll_race_price"))
						magic_doll_race_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("bravery_potion_frame"))
						bravery_potion_frame = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("elven_wafer_frame"))
						elven_wafer_frame = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("holywalk_frame"))
						holywalk_frame = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("world_premium_item_is"))
						world_premium_item_is = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("world_premium_item"))
						world_premium_item = value;
					else if (key.equalsIgnoreCase("world_premium_item_min"))
						world_premium_item_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("world_premium_item_max"))
						world_premium_item_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("world_premium_item_delay"))
						world_premium_item_delay = Integer.valueOf(value) * 1000 * 60;
					else if (key.equalsIgnoreCase("item_equipped_type"))
						item_equipped_type = value.equalsIgnoreCase("new");
					else if (key.equalsIgnoreCase("world_message_join"))
						world_message_join = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("max_mr"))
						max_mr = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("monster_level_exp"))
						monster_level_exp = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("monster_summon_item_drop"))
						monster_summon_item_drop = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_damage_item_drop"))
						is_damage_item_drop = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("monster_item_drop"))
						monster_item_drop = value.equalsIgnoreCase("new");
					else if (key.equalsIgnoreCase("monster_boss_spawn_message"))
						monster_boss_spawn_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("monster_boss_spawn_blue_message"))
						monster_boss_spawn_blue_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("monster_boss_dead_message"))
						monster_boss_dead_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("colosseum_talkingisland"))
						colosseum_talkingisland = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("colosseum_silverknighttown"))
						colosseum_silverknighttown = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("colosseum_gludin"))
						colosseum_gludin = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("colosseum_windawood"))
						colosseum_windawood = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("colosseum_kent"))
						colosseum_kent = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("colosseum_giran"))
						colosseum_giran = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("item_accessory_bless_enchant"))
						item_accessory_bless_enchant = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("item_polymorph_bless"))
						item_polymorph_bless = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("item_enchant_armor_max"))
						item_enchant_armor_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("item_enchant_weapon_max"))
						item_enchant_weapon_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("item_enchant_accessory_max"))
						item_enchant_accessory_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("item_magicdoll_max"))
						item_magicdoll_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("doll_drogon"))
						doll_drogon = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("pet_damage_to_player"))
						pet_damage_to_player = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("pet_tame_is"))
						pet_tame_is = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("item_elixir_max"))
						item_elixir_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("elixir_min_level"))
						elixir_min_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("thread_event"))
						thread_event = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("thread_ai"))
						thread_ai = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("client_ping_time"))
						client_ping_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("world_player_count"))
						world_player_count = Double.valueOf(value);
					else if (key.equalsIgnoreCase("world_player_count_init"))
						world_player_count_init = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("party_autopickup_item_print"))
						party_autopickup_item_print = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("party_pickup_item_print"))
						party_pickup_item_print = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("party_autopickup_item_print_on_screen"))
						party_autopickup_item_print_on_screen = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("party_exp_level_range"))
						party_exp_level_range = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_clan"))
						wanted_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("wanted_name"))
						wanted_name = value;
					else if (key.equalsIgnoreCase("wanted_level")) {
						if (value.startsWith("~")) {
							// max
							wanted_level_min = 0;
							wanted_level_max = Integer.valueOf(value.substring(1).trim());
						} else {
							// min, min~max
							pos = value.indexOf("~");
							if (pos < 0)
								pos = value.length();
							wanted_level_min = Integer.valueOf(value.substring(0, pos).trim());
							if (pos + 1 < value.length())
								wanted_level_max = Integer.valueOf(value.substring(pos + 1).trim());
						}
					} else if (key.equalsIgnoreCase("wanted_price_min"))
						wanted_price_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_max"))
						wanted_price_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_min52"))
						wanted_price_min52 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_max52"))
						wanted_price_max52 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_min55"))
						wanted_price_min55 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_max55"))
						wanted_price_max55 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_min60"))
						wanted_price_min60 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_max60"))
						wanted_price_max60 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_min65"))
						wanted_price_min65 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wanted_price_max65"))
						wanted_price_max65 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("pvp_print_message"))
						pvp_print_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("view_cracker_damage"))
						view_cracker_damage = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("view_cracker_dps"))
						view_cracker_dps = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("cracker_exp_max_level"))
						cracker_exp_max_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("user_command"))
						user_command = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auction_delay"))
						auction_delay = Long.valueOf(value) * 1000;

					else if (key.equalsIgnoreCase("boxspawn"))
						boxspawn = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("speedhack"))
						speedhack = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("speedhack_warning_count"))
						speedhack_warning_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("speedhack_stun"))
						speedhack_stun = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("speed_bad_packet_count"))
						speed_bad_packet_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("speed_good_packet_count"))
						speed_good_packet_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_attack_count_packet"))
						is_attack_count_packet = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("speed_hack_block_time"))
						speed_hack_block_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("speed_hack_message_count"))
						speed_hack_message_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("speed_check_walk_frame_rate"))
						speed_check_walk_frame_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("speed_check_attack_frame_rate"))
						speed_check_attack_frame_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("delay_hack_count"))
						delay_hack_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("delay_hack_frame_rate"))
						delay_hack_frame_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("gost_hack_block_time"))
						gost_hack_block_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("clan_warehouse_message"))
						clan_warehouse_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("clan_max"))
						clan_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("character_delete"))
						character_delete = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("rank_filter_names")) {
						StringBuffer sb = new StringBuffer();
						for (String k : value.split(","))
							sb.append("name!='").append(k).append("' AND ");
						rank_filter_names_query = sb.substring(0, sb.length() - 5);
					} else if (key.equalsIgnoreCase("is_auto_fishing"))
						is_auto_fishing = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("fish_delay"))
						fish_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("fish_exp"))
						fish_exp = value;
					else if (key.equalsIgnoreCase("auto_fish_level"))
						auto_fish_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_fish_coin"))
						auto_fish_coin = value;
					else if (key.equalsIgnoreCase("auto_fish_expense"))
						auto_fish_expense = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("fish_rice"))
						fish_rice = value;
					else if (key.equalsIgnoreCase("healingpotion_message"))
						healingpotion_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("home_hp_tic"))
						home_hp_tic = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("home_mp_tic"))
						home_mp_tic = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_giran_dungeon_time"))
						is_giran_dungeon_time = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("giran_dungeon_time"))
						giran_dungeon_time = Integer.valueOf(value) * 60;
					else if (key.equalsIgnoreCase("giran_dungeon_inti_time"))
						giran_dungeon_inti_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dungeon_inti_time_message"))
						dungeon_inti_time_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("new_clan_name")) {
						new_clan_name = value == null || value.length() < 2 ? "신규혈맹없습니다." : value;

						if (!reload) {
							new_clan_name_temp = new_clan_name;
						} else {
							ClanController.reloadNewClan(new_clan_name_temp, new_clan_name);
						}
					} else if (key.equalsIgnoreCase("is_new_clan_auto_out"))
						is_new_clan_auto_out = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("new_clan_max_level"))
						new_clan_max_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_new_clan_pvp"))
						is_new_clan_pvp = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_new_clan_attack_boss"))
						is_new_clan_attack_boss = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_new_clan_oman_top"))
						is_new_clan_oman_top = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_no_clan_pvp"))
						is_no_clan_pvp = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_no_clan_attack_boss"))
						is_no_clan_attack_boss = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("is_heal_target"))
						is_heal_target = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_greater_heal_target"))
						is_greater_heal_target = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_advance_spirit_target"))
						is_advance_spirit_target = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_iron_skin_target"))
						is_iron_skin_target = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_storm_shot_target"))
						is_storm_shot_target = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("no_remove_item"))
						isRemoveItem(no_remove_item, value);
					else if (key.equalsIgnoreCase("memory_recycle"))
						memory_recycle = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("set_item"))
						isSetItem(set_item, value);
					else if (key.equalsIgnoreCase("set_item_p"))
						kingdomDay(set_item_p, value);
					else if (key.equalsIgnoreCase("set_item_count"))
						kingdomDay(set_item_count, value);
					else if (key.equalsIgnoreCase("set_check_itemname"))
						set_check_itemname = value;
					else if (key.equalsIgnoreCase("set_check_count"))
						set_check_count = Integer.valueOf(value);
					
					else if (key.equalsIgnoreCase("sell_item_rate"))
						sell_item_rate = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("sell_bless_item_rate"))
						sell_bless_item_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("sell_curse_item_rate"))
						sell_curse_item_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("scroll_tell"))
						scroll_tell = value;
					else if (key.equalsIgnoreCase("scroll_poly"))
						scroll_poly = value;
					else if (key.equalsIgnoreCase("scroll_dane_fools"))
						scroll_dane_fools = value;
					else if (key.equalsIgnoreCase("scroll_zel_go_mer"))
						scroll_zel_go_mer = value;
					else if (key.equalsIgnoreCase("scroll_orim"))
						scroll_orim = value;
					else if (key.equalsIgnoreCase("teamBattle_max_pc"))
						teamBattle_max_pc = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("teamBattle_level"))
						teamBattle_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("teamBattle_A_team"))
						teamBattle_A_team = value;
					else if (key.equalsIgnoreCase("teamBattle_B_team"))
						teamBattle_B_team = value;
					else if (key.equalsIgnoreCase("team_battle_world_message"))
						team_battle_world_message = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("team_battle_time"))
						teamBattleTime(team_battle_time, value);
					else if (key.equalsIgnoreCase("is_teamBattle_chatting"))
						is_teamBattle_chatting = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_two_clan_join"))
						is_two_clan_join = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_world_clean"))
						is_world_clean = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_world_clean_message"))
						is_world_clean_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("world_clean_time"))
						world_clean_time = Integer.valueOf(value) * 1000 * 60;
					else if (key.equalsIgnoreCase("world_clean_message_time"))
						world_clean_message_time = (Integer.valueOf(value) * 1000) + 1000;
					else if (key.equalsIgnoreCase("is_exp_support"))
						is_exp_support = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("exp_support_level_gap"))
						exp_support_level_gap = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("exp_support_max_level"))
						exp_support_max_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("buff_max_level"))
						buff_max_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("buff_aden"))
						buff_aden = Long.valueOf(value);
					else if (key.equalsIgnoreCase("is_market_only_aden"))
						is_market_only_aden = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("market_max_count"))
						market_max_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("command"))
						command = value;
					else if (key.equalsIgnoreCase("is_gm_global_chat"))
						is_gm_global_chat = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_weapon_speed"))
						is_weapon_speed = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("enable_custom_armor_accessory_set_bonus"))
						enable_custom_armor_accessory_set_bonus = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("sword_rack_delay"))
						sword_rack_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_pvp_auto_potion"))
						is_pvp_auto_potion = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_chatting_close_command"))
						is_chatting_close_command = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_battle_zone"))
						is_battle_zone = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("battle_zone_x1"))
						battle_zone_x1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("battle_zone_y1"))
						battle_zone_y1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("battle_zone_x2"))
						battle_zone_x2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("battle_zone_y2"))
						battle_zone_y2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("battle_zone_map"))
						battle_zone_map = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_battle_zone_hp_bar"))
						is_battle_zone_hp_bar = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("class_1_boss_aden_min"))
						class_1_boss_aden_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_1_boss_aden_max"))
						class_1_boss_aden_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_2_boss_aden_min"))
						class_2_boss_aden_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_2_boss_aden_max"))
						class_2_boss_aden_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_3_boss_aden_min"))
						class_3_boss_aden_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_3_boss_aden_max"))
						class_3_boss_aden_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_4_boss_aden_min"))
						class_4_boss_aden_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("class_4_boss_aden_max"))
						class_4_boss_aden_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_kingdom_war_hour"))
						giran_kingdom_war_hour = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_kingdom_war_min"))
						giran_kingdom_war_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_kingdom_crown_min"))
						giran_kingdom_crown_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_kingdom_crown_msg_count"))
						giran_kingdom_crown_msg_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("world_result"))
						world_result = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("tr_gift"))
						tr_gift = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("dayc"))
						dayc = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("dayc1"))
						dayc1 = value;
					else if (key.equalsIgnoreCase("daycc1"))
						daycc1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc2"))
						dayc2 = value;
					else if (key.equalsIgnoreCase("daycc2"))
						daycc2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc3"))
						dayc3 = value;
					else if (key.equalsIgnoreCase("daycc3"))
						daycc3 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc4"))
						dayc4 = value;
					else if (key.equalsIgnoreCase("daycc4"))
						daycc4 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc5"))
						dayc5 = value;
					else if (key.equalsIgnoreCase("daycc5"))
						daycc5 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc6"))
						dayc6 = value;
					else if (key.equalsIgnoreCase("daycc6"))
						daycc6 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc7"))
						dayc7 = value;
					else if (key.equalsIgnoreCase("daycc7"))
						daycc7 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc8"))
						dayc8 = value;
					else if (key.equalsIgnoreCase("daycc8"))
						daycc8 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc9"))
						dayc9 = value;
					else if (key.equalsIgnoreCase("daycc9"))
						daycc9 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc10"))
						dayc10 = value;
					else if (key.equalsIgnoreCase("daycc10"))
						daycc10 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc11"))
						dayc11 = value;
					else if (key.equalsIgnoreCase("daycc11"))
						daycc11 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc12"))
						dayc12 = value;
					else if (key.equalsIgnoreCase("daycc12"))
						daycc12 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc13"))
						dayc13 = value;
					else if (key.equalsIgnoreCase("daycc13"))
						daycc13 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc14"))
						dayc14 = value;
					else if (key.equalsIgnoreCase("daycc14"))
						daycc14 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc15"))
						dayc15 = value;
					else if (key.equalsIgnoreCase("daycc15"))
						daycc15 = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("dayc16"))
						dayc16 = value;
					else if (key.equalsIgnoreCase("daycc16"))
						daycc16 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc17"))
						dayc17 = value;
					else if (key.equalsIgnoreCase("daycc17"))
						daycc17 = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("dayc18"))
						dayc18 = value;
					else if (key.equalsIgnoreCase("daycc18"))
						daycc18 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc19"))
						dayc19 = value;
					else if (key.equalsIgnoreCase("daycc19"))
						daycc19 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc20"))
						dayc20 = value;
					else if (key.equalsIgnoreCase("daycc20"))
						daycc20 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc21"))
						dayc21 = value;
					else if (key.equalsIgnoreCase("daycc21"))
						daycc21 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc22"))
						dayc22 = value;
					else if (key.equalsIgnoreCase("daycc22"))
						daycc22 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc23"))
						dayc23 = value;
					else if (key.equalsIgnoreCase("daycc23"))
						daycc23 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc24"))
						dayc24 = value;
					else if (key.equalsIgnoreCase("daycc24"))
						daycc24 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc25"))
						dayc25 = value;
					else if (key.equalsIgnoreCase("daycc25"))
						daycc25 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc26"))
						dayc26 = value;
					else if (key.equalsIgnoreCase("daycc26"))
						daycc26 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc27"))
						dayc27 = value;
					else if (key.equalsIgnoreCase("daycc27"))
						daycc27 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc28"))
						dayc28 = value;
					else if (key.equalsIgnoreCase("daycc28"))
						daycc28 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc29"))
						dayc29 = value;
					else if (key.equalsIgnoreCase("daycc29"))
						daycc29 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayc30"))
						dayc30 = value;
					else if (key.equalsIgnoreCase("daycc30"))
						daycc30 = Integer.valueOf(value);
					
					else if (key.equalsIgnoreCase("q1"))
						q1 = value;
					else if (key.equalsIgnoreCase("qc1"))
						qc1 = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("q2"))
						q2 = value;
					else if (key.equalsIgnoreCase("qc2"))
						qc2 = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("q3"))
						q3 = value;
					else if (key.equalsIgnoreCase("qc3"))
						qc3 = Integer.valueOf(value);
					
					
					else if (key.equalsIgnoreCase("rq1"))
						rq1 = value;
					else if (key.equalsIgnoreCase("rqc1"))
						rqc1 = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("rq2"))
						rq2 = value;
					else if (key.equalsIgnoreCase("rqc2"))
						rqc2 = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("rq3"))
						rq3 = value;
					else if (key.equalsIgnoreCase("rqc3"))
						rqc3 = Integer.valueOf(value);
					
					else if (key.equalsIgnoreCase("lastday"))
						lastday = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("lastquest"))
						lastquest = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dayquest"))
						dayquest = Integer.valueOf(value);


					else if (key.equalsIgnoreCase("checkment"))
						checkment = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("is_critical_effect"))
						is_critical_effect = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_skill_critical_effect"))
						is_skill_critical_effect = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_character_dead_effect"))
						is_character_dead_effect = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_monster_dead_effect"))
						is_monster_dead_effect = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_miss_effect"))
						is_miss_effect = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_dmgviewer"))
						is_DmgViewer = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("giran_dungeon_level"))
						giran_dungeon_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_dungeon_level2"))
						giran_dungeon_level2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_dungeon_level3"))
						giran_dungeon_level3 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("lv_40_exp_rate"))
						lv_40_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_41_exp_rate"))
						lv_41_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_42_exp_rate"))
						lv_42_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_43_exp_rate"))
						lv_43_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_44_exp_rate"))
						lv_44_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_45_exp_rate"))
						lv_45_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_46_exp_rate"))
						lv_46_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_47_exp_rate"))
						lv_47_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_48_exp_rate"))
						lv_48_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_49_exp_rate"))
						lv_49_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_50_exp_rate"))
						lv_50_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_51_exp_rate"))
						lv_51_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_52_exp_rate"))
						lv_52_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_53_exp_rate"))
						lv_53_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_54_exp_rate"))
						lv_54_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_55_exp_rate"))
						lv_55_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_56_exp_rate"))
						lv_56_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_57_exp_rate"))
						lv_57_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_58_exp_rate"))
						lv_58_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_59_exp_rate"))
						lv_59_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_60_exp_rate"))
						lv_60_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_61_exp_rate"))
						lv_61_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_62_exp_rate"))
						lv_62_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_63_exp_rate"))
						lv_63_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_64_exp_rate"))
						lv_64_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_65_exp_rate"))
						lv_65_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_66_exp_rate"))
						lv_66_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_67_exp_rate"))
						lv_67_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_68_exp_rate"))
						lv_68_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_69_exp_rate"))
						lv_69_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_60_exp_rate"))
						lv_60_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_61_exp_rate"))
						lv_61_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_62_exp_rate"))
						lv_62_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_63_exp_rate"))
						lv_63_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_64_exp_rate"))
						lv_64_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_65_exp_rate"))
						lv_65_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_66_exp_rate"))
						lv_66_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_67_exp_rate"))
						lv_67_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_68_exp_rate"))
						lv_68_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_69_exp_rate"))
						lv_69_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_70_exp_rate"))
						lv_70_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_71_exp_rate"))
						lv_71_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_72_exp_rate"))
						lv_72_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_73_exp_rate"))
						lv_73_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_74_exp_rate"))
						lv_74_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_75_exp_rate"))
						lv_75_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_76_exp_rate"))
						lv_76_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_77_exp_rate"))
						lv_77_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_78_exp_rate"))
						lv_78_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_79_exp_rate"))
						lv_79_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_80_exp_rate"))
						lv_80_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_81_exp_rate"))
						lv_81_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_82_exp_rate"))
						lv_82_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_83_exp_rate"))
						lv_83_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_84_exp_rate"))
						lv_84_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_85_exp_rate"))
						lv_85_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_86_exp_rate"))
						lv_86_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_87_exp_rate"))
						lv_87_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_88_exp_rate"))
						lv_88_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_89_exp_rate"))
						lv_89_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("lv_90_exp_rate"))
						lv_90_exp_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_war_day"))
						kingdom_war_day = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("is_auto_hunt_check"))
						is_auto_hunt_check = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_hunt_monster_kill_count"))
						auto_hunt_monster_kill_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_hunt_check_skill"))
						is_auto_hunt_check_skill = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_hunt_answer_time"))
						auto_hunt_answer_time = Integer.valueOf(value) * 1000;

					else if (key.equalsIgnoreCase("is_pc_trade_sell_name"))
						is_pc_trade_sell_name = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("pc_trade_sell_level"))
						pc_trade_sell_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("pc_trade_buy_level"))
						pc_trade_buy_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_pc_trade_sell_only_aden"))
						is_pc_trade_sell_only_aden = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_aden_static_price"))
						is_aden_static_price = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("aden_static_unit"))
						aden_static_unit = Long.valueOf(value);
					else if (key.equalsIgnoreCase("aden_static_price"))
						aden_static_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_pc_trade_success_delete"))
						is_pc_trade_success_delete = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("pc_trade_sale_max_count"))
						pc_trade_sale_max_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("pc_trade_buy_max_count"))
						pc_trade_buy_max_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("pc_trade_buy_deposit_delay"))
						pc_trade_buy_deposit_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_kent_kingdom_war_no_remove"))
						is_kent_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_orcish_kingdom_war_no_remove"))
						is_orcish_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_windawood_kingdom_war_no_remove"))
						is_windawood_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_giran_kingdom_war_no_remove"))
						is_giran_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_heine_kingdom_war_no_remove"))
						is_heine_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_abyss_kingdom_war_no_remove"))
						is_abyss_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_aden_kingdom_war_no_remove"))
						is_aden_kingdom_war_no_remove = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_kent"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_kent, value);
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_orcish"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_orcish, value);
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_windawood"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_windawood, value);
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_giran"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_giran, value);
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_heine"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_heine, value);
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_abyss"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_abyss, value);
					else if (key.equalsIgnoreCase("kingdom_war_no_remove_item_aden"))
						isKingdomWarNoRemoveItem(kingdom_war_no_remove_item_aden, value);
					else if (key.equalsIgnoreCase("clan_rate_drop"))
						clan_rate_drop = Double.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_clan_rate_drop"))
						kingdom_clan_rate_drop = Double.valueOf(value);
					else if (key.equalsIgnoreCase("clan_rate_exp"))
						clan_rate_exp = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("kingdom_clan_rate_exp"))
						kingdom_clan_rate_exp = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("clan_rate_aden"))
						clan_rate_aden = Double.valueOf(value);
					else if (key.equalsIgnoreCase("kingdom_clan_rate_aden"))
						kingdom_clan_rate_aden = Double.valueOf(value);
					else if (key.equalsIgnoreCase("robot_auto_pc"))
						robot_auto_pc = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_party_aden_share"))
						is_party_aden_share = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("giran_dungeon_scroll_count"))
						giran_dungeon_scroll_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("giran_dungeon_reset_hour"))
						giran_dungeon_reset_hour = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("auto_scroll_count"))
						auto_scroll_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_dungeon_reset_hour"))
						auto_dungeon_reset_hour = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("is_giran_dungeon_scroll_message"))
						is_giran_dungeon_scroll_message = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("attackAndMagic_delay"))
						attackAndMagic_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_batpomet_system"))
						is_batpomet_system = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_exp_marble_system"))
						is_exp_marble_system = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_fire_nest_teleport"))
						is_fire_nest_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_oren_teleport"))
						is_oren_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_heine_teleport"))
						is_heine_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_aden_teleport"))
						is_aden_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_welldone_teleport"))
						is_welldone_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_ivory_tower_teleport"))
						is_ivory_tower_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_kingdom_outer_teleport"))
						TeleportPolicy.kingdomOuterTeleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_giran_agit_teleport"))
						TeleportPolicy.giranAgitTeleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_forgotten_island_teleport"))
						TeleportPolicy.forgottenIslandTeleport = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("boss_live_time"))
						boss_live_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("is_rest_cracker"))
						is_rest_cracker = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("rest_cracker_level"))
						rest_cracker_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_delay"))
						rest_cracker_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_name"))
						rest_cracker_name = value;
					else if (key.equalsIgnoreCase("rest_cracker_weapon"))
						rest_cracker_weapon = value;
					else if (key.equalsIgnoreCase("rest_cracker_arrow"))
						rest_cracker_arrow = value;
					else if (key.equalsIgnoreCase("rest_cracker_hp"))
						rest_cracker_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_exp_min"))
						rest_cracker_exp_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_exp_max"))
						rest_cracker_exp_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_aden_min"))
						rest_cracker_aden_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_aden_max"))
						rest_cracker_aden_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_rest_cracker_haste"))
						is_rest_cracker_haste = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_rest_cracker_bravery"))
						is_rest_cracker_bravery = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("rest_cracker_x1"))
						rest_cracker_x1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_y1"))
						rest_cracker_y1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_x2"))
						rest_cracker_x2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_y2"))
						rest_cracker_y2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("rest_cracker_map"))
						rest_cracker_map = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("is_drop_item"))
						is_drop_item = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("is_blue_message"))
						is_blue_message = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("done_ment1"))
						done_ment1 = value;
					else if (key.equalsIgnoreCase("done_ment2"))
						done_ment2 = value;
					else if (key.equalsIgnoreCase("done_ment3"))
						done_ment3 = value;
					else if (key.equalsIgnoreCase("done_ment4"))
						done_ment4 = value;
					else if (key.equalsIgnoreCase("done_ment5"))
						done_ment5 = value;
					else if (key.equalsIgnoreCase("done_ment6"))
						done_ment6 = value;
					else if (key.equalsIgnoreCase("done_ment7"))
						done_ment7 = value;

					else if (key.equalsIgnoreCase("war_ment"))
						war_ment = value;

					else if (key.equalsIgnoreCase("is_spot"))
						is_spot = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("spot_tower_hp"))
						spot_tower_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_tower_x"))
						spot_tower_x = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_tower_y"))
						spot_tower_y = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_tower_map"))
						spot_tower_map = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_tower_start_hour"))
						spot_tower_start_hour = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_tower_start_min"))
						spot_tower_start_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_tower_time"))
						spot_tower_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("spot_item"))
						toFirstInventory(spot_item, value);

					else if (key.equalsIgnoreCase("is_fight"))
						is_fight = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("fight_aden"))
						fight_aden = value;
					else if (key.equalsIgnoreCase("fight_ticket_price"))
						fight_ticket_price = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("fight_rate"))
						fight_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("fight_max_ticket"))
						fight_max_ticket = Long.valueOf(value);

					else if (key.equalsIgnoreCase("is_sword_lack_check"))
						is_sword_lack_check = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("is_maan_effect"))
						마안이팩트여부 = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("maan_birth_percent"))
						탄생의마안_확률 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("maan_birth_aden_name"))
						탄생의마안_제작_아덴 = value;
					else if (key.equalsIgnoreCase("maan_birth_aden_count"))
						탄생의마안_제작_아덴_수량 = Long.valueOf(value);

					else if (key.equalsIgnoreCase("maan_shape_percent"))
						형상의마안_확률 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("maan_shape_aden_name"))
						형상의마안_제작_아덴 = value;
					else if (key.equalsIgnoreCase("maan_shape_aden_count"))
						형상의마안_제작_아덴_수량 = Long.valueOf(value);

					else if (key.equalsIgnoreCase("maan_life_percent"))
						생명의마안_확률 = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("maan_life_aden_name"))
						생명의마안_제작_아덴 = value;
					else if (key.equalsIgnoreCase("maan_life_aden_count"))
						생명의마안_제작_아덴_수량 = Long.valueOf(value);

					else if (key.equalsIgnoreCase("is_immortality"))
						is_immortality = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_immortality_pvp"))
						is_immortality_pvp = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("immortality_item_name"))
						immortality_item_name = value;
					else if (key.equalsIgnoreCase("immortality_aden_percent"))
						immortality_aden_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("immortality_item_percent"))
						immortality_item_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("is_immortality_kill_item"))
						is_immortality_kill_item = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("immortality_kill_item_name"))
						immortality_kill_item_name = value;
					else if (key.equalsIgnoreCase("is_immortality_kill_item_drop"))
						is_immortality_kill_item_drop = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_immortality_kill_item_drop_monster"))
						is_immortality_kill_item_drop_monster = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("is_pet_hungry"))
						is_pet_hungry = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("is_sync"))
						is_sync = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("revenge_uid"))
						revenge_uid = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_revenge"))
						is_revenge = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("revenge_level"))
						revenge_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_clan_revenge"))
						is_clan_revenge = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("revenge_not_map_list"))
						stringToInt(revenge_not_map_list, value);
					else if (key.equalsIgnoreCase("revenge_need_item_list")) {
						revenge_need_item = value;
						toFirstInventory(revenge_need_item_list, value);
					} else if (key.equalsIgnoreCase("revenge_delay"))
						revenge_delay = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("tebe_level"))
						tebe_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("tebe_wanted"))
						tebe_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("tebe_clan"))
						tebe_clan = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("tebe_dungeon_time")) {
						tebe_dungeon_time = value;
						teamBattleTime(tebe_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("tebe_play_time"))
						tebe_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("bug_time")) {
						bug_time = value;
						teamBattleTime(bug_list, value);
					} else if (key.equalsIgnoreCase("bug_play_time"))
						bug_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("hell_level"))
						hell_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("hell_wanted"))
						hell_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("hell_clan"))
						hell_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("hell_dungeon_time")) {
						hell_dungeon_time = value;
						teamBattleTime(hell_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("hell_play_time"))
						hell_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("tebe_dungeon_time2")) {
						tebe_dungeon_time2 = value;
						teamBattleTime(tebe_dungeon_time_list2, value);
					}

					else if (key.equalsIgnoreCase("hell_dungeon_time2")) {
						hell_dungeon_time2 = value;
						teamBattleTime(hell_dungeon_time_list2, value);
					}

					else if (key.equalsIgnoreCase("dete_dungeon_time2")) {
						dete_dungeon_time2 = value;
						teamBattleTime(dete_dungeon_time_list2, value);
					}

					else if (key.equalsIgnoreCase("ice_dungeon_time2")) {
						ice_dungeon_time2 = value;
						teamBattleTime(ice_dungeon_time_list2, value);
					}

					else if (key.equalsIgnoreCase("wg_dungeon_time2")) {
						wg_dungeon_time2 = value;
						teamBattleTime(wg_dungeon_time_list2, value);
					}

					else if (key.equalsIgnoreCase("Treasuress_level"))
						Treasuress_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("Treasuress_dungeon_time")) {
						Treasuress_dungeon_time = value;
						teamBattleTime(Treasuress_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("Treasuress_play_time"))
						Treasuress_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("phunt_level"))
						phunt_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("phunt_wanted"))
						phunt_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("phunt_clan"))
						phunt_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("phunt_dungeon_time")) {
						phunt_dungeon_time = value;
						teamBattleTime(phunt_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("phunt_play_time"))
						phunt_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("dete_level"))
						dete_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dete_wanted"))
						dete_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("dete_clan"))
						dete_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("dete_dungeon_time")) {
						dete_dungeon_time = value;
						teamBattleTime(dete_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("dete_play_time"))
						dete_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("world_level"))
						world_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("world_wanted"))
						world_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("world_clan"))
						world_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("world_dungeon_time")) {
						world_dungeon_time = value;
						teamBattleTime(world_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("world_play_time"))
						world_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("ice_level"))
						ice_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("ice_wanted"))
						ice_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("ice_clan"))
						ice_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("ice_dungeon_time")) {
						ice_dungeon_time = value;
						teamBattleTime(ice_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("ice_play_time"))
						ice_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("wdr_dungeon_time")) {
						wdr_dungeon_time = value;
						teamBattleTime(wdr_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("wdr_play_time"))
						wdr_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("wg_level"))
						wg_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wg_wanted"))
						wg_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("wg_clan"))
						wg_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("wg_dungeon_time")) {
						wg_dungeon_time = value;
						teamBattleTime(wg_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("wg_play_time"))
						wg_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("devil_level"))
						devil_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("devil_wanted"))
						devil_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("devil_clan"))
						devil_clan = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("devil_dungeon_time")) {
						devil_dungeon_time = value;
						teamBattleTime(devil_dungeon_time_list, value);
					} else if (key.equalsIgnoreCase("devil_play_time"))
						devil_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("wh_level"))
						wh_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("wh_wanted"))
						wh_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("wh_clan"))
						wh_clan = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("gr_level"))
						gr_level = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("gr_wanted"))
						gr_wanted = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("gr_clan"))
						gr_clan = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("time_event_time")) {
						time_event_time = value;
						teamBattleTime(time_event_time_list, value);
					} else if (key.equalsIgnoreCase("time_event_play_time"))
						time_event_play_time = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("time_ment"))
						time_ment = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("item_check_name"))
						item_check_name = value;
					else if (key.equalsIgnoreCase("item_check_count"))
						item_check_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("is_item_drop_msg_name"))
						is_item_drop_msg_name = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_item_drop_msg_en"))
						is_item_drop_msg_en = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_item_drop_msg_monster"))
						is_item_drop_msg_monster = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_item_drop_msg_item"))
						is_item_drop_msg_item = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_item_drop_msg_doll"))
						is_item_drop_msg_doll = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_item_drop_msg_life"))
						is_item_drop_msg_life = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_item_drop_msg_create"))
						is_item_drop_msg_create = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("is_character_marble_inventory"))
						is_character_marble_inventory = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_character_marble_pc_shop"))
						is_character_marble_pc_shop = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_character_marble_name"))
						is_character_marble_name = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("exp_marble_percent"))
						exp_marble_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("exp_marble_use_count"))
						exp_marble_use_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("exp_marble_save_count"))
						exp_marble_save_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("exp_marble_time")) {
						exp_marble_time = value;
						teamBattleTime(exp_marble_time_list, value);
					}

					else if (key.equalsIgnoreCase("only_clan_boss_class_royal"))
						only_clan_boss_class_royal = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("recovery_weapon_safe_0_en_min"))
						recovery_weapon_safe_0_en_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("recovery_weapon_safe_6_en_min"))
						recovery_weapon_safe_6_en_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("recovery_armor_safe_0_en_min"))
						recovery_armor_safe_0_en_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("recovery_armor_safe_4_en_min"))
						recovery_armor_safe_4_en_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("recovery_armor_safe_6_en_min"))
						recovery_armor_safe_6_en_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("recovery_acc_en_min"))
						recovery_acc_en_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("recovery_time"))
						recovery_time = Integer.valueOf(value) * 1000;
					else if (key.equalsIgnoreCase("is_recovery_scroll"))
						is_recovery_scroll = value.equalsIgnoreCase("true");

					else if (key.equalsIgnoreCase("weapon_safe_0_0_recovery_item"))
						weapon_safe_0_0_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_0_recovery_item_count"))
						weapon_safe_0_0_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_1_recovery_item"))
						weapon_safe_0_1_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_1_recovery_item_count"))
						weapon_safe_0_1_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_2_recovery_item"))
						weapon_safe_0_2_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_2_recovery_item_count"))
						weapon_safe_0_2_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_3_recovery_item"))
						weapon_safe_0_3_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_3_recovery_item_count"))
						weapon_safe_0_3_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_4_recovery_item"))
						weapon_safe_0_4_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_4_recovery_item_count"))
						weapon_safe_0_4_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_5_recovery_item"))
						weapon_safe_0_5_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_5_recovery_item_count"))
						weapon_safe_0_5_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_6_recovery_item"))
						weapon_safe_0_6_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_6_recovery_item_count"))
						weapon_safe_0_6_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_7_recovery_item"))
						weapon_safe_0_7_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_7_recovery_item_count"))
						weapon_safe_0_7_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_8_recovery_item"))
						weapon_safe_0_8_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_8_recovery_item_count"))
						weapon_safe_0_8_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_9_recovery_item"))
						weapon_safe_0_9_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_9_recovery_item_count"))
						weapon_safe_0_9_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_0_10_recovery_item"))
						weapon_safe_0_10_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_0_10_recovery_item_count"))
						weapon_safe_0_10_recovery_item_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("weapon_safe_6_6_recovery_item"))
						weapon_safe_6_6_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_6_recovery_item_count"))
						weapon_safe_6_6_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_7_recovery_item"))
						weapon_safe_6_7_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_7_recovery_item_count"))
						weapon_safe_6_7_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_8_recovery_item"))
						weapon_safe_6_8_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_8_recovery_item_count"))
						weapon_safe_6_8_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_9_recovery_item"))
						weapon_safe_6_9_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_9_recovery_item_count"))
						weapon_safe_6_9_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_10_recovery_item"))
						weapon_safe_6_10_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_10_recovery_item_count"))
						weapon_safe_6_10_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_11_recovery_item"))
						weapon_safe_6_11_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_11_recovery_item_count"))
						weapon_safe_6_11_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_12_recovery_item"))
						weapon_safe_6_12_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_12_recovery_item_count"))
						weapon_safe_6_12_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_13_recovery_item"))
						weapon_safe_6_13_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_13_recovery_item_count"))
						weapon_safe_6_13_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_14_recovery_item"))
						weapon_safe_6_14_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_14_recovery_item_count"))
						weapon_safe_6_14_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("weapon_safe_6_15_recovery_item"))
						weapon_safe_6_15_recovery_item = value;
					else if (key.equalsIgnoreCase("weapon_safe_6_15_recovery_item_count"))
						weapon_safe_6_15_recovery_item_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("armor_safe_0_0_recovery_item"))
						armor_safe_0_0_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_0_recovery_item_count"))
						armor_safe_0_0_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_1_recovery_item"))
						armor_safe_0_1_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_1_recovery_item_count"))
						armor_safe_0_1_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_2_recovery_item"))
						armor_safe_0_2_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_2_recovery_item_count"))
						armor_safe_0_2_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_3_recovery_item"))
						armor_safe_0_3_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_3_recovery_item_count"))
						armor_safe_0_3_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_4_recovery_item"))
						armor_safe_0_4_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_4_recovery_item_count"))
						armor_safe_0_4_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_5_recovery_item"))
						armor_safe_0_5_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_5_recovery_item_count"))
						armor_safe_0_5_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_6_recovery_item"))
						armor_safe_0_6_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_6_recovery_item_count"))
						armor_safe_0_6_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_7_recovery_item"))
						armor_safe_0_7_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_7_recovery_item_count"))
						armor_safe_0_7_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_8_recovery_item"))
						armor_safe_0_8_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_8_recovery_item_count"))
						armor_safe_0_8_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_9_recovery_item"))
						armor_safe_0_9_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_9_recovery_item_count"))
						armor_safe_0_9_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_0_10_recovery_item"))
						armor_safe_0_10_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_0_10_recovery_item_count"))
						armor_safe_0_10_recovery_item_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("armor_safe_4_4_recovery_item"))
						armor_safe_4_4_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_4_recovery_item_count"))
						armor_safe_4_4_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_5_recovery_item"))
						armor_safe_4_5_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_5_recovery_item_count"))
						armor_safe_4_5_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_6_recovery_item"))
						armor_safe_4_6_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_6_recovery_item_count"))
						armor_safe_4_6_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_7_recovery_item"))
						armor_safe_4_7_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_7_recovery_item_count"))
						armor_safe_4_7_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_8_recovery_item"))
						armor_safe_4_8_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_8_recovery_item_count"))
						armor_safe_4_8_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_9_recovery_item"))
						armor_safe_4_9_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_9_recovery_item_count"))
						armor_safe_4_9_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_10_recovery_item"))
						armor_safe_4_10_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_10_recovery_item_count"))
						armor_safe_4_10_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_11_recovery_item"))
						armor_safe_4_11_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_11_recovery_item_count"))
						armor_safe_4_11_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_4_12_recovery_item"))
						armor_safe_4_12_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_4_12_recovery_item_count"))
						armor_safe_4_12_recovery_item_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("armor_safe_6_6_recovery_item"))
						armor_safe_6_6_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_6_recovery_item_count"))
						armor_safe_6_6_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_6_7_recovery_item"))
						armor_safe_6_7_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_7_recovery_item_count"))
						armor_safe_6_7_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_6_8_recovery_item"))
						armor_safe_6_8_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_8_recovery_item_count"))
						armor_safe_6_8_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_6_9_recovery_item"))
						armor_safe_6_9_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_9_recovery_item_count"))
						armor_safe_6_9_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_6_10_recovery_item"))
						armor_safe_6_10_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_10_recovery_item_count"))
						armor_safe_6_10_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_6_11_recovery_item"))
						armor_safe_6_11_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_11_recovery_item_count"))
						armor_safe_6_11_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("armor_safe_6_12_recovery_item"))
						armor_safe_6_12_recovery_item = value;
					else if (key.equalsIgnoreCase("armor_safe_6_12_recovery_item_count"))
						armor_safe_6_12_recovery_item_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("acc_0_recovery_item"))
						acc_0_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_0_recovery_item_count"))
						acc_0_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_1_recovery_item"))
						acc_1_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_1_recovery_item_count"))
						acc_1_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_2_recovery_item"))
						acc_2_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_2_recovery_item_count"))
						acc_2_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_3_recovery_item"))
						acc_3_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_3_recovery_item_count"))
						acc_3_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_4_recovery_item"))
						acc_4_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_4_recovery_item_count"))
						acc_4_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_5_recovery_item"))
						acc_5_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_5_recovery_item_count"))
						acc_5_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_6_recovery_item"))
						acc_6_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_6_recovery_item_count"))
						acc_6_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_7_recovery_item"))
						acc_7_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_7_recovery_item_count"))
						acc_7_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_8_recovery_item"))
						acc_8_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_8_recovery_item_count"))
						acc_8_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_9_recovery_item"))
						acc_9_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_9_recovery_item_count"))
						acc_9_recovery_item_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("acc_10_recovery_item"))
						acc_10_recovery_item = value;
					else if (key.equalsIgnoreCase("acc_10_recovery_item_count"))
						acc_10_recovery_item_count = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("shop_no_tax_npc"))
						shop_no_tax_npc = value;

					else if (key.equalsIgnoreCase("is_world_open_wait_item"))
						is_world_open_wait_item = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("world_open_wait_item"))
						world_open_wait_item = value;
					else if (key.equalsIgnoreCase("world_open_wait_item_min"))
						world_open_wait_item_min = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("world_open_wait_item_max"))
						world_open_wait_item_max = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("world_open_wait_item_delay"))
						world_open_wait_item_delay = Integer.valueOf(value) * 1000;

					else if (key.equalsIgnoreCase("is_clan_point"))
						is_clan_point = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("clan_point_monster"))
						clan_point_monster = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("clan_point_boss_monster"))
						clan_point_boss_monster = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("clan_point_map_list"))
						kingdomDay(clan_point_map_list, value);

					else if (key.equalsIgnoreCase("is_auto_hunt"))
						is_auto_hunt = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_hunt_pvp"))
						is_auto_hunt_pvp = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_hunt_member"))
						is_auto_hunt_member = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_hunt_exp_percent"))
						is_auto_hunt_exp_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("is_auto_hunt_item_drop_percent"))
						is_auto_hunt_item_drop_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("is_auto_hunt_aden_drop_percent"))
						is_auto_hunt_aden_drop_percent = Double.valueOf(value) * 0.01;
					else if (key.equalsIgnoreCase("is_auto_hunt_time"))
						is_auto_hunt_time = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_hunt_time_account"))
						is_auto_hunt_time_account = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_hunt_time"))
						auto_hunt_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_hunt_time2"))
						auto_hunt_time2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_hunt_reset_time"))
						teamBattleTime(auto_hunt_reset_time, value);
					else if (key.equalsIgnoreCase("auto_hunt_map_list"))
						kingdomDay(auto_hunt_map_list, value);
					else if (key.equalsIgnoreCase("auto_hunt_go_min_hp"))
						auto_hunt_go_min_hp = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_hunt_home_hp_list"))
						kingdomDay(auto_hunt_home_hp_list, value);
					else if (key.equalsIgnoreCase("is_auot_buff"))
						is_auot_buff = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_buff_delay"))
						auto_buff_delay = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_hunt_potion_hp_list"))
						kingdomDay(auto_hunt_potion_hp_list, value);
					//자동사냥 스킬
					else if (key.equalsIgnoreCase("auto_hunt_mp_list"))
						kingdomDay(auto_hunt_mp_list, value);
					else if (key.equalsIgnoreCase("auto_hunt_mp_list2"))
						kingdomDay(auto_hunt_mp_list2, value);
					else if (key.equalsIgnoreCase("autohunt_html_dialog_mode"))
						autohunt_html_dialog_mode = value.trim();
					else if (key.equalsIgnoreCase("autohunt_showhtml_extended"))
						autohunt_showhtml_extended = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("autohunt_html_use_button"))
						autohunt_html_use_button = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("autohunt_use_client_htm"))
						autohunt_use_client_htm = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("autohunt_html_paginate"))
						autohunt_html_paginate = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("w_map_list"))
						kingdomDay(w_map_list, value);
					
					else if (key.equalsIgnoreCase("is_auto_potion_buy"))
						is_auto_potion_buy = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_potion_buy_npc"))
						auto_potion_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_potion_buy_min_count"))
						auto_potion_buy_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_potion_buy_count"))
						auto_potion_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_poly_rank"))
						is_auto_poly_rank = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_poly_rank_buy"))
						is_auto_poly_rank_buy = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_poly_rank_item_name"))
						auto_poly_rank_item_name = value;
					else if (key.equalsIgnoreCase("auto_poly_rank_buy_npc"))
						auto_poly_rank_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_poly_rank_buy_min_count"))
						auto_poly_rank_buy_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_poly_rank_buy_count"))
						auto_poly_rank_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_poly"))
						is_auto_poly = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_poly_buy"))
						is_auto_poly_buy = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_poly_item_name"))
						auto_poly_item_name = value;
					else if (key.equalsIgnoreCase("auto_poly_buy_npc"))
						auto_poly_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_poly_buy_min_count"))
						auto_poly_buy_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_poly_buy_count"))
						auto_poly_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_teleport"))
						is_auto_teleport = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_teleport_item_name"))
						auto_teleport_item_name = value;
					else if (key.equalsIgnoreCase("auto_teleport_buy_npc"))
						auto_teleport_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_teleport_buy_min_count"))
						auto_teleport_buy_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_teleport_buy_count"))
						auto_teleport_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_hunt_teleport_map_list"))
						kingdomDay(auto_hunt_teleport_map_list, value);
					else if (key.equalsIgnoreCase("is_auto_haste"))
						is_auto_haste = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_haste_buy"))
						is_auto_haste_buy = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_haste_item_name"))
						auto_haste_item_name = value;
					else if (key.equalsIgnoreCase("auto_haste_buy_npc"))
						auto_haste_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_haste_buy_min_count"))
						auto_haste_buy_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_haste_buy_count"))
						auto_haste_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_bravery"))
						is_auto_bravery = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_bravery_buy"))
						is_auto_bravery_buy = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_bravery_item_name_royal"))
						auto_bravery_item_name_royal = value;
					else if (key.equalsIgnoreCase("auto_bravery_item_name_knight"))
						auto_bravery_item_name_knight = value;
					else if (key.equalsIgnoreCase("auto_bravery_item_name_elf"))
						auto_bravery_item_name_elf = value;
					else if (key.equalsIgnoreCase("is_auto_bravery_wizard_magic"))
						is_auto_bravery_wizard_magic = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_bravery_item_name_wizard"))
						auto_bravery_item_name_wizard = value;
					else if (key.equalsIgnoreCase("auto_bravery_buy_npc"))
						auto_bravery_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_bravery_buy_min_count"))
						auto_bravery_buy_min_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_bravery_buy_count"))
						auto_bravery_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_arrow_buy"))
						is_auto_arrow_buy = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("auto_arrow_item_name"))
						auto_arrow_item_name = value;
					else if (key.equalsIgnoreCase("auto_arrow_buy_npc"))
						auto_arrow_buy_npc = value;
					else if (key.equalsIgnoreCase("auto_arrow_buy_count"))
						auto_arrow_buy_count = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("is_auto_hunt_skill"))
						is_auto_hunt_skill = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("is_auto_hunt_skill_percent"))
						is_auto_hunt_skill_percent = Double.valueOf(value);
					else if (key.equalsIgnoreCase("auto_hunt_telpeport_delay"))
						auto_hunt_telpeport_delay = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("user_ghost"))
						user_ghost = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("user_ghost_time"))
						user_ghost_time = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("auto_level"))
						auto_level = Integer.valueOf(value);

					else if (key.equalsIgnoreCase("danlevel"))
						danlevel = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dan1"))
						dan1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dan2"))
						dan2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dan3"))
						dan3 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dan4"))
						dan4 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("dan5"))
						dan5 = Integer.valueOf(value);
					
					else if (key.equalsIgnoreCase("runup0"))
						runup0 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup1"))
						runup1 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup2"))
						runup2 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup3"))
						runup3 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup4"))
						runup4 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup5"))
						runup5 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup6"))
						runup6 = Integer.valueOf(value);
					else if (key.equalsIgnoreCase("runup7"))
						runup7 = Integer.valueOf(value);
				
				
					else if (key.equalsIgnoreCase("speed_check_no_dir_magic_frame_rate"))
						speed_check_no_dir_magic_frame_rate = Double.valueOf(value);
					else if (key.equalsIgnoreCase("speed_check_dir_magic_frame_rate"))
						speed_check_dir_magic_frame_rate = Double.valueOf(value);
					
					else if (key.equalsIgnoreCase("bandel_bug"))
						bandel_bug = value.equalsIgnoreCase("true");
					else if (key.equalsIgnoreCase("bandel_bug_check_time"))
						bandel_bug_check_time = Integer.valueOf(value);
		
				}
			}
			lnrr.close();

			for (BuffNpc npc : buffNpcList)
				npc.reloadTitle(false);

			BackgroundDatabase.reloadSpawnBattleZone();
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Lineage.class.toString());
			lineage.share.System.println(String.format("에러 라인 -> [%s]", line == null ? "라인 없음" : line));
			lineage.share.System.println(e);
		}

		// 315 이상부터 해상도가 변경되기때문에
		if (server_version > 310) {
			SEARCH_LOCATIONRANGE = 16;
			SEARCH_MONSTER_TARGET_LOCATION = 21;
		}

		TimeLine.end();
	}

	/**
	 * 순차적으로 배열된 클레스값을 제곱순에 클레스값으로 리턴하는 함수.
	 * 
	 * @param class_type
	 * @return
	 */
	static public int getClassType(int class_type) {
		switch (class_type) {
		case LINEAGE_CLASS_ROYAL:
			return LINEAGE_ROYAL;
		case LINEAGE_CLASS_KNIGHT:
			return LINEAGE_KNIGHT;
		case LINEAGE_CLASS_ELF:
			return LINEAGE_ELF;
		case LINEAGE_CLASS_WIZARD:
			return LINEAGE_WIZARD;
		case LINEAGE_CLASS_DARKELF:
			return LINEAGE_DARKELF;
		case LINEAGE_CLASS_DRAGONKNIGHT:
			return 0;
		case LINEAGE_CLASS_BLACKWIZARD:
			return 0;
		}
		return 0;
	}

	static private void toFirstSpell(List<FirstSpell> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					int uid = Integer.valueOf(st.nextToken());
					if (uid > 0)
						list.add(new FirstSpell(uid));
				} catch (Exception e) {
					lineage.share.System.printf("%s : toFirstSpell(List<FirstSpell> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	static private void toFirstInventory(List<FirstInventory> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String db = st.nextToken().trim();
					int f_pos = db.indexOf("(");
					int e_pos = db.indexOf(")");
					String name = db.substring(0, f_pos).trim();
					int count = Integer.valueOf(db.substring(f_pos + 1, e_pos).trim());
					list.add(new FirstInventory(name, count));
				} catch (Exception e) {
					lineage.share.System.printf("%s : toFirstInventory(List<FirstInventory> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	static private void toFirstSpawn(List<FirstSpawn> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String db = st.nextToken().trim();
					String x = db.substring(0, db.indexOf(" ")).trim();
					String y = db.substring(x.length() + 1, db.indexOf(" ", x.length() + 1)).trim();
					String map = db.substring(x.length() + y.length() + 2).trim();
					list.add(new FirstSpawn(Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(map)));
				} catch (Exception e) {
					lineage.share.System.printf("%s : toFirstSpawn(List<FirstSpawn> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	/**
	 * 아이템 제거 막대로 삭제 불가능한 아이템 목록 2017-10-24 by all-night
	 */
	static private void isRemoveItem(List<String> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String item = st.nextToken().trim();
					list.add(item);
				} catch (Exception e) {
					lineage.share.System.printf("%s : isRemoveItem(List<String> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}
	static private void isSetItem(List<String> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String item = st.nextToken().trim();
					list.add(item);
				} catch (Exception e) {
					lineage.share.System.printf("%s : isSetItem(List<String> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}
	/**
	 * 공성전 보상 아이템 이름 리스트 2017-10-24 by all-night
	 */
	static private void kingdomWarWinItemList(List<String> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String item = st.nextToken().trim();
					list.add(item);
				} catch (Exception e) {
					lineage.share.System.printf("%s : kingdomWarWinItemList(List<String> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	/**
	 * 팀대전 시간 2017-10-24 by all-night
	 */
	static private void teamBattleTime(List<TeamBattleTime> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String time = st.nextToken().trim();
					String hour = time.substring(0, time.indexOf(":")).trim();
					String min = time.substring(hour.length() + 1).trim();
					list.add(new TeamBattleTime(Integer.valueOf(hour), Integer.valueOf(min)));
				} catch (Exception e) {
					lineage.share.System.printf("%s : teamBattleTime(List<TeamBattleTime> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	static private void kingdomDay(List<Integer> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String day = st.nextToken().trim();
					list.add(Integer.valueOf(day));
				} catch (Exception e) {
					lineage.share.System.printf("%s : kingdomDay(List<Integer> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	static public List<Integer> getGiranKingdomWarDayList() {
		synchronized (giran_kingdom_war_day_list) {
			return new ArrayList<Integer>(giran_kingdom_war_day_list);
		}
	}

	/**
	 * 공성중 소모되지 않는 아이템 리스트 2018-08-04 by connector12@nate.com
	 */
	static private void isKingdomWarNoRemoveItem(List<String> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String item = st.nextToken().trim();
					list.add(item);
				} catch (Exception e) {
					lineage.share.System.printf("%s : isKingdomWarNoRemoveItem(List<String> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	/**
	 * 복수 시스템 맵리스트 2020-08-03 by connector12@nate.com
	 */
	static void stringToInt(List<Integer> list, String value) {
		if (value.length() > 0) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				try {
					String number = st.nextToken().trim();
					list.add(Integer.valueOf(number));
				} catch (Exception e) {
					lineage.share.System.printf("%s : stringToInt(List<Integer> list, String value)\r\n", Lineage.class.toString());
					lineage.share.System.println(e);
				}
			}
		}
	}

	/**
	 * 귀환 마을 좌표 2018-08-11 by connector12@nate.com
	 */
	static public int[] getHomeXY() {
		// x, y좌표
		int x = Util.random(33424, 33437);
		int y = Util.random(32801, 32824);
		// 맵번호
		int map = 4;
		int[] loc = { x, y, map };
		int count = 0;

		for (;;) {
			if (count++ > 2) {
				loc[0] = 33430;
				loc[1] = 32817;
				break;
			}

			if ((x >= 33423 && x <= 33425 && y >= 32807 && y <= 32809) || (x >= 33434 && x <= 33436 && y >= 32807 && y <= 32809) || (x >= 33423 && x <= 33425 && y >= 32818 && y <= 32820)
					|| (x >= 33433 && x <= 33435 && y >= 32818 && y <= 32820) || (x == 33430 && y == 32805) || (x == 33429 && y == 32822) || (x == 33430 && y == 32813) || (x == 33424 && y == 32822)) {
				// x, y좌표
				x = Util.random(33424, 33437);
				y = Util.random(32801, 32824);
			} else {
				loc[0] = x;
				loc[1] = y;
				break;
			}
		}
		return loc;
	}
}
