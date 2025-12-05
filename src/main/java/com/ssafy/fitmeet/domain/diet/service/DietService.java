package com.ssafy.fitmeet.domain.diet.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ssafy.fitmeet.domain.diet.dto.DietDto.*;
import com.ssafy.fitmeet.domain.diet.entity.Diet;
import com.ssafy.fitmeet.domain.diet.entity.DietInfo;
import com.ssafy.fitmeet.domain.diet.entity.DietScore;

public interface DietService {

    /**
     * 한 끼 식단 생성 (MANUAL / API / IMAGE)
     */
    Long createDiet(Long userId, DietCreateRequest request);

    /**
     * 한 끼 식단 수정 (MANUAL / API / IMAGE)
     */
    void updateDiet(Long userId, Long dietId, DietUpdateRequest request);

    /**
     * 직접 Diet + DietInfo 엔티티를 받아서 저장하는 버전
     *  - 테스트용, 혹은 특수 케이스용으로 남겨둔 메서드
     */
    Diet createDietWithInfos(Diet diet, List<DietInfo> dietInfos);

    /**
     * Diet + DietInfo 엔티티를 받아서 수정하는 버전
     */
    void updateDietWithInfos(Diet diet, List<DietInfo> dietInfos);

    /**
     * 식단 단건 조회
     */
    Diet getDiet(Long dietId);

    /**
     * 해당 식단의 상세 음식 정보 조회
     */
    List<DietInfo> getDietInfos(Long dietId);

    /**
     * 기간별 식단 목록 조회 (캘린더용)
     */
    List<Diet> getDietsByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 하루 단위 조회 (일간 뷰용)
     */
    List<Diet> getDietsByUserAndLocalDate(Long userId, LocalDate localDate);

    /**
     * 식단 삭제
     */
    void deleteDiet(Long dietId);

    /**
     * 식단 점수 저장/수정
     */
    DietScore saveOrUpdateDietScore(DietScore dietScore);

    /**
     * 식단 점수 조회
     */
    DietScore getDietScore(Long dietId);
}
