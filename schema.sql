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
                        `deleted_at` DATETIME NULL, -- 소프트 삭제 ( 탈퇴 처리 시각 )
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_user_email` (`email`),
                        KEY `idx_user_status` (`status`),
                        KEY `idx_user_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

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
                                  `activity_level` VARCHAR(20) NULL, -- 활동량 수준 ( NONE, LOW, MID, HIGH )
                                  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 정보 등록 시각
                                  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 정보 수정 시각
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `fk_user_body_info_user`
                                      FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
                                          ON DELETE CASCADE
                                          ON UPDATE CASCADE,
                                  KEY `idx_user_body_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 리프레시 토큰 저장 테이블
CREATE TABLE `user_refresh_token` (
                                      `user_id` BIGINT UNSIGNED NOT NULL, -- USER.id (FK)
                                      `refresh_token` VARCHAR(512) NOT NULL, -- 리프레시 토큰 문자열 (JWT)
                                      `expires_at` DATETIME NOT NULL, -- 리프레시 토큰 만료 시각
                                      `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`user_id`),
                                      CONSTRAINT `fk_urt_user`
                                          FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

######################### 식단 #########################

-- 식단 입력 테이블
DROP TABLE IF EXISTS `diet`;
CREATE TABLE `diet` (
                        `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- 기본키 (PK)
                        `user_id` BIGINT UNSIGNED NOT NULL, -- 회원 ID (FK)
                        `date` DATETIME NOT NULL, -- 식단 날짜/시간
                        `meal_type` CHAR(1) NOT NULL, -- 식단 종류 (A:아침, B:점심, C:저녁, D:간식, E:야식)
                        `description` VARCHAR(255), -- 메모
                        `image_url` VARCHAR(255), -- 이미지 URL
                        `source_type` VARCHAR(20) NOT NULL DEFAULT 'MANUAL', -- 입력/분석 관련 메타 정보 (MANUAL: 직접입력, IMAGE: 이미지분석, API: 공공데이터 기반)

    -- 식단 전체 요약
                        `total_kcal` DECIMAL(8,3) DEFAULT NULL, -- 총 칼로리
                        `total_carbohydrate` DECIMAL(8,3) DEFAULT NULL, -- 총 탄수화물
                        `total_protein` DECIMAL(8,3) DEFAULT NULL, -- 총 단백질
                        `total_fat` DECIMAL(8,3) DEFAULT NULL, -- 총 지방
                        `total_sugar` DECIMAL(8,3) DEFAULT NULL,-- 총 당류
                        `total_sodium` DECIMAL(10,3) DEFAULT NULL,-- 총 나트륨
                        `is_public` TINYINT(1) NOT NULL DEFAULT 0, -- 커뮤니티 공개 여부 (0:비공개, 1:공개)

                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `deleted_at` DATETIME NULL,

                        PRIMARY KEY (`id`),
                        KEY `idx_diet_user_date` (`user_id`, `date`),
                        KEY `idx_diet_user_meal` (`user_id`, `date`, `meal_type`),
                        CONSTRAINT `fk_diet_user_id`
                            FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 식단 점수 테이블
DROP TABLE IF EXISTS `diet_score`;
CREATE TABLE `diet_score` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- 기본키 (PK)
                              `diet_id` BIGINT UNSIGNED NOT NULL, -- 식단 ID (FK)
                              `score` INT NOT NULL DEFAULT 0, -- 점수 (0~100 권장)
                              `grade` CHAR(1) DEFAULT NULL, -- 등급 (A~E 등)
                              `feedback` VARCHAR(255) DEFAULT NULL, -- 간단 코멘트
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_diet_score_diet_id` (`diet_id`),    -- 한 식단에 점수 1개 가정
                              CONSTRAINT `fk_diet_score_diet_id`
                                  FOREIGN KEY (`diet_id`) REFERENCES `diet`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;


-- 식단 정보 테이블
DROP TABLE IF EXISTS `diet_info`;
CREATE TABLE `diet_info` (
                             `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- 기본키 (PK)
                             `diet_id` BIGINT UNSIGNED NOT NULL, -- 식단 ID (FK)
                             `meal_id` BIGINT UNSIGNED NOT NULL, -- 음식 마스터 ID (meal.id)
                             `food_nm_kr` VARCHAR(100) NOT NULL, -- 음식 이름 (한글)
                             `food_code` VARCHAR(50) DEFAULT NULL, -- 공공데이터/내부 음식 코드
                             `source_type` VARCHAR(20) NOT NULL DEFAULT 'PUBLIC_API', -- ( PUBLIC_API / MANUAL / MODEL 등 )

                             `intake_gram` DECIMAL(8,3) DEFAULT NULL, -- 섭취 무게 (g)
                             `kcal` DECIMAL(8,3) DEFAULT NULL, -- 칼로리
                             `carbohydrate` DECIMAL(8,3) DEFAULT NULL, -- 탄수화물
                             `protein` DECIMAL(8,3) DEFAULT NULL, -- 단백질
                             `fat` DECIMAL(8,3) DEFAULT NULL, -- 지방
                             `sugar` DECIMAL(8,3) DEFAULT NULL, -- 당류
                             `sodium` DECIMAL(10,3) DEFAULT NULL, -- 나트륨
                             `dietary_fiber` DECIMAL(8,3) DEFAULT NULL, -- 식이섬유

                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             PRIMARY KEY (`id`),
                             KEY `idx_diet_info_diet_id` (`diet_id`),
                             KEY `idx_diet_info_meal_id` (`meal_id`),
                             CONSTRAINT `fk_diet_info_diet_id`
                                 FOREIGN KEY (`diet_id`) REFERENCES `diet`(`id`) ON DELETE CASCADE,
                             CONSTRAINT `fk_diet_info_meal_id`
                                 FOREIGN KEY (`meal_id`) REFERENCES `meal`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

######################### 음식 마스터 #########################

-- 음식 마스터 테이블 (공공데이터 + 커스텀 + 모델 공통)
DROP TABLE IF EXISTS `meal`;
CREATE TABLE `meal` (
                        `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        `food_cd` VARCHAR(50) NOT NULL, -- FOOD_CD (공공데이터 식품코드)
                        `food_nm_kr` VARCHAR(100) NOT NULL, -- FOOD_NM_KR (식품명)
                        `db_grp_cm` VARCHAR(5) NOT NULL, -- DB_GRP_CM (데이터 구분 코드: D=음식 등)
                        `db_grp_nm` VARCHAR(20) NOT NULL, -- DB_GRP_NM (데이터 구분명)

                        `food_cat1_cd` VARCHAR(10) NULL, -- FOOD_CAT1_CD (대분류 코드)
                        `food_cat1_nm` VARCHAR(50) NULL, -- FOOD_CAT1_NM (대분류명)
                        `food_cat2_cd` VARCHAR(10) NULL, -- FOOD_CAT2_CD (중분류 코드)
                        `food_cat2_nm` VARCHAR(50) NULL, -- FOOD_CAT2_NM (중분류명)
                        `food_cat3_cd` VARCHAR(10) NULL, -- FOOD_CAT3_CD (소분류 코드)
                        `food_cat3_nm` VARCHAR(50) NULL, -- FOOD_CAT3_NM (소분류명)

                        `serving_size_raw` VARCHAR(50) NOT NULL, -- SERVING_SIZE
                        `source_type` VARCHAR(20) NOT NULL DEFAULT 'PUBLIC_API', -- PUBLIC_API / CUSTOM / MODEL

                        `category` VARCHAR(50) NULL, -- 상위 카테고리 (예: 밥류, 국/탕류 등)
                        `brand` VARCHAR(100) NULL, -- 브랜드/제조사
                        `tags` VARCHAR(255) NULL, -- "고단백,저탄수,다이어트" 같은 태그 문자열

                        `update_date` DATE NULL, -- 공공데이터 기준 최신일자
                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_meal_food_cd` (`food_cd`),
                        KEY `idx_meal_cat1` (`food_cat1_cd`),
                        KEY `idx_meal_cat2` (`food_cat2_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 100g(또는 SERVING_SIZE 기준) 영양 성분
DROP TABLE IF EXISTS `meal_nutrition`;
CREATE TABLE `meal_nutrition` (
                                  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, -- PK
                                  `meal_id` BIGINT UNSIGNED NOT NULL, -- FK
                                  `base_amount_g` DECIMAL(8,3) NOT NULL, -- SERVING_SIZE에서 숫자만 파싱한 값

                                  `kcal` DECIMAL(8,3) DEFAULT NULL,-- AMT_NUM1 : 열량
                                  `carbohydrate` DECIMAL(8,3) DEFAULT NULL, -- AMT_NUM6 : 탄수화물
                                  `protein` DECIMAL(8,3) DEFAULT NULL, -- AMT_NUM3 : 단백질
                                  `fat` DECIMAL(8,3) DEFAULT NULL, -- AMT_NUM4 : 지방
                                  `sugar` DECIMAL(8,3) DEFAULT NULL, -- AMT_NUM7 : 당류
                                  `sodium` DECIMAL(10,3) DEFAULT NULL, -- AMT_NUM13 : 나트륨
                                  `dietary_fiber` DECIMAL(8,3) DEFAULT NULL, -- AMT_NUM8 : 식이섬유

                                  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  PRIMARY KEY (`id`),
                                  KEY `idx_meal_nutrition_meal_id` (`meal_id`),
                                  CONSTRAINT `fk_meal_nutrition_meal`
                                      FOREIGN KEY (`meal_id`) REFERENCES `meal`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

