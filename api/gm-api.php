<?php
/**
 * GM 툴 통합 API - 계정/캐릭터/DB테이블/창고로그/거래로그 조회
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

$pass = $config['password'] ?? '';
if ($pass === '') {
    jsonFail('DB 비밀번호가 비어 있습니다.');
    exit;
}

try {
    $pdo = new PDO($dsn, $config['user'], $pass, [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]);
} catch (PDOException $e) {
    jsonFail('DB 연결 실패: ' . $e->getMessage());
    exit;
}

$action = $_POST['action'] ?? $_GET['action'] ?? '';

try {
    switch ($action) {
        case 'list_accounts':
            $st = $pdo->query('SELECT uid, id, access_level, last_ip, time AS last_login FROM accounts ORDER BY time DESC LIMIT 500');
            jsonOk(['list' => $st->fetchAll(PDO::FETCH_ASSOC)]);
            break;

        case 'list_characters':
            $st = $pdo->query(
                'SELECT c.objID, c.name, c.level, c.class, c.account_uid, a.id AS account_id ' .
                'FROM characters c LEFT JOIN accounts a ON c.account_uid = a.uid ORDER BY c.name LIMIT 1000'
            );
            jsonOk(['list' => $st->fetchAll(PDO::FETCH_ASSOC)]);
            break;

        case 'list_tables':
            $st = $pdo->query('SHOW TABLES');
            $tables = [];
            while ($row = $st->fetch(PDO::FETCH_NUM)) {
                $tables[] = $row[0];
            }
            $counts = [];
            foreach ($tables as $t) {
                try {
                    $c = $pdo->query("SELECT COUNT(*) FROM `" . str_replace('`', '``', $t) . "`")->fetchColumn();
                    $counts[$t] = (int) $c;
                } catch (Exception $e) {
                    $counts[$t] = null;
                }
            }
            jsonOk(['tables' => $tables, 'counts' => $counts]);
            break;

        case 'list_warehouse_log':
            $st = $pdo->query('SELECT * FROM warehouse_clan_log ORDER BY uid DESC LIMIT 500');
            jsonOk(['list' => $st->fetchAll(PDO::FETCH_ASSOC)]);
            break;

        case 'list_trade_log':
            $st = $pdo->query('SELECT * FROM pc_shop_history ORDER BY 판매_시간 DESC LIMIT 500');
            jsonOk(['list' => $st->fetchAll(PDO::FETCH_ASSOC)]);
            break;

        default:
            jsonFail('지원하지 않는 action. list_accounts, list_characters, list_tables, list_warehouse_log, list_trade_log 중 하나를 지정하세요.');
    }
} catch (Exception $e) {
    jsonFail('조회 실패: ' . $e->getMessage());
}
