package com.wagglex2.waggle.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 프로젝트 공고 지원 응답 DTO
 *
 * <p>프로젝트 공고 전용 필드인 포지션과 기술 스택을 포함한다.
 * {@link ApplicationCommonResponseDto}를 상속하며,
 * {@link Application} 엔티티로부터 생성될 수 있다.</p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <b>상위 클래스 필드</b>
 *     <li>{@link #applicationId} - 지원 ID</li>
 *     <li>{@link #recruitmentId} - 공고 ID</li>
 *     <li>{@link #recruitmentTitle} - 공고 제목</li>
 *     <li>{@link #recruitmentDeadline} - 공고 마감일</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 *     <li>{@link #status} - 지원 상태</li>
 *     <br>
 *     <b>현재 클래스 필드</b>
 *     <li>{@link #position} - 지원 포지션</li>
 *     <li>{@link #skills} - 기술 스택</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>상위 클래스: {@link ApplicationCommonResponseDto}</li>
 * </ul>
 *
 * @see ApplicationCommonResponseDto
 * @see Application
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationProjectResponseDto extends ApplicationCommonResponseDto {

    private final PositionType position;
    private final Set<Skill> skills;

    public ApplicationProjectResponseDto(
            Long applicationId, Long recruitmentId, String recruitmentTitle,
            LocalDateTime recruitmentDeadline, MeetingType meetingType, Integer grade,
            String content, ApplicationStatus status, PositionType position, Set<Skill> skills
    ) {
        super(applicationId, recruitmentId, recruitmentTitle, recruitmentDeadline, meetingType, grade, content, status);
        this.position = position;
        this.skills = skills;
    }

    public static ApplicationProjectResponseDto fromEntity(Application entity) {
        BaseRecruitment recruitment = entity.getRecruitment();

        return new ApplicationProjectResponseDto(
                entity.getId(), recruitment.getId(), recruitment.getTitle(),
                recruitment.getDeadline(), entity.getMeetingType(), entity.getGrade(),
                entity.getContent(), entity.getStatus(), entity.getPosition(), entity.getSkills()
        );
    }
}
