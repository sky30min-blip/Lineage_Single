package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import all_night.Lineage_Balance;
import kubera.AttackController;
import lineage.bean.database.Poly;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Kingdom;
import lineage.bean.lineage.Summon;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.gui.GuiMain;
import lineage.network.LineageServer;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.network.packet.server.S_SkillDelete;
import lineage.network.packet.server.S_SkillList;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MagicDollInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RobotInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.magic.*;
import lineage.world.object.npc.SpotTower;
import lineage.world.object.npc.background.Cracker;
import lineage.world.object.npc.background.Racer;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomDoor;

public final class SkillController {

	// 패킷 전송에 사용됨.
	static private int lv[];
	// 지속형 마법 목록
	static private Map<Character, List<Skill>> list;
	// 마법투구를 통해 익힌 마법
	static private Map<Character, List<Skill>> temp_list;

	static public void init() {
		TimeLine.start("SkillController..");

		temp_list = new HashMap<Character, List<Skill>>();
		list = new HashMap<Character, List<Skill>>();
		lv = new int[28];

		TimeLine.end();
	}

	/**
	 * 스킬 관리가 필요할때 호출해서 등록해주는 함수. : 사용자는 월드 접속햇을때 : 몬스터는 객체 생성될때
	 * 
	 * @param cha
	 */
	static public void toWorldJoin(Character cha) {
		synchronized (list) {
			if (list.get(cha) == null)
				list.put(cha, new ArrayList<Skill>());
		}
		synchronized (temp_list) {
			if (temp_list.get(cha) == null)
				temp_list.put(cha, new ArrayList<Skill>());
		}
	}

	/**
	 * 스킬 관리목록에서 제거처리하는 함수.
	 * 
	 * @param cha
	 */
	static public void toWorldOut(Character cha) {
		List<Skill> skill_list = find(cha);
		if (skill_list != null)
			skill_list.clear();
		skill_list = null;
		synchronized (list) {
			list.remove(cha);
		}
		List<Skill> skill_temp_list = findTemp(cha);
		if (skill_temp_list != null)
			skill_temp_list.clear();
		skill_temp_list = null;
		synchronized (temp_list) {
			temp_list.remove(cha);
		}
	}

	/**
	 * 마법 딜레이 확인하는 함수. : 딜레이 확인한후 마법 시전해도 되는지 리턴함.
	 * 
	 * @param cha
	 * @param skill
	 * @return
	 */
	static public boolean isDelay(Character cha, Skill skill) {
		PcInstance pc = (PcInstance) cha;
		// 투망상태 해제
		Detection.onBuff(cha);

		// 딜레이 확인.
		long time = System.currentTimeMillis();

		// cha.lastMagicActionTime = time + Lineage.attackAndMagic_delay;

		if (Lineage.bandel_bug && cha instanceof PcInstance) {
			long currentTime = System.currentTimeMillis();
			long timeDifference = currentTime - cha.delay_bandel;

			if (timeDifference < Lineage.bandel_bug_check_time) {
				return false;
			}

			cha.delay_bandel = currentTime;
		}

		if ((cha.delay_magic == 0 || cha.delay_magic <= time)) {
			cha.delay_magic = time + skill.getDelay();
			pc.setCurrentSkillId(skill.getSkillNumber());

			if (cha instanceof PcInstance) {
				if (!AttackController.isMagicTime((PcInstance) cha, AttackController.getSkillMotion(skill)))
					return false;
			}
			return true;
		}

		// 실패.
		ChattingController.toChatting(cha, "\\fY마법 재사용시간이 남았습니다.", Lineage.CHATTING_MODE_MESSAGE);
		cha.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
		return false;

	}

	/**
	 * 공격마법 액션인지 확인하는 함수
	 * 
	 * @param
	 * @return 2017-09-05 by all_night.
	 */
	static public boolean isAttackMagicAction(int uid) {
		switch (uid) {
		case 4: // 에너지 볼트
		case 6: // 아이스 대거
		case 7: // 윈드 커터
		case 10: // 칠 터치
		case 15: // 파이어 애로우
		case 17: // 라이트닝
		case 22: // 프로즌 클라우드
		case 25: // 파이어볼
		case 28: // 뱀파이어릭 터치
		case 30: // 어스 재일
		case 34: // 콜 라이트닝
		case 38: // 콘 오브 콜드
		case 45: // 이럽션
		case 46: // 선 버스트
		case 50: // 아이스 랜스
		case 53: // 토네이도
		case 59: // 블리자드
		case 62: // 어스 퀘이크
		case 70: // 파이어 스톰
		case 74: // 미티어 스트라이크
		case 77: // 디스인티그레이트
		case 115: // 트리플 애로우
			return true;
		}

		return false;
	}

	/**
	 * 스킬 사용 요청 처리 함수. 무기에서 마법이 발동될때 처리하는 함수.
	 * 
	 * @param cha
	 * @param o
	 * @param skill
	 * @return
	 */
	static public void toSkill(Character cha, object o, Skill skill, boolean target) {
		if (skill == null)
			return;

		switch (skill.getUid()) {
		case 500:
		case 501:
		case 502:
			EnergyBolt.toBuff(cha, target ? o : cha, skill, cha.getGfxMode() + Lineage.GFX_MODE_ATTACK, skill.getCastGfx(), 0);
			break;
		}
	}

