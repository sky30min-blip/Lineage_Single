package lineage.bean.database;

public class FirstSpawn {
	private int x;
	private int y;
	private int map;
	public FirstSpawn(int x, int y, int map){
		this.x = x;
		this.y = y;
		this.map = map;
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
}
