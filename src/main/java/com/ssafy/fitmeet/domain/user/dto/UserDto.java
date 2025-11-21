package com.ssafy.fitmeet.domain.user.dto;

import java.time.LocalDate;

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
    

}
