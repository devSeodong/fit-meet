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
     * 음식 검색 (검색창용)
     * - keyword: 음식명 일부 (FOOD_NM_KR LIKE %keyword%)
     * - category: 상위 카테고리(대분류명 등), null이면 전체
     */
    List<Meal> searchMeals(String keyword, String category);

    /**
     * 기준량(baseAmount) 기준 영양성분(baseValue)을
     * 실제 섭취량(intakeGram)에 맞게 스케일링
     *
     * ex) baseAmount=100g, baseValue=150kcal, intakeGram=250g
     *   → 250 / 100 * 150 = 375kcal
     */
    BigDecimal calculateScaled(BigDecimal baseAmount, BigDecimal baseValue, BigDecimal intakeGram);

    List<FoodNtrResponse.Item> searchMealsFromApi(String name, String category, int page, int size);
}
