package com.hanaro.shop.controller;

import com.hanaro.shop.dto.request.SignInRequest;
import com.hanaro.shop.dto.request.SignUpRequest;
import com.hanaro.shop.dto.request.TokenRefreshRequest;
import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.dto.response.TokenResponse;
import com.hanaro.shop.security.CustomUserDetails;
import com.hanaro.shop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

//    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성합니다")
    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        MemberResponse response = authService.signUp(request);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다")
    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
        TokenResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃시키고 리프레시 토큰을 삭제합니다")
    @PostMapping("/signout")
    public ResponseEntity<String> signOut(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.signOut(userDetails.getMemberId());
        return ResponseEntity.ok("로그아웃이 완료되었습니다");
    }

//    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberResponse response = MemberResponse.builder()
                .id(userDetails.getMemberId())
                .email(userDetails.getUsername())
                .name(userDetails.getName())
                .nickname(userDetails.getNickname())
                .role(userDetails.getRole())
                .build();
        
        return ResponseEntity.ok(response);
    }
}