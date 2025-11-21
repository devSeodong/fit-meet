package com.ssafy.fitmeet.domain.auth.service;

import com.ssafy.fitmeet.domain.auth.dao.UserRefreshTokenDao;
import com.ssafy.fitmeet.domain.auth.dto.AuthDto;
import com.ssafy.fitmeet.domain.auth.entity.UserRefreshToken;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.ssafy.fitmeet.domain.auth.dto.AuthDto.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final UserBodyInfoDao userBodyInfoDao;
    private final UserRefreshTokenDao userRefreshTokenDao;
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
     * 리프레시 토큰으로 액세스 토큰 재발급 (+ 옵션: 리프레시 토큰 회전)
     */
    @Transactional
    public AuthDto.LoginResponse refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new IllegalStateException("유저를 찾을 수 없습니다.");
        }

        UserRefreshToken stored = userRefreshTokenDao.findByUserId(user.getId());
        if (stored == null) {
            throw new IllegalStateException("저장된 리프레시 토큰 정보가 없습니다.");
        }

        // DB에 저장된 토큰과 일치하는지 확인
        if (!stored.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다.");
        }

        // 새 액세스 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());

        // (선택) 리프레시 토큰 회전: 새로 발급해서 DB/쿠키 업데이트
//        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);
//        long refreshValidityMs = jwtTokenProvider.getRefreshTokenValidityInMs();
//        LocalDateTime newRefreshExpiresAt = LocalDateTime.ofInstant(
//                Instant.ofEpochMilli(System.currentTimeMillis() + refreshValidityMs),
//                ZoneId.systemDefault()
//        );

        stored.setRefreshToken(newRefreshToken);
        stored.setExpiresAt(newRefreshExpiresAt);
        userRefreshTokenDao.update(stored);

        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime accessExpiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + jwtTokenProvider.getAccessTokenValidityInMs()),
                ZoneId.systemDefault()
        );

        return new AuthDto.LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                issuedAt,
                accessExpiresAt
        );
    }
    
    @Transactional
    public void logout(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) return;
        
        userRefreshTokenDao.deleteByUserId(user.getId());
    }
    
}
