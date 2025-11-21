package com.ssafy.fitmeet.domain.auth.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRefreshToken {
	
	private Long userId;
	private String refreshToken;
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
