package lineage.bean.lineage;

import lineage.bean.database.Poly;

public class RobotPoly {
	private Poly poly;
	private String polyClass;
	private boolean isPoly;
	
	public Poly getPoly() {
		return poly;
	}
	public void setPoly(Poly poly) {
		this.poly = poly;
	}
	public String getPolyClass() {
		return polyClass;
	}
	public void setPolyClass(String polyClass) {
		this.polyClass = polyClass;
	}
	public boolean isPoly() {
		return isPoly;
	}
	public void setPoly(boolean isPoly) {
		this.isPoly = isPoly;
	}
}
