package com.wagglex2.waggle.domain.study.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.dto.response.BaseRecruitmentDetailResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.ParticipantInfoResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.PeriodResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyDetailResponseDto extends BaseRecruitmentDetailResponseDto {
    private final ParticipantInfoResponseDto participants;
    private final Set<Skill> skills;
    private final PeriodResponseDto period;

    private StudyDetailResponseDto(
            Long id, Long authorId, String authorNickname, String authorProfileImageUrl, RecruitmentCategory category,
            University university, String title, String content, LocalDateTime deadline,
            LocalDateTime createdAt, RecruitmentStatus status, int viewCount,
            ParticipantInfoResponseDto participants, Set<Skill> skills, PeriodResponseDto period,
            boolean isBookmarked, Long bookmarkId
    ) {
        super(id, authorId, authorNickname, authorProfileImageUrl, category, university, title, content, deadline, createdAt, status, viewCount, isBookmarked, bookmarkId);
        this.participants = participants;
        this.skills = skills;
        this.period = period;
    }

    public static StudyDetailResponseDto fromEntity(Study study, boolean isBookmarked, Long bookmarkId) {

        User author = study.getUser();
        ParticipantInfoResponseDto participants = ParticipantInfoResponseDto.from(study.getParticipants());
        PeriodResponseDto period = PeriodResponseDto.from(study.getPeriod());
        Set<Skill> skills = Set.copyOf(study.getSkills());

        return new StudyDetailResponseDto(
                study.getId(), author.getId(), author.getNickname(), author.getProfileImageUrl(), study.getCategory(), author.getUniversity(),
                study.getTitle(), study.getContent(), study.getDeadline(), study.getCreatedAt(),
                study.getStatus(), study.getViewCount() + 1, participants, skills, period,
                isBookmarked, bookmarkId
        );
    }
}