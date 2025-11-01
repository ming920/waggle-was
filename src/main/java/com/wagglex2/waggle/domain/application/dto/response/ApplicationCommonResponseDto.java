package com.wagglex2.waggle.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공고 지원 응답의 공통 데이터를 담는 추상 DTO
 *
 * <p>프로젝트, 과제, 스터디 공고 지원 시 공통으로 반환되는 필드를 정의하며,
 * 공고 타입에 따라 {@link ApplicationProjectResponseDto}와 {@link ApplicationSimpleResponseDto}로 구분된다.
 * 엔티티 {@link Application}에서 생성될 수 있다.</p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <li>{@link #applicationId} - 지원 ID</li>
 *     <li>{@link #recruitmentId} - 공고 ID</li>
 *     <li>{@link #recruitmentTitle} - 공고 제목</li>
 *     <li>{@link #recruitmentDeadline} - 공고 마감일</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 *     <li>{@link #status} - 지원 상태</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>하위 클래스 1: {@link ApplicationProjectResponseDto} (프로젝트 공고 지원용)</li>
 *     <li>하위 클래스 2: {@link ApplicationSimpleResponseDto} (과제, 스터디 공고 지원용)</li>
 * </ul>
 *
 * @see ApplicationProjectResponseDto
 * @see ApplicationSimpleResponseDto
 * @see Application
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ApplicationCommonResponseDto {

    private Long applicationId;
    private Long recruitmentId;
    private String recruitmentTitle;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDateTime recruitmentDeadline;

    private final MeetingType meetingType;
    private final Integer grade;
    private final String content;
    private final ApplicationStatus status;
}
