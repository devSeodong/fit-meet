package com.ssafy.fitmeet.domain.meal.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.fitmeet.domain.meal.entity.MealNutrition;

@Mapper
public interface MealNutritionDao {

    void insert(MealNutrition nutrition);

    MealNutrition findByMealId(@Param("mealId") Long mealId);

}
