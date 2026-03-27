package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class Boss {
	private String name;
	private List<String> spawn = new ArrayList<String>();
	private List<Integer> spawn_day = new ArrayList<Integer>();
	private List<String> group_monster = new ArrayList<String>();
	int[][] time;
	Monster mon;
	long last_time;
	private boolean 스폰알림여부;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getSpawn() {
		return spawn;
	}
	public List<Integer> getSpawn_day() {
		return spawn_day;
	}
	public List<String> getGroup_monster() {
		return group_monster;
	}
	public Monster getMon() {
		return mon;
	}
	public void setMon(Monster mon) {
		this.mon = mon;
	}
	public int[][] getTime(){
		return time;
	}
	public void setTime(int[][] time){
		this.time = time;
	}
	public long getLastTime() {
		return last_time;
	}
	public void setLastTime(long last_time) {
		this.last_time = last_time;
	}
	public boolean is스폰알림여부() {
		return 스폰알림여부;
	}
	public void set스폰알림여부(boolean 스폰알림여부) {
		this.스폰알림여부 = 스폰알림여부;
	}
	
	/**
	 * 스폰해야할 시간인지 확인해주는 함수.
	 * @param h
	 * @param m
	 * @return
	 */
	public boolean isSpawnTime(int day, int h, int m) {	
		for (int d : spawn_day) {
			if (d == day) {
				for (int[] t : time) {
					if (t[0] == h && t[1] == m) {
						if (last_time > 0 && last_time + (1000 * 60) > System.currentTimeMillis())
							continue;
						else
							return true;
					}
				}
			}
		}
	
		return false;
	}
}
