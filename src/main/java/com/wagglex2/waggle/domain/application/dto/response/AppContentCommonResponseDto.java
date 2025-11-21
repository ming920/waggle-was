package com.wagglex2.waggle.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공고 지원 응답의 공통 데이터를 담는 추상 DTO이다.
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <li>{@link #applicationId} - 지원 ID</li>
 *     <li>{@link #applicantId} - 지원자 ID</li>
 *     <li>{@link #nickname} - 지원자 닉네임</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 지원자 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 *     <li>{@link #appliedAt} - 지원 일자</li>
 * </ul>
 *
 * @see AppContentProjectResponseDto
 * @see AppContentSimpleResponseDto
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AppContentCommonResponseDto {

    private Long applicationId;
    private Long applicantId;
    private String nickname;
    // TODO profile image
    private final MeetingType meetingType;
    private final Integer grade;
    private final String content;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private final LocalDateTime appliedAt;
}
