package lineage.thread;

import lineage.share.Common;
import lineage.share.System;
import lineage.world.controller.FishingController;

public class AutoFishThread implements Runnable {
	static private AutoFishThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	
	static public void init(){	
		thread = new AutoFishThread();
		start();
	}
	
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName(AutoFishThread.class.toString());
		t.start();
	}
	
	@Override
	public void run() {
		for(;running;){
			try {
				long time = System.currentTimeMillis();
				
				// 자동낚시 관리.
				try {
					FishingController.toTimer(time);
				} catch (Exception e) {
					lineage.share.System.println("FishingController.toTimer 에러");
					lineage.share.System.println(e);
				}

				Thread.sleep(Common.TIMER_SLEEP);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.AutoFishThread.run()\r\n : %s\r\n", e.toString());
			}
		}		
	}
	
	/**
	 * 쓰레드 종료처리 함수.
	 */
	static public void close() {
		running = false;
		thread = null;
	}
}
