package com.ssafy.fitmeet.domain.user.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String status; // ACTIVE : 정상, WITHDRAWN : 탈퇴, SUSPENDED : 정지
    private String role; // ROLE_USER : 사용자, ROLE_ADMIN : 관리자
    private String profileImageUrl;
    private boolean emailVerified; // 0 : 미인증, 1 : 인증
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
