package com.ssafy.fitmeet.domain.user.service;

import com.ssafy.fitmeet.domain.user.dao.UserBodyInfoDao;
import com.ssafy.fitmeet.domain.user.dao.UserDao;
import com.ssafy.fitmeet.domain.user.dto.UserDto.ChangePasswordRequest;
import com.ssafy.fitmeet.domain.user.dto.UserDto.UpdateProfileRequest;
import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final UserBodyInfoDao userBodyInfoDao;
    private final PasswordEncoder passwordEncoder;

    /**
     * 현재 로그인한 유저의 email 가져오기
     */
    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    /**
     * 프로필 + 신체 정보 수정
     */
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        User user = userDao.findByEmail(email);
        if (user == null) throw new IllegalStateException("유저를 찾을 수 없습니다.");

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
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String email = getCurrentUserEmail();
        User user = userDao.findByEmail(email);
        if (user == null) throw new IllegalStateException("유저를 찾을 수 없습니다.");

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encoded = passwordEncoder.encode(request.newPassword());
        userDao.updatePassword(user.getId(), encoded, LocalDateTime.now());
    }

    /**
     * 회원 탈퇴 (소프트 삭제)
     */
    @Transactional
    public void withdraw() {
        String email = getCurrentUserEmail();
        User user = userDao.findByEmail(email);
        if (user == null) throw new IllegalStateException("유저를 찾을 수 없습니다.");

        userDao.softDelete(user.getId(), "WITHDRAWN", LocalDateTime.now());
    }
}
