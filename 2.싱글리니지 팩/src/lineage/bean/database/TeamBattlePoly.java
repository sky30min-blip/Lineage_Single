package lineage.bean.database;

public class TeamBattlePoly {
	private long objId;
	private int gfx;
	private Poly poly;
	private int polyTime;
	
	public long getObjId() {
		return objId;
	}
	public void setObjId(long objId) {
		this.objId = objId;
	}
	public int getGfx() {
		return gfx;
	}
	public void setGfx(int gfx) {
		this.gfx = gfx;
	}
	public Poly getPoly() {
		return poly;
	}
	public void setPoly(Poly poly) {
		this.poly = poly;
	}
	public int getPolyTime() {
		return polyTime;
	}
	public void setPolyTime(int polyTime) {
		this.polyTime = polyTime;
	}
}
