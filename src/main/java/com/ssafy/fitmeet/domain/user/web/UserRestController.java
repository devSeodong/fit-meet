package com.ssafy.fitmeet.domain.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.fitmeet.domain.user.dto.UserDto.ChangePasswordRequest;
import com.ssafy.fitmeet.domain.user.dto.UserDto.ProfileBodyInfoResponse;
import com.ssafy.fitmeet.domain.user.dto.UserDto.UpdateProfileRequest;
import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;
import com.ssafy.fitmeet.domain.user.service.UserService;
import com.ssafy.fitmeet.global.response.Response;
import com.ssafy.fitmeet.global.response.ResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API ( CRUD )")
@Log4j2
public class UserRestController {
	
	private final UserService userService;
	
	@PostMapping("/insert-body")
	@Operation(summary = "신체 정보 등록", description = "현재 로그인되어있는 사용자 기준 신체 정보 등록")
	public ResponseEntity<Response<?>> insertBody(@RequestBody UserBodyInfo request) {
        log.info("신체정보 : {}", request.toString());
		userService.insertBodyInfo(request);
		return ResponseEntity.ok(ResponseUtil.ok("신체 정보 등록 완료"));
	}
	
	@GetMapping("/info")
	@Operation(summary = "이메일 사용자 조회", description = " 이메일로 사용자 조회 ")
	public ResponseEntity<Response<?>> getInfo(@RequestParam("email") String email) {
		User info = userService.getUserInfoEmail(email);
		return ResponseEntity.ok(ResponseUtil.ok(info));
	}
	
	@GetMapping("/profile-info")
	@Operation(summary = "사용자 프로필 조회", description = "기본 정보 + 신체 정보")
	public ResponseEntity<Response<?>> getAllInfo() {
		ProfileBodyInfoResponse res = userService.getProfileBody();
		return ResponseEntity.ok(ResponseUtil.ok(res));
	}
	
	@PostMapping("/profile-upt")
	@Operation(summary = "프로필 수정", description = "기본정보, 신체정보 전체 수정")
	public ResponseEntity<Response<?>> profileUpt (@RequestBody UpdateProfileRequest request) {
		userService.updateProfile(request);
		return ResponseEntity.ok(ResponseUtil.ok("프로필 수정 완료"));
	}
	
	@PostMapping("/password-upt")
	@Operation(summary = "비밀번호 변경", description = "기존 비밀번호, 새 비밀번호 요청")
	public ResponseEntity<Response<?>> changePassword (@RequestBody ChangePasswordRequest request) {
		userService.changePassword(request);
		return ResponseEntity.ok(ResponseUtil.ok("비밀번호 변경 완료"));
	}
	
	@PostMapping("/signout")
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴 ( 소프트 삭제 - status, deletedAt )")
	public ResponseEntity<Response<?>> softDelete() {
		userService.softDelete();
		return ResponseEntity.ok(ResponseUtil.ok("회원 탈퇴 완료"));
	}
	
}
