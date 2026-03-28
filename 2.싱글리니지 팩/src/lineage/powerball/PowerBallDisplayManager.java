package lineage.powerball;

import java.util.List;

import lineage.world.controller.PowerballController;

/**
 * 파워볼 NPC 대화창용 HTML(베팅 UI) 생성.
 * (맵 전광판 NPC 6개 표시 기능은 제거됨.)
 */
public class PowerBallDisplayManager {

	/**
	 * HTML 다이얼로그 - 베팅 UI 생성
	 */
	public String generateGameBoard(PowerBallGame game) {
		if (game == null)
			return "<html><body><center>오류</center></body></html>";

		StringBuilder html = new StringBuilder();
		html.append("<html><body>");
		html.append("<title>파워볼</title>");

		html.append("<center>");
		html.append("<font color=\"LEVEL\">제 ").append(game.getRoundDisplay()).append("회 진행중</font><br>");
		html.append("<font color=\"00ff00\">⏰ 남은시간: ").append(game.getRemainingSeconds()).append("초</font><br>");
		html.append("<br>");

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
