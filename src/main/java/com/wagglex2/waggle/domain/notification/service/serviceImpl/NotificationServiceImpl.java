package com.wagglex2.waggle.domain.notification.service.serviceImpl;

import com.wagglex2.waggle.domain.notification.entity.Notification;
import com.wagglex2.waggle.domain.notification.repository.NotificationRepository;
import com.wagglex2.waggle.domain.notification.service.NotificationService;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Transactional
    @Override
    public void createNotification(Long senderId, Long receiverId, NotificationType type) {
        Notification newNotification = new Notification(
                userService.findById(senderId),
                userService.findById(receiverId),
                type
        );

        notificationRepository.save(newNotification);
    }
}
