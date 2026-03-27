-- ============================================================
-- 기란감옥(맵 53, 54, 55, 56) 리스폰 몬스터 확인
-- 맵: 53=기란감옥 1층, 54=2층, 55=3층, 56=4층
-- spawn_map 형식: "53" 또는 "53|54" 등
-- (MariaDB / MySQL 호환. 서버 설정은 mysql.conf → jdbc:mariadb 사용)
-- ============================================================

-- 기란감옥(53,54,55,56)에 스폰되는 모든 몬스터 목록
SELECT
    uid,
    name,
    monster AS 몬스터명,
    spawn_map AS 맵,
    spawn_x AS x,
    spawn_y AS y,
    count AS 마리수,
    re_spawn_min AS 리스폰최소초,
    re_spawn_max AS 리스폰최대초
FROM monster_spawnlist
WHERE spawn_map = '53'
   OR spawn_map = '54'
   OR spawn_map = '55'
   OR spawn_map = '56'
   OR CONCAT('|', spawn_map, '|') LIKE '%|53|%'
   OR CONCAT('|', spawn_map, '|') LIKE '%|54|%'
   OR CONCAT('|', spawn_map, '|') LIKE '%|55|%'
   OR CONCAT('|', spawn_map, '|') LIKE '%|56|%'
ORDER BY spawn_map, uid;

-- 층별로 보기 (1층만)
-- SELECT uid, name, monster, spawn_x, spawn_y, count, re_spawn_min, re_spawn_max
-- FROM monster_spawnlist WHERE spawn_map = '53' ORDER BY uid;

-- 2층만
-- SELECT uid, name, monster, spawn_x, spawn_y, count, re_spawn_min, re_spawn_max
-- FROM monster_spawnlist WHERE spawn_map = '54' ORDER BY uid;

-- 3층만
-- SELECT uid, name, monster, spawn_x, spawn_y, count, re_spawn_min, re_spawn_max
-- FROM monster_spawnlist WHERE spawn_map = '55' ORDER BY uid;

-- 4층만
-- SELECT uid, name, monster, spawn_x, spawn_y, count, re_spawn_min, re_spawn_max
-- FROM monster_spawnlist WHERE spawn_map = '56' ORDER BY uid;
