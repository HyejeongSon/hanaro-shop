package com.hanaro.shop.config;

import com.hanaro.shop.repository.MemberRepository;
import com.hanaro.shop.security.CustomUserDetailsService;
import com.hanaro.shop.security.JwtAuthenticationFilter;
import com.hanaro.shop.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Security configuration started");

        http
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF 비활성화 (JWT 사용)
            .csrf(csrf -> csrf.disable())
            
            // 세션 비활성화 (JWT 사용)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능한 URL
                .requestMatchers(
                    "/api/auth/**",          // 회원가입, 로그인
                    "/api/admin/auth/signin", // 관리자 로그인
                    "/swagger-ui/**",        // Swagger UI
                    "/v3/api-docs/**",       // OpenAPI 문서
                    "/swagger-resources/**", 
                    "/webjars/**",
                    "/upload/**",
                    "/management/**",        // Actuator
                    "/error"                 // 에러 페이지
                ).permitAll()
                
                // 관리자만 접근 가능한 URL
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // 사용자만 접근 가능한 URL (장바구니, 주문 등)
                .requestMatchers("/api/user/**").hasRole("USER")
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // JWT 필터 추가
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService, memberRepository);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}