package lineage.powerball;

import java.util.*;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lineage.database.PowerballDatabase;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.PowerballController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

/**
 * 파워볼 게임 매니저 (싱글톤)
 * 베팅 관리, 추첨, 정산. 기존 powerball_results / powerball_bets DB 사용.
 */
public class PowerBallGame {
    private static PowerBallGame instance;

    /** 최소 배팅 5만, 최대 500만 */
    public static final long MIN_BET = 50000L;
    public static final long MAX_BET = 5000000L;
    public static double PAYOUT_RATE = 1.9;

    private int currentRound = 1;                // 현재 회차
    private GameState state = GameState.BETTING; // 게임 상태
    private long bettingEndTime;                 // 베팅 마감 시간

    // 회차별 베팅 목록
    private Map<Integer, List<Bet>> bets = new ConcurrentHashMap<>();

    // 최근 결과 (최대 100회)
    private LinkedList<PowerBallResult> recentResults = new LinkedList<>();

    // 오늘의 통계 (KST 달력일 기준, powerball_daily_counts DB와 동기화 — 재시작 후에도 이어짐)
    private int todayOddCount = 0;
    private int todayEvenCount = 0;
    private int todayUnderCount = 0;
    private int todayOverCount = 0;
    /** 집계에 사용 중인 KST 날짜 — 자정 넘어가면 DB에서 새 날짜 행 로드 */
    private java.sql.Date statsKstDate;

    public enum GameState {
        BETTING,    // 베팅 가능
        CLOSED,     // 베팅 마감 (대기)
        DRAWING     // 추첨 중
    }

    public enum BetType {
        ODD,    // 홀
        EVEN,   // 짝
        UNDER,  // 언더 (총합≤72)
        OVER    // 오버 (총합>72)
    }

    /**
     * 베팅 정보 클래스
     */
    public static class Bet {
        public PcInstance player;
        public BetType type;
        public long amount;
        public long betTime;

        public Bet(PcInstance player, BetType type, long amount) {
            this.player = player;
            this.type = type;
            this.amount = amount;
            this.betTime = System.currentTimeMillis();
        }
    }

    private PowerBallGame() {
        loadLastRound();
        refreshStatsForKstToday();
    }

    /** KST 날짜가 바뀌었으면 DB에서 당일 집계를 다시 읽는다. */
    private void refreshStatsForKstToday() {
        java.sql.Date today = PowerballDatabase.todayKstSqlDate();
        if (statsKstDate != null && today.equals(statsKstDate))
            return;
        statsKstDate = today;
        int[] c = PowerballDatabase.loadDailyCounts(today);
        todayOddCount = c[0];
        todayEvenCount = c[1];
        todayUnderCount = c[2];
        todayOverCount = c[3];
    }

    public static PowerBallGame getInstance() {
        if (instance == null) {
            instance = new PowerBallGame();
        }
        return instance;
    }

    /**
     * 새 회차 시작.
     * - PowerballController 기준 회차 ID(1~288)와 동기화
     * - 베팅 마감 시간은 "다음 5분 단위 정각"으로 설정 (예: 12:00, 12:05, 12:10 ...)
     */
    public void startNewRound() {
        currentRound = PowerballController.getCurrentRound();
        state = GameState.BETTING;

        // 현재 시각 기준으로 다음 5분 단위 정각(00,05,10...55분) 시각을 계산하여 베팅 마감 시간으로 사용 (한국 시간 기준)
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
        cal.setTimeInMillis(now);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int minute = cal.get(Calendar.MINUTE);
        int mod = minute % 5;

        if (mod == 0 && now <= cal.getTimeInMillis()) {
            // 이미 5분 단위 정각에 딱 맞게 진입한 경우: 다음 5분 뒤를 마감 시각으로 사용
            cal.add(Calendar.MINUTE, 5);
        } else {
            // 그 외에는 "다음 5분 단위"로 올림
            cal.add(Calendar.MINUTE, 5 - mod);
        }

        bettingEndTime = cal.getTimeInMillis();
        bets.put(currentRound, new ArrayList<>());

        System.out.println("[파워볼] 제" + getRoundDisplay() + "회 시작! (회차ID " + currentRound + ") 베팅 마감시각: " + new Date(bettingEndTime));
    }

