package com.wagglex2.waggle.domain.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentSummaryResponseDto extends BaseRecruitmentSummaryResponseDto {

    private final String department;
    private final String lecture;
    private final String lectureCode;
    private final Set<Integer> grades;

    public AssignmentSummaryResponseDto(
            Long id, Long authorId, String authorNickname, String authorProfileImageUrl,
            University university, RecruitmentCategory category, String title,
            LocalDateTime deadline, RecruitmentStatus status,
            String department, String lecture, String lectureCode,
            Set<Integer> grades, boolean isBookmarked, Long bookmarkId
    ) {
        super(id, authorId, authorNickname, authorProfileImageUrl, university, category, title, deadline, status, isBookmarked, bookmarkId);
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
        this.grades = grades;
    }
}
