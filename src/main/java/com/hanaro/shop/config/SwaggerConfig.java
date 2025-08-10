package com.hanaro.shop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hanaro Shop API")
                        .description("하나은행 쇼핑몰 서비스 API 문서")
                        .version("v1.0.0"))
                .servers(List.of(new Server().url("/")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, createBearerScheme()));
    }

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER) // 생략 가능
                .name("Authorization") // 생략 가능
                .description("JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.");
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("관리자 API")
                .displayName("Admin API")
                .pathsToMatch("/api/admin/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Hanaro Shop - Admin API")
                                .description("관리자 전용 API")
                                .version("v1.0.0")))
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("사용자 API")
                .displayName("User API")
                .pathsToMatch("/api/cart/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Hanaro Shop - User API")
                                .description("일반 사용자 기능")
                                .version("v1.0.0")))
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("공통 API")
                .displayName("Public API")
                .pathsToMatch("/api/auth/**", "/api/member/**", "/api/product/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Hanaro Shop - Public API")
                                .description("공통 API (인증, 상품 조회, 개인정보 관리)")
                                .version("v1.0.0")))
                .build();
    }
}