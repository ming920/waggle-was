package com.wagglex2.waggle.domain.auth.dto.response;

import com.wagglex2.waggle.domain.user.entity.type.UserStatus;

public record SignInResult(
        Long userId,
        String username,
        UserStatus status,
        TokenPair tokenPair
) {
}
