package com.wagglex2.waggle.domain.application.dto.request;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.user.entity.User;

/**
 * 과제 및 스터디 공고 지원 요청 DTO
 *
 * <p>과제 공고와 스터디 공고는 지원 시 공통으로 필요한 정보만 있으므로,
 * 이 DTO로 두 공고 타입을 모두 처리한다.
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
public class ApplicationSimpleRequestDto extends ApplicationCommonRequestDto {

    public ApplicationSimpleRequestDto(
            RecruitmentCategory category, MeetingType meetingType,
            Integer grade, String content
    ) {
        super(category, meetingType, grade, content);
    }

    @Override
    public Application toEntity(User applicant, BaseRecruitment recruitment) {
        return Application.builder()
                .applicant(applicant)
                .recruitment(recruitment)
                .meetingType(this.meetingType)
                .grade(this.grade)
                .content(this.content)
                .build();
    }
}
