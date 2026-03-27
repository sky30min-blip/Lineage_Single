package lineage.world.object.instance;

import java.io.IOException;
import java.sql.Connection;

import lineage.bean.database.Item;
import lineage.bean.database.ItemSetoption;
import lineage.bean.database.Poly;
import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Inventory;
import lineage.database.ItemSetoptionDatabase;
import lineage.database.PolyDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.util.BinaryOutputStream;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.item.MagicFlute;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.magic.CounterBarrier;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.SolidCarriage;

public class ItemInstance extends object implements BuffInterface {

	protected Character cha; // 아이템을 소지하고있는 객체
	protected Item item;
	protected int bless; // 축저주 여부
	protected int quantity; // 소막같은 수량
	protected int enLevel; // en
	protected int durability; // 손상도
	protected double dynamicStunDefence; // 스턴내성
	protected int dynamicMr; // mr
	protected boolean definite; // 확인 여부
	protected boolean equipped; // 착용 여부
	protected int nowTime; // 아이템 사용 남은 시간.
	protected long limitTime;		// 아이템 소멸될 시간. item테이블에서 제한시간을 설정함. 아이템생성한 시간을 기준으로 설정한 시간만큼 유지가됨 오바되면 삭제됨.
	protected String itemTimek; // 아이템 사용 남은 시간.
	protected long click_delay; // 아이템 클릭 딜레이를 주기위한 변수.
	protected int dynamicLight; // 동적 라이트값. 현재는 양초쪽에서 사용중. 해당 아이템에 밝기값을 저장하기 위해.
	protected long time_drop; // 드랍된 시간값.
	protected int dynamicAc; // 동적 ac 값.
	protected int enEarth; // 땅 속성 인첸트
	protected int enWater; // 물 속성 인첸트
	protected int enFire; // 불 속성 인첸트
	protected int enWind; // 바람 속성 인첸트
	protected double dynamicStunHit; // 스턴 적중
	protected int dynamicSp; // SP
	protected int dynamicReduction; // 리덕션
	protected int dynamicIgnoreReduction; // 리덕션 무시
	protected int dynamicSwordCritical; // 근접 크리티컬
	protected int dynamicBowCritical; // 원거리 크리티컬
	protected int dynamicMagicCritical; // 마법 크리티컬
	protected int dynamicPvpDmg; // pvp 데미지
	protected int dynamicPvpReduction; // pvp 리덕션

	protected int tollTipMp;
	protected int tollTipAc;
	protected int tollTipSp;
	protected int tollTipHealingPotion;
	protected int tollTipStunDefens;
	protected int tollTipHp;
	protected int tollTipReduction;
	protected int tollTipHit;
	protected int tollTipDmg;
	protected int tollTipMr;
	protected int tollTipPvPReduction;
	protected int tollTipPvPDmg;
	
	protected int DolloptionA;
	protected int DolloptionB;
	protected int DolloptionC;
	protected int DolloptionD;
	protected int DolloptionE;
	// 시간제 아이템을 표시하기위한 변수
	private String timestamp;
	private boolean isTimeCheck;
	
	//치명타
	protected int Add_Min_Dmg;
	protected int Add_Max_Dmg;
	// 개인상점에 사용되는 변수
	private int usershopIdx; // sell 처리시 위치값 지정용.
	private int usershopBuyPrice; // 판매 가격
	private int usershopSellPrice; // 구입 가격
	private int usershopBuyCount; // 판매 갯수
	private int usershopSellCount; // 구입 갯수

	public ItemInstance() {
		//
	}

	static synchronized public ItemInstance clone(ItemInstance item) {
		if (item == null)
			item = new ItemInstance();
		return item;
	}
	
	public ItemInstance clone(Item item) {
		this.item = item;
		name = item.getNameId();
		String forcedCouponName = getPowerballCouponDisplayName(item);
		if (forcedCouponName != null)
			name = forcedCouponName;
		gfx = item.getGroundGfx();
		if(item.getLimitTime() > 0)
			limitTime = System.currentTimeMillis() + (item.getLimitTime() * 1000);
		return this;
	}

	@Override
	public void close() {
		super.close();
		// 메모리 초기화 함수.
		item = null;
		cha = null;
		time_drop = click_delay = quantity = enLevel = durability = dynamicMr = nowTime = usershopBuyPrice = usershopSellPrice = usershopBuyCount = usershopSellCount = usershopIdx = dynamicLight = dynamicAc = enEarth = enWater = enFire = enWind = 0;
		bless = 1;
		limitTime = 0;
		itemTimek = null;
		Add_Min_Dmg = Add_Max_Dmg = 0;
		dynamicStunDefence = 0;
		definite = equipped = false;
		isTimeCheck = false;
		dynamicStunHit = 0;
		DolloptionA = DolloptionB = DolloptionC = DolloptionD =DolloptionE =0;
		dynamicSp = dynamicReduction = dynamicIgnoreReduction = dynamicSwordCritical = dynamicBowCritical = dynamicMagicCritical = 0;
		tollTipMp = tollTipSp = tollTipHealingPotion = tollTipStunDefens = tollTipHp = tollTipReduction = tollTipHit = tollTipDmg = tollTipMr = tollTipPvPReduction = tollTipPvPDmg = 0;
	}

