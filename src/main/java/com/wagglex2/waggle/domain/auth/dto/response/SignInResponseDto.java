package com.wagglex2.waggle.domain.auth.dto.response;

import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.UserStatus;


/**
 * 로그인 성공 시 클라이언트에 반환되는 사용자 정보 DTO.
 *
 * <p>
 * 로그인 응답에는 최소한의 식별자 및 상태 정보만 포함한다.
 * 인증/인가 정보(JWT)는 헤더 및 쿠키로 전달한다.
 * </p>
 *
 * <p>
 * - userId : PK
 * - username : 로그인 아이디.
 * - status : 사용자 가입 진행 상태(INCOMPLETED, ACTIVE 등).
 * </p>
 *
 *  status가 INCOMPLETED일 때 프론트가 기본 정보 입력 모달을 띄운다.
 */
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
