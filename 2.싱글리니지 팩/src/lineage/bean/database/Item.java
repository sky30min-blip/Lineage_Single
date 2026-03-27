package lineage.bean.database;

import java.util.HashMap;
import java.util.Map;

public class Item {
	private String Name;
	private String NameId;
	private String Type1;
	private String Type2;
	private int NameIdNumber;
	private int Material;
	private String materialName;
	private boolean isPcTrade;
	private int DmgMin;	
	private int DmgMax;	
	private double Weight;
	private int InvGfx;
	private int GroundGfx;
	private int GfxMode;
	private int Action1;
	private int Action2;
	private boolean Sell;
	private boolean Piles;
	private boolean Trade;
	private boolean Drop;
	private boolean Warehouse;
	private boolean clanWarehouse;
	private boolean elfWarehouse;
	private boolean Enchant;
	private int SafeEnchant;
	private int MaxEnchant;
	private long limitTime;
	private int Royal;
	private int Knight;
	private int Elf;
	private int Wizard;
	private int DarkElf;
	private int DragonKnight;
	private int BlackWizard;
	private int AddHit;
	private int AddDmg;
	private int AddMagicHit;
	private int Ac;
	private int AddStr;
	private int AddDex;
	private int AddCon;
	private int AddInt;
	private int AddWis;
	private int AddCha;
	private int AddHp;
	private int AddMp;
	private int AddSp;
	private int AddMr;
	private boolean Canbedmg;
	private int LevelMin;
	private int LevelMax;
	private int Effect;
	private boolean Tohand;
	private int SetId;
	private int delay;
	private int Waterress;
	private int Windress;
	private int Earthress;
	private int Fireress;
	private double stunDefense;
	private double enchantStunDefense;
	private double AddWeight;
	private int TicHp;
	private int TicMp;
	private int ShopPrice;
	private double DropChance;
	private int Solvent;
	private int AttributeCrystal;
	private boolean bookChaoticZone;
	private boolean bookLawfulZone;
	private boolean bookMomtreeZone;
	private boolean bookNeutralZone;
	private boolean bookTowerZone;
	private String polyName;
	private boolean isInventorySave;
	private boolean isAqua;
	private int StealHp;
	private int StealMp;
	private int reduction;
	private int ignoreReduction;
	private int enchantMr;
	private int criticalEffect;
	private int addCriticalSword;
	private int addCriticalBow;
	private int addCriticalMagic;
	private boolean addCaoticDamage;
	private int duration;
	private double stunHit;
	private double enchantStunHit;
	private int enchantSp;
	private int enchantReduction;
	private int enchantIgnoreReduction;
	private int enchantSwordCritical;
	private int enchantBowCritical;
	private int enchantMagicCritical;
	private int pvpDamage;
	private int enchantPvpDamage;
	private int pvpReduction;
	private int enchantPvpReduction;
	
	private boolean drop_ment;

	private int Slot;							// 서버 메모리에 등록되는 슬롯 위치
	private int equippedSlot;					// 클라이언트 스탯창에 장착되는 슬롯 위치
	private Map<String, Integer> list_craft;	// 제작처리 구간에서 사용함. 지급될아이템에 제곱값.
	
