package lineage.bean.database;

public class PcTrade {
	// 거래번호
	private int uid;
	// 판매자 계정
	private int sell_account_uid;
	// 판매자 캐릭터
	private String sell_name;
	// 판매자 캐릭터 objId
	private long sell_objId;
	// 거래 상황
	private String state;
	// 구매자 계정
	private int buy_account_uid;
	// 구매자 캐릭터
	private String buy_name;
	// 구매자 캐릭터 objId
	private long buy_objId;
	// 가격
	private int price;
	// 아이템 ojbId
	private long item_objId;
	// 아이템
	private String item;
	// 인챈트
	private int enchant;
	// 축복여부
	private int bless;
	// 수량
	private long count;
	// 구매신청 날짜
	private long buy_apply_day;
	// 거래 완료 날짜
	private long complete_day;
	// 글 제목
	private String subject;
	// 글 내용
	private String content;
	// 글 등록 날짜
	private long write_day;
	// 판매자 이름
	private String name;
	// 판매자 연락처
	private String phone_num;
	// 판매자 은행명
	private String bank_name;
	// 판매자 계좌번호
	private String bank_num;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getSell_account_uid() {
		return sell_account_uid;
	}
	public void setSell_account_uid(int sell_account_uid) {
		this.sell_account_uid = sell_account_uid;
	}	
	public String getSell_name() {
		return sell_name;
	}
	public void setSell_name(String sell_name) {
		this.sell_name = sell_name;
	}
	public long getSell_objId() {
		return sell_objId;
	}
	public void setSell_objId(long sell_objId) {
		this.sell_objId = sell_objId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getBuy_account_uid() {
		return buy_account_uid;
	}
	public void setBuy_account_uid(int buy_account_uid) {
		this.buy_account_uid = buy_account_uid;
	}
	public String getBuy_name() {
		return buy_name;
	}
	public void setBuy_name(String buy_name) {
		this.buy_name = buy_name;
	}
	public long getBuy_objId() {
		return buy_objId;
	}
	public void setBuy_objId(long buy_objId) {
		this.buy_objId = buy_objId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public long getItem_objId() {
		return item_objId;
	}
	public void setItem_objId(long item_objId) {
		this.item_objId = item_objId;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getEnchant() {
		return enchant;
	}
	public void setEnchant(int enchant) {
		this.enchant = enchant;
	}
	public int getBless() {
		return bless;
	}
	public void setBless(int bless) {
		this.bless = bless;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getBuy_apply_day() {
		return buy_apply_day;
	}
	public void setBuy_apply_day(long buy_apply_day) {
		this.buy_apply_day = buy_apply_day;
	}
	public long getComplete_day() {
		return complete_day;
	}
	public void setComplete_day(long complete_day) {
		this.complete_day = complete_day;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getWrite_day() {
		return write_day;
	}
	public void setWrite_day(long write_day) {
		this.write_day = write_day;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_num() {
		return bank_num;
	}
	public void setBank_num(String bank_num) {
		this.bank_num = bank_num;
	}
}
