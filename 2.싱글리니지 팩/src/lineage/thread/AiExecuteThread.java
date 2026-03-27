package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.event.Event;
import lineage.share.Common;
import lineage.share.TimeLine;

public final class AiExecuteThread implements Runnable {

	static private AiExecuteThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	// 처리할 이벤트 목록
	static private List<Event> list;
	// 실제 처리되는 이벤트 목록
	static private List<Event> run;
	
	/**
	 * 초기화 처리 함수.
	 */
	static public void init(){
		TimeLine.start("AiExecuteThread..");

		run = new ArrayList<Event>();
		list = new ArrayList<Event>();
		thread = new AiExecuteThread();
		start();
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName( AiExecuteThread.class.toString() );
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
						lineage.share.System.printf("lineage.thread.AiExecuteThread.run()\r\n : %s\r\n : %s\r\n", e.toString(), e2.toString());
					}
					e.close();
				}
				//
				run.clear();
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.AiExecuteThread.run()\r\n : %s\r\n", e.toString());
			}
		}
	}
	
	static public int getListSize(){
		return list.size();
	}
	
	static public int getRunSize(){
		return run.size();
	}	
}
