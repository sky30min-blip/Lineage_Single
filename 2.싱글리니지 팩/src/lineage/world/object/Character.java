package lineage.world.object;

import lineage.bean.database.Monster;
import lineage.database.MonsterDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.AgitController;
import lineage.world.controller.TeamBattleController;
import lineage.world.controller.BuffController;
import lineage.world.controller.CharacterController;
import lineage.world.controller.ChattingController;
import lineage.world.controller.ElvenforestController;
import lineage.world.controller.InnController;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.Wafer;
import lineage.world.object.npc.background.DeathEffect;
import lineage.world.object.npc.background.RestCracker;

public class Character extends object {

	protected int level;
	private int nowHp;
	protected int maxHp;
	private int dynamicHp;
	private int nowMp;
	protected int maxMp;
	private int dynamicMp;
	private int ac;
	private int dynamicAc;
	private int Str;
	private int Con;
	private int Dex;
	private int Wis;
	private int Int;
	private int Cha;
	private int dynamicInt;
	private int dynamicStr;
	private int dynamicCon;
	private int dynamicDex;
	private int dynamicWis;
	private int dynamicCha;
	private int dynamicMr;
	private int dynamicSp;
	private int dynamicTicHp;
	private int dynamicTicMp;
	private int dynamicHpPotion;
	private int dynamicCritical;
	private int dynamicBowCritical;
	private int dynamicMagicCritical;
	private double exp;
	private int food;
	private double itemWeight; // 동적인 아이템을 더 들수있게 하는 변수.
	private int TimeHpTic; // 자연회복에 사용되는 변수
	private int TimeMpTic; // 자연회복에 사용되는 변수
	private int magicdollTimeHpTic; // 마법인형 회복에 사용되는 변수
	private int magicdollHpTic;
	private int magicdollTimeMpTic; //마법인형 회복에 사용되는 변수
	private int magicdollMpTic;
	private boolean hpMove; // 자연회복에 사용되는 변수
	private boolean mpMove; // 자연회복에 사용되는 변수
	private boolean hpFight; // 자연회복에 사용되는 변수
	private boolean mpFight; // 자연회복에 사용되는 변수
	private ItemInstance tempItem; // 환골탈태 YES NO 메시지에 사용되는 임시 변수
	protected int lvStr; // 51이상 부터 찍는 레벨스탯 변수.
	protected int lvCon;
	protected int lvDex;
	protected int lvWis;
	protected int lvInt;
	protected int lvCha;

	private int dynamicEarthress; // 땅 저항력
	private int dynamicWaterress; // 물 저항력
	private int dynamicFireress; // 불 저항력
	private int dynamicWindress; // 바람 저항력
	private int setitemEarthress; // 땅 저항력
	private int setitemWaterress; // 물 저항력
	private int setitemFireress; // 불 저항력
	private int setitemWindress; // 바람 저항력
	private int earthress; // 땅 저항력
	private int waterress; // 물 저항력
	private int fireress; // 불 저항력
	private int windress; // 바람 저항력
	private int reduction; // 리덕션(공격당한 데미지 감소)
	private int dynamicReduction; // 리덕션(공격당한 데미지 감소)
	private int dynamicIgnoreReduction; // 리덕션 무시

	// 버프 딜레이 확인용 변수.
	public long delay_magic;
	public long delay_bandel;
	
	// 마법 사용 시간
	public long magic_time;
	// 동적 데미지 추가연산 처리 변수.
	private int dynamicAddDmg; // 타격치
	private int dynamicAddDmgBow; // 활타격치
	private int dynamicAddPvpDmg; // 대인전 타격치
	private int dynamicAddPvpReduction; // 대인전 타격치
	private int dynamicAddHit; // 공성
	private int dynamicAddHitBow; // 활명중
	private int dynamicEr; // 장거리 회피율
	private int dynamicDg; // 근거리 회피율
	private double dynamicStunHit; // 스턴명중
	private double dynamicStunResist; // 스턴저항
	private double dynamicElfResist; // 정령 내성
	private int dynamicMagicDmg; // 마법 대미지
	private int dynamicMagicHit; // 마법 명중
	// 허수아비 평균 데미지 연산을 위한 변수
	private ItemInstance weapon;
	private int dmg;
	private int hitCount;
	private int minDmg;
	private int maxDmg;
	private long lastHitTime;
	//혈맹 지휘 채팅을 위한 변수
	private boolean isClanOrder;
	// 낚시를 위한 변수
	private boolean isFishing;
	private long fishingTime;
	private ItemInstance tempFishing;
	private BackgroundInstance fishEffect;
	private int fishStartHeading;

	// 스탯포인트를 위한 변수
	private int levelUpStat;
	private int resetBaseStat;
	private int resetLevelStat;
	// 리덕션 아머 리덕션 변수
	private int reductionAromr;
	// 빠른변신을 위한 변수
	private String quickPolymorph;
	// 물약 딜레이를 체크하기위한 변수
	private long clickHealingPotionTime;
	// 축복받은 방어구 세트 효과 적용 여부를 확인하기위한 변수
	private boolean isBlessArmor;
	// 축복받은 장신구 세트 효과 적용 여부를 확인하기위한 변수
	private boolean isBlessAcc;

	public int getTotalEr() { return dynamicEr; }
	
	private Object sync_dynamic = new Object();
	protected Object sync_hp = new Object();
	protected Object sync_mp = new Object();
	protected Object sync_exp = new Object();
	
	public DeathEffect backGround;
	public boolean armorEnchant8;
	public boolean armorEnchant9;
	public boolean armorEnchant10;


	public Character() {
		//
	}

