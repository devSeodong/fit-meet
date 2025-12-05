package com.ssafy.fitmeet.domain.meal.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    private Long id;
    private String foodCd; // FOOD_CD
    private String foodNmKr; // FOOD_NM_KR
    private String dbGrpCm; // DB_GRP_CM
    private String dbGrpNm; // DB_GRP_NM
    private String foodCat1Cd; // FOOD_CAT1_CD
    private String foodCat1Nm; // FOOD_CAT1_NM
    private String foodCat2Cd; // FOOD_CAT2_CD
    private String foodCat2Nm; // FOOD_CAT2_NM
    private String foodCat3Cd; // FOOD_CAT3_CD
    private String foodCat3Nm; // FOOD_CAT3_NM
    private String servingSizeRaw;
    private String sourceType; // PUBLIC_API / CUSTOM / MODEL

    private String category; // 우리 서비스 기준 카테고리 (예: 밥류, 국/탕류 등)
    private String brand; // 브랜드/제조사 (커스텀용)
    private String tags; // "고단백,저탄수" 이런 식의 태그 문자열

    private LocalDate updateDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
