package com.ssafy.fitmeet.domain.auth.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.fitmeet.domain.auth.entity.UserRefreshToken;

@Mapper
public interface UserRefreshTokenDao {
	
	// UserId로 토큰 정보 가져오기
	UserRefreshToken findByUserId(@Param("userId") Long userId);
	
	// 토큰 저장
	int insert(@Param("token") UserRefreshToken token);
	
	// 토큰 업데이트
	int update(@Param("token") UserRefreshToken token);
	
	// UserId로 토큰 정보 삭제 ( 로그아웃 )
	int deleteByUserId(@Param("userId") Long userId);
}
