package com.hanaro.shop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]", 
             message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하여야 합니다")
    private String name;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다")
    private String nickname;

    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", 
             message = "휴대폰 번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
    private String phone;

    private String address;

    // 비밀번호 확인 검증
    public boolean isPasswordMatched() {
        return password != null && password.equals(confirmPassword);
    }
}