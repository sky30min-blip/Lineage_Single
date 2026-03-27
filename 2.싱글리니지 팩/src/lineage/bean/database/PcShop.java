package lineage.bean.database;

import lineage.world.object.instance.PcShopInstance;

public class PcShop {
	//-- npc용
	private int npcid;		// 
	private int id;			// 
	private int count;		// 
	private String adenType; // 화폐타입
	private long price;		// 판매할 가격
	private int taxPrice;	// 세율에따라 얻는 금액 저장변수
	private boolean shop;	// 판매 가능 여부
	private Item item;		// 
	
	//-- pc용
	private PcShopInstance pc;
	private long InvItemObjectId;
	private long InvItemCount;
	private int InvItemEn;
	private int InvItemBress;
	private boolean InvItemDefinite;
	
	private int InvItemEnFire;    
	private int InvItemEnWater;    
	private int InvItemEnWind;    
	private int InvItemEnEarth;    
	
	private long petObjId;
	private String petName;
	private int petLevel;

	
	private String InvItemK;    
	//-- pc용
		private int InvDolloptionA;
		private int InvDolloptionB;
		private int InvDolloptionC;
		private int InvDolloptionD;
		private int InvDolloptionE;
		
	
		
	public PcShop(PcShopInstance pc_shop, long price, String adenType, long InvItemCount){
		this.pc = pc_shop;
		this.price = price;
		this.adenType = adenType;
		this.InvItemCount = InvItemCount;
	}
	
	public PcShopInstance getPc(){
		return pc;
	}
	public int getNpcid() {
		return npcid;
	}
	public void setNpcid(int npcid) {
		this.npcid = npcid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public boolean isShop() {
		return shop;
	}
	public void setShop(boolean shop) {
		this.shop = shop;
	}
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	public int getTaxPrice() {
		return taxPrice;
	}
	public void setTaxPrice(int taxPrice) {
		this.taxPrice = taxPrice;
	}

	public long getInvItemObjectId() {
		return InvItemObjectId;
	}

	public void setInvItemObjectId(long invItemObjectId) {
		InvItemObjectId = invItemObjectId;
	}

	public long getInvItemCount() {
		return InvItemCount;
	}

	public void setInvItemCount(long invItemCount) {
		InvItemCount = invItemCount;
	}

	public int getInvItemEn() {
		return InvItemEn;
	}

	public void setInvItemEn(int invItemEn) {
		InvItemEn = invItemEn;
	}

	public int getInvItemBress() {
		return InvItemBress;
	}

	public void setInvItemBress(int invItemBress) {
		InvItemBress = invItemBress;
	}

	public boolean isInvItemDefinite() {
		return InvItemDefinite;
	}

	public void setInvItemDefinite(boolean invItemDefinite) {
		InvItemDefinite = invItemDefinite;
	}
	
	public String getAdenType() {
		return adenType;
	}

	public void setAdenType(String adenType) {
		this.adenType = adenType;
	}
	
	public int getInvItemEnFire() {   
	    return InvItemEnFire;
	}

	public void setInvItemEnFire(int invItemEnFire) {
	    InvItemEnFire = invItemEnFire;
	}
	public int getInvItemEnWater() {   
		return InvItemEnWater;
	}

	public void setInvItemEnWater(int invItemEnWater) {
		InvItemEnWater = invItemEnWater;
	}
	public int getInvItemEnWind() {   
		return InvItemEnWind;
	}

	public void setInvItemEnWind(int invItemEnWind) {
		InvItemEnWind = invItemEnWind;
	}
	public int getInvItemEnEarth() {   
		return InvItemEnEarth;
	}

	public void setInvItemEnEarth(int invItemEnEarth) {
		InvItemEnEarth = invItemEnEarth;
	}
		public String getInvItemK() {   
			return InvItemK;
		}
	
		public void setInvItemK(String invItemK) {
			InvItemK = invItemK;
		}

	public int getInvDolloptionA() {
		return InvDolloptionA;
	}
	public void setInvDolloptionA(int invDolloptionA) {
		InvDolloptionA = invDolloptionA;
	}
	public int getInvDolloptionB() {
		return InvDolloptionB;
	}
	public void setInvDolloptionB(int invDolloptionB) {
		InvDolloptionB = invDolloptionB;
	}
	public int getInvDolloptionC() {
		return InvDolloptionC;
	}
	public void setInvDolloptionC(int invDolloptionC) {
		InvDolloptionC = invDolloptionC;
	}
	public int getInvDolloptionD() {
		return InvDolloptionD;
	}
	public void setInvDolloptionD(int invDolloptionD) {
		InvDolloptionD = invDolloptionD;
	}
	public int getInvDolloptionE() {
		return InvDolloptionE;
	}
	public void setInvDolloptionE(int invDolloptionE) {
		InvDolloptionE = invDolloptionE;
	}
	public long getPetObjId() {
		return petObjId;
	}

	public void setPetObjId(long petObjId) {
		this.petObjId = petObjId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public int getPetLevel() {
		return petLevel;
	}

	public void setPetLevel(int petLevel) {
		this.petLevel = petLevel;
	}
}
