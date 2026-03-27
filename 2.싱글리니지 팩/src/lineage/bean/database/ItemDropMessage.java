package lineage.bean.database;

public class ItemDropMessage {
	private String item;
	private boolean 획득시알림여부;

	private int en;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public boolean is획득시알림여부() {
		return 획득시알림여부;
	}
	public void set획득시알림여부(boolean 획득시알림여부) {
		this.획득시알림여부 = 획득시알림여부;
	}
	public int getEn() {
		return en;
	}
	public void setEn(int en) {
		this.en = en;
	}	
}
