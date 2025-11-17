package com.wagglex2.waggle.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;

public record UsernameCheckRequestDto(
        @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$",
                message = "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다.")
        String username
) {
}
