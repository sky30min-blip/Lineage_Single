package lineage.bean.database;

public class Drop {
	private String Name;
	private String MonName;
	private String ItemName;
	private int ItemBress;
	private int ItemEn;
	private int CountMin;
	private int CountMax;
	private double Chance;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getMonName() {
		return MonName;
	}
	public void setMonName(String monName) {
		MonName = monName;
	}
	public String getItemName() {
		return ItemName;
	}
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
	public int getItemBress() {
		return ItemBress;
	}
	public void setItemBress(int itemBress) {
		ItemBress = itemBress;
	}
	public int getItemEn() {
		return ItemEn;
	}
	public void setItemEn(int itemEn) {
		ItemEn = itemEn;
	}
	public int getCountMin() {
		return CountMin;
	}
	public void setCountMin(int countMin) {
		CountMin = countMin;
	}
	public int getCountMax() {
		return CountMax;
	}
	public void setCountMax(int countMax) {
		CountMax = countMax;
	}
	public double getChance() {
		return Chance;
	}
	public void setChance(double chance) {
		Chance = chance;
	}
	
}
