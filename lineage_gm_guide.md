# 리니지 싱글 서버 GM 툴 개발 가이드

## 🎯 프로젝트 목표
Navicat/HeidiSQL 완전 대체 + GM 편의 기능을 가진 Streamlit 웹 애플리케이션

---

## 📁 프로젝트 구조

```
D:\Lineage_Single\gm_tool\
├── app.py                          # 메인 대시보드 (서버 상태, 테이블 체크)
├── config.py                       # DB 연결 설정
├── requirements.txt                # Python 패키지
├── utils/
│   ├── __init__.py
│   ├── db_manager.py              # DB 연결 및 기본 쿼리 클래스
│   ├── table_schemas.py           # 누락 테이블 생성 SQL
│   └── helpers.py                 # 공통 유틸리티 함수
└── pages/
    ├── 1_📋_계정관리.py            # 계정 CRUD
    ├── 2_👤_캐릭터관리.py          # 캐릭터 조회/수정 (레벨/스탯/아덴)
    ├── 3_🎁_아이템관리.py          # 아이템 지급/인벤토리
    ├── 4_⚙️_서버설정.py           # 경험치/아덴 배율 조정
    ├── 5_💾_DB관리.py             # SQL 직접 실행, 테이블 관리
    └── 6_📊_통계분석.py            # 레벨 분포, 순위 등
```

---

## 🗄️ 데이터베이스 정보

### 연결 정보
- Host: `localhost`
- Port: `3306`
- User: `root`
- Password: `1307`
- Database: `l1jdb`

### 주요 테이블 (확인된 것)

#### 1. `accounts` - 계정 정보
```sql
-- 주요 컬럼 (추정)
account_name    VARCHAR     # 계정 ID
password        VARCHAR     # 비밀번호 (암호화)
access_level    INT         # GM 권한 (0=일반, 200=GM)
create_date     DATETIME    # 생성일
last_login      DATETIME    # 최종 접속
```

#### 2. `characters` - 캐릭터 정보
```sql
-- 주요 컬럼 (추정)
char_name       VARCHAR     # 캐릭터명 (PK)
account_name    VARCHAR     # 소유 계정
level           INT         # 레벨
Exp             BIGINT      # 경험치
MaxHp, CurHp    INT         # HP
MaxMp, CurMp    INT         # MP
Str, Dex, Con, Wis, Cha, Intel  INT  # 스탯
LocX, LocY, MapID  INT      # 위치
Class           VARCHAR     # 직업 (0=군주, 1=기사, 2=요정, 3=마법사)
```

#### 3. `character_items` 또는 `inventory` - 인벤토리
```sql
-- 주요 컬럼 (추정)
id              INT         # PK
char_name       VARCHAR     # 캐릭터명
item_id         INT         # 아이템 ID
count           INT         # 개수
enchantlvl      INT         # 인챈트 수치
is_equipped     INT         # 장착 여부
```

#### 4. `etcitem` - 아이템 마스터 데이터
```sql
-- 주요 컬럼 (추정)
item_id         INT         # 아이템 ID (PK)
name            VARCHAR     # 아이템명 (영문)
name_id         VARCHAR     # 아이템명 (한글 키)
```

#### 5. `weapon`, `armor` - 무기/방어구 마스터 데이터
```sql
-- 주요 컬럼 (추정)
item_id         INT         # 아이템 ID
name            VARCHAR     # 아이템명
```

### ⚠️ 누락된 테이블
```sql
-- 서버 실행 시 에러 발생
ban_word        # 금지어 테이블 (채팅 필터용)
```

---

## 🔧 주요 기능 명세

### Phase 1: 기본 인프라 (우선순위 최상)

#### 1.1 메인 대시보드 (`app.py`)
```python
# 표시할 정보
- DB 연결 상태 (✅ 연결됨 / ❌ 연결 실패)
- 전체 테이블 개수
- 누락된 테이블 목록 (ban_word 등)
- 현재 캐릭터 수
- 전체 계정 수

# 기능
- "누락 테이블 생성" 버튼 → ban_word 테이블 자동 생성
- 서버 상태 새로고침
```

#### 1.2 DB 관리 페이지 (`5_💾_DB관리.py`)
```python
# 탭 1: SQL 실행
- st.text_area("SQL 쿼리 입력")
- 실행 버튼
- 결과를 st.dataframe으로 표시

# 탭 2: 테이블 목록
- 전체 테이블 리스트
- 각 테이블 클릭 시 구조 보기 (DESCRIBE)
- 데이터 미리보기 (LIMIT 100)

# 탭 3: 테이블 생성
- ban_word 등 누락 테이블 생성 템플릿
- 원클릭 생성 버튼
```

