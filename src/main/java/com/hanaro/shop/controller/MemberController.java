package com.hanaro.shop.controller;

import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.security.CustomUserDetails;
import com.hanaro.shop.service.MemberService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
//@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

//    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberResponse response = memberService.getMemberById(userDetails.getMemberId());
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다")
    @PutMapping("/me")
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

//    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다")
    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotBlank String currentPassword,
            @RequestParam @NotBlank String newPassword) {
        
        memberService.changePassword(userDetails.getMemberId(), currentPassword, newPassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다");
    }

//    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.ok("계정이 삭제되었습니다");
    }

    // 관리자 전용 API
//    @Operation(summary = "전체 회원 목록 조회", description = "관리자만 접근 가능한 전체 회원 목록을 조회합니다")
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberResponse>> getAllMembers(Pageable pageable) {
        Page<MemberResponse> members = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

//    @Operation(summary = "회원 정보 조회", description = "관리자가 특정 회원 정보를 조회합니다")
    @GetMapping("/admin/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long memberId) {
        MemberResponse response = memberService.getMemberById(memberId);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "회원 삭제", description = "관리자가 회원을 삭제합니다")
    @DeleteMapping("/admin/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok("회원이 삭제되었습니다");
    }
}