	@Override
	public void close() {
		super.close();
		level = nowHp = maxHp = dynamicHp = nowMp = maxMp = dynamicMp = ac = dynamicAc = Str = Con = Dex = Wis = Int = Cha = dynamicInt = dynamicStr = dynamicCon = dynamicDex = dynamicWis = dynamicCha = dynamicTicHp = dynamicTicMp = lvStr = lvCon = lvDex = lvWis = lvInt = lvCha = dynamicSp = dynamicMr = food = dynamicEarthress = dynamicWaterress = dynamicFireress = dynamicWindress = earthress = waterress = fireress = windress = dynamicAddDmg =0;
		itemWeight = dynamicStunHit = dynamicStunResist = exp = setitemEarthress = setitemWaterress = setitemFireress = setitemWindress = dynamicAddDmgBow = dynamicAddHit = dynamicAddHitBow = reduction = dynamicReduction = dynamicEr = dynamicDg = dynamicHpPotion = dynamicAddPvpDmg = dynamicAddPvpReduction = dynamicIgnoreReduction = 0;
		hpMove = mpMove = hpFight = mpFight = isClanOrder = isFishing = isBlessArmor = isBlessAcc = false;
		magic_time = delay_magic = dmg = hitCount = minDmg = maxDmg = magicdollHpTic = magicdollMpTic = 0;
		fishStartHeading = 0;
		tempItem = weapon = tempFishing = null;
		dynamicCritical = dynamicBowCritical = dynamicMagicCritical = dynamicMagicDmg = dynamicMagicHit = 0;
		levelUpStat = resetBaseStat = resetLevelStat = 0;
		lastHitTime = fishingTime = clickHealingPotionTime = 0L;
		reductionAromr = 0;
		quickPolymorph = "";
		armorEnchant8 = armorEnchant9 =  armorEnchant10 = false;
		backGround = null;
		fishEffect = null;
		TimeHpTic = getHpTime();
	      TimeMpTic = getMpTime();
		dynamicElfResist = 0;
		delay_bandel=0;
	}

	public double getDynamicElfResist() {
		return dynamicElfResist;
	}

	public void setDynamicElfResist(double dynamicElfResist) {
		this.dynamicElfResist = dynamicElfResist;
	}
	
	public boolean isBlessAcc() {
		return isBlessAcc;
	}

	public void setBlessAcc(boolean isBlessAcc) {
		this.isBlessAcc = isBlessAcc;
	}
	
	public boolean isBlessArmor() {
		return isBlessArmor;
	}

	public void setBlessArmor(boolean isBlessArmor) {
		this.isBlessArmor = isBlessArmor;
	}
	
	public long getClickHealingPotionTime() {
		return clickHealingPotionTime;
	}

	public void setClickHealingPotionTime(long clickHealingPotionTime) {
		this.clickHealingPotionTime = clickHealingPotionTime;
	}
	
	public String getQuickPolymorph() {
		return quickPolymorph;
	}

	public void setQuickPolymorph(String quickPolymorph) {
		this.quickPolymorph = quickPolymorph;
	}
	
	public int getDynamicMagicDmg() {
		return dynamicMagicDmg;
	}

	public void setDynamicMagicDmg(int dynamicMagicDmg) {
		this.dynamicMagicDmg = dynamicMagicDmg;
	}

	
	public int getDynamicMagicHit() {
		return dynamicMagicHit;
	}

	public void setDynamicMagicHit(int dynamicMagicHit) {
		this.dynamicMagicHit = dynamicMagicHit;
	}
	
	public double getDynamicStunHit() {
		return dynamicStunHit;
	}

	public void setDynamicStunHit(double dynamicStunHit) {
		this.dynamicStunHit = dynamicStunHit;
	}
	
	public int getMagicdollTimeHpTic() {
		return magicdollTimeHpTic;
	}

	public void setMagicdollTimeHpTic(int magicdollTimeHpTic) {
		this.magicdollTimeHpTic = magicdollTimeHpTic;
	}

	public int getMagicdollTimeMpTic() {
		return magicdollTimeMpTic;
	}

	public void setMagicdollTimeMpTic(int magicdollTimeMpTic) {
		this.magicdollTimeMpTic = magicdollTimeMpTic;
	}

	public int getReductionAromr() {
		return reductionAromr;
	}

	public void setReductionAromr(int reductionAromr) {
		this.reductionAromr = reductionAromr;
	}

	public int getDynamicCritical() {
		return dynamicCritical;
	}

	public void setDynamicCritical(int dynamicCritical) {
		this.dynamicCritical = dynamicCritical;
	}

	public int getDynamicBowCritical() {
		return dynamicBowCritical;
	}

	public void setDynamicBowCritical(int dynamicBowCritical) {
		this.dynamicBowCritical = dynamicBowCritical;
	}

	public int getDynamicMagicCritical() {
		return dynamicMagicCritical;
	}

	public void setDynamicMagicCritical(int dynamicMagicCritical) {
		this.dynamicMagicCritical = dynamicMagicCritical;
	}

	public int getDynamicAddPvpReduction() {
		return dynamicAddPvpReduction;
	}

	public void setDynamicAddPvpReduction(int dynamicAddPvpReduction) {
		this.dynamicAddPvpReduction = dynamicAddPvpReduction;
	}

	public int getDynamicAddPvpDmg() {
		return dynamicAddPvpDmg;
	}

	public void setDynamicAddPvpDmg(int dynamicAddPvpDmg) {
		this.dynamicAddPvpDmg = dynamicAddPvpDmg;
	}

	public int getDynamicHpPotion() {
		return dynamicHpPotion;
	}

	public void setDynamicHpPotion(int dynamicHpPotion) {
		this.dynamicHpPotion = dynamicHpPotion;
	}

	public int getResetLevelStat() {
		return resetLevelStat;
	}

	public void setResetLevelStat(int resetLevelStat) {
		this.resetLevelStat = resetLevelStat;
	}

	public int getResetBaseStat() {
		return resetBaseStat;
	}

	public void setResetBaseStat(int resetBaseStat) {
		this.resetBaseStat = resetBaseStat;
	}

	public int getLevelUpStat() {
		return levelUpStat;
	}

	public void setLevelUpStat(int levelUpStat) {
		this.levelUpStat = levelUpStat;
	}

	public double getDynamicStunResist() {
		return dynamicStunResist;
	}

	public void setDynamicStunResist(double dynamicStunResist) {
		this.dynamicStunResist = dynamicStunResist;
	}

	public int getFishStartHeading() {
		return fishStartHeading;
	}

