package lineage.powerball;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.network.packet.server.S_ObjectName;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.npc.PowerballNpc;

/**
 * 파워볼 추첨 발표: 일반볼/파워볼 NPC가 일반채팅으로 번호를 부르고, 호칭에 결과를 적음.
 * 베팅 마감 시 두 NPC 호칭 비움.
 */
public class PowerBallAnnouncer {

    /** npc_spawnlist.name: 일반볼 NPC (5개 번호 발표) */
    public static final String KEY_NORMAL = "일반볼";
    /** npc_spawnlist.name: 파워볼 NPC (마지막 1개 번호 발표) */
    public static final String KEY_POWER = "파워볼";

    private object normalBallNpc;
    private object powerBallNpc;

    /** 12시 방향 (북쪽, y 감소 쪽) — Util.calcheading 기준 heading 0 */
    private static final int HEADING_북 = 0;
    /** 3시 방향 (동쪽, x 증가 쪽) — Util.calcheading 기준 heading 2 */
    private static final int HEADING_동 = 2;
    /** 진행자 방향 고정값 (요청: 5) */
    private static final int HEADING_진행자 = 5;

    /** 일반볼·파워볼·진행자 등 지정 NPC를 북쪽으로 돌리고 같은 맵에 브로드캐스트 */
    public static void setHeading북AndBroadcast(object npc) {
        if (npc == null || npc.isDead()) return;
        npc.setHeading(HEADING_북);
        int map = npc.getMap();
        for (PcInstance pc : World.getPcList()) {
            if (pc != null && !pc.isDead() && pc.getMap() == map)
                pc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc));
        }
        npc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc), false);
    }

    /** 진행자를 heading 5로 고정하고 주변에 브로드캐스트 */
    public static void setHeading진행자AndBroadcast(object npc) {
        if (npc == null || npc.isDead()) return;
        npc.setHeading(HEADING_진행자);
        int map = npc.getMap();
        for (PcInstance pc : World.getPcList()) {
            if (pc != null && !pc.isDead() && pc.getMap() == map)
                pc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc));
        }
        npc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc), false);
    }

    /** 진행자 방향 고정 — ASCII 별칭(IDE 파서 호환). 실제 동작은 {@link #setHeading진행자AndBroadcast(object)} 와 동일. */
    public static void setProgressorHeadingAndBroadcast(object npc) {
        setHeading진행자AndBroadcast(npc);
    }

    /** 지정 NPC를 동쪽으로 돌리고 같은 맵에 브로드캐스트 */
    public static void setHeading동AndBroadcast(object npc) {
        if (npc == null || npc.isDead()) return;
        npc.setHeading(HEADING_동);
        int map = npc.getMap();
        for (PcInstance pc : World.getPcList()) {
            if (pc != null && !pc.isDead() && pc.getMap() == map)
                pc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc));
        }
        npc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc), false);
    }

    /** 진행자 NPC 검색: DB key 변형까지 순차 시도 */
    public static object findProgressorNpc() {
        // 가장 신뢰도 높은 경로: 실제 파워볼진행자 인스턴스 목록.
        PowerballNpc live = PowerballNpc.findAnyAliveInstance();
        if (live != null)
            return live;

        String[] keys = { "파워볼진행자", "파워볼 진행자", "powerball_1", "PowerBall_1", "powerball_npc" };
        for (String k : keys) {
            object o = World.findObjectByDatabaseKey(k);
            if (o != null && !o.isDead())
                return o;
        }
        // DB name/nameId가 다른 경우를 대비한 타입/이름 스캔
        try {
            java.util.List<object> all = new java.util.ArrayList<object>();
            for (int mapId = 0; mapId <= 25000; mapId++) {
                lineage.bean.lineage.Map m = World.get_map(mapId);
                if (m != null)
                    m.collectAllObjects(all);
            }
            for (object o : all) {
                if (!(o instanceof NpcInstance) || o.isDead())
                    continue;
                NpcInstance ni = (NpcInstance) o;
                if (ni.getNpc() == null)
                    continue;
                String n = ni.getNpc().getName();
                String nid = ni.getNpc().getNameId();
                String t = ni.getNpc().getType();
                if ("파워볼진행자".equalsIgnoreCase(t) || "PowerballNpc".equalsIgnoreCase(t)
                        || "파워볼진행자".equalsIgnoreCase(n) || "파워볼진행자".equalsIgnoreCase(nid))
                    return o;
            }
        } catch (Exception ignore) {}
        return null;
    }

    public void initialize() {
        normalBallNpc = World.findObjectByDatabaseKey(KEY_NORMAL);
        if (normalBallNpc == null) normalBallNpc = World.findObjectByDatabaseKey("powerball_일반볼");
        powerBallNpc = World.findObjectByDatabaseKey(KEY_POWER);
        if (powerBallNpc == null) powerBallNpc = World.findObjectByDatabaseKey("powerball_파워볼");
        if (normalBallNpc != null || powerBallNpc != null) {
            System.out.println("[파워볼] 추첨 발표 NPC 연결됨 - 일반볼:" + (normalBallNpc != null) + " 파워볼:" + (powerBallNpc != null));
        }
        // 게임 내 이름을 "일반볼"/"파워볼"로 표시 (nameid 50999 등 방지), 북쪽 방향 고정
        if (normalBallNpc != null && !normalBallNpc.isDead()) {
            normalBallNpc.setName("일반볼");
            // PowerballNpc는 getName()을 getNpc().getName()으로 오버라이드하므로,
            // 객체 name뿐 아니라 DB Npc bean(name/nameid)도 같이 바꿔야 클라에 반영됩니다.
            if (normalBallNpc instanceof PowerballNpc) {
                PowerballNpc p = (PowerballNpc) normalBallNpc;
                p.getNpc().setName("일반볼");
                p.getNpc().setNameId("일반볼");
            }
            normalBallNpc.setHeading(HEADING_북);
            broadcastNameAndHeading(normalBallNpc);
        }
        if (powerBallNpc != null && !powerBallNpc.isDead()) {
            powerBallNpc.setName("파워볼");
            if (powerBallNpc instanceof PowerballNpc) {
                PowerballNpc p = (PowerballNpc) powerBallNpc;
                p.getNpc().setName("파워볼");
                p.getNpc().setNameId("파워볼");
            }
            // 요청사항: 파워볼 NPC는 동쪽 방향 고정.
            powerBallNpc.setHeading(HEADING_동);
            broadcastNameAndHeading(powerBallNpc);
        }
    }

    /** NPC 이름·방향을 같은 맵 유저에게 전송 */
    private void broadcastNameAndHeading(object npc) {
        if (npc == null || npc.isDead()) return;
        int map = npc.getMap();
        for (PcInstance pc : World.getPcList()) {
            if (pc != null && !pc.isDead() && pc.getMap() == map) {
                pc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), npc));
                pc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc));
            }
        }
        npc.toSender(S_ObjectName.clone(BasePacketPooling.getPool(S_ObjectName.class), npc), false);
        npc.toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), npc), false);
    }

    /** 같은 맵 유저에게만 NPC 일반채팅 전송 */
    private void npcSayToMap(object npc, String msg) {
        if (npc == null || msg == null || npc.isDead()) return;
        // 같은 맵 전체가 아니라 NPC 주변(가시 범위)에만 출력.
        npc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), npc, Lineage.CHATTING_MODE_NORMAL, msg), false);
    }

    /** NPC 호칭 변경 후 같은 맵에 브로드캐스트 */
    private void setNpcTitleAndBroadcast(object npc, String title) {
        if (npc == null || npc.isDead()) return;
        npc.setTitle(title != null ? title : "");
        int map = npc.getMap();
        for (PcInstance pc : World.getPcList()) {
            if (pc != null && !pc.isDead() && pc.getMap() == map)
                pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), npc));
        }
        npc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), npc), false);
    }

    private static String oddEvenLabel(boolean odd) {
        return odd ? "홀" : "짝";
    }

    private static String underOverLabel(boolean under) {
        return under ? "언더" : "오버";
    }

    /** 진행자 멘트 삽입용: [홀, 언더] 형태 (색상 코드 없음 → 클라 기본 흰색). */
    private static String formatResultPair(boolean odd, boolean under) {
        return "[" + oddEvenLabel(odd) + ", " + underOverLabel(under) + "]";
    }

    /**
     * 추첨 결과 발표: 일반볼 NPC가 5개 번호 발표 → 파워볼 NPC 발표 → 진행자가 종합+오늘 통계 (같은 맵 채팅만, 전체 채팅 없음).
     * @param todayOdd 오늘 홀 회차 수
     * @param todayEven 오늘 짝 회차 수
     * @param todayUnder 오늘 언더 회차 수
     * @param todayOver 오늘 오버 회차 수
     */
    public void announceResult(final PowerBallResult result, final int roundDisplay,
            final int todayOdd, final int todayEven, final int todayUnder, final int todayOver,
            final Runnable onComplete, final object progressor) {
        if (result == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        new Thread(() -> {
            try {
                object progress = progressor;
                if (progress == null || progress.isDead())
                    progress = findProgressorNpc();

                int[] normals = result.getNormalBalls();
                int power = result.getPowerBall();
                int total = result.getTotalSum();
                String roundStr = String.format("%03d", roundDisplay);

                // 일반볼 NPC: 5개 숫자 1.5초 간격으로 발표 → 호칭 (해당 맵만)
                if (normalBallNpc != null && !normalBallNpc.isDead()) {
                    for (int i = 0; i < 5; i++) {
                        npcSayToMap(normalBallNpc, String.valueOf(normals[i]));
                        if (i < 4) Thread.sleep(1500);
                    }
                    // 일반볼 NPC 호칭: 숫자만 표시 (홀/짝 제외)
                    String normalTitle = roundStr + "차:" + normals[0] + "," + normals[1] + "," + normals[2] + "," + normals[3] + "," + normals[4];
                    setNpcTitleAndBroadcast(normalBallNpc, normalTitle);
                }

                Thread.sleep(2500);

                // 파워볼 NPC: 파워볼 번호 발표 (해당 맵만), 호칭에 숫자 뒤로 [홀]/[짝] 표시
                if (powerBallNpc != null && !powerBallNpc.isDead()) {
                    setHeading동AndBroadcast(powerBallNpc);
                    npcSayToMap(powerBallNpc, String.valueOf(power));
                    Thread.sleep(500);
                    String powerTitle = roundStr + "차:" + power + ", ["
                            + oddEvenLabel(result.isOdd()) + "] ["
                            + underOverLabel(result.isTotalUnder()) + "]";
                    setNpcTitleAndBroadcast(powerBallNpc, powerTitle);
                }

                // 파워볼 NPC 발표 후 1.5초 뒤 진행자가 종합 결과 발표
                Thread.sleep(1500);

                if (progress != null && !progress.isDead()) {
                    // 요청사항: 진행자 방향은 heading 5 고정.
                    setHeading진행자AndBroadcast(progress);
                    String pair = formatResultPair(result.isOdd(), result.isTotalUnder());
                    String summary = String.format("제%d회차는 %d, %d, %d, %d, %d, %d 로 총 %d %s 입니다. 오늘 홀 %d회 | 짝 %d회 | 언더 %d회 | 오버 %d회",
                        roundDisplay, normals[0], normals[1], normals[2], normals[3], normals[4], power,
                        total, pair, todayOdd, todayEven, todayUnder, todayOver);
                    npcSayToMap(progress, summary);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (onComplete != null) onComplete.run();
            }
        }).start();
    }

    /** 다음 회차 베팅 마감 시점에 호칭 비우기 */
    public void clearTitles() {
        setNpcTitleAndBroadcast(normalBallNpc, "");
        setNpcTitleAndBroadcast(powerBallNpc, "");
    }

    /**
     * 지정 NPC가 같은 맵에 일반채팅 + 호칭 설정 (파워볼진행자 진행 안내용).
     */
    public static void npcSayAndSetTitle(object npc, String chatMessage, String title) {
        if (npc == null || npc.isDead()) return;
        if (npc instanceof PowerballNpc) {
            // 이름 깨짐(??? ?????) 방지를 위해 진행자 이름/NameId를 서버에서 고정.
            PowerballNpc p = (PowerballNpc) npc;
            p.setName("파워볼진행자");
            if (p.getNpc() != null) {
                p.getNpc().setName("파워볼진행자");
                p.getNpc().setNameId("파워볼진행자");
            }
        }
        int map = npc.getMap();
        if (chatMessage != null && !chatMessage.isEmpty())
            // 진행자 멘트는 주변 사용자에게만 표시.
            npc.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), npc, Lineage.CHATTING_MODE_NORMAL, chatMessage), false);
        if (title != null) {
            npc.setTitle(title);
            for (PcInstance pc : World.getPcList()) {
                if (pc != null && !pc.isDead() && pc.getMap() == map)
                    pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), npc));
            }
            npc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), npc), false);
        }
    }
}
