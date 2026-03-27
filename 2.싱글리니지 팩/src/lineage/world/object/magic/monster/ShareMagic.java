package lineage.world.object.magic.monster;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.MonsterSkill;
import lineage.bean.lineage.Buff;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Cancellation;
import lineage.world.object.magic.ChillTouch;
import lineage.world.object.magic.CurseBlind;
import lineage.world.object.magic.CurseParalyze;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.DecayPotion;
import lineage.world.object.magic.Disease;
import lineage.world.object.magic.EnergyBolt;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.IceLance;
import lineage.world.object.magic.Lightning;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.ShockStun;
import lineage.world.object.magic.Silence;
import lineage.world.object.magic.Slow;
import lineage.world.object.magic.SummonMonster;
import lineage.world.object.magic.Teleport;
import lineage.world.object.magic.Tornado;
import lineage.world.object.magic.WeaponBreak;

public class ShareMagic {

	static private List<object> list = new ArrayList<object>();
	static private int x;
	static private int y;

	/**
	 * 몬스터 스킬 처리 함수. : MonsterInstance 에 toAiAttack(long time) 에서 호출됨. : monster_skill 에 등록된 정보를 참고로해서 처리함.
	 * 
	 * @param mi
	 * @param o
	 * @param skill
	 * @return
	 */
	static public boolean init(MonsterInstance mi, object o, MonsterSkill skill, int x, int y, boolean bow) {
		synchronized (list) {
			// 확률확인. 100이면 걍 무조건 시전 시도.
			if (skill.getChance() == 100 || Util.random(1, 100) < skill.getChance()) {
				// 시전가능한 hp/mp 인지 확인. 거리안에있는지 확인.
				if (SkillController.isHpMpCheck(mi, skill.getHpConsume(), skill.getMpConsume()) && Util.isDistance(mi, o, skill.getDistance())) {
					// 스킬 딜레이
					mi.lastSkillTime = System.currentTimeMillis() + (skill.getSkill() == null ? skill.getDelay() : skill.getSkill().getDelay());
					
					int action = skill.getActionNumber();
					// 액션 취하는 딜레이 주기.
					mi.setAiTime(SpriteFrameDatabase.getGfxFrameTime(mi, mi.getGfx(), action));
					// 마법 시전.
					if (skill.getType().equalsIgnoreCase("magic_attack")) {
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if (skill.getSkill() == null)
							// 이럽션
							skill.setSkill(SkillDatabase.find(6, 4));
						onBuff(mi, o, skill, action, skill.getCastGfx() == 0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);

					} else if (skill.getType().equalsIgnoreCase("magic_attack_range")) {
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if (skill.getSkill() == null)
							// 이럽션
							skill.setSkill(SkillDatabase.find(6, 4));
						onBuff(mi, o, true, skill, action, skill.getRange());

					} else if (skill.getType().equalsIgnoreCase("magic_none_range")) {
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if (skill.getSkill() == null)
							// 토네이도
							skill.setSkill(SkillDatabase.find(7, 4));
						onBuff(mi, o, skill, action, skill.getCastGfx() == 0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);

					} else if (skill.getType().equalsIgnoreCase("magic_target_range")) {
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if (skill.getSkill() == null)
							// 파이어볼
							skill.setSkill(SkillDatabase.find(4, 0));
						onBuff(mi, o, skill, action, skill.getCastGfx() == 0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);

					} else if (skill.getType().equalsIgnoreCase("magic_attack_and_buff")) {
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if (skill.getSkill() == null)
							// 이럽션
							skill.setSkill(SkillDatabase.find(6, 4));
						EnergyBolt.init(mi, o, skill, action, skill.getCastGfx());
						onBuff(mi, o, skill, -1, 0, false);

					}
					// 물리 시전.
					else if (skill.getType().equalsIgnoreCase("attack")) {
						if (skill.getCastGfx() > 0)
							o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);
						
						mi.toAttack(o, x, y, bow, action, (int) Util.random(skill.getMindmg(), skill.getMaxdmg()), false);

					} else if (skill.getType().equalsIgnoreCase("attack_none_range")) {					
						init(mi, skill, action, skill.getRange(), skill.getCastGfx() == 0 ? Lineage.GFX_MODE_EFFECT_NONE : skill.getCastGfx(), (int) Util.random(skill.getMindmg(), skill.getMaxdmg()));

					} else if (skill.getType().equalsIgnoreCase("attack_target_range")) {
						init(mi, o, action, skill.getRange(), skill.getCastGfx() == 0 ? Lineage.GFX_MODE_EFFECT_NONE : skill.getCastGfx(), (int) Util.random(skill.getMindmg(), skill.getMaxdmg()));

					} else if (skill.getType().equalsIgnoreCase("attack_target_range_and_buff")) {
						init(mi, o, action, skill.getRange(), skill.getCastGfx() == 0 ? Lineage.GFX_MODE_EFFECT_NONE : skill.getCastGfx(), (int) Util.random(skill.getMindmg(), skill.getMaxdmg()));
						if (skill.getSkill() != null)
							onBuff(mi, skill, -1, 0, false, list);
					}
					// 버프 시전.
					else if (skill.getType().equalsIgnoreCase("magic_buff")) {
						if (skill.getSkill() != null) {
							// 텔레포트일때 범위내로 잇을경우 무시.
							if (skill.getSkill().getUid() == 5 && Util.isDistance(mi, o, 3))
								return false;
							// 시전.
							onBuff(mi, o, skill, action, skill.getCastGfx(), true);
						}

					} else if (skill.getType().equalsIgnoreCase("magic_buff_range")) {
						if (skill.getSkill() != null)
							onBuff(mi, o, false, skill, action, skill.getRange());

					} else if (skill.getType().equalsIgnoreCase("magic_buff_target_range")) {
						if (skill.getSkill() != null)
							onBuff(mi, o, skill, action, skill.getRange());

					} else if (skill.getType().equalsIgnoreCase("magic_buff_me")) {
						if (skill.getSkill() != null) {
							// 헤이스트가 없을경우에만 시전
							if (skill.getSkill().getUid() == 43 && BuffController.find(mi, SkillDatabase.find(43)) != null)
								return false;
							
							onBuff(mi, mi, skill, action, skill.getCastGfx(), true);
						}
					} else if (skill.getType().equalsIgnoreCase("call")) {
						toCall(mi, o, skill);

					} else if (skill.getType().equalsIgnoreCase("summon")) {
						// 스킬이 안잡혀있다면 기본으로 서먼몬스터로 설정.
						if (skill.getSkill() == null)
							skill.setSkill(SkillDatabase.find(7, 2));
						// 이미 시전된 상태라면 무시.
						Buff buff = BuffController.find(mi);
						if (buff != null && buff.find(SummonMonster.class) != null)
							return false;
						// 처리.
						onBuff(mi, o, skill, action, skill.getCastGfx() == 0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);
					} else if (skill.getType().equalsIgnoreCase("poly") && skill.getOption().length() > 0) {
						// 몬스터 주변 변신
						init(mi, o, skill, skill.getDistance(), skill.getCastGfx(), skill.getOption(), action, x, y, bow);
					}
					
					if (skill.getMsg() != null && skill.getMsg().length() > 0)
					
				
						ChattingController.toChatting(mi, skill.getMsg(), Lineage.CHATTING_MODE_SHOUT);
				
		
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 몬스터를 중심으로 area 만큼 객체들에게 데미지 입힘.
	 * 
	 * @param mi
	 * @param gfx_mode
	 * @param area
	 * @param effect
	 */
	static private void init(MonsterInstance mi, MonsterSkill ms, int gfx_mode, int area, int effect, int alpha_dmg) {
		// 초기화
		x = 0;
		y = 0;
		list.clear();
		// 검색
		for (object o : mi.getInsideList()) {
			// 공격가능한 객체인지 확인.
			if (!SkillController.isMagicAttackRange(mi, o))
				continue;
			// 범위에있으면 처리.
			if (Util.isDistance(mi, o, area) && o instanceof Character && mi.getObjectId() != o.getObjectId())
				onBuff(mi, o, alpha_dmg);
		}

		// 패킷 처리.
		if (x != 0 && y != 0)
			mi.setHeading(Util.calcheading(mi, x, y));
		
		object o = null;	
		if (list != null) {
			for (object t : list) {
				if (t != null && !t.isWorldDelete() && !t.isDead()) {
					o = t;
					break;
				}
			}
		} else if (mi.getAttackList() != null) {
			for (object t : mi.getAttackList()) {
				if (t != null && !t.isWorldDelete() && !t.isDead()) {
					o = t;
					break;
				}
			}
		}
		
		mi.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), mi, o, list, true, gfx_mode, 0, effect, x, y), false);
	}

