package com.ssafy.fitmeet.domain.auth.service;

import com.ssafy.fitmeet.domain.auth.dto.AuthDto;
import com.ssafy.fitmeet.domain.user.dao.UserBodyInfoDao;
import com.ssafy.fitmeet.domain.user.dao.UserDao;
import com.ssafy.fitmeet.domain.user.entity.User;
import com.ssafy.fitmeet.domain.user.entity.UserBodyInfo;
import com.ssafy.fitmeet.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ssafy.fitmeet.domain.auth.dto.AuthDto.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final UserBodyInfoDao userBodyInfoDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 + 신체정보 등록
     */
    @Transactional
    public Long signUp(SignUpRequest request) {
        // 이메일 중복 체크
        User existing = userDao.findByEmail(request.email());
        if (existing != null) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // User 저장
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .nickname(request.nickname())
                .status("ACTIVE")
                .role("ROLE_USER")
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userDao.insertUser(user);

        // 신체 정보 저장
        UserBodyInfo bodyInfo = UserBodyInfo.builder()
                .userId(user.getId())
                .heightCm(request.heightCm())
                .weightKg(request.weightKg())
                .targetWeightKg(request.targetWeightKg())
                .gender(request.gender())
                .birthDate(request.birthDate())
                .activityLevel(request.activityLevel())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userBodyInfoDao.insertBodyInfo(bodyInfo);

        return user.getId();
    }

    /**
     * 로그인 + JWT 발급
     */
    public LoginResponse login(LoginRequest request) {
        // AuthenticationManager 통해 인증 시도 (비밀번호 검증 포함)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 인증 성공 → UserDetails 에서 정보 추출
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(principal.getUsername(), role);
        String refreshToken = jwtTokenProvider.createRefreshToken(principal.getUsername());

        return new LoginResponse(
        		accessToken,
        		refreshToken,
                "Bearer",
                LocalDateTime.now(),
                null
        );
    }
    
    /**
     * 리프레시 메서드
     */
    public String regenerateAccessTokenByRefreshToken(String refreshToken) {
    	if(!jwtTokenProvider.validateToken(refreshToken)) {
    		throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
    	}
    	
    	String email = jwtTokenProvider.getEmail(refreshToken);
    	
    	// DB 저장? => user_refresh_token 테이블
    	
    	
    }
    
}
