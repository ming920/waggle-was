package com.wagglex2.waggle.domain.application.dto.request;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * 프로젝트 공고 지원 요청 DTO
 *
 * <p>프로젝트 공고에만 필요한 포지션 정보와 기술 스택을 추가로 담는다.
 * {@link ApplicationCommonRequestDto}를 상속하며, 생성된 DTO를 {@link Application} 엔티티로 변환할 수 있다.
 * </p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <b>상위 클래스 필드</b>
 *     <li>{@link #category} - 공고 카테고리</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 *     <br>
 *     <b>현재 클래스 필드</b>
 *     <li>{@link #position} - 지원 포지션</li>
 *     <li>{@link #skills} - 기술 스택</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>상위 클래스: {@link ApplicationCommonRequestDto}</li>
 * </ul>
 *
 * @see ApplicationCommonRequestDto
 * @see Application
 */
public class ApplicationProjectRequestDto extends ApplicationCommonRequestDto {

    @NotNull(message = "지원하는 포지션 정보가 누락되었습니다.")
    private final PositionType position;

    @NotEmpty(message = "기술 스택이 누락되었습니다.")
    private final Set<Skill> skills;

    public ApplicationProjectRequestDto(
            RecruitmentCategory category, MeetingType meetingType, Integer grade,
            String content, PositionType position, Set<Skill> skills
    ) {
        super(category, meetingType, grade, content);
        this.position = position;
        this.skills = skills;
    }

    @Override
    public Application toEntity(User applicant, BaseRecruitment recruitment) {
        return Application.builder()
                .applicant(applicant)
                .recruitment(recruitment)
                .meetingType(this.meetingType)
                .grade(this.grade)
                .content(this.content)
                .position(this.position)
                .skills(this.skills)
                .build();
    }
}
