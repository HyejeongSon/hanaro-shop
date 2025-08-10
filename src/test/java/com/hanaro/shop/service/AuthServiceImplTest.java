package com.hanaro.shop.service;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.Role;
import com.hanaro.shop.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class AuthServiceImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("관리자 계정 생성")
    void initializeAdminAccount_FirstTime_ShouldCreateAdminAccount() {
        // when
        if (!memberRepository.existsByEmail("admin@hanaro.com")) {
            Member admin = Member.builder()
                    .email("admin@hanaro.com")
                    .password(passwordEncoder.encode("admin1234!"))
                    .name("관리자")
                    .nickname("admin")
                    .phone("02-1234-5678")
                    .address("서울시 중구 을지로 하나로빌딩")
                    .role(Role.ADMIN)
                    .build();

            memberRepository.save(admin);
        }

        // then
        assertTrue(memberRepository.existsByEmail("admin@hanaro.com"));
        
        Member admin = memberRepository.findByEmail("admin@hanaro.com").get();
        assertEquals("admin@hanaro.com", admin.getEmail());
        assertEquals("관리자", admin.getName());
        assertEquals("admin", admin.getNickname());
        assertEquals("02-1234-5678", admin.getPhone());
        assertEquals("서울시 중구 을지로 하나로빌딩", admin.getAddress());
        assertEquals(Role.ADMIN, admin.getRole());
        assertTrue(passwordEncoder.matches("admin1234!", admin.getPassword()));
    }

    @Test
    @DisplayName("USER 권한 사용자 생성")
    void createUserAccounts_Should15UserWithUserRole() {
        // when - 15명의 USER 계정 생성
        for (int i = 1; i <= 15; i++) {
            String email = "user" + i + "@hanaro.com";
            
            // 이미 존재하는 경우 생성하지 않음
            if (!memberRepository.existsByEmail(email)) {
                Member user = Member.builder()
                        .email(email)
                        .password(passwordEncoder.encode("user1234!"))
                        .name("사용자" + i)
                        .nickname("user" + i)
                        .phone("010-1234-567" + (i % 10))
                        .address("서울시 성동구 " + i + "번지")
                        .role(Role.USER)
                        .build();

                memberRepository.save(user);
            }
        }

        // then - 15명이 모두 생성되었는지 확인
        for (int i = 1; i <= 15; i++) {
            String email = "user" + i + "@hanaro.com";
            assertTrue(memberRepository.existsByEmail(email));
            
            Member user = memberRepository.findByEmail(email).get();
            assertEquals(email, user.getEmail());
            assertEquals("사용자" + i, user.getName());
            assertEquals("user" + i, user.getNickname());
            assertEquals(Role.USER, user.getRole());
            assertTrue(passwordEncoder.matches("user1234!", user.getPassword()));
        }
    }
}