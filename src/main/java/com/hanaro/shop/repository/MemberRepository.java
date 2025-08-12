package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    // 관리자용 일반 사용자만 조회
    Page<Member> findByRole(Role role, Pageable pageable);
}