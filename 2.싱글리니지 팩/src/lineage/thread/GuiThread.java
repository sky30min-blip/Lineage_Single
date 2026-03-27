package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.event.Event;
import lineage.share.Common;

public class GuiThread implements Runnable {

	static private GuiThread instance;
	// 쓰레드동작 여부
	static private boolean running;
	// 처리할 이벤트 목록
	static private List<Event> list;
	// 실제 처리되는 이벤트 목록
	static private List<Event> run;
	// 메모리 재사용을 위해
	static private List<Event> pool;
	
	static public void init(){
		pool = new ArrayList<Event>();
		run = new ArrayList<Event>();
		list = new ArrayList<Event>();
		instance = new GuiThread();
		
		start();
	}

	static public void start(){
		running = true;
		Thread t = new Thread(instance);
		t.setName("GuiThread");
		t.start();
	}

	static public void close(){
		running = false;
		instance = null;
	}
	
	static public void append(Event e){
		synchronized (list) {
			list.add(e);
		}
	}
	
	static public Event getPool(Class<?> c){
		synchronized (pool) {
			Event e = findPool(c);
			if(e!=null)
				pool.remove(e);
			return e;
		}
	}
	
	static private Event findPool(Class<?> c){
		for(Event e : pool){
			if(e.getClass().equals(c))
				return e;
		}
		return null;
	}

	@Override
	public void run(){
		try {
			while(running){	
				Thread.sleep(Common.THREAD_SLEEP);
				// 이벤트 처리요청된거 옴기기
				synchronized (list) {
					if(list.size()>0){
						run.addAll(list);
						list.clear();
					}
				}
				// 실제 이벤트 처리 구간.
				if(run.size()>0){
					for(Event e : run){
						e.init();
						e.close();
					}
					// 재사용을위해 풀에 다시 넣기.
					synchronized (pool) {
						pool.addAll( run );
					}
					run.clear();
				}
			}		
		} catch (Exception e) {
		}
	}

}
