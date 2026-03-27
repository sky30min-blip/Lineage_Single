package lineage.world.controller;

import all_night.Lineage_Balance;
import lineage.bean.database.Item;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Inventory;
import lineage.bean.lineage.Kingdom;
import lineage.database.CharactersDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.gui.GuiMain;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.PcRobotInstance;
import lineage.world.object.instance.PetInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.magic.AbsoluteBarrier;
import lineage.world.object.magic.Detection;
import lineage.world.object.magic.EraseMagic;
import lineage.world.object.magic.FogOfSleeping;
import lineage.world.object.magic.Meditation;
import lineage.world.object.magic.PolluteWater;
import lineage.world.object.magic.WaterLife;
import lineage.world.object.npc.BuffNpc;
import lineage.world.object.npc.SpotCrown;
import lineage.world.object.npc.background.Cracker;
import lineage.world.object.npc.background.PigeonGroup;
import lineage.world.object.npc.guard.ElvenGuard;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomCrown;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomGuard;

public final class DamageController {

	/** 파워볼 추첨 발표용 NPC: 플레이어 공격에 HP가 닳지 않도록 */
	static private boolean isImmunePowerballDrawNpc(object target) {
		if (target == null || target.getDatabaseKey() == null)
			return false;
		String k = String.valueOf(target.getDatabaseKey());
		return "powerball_일반볼".equals(k) || "powerball_파워볼".equals(k);
	}

	static public void init() {
		TimeLine.start("DamageController..");
		TimeLine.end();
	}

