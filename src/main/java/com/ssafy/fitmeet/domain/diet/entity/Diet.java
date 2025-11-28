package com.ssafy.fitmeet.domain.diet.entity;

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
public class Diet {
	
	private int id;
	private int userId;
	private LocalDateTime date;
	private char mealType;
	private String description;
	private String imageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deleteAt;
	
}
