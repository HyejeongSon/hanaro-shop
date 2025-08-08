package com.hanaro.shop.service;

import com.hanaro.shop.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    
    // 회원 조회
    MemberResponse getMemberById(Long memberId);
    
    MemberResponse getMemberByEmail(String email);
    
    // 관리자용 회원 목록 조회
    Page<MemberResponse> getAllMembers(Pageable pageable);
    
    // 회원 정보 수정
    MemberResponse updateMember(Long memberId, String name, String nickname, String phone, String address);
    
    // 비밀번호 변경
    void changePassword(Long memberId, String currentPassword, String newPassword);
    
    // 회원 삭제
    void deleteMember(Long memberId);
    
    // 이메일 중복 확인
    boolean existsByEmail(String email);
}