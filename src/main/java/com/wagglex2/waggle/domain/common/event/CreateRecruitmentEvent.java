package com.wagglex2.waggle.domain.common.event;

public record CreateRecruitmentEvent(
        Long userId,
        Long recruitmentId
) {
}