	/**
	 * 공격이 가해졋을때 공격성공여부를 처리하고 그후 이곳으로 오게 되는데 이부분에서는 데미지공식을 연산해도 되는지 여부를 판단하는 메서드. 예로 활일경우 화살이 없다면 false를 리턴하며, 사이하활인데 화살이 없을경우 true를 리턴한다. - PcInstance쪽에서도 호출해서 사용중. 패킷중에 화살이 없더라도 사이하활은 이팩트를 그려줘야
	 * 하기때문에 해당 메서드를 활용중.
	 */
	static public boolean isAttack(boolean bow, ItemInstance weapon, ItemInstance arrow) {
		if (bow) {
			if (weapon != null) {
				switch (weapon.getItem().getNameIdNumber()) {
				case 1821: // 사이하활
					return true;
				default:
					return arrow != null;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * cha 로부터 o 가 데미지를 입었을때 뒷처리를 담당.
	 * 
	 * @param cha
	 *            : 가격자
	 * @param o
	 *            : 데미지 줄 타격자
	 * @param dmg
	 *            : 데미지
	 * @param type
	 *            : 공격 방식
	 */
	static public void toDamage(Character cha, object o, int dmg, int type) {

		if (dmg <= 0 || cha == null || o == null || cha.isDead() || o.isDead() || o.isLockHigh() || o.isBuffAbsoluteBarrier())
			return;

		if (isImmunePowerballDrawNpc(o))
			return;

		
		// 데미지 입었다는거 알리기.
		o.toDamage(cha, dmg, type);
		// hp 처리
		o.setNowHp(o.getNowHp() - dmg);
		// 투망상태 해제
		Detection.onBuff(cha);
		Detection.onBuff(o);
		// 관련 버프 제거.
		if (o.isBuffMeditation())
			BuffController.remove(o, Meditation.class);
		if (o.isBuffFogOfSleeping())
			BuffController.remove(o, FogOfSleeping.class);
		if (type == Lineage.ATTACK_TYPE_MAGIC) {
			if (o.isBuffEraseMagic())
				BuffController.remove(o, EraseMagic.class);
		}
		// 아머브레이크 들어가는 데미지에 2배
		if (o.isBuffArmorBreak() ) {
			dmg *= 2;
		}
		
		if (o.isDead()) {
			if (cha instanceof PcRobotInstance) {
				PcRobotInstance pr = (PcRobotInstance) cha;
				pr.teleportTime = System.currentTimeMillis() + Util.random(500, 1500);
			}
			
			// 죽엇을경우.
			toDead(cha, o);
			// 죽은거 알리기.
			if (!(cha instanceof PcInstance && o instanceof PcInstance && World.isBattleZone(cha.getX(), cha.getY(), cha.getMap()) && World.isBattleZone(o.getX(), o.getY(), o.getMap())))
				o.toDead(cha);
		} else {
			// 공격자 분류별로 처리
			if (cha instanceof PcInstance) {
				PcInstance pc = (PcInstance) cha;
				// 소환객체에게 알리기.
				SummonController.toDamage(pc, o, dmg);
				// 요정이라면 근처 경비에게 도움 요청.
				toElven(pc, o);
				// 유저이라면 근처 경비에게 도움 요청.
				attackByGuards(pc, o);
				
				attackByGuards1(pc, o);
			}
		}
	}
	
	/**
	 * 기본적으로 공격 불가능한 객체
	 * 2020-07-29
	 * by connector12@nate.com
	 */
	static public boolean 공격불가능한객체(object target) {
		if (target instanceof ItemInstance) 
			return true;
		if (target instanceof BoardInstance) 
			return true;
		if (!(target instanceof Cracker || target instanceof PigeonGroup) && target instanceof BackgroundInstance) 
			return true;
		if (target instanceof ItemInstance) 
			return true;
		if (target instanceof KingdomCrown || target instanceof SpotCrown) 
			return true;
		
		return false;
	}

	/**
	 * 대미지 시스템 중심 부분.
	 */
	static public int getDamage(Character cha, object target, boolean bow, ItemInstance weapon, ItemInstance arrow, int alpha_dmg) {
		double dmg = 0;

		if (target == null)
			return 0;

		//돌골렘 움크리기 상태 어택불가
    	if (cha instanceof PcInstance && target instanceof MonsterInstance) {
    		
    		if (((MonsterInstance) target).getMonster().getName().equalsIgnoreCase("돌골렘") && ((MonsterInstance) target).getGfxMode() == 4) {
    		
    			return 0;

    		}
		}
		// 메디테이션 해제
		if (cha.isBuffMeditation())
			BuffController.remove(cha, Meditation.class);

		// 앱솔상태 해제
		if (cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);


		if (공격불가능한객체(target))
			return 0;

		// 파워볼 전광판 NPC (DB 스폰, database_key가 파워볼전광판_1~6)
		if (target != null && target.getDatabaseKey() != null
				&& String.valueOf(target.getDatabaseKey()).startsWith("파워볼전광판_"))
			return 0;

		if (isImmunePowerballDrawNpc(target))
			return 0;

		if (target instanceof BuffNpc) {
			((BuffNpc) target).toDamage(cha);
			return 0;
		}

		// 굳은상태라면 패스
		if (target.isLockHigh())
			return 0;

		// 앱솔 상태라면 패스
		if (target.isBuffAbsoluteBarrier())
			return 0;

		// 타켓이 죽은상태라면 패스
		if (target.isDead())
			return 0;

		// 자기자신을 공격할 순 없음.
		if (cha.getObjectId() == target.getObjectId())
			return 0;

		// 장애물 방해하는지 확인.
		if (!Util.isAreaAttack(cha, target) || !Util.isAreaAttack(target, cha)) {
			return 0;
		}

		// 내성문이라면 공성중일때만 가능.
		if (target instanceof KingdomDoor) {
			KingdomDoor kd = (KingdomDoor) target;
			if (kd.getKingdom() == null || kd.getNpc() == null)
				return 0;

			if (!kd.getKingdom().isWar() && kd.getNpc().getName().indexOf("내성문") > 0) {
				return 0;
			}
		}

		// 좌표에 객체가 2명 이상일 경우(겹치기) PC에게 대미지 적용 불가
		if (!target.isDead() && !Lineage_Balance.is_fusion_attack && World.getMapdynamic(cha.getX(), cha.getY(), cha.getMap()) > 1 
				&& cha instanceof PcInstance && target instanceof PcInstance 
				&& cha.getObjectId() != target.getObjectId() && !World.isBattleZone(cha.getX(), cha.getY(), cha.getMap()) 
				&& !World.isBattleZone(target.getX(), target.getY(), target.getMap())) {
			ChattingController.toChatting(cha, "좌표에 2명 이상일 경우 공격이 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
			return 0;
		}

		if (target instanceof KingdomCastleTop && cha.getClanId() < 1)
			return 0;
		

		
		Object temp = PluginController.init(DamageController.class, "getDamage", cha, target, bow, weapon, arrow);
		if (temp != null && temp instanceof Double)
			dmg += (Double) temp;

		if (cha instanceof PcInstance) {			
			dmg += toPcInstance((PcInstance) cha, target, bow, weapon, arrow);

		} else if (cha instanceof SummonInstance) {
			dmg += toSummonInstance((SummonInstance) cha, target, bow);

		} else if (cha instanceof MonsterInstance) {
			dmg += toMonsterInstance((MonsterInstance) cha, target, bow);

		} else if (cha instanceof GuardInstance) {
			dmg += toGuardInstance((GuardInstance) cha, target, bow);
		}
		
		if (target instanceof PcInstance && 
			(!World.isSafetyZone(cha.getX(), cha.getY(), cha.getMap()) || World.isBattleZone(cha.getX(), cha.getY(), cha.getMap())) &&
			(!World.isSafetyZone(target.getX(), target.getY(), target.getMap()) || World.isBattleZone(target.getX(), target.getY(), target.getMap()))) {
			if (target.getInventory() != null) {
				ItemInstance targetWeapon = target.getInventory().getSlot(Lineage.SLOT_WEAPON);
				// 카운터배리어 상태라면 일정확률로 데미지 반사.
				if (!bow && target.isBuffCounterBarrier() && Math.random() < Lineage_Balance.count_barrier_knight && targetWeapon != null
						&& (targetWeapon.getItem().getType2().equalsIgnoreCase("tohandsword") || targetWeapon.getItem().isTohand())) {
					// 착용한 양손무기의 (큰 몬스터 타격치 + 추가 대미지 + 인챈트 수치 ) x 3
					double tempDmg = (targetWeapon.getItem().getBigDmg() + targetWeapon.getItem().getAddDmg() + targetWeapon.getEnLevel()) * 2;

					// 리덕션 적용
					Character c = (Character) target;
					Character use = (Character) cha;
					// 리덕션, 리덕션 무시 적용
					tempDmg -= use.getTotalReduction() - c.getDynamicIgnoreReduction() < 0 ? 0 : use.getTotalReduction() - c.getDynamicIgnoreReduction();

					if (cha.isBuffImmuneToHarm()){
						
						tempDmg *= Lineage_Balance.immuneToHarmReduction;
						
					}
			

					toDamage((Character) target, cha, (int) Math.round(tempDmg), Lineage.ATTACK_TYPE_WEAPON);

					target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, 10710), target instanceof PcInstance);

					if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), cha.getGfxMode() + Lineage.GFX_MODE_DAMAGE))
						cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_DAMAGE), target instanceof PcInstance);
					return 0;
				}
			}
		}

		if (dmg <= 0) {
			return 0;
		} else {
			if (alpha_dmg > 0)
				dmg += Util.random(0, alpha_dmg);

			if (cha instanceof Character && target instanceof Character) {
				Character c = (Character) cha;
				Character use = (Character) target;
				// 리덕션, 리덕션 무시 적용
				dmg -= use.getTotalReduction() - c.getDynamicIgnoreReduction() < 0 ? 0 : use.getTotalReduction() - c.getDynamicIgnoreReduction();
			}

			if (cha instanceof PcInstance && target instanceof PcInstance) {
				PcInstance use = (PcInstance) target;
				// PvP 대미지 리덕션
				dmg -= use.getDynamicAddPvpReduction();
			}
			
//			// 버닝스피릿 1.5배
//			if (!bow && cha.isBuffBurningSpirit() && Util.random(1, 100) <= 20)
//				dmg *= 1.5;
//
//			// 더블 브레이크 2배
//			if (!bow && cha.isBuffDoubleBreak() && Util.random(1, 100) <= 20)
//				dmg *= 2;
//
//			// 쿠베라 땁 구현
			boolean burningSpirit = !bow && cha.isBuffBurningSpirit() && Util.random(1, 100) <=10;
			if (burningSpirit) {
			    dmg *= 1.5;
			}

			// 더블 브레이크 2배
			boolean doubleBreak = !bow && cha.isBuffDoubleBreak() && Util.random(1, 100) <=10;
			if (doubleBreak) {
			    dmg *= 2;
			}

			// 버닝스피릿과 더블 브레이크가 모두 발동한 경우
			if (burningSpirit && doubleBreak) {
			    dmg *= 2; 
				cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 13543), true);
				
			}
			
			// 엘리멘탈 파이어 1.5배
			if (!bow && cha.isBuffElementalFire() && Util.random(1, 100) <= 20)
				dmg *= 1.5;

			// 브레이브 멘탈 1.5배
			if (!bow && cha.isBuffBraveMental() && Util.random(1, 100) <= 20)
				dmg *= 1.5;
			
			if (cha instanceof PcInstance) {
				
				if(cha.getInventory().getSlot(Lineage.SLOT_WEAPON) != null){
					if (cha.getInventory().getSlot(Lineage.SLOT_WEAPON).getItem().getType2().equalsIgnoreCase("edoryu") && Util.random(1, 100) <= 20  && weapon != null && weapon.getItem() != null) {
	
						dmg *= 2;
	
						cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 3398), true);
					}
				}

			}

		
			// 화룡의 마안.
			if (!bow && cha.isBuffMaanFire() && Util.random(1, 100) <= 6) {
				dmg += 2;
				
				if (Lineage.마안이팩트여부)
					cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, Lineage.화룡의마안_이팩트));
			}

			if (target instanceof PcInstance) {
				if (target.getInventory() != null) {
					ItemInstance targetArmor = target.getInventory().getSlot(Lineage.SLOT_ARMOR);
					ItemInstance targetShiled = target.getInventory().getSlot(Lineage.SLOT_SHIELD);
								
					if (targetArmor != null && targetArmor.getItem().getName().equalsIgnoreCase("신성한 요정족 판금 갑옷") && Util.random(1, 100) <= Util.random(5, 7)) {
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, 13429), target instanceof PcInstance);
						target.setNowHp(target.getNowHp() + Util.random(30, 60));
					}
					
					if (targetShiled != null && targetShiled.getItem().getName().equalsIgnoreCase("신성한 요정족 방패") && Util.random(1, 100) <= targetShiled.getEnLevel()) {
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, 14543), target instanceof PcInstance);
						dmg -= 10;
					}
					
					if (targetShiled != null && targetShiled.getItem().getName().equalsIgnoreCase("반역자의 방패") && Util.random(1, 100) <= targetShiled.getEnLevel() * 2) {
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.doll_defence_effect), target instanceof PcInstance);
						dmg -= 50;
					}
					
					// 결속된 파푸리온의 가호
					// 힐계열로 인식하기때문에 폴루트 워터, 워터라이프의 영향을 받음.
					if (target.isFafurionArmor() && Util.random(1, 100) <= 10+targetArmor.getEnLevel()) {
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
					if (target.isLindviorArmor() && Util.random(1, 100) <= 5) {
						int mp = targetArmor.getItem().getName().equalsIgnoreCase("린드비오르의 완력") ? 10 : targetArmor.getItem().getName().equalsIgnoreCase("린드비오르의 예지력") ? 15 : 20;
										
						target.setNowMp(target.getNowMp() + mp);
						target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, 2188), target instanceof PcInstance);
					}
					
					
				}
			}
	
			
			// 이뮨투함.
			if (target.isBuffImmuneToHarm())
				dmg *= dmg <= 0 ? 0 : Lineage_Balance.immuneToHarmReduction;
			
			if (cha instanceof PcInstance && target instanceof PcInstance && weapon != null && weapon.getItem() != null) {
		
				if (weapon.getItem().getType2().equalsIgnoreCase("dagger"))
					dmg *= 0.2;
			}

		
			
			// 클래스별  대미지 최종 밸런스
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				dmg *= Lineage_Balance.ROYAL_dmg;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				dmg *= Lineage_Balance.KNIGHT_dmg;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				dmg *= Lineage_Balance.ELF_dmg;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				dmg *= Lineage_Balance.darkElf_dmg;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				dmg *= Lineage_Balance.WIZARD_dmg;
				break;
			}

			if (cha.isDmgCheck && cha instanceof PcInstance) {
				for (PcInstance pc : World.getPcList()) {
					if (pc.getGm() > 0) {
						if (target instanceof PcInstance) {
							String msg = String.format("\\fR[데미지 확인] %s -> %s (데미지: %d)", cha.getName(), target.getName(), (int) Math.round(dmg));
							ChattingController.toChatting(pc, msg, Lineage.CHATTING_MODE_MESSAGE);
							
							if (!Common.system_config_console) {
								long time = System.currentTimeMillis();
								String timeString = Util.getLocaleString(time, true);
								
								msg = String.format("[데미지 확인] %s -> %s (데미지: %d)", cha.getName(), target.getName(), (int) Math.round(dmg));
								String log = String.format("[%s]\t %s", timeString, msg);
								
								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getDamageCheckComposite().toLog(log);
									}
								});
							}
						} else if (target instanceof MonsterInstance) {
							MonsterInstance monster = (MonsterInstance) target;
							String msg = String.format("\\fR[데미지 확인] %s -> %s (데미지: %d)", cha.getName(), monster.getMonster().getName(), (int) Math.round(dmg));
							ChattingController.toChatting(pc, msg, Lineage.CHATTING_MODE_MESSAGE);
							
							if (!Common.system_config_console) {
								long time = System.currentTimeMillis();
								String timeString = Util.getLocaleString(time, true);
								
								msg = String.format("[데미지 확인] %s -> %s (데미지: %d)", cha.getName(), monster.getMonster().getName(), (int) Math.round(dmg));
								String log = String.format("[%s]\t %s", timeString, msg);
								
								GuiMain.display.asyncExec(new Runnable() {
									public void run() {
										GuiMain.getViewComposite().getDamageCheckComposite().toLog(log);
									}
								});
							}
						}
					}
				}
			}
			// 최종 대미지 반올림
			return dmg > 0 ? (int) Math.round(dmg) : 0;
		}
	}

	/**
	 * 사용자 데미지 추출 함수.
	 * 
	 * @param pc
	 * @param target
	 * @param bow
	 * @param weapon
	 * @param arrow
	 * @return
	 */
	static private double toPcInstance(PcInstance pc, object target, boolean bow, ItemInstance weapon, ItemInstance arrow) {
		boolean Small = true;
		double dmg = 0;
		if (World.isAttack(pc, target)) {
			// 장로 변신상태라면 데미지 일정한거 주기.
			if (pc.getGfx() == 3879)
				return Util.random(1, 30);

			// 데미지 연산
			if (isAttack(bow, weapon, arrow) && isHitFigure(pc, target, bow, weapon)) {
				// 큰몹인지 작은몹인지 설정
				if (target instanceof MonsterInstance) {
					MonsterInstance mon = (MonsterInstance) target;
					Small = mon.getMonster().getSize().equalsIgnoreCase("small");
				}
				// 크리티컬 대미지 재편성
				if (Lineage_Balance.is_critical) {
				    // 치명타 발생 여부 결정
				    boolean isCritical = Util.random(0, 99) <= Lineage_Balance.weapon_critical_persent + Critical(pc);

				    if (isCritical) { // 치명타 발생 시
				        // 무작위로 큰 데미지 또는 작은 데미지 선택
				        boolean isBigDamage = Util.random(0, 99) <= 80;

				        if (isBigDamage || target instanceof PcInstance) { // 큰 데미지 선택 또는 대상이 플레이어 캐릭터인 경우
				            dmg = weapon.getItem().getBigDmg() + weapon.getAdd_Max_Dmg() + Lineage_Balance.critical_Max_Dmg;
				            if (Lineage.view_critical_motion)
				                pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, Lineage.critical_effect), true);
				        } else { // 작은 데미지 선택
				            dmg = weapon.getItem().getSmallDmg() + weapon.getAdd_Min_Dmg() + Lineage_Balance.critical_Min_Dmg;
				            if (Lineage.view_critical_motion)
				                pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, Lineage.critical_effect), true);
				        }
				    }
				}
				// 맨손일 경우
				if (weapon == null) {
					if (pc.getGm() > 0)
						return Util.random(0, 10);
					else
						return Util.random(0, 2);
				}

				// 대미지 산출
				dmg += DmgFigure(pc, bow);
				dmg += DmgWeaponFigure(pc, target, bow, weapon, arrow, Small);
				dmg += DmgPlus(pc, weapon, target, bow);
				dmg += DmgElement(weapon, target);
				
				if (target instanceof MonsterInstance) {
					MonsterInstance mon = (MonsterInstance) target;
					if (mon.getMonster().isUndead()) {
						int material = bow && arrow != null ? arrow.getItem().getMaterial() : weapon.getItem().getMaterial();
						// 언덴드 몬스터일경우
						switch (material) {
						case 14: // 은
						case 17: // 미스릴
						case 22: // 오리하루콘
							dmg += Util.random(10, 30);
							dmg += !bow && weapon.isBuffHolyWeapon() ? Util.random(10, 5) : 0;
							break;
						}
					}
				}
				// 무기 손상도 데미지 감소
				dmg -= weapon.getDurability();
			}
		}
		return dmg;
	}

	private static int Critical(PcInstance pc) {
		int level = 0;

		// 레벨에 의한 크리티컬 확률
		if (pc.getLevel() >= 50) {
			level = pc.getLevel() / 20;
		}
		return level;
	}
	
	static private double toSummonInstance(SummonInstance si, object target, boolean bow) {
		Object o = PluginController.init(DamageController.class, "toSummonInstance", si, target, bow);
		if (o != null)
			return (Double) o;

		double dmg = 0;
		// 데미지 연산
		if (World.isAttack(si, target) && isHitFigure(si, target, bow, null)) {
			// 대미지 산출
			dmg += DmgFigure(si, bow);

			if (si instanceof PetInstance)
				dmg += getPetLeveltoDamage(si);
			else
				dmg += getSummonLeveltoDamage(si);
		}

		return dmg * (si instanceof PetInstance ? Lineage_Balance.pet_damage_rate : Lineage_Balance.summon_damage_rate);
	}

	static private double toMonsterInstance(MonsterInstance mi, object target, boolean bow) {
		Object o = PluginController.init(DamageController.class, "toMonsterInstance", mi, target, bow);
		if (o != null)
			return (Double) o;

		double dmg = 0;
		// 데미지 연산
		if (isHitFigure(mi, target, bow, null)) {
			// 대미지 산출
			dmg += DmgFigure(mi, bow);
			dmg += getMonsterLeveltoDamage(mi);
			
			// 언데드 몬스터는 밤일경우 추가 대미지
			if (mi.getMonster().isUndead() && ServerDatabase.isNight())
				dmg *= Util.random(1.1, 1.3);

		}
		
		return dmg * Lineage_Balance.monster_damage_rate;
	}

	static private double toGuardInstance(GuardInstance gi, object target, boolean bow) {
		double dmg = 0;
		// 데미지 연산
		if (Util.random(1, 100) < 50 || isHitFigure(gi, target, bow, null))
			dmg = gi instanceof KingdomGuard ? Util.random(30, 50) : Util.random(100, 120);
		return dmg;
	}

	/**
	 * 객체가 죽엇을때 그에따른 처리를 하는 함수.
	 * 
	 * @param cha
	 *            : 가해자
	 * @param o
	 *            : 피해자
	 */
	static private void toDead(Character cha, object o) {
		PluginController.init(DamageController.class, "toDead", cha, o);
		

		
		// 객체별 처리 구간.
		if (o instanceof PcInstance) {
			PcInstance use = (PcInstance) o;
			Kingdom kingdom = KingdomController.findKingdomLocation(use);
			boolean exp_drop = use.getLevel() >= Lineage.player_dead_expdown_level && use.getMap() != Lineage.teamBattleMap && use.getMap() != Lineage.BattleRoyalMap
					&& !World.isSafetyZone(use.getX(), use.getY(), use.getMap()) && !World.isCombatZone(use.getX(), use.getY(), use.getMap())
					&& (cha instanceof NpcInstance || cha instanceof MonsterInstance || World.isNormalZone(use.getX(), use.getY(), use.getMap()));
			boolean item_drop = use.getLevel() >= Lineage.player_dead_itemdrop_level && use.getMap() != Lineage.teamBattleMap && use.getMap() != Lineage.BattleRoyalMap
					&& !World.isSafetyZone(use.getX(), use.getY(), use.getMap()) && !World.isCombatZone(use.getX(), use.getY(), use.getMap())
					&& (cha instanceof NpcInstance || cha instanceof MonsterInstance || !World.isCombatZone(use.getX(), use.getY(), use.getMap()));
			boolean magic_drop = use.getLawful() < Lineage.NEUTRAL && !World.isSafetyZone(use.getX(), use.getY(), use.getMap()) && !World.isCombatZone(use.getX(), use.getY(), use.getMap())
					&& use.getMap() != Lineage.teamBattleMap && use.getMap() != Lineage.BattleRoyalMap && !World.isSafetyZone(use.getX(), use.getY(), use.getMap());
			boolean kingdom_war = use.getClanGrade() == 3 && // 군주면서
					use.getClanId() > 0 && // 혈이 있으면서
					kingdom != null && // 외성 좌표 안쪽에 있으면서
					kingdom.isWar() && // 해당성이 공성전중이면서
					kingdom.getListWar().contains(use.getClanName()); // 공성전 선포를 한 상태라면.
			for (int map : Lineage.MAP_EXP_NOT_LOSE) {
				if (map == use.getMap())
					exp_drop = false;
			}
			for (int map : Lineage.MAP_ITEM_NOT_DROP) {
				if (map == use.getMap())
					item_drop = false;
			}
			if ((exp_drop || item_drop || magic_drop) && !(use instanceof PcRobotInstance)) {
				boolean 불멸의가호 = 불멸의가호시스템(cha, use, kingdom);
				
				// 경험치 드랍 처리.
				if (exp_drop && 불멸의가호) {
					if (kingdom != null && kingdom.isWar()) {
						if (Lineage.kingdom_player_dead_expdown)
							CharacterController.toExpDown(use);
					} else if (Lineage.player_dead_expdown) {
						CharacterController.toExpDown(use);
					}
				}
				// 아이템 드랍 처리.
				if (item_drop && 불멸의가호) {
					if (kingdom != null && kingdom.isWar()) {
						if (Lineage.kingdom_player_dead_itemdrop)
							CharacterController.toItemDrop(use);
					} else if (Lineage.player_dead_itemdrop) {
						CharacterController.toItemDrop(use);
					}
				}
				
			}
			
			// 공성전 패배 처리.
			if (kingdom_war) {
				// 전쟁 관리 목록에서 제거.
				kingdom.getListWar().remove(use.getClanName());
				// 
				World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, kingdom.getClanName(), use.getClanName()));
			}
		}

		// pvp 처리
		if (cha instanceof PcInstance && o instanceof PcInstance && !World.isBattleZone(cha.getX(), cha.getY(), cha.getMap()) && !World.isBattleZone(o.getX(), o.getY(), o.getMap())) {
			PcInstance pc = (PcInstance) cha;
			PcInstance use = (PcInstance) o;

			// 컴뱃존이 아니면서 카오틱이 아니라면 pc를 피커로 판단. 보라돌이도.
			boolean is = !World.isCombatZone(use.getX(), use.getY(), use.getMap()) && use.getMap() != Lineage.teamBattleMap && pc.getMap() != Lineage.teamBattleMap
					&& use.getMap() != Lineage.BattleRoyalMap && pc.getMap() != Lineage.BattleRoyalMap;
			// 공성존 인지 확인.
			Kingdom kingdom = KingdomController.findKingdomLocation(use);
			// 성존이고 공성중일경우 환경설정에서 피커 처리 하도록 되있을때만 처리하게 유도.
			if (kingdom != null && kingdom.isWar())
				is = Lineage.kingdom_pvp_pk;
			// 혈전상태 확인. (혈전 상태일때 카오처리 할지 여부.)
			Clan clan = ClanController.find(pc);
			if (clan != null && clan.getWarClan() != null && clan.getWarClan().equalsIgnoreCase(use.getClanName()))
				is = false;
			// 피커 처리를 해도 된다면.
			if (is) {
				PluginController.init(DamageController.class, "toPk", pc, use);
				// pkcount 상승
				pc.setPkCount(pc.getPkCount() + 1);
				// pk한 최근 시간값 기록. 만라였다면 한번 바줌.
				if (pc.getLawful() != Lineage.LAWFUL)
					pc.setPkTime(System.currentTimeMillis());
				// 라우풀값 하향.
				// 가해자와 피해자가 라우풀 수치 0이상일 경우
				if (pc.getLawful() < Lineage.NEUTRAL || !use.isBuffCriminal()) {
					if (pc.getLawful() >= Lineage.NEUTRAL && use.getLawful() >= Lineage.NEUTRAL) {
						pc.setLawful(Lineage.NEUTRAL - (pc.getLevel() * 250));
					} else if (pc.getLawful() < Lineage.NEUTRAL && use.getLawful() >= Lineage.NEUTRAL) {
						// 가해자가 카오이고 피해자가 라우풀 수치 0이상일 경우
						pc.setLawful(pc.getLawful() - (pc.getLevel() * 250));
					}
				}

				// 로그 처리.
				CharactersDatabase.updatePvpKill(pc, use);
				CharactersDatabase.updatePvpDead(use, pc);
				//
				if (Lineage.pvp_print_message) {
					String local = Util.getMapName(pc);
					for (PcInstance p : World.getPcList()){
						ChattingController.toChatting(p, String.format(	Lineage.war_ment,pc.getName(), use.getName()), Lineage.CHATTING_MODE_MESSAGE);
						ChattingController.toChatting(p, String.format(	"위치 : %s"  , local ), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			}
		}

		// gvp 처리
		if (cha instanceof GuardInstance && o instanceof PcInstance && cha.getMap() != Lineage.teamBattleMap && o.getMap() != Lineage.teamBattleMap && cha.getMap() != Lineage.BattleRoyalMap
				&& o.getMap() != Lineage.BattleRoyalMap) {
			PcInstance pc = (PcInstance) o;

			// 피케이한 이전기록이 남았을경우 그값을 초기화함.
			if (pc.getPkTime() > 0)
				pc.setPkTime(0);
		}
	}
	
	
	static public boolean 삭제불가마법(int uid) {
		switch (uid) {
		case 77:	// 디스인티그레이트
		case 80:	// 카운터 배리어
		case 121:	// 스트라이커 게일
		case 136:	// 소울 오브 프레임
			return true;
		}
		
		return false;
	}

	/**
	 * 몬스터의 레벨에따른 데미지 산출 메서드.
	 */
	static private int getMonsterLeveltoDamage(MonsterInstance mi) {
		return (int) Math.round(Util.random(mi.getLevel() * Lineage_Balance.monster_level_min_damage_rate, mi.getLevel() * Lineage_Balance.monster_level_max_damage_rate));
	}
	
	/**
	 * 서먼 몬스터의 레벨에따른 데미지 산출 메서드.
	 * 2017-10-05
	 * by all-night
	 */
	static private int getSummonLeveltoDamage(SummonInstance si) {
		return (int) Math.round(Util.random(si.getLevel() * Lineage_Balance.summon_level_min_damage_rate, si.getLevel() * Lineage_Balance.summon_level_max_damage_rate));
	}
	
	/**
	 * 펫 레벨에따른 데미지 산출 메서드.
	 * 2018-4-30
	 * by all-night
	 */
	static private int getPetLeveltoDamage(SummonInstance si) {
		return (int) Math.round(Util.random(si.getLevel() * Lineage_Balance.pet_level_min_damage_rate, si.getLevel() * Lineage_Balance.pet_level_max_damage_rate));
	}

	/**
	 * 속성 데미지 처리 부분.
	 * 
	 * @param item
	 * @param target
	 * @return
	 */
	static private double DmgElement(ItemInstance item, object target) {
		double dmg = 0;
		int fire = 0, earh = 0, wind = 0, warter = 0;
		//
		if (target instanceof Character) {
			Character cha = (Character) target;
			fire = cha.getTotalFireress();
			earh = cha.getTotalEarthress();
			wind = cha.getTotalWindress();
			warter = cha.getTotalWaterress();
		}
		//
		if (item.getItem().getFireress() > 0) {
			double item_dmg = item.getItem().getFireress() + item.getEnFireDamage();
			double el_dmg = (fire * 0.25) * 0.01;
			item_dmg -= Util.random(0, item_dmg * el_dmg);
			dmg += item_dmg;
		}
		if (item.getItem().getEarthress() > 0) {
			double item_dmg = item.getItem().getEarthress() + item.getEnEarthDamage();
			double el_dmg = (earh * 0.25) * 0.01;
			item_dmg -= Util.random(0, item_dmg * el_dmg);
			dmg += item_dmg;
		}
		if (item.getItem().getWindress() > 0) {
			double item_dmg = item.getItem().getWindress() + item.getEnWindDamage();
			double el_dmg = (wind * 0.25) * 0.01;
			item_dmg -= Util.random(0, item_dmg * el_dmg);
			dmg += item_dmg;
		}
		if (item.getItem().getWaterress() > 0) {
			double item_dmg = item.getItem().getWaterress() + item.getEnWaterDamage();
			double el_dmg = (warter * 0.25) * 0.01;
			item_dmg -= Util.random(0, item_dmg * el_dmg);
			dmg += item_dmg;
		}
		//
		return dmg;
	}

	/**
	 * 추가적으로 적용되는 대미지 처리 부분 - 버프상태에 따른처리
	 * 
	 * @param cha
	 *            : 가격자
	 * @param item
	 *            : 착용한 무기
	 * @param target
	 *            : 몬스터인지 유저인지 구분 처리하기위한 객체정보
	 * @return : 계산된 대미지 리턴
	 */
	static private double DmgPlus(Character cha, ItemInstance item, object target, boolean bow) {
		double dmg = 0;

		// PvP 대미지 
		if (cha instanceof PcInstance && target instanceof PcInstance)
			dmg += cha.getDynamicAddPvpDmg();

		// 마법인형 관리
		// 늑대인간, 크러스트 시안
		if (((cha.isMagicdollWerewolf() && !bow) || (cha.isMagicdollHermitCrab() && bow)) && Util.random(1, 100) <= 5) {
			dmg += 15;
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, cha.isMagicdollHermitCrab() ? Lineage.doll_addDmg_effect_white : Lineage.doll_addDmg_effect_black), true);
		}
		
		// 흑장로
		if (cha.isMagicdollBlackElder() && Util.random(1, 100) <= 8) {
			int addDmg = Util.random(Lineage_Balance.magicDoll_black_elder_min_damage, SkillController.getMrDamage(cha, target, Lineage_Balance.magicDoll_black_elder_max_damage, false));
			dmg += addDmg;
			
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.call_lighting_effect), true);
		}
		
		// 데스나이트
		if (cha.isMagicdollDeathKnight() && Util.random(1, 100) <= 9) {
			int addDmg = Util.random(Lineage_Balance.magicDoll_death_knight_min_damage, SkillController.getMrDamage(cha, target, Lineage_Balance.magicDoll_death_knight_max_damage, false));
			dmg += addDmg;
			
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), ((PcInstance) cha).getMagicDollinstance(), Lineage.hell_fire_effect), true);				
		}
		

		return Math.round(dmg);
	}

	/**
	 * 인벤토리에서 무기를 찾은후 무기의 대미지 토탈값 리턴
	 */
	static private double DmgWeaponFigure(PcInstance pc, object target, boolean bow, ItemInstance weapon, ItemInstance arrow, boolean Small) {
		Object o = PluginController.init(DamageController.class, "DmgWeaponFigure", bow, weapon, arrow, Small);
		
		if (o != null)
			return (Double) o;

		double dmg = 0;
		// 버그 방지
		if (weapon == null || !weapon.isEquipped())
			return 0;
		
		// 인챈트 수치는 고정 추가 데미지
		dmg += weapon.getEnLevel();
		
		// 카오틱 영향 받는 무기 추가 대미지
		if (weapon.getItem().isAddCaoticDamage()) {
			int lawful = pc.getLawful() - 65536;

			if (lawful < 0) {			
				if (lawful < -5000)
					dmg += 1;

				if (lawful < -10000)
					dmg += 1;

				if (lawful < -15000)
					dmg += 1;

				if (lawful < -20000)
					dmg += 1;

				if (lawful < -25000)
					dmg += 1;

				if (lawful < -30000)
					dmg += 1;
	
				if (lawful < -32767)
					dmg += 2;
			}
		}
		
		// 기본 데미지 연산값 저장 변수
		int randomDmg = 0;
		int maxDmg = 0;

		if (!bow) {
			// 큰몹 및 작은몹 구분하여 무기의 기본 데미지 추출
			if (Small)
				randomDmg += weapon.getItem().getSmallDmg();
			else
				randomDmg += weapon.getItem().getBigDmg();

			if (weapon.isBuffHolyWeapon())
				dmg += 1;
			if (weapon.isBuffEnchantWeapon())
				dmg += 2;
			if (weapon.isBuffBlessWeapon())
				dmg += 2;
		} else {
			// 활일경우 화살의 대미지 추출
			// 화살이 있을경우.
			if (arrow != null) {
				if (Small)
					randomDmg += arrow.getItem().getSmallDmg();
				else
					randomDmg += arrow.getItem().getBigDmg();
			} else {
				// 사이하의 활일 경우 무형화살 대미지는 10/12
				if (weapon.getItem().getNameIdNumber() == 1821) {
					Item ar = ItemDatabase.find("사이하의 무형 화살");
					
					if (ar != null) {
						if (Small)
							randomDmg += ar.getSmallDmg();
						else
							randomDmg += ar.getBigDmg();
					} else {
						if (Small)
							randomDmg += 10;
						else
							randomDmg += 12;
					}
				}
			}
			
			// 손상몬스터는 화살대미지의 50%만 적용
			if (target instanceof MonsterInstance && randomDmg > 0) {
				MonsterInstance mon = (MonsterInstance) target;
				
				if (mon.getMonster().isToughskin())
					randomDmg *= 0.5;
			}
		}
		
		// 양손 무기 추가 대미지
		if (weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
			dmg *= Lineage_Balance.two_handsword_damage;
		
		// 인챈트에 따른 메리트
		if (weapon.getEnLevel() > 6) {
			switch (weapon.getEnLevel()) {
			case 7:
				dmg *= Lineage_Balance.weapon_en_7_damage;
				break;
			case 8:
				dmg *= Lineage_Balance.weapon_en_8_damage;
				break;
			case 9:
				dmg *= Lineage_Balance.weapon_en_9_damage;
				break;
			case 10:
				dmg *= Lineage_Balance.weapon_en_10_damage;
				break;
			case 11:
				dmg *= Lineage_Balance.weapon_en_11_damage;
				break;
			case 12:
				dmg *= Lineage_Balance.weapon_en_12_damage;
				break;
			case 13:
				dmg *= Lineage_Balance.weapon_en_13_damage;
				break;
			case 14:
				dmg *= Lineage_Balance.weapon_en_14_damage;
				break;
			case 15:
				dmg *= Lineage_Balance.weapon_en_15_damage;
				break;
			}
		}

		// 인챈트 수치에 따라 최대값이 나오는 확률을 위해 필요한 변수
		maxDmg = randomDmg;

		// 추출된 무기데미지를 랜덤값으로 추출 / 소울 오브 프레임 사용시 랜덤 추출 제외
		if (randomDmg > 0) {
			if (pc.isBuffSoulOfFlame() && !bow) {
				// 소울오브프레임 상태일 경우 대미지는 항상 일정
			} else {
				randomDmg = Util.random(1, randomDmg);
				
				if(weapon.getItem().getType2().equalsIgnoreCase("claw") && Util.random(0, 99) < (weapon.getEnLevel() * 2) + pc.getTotalCritical(bow)){
					randomDmg = maxDmg;
					pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 3671), true);
				}
				// 양손 무기는 치명타 확률이 더 높음
				if (weapon.getItem().getType2().equalsIgnoreCase("tohandsword") ) {
					
				
					// 인챈 레벨 * 4 + 캐릭터의 순수 치명타 확률로 무기의 최대 대미지.
					if (Util.random(0, 99) < (weapon.getEnLevel() * 2) + pc.getTotalCritical(bow))
						randomDmg = maxDmg;
				} else {
					// 인챈 레벨 * 2 + 캐릭터의 순수 치명타 확률로 무기의 최대 대미지.
					if (Util.random(0, 99) < (weapon.getEnLevel() * 2) + pc.getTotalCritical(bow))
						randomDmg = maxDmg;
				}
			}
		}
		
		// 화살이 없을경우 버그 방지 사이하의 활은 제외
		if (bow && arrow == null && weapon.getItem().getNameIdNumber() != 1821) {
			dmg = 0;
			randomDmg = 0;
			maxDmg = 0;
		}
		
		// 크리티컬 이팩트
		if (Lineage.is_critical_effect && randomDmg == maxDmg && randomDmg > 0 && maxDmg > 0)
			pc.setCriticalEffect(true);
		
		return dmg + randomDmg;
	}

	/**
	 * 해당 객체의 스탯정보만으로 총 대미지 산출.
	 */
	static private double DmgFigure(Character cha, boolean bow) {
		Object temp = PluginController.init(DamageController.class, "DmgFigure", cha, bow);

		if (temp != null && temp instanceof Double)
			return (Double) temp;

		double dmg = 0;

		if (bow)
			dmg += CharacterController.toStatDex(cha, "DmgFigure");
		else
			dmg += CharacterController.toStatStr(cha, "DmgFigure");

		// 순수 스탯 대미지 + 10레벨당 추타+1
		return dmg;
	}

	/**
	 * 공격 성공여부 처리
	 */
	static private boolean isHitFigure(Character cha, object target, boolean bow, ItemInstance weapon) {
		// 허수아비는 무조건 성공
		if (target instanceof Cracker)
			return true;
		if (bow && weapon == null && target instanceof PcInstance)
			return true;

		// 지룡/탄생/형상/생명의 마안
		if ((target.isBuffMaanEarth() || target.isBuffMaanLife() || target.isBuffMaanBirth() || target.isBuffMaanShape()) && Util.random(1, 100) <= 6) {
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

			return false;
		}

		// 공격성공율
		// 힘40 / 덱스 40 기준으로 제작하였으므로, 근거리 명중, 원거리 명중치를 빼고 계산
		// by all_night
		double probability = 100 - (bow ? 32 : 28);
		double tempProbability = 0;

		double tmp1 = 0;
		double tmp2 = 0;
		// 무기 인첸
		if (weapon != null)
			probability += weapon.getEnLevel();

		// 무기 인첸에 따른 공격 성공률
		if (weapon != null && weapon.getEnLevel() > 0) { // 1
			switch (weapon.getEnLevel()) {
			case 1:
				probability += 0;
				break;
			case 2:
				probability += 1;
				break;
			case 3:
				probability += 1;
				break;
			case 4:
				probability += 2;
				break;
			case 5:
				probability += 2;
				break;
			case 6:
				probability += 3;
				break;
			case 7:
				probability += 3;
				break;
			case 8:
				probability += 4;
				break;
			case 9:
				probability += 4;
				break;
			case 10:
				probability += 5;
				break;
			case 11:
				probability += 5;
				break;
			case 12:
				probability += 6;
				break;
			case 13:
				probability += 6;
				break;
			case 14:
				probability += 7;
				break;
			case 15:
				probability += 7;
				break;
			}
		} // 무기 공격 성공 본섭화 - 인챈트 +2 당 무기 명중 +1
		if (bow) {
			// 스탯에 따른 보정
			probability += CharacterController.toStatDex(cha, "isHitFigure");
		} else {
			if (weapon != null) {
				// 홀리웨폰 근거리 명중+1
				if (weapon.isBuffHolyWeapon())
					probability += 1;
				// 블레스웨폰 근거리 명중+2
				if (weapon.isBuffBlessWeapon())
					probability += 2;
			}
			// 스탯에 따른 보정
			probability += CharacterController.toStatStr(cha, "isHitFigure");
		}

		if (target instanceof Character) {
			if (bow) {
				// 활공격시 타켓에 er값을 추출함.
				Character c = (Character) target;
				tempProbability = Math.round(acProbability(c, bow)) + getEr(c);

				if (c.isBuffStrikerGale())
					tempProbability /= 3;
			} else {
				// 근거리공격시 타켓에 ac따른 값 추출
				Character c = (Character) target;
				tempProbability = acProbability(c, bow);

				// 보스는 유저 공격시 좀더 잘박히게 설정.
				if (cha instanceof MonsterInstance && target instanceof PcInstance) {
					MonsterInstance boss = (MonsterInstance) cha;
					if (boss != null && boss.getMonster() != null && boss.getMonster().getBossClass().contains("보스"))
						tempProbability /= 2;
				}
			}
		}

		// 피격시 AC 적용률
		if (target instanceof PcInstance) {
			switch (target.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				if (bow)
					tempProbability *= Lineage_Balance.pc_bow_hit_ac_royal_percent;
				else
					tempProbability *= Lineage_Balance.pc_hit_ac_royal_percent;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if (bow)
					tempProbability *= Lineage_Balance.pc_bow_hit_ac_knight_percent;
				else
					tempProbability *= Lineage_Balance.pc_hit_ac_knight_percent;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if (bow)
					tempProbability *= Lineage_Balance.pc_bow_hit_ac_elf_percent;
				else
					tempProbability *= Lineage_Balance.pc_hit_ac_elf_percent;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if (bow)
					tempProbability *= Lineage_Balance.pc_bow_hit_ac_wizard_percent;
				else
					tempProbability *= Lineage_Balance.pc_hit_ac_wizard_percent;
				break;
			}
		}

		if (bow) {
			if (cha instanceof MonsterInstance)
				tempProbability *= Lineage_Balance.monster_bow_hit_rate;
			else if (cha instanceof SummonInstance)
				tempProbability *= Lineage_Balance.summon_bow_hit_rate;
			else if (cha instanceof PcInstance)
				tempProbability *= Lineage_Balance.pc_bow_hit_rate;
		} else {
			if (cha instanceof MonsterInstance)
				tempProbability *= Lineage_Balance.monster_hit_rate;
			else if (cha instanceof SummonInstance)
				tempProbability *= Lineage_Balance.summon_hit_rate;
			else if (cha instanceof PcInstance)
				tempProbability *= Lineage_Balance.pc_hit_rate;
		}
		// 1레벨 차이당 1%감소
		if (cha instanceof MonsterInstance) {
			if (cha.getLevel() < target.getLevel()) {

				tmp1 = (cha.getLevel() - target.getLevel());
				probability += tmp1;

			} else {
				tmp2 = (target.getLevel() - cha.getLevel());
				tempProbability += tmp2;
			}

		}

		if (weapon != null && !bow) {
			if (weapon.getItem().getType2().equalsIgnoreCase("tohandsword"))
				probability *= 1;
		}

		probability -= tempProbability;

		if (probability < 5)
			probability = 5;

		boolean result = Util.random(1, 100) <= (probability < 1 ? 1 : Math.round(probability));

		if (!result) {
			// 크리티컬 이팩트 초기화
			cha.setCriticalEffect(false);

			if (Lineage.is_miss_effect)
				target.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), target, Lineage.miss_effect), true);
		}

		return result;
	}

	static private double acProbability(Character target, boolean bow) {
		double probability = 0;
		
	
		for (int i = 0; i < target.getTotalAc(); i++) {
			if (i < 10)
				probability += 0.5;
			else if (i < 20)
				probability += 0.7;
			else if (i < 30)
				probability += 0.8;
			else if (i < 40)
				probability += 0.85;
			else if (i < 50)
				probability += 0.85;
			else if (i < 60)
				probability += 0.85;
			else if (i < 70)
				probability += 0.85;
			else if (i < 80)
				probability += 0.9;
			else if (i < 90)
				probability += 0.9;
			else if (i < 100)
				probability += 1;
			else if (i < 110)
				probability += 1;
			else if (i < 120)
				probability += 1;
			else if (i < 130)
				probability += 1;
			else if (i < 140)
				probability += 1;
			else if (i < 150)
				probability += 1;
			else if (i < 160)
				probability += 1;
			else
				probability += 1;
		}
		
	
		
		return probability;
	}

	/**
	 * 장거리 공격 회피율 리턴
	 */
	static public int getEr(Character cha) {
		double total_er = (cha.getTotalDex() / 2) + cha.getDynamicEr();

		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
		case Lineage.LINEAGE_CLASS_ELF:
		case Lineage.LINEAGE_CLASS_DARKELF:
			total_er += cha.getLevel() / 6;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			total_er += cha.getLevel() / 4;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			total_er += cha.getLevel() / 10;
			break;
		}

		return (int) Math.round(total_er);
	}
	//2023 쿠베라 er 개편
	public static int toOriginalStatER(Character cha) {
		int sum = 0;

		if (cha.getTotalDex() < 8) {
			sum = -1;
		} else {
			sum += (cha.getTotalDex() - 8) / 2;
		}

		int dex = cha.getDex();
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL:
			dex -= 10;
			if (dex >= 4)
				sum += 1;
			if (dex >= 6)
				sum += 1;
			if (dex >= 8)
				sum += 1;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			dex -= 12;
			if (dex >= 2)
				sum += 1;
			if (dex >= 4)
				sum += 1;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			dex -= 7;
			if (dex >= 2)
				sum += 1;
			if (dex >= 4)
				sum += 1;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			dex -= 15;
			if (dex >= 1)
				sum += 2;
			break;
		}

		return sum;
	}

	/**
	 * 요정 클레스 요숲경비병에게 도움처리 함수. : 요정은 요숲에서 사냥시 근처 요숲경비가잇을경우 도움을 줌.
	 */
	static private void toElven(PcInstance pc, object o) {
		if (pc.getClassType() == Lineage.LINEAGE_CLASS_ELF && o instanceof MonsterInstance && !(o instanceof SummonInstance)) {
			for (object inside : pc.getInsideList()) {
				if (inside instanceof ElvenGuard)
					inside.toDamage((Character) o, 0, Lineage.ATTACK_TYPE_DIRECT);
			}
		}
	}
	
	/**
	 * 일반 클레스 경비병에게 도움처리 함수.
	 */
	static public void attackByGuards1(PcInstance pc, object o) {
		if (isTarget(o)) {
			if (((MonsterInstance) o).getMonster().isBoss())
				return;

			for (object inside : pc.getInsideList()) {
				if (inside instanceof GuardInstance)
					inside.toDamage((Character) o, 0, Lineage.ATTACK_TYPE_DIRECT);
			}
		}
	}
	
	/**
	 * 일반 클레스 경비병에게 도움처리 함수.
	 */
	static public void attackByGuards(PcInstance pc, object o) {
		if (isClass(pc.getClassType()) && isTarget(o)) {
			if (((MonsterInstance) o).getMonster().isBoss())
				return;

			for (object inside : pc.getInsideList()) {
				if (inside instanceof GuardInstance)
					inside.toDamage((Character) o, 0, Lineage.ATTACK_TYPE_DIRECT);
			}
		}
	}

	static private boolean isClass(int classType) {
		return classType == Lineage.LINEAGE_CLASS_ROYAL || classType == Lineage.LINEAGE_CLASS_KNIGHT || classType == Lineage.LINEAGE_CLASS_ELF || classType == Lineage.LINEAGE_CLASS_WIZARD;
	}

	static private boolean isTarget(object o) {
		return o instanceof MonsterInstance && !(o instanceof SummonInstance);
	}
	/**
	 * 근처 경비병에게 도움요청처리하는 함수. : 다른 놈에게 pk를 당하거나 할때 처리하는 함수.
	 * 
	 * @param pc
	 *            : 요청자
	 * @param cha
	 *            : 공격자
	 */
	static public void toGuardHelper(PcInstance pc, Character cha) {
		// 요청자가 카오라면 무시.
		if (pc.getLawful() < Lineage.NEUTRAL)
			return;
		// 요청자가 보라돌이 상태라면 무시. 단! 가격자가 카오가 아닐때만. 
		if (pc.isBuffCriminal() && cha.getLawful() >= Lineage.NEUTRAL)
			return;
		// 사용자가 가격햇고 노말존에 잇엇을경우.
		if (World.isNormalZone(pc.getX(), pc.getY(), pc.getMap())) {
			for (object inside : pc.getInsideList()) {
				if (inside instanceof GuardInstance)
					inside.toDamage(cha, 0, Lineage.ATTACK_TYPE_DIRECT);
			}
		}
	}
	
	/**
	 * 가해자와 피해자가 유저일 경우에만 적용.
	 * 2020-05-05
	 * by connector12@nate.com
	 */
	static public boolean 불멸의가호시스템(Character cha, PcInstance use, Kingdom k) {
		if (Lineage.is_immortality_pvp && !(cha instanceof PcInstance)) {
			return true;
		}
		
		try {
			if (!Lineage.is_immortality || cha == null || use == null || cha.getInventory() == null || use.getInventory() == null) {
				return true;
			}

			if (k != null && k.isWar())
				return true;

			Inventory iv = use.getInventory();
			ItemInstance 불멸의가호 = null;

			for (ItemInstance i : iv.getList()) {
				if (i != null && i.getItem() != null && i.getItem().getName().equalsIgnoreCase(Lineage.immortality_item_name)) {
					불멸의가호 = i;
					break;
				}
			}

			if (불멸의가호 != null) {
				iv.count(불멸의가호, 불멸의가호.getCount() - 1, true);
				ChattingController.toChatting(use, "\\fR불멸의 가호로 사망 패널티를 받지않았습니다.", Lineage.CHATTING_MODE_MESSAGE);

				if (!Lineage.is_immortality_kill_item_drop_monster && cha instanceof MonsterInstance) {
					return false;
				}
				
				if (Lineage.is_immortality_kill_item) {
					try {
						Item i = ItemDatabase.find(Lineage.immortality_kill_item_name);
						long count = 1;

						if (i != null) {
							if (Lineage.is_immortality_kill_item_drop || cha instanceof MonsterInstance) {
								for (int idx = 0; idx < count; idx++) {
									ItemInstance ii = ItemDatabase.newInstance(i);
									ii.setObjectId(ServerDatabase.nextItemObjId());
									ii.setCount(count);

									int x = Util.random(use.getX() - 1, use.getX() + 1);
									int y = Util.random(use.getY() - 1, use.getY() + 1);
									int map = use.getMap();

									if (World.isThroughObject(x, y + 1, map, 0))
										ii.toTeleport(x, y, map, false);
									else
										ii.toTeleport(use.getX(), use.getX(), map, false);
									// 드랍됫다는거 알리기.
									ii.toDrop(null);
								}
							} else {
								if (cha instanceof PcInstance) {
									PcInstance pc = (PcInstance) cha;
									ItemInstance temp = pc.getInventory().find(i.getName(), 1, i.isPiles());

									if (temp != null && (temp.getBless() != 1 || temp.getEnLevel() != 0))
										temp = null;

									if (temp == null) {
										// 겹칠수 있는 아이템이 존재하지 않을경우.
										if (i.isPiles()) {
											temp = ItemDatabase.newInstance(i);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											temp.setBless(1);
											temp.setEnLevel(0);
											temp.setCount(count);
											temp.setDefinite(true);
											pc.getInventory().append(temp, true);
										} else {
											for (int idx = 0; idx < count; idx++) {
												temp = ItemDatabase.newInstance(i);
												temp.setObjectId(ServerDatabase.nextItemObjId());
												temp.setBless(1);
												temp.setEnLevel(0);
												temp.setDefinite(true);
												pc.getInventory().append(temp, true);
											}
										}
									} else {
										// 겹치는 아이템이 존재할 경우.
										pc.getInventory().count(temp, temp.getCount() + count, true);
									}

									ChattingController.toChatting(pc, String.format("\\fR[불멸의 가호 보상] %s 획득.", i.getName()), Lineage.CHATTING_MODE_MESSAGE);
								}
							}
						}
					} catch (Exception e) {
						lineage.share.System.printf("%s : 불멸의 가호 보상 지급 에러. 캐릭명: %s\r\n", DamageController.class.toString(), cha.getName());
						lineage.share.System.println(e);
					}
				}
				return false;
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : 불멸의 가호 에러. 캐릭명: %s\r\n", DamageController.class.toString(), use.getName());
			lineage.share.System.println(e);
		}
		return true;
	}
}
