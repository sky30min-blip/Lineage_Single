<?php
/**
 * GM 툴 API용 DB 설정
 * mysql.conf 가 있으면 자동으로 그 설정을 사용합니다. 없으면 아래 기본값 사용.
 */
$base = [
    'host' => 'localhost',
    'port' => 3306,
    'dbname' => 'l1jdb',
    'user' => 'root',
    'password' => '',
    'charset' => 'utf8mb4',
];

$projectRoot = dirname(__DIR__, 2);
$mysqlConfPath = null;
if (is_dir($projectRoot)) {
foreach (new DirectoryIterator($projectRoot) as $entry) {
    if ($entry->isDir() && !$entry->isDot() && $entry->getFilename() !== 'gm_tool') {
        $candidate = $entry->getPathname() . DIRECTORY_SEPARATOR . 'mysql.conf';
        if (file_exists($candidate)) {
            $mysqlConfPath = $candidate;
            break;
        }
    }
}
}

if ($mysqlConfPath !== null) {
    $raw = @file_get_contents($mysqlConfPath);
    if ($raw !== false) {
        $user = $base['user'];
        $password = $base['password'];
        $url = '';
        foreach (preg_split('/\r?\n/', $raw) as $line) {
            $line = trim($line);
            if ($line === '' || $line[0] === '#') continue;
            if (preg_match('/^\s*id\s*=\s*(.+)$/i', $line, $m)) $user = trim($m[1]);
            if (preg_match('/^\s*pw\s*=\s*(.+)$/i', $line, $m)) $password = trim($m[1]);
            if (preg_match('/^\s*url\s*=\s*(.+)$/i', $line, $m)) $url = trim($m[1]);
        }
        if ($url !== '' && preg_match('#(?:mysql|mariadb)://([^:/]+):(\d+)/([^?]+)#i', $url, $m)) {
            $base['host'] = $m[1];
            $base['port'] = (int) $m[2];
            $base['dbname'] = $m[3];
        }
        $base['user'] = $user;
        $base['password'] = $password;
    }
}

return $base;
