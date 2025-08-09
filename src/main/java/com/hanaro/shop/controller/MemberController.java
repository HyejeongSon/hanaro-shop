package com.hanaro.shop.controller;

import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.security.CustomUserDetails;
import com.hanaro.shop.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "내 정보 관리", description = "개인정보 수정, 비밀번호 변경, 회원 탈퇴")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
    @GetMapping
    public ResponseEntity<MemberResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberResponse response = memberService.getMemberById(userDetails.getMemberId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공")
    @PutMapping
    public ResponseEntity<MemberResponse> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotBlank String name,
            @RequestParam @NotBlank String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address) {
        
        MemberResponse response = memberService.updateMember(
                userDetails.getMemberId(), name, nickname, phone, address
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공")
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotBlank String currentPassword,
            @RequestParam @NotBlank String newPassword) {
        
        memberService.changePassword(userDetails.getMemberId(), currentPassword, newPassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다");
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "계정 삭제 성공")
    @DeleteMapping
    public ResponseEntity<String> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.ok("계정이 삭제되었습니다");
    }

}