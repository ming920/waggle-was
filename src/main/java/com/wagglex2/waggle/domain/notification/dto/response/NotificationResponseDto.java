package com.wagglex2.waggle.domain.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.type.NotificationType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponseDto(
        Long notificationId,
        Long applicationId,
        RecruitmentCategory category,
        String senderNickname,
        NotificationType type,

        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDateTime createdAt,

        boolean isRead
) { }
