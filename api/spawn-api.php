<?php
/**
 * 몬스터 스폰 관리 API
 * GM 툴 monster-spawn-manager.html 에서 호출
 */
header('Content-Type: application/json; charset=utf-8');

$config_file = dirname(__DIR__) . '/config.php';
if (file_exists($config_file)) {
    include $config_file;
} else {
    $db_host = 'localhost';
    $db_name = 'l1jdb';
    $db_user = 'root';
    $db_pass = '1307';
}

try {
    $pdo = new PDO(
        "mysql:host=$db_host;dbname=$db_name;charset=utf8mb4",
        $db_user,
        $db_pass,
        [ PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION ]
    );
} catch (PDOException $e) {
    echo json_encode(['success' => false, 'error' => 'DB 연결 실패: ' . $e->getMessage()]);
    exit;
}

$action = $_POST['action'] ?? '';

if ($action === 'execute_spawn') {
    $queries = json_decode($_POST['queries'] ?? '[]', true);
    if (!is_array($queries) || count($queries) === 0) {
        echo json_encode(['success' => false, 'error' => '실행할 쿼리가 없습니다.']);
        exit;
    }
    try {
        $pdo->beginTransaction();
        foreach ($queries as $sql) {
            $pdo->exec($sql);
        }
        $pdo->commit();
        echo json_encode(['success' => true]);
    } catch (Exception $e) {
        $pdo->rollBack();
        echo json_encode(['success' => false, 'error' => $e->getMessage()]);
    }
    exit;
}

if ($action === 'list_spawns') {
    $map_id = $_POST['map_id'] ?? '';
    if ($map_id === '') {
        echo json_encode(['success' => true, 'spawns' => []]);
        exit;
    }
    try {
        $st = $pdo->prepare("SELECT uid, name, monster, count, spawn_x, spawn_y, spawn_map, re_spawn_min, re_spawn_max FROM monster_spawnlist WHERE spawn_map = ? ORDER BY uid");
        $st->execute([$map_id]);
        $rows = $st->fetchAll(PDO::FETCH_ASSOC);
        echo json_encode(['success' => true, 'spawns' => $rows]);
    } catch (Exception $e) {
        echo json_encode(['success' => false, 'error' => $e->getMessage(), 'spawns' => []]);
    }
    exit;
}

if ($action === 'delete_spawn') {
    $uid = (int) ($_POST['uid'] ?? 0);
    if ($uid <= 0) {
        echo json_encode(['success' => false, 'error' => 'uid가 없습니다.']);
        exit;
    }
    try {
        $st = $pdo->prepare("DELETE FROM monster_spawnlist WHERE uid = ?");
        $st->execute([$uid]);
        echo json_encode(['success' => true]);
    } catch (Exception $e) {
        echo json_encode(['success' => false, 'error' => $e->getMessage()]);
    }
    exit;
}

echo json_encode(['success' => false, 'error' => '알 수 없는 action: ' . $action]);
