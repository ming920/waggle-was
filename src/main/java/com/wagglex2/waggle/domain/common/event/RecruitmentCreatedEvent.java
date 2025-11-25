package com.wagglex2.waggle.domain.common.event;

public record RecruitmentCreatedEvent(
        Long userId,
        Long recruitmentId
) {
}
