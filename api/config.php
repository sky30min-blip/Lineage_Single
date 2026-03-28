<?php
/**
 * GM 툴 API용 DB 설정
 * 1) mysql.conf (싱글리니지 팩) 자동 로드
 * 2) 없으면 lin200 / 127.0.0.1:3306 기본값 (Windows MySQL 실플레이 DB)
 * 3) 같은 폴더의 config.local.php 가 있으면 배열을 덮어씀
 */
$base = [
    'host' => '127.0.0.1',
    'port' => 3306,
    'dbname' => 'lin200',
    'user' => 'root',
    'password' => '',
    'charset' => 'utf8mb4',
];

$apiDir = __DIR__;
$gmToolRoot = dirname($apiDir);
$projectRoot = dirname($gmToolRoot);

/** mysql.conf 후보 (한글 폴더명은 DirectoryIterator 순서에 의존하지 않도록 명시) */
$mysqlConfCandidates = [
    $projectRoot . DIRECTORY_SEPARATOR . '2.싱글리니지 팩' . DIRECTORY_SEPARATOR . 'mysql.conf',
];

$mysqlConfPath = null;
foreach ($mysqlConfCandidates as $p) {
    if ($p !== '' && is_readable($p)) {
        $mysqlConfPath = $p;
        break;
    }
}

if ($mysqlConfPath === null && is_dir($projectRoot)) {
    foreach (new DirectoryIterator($projectRoot) as $entry) {
        if (!$entry->isDir() || $entry->isDot() || $entry->getFilename() === 'gm_tool') {
            continue;
        }
        $candidate = $entry->getPathname() . DIRECTORY_SEPARATOR . 'mysql.conf';
        if (is_readable($candidate)) {
            $mysqlConfPath = $candidate;
            break;
        }
    }
}

if ($mysqlConfPath !== null) {
    $raw = @file_get_contents($mysqlConfPath);
    if ($raw !== false) {
        $raw = preg_replace('/^\xEF\xBB\xBF/', '', $raw);
        $user = $base['user'];
        $password = $base['password'];
        $url = '';
        foreach (preg_split('/\r?\n/', $raw) as $line) {
            $line = trim($line);
            if ($line === '' || (isset($line[0]) && $line[0] === '#')) {
                continue;
            }
            if (preg_match('/^\s*id\s*=\s*(.+)$/i', $line, $m)) {
                $user = trim($m[1]);
            }
            if (preg_match('/^\s*pw\s*=\s*(.+)$/i', $line, $m)) {
                $password = trim($m[1]);
            }
            if (preg_match('/^\s*url\s*=\s*(.+)$/i', $line, $m)) {
                $url = trim($m[1]);
            }
        }
        // jdbc:mariadb://host:port/dbname?... 또는 mysql://...
        if ($url !== '' && preg_match('#(?:mysql|mariadb)://([^:/]+):(\d+)/([^?]+)#i', $url, $m)) {
            $base['host'] = $m[1];
            $base['port'] = (int) $m[2];
            $base['dbname'] = $m[3];
        }
        $base['user'] = $user;
        $base['password'] = $password;
    }
}

$local = $apiDir . DIRECTORY_SEPARATOR . 'config.local.php';
if (is_readable($local)) {
    $override = require $local;
    if (is_array($override)) {
        $base = array_merge($base, $override);
    }
}

return $base;
