package com.ssafy.fitmeet.domain.auth.web;

import com.ssafy.fitmeet.domain.auth.dto.AuthDto.LoginRequest;
import com.ssafy.fitmeet.domain.auth.dto.AuthDto.LoginResponse;
import com.ssafy.fitmeet.domain.auth.dto.AuthDto.SignUpRequest;
import com.ssafy.fitmeet.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
	 * 로그인 뷰가 다른 도메인/포트에서 돌아가면 sameSite("None").secure(true) + CORS 설정까지 맞춰줘야 하는 포인트
	 */
	@PostMapping("/login")
	@Operation(summary = "로그인", description = "이메일/비밀번호로 로그인 후 JWT 액세스 토큰을 반환")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
		LoginResponse loginRes = authService.login(request);

		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", loginRes.accessToken()).httpOnly(true)
				.path("/").maxAge(1800).sameSite("Lax").build();

		ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", loginRes.refreshToken()).httpOnly(true)
				.path("/api/auth").maxAge(1209600).sameSite("Lax").build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		return ResponseEntity
				.ok(new LoginResponse(null, null, loginRes.tokenType(), loginRes.issuedAt(), loginRes.expiresAt()));
	}

	/**
	 * 새로고침 JWT 토큰
	 */
	@PostMapping("/refresh")
	@Operation(summary = "새로고침", description = "")
	public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("REFRESH_TOKEN".equals(cookie.getName())) {
					refreshToken = cookie.getValue();
					break;
				}
			}
		}

		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String newAccessToken = authService.regenerateAccessTokenByRefreshToken(refreshToken);

		ResponseCookie accessCookie = ResponseCookie
				.from("ACCESS_TOKEN", newAccessToken)
				.httpOnly(true)
				.path("/")
				.maxAge(1800)
				.sameSite("Lax")
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

		return ResponseEntity.ok().build();
	}
	
	/**
	 * 로그아웃
	 */
	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		
		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", "")
	            .httpOnly(true)
	            .path("/")
	            .maxAge(0)      // 즉시 만료
	            .sameSite("Lax")
	            .build();

	    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", "")
	            .httpOnly(true)
	            .path("/api/auth")
	            .maxAge(0)
	            .sameSite("Lax")
	            .build();

	    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
	    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

	    // 향후: DB에 저장된 refreshToken 무효화까지 하면 더 안전

	    return ResponseEntity.ok().build();
	}
}
