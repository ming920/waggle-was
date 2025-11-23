package com.wagglex2.waggle.domain.study.dto.request;

import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;

import java.util.Set;

public record StudySearchCondition(
        Set<String> keywords,
        Set<Skill> skills,
        RecruitmentStatus status
) {
    public StudySearchCondition {
        keywords = (keywords == null) ? Set.of() : Set.copyOf(keywords);
        skills = (skills == null) ? Set.of() : Set.copyOf(skills);
    }
}
