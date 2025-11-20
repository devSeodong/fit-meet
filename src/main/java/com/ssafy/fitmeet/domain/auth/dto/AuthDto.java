package com.ssafy.fitmeet.domain.auth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Auth 관련 요청/응답 DTO 모음
 */
public class AuthDto {

    // 로그인 요청
    public record LoginRequest(
            String email,
            String password
    ) {}

    // 로그인 응답
    public record LoginResponse(
            String accessToken,
            String tokenType,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt
    ) {}

    // 회원가입 요청
    public record SignUpRequest(
            String email,
            String password,
            String name,
            String nickname,
            Double heightCm,
            Double weightKg,
            Double targetWeightKg,
            String gender,
            LocalDate birthDate,
            String activityLevel
    ) {}
}
