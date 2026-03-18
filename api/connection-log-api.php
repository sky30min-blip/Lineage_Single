<?php
/**
 * 접속 로그 API - accounts 테이블의 id, last_ip, time(마지막 접속) 조회
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
    jsonFail('DB 비밀번호가 비어 있습니다. config.php를 확인하세요.');
    exit;
}

try {
    $pdo = new PDO($dsn, $config['user'], $pass, [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]);
} catch (PDOException $e) {
    jsonFail('DB 연결 실패: ' . $e->getMessage());
    exit;
}

$action = $_POST['action'] ?? $_GET['action'] ?? 'list';

if ($action !== 'list') {
    jsonFail('지원하지 않는 action');
    exit;
}

try {
    // time = 마지막 접속 시간으로 사용. 없으면 logins_date 등 대체 가능
    $st = $pdo->query(
        'SELECT id, last_ip, time AS last_login FROM accounts ORDER BY time DESC LIMIT 1000'
    );
    $rows = $st->fetchAll(PDO::FETCH_ASSOC);
    jsonOk(['list' => $rows]);
} catch (Exception $e) {
    try {
        $st = $pdo->query(
            'SELECT id, last_ip, logins_date AS last_login FROM accounts ORDER BY logins_date DESC LIMIT 1000'
        );
        $rows = $st->fetchAll(PDO::FETCH_ASSOC);
        jsonOk(['list' => $rows]);
    } catch (Exception $e2) {
        jsonFail('접속 로그 조회 실패: ' . $e->getMessage());
    }
}
