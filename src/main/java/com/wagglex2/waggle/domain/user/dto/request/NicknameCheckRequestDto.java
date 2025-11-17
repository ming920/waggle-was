package com.wagglex2.waggle.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;

public record NicknameCheckRequestDto(
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
        String nickname
) {
}
