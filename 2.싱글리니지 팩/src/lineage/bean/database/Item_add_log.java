package lineage.bean.database;

import java.util.HashMap;
import java.util.Map;

public class Item_add_log {
	private long cha_objId;
	private String cha_name;
	private long item_objId;
	private String item_name;
	private int en_level;
	private int bless;
	private long count;
	private long kubera_add_time;
	private String 여부;
	
	
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
	public long getkubera_add_time() {
		return kubera_add_time;
	}

	public void setkubera_add_time(long kubera_add_time) {
		this.kubera_add_time = kubera_add_time;
	}
	
	public String ishow() {
		return 여부;
	}

	public void sethow(String 여부) {
		this.여부 = 여부;
		
	}

}
