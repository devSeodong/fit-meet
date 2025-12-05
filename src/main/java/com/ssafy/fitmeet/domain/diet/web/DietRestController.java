package com.ssafy.fitmeet.domain.diet.web;

import com.ssafy.fitmeet.domain.diet.dto.DietDto.*;
import com.ssafy.fitmeet.domain.diet.entity.Diet;
import com.ssafy.fitmeet.domain.diet.entity.DietInfo;
import com.ssafy.fitmeet.domain.diet.entity.DietScore;
import com.ssafy.fitmeet.domain.diet.service.DietService;
import com.ssafy.fitmeet.global.error.CustomException;
import com.ssafy.fitmeet.global.error.ErrorCode;
import com.ssafy.fitmeet.global.response.Response;
import com.ssafy.fitmeet.global.response.ResponseUtil;
import com.ssafy.fitmeet.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/diets")
@RequiredArgsConstructor
@Tag(name = "Diet", description = "식단 관련 API")
public class DietRestController {

    private final DietService dietService;

    /**
     * 식단 + 상세 음식 등록
     */
    @PostMapping
    @Operation(summary = "식단 등록", description = "한 끼 식단과 음식 상세 정보 등록")
    public ResponseEntity<Response<?>> createDiet(@AuthenticationPrincipal CustomUserDetails user, @RequestBody DietCreateRequest request ) {
        Long userId = user.getId();

        Long dietId = dietService.createDiet(userId, request);

        Diet diet = dietService.getDiet(dietId);
        List<DietInfo> infos = dietService.getDietInfos(dietId);
        DietScore score = dietService.getDietScore(dietId);

        DietDetailResponse body = DietDetailResponse.of(diet, infos, score);
        return ResponseEntity.ok(ResponseUtil.ok(body));
    }

    /**
     * 식단 단건 상세 조회
     */
    @GetMapping("/{dietId}")
    @Operation(summary = "식단 상세 조회", description = "식단, 음식 상세, 점수 함께 조회")
    public ResponseEntity<Response<?>> getDiet(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long dietId) {
        Diet diet = dietService.getDiet(dietId);
        if (!diet.getUserId().equals(user.getId())) {
            throw new CustomException(ErrorCode.AUTH_ACCESS_DENIED);
        }

        List<DietInfo> infos = dietService.getDietInfos(dietId);
        DietScore score = dietService.getDietScore(dietId);

        DietDetailResponse body = DietDetailResponse.of(diet, infos, score);
        return ResponseEntity.ok(ResponseUtil.ok(body));
    }

    /**
     * 특정 날짜(하루) 기준 식단 목록 조회
     */
    @GetMapping("/day")
    @Operation(summary = "일간 식단 조회", description = "특정 날짜의 모든 식단 요약 정보 조회")
    public ResponseEntity<Response<?>> getDietsByDay(@AuthenticationPrincipal CustomUserDetails user, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = user.getId();
        List<Diet> diets = dietService.getDietsByUserAndLocalDate(userId, date);
        List<DietSummaryResponse> body = diets.stream()
                .map(DietSummaryResponse::from)
                .toList();

        return ResponseEntity.ok(ResponseUtil.ok(body));
    }

    /**
     * 캘린더용 기간 조회
     */
    @GetMapping("/calendar")
    @Operation(summary = "기간(캘린더) 식단 조회", description = "시작일~종료일 범위 내의 식단 요약 정보 조회")
    public ResponseEntity<Response<?>> getDietsByRange(@AuthenticationPrincipal CustomUserDetails user, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = user.getId();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.plusDays(1).atStartOfDay();

        List<Diet> diets = dietService.getDietsByUserAndDateRange(userId, start, end);
        List<DietSummaryResponse> body = diets.stream()
                .map(DietSummaryResponse::from)
                .toList();

        return ResponseEntity.ok(ResponseUtil.ok(body));
    }

    /**
     * 식단 + 상세정보 수정
     *  - MANUAL / API / IMAGE 모두 가능 (createDiet와 동일 분기)
     */
    @PutMapping("/{dietId}")
    @Operation(summary = "식단 수정", description = "식단 기본 정보와 음식 상세 정보 수정")
    public ResponseEntity<Response<?>> updateDiet(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long dietId, @RequestBody DietUpdateRequest request) {
        Long userId = user.getId();

        dietService.updateDiet(userId, dietId, request);

        Diet diet = dietService.getDiet(dietId);
        List<DietInfo> savedInfos = dietService.getDietInfos(dietId);
        DietScore score = dietService.getDietScore(dietId);

        DietDetailResponse body = DietDetailResponse.of(diet, savedInfos, score);
        return ResponseEntity.ok(ResponseUtil.ok("식단 수정 완료", body));
    }

    /**
     * 식단 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{dietId}")
    @Operation(summary = "식단 삭제", description = "식단을 소프트 삭제")
    public ResponseEntity<Response<?>> deleteDiet(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long dietId) {
        Diet diet = dietService.getDiet(dietId);
        if (!diet.getUserId().equals(user.getId())) {
            throw new CustomException(ErrorCode.AUTH_ACCESS_DENIED);
        }

        dietService.deleteDiet(dietId);
        return ResponseEntity.ok(ResponseUtil.ok("식단 삭제 완료", null));
    }

    /**
     * 식단 점수 저장/수정
     */
    @PutMapping("/{dietId}/score")
    @Operation(summary = "식단 점수 저장/수정", description = "AI 평가 등의 점수를 저장하거나 수정")
    public ResponseEntity<Response<?>> saveDietScore(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long dietId, @RequestBody DietScoreRequest request) {
        Diet diet = dietService.getDiet(dietId);
        if (!diet.getUserId().equals(user.getId())) {
            throw new CustomException(ErrorCode.AUTH_ACCESS_DENIED);
        }

        DietScore dietScore = DietScore.builder()
                .dietId(dietId)
                .score(request.score())
                .grade(request.grade())
                .feedback(request.feedback())
                .build();

        DietScore saved = dietService.saveOrUpdateDietScore(dietScore);
        DietScoreResponse body = DietScoreResponse.from(saved);

        return ResponseEntity.ok(ResponseUtil.ok("식단 점수 저장 완료", body));
    }
}
