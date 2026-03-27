package lineage.bean.database;

public class ItemTeleport {
	private int Uid;
	private String Name;
	private int X;
	private int Y;
	private int Map;
	private int range;
	private int Heading;
	private int Level;
	private int ClassType;
	private boolean remove;
	
	public int getUid() {
		return Uid;
	}
	public void setUid(int uid) {
		Uid = uid;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public int getMap() {
		return Map;
	}
	public void setMap(int map) {
		Map = map;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getHeading() {
		return Heading;
	}
	public void setHeading(int heading) {
		Heading = heading;
	}
	public int getLevel() {
		return Level;
	}
	public void setLevel(int level) {
		Level = level;
	}
	public int getClassType() {
		return ClassType;
	}
	public void setClassType(int classType) {
		ClassType = classType;
	}
	public boolean isRemove() {
		return remove;
	}
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
	
}
