package lineage.bean.lineage;

import lineage.bean.database.Item;

public class Craft {
	private Item item;				// 재료 아이템 정보
	private int count;				// 재료 아이템 필요 갯수
	private int temp_craft_max;		// 해당 재료만으로 계산된 제작가능한 최대 갯수.
	private int enchant;
	private int bless;	
	public Craft(Item item, int count){
		this.item = item;
		this.count = count;
	}
	public Craft(Item item, int enchant,int bless, int count) {
		this.item = item;
		this.enchant = enchant;
		this.bless = bless;
		this.count = count;
	}
	public Item getItem() {
		return item;
	}
	public int getCount() {
		return count;
	}
	public int getEnchant() {
		return enchant;
	}
	public int getBless() {
		return bless;
	}

	public int getTempCraftMax() {
		return temp_craft_max;
	}
	public void setTempCraftMax(int temp_craft_max) {
		this.temp_craft_max = temp_craft_max;
	}
}
