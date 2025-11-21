package com.wagglex2.waggle.domain.notification.event;

import com.wagglex2.waggle.domain.application.event.ApplicationProcessedEvent;
import com.wagglex2.waggle.domain.notification.service.NotificationService;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * ApplicationProcessedEvent 수신 후 알림 생성
 * <ul>
 *     <li>Application 상태 변경 이벤트 수신</li>
 *     <li>NotificationService를 통해 알림 생성</li>
 *     <li>비동기 실행 및 트랜잭션 커밋 후 처리</li>
 *     <li>처리 타입: {@link NotificationType}</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createNotification(ApplicationProcessedEvent event) {
        notificationService.createNotification(
                event.senderId(),
                event.receiverId(),
                event.applicationId(),
                event.type()
        );
    }
}
