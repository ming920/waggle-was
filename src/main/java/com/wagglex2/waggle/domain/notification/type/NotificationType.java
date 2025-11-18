package com.wagglex2.waggle.domain.notification.type;

/**
 * <ul>
 *     <li>{@link #APPLICATION_SUBMITTED} : 지원 발생 → 공고 작성자에게 알림</li>
 *     <li>{@link #APPLICATION_ACCEPTED}  : 지원 수락 → 지원자에게 알림</li>
 *     <li>{@link #APPLICATION_REJECTED}  : 지원 거절 → 지원자에게 알림</li>
 * </ul>
 */
public enum NotificationType {
    APPLICATION_SUBMITTED,
    APPLICATION_ACCEPTED,
    APPLICATION_REJECTED,
}
