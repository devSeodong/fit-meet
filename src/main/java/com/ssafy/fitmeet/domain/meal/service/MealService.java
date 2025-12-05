package com.ssafy.fitmeet.domain.meal.service;

import com.ssafy.fitmeet.domain.meal.entity.Meal;
import com.ssafy.fitmeet.domain.meal.entity.MealNutrition;
import com.ssafy.fitmeet.domain.meal.openapi.dto.FoodNtrResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 음식 마스터 / 영양소 조회용 서비스
 * - 공공데이터 on-demand + 로컬 캐시 전략
 */
public interface MealService {

    /**
     * foodCd 기준으로 Meal 조회
     * 1) local DB(meal)에서 먼저 조회
     * 2) 없으면 공공데이터 API 호출 → meal insert → 반환
     */
    Meal getOrFetchMealByFoodCd(String foodCd);

    /**
     * foodCd 기준으로 MealNutrition 조회
     * 1) getOrFetchMealByFoodCd로 Meal 확보
     * 2) local DB(meal_nutrition)에서 조회
     * 3) 없으면 공공데이터 API 호출 → meal_nutrition insert → 반환
     */
    MealNutrition getOrFetchNutritionByFoodCd(String foodCd);

    /**
     * 음식 검색 (검색창용, 통합 플로우)
     *
     * 1) 먼저 local DB(meal)에서 food_nm_kr LIKE %keyword% AND category = ?
     * 2) 결과가 있으면 → 그대로 반환
     * 3) 없으면 → 공공데이터 API 호출 (FOOD_NM_KR, FOOD_CAT1_NM)
     *    → 응답을 meal / meal_nutrition 테이블에 insert(캐시)
     *    → insert된 Meal 리스트 반환
     *
     * @param keyword  품목명(FOOD_NM_KR) 일부, null/blank면 검색 안 함
     * @param category 대분류명(FOOD_CAT1_NM), 선택
     */
    List<Meal> searchMeals(String keyword, String category);

    /**
     * 기준량(baseAmount) 기준 영양성분(baseValue)을
     * 실제 섭취량(intakeGram)에 맞게 스케일링
     */
    BigDecimal calculateScaled(BigDecimal baseAmount, BigDecimal baseValue, BigDecimal intakeGram);

    /**
     * 공공데이터 직접 검색용 - 디버그/관리자용
     * 프론트 일반 검색은 searchMeals()를 우선 사용
     */
    List<FoodNtrResponse.Item> searchMealsFromApi(String name, String category, int page, int size);
}
