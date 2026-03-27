package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

public class Friend {
	private long object_id;
	private String name;
	private List<String> list;
	
	public Friend(){
		list = new ArrayList<String>();
	}
	
	public void close(){
		list.clear();
		name = null;
		object_id = 0;
	}

	public long getObjectId() {
		return object_id;
	}

	public void setObjectId(long object_id) {
		this.object_id = object_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void appendList(String name) {
		synchronized (list) {
			list.add( name );
		}
	}
	
	public void removeList(String name) {
		synchronized (list) {
			list.remove( name );
		}
	}
	
	public boolean containsList(String name) {
		synchronized (list) {
			return list.contains( name );
		}
	}
	
	public int sizeList() {
		return list.size();
	}

	public List<String> getList() {
		synchronized (list) {
			return new ArrayList<String>( list );
		}
	}

}
