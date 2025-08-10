package com.hanaro.shop.security;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // Preflight 요청은 체크하지 않음
        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI();

        // 인증이 필요 없는 경로들
        return path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/upload/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        Optional<String> token = jwtTokenProvider.resolveToken(request);

        token.ifPresent(t -> {
            try {
                if (jwtTokenProvider.validateToken(t)) {
                    Long memberId = jwtTokenProvider.getMemberIdFromAccessToken(t);
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

                    UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());

                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("사용자 인증 완료: {}", member.getEmail());
                }
            } catch (JwtException e) {
                log.debug("JWT 토큰 처리 실패: {}", e.getMessage());
            } catch (Exception e) {
                log.error("인증 필터 처리 중 오류 발생: {}", e.getMessage());
            }
        });

        chain.doFilter(request, response);
    }

}