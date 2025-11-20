package com.ssafy.fitmeet.domain.user.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBodyInfo {

    private Long id;
    private Long userId;
    private Double heightCm;
    private Double weightKg;
    private Double targetWeightKg;
    private String gender; // MALE / FEMALE / OTHER
    private LocalDate birthDate;
    private String activityLevel; // LOW / MID / HIGH
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
