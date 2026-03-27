package lineage.thread;

import java.sql.Connection;

import lineage.database.DatabaseConnection;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.controller.CharacterController;
import lineage.world.controller.RobotController;
import lineage.world.object.instance.PcInstance;
public class CharacterThread implements Runnable {
	static private CharacterThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	
	// 캐릭터 저장 시간.
	static long saveTime;
	
	static public void init(){
		TimeLine.start("CharacterThread..");
		
		thread = new CharacterThread();
		start();
				
		TimeLine.end();
	}
	
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName(CharacterThread.class.toString());
		t.start();
	}
	
	@Override
	public void run() {
		for(;running;){
			try {
				long time = System.currentTimeMillis();
				
				// robot 관리.
				try { RobotController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("robot 관리.");
					lineage.share.System.println(e);
				}
		
				// 케릭터 관리.
				try {
					CharacterController.toTimer(time);
				} catch (Exception e) {
					lineage.share.System.println("CharacterController.toTimer 에러");
					lineage.share.System.println(e);
				}


				Thread.sleep(Common.TIMER_SLEEP);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.CharacterThread.run()\r\n : %s\r\n", e.toString());
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
