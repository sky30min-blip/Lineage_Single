package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.InnKey;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.InnInstance;

public final class InnController {

	static private List<InnKey> pool;
	static private List<InnInstance> list;
	
	static public void init(){
		TimeLine.start("InnController..");
		
		pool = new ArrayList<InnKey>();
		list = new ArrayList<InnInstance>();
		
		TimeLine.end();
	}
	
	static public void toWorldJoin(InnInstance ii){
		synchronized (list) {
			if(!list.contains(ii))
				list.add(ii);
		}
	}
	
	static public void toWorldOut(InnInstance ii){
		synchronized (list) {
			list.remove(ii);
		}
	}
	
	static public void close(){
		synchronized (list) {
			list.clear();
		}
	}
	
	static public InnKey getPool(){
		InnKey ik = null;
		synchronized (pool) {
			if(pool.size()>0){
				ik = pool.get(0);
				pool.remove(0);
			}else{
				ik = new InnKey();
			}
		}
		return ik;
	}
	
	static public void setPool(InnKey ik){
		ik.close();
		synchronized (pool) {
			if(!pool.contains(ik))
				pool.add(ik);
		}
	}
	
	/**
	 * 사용자가 있는 현재 위치와 일치하는 여관을 찾아서 리턴함.
	 *  : 여관 맵일경우 해당 여관지기 리턴.
	 *  : 텔레포트처리 구간내에 이 함수를 호출해서 다른 여관키를 소유하고있는 사용자간에 표현을 막기위해 사용.
	 * @param pc
	 * @return
	 */
	static public InnInstance find(object o){
		synchronized (list) {
			for(InnInstance ii : list){
				if(ii.getRoomMap()==o.getMap() || ii.getHallMap()==o.getMap())
					return ii;
			}
			return null;
		}
	}
	
	/**
	 * 타이머에서 지속적으로 호출됨.
	 * @param time
	 */
	static public void toTimer(long time, List<Object> temp){
		synchronized (list) {
			temp.clear();
			temp.addAll( list );
		}
		for(Object o : temp)
			((InnInstance)o).toTimer(time);
	}
	
	/**
	 * 여관맵에 존재하는지 확인해주는 함수.
	 * @param o
	 * @return
	 */
	static public boolean isInnMap(object o){
		return 
		// room
		o.getMap()==16384 || o.getMap()==17408 || o.getMap()==19456 || o.getMap()==20480 || o.getMap()==21504 || o.getMap()==22528 || o.getMap()==23552 ||
		// hall
		o.getMap()==16896 || o.getMap()==17920 || o.getMap()==19968 || o.getMap()==20992 || o.getMap()==22016 || o.getMap()==23040 || o.getMap()==24064;
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
