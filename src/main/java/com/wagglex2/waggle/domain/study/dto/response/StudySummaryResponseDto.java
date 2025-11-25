package com.wagglex2.waggle.domain.study.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudySummaryResponseDto extends BaseRecruitmentSummaryResponseDto {

    private final Set<Skill> skills;

    public StudySummaryResponseDto(
            Long id, Long authorId, String authorNickname, String authorProfileImageUrl,
            University university, RecruitmentCategory category, String title,
            LocalDateTime deadline, RecruitmentStatus status,
            Set<Skill> skills,
            boolean isBookmarked, Long bookmarkId
    ) {
        super(id, authorId, authorNickname, authorProfileImageUrl, university, category, title, deadline, status, isBookmarked, bookmarkId);
        this.skills = Set.copyOf(skills);
    }
}
