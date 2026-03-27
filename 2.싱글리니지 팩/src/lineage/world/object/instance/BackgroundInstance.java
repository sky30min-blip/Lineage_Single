package lineage.world.object.instance;

import java.util.ArrayList;
import java.util.List;

import lineage.world.object.object;

public class BackgroundInstance extends object {

	private List<object> list; // 백그라운드 주위에 있는 객체들을 관리하기 위한 변수.

	static synchronized public BackgroundInstance clone(BackgroundInstance bi) {
		if (bi == null)
			bi = new BackgroundInstance();
		return bi;
	}

	public BackgroundInstance() {
		list = new ArrayList<object>();

		close();
	}

	public void close() {
		if (list != null) {
			synchronized (list) {
				list.clear();
			}
		}
	}
	
	public List<object> getList() {
		synchronized (list) {
			return new ArrayList<object>(list);
		}
	}
	
	public void appendList(object o) {
		synchronized (list) {
			if (!list.contains(o))
				list.add(o);
		}
	}
	
	public void removeList(object o) {
		synchronized (list) {
			list.remove(o);
		}
	}
	
	public boolean isContainsList(object o) {
		synchronized (list) {
			return list.contains(o);
		}
	}
	
}
