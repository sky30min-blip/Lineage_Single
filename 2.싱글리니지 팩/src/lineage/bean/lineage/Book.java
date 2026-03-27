package lineage.bean.lineage;
//기억의구슬
public class Book {
	private String location;
	private int x;
	private int y;
	private int map;
	private int minLevel;

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
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
	public int getMap() {
		return map;
	}
	public void setMap(int map) {
		this.map = map;
	}
	public int getMinLevel() {
		return minLevel;
	}
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}
	private int random;
	public int getRandom() {
		return random;
	}
	public void setRandom(int i) {
		this.random = i;
	}
}
