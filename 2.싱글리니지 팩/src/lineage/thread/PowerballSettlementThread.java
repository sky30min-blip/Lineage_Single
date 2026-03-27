package lineage.thread;

import lineage.world.controller.PowerballController;

/**
 * 파워볼 정산: 1분마다 powerball_results 확인 후 당첨자 지급 및 전체 공지
 */
public class PowerballSettlementThread implements Runnable {

	static public PowerballSettlementThread thread;
	private static volatile boolean running;
	private static final long INTERVAL_MS = 60 * 1000; // 1분

	public static void start() {
		if (thread == null)
			thread = new PowerballSettlementThread();
		running = true;
		Thread t = new Thread(thread);
		t.setName(PowerballSettlementThread.class.getSimpleName());
		t.setDaemon(true);
		t.start();
	}

	public static void close() {
		running = false;
		thread = null;
	}

	@Override
	public void run() {
		while (running) {
			try {
				PowerballController.runPeriodicLogic();
				PowerballController.doSettlement();
			} catch (Exception e) {
				lineage.share.System.printf("%s.run()\r\n%s\r\n", PowerballSettlementThread.class.getSimpleName(), e.toString());
			}
			try {
				Thread.sleep(INTERVAL_MS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}
