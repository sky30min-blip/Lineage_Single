package lineage.world.object.instance;

import java.sql.Connection;

import all_night.Lineage_Balance;
import lineage.bean.database.ItemSkill;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemSkillDatabase;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectMode;
import lineage.network.packet.server.S_SoundEffect;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.FishingController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.Disease;
import lineage.world.object.magic.EraseMagic;
import lineage.world.object.magic.ShockStun;

public class ItemWeaponInstance extends ItemIllusionInstance {

	private int skill_dmg;
	private int skill_effect;

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemWeaponInstance();
		return item;
	}

	@Override
	public void close() {
		super.close();

		skill_dmg = skill_effect = 0;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (isLvCheck(cha)) {
			if (cha instanceof PcInstance) {
				PcInstance pc = (PcInstance) cha;
			if (isClassCheck(cha)) {
				if ((isEquippedGfx(cha) && PolyDatabase.toEquipped(cha, this)) || equipped || getItem().getType2().equals("fishing_rod")) {
					Inventory inv = cha.getInventory();
					ItemInstance weapon = inv.getSlot(Lineage.SLOT_WEAPON);
					if (weapon != null && weapon.isEquipped() && weapon.getObjectId() != this.getObjectId()) {
						weapon.toClick(cha, null);
				    }
					if (pc.isSound()) {
						if (!equipped)
							 if (getItem().getType2().equalsIgnoreCase("dagger")) {
								 // 단검
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2825)); 
							} else if (getItem().getType2().equalsIgnoreCase("sword")) {
								 // 한손검
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2826)); 
							} else if (getItem().getType2().equalsIgnoreCase("tohandsword") || getItem().getType2().equalsIgnoreCase("axe")) {
								 // 양손검
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2828)); 
							} else if (getItem().getType2().equalsIgnoreCase("spear")) {
								 // 창
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2829)); 
							} else if (getItem().getType2().equalsIgnoreCase("wand") || getItem().getType2().equalsIgnoreCase("staff")) {
								 // 지팡이
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2831)); 
							} else if (getItem().getType2().equalsIgnoreCase("bow")) {
								 // 활
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2835)); 
							} else if (getItem().getType2().equalsIgnoreCase("fishing_rod")) {
								 //낚싯대
								 cha.toSender(S_SoundEffect.clone(BasePacketPooling.getPool(S_SoundEffect.class), 2833)); 
							}
					}
					if (inv != null) {
						if (equipped) {
							if (bless == 2) {
								// \f1그렇게 할 수 없습니다. 저주 받은 것 같습니다.
								cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 150));
								return;
							}
							
							setEquipped(false);
							cha.setRestMode(this, equipped);

							if (getItem().getType2().equalsIgnoreCase("fishing_rod")) {
								cha.setFishStartHeading(0);
								cha.setFishing(false);
								cha.setFishingTime(0L);
								FishingController.setFishEffect(cha);
								ChattingController.toChatting(cha, "낚시를 종료합니다.", Lineage.CHATTING_MODE_MESSAGE);
							}
						} else {
							// 낚시대 및 낚시존 확인
							if (getItem().getType2().equalsIgnoreCase("fishing_rod") && !cha.isFishingZone()) {
								ChattingController.toChatting(cha, "낚시터에서 사용가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
								return;
							}
							if (!cha.isFishing() && getItem().getType2().equalsIgnoreCase("fishing_rod")) {
								// 변신 확인
								if (cha.getGfx() != cha.getClassGfx()) {
									if (cha.isSetPoly) {
										ChattingController.toChatting(cha, "세트 아이템 착용 중 일 경우 낚시가 불가능합니다.", Lineage.CHATTING_MODE_MESSAGE);
										return;
									}
									
									cha.setTempFishing(this);
									// 낚시를 시작하면 변신이 해제됩니다. 계속 하시겠습니까? (y/n)
									cha.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 771));
									return;
								} else {
							        cha.setFishStartHeading(0);
							        cha.setFishing(true);
							        cha.setFishingTime(System.currentTimeMillis());
							        FishingController.setFishEffect(cha);
									ChattingController.toChatting(cha, "낚시를 시작합니다.", Lineage.CHATTING_MODE_MESSAGE);
								}
							}
							if (getItem().isTohand()) {
								if (inv.getSlot(Lineage.SLOT_SHIELD) != null) {
									if (!Lineage.item_equipped_type) {
										// \f1방패를 착용하고서는 두손 무기를 쓸수 없습니다.
										cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 128));
										return;
									} else {
										inv.getSlot(Lineage.SLOT_SHIELD).toClick(cha, null);
									}
								} 
							}
							if (inv.getSlot(Lineage.SLOT_WEAPON) != null) {
								if (Lineage.item_equipped_type && inv.getSlot(Lineage.SLOT_WEAPON).getBless() != 2) {
									inv.getSlot(Lineage.SLOT_WEAPON).toClick(cha, null);
								} else {
									// \f1이미 뭔가를 착용하고 있습니다.
									cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 124));
									return;
								}
							}
							
							setEquipped(true);
							cha.setRestMode(this, equipped);
						}

						toSetoption(cha, true);
						toEquipped(cha, inv);
						toOption(cha, true);
						toBuffCheck(cha);
					}
				} else {
					ChattingController.toChatting(cha, "착용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				if (equipped) {
					setEquipped(false);			
					toSetoption(cha, true);
					toEquipped(cha, cha.getInventory());
					toOption(cha, true);
					toBuffCheck(cha);
				} else {
					// \f1당신의 클래스는 이 아이템을 사용할 수 없습니다.
					cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 264));
				    }
			    }
		    }
	    }
	}
	/**
	 * 무기 착용 및 해제 처리 메서드.
	 */
	@Override
	public void toEquipped(Character cha, Inventory inv) {
		if (inv == null)
			return;

		if (equipped) {
			inv.setSlot(item.getSlot(), this);
			
			if (Lineage.is_weapon_speed) {
				if (!cha.checkSpear()) {
					int baseMode = getBaseGfxMode(cha);
					int weaponMode = item.getGfxMode();
					int targetMode = baseMode + weaponMode;
					// 기본모드 + 무기모드로 계산해 착용/해제가 역연산으로 일치하도록 고정.
					if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), targetMode))
						cha.setGfxMode(targetMode);
					else if (SpriteFrameDatabase.findGfxMode(cha.getGfx(), weaponMode))
						cha.setGfxMode(weaponMode);
					else
						cha.setGfxMode(targetMode);
				}
			} else {
				if (cha.getGfx() == cha.getClassGfx())
					// 변신상태가 아닐때만 변경하도록 함.
					cha.setGfxMode(cha.getGfxMode() + item.getGfxMode());
			}
			
			cha.toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), cha), true);

			if (getBless() == 2) {
				//\f1%0%s 손에 달라 붙었습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 149, getName()));
			}
		} else {
			inv.setSlot(item.getSlot(), null);
			
			if (Lineage.is_weapon_speed) {
				// 해제 시에는 항상 기본모드로 강제 복구 (계산 누적으로 무장 모션 고착 방지)
				cha.setGfxMode(getBaseGfxMode(cha));
			} else {
				if (cha.getGfx() == cha.getClassGfx()) {
					// 변신상태가 아닐때만 변경하도록 함.
					cha.setGfxMode(cha.getGfxMode() - item.getGfxMode());
					cha.toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), cha), true);
				}
			}
			
			cha.toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), cha), true);
		}
			
		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
	}

	private int getBaseGfxMode(Character cha) {
		if ((cha.getGfx() != cha.getClassGfx())
			&& (cha.getGfx() != Lineage.royal_male_gfx && cha.getGfx() != Lineage.royal_female_gfx
			&& cha.getGfx() != Lineage.knight_male_gfx && cha.getGfx() != Lineage.knight_female_gfx
			&& cha.getGfx() != Lineage.elf_male_gfx && cha.getGfx() != Lineage.elf_female_gfx
			&& cha.getGfx() != Lineage.wizard_male_gfx && cha.getGfx() != Lineage.wizard_female_gfx)) {
			try {
				return PolyDatabase.getPolyGfx(cha.getGfx()).getGfxMode();
			} catch (Exception e) {
				return cha.getClassGfxMode();
			}
		}
		return cha.getClassGfxMode();
	}
	
	/**
	 * 무기를 착용할 경우 해당 gfx에 모션이있는지 체크
	 * 2017-11-19
	 * by all-night
	 */
	public boolean isEquippedGfx(Character cha) {
		if (!equipped) {
			if (!cha.isSpearAction(this)) {
				if (!SpriteFrameDatabase.findGfxMode(cha.getGfx(), item.getGfxMode() + Lineage.GFX_MODE_ATTACK))
					return false;
				else
					return true;
			}
		}
		
		return true;
	}

	/**
	 * 리니지 월드에 접속했을때 착용중인 아이템 처리를 위해 사용되는 메서드.
	 */
	@Override
	public void toWorldJoin(Connection con, PcInstance pc) {
		super.toWorldJoin(con, pc);
		if (equipped) {
			toSetoption(pc, false);
			pc.getInventory().setSlot(item.getSlot(), this);
			toOption(pc, false);
		}
	}

	/**
	 * 인첸트 활성화 됫을때 아이템의 뒷처리를 처리하도록 요청하는 메서드.
	 */
	@Override
	public void toEnchant(PcInstance pc, int en) {
		//
		if (en == -125 ||en == -126 || en == -127)
			return;
		//
		if (en != 0) {
			if (isEquipped()) {
				// 성공햇으면서 착용중이면 뭔갈 해줘야할까?
			}
		} else {
			Inventory inv = pc.getInventory();
			if (equipped) {
				setEquipped(false);
				toSetoption(pc, true);
				toEquipped(pc, inv);
				toOption(pc, true);
				toBuffCheck(pc);
			}
			// 인벤에서 제거하면서 메모리도 함께 제거함.
			inv.count(this, 0, true);
		}
		//
		super.toEnchant(pc, en);
	}

	@Override
	public boolean toDamage(Character cha, object o) {
		if (o == null || cha == null || o.isDead() || cha.isDead() || item == null)
			return false;

		// 플러그인 확인.
		Object pco = PluginController.init(ItemWeaponInstance.class, "toDamage", this, cha, o);
		if (pco != null)
			return (Boolean) pco;

		boolean r_bool = false;

		// 체력 스틸하기
		if (o.getNowHp() > 0 && item.getStealHp() > 0 && (o instanceof MonsterInstance || o instanceof PcInstance)) {
			// 1~3랜덤 추출
			int steal_hp = Util.random(1, getEnLevel()) + item.getStealHp();
			
			// 인챈트에 따른 메리트
			if (getEnLevel() > 6) {
				switch (getEnLevel()) {
				case 7:
					steal_hp *= Lineage_Balance.weapon_en_7_damage;
					break;
				case 8:
					steal_hp *= Lineage_Balance.weapon_en_8_damage;
					break;
				case 9:
					steal_hp *= Lineage_Balance.weapon_en_9_damage;
					break;
				case 10:
					steal_hp *= Lineage_Balance.weapon_en_10_damage;
					break;
				case 11:
					steal_hp *= Lineage_Balance.weapon_en_11_damage;
					break;
				case 12:
					steal_hp *= Lineage_Balance.weapon_en_12_damage;
					break;
				case 13:
					steal_hp *= Lineage_Balance.weapon_en_13_damage;
					break;
				case 14:
					steal_hp *= Lineage_Balance.weapon_en_14_damage;
					break;
				case 15:
					steal_hp *= Lineage_Balance.weapon_en_15_damage;
					break;
				}
			}
			
			// 타켓에 hp가 스틸할 값보다 작을경우 현재 가지고있는 hp값으로 변경		
			steal_hp = SkillController.getMrDamage(cha, o, steal_hp, o instanceof Character);
			
			if (o.getNowHp() < steal_hp)
				steal_hp = o.getNowHp();
			// hp제거하기.
			o.setNowHp(o.getNowHp() - steal_hp);
			// hp추가하기.
			cha.setNowHp(cha.getNowHp() + steal_hp);
		}
		
		// 마나 스틸하기
		if (o.getNowMp() > 0 && item.getStealMp() > 0 && (o instanceof MonsterInstance || o instanceof PcInstance)) {
			// 랜덤 추출
			int steal_mp = Util.random(1, getEnLevel()) + item.getStealMp();

			// 인챈트에 따른 메리트
			if (getEnLevel() > 6) {
				switch (getEnLevel()) {
				case 7:
					steal_mp *= Lineage_Balance.weapon_en_7_damage;
					break;
				case 8:
					steal_mp *= Lineage_Balance.weapon_en_8_damage;
					break;
				case 9:
					steal_mp *= Lineage_Balance.weapon_en_9_damage;
					break;
				case 10:
					steal_mp *= Lineage_Balance.weapon_en_10_damage;
					break;
				case 11:
					steal_mp *= Lineage_Balance.weapon_en_11_damage;
					break;
				case 12:
					steal_mp *= Lineage_Balance.weapon_en_12_damage;
					break;
				case 13:
					steal_mp *= Lineage_Balance.weapon_en_13_damage;
					break;
				case 14:
					steal_mp *= Lineage_Balance.weapon_en_14_damage;
					break;
				case 15:
					steal_mp *= Lineage_Balance.weapon_en_15_damage;
					break;
				}
			}

			steal_mp = SkillController.getMrDamage(cha, o, steal_mp, o instanceof Character);

			// 타켓에 mp가 스틸할 값보다 작을경우 현재 가지고있는 mp값으로 변경
			if (o.getNowMp() < steal_mp)
				steal_mp = o.getNowMp();
			// mp제거하기.
			o.setNowMp(o.getNowMp() - steal_mp);
			// mp추가하기.
			cha.setNowMp(cha.getNowMp() + steal_mp);
		}

		// 마법 발동.
		ItemSkill is = ItemSkillDatabase.find(item == null ? "" : item.getName());
		// 마법 발동 확률 체크 하는 부분. 아이템의 기본 확률 + (인첸트레벨 * 인첸트당 추가확률)
		// 발동 인챈트 레벨보다 크거나 같을경우 발동.
		if (is != null && Util.random(1, 100) < (is.getDefaultProbability() + (getEnLevel() * is.getAddEnchantProbability())) && getEnLevel() >= is.getEnLevel()) {
			Skill skill = SkillDatabase.find(is.getSkillUid());
			
			if (skill != null) {
				
				if (cha.무기이펙트){
					skill_effect = skill.getCastGfx();
				}else{
					skill_effect = 0;
				}

				
	/*			if (skill.getUid() == 637) {

					Disease.init2(cha, skill, (int) o.getObjectId());
				}
				if (skill.getUid() == 636) {

					Disease.init2(cha, skill, (int) o.getObjectId());
				} */
				if (skill.getUid() == 16) {
					skill_effect = 0;
					ShockStun.init(cha, skill, o);
				} else {
					// 인트의 영향을 받을 경우
					if (is.isSetInt()) {
						skill_dmg = SkillController.getDamage(cha, o, o, skill, 0, skill.getElement());
						skill_dmg *= is.getRateDmg();

						if (skill_dmg > 0 && o.isBuffEraseMagic())
							BuffController.remove(o, EraseMagic.class);
					} else {
						skill_dmg = (int) Math.round(Util.random(skill.getMindmg(), skill.getMaxdmg()));
						skill_dmg *= is.getRateDmg();
					}
				}
					
				r_bool = true;
			}
		} else {
			skill_effect = 0;
		}
	
		return r_bool;
	}

	@Override
	public int toDamage(int dmg) {
		// 
		PluginController.init(ItemWeaponInstance.class, "toDamage", this, dmg);

		return skill_dmg;
	}

	@Override
	public int toDamageEffect() {
		return skill_effect;
	}

	public void setSkillDmg(int skillDmg) {
		skill_dmg = skillDmg;
	}

	public void setSkillEffect(int skillEffect) {
		skill_effect = skillEffect;
	}
}