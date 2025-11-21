DROP DATABASE IF EXISTS fitmeet;
CREATE DATABASE IF NOT EXISTS fitmeet;
USE fitmeet;

-- 회원 기본정보
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- 기본키 (PK)
    `email` VARCHAR(100) NOT NULL, -- 계정 이메일
    `password` VARCHAR(255) NOT NULL, -- 비밀번호
    `name` VARCHAR(50) NOT NULL, -- 실명(또는 이름)
    `nickname` VARCHAR(50) NOT NULL, -- 서비스 내에서 사용하는 닉네임
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 계정 상태 ( ACTIVE : 정상, WITHDRAWN : 탈퇴, SUSPENDED : 정지 )
    `role` VARCHAR(30) NOT NULL DEFAULT 'ROLE_USER', -- 권한 ( ROLE_USER : 사용자, ROLE_ADMIN : 관리자 )
    `profile_image_url` VARCHAR(255) NULL, -- 프로필 경로 ( URL ? 문자열 ? )
    `email_verified` TINYINT(1) NOT NULL DEFAULT 0, -- 이메일 인증 여부 ( 0 : 미인증, 1 : 인증 )
    `last_login_at` DATETIME NULL, -- 마지막 로그인 시간
    `password_changed_at` DATETIME NULL, -- 마지막 비밀번호 변경 시각
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 계정 생성 시각
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 계정 정보 수정 시각
    `deleted_at` DATETIME NULL COMMENT '탈퇴 처리 시각 (소프트 삭제용)', -- 소프트 삭제 ( 탈퇴 처리 시각 )
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`),
    KEY `idx_user_status` (`status`),
    KEY `idx_user_role` (`role`)
) 
ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 회원 신체정보
DROP TABLE IF EXISTS `user_body_info`;
CREATE TABLE `user_body_info` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- 고유 ID ( PK )
    `user_id` BIGINT UNSIGNED NOT NULL, -- 회원 아이디 ( user.id FK )
    `height_cm` DECIMAL(5,2) NULL, -- 키 소수점 둘째자리, cm
    `weight_kg` DECIMAL(5,2) NULL, -- 몸무게 소수점 둘째자리, kg
    `target_weight_kg` DECIMAL(5,2) NULL, -- 목표 몸무게 소수점 둘째자리, kg
    `gender` VARCHAR(10) NULL, -- 성별 ( MALE, FEMALE, OTHER )
    `birth_date` DATE NULL, -- 생년월일
    `activity_level` VARCHAR(20) NULL, -- 활동량 수준 ( LOW, MID, HIGH )
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 정보 등록 시각
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 정보 수정 시각
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_user_body_info_user`
        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    KEY `idx_user_body_user_id` (`user_id`)
)
ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 비밀번호 재설정 토큰 ( 이메일 재설정 )
DROP TABLE IF EXISTS `password_reset_token`;
CREATE TABLE `password_reset_token` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- 비밀번호 재설정 토큰 ID ( PK )
    `user_id` BIGINT UNSIGNED NOT NULL, -- 회원 ID (user.id FK)
    `token` VARCHAR(100) NOT NULL, -- 비밀번호 재설정용 토큰 문자열 ( 이메일로 보낼 UUID ? )
    `expires_at` DATETIME NOT NULL, -- 토큰 만료 시각
    `used` TINYINT(1) NOT NULL DEFAULT 0, -- 토큰 사용 여부 ( 0 : 미사용, 1 : 사용 )
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 토큰 생성 시각
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_password_reset_token_token` (`token`),
    KEY `idx_password_reset_token_user_used` (`user_id`, `used`),
    CONSTRAINT `fk_password_reset_token_user`
        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 리프레시 토큰 저장 테이블
CREATE TABLE `user_refresh_token` (
    `user_id`      BIGINT UNSIGNED NOT NULL, -- USER.id (FK)
    `refresh_token` VARCHAR(512) NOT NULL, -- 리프레시 토큰 문자열 (JWT)
    `expires_at`    DATETIME NOT NULL, -- 리프레시 토큰 만료 시각
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_urt_user`
        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

select * 
from user u join user_body_info ub
where u.id = ub.id;


