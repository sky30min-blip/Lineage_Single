package lineage.bean.database;

import lineage.world.object.instance.PcShopInstance;

public class marketPrice {
	int x;
	int y;
	int map;
	long objId;
	PcShopInstance shopNpc;
	
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
	public long getObjId() {
		return objId;
	}
	public void setObjId(long objId) {
		this.objId = objId;
	}
	public PcShopInstance getShopNpc() {
		return shopNpc;
	}
	public void setShopNpc(PcShopInstance shopNpc) {
		this.shopNpc = shopNpc;
	}
}