	public void setFishStartHeading(int fishStartHeading) {
		this.fishStartHeading = fishStartHeading;
	}

	public BackgroundInstance getFishEffect() {
		return fishEffect;
	}

	public void setFishEffect(BackgroundInstance fishEffect) {
		this.fishEffect = fishEffect;
	}
	
	public ItemInstance getTempFishing() {
		return tempFishing;
	}

	public void setTempFishing(ItemInstance tempFishing) {
		this.tempFishing = tempFishing;
	}

	public long getFishingTime() {
		return fishingTime;
	}

	public void setFishingTime(long fishingTime) {
		this.fishingTime = fishingTime;
	}

	public boolean isFishing() {
		return isFishing;
	}

	public void setFishing(boolean isFishing) {
		this.isFishing = isFishing;
	}

	public boolean isClanOrder() {
		return isClanOrder;
	}

	public void setClanOrder(boolean isClanOrder) {
		this.isClanOrder = isClanOrder;
	}

	public long getLastHitTime() {
		return lastHitTime;
	}

	public void setLastHitTime(long lastHitTime) {
		this.lastHitTime = lastHitTime;
	}

	public ItemInstance getWeapon() {
		return weapon;
	}

	public void setWeapon(ItemInstance weapon) {
		this.weapon = weapon;
	}

