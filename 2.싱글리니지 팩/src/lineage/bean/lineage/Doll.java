package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lineage.world.object.instance.MagicDollInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.MagicDoll;

public class Doll {
	
	private PcInstance pc;
	private java.util.Map<MagicDoll, MagicDollInstance> list;
	
	public Doll() {
		list = new HashMap<MagicDoll, MagicDollInstance>();
		close();
	}
	
	public void close() {
		pc = null;
		synchronized (list) {
			list.clear();
		}
	}
	
	public void clear() {
		synchronized (list) {
			list.clear();
		}
	}
	
	public void setPcInstance(PcInstance pc) {
		this.pc = pc;
	}
	
	public List<MagicDollInstance> getListValue() {
		synchronized (list) {
			return new ArrayList<MagicDollInstance>(list.values());
		}
	}
	
	public List<MagicDoll> getListKey() {
		synchronized (list) {
			return new ArrayList<MagicDoll>(list.keySet());
		}
	}
	
	public int getSize() {
		return list.size();
	}
	
	public boolean isDoll(MagicDoll md) {
		synchronized (list) {
			for(MagicDoll doll : list.keySet()) {
				if(doll.getItem().getName().equalsIgnoreCase(md.getItem().getName()))
					return true;
			}
		}
		return false;
	}
	
	public MagicDollInstance find(MagicDoll md) {
		synchronized (list) {
			return list.get(md);
		}
	}
	
	public MagicDoll find(MagicDollInstance mdi) {
		synchronized (list) {
			for(MagicDoll md : list.keySet()){
				if(list.get(md) == mdi)
					return md;
			}
		}
		return null;
	}
	
	public PcInstance getMaster() {
		return pc;
	}
	
	public void append(MagicDoll md, MagicDollInstance mdi) {
		synchronized (list) {
			list.put(md, mdi);
		}
	}
	
	public void remove(MagicDoll md) {
		synchronized (list) {
			list.remove(md);
		}
	}
	
	public void remove(MagicDollInstance mdi) {
		MagicDoll md = find(mdi);
		if(md != null)
			remove(md);
	}
	
	public void toTimer(long time) {
		synchronized (list) {
			for(MagicDoll md : list.keySet())
				list.get(md).toTimer(time);
		}
	}

}
