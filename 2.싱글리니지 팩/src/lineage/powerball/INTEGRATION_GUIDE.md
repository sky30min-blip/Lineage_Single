# L1J 서버 통합 가이드

파워볼 시스템을 기존 L1J 서버에 통합하는 상세 가이드입니다.

## 1단계: 파일 배치

### 소스 파일 복사
```bash
# L1J 서버 루트 디렉토리에서
mkdir -p src/lineage/powerball
cp powerball/*.java src/lineage/powerball/
```

### 디렉토리 구조
```
your-l1j-server/
├── src/
│   └── lineage/
│       └── powerball/
│           ├── PowerBallResult.java
│           ├── PowerBallGame.java
│           ├── PowerBallScheduler.java
│           ├── PowerBallDisplayManager.java
│           └── PowerBallNpc.java
├── db/
│   └── schema.sql
└── config/
    └── powerball.properties
```

## 2단계: 데이터베이스 설정

### MySQL/MariaDB 접속
```bash
mysql -u root -p
```

### 스키마 적용
```sql
USE your_lineage_db;
source /path/to/schema.sql;
```

### 확인
```sql
SHOW TABLES LIKE 'powerball%';
-- 4개 테이블이 표시되어야 함:
-- powerball_rounds
-- powerball_bets
-- powerball_statistics
-- powerball_player_stats
```

## 3단계: 서버 코드 수정

### A. GameServer.java (또는 메인 클래스)

기존 코드:
```java
public class GameServer {
    public static void main(String[] args) {
        // ... 초기화 코드
        
        System.out.println("Server started!");
    }
}
```

수정 후:
```java
import lineage.powerball.PowerBallScheduler;
import lineage.powerball.PowerBallDisplayManager;

public class GameServer {
    public static void main(String[] args) {
        // ... 기존 초기화 코드
        
        // 파워볼 시스템 초기화
        initPowerBallSystem();
        
        System.out.println("Server started!");
    }
    
    private static void initPowerBallSystem() {
        try {
            // 전광판 NPC 초기화
            PowerBallDisplayManager displayManager = new PowerBallDisplayManager();
            displayManager.initialize();
            
            // 스케줄러 시작
            Thread powerBallThread = new Thread(new PowerBallScheduler());
            powerBallThread.setName("PowerBall-Scheduler");
            powerBallThread.setDaemon(true);
            powerBallThread.start();
            
            System.out.println("[PowerBall] System initialized successfully!");
        } catch (Exception e) {
            System.err.println("[PowerBall] Initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### B. NPC 핸들러 등록

#### 방법 1: NPCAction 클래스 수정

기존 `NPCAction.java` 또는 유사 클래스에 추가:

```java
import lineage.powerball.PowerBallNpc;

public class NPCAction {
    public static void handleNpcAction(L1PcInstance pc, L1NpcInstance npc, String action) {
        // 파워볼 NPC 체크
        if (npc.getNpcId() == 파워볼NPC_ID) { // NPC ID 설정 필요
            PowerBallNpc powerBallNpc = new PowerBallNpc();
            powerBallNpc.handleAction(pc, action);
            return;
        }
        
        // ... 기존 NPC 처리 코드
    }
}
```

#### 방법 2: 별도 핸들러 등록

```java
public class NPCHandlerRegistry {
    private static Map<Integer, NPCHandler> handlers = new HashMap<>();
    
    static {
        // 파워볼 NPC 등록
        handlers.put(파워볼NPC_ID, new PowerBallNpc());
    }
    
    public static NPCHandler getHandler(int npcId) {
        return handlers.get(npcId);
    }
}
```

### C. DB 연결 구현

`PowerBallGame.java`의 TODO 부분 구현:

```java
import lineage.server.utils.SQLUtil;  // 또는 사용 중인 DB 유틸

private Connection getConnection() throws SQLException {
    // L1J 서버의 DB 연결 방식 사용
    return L1DatabaseFactory.getInstance().getConnection();
}

