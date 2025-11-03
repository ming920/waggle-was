package com.wagglex2.waggle.domain.notification.service.serviceImpl;

import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.dto.response.NotificationResponseDto;
import com.wagglex2.waggle.domain.notification.entity.Notification;
import com.wagglex2.waggle.domain.notification.repository.NotificationRepository;
import com.wagglex2.waggle.domain.notification.service.NotificationService;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final ApplicationService applicationService;

    @Transactional
    @Override
    public void createNotification(Long senderId, Long receiverId, Long applicationId, NotificationType type) {
        Notification newNotification = new Notification(
                userService.findById(senderId),
                userService.findById(receiverId),
                applicationService.findById(applicationId),
                type
        );

        notificationRepository.save(newNotification);
    }

    @PreAuthorize("#receiverId == authentication.principal.userId")
    @Override
    public Page<NotificationResponseDto> getAllByUserIdAndCategory(
            @P("receiverId") Long receiverId,
            RecruitmentCategory category,
            Pageable pageable
    ) {
        return notificationRepository.getAllByUserIdAndCategory(receiverId, category, pageable);
    }
}
