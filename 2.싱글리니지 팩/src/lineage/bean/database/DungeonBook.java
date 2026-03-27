package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class DungeonBook {
	private int uid;
	private String name;
	private List<FirstSpawn> loc_list = new ArrayList<FirstSpawn>();
	private int level;
	private boolean clan;
	private boolean wanted;
	private String aden;
	private long count;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<FirstSpawn> getLoc_list() {
		return loc_list;
	}
	public void setLoc_list(List<FirstSpawn> loc_list) {
		this.loc_list = loc_list;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isClan() {
		return clan;
	}
	public void setClan(boolean clan) {
		this.clan = clan;
	}
	public boolean isWanted() {
		return wanted;
	}
	public void setWanted(boolean wanted) {
		this.wanted = wanted;
	}
	public String getAden() {
		return aden;
	}
	public void setAden(String aden) {
		this.aden = aden;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
