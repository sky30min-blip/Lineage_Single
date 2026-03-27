package lineage.bean.database;

public class CharacterMarble {
	private long item_objId;
	private long cha_objId;
	private String name;
	private int level;
	private String exp;
	private int classType;
	private int sex;
	
	public long getItem_objId() {
		return item_objId;
	}
	public void setItem_objId(long item_objId) {
		this.item_objId = item_objId;
	}
	public long getCha_objId() {
		return cha_objId;
	}
	public void setCha_objId(long cha_objId) {
		this.cha_objId = cha_objId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getExp() {
		return exp;
	}
	public void setExp(String exp) {
		this.exp = exp;
	}
	public int getClassType() {
		return classType;
	}
	public void setClassType(int classType) {
		this.classType = classType;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
}
