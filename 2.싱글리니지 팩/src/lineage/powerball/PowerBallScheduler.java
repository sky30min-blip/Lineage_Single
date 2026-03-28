package lineage.powerball;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lineage.world.object.object;

/**
 * 파워볼 스케줄러
 * - "정각 기준 5분 단위"로 결과 발표 (00, 05, 10, ... 55분)
 * - 각 회차 베팅은 해당 5분 단위 시각까지 진행, 그 시각이 지나면 즉시 추첨/정산 및 NPC 발표
 * - 진행 안내: 파워볼진행자 NPC가 같은 맵 일반채팅 + 호칭으로 표시
 * - 추첨 발표: 일반볼/파워볼 NPC가 일반채팅 + 호칭에 결과 표시 (전체채팅 공지는 사용하지 않음)
 */
public class PowerBallScheduler implements Runnable {

    // 게임 종료(정각 00,05,10...55분) 후 2초 뒤에 일반볼 NPC부터 결과 발표 시작.
    private static final int DELAY_AFTER_CLOSE_MS = 2000;       // 마감 후 2초 뒤 추첨·발표
    private static final int RESULT_DISPLAY_MS = 5000;           // 결과 표시 시간 5초
    /** npc_spawnlist.name: 파워볼진행자(상점·진행 안내) */
    private static final String KEY_PROGRESSOR = "파워볼진행자";

    private PowerBallGame game;
    private PowerBallDisplayManager displayManager;
    private PowerBallAnnouncer announcer;

    public PowerBallScheduler() {
        this.game = PowerBallGame.getInstance();
        this.displayManager = new PowerBallDisplayManager();
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
        displayManager.initialize();

        while (true) {
            try {
                announcer.initialize();
                object host = PowerBallAnnouncer.findProgressorNpc();
                if (host != null && !host.isDead())
                    PowerBallAnnouncer.setHeading진행자AndBroadcast(host);
                game.startNewRound();
                int roundDisplay = game.getRoundDisplay();
                host = resolveHost(host);
                PowerBallAnnouncer.npcSayAndSetTitle(
                        host,
                        String.format("제%d회 베팅 시작!", roundDisplay),
                        "[제" + roundDisplay + "회 베팅중]"
                );

                // 회차가 실제로 종료될 때까지 대기 (정각 00, 05, 10 ... 55분이 지날 때까지, 초 단위 버림 없이)
                long endMs = game.getBettingEndTimeMs();
                boolean said40SecWarn = false;
                while (System.currentTimeMillis() < endMs) {
                    long nowMs = System.currentTimeMillis();
                    long waitMs = endMs - nowMs;
                    long secRemaining = waitMs / 1000L;
                    // 마감 40초~11초 구간: 한 번만 "약 40초 전" 안내
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
                displayManager.clearDisplay();
                announcer.clearTitles();
                host = resolveHost(host);
                PowerBallAnnouncer.npcSayAndSetTitle(host, String.format("%d차 추첨을 시작합니다.", roundDisplay), "[마감]");

                // 게임 종료 후 2초 뒤에 첫 번째 숫자(일반볼) 발표 (해당 맵만 채팅 표시)
                Thread.sleep(DELAY_AFTER_CLOSE_MS);

                game.setState(PowerBallGame.GameState.DRAWING);
                PowerBallResult result = game.drawResult();

                // 결과가 DB에 반영될 때까지 확인(재시도) 후 발표. 그래야 NPC 발표 직후 유저가 판매 시 "아직 결과 없음"이 나오지 않음.
                int roundId = game.getRound();
                int savedType = lineage.database.PowerballDatabase.getResultForRound(roundId);
                if (savedType < 0) {
                    lineage.database.PowerballDatabase.insertResult(roundId, result.getTotalSum(),
                            result.isOdd() ? 1 : 0, result.getUnderOverResultType());
                    try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    savedType = lineage.database.PowerballDatabase.getResultForRound(roundId);
                }
                if (savedType < 0) {
                    System.err.println("[파워볼] 제" + roundDisplay + "회 결과 DB 저장 실패 - 발표만 진행. 매입 시 환불 처리될 수 있음.");
                }

                final String oddEven = result.isOdd() ? "홀" : "짝";
                String uo = result.isTotalUnder() ? "언더" : "오버";
                System.out.println("[파워볼] 제" + roundDisplay + "회 결과 - 일반볼: " + result.getNormalSum() + " | 파워볼: " + result.getPowerBall() + " | 총합: " + result.getTotalSum() + " (" + oddEven + ", " + uo + ")");

                game.settleRound(result);
                displayManager.displayResultWithDelay(result, null);
                // 일반볼 → 파워볼 → 진행자 순 발표. 모두 같은 맵 채팅만 사용(전체 채팅 없음). 진행자가 종합+오늘 통계 한 번에 발표.
                CountDownLatch latch = new CountDownLatch(1);
                host = resolveHost(host);
                announcer.announceResult(result, roundDisplay, game.getTodayOddCount(), game.getTodayEvenCount(),
                        game.getTodayUnderCount(), game.getTodayOverCount(), () -> latch.countDown(), host);
                latch.await(120, TimeUnit.SECONDS);

                Thread.sleep(RESULT_DISPLAY_MS);
                host = resolveHost(host);
                PowerBallAnnouncer.npcSayAndSetTitle(host, null, "");
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[파워볼] 스케줄러 오류: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
