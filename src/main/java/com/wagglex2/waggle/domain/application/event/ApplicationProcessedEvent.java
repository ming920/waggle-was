package com.wagglex2.waggle.domain.application.event;

import com.wagglex2.waggle.domain.notification.type.NotificationType;

public record ApplicationProcessedEvent(
        Long senderId,
        Long receiverId,
        Long applicationId,
        NotificationType type
) { }
