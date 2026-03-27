package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.event.Ai;
import lineage.bean.event.DeleteObject;
import lineage.share.Common;
import lineage.share.TimeLine;
import lineage.world.object.object;

public final class AiThread implements Runnable {

	static private AiThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	// 인공지능 처리 목록
	static private List<object> list;
	// 실제 처리되는 인공지능 목록
	static private List<object> run;
	// 제거 처리해야할 목록
	static private List<object> remove;

	/**
	 * 초기화 처리 함수.
	 */
	static public void init() {
		TimeLine.start("AiThread..");
		
		thread = new AiThread();
		run = new ArrayList<object>();
		list = new ArrayList<object>();
		remove = new ArrayList<object>();
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static public void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName( AiThread.class.toString() );
		t.start();
	}
	/**
	 * 인공지능 컨테인스여부.
	 * @param o
	 * @return 
	 */
	static public boolean contains2(object o){
		synchronized (run) {
			return run.contains(o);
		}
	}
	/**
	 * 종료 함수
	 */
	static public void close(){
		running = false;
		thread = null;
	}
	
	/**
	 * 인공지능 활성화할 객체 등록 함수.
	 * @param o
	 */
	static public void append(object o){
		synchronized (list) {
			list.add(o);
		}
	}
	
	@Override
	public void run() {
		for(long time=0 ; running ;){
			try {
				Thread.sleep(Common.THREAD_SLEEP);
				time = System.currentTimeMillis();

				// 등록요청 처리 구간
				synchronized (list) {
					if(list.size() > 0) {
						run.addAll(list);
						list.clear();
					}
				}

				// 인공지능 처리 구간
				for(object o : run) {
					// 인공지능에서 제거해야할 경우.
					if(o.getAiStatus()<0)
						remove.add(o);
					// 인공지능 활성화 시간이 되엇을 경우.
					else if(o.isAi(time))
						AiExecuteThread.append( Ai.clone(null, o, time, 0) );
				}
				
				// 인공지능 제거 처리 구간
				for(object o : remove) {
					run.remove(o);
					AiExecuteThread.append( DeleteObject.clone(null, o) );
				}
				remove.clear();
			} catch (Exception e) {
				lineage.share.System.printf("%s : run()\r\n", AiThread.class.toString());
				lineage.share.System.println(e.toString());
			}
		}
	}
	
	static public int getSize(){
		return list.size() + run.size();
	}
	
}
