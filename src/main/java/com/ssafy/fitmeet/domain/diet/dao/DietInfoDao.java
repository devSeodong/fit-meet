package com.ssafy.fitmeet.domain.diet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.fitmeet.domain.diet.entity.DietInfo;

@Mapper
public interface DietInfoDao {

    int insert(DietInfo dietInfo);

    int insertBatch(@Param("list") List<DietInfo> list);

    List<DietInfo> findByDietId(@Param("dietId") Long dietId);

    int update(DietInfo dietInfo);

    int deleteByDietId(@Param("dietId") Long dietId);
}
