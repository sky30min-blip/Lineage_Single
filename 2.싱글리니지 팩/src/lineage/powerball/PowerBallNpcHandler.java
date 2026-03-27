package lineage.powerball;

import lineage.world.object.instance.PcInstance;

/**
 * 파워볼 NPC 액션 핸들러
 * 기존 lineage.world.object.npc.PowerballNpc 에서 bypass "powerball_bet odd 50000" 등 처리 시 이 클래스 사용.
 * (L1J의 PowerBallNpc extends L1NpcInstance 대신, 현재 서버는 ShopInstance 기반 PowerballNpc + 이 핸들러 조합)
 */
public class PowerBallNpcHandler {

    private static final PowerBallGame game = PowerBallGame.getInstance();
    private static final PowerBallDisplayManager displayManager = new PowerBallDisplayManager();

    /**
     * 베팅 UI HTML 문자열 반환 (NPC 대화창에 넣을 때 사용)
     */
    public static String getBettingHtml() {
        return displayManager.generateGameBoard(game);
    }

    /**
     * bypass 액션 처리 (예: "powerball_bet odd 50000", "powerball_bet even 500000")
     * 기존 PowerballNpc.toTalk(pc, action, type, cbp) 에서 action 이 powerball_bet 으로 시작하면 이 메서드 호출.
     */
    public static boolean handleAction(PcInstance player, String action) {
        if (player == null || action == null) return false;
        String a = action.trim();
        if (a.toLowerCase().startsWith("bypass ")) a = a.substring(7).trim();
        if (!a.toLowerCase().startsWith("powerball_bet ")) return false;

        String rest = a.substring("powerball_bet ".length()).trim();
        String[] parts = rest.split("\\s+");
        if (parts.length < 2) return false;

        String betTypeStr = parts[0];
        int amount;
        try {
            amount = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        PowerBallGame.BetType betType;
        if (betTypeStr.equalsIgnoreCase("odd")) {
            betType = PowerBallGame.BetType.ODD;
        } else if (betTypeStr.equalsIgnoreCase("even")) {
            betType = PowerBallGame.BetType.EVEN;
        } else if (betTypeStr.equalsIgnoreCase("under")) {
            betType = PowerBallGame.BetType.UNDER;
        } else if (betTypeStr.equalsIgnoreCase("over")) {
            betType = PowerBallGame.BetType.OVER;
        } else {
            return false;
        }

        return game.placeBet(player, betType, amount);
    }
}
