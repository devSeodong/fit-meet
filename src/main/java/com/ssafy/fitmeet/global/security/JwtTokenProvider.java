package com.ssafy.fitmeet.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 담당
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    @Value("${jwt.access-token-validity-in-ms:3600000}")
    private long accessTokenValidityInMs;
    
    @Value("${jwt.refresh-token-validity-in-ms:1209600000}")
    private long refreshTokenValidityInMs;

    private Key key;

    @PostConstruct
    public void init() {
        // HS256 서명에 사용할 키 생성
        this.key = Keys.hmacShaKeyFor(secretKeyPlain.getBytes());
    }

    /**
     * 액세스 토큰 생성
     * @param email   사용자 이메일 (username)
     * @param role    ROLE_USER / ROLE_ADMIN 등
     */
    public String createAccessToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String createRefreshToken(String email) {
    	Claims claims = Jwts.claims().setSubject(email);
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 예외 안 나면 유효
            return true;
        } catch (ExpiredJwtException e) {
            // 만료
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 변조, 형식 오류 등
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
    
    public long getAccessTokenValidityInMs() {
        return accessTokenValidityInMs;
    }

    public long getRefreshTokenValidityInMs() {
        return refreshTokenValidityInMs;
    }
}
