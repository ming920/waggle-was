package com.wagglex2.waggle.domain.notification.service;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.dto.response.NotificationResponseDto;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /**
     * 알림을 생성한다.
     *
     * <p>컨트롤러에서 직접 호출하는 것이 아니라,
     * 다른 서비스 로직 내부에서 알림을 생성할 때 사용한다.</p>
     *
     * @param senderId 알림을 발생시킨 사용자 ID
     * @param receiverId 알림 수신자 ID
     * @param applicationId 지원 ID
     * @param type 알림 타입
     */
    void createNotification(Long senderId, Long receiverId, Long applicationId, NotificationType type);
    Page<NotificationResponseDto> getAllByUserIdAndCategory(Long receiverId, RecruitmentCategory category, Pageable pageable);
    void deleteById(Long userId, Long notificationId);

    /**
     * 지정된 사용자의 알림을 삭제한다.
     * <p>
     * category가 null이면 전체 삭제하고, 그렇지 않으면 해당 카테고리만 삭제한다.
     * </p>
     *
     * @param receiverId 삭제할 사용자의 ID
     * @param category 삭제할 알림 카테고리, null이면 전체 삭제
     */
    void deleteAll(Long receiverId, RecruitmentCategory category);
}
