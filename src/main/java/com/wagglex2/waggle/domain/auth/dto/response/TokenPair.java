package com.wagglex2.waggle.domain.auth.dto.response;


/**
 * 로그인 시 발급되는 토큰 묶음.
 *
 * - userId: 로그인한 사용자 ID
 * - accessToken: Authorization 헤더(Bearer ...)에 넣어 응답할 토큰
 * - refreshToken: 컨트롤러에서 HttpOnly 쿠키로 내려보낼 토큰
 *
 */
public record TokenPair(
        Long userId,
        String accessToken,
        String refreshToken) {
}
