-- GM 툴용: 서버 가동 heartbeat (서버 기동 시 자동 생성되지만, 수동으로도 실행 가능)
CREATE TABLE IF NOT EXISTS `gm_server_status` (
  `id` TINYINT NOT NULL PRIMARY KEY DEFAULT 1,
  `online` TINYINT NOT NULL DEFAULT 0 COMMENT '1=서버가 마지막 heartbeat까지 정상 가동 중으로 표시',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='GM툴: 서버 가동/heartbeat';

INSERT IGNORE INTO `gm_server_status` (`id`, `online`) VALUES (1, 0);

-- 툴에서 "서버 실제 온라인" 판별 예시 (heartbeat 45초 이상 끊기면 오프라인으로 표시)
-- SELECT
--   CASE WHEN `online` = 1 AND TIMESTAMPDIFF(SECOND, `updated_at`, NOW()) < 45 THEN 1 ELSE 0 END AS server_live
-- FROM `gm_server_status` WHERE `id` = 1;

-- 봇 "월드 있음/없음" 표시: server_live=0 이면 gm_robot_live 조인 없이 전부 '월드 없음',
-- server_live=1 이면 gm_robot_live 에 objId 있으면 좌표, 없으면 '월드 없음'(설정상 스폰 대기 등)
