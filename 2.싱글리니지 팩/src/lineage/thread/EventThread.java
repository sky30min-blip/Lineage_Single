package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.Main;
import lineage.bean.event.Event;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;

public final class EventThread implements Runnable {

	static private EventThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	// 메모리 재사용을 위해
	static private List<Event> pool;
	// 처리할 이벤트 목록
	static private List<Event> list;
	// 실제 처리되는 이벤트 목록
	static private List<Event> run;
	
	/**
	 * 초기화 처리 함수.
	 */
	static public void init(){
		TimeLine.start("EventThread..");
		
		pool = new ArrayList<Event>();
		run = new ArrayList<Event>();
		list = new ArrayList<Event>();
		thread = new EventThread();
		start();
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName( EventThread.class.toString() );
		t.start();
	}
	
	/**
	 * 쓰레드 종료처리 함수.
	 */
	static public void close() {
		running = false;
		thread = null;
	}
	
	static public void append(Event e){
		if(!running)
			return;
		
		synchronized (list) {
			list.add(e);
		}
	}
	
	static private void clearPool() {
		TimeLine.start("EventThread 에서 Pool 초과로 메모리 정리 중..");

		// 풀 전체 제거.
		pool.clear();
		// gc 한번 호출.
		System.gc();
		
		TimeLine.end();
	}
	
	static public void setPool(Event e) {
		if(Lineage.pool_eventthread) {
			synchronized (pool) {
				if(Main.running && Util.isPoolAppend(pool)) {
					pool.add(e);
				} else {
					e = null;
					clearPool();
				}
			}
		} else {
			e = null;
		}
	}
	
	/**
	 * 풀에 저장된거 재사용을위해 사용.
	 * 있으면 리턴.
	 * @param c
	 * @return
	 */
	static public Event getPool(Class<?> c){
		if(Lineage.pool_eventthread) {
			synchronized (pool) {
				Event e = findPool(c);
				if(e != null)
					pool.remove(e);
				return e;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * 찾는 객체 꺼내기.
	 *  : 반드시 이것으루 호출하는 코드내에서는 synchronized (pool) 정의되어 있어야 안전하다.
	 * @param c
	 * @return
	 */
	static private Event findPool(Class<?> c){
		for(Event e : pool){
			if(e.getClass().equals(c))
				return e;
		}
		return null;
	}
	
	@Override
	public void run(){
		for(;running;){
			try {
				if(list.size() == 0) {
					Thread.sleep(Common.THREAD_SLEEP);
					continue;
				}

				// 이벤트 처리요청된거 옴기기
				synchronized (list) {
					run.addAll(list);
					list.clear();
				}
				// 실제 이벤트 처리 구간.
				for(Event e : run) {
					try {
						e.init();
					} catch (Exception e2) {
						lineage.share.System.printf("lineage.thread.EventThread.run()\r\n : %s\r\n : %s\r\n", e.toString(), e2.toString());
					}
					e.close();
				}
				if(Lineage.pool_eventthread) {
					// 재사용을위해 풀에 다시 넣기.
					synchronized (pool) {
						if(Main.running)
							pool.addAll( run );
						if(Util.isPoolAppend(pool) == false)
							clearPool();
					}
				}
				//
				run.clear();
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.EventThread.run()\r\n : %s\r\n", e.toString());
			}
		}
	}
	
	static public int getListSize(){
		return list.size();
	}
	
	static public int getRunSize(){
		return run.size();
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
}
