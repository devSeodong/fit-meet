package com.ssafy.fitmeet.domain.user.dto;

import java.time.LocalDate;

import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;

public class UserDto {

    // 프로필 + 신체 정보 수정 요청
    public record UpdateProfileRequest(
            String nickname,
            // 신체 정보
            Double heightCm,
            Double weightKg,
            Double targetWeightKg,
            String gender,
            LocalDate birthDate,
            String activityLevel
    ) {}

    // 비밀번호 변경 요청
    public record ChangePasswordRequest(
            String currentPassword,
            String newPassword
    ) {}
    
    // User, Body join 
    public record ProfileBodyInfoResponse(
            Long id,
            String email,
            String name,
            String nickname,
            String status,
            String role,
            String profileImageUrl,
            Double heightCm,
            Double weightKg,
            Double targetWeightKg,
            String gender,
            LocalDate birthDate,
            String activityLevel
    ) {
        public static ProfileBodyInfoResponse of(User user, UserBodyInfo body) {
            return new ProfileBodyInfoResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getNickname(),
                    user.getStatus(),
                    user.getRole(),
                    user.getProfileImageUrl(),
                    body != null ? body.getHeightCm() : null,
                    body != null ? body.getWeightKg() : null,
                    body != null ? body.getTargetWeightKg() : null,
                    body != null ? body.getGender() : null,
                    body != null ? body.getBirthDate() : null,
                    body != null ? body.getActivityLevel() : null
            );
        }
    }

}
