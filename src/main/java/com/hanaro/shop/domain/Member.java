package com.hanaro.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일은 필수입니다")
    // @Email(message = "올바른 이메일 형식이 아닙니다")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "닉네임은 필수입니다")
    @Column(nullable = false)
    private String nickname;

    @Column(length = 20)
    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    // 비밀번호 업데이트 메서드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 회원 정보 업데이트 메서드
    public void updateProfile(String name, String nickname, String phone, String address) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }

}