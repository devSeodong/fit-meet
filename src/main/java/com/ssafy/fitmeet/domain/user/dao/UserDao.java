package com.ssafy.fitmeet.domain.user.dao;

import com.ssafy.fitmeet.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface UserDao {

    // 이메일 사용자 조회 ( 로그인, 이메일 중복 체크 )
    User findByEmail(@Param("email") String email);

    // ID로 사용자 단건 조회
    User findById(@Param("id") Long id);

    // 신규회원등록
    int insertUser(User user);

    // 마지막 로그인시간
    int updateLastLogin(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);

    // 비밀번호 변경 + 변경시간 업데이트
    int updatePassword(@Param("id") Long id, @Param("password") String password, @Param("passwordChangedAt") LocalDateTime passwordChangedAt);

    // 회원 탈퇴 ( 소프트 딜리트 )
    int softDelete(@Param("id") Long id, @Param("status") String status, @Param("deletedAt") LocalDateTime deletedAt);

}
