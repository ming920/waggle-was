package com.wagglex2.waggle.domain.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
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
) {
    public NotificationResponseDto(
            Long notificationId, Long applicationId, RecruitmentCategory category,
            String senderNickname, ApplicationStatus applicationStatus,
            LocalDateTime createdAt, boolean isRead
    ) {
        this(
            notificationId, applicationId, category, senderNickname,
            convertStatus(applicationStatus), createdAt, isRead
        );
    }

    private static NotificationType convertStatus(ApplicationStatus applicationStatus) {
        return switch (applicationStatus) {
            case SUBMITTED ->  NotificationType.APPLICATION_SUBMITTED;
            case ACCEPTED -> NotificationType.APPLICATION_ACCEPTED;
            case REJECTED -> NotificationType.APPLICATION_REJECTED;
            default -> throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        };
    }
}