private void saveResult(PowerBallResult result) {
    Connection con = null;
    PreparedStatement pstm = null;
    
    try {
        con = getConnection();
        
        String sql = "INSERT INTO powerball_rounds " +
                    "(normal_ball_1, normal_ball_2, normal_ball_3, normal_ball_4, normal_ball_5, " +
                    "normal_sum, power_ball, total_sum, is_odd) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        pstm = con.prepareStatement(sql);
        pstm.setInt(1, result.getNormalBalls()[0]);
        pstm.setInt(2, result.getNormalBalls()[1]);
        pstm.setInt(3, result.getNormalBalls()[2]);
        pstm.setInt(4, result.getNormalBalls()[3]);
        pstm.setInt(5, result.getNormalBalls()[4]);
        pstm.setInt(6, result.getNormalSum());
        pstm.setInt(7, result.getPowerBall());
        pstm.setInt(8, result.getTotalSum());
        pstm.setBoolean(9, result.isOdd());
        
        pstm.execute();
    } catch (SQLException e) {
        System.err.println("[PowerBall] DB 저장 실패: " + e.getMessage());
        e.printStackTrace();
    } finally {
        SQLUtil.close(pstm, con);
    }
}
```

## 4단계: NPC 스포너 설정

### A. DB 기반 스포너

```sql
-- spawnlist_npc 테이블에 추가 (테이블명은 서버마다 다를 수 있음)
INSERT INTO spawnlist_npc (
    location, 
    npc_id, 
    count, 
    x, 
    y, 
    map, 
    heading, 
    respawn_delay
) VALUES (
    '파워볼 게임장',
    파워볼NPC_ID,  -- NPC ID 설정
    1,
    33010,  -- X 좌표
    33000,  -- Y 좌표
    777,    -- 맵 ID
    0,
    0
);
```

### B. XML 기반 스포너

```xml
<!-- spawns/powerball.xml -->
<spawns>
    <spawn name="PowerBall NPC">
        <npc id="파워볼NPC_ID" x="33010" y="33000" map="777" heading="0" />
    </spawn>
</spawns>
```

## 5단계: 컴파일 및 테스트

### 컴파일
```bash
# Ant 사용
ant compile

# Maven 사용
mvn clean compile

# Gradle 사용
gradle build
```

### 서버 시작
```bash
./startServer.sh
# 또는
java -jar l1jserver.jar
```

### 로그 확인
```
[PowerBall] System initialized successfully!
[PowerBall] 전광판 NPC 초기화 완료
[PowerBall] 스케줄러 시작!
[PowerBall] 제1회 시작!
```

## 6단계: 테스트

### 기본 기능 테스트

1. **서버 접속**
   - 게임에 접속하여 파워볼 맵으로 이동

2. **NPC 확인**
   - 파워볼 NPC가 보이는지 확인
   - 전광판 NPC 6개 확인

3. **베팅 테스트**
   - NPC 클릭하여 UI 열림 확인
   - 홀/짝 베팅 실행
   - 아데나 차감 확인

4. **추첨 확인**
   - 4분 대기
   - 숫자 롤링 애니메이션 확인
   - 결과 발표 확인
   - 당첨 시 배당금 지급 확인

### DB 확인
```sql
-- 결과 저장 확인
SELECT * FROM powerball_rounds ORDER BY round_id DESC LIMIT 5;

-- 베팅 기록 확인
SELECT * FROM powerball_bets ORDER BY bet_id DESC LIMIT 10;

-- 통계 확인
SELECT * FROM powerball_statistics WHERE stat_date = CURDATE();
```

## 7단계: 문제 해결

### 컴파일 에러

**에러**: `cannot find symbol: class L1PcInstance`  
**해결**: L1J 버전에 맞는 패키지 경로 확인 및 수정

**에러**: `S_SystemMessage does not exist`  
**해결**: 사용 중인 서버의 패킷 클래스명 확인

### 런타임 에러

**에러**: `NullPointerException at PowerBallGame.placeBet`  
**해결**: 
```java
// bets 맵 초기화 확인
if (!bets.containsKey(currentRound)) {
    bets.put(currentRound, new ArrayList<>());
}
```

**에러**: `SQLException: Table doesn't exist`  
**해결**: schema.sql 재실행

### NPC 미표시

**확인 사항**:
1. NPC ID가 올바르게 설정되었는지
2. 좌표가 맵 범위 내에 있는지
3. 맵 ID가 존재하는지
4. NPC 그래픽 ID가 유효한지

## 8단계: 최적화 (선택사항)

### 성능 개선

```java
// 베팅 조회 캐싱
private Map<Integer, BetStats> betStatsCache = new ConcurrentHashMap<>();

public int getOddBetsAmount() {
    return betStatsCache
        .computeIfAbsent(currentRound, this::calculateBetStats)
        .oddAmount;
}
```

### 메모리 관리

```java
// 오래된 베팅 기록 제거
if (bets.size() > 100) {
    int oldestRound = currentRound - 100;
    bets.remove(oldestRound);
}
```

## 9단계: 모니터링

### GM 명령어 추가 (선택사항)

```java
public class PowerBallCommand {
    public void execute(L1PcInstance gm, String command, String args) {
        if (command.equals(".파워볼상태")) {
            PowerBallGame game = PowerBallGame.getInstance();
            gm.sendPackets(new S_SystemMessage(
                String.format("회차: %d | 상태: %s | 베팅: %d건",
                    game.getRound(),
                    game.getState(),
                    game.getBetCount()
                )
            ));
        }
    }
}
```

## 10단계: 백업

### 정기 백업 설정

```bash
# crontab 설정
0 0 * * * mysqldump -u root -p your_db powerball_rounds > /backup/powerball_$(date +\%Y\%m\%d).sql
```

## 완료!

모든 단계를 완료하면 파워볼 시스템이 정상 작동합니다.

추가 문의사항은 README.md 파일을 참고하세요.
