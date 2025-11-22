package com.wagglex2.waggle.domain.auth.dto.response;

import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.UserStatus;

public record SignInResponseDto(
        Long userId,
        String username,
        UserStatus status
) {
    public static SignInResponseDto fromEntity(User user) {
        return new SignInResponseDto(
                user.getId(),
                user.getUsername(),
                user.getStatus()
        );
    }
}
