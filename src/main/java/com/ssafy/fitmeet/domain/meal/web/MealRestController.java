package com.ssafy.fitmeet.domain.meal.web;

import com.ssafy.fitmeet.domain.meal.entity.Meal;
import com.ssafy.fitmeet.domain.meal.entity.MealNutrition;
import com.ssafy.fitmeet.domain.meal.openapi.dto.FoodNtrResponse;
import com.ssafy.fitmeet.domain.meal.service.MealService;
import com.ssafy.fitmeet.global.response.Response;
import com.ssafy.fitmeet.global.response.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Tag(name = "Meal", description = "음식 마스터/영양성분 API")
public class MealRestController {

    private final MealService mealService;

    /**
     * 음식 검색 (검색창용)
     */
    @GetMapping("/search")
    @Operation(summary = "음식 검색", description = "키워드/카테고리 기반 음식 목록 조회")
    public ResponseEntity<Response<?>> searchMeals(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category) {
        List<Meal> meals = mealService.searchMeals(keyword, category);
        return ResponseEntity.ok(ResponseUtil.ok(meals));
    }

    /**
     * foodCd 기준 단건 조회
     */
    @GetMapping("/{foodCd}")
    @Operation(summary = "음식 단건 조회", description = "foodCd 기준으로 음식 정보 조회")
    public ResponseEntity<Response<?>> getMeal(@PathVariable String foodCd) {
        Meal meal = mealService.getOrFetchMealByFoodCd(foodCd);
        return ResponseEntity.ok(ResponseUtil.ok(meal));
    }

    /**
     * foodCd 기준 영양성분 조회
     */
    @GetMapping("/{foodCd}/nutrition")
    @Operation(summary = "영양성분 조회", description = "foodCd 기준 영양성분 조회")
    public ResponseEntity<Response<?>> getNutrition(@PathVariable String foodCd) {
        MealNutrition nutrition = mealService.getOrFetchNutritionByFoodCd(foodCd);
        return ResponseEntity.ok(ResponseUtil.ok(nutrition));
    }

    @GetMapping("/search/external")
    public ResponseEntity<Response<?>> searchExternalMeals(@RequestParam(required = false) String name, @RequestParam(required = false) String category, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<FoodNtrResponse.Item> items = mealService.searchMealsFromApi(name, category, page, size);
        return ResponseEntity.ok(ResponseUtil.ok(items));
    }

}
