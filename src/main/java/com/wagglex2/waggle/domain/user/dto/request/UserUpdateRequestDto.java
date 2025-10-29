package com.wagglex2.waggle.domain.user.dto.request;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import jakarta.validation.constraints.*;

import java.util.Set;

/**
 * 사용자 프로필 수정 요청 DTO.
 *
 * <p>PATCH /me 요청 시 클라이언트가 전달하는 데이터 구조를 정의한다.</p>
 * <ul>
 *   <li>닉네임 필드는 선택적으로 입력할 수 있으며 나머지 필드는 필수 입력 항목이다.</li>
 *   <li>닉네임, 학년, 포지션, 보유 기술 스택, 한 줄 소개 등을 수정 가능하다.</li>
 * </ul>
 */
public record UserUpdateRequestDto(
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
        String nickname,

        @NotNull(message = "학년은 필수 입력 항목입니다.")
        @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
        @Max(value = 4, message = "학년은 4 이하여어야 합니다.")
        Integer grade,

        @NotNull(message = "포지션은 필수 입력 항목입니다.")
        PositionType position,

        @NotEmpty(message = "기술 스택은 필수 입력 항목입니다.")
        @Size(max = 10, message = "기술 스택은 최대 10개까지 선택할 수 있습니다.")
        Set<Skill> skills,

        @NotBlank(message = "한 줄 소개는 필수 입력 항목입니다.")
        @Size(max = 100, message = "한 줄 소개는 100자를 초과할 수 없습니다.")
        String shortIntro
) {
}
