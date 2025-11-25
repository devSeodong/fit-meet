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
    private final UserRefreshTokenDao userRefreshTokenDao;

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
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String email = principal.getUsername();

        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new IllegalStateException("유저를 찾을 수 없습니다.");
        }

        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        String accessToken = jwtTokenProvider.createAccessToken(email, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        long refreshValidityMs = jwtTokenProvider.getRefreshTokenValidityInMs();
        LocalDateTime refreshExpiresAt = LocalDateTime.now().plusNanos(refreshValidityMs * 1_000_000);

        UserRefreshToken stored = userRefreshTokenDao.findByUserId(user.getId());
        if (stored == null) {
            stored = new UserRefreshToken();
            stored.setUserId(user.getId());
            stored.setRefreshToken(refreshToken);
            stored.setExpiresAt(refreshExpiresAt);
            userRefreshTokenDao.insert(stored);
        } else {
            stored.setRefreshToken(refreshToken);
            stored.setExpiresAt(refreshExpiresAt);
            userRefreshTokenDao.update(stored);
        }

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
    @Transactional
    public String regenerateAccessTokenByRefreshToken(String refreshToken) {
        // 1. JWT 서명/만료 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
        }

        // 2. 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmail(refreshToken);
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new IllegalStateException("유저를 찾을 수 없습니다.");
        }

        // 3. DB에 저장된 리프레시 토큰 조회
        UserRefreshToken stored = userRefreshTokenDao.findByUserId(user.getId());
        if (stored == null) {
            throw new IllegalStateException("저장된 리프레시 토큰 정보가 없습니다.");
        }

        // 4. DB에 저장된 토큰과 일치 여부 확인
        if (!stored.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 5. 만료 시간 확인
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다.");
        }

        // 6. 새 액세스 토큰 발급 (리프레시는 그대로 사용)
        return jwtTokenProvider.createAccessToken(email, user.getRole());
    }

    @Transactional
    public void logout(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return;
        }
        userRefreshTokenDao.deleteByUserId(user.getId());
    }

}
