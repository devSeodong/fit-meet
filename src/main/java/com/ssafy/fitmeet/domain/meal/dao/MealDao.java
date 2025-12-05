package com.ssafy.fitmeet.domain.meal.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.fitmeet.domain.meal.entity.Meal;

@Mapper
public interface MealDao {

    void insert(Meal meal);

    Meal findById(@Param("id") Long id);

    Meal findByFoodCd(@Param("foodCd") String foodCd);

    List<Meal> searchByKeyword(@Param("keyword") String keyword, @Param("category") String category);

}
