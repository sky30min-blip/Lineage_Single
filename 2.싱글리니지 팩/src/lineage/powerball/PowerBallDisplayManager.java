package lineage.powerball;

import java.util.List;

import lineage.world.controller.PowerballController;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectName;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

/**
 * 파워볼 전광판/UI 관리
 * - HTML 게임판 생성 (베팅 현황, 버튼, 히스토리)
 * - 맵 전광판: NPC 6개(일반볼 5 + 파워볼 1)로 추첨 번호 상시 표시
 */
public class PowerBallDisplayManager {

    /** 전광판 NPC 6개 (일반볼 5 + 파워볼 1). npc_spawnlist에서 스폰된 일반 NPC를 참조 */
    private object[] displayNpcs = new object[6];

    /** 전광판 배치: 맵 4. DB npc_spawnlist의 name으로 찾음 */
    private static final int DISPLAY_MAP = 4;
    /** 전광판 스폰 이름 (npc_spawnlist.name). DB에 이 이름으로 6개 등록 필요 */
    public static final String DISPLAY_SPAWN_PREFIX = "파워볼전광판_";

    /**
     * 초기화 - 서버 시작 시 호출. npc_spawnlist에 등록된 전광판 NPC 6개를 월드에서 찾아 등록.
     * (일반 NPC로 DB 등록 후 스폰되므로, 여기서는 코드 스폰 없이 기존 스폰만 참조)
     */
    public void initialize() {
        try {
            int found = 0;
            for (int i = 0; i < 6; i++) {
                String key = DISPLAY_SPAWN_PREFIX + (i + 1);
                object o = World.findObjectByDatabaseKey(key);
                if (o != null) {
                    displayNpcs[i] = o;
                    found++;
                } else {
                    displayNpcs[i] = null;
                }
            }
            if (found > 0) {
                System.out.println("[파워볼] 맵 전광판 NPC " + found + "/6 연결됨 (npc_spawnlist에 " + DISPLAY_SPAWN_PREFIX + "1~6 등록 필요)");
            } else {
                System.out.println("[파워볼] 전광판 미사용. 일반볼/파워볼 NPC로 추첨 결과 발표.");
            }
        } catch (Exception e) {
            System.out.println("[파워볼] 전광판 NPC 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 결과 표시 (한꺼번에). 내부용.
     */
    public void displayResult(PowerBallResult result) {
        if (result == null) return;
        for (int i = 0; i < 6; i++) {
            if (displayNpcs[i] != null) {
                int num = i < 5 ? result.getNormalBalls()[i] : result.getPowerBall();
                updateNpcNumber(displayNpcs[i], num);
            }
        }
        broadcastNpcUpdate();
    }

    /**
     * 결과를 왼쪽(1번)부터 1초 간격으로 순차 표시. 6개 다 표시 후 콜백으로 진행자 안내용 메시지 전달.
     * @param result 추첨 결과
     * @param onComplete 6개 표시 완료 후 호출 (진행자 NPC가 최종 결과 알릴 때 사용, null 가능)
     */
    public void displayResultWithDelay(final PowerBallResult result, final Runnable onComplete) {
        if (result == null) return;
        new Thread(() -> {
            try {
                for (int i = 0; i < 6; i++) {
                    if (displayNpcs[i] != null) {
                        int num = i < 5 ? result.getNormalBalls()[i] : result.getPowerBall();
                        updateNpcNumber(displayNpcs[i], num);
                        broadcastSingleNpcUpdate(displayNpcs[i]);
                    }
                    if (i < 5) Thread.sleep(1000);
                }
                broadcastNpcUpdate();
                if (onComplete != null) onComplete.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /** 전광판 결과 숨김 (배팅 마감 30초 진입 시 호출) */
    public void clearDisplay() {
        for (int i = 0; i < 6; i++) {
            if (displayNpcs[i] != null) {
                updateNpcNumber(displayNpcs[i], -1);
            }
        }
        broadcastNpcUpdate();
    }

    private void broadcastSingleNpcUpdate(object npc) {
        if (npc == null || npc.isDead()) return;
        java.util.List<PcInstance> pcs = World.getPcList();
        if (pcs != null) {
            for (PcInstance pc : pcs) {
                if (pc != null && !pc.isDead() && pc.getMap() == DISPLAY_MAP)
                    pc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), npc));
            }
        }
        npc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), npc), false);
    }

    /** 전광판 NPC 이름을 숫자로 갱신 (맵에 표시되는 텍스트). number < 0 이면 숨김(빈칸). */
    private void updateNpcNumber(object npc, int number) {
        if (npc == null) return;
        npc.setName(number < 0 ? "-" : String.valueOf(number));
    }

    /** 전광판 NPC 이름 변경을 같은 맵 유저에게 전송 (S_ObjectName). 맵 4 전원이 회차 결과를 볼 수 있도록. */
    private void broadcastNpcUpdate() {
        java.util.List<PcInstance> pcs = World.getPcList();
        if (pcs != null) {
            for (PcInstance pc : pcs) {
                if (pc == null || pc.isDead() || pc.getMap() != DISPLAY_MAP) continue;
                for (object npc : displayNpcs) {
                    if (npc == null || npc.isDead()) continue;
                    pc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), npc));
                }
            }
        }
        for (object npc : displayNpcs) {
            if (npc == null || npc.isDead()) continue;
            npc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), npc), false);
        }
    }

    /**
     * 애니메이션 - 추첨 중 숫자 롤링 (전광판이 없으면 스킵)
     */
    public void animateDrawing() {
        boolean hasAny = false;
        for (Object o : displayNpcs) { if (o != null) { hasAny = true; break; } }
        if (!hasAny) return;

        new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 6; j++) {
                        if (displayNpcs[j] != null) {
                            int randomNum = j < 5 ? (int) (Math.random() * 28) + 1 : (int) (Math.random() * 10);
                            updateNpcNumber(displayNpcs[j], randomNum);
                        }
                    }
                    broadcastNpcUpdate();
                    Thread.sleep(100);
                }
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 6; j++) {
                        if (displayNpcs[j] != null) {
                            int randomNum = j < 5 ? (int) (Math.random() * 28) + 1 : (int) (Math.random() * 10);
                            updateNpcNumber(displayNpcs[j], randomNum);
                        }
                    }
                    broadcastNpcUpdate();
                    Thread.sleep(300 + i * 100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * HTML 다이얼로그 - 베팅 UI 생성 (기존 NPC HTML에서 사용)
     */
    public String generateGameBoard(PowerBallGame game) {
        if (game == null) return "<html><body><center>오류</center></body></html>";

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<title>파워볼</title>");

        html.append("<center>");
        html.append("<font color=\"LEVEL\">제 ").append(game.getRoundDisplay()).append("회 진행중</font><br>");
        html.append("<font color=\"00ff00\">⏰ 남은시간: ").append(game.getRemainingSeconds()).append("초</font><br>");
        html.append("<br>");

        // 직전 회차 추첨 번호 (채팅 말고 보드에서 확인)
        PowerBallResult last = game.getLastResult();
        if (last != null) {
            html.append("<font color=\"LEVEL\">📌 직전 회차 결과</font><br>");
            int[] balls = last.getNormalBalls();
            html.append("<font color=\"ffffff\">일반볼 ");
            html.append(balls[0]).append(" ").append(balls[1]).append(" ").append(balls[2]).append(" ").append(balls[3]).append(" ").append(balls[4]);
            html.append(" (합:").append(last.getNormalSum()).append(")</font><br>");
            html.append("<font color=\"ffff00\">파워볼 ").append(last.getPowerBall()).append("</font> ");
            html.append("<font color=\"00ff00\">총합 ").append(last.getTotalSum()).append(" (").append(last.isOdd() ? "홀" : "짝").append(" / ").append(last.isTotalUnder() ? "언더" : "오버").append(")</font><br>");
            html.append("<br>");
        }

        int oddBets = game.getOddBetsAmount();
        int evenBets = game.getEvenBetsAmount();
        int total = oddBets + evenBets;
        int oddPercent = total > 0 ? (oddBets * 100 / total) : 50;
        int evenPercent = 100 - oddPercent;

        html.append("<table width=270>");
        html.append("<tr>");
        html.append("<td width=135 align=center bgcolor=\"ff6b6b\"><font color=\"ffffff\">홀 ").append(oddPercent).append("%</font></td>");
        html.append("<td width=135 align=center bgcolor=\"4ecdc4\"><font color=\"ffffff\">짝 ").append(evenPercent).append("%</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center>").append(String.format("%,d", oddBets)).append("</td>");
        html.append("<td align=center>").append(String.format("%,d", evenBets)).append("</td>");
        html.append("</tr></table><br>");

        int underBets = game.getUnderBetsAmount();
        int overBets = game.getOverBetsAmount();
        int uoTotal = underBets + overBets;
        int underPct = uoTotal > 0 ? (underBets * 100 / uoTotal) : 50;
        int overPct = 100 - underPct;
        html.append("<table width=270>");
        html.append("<tr>");
        html.append("<td width=135 align=center bgcolor=\"6c5ce7\"><font color=\"ffffff\">언더 ").append(underPct).append("%</font></td>");
        html.append("<td width=135 align=center bgcolor=\"fdcb6e\"><font color=\"000000\">오버 ").append(overPct).append("%</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center>").append(String.format("%,d", underBets)).append("</td>");
        html.append("<td align=center>").append(String.format("%,d", overBets)).append("</td>");
        html.append("</tr></table><br>");

        html.append("<table width=270>");
        html.append("<tr>");
        html.append("<td width=135 align=center><button value=\"홀 5만\" action=\"bypass powerball_bet odd 50000\" width=120 height=25></td>");
        html.append("<td width=135 align=center><button value=\"짝 5만\" action=\"bypass powerball_bet even 50000\" width=120 height=25></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center><button value=\"홀 50만\" action=\"bypass powerball_bet odd 500000\" width=120 height=25></td>");
        html.append("<td align=center><button value=\"짝 50만\" action=\"bypass powerball_bet even 500000\" width=120 height=25></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center><button value=\"홀 500만\" action=\"bypass powerball_bet odd 5000000\" width=120 height=25></td>");
        html.append("<td align=center><button value=\"짝 500만\" action=\"bypass powerball_bet even 5000000\" width=120 height=25></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center><button value=\"언더 5만\" action=\"bypass powerball_bet under 50000\" width=120 height=25></td>");
        html.append("<td align=center><button value=\"오버 5만\" action=\"bypass powerball_bet over 50000\" width=120 height=25></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center><button value=\"언더 50만\" action=\"bypass powerball_bet under 500000\" width=120 height=25></td>");
        html.append("<td align=center><button value=\"오버 50만\" action=\"bypass powerball_bet over 500000\" width=120 height=25></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td align=center><button value=\"언더 500만\" action=\"bypass powerball_bet under 5000000\" width=120 height=25></td>");
        html.append("<td align=center><button value=\"오버 500만\" action=\"bypass powerball_bet over 5000000\" width=120 height=25></td>");
        html.append("</tr></table><br>");

        html.append("<font color=\"LEVEL\">최근 10회 결과 (홀/짝)</font><br>");
        html.append(generateHistory(game.getRecentResults(10)));
        html.append("<br>");
        html.append("<font color=\"LEVEL\">최근 3회 번호</font><br>");
        html.append(generateRecentNumbers(game.getRecentResults(3)));
        html.append("<br>");
        html.append("<font color=\"LEVEL\">오늘의 통계</font><br>");
        html.append("홀: ").append(game.getTodayOddCount()).append("회 | 짝: ").append(game.getTodayEvenCount()).append("회<br>");

        html.append("</center></body></html>");
        return html.toString();
    }

    private String generateHistory(List<PowerBallResult> results) {
        if (results == null || results.isEmpty())
            return "<font color=\"666666\">결과 없음</font>";

        StringBuilder sb = new StringBuilder();
        sb.append("<table width=270><tr>");
        for (PowerBallResult result : results) {
            String color = result.isOdd() ? "ff6b6b" : "4ecdc4";
            String text = result.isOdd() ? "홀" : "짝";
            sb.append("<td width=27 align=center bgcolor=\"").append(color).append("\">");
            sb.append("<font color=\"ffffff\">").append(text).append("</font></td>");
        }
        sb.append("</tr></table>");
        return sb.toString();
    }

    /** 최근 N회 추첨 번호 요약 (일반볼+파워볼, 총합 홀/짝) */
    private String generateRecentNumbers(List<PowerBallResult> results) {
        if (results == null || results.isEmpty())
            return "<font color=\"666666\">결과 없음</font>";

        StringBuilder sb = new StringBuilder();
        sb.append("<table width=270 border=0>");
        for (PowerBallResult r : results) {
            int[] b = r.getNormalBalls();
            sb.append("<tr><td align=center><font color=\"aaccff\">제").append(PowerballController.getTodayRoundDisplay(r.getRoundNumber())).append("회 </font>");
            sb.append("<font color=\"ffffff\">").append(b[0]).append(" ").append(b[1]).append(" ").append(b[2]).append(" ").append(b[3]).append(" ").append(b[4]);
            sb.append("</font> <font color=\"ffff00\">+").append(r.getPowerBall()).append("</font> ");
            sb.append("<font color=\"00ff00\">= ").append(r.getTotalSum()).append(" (").append(r.isOdd() ? "홀" : "짝").append("·").append(r.isTotalUnder() ? "언더" : "오버").append(")</font></td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
}
