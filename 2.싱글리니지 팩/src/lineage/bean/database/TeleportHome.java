package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TeleportHome {
	public class Location {
		public int x;
		public int y;
		public int map;
	}
	public class LocationRnd {
		public int x1;
		public int x2;
		public int y1;
		public int y2;
		public int map;
	}
	
	private String name;
	private int classType;
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private int map;
	private List<Location> list_goto = new ArrayList<Location>();
	private List<LocationRnd> list_goto_rnd = new ArrayList<LocationRnd>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getClassType() {
		return classType;
	}
	public void setClassType(int classType) {
		this.classType = classType;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getY2() {
		return y2;
	}
	public void setY2(int y2) {
		this.y2 = y2;
	}
	public int getMap() {
		return map;
	}
	public void setMap(int map) {
		this.map = map;
	}
	public List<Location> getListGoto() {
		return list_goto;
	}
	public List<LocationRnd> getListGotoRnd() {
		return list_goto_rnd;
	}
	public void appendLocation(String loc){
		try {
			Location l = new Location();
			StringTokenizer tok = new StringTokenizer(loc);
			l.x = Integer.valueOf(tok.nextToken());
			l.y = Integer.valueOf(tok.nextToken());
			l.map = Integer.valueOf(tok.nextToken());
			
			list_goto.add(l);
		} catch (Exception e) { }
	}
	public void appendLocationRnd(String loc){
		try {
			LocationRnd l = new LocationRnd();
			StringTokenizer tok = new StringTokenizer(loc);
			l.x1 = Integer.valueOf(tok.nextToken());
			l.x2 = Integer.valueOf(tok.nextToken());
			l.y1 = Integer.valueOf(tok.nextToken());
			l.y2 = Integer.valueOf(tok.nextToken());
			l.map = Integer.valueOf(tok.nextToken());
			
			list_goto_rnd.add(l);
		} catch (Exception e) { }
	}
}
