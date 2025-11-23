package com.wagglex2.waggle.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 과제 및 스터디 공고 지원 응답 DTO이다.
 *
 * <p>공통 필드만 포함하며, {@link AppContentCommonResponseDto}를 상속한다.
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <b>상위 클래스 필드</b>
 *     <li>{@link #applicationId} - 지원 ID</li>
 *     <li>{@link #applicantId} - 지원자 ID</li>
 *     <li>{@link #nickname} - 지원자 닉네임</li>
 *     <li>{@link #profileImageUrl} - 지원자 프로필 이미지 URL</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 지원자 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 *     <li>{@link #appliedAt} - 지원 일자</li>
 * </ul>
 *
 * @see AppContentCommonResponseDto
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppContentSimpleResponseDto extends AppContentCommonResponseDto {

    public AppContentSimpleResponseDto(
            Long applicationId, Long applicantId, String nickname,
            String profileImageUrl, MeetingType meetingType,
            Integer grade, String content, LocalDateTime appliedAt
    ) {
        super(applicationId, applicantId, nickname, profileImageUrl, meetingType, grade, content, appliedAt);
    }
}
