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
public class DietScore {
	
	private int id;
	private int dietId;
	private int score;
	
}
