# 파워볼 패키지 구현 상태

위 항목 모두 반영된 상태입니다.

---

## 완료된 항목

### 1. DB 연동
- **PowerBallGame**: `loadLastRound()` → 기존 `PowerballDatabase.getCurrentRound()` 기준으로 회차 로드
- **saveResult()**: `PowerballDatabase.insertResult(roundId, totalSum, resultType, underOverType)` 호출
- **placeBet 시**: 홀/짝·언더/오버 **각각** 회차당 1회 (`hasOddEvenBetThisRound` / `hasUnderOverBetThisRound`) 후 `insertBet()`
- **정산 후**: `PowerballDatabase.markBetsProcessed(currentRound)` 호출  
→ 기존 **powerball_results**, **powerball_bets** 테이블 사용 (스키마 추가 없음)

### 2. 기존 파워볼 NPC 연동
- **PowerballNpc.toTalk()** 에서:
  - `action` 이 `powerball_bet ...` 포함 시 → `PowerBallNpcHandler.handleAction(pc, action)` 호출 후 베팅 보드 HTML 다시 표시
  - `action` 이 `powerball_board` 또는 `powerball` 이면 → `PowerBallNpcHandler.getBettingHtml()` 로 보드 HTML 표시
- 버튼 액션 `bypass powerball_bet odd 50000` 등은 핸들러에서 `bypass ` 접두어 제거 후 처리

### 3. 메인 기동
- **Main.java** (서버 기동 시):
  - `PowerBallDisplayManager` 생성 후 `initialize()` 호출
  - `PowerBallScheduler` 를 데몬 스레드로 시작 (`PowerBall-Scheduler`)

### 4. 전광판 NPC
- **선택 구현**: 전광판 NPC(맵에 숫자 표시)는 미구현.  
  `initialize()` / `displayResult()` / `animateDrawing()` 은 전광판 없이도 동작하며, 게임·베팅·정산에는 영향 없음.

---

## 사용 방법

1. **서버 기동**  
   그대로 실행하면 파워볼 스케줄러가 자동 기동 (4분 베팅 → 30초 대기 → 추첨/정산 반복).

2. **베팅 보드 열기**  
   파워볼 NPC 대화창(elmina)에 **파워볼 보드** 링크가 있어야 함.  
   없으면 NPC 스크립트/HTML에 아래 중 하나 추가:
   - `bypass powerball_board`  
   - `bypass powerball`  
   예: `<button value="파워볼 보드" action="bypass powerball_board" width=120 height=25>`

3. **기존 쿠폰 방식**  
   기존처럼 구매/판매(홀·짝 쿠폰, 5만~500만)는 그대로 사용 가능.  
   신규 보드는 5만~500만 아데나, 1.9배, 즉시 정산 방식.

---

## 요약

| 항목           | 상태 |
|----------------|------|
| DB 연동        | 완료 (기존 PowerballDatabase 사용) |
| NPC 연동       | 완료 (powerball_bet / powerball_board 처리) |
| 메인 기동      | 완료 (Main에서 전광판 초기화 + 스케줄러 시작) |
| 전광판 NPC     | 선택 (미구현 시에도 게임 정상 동작) |