    /**
     * 베팅 접수
     */
    public boolean placeBet(PcInstance player, BetType type, long amount) {
        if (state != GameState.BETTING) {
            ChattingController.toChatting(player, "지금은 베팅할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
            return false;
        }

        if (amount < MIN_BET) {
            ChattingController.toChatting(player, String.format("최소 베팅 금액은 %,d 아데나입니다.", MIN_BET), Lineage.CHATTING_MODE_MESSAGE);
            return false;
        }

        if (amount > MAX_BET) {
            ChattingController.toChatting(player, String.format("최대 베팅 금액은 %,d 아데나입니다.", MAX_BET), Lineage.CHATTING_MODE_MESSAGE);
            return false;
        }

        int charId = (int) player.getObjectId();
        boolean isOddEven = (type == BetType.ODD || type == BetType.EVEN);
        if (isOddEven) {
            if (PowerballDatabase.hasOddEvenBetThisRound(charId, currentRound)) {
                ChattingController.toChatting(player, "[파워볼] 이 회차 홀/짝 베팅은 이미 하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
                return false;
            }
        } else {
            if (PowerballDatabase.hasUnderOverBetThisRound(charId, currentRound)) {
                ChattingController.toChatting(player, "[파워볼] 이 회차 언더/오버 베팅은 이미 하셨습니다.", Lineage.CHATTING_MODE_MESSAGE);
                return false;
            }
        }

        if (!player.getInventory().isAden(amount, true)) {
            ChattingController.toChatting(player, "아데나가 부족합니다.", Lineage.CHATTING_MODE_MESSAGE);
            return false;
        }

        ItemInstance aden = player.getInventory().findAden();
        if (aden != null)
            player.getInventory().count(aden, aden.getCount() - amount, true);

        Bet bet = new Bet(player, type, amount);
        List<Bet> roundBets = bets.get(currentRound);
        if (roundBets == null) {
            roundBets = new ArrayList<>();
            bets.put(currentRound, roundBets);
        }
        roundBets.add(bet);

        int pickType;
        String betTypeStr;
        if (type == BetType.ODD) {
            pickType = 1;
            betTypeStr = "홀";
        } else if (type == BetType.EVEN) {
            pickType = 0;
            betTypeStr = "짝";
        } else if (type == BetType.UNDER) {
            pickType = 2;
            betTypeStr = "언더(합≤72)";
        } else {
            pickType = 3;
            betTypeStr = "오버(합>72)";
        }
        PowerballDatabase.insertBet(charId, currentRound, amount, pickType);

        ChattingController.toChatting(player,
            String.format("[파워볼] %s에 %,d 아데나 베팅 완료!", betTypeStr, amount),
            Lineage.CHATTING_MODE_MESSAGE);

        return true;
    }

    /**
     * 추첨 실행
     */
    public PowerBallResult drawResult() {
        state = GameState.DRAWING;

        PowerBallResult result = new PowerBallResult(currentRound);
        result.draw();

        recentResults.addFirst(result);
        if (recentResults.size() > 100) {
            recentResults.removeLast();
        }

        refreshStatsForKstToday();

        if (result.isOdd()) {
            todayOddCount++;
        } else {
            todayEvenCount++;
        }

        if (result.isTotalUnder()) {
            todayUnderCount++;
        } else {
            todayOverCount++;
        }

        saveResult(result);
        PowerballDatabase.saveDailyCounts(statsKstDate, todayOddCount, todayEvenCount, todayUnderCount, todayOverCount);

        System.out.println("[파워볼] " + result.toString());

        return result;
    }

    /**
     * 정산 처리
     */
    public void settleRound(PowerBallResult result) {
        List<Bet> roundBets = bets.get(currentRound);
        if (roundBets == null || roundBets.isEmpty()) {
            System.out.println("[파워볼] 베팅이 없습니다.");
            return;
        }

        boolean isOdd = result.isOdd();
        boolean isUnder = result.isTotalUnder();
        int winnerCount = 0;
        long totalPayout = 0;

        for (Bet bet : roundBets) {
            boolean isWin;
            if (bet.type == BetType.ODD)
                isWin = isOdd;
            else if (bet.type == BetType.EVEN)
                isWin = !isOdd;
            else if (bet.type == BetType.UNDER)
                isWin = isUnder;
            else
                isWin = !isUnder;

            if (isWin) {
                PAYOUT_RATE = Lineage.powerball_payout_rate;
                long winAmount = Math.round(bet.amount * PAYOUT_RATE); // 배당률 반영
                ItemInstance aden = bet.player.getInventory().findAden();
                if (aden != null)
                    bet.player.getInventory().count(aden, aden.getCount() + winAmount, true);

                ChattingController.toChatting(bet.player,
                    String.format("[파워볼] 당첨! +%,d 아데나", winAmount),
                    Lineage.CHATTING_MODE_MESSAGE);

                winnerCount++;
                totalPayout += winAmount;
            } else {
                ChattingController.toChatting(bet.player, "[파워볼] 낙첨...", Lineage.CHATTING_MODE_MESSAGE);
            }

            saveBet(bet, isWin ? Math.round(bet.amount * PAYOUT_RATE) : 0);
        }

        PowerballDatabase.markBetsProcessed(currentRound);

        System.out.println(String.format(
            "[파워볼] 정산 완료 - 총 베팅: %d건, 당첨: %d건, 지급액: %,d",
            roundBets.size(), winnerCount, totalPayout
        ));
    }

    public int getOddBetsAmount() {
        List<Bet> roundBets = bets.get(currentRound);
        if (roundBets == null) return 0;

        return (int) roundBets.stream()
            .filter(b -> b.type == BetType.ODD)
            .mapToLong(b -> b.amount)
            .sum();
    }

    public int getEvenBetsAmount() {
        List<Bet> roundBets = bets.get(currentRound);
        if (roundBets == null) return 0;

        return (int) roundBets.stream()
            .filter(b -> b.type == BetType.EVEN)
            .mapToLong(b -> b.amount)
            .sum();
    }

    public int getUnderBetsAmount() {
        List<Bet> roundBets = bets.get(currentRound);
        if (roundBets == null) return 0;
        return (int) roundBets.stream()
            .filter(b -> b.type == BetType.UNDER)
            .mapToLong(b -> b.amount)
            .sum();
    }

    public int getOverBetsAmount() {
        List<Bet> roundBets = bets.get(currentRound);
        if (roundBets == null) return 0;
        return (int) roundBets.stream()
            .filter(b -> b.type == BetType.OVER)
            .mapToLong(b -> b.amount)
            .sum();
    }

    /** 마감 시각(epoch ms). 스케줄러에서 이 시각이 지날 때까지 대기할 때 사용 */
    public long getBettingEndTimeMs() {
        return bettingEndTime;
    }

    public int getRemainingSeconds() {
        if (state != GameState.BETTING) return 0;
        long remaining = bettingEndTime - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000);
    }

    public List<PowerBallResult> getRecentResults(int count) {
        return recentResults.stream()
            .limit(count)
            .collect(Collectors.toList());
    }

    /** DB/배팅용 회차 ID (시간 기준, 기존 시스템과 동일) */
    public int getRound() { return currentRound; }
    /** 표시용 오늘의 N회차 (1~288). NPC·공지·HTML용 */
    public int getRoundDisplay() { return PowerballController.getTodayRoundDisplay(currentRound); }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getTodayOddCount() { return todayOddCount; }
    public int getTodayEvenCount() { return todayEvenCount; }
    public int getTodayUnderCount() { return todayUnderCount; }
    public int getTodayOverCount() { return todayOverCount; }
    public PowerBallResult getLastResult() {
        return recentResults.isEmpty() ? null : recentResults.getFirst();
    }

    // ========== 데이터베이스 (기존 powerball_results / powerball_bets) ==========

    private void loadLastRound() {
        currentRound = PowerballController.getCurrentRound();
    }

    private void saveResult(PowerBallResult result) {
        int resultType = result.isOdd() ? 1 : 0;
        PowerballDatabase.insertResult(currentRound, result.getTotalSum(), resultType, result.getUnderOverResultType());
    }

    private void saveBet(Bet bet, long winAmount) {
        // 베팅은 placeBet 시점에 이미 insertBet 로 저장됨. 정산은 markBetsProcessed 로 처리.
    }
}
