package com.wagglex2.waggle.domain.user.dto.response;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.User;

import java.util.Set;

/**
 * - 타 사용자에게 공개 가능한 유저 정보를 내려주는 응답 DTO.
 * - 자신의 정보(UserResponseDto)와 달리 개인정보나 민감한 정보는 포함하지 않는다.
 * - 프로필 화면, 게시물 작성자 정보 노출 등 “다른 사용자가 보는 정보”에 사용된다.
 *
 * 포함 필드:
 *  • nickname        : 사용자 닉네임
 *  • profileImageUrl : 프로필 이미지 URL
 *  • positionType    : 포지션(백엔드, 프론트엔드 등)
 *  • skills          : 선언한 기술 스택 목록 (Enum Set)
 *  • shortIntro      : 짧은 자기소개
 */
public record PublicUserResponseDto(
        String nickname,
        String profileImageUrl,
        PositionType positionType,
        Set<Skill> skills,
        String shortIntro
) {
    public static PublicUserResponseDto from(User user) {
        return new PublicUserResponseDto(
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getPosition(),
                user.getSkills(),
                user.getShortIntro()
        );
    }
}
