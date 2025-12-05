package com.ssafy.fitmeet.domain.diet.entity;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietScore {

	private Long id;
	private Long dietId;
	private Integer score; // 점수 (0~100)
	private String grade; // 등급 (A~E)
	private String feedback; // 코멘트
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
