package lineage.bean.database;

import lineage.world.object.object;

public class Exp {
	private object o;
	private int level;
	private double exp;
	private double bonus;
	private int dmg;				// 몬스터에게 가한 데미지에 누적값.
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public double getExp() {
		return exp;
	}
	public void setExp(double exp) {
		this.exp = exp;
	}
	public double getBonus() {
		return bonus;
	}
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	public void close(){
		exp = bonus = level = dmg = 0;
		o = null;
	}
	public object getObject() {
		return o;
	}
	public void setObject(object o) {
		this.o = o;
	}
	public int getDmg() {
		return dmg;
	}
	public void setDmg(int dmg) {
		this.dmg = dmg;
	}
}
