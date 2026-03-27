package lineage.thread;

import lineage.database.BackgroundDatabase;
import lineage.gui.GuiMain;
import lineage.network.LineageServer;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.World;
import lineage.world.controller.NoticeController;
import lineage.world.controller.WorldClearController;

public class ServerThread implements Runnable {
	static private ServerThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	
	static public void init(){
		TimeLine.start("ServerThread..");
		
		thread = new ServerThread();
		start();
				
		TimeLine.end();
	}
	
	static private void start() {
		running = true;
		Thread t = new Thread( thread );
		t.setName(ServerThread.class.toString());
		t.start();
	}
	
	@Override
	public void run() {
		for(;running;){
			try {
				long time = System.currentTimeMillis();
				
				// 핑 체크.
				try { LineageServer.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("로그인 & 핑 체크.");
					lineage.share.System.println(e);
				}

				// gui 관리.
				try {
					if(Common.system_config_console == false){
						GuiMain.display.asyncExec(new Runnable(){
							@Override
							public void run(){
								GuiMain.toTimer(time);
							}
						});
					}
				} catch (Exception e) {
					lineage.share.System.println("gui 관리.");
					lineage.share.System.println(e);
				}

				// 공지사항 관리.
				try { NoticeController.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("공지사항 관리.");
					lineage.share.System.println(e);
				}
				
				// 월드맵 청소 관리.
				if (Lineage.is_world_clean) {
					try { WorldClearController.toTimer(time); } catch (Exception e) {
						lineage.share.System.println("월드맵 청소 관리.");
						lineage.share.System.println(e);
					}
				}
				
				// 월드 시간
				try { World.toTimer(time); } catch (Exception e) {
					lineage.share.System.println("월드 시간");
					lineage.share.System.println(e);
				}

				Thread.sleep(Common.TIMER_SLEEP);
			} catch (Exception e) {
				lineage.share.System.printf("lineage.thread.ServerThread.run()\r\n : %s\r\n", e.toString());
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
