package lineage.bean.database;

public class EnchantLostItem implements Comparable<EnchantLostItem> {
	private long cha_objId;
	private String cha_name;
	private long item_objId;
	private String item_name;
	private int en_level;
	private int bless;
	private long count;
	private long lost_time;
	private String scroll_name;
	private int scroll_bless;
	private boolean 지급여부;

	public long getCha_objId() {
		return cha_objId;
	}

	public void setCha_objId(long cha_objId) {
		this.cha_objId = cha_objId;
	}

	public String getCha_name() {
		return cha_name;
	}

	public void setCha_name(String cha_name) {
		this.cha_name = cha_name;
	}

	public long getItem_objId() {
		return item_objId;
	}

	public void setItem_objId(long item_objId) {
		this.item_objId = item_objId;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public int getEn_level() {
		return en_level;
	}

	public void setEn_level(int en_level) {
		this.en_level = en_level;
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

	public long getLost_time() {
		return lost_time;
	}

	public void setLost_time(long lost_time) {
		this.lost_time = lost_time;
	}
	
	public String getScroll_name() {
		return scroll_name;
	}

	public void setScroll_name(String scroll_name) {
		this.scroll_name = scroll_name;
	}

	public int getScroll_bless() {
		return scroll_bless;
	}

	public void setScroll_bless(int scroll_bless) {
		this.scroll_bless = scroll_bless;
	}

	public boolean is지급여부() {
		return 지급여부;
	}

	public void set지급여부(boolean 지급여부) {
		this.지급여부 = 지급여부;
	}

	@Override
	public int compareTo(EnchantLostItem el) {
		if (this.en_level < el.getEn_level()) {
			return -1;
		} else if (this.en_level > el.getEn_level()) {
			return 1;
		} else if (this.en_level == el.getEn_level()) {
			if (this.lost_time < el.getLost_time()) {
				return -1;
			} else if (this.lost_time > el.getLost_time()) {
				return 1;
			}
		}

		return 0;
	}
}
