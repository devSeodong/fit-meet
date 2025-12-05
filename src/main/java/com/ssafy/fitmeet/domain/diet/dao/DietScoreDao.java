package com.ssafy.fitmeet.domain.diet.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.fitmeet.domain.diet.entity.DietScore;

@Mapper
public interface DietScoreDao {

    int insert(DietScore dietScore);

    DietScore findByDietId(@Param("dietId") Long dietId);

    int update(DietScore dietScore);

    int deleteByDietId(@Param("dietId") Long dietId);
}
