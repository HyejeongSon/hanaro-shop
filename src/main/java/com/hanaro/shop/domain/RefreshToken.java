package com.hanaro.shop.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    public RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    // 토큰 업데이트 메서드
    public void updateToken(String newToken) {
        this.token = newToken;
    }
}