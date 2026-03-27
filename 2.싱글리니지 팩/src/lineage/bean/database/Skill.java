package lineage.bean.database;

public class Skill {
	private int Uid;				// 마법아이디
	private String Name;			// 마법이름
	private int SkillLevel;			// 사용레벨
	private int SkillNumber;		// 마법번호
	private int MpConsume;			// 소모되는 mp량
	private int HpConsume;			// 소모되는 hp량
	private int ItemConsume;		// 마법 시전시 사용되는 재료의 네임아이디 넘버
	private int ItemConsumeCount;	// 마법 시전시 사용되는 재료의 갯수
	private int BuffDuration;		// 버프 지속 시간
	private double Mindmg;			// 최소 데미지
	private double Maxdmg;			// 최대 데미지
	private int Id;					// 마법 고유 아이디
	private int CastGfx;			// 마법 시전시 표현될 gfx이팩트 번호
	private int distance;			// 스킬 사거리
	private int Range;				// 스킬 공격 범위
	private int LawfulConsume; 		// 라우풀감소값
	private int Price;				// 상점에서 판매되는 스킬 가격.
	private int Delay;				// 마딜 용.
	private int Element;			// 속성 종류.
	private String lock;			// 굳는 효과 처리.
	
	public Skill(){
		//
	}
	public Skill(int Uid, String Name){
		this.Uid = Uid;
		this.Name = Name;
	}
	public int getUid() {
		return Uid;
	}
	public void setUid(int uid) {
		Uid = uid;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getSkillLevel() {
		return SkillLevel;
	}
	public void setSkillLevel(int skillLevel) {
		SkillLevel = skillLevel;
	}
	public int getSkillNumber() {
		return SkillNumber;
	}
	public void setSkillNumber(int skillNumber) {
		SkillNumber = skillNumber;
	}
	public int getMpConsume() {
		return MpConsume;
	}
	public void setMpConsume(int mpConsume) {
		MpConsume = mpConsume;
	}
	public int getHpConsume() {
		return HpConsume;
	}
	public void setHpConsume(int hpConsume) {
		HpConsume = hpConsume;
	}
	public int getItemConsume() {
		return ItemConsume;
	}
	public void setItemConsume(int itemConsume) {
		ItemConsume = itemConsume;
	}
	public int getItemConsumeCount() {
		return ItemConsumeCount;
	}
	public void setItemConsumeCount(int itemConsumeCount) {
		ItemConsumeCount = itemConsumeCount;
	}
	public int getBuffDuration() {
		return BuffDuration;
	}
	public void setBuffDuration(int buffDuration) {
		BuffDuration = buffDuration;
	}
	public double getMindmg() {
		return Mindmg;
	}
	public void setMindmg(double mindmg) {
		Mindmg = mindmg;
	}
	public double getMaxdmg() {
		return Maxdmg;
	}
	public void setMaxdmg(double maxdmg) {
		Maxdmg = maxdmg;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getCastGfx() {
		return CastGfx;
	}
	public void setCastGfx(int castGfx) {
		CastGfx = castGfx;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getRange() {
		return Range;
	}
	public void setRange(int Range) {
		this.Range = Range;
	}
	public int getLawfulConsume() {
		return LawfulConsume;
	}
	public void setLawfulConsume(int lawfulConsume) {
		LawfulConsume = lawfulConsume;
	}
	public int getPrice() {
		return Price;
	}
	public void setPrice(int price) {
		Price = price;
	}
	public int getDelay() {
		return Delay;
	}
	public void setDelay(int delay) {
		Delay = delay;
	}
	public int getElement() {
		return Element;
	}
	public void setElement(int element) {
		Element = element;
	}
	public String getLock() {
		return lock;
	}
	public void setLock(String lock) {
		this.lock = lock;
	}
}