	/**
	 * 아이템을 사용해도 되는지 확인해주는 함수.<br/>
	 * : 아이템 더블클릭하면 젤 우선적으로 호출됨.<br/>
	 * : C_ItemClick 에서 사용중.<br/>
	 * 
	 * @return
	 */
	public boolean isClick(PcInstance pc) {
		if (pc != null) {
			// 맵에따른 아이템 제한 확인.
			switch (pc.getMap()) {
			case 22:
				// 게라드 시험 퀘 맵일경우 비취물약만 사용가능하도록 하기.
				if (item.getNameIdNumber() != 233) {
					// 귀환이나 순간이동 주문서 사용시 케릭동작에 락이 걸리기때문에 그것을 풀기위한것.
					pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
					return false;
				}
				break;
			case 201:
				// 마법사 30 퀘 일때. 귀환 빼고 다 불가능.
				if (item.getNameIdNumber() != 505) {
					pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
					return false;
				}
				break;
			}
			// 마법의 플룻에 따른 제한 확인.
			if (BuffController.find(pc).find(MagicFlute.class) != null) {
				pc.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 343));
				return false;
			}
		}
		
		// 딜레이 확인.
		long time = System.currentTimeMillis();
		// 물약은 따로 딜레이 체크
		if (this instanceof HealingPotion) {
			if (time - pc.getClickHealingPotionTime() >= item.getDelay()) {
				pc.setClickHealingPotionTime(time);
				return true;
			}
		} else {
			if (time - click_delay >= item.getDelay()) {
				click_delay = time;
				return true;
			}
		}

		return false;
	}
	public int getInvDolloptionA() {
		return DolloptionA;
	}

	public void setInvDolloptionA(int DolloptionA) {
		this.DolloptionA = DolloptionA;
	}
	public int getInvDolloptionB() {
		return DolloptionB;
	}

	public void setInvDolloptionB(int DolloptionB) {
		this.DolloptionB = DolloptionB;
	}
	public int getInvDolloptionC() {
		return DolloptionC;
	}

	public void setInvDolloptionC(int DolloptionC) {
		this.DolloptionC = DolloptionC;
	}
	public int getInvDolloptionD() {
		return DolloptionD;
	}

	public void setInvDolloptionD(int DolloptionD) {
		this.DolloptionD = DolloptionD;
	}
	public int getInvDolloptionE() {
		return DolloptionE;
	}

	public void setInvDolloptionE(int DolloptionE) {
		this.DolloptionE = DolloptionE;
	}
	public int getDynamicPvpDmg() {
		return dynamicPvpDmg;
	}

	public void setDynamicPvpDmg(int dynamicPvpDmg) {
		this.dynamicPvpDmg = dynamicPvpDmg;
	}

	public int getDynamicPvpReduction() {
		return dynamicPvpReduction;
	}

	public void setDynamicPvpReduction(int dynamicPvpReduction) {
		this.dynamicPvpReduction = dynamicPvpReduction;
	}
	
	public double getDynamicStunHit() {
		return dynamicStunHit;
	}

	public void setDynamicStunHit(double dynamicStunHit) {
		this.dynamicStunHit = dynamicStunHit;
	}

	public int getDynamicSp() {
		return dynamicSp;
	}

	public void setDynamicSp(int dynamicSp) {
		this.dynamicSp = dynamicSp;
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

	public int getDynamicSwordCritical() {
		return dynamicSwordCritical;
	}

	public void setDynamicSwordCritical(int dynamicSwordCritical) {
		this.dynamicSwordCritical = dynamicSwordCritical;
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
	
	public double getDynamicStunDefence() {
		return dynamicStunDefence;
	}

	public void setDynamicStunDefence(double dynamicStunDefence) {
		this.dynamicStunDefence = dynamicStunDefence;
	}

	public long getTimeDrop() {
		return time_drop;
	}

	public void setTimeDrop(long time_drop) {
		this.time_drop = time_drop;
	}

	public int getDynamicLight() {
		return dynamicLight;
	}

	public void setDynamicLight(int dynamicLight) {
		this.dynamicLight = dynamicLight;
	}

	public int getDynamicAc() {
		return dynamicAc;
	}

	public void setDynamicAc(int dynamicAc) {
		this.dynamicAc = dynamicAc;
	}

	public int getEnEarth() {
		return enEarth;
	}

	public int getEnEarthDamage() {
		switch (enEarth) {
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 7;
		case 5:
			return 9;
		default:
			return enEarth;
		}
	}

	public void setEnEarth(int enEarth) {
		if (enEarth > 5)
			enEarth = 5;
		this.enEarth = enEarth;
	}

	public int getEnWater() {
		return enWater;
	}

	public int getEnWaterDamage() {
		switch (enWater) {
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 7;
		case 5:
			return 9;
		default:
			return enWater;
		}
	}

	public void setEnWater(int enWater) {
		if (enWater > 5)
			enWater = 5;
		this.enWater = enWater;
	}

	public int getEnFire() {
		return enFire;
	}

	public int getEnFireDamage() {
		switch (enFire) {
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 7;
		case 5:
			return 9;
		default:
			return enFire;
		}
	}

	public void setEnFire(int enFire) {
		if (enFire > 5)
			enFire = 5;
		this.enFire = enFire;
	}

	public int getEnWind() {
		return enWind;
	}

	public int getEnWindDamage() {
		switch (enWind) {
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 7;
		case 5:
			return 9;
		default:
			return enWind;
		}
	}

	public void setEnWind(int enWind) {
		if (enWind > 5)
			enWind = 5;
		this.enWind = enWind;
	}

	@Override
	public Character getCharacter() {
		return cha;
	}

	public Item getItem() {
		return item;
	}

	public int getBless() {
		return bless;
	}

	public void setBless(int bless) {
		this.bless = bless;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getEnLevel() {
		return enLevel;
	}

	public void setEnLevel(int enLevel) {
		this.enLevel = enLevel;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		if (durability > Lineage.item_durability_max)
			durability = Lineage.item_durability_max;
		else if (durability < 0)
			durability = 0;
		this.durability = durability;

	}

	public int getDynamicMr() {
		return dynamicMr;
	}

	public void setDynamicMr(int dynamicMr) {
		this.dynamicMr = dynamicMr;
	}

	public boolean isDefinite() {
		return definite;
	}

	public void setDefinite(boolean definite) {
		this.definite = definite;
	}

	public boolean isEquipped() {
		return equipped;
	}

	public void setEquipped(boolean equipped) {
		this.equipped = equipped;
	}

	public int getNowTime() {
		return nowTime < 0 ? 0 : nowTime;
	}

	public void setNowTime(int nowTime) {
		if (nowTime >= 0)
			this.nowTime = nowTime;
	}
	
	public String getItemTimek() {
		return itemTimek;
	}

	public void setItemTimek(String itemTimek) {
		this.itemTimek = itemTimek;
	}

	
	public int getWeight() {
		return (int) Math.round(item.getWeight() * getCount());
	}

	public int getUsershopBuyPrice() {
		return usershopBuyPrice;
	}

	public void setUsershopBuyPrice(int usershopBuyPrice) {
		this.usershopBuyPrice = usershopBuyPrice;
	}

	public int getUsershopSellPrice() {
		return usershopSellPrice;
	}

	public void setUsershopSellPrice(int usershopSellPrice) {
		this.usershopSellPrice = usershopSellPrice;
	}

	public int getUsershopBuyCount() {
		return usershopBuyCount;
	}

	public void setUsershopBuyCount(int usershopBuyCount) {
		this.usershopBuyCount = usershopBuyCount;
	}

	public int getUsershopSellCount() {
		return usershopSellCount;
	}

	public void setUsershopSellCount(int usershopSellCount) {
		this.usershopSellCount = usershopSellCount;
	}

	public int getUsershopIdx() {
		return usershopIdx;
	}

	public int getAdd_Min_Dmg() {
		return Add_Min_Dmg;
	}

	public void setAdd_Min_Dmg(int Add_Min_Dmg) {
		this.Add_Min_Dmg = Add_Min_Dmg;
	}

	public int getAdd_Max_Dmg() {
		return Add_Max_Dmg;
	}

	public void setAdd_Max_Dmg(int Add_Max_Dmg) {
		this.Add_Max_Dmg = Add_Max_Dmg;
	}
	
	public void setUsershopIdx(int usershopIdx) {
		this.usershopIdx = usershopIdx;
	}
	
	public int getTollTipPvPDmg() {
		return tollTipPvPDmg;
	}

	public void setTollTipPvPDmg(int tollTipPvPDmg) {
		this.tollTipPvPDmg = tollTipPvPDmg;
	}

	public int getTollTipPvPReduction() {
		return tollTipPvPReduction;
	}

	public void setTollTipPvPReduction(int tollTipPvPReduction) {
		this.tollTipPvPReduction = tollTipPvPReduction;
	}

	public int getTollTipMr() {
		return tollTipMr;
	}

	public void setTollTipMr(int tollTipMr) {
		this.tollTipMr = tollTipMr;
	}

	public int getTollTipDmg() {
		return tollTipDmg;
	}

	public void setTollTipDmg(int tollTipDmg) {
		this.tollTipDmg = tollTipDmg;
	}

	public int getTollTipHit() {
		return tollTipHit;
	}

	public void setTollTipHit(int tollTipHit) {
		this.tollTipHit = tollTipHit;
	}

	public int getTollTipReduction() {
		return tollTipReduction;
	}

	public void setTollTipReduction(int tollTipReduction) {
		this.tollTipReduction = tollTipReduction;
	}

	public int getTollTipHp() {
		return tollTipHp;
	}

	public void setTollTipHp(int tollTipHp) {
		this.tollTipHp = tollTipHp;
	}

	public int getTollTipStunDefens() {
		return tollTipStunDefens;
	}

	public void setTollTipStunDefens(int tollTipStunDefens) {
		this.tollTipStunDefens = tollTipStunDefens;
	}

	public int getTollTipHealingPotion() {
		return tollTipHealingPotion;
	}

	public void setTollTipHealingPotion(int tollTipHealingPotion) {
		this.tollTipHealingPotion = tollTipHealingPotion;
	}

	public int getTollTipSp() {
		return tollTipSp;
	}

	public void setTollTipSp(int tollTipSp) {
		this.tollTipSp = tollTipSp;
	}

	public int getTollTipMp() {
		return tollTipMp;
	}

	public void setTollTipMp(int tollTipMp) {
		this.tollTipMp = tollTipMp;
	}

	public int getTollTipAc() {
		return tollTipAc;
	}

	public void setTollTipAc(int tollTipAc) {
		this.tollTipAc = tollTipAc;
	}
	
	public long getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(long limitTime) {
		this.limitTime = limitTime;
	}
	/**
	 * 리니지 월드에 접속했을때 착용중인 아이템 처리를 위해 사용되는 메서드.
	 * 
	 * @param pc
	 */
	public void toWorldJoin(Connection con, PcInstance pc) {
		//
		cha = pc;
		//
		if (getItem().getEnchantMr() != 0 || getItem().getEnchantStunDefense() != 0 || getItem().getEnchantStunHit() != 0 ||
			getItem().getEnchantSp() != 0 || getItem().getEnchantReduction() != 0 || getItem().getEnchantIgnoreReduction() != 0 || 
			getItem().getEnchantSwordCritical() != 0 || getItem().getEnchantBowCritical() != 0 || getItem().getEnchantMagicCritical() != 0 ||
			getItem().getEnchantPvpDamage() != 0 || getItem().getEnchantPvpReduction() != 0) {
			if (getItem().getName().equalsIgnoreCase("신성한 엘름의 축복")) {			
				if (getEnLevel() > 4)
					setDynamicMr((getEnLevel() - 4) * getItem().getEnchantMr());
			} else {
				setDynamicMr(getEnLevel() * getItem().getEnchantMr());
			}		
			setDynamicStunDefence(getEnLevel() * getItem().getEnchantStunDefense());
			setDynamicStunHit(getEnLevel() * getItem().getEnchantStunHit());
			setDynamicSp(getEnLevel() * getItem().getEnchantSp());
			setDynamicReduction(getEnLevel() * getItem().getEnchantReduction());
			setDynamicIgnoreReduction(getEnLevel() * getItem().getEnchantIgnoreReduction());
			setDynamicSwordCritical(getEnLevel() * getItem().getEnchantSwordCritical());
			setDynamicBowCritical(getEnLevel() * getItem().getEnchantBowCritical());
			setDynamicMagicCritical(getEnLevel() * getItem().getEnchantMagicCritical());
			setDynamicPvpDmg(getEnLevel() * getItem().getEnchantPvpDamage());
			setDynamicPvpReduction(getEnLevel() * getItem().getEnchantPvpReduction());
			
			if (Lineage.server_version > 144 && Lineage.server_version <= 200)
				pc.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
		}
	}

	@Override
	public void toPickup(Character cha) {
		this.cha = cha;
	}

	/**
	 * 해당 아이템이 드랍됫을때 호출되는 메서드.
	 * 
	 * @param cha
	 */
	public void toDrop(Character cha) {
		this.cha = null;
		setTimeDrop(System.currentTimeMillis());
	}

	/**
	 * 아이템 착용 및 해제시 호출되는 메서드.
	 * 
	 * @param cha
	 * @param inv
	 */
	public void toEquipped(Character cha, Inventory inv) {
	}

	/**
	 * 인첸트 활성화 됫을때 아이템의 뒷처리를 처리하도록 요청하는 메서드.
	 * 
	 * @param pc
	 * @param en
	 */
	public void toEnchant(PcInstance pc, int en) {
		if (getItem() == null || 
			(getItem().getEnchantMr() == 0 && getItem().getEnchantStunDefense() == 0 && getItem().getEnchantStunHit() == 0 &&
			getItem().getEnchantSp() == 0 && getItem().getEnchantReduction() == 0 && getItem().getEnchantIgnoreReduction() == 0 && 
			getItem().getEnchantSwordCritical() == 0 && getItem().getEnchantBowCritical() == 0 && getItem().getEnchantMagicCritical() == 0 &&
			getItem().getEnchantPvpDamage() == 0 && getItem().getEnchantPvpReduction() == 0) ||
			en == -126 || en == -127)
			return;
		// 인첸을 성공했다면 마법망토는 mr값을 상승해야함.
		if (en != 0) {
			int new_mr = getEnLevel() * getItem().getEnchantMr();
			double new_stunDefence = getEnLevel() * getItem().getEnchantStunDefense();
			double new_stunHit = getEnLevel() * getItem().getEnchantStunHit();
			int new_sp = getEnLevel() * getItem().getEnchantSp();
			int new_reduction = getEnLevel() * getItem().getEnchantReduction();
			int new_ignoreReduction = getEnLevel() * getItem().getEnchantIgnoreReduction();
			int new_swordCritical = getEnLevel() * getItem().getEnchantSwordCritical();
			int new_bowCritical = getEnLevel() * getItem().getEnchantBowCritical();
			int new_magicCritical = getEnLevel() * getItem().getEnchantMagicCritical();
			int new_pvp_dmg = getEnLevel() * getItem().getEnchantPvpDamage();
			int new_pvp_reduction = getEnLevel() * getItem().getEnchantPvpReduction();
			
			if (getItem().getName().equalsIgnoreCase("신성한 엘름의 축복")) {
				if (getEnLevel() > 4)
					new_mr = (getEnLevel() - 4) * getItem().getEnchantMr();
				else
					new_mr = 0;				
			}
			if (equipped) {	
				// 이전에 세팅값 빼기.
				pc.setDynamicMr(pc.getDynamicMr() - getDynamicMr());
				pc.setDynamicStunResist(pc.getDynamicStunResist() - getDynamicStunDefence());
				pc.setDynamicStunHit(pc.getDynamicStunHit() - getDynamicStunHit());
				pc.setDynamicSp(pc.getDynamicSp() - getDynamicSp());
				pc.setDynamicReduction(pc.getDynamicReduction() - getDynamicReduction());
				pc.setDynamicIgnoreReduction(pc.getDynamicIgnoreReduction() - getDynamicIgnoreReduction());
				pc.setDynamicCritical(pc.getDynamicCritical() - getDynamicSwordCritical());
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() - getDynamicBowCritical());
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() - getDynamicMagicCritical());
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() - getDynamicPvpDmg());
				pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() - getDynamicPvpReduction());

				// 인첸에따른 새로운값 적용.
				pc.setDynamicMr(pc.getDynamicMr() + new_mr);
				pc.setDynamicStunResist(pc.getDynamicStunResist() + new_stunDefence);
				pc.setDynamicStunHit(pc.getDynamicStunHit() + new_stunHit);
				pc.setDynamicSp(pc.getDynamicSp() + new_sp);
				pc.setDynamicReduction(pc.getDynamicReduction() + new_reduction);
				pc.setDynamicIgnoreReduction(pc.getDynamicIgnoreReduction() + new_ignoreReduction);
				pc.setDynamicCritical(pc.getDynamicCritical() + new_swordCritical);
				pc.setDynamicBowCritical(pc.getDynamicBowCritical() + new_bowCritical);
				pc.setDynamicMagicCritical(pc.getDynamicMagicCritical() + new_magicCritical);
				pc.setDynamicAddPvpDmg(pc.getDynamicAddPvpDmg() + new_pvp_dmg);
				pc.setDynamicAddPvpReduction(pc.getDynamicAddPvpReduction() + new_pvp_reduction);
				pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
			}
			setDynamicMr(new_mr);
			setDynamicStunDefence(new_stunDefence);
			setDynamicStunHit(new_stunHit);
			setDynamicSp(new_sp);
			setDynamicReduction(new_reduction);
			setDynamicIgnoreReduction(new_ignoreReduction);
			setDynamicSwordCritical(new_swordCritical);
			setDynamicBowCritical(new_bowCritical);
			setDynamicMagicCritical(new_magicCritical);
			setDynamicPvpDmg(new_pvp_dmg);
			setDynamicPvpReduction(new_pvp_reduction);

			if (Lineage.server_version <= 144)
				pc.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
			else
				pc.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
		}
	}

	/**
	 * 마법책 및 수정에 스킬값 지정하는 함수.
	 * 
	 * @param skill_level
	 */
	@Override
	public void setSkill(Skill skill) {
	}

	/**
	 * 아이템을 이용해 cha 가 o 에게 피해를 입히면 호출되는 함수.
	 * 
	 * @param cha
	 * @param o
	 * @return
	 */
	public boolean toDamage(Character cha, object o) {
		return false;
	}

	/**
	 * toDamage(Character cha, object o) 거친후 값이 true가 될경우 아래 함수를 호출해 추가적으로 데미지를 더하도록 함.
	 * 
	 * @return
	 */
	public int toDamage(int dmg) {
		return 0;
	}

	/**
	 * toDamage(Character cha, object o) 거친후 값이 true가 될경우 이팩트를 표현할 값을 턴.
	 * 
	 * @return
	 */
	public int toDamageEffect() {
		return 0;
	}

	/**
	 * 펫의 오프젝트값 리턴.
	 * 
	 * @return
	 */
	public long getPetObjectId() {
		return 0;
	}

	public void setPetObjectId(final long id) {
	}

	/**
	 * 여관방 열쇠 키값
	 * 
	 * @return
	 */
	public long getInnRoomKey() {
		return 0;
	}

	public void setInnRoomKey(final long key) {
	}

	/**
	 * 편지지 디비 연결 고리인 uid
	 * 
	 * @return
	 */
	public int getLetterUid() {
		return 0;
	}

	public void setLetterUid(final int uid) {
	}

	/**
	 * 레이스 관련 함수
	 * 
	 * @return
	 */
	public String getRaceTicket() {
		return "";
	}

	public void setRaceTicket(String ticket) {
	}

	public int getBressPacket() {
		if (Lineage.server_version > 144) {
			if (definite) {
				if (bless < 0)
					return Lineage.server_version > 280 ? bless : bless + 128;
				else
					return bless;
			} else {
				return 3;
			}
		} else {
			return bless;
		}
	}

	/**
	 * 레벨 제한 체크
	 */
	protected boolean isLvCheck(Character cha) {
		// 착용하지 않은 상태에서만 체크
		if (!isEquipped()) {
			if (item.getLevelMin() > 0 && item.getLevelMin() > cha.getLevel()) {
				//			cha.toSender(new SItemLevelFails(item.Level));
				// 672 : 이 아이템은 %d레벨 이상이 되어야 사용할 수 있습니다.
				ChattingController.toChatting(cha, String.format("이 아이템은 %d레벨 이상이 되어야 사용할 수 있습니다.", item.getLevelMin()), Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
			if (item.getLevelMax() > 0 && item.getLevelMax() < cha.getLevel()) {
				// 673 : 이 아이템은 %d레벨 이하일때만 사용할 수 있습니다.
				ChattingController.toChatting(cha, String.format("이 아이템은 %d레벨 이하일때만 사용할 수 있습니다.", item.getLevelMax()), Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
		}

		return true;
	}

	/**
	 * 클레스 착용가능 여부 체크 부분
	 */
	protected boolean isClassCheck(Character cha) {
		switch (cha.getClassType()) {
		case Lineage.LINEAGE_CLASS_ROYAL: // 군주
			return item.getRoyal() > 0;
		case Lineage.LINEAGE_CLASS_KNIGHT: // 기사
			return item.getKnight() > 0;
		case Lineage.LINEAGE_CLASS_ELF: // 요정
			return item.getElf() > 0;
		case Lineage.LINEAGE_CLASS_WIZARD: // 법사
			return item.getWizard() > 0;
		case Lineage.LINEAGE_CLASS_DARKELF: // 다크엘프
			return item.getDarkElf() > 0;
		case Lineage.LINEAGE_CLASS_DRAGONKNIGHT: // 용기사
			return item.getDragonKnight() > 0;
		case Lineage.LINEAGE_CLASS_BLACKWIZARD: // 환술사
			return item.getBlackWizard() > 0;
		}
		return true;
	}

	/**
	 * 아이템을 착용 및 해제할때 호출됨. : 장비를 해제할때 제거해야할 버프가 있는지 확인하고 제거하는 메서드.
	 * 
	 * @param cha
	 */
	public void toBuffCheck(Character cha) {
		// 착용상태는 무시.
		if (isEquipped())
			return;

		if (getItem().getSlot() == Lineage.SLOT_WEAPON)
			BuffController.remove(cha, CounterBarrier.class);
		if (getItem().getSlot() == Lineage.SLOT_SHIELD)
			BuffController.remove(cha, SolidCarriage.class);
	}

	/**
	 * 아이템 부가옵션 적용및 해제 부분
	 */
	public void toOption(Character cha, boolean sendPacket) {
		if (getItem() == null)
			return;
		if (this.getItem().getName().contains("마법인형: ") && getInvDolloptionA() > 0) {

			double 스턴내성 = 0;

			스턴내성 = (getInvDolloptionA() * 0.01);

			if (equipped) {
				cha.setDynamicStunResist(cha.getDynamicStunResist() + 스턴내성);
				if (getInvDolloptionA() > 0)
					ChattingController.toChatting(cha, String.format("%s [인형 부여 옵션] : 스턴 내성+ %d", getItem().getName(), getInvDolloptionA()), Lineage.CHATTING_MODE_MESSAGE);

			} else {
				cha.setDynamicStunResist(cha.getDynamicStunResist() - 스턴내성);
			}
		}
		if(this.getItem().getName().contains("마법인형: ") && getInvDolloptionB() > 0 ){
			double 스턴적중 = 0;
			스턴적중=(getInvDolloptionB()*0.01);
			if (equipped) {
				cha.setDynamicStunHit(cha.getDynamicStunHit() + 스턴적중);
				if (getInvDolloptionB() > 0)
					ChattingController.toChatting(cha, String.format("%s [인형 부여 옵션] : 스턴 적중+ %d", getItem().getName(),getInvDolloptionB()), Lineage.CHATTING_MODE_MESSAGE);

			} else {
				cha.setDynamicStunHit(cha.getDynamicStunHit() - 스턴적중);
			}
		}
		if(this.getItem().getName().contains("마법인형: ") && getInvDolloptionC() > 0 ){
			int 마법적중 = 0;
			마법적중 = getInvDolloptionC();
			
			if (equipped) {
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + 마법적중);
				if (getInvDolloptionC() > 0)
					ChattingController.toChatting(cha, String.format("%s [인형 부여 옵션] : 마법 적중+ %d", getItem().getName(),getInvDolloptionC()), Lineage.CHATTING_MODE_MESSAGE);

			} else {
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - 마법적중);
			}
		}
		if(this.getItem().getName().contains("마법인형: ") && getInvDolloptionD() > 0 ){
			int 대미지 = 0;
			대미지 = getInvDolloptionD();
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 대미지);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 대미지);
				if (getInvDolloptionD() > 0)
					ChattingController.toChatting(cha, String.format("%s [인형 부여 옵션] : 근/원거리 대미지 + %d", getItem().getName(),getInvDolloptionD()), Lineage.CHATTING_MODE_MESSAGE);

			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 대미지);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 대미지);
			}
		}
		if(this.getItem().getName().contains("마법인형: ") && getInvDolloptionE() > 0 ){
			int 명중 = 0;
			명중 = getInvDolloptionE();
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 명중);
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 명중);
				
				if (getInvDolloptionE() > 0)
					ChattingController.toChatting(cha, String.format("%s [인형 부여 옵션] : 근/원거리 명중 + %d", getItem().getName(),getInvDolloptionE()), Lineage.CHATTING_MODE_MESSAGE);

			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 명중);
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 명중);
			}
		}
		if (getItem().getAddStr() != 0) {
			if (equipped) {
				cha.setDynamicStr(cha.getDynamicStr() + getItem().getAddStr());
			} else {
				cha.setDynamicStr(cha.getDynamicStr() - getItem().getAddStr());
			}
		}
		if (getItem().getAddDex() != 0) {
			if (equipped) {
				cha.setDynamicDex(cha.getDynamicDex() + getItem().getAddDex());
			} else {
				cha.setDynamicDex(cha.getDynamicDex() - getItem().getAddDex());
			}

		}
		if (getItem().getAddCon() != 0) {
			if (equipped) {
				cha.setDynamicCon(cha.getDynamicCon() + getItem().getAddCon());
			} else {
				cha.setDynamicCon(cha.getDynamicCon() - getItem().getAddCon());
			}
		}
		if (getItem().getAddInt() != 0) {
			if (equipped) {
				cha.setDynamicInt(cha.getDynamicInt() + getItem().getAddInt());
			} else {
				cha.setDynamicInt(cha.getDynamicInt() - getItem().getAddInt());
			}
		}
		if (getItem().getAddCha() != 0) {
			if (equipped) {
				cha.setDynamicCha(cha.getDynamicCha() + getItem().getAddCha());
			} else {
				cha.setDynamicCha(cha.getDynamicCha() - getItem().getAddCha());
			}
		}
		if (getItem().getAddWis() != 0) {
			if (equipped) {
				cha.setDynamicWis(cha.getDynamicWis() + getItem().getAddWis());
			} else {
				cha.setDynamicWis(cha.getDynamicWis() - getItem().getAddWis());
			}
		}
		if (getItem().getAddHp() > 0) {
			if (equipped)
				cha.setDynamicHp(cha.getDynamicHp() + getItem().getAddHp());
			else
				cha.setDynamicHp(cha.getDynamicHp() - getItem().getAddHp());
		}
		if (getItem().getAddMp() > 0) {
			if (equipped) {
				cha.setDynamicMp(cha.getDynamicMp() + getItem().getAddMp());
			} else {
				cha.setDynamicMp(cha.getDynamicMp() - getItem().getAddMp());
			}
		}
		if (getItem().getAddMr() > 0 || getDynamicMr() > 0) {
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + getItem().getAddMr() + getDynamicMr());
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - getItem().getAddMr() - getDynamicMr());
			}
		}
		if (getItem().getStunDefense() > 0 || getDynamicStunDefence() > 0) {
			if (equipped) {
				cha.setDynamicStunResist(Math.round((cha.getDynamicStunResist() + getItem().getStunDefense() + getDynamicStunDefence()) * 100) / 100.0);
			} else {
				cha.setDynamicStunResist(Math.round((cha.getDynamicStunResist() - getItem().getStunDefense() - getDynamicStunDefence()) * 100) / 100.0);
			}
		}
		if (getItem().getAddWeight() > 0) {
			if (equipped) {
				cha.setItemWeight(cha.getItemWeight() + getItem().getAddWeight());
			} else {
				cha.setItemWeight(cha.getItemWeight() - getItem().getAddWeight());
			}
		}
		if (getItem().getTicHp() > 0) {
			if (equipped) {
				cha.setDynamicTicHp(cha.getDynamicTicHp() + getItem().getTicHp());
			} else {
				cha.setDynamicTicHp(cha.getDynamicTicHp() - getItem().getTicHp());
			}
		}
		if (getItem().getTicMp() > 0) {
			if (equipped) {
				cha.setDynamicTicMp(cha.getDynamicTicMp() + getItem().getTicMp());
			} else {
				cha.setDynamicTicMp(cha.getDynamicTicMp() - getItem().getTicMp());
			}
		}
		if (getItem().getEarthress() > 0) {
			if (equipped) {
				cha.setDynamicEarthress(cha.getDynamicEarthress() + getItem().getEarthress());
			} else {
				cha.setDynamicEarthress(cha.getDynamicEarthress() - getItem().getEarthress());
			}
		}
		if (getItem().getFireress() > 0) {
			if (equipped) {
				cha.setDynamicFireress(cha.getDynamicFireress() + getItem().getFireress());
			} else {
				cha.setDynamicFireress(cha.getDynamicFireress() - getItem().getFireress());
			}
		}
		if (getItem().getWindress() > 0) {
			if (equipped) {
				cha.setDynamicWindress(cha.getDynamicWindress() + getItem().getWindress());
			} else {
				cha.setDynamicWindress(cha.getDynamicWindress() - getItem().getWindress());
			}
		}
		if (getItem().getWaterress() > 0) {
			if (equipped) {
				cha.setDynamicWaterress(cha.getDynamicWaterress() + getItem().getWaterress());
			} else {
				cha.setDynamicWaterress(cha.getDynamicWaterress() - getItem().getWaterress());
			}
		}
		if (getItem().getPolyName() != null && getItem().getPolyName().length() > 0) {
			Poly p = PolyDatabase.getPolyName(getItem().getPolyName());
			// 변신 상태가 아니거나 변신하려는 gfx 와 같을때만 처리.
			if (cha.getGfx() == cha.getClassGfx() || cha.getGfx() == p.getGfxId()) {
				if (equipped) {
					ShapeChange.onBuff(cha, cha, p, -1, false, sendPacket);
				} else {
					BuffController.remove(cha, ShapeChange.class);
				}
			}
		}
		if (getItem().getAddReduction() > 0 || getDynamicReduction() > 0) {
			if (equipped) {
				cha.setDynamicReduction(cha.getDynamicReduction() + getItem().getAddReduction() + getDynamicReduction());
			} else {
				cha.setDynamicReduction(cha.getDynamicReduction() - getItem().getAddReduction() - getDynamicReduction());
			}
		}
		
		if (getItem().getIgnoreReduction() > 0 || getDynamicIgnoreReduction() > 0) {
			if (equipped) {
				cha.setDynamicIgnoreReduction(cha.getDynamicIgnoreReduction() + getItem().getIgnoreReduction() + getDynamicIgnoreReduction());
			} else {
				cha.setDynamicIgnoreReduction(cha.getDynamicIgnoreReduction() - getItem().getIgnoreReduction() - getDynamicIgnoreReduction());
			}
		}

		if (getItem().getAddSp() > 0 || getDynamicSp() > 0) {
			if (equipped)
				cha.setDynamicSp(cha.getDynamicSp() + item.getAddSp() + getDynamicSp());
			else
				cha.setDynamicSp(cha.getDynamicSp() - item.getAddSp() - getDynamicSp());
		}

		if (getItem().getAddDmg() > 0) {
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + item.getAddDmg());
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + item.getAddDmg());
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - item.getAddDmg());
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - item.getAddDmg());
			}
		}
		
		if (getItem().getAddHit() > 0) {
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + item.getAddHit());
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + item.getAddHit());
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - item.getAddHit());
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - item.getAddHit());
			}
		}
		
		if (getItem().getAddMagicHit() > 0) {
			if (equipped)
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + item.getAddMagicHit());
			else
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - item.getAddMagicHit());
		}
		
		if (getItem().getAddCriticalSword() > 0 || getDynamicSwordCritical() > 0) {
			if (equipped)
				cha.setDynamicCritical(cha.getDynamicCritical() + item.getAddCriticalSword() + getDynamicSwordCritical());
			else
				cha.setDynamicCritical(cha.getDynamicCritical() - item.getAddCriticalSword() - getDynamicSwordCritical());
		}
		
		if (getItem().getAddCriticalBow() > 0 || getDynamicBowCritical() > 0) {
			if (equipped)
				cha.setDynamicBowCritical(cha.getDynamicBowCritical() + item.getAddCriticalBow() + getDynamicBowCritical());
			else
				cha.setDynamicBowCritical(cha.getDynamicBowCritical() - item.getAddCriticalBow() - getDynamicBowCritical());
		}
		
		if (getItem().getAddCriticalMagic() > 0 || getDynamicMagicCritical() > 0) {
			if (equipped)
				cha.setDynamicMagicCritical(cha.getDynamicMagicCritical() + item.getAddCriticalMagic() + getDynamicMagicCritical());
			else
				cha.setDynamicMagicCritical(cha.getDynamicMagicCritical() - item.getAddCriticalMagic() - getDynamicMagicCritical());
		}
		
		if (getItem().getStunHit() > 0 || getDynamicStunHit() > 0) {
			if (equipped)
				cha.setDynamicStunHit(Math.round((cha.getDynamicStunHit() + getItem().getStunHit() + getDynamicStunHit()) * 100) / 100.0);
			else
				cha.setDynamicStunHit(Math.round((cha.getDynamicStunHit() - getItem().getStunHit() - getDynamicStunHit()) * 100) / 100.0);
		}
		
		if (getItem().getPvpDamage() > 0 || getDynamicPvpDmg() > 0) {
			if (equipped)
				cha.setDynamicAddPvpDmg(cha.getDynamicAddPvpDmg() + getItem().getPvpDamage() + getDynamicPvpDmg());
			else
				cha.setDynamicAddPvpDmg(cha.getDynamicAddPvpDmg() - getItem().getPvpDamage() - getDynamicPvpDmg());
		}
		
		if (getItem().getPvpReduction() > 0 || getDynamicPvpReduction() > 0) {
			if (equipped)
				cha.setDynamicAddPvpReduction(cha.getDynamicAddPvpReduction() + getItem().getPvpReduction() + getDynamicPvpReduction());
			else
				cha.setDynamicAddPvpReduction(cha.getDynamicAddPvpReduction() - getItem().getPvpReduction() - getDynamicPvpReduction());
		}

		// 지팡이를 제외한 축무기 추가 대미지
		if ((getBless() == 0 || getBless() == -128) && getItem().getType1().equalsIgnoreCase("weapon") && !getItem().getType2().equalsIgnoreCase("wand")) {
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 2);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 2);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 2);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 2);
			}
		}

		// 축복 지팡이 추가 SP
		if ((getBless() == 0 || getBless() == -128) && getItem().getType2().equalsIgnoreCase("wand")) {
			if (equipped)
				cha.setDynamicSp(cha.getDynamicSp() + 1);
			else
				cha.setDynamicSp(cha.getDynamicSp() - 1);
		}

		// 축복 방어구 추가 HP
		if ((getBless() == 0 || getBless() == -128) && !isAcc() && getItem().getType1().equalsIgnoreCase("armor")) {
			if (equipped)
				cha.setDynamicHp(cha.getDynamicHp() + 10);
			else
				cha.setDynamicHp(cha.getDynamicHp() - 10);
		}

		// 축복 장신구 추가 대미지, 추가 명중
		if ((getBless() == 0 || getBless() == -128) && isAcc()) {
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 1);
				cha.setDynamicSp(cha.getDynamicSp() + 1);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 1);
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 1);
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 1);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 1);
				cha.setDynamicSp(cha.getDynamicSp() - 1);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 1);
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 1);
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 1);
			}	
		}
		
		if (getItem().getType2().equalsIgnoreCase("necklace") && getEnLevel() > 0) {
			int hp = 0;
			int hpPotion = 0;
			double stunResist = 0;
			
			switch (getEnLevel()) {
			case 1:
				hp += 5;
				break;
			case 2:
				hp += 10;
				break;
			case 3:
				hp += 20;
				break;
			case 4:
				hp += 30;
				break;
			case 5:
				hp += 40;
				hpPotion += 2;
				break;
			case 6:
				hp += 40;
				hpPotion += 4;
				break;
			case 7:
				hp += 50;
				hpPotion += 6;
				stunResist += 0.02;
				break;
			case 8:
				hp += 50;
				hpPotion += 8;
				stunResist += 0.03;
				break;
			case 9:
				hp += 60;
				hpPotion += 9;
				stunResist += 0.04;
				break;
			case 10:
				hp += 70;
				hpPotion += 10;
				stunResist += 0.05;
				break;
			}
			
			if (equipped) {
				cha.setDynamicHp(cha.getDynamicHp() + hp);
				cha.setDynamicHpPotion(cha.getDynamicHpPotion() + hpPotion);
				cha.setDynamicStunResist(cha.getDynamicStunResist() + stunResist);
			} else {
				cha.setDynamicHp(cha.getDynamicHp() - hp);
				cha.setDynamicHpPotion(cha.getDynamicHpPotion() - hpPotion);
				cha.setDynamicStunResist(cha.getDynamicStunResist() - stunResist);
			}
		}
		
		if (getItem().getType2().equalsIgnoreCase("ring") && getEnLevel() > 0) {
			int hp = 0;
			int addDmg = 0;
			int mr = 0;
			int sp = 0;
			int pvpDmg = 0;
			
			switch (getEnLevel()) {
			case 1:
				hp += 5;
				break;
			case 2:
				hp += 10;
				break;
			case 3:
				hp += 20;
				break;
			case 4:
				hp += 30;
				break;
			case 5:
				hp += 40;
				addDmg += 1;
				break;
			case 6:
				hp += 40;
				addDmg += 2;
				mr += 1;
				break;
			case 7:
				hp += 50;
				addDmg += 3;
				mr += 3;
				sp += 1;
				pvpDmg += 1;
				break;
			case 8:
				hp += 50;
				addDmg += 4;
				mr += 5;
				sp += 2;
				pvpDmg += 2;
				break;
			case 9:
				hp += 60;
				addDmg += 5;
				mr += 7;
				sp += 3;
				pvpDmg += 3;
				break;
			case 10:
				hp += 70;
				addDmg += 6;
				mr += 8;
				sp += 4;
				pvpDmg += 4;
				break;
			}
			
			if (equipped) {
				cha.setDynamicHp(cha.getDynamicHp() + hp);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + addDmg);
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicAddPvpDmg(cha.getDynamicAddPvpDmg() + pvpDmg);
			} else {
				cha.setDynamicHp(cha.getDynamicHp() - hp);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - addDmg);
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicAddPvpDmg(cha.getDynamicAddPvpDmg() - pvpDmg);
			}
		}
		
		if (getItem().getType2().equalsIgnoreCase("belt")) {
			int mp = 0;
			int reduction = 0;
			int hp = 0;
			int pvpReduction = 0;
			
			switch (getEnLevel()) {
			case 1:
				mp += 5;
				break;
			case 2:
				mp += 10;
				break;
			case 3:
				mp += 20;
				break;
			case 4:
				mp += 30;
				break;
			case 5:
				mp += 40;
				reduction += 1;
				break;
			case 6:
				mp += 40;
				reduction += 2;
				hp += 20;
				break;
			case 7:
				mp += 50;
				reduction += 3;
				hp += 30;
				pvpReduction += 2;
				break;
			case 8:
				mp += 50;
				reduction += 4;
				hp += 40;
				pvpReduction += 3;
				break;
			case 9:
				mp += 60;
				reduction += 5;
				hp += 50;
				pvpReduction += 4;
				break;
			case 10:
				mp += 70;
				reduction += 6;
				hp += 60;
				pvpReduction += 5;
				break;
			}
			
			if (equipped) {
				cha.setDynamicMp(cha.getDynamicMp() + mp);
				cha.setDynamicReduction(cha.getDynamicReduction() + reduction);
				cha.setDynamicHp(cha.getDynamicHp() + hp);
				cha.setDynamicAddPvpReduction(cha.getDynamicAddPvpReduction() + pvpReduction);
			} else {
				cha.setDynamicMp(cha.getDynamicMp() - mp);
				cha.setDynamicReduction(cha.getDynamicReduction() - reduction);
				cha.setDynamicHp(cha.getDynamicHp() - hp);
				cha.setDynamicAddPvpReduction(cha.getDynamicAddPvpReduction() - pvpReduction);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("완력의 부츠") || getItem().getName().equalsIgnoreCase("민첩의 부츠") || getItem().getName().equalsIgnoreCase("지식의 부츠")) {
			int addHp = 0;
			int reduction = 0;
			
			switch (getEnLevel()) {
			case 7:
				addHp += 20;
				break;
			case 8:
				addHp += 40;
				break;
			case 9:
				addHp += 60;
				reduction += 1;
				break;
			case 10:
				addHp += 70;
				reduction += 2;
				break;
			}
			
			if (equipped) {
				cha.setDynamicHp(cha.getDynamicHp() + addHp);
				cha.setDynamicReduction(cha.getDynamicReduction() + reduction);
			} else {
				cha.setDynamicHp(cha.getDynamicHp() - addHp);
				cha.setDynamicReduction(cha.getDynamicReduction() - reduction);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("수호성의 파워 글로브") || getItem().getName().equalsIgnoreCase("수호성의 활 골무")) {
			int addHit = 0;
			
			switch (getEnLevel()) {
			case 5:
				addHit += 1;
				break;
			case 6:
				addHit += 2;
				break;
			case 7:
				addHit += 3;
				break;
			case 8:
				addHit += 4;
				break;
			case 9:
				addHit += 5;
				break;
			case 10:
				addHit += 6;
				break;
			}
			
			if (equipped) {
				if (getItem().getName().equalsIgnoreCase("수호성의 파워 글로브"))
					cha.setDynamicAddHit(cha.getDynamicAddHit() + addHit);
				else
					cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + addHit);
			} else {
				if (getItem().getName().equalsIgnoreCase("수호성의 파워 글로브"))
					cha.setDynamicAddHit(cha.getDynamicAddHit() - addHit);
				else
					cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - addHit);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("안타라스의 완력") || getItem().getName().equalsIgnoreCase("안타라스의 마력")
			|| getItem().getName().equalsIgnoreCase("안타라스의 인내력") || getItem().getName().equalsIgnoreCase("안타라스의 예지력")) {
			int reduction = 0;
			
			switch (getEnLevel()) {
			case 7:
				reduction += 1;
				break;
			case 8:
				reduction += 2;
				break;
			case 9:
				reduction += 3;
				break;
			case 10:
				reduction += 4;
				break;
			}
			
			if (equipped) {
				cha.setAntarasArmor(true);
				cha.setDynamicReduction(cha.getDynamicReduction() + reduction);
			} else {
				cha.setAntarasArmor(false);
				cha.setDynamicReduction(cha.getDynamicReduction() - reduction);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("파푸리온의 완력") || getItem().getName().equalsIgnoreCase("파푸리온의 마력")
				|| getItem().getName().equalsIgnoreCase("파푸리온의 인내력") || getItem().getName().equalsIgnoreCase("파푸리온의 예지력")) {
				if (equipped) {
					cha.setFafurionArmor(true);
					cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, 2245));
				} else {
					cha.setFafurionArmor(false);
				}
			}
		
		if (getItem().getName().equalsIgnoreCase("린드비오르의 완력") || getItem().getName().equalsIgnoreCase("린드비오르의 마력")
				|| getItem().getName().equalsIgnoreCase("린드비오르의 인내력") || getItem().getName().equalsIgnoreCase("린드비오르의 예지력")) {
				if (equipped)
					cha.setLindviorArmor(true);
				else
					cha.setLindviorArmor(false);
			}
		
		if (getItem().getName().equalsIgnoreCase("발라카스의 완력") || getItem().getName().equalsIgnoreCase("발라카스의 마력")
				|| getItem().getName().equalsIgnoreCase("발라카스의 인내력") || getItem().getName().equalsIgnoreCase("발라카스의 예지력")) {
			int ignore_reduction = 0;
			int critical = getItem().getName().equalsIgnoreCase("발라카스의 완력") ? 3 : getItem().getName().equalsIgnoreCase("발라카스의 예지력") ? 2 : 0;
			int criticalBow = getItem().getName().equalsIgnoreCase("발라카스의 인내력") ? 2 : 0;
			int criticalMagic = getItem().getName().equalsIgnoreCase("발라카스의 마력") ? 2 : 0;
			
			switch (getEnLevel()) {
			case 7:
				ignore_reduction += 1;
				critical += getItem().getName().equalsIgnoreCase("발라카스의 완력") || getItem().getName().equalsIgnoreCase("발라카스의 예지력") ? 1 : 0;
				criticalBow += getItem().getName().equalsIgnoreCase("발라카스의 인내력") ? 1 : 0;
				criticalMagic += getItem().getName().equalsIgnoreCase("발라카스의 마력") ? 1 : 0;
				break;
			case 8:
				ignore_reduction += 2;
				critical += getItem().getName().equalsIgnoreCase("발라카스의 완력") || getItem().getName().equalsIgnoreCase("발라카스의 예지력") ? 2 : 0;
				criticalBow += getItem().getName().equalsIgnoreCase("발라카스의 인내력") ? 2 : 0;
				criticalMagic += getItem().getName().equalsIgnoreCase("발라카스의 마력") ? 2 : 0;
				break;
			case 9:
				ignore_reduction += 3;
				critical += getItem().getName().equalsIgnoreCase("발라카스의 완력") || getItem().getName().equalsIgnoreCase("발라카스의 예지력") ? 3 : 0;
				criticalBow += getItem().getName().equalsIgnoreCase("발라카스의 인내력") ? 3 : 0;
				criticalMagic += getItem().getName().equalsIgnoreCase("발라카스의 마력") ? 3 : 0;
				break;
			case 10:
				ignore_reduction += 3;
				critical += getItem().getName().equalsIgnoreCase("발라카스의 완력") || getItem().getName().equalsIgnoreCase("발라카스의 예지력") ? 4 : 0;
				criticalBow += getItem().getName().equalsIgnoreCase("발라카스의 인내력") ? 4 : 0;
				criticalMagic += getItem().getName().equalsIgnoreCase("발라카스의 마력") ? 4 : 0;
				break;
			}
			
				if (equipped) {
					cha.setValakasArmor(true);
					cha.setDynamicIgnoreReduction(cha.getDynamicIgnoreReduction() + ignore_reduction);
					cha.setDynamicCritical(cha.getDynamicCritical() + critical);
					cha.setDynamicBowCritical(cha.getDynamicBowCritical() + criticalBow);
					cha.setDynamicMagicCritical(cha.getDynamicMagicCritical() + criticalMagic);
				} else {
					cha.setValakasArmor(false);
					cha.setDynamicIgnoreReduction(cha.getDynamicIgnoreReduction() - ignore_reduction);
					cha.setDynamicCritical(cha.getDynamicCritical() - critical);
					cha.setDynamicBowCritical(cha.getDynamicBowCritical() - criticalBow);
					cha.setDynamicMagicCritical(cha.getDynamicMagicCritical() - criticalMagic);
				}
			}
		
		if (getItem().getName().equalsIgnoreCase("고대 신의 창")) {
			int addDmg = 0;
			int critical = 3;
			
			if (getEnLevel() > 0) {
				addDmg = getEnLevel() * 2;
				critical = getEnLevel();
			}
			
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() + critical);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() - critical);
			}
		}
		if (getItem().getName().equalsIgnoreCase("드래곤 슬레이어")) {
			int addDmg = 0;
			int critical = 0;
			double skillHit = 10;
			
			if (getEnLevel() > 0) {
				addDmg = getEnLevel() * 4;
				critical = getEnLevel();
				skillHit += getEnLevel();
			}
			
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() + critical);
				cha.setKnightSkillHit(cha.getKnightSkillHit() + skillHit);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() - critical);
				cha.setKnightSkillHit(cha.getKnightSkillHit() - skillHit);
			}
		}
		if (getItem().getName().equalsIgnoreCase("진명황의 집행검")) {
			int addDmg = 0;
			int critical = 0;
			double skillHit = 10;
			
			if (getEnLevel() > 0) {
				addDmg = getEnLevel() * 4;
				critical = getEnLevel();
				skillHit += getEnLevel();
			}
			
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() + critical);
				cha.setKnightSkillHit(cha.getKnightSkillHit() + skillHit);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() - critical);
				cha.setKnightSkillHit(cha.getKnightSkillHit() - skillHit);
			}
		}
		if (getItem().getName().equalsIgnoreCase("바람칼날의 단검")) {
			int addDmg = 0;
			int critical = 0;
			int 근거리명중 = 0;
			
			if (getEnLevel() > 0) {
				addDmg = getEnLevel() * 2;
				critical = getEnLevel();
				근거리명중 = getEnLevel();
			}
			
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() + critical);
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);
				cha.setDynamicCritical(cha.getDynamicCritical() - critical);
	
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("가이아의 격노")) {
			int addDmg = 0;
			int critical = 1;
			int ignoreReduction = 12;
			double skillHit = 2;
			
			if (getEnLevel() > 0) {
				addDmg = getEnLevel() * 4;
				critical = critical+getEnLevel();
				ignoreReduction = ignoreReduction+getEnLevel();
				skillHit += getEnLevel();
			}
			
			if (equipped) {
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + addDmg);
				cha.setDynamicBowCritical(cha.getDynamicBowCritical() + critical);
				cha.setDynamicIgnoreReduction(cha.getDynamicIgnoreReduction() + ignoreReduction);
				cha.setElfSkillHit(cha.getElfSkillHit() + skillHit);
			} else {
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - addDmg);
				cha.setDynamicBowCritical(cha.getDynamicBowCritical() - critical);
				cha.setDynamicIgnoreReduction(cha.getDynamicIgnoreReduction() - ignoreReduction);
				cha.setElfSkillHit(cha.getElfSkillHit() - skillHit);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("수정 결정체 지팡이")) {
			int addDmg = 0;
			int magicDmg = 0;
			int sp = 0;
			int magicHit = 2;
			
			if (getEnLevel() > 0) {
				addDmg = getEnLevel() * 2;
				sp = getEnLevel();
				magicDmg = getEnLevel();
				magicHit =  magicHit +getEnLevel();
			}
			
			if (equipped) {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);
				cha.setDynamicMagicDmg(cha.getDynamicMagicDmg() + magicDmg);
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + magicHit);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);
				cha.setDynamicMagicDmg(cha.getDynamicMagicDmg() - magicDmg);
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - magicHit);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("시어의 심안")) {
			int magicHit = 0;
			
			switch (getEnLevel()) {
			case 7:
				magicHit = 1;
				break;
			case 8:
				magicHit = 2;
				break;
			case 9:
				magicHit = 3;
				break;
			case 10:
				magicHit = 4;
				break;
			}
			
			if (equipped) {
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + magicHit);
				
				if (magicHit > 0)
					ChattingController.toChatting(cha, String.format("%s: 마법 적중+%d%%", getItem().getName(), magicHit), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - magicHit);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("리치 로브")) {
			int sp = 0;
			
			switch (getEnLevel()) {
			case 3:
				sp = 1;
				break;
			case 4:
				sp = 2;
				break;
			case 5:
				sp = 3;
				break;
			case 6:
				sp = 4;
				break;
			case 7:
				sp = 5;
				break;
			case 8:
				sp = 6;
				break;
			case 9:
				sp = 7;
				break;
			case 10:
				sp = 8;
				break;
			}
			
			if (equipped) {
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				
				if (sp > 0)
					ChattingController.toChatting(cha, String.format("%s: SP+%d", getItem().getName(), sp), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicSp(cha.getDynamicSp() - sp);
			}
		}
		if (getItem().getName().equalsIgnoreCase("악마왕의 양손검")) {
			int 근거리대미지 = 0;
			int  근거리명중  = 0;
			
			switch (getEnLevel()) {
			case 1:
				근거리대미지=1;
				근거리명중 = 1;
				break;
			case 2:
				근거리대미지=2;
				근거리명중 = 2;
				break;
			case 3:
				근거리대미지=3;
				근거리명중 = 3;
				break;
			case 4:
				근거리대미지=4;
				근거리명중 = 4;
				break;
			case 5:
				근거리대미지=5;
				근거리명중 = 5;
				break;
			case 6:
				근거리대미지=6;
				근거리명중 = 6;
				break;
			case 7:
				근거리대미지=7;
				근거리명중 = 7;
				break;
			case 8:
				근거리대미지=9;
				근거리명중 = 8;
				break;
			case 9:
				근거리대미지=11;
				근거리명중 = 9;
				break;
			case 10:
				근거리대미지=13;
				근거리명중 = 10;
				break;
			case 11:
				근거리대미지=15;
				근거리명중 = 11;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근거리대미지);

				
				if (근거리명중 > 0 || 근거리대미지 > 0 )
					ChattingController.toChatting(cha, String.format("%s: 근거리명중+%d, 근거리 대미지+%d", getItem().getName(), 근거리명중, 근거리대미지), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근거리대미지);

			}
		}
		if (getItem().getName().equalsIgnoreCase("악마왕의 한손검")) {
			int 근거리대미지 = 0;
			int  근거리명중  = 0;
			
			switch (getEnLevel()) {
			case 1:
				근거리대미지=1;
				근거리명중 = 1;
				break;
			case 2:
				근거리대미지=2;
				근거리명중 = 2;
				break;
			case 3:
				근거리대미지=3;
				근거리명중 = 3;
				break;
			case 4:
				근거리대미지=4;
				근거리명중 = 4;
				break;
			case 5:
				근거리대미지=5;
				근거리명중 = 5;
				break;
			case 6:
				근거리대미지=6;
				근거리명중 = 6;
				break;
			case 7:
				근거리대미지=7;
				근거리명중 = 7;
				break;
			case 8:
				근거리대미지=9;
				근거리명중 = 8;
				break;
			case 9:
				근거리대미지=11;
				근거리명중 = 9;
				break;
			case 10:
				근거리대미지=13;
				근거리명중 = 10;
				break;
			case 11:
				근거리대미지=15;
				근거리명중 = 11;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근거리대미지);

				
				if (근거리명중 > 0 || 근거리대미지 > 0 )
					ChattingController.toChatting(cha, String.format("%s: 근거리명중+%d, 근거리 대미지+%d", getItem().getName(), 근거리명중, 근거리대미지), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근거리대미지);

			}
		}
		if (getItem().getName().equalsIgnoreCase("악마왕의 단검")) {
			int 근거리대미지 = 0;
			int  근거리명중  = 0;
			
			switch (getEnLevel()) {
			case 1:
				근거리대미지=1;
				근거리명중 = 1;
				break;
			case 2:
				근거리대미지=2;
				근거리명중 = 2;
				break;
			case 3:
				근거리대미지=3;
				근거리명중 = 3;
				break;
			case 4:
				근거리대미지=4;
				근거리명중 = 4;
				break;
			case 5:
				근거리대미지=5;
				근거리명중 = 5;
				break;
			case 6:
				근거리대미지=6;
				근거리명중 = 6;
				break;
			case 7:
				근거리대미지=7;
				근거리명중 = 7;
				break;
			case 8:
				근거리대미지=9;
				근거리명중 = 8;
				break;
			case 9:
				근거리대미지=11;
				근거리명중 = 9;
				break;
			case 10:
				근거리대미지=13;
				근거리명중 = 10;
				break;
			case 11:
				근거리대미지=15;
				근거리명중 = 11;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근거리대미지);

				
				if (근거리명중 > 0 || 근거리대미지 > 0 )
					ChattingController.toChatting(cha, String.format("%s: 근거리명중+%d, 근거리 대미지+%d", getItem().getName(), 근거리명중, 근거리대미지), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근거리대미지);

			}
		}
		if (getItem().getName().equalsIgnoreCase("악마왕의 지팡이")) {
			int 근거리대미지 = 0;
			int  근거리명중  = 0;
			
			switch (getEnLevel()) {
			case 1:
				근거리대미지=1;
				근거리명중 = 1;
				break;
			case 2:
				근거리대미지=2;
				근거리명중 = 2;
				break;
			case 3:
				근거리대미지=3;
				근거리명중 = 3;
				break;
			case 4:
				근거리대미지=4;
				근거리명중 = 4;
				break;
			case 5:
				근거리대미지=5;
				근거리명중 = 5;
				break;
			case 6:
				근거리대미지=6;
				근거리명중 = 6;
				break;
			case 7:
				근거리대미지=7;
				근거리명중 = 7;
				break;
			case 8:
				근거리대미지=9;
				근거리명중 = 8;
				break;
			case 9:
				근거리대미지=11;
				근거리명중 = 9;
				break;
			case 10:
				근거리대미지=13;
				근거리명중 = 10;
				break;
			case 11:
				근거리대미지=15;
				근거리명중 = 11;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근거리대미지);

				
				if (근거리명중 > 0 || 근거리대미지 > 0 )
					ChattingController.toChatting(cha, String.format("%s: 근거리명중+%d, 근거리 대미지+%d", getItem().getName(), 근거리명중, 근거리대미지), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근거리대미지);

			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 붉은빛 귀걸이") && getBless() == 1) {
			int ac = 0;
			int 댐감 = 0;
			int hp = 0;
			int 확률 = 0;

			switch (getEnLevel()) {
			case 0:
				ac = 1;
				hp = 20;
				break;
			case 1:
				ac = 2;
				hp = 40;
				break;
			case 2:
				ac = 3;
				hp = 50;
				break;
			case 3:
				ac = 4;
				hp = 60;
				break;
			case 4:
				ac = 5;
				hp = 70;
				댐감 = 1;
				break;
			case 5:
				ac = 6;
				hp = 80;
				댐감 = 2;
				break;
			case 6:
				ac = 7;
				hp = 90;
				댐감 = 3;
				확률 = 3;
				break;
			case 7:
				ac = 8;
				hp = 100;
				댐감 = 4;
				확률 = 4;
				break;
			case 8:
				ac = 9;
				hp = 110;
				댐감 = 5;
				확률 = 5;
				break;
			}

			if (equipped) {
				cha.setDynamicAc(cha.getDynamicAc() + ac);
				cha.setDynamicHp(cha.getDynamicHp() + hp);
				cha.setDynamicReduction(cha.getDynamicReduction() + 댐감);
				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, hp+%d,  댐감+%d ,대미지 감소확률 +%d", getItem().getName(), ac, hp, 댐감, 확률), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAc(cha.getDynamicAc() - ac);
				cha.setDynamicHp(cha.getDynamicHp() - hp);
				cha.setDynamicReduction(cha.getDynamicReduction() - 댐감);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 붉은빛 귀걸이") && getBless() == 0) {
			int ac = 0;
			int 댐감 = 0;
			int hp = 0;
			int 확률 = 0;
			int 근명 = 0;

			switch (getEnLevel()) {
			case 0:
				ac = 2;
				hp = 20;
				break;
			case 1:
				ac = 3;
				hp = 40;
				break;
			case 2:
				ac = 4;
				hp = 50;
				break;
			case 3:
				ac = 5;
				hp = 60;
				break;
			case 4:
				ac = 6;
				hp = 100;
				댐감 = 2;
				break;
			case 5:
				ac = 7;
				hp = 110;
				댐감 = 3;
				확률 = 3;
				break;
			case 6:
				ac = 8;
				hp = 120;
				댐감 = 4;
				확률 = 4;
				break;
			case 7:
				ac = 9;
				hp = 130;
				댐감 = 5;
				확률 = 5;
				break;
			case 8:
				ac = 10;
				hp = 160;
				댐감 = 6;
				확률 = 6;

				break;
			}

			if (equipped) {
				cha.setDynamicAc(cha.getDynamicAc() + ac);
				cha.setDynamicHp(cha.getDynamicHp() + hp);
				cha.setDynamicReduction(cha.getDynamicReduction() + 댐감);
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근명);
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 근명);
				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, hp+%d, 댐감+%d ,원/근거리 명중 +%d,대미지 감소확률 +%d", getItem().getName(), ac, hp, 댐감, 근명, 확률), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAc(cha.getDynamicAc() - ac);
				cha.setDynamicHp(cha.getDynamicHp() - hp);
				cha.setDynamicReduction(cha.getDynamicReduction() - 댐감);
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근명);
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 근명);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 보랏빛 귀걸이") && getBless() == 1) {
			int mp = 0;
			int mr = 0;
			int sp = 0;
			int ac = 0;
			int 마법적중 = 0;

			switch (getEnLevel()) {
			case 0:
				mp = 5;
				mr = 2;
				break;
			case 1:
				mp = 15;
				mr = 5;
				break;
			case 2:
				mp = 20;
				mr = 6;
				break;
			case 3:
				mp = 35;
				mr = 7;
				sp = 1;
				break;
			case 4:
				mp = 40;
				mr = 8;
				sp = 1;
				break;
			case 5:
				mp = 55;
				mr = 9;
				sp = 2;
				break;
			case 6:
				mp = 60;
				mr = 10;
				sp = 2;
				ac = 1;
				break;
			case 7:
				mp = 75;
				mr = 12;
				sp = 3;
				ac = 2;
				마법적중 = 1;
				break;
			case 8:
				mp = 100;
				mr = 17;
				sp = 5;
				ac = 3;
				마법적중 = 5;

				break;
			}

			if (equipped) {
				cha.setDynamicAc(cha.getDynamicAc() + ac);
				cha.setDynamicMp(cha.getDynamicMp() + mp);
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + 마법적중);
				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, mp+%d, sp+%d ,mr +%d,마법적중 +%d", getItem().getName(), ac, mp, sp, mr, 마법적중), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAc(cha.getDynamicAc() - ac);
				cha.setDynamicMp(cha.getDynamicMp() - mp);
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - 마법적중);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 보랏빛 귀걸이") && getBless() == 0) {
			int mp = 0;
			int mr = 0;
			int sp = 0;
			int ac = 0;
			int 마법적중 = 0;

			switch (getEnLevel()) {
			case 0:
				mp = 10;
				mr = 3;
				break;
			case 1:
				mp = 20;
				mr = 6;
				break;
			case 2:
				mp = 40;
				mr = 7;
				break;
			case 3:
				mp = 40;
				mr = 8;
				sp = 1;
				break;
			case 4:
				mp = 55;
				mr = 9;
				sp = 2;
				break;
			case 5:
				mp = 60;
				mr = 10;
				sp = 2;
				break;
			case 6:
				mp = 75;
				mr = 12;
				sp = 3;
				마법적중 = 1;
				ac = 2;
				break;
			case 7:
				mp = 100;
				mr = 15;
				sp = 4;
				마법적중 = 5;
				ac = 3;
				break;
			case 8:
				mp = 130;
				mr = 20;
				sp = 5;
				마법적중 = 7;
				ac = 4;

				break;
			}

			if (equipped) {
				cha.setDynamicAc(cha.getDynamicAc() + ac);
				cha.setDynamicMp(cha.getDynamicMp() + mp);
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + 마법적중);
				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, mp+%d, sp+%d ,mr +%d,마법적중 +%d", getItem().getName(), ac, mp, sp, mr, 마법적중), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAc(cha.getDynamicAc() - ac);
				cha.setDynamicMp(cha.getDynamicMp() - mp);
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - 마법적중);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 검은빛 귀걸이") && getBless() == 1) {
			int 근댐 = 0;
			int 원댐 = 0;
			int ac = 0;
			int 추댐 = 0;

			switch (getEnLevel()) {
			case 0:
				ac = 1;
				break;
			case 1:
				ac = 2;
				break;
			case 2:
				ac = 3;
				break;
			case 3:
				ac = 4;
				근댐 = 1;
				원댐 = 1;
				break;
			case 4:
				ac = 5;
				근댐 = 1;
				원댐 = 1;
				break;
			case 5:
				ac = 6;
				근댐 = 3;
				원댐 = 3;
				break;
			case 6:
				ac = 7;
				근댐 = 5;
				원댐 = 5;
				break;
			case 7:
				ac = 8;
				근댐 = 7;
				원댐 = 7;
				break;
			case 8:
				ac = 9;
				근댐 = 8;
				원댐 = 8;

				break;
			}

			if (equipped) {
				cha.setDynamicAc(cha.getDynamicAc() + ac);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근댐);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 원댐);
				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, 근/원거리 대미지 +%d", getItem().getName(), ac, 근댐), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAc(cha.getDynamicAc() - ac);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근댐);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 원댐);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 검은빛 귀걸이") && getBless() == 0) {
			int 근댐 = 0;
			int 원댐 = 0;
			int ac = 0;

			switch (getEnLevel()) {
			case 0:
				ac = 2;
				break;
			case 1:
				ac = 3;
				break;
			case 2:
				ac = 4;
				break;
			case 3:
				ac = 5;
				break;
			case 4:
				ac = 6;
				근댐 = 2;
				원댐 = 2;
				break;
			case 5:
				ac = 7;
				근댐 = 5;
				원댐 = 5;
				break;
			case 6:
				ac = 8;
				근댐 = 7;
				원댐 = 7;
				break;
			case 7:
				ac = 9;
				근댐 = 9;
				원댐 = 9;
				break;
			case 8:
				ac = 10;
				근댐 = 11;
				원댐 = 11;

				break;
			}

			if (equipped) {
				cha.setDynamicAc(cha.getDynamicAc() + ac);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근댐);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 원댐);
				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, 근/원거리 대미지 +%d", getItem().getName(), ac, 근댐), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAc(cha.getDynamicAc() - ac);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근댐);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 원댐);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 푸른빛 귀걸이") && getBless() == 1) {
			int 물회 = 0;
			int ac = 0;

			switch (getEnLevel()) {
			case 0:
				물회 = 2;
				break;
			case 1:
				물회 = 4;
				break;
			case 2:
				물회 = 6;
				break;
			case 3:
				물회 = 8;
				break;
			case 4:
				물회 = 10;
				break;
			case 5:
				물회 = 12;
				ac = 1;
				break;
			case 6:
				물회 = 14;
				ac = 2;
				break;
			case 7:
				물회 = 16;
				ac = 3;
				break;
			case 8:
				물회 = 18;
				ac = 4;

				break;
			}

			if (equipped) {
				cha.setDynamicHpPotion(cha.getDynamicHpPotion() + ac);
				cha.setDynamicAc(cha.getDynamicAc() + ac);

				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, 물약회복량 +%d", getItem().getName(), ac, 물회), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicHpPotion(cha.getDynamicHpPotion() - ac);
				cha.setDynamicAc(cha.getDynamicAc() - ac);
			}
		}
		if (getItem().getName().equalsIgnoreCase("룸티스의 푸른빛 귀걸이") && getBless() == 0) {
			int 물회 = 0;
			int ac = 0;

			switch (getEnLevel()) {
			case 0:
				물회 = 4;
				break;
			case 1:
				물회 = 6;
				break;
			case 2:
				물회 = 8;
				break;
			case 3:
				물회 = 10;
				break;
			case 4:
				물회 = 12;
				break;
			case 5:
				물회 = 14;
				ac = 1;
				break;
			case 6:
				물회 = 16;
				ac = 2;
				break;
			case 7:
				물회 = 18;
				ac = 3;
				break;
			case 8:
				물회 = 20;
				ac = 4;

				break;
			}

			if (equipped) {
				cha.setDynamicHpPotion(cha.getDynamicHpPotion() + ac);
				cha.setDynamicAc(cha.getDynamicAc() + ac);

				if (ac > 0)
					ChattingController.toChatting(cha, String.format("%s: ac+%d, 물약회복량 +%d", getItem().getName(), ac, 물회), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicHpPotion(cha.getDynamicHpPotion() - ac);
				cha.setDynamicAc(cha.getDynamicAc() - ac);
			}
		}
		if (getItem().getName().equalsIgnoreCase("악마왕의 활")) {
			int 원거리대미지 = 0;
			int  원거리명중  = 0;
			
			switch (getEnLevel()) {
			case 1:
				원거리대미지=1;
				원거리명중 = 1;
				break;
			case 2:
				원거리대미지=2;
				원거리명중 = 2;
				break;
			case 3:
				원거리대미지=3;
				원거리명중 = 3;
				break;
			case 4:
				원거리대미지=4;
				원거리명중 = 4;
				break;
			case 5:
				원거리대미지=5;
				원거리명중 = 5;
				break;
			case 6:
				원거리대미지=6;
				원거리명중 = 6;
				break;
			case 7:
				원거리대미지=7;
				원거리명중 = 7;
				break;
			case 8:
				원거리대미지=9;
				원거리명중 = 8;
				break;
			case 9:
				원거리대미지=11;
				원거리명중 = 9;
				break;
			case 10:
				원거리대미지=13;
				원거리명중 = 10;
				break;
			case 11:
				원거리대미지=15;
				원거리명중 = 11;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 원거리명중);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 원거리대미지);

				
				if (원거리명중 > 0 || 원거리대미지 > 0 )
					ChattingController.toChatting(cha, String.format("%s: 원거리 명중+%d, 원거리 대미지+%d", getItem().getName(), 원거리명중, 원거리대미지), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 원거리명중);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 원거리대미지);

			}
		}
		if(getEnFire()>0){
			int addDmg = 0;
			
			switch (getEnFire()) {
			case 1:
				addDmg = 1;
				break;
			case 2:
				addDmg = 2;
				break;
			case 3:
				addDmg = 3;
				break;
			case 4:
				addDmg = 4;
				break;
			case 5:
				addDmg = 5;
				break;
			}
			
			if (equipped) {

				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + addDmg);

				if (addDmg > 0 )
					ChattingController.toChatting(cha, String.format("%s: [화령 속성 보너스] 근거리 대미지+%d", getItem().getName(), addDmg), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - addDmg);

			}
		}
		
		if(getEnWater()>0){
			int MagicCritical = 0;
			
			switch (getEnWater()) {
			case 1:
				MagicCritical = 1;
				break;
			case 2:
				MagicCritical = 2;
				break;
			case 3:
				MagicCritical = 3;
				break;
			case 4:
				MagicCritical = 4;
				break;
			case 5:
				MagicCritical = 5;
				break;
			}
			
			if (equipped) {

				cha.setDynamicSp(cha.getDynamicSp() + MagicCritical);

				if (MagicCritical > 0 )
					ChattingController.toChatting(cha, String.format("%s: [수령 속성 보너스] sp+%d", getItem().getName(), MagicCritical), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicSp(cha.getDynamicSp() - MagicCritical);
			}
		}
		if(getEnWind()>0){
			int addDmg = 0;
			
			switch (getEnWind()) {
			case 1:
				addDmg = 1;
				break;
			case 2:
				addDmg = 2;
				break;
			case 3:
				addDmg = 3;
				break;
			case 4:
				addDmg = 4;
				break;
			case 5:
				addDmg = 5;
				break;
			}
			
			if (equipped) {

				cha.setDynamicAddDmg(cha.getDynamicAddDmgBow() + addDmg);

				if (addDmg > 0 )
					ChattingController.toChatting(cha, String.format("%s:[풍령 속성 보너스]  원거리 대미지+%d", getItem().getName(), addDmg), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddDmg(cha.getDynamicAddDmgBow() - addDmg);

			}
		}
		if(getEnEarth()>0){
			int ignoreReduction = 0;
			
			switch (getEnEarth()) {
			case 1:
				ignoreReduction = 1;
				break;
			case 2:
				ignoreReduction = 2;
				break;
			case 3:
				ignoreReduction = 3;
				break;
			case 4:
				ignoreReduction = 4;
				break;
			case 5:
				ignoreReduction = 5;
				break;
			}
			
			if (equipped) {

			
				cha.setDynamicReduction(cha.getDynamicReduction() + ignoreReduction);
				if (ignoreReduction > 0 )
					ChattingController.toChatting(cha, String.format("%s: [지령 속성 보너스] 리덕션+%d", getItem().getName(), ignoreReduction), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicReduction(cha.getDynamicReduction()- ignoreReduction);

			}
		
		}
		
		if (getItem().getName().equalsIgnoreCase("마법 망토")) {
			int mr = getEnLevel() *2;
			
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + mr);
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - mr);
			}
		}
		if (getItem().getName().equalsIgnoreCase("마법 방어 투구")) {
			int mr = getEnLevel();
			
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + mr);
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - mr);
			}
		}
		if (getItem().getName().equalsIgnoreCase("화령의 가더")) {
			int 근거리명중 = 0;
			int 근거리대미지 = 0;
			double 스턴내성 = 0;
			
			switch (getEnLevel()) {
			case 1:
				근거리명중 = 1;
				break;
			case 2:
				근거리명중 = 2;
				스턴내성 = 0.01;
				break;
			case 3:
				근거리명중 = 3;
				근거리대미지 = 2;
				스턴내성 = 0.02;
				break;
			case 4:
				근거리명중 = 4;
				근거리대미지 = 2;
				스턴내성 = 0.03;
				break;
			case 5:
				근거리명중 = 5;
				근거리대미지 = 3;
				스턴내성 = 0.04;
				break;
			case 6:
				근거리명중 = 6;
				근거리대미지 = 4;
				스턴내성 = 0.05;
				break;
			case 7:
				근거리명중 = 6;
				근거리대미지 = 5;
				스턴내성 = 0.06;
				break;
			case 8:
				근거리명중 = 6;
				근거리대미지 = 6;
				스턴내성 = 0.06;
				break;
			case 9:
				근거리명중 = 7;
				근거리대미지 = 7;
				스턴내성 = 0.07;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근거리대미지);
				cha.setDynamicStunResist(cha.getDynamicStunResist() + 스턴내성);
				
				if (근거리명중 > 0 || 근거리대미지 > 0 || 스턴내성 > 0)
					ChattingController.toChatting(cha, String.format("%s: 근거리명중+%d, 근거리 대미지+%d, 스턴 내성+%.0f%%", getItem().getName(), 근거리명중, 근거리대미지, (스턴내성 * 100)), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근거리대미지);
				cha.setDynamicStunResist(cha.getDynamicStunResist() - 스턴내성);
			}
		}
	

	
		if (getItem().getName().equalsIgnoreCase("지령의 가더")) {
			int mr = 0;
			int 대미지감소 = 0;
			
			switch (getEnLevel()) {
			case 1:
				대미지감소 = 1;
				break;
			case 2:
				대미지감소 = 2;
				break;
			case 3:
				mr = 3;
				대미지감소 = 3;
				break;
			case 4:
				mr = 4;
				대미지감소 = 4;
				break;
			case 5:
				mr = 5;
				대미지감소 = 5;
				break;
			case 6:
				mr = 6;
				대미지감소 = 7;
				break;
			case 7:
				mr = 8;
				대미지감소 = 9;
				break;
			case 8:
				mr = 10;
				대미지감소 = 10;
				break;
			case 9:
				mr = 13;
				대미지감소 = 13;
				break;	
			}
			
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				
				if (mr > 0 || 대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s: MR+%d, 대미지 감소+%d", getItem().getName(), mr, 대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("풍령의 가더")) {
			int 원거리명중 = 0;
			int 원거리대미지 = 0;
			double 스턴내성 = 0;
			
			switch (getEnLevel()) {
			case 1:
				원거리명중 = 1;
				break;
			case 2:
				원거리명중 = 2;
				스턴내성 = 0.01;
				break;
			case 3:
				원거리명중 = 3;
				원거리대미지 = 2;
				스턴내성 = 0.02;
				break;
			case 4:
				원거리명중 = 4;
				원거리대미지 = 2;
				스턴내성 = 0.03;
				break;
			case 5:
				원거리명중 = 5;
				원거리대미지 = 3;
				스턴내성 = 0.04;
				break;
			case 6:
				원거리명중 = 6;
				원거리대미지 = 4;
				스턴내성 = 0.05;
				break;
			case 7:
				원거리명중 = 6;
				원거리대미지 = 5;
				스턴내성 = 0.05;
				break;
			case 8:
				원거리명중 = 6;
				원거리대미지 = 6;
				스턴내성 = 0.06;
				break;	
			case 9:
				원거리명중 = 7;
				원거리대미지 = 7;
				스턴내성 = 0.07;
				break;	
			}
			
			if (equipped) {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 원거리명중);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 원거리대미지);
				cha.setDynamicStunResist(cha.getDynamicStunResist() + 스턴내성);
				
				if (원거리명중 > 0 || 원거리대미지 > 0 || 스턴내성 > 0)
					ChattingController.toChatting(cha, String.format("%s: 원거리 명중+%d, 원거리 대미지+%d, 스턴 내성+%.0f%%", getItem().getName(), 원거리명중, 원거리대미지, (스턴내성 * 100)), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 원거리명중);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 원거리대미지);
				cha.setDynamicStunResist(cha.getDynamicStunResist() - 스턴내성);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("수령의 가더")) {
			int 마법적중 = 0;
			int sp = 0;
			double 스턴내성 = 0;
			
			switch (getEnLevel()) {
			case 1:
				마법적중 = 1;
				break;
			case 2:
				마법적중 = 2;
				스턴내성 = 0.01;
				break;
			case 3:
				마법적중 = 3;
				sp = 1;
				스턴내성 = 0.02;
				break;
			case 4:
				마법적중 = 4;
				sp = 2;
				스턴내성 = 0.03;
				break;
			case 5:
				마법적중 = 5;
				sp = 3;
				스턴내성 = 0.04;
				break;
			case 6:
				마법적중 = 6;
				sp = 4;
				스턴내성 = 0.05;
				break;
			case 7:
				마법적중 = 7;
				sp = 5;
				스턴내성 = 0.06;
				break;
			case 8:
				마법적중 = 7;
				sp = 6;
				스턴내성 = 0.07;
				break;
			case 9:
				마법적중 = 8;
				sp = 7;
				스턴내성 = 0.08;
				break;
			}
			
			if (equipped) {
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + 마법적중);
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicStunResist(cha.getDynamicStunResist() + 스턴내성);
				
				if (마법적중 > 0 || sp > 0 || 스턴내성 > 0)
					ChattingController.toChatting(cha, String.format("%s: 마법 적중+%d%%, SP+%d, 스턴 내성+%.0f%%", getItem().getName(), 마법적중, sp, (스턴내성 * 100)), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - 마법적중);
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicStunResist(cha.getDynamicStunResist() - 스턴내성);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("쿠거의 가더")) {
			int 근거리명중 = 0;
			double 스턴내성 = 0;
			
			switch (getEnLevel()) {
			case 5:
				근거리명중 = 1;
				스턴내성 = 0.01;
				break;
			case 6:
				근거리명중 = 2;
				스턴내성 = 0.02;
				break;
			case 7:
				근거리명중 = 3;
				스턴내성 = 0.03;
				break;
			case 8:
				근거리명중 = 4;
				스턴내성 = 0.04;
				break;
			case 9:
				근거리명중 = 5;
				스턴내성 = 0.05;
				break;
			case 10:
				근거리명중 = 5;
				스턴내성 = 0.05;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicStunResist(cha.getDynamicStunResist() + 스턴내성);

				if (근거리명중 > 0 || 스턴내성 > 0)
					ChattingController.toChatting(cha, String.format("%s: 근거리 명중+%d, 스턴 내성+%.0f%%", getItem().getName(), 근거리명중, (스턴내성 * 100)), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicStunResist(cha.getDynamicStunResist() - 스턴내성);
			}
		}
		//야도란 아이템추가
		if (getItem().getName().equalsIgnoreCase("머미로드의 왕관")) {
			int addDmg = 0;

			
			switch (getEnLevel()) {

			case 6:
				addDmg = 2;

				break;
			case 7:
				addDmg = 3;
				break;
			case 8:
				addDmg = 4;
				break;
			case 9:
				addDmg = 5;
				break;
			case 10:
				addDmg = 6;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + addDmg);


				if (addDmg > 0)
					ChattingController.toChatting(cha, String.format("%s: 원거리 대미지+%d", getItem().getName(), addDmg), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - addDmg);
			}
		}
		if (getItem().getName().equalsIgnoreCase("가디언의 투구")) {
			int sp = 0;

			
			switch (getEnLevel()) {

			case 5:
				sp = 2;

				break;
			case 6:
				sp = 3;
				break;
			case 7:
				sp = 4;
				break;
			case 8:
				sp = 5;
				break;
			case 9:
				sp = 6;
				break;
			case 10:
				sp = 7;
				break;	
			}
			
			if (equipped) {
				cha.setDynamicSp(cha.getDynamicSp() + sp);


				if (sp > 0)
					ChattingController.toChatting(cha, String.format("%s: sp+%d", getItem().getName(), sp), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicSp(cha.getDynamicSp() - sp);
			}
		}
		
		
		if (getItem().getName().equalsIgnoreCase("기사 대장의 면갑")) {
			int 대미지감소 = 0;

			
			switch (getEnLevel()) {

			case 6:
				대미지감소 = 2;

				break;
			case 7:
				대미지감소 = 3;
				break;
			case 8:
				대미지감소 = 4;
				break;
			case 9:
				대미지감소 = 5;
				break;
			case 10:
				대미지감소 = 6;
				break;

			}
			
			if (equipped) {
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);


				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s: 대미지감소 +%d", getItem().getName(), 대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		
		if (getItem().getName().equalsIgnoreCase("빛나는 민첩의 티셔츠")) {
			int 원거리명중 = 0;
			int 원거리대미지 = 0;
			int mr = 0;
			switch (getEnLevel()) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				원거리대미지 = 1;
				원거리명중 = 1;
				mr=0;
				break;
			case 5:
				원거리대미지 = 2;
				원거리명중 = 2;
				mr=5;
				break;
			case 6:
				원거리명중 = 3;
				원거리대미지 = 3;
				mr=6;
				break;
			case 7:
				원거리명중 = 4;
				원거리대미지 = 4;
				mr=7;
				break;
			case 8:
				원거리명중 = 5;
				원거리대미지 = 5;
				mr=8;
				break;
			case 9:
				원거리명중 = 6;
				원거리대미지 = 6;
				mr=9;
				break;
			case 10:
				원거리명중 = 7;
				원거리대미지 = 7;
				mr=10;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 원거리명중);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() + 원거리대미지);
				cha.setDynamicMr(cha.getDynamicMr() + mr);

				if (원거리명중 > 0 ||원거리대미지 > 0 || mr > 0 )
					ChattingController.toChatting(cha, String.format("%s: 원거리명중+%d , 원거리 대미지+%d , mr+%d", getItem().getName(), 원거리명중,원거리대미지,mr), Lineage.CHATTING_MODE_MESSAGE);
				
			} else {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 원거리명중);
				cha.setDynamicAddDmgBow(cha.getDynamicAddDmgBow() - 원거리대미지);
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				
			}
		}
		if (getItem().getName().equalsIgnoreCase("빛나는 완력의 티셔츠")) {
			int 근거리명중 = 0;
			int 근거리대미지 = 0;
			int mr = 0;
			switch (getEnLevel()) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				근거리명중 = 1;
				근거리대미지 = 1;
				mr=0;
				break;
			case 5:
				근거리명중 = 2;
				근거리대미지 = 2;
				mr=5;
				break;
			case 6:
				근거리명중 = 3;
				근거리대미지 = 3;
				mr=6;
				break;
			case 7:
				근거리명중 = 4;
				근거리대미지 = 4;
				mr=7;
				break;
			case 8:
				근거리명중 = 5;
				근거리대미지 = 5;
				mr=8;
				break;
			case 9:
				근거리명중 = 6;
				근거리대미지 = 6;
				mr=9;
				break;
			case 10:
				근거리명중 = 7;
				근거리대미지 = 7;
				mr=10;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() + 근거리대미지);
				cha.setDynamicMr(cha.getDynamicMr() + mr);

				if (근거리명중 > 0 ||근거리대미지 > 0 || mr > 0 )
					ChattingController.toChatting(cha, String.format("%s: 근거리명중+%d , 근거리대미지+%d , mr+%d", getItem().getName(), 근거리명중,근거리대미지,mr), Lineage.CHATTING_MODE_MESSAGE);
				
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicAddDmg(cha.getDynamicAddDmg() - 근거리대미지);
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				
			}
		}
		if (getItem().getName().equalsIgnoreCase("빛나는 지식의 티셔츠")) {
			int sp = 0;
			int 마법적중 = 0;
			int mr = 0;
			switch (getEnLevel()) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				sp = 1;
				마법적중 = 1;
				mr=0;
				break;
			case 5:
				sp = 2;
				마법적중 = 2;
				mr=5;
				break;
			case 6:
				sp = 3;
				마법적중 = 3;
				mr=6;
				break;
			case 7:
				sp = 4;
				마법적중 = 4;
				mr=7;
				break;
	
			case 8:
				sp = 5;
				마법적중 = 5;
				mr=8;
				break;
			case 9:
				sp = 6;
				마법적중 = 6;
				mr=9;
				break;
			case 10:
				sp = 7;
				마법적중 = 7;
				mr=10;
				break;
			}
			
			if (equipped) {
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() + 마법적중);
				cha.setDynamicMr(cha.getDynamicMr() + mr);

				if (sp > 0 ||마법적중 > 0 || mr > 0 )
					ChattingController.toChatting(cha, String.format("%s: sp+%d , 마법적중+%d , mr+%d", getItem().getName(), sp,마법적중,mr), Lineage.CHATTING_MODE_MESSAGE);
				
			} else {
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicMagicHit(cha.getDynamicMagicHit() - 마법적중);
				cha.setDynamicMr(cha.getDynamicMr() - mr);
				
			}
		}
		if (getItem().getName().equalsIgnoreCase("격분의 장갑")) {
			int 대미지감소 = 0;
			int 근거리명중 = 0;
			
			switch (getEnLevel()) {
			case 1:
				근거리명중 = 1;
				대미지감소 = 1;
				break;
			case 2:
				근거리명중 = 2;
				대미지감소 = 2;
				break;
			case 3:
				근거리명중 = 3;
				대미지감소 = 3;
				break;
			case 4:
				근거리명중 = 4;
				대미지감소 = 4;
				break;
			case 5:
				근거리명중 = 5;
				대미지감소 = 5;
				break;
			case 6:
				근거리명중 = 6;
				대미지감소 = 6;
				break;
			case 7:
				근거리명중 = 7;
				대미지감소 = 7;
				break;
			case 8:
				근거리명중 = 8;
				대미지감소 = 8;
				break;
			case 9:
				근거리명중 = 9;
				대미지감소 = 9;
				break;
			case 10:
				근거리명중 = 10;
				대미지감소 = 10;
				break;
			}
			
			if (equipped) {
				cha.setDynamicAddHit(cha.getDynamicAddHit() + 근거리명중);
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s:  근거리명중+%d, 대미지감소+%d", getItem().getName(), 근거리명중,대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHit(cha.getDynamicAddHit() - 근거리명중);
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		if (getItem().getName().equalsIgnoreCase("아이리스의 장갑")) {
			int 대미지감소 = 0;
			int 원거리명중 = 0;
			
			switch (getEnLevel()) {
			case 1:
				원거리명중 = 1;
				대미지감소 = 1;
				break;
			case 2:
				원거리명중 = 2;
				대미지감소 = 2;
				break;
			case 3:
				원거리명중 = 3;
				대미지감소 = 3;
				break;
			case 4:
				원거리명중 = 4;
				대미지감소 = 4;
				break;
			case 5:
				원거리명중 = 5;
				대미지감소 = 5;
				break;
			case 6:
				원거리명중 = 6;
				대미지감소 = 6;
				break;
			case 7:
				원거리명중 = 7;
				대미지감소 = 7;
				break;
			case 8:
				원거리명중 = 8;
				대미지감소 = 8;
				break;
			case 9:
				원거리명중 = 9;
				대미지감소 = 9;
				break;
			case 10:
				원거리명중 = 10;
				대미지감소 = 10;
				break;
			}
			if (equipped) {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() + 원거리명중);
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s:  원거리명중+%d, 대미지감소+%d", getItem().getName(), 원거리명중,대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicAddHitBow(cha.getDynamicAddHitBow() - 원거리명중);
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		if (getItem().getName().equalsIgnoreCase("대마법사의 장갑")) {
			int 대미지감소 = 0;
			int sp = 0;
			
			switch (getEnLevel()) {
			case 1:
				sp = 1;
				대미지감소 = 1;
				break;
			case 2:
				sp = 1;
				대미지감소 = 2;
				break;
			case 3:
				sp = 2;
				대미지감소 = 3;
				break;
			case 4:
				sp = 2;
				대미지감소 = 4;
				break;
			case 5:
				sp = 3;
				대미지감소 = 5;
				break;
			case 6:
				sp = 4;
				대미지감소 = 6;
				break;
			case 7:
				sp = 5;
				대미지감소 = 7;
				break;
			case 8:
				sp = 6;
				대미지감소 = 8;
				break;
			case 9:
				sp = 7;
				대미지감소 = 9;
				break;
			case 10:
				sp = 8;
				대미지감소 = 10;
				break;
			}
			if (equipped) {
				cha.setDynamicSp(cha.getDynamicSp() + sp);
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s:  sp+%d, 대미지감소+%d", getItem().getName(), sp,대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicSp(cha.getDynamicSp() - sp);
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		if (getItem().getName().equalsIgnoreCase("가디언의 망토")) {
			
			int mr = 0;
			switch (getEnLevel()) {
				case 0:
					mr = 0;
				break;	
				case 1:
					mr = 5;
					break;
				case 2:
					mr = 10;
					break;
				case 3:
					mr = 15;
					break;
				case 4:
					mr = 20;
					break;
				case 5:
					mr = 25;
					break;
				case 6:
					mr = 30;
					break;
				case 7:
					mr = 35;
					break;
				case 8:
					mr = 40;
					break;
				case 9:
					mr = 45;
					break;
			}
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				if (mr > 0)
					ChattingController.toChatting(cha, String.format("%s: MR+%d", getItem().getName(), mr), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - mr);
			}
		}
		
	
		if (getItem().getName().equalsIgnoreCase("뱀파이어의 망토")) {
			
			int mr = 0;
			switch (getEnLevel()) {
				case 0:
					mr = 0;
				break;	
				case 1:
					mr = 5;
					break;
				case 2:
					mr = 10;
					break;
				case 3:
					mr = 15;
					break;
				case 4:
					mr = 20;
					break;
				case 5:
					mr = 25;
					break;
				case 6:
					mr = 30;
					break;
				case 7:
					mr = 35;
					break;
				case 8:
					mr = 40;
					break;
				case 9:
					mr = 45;
					break;
			}
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				if (mr > 0)
					ChattingController.toChatting(cha, String.format("%s: MR+%d", getItem().getName(), mr), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - mr);
			}
		}
		if (getItem().getName().equalsIgnoreCase("쿠거가죽 망토")) {
			
			int mr = 0;
			switch (getEnLevel()) {
				case 0:
					mr = 0;
				break;	
				case 1:
					mr = 5;
					break;
				case 2:
					mr = 10;
					break;
				case 3:
					mr = 15;
					break;
				case 4:
					mr = 20;
					break;
				case 5:
					mr = 25;
					break;
				case 6:
					mr = 30;
					break;
				case 7:
					mr = 35;
					break;
				case 8:
					mr = 40;
					break;
				case 9:
					mr = 45;
					break;
			}
			if (equipped) {
				cha.setDynamicMr(cha.getDynamicMr() + mr);
				if (mr > 0)
					ChattingController.toChatting(cha, String.format("%s: MR+%d", getItem().getName(), mr), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicMr(cha.getDynamicMr() - mr);
			}
		}
		if (getItem().getName().equalsIgnoreCase("가디언의 부츠")) {
			int 대미지감소 = 0;
			
			switch (getEnLevel()) {
			case 5:
				대미지감소 = 1;
				break;
			case 6:
				대미지감소 = 2;
				break;
			case 7:
				대미지감소 = 3;
				break;
			case 8:
				대미지감소 = 4;
				break;
			case 9:
				대미지감소 = 5;
				break;
			case 10:
				대미지감소 = 6;
				break;
			}
			
			if (equipped) {
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s: 대미지감소+%d", getItem().getName(), 대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		if (getItem().getName().equalsIgnoreCase("뱀파이어의 부츠")) {
			int 대미지감소 = 0;
			
			switch (getEnLevel()) {
			case 5:
				대미지감소 = 1;
				break;
			case 6:
				대미지감소 = 2;
				break;
			case 7:
				대미지감소 = 3;
				break;
			case 8:
				대미지감소 = 4;
				break;
			case 9:
				대미지감소 = 5;
				break;
			case 10:
				대미지감소 = 6;
				break;
			}
			
			if (equipped) {
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s: 대미지감소+%d", getItem().getName(), 대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
	
		if (getItem().getName().equalsIgnoreCase("아이리스의 부츠")) {
			int 대미지감소 = 0;
			
			switch (getEnLevel()) {
			case 5:
				대미지감소 = 1;
				break;
			case 6:
				대미지감소 = 2;
				break;
			case 7:
				대미지감소 = 3;
				break;
			case 8:
				대미지감소 = 4;
				break;
			case 9:
				대미지감소 = 5;
				break;
			case 10:
				대미지감소 = 6;
				break;
			}
			
			if (equipped) {
				cha.setDynamicReduction(cha.getDynamicReduction() + 대미지감소);
				if (대미지감소 > 0)
					ChattingController.toChatting(cha, String.format("%s: 대미지감소+%d", getItem().getName(), 대미지감소), Lineage.CHATTING_MODE_MESSAGE);
			} else {
				cha.setDynamicReduction(cha.getDynamicReduction() - 대미지감소);
			}
		}
		
		if (sendPacket && cha instanceof PcInstance) {
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
			cha.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), cha));
		}
	}

	/**
	 * 셋트아이템 착용여부 확인하여 옵션 적용및 해제처리하는 함수. : 인벤토리와 연동하여 적용된 세트가 있는지 확인. 있는데 현재 착용된 아이템에서 없을경우 옵션 해제. 없는데 전체 셋트착용중일경우 옵션 적용.
	 */
	public void toSetoption(Character cha, boolean sendPacket) {
		Inventory inv = cha.getInventory();
		if (inv != null && item.getSetId() > 0) {
			ItemSetoption is = ItemSetoptionDatabase.find(item.getSetId());
			if (is != null) {
				if (equipped) {
					// 적용된 셋트가 없다면.
					if (!inv.isSetoption(is)) {
						// 셋트아이템 갯수 이상일 경우에만 적용.
						int cnt = 1; // 해당 아이템이 착용될 것이기때문에 초기값을 1
						for (int i = 0; i <= Lineage.SLOT_NONE; ++i) {
							ItemInstance slot = inv.getSlot(i);
							if (slot != null && slot.getItem().getSetId() == is.getUid())
								cnt += 1;
						}
						if (is.getCount() <= cnt) {
							inv.appendSetoption(is);
							ItemSetoptionDatabase.setting(cha, is, equipped, sendPacket);
						}
					}
				} else {
					// 적용된 셋트가 있다면
					if (inv.isSetoption(is)) {
						// 셋트아이템 갯수 미만일경우에만 해제.
						int cnt = 0;
						for (int i = 0; i <= Lineage.SLOT_NONE; ++i) {
							ItemInstance slot = inv.getSlot(i);
							if (slot != null && slot.getItem().getSetId() == is.getUid())
								cnt += 1;
						}
						if (is.getCount() >= cnt) {
							inv.removeSetoption(is);
							ItemSetoptionDatabase.setting(cha, is, equipped, sendPacket);
						}
					}
				}
			}
		}
	}

	/**
	 * 아이템을 사용해도 되는 상태인지 확인해주는 함수.
	 * 아이템 사용시 버그 체크
	 * @param cha
	 * @return
	 */
	protected boolean isClick(Character cha) {
		if (this != null && getItem() != null && cha != null && cha instanceof PcInstance && cha.getInventory() != null && !cha.isWorldDelete()) {
			return true;
		}
		
		if (cha.isBuffDecayPotion())
			return false;

		return true;
	}
	
	/**
	 * 아이템 사용해도 되는 상황인지 확인
	 * 2020-12-01
	 * by connector12@nate.com
	 */
	public boolean isClickState(object o) {
		if (getItem() == null || o == null || o.getInventory() == null || o.isWorldDelete()) {
			return false;
		}
		
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			
			if (pc.isDead()) {
				ChattingController.toChatting(pc, "\\fY죽은 상태에서 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
			
			if (pc.isLock()) {
				ChattingController.toChatting(pc, "\\fY기절하거나 굳은 상태에서 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
			
			if (pc.isFishing()) {
				ChattingController.toChatting(pc, "\\fY낚시중에 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return false;
			}
			
			return true;
		}
		
		return false;
	}

	/**
	 * 파워볼 쿠폰 이름 강제 매핑 (ID 우선)
	 * - 정상 NameId: $1249/$1250/$1254/$1255
	 * - 구DB 오매핑 NameId: $1251/$1252 도 언더/오버로 보정
	 */
	private static String getPowerballCouponDisplayName(Item item) {
		if (item == null)
			return null;

		int nidNumber = item.getNameIdNumber();
		if (nidNumber == 1249)
			return "홀쿠폰";
		if (nidNumber == 1250)
			return "짝쿠폰";
		if (nidNumber == 1254 || nidNumber == 1251)
			return "언더쿠폰";
		if (nidNumber == 1255 || nidNumber == 1252)
			return "오버쿠폰";

		String nid = item.getNameId();
		if ("$1249".equals(nid))
			return "홀쿠폰";
		if ("$1250".equals(nid))
			return "짝쿠폰";
		if ("$1254".equals(nid) || "$1251".equals(nid))
			return "언더쿠폰";
		if ("$1255".equals(nid) || "$1252".equals(nid))
			return "오버쿠폰";

		String n = item.getName();
		if ("홀 쿠폰".equals(n) || "홀쿠폰".equals(n))
			return "홀쿠폰";
		if ("짝 쿠폰".equals(n) || "짝쿠폰".equals(n))
			return "짝쿠폰";
		if ("언더 쿠폰".equals(n) || "언더쿠폰".equals(n))
			return "언더쿠폰";
		if ("오버 쿠폰".equals(n) || "오버쿠폰".equals(n))
			return "오버쿠폰";

		return null;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp) {
		if (cha instanceof PcInstance) {
			PcInstance pc = (PcInstance) cha;
			String compact = getPowerballCouponDisplayName(getItem());
			if (compact != null) {
				ChattingController.toChatting(pc, "\\fY" + compact + "을 사용 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 330, toString()));
		}
	}

	@Override
	public String toString() {
		StringBuffer msg = new StringBuffer();
		if (definite && (this instanceof ItemWeaponInstance || this instanceof ItemArmorInstance)) {
			if (enLevel >= 0)
				msg.append("+");
			msg.append(enLevel);
			msg.append(" ");
		}
		String displayName = name;
		String forcedCouponName = getPowerballCouponDisplayName(getItem());
		if (forcedCouponName != null)
			displayName = forcedCouponName;
		msg.append(displayName);
		if (getCount() > 1) {
			msg.append(" (");
			msg.append(getCount());
			msg.append(")");
		}

		return msg.toString();
	}

	public String toStringDB() {
		StringBuffer msg = new StringBuffer();
		if (definite && (this instanceof ItemWeaponInstance || this instanceof ItemArmorInstance)) {
			if (enLevel >= 0)
				msg.append("+");
			msg.append(enLevel);
			msg.append(" ");
		}
		msg.append(item.getName());
		if (getCount() > 1) {
			msg.append(" (");
			msg.append(getCount());
			msg.append(")");
		}
		return msg.toString();
	}
	
	public String toStringSearch() {
		StringBuffer msg = new StringBuffer();
		
		if (definite) {
			if (getBless() < 0)
				msg.append("[봉인] ");
			if (getBless() == 0)
				msg.append("[축복받은] ");
			if (getBless() == 2)
				msg.append("[저주받은] ");
		}
		
		if (definite && (this instanceof ItemWeaponInstance || this instanceof ItemArmorInstance)) {
			if (getEnFire() > 0)
				msg.append("속성: " + getEnFire() + "단 ");
			if (enLevel >= 0)
				msg.append("+");
			
			msg.append(enLevel);
			msg.append(" ");
		}
		
		msg.append(item.getName());
		
		if (getCount() > 1) {
			msg.append(" (");
			msg.append(getCount());
			msg.append(")");
		}
		return msg.toString();
	}

	@Override
	public Skill getSkill() {
		return null;
	}

	@Override
	public void setTime(int time) {
	}

	@Override
	public int getTime() {
		return nowTime;
	}

	@Override
	public void setCharacter(Character cha) {
	}

	@Override
	public boolean isBuff(long time) {
		return --nowTime > 0;
	}

	@Override
	public void toBuffStart(object o) {
	}

	@Override
	public void toBuffUpdate(object o) {
	}

	@Override
	public void toBuff(object o) {
	}

	@Override
	public void toBuffStop(object o) {
	}

	@Override
	public void toBuffEnd(object o) {
	}

	@Override
	public boolean equal(BuffInterface bi) {
		return false;
	}

	@Override
	public void setTime(int time, boolean restart) {
	}

	@Override
	public void setEffect(BackgroundInstance effect) {
	}

	@Override
	public BackgroundInstance getEffect() {
		return null;
	}

	@Override
	public void setDamage(int damage) {
	}

	@Override
	public int getDamage() {
		return 0;
	}
	
	// 시간제 아이템을 표시하기위한 변수
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	// 시간제 아이템을 표시하기위한 변수
	public boolean isTimeCheck() {
		return isTimeCheck;
	}
	
	public void setTimeCheck(boolean isTimeCheck) {
		this.isTimeCheck = isTimeCheck;
	}
	
	/**
	 * 장신구 확인.
	 * 2020-09-04
	 * by connector12@nate.com
	 */
	public boolean isAcc() {
		if (this != null && this instanceof ItemArmorInstance && getItem() != null) {
			if (getItem().getType2().equalsIgnoreCase("necklace") || 
				getItem().getType2().equalsIgnoreCase("belt") || 
				getItem().getType2().equalsIgnoreCase("ring") || 
				getItem().getType2().equalsIgnoreCase("earring")) {
				return true;
			}
		}
		
		return false;
	}
	public byte[] getStatusBytes() {
		BinaryOutputStream os = new BinaryOutputStream();
		try {

		if (item.getType1().equalsIgnoreCase("weapon") || item.getType1().equalsIgnoreCase("petWeapon")) {
			os.writeC(1);
			os.writeC(item.getSmallDmg());
			os.writeC(item.getBigDmg());
			os.writeC(item.getMaterial());
			os.writeD(getWeight());
		} else if (item.getType1().equalsIgnoreCase("armor") || item.getType1().equalsIgnoreCase("petArmor")) {
			// System.out.println("100 : " + os.getBytes().length);
			os.writeC(19);
			// System.out.println("101 : " + os.getBytes().length);
			os.writeC(item.getAc());
			// System.out.println("102 : " + os.getBytes().length);
			os.writeC(item.getMaterial());
			// System.out.println("103 : " + os.getBytes().length);
			// os.writeC(-1);
			// System.out.println("104 : " + os.getBytes().length);
			os.writeD(getWeight());
			// System.out.println("105 : " + os.getBytes().length);
		} else {

		}

		// System.out.println("1 : " + os.getBytes().length);

		if (getEnLevel() != 0) {
			os.writeC(2);
			os.writeC(getEnLevel());
		}

		// System.out.println("2 : " + os.getBytes().length);

		if (getDurability() != 0) {
			os.writeC(3);
			os.writeC(getDurability());
		}

		if (getItem().isTohand())
			os.writeC(4);

		if (getItem().getAddHit() != 0 || getTollTipHit() != 0) {
			os.writeC(5);
			os.writeC(getItem().getAddHit() + getTollTipHit());
		}
		// System.out.println("3 : " + os.getBytes().length);

		if (getItem().getAddDmg() != 0 || getTollTipDmg() != 0) {
			os.writeC(6);
			os.writeC(getItem().getAddDmg() + getTollTipDmg());
		}

		// System.out.println("4 : " + os.getBytes().length);
		int type = item.getRoyal() == 0 ? 0 : 1;
		type += item.getKnight() == 0 ? 0 : 2;
		type += item.getElf() == 0 ? 0 : 4;
		type += item.getWizard() == 0 ? 0 : 8;
		type += item.getDarkElf() == 0 ? 0 : 16;
		type += item.getDragonKnight() == 0 ? 0 : 32;
		type += item.getBlackWizard() == 0 ? 0 : 64;

		// System.out.println("5 : " + os.getBytes().length);

		os.writeC(7);
		os.writeC(type);
		
			
		if (getItem().getStealMp() != 0) {
			os.writeC(16);
		}
		if (getItem().getStealHp() != 0) {
			os.writeC(34);
		}
		if (getItem().getAddStr() != 0) {
			os.writeC(8);
			os.writeC(getItem().getAddStr());
		}
		if (getItem().getAddDex() != 0) {
			os.writeC(9);
			os.writeC(getItem().getAddDex());
		}
		if (getItem().getAddCon() != 0) {
			os.writeC(10);
			os.writeC(getItem().getAddCon());
		}
		if (getItem().getAddWis() != 0) {
			os.writeC(11);
			os.writeC(getItem().getAddWis());
		}
		if (getItem().getAddInt() != 0) {
			os.writeC(12);
			os.writeC(getItem().getAddInt());
		}
		if (getItem().getAddCha() != 0) {
			os.writeC(13);
			os.writeC(getItem().getAddCha());
		}

		// System.out.println("6 : " + os.getBytes().length);

		if (getItem().getAddHp() != 0 || getTollTipHp() != 0) {
			os.writeC(14);
			os.writeH(getItem().getAddHp() + getTollTipHp());
		}

		// System.out.println("7 : " + os.getBytes().length);

		if (getItem().getAddMp() != 0 || getTollTipMp() != 0) {
			os.writeC(24);
			os.writeC(getItem().getAddMp() + getTollTipMp());
		}

		// System.out.println("8 : " + os.getBytes().length);

		if (getItem().getAddMr() != 0 || tollTipMr != 0) {
			os.writeC(15);
			os.writeH(getItem().getAddMr() + tollTipMr);
		}

		// System.out.println("9 : " + os.getBytes().length);
		if (getItem().getAddSp() != 0 || getTollTipSp() != 0) {
			os.writeC(17);
			os.writeC(getItem().getAddSp() + getTollTipSp());
		}

		// System.out.println("10 : " + os.getBytes().length);

		ItemSetoption setoption = Lineage.server_version >= 200 ? ItemSetoptionDatabase.find(item.getSetId()) : null;

		if (setoption != null && setoption.isHaste())
			os.writeC(18);

		if (setoption != null && setoption.isBrave())
			os.writeC(18);

		// System.out.println("11 : " + os.getBytes().length);

		if (getItem().getStunDefense() != 0 || getTollTipStunDefens() != 0) {
			os.writeC(28);
			os.writeC(((int) getItem().getStunDefense() * 100) + getTollTipStunDefens());
		}

		// System.out.println("12 : " + os.getBytes().length);

		// 데미지 감소
		if (getItem().getAddReduction() != 0 || getTollTipReduction() != 0) {
			os.writeC(20);
			os.writeC(getItem().getAddReduction() + getTollTipReduction());
		}

		// 물약 회복량 증가
		if (getTollTipHealingPotion() != 0) {
			os.writeC(27);
			os.writeC(getTollTipHealingPotion());
		}

		// PvP 데미지 감소
		if (getTollTipPvPReduction() != 0) {
			os.writeC(28);
			os.writeC(getTollTipPvPReduction());
		}
		
		// PvP 데미지 감소
		if (getTollTipPvPDmg() != 0) {
			os.writeC(29);
			os.writeC(getTollTipPvPDmg());
		}
		// System.out.println("13 : " + os.getBytes().length);

		return os.getBytes();
		} finally {
			try {
				os.close();
			} catch (IOException ignored) {
			}
		}
	}
	/**
	 * 아이템 옵션 확인
	 */
	public void checkOption() {
		int hp = 0;
		int mp = 0;
		int sp = 0;
		int enchant_max = 10;
		int healingpotion = 0;
		int stundefens = 0;
		int reduction = 0;
		int hit = 0;
		int dmg = 0;
		int mr = 0;
		int PVPreduction = 0;
		int PVPDmg = 0;
		int ac = 0;
		
		if (getItem().getName().equalsIgnoreCase("마법 망토")) {
			mr += getEnLevel() *2;
			
		
		}
		if (getItem().getName().equalsIgnoreCase("마법 방어 투구")) {
			mr += getEnLevel();
		}
		// 지팡이를 제외한 축무기 추가 대미지
		if ((getBless() == 0 || getBless() == -128) && getItem().getType1().equalsIgnoreCase("weapon") && !getItem().getType2().equalsIgnoreCase("wand")) {
			dmg += 2;
		}
		// 축복 지팡이 추가 SP
		if ((getBless() == 0 || getBless() == -128) && getItem().getType2().equalsIgnoreCase("wand")) {
			sp += 1;
		
		}

		// 축복 방어구 추가 HP (악세사리 제외)
		if ((getBless() == 0 || getBless() == -128) && getItem().getType1().equalsIgnoreCase("armor")
				&& !getItem().getType2().equalsIgnoreCase("ring") && !getItem().getType2().equalsIgnoreCase("belt")
				&& !getItem().getType2().equalsIgnoreCase("necklace")) {
			hp += 10;
			
		}

		// 축복 장신구 추가 대미지, 추가 명중 (방어구류 제외)
		if ((getBless() == 0 || getBless() == -128)
				&& (getItem().getType2().equalsIgnoreCase("ring") || getItem().getType2().equalsIgnoreCase("belt")
						|| getItem().getType2().equalsIgnoreCase("necklace"))) {
			dmg += 1;
			hit += 1;
			sp += 1;
		}

		// 악세사리 인첸트 효과
		if (getEnLevel() > 0 && getEnLevel() <= enchant_max) {
			int en = getEnLevel();
			// 귀걸이
			if (getItem().getType2().equalsIgnoreCase("earring")) {
				switch (en) {
				case 1:
					ac += 1;
					hp += 5;
					break;
				case 2:
					ac += 2;
					hp += 10;
					break;
				case 3:
					ac += 3;
					hp += 20;
					break;
				case 4:
					ac += 4;
					hp += 30;
					break;
				case 5:
					ac += 5;
					hp += 40;
					healingpotion += 2;
					break;
				case 6:
					ac += 6;
					hp += 40;
					healingpotion += 4;
					break;
				case 7:
					ac += 7;
					hp += 50;
					healingpotion += 6;
					stundefens += 2;
					break;
				case 8:
					ac += 8;
					hp += 50;
					healingpotion += 8;
					stundefens += 3;
					break;
				case 9:
					ac += 9;
					healingpotion += 9;
					stundefens += 4;
					hp += 60;
					break;
				default:
					ac += 10;
					healingpotion += 10;
					stundefens += 5;
					hp += 70;
				}
			}

			// 벨트
			if (getItem().getType2().equalsIgnoreCase("belt")) {
				switch (en) {
				case 1:
					mp += 5;
					break;
				case 2:
					mp += 10;
					break;
				case 3:
					mp += 20;
					break;
				case 4:
					mp += 30;
					break;
				case 5:
					mp += 40;
					reduction += 1;
					break;
				case 6:
					mp += 40;
					reduction += 2;
					hp += 20;
					break;
				case 7:
					mp += 50;
					reduction += 3;
					hp += 30;
					PVPreduction += 2;
					break;
				case 8:
					mp += 50;
					reduction += 4;
					hp += 40;
					PVPreduction += 3;
					break;
				case 9:
					mp += 60;
					reduction += 5;
					hp += 50;
					PVPreduction += 4;
					break;
				case 10:
					mp += 70;
					reduction += 6;
					hp += 60;
					PVPreduction += 5;
					break;
				}
			}

			// 목걸이
			if (getItem().getType2().equalsIgnoreCase("necklace")) {
				switch (en) {
				case 1:
					hp += 5;
					break;
				case 2:
					hp += 10;
					break;
				case 3:
					hp += 20;
					break;
				case 4:
					hp += 30;
					break;
				case 5:
					hp += 40;
					healingpotion += 2;
					break;
				case 6:
					hp += 40;
					healingpotion += 4;
					break;
				case 7:
					hp += 50;
					healingpotion += 6;
					stundefens += 0.02;
					break;
				case 8:
					hp += 50;
					healingpotion += 8;
					stundefens += 0.03;
					break;
				case 9:
					hp += 60;
					healingpotion += 9;
					stundefens += 0.04;
					break;
				case 10:
					hp += 70;
					healingpotion += 10;
					stundefens += 0.05;
					break;
				}
			}

			// 반지
			if (getItem().getType2().equalsIgnoreCase("ring")) {
				switch (en) {
				case 1:
					hp += 5;
					break;
				case 2:
					hp += 10;
					break;
				case 3:
					hp += 20;
					break;
				case 4:
					hp += 30;
					break;
				case 5:
					hp += 40;
					dmg += 1;
					break;
				case 6:
					hp += 40;
					dmg += 2;
					mr += 1;
					break;
				case 7:
					hp += 50;
					dmg += 3;
					mr += 3;
					sp += 1;
					PVPDmg += 1;
					break;
				case 8:
					hp += 50;
					dmg += 4;
					mr += 5;
					sp += 2;
					PVPDmg += 2;
					break;
				case 9:
					hp += 60;
					dmg += 5;
					mr += 7;
					sp += 3;
					PVPDmg += 3;
					break;
				case 10:
					hp += 70;
					dmg += 6;
					mr += 8;
					sp += 4;
					PVPDmg += 4;
					break;
				}
			}
		}

		setTollTipHit(hit);
		setTollTipHp(hp);
		setTollTipAc(ac);
		setTollTipMp(mp);
		setTollTipSp(sp);
		setTollTipHealingPotion(healingpotion);
		setTollTipStunDefens(stundefens);
		setTollTipReduction(reduction);
		setTollTipDmg(dmg);
		setTollTipMr(mr);
		setTollTipPvPReduction(PVPreduction);
		setTollTipPvPDmg(PVPDmg);
	}
}
