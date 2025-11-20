package com.ssafy.fitmeet.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(OpenAPI) 설정
 * - /swagger-ui.html 에서 문서 확인
 * - JWT Bearer 인증 스키마 등록
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fitMeetOpenAPI() {

        // JWT Security 스키마 정의
        SecurityScheme JwtAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)        // HTTP 타입
                .scheme("bearer")                      // Bearer 토큰
                .bearerFormat("JWT");                  // JWT 포맷

        // 전체 OpenAPI 객체 구성
        return new OpenAPI()
                .info(new Info()
                        .title("FitMeet API")
                        .description("FitMeet API 통합 문서")
                        .version("v1.0.0")
                )
                .components(
                        new Components()
                                .addSecuritySchemes("JwtAuth", JwtAuth)  // 이름: JwtAuth
                )
                // 전체 API에 기본 적용할 SecurityRequirement
                // → 이걸 빼고 개별 메서드에만 @SecurityRequirement 달 수도 있음
                .addSecurityItem(
                        new SecurityRequirement().addList("JwtAuth")
                );
    }
}
