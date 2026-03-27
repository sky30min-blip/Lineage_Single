package lineage.powerball;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 파워볼 추첨 결과를 담는 클래스
 * 일반볼 5개 (1~28) + 파워볼 1개 (0~9)
 */
public class PowerBallResult {
    /** 일반볼 5개 합 + 파워볼 총합 기준: 이하 언더, 초과 오버 */
    public static final int TOTAL_UNDER_OVER_LINE = 72;

    private int[] normalBalls = new int[5];  // 일반볼 5개
    private int powerBall;                    // 파워볼 1개
    private long drawTime;                    // 추첨 시간
    private int roundNumber;                  // 회차 번호

    public PowerBallResult(int roundNumber) {
        this.roundNumber = roundNumber;
        this.drawTime = System.currentTimeMillis();
    }

    /**
     * 추첨 실행
     * 일반볼 5개는 1~28 범위에서 중복 없이 추출
     * 파워볼 1개는 0~9 범위에서 추출
     */
    public void draw() {
        SecureRandom random = new SecureRandom();

        // 1. 일반볼 5개 추첨 (1~28, 중복 없음)
        Set<Integer> selected = new HashSet<>();
        while (selected.size() < 5) {
            selected.add(random.nextInt(28) + 1);
        }

        int i = 0;
        for (int ball : selected) {
            normalBalls[i++] = ball;
        }
        Arrays.sort(normalBalls);  // 오름차순 정렬

        // 2. 파워볼 1개 추첨 (0~9)
        powerBall = random.nextInt(10);
    }

    /**
     * 일반볼 5개 합계
     */
    public int getNormalSum() {
        return Arrays.stream(normalBalls).sum();
    }

    /**
     * 총합 (일반볼 합 + 파워볼)
     */
    public int getTotalSum() {
        return getNormalSum() + powerBall;
    }

    /**
     * 홀짝 판정 (총합 기준)
     */
    public boolean isOdd() {
        return getTotalSum() % 2 == 1;
    }

    /**
     * 파워볼 번호 기준 오버 (0~4 짝쪽, 5~9 홀쪽 등 레거시 표기용)
     */
    public boolean isOver() {
        return powerBall >= 5;
    }

    /** 총합(일반볼 합+파워볼)이 기준 이하이면 언더 */
    public boolean isTotalUnder() {
        return getTotalSum() <= TOTAL_UNDER_OVER_LINE;
    }

    /** DB 저장용: 0=언더(총합≤72), 1=오버(총합>72) */
    public int getUnderOverResultType() {
        return isTotalUnder() ? 0 : 1;
    }

    /**
     * 구간 판정
     * 소: 15~64, 중: 65~80, 대: 81~130
     */
    public String getRange() {
        int sum = getTotalSum();
        if (sum >= 15 && sum <= 64) return "소";
        if (sum >= 65 && sum <= 80) return "중";
        if (sum >= 81 && sum <= 130) return "대";
        return "오류";
    }

    // Getters
    public int[] getNormalBalls() { return normalBalls; }
    public int getPowerBall() { return powerBall; }
    public long getDrawTime() { return drawTime; }
    public int getRoundNumber() { return roundNumber; }

    @Override
    public String toString() {
        return String.format(
            "제%d회 | 일반볼: %d,%d,%d,%d,%d (합:%d) | 파워볼: %d | 총합: %d (%s·%s)",
            roundNumber,
            normalBalls[0], normalBalls[1], normalBalls[2],
            normalBalls[3], normalBalls[4],
            getNormalSum(),
            powerBall,
            getTotalSum(),
            isOdd() ? "홀" : "짝",
            isTotalUnder() ? "언더" : "오버"
        );
    }
}
