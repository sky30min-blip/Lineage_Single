package lineage.bean.lineage;

public class StatDice {
	private int Str;
	private int Dex;
	private int Con;
	private int Wis;
	private int Int;
	private int Cha;
	private int type;
	public int getStr() {
		return Str;
	}
	public void setStr(int str) {
		Str = str;
	}
	public int getDex() {
		return Dex;
	}
	public void setDex(int dex) {
		Dex = dex;
	}
	public int getCon() {
		return Con;
	}
	public void setCon(int con) {
		Con = con;
	}
	public int getWis() {
		return Wis;
	}
	public void setWis(int wis) {
		Wis = wis;
	}
	public int getInt() {
		return Int;
	}
	public void setInt(int i) {
		Int = i;
	}
	public int getCha() {
		return Cha;
	}
	public void setCha(int cha) {
		Cha = cha;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStat(){
		return getStr()+getDex()+getCon()+getWis()+getInt()+getCha();
	}
}