---

### Phase 2: 핵심 GM 기능

#### 2.1 캐릭터 관리 (`2_👤_캐릭터관리.py`)
```python
# 기능 1: 캐릭터 목록
- SELECT char_name, level, Class, account_name FROM characters
- st.dataframe으로 표시
- 검색 필터 (이름, 레벨 범위, 직업)

# 기능 2: 캐릭터 수정
- 캐릭터 선택 (st.selectbox)
- 레벨 수정 (st.number_input, 1~99)
- 스탯 수정 (Str, Dex, Con, Wis, Cha, Intel)
- HP/MP 수정
- 경험치 수정
- UPDATE characters SET ... WHERE char_name=?

# 기능 3: 아덴 지급/차감
- 현재 아덴 표시
- 지급할 금액 입력
- "지급" 버튼 → 현재값에 더하기
- "차감" 버튼 → 현재값에서 빼기
- "설정" 버튼 → 정확한 값으로 세팅

# 기능 4: 위치 이동
- 현재 좌표 표시 (LocX, LocY, MapID)
- 좌표 직접 입력
- 주요 마을 프리셋:
  * 기란: X=33936, Y=32318, MapID=4
  * 아덴: X=33430, Y=32815, MapID=4
  * 하이네: X=33605, Y=33235, MapID=4
```

#### 2.2 아이템 관리 (`3_🎁_아이템관리.py`)
```python
# 기능 1: 아이템 지급
- 캐릭터 선택
- 아이템 검색 (이름 또는 ID)
  * SELECT item_id, name FROM etcitem WHERE name LIKE '%검색어%'
  * UNION SELECT item_id, name FROM weapon ...
  * UNION SELECT item_id, name FROM armor ...
- 개수 입력
- 인챈트 수치 입력 (0~10)
- "지급" 버튼
  * INSERT INTO character_items (char_name, item_id, count, enchantlvl)

# 기능 2: 인벤토리 조회
- 캐릭터 선택
- 현재 인벤토리 목록 표시
  * SELECT i.item_id, e.name, i.count, i.enchantlvl
  * FROM character_items i
  * LEFT JOIN etcitem e ON i.item_id = e.item_id
- 아이템 삭제 버튼
```

#### 2.3 계정 관리 (`1_📋_계정관리.py`)
```python
# 기능 1: 계정 생성
- 계정 ID 입력
- 비밀번호 입력
- GM 권한 체크박스 (access_level=200)
- INSERT INTO accounts ...

# 기능 2: 계정 목록
- SELECT account_name, access_level, create_date FROM accounts
- st.dataframe 표시

# 기능 3: GM 권한 부여/해제
- 계정 선택
- UPDATE accounts SET access_level=200 WHERE account_name=?
```

---

### Phase 3: 고급 기능

#### 3.1 서버 설정 (`4_⚙️_서버설정.py`)
```python
# 서버 설정은 보통 설정 파일(.properties 또는 .xml)에 있음
# DB에서 조정 가능한 항목만 구현

# 기능: 현재 배율 표시
- "경험치 배율: 10배" (정보성)
- "아덴 배율: 50배" (정보성)
- "이 설정은 서버 설정 파일에서 직접 수정해야 합니다" 안내

# 추가 가능 기능:
- 특정 캐릭터에게 경험치 직접 지급
- 특정 캐릭터 아덴 설정
```

#### 3.2 통계 분석 (`6_📊_통계분석.py`)
```python
# 차트 1: 레벨 분포
- SELECT level, COUNT(*) as count FROM characters GROUP BY level
- st.bar_chart 사용

# 차트 2: 직업별 분포
- SELECT Class, COUNT(*) FROM characters GROUP BY Class
- st.pie_chart 사용

# 순위 3: 아덴 부자 순위
- SELECT char_name, 아덴컬럼 FROM characters ORDER BY 아덴컬럼 DESC LIMIT 10
- st.dataframe

# 순위 4: 레벨 순위
- SELECT char_name, level FROM characters ORDER BY level DESC LIMIT 10
```

---

## 🔑 중요 SQL 쿼리 모음

### 테이블 존재 확인
```sql
SELECT TABLE_NAME 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'l1jdb';
```

