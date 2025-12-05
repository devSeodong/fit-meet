package com.ssafy.fitmeet.domain.meal.service;

import com.ssafy.fitmeet.domain.meal.dao.MealDao;
import com.ssafy.fitmeet.domain.meal.dao.MealNutritionDao;
import com.ssafy.fitmeet.domain.meal.entity.Meal;
import com.ssafy.fitmeet.domain.meal.entity.MealNutrition;
import com.ssafy.fitmeet.domain.meal.openapi.FoodOpenApiClient;
import com.ssafy.fitmeet.domain.meal.openapi.dto.FoodNtrResponse;
import com.ssafy.fitmeet.global.error.CustomException;
import com.ssafy.fitmeet.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealServiceImpl implements MealService {

    private final MealDao mealDao;
    private final MealNutritionDao mealNutritionDao;
    private final FoodOpenApiClient foodOpenApiClient;

    @Override
    @Transactional
    public Meal getOrFetchMealByFoodCd(String foodCd) {

        Meal existing = mealDao.findByFoodCd(foodCd);
        if (existing != null) {
            return existing;
        }

        Meal newMeal = fetchMealFromPublicApi(foodCd);
        if (newMeal == null) {
            throw new CustomException(ErrorCode.MEAL_NOT_FOUND);
        }

        mealDao.insert(newMeal);
        return newMeal;
    }

    @Override
    @Transactional
    public MealNutrition getOrFetchNutritionByFoodCd(String foodCd) {
        Meal meal = getOrFetchMealByFoodCd(foodCd);

        MealNutrition existing = mealNutritionDao.findByMealId(meal.getId());
        if (existing != null) {
            return existing;
        }

        MealNutrition nutrition = fetchMealNutritionFromPublicApi(meal);
        if (nutrition == null) {
            throw new CustomException(ErrorCode.MEAL_NOT_FOUND2);
        }

        nutrition.setMealId(meal.getId());
        mealNutritionDao.insert(nutrition);
        return nutrition;
    }

    @Override
    public List<Meal> searchMeals(String keyword, String category) {
        return mealDao.searchByKeyword(keyword, category);
    }

    @Override
    public BigDecimal calculateScaled(BigDecimal baseAmount, BigDecimal baseValue, BigDecimal intakeGram) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) == 0 || baseValue == null || intakeGram == null) {
            return null;
        }

        return intakeGram
                .divide(baseAmount, 6, RoundingMode.HALF_UP)
                .multiply(baseValue)
                .setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * 공공데이터에서 foodCd 기준으로 Meal 생성
     * - FoodNtrCpntDbInfo02 API 응답 → Meal 매핑
     */
    private Meal fetchMealFromPublicApi(String foodCd) {

        FoodNtrResponse.Item item = foodOpenApiClient.getByFoodCd(foodCd);
        if (item == null) {
            log.warn("[MealService] 공공데이터에서 foodCd={} 결과 없음", foodCd);
            return null;
        }

        LocalDate updateDate = null;
        if (item.getUpdateDate() != null && !item.getUpdateDate().isBlank()) {
            updateDate = LocalDate.parse(item.getUpdateDate());
        }

        return Meal.builder()
                .foodCd(item.getFoodCd())
                .foodNmKr(item.getFoodNmKr())
                .dbGrpCm(item.getDbGrpCm())
                .dbGrpNm(item.getDbGrpNm())
                .foodCat1Cd(item.getFoodCat1Cd())
                .foodCat1Nm(item.getFoodCat1Nm())
                .foodCat2Cd(item.getFoodCat2Cd())
                .foodCat2Nm(item.getFoodCat2Nm())
                .foodCat3Cd(item.getFoodCat3Cd())
                .foodCat3Nm(item.getFoodCat3Nm())
                .servingSizeRaw(item.getServingSize())
                .sourceType("PUBLIC_API")
                .category(item.getFoodCat1Nm())
                .brand(null)
                .tags(null)
                .updateDate(updateDate)
                .build();
    }

    /**
     * 공공데이터에서 영양성분 부분만 뽑아서 MealNutrition 생성
     */
    private MealNutrition fetchMealNutritionFromPublicApi(Meal meal) {

        FoodNtrResponse.Item item = foodOpenApiClient.getByFoodCd(meal.getFoodCd());
        if (item == null) {
            log.warn("[MealService] 공공데이터에서 영양성분 조회 실패, foodCd={}", meal.getFoodCd());
            return null;
        }

        BigDecimal baseAmountG = parseBaseAmountG(item.getServingSize());

        BigDecimal kcal = toBigDecimalOrNull(item.getAmtNum1()); // 열량
        BigDecimal protein = toBigDecimalOrNull(item.getAmtNum3()); // 단백질
        BigDecimal fat = toBigDecimalOrNull(item.getAmtNum4()); // 지방
        BigDecimal carbohydrate = toBigDecimalOrNull(item.getAmtNum6()); // 탄수화물
        BigDecimal sugar = toBigDecimalOrNull(item.getAmtNum7()); // 당류
        BigDecimal dietaryFiber = toBigDecimalOrNull(item.getAmtNum8()); // 식이섬유
        BigDecimal sodium = toBigDecimalOrNull(item.getAmtNum13()); // 나트륨

        return MealNutrition.builder()
                .mealId(null) // getOrFetchNutritionByFoodCd에서 세팅
                .baseAmountG(baseAmountG)
                .kcal(kcal)
                .carbohydrate(carbohydrate)
                .protein(protein)
                .fat(fat)
                .sugar(sugar)
                .sodium(sodium)
                .dietaryFiber(dietaryFiber)
                .build();
    }

    /**
     * 문자열에서 숫자만 파싱해서 g 기준
     */
    private BigDecimal parseBaseAmountG(String servingSizeRaw) {
        if (servingSizeRaw == null) {
            return new BigDecimal("100.000");
        }

        // "100g" → "100", "1회 200 g" → "200"
        String digits = servingSizeRaw.replaceAll("[^0-9.]", "");
        if (digits.isBlank()) {
            return new BigDecimal("100.000");
        }

        BigDecimal v = new BigDecimal(digits);
        return v.setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * 콤마 포함 문자열을 BigDecimal로 변환
     */
    private BigDecimal toBigDecimalOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            String normalized = s.replace(",", "");
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            log.warn("[MealService] 숫자 파싱 실패: {}", s);
            return null;
        }
    }

    @Override
    public List<FoodNtrResponse.Item> searchMealsFromApi(String name, String category, int page, int size) {
        return foodOpenApiClient.searchFoods(name, category, page, size);
    }
}
