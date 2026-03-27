# 새 시즌 서버 초기화 체크리스트

`2.싱글리니지 팩/src` 기준으로 **유저(계정·캐릭터) 진행 데이터**를 비워 새 시즌을 시작할 때 참고하는 목록입니다.  
실행 전 **전체 DB 백업(mysqldump 등)** 필수.

---

## 1. 전제 (무엇을 유지할지)

| 유지 | 설명 |
|------|------|
| `accounts` 행 | 로그인용 계정(아이디·비번·uid). **행 삭제하지 않음**이 일반적. |
| 정적 마스터 데이터 | `item`, `npc`, `npc_shop`, `monster`, `monster_spawnlist`, `dungeon` 등 게임 규칙·월드 구성 테이블 |
| 서버 설정성 테이블 | `server`, `server_reload`, `server_notice`, `bad_name`, `hack_no_check_ip` 등(운영 정책에 따름) |

---

## 2. 필수 초기화 — 캐릭터 본체·직결 서브테이블

캐릭터 `objID` / `cha_objId` / `objId`(캐릭터 키)에 묶인 데이터.

| 테이블 | 비고 |
|--------|------|
| `characters` | 본 테이블. 비우면 모든 캐릭터 제거. |
| `characters_book` | 북마크 |
| `characters_buff` | 버프 저장 |
| `characters_friend` | 친구 |
| `characters_inventory` | 인벤토리 |
| `characters_quest` | 퀘스트 진행 |
| `characters_skill` | 스킬 |
| `characters_swap` | 스왑(단축) |
| `characters_block_list` | 차단 목록 |
| `characters_pvp` | PVP 로그/카운트용 |
| `characters_pet` | 펫 (**캐릭터 삭제 패킷에서는 안 지움** → 시즌 초기화에 포함 권장) |
| `characters_letter` | 우편 |
| `characters_wedding` | 결혼 |
| `character_marble` | 경험치 구슬(마블) 연동 데이터 |

---

## 3. 필수 초기화 — 창고·혈맹·혈맹 부가

| 테이블 | 비고 |
|--------|------|
| `warehouse` | 일반 창고 (`account_uid`) |
| `warehouse_elf` | 요정 창고 |
| `warehouse_clan` | 혈맹 창고 |
| `clan_list` | 혈맹 목록·정보 |
| `clan_agit` | 혈맹 아지트 |
| `warehouse_clan_log` | 혈맹 창고 로그 |
| `auto_clan_list` | 자동 혈맹 NPC/가입 관련 |

---

## 4. 필수 초기화 — 거래·상점·낚시 등 월드 진행

| 테이블 | 비고 |
|--------|------|
| `pc_shop` | 개인 상점 진열 |
| `pc_shop_robot` | 상점 로봇(위치·멘트 등) |
| `pc_shop_history` | 개인상점 거래 이력 |
| `pc_trade` | 거래소(캐릭터/계정 uid 연동) |
| `boards` | 게시판(거래 글 등, `account_id`·캐릭터명) |
| `boards_auction` | 경매장 |
| `auto_fish_list` | 자동 낚시 등록 |
| `wanted` | 수배 |

---

## 5. 필수 초기화 — 파워볼

| 테이블 | 비고 |
|--------|------|
| `powerball_bets` | 배팅 (`char_id` = 캐릭터 objID) |
| `powerball_results` | 회차 결과 |
| `powerball_reward_run` | 일일 포상 정산 메타(GM 툴) |
| `powerball_reward_line` | 일일 포상 명세(`char_obj_id`) |

---

## 6. 필수 초기화 — 사망/인챈 복구 큐·GM 배달 대기

| 테이블 | 비고 |
|--------|------|
| `dead_lost_item` | 사망 드랍 복구 대기 |
| `dead_lost_item_log` | 위 복구 관련 로그 |
| `enchant_lost_item` | 인챈 실패 복구 대기 |
| `gm_item_delivery` | GM 아이템 배달 큐 |
| `gm_adena_delivery` | GM 아데나 배달 큐 |
| `gm_location_delivery` | GM 위치 이동 배달 큐 |

---

## 7. 선택 초기화 (운영 정책)

| 테이블 | 비고 |
|--------|------|
| `kingdom` / `kingdom_tax_log` | 성 점유·세금 로그를 새 시즌부터 백지로 할지 |
| `race_log` | 경마/슬라임 등 베팅 로그 보존 여부 |
| `promote` | 홍보 보상 신청 등 (`name`이 캐릭터명) |
| `donation_history` | 후원 연동 이력 보존 여부 |
| `member` | 고정 멤버/가입 신청 테이블(있을 경우) |
| `gm_chat_log` | GM 채팅 로그 보존 여부 |
| `cashback` | 계정 단위 포인트를 시즌마다 리셋할지 |

---

## 8. `accounts` 테이블 — 행은 두고 “진행 필드만” 돌릴지

계정 행은 유지하되, **캐릭터가 없어진 뒤에도 남으면 이상한 값**인 컬럼은 시즌 정책에 따라 초기화 검토.

코드에서 `accounts`에 자주 쓰는 진행성 필드 예:

- `giran_dungeon_time`, `giran_dungeon_count`
- `auto_count`, `자동사냥_이용시간`
- `daycount`, `daycheck`, `daytime`
- `레벨달성체크`
- `member` (유료/고정 멤버 플래그를 시즌마다 뺄지)
- `info_name`, `info_phone_num`, `info_bank_name`, `info_bank_num` (개인정보·정산 정보 재수집 정책)

**주의:** `id`, `pw`, `uid`, `register_date` 등 로그인 식별은 보통 유지.

---

## 9. 실행 시 주의

1. **외래키(FK)** 가 있으면 `SET FOREIGN_KEY_CHECKS=0` 후 TRUNCATE/DELETE, 또는 **자식→부모 순서**로 삭제.
2. `characters` 를 먼저 지우면 다른 테이블이 이름/objID를 참조하므로, **실제 순서는 DB 스키마에 맞출 것**.
3. 서버 **완전 종료** 후 실행 권장(접속 중 DB 비우기 금지).

---

## 10. 한 줄 요약

- **시즌 초기화 = `characters` + 캐릭터 직결 테이블 + 창고·혈맹 + 거래/상점/낚시 + 파워볼 + 복구·GM 큐**까지 한 묶음으로 보는 것이 안전합니다.  
- **성(`kingdom`)·各종 로그·후원/멤버**는 운영 정책에 따라 7절·8절에서 골라 적용하세요.
