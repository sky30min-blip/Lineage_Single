package lineage.bean.database;

public class LifeLostItem {
	private String item;
	private double nomalChance;
	private double blessChance;
	private double blessContinueChance;
	private String itemName;
	private int bless;
	private int en;
	private long minCount;
	private long maxCount;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getNomalChance() {
		return nomalChance;
	}
	public void setNomalChance(double nomalChance) {
		this.nomalChance = nomalChance;
	}
	public double getBlessChance() {
		return blessChance;
	}
	public void setBlessChance(double blessChance) {
		this.blessChance = blessChance;
	}
	public double getBlessContinueChance() {
		return blessContinueChance;
	}
	public void setBlessContinueChance(double blessContinueChance) {
		this.blessContinueChance = blessContinueChance;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getBless() {
		return bless;
	}
	public void setBless(int bless) {
		this.bless = bless;
	}
	public int getEn() {
		return en;
	}
	public void setEn(int en) {
		this.en = en;
	}
	public long getMinCount() {
		return minCount;
	}
	public void setMinCount(long minCount) {
		this.minCount = minCount;
	}
	public long getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(long maxCount) {
		this.maxCount = maxCount;
	}
}
