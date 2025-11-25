package com.ssafy.fitmeet.domain.user.service;

import com.ssafy.fitmeet.domain.user.dto.UserDto.ChangePasswordRequest;
import com.ssafy.fitmeet.domain.user.dto.UserDto.ProfileBodyInfoResponse;
import com.ssafy.fitmeet.domain.user.dto.UserDto.UpdateProfileRequest;
import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;

public interface UserService {

	/**
	 * 현재 로그인한 유저의 email 가져오기
	 */
	String getCurrentUserEmail();

	/**
	 * 이메일 사용자 조회 ( 로그인, 이메일 중복 체크 )
	 */
	User getUserInfoEmail(String email);

	/**
	 * id 사용자 프로필 + 신체정보 조회
	 */
	ProfileBodyInfoResponse getProfileBody();

	/**
	 * 신체 정보 조회
	 */
	UserBodyInfo findByUserId(Long userId);

	/**
	 * 신체 정보 등록 
	 */
	void insertBodyInfo(UserBodyInfo userBodyInfo);

	/**
	 * 프로필 + 신체 정보 수정
	 */
	void updateProfile(UpdateProfileRequest request);

	/**
	 * 비밀번호 변경
	 */
	void changePassword(ChangePasswordRequest request);

	/**
	 * 회원 탈퇴 (소프트 삭제)
	 */
	void softDelete();

}
