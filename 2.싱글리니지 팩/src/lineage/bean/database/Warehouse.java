package lineage.bean.database;

public class Warehouse {
	private int uid;
	private int accountUid;
	private int clanId;
	private int invId;
	private int petId;
	private int letterId;
	private String name;
	private String 구분1;
	private String 구분2;
	private int type;
	private int gfxid;
	private long count;
	private int quantity;
	private int en;
	private boolean definite;
	private int bress;
	private int durability;
	private int time;
	private int enfire;
	private int enwater;
	private int enwind;
	private int enearth;
	private int InvDolloptionA;
	private int InvDolloptionB;
	private int InvDolloptionC;
	private int InvDolloptionD;
	private int InvDolloptionE;
	
	public void clear() {
		uid = accountUid = clanId = invId = petId = letterId = type = gfxid = quantity = en = bress = durability = time = enfire = enwater = enwind=  enearth= 0;
		InvDolloptionA = InvDolloptionB = InvDolloptionC = InvDolloptionD = InvDolloptionE = 0;
		count = 0;
		name = 구분1 = 구분2 = null;
		definite = false;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getAccountUid() {
		return accountUid;
	}
	public void setAccountUid(int accountUid) {
		this.accountUid = accountUid;
	}
	public int getClanId() {
		return clanId;
	}
	public void setClanId(int clanId) {
		this.clanId = clanId;
	}
	public int getInvId() {
		return invId;
	}
	public void setInvId(int invId) {
		this.invId = invId;
	}
	public int getPetId() {
		return petId;
	}
	public void setPetId(int petId) {
		this.petId = petId;
	}
	public int getLetterId() {
		return letterId;
	}
	public void setLetterId(int letterId) {
		this.letterId = letterId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String get구분1() {
		return 구분1;
	}
	public void set구분1(String 구분1) {
		this.구분1 = 구분1;
	}
	public String get구분2() {
		return 구분2;
	}
	public void set구분2(String 구분2) {
		this.구분2 = 구분2;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getGfxid() {
		return gfxid;
	}
	public void setGfxid(int gfxid) {
		this.gfxid = gfxid;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getEn() {
		return en;
	}
	public void setEn(int en) {
		this.en = en;
	}
	public boolean isDefinite() {
		return definite;
	}
	public void setDefinite(boolean definite) {
		this.definite = definite;
	}
	public int getBress() {
		return bress;
	}
	public void setBress(int bress) {
		this.bress = bress;
	}
	public int getDurability() {
		return durability;
	}
	public void setDurability(int durability) {
		this.durability = durability;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getEnfire(){
		return enfire;
	}
	public void setEnfire(int enfire){
		this.enfire = enfire;
	}
	
	public int getEnwater(){
		return enwater;
	}
	public void setEnwater(int enwater){
		this.enwater = enwater;
	}
	public int getEnwind(){
		return enwind;
	}
	public void setEnwind(int enwind){
		this.enwind = enwind;
	}
	public int getEnearth(){
		return enearth;
	}
	public void setEnearth(int enearth){
		this.enearth = enearth;
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
}
