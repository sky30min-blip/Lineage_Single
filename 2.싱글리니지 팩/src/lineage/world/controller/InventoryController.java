package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Inventory;
import lineage.share.TimeLine;
import lineage.world.object.Character;

public final class InventoryController {

	static private Map<Character, Inventory> list;
	static private List<Inventory> pool;

	/**
	 * 초기화 함수.
	 */
	static public void init() {
		TimeLine.start("InventoryController..");

		pool = new ArrayList<Inventory>();
		list = new HashMap<Character, Inventory>();

		TimeLine.end();
	}

	/**
	 * 월드에서 들어올때 호출되는 함수 : 매개변수를 PcInstance로 하려 했으나 몬스터도 이것을 이용할 것으로 판단되므로 포괄적으로
	 * 잡기위해.
	 * 
	 * @param cha
	 */
	static public void toWorldJoin(Character cha) {
		Inventory inv = find(cha);
		// 생성된게 없을경우 생성해서 관리목록에 추가.
		if (inv == null) {
			inv = getPool().clone(cha);
			synchronized (list) {
				list.put(cha, inv);
			}
		}

		cha.setInventory(inv);
	}

	/**
	 * 월드에서 나갈때 호출되는 함수
	 * 
	 * @param cha
	 */
	static public void toWorldOut(Character cha) {
		// 관리되고있는 자료구조 찾기.
		Inventory inv = find(cha);
		if (inv == null)
			inv = cha.getInventory();
		if (inv != null) {
			// 목록에서 제거.
			synchronized (list) {
				list.remove(cha);
			}

			if (!FishingController.toWorldOut(cha))
				// 풀에 다시 넣기.
				setPool(inv);
		}

		cha.setInventory(null);
	}

	/**
	 * 사용자와 연결된 객체 찾아서 리턴.
	 * 
	 * @param pc
	 * @return
	 */
	static public Inventory find(Character cha) {
		synchronized (list) {
			return list.get(cha);
		}
	}

	/**
	 * 풀에 있는 Inventory 객체 하나 꺼내서 리턴. 더이상 없을경우 새로 할당해서 리턴.
	 * 
	 * @return
	 */
	static private Inventory getPool() {
		Inventory i = null;
		synchronized (pool) {
			if (pool.size() > 0) {
				i = pool.get(0);
				pool.remove(0);
			} else {
				i = new Inventory();
			}
		}

		// lineage.share.System.println("remove : "+pool.size());
		return i;
	}

	/**
	 * 풀에 사용한 Book클레스 넣는 함수.
	 * 
	 * @param b
	 */
	static private void setPool(Inventory i) {
		i.close();
		synchronized (pool) {
			if (!pool.contains(i))
				pool.add(i);
		}

		// lineage.share.System.println("append : "+pool.size());
	}

	static public int getPoolSize() {
		return pool.size();
	}
}
