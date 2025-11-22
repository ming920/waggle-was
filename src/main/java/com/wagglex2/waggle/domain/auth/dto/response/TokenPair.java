package com.wagglex2.waggle.domain.auth.dto.response;

public record TokenPair(
        Long userId,
        String accessToken,
        String refreshToken) {
}
