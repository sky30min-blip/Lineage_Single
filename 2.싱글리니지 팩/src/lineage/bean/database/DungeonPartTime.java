package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class DungeonPartTime {
	
	private int uid;
	private String name;
	private int time;
	private List<Integer> list;
	private long updateTime;
	
	public DungeonPartTime() {
		list = new ArrayList<Integer>();
	}
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
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public List<Integer> getList() {
		return list;
	}
	public void setList(List<Integer> list) {
		this.list = list;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
}
