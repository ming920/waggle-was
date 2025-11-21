package com.wagglex2.waggle.domain.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wagglex2.waggle.domain.application.dto.response.AppContentCommonResponseDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모집 공고와 해당 공고에 대한 지원 정보를 포함하는 DTO이다.
 */
public record RecruitmentWithAppsResponseDto(
        Long recruitmentId,
        String title,

        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDateTime deadline,

        List<? extends AppContentCommonResponseDto> applications
) { }
