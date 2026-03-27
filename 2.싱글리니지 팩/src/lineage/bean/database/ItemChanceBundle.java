package lineage.bean.database;

public class ItemChanceBundle {
	private String name;
	private String item;
	private int itemBless;
	private int itemEnchant;
	private int itemCountMin;
	private int itemCountMax;
	private int Count;
	private boolean define;
	private double itemChance;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getItemBless() {
		return itemBless;
	}
	public void setItemBless(int itemBless) {
		this.itemBless = itemBless;
	}
	public int getItemEnchant() {
		return itemEnchant;
	}
	public void setItemEnchant(int itemEnchant) {
		this.itemEnchant = itemEnchant;
	}
	public int getItemCountMin() {
		return itemCountMin;
	}
	public void setItemCountMin(int itemCountMin) {
		this.itemCountMin = itemCountMin;
	}
	public int getCount() {
		return Count;
	}
	public void setCount(int Count) {
		this.Count = Count;
	}
	public int getItemCountMax() {
		return itemCountMax;
	}
	public void setItemCountMax(int itemCountMax) {
		this.itemCountMax = itemCountMax;
	}
	public boolean isDefine() {
		return define;
	}
	public void setDefine(boolean define) {
		this.define = define;
	}
	public double getItemChance() {
		return itemChance;
	}
	public void setItemChance(double itemChance) {
		this.itemChance = itemChance;
	}
}
