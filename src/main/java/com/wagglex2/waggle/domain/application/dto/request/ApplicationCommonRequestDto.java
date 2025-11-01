package com.wagglex2.waggle.domain.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공고 지원 요청의 공통 데이터를 담는 추상 DTO
 *
 * <p>프로젝트, 과제, 스터디 공고 지원 시 공통으로 필요한 필드를 정의하며,
 * 공고 타입에 따라 {@link ApplicationProjectRequestDto}와 {@link ApplicationSimpleRequestDto}로 구분된다.
 * 생성된 DTO를 {@link Application} 엔티티로 변환할 수 있다.
 * </p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <li>{@link #category} - 공고 카테고리</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #grade} - 학년</li>
 *     <li>{@link #content} - 지원서 본문</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>하위 클래스 1: {@link ApplicationProjectRequestDto} (프로젝트 공고 지원용)</li>
 *     <li>하위 클래스 2: {@link ApplicationSimpleRequestDto} (과제, 스터디 공고 지원용)</li>
 * </ul>
 *
 * <p>JSON 요청 시 {@code category} 필드를 통해 자동으로 적절한 DTO로 역직렬화된다.
 *
 * @see ApplicationProjectRequestDto
 * @see ApplicationSimpleRequestDto
 * @see Application
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "category",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationProjectRequestDto.class, name = "PROJECT"),
        @JsonSubTypes.Type(value = ApplicationSimpleRequestDto.class, names = {"ASSIGNMENT", "STUDY"})
})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ApplicationCommonRequestDto {

    @Getter
    @NotNull(message = "지원하는 공고의 카테고리가 누락되었습니다.")
    private final RecruitmentCategory category;

    @NotNull(message = "선호하는 진행 방식이 누락되었습니다.")
    protected final MeetingType meetingType;

    @NotNull(message = "학년이 누락되었습니다.")
    @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
    @Max(value = 4, message = "학년은 4 이하여야 합니다.")
    protected final Integer grade;

    @NotBlank(message = "지원서 본문이 누락되었습니다.")
    protected final String content;

    public abstract Application toEntity(User applicant, BaseRecruitment recruitment);
}
