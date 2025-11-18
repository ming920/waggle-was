package com.wagglex2.waggle.domain.notification.service.serviceImpl;

import com.wagglex2.waggle.common.validator.PageableValidator;
import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
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

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Set<String> NOTIFICATION_SORT_FIELDS = Set.of("createdAt");
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final PageableValidator pageableValidator;

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
        pageableValidator.validate(pageable);
        pageableValidator.validateSort(pageable, NOTIFICATION_SORT_FIELDS);

        return notificationRepository.getAllByUserIdAndCategory(receiverId, category, pageable);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteById(@P("userId") Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(notification.getReceiver().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_NOTIFICATION);
        }

        notificationRepository.delete(notification);
    }

    @PreAuthorize("#receiverId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteAll(@P("receiverId") Long receiverId, RecruitmentCategory category) {
        // 전체 삭제
        if (category == null) {
            notificationRepository.deleteAllByReceiverId(receiverId);
            return;
        }

        // 카테고리별 삭제
        notificationRepository.deleteAllByReceiverIdAndCategory(receiverId, category);
    }
}
