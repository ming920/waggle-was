package com.wagglex2.waggle.domain.assignment.dto.request;

import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;

import java.util.Set;

public record AssignmentSearchCondition(
        Set<String> keywords,
        Set<Integer> grades,
        RecruitmentStatus status
) {
    public AssignmentSearchCondition {
        keywords = (keywords == null) ? Set.of() : Set.copyOf(keywords);
        grades = (grades == null) ? Set.of() : Set.copyOf(grades);
    }
}
