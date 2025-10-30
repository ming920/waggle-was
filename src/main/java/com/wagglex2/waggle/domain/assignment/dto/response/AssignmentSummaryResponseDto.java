package com.wagglex2.waggle.domain.assignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.ParticipantInfoResponseDto;
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
    private final ParticipantInfoResponseDto participants;
    private final Set<Integer> grades;

    protected AssignmentSummaryResponseDto(
            Long id, Long authorId, String authorNickname,
            RecruitmentCategory category, University university, String title,
            LocalDateTime deadline, RecruitmentStatus status,
            String department, String lecture, String lectureCode,
            ParticipantInfoResponseDto participants, Set<Integer> grades
    ) {
        super(id, authorId, authorNickname, university, category, title, deadline, status);
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
        this.participants = participants;
        this.grades = grades;
    }
}
