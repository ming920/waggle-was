package com.wagglex2.waggle.domain.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponseDto {

    private final Long notificationId;
    private final Long applicationId;
    private final RecruitmentCategory category;
    private final String senderNickname;
    private NotificationType type;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private final LocalDateTime createdAt;

    private final boolean isRead;

    public NotificationResponseDto(
            Long notificationId, Long applicationId, RecruitmentCategory category,
            String senderNickname, ApplicationStatus applicationStatus,
            LocalDateTime createdAt, boolean isRead
    ) {
        this.notificationId = notificationId;
        this.applicationId = applicationId;
        this.category = category;
        this.senderNickname = senderNickname;
        this.createdAt = createdAt;
        this.isRead = isRead;
        setType(applicationStatus);
    }

    private void setType(ApplicationStatus applicationStatus) {
        switch (applicationStatus) {
            case SUBMITTED ->  this.type = NotificationType.APPLICATION_SUBMITTED;
            case ACCEPTED -> this.type = NotificationType.APPLICATION_ACCEPTED;
            case REJECTED -> this.type = NotificationType.APPLICATION_REJECTED;
        }
    }
}
