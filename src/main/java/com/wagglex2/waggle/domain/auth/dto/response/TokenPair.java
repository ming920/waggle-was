package com.wagglex2.waggle.domain.auth.dto.response;


/**
 * 로그인 시 발급되는 토큰 묶음.
 *
 * - accessToken: Authorization 헤더(Bearer ...)에 넣어 응답할 토큰
 * - refreshToken: 컨트롤러에서 HttpOnly 쿠키로 내려보낼 토큰
 *
 */
public record TokenPair(
        String accessToken,
        String refreshToken) {
}
