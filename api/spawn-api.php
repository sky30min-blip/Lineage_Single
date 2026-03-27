<?php
/**
 * 몬스터 스폰 관리 GM 툴 API
 * - list_spawns: 맵별 스폰 목록
 * - execute_spawn: SQL 일괄 실행 (스폰 추가)
 * - delete_spawn: uid로 스폰 삭제
 * - update_spawn: uid 기준 count/re_spawn 수정
 * - reload_all_spawn: 서버에 전체스폰 리로드 명령 큐잉
 * - reload_status: 리로드 요청 처리 상태 조회
 */
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

$config = require __DIR__ . '/config.php';
$dsn = sprintf(
    'mysql:host=%s;port=%s;dbname=%s;charset=%s',
    $config['host'],
    $config['port'],
    $config['dbname'],
    $config['charset']
);

function jsonOk($data = []) {
    echo json_encode(array_merge(['success' => true], $data), JSON_UNESCAPED_UNICODE);
}

function jsonFail($error) {
    echo json_encode(['success' => false, 'error' => $error], JSON_UNESCAPED_UNICODE);
}

function getPdo($config) {
    global $dsn;
    $pass = $config['password'] ?? '';
    if ($pass === '') {
        jsonFail('DB 비밀번호가 비어 있습니다. gm_tool/api/config.php 를 열어서 \'password\' => \'MySQL비밀번호\' 로 수정하세요.');
        exit;
    }
    try {
        $pdo = new PDO($dsn, $config['user'], $pass, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        ]);
        return $pdo;
    } catch (PDOException $e) {
        $msg = $e->getMessage();
        if (strpos($msg, '1045') !== false || strpos($msg, 'using password: NO') !== false) {
            $msg = 'root 비밀번호가 필요합니다. gm_tool/api/config.php 에서 \'password\' => \'MySQL비밀번호\' 로 수정 후 저장하세요.';
        } else {
            $msg = 'DB 연결 실패: ' . $msg;
        }
        jsonFail($msg);
        exit;
    }
}

$action = $_POST['action'] ?? $_GET['action'] ?? '';

