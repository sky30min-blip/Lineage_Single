package lineage.bean.lineage;

public class Teleport {
	private int price;
	private String action;
	private int x;
	private int y;
	private int map;
	public Teleport(String action, int price, int x, int y, int map){
		this.action = action;
		this.price = price;
		this.x = x;
		this.y = y;
		this.map = map;
	}
	public int getPrice() {
		return price;
	}
	public String getAction() {
		return action;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getMap() {
		return map;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setMap(int map) {
		this.map = map;
	}
	
}
