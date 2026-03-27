package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class MonsterSpawnlist {
	private int uid;
	private String name;
	private Monster monster;
	private boolean random;
	private int count;
	private int locSize;
	private int x;
	private int y;
	private List<Integer> map = new ArrayList<Integer>();
	private int reSpawn;
	private int reSpawnMax;
	private boolean group;
	private List<MonsterGroup> listGroup = new ArrayList<MonsterGroup>();

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

	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public int getLocSize() {
		return locSize;
	}

	public void setLocSize(int locSize) {
		this.locSize = locSize;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public List<Integer> getMap() {
		return map;
	}

	public int getReSpawn() {
		return reSpawn;
	}
	
	public void setReSpawn(int reSpawn) {
		this.reSpawn = reSpawn;
	}
	
	public int getReSpawnMax() {
		return reSpawnMax;
	}
	
	public void setReSpawnMax(int reSpawnMax) {
		this.reSpawnMax = reSpawnMax;
	}
	
	public boolean isGroup() {
		return group;
	}
	public void setGroup(boolean group) {
		this.group = group;
	}
	public List<MonsterGroup> getListGroup() {
		return listGroup;
	}
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
