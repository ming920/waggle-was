package com.wagglex2.waggle.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCheckRequestDto(
        @NotBlank(message = "이메일이 누락되었습니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {
}
