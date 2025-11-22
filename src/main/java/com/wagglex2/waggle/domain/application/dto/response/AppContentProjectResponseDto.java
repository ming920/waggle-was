package com.wagglex2.waggle.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 프로젝트 공고 지원 응답 DTO이다.
 *
 * <p>프로젝트 공고 전용 필드인 포지션과 기술 스택을 포함하며,
 * {@link AppContentCommonResponseDto}를 상속한다.
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <b>상위 클래스 필드</b>
 *     <li>{@link #applicationId} - 지원 ID</li>
 *     <li>{@link #applicantId} - 지원자 ID</li>
 *     <li>{@link #nickname} - 지원자 닉네임</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 지원자 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 *     <li>{@link #appliedAt} - 지원 일자</li>
 *     <br>
 *     <b>현재 클래스 필드</b>
 *     <li>{@link #position} - 지원 포지션</li>
 *     <li>{@link #skills} - 지원자 기술 스택</li>
 * </ul>
 *
 * @see AppContentCommonResponseDto
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppContentProjectResponseDto extends AppContentCommonResponseDto {

    private final PositionType position;
    private final Set<Skill> skills;

    public AppContentProjectResponseDto(
            Long applicationId, Long applicantId, String nickname,
            MeetingType meetingType, Integer grade, String content,
            LocalDateTime appliedAt, PositionType position, Set<Skill> skills
    ) {
        super(applicationId, applicantId, nickname, meetingType, grade, content, appliedAt);
        this.position = position;
        this.skills = skills;
    }
}
