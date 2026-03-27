package lineage.bean.database;

public class MonsterGroup {
	private Monster monster;
	private int count;
	public MonsterGroup(Monster monster, int count){
		this.monster = monster;
		this.count = count<=0 ? 1 : count;
	}
	public Monster getMonster() {
		return monster;
	}
	public void setMonster(Monster monster) {
		this.monster = monster;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
