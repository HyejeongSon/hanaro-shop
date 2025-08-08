package com.hanaro.shop.service;

import com.hanaro.shop.dto.request.SignInRequest;
import com.hanaro.shop.dto.request.SignUpRequest;
import com.hanaro.shop.dto.request.TokenRefreshRequest;
import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.dto.response.TokenResponse;

public interface AuthService {

    // 회원가입
    MemberResponse signUp(SignUpRequest request);

    // 로그인
    TokenResponse signIn(SignInRequest request);

    // 토큰 재발급
    TokenResponse refreshToken(TokenRefreshRequest request);

    // 로그아웃
    void signOut(Long memberId);

    // 관리자 계정 초기화
    void initializeAdminAccount();
}