	public int getDmg() {
		return dmg;
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public int getMinDmg() {
		return minDmg;
	}

	public void setMinDmg(int minDmg) {
		this.minDmg = minDmg;
	}

	public int getMaxDmg() {
		return maxDmg;
	}

	public void setMaxDmg(int maxDmg) {
		this.maxDmg = maxDmg;
	}

	public ItemInstance getTempItem() {
		return tempItem;
	}

	public void setTempItem(ItemInstance tempItem) {
		this.tempItem = tempItem;
	}

	public int getDynamicReduction() {
		return dynamicReduction;
	}

	public void setDynamicReduction(int dynamicReduction) {
		this.dynamicReduction = dynamicReduction;
	}
	
	public int getDynamicIgnoreReduction() {
		return dynamicIgnoreReduction;
	}

	public void setDynamicIgnoreReduction(int dynamicIgnoreReduction) {
		this.dynamicIgnoreReduction = dynamicIgnoreReduction;
	}

	public int getDynamicAddDmgBow() {
		return dynamicAddDmgBow;
	}

	public void setDynamicAddDmgBow(int dynamicAddDmgBow) {
		this.dynamicAddDmgBow = dynamicAddDmgBow;
	}

	public int getDynamicAddHit() {
		return dynamicAddHit;
	}

	public void setDynamicAddHit(int dynamicAddHit) {
		this.dynamicAddHit = dynamicAddHit;
	}

	public int getDynamicAddHitBow() {
		return dynamicAddHitBow;
	}

	public void setDynamicAddHitBow(int dynamicAddHitBow) {
		this.dynamicAddHitBow = dynamicAddHitBow;
	}

	public int getDynamicAddDmg() {
		return dynamicAddDmg;
	}

	public void setDynamicAddDmg(int dynamicAddDmg) {
		this.dynamicAddDmg = dynamicAddDmg;
	}

	public int getDynamicEr() {
		return dynamicEr;
	}

	public void setDynamicEr(int dynamicEr) {
		this.dynamicEr = dynamicEr;
	}

	public int getDynamicDg() {
		return dynamicDg;
	}

	public void setDynamicDg(int dynamicDg) {
		this.dynamicDg = dynamicDg;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int getLevel() {
		return level;
	}
	
	public int getMagicdollHpTic() {
		return magicdollHpTic;
	}

	public void setMagicdollHpTic(int magicdollHpTic) {
		this.magicdollHpTic = magicdollHpTic;
	}
	
	public int getMagicdollMpTic() {
		return magicdollMpTic;
	}

	public void setMagicdollMpTic(int magicdollMpTic) {
		this.magicdollMpTic = magicdollMpTic;
	}

	@Override
	public void setNowHp(int nowHp) {
		if (Lineage.is_sync) {
			synchronized (sync_hp) {
				if (!isDead()) {
					if (getTotalHp() < nowHp) {
						nowHp = getTotalHp();
					} else if (nowHp <= 0) {
						if (getGm() > 0) {
							nowHp = getTotalHp();
						} else {
							if (this instanceof PcInstance) {
								PcInstance pc = (PcInstance) this;
								if (!pc.isTeamBattleDead() && TeamBattleController.checkList(pc) && pc.getBattleTeam() > 0 && 
									pc.getMap() == Lineage.teamBattleMap && TeamBattleController.startTeamBattle) {
									nowHp = pc.getTotalHp();
									TeamBattleController.setDead(pc);
								} else {
									nowHp = 0;
									setDead(true);
									
									if (Lineage.is_character_dead_effect) {
										// 사망시 이팩트
										if (!World.isBattleZone(getX(), getY(), getMap()) && !World.isTeamBattleMap(this)) {
											//setDeathEffect(true);
											toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 12111), true);
										}
									}
								}
							} else {
								nowHp = 0;
								setDead(true);

								if (Lineage.is_monster_dead_effect && this instanceof MonsterInstance) {
									// 오만의 탑 몬스터 사망시 이팩트
									MonsterInstance mon = (MonsterInstance) this;

									if (mon.getMonster().isBoss())
										toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.boss_monster_dead_effect), true);
									else if (getMap() >= 101 && getMap() <= 200)
										toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.monster_dead_effect), true);
								}
							}
						}
					}
					this.nowHp = nowHp;
				}
			}
		} else {
			synchronized (sync_dynamic) {
				if (!isDead()) {
					if (getTotalHp() < nowHp) {
						nowHp = getTotalHp();
					} else if (nowHp <= 0) {
						if (getGm() > 0) {
							nowHp = getTotalHp();
						} else {
							if (this instanceof PcInstance) {
								PcInstance pc = (PcInstance) this;
								if (!pc.isTeamBattleDead() && TeamBattleController.checkList(pc) && pc.getBattleTeam() > 0 && 
									pc.getMap() == Lineage.teamBattleMap && TeamBattleController.startTeamBattle) {
									nowHp = pc.getTotalHp();
									TeamBattleController.setDead(pc);
								} else {
									nowHp = 0;
									setDead(true);
									
									if (Lineage.is_character_dead_effect) {
										// 사망시 이팩트
										if (!World.isBattleZone(getX(), getY(), getMap()) && !World.isTeamBattleMap(this)) {
											//setDeathEffect(true);
											toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 12111), true);
										}
									}
								}
							} else {
								nowHp = 0;
								setDead(true);

								if (Lineage.is_monster_dead_effect && this instanceof MonsterInstance) {
									// 오만의 탑 몬스터 사망시 이팩트
									MonsterInstance mon = (MonsterInstance) this;

									if (mon.getMonster().isBoss())
										toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.boss_monster_dead_effect), true);
									else if (getMap() >= 101 && getMap() <= 200)
										toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.monster_dead_effect), true);
								}
							}
						}
					}
					this.nowHp = nowHp;
				}
			}
		}
	}

	@Override
	public int getNowHp() {
		if (Lineage.is_sync) {
			synchronized (sync_hp) {
				if (getTotalHp() < nowHp)
					nowHp = getTotalHp();
				return nowHp;
			}
		} else {
			synchronized (sync_dynamic) {
				if (getTotalHp() < nowHp)
					nowHp = getTotalHp();
				return nowHp;
			}
		}
	}

	@Override
	public void setMaxHp(int maxHp) {
		int temp = 1;
		
		if (this instanceof PcInstance) {
			switch (getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				temp = Lineage.royal_hp;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				temp = Lineage.knight_hp;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				temp = Lineage.elf_hp;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				temp = Lineage.wizard_hp;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				temp = Lineage.darkelf_hp;
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				temp = Lineage.dragonknight_hp;
				break;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				temp = Lineage.blackwizard_hp;
				break;
			}
		}

		if (maxHp < temp)
			maxHp = temp;
		this.maxHp = maxHp;
	}

	@Override
	public int getMaxHp() {
		return maxHp;
	}

	public void setDynamicHp(int dynamicHp) {
		this.dynamicHp = dynamicHp;
	}

	public int getDynamicHp() {
		return dynamicHp;
	}

	@Override
	public void setNowMp(int nowMp) {
		if (Lineage.is_sync) {
			synchronized (sync_mp) {
				if (!isDead()) {
					if (getTotalMp() < nowMp) {
						nowMp = getTotalMp();
					} else if (nowMp <= 0) {
						nowMp = 0;
					}
					this.nowMp = nowMp;
				}
			}
		} else {
			synchronized (sync_dynamic) {
				if (!isDead()) {
					if (getTotalMp() < nowMp) {
						nowMp = getTotalMp();
					} else if (nowMp <= 0) {
						nowMp = 0;
					}
					this.nowMp = nowMp;
				}
			}
		}
	}

	@Override
	public int getNowMp() {
		if (Lineage.is_sync) {
			synchronized (sync_mp) {
				if (getTotalMp() < nowMp)
					nowMp = getTotalMp();
				return nowMp;
			}
		} else {
			synchronized (sync_dynamic) {
				if (getTotalMp() < nowMp)
					nowMp = getTotalMp();
				return nowMp;
			}
		}
	}

	@Override
	public void setMaxMp(int maxMp) {
		int temp = Lineage.royal_mp;
		switch (getClassType()) {
		case Lineage.LINEAGE_CLASS_KNIGHT:
			temp = Lineage.knight_mp;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			temp = Lineage.elf_mp;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			temp = Lineage.wizard_mp;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			temp = Lineage.darkelf_mp;
			break;
		case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
			temp = Lineage.dragonknight_mp;
			break;
		case Lineage.LINEAGE_CLASS_BLACKWIZARD:
			temp = Lineage.blackwizard_mp;
			break;
		}
		if (maxMp < temp)
			maxMp = temp;
		this.maxMp = maxMp;
	}

	@Override
	public int getMaxMp() {
		return maxMp;
	}

	public void setDynamicMp(int dynamicMp) {
		this.dynamicMp = dynamicMp;
	}

	public int getDynamicMp() {
		return dynamicMp;
	}

	public void setAc(int ac) {
		if (ac < 0)
			ac = 0;
		if (ac > Lineage.MAX_AC)
			ac = Lineage.MAX_AC;
		this.ac = ac;
	}

	public int getAc() {
		return ac;
	}

	public void setDynamicAc(int dynamicAc) {
		this.dynamicAc = dynamicAc;
	}

	public int getDynamicAc() {
		return dynamicAc;
	}

	public void setStr(int Str) {
		this.Str = Str;
	}

	public int getStr() {
		return Str;
	}

	public void setCon(int Con) {
		this.Con = Con;
	}

	public int getCon() {
		return Con;
	}

	public void setDex(int Dex) {
		this.Dex = Dex;
	}

	public int getDex() {
		return Dex;
	}

	public void setWis(int Wis) {
		this.Wis = Wis;
	}

	public int getWis() {
		return Wis;
	}

	public void setInt(int Int) {
		this.Int = Int;
	}

	public int getInt() {
		return Int;
	}

	public void setCha(int Cha) {
		this.Cha = Cha;
	}

	public int getCha() {
		return Cha;
	}

	public void setDynamicStr(int dynamicStr) {
		this.dynamicStr = dynamicStr;
	}

	public int getDynamicStr() {
		return dynamicStr;
	}

	public void setDynamicCon(int dynamicCon) {
		this.dynamicCon = dynamicCon;
	}

	public int getDynamicCon() {
		return dynamicCon;
	}

	public void setDynamicDex(int dynamicDex) {
		this.dynamicDex = dynamicDex;
	}

	public int getDynamicDex() {
		return dynamicDex;
	}

	public void setDynamicWis(int dynamicWis) {
		this.dynamicWis = dynamicWis;
	}

	public int getDynamicWis() {
		return dynamicWis;
	}

	public void setDynamicInt(int dynamicInt) {
		this.dynamicInt = dynamicInt;
	}

	public int getDynamicInt() {
		return dynamicInt;
	}

	public void setDynamicCha(int dynamicCha) {
		this.dynamicCha = dynamicCha;
	}
	public int getLvStat() {
		return lvStr + lvDex + lvCon + lvWis + lvInt + lvCha;
	}
	public int getDynamicCha() {
		return dynamicCha;
	}

	public int getLvStr() {
		return lvStr;
	}

	public void setLvStr(int lvStr) {
		this.lvStr = lvStr;
	}

	public int getLvCon() {
		return lvCon;
	}

	public void setLvCon(int lvCon) {
		this.lvCon = lvCon;
	}

	public int getLvDex() {
		return lvDex;
	}

	public void setLvDex(int lvDex) {
		this.lvDex = lvDex;
	}

	public int getLvWis() {
		return lvWis;
	}

	public void setLvWis(int lvWis) {
		this.lvWis = lvWis;
	}

	public int getLvInt() {
		return lvInt;
	}

	public void setLvInt(int lvInt) {
		this.lvInt = lvInt;
	}

	public int getLvCha() {
		return lvCha;
	}

	public void setLvCha(int lvCha) {
		this.lvCha = lvCha;
	}

	public int getDynamicMr() {
		return dynamicMr;
	}

	public void setDynamicMr(int dynamicMr) {
		this.dynamicMr = dynamicMr;
	}

	public int getDynamicSp() {
		return dynamicSp;
	}

	public void setDynamicSp(int dynamicSp) {
		this.dynamicSp = dynamicSp;
	}

	public int getDynamicTicHp() {
		return dynamicTicHp;
	}

	public void setDynamicTicHp(int dynamicTicHp) {
		this.dynamicTicHp = dynamicTicHp;
	}

	public int getDynamicTicMp() {
		return dynamicTicMp;
	}

	public void setDynamicTicMp(int dynamicTicMp) {
		this.dynamicTicMp = dynamicTicMp;
	}

	public double getItemWeight() {
		return itemWeight;
	}

	public void setItemWeight(double itemWeight) {
		this.itemWeight = itemWeight;
	}

	public double getExp() {
		if (Lineage.is_sync) {
			synchronized (sync_exp) {
				return exp;
			}
		} else {
			synchronized (sync_dynamic) {
				return exp;
			}
		}
	}

	public void setExp(double exp) {
		if (Lineage.is_sync) {
			synchronized (sync_exp) {
				if (exp < 0)
					exp = 0;
				this.exp = exp;
			}
		} else {
			synchronized (sync_dynamic) {
				if (exp < 0)
					exp = 0;
				this.exp = exp;
			}
		}
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		if (food >= Lineage.MAX_FOOD)
			food = Lineage.MAX_FOOD;
		if (food < 0)
			food = 0;
		this.food = food;
	}

	public int getDynamicEarthress() {
		return dynamicEarthress;
	}

	public void setDynamicEarthress(int dynamicEarthress) {
		this.dynamicEarthress = dynamicEarthress;
	}

	public int getDynamicWaterress() {
		return dynamicWaterress;
	}

	public void setDynamicWaterress(int dynamicWaterress) {
		this.dynamicWaterress = dynamicWaterress;
	}

	public int getDynamicFireress() {
		return dynamicFireress;
	}

	public void setDynamicFireress(int dynamicFireress) {
		this.dynamicFireress = dynamicFireress;
	}

	public int getDynamicWindress() {
		return dynamicWindress;
	}

	public void setDynamicWindress(int dynamicWindress) {
		this.dynamicWindress = dynamicWindress;
	}

	public int getEarthress() {
		return earthress;
	}

	public void setEarthress(int earthress) {
		this.earthress = earthress;
	}

	public int getWaterress() {
		return waterress;
	}

	public void setWaterress(int waterress) {
		this.waterress = waterress;
	}

	public int getFireress() {
		return fireress;
	}

	public void setFireress(int fireress) {
		this.fireress = fireress;
	}

	public int getWindress() {
		return windress;
	}

	public void setWindress(int windress) {
		this.windress = windress;
	}

	public int getSetitemEarthress() {
		return setitemEarthress;
	}

	public void setSetitemEarthress(int setitemEarthress) {
		this.setitemEarthress = setitemEarthress;
	}

	public int getSetitemWaterress() {
		return setitemWaterress;
	}

	public void setSetitemWaterress(int setitemWaterress) {
		this.setitemWaterress = setitemWaterress;
	}

	public int getSetitemFireress() {
		return setitemFireress;
	}

	public void setSetitemFireress(int setitemFireress) {
		this.setitemFireress = setitemFireress;
	}

	public int getSetitemWindress() {
		return setitemWindress;
	}

	public void setSetitemWindress(int setitemWindress) {
		this.setitemWindress = setitemWindress;
	}

	public int getReduction() {
		return reduction;
	}

	public void setReduction(int reduction) {
		this.reduction = reduction;
	}

	public int getTotalStr() {
		return Str + dynamicStr + lvStr;
	}

	public int getTotalDex() {
		return Dex + dynamicDex + lvDex;
	}

	public int getTotalCon() {
		return Con + dynamicCon + lvCon;
	}

	public int getTotalWis() {
		return Wis + dynamicWis + lvWis;
	}

	public int getTotalInt() {
		return Int + dynamicInt + lvInt;
	}

	public int getTotalCha() {
		return Cha + dynamicCha + lvCha;
	}

	public int getTotalHp() {
		return maxHp + dynamicHp;
	}

	public int getTotalMp() {
		return maxMp + dynamicMp;
	}

	public int getTotalEarthress() {
		return earthress + dynamicEarthress + setitemEarthress;
	}

	public int getTotalWaterress() {
		return waterress + dynamicWaterress + setitemWaterress;
	}

	public int getTotalFireress() {
		return fireress + dynamicFireress + setitemFireress;
	}

	public int getTotalWindress() {
		return windress + dynamicWindress + setitemWindress;
	}

	public int getTotalAc() {
		int total_ac = ac + dynamicAc + (Lineage.is_dex_ac ? getAcDex() : 0) ;

		return total_ac > Lineage.MAX_AC ? Lineage.MAX_AC : total_ac;
	}

	public int getTotalReduction() {
		return reduction + dynamicReduction;
	}

	public int getTotalHpPotion() {
		return CharacterController.toStatCon(this, "HealingPotion") + dynamicHpPotion;
	}

	public double getTotalStunResist() {
		return dynamicStunResist;
	}

	public int getTotalCritical(boolean bow) {
		if (bow)
			return CharacterController.toStatDex(this, "Critical");
		else
			return CharacterController.toStatStr(this, "Critical");
	}

	/**
	 * 현재 hp를 백분율로 연산하여 리턴함.
	 * 
	 * @return
	 */
	public int getHpPercent() {
		double nowhp = getNowHp();
		double maxhp = getTotalHp();
		return (int) ((nowhp / maxhp) * 100);
	}
	public int getMpPercent() {
		double nowmp = getNowMp();
		double maxmp = getTotalMp();
		return (int) ((nowmp / maxmp) * 100);
	}

	public boolean isHpTic() {
		// 마법인형 관리
		if (--magicdollTimeHpTic <= 0 && magicdollHpTic > 0) {
			magicdollTimeHpTic = 32;

			setNowHp(getNowHp() + magicdollHpTic);
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, 8910), true);
		}
		// 버서커스 상태일 경우 회복 불가
		if (isBuffBerserks())
			return false;

		if (--TimeHpTic <= 0) {
			moving = false;
			fight = false;
			hpFight = false;
			hpMove = false;
			TimeHpTic = getHpTime(); // HP 회복(틱) 1 당 HP 1을 회복하며 서 있는 경우에는 3초, 이동하는 경우에는 6초, 전투 시에는 12초마다 HP를 회복한다.
			return true;
		}
		return false;
	}

	public boolean isMpTic() {
		// 마법인형 관리
		if (--magicdollTimeMpTic <= 0 && magicdollMpTic > 0) {
			magicdollTimeMpTic = 64;

			setNowMp(getNowMp() + magicdollMpTic);
			toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), this, Lineage.doll_mana_effect), true);
		}
	
		if (--TimeMpTic <= 0) {
			moving = false;
			fight = false;
			mpFight = false;
			mpMove = false;
			TimeMpTic = getMpTime(); // MP 회복(틱) 1 당 MP 1을 회복하며 서 있는 경우에는 8초, 이동하는 경우에는 16초, 전투 시에는 32초마다 MP를 회복한다
			if (isBuffMeditation())
				setBuffMeditaitonLevel(getBuffMeditaitonLevel() + 1);
			return true;
		}
		return false;
	}

	/**
	 * 자연회복의 hp상승값을 리턴함. : 여기 함수가 hp틱 상승값을 리턴하는 녀석입니다.
	 */
	public int hpTic() {
		int tic = dynamicTicHp;

		// 스탯에 따른 틱 계산
		tic += CharacterController.toStatCon(this, "hpTic");
		// 여관맵일경우 틱처리
		if (InnController.isInnMap(this))
			tic += 20;
		// 아지트일경우 틱처리
		if (AgitController.isAgitLocation(this))
			tic += 20;
		// 내성 틱처리
		if (KingdomController.isKingdomInsideLocation(this))
			tic += 15;
		// 엄마나무 틱처리
		if (getClassType() == Lineage.LINEAGE_CLASS_ELF && ElvenforestController.isTreeZone(this))
			tic += Lineage.home_hp_tic;
		if (isHome())
			tic += Lineage.home_hp_tic;
		// 버서커스 상태일때 자연회복 불가
		if (isBuffBerserks())
			tic = 0;
		// 네이쳐스 터치 틱당 HP 회복량은 대상의 레벨-9로, 대상의 레벨이 24 이상일 때 최대의 효과
		// 기본 최대 효과는 15
		if (isBuffNaturesTouch())
			tic += getLevel() - 9 > 15 ? 15 : getLevel() - 9 < 1 ? 1 : getLevel() - 9;
		// 0보다 작을경우 0을 리턴하도록 함.
		return tic <= 0 ? 1 : Util.random(1, tic);
	}

	// .틱 명령어에서 사용중. HP틱은 1 ~ 최대 tic까지 랜덤으로 회복되므로 최대tic값을 리턴
	public int getHpTic() {
		int tic = dynamicTicHp;

		// 스탯에 따른 틱 계산
		tic += CharacterController.toStatCon(this, "hpTic");
		// 여관맵일경우 틱처리
		if (InnController.isInnMap(this))
			tic += 20;
		// 아지트일경우 틱처리
		if (AgitController.isAgitLocation(this))
			tic += 20;
		// 내성 틱처리
		if (KingdomController.isKingdomInsideLocation(this))
			tic += 15;
		// 엄마나무 틱처리
		if (getClassType() == Lineage.LINEAGE_CLASS_ELF && ElvenforestController.isTreeZone(this))
			tic += Lineage.home_hp_tic;
		if (isHome())
			tic += Lineage.home_hp_tic;
		// 버서커스 상태일때 자연회복 불가
		if (isBuffBerserks())
			tic = 0;
		// 네이쳐스 터치 틱당 HP 회복량은 대상의 레벨-9로, 대상의 레벨이 24 이상일 때 최대의 효과
		// 기본 최대 효과는 15
		if (isBuffNaturesTouch())
			tic += getLevel() - 9 > 15 ? 15 : getLevel() - 9 < 1 ? 1 : getLevel() - 9;
		// 0보다 작을경우 0을 리턴하도록 함.
		return tic <= 0 ? 1 : tic;
	}

	/**
	 * 자연회복의 mp상승값을 리턴함.
	 */
	public int mpTic() {
		int tic = dynamicTicMp;

		// 스탯에 따른 틱 계산
		tic += CharacterController.toStatWis(this, "mpTic");
		// 여관맵일경우 틱처리
		if (InnController.isInnMap(this))
			tic += 20;
		// 아지트일경우 틱처리
		if (AgitController.isAgitLocation(this))
			tic += 15;
		// 내성 틱처리
		if (KingdomController.isKingdomInsideLocation(this))
			tic += 10;
		// 엄마나무 틱처리
		if (getClassType() == Lineage.LINEAGE_CLASS_ELF && ElvenforestController.isTreeZone(this))
			tic += Lineage.home_mp_tic;
		if (isHome())
			tic += Lineage.home_mp_tic;
		if (isBuffMeditation())
			tic += getBuffMeditaitonLevel() * 2;
		if (isBuffBluePotion())
			tic += getTotalWis() - 10;

		// 0보다 작을경우 0을 리턴하도록 함.
		return tic <= 0 ? 1 : tic;
	}

	// .틱 명령어에서 사용중.
	public int getMpTic() {
		int tic = dynamicTicMp;

		// 스탯에 따른 틱 계산
		tic += CharacterController.toStatWis(this, "mpTic");
		// 여관맵일경우 틱처리
		if (InnController.isInnMap(this))
			tic += 20;
		// 아지트일경우 틱처리
		if (AgitController.isAgitLocation(this))
			tic += 15;
		// 내성 틱처리
		if (KingdomController.isKingdomInsideLocation(this))
			tic += 10;
		// 엄마나무 틱처리
		if (getClassType() == Lineage.LINEAGE_CLASS_ELF && ElvenforestController.isTreeZone(this))
			tic += Lineage.home_mp_tic;
		if (isHome())
			tic += Lineage.home_mp_tic;
		if (isBuffMeditation())
			tic += getBuffMeditaitonLevel() * 2;
		if (isBuffBluePotion())
			tic += getTotalWis() - 10;

		// 0보다 작을경우 0을 리턴하도록 함.
		return tic <= 0 ? 1 : tic;
	}

	public int getHpTime() {
		int hpTime = 3;

		if (hpMove)
			hpTime = 6;

		if (hpFight)
			hpTime = 12;

		return hpTime;
	}

	public int getMpTime() {
		int mpTime = 8;

		if (mpMove)
			mpTime = 16;

		if (mpFight)
			mpTime = 32;

		return mpTime;
	}

	/**
	 * 케릭터는 자연회복에 영향을 받는 클레스임. 그렇기 때문에 이동하는 초기 시점이라면 자연회복을 좀 지연해야됨.
	 */
	@Override
	public void setMoving(boolean moving) {
		if (!this.moving && moving) {
			if (!hpMove) {
				hpMove = true;
				TimeHpTic = 6;
			}
			if (!mpMove) {
				mpMove = true;
				TimeMpTic = 16;
			}
		}
		super.setMoving(moving);
	}

	/**
	 * 케릭터는 자연회복에 영향을 받는 클레스임. 그렇기 때문에 전투가 활성화 되는 초기 시점이라면 자연회복을 좀 지연해야됨.
	 */
	@Override
	public void setFight(boolean fight) {
		if (!this.fight && fight) {
			if (!hpFight) {
				hpFight = true;
				TimeHpTic = 12;
			}
			if (!mpFight) {
				mpFight = true;
				TimeMpTic = 32;
			}
		}
		super.setFight(fight);
	}

	@Override
	public void toReset(boolean world_out) {
		super.toReset(world_out);
		
		if (isDead()) {
			if (this instanceof PcInstance) {
				if (!World.isBattleZone(getX(), getY(), getMap()))
					// 버프제거
					BuffController.removeDead(this);
			} else {
				// 버프제거
				BuffController.removeAll(this);
			}
		}
	}

	/**
	 * 덱스에 따른 ac +@ 연산 리턴.
	 * 
	 * @return
	 */
	public int getAcDex() {
		int total_dex = Dex + lvDex ;
		int gab = total_dex>=18 ? 4 : total_dex<=17 && total_dex>=16 ? 5 : total_dex<=15 && total_dex>=13 ? 6 : total_dex<=12 && total_dex>=10 ? 7 : 8;
		return level / gab;
	}

