package com.hanaro.shop.controller.admin;

import com.hanaro.shop.dto.request.SignInRequest;
import com.hanaro.shop.dto.response.TokenResponse;
import com.hanaro.shop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SignIn - 관리자 로그인", description = "관리자 로그인 API")
public class AdminAuthController {
    
    private final AuthService authService;

    // 관리자 아이디/패스워드 - hanaro/12345678 이메일 vaildation 미적용
    @PostMapping("/signin")
    @Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인합니다")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request) {
        TokenResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }
}