	/**
	 * 스킬 사용 요청 처리 함수.
	 * 
	 * @param cha
	 * @param level
	 * @param number
	 */
	static public void toSkill(Character cha, final ClientBasePacket cbp) {
		int level = cbp.readC() + 1;
		int number = cbp.readC();

		// 플러그인 확인.
		Object p = PluginController.init(SkillController.class, "toSkill", cha, cbp, level, number);
		if (p != null)
			return;

		// 자동사냥 방지 확인.
		if (Lineage.is_auto_hunt_check_skill && cha instanceof PcInstance && !AutoHuntCheckController.checkCount((PcInstance) cha))
			return;

		// 콜로세움에도 적용됫을지 확인해봐야할듯
		Skill skill = find(cha, level, number);

		if (skill != null && isDelay(cha, skill)) {
			switch (skill.getSkillLevel()) {
			case 1:
				switch (skill.getSkillNumber()) {
				case 0:
					Heal.init(cha, skill, cbp.readD());
					break;
				case 1:
					Light.init(cha, skill);
					break;
				case 2:
					Shield.init(cha, skill);
					break;
				case 3:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 4:
					Teleport.init(cha, skill, cbp);
					break;
				case 5:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 6:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 7:
					HolyWeapon.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 2:
				switch (skill.getSkillNumber()) {
				case 0:
					CurePoison.init(cha, skill, cbp.readD());
					break;
				case 1:
					ChillTouch.init(cha, skill, cbp.readD());
					break;
				case 2:
					CursePoison.init(cha, skill, cbp.readD());
					break;
				case 3:
					EnchantWeapon.init(cha, skill, cbp.readD());
					break;
				case 4:
					Detection.init(cha, skill);
					break;
				case 5:
					DecreaseWeight.init(cha, skill);
					break;
				case 6:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 7:
					ShockStun.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 3:
				switch (skill.getSkillNumber()) {
				case 0:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 1:
					TurnUndead.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 2:
					Heal.init(cha, skill, cbp.readD());
					break;
				case 3:
					CurseBlind.init(cha, skill, cbp.readD());
					break;
				case 4:
					BlessedArmor.init(cha, skill, cbp.readD());
					break;
				case 5:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 6:
					RevealWeakness.init(cha, skill, cbp.readD());
				case 7:
					//Berserks.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 4:
				switch (skill.getSkillNumber()) {
				case 0:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 1:
					EnchantDexterity.init(cha, skill, cbp.readD());
					break;
				case 2:
					WeaponBreak.init(cha, skill, cbp.readD());
					break;
				case 3:
					ChillTouch.init(cha, skill, cbp.readD());
					break;
				case 4:
					Slow.init(cha, skill, cbp.readD(), true);
					break;
				case 5:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 6:
					CounterMagic.init(cha, skill);
					break;
				case 7:
					Meditation.init(cha, skill);
					break;
				}
				break;
			case 5:
				switch (skill.getSkillNumber()) {
				case 0:
					CurseParalyze.init(cha, skill, cbp.readD());
					break;
				case 1:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 2:
					GreaterHeal.init(cha, skill, cbp.readD());
					break;
				case 3:
					TameMonster.init(cha, skill, cbp.readD());
					break;
				case 4:
					RemoveCurse.init(cha, skill, cbp.readD());
					break;
				case 5:
					ConeOfCold.init(cha, skill, cbp.readD());
					break;
				case 6:
					ManaDrain.init(cha, skill, cbp.readD());
					break;
				case 7:
					CurseBlind.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 6:
				switch (skill.getSkillNumber()) {
				case 0:
					CreateZombie.init(cha, skill, cbp.readD());
					break;
				case 1:
					EnchantMighty.init(cha, skill, cbp.readD());
					break;
				case 2:
					Haste.init(cha, skill, cbp.readD());
					break;
				case 3:
					Cancellation.init(cha, skill, cbp.readD());
					break;
				case 4:
					Eruption.init(cha, skill, cbp.readD());
					break;
				case 5:
					Sunburst.init(cha, skill, cbp.readD());
					break;
				case 6:
					Weakness.init(cha, skill, cbp.readD());
					break;
				case 7:
					BlessWeapon.init(cha, skill, cbp.readD(), true, true);
					break;
				}
				break;
			case 7:
				switch (skill.getSkillNumber()) {
				case 0:
					HealAll.init(cha, skill);
					break;
				case 1:
					IceLance.init(cha, skill, cbp.readD());
					break;
				case 2:
					SummonMonster.init(cha, skill, cbp.isRead(2) ? cbp.readH() : 0);
					break;
				case 3:
					HolyWalk.init(cha, skill);
					break;
				case 4:
					Tornado.init(cha, skill);
					break;
				case 5:
					GreaterHaste.init(cha, skill, cha.getObjectId());
					break;
				case 6:
					ReductionArmor.init(cha, skill);
					break;
				case 7:
					Disease.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 8:
				switch (skill.getSkillNumber()) {
				case 0:
					FullHeal.init(cha, skill, cbp.readD());
					break;
				case 1:
					Firewall.init(cha, skill, cbp.readH(), cbp.readH());
					break;
				case 2:
					Blizzard.init(cha, skill);
					break;
				case 3:
					InvisiBility.init(cha, skill);
					break;
				case 4:
					Resurrection.init(cha, skill, cbp.readD());
					break;
				case 5:
					Tornado.init(cha, skill);
					break;
				case 6:
					// LifeStream.init(cha, skill);
					BounceAttack.init(cha, skill);
					break;
				case 7:
					Silence.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 9:
				switch (skill.getSkillNumber()) {
				case 0:
					// 자신에게 시전되는 마법

					break;
				case 1:
					if (Lineage.server_version >= 250)
						FogOfSleeping.init(cha, skill, cbp.readD());
					else
						FogOfSleeping.init(cha, skill, cbp.readH(), cbp.readH());
					break;
				case 2:
					// 세이프 체인지
					// ShapeChange.init(cha, skill, cbp.readD());
					// 어드밴스 스피릿
					AdvanceSpirit.init(cha, skill, cbp.readD());
					break;
				case 3:
					ImmuneToHarm.init(cha, skill, cbp.readD());
					break;
				case 4:
					MassTeleport.init(cha, skill, cbp);
					break;
				case 5:
					Tornado.init(cha, skill);
					break;
				case 6:
					DecayPotion.init(cha, skill, cbp.readD());
					break;
				case 7:
					SolidCarriage.init(cha, skill);
					break;
				}
				break;
			case 10:
				switch (skill.getSkillNumber()) {
				case 0:
					CreateMagicalWeapon.init(cha, skill, cbp.readD());
					break;
				case 1:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 2:

					break;
				case 3:
					MassSlow.init(cha, skill, cbp.readD());
					break;
				case 4:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 5:
					AbsoluteBarrier.init(cha, skill);
					break;
				case 6:
					// 사용불가
					break;
				case 7:
					CounterBarrier.init(cha, skill);
					break;
				}
				break;
			case 11:
				switch (skill.getSkillNumber()) {
				case 6:

					break;
				case 7: // 리덕션아머
					ReductionArmor.init(cha, skill);
					break;
				}
				break;
			case 12:
				switch (skill.getSkillNumber()) {
				case 0: // 바운스 어택
					BounceAttack.init(cha, skill);
					break;
				case 1: // 솔리드 캐리지
					SolidCarriage.init(cha, skill);
					break;
				case 2: // 카운터 배리어
					CounterBarrier.init(cha, skill);
					break;
				}
				break;
			case 13:
				switch (skill.getSkillNumber()) {
				case 0:
					InvisiBility.init(cha, skill);
					break;
				case 1:
					EnchantVenom.init(cha, skill);
					break;
				case 2:
					ShadowArmor.init(cha, skill);
					break;
				case 3:
					PurifyStone.init(cha, skill, cbp.readD());
					break;
				case 4:
					HolyWalk.init(cha, skill);
					break;
				case 5:
					BurningSpirit.init(cha, skill);
					break;
				case 6:
					CurseBlind.init(cha, skill, cbp.readD());
					break;
				case 7:
					VenomResist.init(cha, skill);
					break;
				}
				break;
			case 14:
				switch (skill.getSkillNumber()) {
				case 0:
					DoubleBreak.init(cha, skill);
					break;
				case 1:
					UncannyDodge.init(cha, skill);
					break;
				case 2:
					ShadowFang.init(cha, skill, cbp.readD());
					break;
				case 3:
					FinalBurn.init(cha, skill, cbp.readD());
					break;
				case 4:
					DressMighty.init(cha, skill);
					break;
				case 5:
					DressDexterity.init(cha, skill);
					break;
				case 6:
					DressEvasion.init(cha, skill);
					break;
				}
				break;
			case 15:
				switch (skill.getSkillNumber()) {
				case 0:
					TrueTarget.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH(), cbp.readS());
					break;
				case 1:
					GlowingWeapon.init(cha, skill);
					break;
				case 2:
					ShiningShield.init(cha, skill);
					break;
				case 3:
					CallClan.init(cha, skill, cbp.readS());
					break;
				case 4:
					BraveMental.init(cha, skill);
					break;
				case 5:
					BraveAvatar.init(cha, skill);
					break;
				case 6:
					break;
				case 7:
					break;
				}
				break;
			case 17:
				switch (skill.getSkillNumber()) {
				case 0:
					ResistMagic.init(cha, skill);
					break;
				case 1:
					BodyToMind.init(cha, skill);
					break;
				case 2:
					TeleportToMother.init(cha, skill);
					break;
				case 3:
					break;
				case 4:
					ElementalFalldown.init(cha, skill, cbp.readD());
					break;
				case 5:
					CounterMirror.init(cha, skill);
					break;
				}
				break;
			case 18:
				switch (skill.getSkillNumber()) {
				case 0:
					ClearMind.init(cha, skill);
					break;
				case 1:
					ResistElemental.init(cha, skill);
					break;
				}
				break;
			case 19:
				switch (skill.getSkillNumber()) {
				case 0:
					TripleArrow.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 1:
					BloodToSoul.init(cha, skill);
					break;
				case 2:
					WindShot.init(cha, skill);
					// EagleEye.init(cha, skill);
					// ProtectionFromElemental.init(cha, skill);
					break;
				case 3:
					AquaProtect.init(cha, skill, cbp.readD());
					break;
				case 4:
					PolluteWater.init(cha, skill, cbp.readD());
					break;
				case 5:
					// ExoticVitalize.init(cha, skill);
					EarthGuardian.init(cha, skill);
					break;
				case 6:
					StrikerGale.init(cha, skill, cbp.readD());
					// EarthSkin.init(cha, skill, cbp.readD());
					break;
				case 7:
					Slow.init(cha, skill, cbp.readD(), false);
					break;
				}
				break;
			case 20:
				switch (skill.getSkillNumber()) {
				case 0:
					EraseMagic.init(cha, skill, cbp.readD());
					break;
				case 1:
					BurningWeapon.init(cha, skill);
					// SummonLesserElemental.init(cha, skill);
					break;
				case 2:
					ElementalFire.init(cha, skill);
					break;
				case 3:
					EyeOfStorm.init(cha, skill);
					break;
				case 4:
					// EarthGuardian.init(cha, skill);
					break;
				case 5:
					NaturesTouch.init(cha, skill, cbp.readD());
					break;
				case 6:
					// EarthGuardian.init(cha, skill);
					ExoticVitalize.init(cha, skill);
					break;
				case 7:
					break;
				}
				break;
			case 21:
				switch (skill.getSkillNumber()) {
				case 0:
					AreaOfSilence.init(cha, skill);
					break;
				case 1:
					// SummonGreaterElemental.init(cha, skill);
					AdditionalFire.init(cha, skill);
					break;
				case 2:
					WaterLife.init(cha, skill, cbp.readD());
					// BurningWeapon.init(cha, skill, Lineage.server_version >=
					// 270 ? cha.getObjectId() : cbp.readD());
					break;
				case 3:
					NaturesBlessing.init(cha, skill);
					break;
				case 4:
					EarthBind.init(cha, skill, cbp.readD());
					break;
				case 5:
					StormShot.init(cha, skill, Lineage.server_version >= 270 ? cha.getObjectId() : cbp.readD());
					break;
				case 6:
					SoulOfFlame.init(cha, skill);
					break;
				case 7:
					IronSkin.init(cha, skill, Lineage.server_version >= 270 ? cha.getObjectId() : cbp.readD());
					break;
				}
				break;
			case 22:
				switch (skill.getSkillNumber()) {
				case 0:
					ExoticVitalize.init(cha, skill);
					break;
				case 1:
					WaterLife.init(cha, skill, cbp.readD());
					break;
				case 2:
					ElementalFire.init(cha, skill);
					break;
				case 4:
					PolluteWater.init(cha, skill, cbp.readD());
					break;
				case 5:
					StrikerGale.init(cha, skill, cbp.readD());
					break;
				case 6:
					SoulOfFlame.init(cha, skill);
					break;
				case 7:
					AdditionalFire.init(cha, skill);
					break;
				}
				break;
			}
		}
	}

	/**
	 * npc로부터 배울수 있는 스킬 갯수 리턴하는 메서드.
	 */
	static public int getBuySkillCount(Character cha) {
		int count = 0;
		int idx = getBuySkillIdx(cha);
		// 최대 배울수 잇는 범위내에 가지고잇는 스킬 갯수 추출
		for (Skill s : find(cha)) {
			if (s.getUid() <= idx)
				count += 1;
		}

		if (idx >= 15)
			count += 1;

		if (idx >= 22)
			count += 1;

		// 최대배울수잇는갯수 - 가지고잇는 갯수 = 남은갯수를 리턴함.
		return idx - count;
	}

	/**
	 * 클레스별로 레벨에 맞춰서 최대 배울수 잇는 갯수 추출.
	 */
	static public int getBuySkillIdx(Character cha) {
		int level = cha.getLevel();
		int gab = 0;
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			if (cha.getLevel() > 20)
				level = 20;
			gab = 10;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			if (cha.getLevel() > 50)
				level = 50;
			gab = 50;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			if (cha.getLevel() > 24)
				level = 24;
			gab = 8;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			if (cha.getLevel() > 12)
				level = 12;
			gab = 4;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			if (cha.getLevel() > 24)
				level = 24;
			gab = 12;
			break;
		}
		return (level / gab) * 8;
	}

	/**
	 * 스킬 제거 처리 함수.
	 * 
	 * @param cha
	 * @param uid
	 * @param temp
	 */
	static public void remove(Character cha, int uid, boolean temp) {
		remove(cha, find(cha, uid, temp), temp);
	}

	/**
	 * 스킬 제거 처리 함수.
	 * 
	 * @param cha
	 * @param level
	 * @param number
	 */
	static public void remove(Character cha, int level, int number) {
		remove(cha, find(cha, level, number, false), false);
	}

	/**
	 * 스킬 제거 처리 함수. : 중복 코드 방지용.
	 * 
	 * @param cha
	 * @param s
	 * @param temp
	 */
	static public void remove(Character cha, Skill s, boolean temp) {
		if (s == null)
			return;
		if (temp)
			findTemp(cha).remove(s);
		else
			find(cha).remove(s);
		// 메모리 초기화
		for (int i = lv.length - 1; i >= 0; --i)
			lv[i] = 0;
		// 가지고있는 스킬이라면 갱신 안하기.
		if (find(cha, s.getUid(), false) == null)
			lv[s.getSkillLevel() - 1] += s.getId();
		// 패킷 전송.
		cha.toSender(S_SkillDelete.clone(BasePacketPooling.getPool(S_SkillDelete.class), lv));
	}

	/**
	 * 스킬 추가 처리 함수.
	 * 
	 * @param cha
	 * @param uid
	 * @param temp
	 */
	static public void append(Character cha, int uid, boolean temp) {
		Skill s = find(cha, uid, temp);
		if (s == null) {
			s = SkillDatabase.find(uid);
			if (s != null) {
				if (temp)
					findTemp(cha).add(s);
				else
					find(cha).add(s);
			}
		}
	}

	/**
	 * 스킬 추가 처리 함수.
	 * 
	 * @param cha
	 * @param level
	 * @param number
	 * @param temp
	 */
	static public void append(Character cha, int level, int number, boolean temp) {
		Skill s = find(cha, level, number, temp);
		if (s == null) {
			s = SkillDatabase.find(level, number);
			if (s != null) {
				if (temp)
					findTemp(cha).add(s);
				else
					find(cha).add(s);
			}
		}
	}

	/**
	 * 연결된 객체 찾아서 리턴.
	 * 
	 * @param pc
	 * @return
	 */
	static public List<Skill> find(Character cha) {
		synchronized (list) {
			return list.get(cha);
		}
	}

	/**
	 * 연결된 객체 찾아서 리턴.
	 * 
	 * @param pc
	 * @return
	 */
	static public List<Skill> findTemp(Character cha) {
		synchronized (temp_list) {
			return temp_list.get(cha);
		}
	}

	/**
	 * 같은 스킬이 존재하는지 확인하는 메서드.
	 */
	static public Skill find(Character cha, int uid) {
		for (Skill s : findTemp(cha)) {
			if (s != null && uid == s.getUid())
				return s;
		}

		for (Skill s : find(cha)) {
			if (s != null && uid == s.getUid())
				return s;
		}

		return null;
	}

	/**
	 * 같은 스킬이 존재하는지 확인하는 메서드.
	 */
	static public Skill find(Character cha, int uid, boolean temp) {
		if (temp) {
			for (Skill s : findTemp(cha)) {
				if (s != null && uid == s.getUid())
					return s;
			}
		} else {
			for (Skill s : find(cha)) {
				if (s != null && uid == s.getUid())
					return s;
			}
		}
		return null;
	}

	/**
	 * 같은 스킬이 존재하는지 확인하는 메서드.
	 */
	static public Skill find(Character cha, int level, int number) {
		for (Skill s : find(cha)) {
			if (s != null && s.getSkillLevel() == level && s.getSkillNumber() == number)
				return s;
		}
		for (Skill s : findTemp(cha)) {
			if (s != null && s.getSkillLevel() == level && s.getSkillNumber() == number)
				return s;
		}
		return null;
	}

	/**
	 * 일치하는 마법 리턴함.
	 */
	static public Skill find(Character cha, int level, int number, boolean temp) {
		if (temp) {
			for (Skill s : findTemp(cha)) {
				if (s != null && s.getSkillLevel() == level && s.getSkillNumber() == number)
					return s;
			}
		} else {
			for (Skill s : find(cha)) {
				if (s != null && s.getSkillLevel() == level && s.getSkillNumber() == number)
					return s;
			}
		}
		return null;
	}

	/**
	 * 습득한 스킬정보 전송하는 함수.
	 */
	static public void sendList(Character cha) {
		// 메모리 초기화
		for (int i = lv.length - 1; i >= 0; --i)
			lv[i] = 0;
		// 실제 습득을 통해 익힌 마법목록
		for (Skill s : find(cha))
			lv[s.getSkillLevel() - 1] += s.getId();
		// 마법투구를 통해 습득한 임시 마법. 실제습득한 마법에 없을경우 그값을 갱신.
		for (Skill s : findTemp(cha)) {
			if (find(cha, s.getUid(), false) == null)
				lv[s.getSkillLevel() - 1] += s.getId();
		}
		// 패킷 전송.
		cha.toSender(S_SkillList.clone(BasePacketPooling.getPool(S_SkillList.class), lv));
	}

	/**
	 * 마법을 시전하기전에 hp, mp, item 및 상태에 따라 마법시전해도 되는지 확인해보는 메서드. : skill 이 null 로
	 * 올수도 잇기 때문에 gamso 값이 true일때만 사용하기.
	 */
	static public boolean isMagic(Character cha, Skill skill, boolean gamso) {

		if (cha.isBuffInvisiBility()) {
			BuffController.remove(cha, InvisiBility.class);
		}

		if (cha.isDead() || cha.isWorldDelete())
			return false;
		if (cha.getMap() == Lineage.teamBattleMap && cha instanceof PcInstance) {
			if (((PcInstance) cha).isTeamBattleDead())
				return false;
		}
		if (cha.isLock())
			return false;
		// 운영자는 무조건 성공.
		if (cha.getGm() > 0)
			return true;
		// 앱솔상태 해제.
		if (cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);
		// 사일런스 상태라면 마법을 사용 할 수 없도록 차단
		if (cha.isBuffSilence())
			return false;
		// 무게 확인
		if (cha instanceof PcInstance && cha.getInventory() != null && cha.getInventory().isWeightPercent(82) == false) {
			// \f1짐이 너무 무거워 마법을 사용할 수 없습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 316));
			return false;
		}
		// 변신상태일경우 마법 시전가능한지 확인.
		if (cha.getGfx() != cha.getClassGfx()) {
			Poly p = PolyDatabase.getPolyGfx(cha.getGfx());
			if (p != null && !p.isSkill()) {
				// \f1그 상태로는 마법을 사용할 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 285));
				return false;
			}
		}

		// 감소 처리를 할경우.
		if (gamso) {
			if (skill == null)
				return false;

			// 처리.
			int hp = skill.getHpConsume();
			int mp = skill.getMpConsume();
			int lawful = cha instanceof MonsterInstance ? 0 : skill.getLawfulConsume();
			ItemInstance item1 = null;

			// 사용자들만 재료 확인.
			if (skill.getItemConsume() > 0 && cha instanceof PcInstance && !(cha instanceof RobotInstance)) {
				item1 = cha.getInventory().findDbNameId(skill.getItemConsume());
				if (item1 == null || item1.getCount() < skill.getItemConsumeCount()) {
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 299));
					return false;
				}
			}

			// int값에따른 mp소모량 감소
			mp -= Math.round(mp * (CharacterController.toStatInt(cha, "mpDecrease") * 0.01));

			// 기사 패널티 적용.
			if (cha.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT) {
				mp = (int) (mp * 0.5);
				hp = (int) (hp * 0.5);
			}
			// hp확인
			if (hp > 0 && cha.getNowHp() <= hp) {
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 279));
				return false;
			}
			// mp확인
			if (mp > 0 && cha.getNowMp() < mp) {
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 278));
				return false;
			}

