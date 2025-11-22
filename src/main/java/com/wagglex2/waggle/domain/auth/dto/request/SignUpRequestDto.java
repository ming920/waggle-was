package com.wagglex2.waggle.domain.auth.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import com.wagglex2.waggle.domain.user.entity.type.UserStatus;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

public record SignUpRequestDto(
        @NotBlank(message = "아이디가 누락되었습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다.")
        String username,

        @NotBlank(message = "닉네임이 누락되었습니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
        String nickname,

        @NotBlank(message = "이메일이 누락되었습니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호가 누락되었습니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+~])[A-Za-z\\d!@#$%^&*()_+~]{8,72}$",
                message = "비밀번호는 8자 이상 72자 이내의 영문, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @NotBlank(message = "비밀번호 확인이 누락되었습니다.")
        String passwordConfirm
) {
    public SignUpRequestDto {
        if (password != null && passwordConfirm != null && !password.equals(passwordConfirm)) {
            throw new BusinessException(ErrorCode.MISMATCHED_PASSWORD);
        }
    }

    public User toEntity(PasswordEncoder passwordEncoder, String defaultProfileImageUrl) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .university(University.fromEmail(email))
                .role(UserRoleType.ROLE_USER)
                .status(UserStatus.INCOMPLETED)
                .profileImageUrl(defaultProfileImageUrl)
                .build();
    }
}

