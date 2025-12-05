package com.ssafy.fitmeet.domain.diet.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.fitmeet.domain.diet.entity.Diet;

@Mapper
public interface DietDao {

    int insert(Diet diet);

    Diet findById(@Param("id") Long id);

    List<Diet> findByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Diet> findByUserAndLocalDate(@Param("userId") Long userId, @Param("localDate") LocalDate localDate);

    List<Diet> findByUserAndDateAndMealType(@Param("userId") Long userId, @Param("date") LocalDateTime date, @Param("mealType") String mealType);

    int update(Diet diet);

    int softDelete(@Param("id") Long id);

    int delete(@Param("id") Long id);

}
