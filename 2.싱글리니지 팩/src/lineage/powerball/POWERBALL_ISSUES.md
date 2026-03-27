# 파워볼 결과 발표·정산 로직 검토 보고서

## 1. 현재 구조 요약

### 1.1 베팅 진입 경로 (두 가지가 공존)

| 경로 | 트리거 | 저장 위치 | 당첨 시 수령 |
|------|--------|-----------|--------------|
| **A. bypass (powerball_bet)** | NPC 대화창에서 "powerball_bet odd 50000" 등 | `PowerBallGame.bets`(메모리) + `powerball_bets`(DB) | `settleRound()`에서 **즉시 아데나 지급** |
| **B. 쿠폰 구매** | NPC 상점에서 홀/짝 쿠폰 선택 → 금액 입력 | `powerball_bets`(DB) + 인벤 **쿠폰 아이템** (itemTimek=회차:금액) | NPC에게 **쿠폰 판매(toSell)** 시 아데나 지급 |

- 동일 회차에 대해 A/B 중 하나만 사용 가능 (둘 다 `hasBetThisRound`로 1인 1회 제한).

### 1.2 결과 발표·정산 흐름

1. **PowerBallScheduler** (메인 루프)  
   - `startNewRound()` → 베팅 마감 시각까지 대기 → `drawResult()` → **`settleRound(result)`** → 전광판·NPC 발표 → 전체 채팅 공지  
2. **settleRound()**  
   - **메모리** `bets.get(currentRound)`만 순회하여 당첨자에게 아데나 지급.  
   - 마지막에 `PowerballDatabase.markBetsProcessed(currentRound)` 호출 → 해당 회차 모든 베팅의 `is_processed = 1` 처리.  
3. **PowerballSettlementThread** (1분 주기)  
   - `PowerballController.doSettlement()` 호출.  
   - `getUnsettledResults()`로 “결과는 있는데 아직 is_processed=0인 베팅이 있는 회차”만 조회 후, 해당 회차에 대해 `markBetsProcessed(roundId)` + 전체 공지만 수행.  
   - **아데나 지급은 하지 않음.**

---

## 2. 발견된 문제점

### 2.1 정산 이중 구조로 인한 혼란

- **즉시 정산(경로 A)**  
  - `settleRound()`가 메모리의 베팅만 보고 당첨자에게 바로 아데나를 준다.  
  - 이때 같이 호출되는 `markBetsProcessed(currentRound)` 때문에, 해당 회차의 **쿠폰 구매자(경로 B) 레코드도** DB에서 `is_processed=1`로 바뀐다.  
  - 따라서 쿠폰 구매자는 “결과 나온 뒤 NPC에게 쿠폰 판매”로만 수령 가능하다.  
- **지연 정산(경로 B)**  
  - `doSettlement()`는 “결과는 있는데 미처리 베팅이 있는 회차”에 대해 `markBetsProcessed`만 하고, **당첨금 지급은 전혀 하지 않는다.**  
  - 공지 메시지(“당첨 쿠폰은 파워볼 NPC에게 팔아 수령하세요”)만 나가므로, 쿠폰 사용자는 반드시 NPC 매입을 해야 한다.  

정리하면, “누가 언제 돈을 주는지”가 경로별로 나뉘어 있고, 코드/주석만으로는 한눈에 들어오지 않음.

### 2.2 쿠폰 여러 장 매입 시 과다 지급 가능성

- **PowerballNpc.toSell()**  
  - 쿠폰 1개당 `getBetByCharRound(charId, roundId)`를 **한 번만** 호출하고,  
    `payout * item_count`를 지급한 뒤 `setClaimed(bet[0])`를 **한 번만** 호출한다.  
  - 1인 1회차 1베팅 제한으로 보통은 쿠폰 1장이지만,  
    만약 같은 회차 쿠폰을 여러 장 갖고 있으면(`item_count > 1`):  
    - 실제 DB에는 해당 회차당 1건만 있으므로  
    - “지급액 = payout * item_count”만큼 주고,  
    - claimed 처리되는 건 1건뿐이라  
    - **같은 회차 쿠폰 2장 이상이면 과다 지급**될 수 있다.  
- 권장: 쿠폰(홀/짝, itemTimek) 판매 시 **item_count는 1로만 허용**하거나,  
  또는 1쿠폰당 1건씩 claimed 처리하도록 로직을 나누는 편이 안전하다.

### 2.3 bypass(경로 A) 사용자만 서버 다운 시 미지급 위험

- 경로 A는 “메모리 베팅 + DB 베팅”이고, 당첨금은 **오직 `settleRound()`**에서만 지급된다.  
- 만약 **추첨 직후 `drawResult()`까지 하고 `settleRound()` 실행 전에 서버가 다운**되면:  
  - 결과는 DB에만 있고,  
  - 메모리는 날아가서 `settleRound()`로 당첨금을 받을 기회가 없으며,  
  - 경로 A 사용자는 **쿠폰이 없어서** NPC 매입으로 수령할 수도 없다.  
