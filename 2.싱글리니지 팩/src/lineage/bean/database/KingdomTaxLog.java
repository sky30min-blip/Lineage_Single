package lineage.bean.database;


public class KingdomTaxLog {
	private int kingdom;		// 성 고유값
	private String kingdomName;	// 성 이름
	private String type;		// 종류
	private int tax;			// 아덴
	private long date;			// 날자
	public void close(){
		date = kingdom = tax = 0;
		type = kingdomName = null;
	}
	public int getKingdom() {
		return kingdom;
	}
	public void setKingdom(int kingdom) {
		this.kingdom = kingdom;
	}
	public String getKingdomName() {
		return kingdomName;
	}
	public void setKingdomName(String kingdomName) {
		this.kingdomName = kingdomName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
}
