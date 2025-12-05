package com.ssafy.fitmeet.domain.meal.openapi;

import com.ssafy.fitmeet.domain.meal.openapi.dto.FoodNtrResponse;

import java.util.List;

public interface FoodOpenApiClient {

    /**
     * FOOD_CD 기준 단일 조회 (정확코드)
     */
    FoodNtrResponse.Item getByFoodCd(String foodCd);

    /**
     * FOOD_NM_KR + FOOD_CAT1_NM 기준 검색 후 첫 번째 아이템 반환
     */
    FoodNtrResponse.Item searchOne(String foodName, String cat1Name);

    /**
     * 이름/대분류 기반 다건 검색
     */
    List<FoodNtrResponse.Item> searchFoods(String foodName, String cat1Name, int pageNo, int numOfRows);
}
