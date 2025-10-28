package com.wagglex2.waggle.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 수정 요청 DTO
 */
public record ReviewUpdateRequestDto(
        @NotBlank(message = "후기 내용이 누락되었습니다.")
        @Size(max = 100, message = "내용은 100자 이내로 입력해주세요.")
        String content
) {
}
