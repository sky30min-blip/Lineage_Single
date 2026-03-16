# Cursor 2단계 프롬프트 - 캐릭터 관리

1단계가 정상 작동하면 이제 캐릭터 관리 페이지를 만들어줘.

## 파일 생성
**경로**: D:\Lineage_Single\gm_tool\pages\2_👤_캐릭터관리.py

## 요구사항

### 임포트
```python
import streamlit as st
import pandas as pd
from utils.db_manager import get_db
import config
```

### 레이아웃
```
탭 1: 캐릭터 목록
탭 2: 캐릭터 수정
탭 3: 아덴 관리
탭 4: 위치 이동
```

### 탭 1: 캐릭터 목록
- 전체 캐릭터 조회:
  ```sql
  SELECT char_name, level, Class, account_name 
  FROM characters 
  ORDER BY level DESC
  ```
- Class 값을 config.CLASS_NAMES로 변환해서 표시
- pandas DataFrame으로 변환 후 st.dataframe 사용

### 탭 2: 캐릭터 수정
1. 캐릭터 선택 (st.selectbox)
2. 선택한 캐릭터 정보 표시:
   ```sql
   SELECT * FROM characters WHERE char_name = %s
   ```
3. 수정 폼:
   - 레벨 (st.number_input, 1~99)
   - HP/MP (st.number_input)
   - 스탯 (Str, Dex, Con, Wis, Cha, Intel)
4. "저장" 버튼:
   ```sql
   UPDATE characters 
   SET level=?, MaxHp=?, MaxMp=?, Str=?, Dex=?, Con=?, Wis=?, Cha=?, Intel=?
   WHERE char_name=?
   ```

### 탭 3: 아덴 관리
⚠️ 주의: 아덴 컬럼명은 실제 DB 구조 확인 필요
- 가능한 컬럼명: AdenaCount, Adena, Money, Gold
- 먼저 DESCRIBE characters로 확인 후 정확한 컬럼명 사용

1. 캐릭터 선택
2. 현재 아덴 표시
3. 입력 필드 (st.number_input, 최소값 0)
4. 버튼 3개:
   - "지급" → 현재값 + 입력값
   - "차감" → 현재값 - 입력값
   - "설정" → 입력값으로 정확히 설정

### 탭 4: 위치 이동
1. 캐릭터 선택
2. 현재 위치 표시 (LocX, LocY, MapID)
3. 방법 1: 주요 마을로 이동
   - config.TOWN_COORDINATES 사용
   - 마을 선택 (st.selectbox)
   - "이동" 버튼
4. 방법 2: 좌표 직접 입력
   - X, Y, MapID 입력
   - "이동" 버튼

업데이트 SQL:
```sql
UPDATE characters 
SET LocX=?, LocY=?, MapID=?
WHERE char_name=?
```

## 스타일링
- st.success, st.error, st.warning 적극 활용
- 수정 후 성공 메시지 표시
- 에러 발생 시 에러 메시지 표시

## 디버깅 팁
1. 실제 컬럼명 확인:
   ```python
   structure = db.get_table_structure('characters')
   st.write(structure)
   ```
2. 쿼리 실행 전 결과 미리보기
3. 파라미터 바인딩 올바르게 사용 (SQL Injection 방지)

이 페이지를 완성하면 캐릭터 레벨/스탯/아덴/위치를 모두 관리할 수 있어!