//	private int getAcLevel() {
//		int level_ac = 0;
//
//		switch (getClassType()) {
//		case Lineage.LINEAGE_CLASS_ROYAL:
//		case Lineage.LINEAGE_CLASS_KNIGHT:
//			level_ac = getLevel() / 6;
//			break;
//		case Lineage.LINEAGE_CLASS_ELF:
//			level_ac = getLevel() / 7;
//			break;
//		case Lineage.LINEAGE_CLASS_WIZARD:
//			level_ac = getLevel() / 8;
//			break;
//		}
//
//		return level_ac;
//	}

	/**
	 * 낚시터 확인
	 * 
	 */
	public boolean isFishingZone() {
		return getMap() == 5124 && getX() >= Lineage.FISHZONEX1 && getX() <= Lineage.FISHZONEX2 && getY() >= Lineage.FISHZONEY1 && getY() <= Lineage.FISHZONEY2;
	}

	/**
	 * 기란마을 중앙 확인
	 * 
	 */
	public boolean isHome() {
		return getMap() == 4 && getX() >= Lineage.HOMEX1 && getX() <= Lineage.HOMEX2 && getY() >= Lineage.HOMEY1 && getY() <= Lineage.HOMEY2;
	}

/*	public void setDeathEffect(boolean dead) {
		if (dead) {
			backGround = new DeathEffect();
			backGround.setGfx(13600);	
			backGround.setObjectId(ServerDatabase.nextEtcObjId());
			backGround.toTeleport(getX(), getY(), getMap(), false);
			backGround.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), backGround, 4), this instanceof PcInstance);
		} else {			
			if (isDead() && backGround != null) {
				backGround.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), backGround, 8), this instanceof PcInstance);
				backGround.clearList(true);
				World.remove(backGround);
				backGround = null;
			}
		}
	} */
	
	/**
	 * 잠수 모드 설정.
	 * 2019-09-26
	 * by connector12@nate.com
	 */
	public void setRestMode(ItemInstance weapon, boolean equipped) {
		if (getInventory() != null && getInventory().getList() != null) {
			if (weapon != null && weapon.getItem() != null && weapon.getItem().getName().equalsIgnoreCase(Lineage.rest_cracker_weapon)) {
				if (equipped) {
					// 착용
					if (Lineage.is_rest_cracker) {
						isRestCracker = equipped;

						if (Lineage.rest_cracker_level > getLevel()) {
							ChattingController.toChatting(this, String.format("\\fY[%s] %d레벨 이상 사용 가능합니다.", Lineage.rest_cracker_name, Lineage.rest_cracker_level), Lineage.CHATTING_MODE_MESSAGE);
							return;
						}

						if (World.isRestCracker(getX(), getY(), getMap())) {
							if (restCrackerDelay > System.currentTimeMillis()) {
								ChattingController.toChatting(this, String.format("\\fY[%s] 딜레이는 %d초 입니다.", Lineage.rest_cracker_name, Lineage.rest_cracker_delay), Lineage.CHATTING_MODE_MESSAGE);
								return;
							}

							setRestCracker(equipped);
							restCrackerDelay = System.currentTimeMillis() + (Lineage.rest_cracker_delay * 1000);

							if (Lineage.is_rest_cracker_haste)
								Haste.init(this, -1, true);

							if (Lineage.is_rest_cracker_bravery) {
								Bravery.init(this, -1, true);
								Wafer.init(this, -1, true);
								HolyWalk.init(this, -1);
							}
						} else {
							ChattingController.toChatting(this, String.format("\\fY[%s] 지정된 위치에서 사용 가능합니다.", Lineage.rest_cracker_name, Lineage.rest_cracker_level), Lineage.CHATTING_MODE_MESSAGE);
						}
					} else {
						ChattingController.toChatting(this, String.format("\\fY[%s] 사용 불가합니다.", Lineage.rest_cracker_name, Lineage.rest_cracker_level), Lineage.CHATTING_MODE_MESSAGE);
					}
				} else {
					// 착용해제
					isRestCracker = equipped;
					setRestCracker(equipped);

					BuffController.remove(this, Haste.class);
					BuffController.remove(this, Bravery.class);
					BuffController.remove(this, Wafer.class);
					BuffController.remove(this, HolyWalk.class);
				}
			}
		}
	}
	
	/**
	 * 허수아비 설정
	 * 2019-09-26
	 * by connector12@nate.com
	 */
	public void setRestCracker(boolean equipped) {
		if (Lineage.is_rest_cracker) {
			if (equipped) {
				Monster m = MonsterDatabase.find(Lineage.rest_cracker_name);
				
				if (m != null) {
					RestCracker rc = new RestCracker();
					rc.setObjectId(ServerDatabase.nextEtcObjId());
					rc.setName(getName() + "의 허수아비");
					rc.setClanId(0);
					rc.setClanName("");
					rc.setTitle("");
					rc.setGfx(m.getGfx());
					rc.toTeleport(getX(), getY(), getMap(), false);
					restcCracker = rc;
					ChattingController.toChatting(this, String.format("\\fY[%s] 소환.", Lineage.rest_cracker_name), Lineage.CHATTING_MODE_MESSAGE);
				}
			} else {
				if (restcCracker != null) {
					restcCracker.close();
					ChattingController.toChatting(this, String.format("\\fY[%s] 소환해제.", Lineage.rest_cracker_name), Lineage.CHATTING_MODE_MESSAGE);
				}
				restcCracker = null;			
			}
		}
	}
	
	/**
	 * 잠수 전용 화살 가져오기.
	 * 2019-09-27
	 * by connector12@nate.com
	 */
	public ItemInstance setRestCrackerArrow() {
		if (getInventory() != null && getInventory().getList() != null) {
			if (isRestCracker) {
				for (ItemInstance arrow : getInventory().getList()) {
					if (arrow != null && arrow.getItem() != null && arrow.getItem().getName().equalsIgnoreCase(Lineage.rest_cracker_arrow))
						return arrow;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 잠수 전용 데미지 계산.
	 * 2019-09-27
	 * by connector12@nate.com
	 */
	public int getRestCrackerDmg(ItemInstance weapon, ItemInstance arrow, object o) {
		int dmg = 0;

		if (getInventory() != null && getInventory().getList() != null) {
			if (o instanceof RestCracker) {
				if (weapon != null && arrow != null) {
					if (restcCracker != null && restcCracker.getObjectId() == o.getObjectId()) {
						dmg += Util.random(weapon.getItem().getSmallDmg(), weapon.getItem().getBigDmg());
						dmg += Util.random(arrow.getItem().getSmallDmg(), arrow.getItem().getBigDmg());
					} else {
						ChattingController.toChatting(this, String.format("\\fY[%s] 소유자만 공격가능.", Lineage.rest_cracker_name), Lineage.CHATTING_MODE_MESSAGE);
					}
				}
			} else {
				ChattingController.toChatting(this, String.format("\\fY[%s] 이외에 사용 불가.", Lineage.rest_cracker_name), Lineage.CHATTING_MODE_MESSAGE);
			}
		}
		
		return dmg;
	}
	
	/**
	 * 잠수 전용 화살 소모시.
	 * 2019-09-27
	 * by connector12@nate.com
	 */
	public void endRestCracker() {
		isAutoAttack = false;
		autoAttackTarget = null;
		autoAttackTime = 0L;
		targetX = 0;
		targetY = 0;	
		toTeleport(getX(), getY(), getMap(), false);
		ChattingController.toChatting(this, String.format("\\fY[%s 모두 소모] 종료 되었습니다.", Lineage.rest_cracker_arrow), Lineage.CHATTING_MODE_MESSAGE);
	}
	
	/**
	 * 잠수용 허수아비는 월드 종료시 장착 해제.
	 * 2019-09-27
	 * by connector12@nate.com
	 */
	public void restCrackerWorldOut() {
		if (isRestCracker) {
			ItemInstance weapon = getInventory().getSlot(Lineage.SLOT_WEAPON);
			
			if (weapon != null && weapon.isEquipped())
				weapon.toClick(this, null);
			
			if (restcCracker != null)
				restcCracker.close();
			restcCracker = null;
		}
	}
}
