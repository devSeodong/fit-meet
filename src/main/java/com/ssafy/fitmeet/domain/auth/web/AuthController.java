package com.ssafy.fitmeet.domain.auth.web;

import com.ssafy.fitmeet.domain.auth.dto.AuthDto.LoginRequest;
import com.ssafy.fitmeet.domain.auth.dto.AuthDto.LoginResponse;
import com.ssafy.fitmeet.domain.auth.dto.AuthDto.SignUpRequest;
import com.ssafy.fitmeet.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API (회원가입, 로그인 등)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API (회원가입, 로그인 등)")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 기본정보, 신체정보 입력 회원가입")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        Long userId = authService.signUp(request);
        return ResponseEntity.ok(userId);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인 후 JWT 액세스 토큰을 반환")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
