# 리니지 프리서버 파워볼 미니게임 시스템

리니지 1 프리서버(L1J)에 파워볼 홀짝 게임을 추가하는 완전한 구현입니다.

## 📋 주요 기능

### 게임 시스템
- **추첨 방식**: 일반볼 5개(1~28) + 파워볼 1개(0~9)
- **게임 주기**: 5분 (베팅 4분 + 대기 30초 + 추첨/정산 30초)
- **베팅 옵션**: 홀/짝 (배당률 1.95배)
- **최소/최대 베팅**: 1,000 ~ 100,000 아데나

### 시각적 요소
- **전광판 NPC**: 맵에 6개 NPC 배치하여 숫자 표시
- **HTML 다이얼로그**: 베팅 현황, 히스토리, 통계 표시
- **화면 메시지**: 결과를 화면 중앙에 크게 표시
- **애니메이션**: 추첨 시 숫자 롤링 효과

### 데이터베이스
- 회차별 결과 저장
- 베팅 기록 관리
- 일일/플레이어 통계

## 📦 파일 구조

```
powerball/
├── PowerBallResult.java          # 추첨 결과 데이터 클래스
├── PowerBallGame.java             # 게임 매니저 (싱글톤)
├── PowerBallScheduler.java        # 5분 주기 스케줄러
├── PowerBallDisplayManager.java   # 전광판/UI 관리
├── PowerBallNpc.java              # NPC 핸들러
├── schema.sql                     # 데이터베이스 스키마
└── README.md                      # 이 파일
```

## 🚀 설치 방법

### 1. 데이터베이스 설정

```sql
-- MySQL/MariaDB에서 실행
mysql -u root -p your_database < schema.sql
```

### 2. 소스 코드 통합

```bash
# L1J 서버 소스 디렉토리에 복사
cp powerball/*.java /your/l1j/server/src/lineage/powerball/
```

### 3. 서버 시작 코드 수정

`GameServer.java` 또는 메인 클래스에 추가:

```java
public class GameServer {
    public static void main(String[] args) {
        // ... 기존 초기화 코드
        
        // 파워볼 전광판 초기화
        PowerBallDisplayManager displayManager = new PowerBallDisplayManager();
        displayManager.initialize();
        
        // 파워볼 스케줄러 시작
        Thread powerBallThread = new Thread(new PowerBallScheduler());
        powerBallThread.setDaemon(true);
        powerBallThread.start();
        
        System.out.println("파워볼 게임 시스템 시작!");
    }
}
```

### 4. NPC 설정

NPC 데이터베이스 또는 스포너에 파워볼 NPC 추가:

```sql
-- NPC 추가 예시 (테이블 구조는 서버마다 다를 수 있음)
INSERT INTO spawnlist_npc (location, npc_id, x, y, map_id)
VALUES ('파워볼 게임장', 파워볼NPC_ID, 33010, 33000, 777);
```

### 5. 그래픽 리소스 (선택사항)

전광판용 NPC 그래픽이 필요한 경우:
- 70000: 노란 구체 (일반볼)
- 70001: 빨간 구체 (파워볼 홀)
- 70002: 파란 구체 (파워볼 짝)

클라이언트 `.spr` 파일에 추가하거나 기존 그래픽 ID 재사용

## 🎮 사용 방법

### 플레이어 관점

1. **파워볼 NPC 찾기**: 게임장 맵으로 이동
2. **NPC 클릭**: 베팅 UI 열림
3. **베팅 선택**: 홀/짝 선택 및 금액 설정
4. **결과 확인**: 추첨 후 당첨 시 배당금 자동 지급

### 관리자 명령어 (추가 구현 필요)

```java
// 예시: GM 명령어 추가
.파워볼상태        // 현재 게임 상태 확인
.파워볼통계        // 오늘의 통계 조회
.파워볼회차 [번호]  // 특정 회차 결과 조회
```

## ⚙️ 설정 커스터마이징

### 게임 주기 변경

`PowerBallScheduler.java`:

```java
private static final int BETTING_DURATION = 4 * 60 * 1000;  // 베팅 시간
private static final int WAITING_DURATION = 30 * 1000;       // 대기 시간
private static final int DRAW_DURATION = 30 * 1000;          // 추첨 시간
```

### 베팅 한도 변경

`PowerBallGame.java`:

```java
public boolean placeBet(...) {
    if (amount < 1000) {  // 최소 베팅
        // ...
    }
    if (amount > 100000) {  // 최대 베팅
        // ...
    }
}
```

### 배당률 변경

`PowerBallGame.java`:

```java
public void settleRound(PowerBallResult result) {
    // ...
    long winAmount = (long)(bet.amount * 1.95);  // 배당률 수정
}
```

### 전광판 위치 변경

`PowerBallDisplayManager.java`:

```java
private static final int MAP_ID = 777;  // 맵 ID 변경
private static final L1Location[] BALL_POSITIONS = {
    new L1Location(33000, 33000, MAP_ID),  // 좌표 수정
    // ...
};
```

## 🔧 문제 해결

### 컴파일 에러

**문제**: L1J 버전별 API 차이  
**해결**: 패킷 클래스명, 메서드명을 사용 중인 L1J 버전에 맞게 수정

예시:
```java
// 서버마다 다를 수 있음
player.sendPackets(new S_SystemMessage(...));  // 또는
player.sendMessage(...);  // 또는
player.sendChatPacket(...);
```

### NPC가 보이지 않음

**문제**: NPC 소환 코드 미구현  
**해결**: `PowerBallDisplayManager.spawnDisplayNPC()` 메서드 구현

```java
private L1NpcInstance spawnDisplayNPC(int gfxId, L1Location loc, String name) {
    L1NpcInstance npc = new L1NpcInstance(
        // 사용 중인 L1J 버전의 NPC 생성자 사용
    );
    npc.setX(loc.getX());
    npc.setY(loc.getY());
    npc.setMap(loc.getMapId());
    npc.setNameId(name);
    npc.setTempCharGfx(gfxId);
    
    L1World.getInstance().storeObject(npc);
    L1World.getInstance().addVisibleObject(npc);
    
    return npc;
}
```

### HTML 다이얼로그가 안 보임

**문제**: 패킷 클래스명 차이  
**해결**: `S_NPCTalkReturn` 대신 사용 중인 서버의 HTML 패킷 사용

### 데이터베이스 연결 안됨

**문제**: DB 접속 정보 미설정  
**해결**: `PowerBallGame.java`의 DB 메서드 구현

```java
private Connection getConnection() throws SQLException {
    // L1J 서버의 DB 연결 풀 사용
    return L1DatabaseFactory.getInstance().getConnection();
}

private void saveResult(PowerBallResult result) {
    try (Connection con = getConnection();
         PreparedStatement pstm = con.prepareStatement(
             "INSERT INTO powerball_rounds (...) VALUES (...)")) {
        
        pstm.setInt(1, result.getNormalBalls()[0]);
        // ... 나머지 필드
        pstm.execute();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

## 📊 통계 조회 쿼리

```sql
-- 오늘의 홀짝 비율
SELECT 
    SUM(CASE WHEN is_odd = 1 THEN 1 ELSE 0 END) as odd_count,
    SUM(CASE WHEN is_odd = 0 THEN 1 ELSE 0 END) as even_count
FROM powerball_rounds
WHERE DATE(created_at) = CURDATE();

-- 가장 많이 나온 파워볼 숫자
SELECT power_ball, COUNT(*) as count
FROM powerball_rounds
GROUP BY power_ball
ORDER BY count DESC;

-- 최고 수익 플레이어 TOP 10
SELECT char_name, profit
FROM powerball_player_stats
ORDER BY profit DESC
LIMIT 10;
```

## 🔐 보안 및 법적 고려사항

### 중요 경고
1. **사행성 주의**: 게임머니 전용으로만 운영
2. **현금 환전 금지**: 법적 문제 발생 가능
3. **청소년 보호**: 연령 제한 고려
4. **저작권**: 리니지 IP 사용 주의

### 권장 사항
- 서버 규칙에 명시
- 게임머니 외부 거래 모니터링
- 로그 보관 (최소 6개월)
- 비정상 베팅 패턴 감지 시스템

## 📝 라이선스

이 코드는 교육 및 학습 목적으로 제공됩니다.  
상업적 사용 시 발생하는 모든 법적 책임은 사용자에게 있습니다.

## 🤝 기여

버그 리포트 및 개선 제안 환영합니다.

## 📧 문의

구현 중 문제가 발생하면 L1J 커뮤니티 포럼에 문의하세요.

---

**제작**: 형민  
**날짜**: 2025-03-17  
**버전**: 1.0.0
