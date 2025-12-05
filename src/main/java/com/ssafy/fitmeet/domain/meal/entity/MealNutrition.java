package com.ssafy.fitmeet.domain.meal.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealNutrition {

    private Long id;
    private Long mealId;
    private BigDecimal baseAmountG;

    private BigDecimal kcal; // AMT_NUM1
    private BigDecimal carbohydrate; // AMT_NUM6
    private BigDecimal protein; // AMT_NUM3
    private BigDecimal fat; // AMT_NUM4
    private BigDecimal sugar; // AMT_NUM7
    private BigDecimal sodium; // AMT_NUM13
    private BigDecimal dietaryFiber; // AMT_NUM8
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
