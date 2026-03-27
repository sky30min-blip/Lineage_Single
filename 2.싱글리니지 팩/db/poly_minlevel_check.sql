-- ============================================================
-- 변신(poly) 테이블 minlevel 확인 및 수정
-- 변신주문서 사용 시 이 minlevel 이상만 변신 가능 (변신 조종/고대의 반지 착용 시 1~2 감소)
-- ============================================================

-- 1) 현재 변신 목록과 minlevel 조회 (변신목록에 나오는 레벨 확인)
SELECT id, name, db, minlevel, polyid
FROM poly
ORDER BY minlevel ASC, name;

-- 2) minlevel이 0이거나 비정상적으로 낮은 고레벨 변신만 따로 조회
-- (데스나이트, 다크엘프, 바포메트 등은 보통 52 이상 권장)
SELECT id, name, db, minlevel
FROM poly
WHERE (db LIKE '%데스나이트%' OR db LIKE '%다크엘프%' OR db LIKE '%바포메트%' OR db LIKE '%리치%'
   OR name LIKE '%데스나이트%' OR name LIKE '%다크엘프%' OR name LIKE '%바포메트%' OR name LIKE '%리치%')
ORDER BY db;

-- 3) 필요 시 minlevel 수정 (예: 데스나이트·다크엘프 52레벨로 통일)
-- UPDATE poly SET minlevel = 52 WHERE db LIKE '%데스나이트%' OR name LIKE '%데스나이트%';
-- UPDATE poly SET minlevel = 52 WHERE db LIKE '%다크엘프%' OR name LIKE '%다크엘프%';

-- 4) 전체 변신 minlevel 일괄 점검 후 수정 예시 (실제 값은 서버 정책에 맞게 변경)
-- UPDATE poly SET minlevel = 52 WHERE db IN ('데스나이트', '다크엘프');
-- 수정 후 반드시 서버에서 poly 테이블 리로드 또는 서버 재시작.
