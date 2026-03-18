<?php
/**
 * GM 툴 API용 DB 설정
 * 이 파일을 수정해 실제 DB 비밀번호 등을 입력하세요.
 * config.example.php 를 복사해 사용해도 됩니다.
 */
return [
    'host' => 'localhost',
    'port' => 3306,
    'dbname' => 'l1jdb',
    'user' => 'root',
    // 아래 '' 안에 MySQL root 비밀번호를 넣으세요. 비우면 "Access denied (using password: NO)" 오류 발생.
    'password' => '',
    'charset' => 'utf8mb4',
];
