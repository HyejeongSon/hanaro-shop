package com.hanaro.shop.controller.admin;

import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@Tag(name = "Admin - 회원 관리", description = "관리자 전용 회원 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController {

    private final MemberService memberService;

    @Operation(summary = "전체 회원 목록 조회", description = "관리자만 접근 가능한 전체 회원 목록을 조회합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponse> members = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "회원 정보 조회", description = "관리자가 특정 회원 정보를 조회합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공")
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long memberId) {
        MemberResponse response = memberService.getMemberById(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 삭제", description = "관리자가 회원을 삭제합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "회원 삭제 성공")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok("회원이 삭제되었습니다");
    }
}