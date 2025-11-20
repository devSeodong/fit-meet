package com.ssafy.fitmeet.domain.user.dao;

import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserBodyInfoDao {

    // 신체 정보 조회
    UserBodyInfo findByUserId(@Param("userId") Long userId);

    // 신체 정보 최초 입력
    int insertBodyInfo(UserBodyInfo bodyInfo);

    // 신체 정보 수정
    int updateBodyInfo(UserBodyInfo bodyInfo);

}
