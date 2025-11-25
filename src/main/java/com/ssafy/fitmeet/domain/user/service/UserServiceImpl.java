package com.ssafy.fitmeet.domain.user.service;

import com.ssafy.fitmeet.domain.user.dao.UserBodyInfoDao;
import com.ssafy.fitmeet.domain.user.dao.UserDao;
import com.ssafy.fitmeet.domain.user.dto.UserDto.ChangePasswordRequest;
import com.ssafy.fitmeet.domain.user.dto.UserDto.ProfileBodyInfoResponse;
import com.ssafy.fitmeet.domain.user.dto.UserDto.UpdateProfileRequest;
import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;
import com.ssafy.fitmeet.global.error.CustomException;
import com.ssafy.fitmeet.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDao userDao;
    private final UserBodyInfoDao userBodyInfoDao;
    private final PasswordEncoder passwordEncoder;

    /**
     * 현재 로그인한 유저의 email 가져오기
     */
    @Override
    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
    
    /**
     * 이메일 사용자 조회 ( 로그인, 이메일 중복 체크 )
     */
    @Override
    public User getUserInfoEmail(String email) {
    	User user = userDao.findByEmail(email);
    	if(user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
    	return user;
    }
    
    /**
     * id 사용자 프로필 + 신체정보 조회
     */
    @Override
    public ProfileBodyInfoResponse getProfileBody() {
    	String email = getCurrentUserEmail();
    	
    	User user = getUserInfoEmail(email);
        UserBodyInfo bodyInfo = findByUserId(user.getId());
        
        return ProfileBodyInfoResponse.of(user, bodyInfo);
    }
    
    /**
     * 신체 정보 조회
     */
    @Override
    public UserBodyInfo findByUserId(Long userId) {
        return userBodyInfoDao.findByUserId(userId);
    }
    
    /**
     * 신체 정보 등록 
     */
    @Override
    @Transactional
    public void insertBodyInfo(UserBodyInfo userBodyInfo) {
    	String email = getCurrentUserEmail();
    	User user = getUserInfoEmail(email);
        
        UserBodyInfo bodyInfo = findByUserId(user.getId());
    	userBodyInfo.setUserId(user.getId());
    	
        if(bodyInfo == null) {
            userBodyInfoDao.insertBodyInfo(userBodyInfo);
        } else {
        	userBodyInfoDao.updateBodyInfo(userBodyInfo);
        }
    }

    /**
     * 프로필 + 신체 정보 수정
     */
    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
    	String email = getCurrentUserEmail();
    	User user = getUserInfoEmail(email);

        // 닉네임 변경
        user.setNickname(request.nickname());
        user.setUpdatedAt(LocalDateTime.now());

        UserBodyInfo bodyInfo = userBodyInfoDao.findByUserId(user.getId());
        if (bodyInfo == null) {
            bodyInfo = UserBodyInfo.builder()
                    .userId(user.getId())
                    .build();
            bodyInfo.setHeightCm(request.heightCm());
            bodyInfo.setWeightKg(request.weightKg());
            bodyInfo.setTargetWeightKg(request.targetWeightKg());
            bodyInfo.setGender(request.gender());
            bodyInfo.setBirthDate(request.birthDate());
            bodyInfo.setActivityLevel(request.activityLevel());
            bodyInfo.setCreatedAt(LocalDateTime.now());
            bodyInfo.setUpdatedAt(LocalDateTime.now());

            userBodyInfoDao.insertBodyInfo(bodyInfo);
        } else {
            bodyInfo.setHeightCm(request.heightCm());
            bodyInfo.setWeightKg(request.weightKg());
            bodyInfo.setTargetWeightKg(request.targetWeightKg());
            bodyInfo.setGender(request.gender());
            bodyInfo.setBirthDate(request.birthDate());
            bodyInfo.setActivityLevel(request.activityLevel());
            bodyInfo.setUpdatedAt(LocalDateTime.now());

            userBodyInfoDao.updateBodyInfo(bodyInfo);
        }
    }

    /**
     * 비밀번호 변경
     */
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
    	String email = getCurrentUserEmail();
    	User user = getUserInfoEmail(email);

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_BAD_CREDENTIALS);
        }

        String encoded = passwordEncoder.encode(request.newPassword());
        userDao.updatePassword(user.getId(), encoded, LocalDateTime.now());
    }

    /**
     * 회원 탈퇴 (소프트 삭제)
     */
    @Override
    @Transactional
    public void softDelete() {
    	String email = getCurrentUserEmail();
    	User user = getUserInfoEmail(email);

        userDao.softDelete(user.getId(), "WITHDRAWN", LocalDateTime.now());
    }
}
