package system;

import java.util.concurrent.ConcurrentHashMap;

import lineage.world.object.object;

public class ObjectManager {
	private static ConcurrentHashMap<Long, object> list = new ConcurrentHashMap<Long, object>();
	
	public static object getObject(Long key) {
		object obj = list.get(key);
		if(obj != null) 
			return obj;
		obj = new object();
		list.put(key, obj);
		
		return obj;
	}
	
	public static void removeObject(Long key) {
		list.remove(key);
	}
}
