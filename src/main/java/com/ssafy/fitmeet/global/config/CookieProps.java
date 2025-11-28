package com.ssafy.fitmeet.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CookieProps {

    /**
     * SameSite 설정
     * - dev: Lax
     * - prod: None
     */
    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;

    /**
     * Secure 플래그
     * - dev: false
     * - prod: true
     */
    @Value("${app.cookie.secure:false}")
    private boolean secure;

    /**
     * Access Token 쿠키 만료 시간 (초 단위)
     */
    @Value("${app.cookie.access-max-age:1800}")
    private long accessMaxAge;

    /**
     * Refresh Token 쿠키 만료 시간 (초 단위)
     */
    @Value("${app.cookie.refresh-max-age:1209600}")
    private long refreshMaxAge;
}
