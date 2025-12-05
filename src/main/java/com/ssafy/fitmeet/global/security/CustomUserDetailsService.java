package com.ssafy.fitmeet.global.security;

import com.ssafy.fitmeet.domain.user.dao.UserDao;
import com.ssafy.fitmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security가 인증할 때 사용하는 UserDetailsService 구현체
 *  - 이메일을 username 으로 사용
 *  - DB에서 User 조회 후, Security용 UserDetails 객체로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 조회
        User user = userDao.findByEmail(email);

        // 존재하지 않거나, 비활성/탈퇴 상태인 경우 예외
        if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UsernameNotFoundException("User not found or inactive: " + email);
        }

        // 권한 목록 구성 (ROLE_USER, ROLE_ADMIN 등)
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(user.getRole()));

        return new CustomUserDetails(user, authorities);
    }
}