			if (hp > 0)
				cha.setNowHp(cha.getNowHp() - hp);
			if (mp > 0)
				cha.setNowMp(cha.getNowMp() - mp);
			if (lawful > 0)
				cha.setLawful(cha.getLawful() - lawful);
			if (item1 != null) {
				//
				cha.getInventory().count(item1, item1.getCount() - skill.getItemConsumeCount(), true);
			}
		}
		return true;
	}

	/**
	 * 해당 스킬에 필요한 hp, mp가 있는지 확인해주는 함수.
	 * 
	 * @param cha
	 * @param skill
	 * @return
	 */
	static public boolean isHpMpCheck(Character cha, int hp, int mp) {
		if (hp > 0 && cha.getNowHp() <= hp)
			return false;
		if (mp > 0 && cha.getNowMp() < mp)
			return false;

		return true;
	}

	/**
	 * 마법시전 성공여부 확인하는 함수.
	 * 
	 * @param cha
	 * @param o
	 * @param skill
	 * @return
	 */
	static public boolean isFigure(Character cha, object o, Skill skill, boolean is_probability, boolean is_clan) {
		cha.setFight(true);

		if (FightController.isFightMonster(o))
			return false;
		if (o instanceof KingdomCastleTop)
			return false;
		if (o instanceof SpotTower)
			return false;
		// 영자는 무조건 성공
		if (cha.getGm() > 0)
			return true;
		// 없거나 죽은상태 패스
		if (o == null || o.isDead())
			return false;
		if (o instanceof Racer)
			return false;
		// 쿠베라 펫장비
		// // 몬스터끼리는 모두 실패시키기.
		// if (cha instanceof MonsterInstance && o instanceof MonsterInstance)
		// return false;
		// 공격존체크해야되는 마법시 공격가능존 체크.

		// 쿠베라 몬스터 마법 확률 조정
		if (cha instanceof MonsterInstance) {

			Character target = (Character) o;

			int target_mr = getMr(target, false);
			if (target_mr < 60) {
				return true;
			} else if (target_mr < 70) {
				return Util.random(1, 100) < 50;
			} else if (target_mr < 80) {
				return Util.random(1, 100) < 25;
			} else if (target_mr < 90) {
				return Util.random(1, 100) < 10;
			} else if (target_mr < 100) {
				return Util.random(1, 100) < 5;
			} else {
				return false;
			}

		}
		if (isZoneCheckMagic(skill) && !World.isAttack(cha, o) && cha.getObjectId() != o.getObjectId()) {
			if (cha instanceof PcInstance)
				ChattingController.toChatting(cha, "이곳에서 마법을 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			return false;
		}
		// 장애물이 막고있으면 무시.
		if (!Util.isAreaAttack(cha, o) || !Util.isAreaAttack(o, cha))
			return false;
		// 존 체크마법일경우라면 보라돌이로 처리.
		if (isZoneCheckMagic(skill) && cha instanceof PcInstance && o instanceof PcInstance && !World.isCombatZone(cha.getX(), cha.getY(), cha.getMap()) && !World.isCombatZone(o.getX(), o.getY(), o.getMap())
				&& cha.getMap() != Lineage.teamBattleMap && o.getMap() != Lineage.teamBattleMap) {
			// 보라도리로 변경하기.
			if (Lineage.server_version > 182)
				Criminal.init(cha, o);
		}
		// 앱솔 무시.
		if (o.isBuffAbsoluteBarrier())
			return false;
		// 캔슬레이션을 제외한 모든확률마법은 석화상태 패스하기.
		if (skill.getUid() != 44 && o.isLockHigh())
			return false;
		// 독계열 마법일경우 저항력 체크.
		if (isPoisonMagic(skill) || skill.getElement() == Lineage.ELEMENT_POISON) {
			if (o.isBuffVenomResist())
				return false;
			// 안타라스 마갑주 독 내성
			if (o.isAntarasArmor())
				return false;
		}

		// 제니스의 반지 추가
		if (o instanceof PcInstance) {
			Character pc = (Character) o;
			// 제니스의 반지가 있는지 확인
			ItemInstance item = pc.getInventory().find("제니스의 반지");
			if (item != null && item.isEquipped()) {
				if (skill != null && skill.getUid() == 11)
					return false;
			}
		}
		// 반사방패 석화 마법 방어
		if (o instanceof PcInstance) {
			Character pc = (Character) o;
			// 인벤토리에서 제니스의 반지가 있는지 확인
			ItemInstance item = pc.getInventory().find("반사 방패");
			if (item != null && item.isEquipped()) {
				if (skill != null && (skill.getUid() == 33 || skill.getUid() == 302 || skill.getUid() == 305))
					return false;
			}
		}
		// 카운터 매직 영향을 받지 않는 마법
		if (!isNoCounterMagic(skill) && o.isBuffCounterMagic() && cha.getObjectId() != o.getObjectId()) {
			BuffController.remove(o, CounterMagic.class);
			return false;
		}
		// 자기자신은 100% 확률
		if (cha.getObjectId() == o.getObjectId())
			return true;
		// 확률 체크 안할경우 그냥 성공으로 리턴.
		if (!is_probability)
			return true;

		// 정령마법
		if (isElfMagic(skill)) {
			boolean result = false;
			double probability = 0;
			int level = cha.getLevel() - o.getLevel();

			// 시전자가 레벨이 높을때
			if (level > 0) {
				if (level == 1)
					probability = 0.27;
				else if (level == 2)
					probability = 0.29;
				else if (level == 3)
					probability = 0.31;
				else if (level == 4)
					probability = 0.33;
				else if (level == 5)
					probability = 0.35;
				else if (level == 6)
					probability = 0.37;
				else if (level == 7)
					probability = 0.39;
				else if (level == 8)
					probability = 0.41;
				else if (level == 9)
					probability = 0.43;
				else
					probability = 0.45;
			} else {
				// 시전자가 레벨이 같거나 낮을때
				if (level == 0)
					probability = 0.26;
				else if (level == -1)
					probability = 0.24;
				else if (level == -2)
					probability = 0.22;
				else if (level == -3)
					probability = 0.20;
				else if (level == -4)
					probability = 0.18;
				else if (level == -5)
					probability = 0.16;
				else if (level == -6)
					probability = 0.14;
				else if (level == -7)
					probability = 0.12;
				else if (level == -8)
					probability = 0.10;
				else if (level == -9)
					probability = 0.08;
				else
					probability = 0.05;
			}

			// 스트라이커 게일
			if (skill.getUid() == 121)
				probability *= Lineage_Balance.striker_gale;

			// 어스 바인드
			if (skill.getUid() == 134)
				probability *= Lineage_Balance.earth_bind;

			// 폴루트 워터
			if (skill.getUid() == 119)
				probability *= Lineage_Balance.pollute_watar;

			// 인탱글
			if (skill.getUid() == 122)
				probability *= Lineage_Balance.entangle;

			// 에어리어 오브 사일런스
			if (skill.getUid() == 130)
				probability *= Lineage_Balance.area_of_silence;

			// 정령내성
			probability -= cha.getDynamicElfResist();

			probability *= 100;
			probability += cha.getElfSkillHit();
			probability += cha.getTotalInt() * 0.20;
			probability -= getMr((Character) o, false) * 0.05;

			result = Util.random(1, 100) < (probability < 1 ? Util.random(1, 3) : Math.round(probability));

			if (Lineage.is_miss_effect && !result)
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, Lineage.miss_effect));

			return result;
		}
		if (skill.getUid() == 949) {
			boolean result = false;
			double probability = 0;
			int level = cha.getLevel() - o.getLevel();

			// 시전자가 레벨이 높을때
			if (level > 0) {
				if (level == 1)
					probability = 0.56;
				else if (level == 2)
					probability = 0.58;
				else if (level == 3)
					probability = 0.60;
				else if (level == 4)
					probability = 0.62;
				else if (level == 5)
					probability = 0.64;
				else if (level == 6)
					probability = 0.66;
				else if (level == 7)
					probability = 0.68;
				else if (level == 8)
					probability = 0.70;
				else if (level == 9)
					probability = 0.72;
				else
					probability = 0.74;
			} else {
				// 시전자가 레벨이 같거나 낮을때
				if (level == 0)
					probability = 0.55;
				else if (level == -1)
					probability = 0.53;
				else if (level == -2)
					probability = 0.50;
				else if (level == -3)
					probability = 0.47;
				else if (level == -4)
					probability = 0.44;
				else if (level == -5)
					probability = 0.41;
				else if (level == -6)
					probability = 0.38;
				else if (level == -7)
					probability = 0.35;
				else if (level == -8)
					probability = 0.32;
				else if (level == -8)
					probability = 0.29;
				else
					probability = 0.15;
			}

			probability *= 100;

			probability *= Lineage_Balance.am_probability;

			result = Util.random(1, 100) < (probability < 1 ? Util.random(1, 3) : Math.round(probability));

			if (Lineage.is_miss_effect && !result)
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, Lineage.miss_effect));

			return result;

		}
		// 쇼크 스턴 확률 계산
		if (skill.getUid() == 16) {
			boolean result = false;
			double probability = 0;
			int level = cha.getLevel() - o.getLevel();

			// 시전자가 레벨이 높을때
			if (level > 0) {
				if (level == 1)
					probability = 0.56;
				else if (level == 2)
					probability = 0.58;
				else if (level == 3)
					probability = 0.60;
				else if (level == 4)
					probability = 0.62;
				else if (level == 5)
					probability = 0.64;
				else if (level == 6)
					probability = 0.66;
				else if (level == 7)
					probability = 0.68;
				else if (level == 8)
					probability = 0.70;
				else if (level == 9)
					probability = 0.72;
				else
					probability = 0.74;
			} else {
				// 시전자가 레벨이 같거나 낮을때
				if (level == 0)
					probability = 0.55;
				else if (level == -1)
					probability = 0.53;
				else if (level == -2)
					probability = 0.50;
				else if (level == -3)
					probability = 0.47;
				else if (level == -4)
					probability = 0.44;
				else if (level == -5)
					probability = 0.41;
				else if (level == -6)
					probability = 0.38;
				else if (level == -7)
					probability = 0.35;
				else if (level == -8)
					probability = 0.32;
				else if (level == -8)
					probability = 0.29;
				else
					probability = 0.15;
			}

			probability += cha.getDynamicStunHit();

			if (o instanceof Character)
				probability -= ((Character) o).getTotalStunResist() * 0.4;

			probability *= 100;

			probability += cha.getKnightSkillHit();

			probability *= Lineage_Balance.stun_percent_rate;

			result = Util.random(1, 100) < (probability < 1 ? Util.random(1, 3) : Math.round(probability));

			if (Lineage.is_miss_effect && !result)
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, Lineage.miss_effect));

			return result;
		}

		boolean result = false;
		// 확률 체크.
		if (o instanceof Character) {
			Character target = (Character) o;
			// 리니지 인벤의 캔슬학개론 1강, 2강, 파워북 자료를 통계로 자체 공식을 직접 제작.
			// 인트 45 / 마법 명중 13을 기준으로 시전자의 인트나 명중이 기준보다 낮으면 확률이 감소
			// 인트 1당 0.5%의 확률 증가 또는 감소 / 마법 명중 1당 1%확률 증가 또는 감소
			// by all_night
			double statInt = (cha.getTotalInt() - 45) * 0.5;
			int magicHit = CharacterController.toStatInt(cha, "magicHit") - 15;
			double probability = Lineage_Balance.magic_probability + statInt + magicHit;

			for (int i = 0; i < getMr(target, false); i++) {
				if (i < 10)
					probability -= 0.32;
				else if (i < 20)
					probability -= 0.33;
				else if (i < 30)
					probability -= 0.35;
				else if (i < 40)
					probability -= 0.4;
				else if (i < 50)
					probability -= 0.47;
				else if (i < 60)
					probability -= 0.53;
				else if (i < 70)
					probability -= 0.57;
				else if (i < 80)
					probability -= 0.63;
				else if (i < 90)
					probability -= 0.85;
				else if (i < 100)
					probability -= 0.97;
				else if (i < 110)
					probability -= 0.85;
				else if (i < 120)
					probability -= 0.95;
				else if (i < 130)
					probability -= 1;
				else if (i < 140)
					probability -= 1.18;
				else if (i < 150)
					probability -= 1.35;
				else if (i < 160)
					probability -= 1.5;
				else
					probability -= 1.75;
			}

			// 대상의 레벨에 차이에 따른 확률 감소.
			if (target.getLevel() > 0) {
				int levelGap = cha.getLevel() - target.getLevel();

				switch (skill.getUid()) {
				// 턴언데드
				case 18:
					if (levelGap > 0)
						// 시전자가 대상보다 레벨이 높을 경우 1렙당 1%증가.
						probability += levelGap;
					else
						// 시전자보다 대상의 레벨이 높을 경우 2렙당 1%감소.
						probability += levelGap / 2;
					break;
				// 기타 다른 마법.
				default:
					// 시전자가 대상보다 레벨이 높을경우 1렙당 1%증가.
					// 시전자보다 대상의 레벨이 높을 경우 1렙당 1%감소.
					probability += levelGap;
					break;
				}
			}

			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				probability *= Lineage_Balance.royal_magic_final_hit_figure;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				probability *= Lineage_Balance.knight_magic_final_hit_figure;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				probability *= Lineage_Balance.elf_magic_final_hit_figure;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				probability *= Lineage_Balance.darkelf_magic_final_hit_figure;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				probability *= Lineage_Balance.wizard_magic_final_hit_figure;
				break;
			}

			switch (skill.getUid()) {
			// 턴언데드 확률 조절
			case 18:
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ELF:
					probability *= Lineage_Balance.turn_undead_elf_probability;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					probability *= Lineage_Balance.turn_undead_wizard_probability;
					break;
				}
				break;
			// 커스 패럴라이즈 확률 조절
			case 33:
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ELF:
					probability *= Lineage_Balance.curseParalyze_elf_probability;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					probability *= Lineage_Balance.curseParalyze_wizard_probability;
					break;
				}
				break;
			// 캔슬레이션
			case 44:
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ELF:
					probability *= Lineage_Balance.cancellation_elf_probability;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					probability *= Lineage_Balance.cancellation_wizard_probability;
					break;
				}
				break;
			}

			// 특정 마법은 확률 감소
			// 사일런스
			if (skill.getUid() == 64)
				probability *= Lineage_Balance.silence_probability;
			// 디케이 포션
			if (skill.getUid() == 71)
				probability *= Lineage_Balance.decay_potion_probability;

			probability *= 0.01;
			result = Math.random() < probability;

		} else
			result = Util.random(1, 100) < cha.getLevel();

		if (Lineage.is_miss_effect && !result)
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, Lineage.miss_effect));

		BuffController.remove(o, EraseMagic.class);

		return result;
	}

	/**
	 * 같은 혈맹원인지 체크
	 * 
	 * @param cha
	 * @param o
	 * @return
	 */
	static public boolean isClan(Character cha, object o) {
		if (o == null)
			return false;
		if (cha.getObjectId() != o.getObjectId()) {
			if (o.getClanId() == 0)
				return false;
			if (cha.getClanId() != o.getClanId())
				return false;
		}
		return true;
	}

	/**
	 * 같은 파티원인지 체크.
	 * 
	 * @param cha
	 * @param o
	 * @return
	 */
	static public boolean isParty(Character cha, object o) {
		return cha.getObjectId() == o.getObjectId() || (cha.getPartyId() != 0 && o.getPartyId() == cha.getPartyId());
	}

	/**
	 * 데미지 추출 처리 함수.
	 * 
	 * @param cha
	 * @param target
	 * @param o
	 * @param skill
	 * @param alpha_dmg
	 * @param skill_element
	 * @return
	 */
	static public int getDamage(Character cha, object target, object o, Skill skill, double alpha_dmg, int skill_element) {

		cha.setFight(true);

		// 버그 방지
		if (o == null || skill == null)
			return 0;
		if (DamageController.공격불가능한객체(target) || DamageController.공격불가능한객체(o))
			return 0;
		// 죽은거 무시.
		if (o.isDead())
			return 0;

		// 돌골렘 움크리기 상태 어택불가
		if (cha instanceof PcInstance && target instanceof MonsterInstance) {

			if (((MonsterInstance) target).getMonster().getName().equalsIgnoreCase("돌골렘") && ((MonsterInstance) target).getGfxMode() == 4) {

				return 0;

			}
		}
		// 굳은거 무시.
		if (o.isLockHigh())
			return 0;
		// 앱솔 무시.
		if (o.isBuffAbsoluteBarrier())
			return 0;

		// 메디테이션 제거
		if (cha.isBuffMeditation())
			BuffController.remove(cha, Meditation.class);

		// 카운터 매직 영향을 받지 않는 마법
		if (!isNoCounterMagic(skill) && o.isBuffCounterMagic() && cha.getObjectId() != o.getObjectId()) {
			BuffController.remove(o, CounterMagic.class);
			return 0;
		}
		// 공격마법일때 공격 가능존인지 확인.
		if (isAttackMagic(skill) && !World.isAttack(cha, o))
			return 0;
		if (!World.isAttack(cha, o))
			return 0;
		if (isAttackRangeMagic(skill) && !isMagicAttackRange(cha, o) && target.getObjectId() != o.getObjectId())
			return 0;
		// 장애물이 막고있으면 무시.
		if (!Util.isAreaAttack(cha, o) || !Util.isAreaAttack(o, cha))
			return 0;
		// 내성문이라면 공성중일때만 가능. 힐계열마법 무시.
		if (o instanceof KingdomDoor) {
			KingdomDoor kd = (KingdomDoor) o;
			if (kd.getKingdom() == null || kd.getNpc() == null)
				return 0;
			if (!kd.getKingdom().isWar() && kd.getNpc().getName().indexOf("내성문") > 0)
				return 0;
			if (isNoCounterMagic(skill))
				return 0;
		}

		// 좌표에 객체가 2명 이상일 경우(겹치기) PC에게 대미지 적용 불가
		if (!Lineage_Balance.is_fusion_attack && World.getMapdynamic(cha.getX(), cha.getY(), cha.getMap()) > 1 && cha instanceof PcInstance && o instanceof PcInstance && cha.getObjectId() != o.getObjectId()
				&& !World.isBattleZone(cha.getX(), cha.getY(), cha.getMap()) && !World.isBattleZone(o.getX(), o.getY(), o.getMap())) {
			ChattingController.toChatting(cha, "좌표에 2명 이상일 경우 공격이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return 0;
		}

		if (o instanceof Character) {
			Character t = (Character) o;

			ItemInstance shield = null;
			// 레이저종류의 마법일경우 타켓의 방패종류에따라 데미지를 적용 안함.
			if (skill.getElement() == Lineage.ELEMENT_LASER && t.getInventory() != null) {
				shield = t.getInventory().getSlot(Lineage.SLOT_SHIELD);
				if (shield != null) {
					switch (shield.getItem().getNameIdNumber()) {
					case 196: // 반사 방패
						if (shield.getEnLevel() >= 5 && Util.random(1, 100) <= 20)
							return 0;
						break;
					case 2035: // 붉은 기사의 방패
						if (shield.getEnLevel() >= 7 && Util.random(1, 100) <= 20)
							return 0;
						break;
					}
				}
			}
			// 굳는 마법 일경우 반사방패 반사 처리효과 넣기.
			if (skill.getLock().equalsIgnoreCase("low") || skill.getLock().equalsIgnoreCase("high")) {
				shield = t.getInventory().getSlot(Lineage.SLOT_SHIELD);
				if (shield != null) {
					switch (shield.getItem().getNameIdNumber()) {
					case 196: // 반사 방패
						if (shield.getEnLevel() > 4)
							return 0;
						break;
					}
				}
			}
		}

		int o_fire = 0;
		int o_warter = 0;
		int o_earh = 0;
		int o_wind = 0;
		int o_element = 0;
		if (o instanceof Character) {
			Character o_cha = (Character) o;
			o_fire = o_cha.getTotalFireress();
			o_earh = o_cha.getTotalEarthress();
			o_wind = o_cha.getTotalWindress();
			o_warter = o_cha.getTotalWaterress();
		}

		double dmg = alpha_dmg;

		// 나비켓의 하급, 중급, 상급, 최상급 보스들은 대상의 마방 무시
		if (Lineage_Balance.is_boss_monster_mr_dmg && dmg > 0 && cha != null && cha instanceof MonsterInstance) {
			MonsterInstance boss = (MonsterInstance) cha;

			if (boss.getMonster() != null && boss.getMonster().getBossClass().contains("보스")) {
				return (int) Math.round(dmg);
			}
		}

		// 대미지 연산
		double magicDmg = CharacterController.toStatInt(cha, "magicDamage") + getSp(cha);
		int tatgetMr = o instanceof Character ? getMr((Character) o, false) : 0;
		double mindmg = skill.getMindmg() * magicDmg;
		double maxdmg = skill.getMaxdmg() * magicDmg;

		dmg += Util.random(mindmg, maxdmg);

		// 카오틱 마법이라면 라우풀 수치에따라 대미지에 영향주기.
		// 풀카오일 경우 대미지의 49% 추가
		if (isChaoticMagic(skill) && cha instanceof PcInstance && cha.getLawful() < Lineage.NEUTRAL)
			dmg += dmg * (((Lineage.NEUTRAL - cha.getLawful()) * 0.00001) * 1.5);

		// 라우풀 마법이라면 라우풀 수치에따라 대미지에 영향주기.
		// 풀라우풀일 경우 대미지의 48% 추가

		if (isLawfulMagic(skill) && cha instanceof PcInstance && cha.getLawful() >= Lineage.NEUTRAL + 500) {
			dmg += dmg * (((cha.getLawful() - 66035) * 0.00001) * 1.5);

		}

		// 힐계열 마법이 아닐때
		// : mr체크해서 데미지 하향.
		// : 기타 버프 상태따라 처리.
		if (!isNoCounterMagic(skill) && !isHeal(skill)) {
			// 속성저항력값 추출.
			switch (skill_element) {
			case Lineage.ELEMENT_FIRE:
				o_element = o_fire;
				break;
			case Lineage.ELEMENT_WATER:
				o_element = o_warter;
				break;
			case Lineage.ELEMENT_EARTH:
				o_element = o_earh;
				break;
			case Lineage.ELEMENT_WIND:
				o_element = o_wind;
				break;
			}

			// 속성마법 데미지 하향처리.
			if (o_element > 0) {
				double el_dmg = o_element * 0.6;
				if (el_dmg > 100)
					el_dmg = 100;
				el_dmg = el_dmg * 0.01;
				dmg -= dmg * el_dmg;
			}

			// 리덕션에 따른 데미지 감소
			if (cha instanceof Character && o instanceof Character) {
				Character c = (Character) cha;
				Character use = (Character) o;
				// 리덕션, 리덕션 무시 적용
				dmg -= use.getTotalReduction() - c.getDynamicIgnoreReduction() < 0 ? 0 : use.getTotalReduction() - c.getDynamicIgnoreReduction();
			}

			if (cha instanceof PcInstance && o instanceof PcInstance) {
				PcInstance use = (PcInstance) o;
				// PvP 대미지 리덕션
				dmg -= use.getDynamicAddPvpReduction();
			}

			// MR, 레벨에 의한 대미지 감소
			dmg = getMrDamage(cha, o, dmg, true);

			int criticalChance = (int) Math.round(CharacterController.toStatInt(cha, "magicCritical") - (tatgetMr * 0.05));

			cha.setCriticalMagicEffect(false);
			// 마법 치명타 발동시 대미지의 1.3배 적용.
			if (Util.random(1, 100) <= criticalChance) {
				if (Lineage.is_skill_critical_effect)
					cha.setCriticalMagicEffect(true);
				dmg *= 1.3;
			}

			if (o instanceof PcInstance) {
				if (o.getInventory() != null) {
					ItemInstance targetArmor = o.getInventory().getSlot(Lineage.SLOT_ARMOR);
					ItemInstance targetShiled = o.getInventory().getSlot(Lineage.SLOT_SHIELD);

					if (targetArmor != null && targetArmor.getItem().getName().equalsIgnoreCase("신성한 요정족 판금 갑옷") && Util.random(1, 100) <= Util.random(5, 7)) {
						o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 13429), true);
						o.setNowHp(o.getNowHp() + Util.random(30, 60));
					}

					if (targetShiled != null && targetShiled.getItem().getName().equalsIgnoreCase("신성한 요정족 방패") && Util.random(1, 100) <= targetShiled.getEnLevel()) {
						o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, 14543), true);
						dmg -= 10;
					}

					if (targetShiled != null && targetShiled.getItem().getName().equalsIgnoreCase("반역자의 방패") && Util.random(1, 100) <= targetShiled.getEnLevel() * 2) {
						o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, Lineage.doll_defence_effect), true);
						dmg -= 50;
					}

					// 결속된 파푸리온의 가호
					// 힐계열로 인식하기때문에 폴루트 워터, 워터라이프의 영향을 받음.
					if (target.isFafurionArmor() && Util.random(1, 100) <= 8) {
						int hp = Util.random(70, 90);

						switch (targetArmor.getEnLevel()) {
						case 7:
							hp += 10;
							break;
						case 8:
							hp += 20;
							break;
						case 9:
							hp += 40;
							break;
						}

						if (target.isBuffPolluteWater()) {
							hp *= 0.5;
							BuffController.remove(target, PolluteWater.class);
						} else if (target.isBuffWaterLife()) {
							hp *= 2;
							BuffController.remove(target, WaterLife.class);
						}

						target.setNowHp(target.getNowHp() + hp);
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, 2187), target instanceof PcInstance);
					}

					// 마력의 린드비오르 가호
					if (target.isLindviorArmor() && Util.random(1, 100) <= 8) {
						int mp = targetArmor.getItem().getName().equalsIgnoreCase("린드비오르의 완력") ? 10 : targetArmor.getItem().getName().equalsIgnoreCase("린드비오르의 예지력") ? 15 : 20;

						target.setNowMp(target.getNowMp() + mp);
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, 2188), target instanceof PcInstance);
					}

				}
			}

			if (cha instanceof PcInstance) {
				// 클래스별 마법 대미지 최종 밸런스
				switch (cha.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL:
					dmg *= Lineage_Balance.royal_magic_final_damage_figure;
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					dmg *= Lineage_Balance.knight_magic_final_damage_figure;
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					dmg *= Lineage_Balance.elf_magic_final_damage_figure;
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					dmg *= Lineage_Balance.darkelf_magic_final_damage_figure;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					dmg *= Lineage_Balance.wizard_magic_final_damage_figure;
					break;
				}
			}

			if (cha instanceof MonsterInstance) {
				// 언데드 몬스터는 밤일경우 추가 대미지
				if (((MonsterInstance) cha).getMonster().isUndead() && ServerDatabase.isNight())
					dmg *= Util.random(1.1, 1.3);
			}

			// 수룡/탄생/형상/생명의 마안
			if ((target.isBuffMaanWatar() || target.isBuffMaanLife() || target.isBuffMaanBirth() || target.isBuffMaanShape()) && Util.random(1, 100) <= 6) {
				dmg = dmg / 2;

				if (Lineage.마안이팩트여부) {
					if (target.isBuffMaanEarth())
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.지룡의마안_이팩트));
					else if (target.isBuffMaanBirth())
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.탄생의마안_이팩트));
					else if (target.isBuffMaanShape())
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.형상의마안_이팩트));
					else if (target.isBuffMaanLife())
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.생명의마안_이팩트));
				}
			}

			// 이뮨투함.
			if (o.isBuffImmuneToHarm())
				dmg *= dmg <= 0 ? 0 : Lineage_Balance.immuneToHarmReduction;
		}

		/*
		 * try { if (cha.getGm() > 0) { long time = System.currentTimeMillis();
		 * String timeString = Util.getLocaleString(time, true);
		 * System.out.println(String.
		 * format("[%s] [%s->%s] [스킬: %s] [데미지: %.1f] 마지막 공격 시간차: %d",
		 * timeString, cha.getName(), o.getName(), skill.getName(), dmg, time -
		 * cha.testTime)); cha.testTime = time; } } catch (Exception e) {
		 * System.out.println(e); }
		 */

		// 최종 대미지 반올림

		if (cha instanceof PcInstance && cha.getGm() == 0) {
			if (Lineage_Balance.dmg_limit) {
				PcInstance pc = (PcInstance) cha;
				boolean dmglimit = false;

				switch (pc.getClassType()) {
				case Lineage.LINEAGE_CLASS_ROYAL:
					if (dmg >= Lineage_Balance.royalskillmaxdmg) {
						dmglimit = true;
					}
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					if (dmg >= Lineage_Balance.knightskillmaxdmg) {
						dmglimit = true;
					}
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					if (dmg >= Lineage_Balance.elfskillmaxdmg) {
						dmglimit = true;
					}
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					if (dmg >= Lineage_Balance.wizardskillmaxdmg) {
						dmglimit = true;
					}
					break;
				}

				if (dmglimit) {

					String log = String.format("[마법 데미지 초과] -> [캐릭터: %s]  [스킬: %s]  [데미지: %.0f]", pc.getName(), skill.getName(), dmg);

					GuiMain.display.asyncExec(new Runnable() {
						public void run() {
							GuiMain.getViewComposite().getDamageCheckComposite().toLog(log);
						}
					});
					FrameSpeedOverStun.init(pc, Lineage_Balance.dmg_limit_sturn);
					ChattingController.toChatting(pc, String.format("불법적인 행위로 운영자로 부터 %d 초간 스턴을 당하였습니다.", Lineage_Balance.dmg_limit_sturn), Lineage.CHATTING_MODE_MESSAGE);
					pc.mdmglimitcheck++;

					if (Lineage_Balance.dmg_limit_out && Lineage_Balance.dmg_limit_count < pc.mdmglimitcheck) {
						String log2 = String.format("[마법 데미지 검출 횟수 초과] -> [검출횟수: %d] [캐릭터: %s]  [스킬: %s]  [데미지: %.0f]", pc.mdmglimitcheck, pc.getName(), skill.getName(), dmg);

						GuiMain.display.asyncExec(new Runnable() {
							public void run() {
								GuiMain.getViewComposite().getDamageCheckComposite().toLog(log2);
							}
						});
						// 사용자 강제종료 시키기.
						pc.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
						LineageServer.close(pc.getClient());
						return 0;
					}
				}
			}
		}

		// PcInstance.DmgViewer(target,(int) dmg);
		return (int) Math.round(dmg);
	}

	static public int getMrDamage(Character cha, object target, double dmg, boolean levelGab) {
		double o_mr = 0;
		double mr_dmg = 0;
		int targetLevel = target.getLevel();

		if (target instanceof Cracker) {
			targetLevel = cha.getLevel();

			if (target.getName().contains("초급"))
				o_mr = 80;

			else if (target.getName().contains("중급"))
				o_mr = 120;
			else if (target.getName().contains("고급"))
				o_mr = 150;
		}

		// 마법 대미지는 1레벨 차이당 3%감소, 10레벨 차이 이상 10% 대미지 감소 고정
		if (levelGab && targetLevel - cha.getLevel() > 0) {
			if (targetLevel - cha.getLevel() < 10)
				dmg *= 1 - ((targetLevel - cha.getLevel()) * 0.01);
			else
				dmg *= 0.9;
		}

		if (target instanceof Character)
			o_mr = getMr((Character) target, false);

		// mr 100까지는 mr 1당 대미지의 0.45%씩 감소
		// mr 100일 경우 최대 대미지의 55% 대미지만 받음
		mr_dmg = 1 - ((o_mr > 100 ? 100 : o_mr) * Lineage_Balance.mr_low_damage_reduce);

		// mr 100 이상 mr 1당 대미지의 0.15%씩 감소
		if (o_mr - 100 > 0)
			mr_dmg -= (o_mr - 100) * Lineage_Balance.mr_high_damage_reduce;

		// mr에따른 대미지 감소.
		dmg *= mr_dmg;

		return (int) Math.round(dmg);

	}

	/**
	 * 데미지 추출 처리 함수. 마법무기에서 사용중.
	 * 
	 * @return
	 */
	static public int getDamage(object target, double dmg, int element) {
		int o_fire = 0;
		int o_warter = 0;
		int o_earh = 0;
		int o_wind = 0;
		int o_element = 0;
		double o_mr = 0;

		if (target.isBuffCounterMagic()) {
			BuffController.remove(target, CounterMagic.class);
			return 0;
		}

		if (target instanceof Character) {
			Character o_cha = (Character) target;
			o_fire = o_cha.getTotalFireress();
			o_earh = o_cha.getTotalEarthress();
			o_wind = o_cha.getTotalWindress();
			o_warter = o_cha.getTotalWaterress();
		}

		switch (element) {
		case Lineage.ELEMENT_EARTH: // 1
			o_element = o_earh;
			break;
		case Lineage.ELEMENT_FIRE: // 2
			o_element = o_fire;
			break;
		case Lineage.ELEMENT_WIND: // 3
			o_element = o_wind;
			break;
		case Lineage.ELEMENT_WATER: // 4
			o_element = o_warter;
			break;
		}

		if (target instanceof Character) {
			Character t = (Character) target;
			o_mr = getMr(t, false);
		}
		// 속성마법 데미지 하향처리.
		if (o_element > 0) {
			double el_dmg = o_element * 0.6;
			if (el_dmg > 100)
				el_dmg = 100;
			el_dmg = el_dmg * 0.01;
			dmg -= dmg * el_dmg;
		}
		// mr 값에 따른 감소값 추출. 퍼센트
		double mr_dmg = (o_mr * 0.45) * 0.01; // mr에 45%만 감소에 반영하기.
		// 감소.
		dmg -= dmg * mr_dmg; // mr에따른 데미지 감소.
		dmg = Util.random(dmg * 0.8, dmg); // 최종 추출된 데미지에서 10%정도만 낮춰서 랜덤 추출.

		return (int) ((dmg - (int) dmg >= 0.5) ? dmg + 1 : dmg);

	}

	/**
	 * 카오틱 마법인지 확인.
	 */
	static private boolean isChaoticMagic(Skill skill) {
		switch (skill.getUid()) {
		case 10: // 칠터치
		case 28: // 뱀파이어릭터치
		case 59: // 블리자드
			return true;
		}
		return false;
	}

	/**
	 * 로우풀 마법인지 확인. 2017-11-07 by all-night
	 */
	static private boolean isLawfulMagic(Skill skill) {
		switch (skill.getUid()) {
		case 77: // 디스인티그레이트
			return true;
		}
		return false;
	}

	/**
	 * 독속성 마법인지 확인 2017-11-21 by all-night
	 */
	static private boolean isPoisonMagic(Skill skill) {
		switch (skill.getUid()) {
		case 11: // 커스: 포이즌
		case 301: // 구울 독
			return true;
		}
		return false;
	}

	/**
	 * 카운터 매직 영향을 받지 않는 마법
	 * 
	 * @param skill
	 * @return
	 */
	static private boolean isNoCounterMagic(Skill skill) {
		switch (skill.getUid()) {
		case 1: // 힐
		case 16: // 쇼크 스턴
		case 19: // 익스트라 힐
		case 35: // 그레이터 힐
		case 49: // 힐 올
		case 57: // 풀 힐
		case 68: // 이뮨 투 함
		case 115: // 트리플 애로우
		case 133: // 네이쳐스블레싱
			return true;
		}
		return false;
	}

	/**
	 * 정령 마법 2017-10-30 by all-night
	 */
	static private boolean isElfMagic(Skill skill) {
		switch (skill.getUid()) {
		case 107: // 레지스트 매직
		case 108: // 바디 투 마인드
		case 109: // 텔레포트 투 마더
		case 113: // 클리어 마인드
		case 114: // 레지스트 엘리멘트
		case 115: // 트리플 애로우
		case 116: // 블러드 투 소울
		case 117: // 이글 아이
		case 118: // 아쿠아 프로텍트
		case 119: // 폴루트 워터
		case 121: // 스트라이커 게일
		case 122: // 인탱글
		case 123: // 이레이즈 매직
		case 124: // 버닝 웨폰
		case 125: // 엘리멘탈 파이어
		case 126: // 아이 오브 스톰
		case 128: // 네이쳐스 터치
		case 129: // 어스 가디언
		case 130: // 에어리어 오브 사일런스
		case 131: // 어디셔널 파이어
		case 132: // 워터 라이프
		case 133: // 네이쳐스 블레싱
		case 134: // 어스 바인드
		case 135: // 스톰 샷
		case 136: // 소울 오브 프레임
		case 137: // 아이언 스킨
			return true;
		}
		return false;
	}

	/**
	 * 힐 계열 마법
	 * 
	 * @param skill
	 * @return
	 */
	static private boolean isHeal(Skill skill) {
		switch (skill.getUid()) {
		case 1: // 힐
		case 19: // 익스트라 힐
		case 35: // 그레이터 힐
		case 49: // 힐 올
		case 57: // 풀 힐
		case 128: // 네이쳐스 터치
		case 133: // 네이쳐스블레싱
			return true;
		}
		return false;
	}

	/**
	 * 공격형 마법인지 확인.
	 */
	static private boolean isAttackMagic(Skill skill) {
		switch (skill.getUid()) {
		case 4: // 에너지 볼트
		case 6: // 아이스 대거
		case 7: // 윈드 커터
		case 10: // 칠 터치
		case 15: // 파이어 애로우
		case 16: // 스탈락
		case 17: // 라이트닝
		case 18: // 턴 언데드
		case 22: // 프로즌 클라우드
		case 25: // 파이어 볼
		case 28: // 뱀파이어릭 터치
		case 30: // 어스 재일
		case 34: // 콜 라이트닝
		case 38: // 콘 오브 콜드
		case 45: // 이럽션
		case 46: // 선 버스트
		case 50: // 아이스 랜스
		case 53: // 토네이도
		case 58: // 파이어 월
		case 59: // 블리자드
		case 62: // 어스 퀘이크
		case 65: // 라이트닝 스톰
		case 70: // 스톰
		case 74: // 미티어 스트라이크
		case 77: // 디스인티 그레이트
		case 80: // 프리징 블리자드
			return true;
		}
		return false;
	}

	/**
	 * 범위 공격형 마법인지 확인.
	 */
	static private boolean isAttackRangeMagic(Skill skill) {
		switch (skill.getUid()) {
		case 17: // 라이트닝
		case 22: // 프로즌 클라우드
		case 25: // 파이어 볼
		case 53: // 토네이도
		case 59: // 블리자드
		case 62: // 어스 퀘이크
		case 65: // 라이트닝 스톰
		case 70: // 스톰
		case 74: // 미티어 스트라이크
		case 80: // 프리징 블리자드
			return true;
		}
		return false;
	}

	/**
	 * 확률 계산시 존을 체크해서 처리해야되는 마법인지 확인용 함수. : 보라돌이 처리할 마법인지 확인할때도 사용중.
	 */
	static private boolean isZoneCheckMagic(Skill skill) {
		switch (skill.getUid()) {
		case 11: // 커스:포이즌
		case 16: // 쇼크 스턴
		case 18: // 턴 언데드
		case 20: // 커스:블라인드
		case 27: // 웨폰브레이크
		case 29: // 슬로우
		case 33: // 커스:패럴라이즈
		case 39: // 마나드레인
		case 40: // 다크니스
		case 44: // 캔슬레이션
		case 47: // 위크니스
		case 50: // 아이스랜스
		case 56: // 디지즈
		case 64: // 사일런스
		case 66: // 포그오브슬리핑
		case 71: // 디케이포션
		case 119: // 폴루트 워터
		case 121: // 스트라이커 게일
		case 122: // 인탱글
		case 123: // 이레이즈매직
		case 130: // 에어리어 오브 사일런스
		case 134: // 어스 바인드
			return true;
		}
		return false;
	}

	/**
	 * 범위 공격형 마법시 데미지를 연산 해도되는 타켓인지 검색하는 함수.
	 * 
	 * @param cha
	 *            : 공격자
	 * @param o
	 *            : 대상자
	 * @return
	 */
	static public boolean isMagicAttackRange(Character cha, object o) {
		// 마법인형 공격 안되게.
		if (o instanceof MagicDollInstance)
			return false;
		// 사용자 일경우
		if (cha instanceof PcInstance) {
			// 성존 부분 따로 구분.
			Kingdom k = KingdomController.findKingdomLocation(cha);
			if (k != null && k.isWar()) {
				// 성존에 공성중일때 같은 혈맹 소속들은 무시.
				if (cha.getClanId() > 0 && cha.getClanId() == o.getClanId())
					return false;
				return true;
			}
			return !(o instanceof PcInstance) && !(o instanceof SummonInstance) && !(o instanceof GuardInstance);
		}
		// 몬스터일 경우
		if (cha instanceof MonsterInstance)
			return o instanceof PcInstance || o instanceof SummonInstance;
		// 그외엔 걍 성공하기.
		return true;
	}

	/**
	 * 스펠파워 리턴
	 */
	static public int getSp(Character cha) {
		int sp = cha.getDynamicSp();
		int level = cha.getLevel();
		int gab = 0;

		if (cha instanceof PcInstance) {
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				if (cha.getLevel() > 20)
					level = 20;
				gab = 10;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if (cha.getLevel() > 50)
					level = 50;
				gab = 50;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (cha.getLevel() > 48)
					level = 48;
				gab = 8;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if (cha.getLevel() > 40)
					level = 40;
				gab = 4;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				if (cha.getLevel() > 24)
					level = 24;
				gab = 12;
				break;
			}

			sp += level / gab;
		}

		// int에 따른 마법 보너스
		sp += CharacterController.toStatInt(cha, "magicBonus");

		return sp;
	}

	/**
	 * 마법방어력 리턴
	 * 
	 * @param cha
	 * @param packet
	 *            : 패킷용과 일반 연산용 구분을 위해. : 패킷에선 클라자체 내부 공식과 리턴한 mr 이 함께 연산됨.
	 * @return
	 */
	static public int getMr(Character cha, boolean packet) {
		// 기본 mr 추출.
		int mr = cha.getDynamicMr();

		// 클레스별 보너스 mr 추출.
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			mr += 10;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			mr += 20;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			mr += 15;
			break;
		}

		switch (cha.getWis() + cha.getLvWis()) {
		case 15:
			mr += 3;
			break;
		case 16:
			mr += 3;
			break;
		case 17:
			mr += 6;
			break;
		case 18:
			mr += 10;
			break;
		case 19:
			mr += 15;
			break;
		case 20:
			mr += 21;
			break;
		case 21:
			mr += 28;
			break;
		case 22:
			mr += 37;
			break;
		case 23:
			mr += 47;
			break;
		case 24:
			mr += 50;
			break;
		case 25:
			mr += 50;
			break;
		case 26:
			mr += 50;
			break;
		case 27:
			mr += 50;
			break;
		case 28:
			mr += 50;
			break;
		case 29:
			mr += 50;
			break;
		case 30:
			mr += 50;
			break;
		case 31:
			mr += 50;
			break;
		case 32:
			mr += 50;
			break;
		case 33:
			mr += 50;
			break;
		case 34:
			mr += 50;
			break;
		case 35:
			mr += 50;
			break;
		}

		mr = mr + (cha.getLevel() / 2);

		// 이레이즈매직
		if (cha.isBuffEraseMagic())
			mr /= 4;

		// 버그 방지.
		if (mr < 0)
			mr = 0;

		mr = Math.round(mr);

		// 패킷 용이라면 최대 mr값 확인.
		// 리턴.
		if (packet) {
			int max_packet_mr = 100;
			if (Lineage.max_mr < max_packet_mr)
				return mr > Lineage.max_mr ? Lineage.max_mr : mr;
			else
				return mr > max_packet_mr ? max_packet_mr : mr;
		} else
			return mr > Lineage.max_mr ? Lineage.max_mr : mr;
	}

}