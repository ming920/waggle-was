package com.wagglex2.waggle.domain.notification.service;

import com.wagglex2.waggle.domain.notification.type.NotificationType;

public interface NotificationService {

    /**
     * 알림을 생성한다.
     *
     * <p>컨트롤러에서 직접 호출하는 것이 아니라,
     * 다른 서비스 로직 내부에서 알림을 생성할 때 사용한다.</p>
     *
     * @param senderId 알림을 발생시킨 사용자 ID
     * @param receiverId 알림 수신자 ID
     * @param type 알림 타입
     */
    void createNotification(Long senderId, Long receiverId, NotificationType type);
}
