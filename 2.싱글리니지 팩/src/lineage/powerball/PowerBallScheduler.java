package lineage.powerball;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lineage.world.object.object;

/** Powerball round scheduler (5-minute KST-aligned rounds). */
public class PowerBallScheduler implements Runnable {

	private static final int DELAY_AFTER_CLOSE_MS = 2000;
	private static final int RESULT_DISPLAY_MS = 5000;

	private PowerBallGame game;
	private PowerBallAnnouncer announcer;

	public PowerBallScheduler() {
		this.game = PowerBallGame.getInstance();
		this.announcer = new PowerBallAnnouncer();
	}

	private object resolveHost(object host) {
		if (host == null || host.isDead())
			return PowerBallAnnouncer.findProgressorNpc();
		return host;
	}

	@Override
	public void run() {
		System.out.println("[파워볼] 스케줄러 시작!");
		while (true) {
			try {
				runOneCycle();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("[파워볼] 스케줄러 오류: " + e.getMessage());
				e.printStackTrace();
				break;
			}
		}
	}

	/** One betting round: wait, close, draw, announce. */
	private void runOneCycle() throws InterruptedException {
		announcer.initialize();
		object host = PowerBallAnnouncer.findProgressorNpc();
		if (host != null && !host.isDead())
			PowerBallAnnouncer.setProgressorHeadingAndBroadcast(host);
		game.startNewRound();
		int roundDisplay = game.getRoundDisplay();
		host = resolveHost(host);
		PowerBallAnnouncer.npcSayAndSetTitle(
				host,
				String.format("제%d회 베팅 시작!", roundDisplay),
				"[제" + roundDisplay + "회 베팅중]"
		);

		long endMs = game.getBettingEndTimeMs();
		boolean said40SecWarn = false;
		while (System.currentTimeMillis() < endMs) {
			long nowMs = System.currentTimeMillis();
			long waitMs = endMs - nowMs;
			long secRemaining = waitMs / 1000L;
			if (!said40SecWarn && secRemaining <= 40 && secRemaining > 10) {
				said40SecWarn = true;
				host = resolveHost(host);
				PowerBallAnnouncer.npcSayAndSetTitle(host, "배팅마감 10초 남았습니다.", null);
			}
			if (waitMs > 1000L)
				Thread.sleep(1000);
			else if (waitMs > 0)
				Thread.sleep(waitMs);
			else
				break;
		}

		game.setState(PowerBallGame.GameState.CLOSED);
		announcer.clearTitles();
		host = resolveHost(host);
		PowerBallAnnouncer.npcSayAndSetTitle(host, String.format("%d차 추첨을 시작합니다.", roundDisplay), "[마감]");

		Thread.sleep(DELAY_AFTER_CLOSE_MS);

		game.setState(PowerBallGame.GameState.DRAWING);
		PowerBallResult result = game.drawResult();

		int roundId = game.getRound();
		int savedType = lineage.database.PowerballDatabase.getResultForRound(roundId);
		if (savedType < 0) {
			lineage.database.PowerballDatabase.insertResult(roundId, result.getTotalSum(),
					result.isOdd() ? 1 : 0, result.getUnderOverResultType());
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			savedType = lineage.database.PowerballDatabase.getResultForRound(roundId);
		}
		if (savedType < 0) {
			System.err.println("[파워볼] 제" + roundDisplay + "회 결과 DB 저장 실패 - 발표만 진행. 매입 시 환불 처리될 수 있음.");
		}

		final String oddEven = result.isOdd() ? "홀" : "짝";
		String uo = result.isTotalUnder() ? "언더" : "오버";
		System.out.println("[파워볼] 제" + roundDisplay + "회 결과 - 일반볼: " + result.getNormalSum() + " | 파워볼: " + result.getPowerBall() + " | 총합: " + result.getTotalSum() + " (" + oddEven + ", " + uo + ")");

		game.settleRound(result);
		final CountDownLatch latch = new CountDownLatch(1);
		host = resolveHost(host);
		final Runnable onAnnounceDone = () -> latch.countDown();
		announcer.announceResult(result, roundDisplay, game.getTodayOddCount(), game.getTodayEvenCount(), game.getTodayUnderCount(), game.getTodayOverCount(), onAnnounceDone, host);
		latch.await(120, TimeUnit.SECONDS);

		Thread.sleep(RESULT_DISPLAY_MS);
		host = resolveHost(host);
		PowerBallAnnouncer.npcSayAndSetTitle(host, null, "");
		Thread.sleep(2000);
	}

}