switch ($action) {
    case 'list_spawns':
        $mapId = trim($_POST['map_id'] ?? $_GET['map_id'] ?? '');
        if ($mapId === '') {
            jsonFail('map_id 필요');
            exit;
        }
        try {
            $pdo = getPdo($config);
            // spawn_map은 "4" 또는 "4|5|6" 형태 문자열이므로 해당 맵이 포함된 행 조회
            $st = $pdo->prepare(
                'SELECT uid, name, monster, count, spawn_x, spawn_y, re_spawn_min, re_spawn_max FROM monster_spawnlist ' .
                'WHERE spawn_map = ? OR spawn_map LIKE ? OR spawn_map LIKE ? OR spawn_map LIKE ? ORDER BY uid'
            );
            $st->execute([
                $mapId,
                $mapId . '|%',
                '%|' . $mapId . '|%',
                '%|' . $mapId
            ]);
            $rows = $st->fetchAll(PDO::FETCH_ASSOC);
            jsonOk(['spawns' => $rows]);
        } catch (Exception $e) {
            jsonFail('목록 조회 실패: ' . $e->getMessage());
        }
        break;

    case 'execute_spawn':
        $queriesJson = $_POST['queries'] ?? '';
        if ($queriesJson === '') {
            jsonFail('queries 필요');
            exit;
        }
        $queries = json_decode($queriesJson, true);
        if (!is_array($queries) || count($queries) === 0) {
            jsonFail('유효한 쿼리 배열이 아님');
            exit;
        }
        try {
            $pdo = getPdo($config);
            $pdo->beginTransaction();
            $nextSpawnUid = null;
            foreach ($queries as $sql) {
                if (is_string($sql) && trim($sql) !== '') {
                    // monster_spawnlist INSERT는 전달된 uid가 중복될 수 있어 서버에서 현재 최대 uid 기준으로 재할당한다.
                    if (preg_match('/^\s*INSERT\s+INTO\s+monster_spawnlist\b/i', $sql)) {
                        if ($nextSpawnUid === null) {
                            $stUid = $pdo->query('SELECT IFNULL(MAX(uid), 0) + 1 AS next_uid FROM monster_spawnlist');
                            $rowUid = $stUid->fetch(PDO::FETCH_ASSOC);
                            $nextSpawnUid = (int)($rowUid['next_uid'] ?? 1);
                            if ($nextSpawnUid < 1) $nextSpawnUid = 1;
                        }
                        $uidForThisRow = $nextSpawnUid;
                        $replaced = 0;
                        $sql = preg_replace_callback(
                            '/(\bVALUES\s*\(\s*)\d+(\s*,)/i',
                            function ($m) use ($uidForThisRow, &$replaced) {
                                $replaced = 1;
                                return $m[1] . $uidForThisRow . $m[2];
                            },
                            $sql,
                            1
                        );
                        if ($replaced === 1) $nextSpawnUid++;
                    }
                    $pdo->exec($sql);
                }
            }
            $pdo->commit();
            jsonOk();
        } catch (Exception $e) {
            if (isset($pdo)) {
                $pdo->rollBack();
            }
            jsonFail('실행 실패: ' . $e->getMessage());
        }
        break;

    case 'update_spawn':
        $uid = $_POST['uid'] ?? $_GET['uid'] ?? '';
        $count = $_POST['count'] ?? $_GET['count'] ?? null;
        $respawnMin = $_POST['re_spawn_min'] ?? $_GET['re_spawn_min'] ?? null;
        $respawnMax = $_POST['re_spawn_max'] ?? $_GET['re_spawn_max'] ?? null;

        if ($uid === '' || $count === null || $respawnMin === null || $respawnMax === null) {
            jsonFail('uid, count, re_spawn_min, re_spawn_max 필요');
            exit;
        }
        if (!ctype_digit((string)$uid)) {
            jsonFail('uid는 숫자여야 함');
            exit;
        }
        if (!ctype_digit((string)$count) || (int)$count < 1 || (int)$count > 10000) {
            jsonFail('count는 1~10000 숫자여야 함');
            exit;
        }
        if (!ctype_digit((string)$respawnMin) || !ctype_digit((string)$respawnMax)) {
            jsonFail('re_spawn_min/max 는 0 이상의 숫자여야 함');
            exit;
        }
        $respawnMin = (int)$respawnMin;
        $respawnMax = (int)$respawnMax;
        if ($respawnMin > 86400 || $respawnMax > 86400) {
            jsonFail('re_spawn_min/max 는 86400 이하로 입력하세요');
            exit;
        }
        if ($respawnMin > $respawnMax) {
            jsonFail('re_spawn_min 은 re_spawn_max 보다 클 수 없습니다');
            exit;
        }
        try {
            $pdo = getPdo($config);
            $st = $pdo->prepare('UPDATE monster_spawnlist SET count = ?, re_spawn_min = ?, re_spawn_max = ? WHERE uid = ?');
            $st->execute([(int)$count, $respawnMin, $respawnMax, (int)$uid]);
            if ($st->rowCount() === 0) {
                jsonFail('해당 uid 스폰이 없습니다.');
            } else {
                jsonOk();
            }
        } catch (Exception $e) {
            jsonFail('수정 실패: ' . $e->getMessage());
        }
        break;

    case 'reload_all_spawn':
        try {
            $pdo = getPdo($config);
            // 서버측 GmDeliveryController가 주기적으로 폴링해서 실행
            $st = $pdo->prepare(
                "INSERT INTO gm_server_command (command, param, executed) VALUES ('reload', 'monster_spawnlist', 0)"
            );
            $st->execute();
            $requestId = (int)$pdo->lastInsertId();
            jsonOk([
                'message' => '전체스폰 리로드 요청이 서버 큐에 등록되었습니다. 잠시 후 적용됩니다.',
                'request_id' => $requestId,
            ]);
        } catch (Exception $e) {
            jsonFail('리로드 요청 실패: ' . $e->getMessage() . ' (gm_server_command 테이블/서버 폴링 상태 확인)');
        }
        break;

    case 'reload_status':
        $id = $_POST['id'] ?? $_GET['id'] ?? '';
        if ($id === '' || !ctype_digit((string)$id)) {
            jsonFail('id 필요(숫자)');
            exit;
        }
        try {
            $pdo = getPdo($config);
            $st = $pdo->prepare("SELECT id, executed, command, param FROM gm_server_command WHERE id = ? LIMIT 1");
            $st->execute([(int)$id]);
            $row = $st->fetch(PDO::FETCH_ASSOC);
            if (!$row) {
                jsonFail('요청 ID를 찾을 수 없습니다.');
                exit;
            }
            jsonOk([
                'request_id' => (int)$row['id'],
                'executed' => ((int)$row['executed'] === 1),
                'command' => $row['command'],
                'param' => $row['param'],
            ]);
        } catch (Exception $e) {
            jsonFail('상태 조회 실패: ' . $e->getMessage());
        }
        break;

    case 'list_monsters':
        try {
            $pdo = getPdo($config);
            try {
                $st = $pdo->query('SELECT name, level FROM monster ORDER BY name');
            } catch (Exception $e) {
                $st = $pdo->query('SELECT name FROM monster ORDER BY name');
            }
            $rows = $st->fetchAll(PDO::FETCH_ASSOC);
            foreach ($rows as &$r) {
                if (!isset($r['level'])) $r['level'] = 0;
            }
            unset($r);
            jsonOk(['monsters' => $rows]);
        } catch (Exception $e) {
            jsonFail('몬스터 목록 조회 실패: ' . $e->getMessage());
        }
        break;

    case 'insert_monster':
        $name = trim($_POST['name'] ?? '');
        $nameId = trim($_POST['name_id'] ?? '$0');
        $gfx = (int)($_POST['gfx'] ?? 0);
        $level = (int)($_POST['level'] ?? 1);
        $hp = (int)($_POST['hp'] ?? 50);
        $mp = (int)($_POST['mp'] ?? 10);
        $exp = (int)($_POST['exp'] ?? 0);
        if ($name === '') {
            jsonFail('몬스터 이름(name) 필요');
            exit;
        }
        try {
            $pdo = getPdo($config);
            $sql = "INSERT INTO monster (name, name_id, gfx, gfx_mode, boss, boss_class, level, hp, mp, tic_hp, tic_mp, str, dex, con, `int`, wis, cha, mr, ac, exp, lawful, size, family, atk_type, atk_range, atk_invis, atk_poly, is_pickup, is_revival, is_toughskin, is_adendrop, is_taming, resistance_earth, resistance_fire, resistance_wind, resistance_water, is_undead, is_turn_undead, arrowGfx, haste, bravery, faust_monster, chance, effect) " .
                "VALUES (?, ?, ?, 0, 'false', '', ?, ?, ?, 0, 0, 10, 10, 10, 10, 10, 10, 0, 0, ?, 0, 'small', '', 0, 0, 'false', 'false', 'false', 'false', 'false', 'false', 'false', 0, 0, 0, 0, 'false', 'false', 0, 'false', 'false', '', 0, 0)";
            $st = $pdo->prepare($sql);
            $st->execute([$name, $nameId ?: ('$' . abs(crc32($name))), $gfx, $level, $hp, $mp, $exp]);
            jsonOk(['message' => '커스텀 몬스터가 추가되었습니다. 목록 새로고침 후 선택하세요.']);
        } catch (Exception $e) {
            jsonFail('몬스터 추가 실패: ' . $e->getMessage());
        }
        break;

    case 'delete_spawn':
        $uid = $_POST['uid'] ?? $_GET['uid'] ?? '';
        if ($uid === '') {
            jsonFail('uid 필요');
            exit;
        }
        if (!ctype_digit((string)$uid)) {
            jsonFail('uid는 숫자여야 함');
            exit;
        }
        try {
            $pdo = getPdo($config);
            $st = $pdo->prepare('DELETE FROM monster_spawnlist WHERE uid = ?');
            $st->execute([$uid]);
            if ($st->rowCount() === 0) {
                jsonFail('해당 uid 스폰이 없습니다.');
            } else {
                jsonOk();
            }
        } catch (Exception $e) {
            jsonFail('삭제 실패: ' . $e->getMessage());
        }
        break;

    default:
        jsonFail('지원하지 않는 action: ' . $action);
}
