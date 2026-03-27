package lineage.world.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lineage.bean.database.TeamBattleTime;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.ServerDatabase;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class DollRaceController2 {
    static private Calendar calendar;
    public static boolean isOpen;
    static private BackgroundInstance raceNPC;
    static private String raceNPCName = "레이싱 안내원";
    static private int currentStage = 0; // 현재 스테이지
    static private int finalStage = 3; // 마지막 스테이지
    static private List<BackgroundInstance> magicCircleList; // 마법진
    static private HashSet<String> usedCoordinates; // 사용된 좌표 목록
    // 게임이 진행 중인지 확인
    public static boolean gameInProgress = false;

    // 보스 소환 작업을 스케줄링할 ScheduledExecutorService 생성
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static public void init() {
        TimeLine.start("자리 쟁탈전 컨트롤러..");
        calendar = Calendar.getInstance();
        isOpen = false;
        magicCircleList = new ArrayList<>();
        usedCoordinates = new HashSet<>();
        TimeLine.end();
    }

    static public void toTimer(long time) {
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        int hour = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds();


        for (TeamBattleTime teamTime : Lineage.bug_list) {

            if (!gameInProgress) {
                if (teamTime.getHour() == hour && teamTime.getMin() == min && sec == 0) {
                    for (PcInstance pc : World.getPcList()) {
                        if (pc.getMap() == 508)
                            ChattingController.toChatting(pc, String.format("[알림] 1분뒤 자리 쟁탈전이 시작 됩니다"), Lineage.CHATTING_MODE_MESSAGE);
                    }
                    currentStage=0;
                    gameInProgress = true;
     
                    break;
                }

            }
        }
        if (currentStage <= finalStage) {

        	
     
        	   
            if (gameInProgress && sec == 50) {
                for (PcInstance pc : World.getPcList()) {
                    if (pc.getMap() == 508)
                        ChattingController.toChatting(pc, String.format("[알림] %d 라운드 10초뒤 자리가 변경 됩니다.", currentStage), Lineage.CHATTING_MODE_MESSAGE);
                        ChattingController.toChatting(pc, String.format("[알림] 최종 승리자가 되어 보스를 쟁취하세요"), Lineage.CHATTING_MODE_MESSAGE);
                }
            }

            // 1분마다 스테이지 변경
            if (gameInProgress && sec == 0) {
                for (PcInstance pc : World.getPcList()) {
                    if (pc.getMap() == 508)
                    	   ChattingController.toChatting(pc, String.format("[알림] %d 라운드가 시작되었습니다.", currentStage), Lineage.CHATTING_MODE_MESSAGE);
                }
              	 currentStage++;
                // 스테이지가 2번째 이상일 때만 유저를 탈락시킴
                if (currentStage > 1) {     
                    for (PcInstance pc : World.getPcList()) {
                        if (pc.getMap() == 508) {
                            int userX = pc.getX();
                            int userY = pc.getY();
                            String userCoordinate = userX + "," + userY;
                            if (!usedCoordinates.contains(userCoordinate)) {
                                pc.toPotal(33431, 32809, 4); 
                            }
                        }
                    }
                }
                removeNPCs();
                generateRandomCoordinates();
            }

        }

        // 마지막 라운드인 경우 처리
        if (currentStage == finalStage && gameInProgress) {
            gameInProgress = false; 
            removeNPCs();



            for (PcInstance pc : World.getPcList()) {
                if (pc.getMap() == 508){
                    ChattingController.toChatting(pc, String.format("[알림] 자리 쟁탈전은 종료되었고 마지막 라운드입니다."), Lineage.CHATTING_MODE_MESSAGE);
                    ChattingController.toChatting(pc, String.format("[알림] 10초뒤 보스가 등장합니다"), Lineage.CHATTING_MODE_MESSAGE);
                }
            }

            scheduler.schedule(() -> {
                MonsterInstance boss = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("데스나이트"));
                boss.setHomeX(32828);
                boss.setHomeY(33148);
                boss.setHomeMap(508);
                boss.setBoss(true);

                AiThread.append(boss);
                BossController.appendBossList(boss);
                boss.toTeleport(boss.getHomeX(), boss.getHomeY(), boss.getHomeMap(), false);
            }, 10, TimeUnit.SECONDS);
        }
    }

    public static void generateRandomCoordinates() {
        HashSet<String> selectedCoordinates = new HashSet<>();
        int count = 0;
        int minX = 32820;
        int minY = 33142;
        int maxX = 32822;
        int maxY = 33159;

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            // Ensure min and max values are positive
            int x = getRandomNumber(Math.min(minX, maxX), Math.max(minX, maxX));
            int y = getRandomNumber(Math.min(minY, maxY), Math.max(minY, maxY));

            // Ensure positive coordinates
            x = Math.abs(x);
            y = Math.abs(y);

            // Update selected coordinates
            String coordinate = x + "," + y;
            while (selectedCoordinates.contains(coordinate)) {
                x = getRandomNumber(Math.min(minX, maxX), Math.max(minX, maxX));
                y = getRandomNumber(Math.min(minY, maxY), Math.max(minY, maxY));
                coordinate = x + "," + y;
            }
            selectedCoordinates.add(coordinate);

            BackgroundInstance npc = new lineage.world.object.npc.background.kuberagameb1();
            npc.setGfx(12938);
            npc.setObjectId(ServerDatabase.nextEtcObjId());

            // Set NPC coordinates
            npc.toTeleport(x, y, 508, false);

            // Add NPC to the list
            magicCircleList.add(npc);

            // Adjust coordinate intervals
            minX += Util.random(2, 3);
            maxX += Util.random(2, 3);
            minY += Util.random(2, 3);
            maxY += Util.random(2, 3);

            count++;
        }

        // Update used coordinates
        usedCoordinates = new HashSet<>(selectedCoordinates);
    }

    // 생성된 기존 NPC 삭제
    public static void removeNPCs() {
        for (BackgroundInstance npc : magicCircleList) {
            npc.clearList(true);
            World.remove(npc);
        }
        magicCircleList.clear(); // 리스트 초기화
        usedCoordinates.clear(); // 사용된 좌표 초기화
    }

    // 범위 내에서 무작위 좌표를 반환하는 메소드
    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}