package com.wagglex2.waggle.domain.auth.dto.request;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UserBasicInfoRequestDto(
        @NotNull(message = "학년이 누락되었습니다.")
        @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
        @Max(value = 4, message = "학년은 4 이하이어야 합니다.")
        Integer grade,

        @NotNull(message = "포지션이 누락되었습니다.")
        PositionType position,

        @NotEmpty(message = "기술 스택이 누락되었습니다.")
        @Size(max = 10, message = "기술 스택은 최대 10개까지 선택할 수 있습니다.")
        Set<Skill> skills,

        @NotBlank(message = "한 줄 소개가 누락되었습니다.")
        @Size(max = 100, message = "한 줄 소개는 100자를 초과할 수 없습니다.")
        String shortIntro
) {
}
