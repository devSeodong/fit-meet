package com.ssafy.fitmeet.domain.diet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietInfo {

	private Long id;
	private Long dietId;
	private Long mealId;
	private String foodNmKr;
	private String foodCode;
	private String sourceType;
	private BigDecimal intakeGram;
	private BigDecimal kcal;
	private BigDecimal carbohydrate;
	private BigDecimal protein;
	private BigDecimal fat;
	private BigDecimal sugar;
	private BigDecimal sodium;
	private BigDecimal dietaryFiber;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
