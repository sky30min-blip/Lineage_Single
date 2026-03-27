package lineage.bean.database;

import java.util.HashMap;
import java.util.Map;

public class SpriteFrame {

	private Map<Integer, Integer> list;			// mode넘버값에 해당하는 프레임
	private int gfx;
	
	public SpriteFrame(){
		list = new HashMap<Integer, Integer>();
	}

	public Map<Integer, Integer> getList() {
		return list;
	}

	public int getGfx() {
		return gfx;
	}

	public void setGfx(int gfx) {
		this.gfx = gfx;
	}
}
