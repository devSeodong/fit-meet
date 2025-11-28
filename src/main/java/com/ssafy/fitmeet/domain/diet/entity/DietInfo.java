package com.ssafy.fitmeet.domain.diet.entity;

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
public class DietInfo {
	
	private int id;
	private int dietId;
	private String foodNmKr;
	private double intakeGram;
	private double kcal;
	private double carbohydrate;
	private double protein;
	private double fat;
	private double sugar;
	private double sodium;
	private double dietaryFiber;
	
}
