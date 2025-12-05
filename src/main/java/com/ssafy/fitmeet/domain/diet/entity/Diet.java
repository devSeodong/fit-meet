package com.ssafy.fitmeet.domain.diet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diet {

	private Long id;
	private Long userId;
	private LocalDateTime date;
	private String mealType;
	private String description;
	private String imageUrl;
	private String sourceType;
	private BigDecimal totalKcal;
	private BigDecimal totalCarbohydrate;
	private BigDecimal totalProtein;
	private BigDecimal totalFat;
	private BigDecimal totalSugar;
	private BigDecimal totalSodium;
	private Boolean isPublic;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

}
