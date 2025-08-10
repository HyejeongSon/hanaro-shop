package com.hanaro.shop.service;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.RefreshToken;
import com.hanaro.shop.domain.Role;
import com.hanaro.shop.dto.request.SignInRequest;
import com.hanaro.shop.dto.request.SignUpRequest;
import com.hanaro.shop.dto.request.TokenRefreshRequest;
import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.dto.response.TokenResponse;
import com.hanaro.shop.repository.MemberRepository;
import com.hanaro.shop.repository.RefreshTokenRepository;
import com.hanaro.shop.security.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    @Override
    public MemberResponse signUp(SignUpRequest request) {
        // 비밀번호 확인 검증
        if (!request.isPasswordMatched()) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다");
        }


        // 회원 생성
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Role.USER) // 기본값은 USER
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("새 회원 가입: {}", savedMember.getEmail());

        return MemberResponse.from(savedMember);
    }

    // 로그인
    @Override
    public TokenResponse signIn(SignInRequest request) {
        try {
            // 1. 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. 인증된 사용자 정보 조회
            Member member = memberRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다"));

            // 3. JWT 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(
                    member.getId(), 
                    Collections.singletonList("ROLE_" + member.getRole().name())
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

            // 4. 리프레시 토큰 DB 저장
            refreshTokenRepository.findByMember(member)
                    .ifPresentOrElse(
                            existingToken -> existingToken.updateToken(refreshToken),
                            () -> refreshTokenRepository.save(new RefreshToken(member, refreshToken))
                    );

            log.info("로그인 성공: {}", member.getEmail());

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(3600L) // 1시간
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패: {}", request.getEmail());
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다");
        }
    }

    // 토큰 재발급
    @Override
    public TokenResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. 리프레시 토큰 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다");
        }

        // 2. 토큰에서 회원 ID 추출
        Long memberId = jwtTokenProvider.getMemberIdFromRefreshToken(refreshToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 3. DB에 저장된 리프레시 토큰과 비교
        RefreshToken storedToken = refreshTokenRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰이 존재하지 않습니다"));

        if (!storedToken.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다");
        }

        // 4. 새로운 액세스 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                member.getId(),
                Collections.singletonList("ROLE_" + member.getRole().name())
        );

        log.info("토큰 재발급: {}", member.getEmail());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 리프레시 토큰 재사용
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    // 로그아웃
    @Override
    public void signOut(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // DB에서 리프레시 토큰 삭제
        refreshTokenRepository.findByMember(member)
                .ifPresent(refreshTokenRepository::delete);

        log.info("로그아웃: {}", member.getEmail());
    }

}