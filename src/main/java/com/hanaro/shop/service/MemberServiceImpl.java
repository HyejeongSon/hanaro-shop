package com.hanaro.shop.service;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.Role;
import com.hanaro.shop.dto.response.MemberResponse;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import com.hanaro.shop.repository.MemberRepository;
import com.hanaro.shop.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        return MemberResponse.from(member);
    }

    @Override
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        return MemberResponse.from(member);
    }

    @Override
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        // 관리자 제외하고 일반 사용자만 조회
        Page<Member> members = memberRepository.findByRole(Role.USER, pageable);
        return members.map(MemberResponse::from);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long memberId, String name, String nickname, String phone, String address) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateProfile(name, nickname, phone, address);
        Member updatedMember = memberRepository.save(member);
        
        log.info("회원 정보 수정: {}", member.getEmail());
        return MemberResponse.from(updatedMember);
    }

    @Override
    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호로 변경
        member.updatePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        
        log.info("비밀번호 변경: {}", member.getEmail());
    }


    @Override
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 1. 리프레시 토큰 삭제
        refreshTokenRepository.findByMember(member)
                .ifPresent(refreshTokenRepository::delete);

        // 2. 회원 삭제
        memberRepository.delete(member);
        
        log.info("회원 삭제: {}", member.getEmail());
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}