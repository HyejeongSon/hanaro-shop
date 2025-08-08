package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByMember(Member member);
    
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByMember(Member member);
}