	/**
	 * target 중심으로 area 만큼 객체들에게 데미지 입힘.
	 * 
	 * @param mi
	 * @param o
	 * @param gfx_mode
	 * @param area
	 * @param effect
	 */
	static private void init(MonsterInstance mi, object target, int gfx_mode, int area, int effect, int alpha_dmg) {
		// 초기화
		x = 0;
		y = 0;
		list.clear();
		
		onBuff(mi, target, alpha_dmg);

		for (object o : target.getInsideList()) {
			// 공격가능한 객체인지 확인.
			if (!SkillController.isMagicAttackRange(mi, o))
				continue;
			// 범위에있으면 처리.
			if (Util.isDistance(target, o, area) && o instanceof Character && mi.getObjectId() != o.getObjectId())
				onBuff(mi, o, alpha_dmg);
		}
		
		// 패킷 처리.
		if (x != 0 && y != 0)
			mi.setHeading(Util.calcheading(mi, x, y));
		
		object o = null;		
		if (list != null) {
			for (object t : list) {
				if (t != null && !t.isWorldDelete() && !t.isDead()) {
					o = t;
					break;
				}
			}
		} else if (mi.getAttackList() != null) {
			for (object t : mi.getAttackList()) {
				if (t != null && !t.isWorldDelete() && !t.isDead()) {
					o = t;
					break;
				}
			}
		}

		mi.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), mi, o, list, true, gfx_mode, 0, effect, x, y), false);
	}
	
	/**
	 * 좀비 로드가 좀비로 변신시키기 위해 사용중
	 * 2017-10-26
	 * by all-night
	 */
	static private void init(MonsterInstance mi, object o, MonsterSkill skill, int area, int effect, String name, int gfx_mode, int x, int y, boolean bow) {
		if (gfx_mode > 0)
			mi.toAttack(o, x, y, bow, gfx_mode, (int) Util.random(skill.getMindmg(), skill.getMaxdmg()), false);
		
		// 검색
		for (object pc : mi.getInsideList()) {
			// 공격가능한 객체인지 확인.
			if (!SkillController.isMagicAttackRange(mi, pc))
				continue;
			// 범위에있으면 처리.
			if (Util.isDistance(mi, pc, area) && pc instanceof PcInstance) {
				ShapeChange.onBuff(mi, pc, PolyDatabase.getName(name), -10, false, true);
				
				if (effect > 0)
					pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, effect), true);
			}
		}
	}

	/**
	 * 중복 코드 방지용.
	 * 
	 * @param mi
	 * @param o
	 */
	static private void onBuff(MonsterInstance mi, object target, int alpha_dmg) {
		if (target != null && !target.isWorldDelete() && !target.isDead() && !target.isTransparent()) {
			int dmg = DamageController.getDamage(mi, target, false, null, null, alpha_dmg);
			DamageController.toDamage(mi, target, dmg, Lineage.ATTACK_TYPE_WEAPON);
			if (dmg > 0) {
				if (x == 0 && y == 0) {
					x = target.getX();
					y = target.getY();
				}
				list.add(target);
			}
		}
	}

	/**
	 * 중복코드 방지용.
	 * 
	 * @param mi
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check
	 * @param list
	 */
	static public void onBuff(MonsterInstance mi, MonsterSkill ms, int action, int effect, boolean check, List<object> list) {
		for (object o : list)
			onBuff(mi, o, ms, -1, effect, false);
	}

	/**
	 * 중복 코드 방지용.
	 * 
	 * @param mi
	 * @param o
	 * @param skill
	 * @param action
	 * @param area
	 */
	static public void onBuff(MonsterInstance mi, object target, boolean attack, MonsterSkill ms, int action, int area) {
		// 액션값이 정해졌을때만 패킷처리.
		if (action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		
		if (target != null)
			mi.setHeading(Util.calcheading(mi, target.getX(), target.getY()));
		
		// 주변객체 검색해서 범위안에있는 애들만 처리.
		for (object o : mi.getInsideList()) {
			if (Util.isDistance(mi, o, area) && o instanceof Character)
				// 몬스터 액션 취할필요 없기때문에 -1로 지정.
				onBuff(mi, o, ms, -1, ms.getCastGfx(), false);
		}
	}
	
	/**
	 * target 주위로 area안의 객체에 버프.
	 * 2017-10-29
	 * by all-night
	 */
	static public void onBuff(MonsterInstance mi, object target, MonsterSkill ms, int action, int area) {
		// 액션값이 정해졌을때만 패킷처리.
		if (action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		
		if (ms.getCastGfx() > 0)
			mi.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), mi, ms.getCastGfx()), true);
		
		onBuff(mi, target, ms, -1, ms.getCastGfx(), false);
		
		// 주변객체 검색해서 범위안에있는 애들만 처리.
		for (object o : target.getInsideList()) {
			if (Util.isDistance(mi, o, area) && o instanceof Character && mi.getObjectId() != o.getObjectId())
				// 몬스터 액션 취할필요 없기때문에 -1로 지정.
				onBuff(mi, o, ms, -1, ms.getCastGfx(), false);
		}
	}

	/**
	 * 중복 코드 방지용.
	 * 
	 * @param mi
	 * @param o
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check
	 */
	static public void onBuff(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check) {
		switch (ms.getSkill().getSkillLevel()) {
		case 1:
			switch (ms.getSkill().getSkillNumber()) {
			case 3:
			case 5:
			case 6:
				EnergyBolt.init(mi, o, ms, action, effect);
				break;
			case 4:
				Teleport.init(mi, o, ms, action, effect, check);
				break;
			}
			break;
		case 2:
			switch (ms.getSkill().getSkillNumber()) {
			case 1:
				ChillTouch.init(mi, ms, o, action, effect);
				break;
			case 2:
				CursePoison.init(mi, o, ms, action, effect, check);
				break;
			case 6:
			case 7:
				ShockStun.init(mi, o, ms, action);
				break;
			}
			break;
		case 3:
			switch (ms.getSkill().getSkillNumber()) {
			case 0:
			case 5:
				Lightning.init(mi, ms, o, action, effect);
				break;
			case 3:
				CurseBlind.init(mi, o, ms, action, effect);
				break;
			}
			break;
		case 4:
			switch (ms.getSkill().getSkillNumber()) {
			case 0:
				Lightning.init(mi, ms, o, action, effect);
				break;
			case 2:
				WeaponBreak.init(mi, ms, o, action);
				break;
			case 3:
				ChillTouch.init(mi, ms, o, action, effect);
				break;
			case 4:
				Slow.init(mi, o, ms, action, effect, true);
				break;
			case 5:
				Lightning.init(mi, ms, o, action, effect);
				break;
			}
			break;
		case 5:
			switch (ms.getSkill().getSkillNumber()) {
			case 0:
				CurseParalyze.init(mi, o, ms, action, effect, check);
				break;
			case 1:
			case 5:
				EnergyBolt.init(mi, o, ms, action, effect);
				break;
			case 7:
				CurseBlind.init(mi, o, ms, action, effect);
				break;
			}
			break;
		case 6:
			switch (ms.getSkill().getSkillNumber()) {
			case 2:
				Haste.init(mi, o, ms);
			case 3:
				Cancellation.init(mi, o, ms);
				break;
			case 4:
			case 5:
				EnergyBolt.init(mi, o, ms, action, effect);
				break;
			}
			break;
		case 7:
			switch (ms.getSkill().getSkillNumber()) {
			case 1:
				IceLance.init(mi, o, ms);
				break;
			case 2:
				SummonMonster.init(mi, o, ms, action, effect, check);
				break;
			case 4:
				Tornado.init(mi, ms, action, effect);
				break;
			//case 7:
				//Disease.init(mi, o, ms, action);
				//break;
			}
			break;
		case 8:
			switch (ms.getSkill().getSkillNumber()) {
			case 2:
				Tornado.init(mi, ms, action, effect);
				break;
			case 5:
				Tornado.init(mi, ms, action, effect);
				break;
			case 7:
				Silence.init(mi, o, ms, action);
				break;
			}
			break;
		case 9:
			switch (ms.getSkill().getSkillNumber()) {
			case 5:
				Tornado.init(mi, ms, action, effect);
				break;
			case 6:
				DecayPotion.init(mi, o, ms);
				break;
			}
			break;
		case 10:
			switch (ms.getSkill().getSkillNumber()) {
			case 1:
				Lightning.init(mi, ms, o, action, effect);
				break;
			case 3:
				Tornado.init(mi, ms, action, effect);
				break;
			case 4:
				EnergyBolt.init(mi, o, ms, action, effect);
				break;
			}
			break;
		case 19:
			switch (ms.getSkill().getSkillNumber()) {
			case 7:
				Slow.init(mi, o, ms, action, effect, false);
				break;
			}
			break;	
		case 100:
			switch (ms.getSkill().getSkillNumber()) {
			case 0:
				CurseGhoul.init(mi, o, ms, action, effect, check);
				break;
			case 3:
			case 4:
				CurseParalyze.init(mi, o, ms, action, effect, check);
				break;
			}
			break;
		}
	}

	/**
	 * 주변객체를 검색하여 자신에게로 텔레포트 시키는 함수.
	 * 
	 * @param mi
	 * @param o
	 * @param skill
	 */
	static private void toCall(MonsterInstance mi, object o, MonsterSkill skill) {
		// 초기화
		list.clear();
		list.add(o);
		// 호출하는객체에 좌표는 몬스터좌표내에 +-2 정도로
		int x = mi.getX();
		int y = mi.getY();
		int map = mi.getMap();

		// 선책된 객체 범위값을 참고해서 검색하기.
		// 범위값이 1이상이라면 그주변에 객체도 같이 호출하기위해 검색후 등록처리.
		// 유저만 소환하기.
		if (skill.getRange() > 1) {
			for (object oo : o.getInsideList()) {
				if (!oo.isDead() && !oo.isTransparent() && oo.getGm() == 0 && oo instanceof PcInstance && Util.isDistance(o, oo, skill.getRange()) && !list.contains(oo))
					list.add(oo);
			}
		}
		// 찾은 객체들 처리하기.
		for (object oo : list) {
			// 이팩트값이 존재한다면 그것으로 표현하기.
			if (skill.getCastGfx() > 0)
				oo.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), oo, skill.getCastGfx()), true);
			// 이팩트값이 없다면 기본 텔레포트 이팩트 처리.
			oo.toTeleport(x, y, map, skill.getCastGfx() == 0);
		}
	}
}