### ban_word 테이블 생성
```sql
CREATE TABLE IF NOT EXISTS `ban_word` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `word` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 캐릭터 레벨 수정
```sql
UPDATE characters 
SET level = ? 
WHERE char_name = ?;
```

### 아덴 지급 (컬럼명 확인 필요)
```sql
-- 아덴 컬럼명이 AdenaCount 또는 Adena일 가능성
UPDATE characters 
SET AdenaCount = AdenaCount + ? 
WHERE char_name = ?;
```

### 아이템 지급
```sql
INSERT INTO character_items (char_name, item_id, count, enchantlvl, is_equipped)
VALUES (?, ?, ?, ?, 0);
```

---

## 🐛 디버깅 포인트

### 1. 테이블/컬럼명 확인
```sql
-- 실제 테이블 구조 확인
DESCRIBE characters;
DESCRIBE accounts;
DESCRIBE character_items;  -- 또는 inventory
```

### 2. 아덴 컬럼명 찾기
```sql
-- characters 테이블에서 '아덴' 관련 컬럼 찾기
SHOW COLUMNS FROM characters LIKE '%aden%';
SHOW COLUMNS FROM characters LIKE '%money%';
SHOW COLUMNS FROM characters LIKE '%gold%';
```

### 3. 인벤토리 테이블명 확인
```sql
-- 인벤토리 관련 테이블 찾기
SHOW TABLES LIKE '%item%';
SHOW TABLES LIKE '%inven%';
```

---

## 📝 Cursor에게 줄 프롬프트 예시

### 1단계: 기본 파일 생성
```
D:\Lineage_Single\gm_tool 폴더에 다음 파일들을 생성해줘:

1. requirements.txt
   - streamlit
   - pymysql
   - pandas
   - plotly

2. config.py
   - DB 연결 정보 (host=localhost, port=3306, user=root, password=1307, database=l1jdb)

3. utils/db_manager.py
   - DBManager 클래스
   - connect() 메서드
   - execute_query(sql, params) 메서드
   - fetch_all(sql, params) 메서드
   - fetch_one(sql, params) 메서드
```

### 2단계: 메인 대시보드
```
app.py를 만들어줘:
- Streamlit 페이지 설정 (타이틀: "리니지 GM 툴", 아이콘: 🎮)
- 사이드바에 로고와 DB 연결 상태 표시
- 메인 영역:
  * DB 연결 상태 (st.success 또는 st.error)
  * 전체 테이블 개수
  * 계정 수, 캐릭터 수 표시
  * "SHOW TABLES" 실행해서 ban_word 있는지 체크
  * 없으면 경고 메시지 + "ban_word 테이블 생성" 버튼
```

### 3단계: 캐릭터 관리 페이지
```
pages/2_👤_캐릭터관리.py를 만들어줘:

탭 1: 캐릭터 목록
- SELECT char_name, level, Class, account_name FROM characters
- st.dataframe으로 표시

탭 2: 캐릭터 수정
- 캐릭터 선택 (st.selectbox)
- 선택한 캐릭터 정보 표시
- 레벨 수정 (st.number_input, 1~99)
- 저장 버튼 → UPDATE characters SET level=? WHERE char_name=?

탭 3: 아덴 지급
- 캐릭터 선택
- 현재 아덴 표시 (컬럼명 확인 필요)
- 지급 금액 입력
- "지급" 버튼 → UPDATE ... SET 아덴컬럼 = 아덴컬럼 + ?
```

---

## ⚡ 빠른 시작 명령어

```bash
# 1. 폴더 이동
cd D:\Lineage_Single\gm_tool

# 2. 가상환경 생성 (선택)
python -m venv venv
venv\Scripts\activate

# 3. 패키지 설치
pip install -r requirements.txt

# 4. 실행
streamlit run app.py
```

---

## 🎯 개발 우선순위

1. ✅ config.py + db_manager.py (기반)
2. ✅ app.py (대시보드 + ban_word 생성)
3. ✅ 2_👤_캐릭터관리.py (레벨/아덴 수정)
4. ✅ 5_💾_DB관리.py (SQL 실행)
5. ⏳ 3_🎁_아이템관리.py
6. ⏳ 1_📋_계정관리.py
7. ⏳ 6_📊_통계분석.py
8. ⏳ 4_⚙️_서버설정.py

---

## 💡 팁

- 실제 테이블 구조는 `DESCRIBE 테이블명` 또는 Navicat으로 확인 후 조정
- 컬럼명은 대소문자 구분할 수 있으니 정확히 확인
- 먼저 SELECT로 데이터 확인 후 UPDATE 실행
- 백업 기능은 나중에 추가 (mysqldump 사용)

---

이 가이드를 Cursor에 붙여넣고 단계별로 개발하세요! 🚀
