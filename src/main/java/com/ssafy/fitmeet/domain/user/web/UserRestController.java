package com.ssafy.fitmeet.domain.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.fitmeet.domain.auth.service.AuthService;
import com.ssafy.fitmeet.domain.auth.web.AuthRestController;
import com.ssafy.fitmeet.domain.user.dto.UserDto.UpdateProfileRequest;
import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.service.UserService;
import com.ssafy.fitmeet.global.response.Response;
import com.ssafy.fitmeet.global.response.ResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
	
	@GetMapping("/info")
	@Operation(summary = "이메일 사용자 조회", description = " 로그인, 이메일 중복 체크 ")
	public ResponseEntity<Response<?>> getInfo(@RequestParam("email") String email) {
		User info = userService.getUserInfoEmail(email);
		return ResponseEntity.ok(ResponseUtil.ok(info));
	}
	
	@PostMapping("/profile-upt")
	@Operation(summary = "프로필 수정 ( +신체정보 )", description = "기본정보, 신체정보 전체 수정")
	public ResponseEntity<Response<?>> profileUpt (@RequestBody UpdateProfileRequest request) {
		userService.updateProfile(request);
		
		return ResponseEntity.ok(ResponseUtil.ok("프로필 수정 완료"));
	}
	
	
}