- 반면 경로 B(쿠폰)는 DB에 베팅만 있어도, 나중에 `doSettlement()`가 `markBetsProcessed`만 해주면 NPC 매입으로 수령 가능하다.  
- 따라서 **서버 다운 대비 관점에서는 경로 A가 불리한 구조**다.

### 2.4 PowerballDatabase.getCurrentRound()와의 의미 차이

- **PowerballController.getCurrentRound()**  
  - 실제 게임/베팅/결과에 사용되는 “현재 회차 ID”.  
  - `(날짜 기준 인덱스 * 288) + (현재 시각 기준 5분 구간 번호)` 형태로, **시간 기반** 회차 ID.  
- **PowerballDatabase.getCurrentRound()**  
  - `powerball_results`의 `MAX(round_id) + 1` 반환.  
  - “다음에 넣을 결과 회차 번호”에 가깝고, **시간과 무관**.  
- 지금 게임 로직은 전부 Controller의 시간 기반 회차만 쓰고, DB의 getCurrentRound()는 사용하지 않는 것으로 보인다.  
  - 나중에 다른 코드에서 DB 기준 getCurrentRound()를 쓰면 “회차 ID” 의미가 달라져서 버그 소지가 있음.

### 2.5 결과 발표 순서·동기화

- Scheduler에서는  
  - `settleRound(result)`  
  → `displayManager.displayResultWithDelay(...)`  
  → `announcer.announceResult(..., onComplete, host)`  
  → **onComplete** 안에서 `broadcastMessage(resultMessage)`  
  순서로 되어 있어, “정산 → 전광판/연출 → NPC 발표 → 전체 채팅 공지” 순서는 맞다.  
- 다만 `announceResult`가 **별도 스레드**에서 돌고, 메인은 `latch.await(30, TimeUnit.SECONDS)`로 기다리므로,  
  NPC 발표가 30초 안에 끝나지 않으면 타임아웃 후에도 메인 루프가 다음 회차로 진행할 수 있다.  
  (실제 발표 시간이 30초를 넘지 않으면 문제 없음.)

### 2.6 스키마와 코드 불일치 가능성

- **PowerballDatabase**는  
  - `powerball_results` (round_id, total_sum, result_type)  
  - `powerball_bets` (char_id, round_id, bet_amount, pick_type, is_processed, claimed)  
  를 전제로 한다.  
- **schema.sql**에는  
  - `powerball_rounds`,  
  - `powerball_bets` (char_name, bet_type ENUM 등)  
  처럼 테이블/컬럼 이름·타입이 다르게 정의되어 있을 수 있다.  
- 실제 DB가 코드와 맞게 되어 있는지 확인 필요.  
  코드와 다른 스키마를 쓰면 런타임에서 예외나 잘못된 정산이 날 수 있다.

---

## 3. 권장 사항 요약

1. **정산 경로 정리**  
   - “즉시 정산(메모리)” vs “쿠폰 매입 시 정산” 역할을 주석/문서로 명확히 하고,  
   - 가능하면 한쪽(예: 쿠폰만 사용)으로 통일하는 것도 검토.  

2. **쿠폰 매입**  
   - 홀/짝 쿠폰은 **1회차당 1장만** 판매 가능하도록 하고,  
   - toSell에서 **item_count > 1**이면 거부하거나,  
   - 1쿠폰당 1건씩 claimed 처리해 과다 지급을 막기.  

3. **서버 다운 대비 (경로 A)**  
   - 재기동 후 “결과는 있는데 메모리 정산이 안 된 회차”를 찾아,  
     해당 회차의 **DB 베팅만 보고** 당첨자에게 지급하는 “복구 정산” 로직을 두는 것을 권장.  
   - 또는 경로 A를 제거하고 쿠폰(경로 B)만 사용하면, 정산 경로가 하나로 단순해짐.  

4. **회차 ID 정의**  
   - “현재 회차”는 **PowerballController.getCurrentRound()** 한 곳에서만 정의하고,  
   - PowerballDatabase.getCurrentRound()는 사용하지 않거나,  
     이름/주석을 바꿔 “결과 테이블 기준 다음 round_id” 등으로 의미를 분리.  

5. **스키마**  
   - schema.sql을 실제 사용 중인 `powerball_results` / `powerball_bets` 구조에 맞추거나,  
   - 코드를 현재 schema.sql에 맞추어 두면 유지보수 시 혼란이 줄어든다.

---

이 문서는 코드 검토 결과를 정리한 것이며, 위 권장 사항 반영 여부는 운영 방침에 따라 결정하면 된다.