	public Item(){
		list_craft = new HashMap<String, Integer>();
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getNameId() {
		return NameId;
	}
	public void setNameId(String nameId) {
		NameId = nameId;
	}
	public String getType1() {
		return Type1;
	}
	public void setType1(String type1) {
		Type1 = type1;
	}
	public String getType2() {
		return Type2;
	}
	public void setType2(String type2) {
		Type2 = type2;
	}
	public int getNameIdNumber() {
		return NameIdNumber;
	}
	public void setNameIdNumber(int nameIdNumber) {
		NameIdNumber = nameIdNumber;
	}
	public int getMaterial() {
		return Material;
	}
	public void setMaterial(int material) {
		Material = material;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	public boolean isPcTrade() {
		return isPcTrade;
	}
	public void setPcTrade(boolean isPcTrade) {
		this.isPcTrade = isPcTrade;
	}
	public int getSmallDmg() {
		return DmgMin;
	}
	public void setSmallDmg(int dmgMin) {
		DmgMin = dmgMin;
	}
	public int getBigDmg() {
		return DmgMax;
	}
	public void setBigDmg(int dmgMax) {
		DmgMax = dmgMax;
	}
	public double getWeight() {
		return Weight;
	}
	public void setWeight(double weight) {
		Weight = weight;
	}
	public int getInvGfx() {
		return InvGfx;
	}
	public void setInvGfx(int invGfx) {
		InvGfx = invGfx;
	}
	public int getGroundGfx() {
		return GroundGfx;
	}
	public void setGroundGfx(int groundGfx) {
		GroundGfx = groundGfx;
	}
	public int getGfxMode() {
		return GfxMode;
	}
	public void setGfxMode(int gfxMode) {
		GfxMode = gfxMode;
	}
	public int getAction1() {
		return Action1;
	}
	public void setAction1(int action1) {
		Action1 = action1;
	}
	public int getAction2() {
		return Action2;
	}
	public void setAction2(int action2) {
		Action2 = action2;
	}
	public boolean isSell() {
		return Sell;
	}
	public void setSell(boolean sell) {
		Sell = sell;
	}
	public boolean isPiles() {
		return Piles;
	}
	public void setPiles(boolean piles) {
		Piles = piles;
	}
	public boolean isTrade() {
		return Trade;
	}
	public void setTrade(boolean trade) {
		Trade = trade;
	}
	public boolean isDrop() {
		return Drop;
	}
	public void setDrop(boolean drop) {
		Drop = drop;
	}
	public boolean isWarehouse() {
		return Warehouse;
	}
	public void setWarehouse(boolean warehouse) {
		Warehouse = warehouse;
	}
	public boolean isClanWarehouse() {
		return clanWarehouse;
	}
	public void setClanWarehouse(boolean clanWarehouse) {
		this.clanWarehouse = clanWarehouse;
	}
	public boolean isElfWarehouse() {
		return elfWarehouse;
	}
	public void setElfWarehouse(boolean elfWarehouse) {
		this.elfWarehouse = elfWarehouse;
	}
	public boolean isEnchant() {
		return Enchant;
	}
	public void setEnchant(boolean enchant) {
		Enchant = enchant;
	}
	public int getSafeEnchant() {
		return SafeEnchant;
	}
	public void setSafeEnchant(int safeEnchant) {
		SafeEnchant = safeEnchant;
	}
	//야도란 최고인챈 아이템별 인챈제한 
	public int getmaxEnchant() {
		return MaxEnchant;
	}
	public void setMaxEnchant(int maxEnchant) {
		MaxEnchant = maxEnchant;
	}

	public long getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(long limitTime) {
		this.limitTime = limitTime;
	}

	public int getRoyal() {
		return Royal;
	}
	public void setRoyal(int royal) {
		Royal = royal;
	}
	public int getKnight() {
		return Knight;
	}
	public void setKnight(int knight) {
		Knight = knight;
	}
	public int getElf() {
		return Elf;
	}
	public void setElf(int elf) {
		Elf = elf;
	}
	public int getWizard() {
		return Wizard;
	}
	public void setWizard(int wizard) {
		Wizard = wizard;
	}
	public int getDarkElf() {
		return DarkElf;
	}
	public void setDarkElf(int darkElf) {
		DarkElf = darkElf;
	}
	public int getDragonKnight() {
		return DragonKnight;
	}
	public void setDragonKnight(int dragonKnight) {
		DragonKnight = dragonKnight;
	}
	public int getBlackWizard() {
		return BlackWizard;
	}
	public void setBlackWizard(int blackWizard) {
		BlackWizard = blackWizard;
	}
	public int getAddHit() {
		return AddHit;
	}
	public void setAddHit(int addHit) {
		AddHit = addHit;
	}
	public int getAddDmg() {
		return AddDmg;
	}
	public void setAddDmg(int addDmg) {
		AddDmg = addDmg;
	}
	public int getAddMagicHit() {
		return AddMagicHit;
	}
	public void setAddMagicHit(int addMagicHit) {
		AddMagicHit = addMagicHit;
	}
	public int getAc() {
		return Ac;
	}
	public void setAc(int ac) {
		Ac = ac;
	}
	public int getAddStr() {
		return AddStr;
	}
	public void setAddStr(int addStr) {
		AddStr = addStr;
	}
	public int getAddDex() {
		return AddDex;
	}
	public void setAddDex(int addDex) {
		AddDex = addDex;
	}
	public int getAddCon() {
		return AddCon;
	}
	public void setAddCon(int addCon) {
		AddCon = addCon;
	}
	public int getAddInt() {
		return AddInt;
	}
	public void setAddInt(int addInt) {
		AddInt = addInt;
	}
	public int getAddWis() {
		return AddWis;
	}
	public void setAddWis(int addWis) {
		AddWis = addWis;
	}
	public int getAddCha() {
		return AddCha;
	}
	public void setAddCha(int addCha) {
		AddCha = addCha;
	}
	public int getAddHp() {
		return AddHp;
	}
	public void setAddHp(int addHp) {
		AddHp = addHp;
	}
	public int getAddMp() {
		return AddMp;
	}
	public void setAddMp(int addMp) {
		AddMp = addMp;
	}
	public int getAddSp() {
		return AddSp;
	}
	public void setAddSp(int addSp) {
		AddSp = addSp;
	}
	public int getAddMr() {
		return AddMr;
	}
	public void setAddMr(int addMr) {
		AddMr = addMr;
	}
	public boolean isCanbedmg() {
		return Canbedmg;
	}
	public void setCanbedmg(boolean canbedmg) {
		Canbedmg = canbedmg;
	}
	public int getLevelMin() {
		return LevelMin;
	}
	public void setLevelMin(int levelMin) {
		LevelMin = levelMin;
	}
	public int getLevelMax() {
		return LevelMax;
	}
	public void setLevelMax(int levelMax) {
		LevelMax = levelMax;
	}
	public int getEffect() {
		return Effect;
	}
	public void setEffect(int effect) {
		Effect = effect;
	}
	public boolean isTohand() {
		return Tohand;
	}
	public void setTohand(boolean tohand) {
		Tohand = tohand;
	}
	public int getSetId() {
		return SetId;
	}
	public void setSetId(int setId) {
		SetId = setId;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getWaterress() {
		return Waterress;
	}
	public void setWaterress(int waterress) {
		Waterress = waterress;
	}
	public int getWindress() {
		return Windress;
	}
	public void setWindress(int windress) {
		Windress = windress;
	}
	public int getEarthress() {
		return Earthress;
	}
	public void setEarthress(int earthress) {
		Earthress = earthress;
	}
	public int getFireress() {
		return Fireress;
	}
	public void setFireress(int fireress) {
		Fireress = fireress;
	}
	public double getStunDefense() {
		return stunDefense;
	}
	public void setStunDefense(double stunDefense) {
		this.stunDefense = stunDefense;
	}
	public double getEnchantStunDefense() {
		return enchantStunDefense;
	}
	public void setEnchantStunDefense(double enchantStunDefense) {
		this.enchantStunDefense = enchantStunDefense;
	}
	public double getAddWeight() {
		return AddWeight;
	}
	public void setAddWeight(double addWeight) {
		AddWeight = addWeight;
	}
	public int getTicHp() {
		return TicHp;
	}
	public void setTicHp(int ticHp) {
		TicHp = ticHp;
	}
	public int getTicMp() {
		return TicMp;
	}
	public void setTicMp(int ticMp) {
		TicMp = ticMp;
	}
	public int getShopPrice() {
		return ShopPrice;
	}
	public void setShopPrice(int shopPrice) {
		ShopPrice = shopPrice;
	}
	public double getDropChance() {
		return DropChance;
	}
	public void setDropChance(double dropChance) {
		DropChance = dropChance;
	}
	public int getSlot() {
		return Slot;
	}
	public void setSlot(int slot) {
		Slot = slot;
	}
	public int getEquippedSlot() {
		return equippedSlot;
	}
	public void setEquippedSlot(int equippedSlot) {
		this.equippedSlot = equippedSlot;
	}
	public Map<String, Integer> getListCraft(){
		return list_craft;
	}
	public int getSolvent() {
		return Solvent;
	}
	public void setSolvent(int solvent) {
		Solvent = solvent;
	}
	public int getAttributeCrystal() {
		return AttributeCrystal;
	}
	public void setAttributeCrystal(int attributeCrystal) {
		AttributeCrystal = attributeCrystal;
	}
	public boolean isBookChaoticZone() {
		return bookChaoticZone;
	}
	public void setBookChaoticZone(boolean bookChaoticZone) {
		this.bookChaoticZone = bookChaoticZone;
	}
	public boolean isBookLawfulZone() {
		return bookLawfulZone;
	}
	public void setBookLawfulZone(boolean bookLawfulZone) {
		this.bookLawfulZone = bookLawfulZone;
	}
	public boolean isBookMomtreeZone() {
		return bookMomtreeZone;
	}
	public void setBookMomtreeZone(boolean bookMomtreeZone) {
		this.bookMomtreeZone = bookMomtreeZone;
	}
	public boolean isBookNeutralZone() {
		return bookNeutralZone;
	}
	public void setBookNeutralZone(boolean bookNeutralZone) {
		this.bookNeutralZone = bookNeutralZone;
	}
	public boolean isBookTowerZone() {
		return bookTowerZone;
	}
	public void setBookTowerZone(boolean bookTowerZone) {
		this.bookTowerZone = bookTowerZone;
	}
	public String getPolyName() {
		return polyName;
	}
	public void setPolyName(String polyName) {
		this.polyName = polyName;
	}
	public boolean isInventorySave() {
		return isInventorySave;
	}
	public void setInventorySave(boolean isInventorySave) {
		this.isInventorySave = isInventorySave;
	}
	public boolean isAqua() {
		return isAqua;
	}
	public void setAqua(boolean isAqua) {
		this.isAqua = isAqua;
	}
	public int getStealHp() {
		return StealHp;
	}
	public void setStealHp(int stealHp) {
		StealHp = stealHp;
	}
	public int getStealMp() {
		return StealMp;
	}
	public void setStealMp(int stealMp) {
		StealMp = stealMp;
	}
	public int getAddReduction() {
		return reduction;
	}
	public void setAddReduction(int reduction) {
		this.reduction = reduction;
	}
	public int getIgnoreReduction() {
		return ignoreReduction;
	}
	public void setIgnoreReduction(int ignoreReduction) {
		this.ignoreReduction = ignoreReduction;
	}
	public int getEnchantMr() {
		return enchantMr;
	}
	public void setEnchantMr(int enchantMr) {
		this.enchantMr = enchantMr;
	}
	public int getCriticalEffect() {
		return criticalEffect;
	}
	public void setCriticalEffect(int criticalEffect) {
		this.criticalEffect = criticalEffect;
	}
	public int getDuration() {
		return duration;
	}
	public int getAddCriticalSword() {
		return addCriticalSword;
	}
	public void setAddCriticalSword(int addCriticalSword) {
		this.addCriticalSword = addCriticalSword;
	}
	public int getAddCriticalBow() {
		return addCriticalBow;
	}
	public void setAddCriticalBow(int addCriticalBow) {
		this.addCriticalBow = addCriticalBow;
	}
	public boolean isAddCaoticDamage() {
		return addCaoticDamage;
	}
	public int getAddCriticalMagic() {
		return addCriticalMagic;
	}
	public void setAddCriticalMagic(int addCriticalMagic) {
		this.addCriticalMagic = addCriticalMagic;
	}
	public void setAddCaoticDamage(boolean addCaoticDamage) {
		this.addCaoticDamage = addCaoticDamage;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public double getStunHit() {
		return stunHit;
	}
	public void setStunHit(double stunHit) {
		this.stunHit = stunHit;
	}
	public double getEnchantStunHit() {
		return enchantStunHit;
	}
	public void setEnchantStunHit(double enchantStunHit) {
		this.enchantStunHit = enchantStunHit;
	}
	public int getEnchantSp() {
		return enchantSp;
	}
	public void setEnchantSp(int enchantSp) {
		this.enchantSp = enchantSp;
	}
	public int getEnchantReduction() {
		return enchantReduction;
	}
	public void setEnchantReduction(int enchantReduction) {
		this.enchantReduction = enchantReduction;
	}
	public int getEnchantIgnoreReduction() {
		return enchantIgnoreReduction;
	}
	public void setEnchantIgnoreReduction(int enchantIgnoreReduction) {
		this.enchantIgnoreReduction = enchantIgnoreReduction;
	}
	public int getEnchantSwordCritical() {
		return enchantSwordCritical;
	}
	public void setEnchantSwordCritical(int enchantSwordCritical) {
		this.enchantSwordCritical = enchantSwordCritical;
	}
	public int getEnchantBowCritical() {
		return enchantBowCritical;
	}
	public void setEnchantBowCritical(int enchantBowCritical) {
		this.enchantBowCritical = enchantBowCritical;
	}
	public int getEnchantMagicCritical() {
		return enchantMagicCritical;
	}
	public void setEnchantMagicCritical(int enchantMagicCritical) {
		this.enchantMagicCritical = enchantMagicCritical;
	}
	public int getPvpDamage() {
		return pvpDamage;
	}
	public void setPvpDamage(int pvpDamage) {
		this.pvpDamage = pvpDamage;
	}
	public int getEnchantPvpDamage() {
		return enchantPvpDamage;
	}
	public void setEnchantPvpDamage(int enchantPvpDamage) {
		this.enchantPvpDamage = enchantPvpDamage;
	}
	public int getPvpReduction() {
		return pvpReduction;
	}
	public void setPvpReduction(int pvpReduction) {
		this.pvpReduction = pvpReduction;
	}
	public int getEnchantPvpReduction() {
		return enchantPvpReduction;
	}
	public void setEnchantPvpReduction(int enchantPvpReduction) {
		this.enchantPvpReduction = enchantPvpReduction;
	}
	
	public boolean getDrop_Ment() {
		return drop_ment;
	}
	public void setDrop_Ment(boolean dropment){
		this.drop_ment = dropment;
	}
	
	/**
	 * 장신구 확인.
	 * 2020-09-04
	 * by connector12@nate.com
	 */
	public boolean isAcc() {
		if (this != null) {
			if (getType2().equalsIgnoreCase("necklace") || getType2().equalsIgnoreCase("belt") || getType2().equalsIgnoreCase("ring") || getType2().equalsIgnoreCase("earring")) {
				return true;
			}
		}
		
		return false;
	}
}
